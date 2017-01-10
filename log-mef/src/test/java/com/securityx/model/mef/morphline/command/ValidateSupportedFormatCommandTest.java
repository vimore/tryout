package com.securityx.model.mef.morphline.command;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class ValidateSupportedFormatCommandTest extends TestCase {


  @Test
  public void test() throws  Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-validatesupportedformat-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");

    Record in = new Record();
    in.put("logSourceType", "WebProxyMef");
    in.put("sourceNameOrIp", "127.0.0.1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedRecords(in);
    morphlineHarness.shutdown();

    //System.out.println(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record out = outCommand.getRecord(0);
    //System.out.println(outCommand.getRecord(0));
    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    assertEquals("sourceAddress", "127.0.0.1", out.get("sourceAddress").get(0));
    assertEquals("sourceNameOrIp", "127.0.0.1", out.get("sourceNameOrIp").get(0));
  }

  @Test
  public void testPassThroughFail() throws Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-validatesupportedformat-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline2");

    Record in = new Record();
    in.put("logSourceType", "WebProxyMef");
    in.put("sourceNameOrIp", "127.0.0.1");
    in.put("sourceMacAddress", "127.0.0.1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedRecords(in);
    morphlineHarness.shutdown();

    //System.out.println(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record out = outCommand.getRecord(0);
    OutUtils.printOut(outCommand.getRecord(0));
    assertEquals("logSourceType", "UnMatched-validation", out.get("logSourceType").get(0));
    assertEquals("sourceAddress", "127.0.0.1", out.get("sourceAddress").get(0));
    assertEquals("sourceNameOrIp", "127.0.0.1", out.get("sourceNameOrIp").get(0));
    assertEquals("_validationLog", "[unexpected value format for field 'sourceMacAddress' : [127.0.0.1]]", out.get("_validationLog").get(0));
  }

  @Test
  public void testFail() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-validatesupportedformat-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");

    Record in = new Record();
    in.put("logSourceType", "WebProxyMef");
    in.put("sourceNameOrIp", "127.0.0.1");
    in.put("sourceMacAddress", "127.0.0.1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedRecords(in);
    morphlineHarness.shutdown();

    //System.out.println(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 0);
  }


}
