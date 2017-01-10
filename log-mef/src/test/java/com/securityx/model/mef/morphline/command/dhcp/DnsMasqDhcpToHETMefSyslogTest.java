package com.securityx.model.mef.morphline.command.dhcp;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class DnsMasqDhcpToHETMefSyslogTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(DnsMasqDhcpToHETMefSyslogTest.class);

  public DnsMasqDhcpToHETMefSyslogTest(String name) {
    super(name);
    this.morphlineId = "dnsmasq-dhcp";
    this.confFile = "hetmef-dnsmasq-dhcp.conf";
  }

  /*
   <30>May  9 09:52:40 dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e
   <30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPDISCOVER(br0) b8:e8:56:03:c9:7e
   <30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPNAK(br0) 192.168.1.192 b8:e8:56:03:c9:7e wrong address
   <30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPOFFER(br0) 192.168.10.208 b8:e8:56:03:c9:7e
   <30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPREQUEST(br0) 192.168.1.192 b8:e8:56:03:c9:7e

   */
  private Record buildRecord(String line) {
    Record input = new Record();
    input.put("dnsmasqInput", line);
    input.put("logCollectionHost", "somehost");
    input.put("logCollectionType", "syslog");
    return input;
  }

  @Test
   public void test_DhcpAck() throws FileNotFoundException {
   String line = "<30>May  9 09:52:40 dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record res = this.outCommand.getRecord(0);
   assertEquals("destinationAddress", "192.168.10.208", res.get("destinationAddress").get(0));
   assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", res.get("destinationMacAddress").get(0));
   assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
   assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
   assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
   assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
   assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
   assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
   assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
   assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
   assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
      // Sat, 09 May 2015 16:52:40 GMT
      assertEquals("startTime", 1462812760000L, res.get("startTime").get(0));
   }
   
   @Test
   public void test_DhcpDiscover() throws FileNotFoundException {
   String line = "<30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPDISCOVER(br0) b8:e8:56:03:c9:7e";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record res = this.outCommand.getRecord(0);

   assertEquals("cefSignatureId", "DHCPDISCOVER", res.get("cefSignatureId").get(0));
   assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
   assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
   assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
   assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
   assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
   assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
   assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
   assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
   assertEquals("sourceMacAddress", "b8:e8:56:03:c9:7e", res.get("sourceMacAddress").get(0));
   assertEquals("startTime", 1462812759000L, res.get("startTime").get(0));

   }
   
   @Test
   public void test_DhcpNak() throws FileNotFoundException {
   String line = "<30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPNAK(br0) 192.168.1.192 b8:e8:56:03:c9:7e wrong address";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record res = this.outCommand.getRecord(0);

   assertEquals("destinationAddress", "192.168.1.192", res.get("destinationAddress").get(0));
   assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", res.get("destinationMacAddress").get(0));
   assertEquals("cefSignatureId", "DHCPNAK", res.get("cefSignatureId").get(0));
   assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
   assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
   assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
   assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
   assertEquals("eventOutcome", "wrong address", res.get("eventOutcome").get(0));
   assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
   assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
   assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
   assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
   assertEquals("startTime", 1462812759000L, res.get("startTime").get(0));

   }   
   @Test
   public void test_DhcpOffer() throws FileNotFoundException {
   String line = "<30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPOFFER(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record res = this.outCommand.getRecord(0);

   assertEquals("destinationAddress", "192.168.10.208", res.get("destinationAddress").get(0));
   assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", res.get("destinationMacAddress").get(0));
   assertEquals("cefSignatureId", "DHCPOFFER", res.get("cefSignatureId").get(0));
   assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
   assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
   assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
   assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
   assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
   assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
   assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
   assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
   assertEquals("startTime", 1462812759000L, res.get("startTime").get(0));

   }
   
  @Test
  public void test_DhcpRequest() throws FileNotFoundException {
    String line = "<30>May  9 09:52:39 dnsmasq-dhcp[304]: DHCPREQUEST(br0) 192.168.1.192 b8:e8:56:03:c9:7e";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);

    assertEquals("cefSignatureId", "DHCPREQUEST", res.get("cefSignatureId").get(0));
    assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
    assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
    assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
    assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
    assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
    assertEquals("sourceAddress", "192.168.1.192", res.get("sourceAddress").get(0));
    assertEquals("sourceMacAddress", "b8:e8:56:03:c9:7e", res.get("sourceMacAddress").get(0));
    assertEquals("startTime", 1462812759000L, res.get("startTime").get(0));

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


//    @Test
//    public void test_DhcpDateIssue() throws FileNotFoundException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat
//                ("MMM  dd HH:mm:ss ", Locale.US);
//        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
//        SimpleDateFormat dateFormat2 = new SimpleDateFormat
//                ("YYYY MMM  dd HH:mm:ss ", Locale.US);
//        dateFormat2.setTimeZone(TimeZone.getTimeZone("PST"));
//        long epoch = 1446058691000L;
//        for (int i =0; i<24; i++) {
//
//            String dateStr = dateFormat.format(new Date(epoch));
//            //OutUtils.printOut();
//            String line = "<30>"+dateStr+"dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
//            boolean result = doTest(buildRecord(line));
//            assertEquals(true, result);
//            Record res = this.outCommand.getRecord(0);
//            this.outCommand.clear();
//            long parsedEpoch = (Long) res.get("startTime").get(0);
//            OutUtils.printOut("--> "+i+" "+epoch+" '"+dateStr+"'parsed date : "+ parsedEpoch+" : "+ dateFormat2.format(new Date(parsedEpoch))+" "+(parsedEpoch-epoch));
//            epoch+=3600*1000;
//        }
//        assertTrue(false);
//        String line = "<30>May  9 09:52:40 dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
//        boolean result = doTest(buildRecord(line));
//        assertEquals(true, result);
//        Record res = this.outCommand.getRecord(0);
//        assertEquals("destinationAddress", "192.168.10.208", res.get("destinationAddress").get(0));
//        assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", res.get("destinationMacAddress").get(0));
//        assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
//        assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
//        assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
//        assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
//        assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
//        assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
//        assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
//        assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
//        assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
//        // Sat, 09 May 2015 16:52:40 GMT
//        assertEquals("startTime", 1431190360000L, res.get("startTime").get(0));
//    }
}
