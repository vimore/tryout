/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.mef.log.azkaban;

import org.joda.time.DateTime;

import java.util.Properties;

/**
 * Common lib for azkaban toolset.
 *
 * @author jyrialhon
 */
public class ConfigUtils {

    public static Integer[] getParts(DateTime dt) {
        Integer[] dateParts = new Integer[4];
        dateParts[0] = dt.getYear();
        dateParts[1] = dt.getMonthOfYear();
        dateParts[2] = dt.getDayOfMonth();
        dateParts[3] = dt.getHourOfDay();
        return dateParts;
    }

    public static DateTime getHourlyBucketFromJobProps(Properties props) {

        String yearStr = props.getProperty("year");
        String monthStr = props.getProperty("month");
        String dayStr = props.getProperty("day");
        String hourStr = props.getProperty("hour");

        Integer year = Integer.valueOf(yearStr);
        Integer month = Integer.valueOf(monthStr);
        Integer day = Integer.valueOf(dayStr);
        Integer hour = Integer.valueOf(hourStr);

        //TODO : (jyria thoughts need timezone to be added there
        return new DateTime(year, month, day, hour, 0);
    }


    public static DateTime getDailyBucketFromJobProps(Properties props) {

        String yearStr = props.getProperty("year");
        String monthStr = props.getProperty("month");
        String dayStr = props.getProperty("day");

        Integer year = Integer.valueOf(yearStr);
        Integer month = Integer.valueOf(monthStr);
        Integer day = Integer.valueOf(dayStr);

        return new DateTime(year, month, day, 0, 0);
    }
  /*public static void main(String[] args){
   DateTime test;
   test = new DateTime(2014, 07, 23, 0, 0, DateTimeZone.UTC);
   System.out.println(test);
   StringUtils.leftPad(getParts(test)[0].toString(),4,"0");
   for( int i : getParts(test)){
   System.out.println(i);
       
   }
     
     
   System.out.println(test.minusHours(1));
    
     
     
   }*/
}
