/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.mef.log.mapreduce.file;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * @author jyria <jean-yves@e8security.com>
 *         <p/>
 *         force processing of the whole file by a single mapper
 *         extends TextInputFormat
 */
public class NonSplittableTextInputFormat extends TextInputFormat {

    @Override
    protected boolean isSplitable(JobContext context, Path file) {
        return false;
    }

}
