package com.securityx.model.mef.morphline.command.cef;

import com.securityx.model.mef.field.api.WebProxyMefField;
import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefCefScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefCefScriptTest.class);

  public MefCefScriptTest() {
    super(MefCefScriptTest.class.toString());
    this.morphlineId = "ceffromsyslog";
    //this.morphlineId = "ceffromsyslogmissinghost";
    this.confFile = "logcollection-cef.conf";
  }

  @Test
  public void test0() throws FileNotFoundException {
    String line = "10/31/2013 23:59:20 cefsource CEF:0|ForeScout Technologies|CounterAct|6.3.4|COMPLIANCE|host is compliant|1|cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Operating System  cs2=Updated cs3=yes cs4=CounterAct Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
    //     line = "10/31/2013 23:59:12 CEF:0|ForeScout Technologies|CounterAct|6.3.4|NONCOMPLIANCE|host is not compliant|5|cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Security policy  cs2=Test failed cs3=no cs4=CounterAct Action dmac=00:03:47:24:46:10 dst=10.10.1.8 dntdom=mydomain.com dhost=wks-105 duser=Dick_Dietz dvc=1.1.1.2 dvchost=forescout-02 rt=1328814511000";
    Record r = new Record();
    r.put("message", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test4() throws FileNotFoundException {
    // 
    String line = "10/31/2013 23:59:20 cefsource CEF:0|ForeScout Technologies|CounterAct|6.3.4|COMPLIANCE|host is compliant|1|cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Operating System  cs2=Updated cs3=yes cs4=CounterAct Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
    Record r = new Record();
    r.put("message", line);
    boolean result = doTest(r);

    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("Cef",
            res.get(WebProxyMefField.externalLogSourceType.getPrettyName()).get(0));
    Assert.assertEquals("WebProxyMef",
            res.get(WebProxyMefField.logSourceType.getPrettyName()).get(0));
    Assert.assertEquals("CounterAct",
            res.get("cefDeviceProduct").get(0));
    Assert.assertEquals("ForeScout Technologies",
            res.get("cefDeviceVendor").get(0));
    Assert.assertEquals("6.3.4",
            res.get("cefDeviceVersion").get(0));
    Assert.assertEquals("host is compliant",
            res.get("cefEventName").get(0));
    Assert.assertEquals("1",
            res.get("cefEventSeverity").get(0));
    Assert.assertEquals("0",
            res.get("cefHeaderVersion").get(0));
    Assert.assertEquals("COMPLIANCE",
            res.get("cefSignatureId").get(0));
    Assert.assertEquals("00:03:47:24:46:65",
            res.get("destinationMacAddress").get(0));
    Assert.assertEquals("00:03:47:24:46:65",
            res.get("dmac").get(0));
    Assert.assertEquals("1.1.1.1",
            res.get("deviceAddress").get(0));
    Assert.assertEquals("1.1.1.1",
            res.get("dvc").get(0));
    Assert.assertEquals("10.10.1.4",
            res.get("destinationAddress").get(0));
    Assert.assertEquals("10.10.1.4",
            res.get("dst").get(0));
    Assert.assertEquals("1328814052000",
            res.get("receiptTime").get(0));
    Assert.assertEquals("1328814052000",
            res.get("rt").get(0));
    Assert.assertEquals("Compliancy Event Trigger",
            res.get("cs4Label").get(0));
    Assert.assertEquals("Compliancy Event Trigger",
            res.get("deviceCustomString4Label").get(0));
    Assert.assertEquals("Compliancy Policy Name",
            res.get("cs1Label").get(0));
    Assert.assertEquals("Compliancy Policy Name",
            res.get("deviceCustomString1Label").get(0));
    Assert.assertEquals("Compliancy Policy Subrule Name",
            res.get("cs2Label").get(0));
    Assert.assertEquals("Compliancy Policy Subrule Name",
            res.get("deviceCustomString2Label").get(0));
    Assert.assertEquals("CounterAct Action",
            res.get("cs4").get(0));
    Assert.assertEquals("CounterAct Action",
            res.get("deviceCustomString4").get(0));
    Assert.assertEquals("forescout-02",
            res.get("deviceNameOrIp").get(0));
    Assert.assertEquals("forescout-02",
            res.get("dvchost").get(0));
    Assert.assertEquals("Host Compliancy Status",
            res.get("cs3Label").get(0));
    Assert.assertEquals("Host Compliancy Status",
            res.get("deviceCustomString3Label").get(0));
    Assert.assertEquals("Kevin_Mitchell",
            res.get("destinationUserName").get(0));
    Assert.assertEquals("Kevin_Mitchell",
            res.get("duser").get(0));
    Assert.assertEquals("loglogicrocks.com",
            res.get("destinationNtDomain").get(0));
    Assert.assertEquals("loglogicrocks.com",
            res.get("dntdom").get(0));
    Assert.assertEquals("Operating System",
            res.get("cs1").get(0));
    Assert.assertEquals("Operating System",
            res.get("deviceCustomString1").get(0));
    Assert.assertEquals("Updated",
            res.get("cs2").get(0));
    Assert.assertEquals("Updated",
            res.get("deviceCustomString2").get(0));
    Assert.assertEquals("wks-105",
            res.get("destinationHostName").get(0));
    Assert.assertEquals("wks-105",
            res.get("dhost").get(0));
    Assert.assertEquals("yes",
            res.get("cs3").get(0));
    Assert.assertEquals("yes",
            res.get("deviceCustomString3").get(0));
    Assert.assertEquals("yes",
            res.get("cs3").get(0));
    Assert.assertEquals("yes",
            res.get("deviceCustomString3").get(0));

  }

}
