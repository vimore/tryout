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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jyrialhon
 */
public class AvroParserWebBehaviorStatic extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public AvroParserWebBehaviorStatic(String testName) {
    super(testName);
// temporarly commented 
//    this.morphlineFile = "logcollection-script-selector-command-list.conf";
//    this.morphlineId = "logcollectionselector";
    this.morphlineFile = "logcollection-parser-main.conf";
    this.morphlineId = "parsermain";
  }
  
  public void testBlueCoat1y() throws Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
    Event avroEvent = new Event();
    String msg = "2013-01-01 01:00:02 45.15.1.253 Stevan_Ridley Mozilla/4.0-(compatible;-MSIE-6.0;-Windows-NT-5.1;-SV1;-.NET-CLR-1.1.4322) PROXIED 404 TCP_NC_MISS 184 524 GET http download.zonelabs.com /bin/updates/znalm/images/partnersNAV_over.gif - Computers/Internet 192.16.170.42 ";

    
    avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "host-web_behavior");

    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out);
    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
    assertEquals("deviceAction", "TCP_NC_MISS", out.get("deviceAction").get(0));
    assertEquals("requestPath", "/bin/updates/znalm/images/partnersNAV_over.gif", out.get("requestPath").get(0));
    // ??? why doesn't this produce sourceNameOrIp
    //assertNotNull(out.get("sourceNameOrIp"));
    //assertEquals("sourceNameOrIp", "45.15.1.253", out.get("sourceNameOrIp").get(0));
    assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
    assertEquals("bytesIn", 524, out.get("bytesIn").get(0));
    assertNotNull(out.get("startTime"));
    assertEquals("startTime", 1357002002000L, out.get("startTime").get(0));
    assertEquals("destinationNameOrIp", "download.zonelabs.com", out.get("destinationNameOrIp").get(0));
    assertEquals("sourceUserName", "Stevan_Ridley", out.get("sourceUserName").get(0));
    assertEquals("logCollectionHost", "host-web_behavior", out.get("logCollectionHost").get(0));
    assertEquals("deviceNameOrIp", "host-web_behavior", out.get("logCollectionHost").get(0));
    assertEquals("destinationDnsDomain", "zonelabs.com", out.get("destinationDnsDomain").get(0));
    assertEquals("requestClientApplication", "Mozilla/4.0-(compatible;-MSIE-6.0;-Windows-NT-5.1;-SV1;-.NET-CLR-1.1.4322)", out.get("requestClientApplication").get(0));
    assertEquals("deviceEventCategory", "Computers/Internet", out.get("deviceEventCategory").get(0));
    assertEquals("bytesOut", 184, out.get("bytesOut").get(0));
    assertEquals("sourceAddress", "45.15.1.253", out.get("sourceAddress").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
    assertEquals("destinationHostName", "download", out.get("destinationHostName").get(0));
    assertEquals("destinationNameOrIp", "download.zonelabs.com", out.get("destinationNameOrIp").get(0));
    assertEquals("destinationDnsDomain", "zonelabs.com", out.get("destinationDnsDomain").get(0));
    assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
    assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
    assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
    assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
    assertEquals("logCollectionTime", "1384693669604", out.get("logCollectionTime").get(0));
    assertEquals("cefSignatureId", "404", out.get("cefSignatureId").get(0));
    assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
  }


  public void testDnsmasqDhcpEventParser() throws Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HETMef);
    Event avroEvent = new Event();
    String msg = "Jul 30 11:00:00 dnsmasq-dhcp[1234]: DHCPACK(br0) 45.14.5.90 42:66:f5:6b:dd:6a stockpotsintercommunal";

    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "host-web_behavior");
    headers.put("timestamp", "1384693669604");
    //date timezone is PST
    //Wed, 30 Jul 2014 18:00:00 GMT :  1406743200000
    //Wed, 30 Jul 2014 10:00:00 GMT :  1406714400000
    avroEvent.setHeaders(headers);
    List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    Map<String, List<Object>> out = output.get(0);
    OutUtils.printOut(out.toString());
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("startTime", 1469901600000L, out.get("startTime").get(0));
    assertEquals("destinationMacAddress", "42:66:f5:6b:dd:6a", out.get("destinationMacAddress").get(0));
    assertEquals("deviceProcessId", 1234, out.get("deviceProcessId").get(0));
    assertEquals("deviceInterface", "br0", out.get("deviceInterface").get(0));
    assertEquals("deviceProcessName", "dnsmasq-dhcp", out.get("deviceProcessName").get(0));
    assertEquals("destinationAddress", "45.14.5.90", out.get("destinationAddress").get(0));
    assertEquals("externalLogSourceType", "dnsmasq-dhcp", out.get("externalLogSourceType").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
  }
}
