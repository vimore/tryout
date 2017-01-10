package com.securityx.model.mef.morphline.command;

import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefCiscoScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefCiscoScriptTest.class);

  public MefCiscoScriptTest() {
    super(MefCiscoScriptTest.class.toString());
    this.morphlineId = "asapixfwsm";
    this.confFile = "ciscopix-to-mef.conf";
  }

  @Test
  public void test0() throws FileNotFoundException {
    String line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);
    assertEquals(true, result);
  }

  @Test
  public void test1() throws FileNotFoundException {
    String line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test2() throws FileNotFoundException {
    String line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test3() throws FileNotFoundException {
    String line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test4() throws FileNotFoundException {
    // 
    String line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("302015",
            res.get("ciscoEventId").get(0));
    Assert.assertEquals("6",
            res.get("ciscoEventSeverity").get(0));
    Assert.assertEquals("Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)",
            res.get("ciscoMessage").get(0));
    Assert.assertEquals("%PIX",
            res.get("deviceType").get(0));
    Assert.assertEquals("%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)",
            res.get("syslogMessage").get(0));
  }

}
