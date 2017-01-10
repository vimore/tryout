package com.securityx.model.mef.morphline.command.maprules;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.Assert;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MapRulesCommandBuilderTest {

  @Test
  public void test() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-maprules-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    Record r = new Record();
    r.put("EventId", "a");
    morphlineHarness.feedRecords(r);
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "result", "Match A"));
    outCommand.clear();
    r = new Record();
    r.put("EventId", "b");
    morphlineHarness.feedRecords(r);
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "result", "Match B"));

    outCommand.clear();
    r = new Record();
    r.put("EventId", "c");
    morphlineHarness.feedRecords(r);
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "result", "Match C or D"));

  }

}
