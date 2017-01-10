package com.securityx.mef.log.mapreduce;

import com.securityx.flume.log.avro.Event;
import com.securityx.log.parsed.avro.FullParsedOutput;
import com.securityx.logcollection.parser.configchecker.ConfigChecker;
import com.securityx.mef.log.azkaban.ConfigUtils;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.InvalidInputException;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static com.securityx.mef.log.azkaban.ConfigUtils.getHourlyBucketFromJobProps;

/**
 * A map reduce job submitter from the command line
 */
public class LogParsingFullOutputJob extends Configured implements Tool {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParsingFullOutputJob.class);
    private static final String HDFS = "hdfs://";
    public static final String MORPHLINE_OUTPUT = "morphline_output";
    public static final int LONGPARSING = 5000;

    public static final String FLUMELIVESTREAM = "flume/livestream";

    private static final String HDFSRAWLOGBASEDIR = "/securityx/flume/livestream";
    private static final String DEFAULTPROPERTIESFILE = "logparsingjob.properties";

    private static Options options = new Options();
 //   private Configuration conf;
    private Properties props;
    private String inputPathStr;
    private String outputPathStr;
    private String processingName;
    private boolean purgeDestDir = false;
    //private String cluster;

    public LogParsingFullOutputJob() {
        Configuration c = new Configuration();
        setConf(c);
    }

    public LogParsingFullOutputJob(String id, Properties props) {
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
     * Job execution, called from azkaban.
     * props set by public Constructor (String id, Properties props)
     *
     * @return succeeded job : 0, failed job : 1
     * @throws Exception
     */
    public int run() throws Exception {

        /* Go through some trouble to subtract one day - to run on yesterday's data, today. */
        DateTime dt = getHourlyBucketFromJobProps(props);
        String environmentStr = props.getProperty("environment");
        /* TODO : make as ark when called from azkaban (must be herited from context */
        /* String filesystemStr = props.getProperty("filesystem"); */

        if (props.contains("purge_dest_dir")){
            String purgeDestDir = props.getProperty("purge_dest_dir");
             if ("yes".equals(purgeDestDir.toLowerCase())){
                 this.purgeDestDir = true;
             }
        }


        //dt = dt.minusHours(1);
        Integer[] dateParts = ConfigUtils.getParts(dt);

        String yearStr = StringUtils.leftPad(dateParts[0].toString(), 4, "0");
        String monthStr = StringUtils.leftPad(dateParts[1].toString(), 2, "0");
        String dayStr = StringUtils.leftPad(dateParts[2].toString(), 2, "0");
        String hourStr = StringUtils.leftPad(dateParts[3].toString(), 2, "0");

        inputPathStr = String.format( "/e8/" + environmentStr + "/" + FLUMELIVESTREAM + "/%s/%s/%s/%s/*", yearStr, monthStr, dayStr, hourStr);
        outputPathStr = String.format( "/e8/" + environmentStr + "/" + MORPHLINE_OUTPUT + "/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);
        processingName = String.format(environmentStr + "-" + "%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);


        // Remove the existing output so we can overwrite it
//        FileSystem hdfs = FileSystem.get(conf);
//        Path oldOutputPath = new Path(String.format("hdfs://hivecluster2/securityx/morphline_output/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr));
//        hdfs.delete(oldOutputPath, true);
//        System.out.println("Deleted " + oldOutputPath);
        return doJob();
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
        System.out.println("Input Data: " + inputPathStr);
        System.out.println("Output Data: " + tmpOutputDir);

        //Paths inputPath = new Path(inputPathStr);
        FileInputFormat.addInputPaths(job, inputPathStr);
        FileInputFormat.setInputPathFilter(job, InputPathFilter.class);

        //AvroKeyInputFormat passes an avro record in the Key of a map and the value is Null
        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);
        job.setMapperClass(LogParsingFullOutputMapperOnly.class);

        //job.setMapOutputKeyClass(Text.class);
        //job.setMapOutputValueClass(AvroValue.class);
        //AvroJob.setMapOutputValueSchema(job, ParsedOutput.SCHEMA$);
        //job.setMapOutputValueClass(NullWritable.class);
        //job.setReducerClass(LogParsingReducer.class);
        job.setOutputFormatClass(AvroKeyOutputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(tmpOutputDir));
        //AvroJob.setOutputValueSchema(job, ParsedOutput.SCHEMA$);
        AvroJob.setOutputKeySchema(job, FullParsedOutput.SCHEMA$);
        job.setNumReduceTasks(0);
        //job.setOutputFormatClass( NullOutputFormat.class);

        if (this.purgeDestDir){
            fs.delete(new Path(outputPathStr), true);
        }

        int r = 1;
        try {
            r = job.waitForCompletion(true) ? 0 : 1;
            System.out.println("Job completed");
            if (r == 0) {
                List<Path> resultFiles = HdfsFileUtils.moveToPersistentDirWithoutOverlapping(job.getJobID().toString(), fs,
                        new Path(tmpOutputDir), new Path(outputPathStr), Pattern.compile(".*\\.avro"));
                if (resultFiles.size() > 0) {
                    fs.delete(new Path(tmpOutputDir), true);
                    for (Path p : resultFiles) {
                        System.out.println(String.format("Job result file: %s", p.toString()));
                    }
                } else {
                    // relocation of tmp result failed, consider job as failed.
                    return 1;
                }
            }
        }catch(InvalidInputException e){
             LOGGER.error("Invalid input exception: ", e);
        }

        countersToGraphite(job,r == 0);


        return r;
    }


    private void countersToGraphite(Job job, boolean completion){
        Counters counters = null;
        String graphite = getConf().get(LogParsingJobProperties.GRAPHITESERVER.getPropertyName());
        if (null != graphite) {
            LOGGER.info("reporting counters to "+graphite);
            String realm = getConf().get(LogParsingJobProperties.GRAPHITEREALM.getPropertyName());
            String realmOverWrite = getConf().get(LogParsingJobProperties.GRAPHITEREALMOVER.getPropertyName());
            if (null != realmOverWrite)
                realm = realmOverWrite;
            String[] parts = graphite.split(":");
            if (parts.length != 2){
                LOGGER.error ("invalid value for property "+LogParsingJobProperties.GRAPHITESERVER.getPropertyName()+" : "+graphite );
                LOGGER.error("expected format : server:port (for instance 127.0.0.1:9000");
            }  else

                try {
                    GraphiteReporter graphiteReporter = new GraphiteReporter(parts[0],
                            Integer.valueOf(parts[1]), java.net.InetAddress.getLocalHost().getHostName().split("\\.")[0],
                            realm);
                    CounterGroup mycounters = job.getCounters().getGroup(LogParsingMapper.class.getCanonicalName());
                    graphiteReporter.publishCounter(LogParsingFullOutputJob.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.FileSystemCounter.class.getCanonicalName());
                    graphiteReporter.publishCounter(org.apache.hadoop.mapreduce.FileSystemCounter.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.JobCounter.class.getCanonicalName());
                    graphiteReporter.publishCounter(org.apache.hadoop.mapreduce.JobCounter.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.TaskCounter.class.getCanonicalName());
                    graphiteReporter.publishCounter(org.apache.hadoop.mapreduce.TaskCounter.class.getSimpleName(),mycounters);
                    if (completion)
                        graphiteReporter.publish("job.completion","1");
                    else
                        graphiteReporter.publish("job.completion", "0");
                    graphiteReporter.close();

                } catch (IOException e) {
                    LOGGER.error(" error sending counter to graphite",e);
                }
        }else
            LOGGER.info("no propertiy "+LogParsingJobProperties.GRAPHITESERVER.getPropertyName()+" found");
    }

    /**
     * Job execution, called from command line, passing options.
     *
     * @param args
     * @return
     * @throws Exception
     */

    public int runWithOptions(String[] args) throws Exception {


        options = LogParsingJobProperties.getOptionsLogFileParsingJob(); //DataCaptureOptions.buildDataCaptureOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            exp.printStackTrace();
            return help();
        }
        String yearStr = LogParsingJobProperties.getOptionValue(line,LogParsingJobProperties.YEAR);
        String monthStr  = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.MONTH);
        if (null != monthStr)
            monthStr = StringUtils.leftPad(monthStr, 2, "0");
        String dayStr = null;
        if (LogParsingJobProperties.hasOption(line, LogParsingJobProperties.DAY)) {
            dayStr = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.DAY);
        } else {
            dayStr = "*";
        }
        String hourStr = null;
        if (LogParsingJobProperties.hasOption(line, LogParsingJobProperties.HOUR)) {
            hourStr = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.HOUR);
            if (hourStr != null)
                hourStr = StringUtils.leftPad(hourStr, 2, "0");
        } else {
            hourStr = "*";
        }
        String basedir = HDFSRAWLOGBASEDIR;
        if (LogParsingJobProperties.hasOption(line, LogParsingJobProperties.BASEDIR)) {
            basedir = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.BASEDIR);
        }
        if (LogParsingJobProperties.hasOption(line, LogParsingJobProperties.DOPURGEDST)){
            this.purgeDestDir = true;
        }
        if (basedir.contains(",")){
            String[] paths = basedir.split(",");
            for (String basedirPath : paths){
                System.err.println("processing path parts : " +basedirPath);
                inputPathStr+=(inputPathStr.length()>0?",":"")+ String.format("%s/%s/%s/%s/%s/*", basedirPath, yearStr, monthStr, dayStr, hourStr);

            }

        }else{
            inputPathStr = String.format("%s/%s/%s/%s/%s/*", basedir, yearStr, monthStr, dayStr, hourStr);
        }
        //inputPathStr = String.format("%s/%s/%s/%s/%s/%s/*", clusterStr, basedir, yearStr, monthStr, dayStr, hourStr);
        outputPathStr = line.getOptionValue("o");
        processingName = String.format(basedir + "-" + "%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);

        return doJob();
    }

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
            String yearStr = LogParsingJobProperties.get(conf,LogParsingJobProperties.YEAR);
            String monthStr = LogParsingJobProperties.get(conf,LogParsingJobProperties.MONTH);
            monthStr = StringUtils.leftPad(monthStr, 2, "0");
            String dayStr = LogParsingJobProperties.get(conf,LogParsingJobProperties.DAY);
            if ( null != dayStr ) {
                dayStr = StringUtils.leftPad(dayStr, 2, "0");
            } else {
                dayStr = "*";
            }
            String hourStr = LogParsingJobProperties.get(conf,LogParsingJobProperties.HOUR);
            if ( null != hourStr) {
                hourStr = StringUtils.leftPad(hourStr, 2, "0");
            } else {
                hourStr = "*";
            }

            String basedir = LogParsingJobProperties.get(conf,LogParsingJobProperties.BASEDIR);
            if ( null == basedir ) {
                basedir = HDFSRAWLOGBASEDIR;
            }
            boolean purge = LogParsingJobProperties.getBoolean(conf, LogParsingJobProperties.DOPURGEDST, false);
            if (purge){
                this.purgeDestDir = true;
            }
            String outPath = LogParsingJobProperties.get(conf,LogParsingJobProperties.OUTPUT);
            if (basedir.contains(",")){
                inputPathStr="";
                String[] paths = basedir.split(",");
                for (String basedirPath : paths){
                    System.err.println("processing path parts : " +basedirPath);
                    inputPathStr+=(inputPathStr.length()>0?",":"")+ String.format("%s/%s/%s/%s/%s/*", basedirPath, yearStr, monthStr, dayStr, hourStr);

                }

            }else{
                inputPathStr = String.format("%s/%s/%s/%s/%s/*", basedir, yearStr, monthStr, dayStr, hourStr);
            }
            outputPathStr=outPath;
            processingName = String.format(basedir + "-" + "%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);
            return doJob();
        }else{
            System.err.println(LogParsingJobProperties.usageLogParsingJob());
            System.err.println("unable to validate -Dcom.e8sec... style parameters, defaulting to Options read from command line");

            return runWithOptions(args);
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        //conf.addResource("e8sec-site.properties");

        int res = ToolRunner.run(conf, new LogParsingFullOutputJob(), args);
        System.exit(res);
    }
}
