package com.securityx.mef.log.mapreduce;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.securityx.mef.log.mapreduce.file.ParsingJobConfig;
import com.securityx.mef.log.mapreduce.parserutils.MefParser;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.avro.mapreduce.AvroMultipleOutputs;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.securityx.flume.log.avro.Event;
import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.logcollection.parser.configchecker.ConfigChecker;
import com.securityx.model.mef.field.api.SupportedFormats;

/**
 * A map reduce job submitter from the command line
 */
public class LogParsingJob extends Configured implements Tool {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParsingJob.class);
    private static final String HDFS = "hdfs://";
    public static final String MORPHLINE_OUTPUT = "morphline_output";
    public static final int LONGPARSING = 5000;

    public static final String FLUMELIVESTREAM = "flume/livestream";

    private static final String DEFAULTPROPERTIESFILE = "logparsingjob.properties";

    private static Options options = new Options();
 //   private Configuration conf;
    private Properties props;
    private String processingName;
    private boolean purgeDestDir = false;
    private ParsingJobConfig jobConfig;

    public LogParsingJob() {
        Configuration c = new Configuration();
        setConf(c);
    }

    public LogParsingJob(String id, Properties props) {
        this.props = props;
        Configuration c = new Configuration();
        setConf(c);
    }

    private static int help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.securityx.mef.log.mapreduce.LogParsingJob", options);
        return -1;
    }

    /**
     * the core map reduce job definition
     *
     * @return
     * @throws Exception
     */
    private int doJob() throws Exception {
        // Get the config magically ( default ??)

        Configuration conf = getConf();
        
        ConfigChecker.checkConfig();
        if (System.getenv("HADOOP_TOKEN_FILE_LOCATION") != null) {
            conf.set("mapreduce.job.credentials.binary", System.getenv("HADOOP_TOKEN_FILE_LOCATION"));
        }

        conf.set("mapred.reduce.child.java.opts", "-Xmx1024m");
        Job job = Job.getInstance(conf);
        job.setJobName("LogParsingJob " + processingName);
        job.setJarByClass(getClass());
        FileSystem fs = FileSystem.get(conf);


        String tmpOutputDir = HdfsFileUtils.genJobTmpDir(fs);
        System.out.println("Input Data: " + jobConfig.getInputPaths());
        System.out.println("Output Data: " + tmpOutputDir);

        //Paths inputPath = new Path(inputPathStr);
        FileInputFormat.addInputPaths(job, jobConfig.getInputPaths());
        FileInputFormat.setInputPathFilter(job, InputPathFilter.class);

        //AvroKeyInputFormat passes an avro record in the Key of a map and the value is Null
        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);
        job.setMapperClass(LogParsingMapper.class);

        job.setMapOutputKeyClass(AvroKey.class);
        job.setMapOutputValueClass(AvroValue.class);
        AvroJob.setMapOutputKeySchema(job, Schema.create(Schema.Type.STRING));
        AvroJob.setMapOutputValueSchema(job, ParsedOutputConverter.SCHEMA);      
        job.setReducerClass(LogParsingReducer.class);
        FileOutputFormat.setOutputPath(job, new Path(tmpOutputDir));
        job.setNumReduceTasks(ParsedOutputConverter.supportedSchemas.length);

		// add multiple outputs
		// for unsupported format
		AvroMultipleOutputs.addNamedOutput(job, MefParser.UNMATCHED_RAW_KEY, AvroKeyOutputFormat.class,
				ParsedOutput.SCHEMA$, null);
        AvroMultipleOutputs.addNamedOutput(job, MefParser.UN_MATCHED_RAW_LOG_EXCEPTION, AvroKeyOutputFormat.class,
                ParsedOutput.SCHEMA$, null);
		for (SupportedFormats format : ParsedOutputConverter.typeMap.keySet()) {
			AvroMultipleOutputs.addNamedOutput(job, format.name(),
					AvroKeyOutputFormat.class, ParsedOutputConverter.typeMap.get(format), null);
		}
        int r = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("Job completed");
        if (r == 0) {

            if (MefParser.moveToFinalDest(fs, new Path(tmpOutputDir), this.jobConfig, job.getJobID().toString(), this.purgeDestDir) != 0)
                // facing failure while relocating files, consider job as failed.
                r=1;

		}
        MefParser.countersToGraphite(job,getConf(),r == 0);
        MefParser.countersToHealthTrack(job,getConf(),r == 0);
        return r;
    }




    /**
     * Job execution, called from command line, passing options.
     *
     * @param args
     * @return
     * @throws Exception
     */

    /**
     * Job execution, called from command.</p>
     * test if  params are provided with -Dsome.variable=some_value or try to read Options from command line
     * @param args
     * @return
     * @throws Exception
     */

    @Override
    public int run(String[] args) throws Exception {

        Configuration conf = getConf();

        LogParsingJobProperties.getPropertiesFile(conf, DEFAULTPROPERTIESFILE);

        if (LogParsingJobProperties.validateLogParsingJob(conf)){

             jobConfig = ParsingJobConfig.genJobConfigFromConf(conf);

            boolean purge = LogParsingJobProperties.getBoolean(conf, LogParsingJobProperties.DOPURGEDST, false);
            if (purge){
                this.purgeDestDir = true;
            }
            processingName = String.format(this.getClass().getName()+"-"+jobConfig.getInputPaths());
            return doJob();
        }else{
            System.err.println(LogParsingJobProperties.usageLogParsingJob());
            System.err.println("unable to validate -Dcom.e8sec... style parameters");

            return -1;
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        //conf.addResource("e8sec-site.properties");

        int res = ToolRunner.run(conf, new LogParsingJob(), args);
        System.exit(res);
    }
}
