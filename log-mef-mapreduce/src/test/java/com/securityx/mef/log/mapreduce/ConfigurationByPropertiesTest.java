package com.securityx.mef.log.mapreduce;

import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class ConfigurationByPropertiesTest extends TestCase{


    @Test
    public void test_ConfigFromProterties(){
        Configuration conf = new Configuration();
        LogParsingJobProperties.getPropertiesFile(conf, "tst_properties_file.properties");

        assertEquals("contains com.e8sec.tst.value", true, null != conf.get("com.e8sec.tst.value"));
        assertEquals("contains com.e8sec.tst.value", "jyria", conf.get("com.e8sec.tst.value"));
    }

    @Test
    public void test_LogParsingJobProterties(){
        Configuration conf = new Configuration();
        LogParsingJobProperties.getPropertiesFile(conf, "logparsingjob.properties");

        assertEquals("contains com.e8sec.cluster", true, null != conf.get("com.e8sec.cluster"));
        assertEquals("contains com.e8sec.cluster", "$cluster", conf.get("com.e8sec.cluster"));
        assertEquals("is a valid LogFileParsingJob properties file", true, LogParsingJobProperties.validateLogFileParsingJob(conf));
    }
}
