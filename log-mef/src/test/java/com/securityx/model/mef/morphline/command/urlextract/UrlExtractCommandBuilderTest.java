package com.securityx.model.mef.morphline.command.urlextract;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.TestCase;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class UrlExtractCommandBuilderTest extends TestCase {



  @Test
  public void test() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-urlextract-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "http://www.google.com"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    OutUtils.printOut(outCommand.getRecord(0));

  }

  @Test
  public void test2() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-urlextract-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "https://www.google.com/#q=how+to+search+an+%40+in+google"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    OutUtils.printOut(outCommand.getRecord(0));

  }

  @Test
  public void test3() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-urlextract-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline2");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "https://www.google.com/#q=how+to+search+an+%40+in+google"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    OutUtils.printOut(outCommand.getRecord(0));
    Record out = outCommand.getRecord(0);
    assertEquals("1", "https", out.get("1").get(0));
    assertEquals("3", "www.google.com", out.get("3").get(0));
    assertEquals("4", "443", out.get("4").get(0));
    assertEquals("5", "/", out.get("5").get(0));
    assertEquals("6", "#q=how+to+search+an+%40+in+google", out.get("6").get(0));

  }


  @Test
  public void test4() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-urlextract-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline2");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "https://user@www.google.com/#q=how+to+search+an+%40+in+google"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    OutUtils.printOut(outCommand.getRecord(0));
    Record out = outCommand.getRecord(0);
    assertEquals("1", "https", out.get("1").get(0));
    assertEquals("3", "www.google.com", out.get("3").get(0));
    assertEquals("4", "443", out.get("4").get(0));
    assertEquals("5", "/", out.get("5").get(0));
    assertEquals("6", "#q=how+to+search+an+%40+in+google", out.get("6").get(0));

  }

  //
  @Test
  public void test5() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-urlextract-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline2");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "https://oascentral.comcast.net/RealMedia/ads/adstream_sx.ads/dev.comcast.net/com-mail/inbox/@x32?AdParam\\=adx"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    OutUtils.printOut(outCommand.getRecord(0));
    Record out = outCommand.getRecord(0);
    assertEquals("1", "https", out.get("1").get(0));
    assertEquals("3", "oascentral.comcast.net", out.get("3").get(0));
    assertEquals("4", "443", out.get("4").get(0));
    assertEquals("5", "/RealMedia/ads/adstream_sx.ads/dev.comcast.net/com-mail/inbox/@x32", out.get("5").get(0));
    assertEquals("6", "AdParam\\=adx", out.get("6").get(0));

  }

  @Test
  public void test6() throws FileNotFoundException, Exception {
    Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-urlextract-command.conf");
    MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
    AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
    MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
    morphlineHarness.startup(outCommand);
    morphlineHarness.feedLines(1, new String[]{
            "https://oascentral.comcast.net/RealMedia/ads/adstream_sx.ads/dev.comcast.net/com-mail/inbox/@x32?AdParam\\=adx"});
    morphlineHarness.shutdown();

    OutUtils.printOut(outCommand.getNumRecords());
    OutUtils.printOut(outCommand.getRecord(0));
    Record out = outCommand.getRecord(0);
    assertEquals("destinationNameOrIp", "oascentral.comcast.net", out.get("destinationNameOrIp").get(0));
    assertEquals("destinationPort", "443", out.get("destinationPort").get(0));
    assertEquals("requestPath", "/RealMedia/ads/adstream_sx.ads/dev.comcast.net/com-mail/inbox/@x32", out.get("requestPath").get(0));
    assertEquals("requestQuery", "AdParam\\=adx", out.get("requestQuery").get(0));
    assertEquals("requestScheme", "https", out.get("requestScheme").get(0));


  }

}
