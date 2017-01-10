package com.securityx.mef.log.mapreduce;

import com.securityx.flume.log.avro.Event;
import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.mef.log.azkaban.ConfigUtils;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static com.securityx.mef.log.azkaban.ConfigUtils.getHourlyBucketFromJobProps;

/**
 * A map reduce job submitter from the command line
 */
public class FileUploadRelocationJob extends Configured implements Tool {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadRelocationJob.class);
    private static final String HDFS = "hdfs://";
    public static final String FLUMELIVESTREAM = "flume/livestream";
    public static final String FLUMEUPLOADJOBSTREAM = "flume/uploadJobstream";
    public static final String FLUMEUPLOADSSTREAM = "flume/uploadstream";
    private static final String HDFSUPLOADJOBBASEDIR = "/e8/dev/" + FLUMEUPLOADJOBSTREAM;
    private static final String HDFSUPLOADSBASEDIR = "/e8/dev/" + FLUMEUPLOADSSTREAM;
    private static final String HDFSLIVEBASEDIR = "/e8/dev/" + FLUMELIVESTREAM;

    private static final String UPLOADPATH = "uploadPath";
    private static final String DSTPATH = "destPath";


    private static Options options = new Options();
    private Configuration conf;
    private Properties props;

    public FileUploadRelocationJob() {
        Configuration c = new Configuration();
        setConf(c);
    }

    public FileUploadRelocationJob(String id, Properties props) {
        this.props = props;
        Configuration c = new Configuration();
        setConf(c);
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConf() {
        return conf;
    }


    private static int help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.securityx.mef.log.mapreduce.LogParsingJob", options);
        return -1;
    }

    /**
     * A mapper to parse log files. Reads an avro file and one avro record is passed to the map at a time.
     * After parsing, the morphlines output is stored in an avro record and send as input to the reducer
     */
    private static class UploadJobMapper extends Mapper<AvroKey<Event>, NullWritable, Text, AvroValue<ParsedOutput>> {
        private final Logger LOGGER = LoggerFactory.getLogger(UploadJobMapper.class);
        private Path uploadsPath;
        private Path destPath;
        private FileSystem fs;


        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            String uploadPathStr = context.getConfiguration().get(UPLOADPATH);
            String destPathStr = context.getConfiguration().get(DSTPATH);
            uploadsPath = new Path(uploadPathStr);
            destPath = new Path(destPathStr);
            LOGGER.info("uploadsPath=" + uploadPathStr);
            LOGGER.info("destPathStr=" + destPathStr);
            fs = FileSystem.get(context.getConfiguration());
        }

        /**
         * map function
         *
         * @param key     An avro record ( from flume output)
         * @param value   Null as the complete Avro record is passed as the key
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(AvroKey<Event> key, NullWritable value, Context context) throws IOException, InterruptedException {
            Event e = key.datum();
            String jobLocation = new String(e.getBody().array());
            Path uploadPath = new Path(uploadsPath, new Path(jobLocation));
            FileStatus[] srcFiles = fs.listStatus(uploadPath, new InputPathFilter());
            LOGGER.info("importing " + srcFiles.length + " files from " + uploadPath.toString());
            for (int i = 0; i < srcFiles.length; i++) {
                LOGGER.info("creating dest path with  " + destPath.toString() + " and  " + srcFiles[i].getPath().getName());
                Path dest = new Path(destPath, srcFiles[i].getPath().getName());
                fs.rename(srcFiles[i].getPath(), dest);
                LOGGER.info("imported " + srcFiles[i].getPath().toString() + " to " + dest.toString());
            }
        }
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
        String clusterStr = props.getProperty("cluster");
        String filesystemStr = "hdfs://" + clusterStr;
        /* TODO : make as ark when called from azkaban (must be herited from context */
        /* String filesystemStr = props.getProperty("filesystem"); */

        //dt = dt.minusHours(1);
        Integer[] dateParts = ConfigUtils.getParts(dt);

        String yearStr = StringUtils.leftPad(dateParts[0].toString(), 4, "0");
        String monthStr = StringUtils.leftPad(dateParts[1].toString(), 2, "0");
        String dayStr = StringUtils.leftPad(dateParts[2].toString(), 2, "0");
        String hourStr = StringUtils.leftPad(dateParts[3].toString(), 2, "0");

        String inputPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + FLUMEUPLOADJOBSTREAM + "/%s/%s/%s/%s/*", yearStr, monthStr, dayStr, hourStr);
        String destPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + FLUMELIVESTREAM + "/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);
        String uploadPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + FLUMEUPLOADSSTREAM);

        // Remove the existing output so we can overwrite it
