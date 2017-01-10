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
public class TestCustomAvroParserTestForHermes extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForHermes(String testName) {
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


//  Nov  9 17:38:10 stuxsh01.st7082.example.com CEF: 0|unknown |dhcpd|unknown|DHCPACK|unknown|0|shost=HD7082DE32448B cs1Label=deviceInboundInterface cs6Label=raw cs6=Nov  9 15:38:10 stuxsh01 dhcpd: DHCPACK on 10.195.189.142 to 40:83:de:32:44:8b (HD7082DE32448B) via eth0:2 cs3Label=src_mac cs2=10.195.189.142 cs3=40:83:de:32:44:8b cs1=eth0:2 dproc=dhcpd cs2Label=src_ip
@Test
public void testDhcpdAckSplunkForHermes() throws IOException, Exception {
  AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
  Event avroEvent = new Event();
  String msg = "Nov  9 17:38:10 stuxsh01.st7082.example.com CEF: 0|unknown |dhcpd|unknown|DHCPACK|unknown|0|shost=HD7082DE32448B cs1Label=deviceInboundInterface cs6Label=raw cs6=Nov  9 15:38:10 stuxsh01 dhcpd: DHCPACK on 10.195.189.142 to 40:83:de:32:44:8b (HD7082DE32448B) via eth0:2 cs3Label=src_mac cs2=10.195.189.142 cs3=40:83:de:32:44:8b cs1=eth0:2 dproc=dhcpd cs2Label=src_ip";
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
  assertEquals("destinationMacAddress", "40:83:de:32:44:8b", out.get("destinationMacAddress").get(0));
  assertEquals("destinationHostName", "HD7082DE32448B", out.get("destinationHostName").get(0));
  assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
  assertEquals("deviceHostName", "stuxsh01", out.get("deviceHostName").get(0));
  assertEquals("startTime", 1447090690000L, out.get("startTime").get(0));
  assertEquals("destinationNameOrIp", "HD7082DE32448B", out.get("destinationNameOrIp").get(0));
  assertEquals("deviceDnsDomain", "st7082.example.com", out.get("deviceDnsDomain").get(0));
  assertEquals("deviceInterface", "eth0:2", out.get("deviceInterface").get(0));
  assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
  assertEquals("destinationAddress", "10.195.189.142", out.get("destinationAddress").get(0));
  assertEquals("deviceNameOrIp", "stuxsh01.st7082.example.com", out.get("deviceNameOrIp").get(0));
  assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
  assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
  assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
  assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
  assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


}

