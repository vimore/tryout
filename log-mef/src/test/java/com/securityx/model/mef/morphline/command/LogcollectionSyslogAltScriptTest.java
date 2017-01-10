package com.securityx.model.mef.morphline.command;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.RecordSystemOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.TestCase;
import org.kitesdk.morphline.api.MorphlineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class LogcollectionSyslogAltScriptTest extends TestCase {
    private Logger logger = LoggerFactory.getLogger(LogcollectionSyslogAltScriptTest.class);
    public LogcollectionSyslogAltScriptTest() {
    }
    
    private boolean doTest(String line, String confFile, String morphlineId) throws FileNotFoundException, Exception {
      Config morphlineConf = null;
        morphlineConf = MorphlineResourceLoader.getConfFile(confFile);
        
        boolean exists = morphlineConf.hasPath("morphlines");
        boolean result = false;
        if (exists){
          MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
          RecordSystemOutCommand outCommand = new RecordSystemOutCommand(morphlineContext);
          MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, morphlineId);
          morphlineHarness.startup(outCommand);
          result = morphlineHarness.feedLines(1, new String[]{line});
          morphlineHarness.shutdown();
        }else{
          OutUtils.printOut("ERROR : file not found : morphline conf file "+confFile);
        }
        return result;
    }
        
    @Test
    public void test1() throws FileNotFoundException, Exception {
        
        String line = "10/31/2013 23:59:12 10.2.1.1 %CP: time:31May2003  6:46:25;action:accept;orig:10.2.1.1;i/f_dir:0;i/f_name:1;src:209.46.4.217;s_port:68;dst:255.255.255.255;service:67;proto:17;rule:6;packets:1;bytes:339;";
        String confFile = "logcollection-syslog.conf";
       boolean result= doTest(line, confFile, "sysloglogcollectionalt");
       assertEquals(true, result);
    }
    
    @Test
    public void test2() throws FileNotFoundException, Exception {
        
        String line = "10/31/2013 20:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
        String confFile = "logcollection-syslog.conf";
       boolean result= doTest(line, confFile, "sysloglogcollectionalt");
       assertEquals(true, result);
    }
    

}