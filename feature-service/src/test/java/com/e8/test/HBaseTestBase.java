package com.e8.test;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.fs.HFileSystem;
import org.apache.hadoop.hdfs.DFSClient;
import org.reflections.Reflections;

import java.io.IOException;

public class HBaseTestBase {
    static{
        try {
            System.setProperty("log4j.configuration", Thread.currentThread().getContextClassLoader().getResource("log4j.properties").toString());
            org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.ALL);
            ((Log4JLogger) DFSClient.LOG).getLogger().setLevel(org.apache.log4j.Level.ALL);
            ((Log4JLogger) HFileSystem.LOG).getLogger().setLevel(org.apache.log4j.Level.ALL);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    protected static HBaseTestingUtility utility = null;
    protected static int hBaseStartedCalls = 0;
    protected static boolean HBaseStarted = false;
    // Figure out how many classes extend this class.  This will be used to count how many classes have run tests, and to
    // do teardown only after all of them have run.
    protected static Reflections reflections = new Reflections("com.e8");
    protected static int numChildren = reflections.getSubTypesOf(HBaseTestBase.class).size();

    // We want to start the local HBase cluster only once for all the tests, since it takes a significant amount of time to start it. This
    // is done by having all tests that need HBase extend this class and implement a BeforeClass method which calls this method. The first
    // time this happens, we will start HBase.
    public static void setupHBase() throws Exception {
        if (!HBaseStarted) {
            utility = new HBaseTestingUtility();
            utility.startMiniCluster();
            HBaseStarted = true;
        }
        hBaseStartedCalls++;
    }

    // All tests which use HBase must also implement an AfterClass method and that method must call this method.  When we have
    // run all the tests that extend this class, we will call shutdown the last time this is called.  NOTE! This will only
    // work if _every_ child class has an AfterClass method which calls this method.
    // This is not the greatest mechanism.  For instance, there will be no teardown if a test fails.  But it's the best I've
    // come up with.
    public static void teardownHBase() throws IOException {
        if (hBaseStartedCalls == numChildren) {
            utility.shutdownMiniHBaseCluster();
        }
    }
}
