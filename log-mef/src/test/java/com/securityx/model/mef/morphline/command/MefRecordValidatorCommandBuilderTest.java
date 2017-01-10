package com.securityx.model.mef.morphline.command;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.RecordSystemOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.MorphlineContext;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefRecordValidatorCommandBuilderTest {

  public MefRecordValidatorCommandBuilderTest() {
  }

  
  
  @Test
  public void test() throws FileNotFoundException, Exception {
    
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test-mef-validate-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    RecordSystemOutCommand outCommand = new RecordSystemOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)"});
    morphlineHarness.shutdown();
  }

  @Test
  public void test2() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test-mef-fields.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    RecordSystemOutCommand outCommand = new RecordSystemOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "jyria, hivedata, 192.168.10.1"});
    morphlineHarness.shutdown();
  }

  @Test
  public void test3() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test-mef-fields.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    RecordSystemOutCommand outCommand = new RecordSystemOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "jyria, hivedata, el002.energyleaks.org"});
    morphlineHarness.shutdown();
  }

}