//        FileSystem hdfs = FileSystem.get(conf);
//        Path oldOutputPath = new Path(String.format("hdfs://hivecluster2/securityx/morphline_output/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr));
//        hdfs.delete(oldOutputPath, true);
//        System.out.println("Deleted " + oldOutputPath);
        return doJob(inputPathStr, destPathStr, uploadPathStr);
    }

    /**
     * the core map reduce job definition
     *
     * @param inputPathStr
     * @param destPathStr
     * @return
     * @throws Exception
     */
    private int doJob(String inputPathStr, String destPathStr, String uploadPathStr) throws Exception {
        // Get the config magically ( default ??)
        Configuration conf = getConf();

        //conf.set("mapreduce.reduce.java.opts", "-Xmx1024m ");
        //conf.set("mapreduce.map.memory.mb", "2048");
        //conf.set("mapreduce.map.java.opts", "-Xmx1024m -XX:PermSize=128m");
        //conf.set("mapreduce.input.fileinputformat.split.maxsize", "2097152");
        Job job = Job.getInstance(conf);
        job.setJobName("FileUpload Job relocation to livestream");
        job.setJarByClass(getClass());

        System.out.println("Input data: " + inputPathStr);
        System.out.println("Ouput path: " + destPathStr);
        System.out.println("uploads path: " + uploadPathStr);

        job.getConfiguration().set(UPLOADPATH, uploadPathStr);
        job.getConfiguration().set(DSTPATH, destPathStr);

        Path inputPath = new Path(inputPathStr);
        FileInputFormat.addInputPath(job, inputPath);
        FileInputFormat.setInputPathFilter(job, UploadJobInputPathFilter.class);

        //AvroKeyInputFormat passes an avro record in the Key of a map and the value is Null
        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);
        job.setMapperClass(UploadJobMapper.class);

        job.setOutputFormatClass(NullOutputFormat.class);
        job.setNumReduceTasks(0);

        //job.setMapOutputKeyClass(Text.class);
        //job.setMapOutputValueClass(AvroValue.class);
        //AvroJob.setMapOutputValueSchema(job, ParsedOutput.SCHEMA$);
        //job.setMapOutputValueClass(NullWritable.class);
        //job.setReducerClass(LogParsingReducer.class);
        //job.setOutputFormatClass(AvroKeyOutputFormat.class);
        //FileOutputFormat.setOutputPath(job, new Path(destPathStr));
        //AvroJob.setOutputValueSchema(job, ParsedOutput.SCHEMA$);
        //AvroJob.setOutputKeySchema ( job, ParsedOutput.SCHEMA$);
        //job.setNumReduceTasks(1);
        //job.setOutputFormatClass( NullOutputFormat.class);

        int r = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("Job completed");
        return r;
    }

    /**
     * Job execution, called from command line.  using Options parsing
     *
     * @param args
     * @return
     * @throws Exception
     */
    public int runWithOptions(String[] args) throws Exception {
        options = LogParsingJobProperties.getOptionsFileUploadRelocationJob();
        CommandLineParser parser = new PosixParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            exp.printStackTrace();
            return help();
        }
        String yearStr = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.YEAR);
        String monthStr =   LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.MONTH);
        monthStr = StringUtils.leftPad(monthStr, 2, "0");
        String dayStr = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.DAY);
        if (null != dayStr) {
            dayStr = StringUtils.leftPad(dayStr, 2, "0");
        } else {
            dayStr = "*";
        }
        String hourStr = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.HOUR);;
        if (null != hourStr) {
            hourStr = StringUtils.leftPad(hourStr, 2, "0");
        } else {
            hourStr = "*";
        }
        String basedir = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.BASEDIR);//HDFSUPLOADJOBBASEDIR;
        if (null == basedir) {
            basedir = HDFSUPLOADJOBBASEDIR;
        }

        String livestreamdir = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.OUTPUT);//HDFSLIVEBASEDIR;
        if (null == livestreamdir) {
            livestreamdir = HDFSLIVEBASEDIR;
        }
        String uploadsdir = LogParsingJobProperties.getOptionValue(line, LogParsingJobProperties.UPLOADSTREAMDIR);//HDFSUPLOADSBASEDIR;
        if (null == uploadsdir) {
            uploadsdir = line.getOptionValue("u");
        }

        String inputPathStr = String.format("%s/%s/%s/%s/%s/*", basedir, yearStr, monthStr, dayStr, hourStr);
        String destPathStr = String.format("%s/%s/%s/%s/%s", livestreamdir, yearStr, monthStr, dayStr, hourStr);
        String uploadPathStr = String.format("%s", uploadsdir);
        System.out.println("--Input data: " + inputPathStr);
        System.out.println("--Ouput path: " + destPathStr);
        System.out.println("--uploads path: " + uploadPathStr);

        return doJob(inputPathStr, destPathStr, uploadPathStr);
    }

    /**
     * Job execution, called from command line.
     *
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception {

        if(LogParsingJobProperties.validateFileUploadRelocationJob(conf)){
            String yearStr = LogParsingJobProperties.get(conf, LogParsingJobProperties.YEAR);
            String monthStr = LogParsingJobProperties.get(conf, LogParsingJobProperties.MONTH);
            monthStr = StringUtils.leftPad(monthStr, 2, "0");
            String dayStr = LogParsingJobProperties.get(conf, LogParsingJobProperties.DAY);
            dayStr =   StringUtils.leftPad(dayStr, 2, "0");
            String hourStr = LogParsingJobProperties.get(conf, LogParsingJobProperties.HOUR);
            if (null != hourStr) {
                hourStr = StringUtils.leftPad(hourStr, 2, "0");
            } else {
                hourStr = "*";
            }
            String basedir = LogParsingJobProperties.get(conf, LogParsingJobProperties.BASEDIR);;//HDFSUPLOADJOBBASEDIR;
            if (null == basedir) {
                basedir = HDFSUPLOADJOBBASEDIR;
            }

            String livestreamdir = LogParsingJobProperties.get(conf, LogParsingJobProperties.OUTPUT);;//;
            if (null == livestreamdir) {
                livestreamdir = HDFSLIVEBASEDIR;
            }
            String uploadsdir = LogParsingJobProperties.get(conf, LogParsingJobProperties.UPLOADSTREAMDIR);//HDFSUPLOADSBASEDIR;
            if (null == uploadsdir) {
                uploadsdir = HDFSUPLOADSBASEDIR;
            }

            String inputPathStr = String.format("%s/%s/%s/%s/%s/*", HDFS, yearStr, monthStr, dayStr, hourStr);
            String destPathStr = String.format("%s/%s/%s/%s/%s", livestreamdir, yearStr, monthStr, dayStr, hourStr);
            String uploadPathStr = String.format("%s", uploadsdir);
            System.out.println("--Input data: " + inputPathStr);
            System.out.println("--Ouput path: " + destPathStr);
            System.out.println("--uploads path: " + uploadPathStr);

            return doJob(inputPathStr, destPathStr, uploadPathStr);
        }   else{
            System.err.println(LogParsingJobProperties.usageFileUploadRelocationJob());
            System.err.println("unable to validate -Dcom.e8sec... style parameters, defaulting to Options read from command line");
            return runWithOptions(args);
        }


    }

    public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new Configuration(), new FileUploadRelocationJob(), args);
        System.exit(res);
    }
}
