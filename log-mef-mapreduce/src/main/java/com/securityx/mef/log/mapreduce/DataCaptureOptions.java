/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.mef.log.mapreduce;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @author jyrialhon
 */


public class DataCaptureOptions {

//    public static Options buildDataCaptureOptions() {
//        Options options = new Options();
//        // year , month , day , hour are required parameters
//        Option op1 = new Option("y", "year", true, "year part of flume dir - yyyy");
//        op1.setRequired(true);
//        options.addOption(op1);
//
//        Option op2 = new Option("m", "month", true, "Month part of flume dir");
//        op2.setRequired(true);
//        options.addOption(op2);
//
//        Option op3 = new Option("d", "day", true, "Day part of flume dir");
//        op3.setRequired(true);
//        options.addOption(op3);
//
//        Option op4 = new Option("h", "hour", true, "hour part of flume dir");
//        op4.setRequired(true);
//        options.addOption(op4);
//
//        Option op5 = new Option("o", "output", true, "Output dir");
//        op5.setRequired(true);
//        options.addOption(op5);
//
//        Option op6 = new Option("b", "basedir", true, "base directory");
//        op6.setRequired(false);
//        options.addOption(op6);
//
//        Option op7 = new Option("c", "cluster", true, "Cluster hostname");
//        op7.setRequired(true);
//        options.addOption(op7);
//
//        options.addOption("debug", false, "enable debug level of logging");
//        options.addOption("purgeDestDir", false, "enable the deletion of dest dir if it exists");
//
//        return options;
//
//    }

//    public static Options buildUploadJobOptions_() {
//        Options options = buildDataCaptureOptions();
//        Option opt2 = new Option("u", "uploadstreamdir", true, "an uploadstream dir");
//        opt2.setRequired(true);
//        options.addOption(opt2);
//        return options;
//    }

    public static Options buildFileJobCaptureOptions_() {
        Options options = new Options();
        // year , month , day , hour are required parameters
        Option op1 = new Option("y", "year", true, "year part of flume dir - yyyy");
        op1.setRequired(false);
        options.addOption(op1);

        Option op2 = new Option("m", "month", true, "Month part of flume dir");
        op2.setRequired(false);
        options.addOption(op2);

        Option op3 = new Option("d", "day", true, "Day part of flume dir");
        op3.setRequired(false);
        options.addOption(op3);

        Option op4 = new Option("h", "hour", true, "hour part of flume dir");
        op4.setRequired(false);
        options.addOption(op4);

        Option op5 = new Option("o", "output", true, "Output dir");
        op5.setRequired(true);
        options.addOption(op5);

        Option op6 = new Option("b", "basedir", true, "base directory");
        op6.setRequired(false);
        options.addOption(op6);

        Option op7 = new Option("c", "cluster", true, "Cluster hostname");
        op7.setRequired(true);
        options.addOption(op7);

        Option op8 = new Option("f", "file", true, "morphline file");
        op8.setRequired(true);
        options.addOption(op8);

        Option op9 = new Option("i", "id", true, "morphline id");
        op9.setRequired(true);
        options.addOption(op9);

        Option op10 = new Option("split", "splitable", false, "allow file input file (only relevant for files without a headers on top of it");
        op10.setRequired(false);
        options.addOption(op10);

        Option op11 = new Option("src", "logSource", true, "log source name");
        op11.setRequired(false);
        options.addOption(op11);

        Option op12 = new Option("inputfile", "input-file", true, "URI of the input file to be processed");
        op12.setRequired(false);
        op12.setType(String.class);
        options.addOption(op12);


        options.addOption("debug", false, "enable debug level of logging");
        options.addOption("purgeDestDir", false, "enable the deletion of dest dir if it exists");

        return options;

    }

}
