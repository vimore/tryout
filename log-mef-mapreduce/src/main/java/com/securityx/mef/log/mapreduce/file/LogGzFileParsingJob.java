package com.securityx.mef.log.mapreduce.file;

import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.logcollection.parser.configchecker.ConfigChecker;
import com.securityx.mef.log.mapreduce.*;
import com.securityx.mef.log.mapreduce.parserutils.MefParser;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.apache.avro.Schema;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.avro.mapreduce.AvroMultipleOutputs;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A map reduce job submitter from the command line
 */
public class LogGzFileParsingJob extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogGzFileParsingJob.class);
    private static final String HDFS = "hdfs://";
    public static final String MORPHLINE_OUTPUT = "morphline_output";
    public static final int LONGPARSING = 5000;
    public static final String MORPHLINESCRIPT = "MORPHLINE_SCRIPT";
    public static final String MORPHLINEID = "MORPHLINEID";
    public static final String LOGSOURCE = "LOGSOURCE";

    public static final String FLUMELIVESTREAM = "flume/livestream";

    private static final String HDFSRAWLOGBASEDIR = "/securityx/flume/livestream";
    private static final String DEFAULTPROPERTIESFILE = "loggzfileparsingjob.properties";
    private Class inputFormatClass = GZIPFilterFileInputFormat.class;

    private static Options options = new Options();
    private Properties props;
    private String optLogSource = null;
    private String processingName;
    private boolean purgeDestDir = false;
    private String morphlineFile;
    private String morphlineId;
    private ParsingJobConfig jobConfig;

    public LogGzFileParsingJob() {
        super();
    }

    public LogGzFileParsingJob(String id, Properties props) {
        super();
        this.props = props;
    }


    private static int help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.securityx.mef.log.mapreduce.LogFileParsingJob", options);
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

        Job job = Job.getInstance(conf);
        job.setJobName("LogParsingJob " + processingName);
        job.setJarByClass(getClass());
        FileSystem fs = FileSystem.get(conf);

        job.getConfiguration().set(MORPHLINESCRIPT, morphlineFile);
        job.getConfiguration().set(MORPHLINEID, morphlineId);
        if (this.optLogSource != null)
            job.getConfiguration().set(LOGSOURCE, this.optLogSource);

        String tmpOutputDir = HdfsFileUtils.genJobTmpDir(fs);
        System.out.println("Input Data: " + jobConfig.getInputPaths());
        System.out.println("Output Data: " + tmpOutputDir);
        System.out.println("Morphline file: " + morphlineFile);
        System.out.println("Morphline script: " + morphlineId);

        //Paths inputPath = new Path(inputPathStr);
        FileInputFormat.addInputPaths(job, jobConfig.getInputPaths());
        //FileInputFormat.setInputPathFilter(job, GzInputPathFilter.class);

        //TextInputFormat splits file into <offset, value>
        job.setInputFormatClass(inputFormatClass);
        job.setMapperClass(LogFileParsingMapper.class);

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
        return r;
    }




    public int run(String[] args) throws  Exception{
        Configuration conf = getConf();

        LogParsingJobProperties.getPropertiesFile(conf, DEFAULTPROPERTIESFILE);

        if (LogParsingJobProperties.validateLogGzFileParsingJob(conf)) {
            jobConfig = ParsingJobConfig.genJobConfigFromConf(conf);
            String morphlineFile =  LogParsingJobProperties.get(conf, LogParsingJobProperties.MORPHLINEFILE);
            String morphlineId = LogParsingJobProperties.get(conf, LogParsingJobProperties.MORPHLINEID);

            if (morphlineFile != null )
                this.morphlineFile = morphlineFile;
            if (morphlineId != null )
                this.morphlineId = morphlineId;


            processingName = String.format(this.getClass().getName()+"-"+jobConfig.getInputPaths());

            String optLogSource = LogParsingJobProperties.get(conf, LogParsingJobProperties.LOGSOURCE);
            if ( null != optLogSource ) {
                this.optLogSource = optLogSource;
            }

            String splitable = LogParsingJobProperties.get(conf, LogParsingJobProperties.SPLITABLE);
            if ( null != splitable ) {
                inputFormatClass = TextInputFormat.class;
            }

            return doJob();
        }else{
            System.err.println(LogParsingJobProperties.usageLogParsingJob());
            System.err.println("unable to validate -Dcom.e8sec... style parameters");

            return -1;
        }

    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        int res = ToolRunner.run(conf, new LogGzFileParsingJob(), args);
        System.exit(res);
    }
}
