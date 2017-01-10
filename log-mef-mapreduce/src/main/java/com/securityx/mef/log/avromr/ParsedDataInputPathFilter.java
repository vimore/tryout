/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.mef.log.avromr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import java.io.IOException;

/**
 * @author jyrialhon
 */
public class ParsedDataInputPathFilter extends Configured implements PathFilter {
    private FileSystem fs;

    @Override
    public boolean accept(Path path) {
        //LOGGER.info("Path is "+ path.getName() );
        boolean ret = false;
        try {
            String fileName = path.getName();
            //consider only files that can be parsed by morphlines script
            // first skip .tmp files if running in current feeding directory
            if (fileName.endsWith(".tmp")) {
                return false;
            }
            if (fileName.matches(".*\\.avro")) {
                ret = true;
            }
        } catch (Exception e) {
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
