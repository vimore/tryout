package com.securityx.model.mef.morphline.command.tsv;

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

public class TokenSeparatedValuesQuotedCommandTest {


  @Test
  public void test() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-tsvq-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "1,2,3"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "f1", "1"));
    Assert.assertTrue(outCommand.assertValue(0, "f2", "2"));
    Assert.assertTrue(outCommand.assertValue(0, "f3", "3"));
  }

  @Test
  public void test2() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-tsvq-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "1,\"2\",3"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "f1", "1"));
    Assert.assertTrue(outCommand.assertValue(0, "f2", "2"));
    Assert.assertTrue(outCommand.assertValue(0, "f3", "3"));
  }

  @Test
  public void test3() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-tsvq-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
      "1,\"2\",\"3\""});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "f1", "1"));
    Assert.assertTrue(outCommand.assertValue(0, "f2", "2"));
    Assert.assertTrue(outCommand.assertValue(0, "f3", "3"));
  }
  @Test
  public void test4() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-tsvq-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "1,\"2\",\"3\",\""});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "f1", "1"));
    Assert.assertTrue(outCommand.assertValue(0, "f2", "2"));
    Assert.assertTrue(outCommand.assertValue(0, "f3", "3"));
  }

  @Test
  public void test5() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-tsvq-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline2");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "1,\"2\",\"3\",\""});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    Record r = outCommand.getRecord(0);
    OutUtils.printOut(r);
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Assert.assertTrue(outCommand.assertValue(0, "f1", "1"));
    Assert.assertTrue(outCommand.assertValue(0, "f2", "2"));
    Assert.assertTrue(outCommand.assertValue(0, "f3", "3\","));
  }


}
