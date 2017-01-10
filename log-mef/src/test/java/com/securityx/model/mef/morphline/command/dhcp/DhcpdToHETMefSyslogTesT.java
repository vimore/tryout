package com.securityx.model.mef.morphline.command.dhcp;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class DhcpdToHETMefSyslogTesT extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(DhcpdToHETMefSyslogTesT.class);
  private Record input ;
  private Record res;

  public DhcpdToHETMefSyslogTesT(String name) {
    super(name);
    this.morphlineId = "dhcpd";
    this.confFile = "hetmef-dhcpd.conf";
  }


  private void  buildRecord(String line) {
    input = new Record();
    input.put("dhcpdInput", line);
    input.put("logCollectionHost", "192.168.40.20");
    input.put("logCollectionType", "syslog");
    input.put("receiptTime", 1433966400000L);

  }

  @Test
  public void test_DhcpRenewLease() throws FileNotFoundException {
    String line = "/opt/qip/usr/bin/dhcpd[27271]: DHCP_RenewLease: Host=SEPF47F35A29A7A IP=10.1.248.75 MAC=f47f35a29a7a Domain=dhcp.amer.blabla.net ClientID=01f47f35a29a7a";
    boolean result=false;
    // loop added for profiling
//    for (int i=0; i<50000; i++){
      buildRecord(line) ;
      result= doTest(input);
      res = this.outCommand.getRecord(0);
      this.outCommand.clear();
//    }
    assertEquals(true, result);
    OutUtils.printOut(res);
    assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
    assertEquals("deviceNameOrIp", "192.168.40.20", res.get("deviceNameOrIp").get(0));
    assertEquals("externalLogSourceType", "dhcpd", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "192.168.40.20", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("destinationAddress", "10.1.248.75", res.get("destinationAddress").get(0));
    assertEquals("destinationHostName", "SEPF47F35A29A7A", res.get("destinationHostName").get(0));
    assertEquals("destinationDnsDomain", "dhcp.amer.blabla.net", res.get("destinationDnsDomain").get(0));
    assertEquals("destinationMacAddress", "f47f35a29a7a", res.get("destinationMacAddress").get(0));
    assertEquals("startTime", 1433966400000L, res.get("startTime").get(0));
  }

  @Test
  public void test_DhcpDHCP_GrantLease() throws FileNotFoundException {
    String line = "/opt/qip/usr/bin/dhcpd[18798]: DHCP_GrantLease: Host=android-31c8cb0ca3d29519 IP=10.1.1.233 MAC=f895c7a52f18 Domain= ClientID=01f895c7a52f18 ";
    boolean result=false;
    // loop added for profiling
//    for (int i=0; i<50000; i++){
    buildRecord(line) ;
    result= doTest(input);
    res = this.outCommand.getRecord(0);
    this.outCommand.clear();
//    }
    assertEquals(true, result);
    OutUtils.printOut(res);
    assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
    assertEquals("destinationAddress", "10.1.1.233", res.get("destinationAddress").get(0));
    assertEquals("destinationHostName", "android-31c8cb0ca3d29519", res.get("destinationHostName").get(0));
    assertEquals("destinationMacAddress", "f895c7a52f18", res.get("destinationMacAddress").get(0));
    assertEquals("deviceAddress", "192.168.40.20", res.get("deviceAddress").get(0));
    assertEquals("deviceNameOrIp", "192.168.40.20", res.get("deviceNameOrIp").get(0));
    assertEquals("deviceProcessId", 18798, res.get("deviceProcessId").get(0));
    assertEquals("deviceProcessName", "/opt/qip/usr/bin/dhcpd", res.get("deviceProcessName").get(0));
    assertEquals("externalLogSourceType", "dhcpd", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "192.168.40.20", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("startTime", 1433966400000L, res.get("startTime").get(0));
  }

  @Test
  public void test_DhcpDHCPKVPGrant() throws FileNotFoundException {
    String line = "dhcpd: [INFO] TYPE=GRANT IP=10.30.50.70 MAC=2:0:36:4b:d1:27:1b HOSTNAME=BTP012345 DOMAIN=nat.bt.com LEASETIME=259200 DHCPSERVER=system-server-01";
    boolean result=false;
    // loop added for profiling
//    for (int i=0; i<50000; i++){
    buildRecord(line) ;
    result= doTest(input);
    res = this.outCommand.getRecord(0);
    this.outCommand.clear();
//    }
    assertEquals(true, result);
    OutUtils.printOut(res);
    assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
    assertEquals("destinationAddress", "10.30.50.70", res.get("destinationAddress").get(0));
    assertEquals("destinationNameOrIp", "BTP012345", res.get("destinationNameOrIp").get(0));
    assertEquals("destinationMacAddress", "0:36:4b:d1:27:1b", res.get("destinationMacAddress").get(0));
    assertEquals("deviceNameOrIp", "system-server-01", res.get("deviceNameOrIp").get(0));
    assertEquals("deviceProcessName", "dhcpd", res.get("deviceProcessName").get(0));
    assertEquals("externalLogSourceType", "dhcpd", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "192.168.40.20", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("startTime", 1433966400000L, res.get("startTime").get(0));
  }

}
