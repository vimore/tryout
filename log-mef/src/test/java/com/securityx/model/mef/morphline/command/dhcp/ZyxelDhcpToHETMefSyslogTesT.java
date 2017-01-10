package com.securityx.model.mef.morphline.command.dhcp;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class ZyxelDhcpToHETMefSyslogTesT extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(ZyxelDhcpToHETMefSyslogTesT.class);

  public ZyxelDhcpToHETMefSyslogTesT(String name) {
    super(name);
    this.morphlineId = "zyxel_dhcp";
    this.confFile = "hetmef-zyxel-dhcp.conf";
  }

  /*
   src="0.0.0.0:0" dst="0.0.0.0:0" msg="Requested 192.168.2.49 from android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)" note="DHCP Request" user="unknown" devID="b0b2dcbe45d3" cat="DHCP"
   src="0.0.0.0:0" dst="0.0.0.0:0" msg="Requested 192.168.1.104 from (3C:15:C2:C9:CA:A6)" note="DHCP Request" user="unknown" devID="b0b2dcbe45d3" cat="DHCP"

   src="0.0.0.0:0" dst="0.0.0.0:0" msg="DHCP server offered 192.168.2.49 to android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)" note="DHCP Offer" user="unknown" devID="b0b2dcbe45d3" cat="DHCP"
   src="0.0.0.0:0" dst="0.0.0.0:0" msg="DHCP server offered 192.168.2.30 to Harishs-MBP(3C:15:C2:C9:CA:A6)" note="DHCP Offer" user="unknown" devID="b0b2dcbe45d3" cat="DHCP"

   src="0.0.0.0:0" dst="0.0.0.0:0" msg="DHCP server assigned 192.168.2.49 to android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)" note="DHCP ACK" user="unknown" devID="b0b2dcbe45d3" cat="DHCP"
   src="0.0.0.0:0" dst="0.0.0.0:0" msg="DHCP server assigned 192.168.2.30 to Harishs-MBP(3C:15:C2:C9:CA:A6)" note="DHCP ACK" user="unknown" devID="b0b2dcbe45d3" cat="DHCP"

   */
  private Record buildRecord(String line) {
    Record input = new Record();
    input.put("zyxelMessage", line);
    input.put("logCollectionHost", "somehost");
    input.put("logCollectionType", "syslog");
    input.put("receiptTimeStr", "Dec  1 12:54:47");
    return input;
  }

  @Test
  public void test_DhcpRequest() throws FileNotFoundException {
    String line = "src=\"0.0.0.0:0\" dst=\"0.0.0.0:0\" msg=\"Requested 192.168.2.49 from android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)\" note=\"DHCP Request\" user=\"unknown\" devID=\"b0b2dcbe45d3\" cat=\"DHCP\"";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    assertEquals("cefSignatureId", "DHCPREQUEST", res.get("cefSignatureId").get(0));
    assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
    assertEquals("externalLogSourceType", "zyxel-dhcp", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("msg", "Requested 192.168.2.49 from android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)", res.get("msg").get(0));
    assertEquals("receiptTimeStr", "Dec  1 12:54:47", res.get("receiptTimeStr").get(0));
    assertEquals("sourceAddress", "192.168.2.49", res.get("sourceAddress").get(0));
    assertEquals("sourceHostName", "android-5d9a8e930cf598c9", res.get("sourceHostName").get(0));
    assertEquals("sourceMacAddress", "F4:09:D8:A1:41:A5", res.get("sourceMacAddress").get(0));
    assertEquals("sourceUserName", "unknown", res.get("sourceUserName").get(0));
    assertEquals("startTime", 1448974487000L, res.get("startTime").get(0));
    //Mon, 01 Dec 2014 12:54:47 GMT
    assertEquals("user", "unknown", res.get("user").get(0));
  }
  @Test
  public void test_DhcpOffer() throws FileNotFoundException {
    String line = "src=\"0.0.0.0:0\" dst=\"0.0.0.0:0\" msg=\"DHCP server offered 192.168.2.49 to android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)\" note=\"DHCP Offer\" user=\"unknown\" devID=\"b0b2dcbe45d3\" cat=\"DHCP\"";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    assertEquals("cefSignatureId", "DHCPOFFER", res.get("cefSignatureId").get(0));
    assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
    assertEquals("externalLogSourceType", "zyxel-dhcp", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("msg", "DHCP server offered 192.168.2.49 to android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)", res.get("msg").get(0));
    assertEquals("receiptTimeStr", "Dec  1 12:54:47", res.get("receiptTimeStr").get(0));
    assertEquals("destinationAddress", "192.168.2.49", res.get("destinationAddress").get(0));
    assertEquals("destinationNameOrIp", "android-5d9a8e930cf598c9", res.get("destinationNameOrIp").get(0));
    assertEquals("destinationMacAddress", "F4:09:D8:A1:41:A5", res.get("destinationMacAddress").get(0));
    assertEquals("sourceUserName", "unknown", res.get("sourceUserName").get(0));
    assertEquals("startTime", 1448974487000L, res.get("startTime").get(0));
    assertEquals("user", "unknown", res.get("user").get(0));
  }

  @Test
  public void test_DhcpAck() throws FileNotFoundException {
    String line = "src=\"0.0.0.0:0\" dst=\"0.0.0.0:0\" msg=\"DHCP server assigned 192.168.2.30 to Harishs-MBP(3C:15:C2:C9:CA:A6)\" note=\"DHCP ACK\" user=\"unknown\" devID=\"b0b2dcbe45d3\" cat=\"DHCP\"";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
    assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
    assertEquals("externalLogSourceType", "zyxel-dhcp", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("msg", "DHCP server assigned 192.168.2.30 to Harishs-MBP(3C:15:C2:C9:CA:A6)", res.get("msg").get(0));
    assertEquals("receiptTimeStr", "Dec  1 12:54:47", res.get("receiptTimeStr").get(0));
    assertEquals("destinationAddress", "192.168.2.30", res.get("destinationAddress").get(0));
    assertEquals("destinationNameOrIp", "Harishs-MBP", res.get("destinationNameOrIp").get(0));
    assertEquals("destinationMacAddress", "3C:15:C2:C9:CA:A6", res.get("destinationMacAddress").get(0));
    assertEquals("sourceUserName", "unknown", res.get("sourceUserName").get(0));
    assertEquals("startTime", 1448974487000L, res.get("startTime").get(0));
    assertEquals("user", "unknown", res.get("user").get(0));
  }

//  public void test_samples() throws FileNotFoundException {
//
//    try {
//      File f = new File("./src/test/resources/access.log");
//      BufferedReader br = new BufferedReader(new FileReader(f));
//      String line;
//      int i = 0;
//      while ((line = br.readLine()) != null) { // while loop begins here
//        i++;
//        boolean result = doTest(line);
//        if (!result) {
//          System.res.println("DO NOT MATCH :" + line);
//        }
//      } // end while
//      System.res.println("parsed : " + i + " lines");
//
////      String line = "1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
////      boolean result = doTest(line);
////      assertEquals(true, result);
////      Record res = this.resCommand.getRecord(0);
//    } catch (IOException ex) {
//      java.util.logging.Logger.getLogger(WebProxySquidTest.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
}
