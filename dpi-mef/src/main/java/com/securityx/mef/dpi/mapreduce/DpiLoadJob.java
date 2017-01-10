package com.securityx.mef.dpi.mapreduce;

import com.securityx.mef.dpi.avro.Event;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class DpiLoadJob extends Configured implements Tool {
    public static final String CUSTOMER_ID = "customerId";
    private static Logger logger = Logger.getLogger(DpiLoadJob.class);

    private static Options options = new Options();

    private static int help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.securityx.mef.dpi.mapreduce.DpiLoadJob", options);
        return -1;
    }

    @Override
    public int run(String[] args) throws Exception {
        // A simple CLI. Need to make better option handling
        //options.addOption("y", "year", true, "Year part of flume dir - yyyy");
        Option op1 = new Option("y","year",true, "year part of flume dir - yyyy") ;
        op1.setRequired(true);
        options.addOption(op1);
        op1 = new Option("m", "month", true, "Month part of flume dir") ;
        op1.setRequired(true);
        options.addOption(op1);
        options.addOption("d", "day", true, "Day part of flume dir");
        options.addOption("h", "hour", true, "hour part of flume dir");
        options.addOption("debug",false, "enable debug level of logging");

        CommandLineParser parser = new PosixParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        }
        catch (ParseException exp) {
            return help();
        }
        String yearStr  = line.getOptionValue("y");
        String monthStr = StringUtils.leftPad(line.getOptionValue("m"),2,"0");
        String dayStr = null;
        if (line.hasOption("d")) {
            dayStr = StringUtils.leftPad(line.getOptionValue("d"),2,"0");
        }
        else {
            dayStr = "*";
        }
        String hourStr =  null ;
        if (line.hasOption("h")) {
            hourStr = StringUtils.leftPad(line.getOptionValue("h"),2,"0");
        }
        else {
            hourStr = "*";
        }

        String ps = String.format("/securityx/flume/livestream/%s/%s/%s/%s/security1.packet.*", yearStr,monthStr,dayStr,hourStr);
        System.out.println(ps);
        Path inputPath = new Path(ps);

        //Path inputPath = new Path("/securityx/flume/livestream/2013/11/05/15/security1.packet.1383692459327");
        //Path inputPath = new Path("/securityx/flume/livestream/2013/11/05/22/security1.packet.1383719767198");

        Configuration conf = getConf();
        if (line.hasOption("debug")) {
            System.out.println(" Option -debug passed ......");
            conf.set("mapred.map.child.log.level", "debug");
        }

        //conf.set("mapred.child.java.opts","-Dlog4j.configuration=debug_log4j.properties ");   does not work

        //For now we use this customerId for all data loaded through flume. At some point we need to include
        //the customerId in the flume record headers
        conf.set(DpiLoadJob.CUSTOMER_ID, "securityx") ;
        Job job = new Job(conf);
        job.setJobName("DPI loading");
        job.setJarByClass(getClass());

        FileInputFormat.setInputPaths(job, inputPath);
        //AvroKeyInputFormat passes an avro record in the Key of a map and the value is Null
        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);
        job.setMapperClass(DpiAvroMapper.class);
        job.setNumReduceTasks(0);
        job.setOutputFormatClass( NullOutputFormat.class);

//        RunningJob job = JobClient.runJob(conf);
//        if (!job.isComplete() || !job.isSuccessful()) {
//            return -1;
//        }

        int r = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("Job completed");
        return r;
    }

    public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new Configuration(), new DpiLoadJob(), args);
        System.exit(res);
    }
}


//    public static void main(String[] args) throws Exception {
//
//        Job job = new Job();
//
//        Path inputPath = new Path("");
//        FileInputFormat.setInputPaths(job, inputPath);
//
//        //FileInputFormat.setInputPaths(job, new Path(getClass()
//        //        .getResource("/org/apache/avro/mapreduce/mapreduce-test-input.avro")
//        //        .toURI().toString()));
//        job.setInputFormatClass(AvroKeyInputFormat.class);
//        AvroJob.setInputKeySchema(job, Event.SCHEMA$);
//
//        job.setMapperClass(DpiAvroMapper.class);
//
//        //job.setMapOutputKeyClass(Text.class);
//        //AvroJob.setMapOutputValueSchema(job, MasterEventFormat.SCHEMA$);
//        //job.setReducerClass(DpiAvroReducer.class);
//        //AvroJob.setOutputKeySchema(job, MasterEventFormat.SCHEMA$);
//        //job.setOutputFormatClass(AvroKeyOutputFormat.class);
//        //Path outputPath = new Path("");
//        //FileOutputFormat.setOutputPath(job, outputPath);
//
//        job.setNumReduceTasks(0);
//
//        int r = job.waitForCompletion(true) ? 0 : 1;
//        System.out.println("Job completed");
//    }
