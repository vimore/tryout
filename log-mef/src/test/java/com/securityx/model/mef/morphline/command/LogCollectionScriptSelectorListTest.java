package com.securityx.model.mef.morphline.command;

import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class LogCollectionScriptSelectorListTest extends LogCollectionAbstractTest {

  public LogCollectionScriptSelectorListTest() {
    super(LogCollectionScriptSelectorListTest.class.toString());
    this.morphlineId = "logcollectionselector";
    this.confFile = "logcollection-script-selector-command-list.conf";
  }

  @Test
  public void test() throws FileNotFoundException {
    String line = "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    boolean result = doTest(line);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record r = outCommand.getRecord(0);
    //Assert.assertEquals(r.get("Foo"), "bar");
  }

  @Test
  public void test1() throws FileNotFoundException {
    String line = "Sep 23 10:14:45 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [23/Sep/2013:10:14:43 +0000] \"GET / HTTP/1.1\" 200 1494";
    boolean result = doTest(line);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record r = outCommand.getRecord(0);

    //Assert.assertEquals(r.get("Foo"), "bar");
  }

  @Test
  public void test2() throws FileNotFoundException {
    String line = "Nov 12 14:24:12 hivedev1 sshd[4924]: Disconnecting: Too many authentication failures for root";
    boolean result = doTest(line);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record r = outCommand.getRecord(0);

    //Assert.assertEquals(r.get("Foo"), "bar");
  }

  @Test
  public void test22() throws FileNotFoundException {
    String line = "Nov 12 14:24:12 hivedev1 sshd[4924]: bouloub";
    boolean result = doTest(line);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record r = outCommand.getRecord(0);
    this.morphlineHarness.shutdown();

    //Assert.assertEquals(r.get("Foo"), "bar");
  }
  
  @Test
  public void test3() throws FileNotFoundException {
    String line = "10/31/2013 23:59:58 cefsource CEF:0|ForeScout Technologies|CounterAct|6.3.4|NONCOMPLIANCE|host is not compliant|5|cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Security policy  cs2=Do not comply  cs3=no cs4=CounterAct Action dmac=00:03:47:24:46:10 dst=10.10.1.1 dntdom=loglogic.com dhost=wks-104 duser=John_Montefusco dvc=1.1.1.1 dvchost=forescout-01 rt=1328814511000";
    boolean result = doTest(line);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record r = outCommand.getRecord(0);

    //Assert.assertEquals(r.get("Foo"), "bar");
  }

  @Test
  public void test4() throws FileNotFoundException {
    String line = "Oct 10 18:28:47 security1 sudo: ec2-user : TTY=pts/0 ; PWD=/home/ec2-user ; USER=root ; COMMAND=/bin/bash";
    boolean result = doTest(line);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record r = outCommand.getRecord(0);

    //Assert.assertEquals(r.get("Foo"), "bar");
  }

  /* obsolete out of scope at now, must be reworked to  be uncommented
  @Test
  public void test5() throws FileNotFoundException {
    Record r = new Record();
    r.put("logCollectionCategory","firewall");
    r.put("logCollectionContainer","C:\\Users\\jyrialhon.LOGLOGIC\\Documents\\NetBeansProjects\\Test\\xef\\com\\xef\\model\\log-mef\\.\\src\\test\\resources\\hivedev1.labs.lan.firewall.1384678800654");
    r.put("logCollectionHost","hivedev1.labs.lan");
    r.put("logCollectionTime",1384679055637L);
    r.put("logCollectionType","firewall");
    r.put("message","id=firewall sn=0006B129195C time=\"2013-11-17 01:04:15\" fw=71.6.1.234 pri=6 c=1024 m=537 msg=\"Connection Closed\" n=5921677 src=173.238.116.209:34209:X1:d173-238-116-209.home4.cgocable.net dst=10.10.30.100:53:X0: proto=udp/dns sent=74");
    boolean result = doTest(r);
    assertTrue("line successfully processed", result);
    OutUtils.printOut(outCommand.getNumRecords());
    Assert.assertTrue(outCommand.getNumRecords() == 1);
    Record out = outCommand.getRecord(0);

    //Assert.assertEquals(r.get("Foo"), "bar");
  } */

  
  
}
