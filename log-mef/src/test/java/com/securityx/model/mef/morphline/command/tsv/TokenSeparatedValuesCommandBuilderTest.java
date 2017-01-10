package com.securityx.model.mef.morphline.command.tsv;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.Assert;
import org.kitesdk.morphline.api.MorphlineContext;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class TokenSeparatedValuesCommandBuilderTest {

  @Test
  public void test() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-tsv-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "Oct 16 10:49:20"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "month", "Oct"));
    Assert.assertTrue(outCommand.assertValue(0, "day", "16"));
    Assert.assertTrue(outCommand.assertValue(0, "time", "10:49:20"));
  }

}