  @Test
  public void testDhcpdAckIsoForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2015-07-31T09:12:55-04:00 atl-ern-dhcpb dhcpd: DHCPACK to 172.16.47.209 (80:86:f2:ce:d5:3d) via eth0";
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
    assertEquals("startTime", 1438348375000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("destinationMacAddress", "80:86:f2:ce:d5:3d", out.get("destinationMacAddress").get(0));
    assertEquals("deviceInterface", "eth0", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("syslogMessage", "dhcpd: DHCPACK to 172.16.47.209 (80:86:f2:ce:d5:3d) via eth0", out.get("syslogMessage").get(0));
    assertEquals("destinationAddress", "172.16.47.209", out.get("destinationAddress").get(0));
    assertEquals("deviceNameOrIp", "atl-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "atl-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }
  @Test
  public void testDhcpdRequestIsoForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2015-07-31T09:12:55-04:00 atl-ern-dhcpb dhcpd: DHCPREQUEST for 10.0.0.21 from 28:5a:eb:56:9e:df via 172.25.44.2: ignored (unknown subnet).";
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
    assertEquals("syslogMessage", "dhcpd: DHCPREQUEST for 10.0.0.21 from 28:5a:eb:56:9e:df via 172.25.44.2: ignored (unknown subnet).", out.get("syslogMessage").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "atl-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1438348375000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "172.25.44.2:", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("deviceNameOrIp", "atl-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("sourceMacAddress", "28:5a:eb:56:9e:df", out.get("sourceMacAddress").get(0));
    assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
    assertEquals("sourceAddress", "10.0.0.21", out.get("sourceAddress").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }

  @Test
  public void testDhcpdRequestAltFormatIsoForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2015-07-31T09:12:55-04:00 atl-ern-dhcpb dhcpd: DHCPREQUEST for 172.25.192.67 (151.140.107.119) from d0:22:be:1a:29:4f via 172.25.192.2: ignored (unknown subnet).";
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
    assertEquals("syslogMessage", "dhcpd: DHCPREQUEST for 172.25.192.67 (151.140.107.119) from d0:22:be:1a:29:4f via 172.25.192.2: ignored (unknown subnet).", out.get("syslogMessage").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "atl-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1438348375000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "172.25.192.2:", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("deviceNameOrIp", "atl-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("sourceMacAddress", "d0:22:be:1a:29:4f", out.get("sourceMacAddress").get(0));
    assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
    assertEquals("sourceAddress", "172.25.192.67", out.get("sourceAddress").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }

  //2015-07-31T09:12:55-04:00 aus-ern-dhcpb dhcpd: DHCPNAK on 10.0.0.21 to 28:5a:eb:56:9e:df via 172.25.44.2
  @Test
  public void testDhcpdNakIsoForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2015-07-31T09:12:55-04:00 atl-ern-dhcpb dhcpd: DHCPREQUEST for 172.25.192.67 (151.140.107.119) from d0:22:be:1a:29:4f via 172.25.192.2: ignored (unknown subnet).";
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
    assertEquals("syslogMessage", "dhcpd: DHCPREQUEST for 172.25.192.67 (151.140.107.119) from d0:22:be:1a:29:4f via 172.25.192.2: ignored (unknown subnet).", out.get("syslogMessage").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "atl-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1438348375000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "172.25.192.2:", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("deviceNameOrIp", "atl-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("sourceMacAddress", "d0:22:be:1a:29:4f", out.get("sourceMacAddress").get(0));
    assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
    assertEquals("sourceAddress", "172.25.192.67", out.get("sourceAddress").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }
  @Test
  public void testDhcpdInformForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Oct  9 01:24:05 aus-ern-dhcpb dhcpd: DHCPINFORM from 151.140.179.159 via 151.140.178.2";
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
    assertEquals("startTime", 1444371845000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("deviceInterface", "151.140.178.2", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("syslogMessage", "dhcpd: DHCPINFORM from 151.140.179.159 via 151.140.178.2", out.get("syslogMessage").get(0));
    assertEquals("deviceNameOrIp", "aus-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPINFORM", out.get("cefSignatureId").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "aus-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }


  @Test
  public void testDhcpdInformForHermes2() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Oct 12 20:05:09 aus-ern-dhcpa dhcpd: DHCPINFORM from 172.22.32.82 via 172.22.32.2: unknown subnet for client address 172.22.32.82";
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
    assertEquals("startTime", 1444698309000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }


  @Test
  public void testDhcpdDiscoverForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Oct  9 01:24:35 aus-ern-dhcpb dhcpd: DHCPDISCOVER from 00:14:38:5c:c2:ed via 151.140.184.4";
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
    assertEquals("syslogMessage", "dhcpd: DHCPDISCOVER from 00:14:38:5c:c2:ed via 151.140.184.4", out.get("syslogMessage").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "aus-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1444371875000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "151.140.184.4", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("deviceNameOrIp", "aus-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("sourceMacAddress", "00:14:38:5c:c2:ed", out.get("sourceMacAddress").get(0));
    assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }

  //2015-07-31T09:19:58-04:00 atl-ern-dhcpa dhcpd: DHCPRELEASE of 172.31.255.161 from 00:15:70:8a:93:b9 (HD5085708A93B9) via eth0 (found)

  @Test
  public void testDhcpdReleaseIsoForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2015-07-31T09:19:58-04:00 atl-ern-dhcpa dhcpd: DHCPRELEASE of 172.31.255.161 from 00:15:70:8a:93:b9 (HD5085708A93B9) via eth0 (found)";
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
    assertEquals("destinationMacAddress", "00:15:70:8a:93:b9", out.get("destinationMacAddress").get(0));
    assertEquals("destinationHostName", "HD5085708A93B9", out.get("destinationHostName").get(0));
    assertEquals("syslogMessage", "dhcpd: DHCPRELEASE of 172.31.255.161 from 00:15:70:8a:93:b9 (HD5085708A93B9) via eth0 (found)", out.get("syslogMessage").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "atl-ern-dhcpa", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1438348798000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "eth0", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("destinationAddress", "172.31.255.161", out.get("destinationAddress").get(0));
    assertEquals("deviceNameOrIp", "atl-ern-dhcpa", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPRELEASE", out.get("cefSignatureId").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));

    msg = "2015-07-31T09:20:28-04:00 aus-ern-dhcpb dhcpd: DHCPRELEASE of 151.140.134.156 from 7c:ad:74:84:f1:40 via eth0 (found)";
    buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    output = instance.parse(avroEvent);
    out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("startTime", 1438348828000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("destinationMacAddress", "7c:ad:74:84:f1:40", out.get("destinationMacAddress").get(0));
    assertEquals("deviceInterface", "eth0", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("syslogMessage", "dhcpd: DHCPRELEASE of 151.140.134.156 from 7c:ad:74:84:f1:40 via eth0 (found)", out.get("syslogMessage").get(0));
    assertEquals("destinationAddress", "151.140.134.156", out.get("destinationAddress").get(0));
    assertEquals("deviceNameOrIp", "aus-ern-dhcpb", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "DHCPRELEASE", out.get("cefSignatureId").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "aus-ern-dhcpb", out.get("deviceHostName").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
  }



  //MSWinEventLog 0 Security 0 Wed Jul 22 21 51 2015 4624 Microsoft-Windows-Security-Auditing NULL SID Well Known Group Success Audit LoadServer.redwood.e8 Logon An account was successfully logged on. Subject: Security ID: S-1-0-0 Account Name: - Account Domain: - Logon ID: 0x0 Logon Type: 3 New Logon: Security ID: S-1-5-21-1482476501-484061587-839522115-43602 Account Name: Carson_Palmer Account Domain: redwood.e8 Logon ID: 0x221ebb06f Logon GUID: {6A904B52-6626-9211-C58E-A18CE597005F} Process Information: Process ID: 0x0 Process Name: - Network Information: Workstation Name: Source Network Address: 192.168.1.36 Source Port: 55637 Detailed Authentication Information: Logon Process: Kerberos Authentication Package: Kerberos Transited Services: - Package Name (NTLM only): - Key Length: 0 This event is generated when a logon session is created. It is generated on the computer that was accessed. The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service

    //2015-09-04T08:56:00-04:00 atl-ern-lb01-3-netscaler.example.com 2015-09-04 12:56:00 55 172.31.181.102 - - - OBSERVED "Business/Economy" - 200 TCP_NC_MISS GET image/gif http dw.cbsi.com 80 /levt/video/e.gif ?ts=1441371360953&event=end&siteid=175&onid=&ptid=&edid=&ursuid=&pid=&cid=&pguid=&v16=&v17=&v18=&v19=&v20=&v21=desktop%20web&v22=KNW9sXqXoJAM634_15h7q4NOyKx81hn1&v23=cbsicbssportssite&v25=anon&rsid=cbsicbssportssite&ednm=us&srchost=www.cbssports.com&componentid=2a96f48343a1fb90ba58b171caaa75da&mso=&playertime=661&medastid=595&medid=519239747508&medrls=pup1aCshQYMB&medlength=33&medtime=32&mednum=5&part=cbssports gif "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3; MS-RTC LM 8)" 207.11.1.74 335 985 -

  @Test
  public void testBluecoatForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2015-09-04T08:56:00-04:00 atl-ern-lb01-3-netscaler.example.com 2015-09-04 12:56:00 55 172.31.181.102 - - - OBSERVED \"Business/Economy\" - 200 TCP_NC_MISS GET image/gif http dw.cbsi.com 80 /levt/video/e.gif ?ts=1441371360953&event=end&siteid=175&onid=&ptid=&edid=&ursuid=&pid=&cid=&pguid=&v16=&v17=&v18=&v19=&v20=&v21=desktop%20web&v22=KNW9sXqXoJAM634_15h7q4NOyKx81hn1&v23=cbsicbssportssite&v25=anon&rsid=cbsicbssportssite&ednm=us&srchost=www.cbssports.com&componentid=2a96f48343a1fb90ba58b171caaa75da&mso=&playertime=661&medastid=595&medid=519239747508&medrls=pup1aCshQYMB&medlength=33&medtime=32&mednum=5&part=cbssports gif \"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3; MS-RTC LM 8)\" 207.11.1.74 335 985 -";
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
    assertEquals("requestPath", "/levt/video/e.gif", out.get("requestPath").get(0));
    assertEquals("requestQuery", "?ts=1441371360953&event=end&siteid=175&onid=&ptid=&edid=&ursuid=&pid=&cid=&pguid=&v16=&v17=&v18=&v19=&v20=&v21=desktop%20web&v22=KNW9sXqXoJAM634_15h7q4NOyKx81hn1&v23=cbsicbssportssite&v25=anon&rsid=cbsicbssportssite&ednm=us&srchost=www.cbssports.com&componentid=2a96f48343a1fb90ba58b171caaa75da&mso=&playertime=661&medastid=595&medid=519239747508&medrls=pup1aCshQYMB&medlength=33&medtime=32&mednum=5&part=cbssports", out.get("requestQuery").get(0));
    assertEquals("sourceNameOrIp", "172.31.181.102", out.get("sourceNameOrIp").get(0));
    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "atl-ern-lb01-3-netscaler", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1441371360000L, out.get("startTime").get(0));
    assertEquals("bytesIn", 985, out.get("bytesIn").get(0));
    assertEquals("destinationNameOrIp", "dw.cbsi.com", out.get("destinationNameOrIp").get(0));
    assertEquals("deviceDnsDomain", "example.com", out.get("deviceDnsDomain").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("destinationDnsDomain", "cbsi.com", out.get("destinationDnsDomain").get(0));
    assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3; MS-RTC LM 8)", out.get("requestClientApplication").get(0));
    assertEquals("bytesOut", 335, out.get("bytesOut").get(0));
    assertEquals("deviceEventCategory", "Business/Economy", out.get("deviceEventCategory").get(0));
    assertEquals("sourceAddress", "172.31.181.102", out.get("sourceAddress").get(0));
    assertEquals("receiptTime", 1441371360000L, out.get("receiptTime").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("devicePolicyAction", "OBSERVED", out.get("devicePolicyAction").get(0));
    assertEquals("destinationHostName", "dw", out.get("destinationHostName").get(0));
    assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
    assertEquals("responseContentType", "image/gif", out.get("responseContentType").get(0));
    assertEquals("deviceCustomString2", "55", out.get("deviceCustomString2").get(0));
    assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
    assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
    assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
    assertEquals("deviceAddress", "207.11.1.74", out.get("deviceAddress").get(0));
    assertEquals("deviceNameOrIp", "atl-ern-lb01-3-netscaler.example.com", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
    assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
    assertEquals("requestReferer", "-", out.get("requestReferer").get(0));
    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

  }
  @Test
  public void testBluecoatAltSyslogFormatForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Sep 10 17:56:41 cpliisk6  207.11.1.74 2015-09-10T13:56:36-04:00 atl-ern-lb02-4-netscaler.example.com 2015-09-10 17:56:36 7 172.29.166.66 - - - OBSERVED \"Business/Economy\" - 200 TCP_HIT GET text/html http seo.bazaarvoice.com 80 /example-b760c1815b8925a982512132f048d178/1999/reviews/product/1/100375216.htm - htm \"Apache-HttpClient/4.3.5 (java 1.5)\" 207.11.1.74 8359 228 -";
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


  }
  @Test
  public void testBluecoatSyslogNewFieldsFormatForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String bluecoatHeader = "<13>Sep 16 15:36:24 cpliisk5.example.com  atl-ern-lb01-3-netscaler.example.com 2015-09-16T11:30:30-04:00 atl-ern-lb01-3-netscaler.example.com #Fields: date time time-taken c-ip cs(X-Forwarded-For) cs-username cs-auth-group x-exception-id sc-filter-result cs-categories cs(Referer) sc-status s-action cs-method rs(Content-Type) cs-uri-scheme cs-host cs-uri-port cs-uri-path cs-uri-query cs-uri-extension cs(User-Agent) s-ip sc-bytes cs-bytes x-virus-id c-uri-address";

    String msg = "<13>Oct  2 17:25:11 cpliisuh.example.com  207.11.1.76 2015-10-02T13:08:15-04:00 atl-ern-lb01-3-netscaler.example.com 2015-10-02 17:08:15 20 172.22.45.20 - - - - OBSERVED \"Shopping\" http://www.example.com/s/niteize?NCNI-5 200 TCP_NC_MISS GET image/jpeg http www.example.com 80 /catalog/productImages/145/33/33bd9652-ca8e-438d-83de-aec758bf53c1_145.jpg - jpg \"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36\" 207.11.1.76 2293 3954 - 96.17.170.27";
    ByteBuffer buf1 = ByteBuffer.wrap(bluecoatHeader.getBytes());
    avroEvent.setBody(buf1);

    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    output = instance.parse(avroEvent);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());


  }

  //
  @Test
  public void testBluecoatSyslogNewFieldsUnmatchedFormatForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String bluecoatHeader = "<13>Sep 16 15:36:24 cpliisk5.example.com  atl-ern-lb01-3-netscaler.example.com 2015-09-16T11:30:30-04:00 atl-ern-lb01-3-netscaler.example.com #Fields: date time time-taken c-ip cs(X-Forwarded-For) cs-username cs-auth-group x-exception-id sc-filter-result cs-categories cs(Referer) sc-status s-action cs-method rs(Content-Type) cs-uri-scheme cs-host cs-uri-port cs-uri-path cs-uri-query cs-uri-extension cs(User-Agent) s-ip sc-bytes cs-bytes x-virus-id c-uri-address";

    String msg = "<13>Oct 13 13:15:05 cpliisug.example.com  207.11.1.78 2015-10-13T09:15:03-04:00 atl-ern-lb03-1-netscaler.example.com 2015-10-13 13:15:03 4 172.22.37.237 - - - invalid_request DENIED \"unavailable\" - 400 TCP_NC_MISS *3 - - - 0 / - - - 207.11.1.78 842 97 - -";
    ByteBuffer buf1 = ByteBuffer.wrap(bluecoatHeader.getBytes());
    avroEvent.setBody(buf1);

    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    output = instance.parse(avroEvent);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());


  }

  @Test
  public void testBluecoatCsXForwardForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String bluecoatHeader = "<13>Sep 16 15:36:24 cpliisk5.example.com  atl-ern-lb01-3-netscaler.example.com 2015-09-16T11:30:30-04:00 atl-ern-lb01-3-netscaler.example.com #Fields: date time time-taken c-ip cs(X-Forwarded-For) cs-username cs-auth-group x-exception-id sc-filter-result cs-categories cs(Referer) sc-status s-action cs-method rs(Content-Type) cs-uri-scheme cs-host cs-uri-port cs-uri-path cs-uri-query cs-uri-extension cs(User-Agent) s-ip sc-bytes cs-bytes x-virus-id c-uri-address";

    String msg = "<13>Oct 13 13:15:05 cpliisug.example.com  207.11.1.78 2015-10-13T09:15:03-04:00 atl-ern-lb03-1-netscaler.example.com 2015-10-13 13:15:03 4 172.22.37.237 192.168.12.12 - - invalid_request DENIED \"unavailable\" - 400 TCP_NC_MISS *3 - - - 0 / - - - 207.11.1.78 842 97 - -";
    ByteBuffer buf1 = ByteBuffer.wrap(bluecoatHeader.getBytes());
    avroEvent.setBody(buf1);

    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    output = instance.parse(avroEvent);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("deviceAction", "TCP_NC_MISS", out.get("deviceAction").get(0));
    assertEquals("requestPath", "/", out.get("requestPath").get(0));
    assertEquals("sourceNameOrIp", "192.168.12.12", out.get("sourceNameOrIp").get(0));
    assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "cpliisug", out.get("deviceHostName").get(0));
    assertEquals("bytesIn", 97, out.get("bytesIn").get(0));
    assertEquals("startTime", 1444742103000L, out.get("startTime").get(0));
    // uuid seems to be different each time - its causing the test case to fail
    assertEquals("destinationNameOrIp", "-", out.get("destinationNameOrIp").get(0));
    assertEquals("deviceDnsDomain", "example.com", out.get("deviceDnsDomain").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("requestClientApplication", "-", out.get("requestClientApplication").get(0));
    assertEquals("deviceEventCategory", "unavailable", out.get("deviceEventCategory").get(0));
    assertEquals("bytesOut", 842, out.get("bytesOut").get(0));
    assertEquals("sourceAddress", "192.168.12.12", out.get("sourceAddress").get(0));
    assertEquals("receiptTime", 1444742105000L, out.get("receiptTime").get(0));
    assertEquals("devicePolicyAction", "DENIED", out.get("devicePolicyAction").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("requestScheme", "-", out.get("requestScheme").get(0));
    assertEquals("responseContentType", "-", out.get("responseContentType").get(0));
    assertEquals("deviceCustomString2", "4", out.get("deviceCustomString2").get(0));
    assertEquals("destinationPort", 0, out.get("destinationPort").get(0));
    assertEquals("applicationProtocol", "Unknown", out.get("applicationProtocol").get(0));
    assertEquals("transportProtocol", "Unknown", out.get("transportProtocol").get(0));
    assertEquals("deviceAddress", "207.11.1.78", out.get("deviceAddress").get(0));
    assertEquals("deviceNameOrIp", "cpliisug.example.com", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "400", out.get("cefSignatureId").get(0));
    assertEquals("requestMethod", "*3", out.get("requestMethod").get(0));
    assertEquals("requestReferer", "-", out.get("requestReferer").get(0));
    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

  }

  //Sep 16 15:11:23 cpliisk5.example.com  207.11.1.74 2015-09-16T11:11:20-04:00 atl-ern-lb04-2-netscaler.example.com 2015-09-16 15:11:20 10 172.22.35.56 - - - OBSERVED \"Technology/Internet\" http://www.example.com/s/254902?NCNI-5 200 TCP_HIT GET application/javascript http cdn1-res.sundaysky.com 80 /vop/t.js - js \"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3; Tablet PC 2.0)\" 207.11.1.74 2974 510 -
  @Test
  public void testBluecoatAlt2SyslogFormatForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Sep 16 15:36:24 cpliisk5.example.com  atl-ern-lb01-3-netscaler.example.com 2015-09-16T11:30:30-04:00 atl-ern-lb01-3-netscaler.example.com 2015-09-16 15:28:23 6 172.22.40.60 - - - OBSERVED \"Sports/Recreation\" http://www.tennis.com/ 304 TCP_HIT GET image/jpeg http cdn.tennis.com 80 /uploads/img/2015/09/10/xxx-6188/photo-band.jpg - jpg \"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36\" 207.11.1.72 357 753 - 54.230.206.152";
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
    assertEquals("deviceAction", "TCP_HIT", out.get("deviceAction").get(0));
    assertEquals("requestPath", "/uploads/img/2015/09/10/xxx-6188/photo-band.jpg", out.get("requestPath").get(0));
    assertEquals("sourceNameOrIp", "172.22.40.60", out.get("sourceNameOrIp").get(0));
    assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "cpliisk5", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1442417303000L, out.get("startTime").get(0));
    assertEquals("bytesIn", 753, out.get("bytesIn").get(0));
    assertEquals("destinationNameOrIp", "cdn.tennis.com", out.get("destinationNameOrIp").get(0));
    assertEquals("deviceDnsDomain", "example.com", out.get("deviceDnsDomain").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("destinationDnsDomain", "tennis.com", out.get("destinationDnsDomain").get(0));
    assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36", out.get("requestClientApplication").get(0));
    assertEquals("deviceEventCategory", "Sports/Recreation", out.get("deviceEventCategory").get(0));
    assertEquals("bytesOut", 357, out.get("bytesOut").get(0));
    assertEquals("sourceAddress", "172.22.40.60", out.get("sourceAddress").get(0));
    assertEquals("receiptTime", 1442432184000L, out.get("receiptTime").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("devicePolicyAction", "OBSERVED", out.get("devicePolicyAction").get(0));
    assertEquals("destinationHostName", "cdn", out.get("destinationHostName").get(0));
    assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
    assertEquals("responseContentType", "image/jpeg", out.get("responseContentType").get(0));
    assertEquals("deviceCustomString2", "6", out.get("deviceCustomString2").get(0));
    assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
    assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
    assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
    assertEquals("deviceAddress", "207.11.1.72", out.get("deviceAddress").get(0));
    assertEquals("deviceNameOrIp", "cpliisk5.example.com", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "304", out.get("cefSignatureId").get(0));
    assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
    assertEquals("requestReferer", "http://www.tennis.com/", out.get("requestReferer").get(0));
    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

  }



  @Test
  public void testBluecoatTruncatedForHermes() throws IOException, Exception {
      AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
      Event avroEvent = new Event();
      String msg = "<13>Sep 11 13:30:01 cpliisk5  207.11.1.68 2015-09-11T09:29:55-04:00 atl-ern-lb01-3-netscaler.example.com 2015-09-11 13:29:55 197 172.22.37.33 - - - OBSERVED \"Web Ads/Analytics\" http://majicatl.hellobeautiful.com/listen-live/ 200 TCP_NC_MISS GET text/xml;%20charset=UTF-8 http c2s-openrtb.liverail.com 80 / ?url=http%3A%2F%2Fliverail-emea.adnxs.com%2Fopenrtb%3Fmember_id%3D3055%26seat_id%3D129556&buy_type=rtb&network_id=129556&partnerId=tp3056&ortb=eyJpZCI6IkxSX0RSYnFURHlyeEsyOWE0anJOIiwiaW1wIjpbeyJpZCI6IjEiLCJkaXNwbGF5bWFuYWdlciI6IkxpdmVSYWlsIiwiaW5zdGwiOjAsInZpZGVvIjp7InN0YXJ0ZGVsYXkiOjAsIm1pbWVzIjpbInZpZGVvXC9tcDQiLCJ2aWRlb1wveC1mbHYiLCJhcHBsaWNhdGlvblwveC1zaG9ja3dhdmUtZmxhc2giLCJpbWFnZVwvanBlZyIsImltYWdlXC9wbmciLCJpbWFnZVwvZ2lmIl0sImgiOjI1MSwidyI6MzAxLCJwcm90b2NvbHMiOlsyLDVdLCJtaW5kdXJhdGlvbiI6MCwibWF4ZHVyYXRpb24iOjE4MCwibGluZWFyaXR5IjoxLCJtaW5iaXRyYXRlIjoyNTYsInBsYXliYWNrbWV0aG9kIjpbM10sImFwaSI6WzEsMl0sImV4dCI6eyJhZHR5cGUiOjN9fX1dLCJzaXRlIjp7ImlkIjoiMTQ2NjM0IiwiZG9tYWluIjoibWFqaWNhdGwuaGVsbG9iZWF1dGlmdWwuY29tIiwicGFnZSI6Imh0dHAlM0";
      ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
      avroEvent.setBody(buf);
      Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
      headers.put("category", "syslog");
      headers.put("hostname", "somehost");
      headers.put("timestamp", "1384693669604");

      avroEvent.setHeaders(headers);
      List<Map<String, List<Object>>> output = instance.parse(avroEvent);
      //Map<String, List<Object>> out = output.get(0);
      OutUtils.printOut(output.toString());
      assertTrue(null!=output && output.size()>0);


  }

  @Test
  public void testBluecoatTruncatedRejectedForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Sep 11 13:30:01 cpliisk5  207.11.1";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    assertTrue(null==output || output.size()==0);


  }
  @Test
  public void testDhcpdRequestForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Sep 11 09:30:02 aus-ern-dhcpa dhcpd: DHCPREQUEST for 172.25.192.67 (151.140.107.119) from d0:22:be:1a:29:4f via 172.25.192.2: ignored (unknown subnet).";
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
    assertEquals("syslogMessage", "dhcpd: DHCPREQUEST for 172.25.192.67 (151.140.107.119) from d0:22:be:1a:29:4f via 172.25.192.2: ignored (unknown subnet).", out.get("syslogMessage").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "aus-ern-dhcpa", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1473604202000L, out.get("startTime").get(0));
    assertEquals("deviceInterface", "172.25.192.2:", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
    assertEquals("deviceNameOrIp", "aus-ern-dhcpa", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
    assertEquals("sourceMacAddress", "d0:22:be:1a:29:4f", out.get("sourceMacAddress").get(0));
    assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
    assertEquals("sourceAddress", "172.25.192.67", out.get("sourceAddress").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));


  }




  //Sep 10 12:16:47 WPR0002DCA0002 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn1Label=eventOutcome externalId=4776 cn2Label=journalName deviceFacility=Microsoft Windows security auditing. dhost=WPR0002DCA0002.amer.example.com cs2=Security cs1=Audit Success


  //Sep 10 12:16:51 WSPRCX579 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn1Label=eventOutcome externalId=4647 cn2Label=journalName deviceFacility=Microsoft Windows security auditing. dhost=WSPRCX579.amer.example.com cs3=AMER\\MXM8315 cs2=Security cs1=Audit Success duser=mxm8315



  //Sep 10 12:16:47 WAPRDC027 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn1Label=eventOutcome externalId=4648 cn2Label=journalName deviceFacility=Microsoft Windows security auditing. dhost=WAPRDC027.amer.example.com cs3=S-1-5-18 cs2=Security cs1=Audit Success duser=WAPRDC027$\\n_svcAAAra1



  //Sep 10 12:17:14 WAPRDC026 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn1Label=eventOutcome externalId=4738 cn2Label=journalName deviceFacility=Microsoft Windows security auditing. dhost=WAPRDC026.amer.example.com cs3=S-1-5-21-450285137-3616678309-1244856752-1374998\\nS-1-5-21-450285137-3616678309-1244856752-1874221 cs2=Security cs1=Audit Success duser=_svcVISAdmin\\nRAS4254




    //





    // 4742
    @Test
    public void testADSplunkCef4742ForHermes() throws IOException, Exception {
      AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
      Event avroEvent = new Event();
      String msg = "Sep 14 15:36:56 WSPRDC111 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=AMER\\HD6331F66B494BF$\\nAMER\\HD6331F66B494BF$ cs2=Security cs1=Audit Success dpriv=- externalId=4742 duser=HD6331F66B494BF$\\nHD6331F66B494BF$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC111.amer.example.com dntdom=AMER\\nAMER cs4=0x26a59e5a9 cs5Label=Sub_Status cs4Label=Logon_ID ";
      ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
      avroEvent.setBody(buf);
      Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
      headers.put("category", "activedirectory");
      headers.put("hostname", "somehost");
      headers.put("timestamp", "1384693669604");

      avroEvent.setHeaders(headers);
      List<Map<String, List<Object>>> output = instance.parse(avroEvent);
      Map<String, List<Object>> out = output.get(0);
      OutUtils.printOut(out.toString());
      assertEquals("privileges","-",out.get("privileges").get(0));
      assertEquals("sourceNtDomain","AMER",out.get("sourceNtDomain").get(0));
      assertEquals("sourceUserName","hd6331f66b494bf$",out.get("sourceUserName").get(0));
      assertEquals("logCollectionCategory","activedirectory",out.get("logCollectionCategory").get(0));
      assertEquals("sourceSecurityID","AMER\\HD6331F66B494BF$",out.get("sourceSecurityID").get(0));
      //assertEquals("uuid","2-e17f25c6-d750-11e5-99b1-ba82299662e2-1645-ef6ab9dbe1a0eb47130c6e3777653a9e",out.get("uuid").get(0));
      assertEquals("cefSignatureId","Security-4742-Audit Success",out.get("cefSignatureId").get(0));
      assertEquals("sourceLogonID","0x26a59e5a9",out.get("sourceLogonID").get(0));
      assertEquals("externalLogSourceType","MSWinEventLog-splunkcef",out.get("externalLogSourceType").get(0));
      assertEquals("destinationSecurityID","AMER\\HD6331F66B494BF$",out.get("destinationSecurityID").get(0));
      assertEquals("logCollectionHost","somehost",out.get("logCollectionHost").get(0));
      assertEquals("destinationUserName","hd6331f66b494bf$",out.get("destinationUserName").get(0));
      assertEquals("receiptTime",1473867416000L,out.get("receiptTime").get(0));
      assertEquals("startTime",1473867416000L,out.get("startTime").get(0));
      assertEquals("parserOutFormat","IAMMef",out.get("parserOutFormat").get(0));
      assertEquals("deviceNameOrIp","WSPRDC111",out.get("deviceNameOrIp").get(0));
      assertEquals("logSourceType","IAMMef",out.get("logSourceType").get(0));
      assertEquals("deviceHostName","WSPRDC111",out.get("deviceHostName").get(0));
      assertEquals("destinationNtDomain","AMER",out.get("destinationNtDomain").get(0));
    }


  // 4742
  @Test
  public void testADSplunkCef4742ForGeneric() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:36:56 WSPRDC111 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=AMER\\HD6331F66B494BF$\\nAMER\\HD6331F66B494BF$ cs2=Security cs1=Audit Success dpriv=- externalId=4742 duser=HD6331F66B494BF$\\nHD6331F66B494BF$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC111.amer.example.com dntdom=AMER\\nAMER cs4=0x26a59e5a9 cs5Label=Sub_Status cs4Label=Logon_ID ";
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
    assertEquals("privileges","-",out.get("privileges").get(0));
    assertEquals("sourceNtDomain","AMER",out.get("sourceNtDomain").get(0));
    assertEquals("sourceUserName","hd6331f66b494bf$",out.get("sourceUserName").get(0));
    assertEquals("logCollectionCategory","syslog",out.get("logCollectionCategory").get(0));
    assertEquals("sourceSecurityID","AMER\\HD6331F66B494BF$",out.get("sourceSecurityID").get(0));
    //assertEquals("uuid","2-e17f25c6-d750-11e5-99b1-ba82299662e2-1645-ef6ab9dbe1a0eb47130c6e3777653a9e",out.get("uuid").get(0));
    assertEquals("cefSignatureId","Security-4742-Audit Success",out.get("cefSignatureId").get(0));
    assertEquals("sourceLogonID","0x26a59e5a9",out.get("sourceLogonID").get(0));
    assertEquals("externalLogSourceType","MSWinEventLog-splunkcef",out.get("externalLogSourceType").get(0));
    assertEquals("destinationSecurityID","AMER\\HD6331F66B494BF$",out.get("destinationSecurityID").get(0));
    assertEquals("logCollectionHost","somehost",out.get("logCollectionHost").get(0));
    assertEquals("destinationUserName","hd6331f66b494bf$",out.get("destinationUserName").get(0));
    assertEquals("receiptTime",1473867416000L,out.get("receiptTime").get(0));
    assertEquals("startTime",1473867416000L,out.get("startTime").get(0));
    assertEquals("parserOutFormat","IAMMef",out.get("parserOutFormat").get(0));
    assertEquals("deviceNameOrIp","WSPRDC111",out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType","IAMMef",out.get("logSourceType").get(0));
    assertEquals("deviceHostName","WSPRDC111",out.get("deviceHostName").get(0));
    assertEquals("destinationNtDomain","AMER",out.get("destinationNtDomain").get(0));
  }


  @Test
  public void testADSplunkCef4624ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:16:07 WSPRDC115 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|sourceServiceName=- spt=60825 duid={B29DED39-7828-C1E1-DFCB-DC87DB893A78} cn2Label=Logon_Type cn2=3 cn3=0 deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NULL SID\\nAMER\\STVDSH014139$ cs2=Security cs1=Audit Success cs6=Kerberos deviceProcessName=- src=10.178.144.32 externalId=4624 duser=-\\nSTVDSH014139$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC115.amer.example.com dntdom=-\\nAMER cs4=0x0\\n0x2fad19d57 cs5Label=Sub_Status cs4Label=Logon_ID ";

    //Sep 15 07:19:58 WSPRDC114 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|sourceServiceName=- spt=64278 duid={69A1856E-55B9-E02D-773A-C14E7C9BAA3E} cn2Label=Logon_Type cn2=3 cn3=0 deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NULL SID\\nAMER\\HD630326DB253B7$ cs2=Security cs1=Audit Success cs6=Kerberos deviceProcessName=- src=10.131.81.203 externalId=4624 duser=-\\nHD630326DB253B7$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC114.amer.example.com dntdom=-\\nAMER cs4=0x0\\n0x4b2634a0d cs5Label=Sub_Status cs4Label=Logon_ID

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationLogonGUID", "{B29DED39-7828-C1E1-DFCB-DC87DB893A78}", out.get("destinationLogonGUID").get(0));
    assertEquals("destinationHostName", "WSPRDC115", out.get("destinationHostName").get(0));
    assertEquals("sourcePort", 60825, out.get("sourcePort").get(0));
    assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "WSPRDC115", out.get("deviceHostName").get(0));
    assertEquals("destinationUserName", "STVDSH014139$".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("startTime", 1473866167000L, out.get("startTime").get(0));
    assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
    assertEquals("destinationNameOrIp", "WSPRDC115.amer.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("destinationLogonID", "0x2fad19d57", out.get("destinationLogonID").get(0));
    assertEquals("sourceServiceName", "-", out.get("sourceServiceName").get(0));
    assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("deviceNameOrIp", "WSPRDC115", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("destinationDnsDomain", "amer.example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4624-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("sourceAddress", "10.178.144.32", out.get("sourceAddress").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

  }

  @Test
  public void testADSplunkCef4625ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:04:47 WSPRDC121 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|sourceServiceName=- spt=55513 dproc=- shost=HD6679CC465C07C cn2Label=Logon_Type cn2=3 cn3=0 deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NULL SID\\nNULL SID cs2=Security cs1=Audit Failure cs6=NTLM cs5=0xc0000064 src=10.177.113.95 externalId=4625 duser=-\\nHD6679CC465C07C$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC121.amer.example.com dntdom=-\\nAMER cs4=0x0 cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationHostName", "WSPRDC121", out.get("destinationHostName").get(0));
    assertEquals("sourcePort", 55513, out.get("sourcePort").get(0));
    assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
    assertEquals("sourceNameOrIp", "HD6679CC465C07C", out.get("sourceNameOrIp").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "WSPRDC121", out.get("deviceHostName").get(0));
    assertEquals("destinationUserName", "HD6679CC465C07C$".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("startTime", 1473865487000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "WSPRDC121.amer.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("sourceServiceName", "-", out.get("sourceServiceName").get(0));
    assertEquals("authenticationPackage", "NTLM", out.get("authenticationPackage").get(0));
    assertEquals("sourceSecurityID", "NULL SID", out.get("sourceSecurityID").get(0));
    assertEquals("cefEventName", "An account failed to log on", out.get("cefEventName").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("deviceNameOrIp", "WSPRDC121", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("destinationDnsDomain", "amer.example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("sourceHostName", "HD6679CC465C07C", out.get("sourceHostName").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4625-Audit Failure", out.get("cefSignatureId").get(0));
    assertEquals("destinationSecurityID", "NULL SID", out.get("destinationSecurityID").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

  }

  @Test
  public void testADSplunkCef4634ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:16:08 WSPRDC115 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type cn2=3 deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=AMER\\HD2313F66B5F6D0$ cs2=Security cs1=Audit Success externalId=4634 duser=HD2313F66B5F6D0$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC115.amer.example.com dntdom=AMER cs4=0x2faced8e0 cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
//    assertEquals("startTime", 1442243768000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationLogonID", "0x2faced8e0", out.get("destinationLogonID").get(0));
    assertEquals("cefEventName", "An account was logged off", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "WSPRDC115", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4634-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("deviceHostName", "WSPRDC115", out.get("deviceHostName").get(0));
    assertEquals("destinationUserName", "HD2313F66B5F6D0$".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
  }

  @Test
  public void testADSplunkCef4647ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:49:02 WSPRCX547 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=AMER\\mxm8315 cs2=Security cs1=Audit Success externalId=4647 duser=mxm8315 cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRCX547.amer.example.com dntdom=AMER cs4=0x37a20f0b cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    //10 Sep 2015 17:16:51
    assertEquals("startTime", 1473868142000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationLogonID", "0x37a20f0b", out.get("destinationLogonID").get(0));
    assertEquals("cefEventName", "User initiated logoff", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "WSPRCX547", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4647-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("deviceHostName", "WSPRCX547", out.get("deviceHostName").get(0));
    assertEquals("destinationUserName", "mxm8315", out.get("destinationUserName").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

  }

  @Test
  public void testADSplunkCef4648ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:48:25 WPR9100DCA0004 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|duid={00000000-0000-0000-0000-000000000000}\\n{00000000-0000-0000-0000-000000000000} cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NT AUTHORITY\\SYSTEM cs2=Security cs1=Audit Success deviceProcessName=C:\\Windows\\System32\\lsass.exe externalId=4648 dpt=41732 duser=WPR9100DCA0004$\\n_SVC_ServiceNow2 cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dst=172.29.238.82 dhost=WPR9100DCA0004.amer.example.com dntdom=AMER\\nAMER cs4=0x3e7 cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("sourcePort", 41732, out.get("sourcePort").get(0));
    assertEquals("sourceNameOrIp", "172.29.238.82", out.get("sourceNameOrIp").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "WPR9100DCA0004", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1473868105000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "WPR9100DCA0004.amer.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
    assertEquals("sourceUserName", "WPR9100DCA0004$".toLowerCase(), out.get("sourceUserName").get(0));
    assertEquals("destinationDnsDomain", "amer.example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("sourceAddress", "172.29.238.82", out.get("sourceAddress").get(0));
    assertEquals("sourceNtDomain", "AMER", out.get("sourceNtDomain").get(0));
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
    assertEquals("destinationHostName", "WPR9100DCA0004", out.get("destinationHostName").get(0));
    assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
    assertEquals("destinationUserName", "_SVC_ServiceNow2".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
    assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "WPR9100DCA0004", out.get("deviceNameOrIp").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4648-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("sourceLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("sourceLogonGUID").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

  }

  @Test
  public void testADSplunkCef4672ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:47:41 CPWITA28 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NT AUTHORITY\\SYSTEM cs2=Security cs1=Audit Success dpriv=SeSecurityPrivilege externalId=4672 duser=CPWITA28$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=CPWITA28.amer.example.com dntdom=AMER cs4=0x8a669cbd5 cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("startTime", 1473868061000L, out.get("startTime").get(0));
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationLogonID", "0x8a669cbd5", out.get("destinationLogonID").get(0));
    assertEquals("deviceNameOrIp", "CPWITA28", out.get("deviceNameOrIp").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4672-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("deviceHostName", "CPWITA28", out.get("deviceHostName").get(0));
    assertEquals("destinationSecurityID", "NT AUTHORITY\\SYSTEM", out.get("destinationSecurityID").get(0));
    assertEquals("destinationUserName", "CPWITA28$".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    assertEquals("privileges", "SeSecurityPrivilege", out.get("privileges").get(0));
  }

  @Test
  public void testADSplunkCef4673ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:48:24 WPR9100DCA0004 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NT AUTHORITY\\SYSTEM cs2=Security cs1=Audit Success deviceProcessName=C:\\Windows\\System32\\lsass.exe dpriv=SeTcbPrivilege externalId=4673 duser=WPR9100DCA0004$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WPR9100DCA0004.amer.example.com dntdom=AMER cs4=0x3e7 cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationHostName", "WPR9100DCA0004", out.get("destinationHostName").get(0));
    assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "WPR9100DCA0004", out.get("deviceHostName").get(0));
    assertEquals("destinationUserName", "NT AUTHORITY\\SYSTEM".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("startTime", 1473868104000L, out.get("startTime").get(0));
    assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
    assertEquals("destinationNameOrIp", "WPR9100DCA0004.amer.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("cefEventName", "A privileged service was called", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "WPR9100DCA0004", out.get("deviceNameOrIp").get(0));
    assertEquals("sourceUserName", "WPR9100DCA0004$".toLowerCase(), out.get("sourceUserName").get(0));
    assertEquals("destinationDnsDomain", "amer.example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4673-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("sourceNtDomain", "AMER", out.get("sourceNtDomain").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    assertEquals("privileges", "SeTcbPrivilege", out.get("privileges").get(0));

  }

  @Test
  public void testADSplunkCef4674ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:49:51 WAPRDC029 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NT AUTHORITY\\LOCAL SERVICE cs2=Security cs1=Audit Failure deviceProcessName=C:\\Windows\\System32\\lsass.exe dpriv=SeSecurityPrivilege externalId=4674 duser=LOCAL SERVICE cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WAPRDC029.example.com dntdom=NT AUTHORITY cs4=0x3e5 cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationHostName", "WAPRDC029", out.get("destinationHostName").get(0));
    assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "WAPRDC029", out.get("deviceHostName").get(0));
    assertEquals("startTime", 1473868191000L, out.get("startTime").get(0));
    assertEquals("sourceLogonID", "0x3e5", out.get("sourceLogonID").get(0));
    assertEquals("destinationNameOrIp", "WAPRDC029.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("sourceSecurityID", "NT AUTHORITY\\LOCAL SERVICE", out.get("sourceSecurityID").get(0));
    assertEquals("cefEventName", "An operation was attempted on a privileged object", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "WAPRDC029", out.get("deviceNameOrIp").get(0));
    assertEquals("sourceUserName", "LOCAL SERVICE".toLowerCase(), out.get("sourceUserName").get(0));
    assertEquals("destinationDnsDomain", "example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4674-Audit Failure", out.get("cefSignatureId").get(0));
    assertEquals("sourceNtDomain", "NT AUTHORITY", out.get("sourceNtDomain").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    assertEquals("privileges", "SeSecurityPrivilege", out.get("privileges").get(0));
  }

  @Test
  public void testADSplunkCef4738ForHermes() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Sep 14 15:46:10 WSPRDC114 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cn2Label=Logon_Type deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=AMER\\_svcDRA\\nAMER\\SXR8969 cs2=Security cs1=Audit Success dpriv=- externalId=4738 duser=_svcDRA\\nSXR8969 cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC114.amer.example.com dntdom=AMER\\nAMER cs4=0x475c3abbf cs5Label=Sub_Status cs4Label=Logon_ID ";
    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationHostName", "WSPRDC114", out.get("destinationHostName").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "WSPRDC114", out.get("deviceHostName").get(0));
    assertEquals("destinationUserName", "SXR8969".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("startTime", 1473867970000L, out.get("startTime").get(0));
    assertEquals("sourceLogonID", "0x475c3abbf", out.get("sourceLogonID").get(0));
    assertEquals("destinationNameOrIp", "WSPRDC114.amer.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("sourceSecurityID", "AMER\\_svcDRA", out.get("sourceSecurityID").get(0));
    assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "WSPRDC114", out.get("deviceNameOrIp").get(0));
    assertEquals("sourceUserName", "_svcDRA".toLowerCase(), out.get("sourceUserName").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("destinationDnsDomain", "amer.example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4738-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("destinationSecurityID", "AMER\\SXR8969", out.get("destinationSecurityID").get(0));
    assertEquals("sourceNtDomain", "AMER", out.get("sourceNtDomain").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

  }

  @Test
  public void testADSplunkCef4624ForHermesBis() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Oct 09 16:14:19 CPWITA26 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package sourceServiceName=- cs5Label=Sub_Status externalId=4624 deviceFacility=Microsoft Windows security auditing. src=- duid={FEDDBF0B-B3B3-BF7C-F6CF-7234DFAA6E24} cn2Label=Logon_Type cs3Label=Security_ID cs1Label=eventOutcome dntdom=-\\nAMER duser=-\\nSSCVMB3SALE4104$ cn3=0 deviceProcessName=- cs3=NULL SID\\nAMER\\SSCVMB3SALE4104$ cs2=Security cs1=Audit Success cs4Label=Logon_ID cs6=Kerberos cs4=0x0\\n0x13e66f26c dhost=CPWITA26.amer.example.com cn2=3 spt=- ";

    //Sep 15 07:19:58 WSPRDC114 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|sourceServiceName=- spt=64278 duid={69A1856E-55B9-E02D-773A-C14E7C9BAA3E} cn2Label=Logon_Type cn2=3 cn3=0 deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NULL SID\\nAMER\\HD630326DB253B7$ cs2=Security cs1=Audit Success cs6=Kerberos deviceProcessName=- src=10.131.81.203 externalId=4624 duser=-\\nHD630326DB253B7$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC114.amer.example.com dntdom=-\\nAMER cs4=0x0\\n0x4b2634a0d cs5Label=Sub_Status cs4Label=Logon_ID

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "CPWITA26", out.get("deviceHostName").get(0));
    assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
    assertEquals("startTime", 1444407259000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "CPWITA26.amer.example.com", out.get("destinationNameOrIp").get(0));
    assertEquals("destinationLogonID", "0x13e66f26c", out.get("destinationLogonID").get(0));
    assertEquals("sourceSecurityID", "NULL SID", out.get("sourceSecurityID").get(0));
    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("destinationDnsDomain", "amer.example.com", out.get("destinationDnsDomain").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
    assertEquals("logCollectionCategory", "activedirectory", out.get("logCollectionCategory").get(0));
    assertEquals("destinationLogonGUID", "{FEDDBF0B-B3B3-BF7C-F6CF-7234DFAA6E24}", out.get("destinationLogonGUID").get(0));
    assertEquals("destinationHostName", "CPWITA26", out.get("destinationHostName").get(0));
    assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
    assertEquals("destinationUserName", "SSCVMB3SALE4104$".toLowerCase(), out.get("destinationUserName").get(0));
    assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
    assertEquals("sourceServiceName", "-", out.get("sourceServiceName").get(0));
    assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
    assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
    assertEquals("deviceNameOrIp", "CPWITA26", out.get("deviceNameOrIp").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
    assertEquals("cefSignatureId", "Security-4624-Audit Success", out.get("cefSignatureId").get(0));
    assertEquals("destinationNtDomain", "AMER", out.get("destinationNtDomain").get(0));
    assertEquals("destinationSecurityID", "AMER\\SSCVMB3SALE4104$", out.get("destinationSecurityID").get(0));
    assertEquals("keyLength", 0, out.get("keyLength").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

  }

  @Test
  public void testTaniumJsonFromHermesProcessWithErrorDebugMefEventParser() throws IOException, Exception {

    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg = "{\"E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2\": {\"QuestionName\":\"E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2\",\"Computer Name\": \"Brians-MacBook-Pro.\", \"Path\": \"/System/Library/CoreServices/Finder.app/Contents/MacOS/Finder\", \"MD5 Hash\": \"28767d41f64c5ab5a73524b6613c2ad8\", \"Count\": \"1\"}}";

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "tanium");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    assertNotNull(output);
    assertTrue(output.size()>0);
    //assertEquals("no event", null, output);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
  }

  @Test
  public void testTaniumJsonFromHermesProcessDebugMefEventParser() throws IOException, Exception {

    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg = "{\"E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2\": {\"QuestionName\":\"E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2\",\"Computer Name\": \"HD6662LN57.amer.example.com\", \"Path\": \"c:\\\\robot\\\\bin\\\\robot.exe\", \"MD5 Hash\": \"1d8ff7af80eb26047bf56faa3b8f7f83\", \"Count\": \"1\"}}";

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "tanium");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    assertNotNull(output);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
  }

  @Test
  public void testTaniumJsonFromHermesProcessMefEventParser() throws IOException, Exception {

    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg ="{\"E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2\": {\"QuestionName\":\"E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2\",\"Computer Name\": \"ATCVMCORP010.amer.example.com\", \"Path\": \"c:\\\\program-files\\\\opnet\\\\appcapture3.9\\\\op_capture_server.exe\", \"MD5 Hash\": \"e4d0757e74bc84b6fa22e1e59ebcea74\", \"Count\": \"1\"}}";

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "tanium");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    assertNotNull(output);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("deviceDnsDomain", "amer.example.com", out.get("deviceDnsDomain").get(0));
    assertEquals("logCollectionCategory", "tanium", out.get("logCollectionCategory").get(0));
    assertEquals("processFileMd5", "e4d0757e74bc84b6fa22e1e59ebcea74", out.get("processFileMd5").get(0));
    assertEquals("processFilePath", "c:\\program-files\\opnet\\appcapture3.9\\op_capture_server.exe", out.get("processFilePath").get(0));
    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
    assertEquals("externalLogSourceType", "tanium-E8-Get-Computer-Name-and-Running-Processes-with-MD5-Hash-from-all-machines---2", out.get("externalLogSourceType").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("processName", "op_capture_server.exe", out.get("processName").get(0));
    assertEquals("startTime", 1384693669604L, out.get("startTime").get(0));
    assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
    assertEquals("deviceNameOrIp", "ATCVMCORP010.amer.example.com", out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "ATCVMCORP010", out.get("deviceHostName").get(0));

  }

  @Test
  public void testTaniumJsonAutoRunFromHermesProcessMefEventParser() throws IOException, Exception {

    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg ="{\"E8-Get-Computer-Name-and-AutoRun-Program-Details-from-all-machines\": {\"QuestionName\":\"E8-Get-Computer-Name-and-AutoRun-Program-Details-from-all-machines\",\"Computer Name\": \"SSCVMDEVU39017.amer.example.com\", \"Entry Location\": \"HKLM\\\\System\\\\CurrentControlSet\\\\Services\", \"Entry\": \"amdsbs\", \"Category\": \"Drivers\", \"Profile\": \"System-wide\", \"Description\": \"AMD-Technology-AHCI-Compatible-Controller-Driver-for-Windows-family\", \"Publisher\": \"AMD-Technologies-Inc.\", \"Image Path\": \"c:\\\\windows\\\\system32\\\\drivers\\\\amdsbs.sys\", \"Version\": \"3.6.1540.127\", \"Launch String\": \"\\\\SystemRoot\\\\system32\\\\drivers\\\\amdsbs.sys\", \"MD5 Hash\": \"EA43AF0C423FF267355F74E7A53BDABA\", \"SHA-1 Hash\": \"E944C08A4FCF93206FECB658F6E23F7B2D3B98B1\", \"SHA-256 Hash\": \"3F1335909AB0281A2FBDD7AD90E18309E091656CD32B48894B992789D8C61DB4\", \"Count\": \"1\"}}";

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "tanium");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    assertNotNull(output);
    assertTrue(output.size()>0);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());

    assertEquals("jobName", "amdsbs", out.get("jobName").get(0));
    assertEquals("deviceDnsDomain", "amer.example.com", out.get("deviceDnsDomain").get(0));
    assertEquals("logCollectionCategory", "tanium", out.get("logCollectionCategory").get(0));
    assertEquals("processFileMd5", "EA43AF0C423FF267355F74E7A53BDABA", out.get("processFileMd5").get(0));
    assertEquals("processFilePath", "c:\\windows\\system32\\drivers\\amdsbs.sys", out.get("processFilePath").get(0));
    assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
    assertEquals("externalLogSourceType", "tanium-Get-Computer-Name-and-AutoRun-Program-Details-from-all-machines", out.get("externalLogSourceType").get(0));
    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("jobCmd", "\\SystemRoot\\system32\\drivers\\amdsbs.sys", out.get("jobCmd").get(0));
    assertEquals("startTime", 1384693669604L, out.get("startTime").get(0));
    assertEquals("parserOutFormat", "HostJobMef", out.get("parserOutFormat").get(0));
    assertEquals("deviceNameOrIp", "SSCVMDEVU39017.amer.example.com", out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
    assertEquals("deviceHostName", "SSCVMDEVU39017", out.get("deviceHostName").get(0));

  }

  //Oct 09 16:19:58 WSPRCX588 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package cs5Label=Sub_Status externalId=4648 deviceFacility=Microsoft Windows security auditing. duid={00000000-0000-0000-0000-000000000000}\\n{D555567A-C28A-D9AC-4251-9A541CA96261} cn2Label=Logon_Type cs3Label=Security_ID dpt=- cs1Label=eventOutcome dst=- dntdom=AMER\\nAMER.EXAMPLE.COM duser=WSPRCX588$\\nWSPRCX588$ deviceProcessName=C:\\Windows\\System32\\taskhost.exe cs3=NT AUTHORITY\\SYSTEM cs2=Security cs1=Audit Success cs4Label=Logon_ID cs4=0x3e7 dhost=WSPRCX588.amer.example.com
  @Test
  public void testADSplunkCef4648ForHermesBis() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "Oct 09 16:19:58 WSPRCX588 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package cs5Label=Sub_Status externalId=4648 deviceFacility=Microsoft Windows security auditing. duid={00000000-0000-0000-0000-000000000000}\\n{D555567A-C28A-D9AC-4251-9A541CA96261} cn2Label=Logon_Type cs3Label=Security_ID dpt=- cs1Label=eventOutcome dst=- dntdom=AMER\\nAMER.EXAMPLE.COM duser=WSPRCX588$\\nWSPRCX588$ deviceProcessName=C:\\Windows\\System32\\taskhost.exe cs3=NT AUTHORITY\\SYSTEM cs2=Security cs1=Audit Success cs4Label=Logon_ID cs4=0x3e7 dhost=WSPRCX588.amer.example.com";

    //Sep 15 07:19:58 WSPRDC114 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|sourceServiceName=- spt=64278 duid={69A1856E-55B9-E02D-773A-C14E7C9BAA3E} cn2Label=Logon_Type cn2=3 cn3=0 deviceFacility=Microsoft Windows security auditing. cs1Label=eventOutcome cs3=NULL SID\\nAMER\\HD630326DB253B7$ cs2=Security cs1=Audit Success cs6=Kerberos deviceProcessName=- src=10.131.81.203 externalId=4624 duser=-\\nHD630326DB253B7$ cs3Label=Security_ID cs2Label=journalName cn3Label=Key_Length cs6Label=Authentication_Package dhost=WSPRDC114.amer.example.com dntdom=-\\nAMER cs4=0x0\\n0x4b2634a0d cs5Label=Sub_Status cs4Label=Logon_ID

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "activedirectory");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    assertEquals("destinationDnsDomain","amer.example.com",out.get("destinationDnsDomain").get(0));
    assertEquals("sourceNtDomain","AMER",out.get("sourceNtDomain").get(0));
    assertEquals("sourceLogonGUID","{00000000-0000-0000-0000-000000000000}",out.get("sourceLogonGUID").get(0)); 
    assertEquals("sourceUserName","wsprcx588$",out.get("sourceUserName").get(0));
    assertEquals("logCollectionCategory","activedirectory",out.get("logCollectionCategory").get(0));
    assertEquals("sourceSecurityID","NT AUTHORITY\\SYSTEM",out.get("sourceSecurityID").get(0)); 
    assertEquals("destinationLogonGUID","{D555567A-C28A-D9AC-4251-9A541CA96261}",out.get("destinationLogonGUID").get(0));
    assertEquals("sourceProcessName","C:\\Windows\\System32\\taskhost.exe",out.get("sourceProcessName").get(0)); 
    assertEquals("destinationHostName","WSPRCX588",out.get("destinationHostName").get(0));
    // This one seems to be unique each time the test is run
    //uuid=[1-0f754600-d774-11e5-849a-ba82299662e2-1645-2ba9eff3679172f951ce5ddb7e7b955b], 
    assertEquals("cefSignatureId","Security-4648-Audit Success",out.get("cefSignatureId").get(0));
    assertEquals("destinationNameOrIp","WSPRCX588.amer.example.com",out.get("destinationNameOrIp").get(0));
    assertEquals("sourceLogonID","0x3e7",out.get("sourceLogonID").get(0));
    assertEquals("externalLogSourceType","MSWinEventLog-splunkcef",out.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost","somehost",out.get("logCollectionHost").get(0));
    assertEquals("destinationUserName","wsprcx588$",out.get("destinationUserName").get(0));
    assertEquals("receiptTime",1444407598000L,out.get("receiptTime").get(0));
    assertEquals("startTime",1444407598000L,out.get("startTime").get(0));
    assertEquals("parserOutFormat","IAMMef",out.get("parserOutFormat").get(0));
    assertEquals("deviceNameOrIp","WSPRCX588",out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType","IAMMef", out.get("logSourceType").get(0));
    assertEquals("cefEventName","A logon was attempted using explicit credentials",out.get("cefEventName").get(0));
    assertEquals("deviceHostName","WSPRCX588",out.get("deviceHostName").get(0));
    assertEquals("destinationNtDomain","AMER.EXAMPLE.COM",out.get("destinationNtDomain").get(0));
  }

  /*
  @Test
  public void testUnMactchedFile() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);

    InputStream fis = new FileInputStream("/Volumes/Volume 1000Go 1/JY/SecurityX/Project/201510-mcafee_web_sec/mcafee_wg_sample.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ;
    String line;
    long cpt = 0;
    while ((line = br.readLine()) !=  null){
      InputStream ais = new ByteArrayInputStream(line.getBytes());
      DataInputStream dais = new DataInputStream(ais);
      Decoder decoder = DecoderFactory.get().jsonDecoder(Event.SCHEMA$, dais);
     DatumReader<Event> reader = new SpecificDatumReader<Event>(Event.SCHEMA$);
      avroEvent = reader.read(null, decoder);
      //JsonReader reader = Json.createReader(new StringReader(line));
      //JsonObject object = reader.readObject();
      //reader.close();

      //OutUtils.printOut(object.getString("rawLog"));
      //ByteBuffer buf = ByteBuffer.wrap(object.getString("rawLog").getBytes());
      avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));

      List<Map<String, List<Object>>> output = instance.parse(avroEvent);
      try {
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
      }catch (Exception e ){
        OutUtils.printOut("failed on line " +cpt+" ("+e.getMessage()+")");
        throw e;
      }
      cpt+=1;
    }


  }
  */

}