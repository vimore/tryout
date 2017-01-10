/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.parser.avro;


import com.securityx.flume.log.avro.Event;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.kitesdk.morphline.api.Record;

/**
 *
 * @author jyrialhon
 */
public class TestCustomAvroParserTestForBoreas extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForBoreas(String testName) {
    super(testName);
// temporarly commented
//    this.morphlineFile = "logcollection-script-selector-command-list.conf";
//    this.morphlineId = "logcollectionselector";
    this.morphlineFile = "logcollection-parser-main.conf";
    this.morphlineId = "parsermain";
  }

  //
//    /**
//     * Test of getEventParser method, of class AvroParser.
//     */
//    public void testGetEventParser() throws Exception {
//        OutUtils.printOut("getEventParser");
//        AvroParser instance = AvroParser.BuildParser();
//        MorphlineParser expResult = null;
//        MorphlineParser result = instance.getEventParser();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of parseOnly method, of class AvroParser.
//     */
//    public void testParseOnly() throws Exception {
//        OutUtils.printOut("parseOnly");
//        Record[] in = null;
//        AvroParser instance = AvroParser.BuildParser();
//        Record[] expResult = null;
//        Record[] result = instance.parseOnly(in);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of shutdown method, of class AvroParser.
//     */
//    public void testShutdown() throws Exception {
//        OutUtils.printOut("shutdown");
//        AvroParser instance = AvroParser.BuildParser();
//        instance.shutdown();
//    }
//
//    /**
//     * Test from avro src file to avro outFile
//     */



  //

  //



  @Test
  public void testDhcpdGrantIsoForBoreas() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Mar 19 09:28:04 11.22.33.44 dhcpd: [INFO] TYPE=GRANT IP=10.30.50.70 MAC=2:0:36:4b:d1:27:1b HOSTNAME=BTP012345 DOMAIN=nat.bt.comLEASETIME=259200 DHCPSERVER=system-server-01";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("{logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("destinationMacAddress", "0:36:4b:d1:27:1b", out.get("destinationMacAddress").get(0));
    assertEquals("destinationHostName", "BTP012345", out.get("destinationHostName").get(0));
    assertEquals("deviceAddress", "11.22.33.44", out.get("deviceAddress").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    //Thu, 19 Mar 2015 09:28:04 GMT
    assertEquals("startTime", 1458379684000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "BTP012345", out.get("destinationNameOrIp").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("destinationAddress", "10.30.50.70", out.get("destinationAddress").get(0));
    assertEquals("deviceNameOrIp", "11.22.33.44", out.get("deviceNameOrIp").get(0));
    assertEquals("destinationDnsDomain", "nat.bt.comLEASETIME=259200", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }


  @Test
  public void testDhcpdRenewIsoForBoreas() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Jul  8 16:12:08 32.112.2.56 dhcpd: [INFO] TYPE=RENEW IP=40.64.107.77 MAC=1:52:23:f3:2d:ee:58 HOSTNAME=ABC157266 DOMAIN=nat.bt.com LEASETIME=43200 DHCPSERVER=icore-cvnls-77";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("destinationMacAddress", "52:23:f3:2d:ee:58", out.get("destinationMacAddress").get(0));
    assertEquals("destinationHostName", "ABC157266", out.get("destinationHostName").get(0));
    assertEquals("deviceAddress", "32.112.2.56", out.get("deviceAddress").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    //08 Jul 2015 16:12:08 GMT
    // assertEquals("startTime", 1436371928000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "ABC157266", out.get("destinationNameOrIp").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("destinationAddress", "40.64.107.77", out.get("destinationAddress").get(0));
    assertEquals("deviceNameOrIp", "32.112.2.56", out.get("deviceNameOrIp").get(0));
    assertEquals("destinationDnsDomain", "nat.bt.com", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }

  @Test
  public void testDhcpdAckForBoreas() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Jul  8 16:12:12 32.112.5.56 dhcpd: DHCPACK on 10.189.69.204 to 71:a4:e1:a8:66:dc (ABC582781) via 10.189.68.2";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("destinationMacAddress", "71:a4:e1:a8:66:dc", out.get("destinationMacAddress").get(0));
    assertEquals("destinationHostName", "ABC582781", out.get("destinationHostName").get(0));
    assertEquals("syslogMessage", "dhcpd: DHCPACK on 10.189.69.204 to 71:a4:e1:a8:66:dc (ABC582781) via 10.189.68.2", out.get("syslogMessage").get(0));
    assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("startTime", 1467994332000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "10.189.68.2", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("destinationAddress", "10.189.69.204", out.get("destinationAddress").get(0));
    assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

  }


    @Test
    public void testDhcpdAckAltfFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:02 32.112.2.56 dhcpd: DHCPACK to 40.64.253.158 (7e:34:b3:4b:33:aa) via bond0";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("startTime", 1467994322000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "7e:34:b3:4b:33:aa", out.get("destinationMacAddress").get(0));
        assertEquals("deviceInterface", "bond0", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "40.64.253.158", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "32.112.2.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
        assertEquals("deviceAddress", "32.112.2.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testDhcpdRequestfFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:12 32.112.5.56 dhcpd: DHCPREQUEST for 10.189.69.204 from 71:a4:e1:a8:66:dc (ABC165621) via 10.189.68.2";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("syslogMessage", "dhcpd: DHCPREQUEST for 10.189.69.204 from 71:a4:e1:a8:66:dc (ABC165621) via 10.189.68.2", out.get("syslogMessage").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994332000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "10.189.68.2", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceHostName", "ABC165621", out.get("sourceHostName").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "71:a4:e1:a8:66:dc", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
        assertEquals("sourceAddress", "10.189.69.204", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testDhcpdRequest2fFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:11:59 32.112.5.56 dhcpd: DHCPREQUEST for 88.211.176.35 (32.112.5.56) from 56:f3:b6:0d:33:e6 (ABC173248) via 123.55.250.3";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceNameOrIp", "ABC173248", out.get("sourceNameOrIp").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994319000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "123.55.250.3", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceHostName", "ABC173248", out.get("sourceHostName").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "56:f3:b6:0d:33:e6", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
        assertEquals("sourceAddress", "88.211.176.35", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    //
    @Test
    public void testDhcpdRequest3fFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:08 32.112.9.55 dhcpd: DHCPREQUEST for 6.60.97.177 (32.112.5.56) from 36:b1:85:ff:e5:3d via 6.60.97.2: lease owned by peer";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceAddress", "32.112.9.55", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994328000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "6.60.97.2:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.9.55", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "36:b1:85:ff:e5:3d", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
        assertEquals("sourceAddress", "6.60.97.177", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testDhcpdRequest4fFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:11:55 32.112.9.55 dhcpd: DHCPREQUEST for 192.168.1.5 (192.168.5.172) from 5f:31:dd:38:c2:b6 via 10.189.219.2: wrong network.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("syslogMessage", "dhcpd: DHCPREQUEST for 192.168.1.5 (192.168.5.172) from 5f:31:dd:38:c2:b6 via 10.189.219.2: wrong network.", out.get("syslogMessage").get(0));
        assertEquals("deviceAddress", "32.112.9.55", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994315000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "10.189.219.2:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.9.55", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "5f:31:dd:38:c2:b6", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
        assertEquals("sourceAddress", "192.168.1.5", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testDhcpdRealeasefFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:29:46 32.112.5.56 dhcpd: DHCPRELEASE of 10.189.206.240 from ae:45:f3:7e:dd:e4 via bond0 (found)";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("startTime", 1467995386000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "ae:45:f3:7e:dd:e4", out.get("destinationMacAddress").get(0));
        assertEquals("deviceInterface", "bond0", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("syslogMessage", "dhcpd: DHCPRELEASE of 10.189.206.240 from ae:45:f3:7e:dd:e4 via bond0 (found)", out.get("syslogMessage").get(0));
        assertEquals("destinationAddress", "10.189.206.240", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPRELEASE", out.get("cefSignatureId").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testDhcpdRealease2fFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:29:45 32.112.5.56 dhcpd: DHCPRELEASE of 10.189.206.240 from ae:45:f3:7e:dd:e4 (NEFAL4-3) via bond0 (found)";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "ae:45:f3:7e:dd:e4", out.get("destinationMacAddress").get(0));
        assertEquals("destinationHostName", "NEFAL4-3", out.get("destinationHostName").get(0));
        assertEquals("syslogMessage", "dhcpd: DHCPRELEASE of 10.189.206.240 from ae:45:f3:7e:dd:e4 (NEFAL4-3) via bond0 (found)", out.get("syslogMessage").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467995385000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "bond0", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "10.189.206.240", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPRELEASE", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdRealease3fFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:28:28 32.112.5.56 dhcpd: DHCPRELEASE of 102.23.50.90 from 44:14:f3:7c:51:de (B011265BEABC2) via bond0 (found)";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "44:14:f3:7c:51:de", out.get("destinationMacAddress").get(0));
        assertEquals("destinationHostName", "B011265BEABC2", out.get("destinationHostName").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467995308000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "bond0", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "102.23.50.90", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPRELEASE", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdNackfFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:05 32.112.5.56 dhcpd: DHCPNAK on 10.189.219.197 to 36:b1:85:ff:e5:3d via 6.60.97.3";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("startTime", 1467994325000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "36:b1:85:ff:e5:3d", out.get("destinationMacAddress").get(0));
        assertEquals("deviceInterface", "6.60.97.3", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "10.189.219.197", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPNACK", out.get("cefSignatureId").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscoverFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:09 32.112.2.56 dhcpd: DHCPDISCOVER from 7e:34:b3:a5:6b:8f via 23.106.243.131: unknown network segment";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceAddress", "32.112.2.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994329000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "23.106.243.131:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.2.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "7e:34:b3:a5:6b:8f", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover2FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:08 202.170.160.74 dhcpd: DHCPDISCOVER from 45:d3:80:7a:bb:4d (ABCD-E0139438) via 202.170.74.3";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceNameOrIp", "ABCD-E0139438", out.get("sourceNameOrIp").get(0));
        assertEquals("deviceAddress", "202.170.160.74", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994328000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "202.170.74.3", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "202.170.160.74", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceHostName", "ABCD-E0139438", out.get("sourceHostName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "45:d3:80:7a:bb:4d", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover3FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:08 32.112.9.55 dhcpd: DHCPDISCOVER from 36:b1:85:ff:e5:3d (ABC025230) via 6.60.97.3";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceNameOrIp", "ABC025230", out.get("sourceNameOrIp").get(0));
        assertEquals("deviceAddress", "32.112.9.55", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994328000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "6.60.97.3", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.9.55", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceHostName", "ABC025230", out.get("sourceHostName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "36:b1:85:ff:e5:3d", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover4FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:05 32.112.9.55 dhcpd: DHCPDISCOVER from 36:b1:85:ff:e5:3d (ABC025230) via 6.60.97.3: load balance to peer ICORE-ABCDE-24.nexus.btintra.com-ICORE-TEFLO-24.nexus.btintra.com";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceNameOrIp", "ABC025230", out.get("sourceNameOrIp").get(0));
        assertEquals("deviceAddress", "32.112.9.55", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994325000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "6.60.97.3:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.9.55", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceHostName", "ABC025230", out.get("sourceHostName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "36:b1:85:ff:e5:3d", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover5FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:11:55 32.112.9.55 dhcpd: DHCPDISCOVER from 5f:31:dd:38:c2:b6 (EFH521A6D7AC429) via 10.189.219.3: load balance to peer ICORE-ABCDE-24.nexus.btintra.com-ICORE-TEFLO-24.nexus.btintra.com";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceNameOrIp", "EFH521A6D7AC429", out.get("sourceNameOrIp").get(0));
        assertEquals("deviceAddress", "32.112.9.55", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994315000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "10.189.219.3:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.9.55", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceHostName", "EFH521A6D7AC429", out.get("sourceHostName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "5f:31:dd:38:c2:b6", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover6FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:11:58 32.112.5.56 dhcpd: DHCPDISCOVER from 56:f3:b6:0d:33:e6 via 123.55.250.2";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994318000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "123.55.250.2", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "56:f3:b6:0d:33:e6", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover7FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:36:17 32.112.4.52 dhcpd: DHCPDISCOVER from 42:b6:9e:f7:01:e4 via 62.159.19.2: network 62.159.19.0/24: no free leases";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceAddress", "32.112.4.52", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467995777000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "62.159.19.2:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.4.52", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "42:b6:9e:f7:01:e4", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }


    @Test
    public void testDhcpdDiscover8FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:36:17 32.112.4.52 dhcpd: DHCPDISCOVER from 42:b6:9e:f7:01:e4 via 62.159.19.2: network 62.159.19.0/24: no free leases";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceAddress", "32.112.4.52", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467995777000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "62.159.19.2:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.4.52", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "42:b6:9e:f7:01:e4", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDiscover9FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:11:55 202.170.1.145 dhcpd: DHCPDISCOVER from 53:e8:94:cb:fa:a4 (AB_CDE_10th-AB23) via 202.170.44.2";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceNameOrIp", "AB_CDE_10th-AB23", out.get("sourceNameOrIp").get(0));
        assertEquals("deviceAddress", "202.170.1.145", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994315000L, out.get("startTime").get(0));
        assertEquals("deviceInterface", "202.170.44.2", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "202.170.1.145", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceHostName", "AB_CDE_10th-AB23", out.get("sourceHostName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "53:e8:94:cb:fa:a4", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdOfferFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:09 32.112.5.56 dhcpd: DHCPOFFER on 36.70.7.151 to 56:4e:7e:f4:c2:bc (ABC083499) via 36.70.7.3";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "56:4e:7e:f4:c2:bc", out.get("destinationMacAddress").get(0));
        assertEquals("destinationHostName", "ABC083499", out.get("destinationHostName").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994329000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "ABC083499", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceInterface", "36.70.7.3", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("destinationAddress", "36.70.7.151", out.get("destinationAddress").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdOfferNoMACFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:12:09 32.112.5.56 dhcpd: DHCPOFFER on 36.70.7.151 to \"\" (ABC083499) via 36.70.7.3";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "ABC083499", out.get("destinationHostName").get(0));
        assertEquals("deviceAddress", "32.112.5.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994329000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "ABC083499", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceInterface", "36.70.7.3", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.5.56", out.get("deviceNameOrIp").get(0));
        assertEquals("destinationAddress", "36.70.7.151", out.get("destinationAddress").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdOffer1FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:11:59 202.170.1.145 dhcpd: DHCPOFFER on 202.170.44.139 to 53:e8:94:cb:fa:a4 (AB_CDE_10th-AB23) via 202.170.44.2";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "53:e8:94:cb:fa:a4", out.get("destinationMacAddress").get(0));
        assertEquals("destinationHostName", "AB_CDE_10th-AB23", out.get("destinationHostName").get(0));
        assertEquals("deviceAddress", "202.170.1.145", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467994319000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "AB_CDE_10th-AB23", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceInterface", "202.170.44.2", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "202.170.1.145", out.get("deviceNameOrIp").get(0));
        assertEquals("destinationAddress", "202.170.44.139", out.get("destinationAddress").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdOffer2FormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:29:35 202.170.1.145 dhcpd: DHCPOFFER on 13.155.87.60 to 22:11:f4:ea:b6:23 via 13.155.87.60";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("startTime", 1467995375000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "22:11:f4:ea:b6:23", out.get("destinationMacAddress").get(0));
        assertEquals("deviceInterface", "13.155.87.60", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "13.155.87.60", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "202.170.1.145", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("deviceAddress", "202.170.1.145", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testDhcpdDeclineFormatForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<13>Jul  8 16:00:12 32.112.6.56 dhcpd: DHCPDECLINE of 62.159.20.237 from 91:46:b4:c1:2d:22 (HELAD0926E5726) via 62.159.20.3: abandoned";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationMacAddress", "91:46:b4:c1:2d:22", out.get("destinationMacAddress").get(0));
        assertEquals("destinationHostName", "HELAD0926E5726", out.get("destinationHostName").get(0));
        assertEquals("deviceAddress", "32.112.6.56", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1467993612000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "HELAD0926E5726", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceInterface", "62.159.20.3:", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("deviceNameOrIp", "32.112.6.56", out.get("deviceNameOrIp").get(0));
        assertEquals("destinationAddress", "62.159.20.237", out.get("destinationAddress").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    //#Fields: date time time-taken c-ip sc-status s-action sc-bytes cs-bytes cs-method cs-uri-scheme cs-host cs-uri-port cs-uri-path cs-uri-query cs-username cs-auth-group s-supplier-name rs(Content-Type) cs(Referer) cs(User-Agent) sc-filter-result cs-categories x-virus-id s-ip s-action x-exception-id r-ip
  //2015-10-06 11:56:47 522 172.23.138.12 200 TCP_NC_MISS 572 372 GET http quotecast.vwdservices.com 80 /POLLING/4f540977-81ab-4753-81aa-f3c92cb32cc7 - - - quotecast.vwdservices.com application/javascript;%20charset=US-ASCII http://www.telegraaf.nl/dft/ "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)" OBSERVED "Financial Services" - 147.151.27.51 TCP_NC_MISS - 213.244.168.121

  @Test
  public void testBluecoatForBoreas() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();

    String msg = "2015-10-06 11:56:47 522 172.23.138.12 200 TCP_NC_MISS 572 372 GET http quotecast.vwdservices.com 80 /POLLING/4f540977-81ab-4753-81aa-f3c92cb32cc7 - - - quotecast.vwdservices.com application/javascript;%20charset=US-ASCII http://www.telegraaf.nl/dft/ \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\" OBSERVED \"Financial Services\" - 147.151.27.51 TCP_NC_MISS - 213.244.168.121";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("deviceAction", "TCP_NC_MISS", out.get("deviceAction").get(0));
    assertEquals("requestPath", "/POLLING/4f540977-81ab-4753-81aa-f3c92cb32cc7", out.get("requestPath").get(0));
    assertEquals("sourceNameOrIp", "172.23.138.12", out.get("sourceNameOrIp").get(0));
    assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    assertEquals("bytesIn", 372, out.get("bytesIn").get(0));
    assertEquals("startTime", 1444132607000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "quotecast.vwdservices.com", out.get("destinationNameOrIp").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("destinationDnsDomain", "vwdservices.com", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("requestClientApplication", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)", out.get("requestClientApplication").get(0));
    assertEquals("deviceEventCategory", "Financial Services", out.get("deviceEventCategory").get(0));
    assertEquals("bytesOut", 572, out.get("bytesOut").get(0));
    assertEquals("sourceAddress", "172.23.138.12", out.get("sourceAddress").get(0));
    assertEquals("devicePolicyAction", "OBSERVED", out.get("devicePolicyAction").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("destinationHostName", "quotecast", out.get("destinationHostName").get(0));
    assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
    assertEquals("responseContentType", "application/javascript;%20charset=US-ASCII", out.get("responseContentType").get(0));
    assertEquals("deviceCustomString2", "522", out.get("deviceCustomString2").get(0));
    assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
    assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
    assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
    assertEquals("deviceAddress", "147.151.27.51", out.get("deviceAddress").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
    assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
    assertEquals("requestReferer", "http://www.telegraaf.nl/dft/", out.get("requestReferer").get(0));
    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

  }

    // the following Test deals with livestream : bluecoat stream to e8 via syslog.
    // Fake-Field-JYR-workarround introduced as a work arround in bluecoat field list described in logparsingjob.conf for hourly
    // parsing
    // parsing

    @Test
    public void testBluecoatFromLabForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String bluecoatHeader="#Fields: date time time-taken c-ip cs-username cs-auth-group s-supplier-name s-supplier-ip s-supplier-country s-supplier-failures x-exception-id sc-filter-result cs-categories cs(Referer) Fake-Field-JYR-workarround sc-status s-action cs-method rs(Content-Type) cs-uri-scheme cs-host cs-uri-port cs-uri-path cs-uri-query cs-uri-extension cs(User-Agent) s-ip sc-bytes cs-bytes x-virus-id x-bluecoat-application-name x-bluecoat-application-operation cs-threat-risk\r";
        String msg = "2015-11-30 06:34:27 30490 192.168.23.241 - - 192.168.23.238 192.168.23.238 None - - PROXIED \"Technology/Internet\" -  0 TUNNELED unknown - ssl cloud-dc.amp.sourcefire.com 443 / - - - 192.168.23.81 420 468 - \"none\" \"none\" 4\r";
        ByteBuffer buf = ByteBuffer.wrap(bluecoatHeader.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("deviceAction", "TUNNELED", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "192.168.23.241", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("bytesIn", 468, out.get("bytesIn").get(0));
        assertEquals("startTime", 1448865267000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "cloud-dc.amp.sourcefire.com", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("destinationDnsDomain", "amp.sourcefire.com", out.get("destinationDnsDomain").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "-", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "Technology/Internet", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 420, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "192.168.23.241", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("destinationHostName", "cloud-dc", out.get("destinationHostName").get(0));
        assertEquals("requestScheme", "ssl", out.get("requestScheme").get(0));
        assertEquals("responseContentType", "-", out.get("responseContentType").get(0));
        assertEquals("deviceCustomString2", "30490", out.get("deviceCustomString2").get(0));
        assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
        assertEquals("applicationProtocol", "Unknown", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "Unknown", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "192.168.23.81", out.get("deviceAddress").get(0));
        assertEquals("destinationAddress", "192.168.23.238", out.get("destinationAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "0", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "unknown", out.get("requestMethod").get(0));
        assertEquals("requestReferer", "-", out.get("requestReferer").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testBluecoat2ForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2015-11-17 23:21:19 121 10.215.63.190 200 TCP_CLIENT_REFRESH 4034 259 GET http next-services.apps.microsoft.com 80 /ApplicationRevocation/RevokedApps - - - next-services.apps.microsoft.com text/html - \"Windows Store/1.0\" OBSERVED \"Technology/Internet\" - 132.146.1.143 TCP_CLIENT_REFRESH - 157.56.194.72";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("deviceAction", "TCP_CLIENT_REFRESH", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/ApplicationRevocation/RevokedApps", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "10.215.63.190", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("bytesIn", 259, out.get("bytesIn").get(0));
        assertEquals("startTime", 1447802479000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "next-services.apps.microsoft.com", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("destinationDnsDomain", "apps.microsoft.com", out.get("destinationDnsDomain").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Windows Store/1.0", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "Technology/Internet", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 4034, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "10.215.63.190", out.get("sourceAddress").get(0));
        assertEquals("devicePolicyAction", "OBSERVED", out.get("devicePolicyAction").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "next-services", out.get("destinationHostName").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("responseContentType", "text/html", out.get("responseContentType").get(0));
        assertEquals("deviceCustomString2", "121", out.get("deviceCustomString2").get(0));
        assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "132.146.1.143", out.get("deviceAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestReferer", "-", out.get("requestReferer").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

    }


    //DHCP EVENTS
    //SourceIp=192.168.23.11\tAgentLogFile=DhcpSrvLog-Fri\tAgentProtocol=WindowsDHCP\tID=25\tDate=11/20/15\tTime=00:00:44\tDescription=0 leases expired and 0 leases deleted\tIP Address=\tHost Name=\tMAC Address=\tUser Name=\tTransactionID=0\tQResult=6\tProbationtime=\tCorrelationID=\tDhcid=\tVendorClass(Hex)=\tVendorClass(ASCII)=\tUserClass(Hex)=\tUserClass(ASCII)=\tRelayAgentInformation=\tDnsRegError=0\t
    @Test
    public void testQradarWindowsDHCPForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "SourceIp=192.168.23.11\tAgentLogFile=DhcpSrvLog-Fri\tAgentProtocol=WindowsDHCP\tID=11\tDate=11/20/15\tTime=02:16:18\tDescription=Renew\tIP Address=192.168.23.221\tHost Name=CAL-XA-1-03.cyber.lab\tMAC Address=005056AAD63E\tUser Name=\tTransactionID=1818837492\tQResult=0\tProbationtime=\tCorrelationID=\tDhcid=\tVendorClass(Hex)=0x4D53465420352E30\tVendorClass(ASCII)=MSFT 5.0\tUserClass(Hex)=\tUserClass(ASCII)=\tRelayAgentInformation=\tDnsRegError=0\t";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("startTime", 1447985778000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationNameOrIp", "CAL-XA-1-03.cyber.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationMacAddress", "005056AAD63E", out.get("destinationMacAddress").get(0));
        assertEquals("destinationHostName", "CAL-XA-1-03", out.get("destinationHostName").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
        assertEquals("destinationAddress", "192.168.23.221", out.get("destinationAddress").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "cyber.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "qradar-windows-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "somehost", out.get("deviceHostName").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    }

    //WINDOWS EVENTS
    //4624
    @Test
    public void testQradarWindows4624ForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Nov 19 11:07:35 superintendent2.cyber.lab AgentDevice=WindowsLog\tAgentLogFile=Security\tSource=Microsoft-Windows-Security-Auditing\tComputer=superintendent2.cyber.lab\tUser=\tDomain=\tEventID=4624\tEventIDCode=4624\tEventType=8\tEventCategory=12544\tRecordNumber=227088446\tTimeGenerated=1447931196566\tTimeWritten=1447931196566\tMessage=An account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    Impersonation Level:  %%1840    New Logon:   Security ID:  S-1-5-21-3158436021-3638059681-3118127365-1346   Account Name:  INTEL-EPO-01$   Account Domain:  CYBER   Logon ID:  0xf3b7a3b   Logon GUID:  6f47ba17-15e-4269-556b-8708a67647e6    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name:    Source Network Address: 10.20.10.10   Source Port:  50652    Detailed Authentication Information:   Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).    The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.    The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The impersonation level field indicates the extent to which a process in the logon session can impersonate.    The authentication information fields provide detailed information about this specific logon request.   - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("sourcePort", 50652, out.get("sourcePort").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "superintendent2", out.get("deviceHostName").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("startTime", 1447931255000L, out.get("startTime").get(0));
        assertEquals("destinationLogonID", "0xf3b7a3b", out.get("destinationLogonID").get(0));
        assertEquals("deviceDnsDomain", "cyber.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
//        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceAddress", "10.20.10.10", out.get("sourceAddress").get(0));
//        assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationLogonGUID", "6f47ba17-15e-4269-556b-8708a67647e6", out.get("destinationLogonGUID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
        assertEquals("destinationUserName", "INTEL-EPO-01$".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
        assertEquals("deviceNameOrIp", "superintendent2.cyber.lab", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "CYBER", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-3158436021-3638059681-3118127365-1346", out.get("destinationSecurityID").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }


    //4625
    @Test
    public void testQradarWindows4625ForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Nov 19 12:25:06 superintendent2.cyber.lab AgentDevice=WindowsLog\tAgentLogFile=Security\tSource=Microsoft-Windows-Security-Auditing\tComputer=superintendent2.cyber.lab\tUser=\tDomain=\tEventID=4625\tEventIDCode=4625\tEventType=16\tEventCategory=12544\tRecordNumber=227137904\tTimeGenerated=1447935869429\tTimeWritten=1447935869429\tMessage=An account failed to log on.    Subject:   Security ID:  S-1-5-18   Account Name:  SUPERINTENDENT2$   Account Domain:  CYBER   Logon ID:  0x3e7    Logon Type:   3    Account For Which Logon Failed:   Security ID:  S-1-0-0   Account Name:  admin   Account Domain:  CYBER    Failure Information:   Failure Reason:  %%2313   Status:   0xc000006d   Sub Status:  0xc000006a    Process Information:   Caller Process ID: 0x244   Caller Process Name: C:\\Windows\\System32\\lsass.exe    Network Information:   Workstation Name: SUPERINTENDENT2   Source Network Address: 192.168.23.37   Source Port:  44904    Detailed Authentication Information:   Logon Process:  Advapi     Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon request fails. It is generated on the computer where access was attempted.    The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network).    The Process Information fields indicate which account and process on the system requested the logon.    The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The authentication information fields provide detailed information about this specific logon request.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("sourcePort", 44904, out.get("sourcePort").get(0));
        assertEquals("sourceNameOrIp", "SUPERINTENDENT2", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "superintendent2", out.get("deviceHostName").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("startTime", 1447935906000L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "cyber.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessID", "0x244", out.get("sourceProcessID").get(0));
        assertEquals("sourceUserName", "SUPERINTENDENT2$".toLowerCase(), out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceAddress", "192.168.23.37", out.get("sourceAddress").get(0));
        assertEquals("sourceNtDomain", "CYBER", out.get("sourceNtDomain").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("status", "0xc000006d", out.get("status").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("logonProcess", "Advapi", out.get("logonProcess").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("destinationUserName", "admin".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("authenticationPackage", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("authenticationPackage").get(0));
        assertEquals("deviceNameOrIp", "superintendent2.cyber.lab", out.get("deviceNameOrIp").get(0));
        assertEquals("subStatus", "0xc000006a", out.get("subStatus").get(0));
        assertEquals("sourceHostName", "SUPERINTENDENT2", out.get("sourceHostName").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4625-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "CYBER", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-0-0", out.get("destinationSecurityID").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    }
    //4648
    @Test
    public void testQradarWindows4648ForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Nov 19 11:07:40 superintendent.cyber.lab AgentDevice=WindowsLog\tAgentLogFile=Security\tSource=Microsoft-Windows-Security-Auditing\tComputer=superintendent.cyber.lab\tUser=\tDomain=\tEventID=4648\tEventIDCode=4648\tEventType=8\tEventCategory=12544\tRecordNumber=557071430\tTimeGenerated=1447931162259\tTimeWritten=1447931162259\tMessage=A logon was attempted using explicit credentials.    Subject:   Security ID:  S-1-5-18   Account Name:  SUPERINTENDENT$   Account Domain:  CYBER   Logon ID:  0x3e7   Logon GUID:  0-0-0-0000-000000000000    Account Whose Credentials Were Used:   Account Name:  Administrator   Account Domain:  CYBER   Logon GUID:  0-0-0-0000-000000000000    Target Server:   Target Server Name: localhost   Additional Information: localhost    Process Information:   Process ID:  0x230   Process Name:  C:\\Windows\\System32\\lsass.exe    Network Information:   Network Address: 192.168.23.37   Port:   60267    This event is generated when a process attempts to log on an account by explicitly specifying that accountâs credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationLogonGUID", "0-0-0-0000-000000000000", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationHostName", "localhost", out.get("destinationHostName").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourcePort", 60267, out.get("sourcePort").get(0));
        assertEquals("sourceNameOrIp", "192.168.23.37", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "superintendent", out.get("deviceHostName").get(0));
        assertEquals("destinationUserName", "Administrator".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("startTime", 1447931260000L, out.get("startTime").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("destinationNameOrIp", "localhost", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceDnsDomain", "cyber.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "SUPERINTENDENT$".toLowerCase(), out.get("sourceUserName").get(0));
        assertEquals("deviceNameOrIp", "superintendent.cyber.lab", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4648-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "CYBER", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "192.168.23.37", out.get("sourceAddress").get(0));
        assertEquals("sourceNtDomain", "CYBER", out.get("sourceNtDomain").get(0));
        assertEquals("sourceLogonGUID", "0-0-0-0000-000000000000", out.get("sourceLogonGUID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    }
    //4678  MISSING : sample not found

    //4679  MISSING : sample not found

    //TANIUM

    //Running process
    // <134>1 2015-11-24T08:33:19.721679+00:00 TANIUM Tanium 4628 - [E8---Running-Processes@017472 Computer-Name=BTG189212.iuser.iroot.adidom.com Path=d:\\program-files-(x86)\\mcafee\\epolicy-orchestrator\\eventparser.exe MD5-Hash=ec3f02af637bedb476bd7dfadf810261 Count=1]
    @Test
    public void testTaniumRunningProcessesToHostProcessMefForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<134>1 2015-11-24T08:33:19.721679+00:00 TANIUM Tanium 4628 - [E8---Running-Processes@017472 Computer-Name=BTG189212.iuser.iroot.adidom.com Path=d:\\program-files-(x86)\\mcafee\\epolicy-orchestrator\\eventparser.exe MD5-Hash=ec3f02af637bedb476bd7dfadf810261 Count=1] ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("processFileMd5", "ec3f02af637bedb476bd7dfadf810261", out.get("processFileMd5").get(0));
        assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "BTG189212", out.get("deviceHostName").get(0));
        assertEquals("startTime", 1448353999721L, out.get("startTime").get(0));
        assertEquals("deviceNameOrIp", "BTG189212.iuser.iroot.adidom.com", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "tanium_connect-Running-Processes-with-MD5-Hash", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("processFilePath", "d:\\program-files-(x86)\\mcafee\\epolicy-orchestrator\\eventparser.exe", out.get("processFilePath").get(0));
        assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
    }
    //Autorun
    // <134>1 2015-11-24T08:33:06.650847+00:00 TANIUM Tanium 4628 - [E8---AutoRun@017472 Computer-Name=CAL-Citrix-01.cyber.lab Entry-Location=HKLM\\System\\CurrentControlSet\\Services Entry=mvumis Enabled=enabled Category=Drivers Profile=System-wide Description=Marvell-Flash-Controller-Driver Publisher=Marvell-Semiconductor,-Inc. Image-Path=c:\\windows\\system32\\drivers\\mvumis.sys Version=1.0.5.1015 Launch-String=System32\\drivers\\mvumis.sys MD5-Hash=B8C35C94DCB2DFEAF03BB42131F2F77F SHA-1-Hash=369C839C0B28866C5BF03E3F2CEBE44F5ED3E7F7 SHA-256-Hash=33B2962A0373EADB6D2B26EC146487D39F7DADB3 Count=1]
    @Test
    public void testTaniumAutorunToHostJobMefForBoreas() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<134>1 2015-11-24T08:33:06.650847+00:00 TANIUM Tanium 4628 - [E8---AutoRun@017472 Computer-Name=CAL-Citrix-01.cyber.lab Entry-Location=HKLM\\System\\CurrentControlSet\\Services Entry=mvumis Enabled=enabled Category=Drivers Profile=System-wide Description=Marvell-Flash-Controller-Driver Publisher=Marvell-Semiconductor,-Inc. Image-Path=c:\\windows\\system32\\drivers\\mvumis.sys Version=1.0.5.1015 Launch-String=System32\\drivers\\mvumis.sys MD5-Hash=B8C35C94DCB2DFEAF03BB42131F2F77F SHA-1-Hash=369C839C0B28866C5BF03E3F2CEBE44F5ED3E7F7 SHA-256-Hash=33B2962A0373EADB6D2B26EC146487D39F7DADB3 Count=1]";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("processFileMd5", "B8C35C94DCB2DFEAF03BB42131F2F77F", out.get("processFileMd5").get(0));
        assertEquals("jobCmd", "System32\\drivers\\mvumis.sys", out.get("jobCmd").get(0));
        assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "CAL-Citrix-01", out.get("deviceHostName").get(0));
        assertEquals("jobName", "mvumis", out.get("jobName").get(0));
        assertEquals("startTime", 1448353986650L, out.get("startTime").get(0));
        assertEquals("deviceNameOrIp", "CAL-Citrix-01.cyber.lab", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "tanium_connect-AutoRun-Program-Details", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
        assertEquals("processFilePath", "c:\\windows\\system32\\drivers\\mvumis.sys", out.get("processFilePath").get(0));
        assertEquals("parserOutFormat", "HostJobMef", out.get("parserOutFormat").get(0));
    }

  /*@Test
  public void testUnMactchedFile() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);

    InputStream fis = new FileInputStream("/Users/macadmin/Downloads/logs.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ;
    String line;
    long cpt = 0;
    while ((line = br.readLine()) !=  null){

      //InputStream ais = new ByteArrayInputStream(line.getBytes());
      //DataInputStream dais = new DataInputStream(ais);
      //Decoder decoder = DecoderFactory.get().jsonDecoder(Event.SCHEMA$, dais);
     //DatumReader<Event> reader = new SpecificDatumReader<Event>(Event.SCHEMA$);
     // avroEvent = reader.read(null, decoder);

      //JsonReader reader = Json.createReader(new StringReader(line));
      //JsonObject object = reader.readObject();
      //reader.close();

      //OutUtils.printOut(object.getString("rawLog"));
      //ByteBuffer buf = ByteBuffer.wrap(object.getString("rawLog").getBytes());
      avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));

      List<Map<String, List<Object>>> output = instance.parse(avroEvent);
      if ((! line.startsWith("#")) && line.length() > 0) {
            try {
                Map<String, List<Object>> out = output.get(0);
                OutUtils.printOut(out.toString());

                assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
            } catch (Exception e) {
                OutUtils.printOut("failed on line " + cpt + " (" + e.getMessage() + ")");
                throw e;
            }
        }
      cpt+=1;
    }
  }
   */


}