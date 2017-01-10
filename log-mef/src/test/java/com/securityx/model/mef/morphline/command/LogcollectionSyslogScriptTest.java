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

import java.io.File;
import java.io.FileNotFoundException;

public class LogcollectionSyslogScriptTest extends TestCase {

  private Logger logger = LoggerFactory.getLogger(LogcollectionSyslogScriptTest.class);

  public LogcollectionSyslogScriptTest() {
  }

  private boolean doTest(String line, String confFile, String morphlineId) throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile(confFile);

    boolean exists = morphlineConf.hasPath("morphlines");
    boolean result = false;
    if (exists) {
      MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
      RecordSystemOutCommand outCommand = new RecordSystemOutCommand(morphlineContext);
      MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, morphlineId);
      morphlineHarness.startup(outCommand);
      result = morphlineHarness.feedLines(1, new String[]{line});
      morphlineHarness.shutdown();
    } else {
      OutUtils.printOut("ERROR : file not found : morphline conf file " + confFile);
    }
    return result;
  }

  @Test
  public void test1() throws FileNotFoundException, Exception {
    String line = "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    String confFile = "logcollection-syslog.conf";
    boolean result = doTest(line, confFile, "sysloglogcollection");
    assertEquals(true, result);
  }

  @Test
  public void test2() throws FileNotFoundException, Exception {
    File f = new File(".");
    String line = "0:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    String confFile = "logcollection-syslog.conf";
    boolean result = doTest(line, confFile, "sysloglogcollection");
    assertEquals(false, result);
  }

}
