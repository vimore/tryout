package com.securityx.mef.dpi.mapreduce;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class DpiPcapJsonLoadJob extends Configured implements Tool  {

//    public static class DpiPcapJsonMapper extends Mapper<Object, Text, NullWritable, NullWritable> {
//        private static Logger logger = Logger.getLogger(DpiPcapJsonMapper.class);
//
//        private DpiToMef dpiToMef = null;
//
//        enum DpiRecordProcess {
//            PCAP_PROCESSING_SUCCESS, PCAP_PROCESSING_FAILED
//        }
//
//        private String filename = "";
//
//        @Override
//        protected void setup(Context context) throws IOException, InterruptedException {
//            super.setup(context);
//            dpiToMef  = new DpiToMef();
//
//            try {
//                //InputSplit is of FileSplit as we read records from an avro file
//                FileSplit fileSplit = (FileSplit)context.getInputSplit();
//                filename = fileSplit.getPath().getName();
//            }
//            catch (Exception ex) {
//                logger.error("Getting file name failed:", ex);
//            }
//        }
//
//        @Override
//        public void map(Object key, Text value, Context context ) throws IOException, InterruptedException {
//            String jsonLine = value.toString();
//
//            try {
//                String customerId = context.getConfiguration().get(DpiLoadJob.CUSTOMER_ID);
//                dpiToMef.saveDpiEvent(jsonLine,customerId,filename);
//                context.getCounter(DpiRecordProcess.PCAP_PROCESSING_SUCCESS).increment(1);
//            }
//            catch (Exception e) {
//                context.getCounter(DpiRecordProcess.PCAP_PROCESSING_FAILED).increment(1);
//                logger.error("Saving failed : ", e );
//                logger.error(jsonLine);
//            }
//        }
//    }

    private static Logger logger = Logger.getLogger(DpiPcapJsonLoadJob.class);

    private static Options options = new Options();

    private static int help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("com.securityx.mef.dpi.mapreduce.DpiJsonLoadJob", options);
        return -1;
    }

    @Override
    public int run(String[] args) throws Exception {
        Option op1 = new Option("j","jsonfile",true, "dpi json record file from pcap data") ;
        op1.setRequired(true);
        options.addOption(op1);

        op1 = new Option("c","customerId",true, "customer id of pcap data") ;
        op1.setRequired(true);
        options.addOption(op1);

        options.addOption("debug",false, "enable debug level of logging");

        CommandLineParser parser = new PosixParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        }
        catch (ParseException exp) {
            return help();
        }
        String jsonFile  = line.getOptionValue("j");
        Path inputPath = new Path(jsonFile);

        Configuration conf = getConf();
        if (line.hasOption("debug")) {
            System.out.println(" Option -debug passed ......");
            conf.set("mapred.map.child.log.level", "debug");
        }
        conf.set(DpiLoadJob.CUSTOMER_ID,line.getOptionValue("c"));
        Job job = new Job(conf);
        job.setJobName("DPI loading PCAP data");
        job.setJarByClass(getClass());

        FileInputFormat.setInputPaths(job, inputPath);
        job.setInputFormatClass(TextInputFormat.class);

        job.setMapperClass(DpiPcapJsonMapper.class);
        job.setReducerClass(DpiPcapjsonReducer.class);
        //job.setNumReduceTasks(0);
        job.setMapOutputKeyClass(RequestResponseWritable.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputFormatClass( NullOutputFormat.class);

        int r = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("Job completed");
        return r;

    }

    public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new Configuration(), new DpiPcapJsonLoadJob(), args);
        System.exit(res);
    }
}
