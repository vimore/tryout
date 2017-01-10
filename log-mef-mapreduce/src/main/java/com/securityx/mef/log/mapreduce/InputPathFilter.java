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
public class InputPathFilter extends Configured implements PathFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputPathFilter.class);
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
            if (fileName.startsWith("security1.syslog")) {
                ret = true;
            } else if (fileName.startsWith("hivedev1.labs.lan.firewall")) {
                ret = true;
            }/*else if (fileName.startsWith("ip-192-168-12-16.ec2.internal")) {
                ret = false;
            }*/ else if (fileName.startsWith("hivedev1.labs.lan.syslog")) {
                ret = true;
            } else if (fileName.matches(".*\\.(?:syslog|packet|nprobe).*\\.avro")) {
                ret = true;
            } else if (fileName.startsWith("security1.linux")) {
                ret = true;
            } else if (fileName.matches(".*\\.syslog\\.\\d+")) {
                ret = true;
            } else if (fileName.matches(".*\\.nprobe\\.\\d+")) {
                ret = true;
            } else if (fileName.matches(".*\\.packet\\.\\d+")) {
                ret = true;
            }else if (fileName.matches(".*\\.tanium\\.\\d+")) {
                ret = true;
            }else if (fileName.matches(".*\\.carbonblack\\.\\d+")) {
                ret = true;
            } else if (fileName.matches(".*\\.flume_tanium\\.\\d+")) {
                ret = true;
            }else if (fileName.matches(".*\\.tanium_feed\\.\\d+")) {
                ret = true;
            }else if (fileName.matches(".*\\.tanium_csv\\.\\d+")) {
                ret = true;
            }else if (fileName.matches(".*\\.activedirectory\\.\\d+")) {
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