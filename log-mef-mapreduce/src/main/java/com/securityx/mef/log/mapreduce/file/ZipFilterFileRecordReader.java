package com.securityx.mef.log.mapreduce.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

/*
 * This code was written by Alex Kozlov of Cloudera, Inc. (http://www.cloudera.com) on Feb 3, 2010.
 * 
 * Copyright (c) 2010 Cloudera, Inc. All rights reserved.
 *
 */

/**
 * Reads a file matching a pattern from a log.gz archive
 * <p/>
 * {@link org.apache.hadoop.mapreduce.RecordReader}
 *
 * @author alexvk / jyria
 */
class ZipFilterFileRecordReader extends RecordReader<LongWritable, Text> {

    private final static Log logger = LogFactory.getLog(ZipFilterFileRecordReader.class.getName());

    private InputSplit inputSplit;
    private long inputPosition;
    private long inputLength;
    private int linePosition;

    private Configuration conf;

    private String fileName;

    // TODO should initialize regex in initialized() based on a conf parameter
    PathFilter filter = new PathFilter() {
        @Override
        public boolean accept(Path path) {
            return path.toString().matches(".*log\\.gz$");
        }
    };

    private boolean processed = false;

    //ZipInputStream zis;
    GZIPInputStream zis2;
    LineReader lr = null;

    private final LongWritable key = new LongWritable();
    private final Text value = new Text();

    //private ZipEntry zipEntry;

    @Override
    public float getProgress() throws IOException {
        return processed ? 1.0f : (float) inputPosition / inputLength;
    }

    @Override
    public void close() throws IOException {
        if (zis2 != null) {
            zis2.close();
            zis2 = null;
        }
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        key.set(linePosition);
        return key;
    }

    @Override
    public Text getCurrentValue() throws IOException,
            InterruptedException {
        return value;
    }

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        inputSplit = split;
        inputPosition = 0;
        linePosition = 0;
        inputLength = inputSplit.getLength();
        Path path = ((FileSplit) split).getPath();
        fileName = path.getName();
        //key.set(path.toString());
        conf = context.getConfiguration();
        FileSystem fs = path.getFileSystem(conf);
        lr = null;
        //zis = new ZipInputStream(fs.open(path));
        zis2 = new GZIPInputStream(fs.open(path));
        lr = new LineReader(zis2);

//        if (zis != null) {
//            zipEntry = zis.getNextEntry();
//            if (zipEntry != null) {
//                while (zipEntry != null && (zipEntry.isDirectory() || (filter != null && !filter.accept(new Path(zipEntry.getName()))))) {
//                    inputPosition += zipEntry.getCompressedSize();
//                    zipEntry = zis.getNextEntry();
//                }
//                if (zipEntry != null) {
//                    lr = new LineReader(zis);
//                    processed = false;
//                } else {
//                    System.err.println("No entry matching the filter in file " + path.toString());
//                    processed = true;
//                }
//            } else {
//                logger.error("No entries in file " + path.toString());
//                processed = true;
//            }
//        }
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (!processed) {
            while (lr != null) {
                linePosition++;
                if (lr.readLine(value) > 0)
                    return true;
                lr.close();
                lr = null;
            }
            processed = true;
        }
        return false;
    }

}