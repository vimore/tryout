package com.securityx.mef.dpi.mapreduce;

import com.securityx.mef.dpi.avro.Event;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.log4j.Logger;

public class DpiLoadJob2  {

    private static Logger logger = Logger.getLogger(DpiLoadJob2.class);


    public static void main (String[] strings) throws Exception {


        Job job = new Job();
        job.setJobName("DPI loading");
        job.setJarByClass(DpiLoadJob2.class);

        Path inputPath = new Path("/securityx/flume/livestream/2013/11/05/22/security1.packet.1383719767198");
        FileInputFormat.setInputPaths(job, inputPath);

        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);

        job.setMapperClass(DpiAvroMapper.class);

        job.setNumReduceTasks(0);

        job.setOutputFormatClass( NullOutputFormat.class);

        int r = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("Job completed with r " + r);

    }

}