package com.securityx.mef.log.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter out only files which we want to parse using Morphlines scripts
 * This will work only if we want to exclude any existing file. If the file does not exit, it seems
 * this method will not be called
 */
public class UploadJobInputPathFilter extends Configured implements PathFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadJobInputPathFilter.class);
    private FileSystem fs;

    @Override
    public boolean accept(Path path) {
        LOGGER.info("Path is " + path.getName());
        boolean ret = false;
        try {
            String fileName = path.getName();
            //consider only files that can be parsed by morphlines script
            // first skip .tmp files if running in current feeding directory
            if (fileName.endsWith(".tmp")) {
                LOGGER.info("skipping " + fileName);
                return false;
            }
            if (fileName.matches(".*\\.(?:fileUploadJobReady).*\\.avro")) {
                ret = true;
            } else if (fileName.matches(".*\\.fileUploadJobReady\\.\\d+")) {
                ret = true;
            }

        } catch (Exception e) {
            LOGGER.error("", e);
            ret = false;
        }
        return ret;
    }

    @Override
    public void setConf(Configuration conf) {
        if (null != conf) {
            try {
                this.fs = FileSystem.get(conf);
            } catch (IOException e) {
            }
        }
    }
}