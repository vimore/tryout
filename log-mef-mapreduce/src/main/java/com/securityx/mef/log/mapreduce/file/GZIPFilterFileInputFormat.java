package com.securityx.mef.log.mapreduce.file;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

/**
 * read gzip files : decompress and read line by line
 */

public class GZIPFilterFileInputFormat extends FileInputFormat<LongWritable, Text> {
    @Override
    protected boolean isSplitable(JobContext context, Path filename) {
        return false;
    }

    @Override
    public RecordReader<LongWritable, Text> createRecordReader(
            InputSplit split, TaskAttemptContext context) throws IOException,
            InterruptedException {
        RecordReader<LongWritable, Text> rr = new ZipFilterFileRecordReader();
        rr.initialize(split, context);
        return rr;
    }
}
