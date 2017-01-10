package com.securityx.model.mef.morphline.command;

import com.securityx.model.mef.field.api.WebProxyMefField;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefSonicOsScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefSonicOsScriptTest.class);

  public MefSonicOsScriptTest() {
    super(MefSonicOsScriptTest.class.toString());
    this.morphlineId = "sonicoswithvalidate";
    this.confFile = "logcollection-sonicos.conf";
  }

  @Test
  public void test_simple() throws FileNotFoundException {
    String line = "id=firewall sn=0006B129195C time=\"2013-11-17 01:04:39\" fw=71.6.1.234 pri=6 c=1024 m=537 msg=\"Connection Closed\" n=5921690 src=71.6.1.234:4008:X1: dst=222.234.2.135:137:X1: proto=udp/netbios-ns sent=156 ";
    boolean result = doTest(line);
    assertEquals(true, result);
  }

  @Test
  public void test1() throws FileNotFoundException {
    // 
    String line = "id=firewall sn=0006B129195C time=\"2013-11-17 01:04:39\" fw=71.6.1.234 pri=6 c=1024 m=537 msg=\"Connection Closed\" n=5921690 src=71.6.1.234:4008:X1: dst=222.234.2.135:137:X1: proto=udp/netbios-ns sent=156 ";
    boolean result = doTest(line);
    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record r = this.outCommand.getRecord(0);
    Assert.assertEquals("WebProxyMef",
            r.get(WebProxyMefField.logSourceType.getPrettyName()).get(0));
    Assert.assertEquals("SonicOs",
            r.get(WebProxyMefField.externalLogSourceType.getPrettyName()).get(0));
    Assert.assertEquals(5921690,
            r.get("baseEventCount").get(0));
    Assert.assertEquals(156,
            r.get("bytesOut").get(0));
    Assert.assertEquals("1024",
            r.get("c").get(0));
    Assert.assertEquals("Connection Closed",
            r.get("cefEventName").get(0));
    Assert.assertEquals("6",
            r.get("cefEventSeverity").get(0));
    Assert.assertEquals("537",
            r.get("cefSignatureId").get(0));
    Assert.assertEquals("222.234.2.135",
            r.get("destinationAddress").get(0));
    Assert.assertEquals("222.234.2.135:137:X1:",
            r.get("destinationIPAndInterfaceAndName").get(0));
    Assert.assertEquals(137,
            r.get("destinationPort").get(0));
    Assert.assertEquals("71.6.1.234",
            r.get("deviceAddress").get(0));
    Assert.assertEquals("1024",
            r.get("deviceEventCategory").get(0));
    Assert.assertEquals("X1",
            r.get("deviceInboundInterface").get(0));
    Assert.assertEquals("71.6.1.234",
            r.get("deviceNameOrIp").get(0));
    Assert.assertEquals("X1",
            r.get("deviceOutboundInterface").get(0));
    Assert.assertEquals("222.234.2.135:137:X1:",
            r.get("dst").get(0));
    Assert.assertEquals("71.6.1.234",
            r.get("fw").get(0));
    Assert.assertEquals("537",
            r.get("m").get(0));
    Assert.assertEquals("Connection Closed",
            r.get("msg").get(0));
    Assert.assertEquals("5921690",
            r.get("n").get(0));
    Assert.assertEquals("6",
            r.get("pri").get(0));
    Assert.assertEquals("udp/netbios-ns",
            r.get("proto").get(0));
    Assert.assertEquals("156",
            r.get("sent").get(0));
    Assert.assertEquals("0006B129195C",
            r.get("sn").get(0));
    Assert.assertEquals("71.6.1.234",
            r.get("sourceAddress").get(0));
    Assert.assertEquals("71.6.1.234:4008:X1:",
            r.get("sourceIPAndInterfaceAndName").get(0));
    Assert.assertEquals(4008,
            r.get("sourcePort").get(0));
    Assert.assertEquals("71.6.1.234:4008:X1:",
            r.get("src").get(0));
    Assert.assertEquals(1384650279000L,
            r.get("startTime").get(0));
    Assert.assertEquals("2013-11-17 01:04:39",
            r.get("time").get(0));
    Assert.assertEquals("udp/netbios-ns",
            r.get("transportProtocol").get(0));

  }

}
