/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.parser.avro;

import com.securityx.flume.log.avro.Event;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jyrialhon
 */
public class AvroParserTest extends TestCase {

    private String morphlineFile;
    private String morphlineId;

    public AvroParserTest(String testName) {
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

//  public void testParse() throws FileNotFoundException, Exception {
//    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
//    String inFileStr = "src/test/resources/hivedev1.labs.lan.firewall.1384678800654";
//    File inFile = new File(inFileStr);
//    InputStream in = AvroParser.fileToStream(inFile);
//    Record[] out = instance.parseAvroContainer(in);
//    int i = 0;
//    for (Record r : out) {
//      OutUtils.printOut(i + " : " + r.toString());
//      i++;
//    }
//  }

//  public void testSquidParse() throws FileNotFoundException, Exception {
//    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
//    String inFileStr = "src/test/resources/hivedev1.labs.lan.syslog.squid";
//    File inFile = new File(inFileStr);
//    InputStream in = AvroParser.fileToStream(inFile);
//    Record[] out = instance.parseAvroContainer(in);
//    int i = 0;
//    for (Record r : out) {
//      OutUtils.printOut(i + " : " + r.toString());
//      i++;
//    }
//  }
////disabled because DPI parsing requires parse AvroContainer to be updated
//  public void testDpiHttpParse() throws FileNotFoundException, Exception {
//    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyDpi);
//    String inFileStr = "src/test/resources/hivedev1.labs.lan.packet.1394202907090";
//    File inFile = new File(inFileStr);
//    InputStream in = AvroParser.fileToStream(inFile);
//    Record[] out = instance.parseAvroContainer(in);
//    int i = 0;
//    for (Record r : out) {
//      OutUtils.printOut(i + " : " + r.toString());
//      i++;
//    }
//  }

    //
//    /**
//     * Test from avro src file to avro outFile
//     */
//    public void testParse1() throws FileNotFoundException, Exception {
//        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
//        String inFileStr = "src/test/resources/security1.linux.1381871826251";
//        File inFile = new File(inFileStr);
//        InputStream in = AvroParser.fileToStream(inFile);
//        Record[] out = instance.parseAvroContainer(in);
//        int i = 0;
//        for (Record r : out) {
//            OutUtils.printOut(i + " : " + r.toString());
//            i++;
//        }
//    }
//
//    /**
//     * Test from avro src file to avro outFile
//     */
//    public void testParse2() throws FileNotFoundException, Exception {
//        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
//        String inFileStr = "src/test/resources/security1.syslog.1384693202191";
//        File inFile = new File(inFileStr);
//        InputStream in = AvroParser.fileToStream(inFile);
//        Record[] out = instance.parseAvroContainer(in);
//        int i = 0;
//        for (Record r : out) {
//            OutUtils.printOut(i + " : " + r.toString());
//            i++;
//        }
//    }
//
//    public void testRecordParse0() throws Exception {
//        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
//        String line = "{\"headers\":{\"timestamp\":\"1384679104697\",\"hostname\":\"hivedev1.labs.lan\",\"category\":\"firewall\",\"Severity\":\"6\",\"Facility\":\"16\"},\"body\":\"id=firewall sn=0006B129195C time=\\\"2013-11-17 01:05:04\\\" fw=71.6.1.234 pri=6 c=262144 m=98 msg=\\\"Connection Opened\\\" n=5283286 src=99.9.44.224:28520:X1:99-9-44-224.lightspeed.cicril.sbcglobal.net dst=10.10.30.100:53:X0: proto=udp/dns \"}";
//
//        Record extractedData = instance.readOnlyJsonAvroRecordAsInputStream("test", new ByteArrayInputStream(line.getBytes()));
//        OutUtils.printOut(extractedData.toString());
//        Record parsedData = instance.parseOnly(extractedData);
//        OutUtils.printOut(parsedData.toString());
//        RecordToOutFormat formatter = new RecordToOutFormat(SupportedFormats.WebProxyMef);
//        Record realOutput = formatter.keepFormat(parsedData);
//        OutUtils.printOut(realOutput.toString());
//
//    }
//
//    public void testRecordParse1() throws IOException, Exception {
//        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.BlueCoat);
//        String line = "{\"headers\":{\"timestamp\":\"1384693669604\",\"hostname\":\"security1\",\"category\":\"syslog\",\"Severity\":\"0\",\"flume.syslog.status\":\"Invalid\",\"Facility\":\"0\"},\"body\":\"11/17/2013 13:07:49 349 45.21.4.244 200 TCP_MISS 40691 320 GET http www.accelacast.com /programs/f5_assetcentric/050411_f5_ab_6.swf - - DIRECT www.accelacast.com application/x-shockwave-flash \\\"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\\\" PROXIED none - 192.16.170.42 SG-HTTP-Service - none -\"}";
//        Record out = instance.parseAvroRecord(new ByteArrayInputStream(line.getBytes()));
//        OutUtils.printOut(out.toString());
//        RecordToOutFormat formatter = new RecordToOutFormat(SupportedFormats.BlueCoat);
//        Record realOutput = formatter.keepFormat(out);
//        OutUtils.printOut(realOutput.toString());
//    }
//    public void testRecordParse2() throws IOException, Exception {
//        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.BlueCoat);
//        String line = "{\"headers\":{\"timestamp\":\"1392831666488\",\"hostname\":\"hivedev1.labs.lan\",\"category\":\"syslog\",\"Severity\":\"0\",\"flume.syslog.status\":\"Invalid\",\"Facility\":\"0\"},\"body\":\"1392831663.466     114 81.56.112.95 TCP_MISS/200 4656 GET http://perspectives4.networknotary.org:8080/? - DIRECT/50.112.37.9 text/xml\"}";
//        Record out = instance.parseAvroRecord(new ByteArrayInputStream(line.getBytes()));
//        OutUtils.printOut(out.toString());
//    } 
  /* to be fixed
   // disabled, pending external format refactoring, it should provide the set of 
   // formats supported by the external format (for instance, bluecoat -> [BlueCoat, WebProxyMef]
   // validate format to be adapted accordingly
   public void testBlueCoatAvroEventParser() throws IOException, Exception {
   AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.BlueCoat);
   Event avroEvent = new Event();
   String msg = "11/17/2013 13:07:49 349 45.21.4.244 200 TCP_MISS 40691 320 GET http www.accelacast.com /programs/f5_assetcentric/050411_f5_ab_6.swf - - DIRECT www.accelacast.com application/x-shockwave-flash \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" PROXIED none - 192.16.170.42 SG-HTTP-Service - none -";

   Charset utf8charset = Charset.forName("UTF-8");
   CharsetEncoder encoder = utf8charset.newEncoder();
   CharBuffer cbuf = CharBuffer.wrap(msg);

   ByteBuffer buf = encoder.encode(cbuf);
   avroEvent.setBody(buf);
   Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
   headers.put("category", "syslog");
   headers.put("hostname", "somehost");

   headers.put("timestamp", "1384693669604");

   avroEvent.setHeaders(headers);
   Map<String, List<Object>> out = instance.parse(avroEvent);
   assertEquals("parserOutFormat", SupportedFormats.BlueCoat.name(), out.get("parserOutFormat").get(0));
   assertEquals("c_ip", "45.21.4.244", out.get("c_ip").get(0));
   assertEquals("cs_uri_query", "-", out.get("cs_uri_query").get(0));
   assertEquals("cs_method", "GET", out.get("cs_method").get(0));
   assertEquals("x_virus_id", "-", out.get("x_virus_id").get(0));
   assertEquals("date", "11/17/2013", out.get("date").get(0));
   assertEquals("s_action", "TCP_MISS", out.get("s_action").get(0));
   assertEquals("cs_bytes", 320, out.get("cs_bytes").get(0));
   assertEquals("rs0_Content_Type_0", "application/x-shockwave-flash", out.get("rs0_Content_Type_0").get(0));
   assertEquals("sc_filter_category", "none", out.get("sc_filter_category").get(0));
   assertEquals("time", "13:07:49", out.get("time").get(0));
   //assertEquals("cs0_User_Agent_0", "\"Mozilla/4.0 (compatible); MSIE 6.0); Windows NT 5.1); SV1); .NET CLR 1.1.4322)\"", out.get("cs0_User_Agent_0").get(0));
   assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
   assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
   assertEquals("cs_uri_scheme", "http", out.get("cs_uri_scheme").get(0));
   assertEquals("time_taken", 349, out.get("time_taken").get(0));
   assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
   assertEquals("s_supplier_name", "www.accelacast.com", out.get("s_supplier_name").get(0));
   assertEquals("s_sitename", "SG-HTTP-Service", out.get("s_sitename").get(0));
   assertEquals("cs_uri_path", "/programs/f5_assetcentric/050411_f5_ab_6.swf", out.get("cs_uri_path").get(0));
   assertEquals("sc_bytes", 40691, out.get("sc_bytes").get(0));
   assertEquals("x_virus_details", "-", out.get("x_virus_details").get(0));
   assertEquals("cs_host", "www.accelacast.com", out.get("cs_host").get(0));
   assertEquals("s_ip", "192.16.170.42", out.get("s_ip").get(0));
   assertEquals("sc_status", "200", out.get("sc_status").get(0));
   assertEquals("s_hierarchy", "DIRECT", out.get("s_hierarchy").get(0));
   assertEquals("cs_username", "-", out.get("cs_username").get(0));
   assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
   assertEquals("x_icap_error_code", "none", out.get("x_icap_error_code").get(0));

   }
   */
    public void testBlueCoatWebProxyAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "17/11/2013 13:07:49 349 45.21.4.244 200 TCP_MISS 40691 320 GET http www.accelacast.com /programs/f5_assetcentric/050411_f5_ab_6.swf - - DIRECT www.accelacast.com application/x-shockwave-flash \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" PROXIED none - 192.16.170.42 SG-HTTP-Service - none -";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out);
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/programs/f5_assetcentric/050411_f5_ab_6.swf", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "45.21.4.244", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 320, out.get("bytesIn").get(0));
        assertEquals("startTime", 1384693669000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "accelacast.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "none", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 40691, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "45.21.4.244", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("destinationHostName", "www", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceCustomString2", "349", out.get("deviceCustomString2").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
    }

    public void testBlueCoatWebProxyAvroEventParser2() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "2013-11-17 13:07:49 349 45.21.4.244 200 TCP_MISS 40691 320 GET http www.accelacast.com /programs/f5_assetcentric/050411_f5_ab_6.swf - - DIRECT www.accelacast.com application/x-shockwave-flash \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" PROXIED none - 192.16.170.42 SG-HTTP-Service - none -";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out);
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/programs/f5_assetcentric/050411_f5_ab_6.swf", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "45.21.4.244", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 320, out.get("bytesIn").get(0));
        assertEquals("startTime", 1384693669000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "accelacast.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "none", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 40691, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "45.21.4.244", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("destinationHostName", "www", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceCustomString2", "349", out.get("deviceCustomString2").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
    }

    public void testBlueCoatWebProxyAvroEventParser3() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "<134>May 20 13:38:01 192.168.12.10 2013-11-17 13:07:49 349 45.21.4.244 200 TCP_MISS 40691 320 GET http www.accelacast.com /programs/f5_assetcentric/050411_f5_ab_6.swf - - DIRECT www.accelacast.com application/x-shockwave-flash \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" PROXIED none - 192.16.170.42 SG-HTTP-Service - none -";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out);
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/programs/f5_assetcentric/050411_f5_ab_6.swf", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "45.21.4.244", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 320, out.get("bytesIn").get(0));
        assertEquals("startTime", 1384693669000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "accelacast.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "none", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 40691, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "45.21.4.244", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("destinationHostName", "www", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceCustomString2", "349", out.get("deviceCustomString2").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
    }

    public void testBlueCoatWebProxyAvroEventParserDemoCDns() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "#Fields: date time time-taken c-ip sc-status c-dns s-action sc-bytes cs-bytes cs-method cs-uri-scheme cs-host cs-uri-path cs-uri-query cs-username s-hierarchy s-supplier-name rs(Content-Type) cs(User-Agent) sc-filter-result sc-filter-category x-virus-id s-ip s-sitename x-virus-details x-icap-error-code x-icap-error-details";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        msg = "2016-07-03 00:00:00 789 192.168.1.42 304 laptop812.acmebank.xyz TCP_MISS 225 294 GET http www.acmebank.xyz /demo/dataset/webpage.xml - Eric_Decker DIRECT www.acmebank.xyz text/plain \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1\" PROXIED Web%20Advertisements - 192.168.13.24 SG-HTTP-Service - none -";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        output = instance.parse(avroEvent);

        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out);
        assertEquals("reason", "-, none : -", out.get("reason").get(0));
        assertEquals("sourceHostName", "laptop812", out.get("sourceHostName").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("destinationNameOrIp", "www.acmebank.xyz", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("deviceAddress", "192.168.13.24", out.get("deviceAddress").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("startTime", 1467504000000L, out.get("startTime").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("requestPath", "/demo/dataset/webpage.xml", out.get("requestPath").get(0));
        assertEquals("bytesIn", 294, out.get("bytesIn").get(0));
        assertEquals("bytesOut", 225, out.get("bytesOut").get(0));
        assertEquals("destinationDnsDomain", "acmebank.xyz", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "192.168.1.42", out.get("sourceAddress").get(0));
        assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
        assertEquals("sourceUserName", "Eric_Decker", out.get("sourceUserName").get(0));
        assertEquals("sourceDnsDomain", "acmebank.xyz", out.get("sourceDnsDomain").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceCustomString2", "789", out.get("deviceCustomString2").get(0));
        assertEquals("destinationHostName", "www", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "304", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceEventCategory", "Web%20Advertisements", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "text/plain", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceNameOrIp", "laptop812.acmebank.xyz", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    }

    public void testSquidWebProxyLogAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "(squid): 1392831663.466     114 81.56.112.95 TCP_MISS/200 4656 GET http://perspectives4.networknotary.org:8080/? - DIRECT/50.112.37.9 text/xml";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("devicePolicyAction", "TCP_MISS", out.get("devicePolicyAction").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "perspectives4", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "networknotary.org", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/?", out.get("requestPath").get(0));
        assertEquals("destinationPort", 8080, out.get("destinationPort").get(0));
        assertEquals("sourceNameOrIp", "81.56.112.95", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Squid", out.get("externalLogSourceType").get(0));
        assertEquals("startTime", 1392831663466L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "perspectives4.networknotary.org", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("destinationAddress", "50.112.37.9", out.get("destinationAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "networknotary.org", out.get("destinationDnsDomain").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("sourceAddress", "81.56.112.95", out.get("sourceAddress").get(0));

    }
    
  
    public void testWebProxyLogAvroEventParserWithSyslogHeader() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "<134>May 20 13:38:01 192.168.12.10 (squid): 1392831663.466     114 81.56.112.95 TCP_MISS/200 4656 GET http://perspectives4.networknotary.org:8080/? - DIRECT/50.112.37.9 text/xml";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("devicePolicyAction", "TCP_MISS", out.get("devicePolicyAction").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "perspectives4", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "networknotary.org", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/?", out.get("requestPath").get(0));
        assertEquals("destinationPort", 8080, out.get("destinationPort").get(0));
        assertEquals("sourceNameOrIp", "81.56.112.95", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Squid", out.get("externalLogSourceType").get(0));
        assertEquals("startTime", 1392831663466L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "perspectives4.networknotary.org", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("destinationAddress", "50.112.37.9", out.get("destinationAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "192.168.12.10", out.get("deviceNameOrIp").get(0));
        assertEquals("destinationDnsDomain", "networknotary.org", out.get("destinationDnsDomain").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("sourceAddress", "81.56.112.95", out.get("sourceAddress").get(0));

    }

    public void testSquidLogCombinedWebProxyLogAvroEventParserWithSyslogHeader() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        // extended log combined format containing resquest size and response size
        String msg = "<134>Oct 14 04:45:40 domU-12-31-39-04-39-AB (squid):   81.56.112.95 - jyria [28/Feb/2014:08:32:35 -0500] \"GET http://www.squid-cache.org/Doc/config/logformat/ HTTP/1.1\" 304 123 431 \"http://wiki.squid-cache.org/Features/LogFormat\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0\" TCP_REFRESH_UNMODIFIED:DIRECT";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("cefSignatureId", "304", out.get("cefSignatureId").get(0));
        assertEquals("destinationNameOrIp", "www.squid-cache.org", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));
        assertEquals("devicePolicyAction", "TCP_REFRESH_UNMODIFIED", out.get("devicePolicyAction").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Squid", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 431, out.get("bytesIn").get(0));
        assertEquals("bytesOut", 123, out.get("bytesOut").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0", out.get("requestClientApplication").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestPath", "/Doc/config/logformat/", out.get("requestPath").get(0));
        assertEquals("requestReferer", "http://wiki.squid-cache.org/Features/LogFormat", out.get("requestReferer").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("sourceNameOrIp", "81.56.112.95", out.get("sourceNameOrIp").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1393594355000L, out.get("startTime").get(0));

        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));

    }

    public void testSquidLogCombinedWebProxyLogAvroEventParserWithSyslogHeader2() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        // default logcombined format
        String msg = "<134>Oct 14 04:45:40 domU-12-31-39-04-39-AB (squid):   81.56.112.95 - jyria [28/Feb/2014:08:32:35 -0500] \"GET http://www.squid-cache.org/Doc/config/logformat/ HTTP/1.1\" 304 431 \"http://wiki.squid-cache.org/Features/LogFormat\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0\" TCP_REFRESH_UNMODIFIED:DIRECT";

        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("cefSignatureId", "304", out.get("cefSignatureId").get(0));
        assertEquals("destinationNameOrIp", "www.squid-cache.org", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));
        assertEquals("devicePolicyAction", "TCP_REFRESH_UNMODIFIED", out.get("devicePolicyAction").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Squid", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 431, out.get("bytesIn").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0", out.get("requestClientApplication").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestPath", "/Doc/config/logformat/", out.get("requestPath").get(0));
        assertEquals("requestReferer", "http://wiki.squid-cache.org/Features/LogFormat", out.get("requestReferer").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("sourceNameOrIp", "81.56.112.95", out.get("sourceNameOrIp").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1393594355000L, out.get("startTime").get(0));

        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));

    }
    
    public void testDpiHttpAvroEventParserStartTimeAsLong() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "{\"data\": {\"requestMethod\": \"GET\", \"startTime\": 1464373081000, \"requestParams\": [{\"name\": \"a\", \"value\": \"321b3f7f-c77e-4798-8487-a71086f57d17\"}, {\"name\": \"s\", \"value\": \"0bf984e9-7894-4e7c-916d-b690b50a2de3\"}, {\"name\": \"c\", \"value\": \"1301947289\"}], \"requestPath\": \"/VFWIU/wiu.ashx\", \"requestVersion\": \"1.1\", \"responseContentType\": \"text/html\", \"requestClientApplication\": \"VfAgent 4.5.44.498\", \"requestHost\": \"svc103.viewfinity.com\", \"responseStatus\": \"200\", \"reqcc\": \"private\", \"responseServer\": \"Microsoft-IIS/7.5\", \"responseTime\": \"1.601\", \"responseContentLength\": \"222\", \"requestContentLength\": \"341\"}, \"net\": {\"destNameOrIp\": \"198.11.230.104\", \"sourceNameOrIp\": \"10.44.251.25\", \"sourcePort\": \"9983\", \"destPort\": \"80\", \"transportProtocol\": \"tcp\", \"timestamp\": 1464373015, \"application\": \"http\", \"dpiFlowId\": \"86882\"}, \"dpiSignatureId\": \"URL request\"}";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "packet");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
        assertEquals("sourcePort", 9983, out.get("sourcePort").get(0));
        assertEquals("destinationAddress", "198.11.230.104", out.get("destinationAddress").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestParams", "[{\"name\":\"a\",\"value\":\"321b3f7f-c77e-4798-8487-a71086f57d17\"},{\"name\":\"s\",\"value\":\"0bf984e9-7894-4e7c-916d-b690b50a2de3\"},{\"name\":\"c\",\"value\":\"1301947289\"}]", out.get("requestParams").get(0));
        assertEquals("destinationNameOrIp", "svc103.viewfinity.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "Dpi-Http", out.get("externalLogSourceType").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("startTime", 1464373081000L, out.get("startTime").get(0));
        assertEquals("requestPath", "/VFWIU/wiu.ashx", out.get("requestPath").get(0));
        assertEquals("bytesOut", 222, out.get("bytesOut").get(0));
        assertEquals("bytesIn", 341, out.get("bytesIn").get(0));
        assertEquals("dpiSignatureId", "URL request", out.get("dpiSignatureId").get(0));
        assertEquals("dpiFlowId", "86882", out.get("dpiFlowId").get(0));
        assertEquals("destinationDnsDomain", "viewfinity.com", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "10.44.251.25", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "packet", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("destinationHostName", "svc103", out.get("destinationHostName").get(0));
        assertEquals("message", "URL request", out.get("message").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "VfAgent 4.5.44.498", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "text/html", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceNameOrIp", "10.44.251.25", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    }


    public void testDpiHttpAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "{\"data\":{\"startTime\":\"1359931603.14201\",\"requestClientApplication\":\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\",\"requestContentLength\":0,\"requestHost\":\"google.com\",\"requestMethod\":\"GET\",\"requestParams\":[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}],\"requestQuery\":\"/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB\",\"requestVersion\":\"1.1\",\"responseContentLength\":3826,\"responseContentType\":\"text/html\",\"responseServer\":\"Microsoft-IIS/5.0\",\"responseStatus\":\"200\"},\"dpiSignatureId\":\"URL request\",\"net\":{\"application\":\"http\",\"destNameOrIp\":\"51.78.4.1\",\"destPort\":80,\"dpiFlowId\":\"1893479\",\"sourceNameOrIp\":\"192.168.1.1\",\"sourcePort\":4190,\"transportProtocol\":\"tcp\"}}";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "packet");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "packet", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("responseContentType", "text/html", out.get("responseContentType").get(0));
        assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
        assertEquals("sourcePort", 4190, out.get("sourcePort").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("sourceNameOrIp", "192.168.1.1", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Dpi-Http", out.get("externalLogSourceType").get(0));
        assertEquals("dpiSignatureId", "URL request", out.get("dpiSignatureId").get(0));
        assertEquals("bytesIn", 0, out.get("bytesIn").get(0));
        assertEquals("startTime", 1359931603142L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "google.com", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationDnsDomain", "google.com", out.get("destinationDnsDomain").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)", out.get("requestClientApplication").get(0));
        assertEquals("bytesOut", 3826, out.get("bytesOut").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("dpiFlowId", "1893479", out.get("dpiFlowId").get(0));
        assertEquals("sourceAddress", "192.168.1.1", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("requestParams", "[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}]", out.get("requestParams").get(0));
    }

    public void testDpiHttpAvroEventParser2() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "{\"data\":{\"requestMethod\":\"GET\",\"startTime\":\"1216691467.114897\",\"requestPath\":\"/mail/\",\"requestParams\":[{\"name\":\"logout\"},{\"name\":\"hl\",\"value\":\"en\"}],\"requestVersion\":\"1.1\",\"requestClientApplication\":\"Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_4; en-us) AppleWebKit/525.18 (KHTML, like Gecko) Version/3.1.2 Safari/525.20.1\",\"requestReferer\":\"http://mail.google.com/mail/?ui=2&view=bsp&ver=1qygpcgurkovy\",\"requestHost\":\"mail.google.com\",\"responseContentLength\":\"1214\",\"responseStatus\":\"302\",\"responseContentType\":\"text/html\",\"responseServer\":\"GFE/1.3\"},\"net\":{\"destNameOrIp\":\"192.168.1.64\",\"sourceNameOrIp\":\"74.125.19.19\",\"sourcePort\":\"35011\",\"destPort\":\"80\",\"transportProtocol\":\"tcp\",\"timestamp\":1216691467},\"dpiSignatureId\":\"URL request\"}";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "packet");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("logCollectionCategory", "packet", out.get("logCollectionCategory").get(0));
        assertEquals("bytesOut", 1214, out.get("bytesOut").get(0));
        assertEquals("responseContentType", "text/html", out.get("responseContentType").get(0));
        assertEquals("destinationNameOrIp", "mail.google.com", out.get("destinationNameOrIp").get(0));
        assertEquals("sourcePort", 35011, out.get("sourcePort").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("requestParams", "[{\"name\":\"logout\"},{\"name\":\"hl\",\"value\":\"en\"}]", out.get("requestParams").get(0));
        assertEquals("requestPath", "/mail/", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "74.125.19.19", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Dpi-Http", out.get("externalLogSourceType").get(0));
        assertEquals("dpiSignatureId", "URL request", out.get("dpiSignatureId").get(0));
        assertEquals("startTime", 1216691467114L, out.get("startTime").get(0));
        assertEquals("cefSignatureId", "302", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_4; en-us) AppleWebKit/525.18 (KHTML, like Gecko) Version/3.1.2 Safari/525.20.1", out.get("requestClientApplication").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestReferer", "http://mail.google.com/mail/?ui=2&view=bsp&ver=1qygpcgurkovy", out.get("requestReferer").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

    }

    public void testDpiDnsAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.DnsMef);
        Event avroEvent = new Event();
        String msg = "{\"data\": {\"startTime\": \"1397202576.372667\", \"transaction_id\": \"2859\", \"rtt\": \"0.00909\", \"query\": \"refer.ccbill.com\", \"query_type\": \"Host address\", \"name\": \"refer.ccbill.com\", \"ttl\": \"30\", \"host_addr\": \"64.38.212.57\", \"host_type\": \"IP address\"}, \"net\": {\"destNameOrIp\": \"10.240.62.89\", \"sourceNameOrIp\": \"172.16.0.23\", \"sourcePort\": \"35418\", \"destPort\": \"53\", \"transportProtocol\": \"udp\", \"timestamp\": 1397202506, \"application\": \"dns\"}, \"dpiSignatureId\": \"DNS response\"}";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "packet");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "packet", out.get("logCollectionCategory").get(0));
        assertEquals("destinationNameOrIp", "10.240.62.89", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 53, out.get("destinationPort").get(0));
        assertEquals("dnsRecordIp", "64.38.212.57", out.get("dnsRecordIp").get(0));
        assertEquals("dnsRecordName", "refer.ccbill.com", out.get("dnsRecordName").get(0));
        assertEquals("dnsRecordTTL", 30, out.get("dnsRecordTTL").get(0));
        assertEquals("dnsRecordType", "IP address", out.get("dnsRecordType").get(0));
        assertEquals("externalLogSourceType", "Dpi-Dns", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "DnsMef", out.get("logSourceType").get(0));
        assertEquals("query", "refer.ccbill.com", out.get("query").get(0));
        assertEquals("queryRTT", 0.00909f, out.get("queryRTT").get(0));
        assertEquals("queryType", "Host address", out.get("queryType").get(0));
        assertEquals("sourceNameOrIp", "172.16.0.23", out.get("sourceNameOrIp").get(0));
        assertEquals("sourcePort", 35418, out.get("sourcePort").get(0));
        assertEquals("startTime", 1397202576372L, out.get("startTime").get(0));
        assertEquals("transactionId", "2859", out.get("transactionId").get(0));
        assertEquals("transportProtocol", "udp", out.get("transportProtocol").get(0));
        assertEquals("parserOutFormat", "DnsMef", out.get("parserOutFormat").get(0));
    }

    public void testDpiSslAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, SupportedFormats.CertMef);
        Event avroEvent = new Event();
        String msg = "{\"data\": {\"startTime\": \"1425684968.764013\", \"crt_md5\": \"FFFFFF5DFF16FF0EFF77FF0BFFFF3CFFFFFFD5\", \"crt_sln\": \"52FF27230842FF065AFF487B39FF6AFF00\", \"crt_issuer\": \"VeriSign Class 3 Secure Server CA - G3\", \"crt_noValidBefore\": \"140811000000Z\", \"crt_noValidAfter\": \"151102235959Z\", \"crt_commonName\": \"textchat.bankofamerica.com\", \"crt_subjAltName\": \"textchat.bankofamerica.com\"}, \"net\": {\"destNameOrIp\": \"208.89.15.8\", \"sourceNameOrIp\": \"192.168.1.56\", \"sourcePort\": \"53232\", \"destPort\": \"443\", \"transportProtocol\": \"tcp\", \"timestamp\": 1425684968, \"application\": \"ssl\", \"dpiFlowId\": \"5\"}, \"dpiSignatureId\": \"SSL CERTIFICATE\"}";


        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "packet");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out);


        assertEquals("certCommonName", "textchat.bankofamerica.com", out.get("certCommonName").get(0));
        assertEquals("certIssuer", "VeriSign Class 3 Secure Server CA - G3", out.get("certIssuer").get(0));
        assertEquals("certMd5", "FFFFFF5DFF16FF0EFF77FF0BFFFF3CFFFFFFD5", out.get("certMd5").get(0));
        assertEquals("certNoValidAfter", 1446508799000L, out.get("certNoValidAfter").get(0));
        assertEquals("certNoValidBefore", 1407715200000L, out.get("certNoValidBefore").get(0));
        assertEquals("certSerial", "52FF27230842FF065AFF487B39FF6AFF00", out.get("certSerial").get(0));
        assertEquals("certSubject", "textchat.bankofamerica.com", out.get("certSubject").get(0));
        assertEquals("destinationNameOrIp", "208.89.15.8", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
        assertEquals("cefSignatureId", "SSL CERTIFICATE", out.get("cefSignatureId").get(0));
        assertEquals("externalLogSourceType", "Dpi-ssl", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "CertMef", out.get("logSourceType").get(0));
        assertEquals("sourceAddress", "192.168.1.56", out.get("sourceAddress").get(0));
        assertEquals("sourceNameOrIp", "192.168.1.56", out.get("sourceNameOrIp").get(0));
        assertEquals("sourcePort", 53232, out.get("sourcePort").get(0));
        assertEquals("startTime", 1425684968764L, out.get("startTime").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
    }


    public void testNetFlow5AvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.FlowMef);
        Event avroEvent = new Event();
        String msg = "[NetFlow: 9995] version=\"5\",sysUptime=\"272171940\",unixSecs=\"2010-03-25T14:09:13\",unixNsecs=\"685206264\",flowSequence=\"43975\",engineType=\"0\",engineId=\"0\",samplingInterval=\"0\",IN_BYTES=\"\",IN_PKTS=\"\",FLOWS=\"\",PROTOCOL=\"0\",TCP_FLAGS=\"138\",L4_SRC_PORT=\"4152\",IPV4_SRC_ADDR=\"24.32.0.0\",INPUT_SNMP=\"2620\",L4_DST_PORT=\"46348\",IPV4_DST_ADDR=\"10.60.0.112\",OUTPUT_SNMP=\"1\",SRC_AS=\"16\",DST_AS=\"4352\",MUL_DST_PKTS=\"\",MUL_DST_BYTES=\"\",LAST_SWITCHED=\"272151820\",FIRST_SWITCHED=\"229\",OUT_BYTES=\"\",OUT_PKTS=\"\",MIN_PKT_LNGTH=\"\",MAX_PKT_LNGTH=\"\",IPV6_SRC_ADDR=\"\",IPV6_DST_ADDR=\"\",SAMPLING_INTERVAL=\"\",SAMPLING_ALGORITHM=\"\",FLOW_ACTIVE_TIMEOUT=\"\",FLOW_INACTIVE_TIMEOUT=\"\",TOTAL_BYTES_EXP=\"1\",TOTAL_PKTS_EXP=\"65537\",TOTAL_FLOWS_EXP=\"\",SRC_VLAN=\"\",DST_VLAN=\"\",IF_NAME=\"\",IF_DESC=\"\",DST_MASK=\"0\",IPV4_NEXT_HOP=\"10.60.255.255\",SRC_MASK=\"0\",SRC_TOS=\"138\"";
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
        assertEquals("sourcePort", 4152, out.get("sourcePort").get(0));
        assertEquals("destinationPort", 46348, out.get("destinationPort").get(0));
        assertEquals("transportProtocol", "0", out.get("transportProtocol").get(0));
        assertEquals("sourceMask", "0", out.get("sourceMask").get(0));
        assertEquals("logSourceType", "FlowMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "NetFlow_v5", out.get("externalLogSourceType").get(0));
        assertEquals("destinationAutonomousSystem", 4352, out.get("destinationAutonomousSystem").get(0));
        assertEquals("sourceAutonomousSystem", 16, out.get("sourceAutonomousSystem").get(0));
        assertEquals("startTime", 685206264000L, out.get("startTime").get(0));
        assertEquals("destinationMask", "0", out.get("destinationMask").get(0));
        assertEquals("destinationAddress", "10.60.0.112", out.get("destinationAddress").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("tcpFlags", 138, out.get("tcpFlags").get(0));
        assertEquals("nextHopAddress", "10.60.255.255", out.get("nextHopAddress").get(0));
        assertEquals("sourceTos", 138, out.get("sourceTos").get(0));
        assertEquals("totalPacketsExp", 65537L, out.get("totalPacketsExp").get(0));
        assertEquals("totalBytesExp", 1L, out.get("totalBytesExp").get(0));
        assertEquals("sourceAddress", "24.32.0.0", out.get("sourceAddress").get(0));
        assertEquals("samplingInterval", 0, out.get("samplingInterval").get(0));
        assertEquals("parserOutFormat", "FlowMef", out.get("parserOutFormat").get(0));
    }

    public void testNProbeAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.FlowMef);
        Event avroEvent = new Event();
        String msg = "10.240.62.89,54.234.106.123,53630,10001,1397033525,1397033525,8685,231,5,4,12:31:39:04:39:AB,FE:FF:FF:FF:FF:FF,0,6,tcp,0,24,0,0,0.0.0.0,0,0,0,1,1,120,30,0,231,3796,6,24,49,49,0,0,1,0,0,1397033525364,1397033525373,10001,4,1";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "nprobe");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("bytesIn", 8685L, out.get("bytesIn").get(0));
        assertEquals("bytesOut", 231L, out.get("bytesOut").get(0));
        assertEquals("destinationAddress", "54.234.106.123", out.get("destinationAddress").get(0));
        assertEquals("destinationAutonomousSystem", 0, out.get("destinationAutonomousSystem").get(0));
        assertEquals("destinationMacAddress", "FE:FF:FF:FF:FF:FF", out.get("destinationMacAddress").get(0));
        assertEquals("destinationMask", "0", out.get("destinationMask").get(0));
        assertEquals("destinationPort", 10001, out.get("destinationPort").get(0));
        assertEquals("destinationVlan", 0, out.get("destinationVlan").get(0));
        assertEquals("direction", "1", out.get("direction").get(0));
        assertEquals("endTime", 1397033525373L, out.get("endTime").get(0));
        assertEquals("engineId", "231", out.get("engineId").get(0));
        assertEquals("engineType", "0", out.get("engineType").get(0));
        assertEquals("externalLogSourceType", "nProbe", out.get("externalLogSourceType").get(0));
        assertEquals("icmpType", 0, out.get("icmpType").get(0));
        assertEquals("logSourceType", "FlowMef", out.get("logSourceType").get(0));
        assertEquals("maxTTL", 49, out.get("maxTTL").get(0));
        assertEquals("minTTL", 49, out.get("minTTL").get(0));
        assertEquals("nextHopAddress", "0.0.0.0", out.get("nextHopAddress").get(0));
        assertEquals("numberFlows", 0L, out.get("numberFlows").get(0));
        assertEquals("packetsIn", 5L, out.get("packetsIn").get(0));
        assertEquals("packetsOut", 4L, out.get("packetsOut").get(0));
        assertEquals("samplingAlgorithm", "1", out.get("samplingAlgorithm").get(0));
        assertEquals("samplingInterval", 1, out.get("samplingInterval").get(0));
        assertEquals("sourceAddress", "10.240.62.89", out.get("sourceAddress").get(0));
        assertEquals("sourceAutonomousSystem", 0, out.get("sourceAutonomousSystem").get(0));
        assertEquals("sourceMacAddress", "12:31:39:04:39:AB", out.get("sourceMacAddress").get(0));
        assertEquals("sourceMask", "0", out.get("sourceMask").get(0));
        assertEquals("sourcePort", 53630, out.get("sourcePort").get(0));
        assertEquals("sourceTos", 0, out.get("sourceTos").get(0));
        assertEquals("sourceVlan", 0, out.get("sourceVlan").get(0));
        assertEquals("startTime", 1397033525364L, out.get("startTime").get(0));
        assertEquals("tcpFlags", 24, out.get("tcpFlags").get(0));
        assertEquals("totalBytesExp", 3796L, out.get("totalBytesExp").get(0));
        assertEquals("totalFlowsExp", 24L, out.get("totalFlowsExp").get(0));
        assertEquals("totalPacketsExp", 6L, out.get("totalPacketsExp").get(0));
        assertEquals("transportProtocol", "6", out.get("transportProtocol").get(0));
        assertEquals("parserOutFormat", "FlowMef", out.get("parserOutFormat").get(0));
    }

    /* created to troubleshoot issue with rawlog changing and not the parsed result. */
    public void testmultipleEventsParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "2014-03-17 14:16:48 49 45.14.5.246 200 TCP_NC_MISS 21712 669 GET http www.bellagio.com /pages/shops_via.asp - - DIRECT www.bellagio.com text/html \"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.5) Gecko/20041107 Firefox/1.0\" PROXIED Travel - 192.16.170.42 SG-HTTP-Service - none -\t411315";
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
        assertEquals("deviceAction", "TCP_NC_MISS", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/pages/shops_via.asp", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "45.14.5.246", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 669, out.get("bytesIn").get(0));
        assertEquals("startTime", 1395065808000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "www.bellagio.com", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "bellagio.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.5) Gecko/20041107 Firefox/1.0", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "Travel", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 21712, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "45.14.5.246", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("destinationHostName", "www", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "bellagio.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceCustomString2", "49", out.get("deviceCustomString2").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

        msg = "2014-03-17 14:16:48 13 45.14.5.177 200 TCP_RESCAN_HIT 2802 304 GET http server-dk.imrworldwide.com /a1.js - - DIRECT 80.80.13.206 application/x-javascript \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)\" PROXIED Web%20Advertisements - 192.16.170.42 SG-HTTP-Service - none -\t411512";
        avroEvent = new Event();
        buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        output = instance.parse(avroEvent);
        out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("deviceAction", "TCP_RESCAN_HIT", out.get("deviceAction").get(0));
        assertEquals("requestPath", "/a1.js", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "45.14.5.177", out.get("sourceNameOrIp").get(0));
        assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("bytesIn", 304, out.get("bytesIn").get(0));
        assertEquals("startTime", 1395065808000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "server-dk.imrworldwide.com", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "imrworldwide.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)", out.get("requestClientApplication").get(0));
        assertEquals("deviceEventCategory", "Web%20Advertisements", out.get("deviceEventCategory").get(0));
        assertEquals("bytesOut", 2802, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "45.14.5.177", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("devicePolicyAction", "PROXIED", out.get("devicePolicyAction").get(0));
        assertEquals("destinationHostName", "server-dk", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "imrworldwide.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceCustomString2", "13", out.get("deviceCustomString2").get(0));
        assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
        assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
    }

    public void testSnareEventParserTmp() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event();
        String msg = "MSWinEventLog\t0\tSecurity\t0\tFri Aug 21 15 59 34 2015\t4625\tMicrosoft-Windows-Security-Auditing\tSYSTEM\tWell Known Group\tFailure Audit\twindc.acmebank.com \tLogon\tAn account failed to log on.    Subject:   Security ID:  S-1-5-18   Account Name:  SERVICEACCOUNT$   Account Domain:  acmebank.com   Logon ID:  0x3e7    Logon Type:   2    Account For Which Logon Failed:   Security ID:  S-1-0-0   Account Name:  Kevin_Ogletree   Account Domain:  acmebank.com    Failure Information:   Failure Reason:  The user has not been granted the requested logon type at this machine.   Status:   0xc000015b   Sub Status:  0x0    Process Information:   Caller Process ID: 0x25b4   Caller Process Name: C:\\Program Files\\Software\\bin\\Agent.exe    Network Information:   Workstation Name: WSRE862   Source Network Address: 192.168.1.162   Source Port:  53176    Detailed Authentication Information:   Logon Process:  Advapi     Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon request fails. It is generated on the computer where access was attempted.    The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service";
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
        assertEquals("sourcePort", 53176, out.get("sourcePort").get(0));
        assertEquals("sourceNtDomain", "acmebank.com", out.get("sourceNtDomain").get(0));
        assertEquals("sourceHostName", "wsre862", out.get("sourceHostName").get(0));
        assertTrue("uuid",  out.containsKey("uuid"));
        assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
        assertEquals("destinationUserName", "Kevin_Ogletree".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("logonProcess", "Advapi", out.get("logonProcess").get(0));
        assertEquals("startTime", 1440172774000L, out.get("startTime").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("cefEventName", "An account failed to log on.", out.get("cefEventName").get(0));
        assertEquals("destinationNtDomain", "acmebank.com", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "192.168.1.162", out.get("sourceAddress").get(0));
        assertEquals("sourceUserName", "SERVICEACCOUNT$".toLowerCase(), out.get("sourceUserName").get(0));
        assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "C:\\Program Files\\Software\\bin\\Agent.exe", out.get("sourceProcessName").get(0));
        assertEquals("cefSignatureId", "Security-4625-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("subStatus", "0x0", out.get("subStatus").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("authenticationPackage", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("authenticationPackage").get(0));
        assertEquals("destinationSecurityID", "S-1-0-0", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceUserName", "SYSTEM".toLowerCase(), out.get("deviceUserName").get(0));
        assertEquals("sourceLogonType", "2", out.get("sourceLogonType").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceProcessID", "0x25b4", out.get("sourceProcessID").get(0));
        assertEquals("deviceNameOrIp", "windc.acmebank.com", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "WSRE862", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("status", "0xc000015b", out.get("status").get(0));
    }

    //
    public void testWebsenseWBSNProxyBlockedToWebProxy() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "2016-04-04T16:29:27.015125-07:00 1.2.3.4 LEEF: 1.0|Websense|Security|8.1.0|transaction:blocked|sev=7#011cat=29#011usrName=-#011src=10.00.000.00#011srcPort=53536#011srcBytes=0#011dstBytes=0#011dst=172.000.0.000#011dstPort=443#011proxyStatus-code=200#011serverStatus-code=0#011duration=0#011method=CONNECT#011disposition=1025#011contentType=-#011reason=-#011policy=Super Administrator**Default#011role=8#011userAgent=-#011url=https://www.googletagservices.com";
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
    }

    public void testWebsenseWBSNProxyPermittedToWebProxy() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "2016-04-04T16:15:28.710863-07:00 1.2.3.4 LEEF: 1.0|Websense|Security|8.1.0|transaction:permitted|sev=1#011cat=227#011usrName=WinNT://WEBSENSE.COM/username#011src=10.00.000.00#011srcPort=60583#011srcBytes=521#011dstBytes=88#011dst=50.00.000.000#011dstPort=443#011proxyStatus-code=200#011serverStatus-code=200#011duration=0#011method=GET#011disposition=1026#011contentType=image/gif#011reason=-#011policy=Super Administrator**APAC Corp Network#011role=8#011userAgent=Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)#011url=https://bam.nr-data.net/jserrors";
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
        assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
        assertEquals("reason", "-", out.get("reason").get(0));
        assertEquals("sourcePort", 60583, out.get("sourcePort").get(0));
        assertEquals("destinationAddress", "50.0.0.0", out.get("destinationAddress").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("destinationNameOrIp", "bam.nr-data.net", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "leefWebsenseSecurity", out.get("externalLogSourceType").get(0));
        assertEquals("deviceAddress", "1.2.3.4", out.get("deviceAddress").get(0));
        assertEquals("receiptTime", 1459811728710L, out.get("receiptTime").get(0));
        assertEquals("devicePolicyAction", "Super Administrator**APAC Corp Network", out.get("devicePolicyAction").get(0));
        assertEquals("startTime", 1459811728710L, out.get("startTime").get(0));
        assertEquals("requestPath", "/jserrors", out.get("requestPath").get(0));
        assertEquals("bytesOut", 88, out.get("bytesOut").get(0));
        assertEquals("bytesIn", 521, out.get("bytesIn").get(0));
        assertEquals("destinationDnsDomain", "nr-data.net", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "10.0.0.0", out.get("sourceAddress").get(0));
        assertEquals("deviceAction", "permitted", out.get("deviceAction").get(0));
        assertEquals("sourceUserName", "WinNT://WEBSENSE.COM/username", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "https", out.get("requestScheme").get(0));
        assertEquals("destinationHostName", "bam", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceEventCategory", "227", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "image/gif", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "1.2.3.4", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));

    }

    public void testSnareEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "MSWinEventLog\t0\tSecurity\t10909\tWed Apr 23 15:51:08 2014\t4624\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tLogon\t\tAn account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    New Logon:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0x9db891   Logon GUID:  {256434D9-27A8-8DD4-DC5D-E6466FFA6773}    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name:    Source Network Address: 192.168.12.21   Source Port:  58743    Detailed Authentication Information:   Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).    The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.    The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The authentication information fields provide detailed information about this specific logon request.   - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.\t10908 ";
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
        assertEquals("sourcePort", 58743, out.get("sourcePort").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("startTime", 1398268268000L, out.get("startTime").get(0));
        assertEquals("destinationLogonID", "0x9db891", out.get("destinationLogonID").get(0));
        assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
//        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
//        assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationLogonGUID", "{256434D9-27A8-8DD4-DC5D-E6466FFA6773}", out.get("destinationLogonGUID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
        assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("deviceUserName", "E8SEC\\jyria".toLowerCase(), out.get("deviceUserName").get(0));
        assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
        assertEquals("cefEventName", "An account was successfully logged on.", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("destinationSecurityID").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }

    public void testWindowsVRZdatasetEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<134>Dec 07 10:50:43 192.168.15.10 MSWinEventLog\t0\tSecurity\t0\tSun Dec 07 10 50 43 2014\t4672\tMicrosoft-Windows-Security-Auditing\tsvc-LogReader\tUser\tSuccess Audit\ttpap1lvdchw01v.vzh.ent.verizon.com\tSpecial Logon\t\tSpecial privileges assigned to new logon.    Subject:   Security ID:  S-1-5-21-1482476501-484061587-839522115-43653   Account Name:  svc-LogReader   Account Domain:  VZH   Logon ID:  0x85c7a2e    Privileges:  SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeDebugPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeTcbPrivilege     SeCreateTokenPrivilege     SeAuditPrivilege     SeAssignPrimaryTokenPrivilege     SeEnableDelegationPrivilege\t304283\t375351";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "zyxelhost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("deviceAddress", "192.168.15.10", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("destinationUserName", "svc-LogReader".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("startTime", 1417949443000L, out.get("startTime").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("deviceUserName", "svc-LogReader".toLowerCase(), out.get("deviceUserName").get(0));
        assertEquals("cefEventName", "Special privileges assigned to new logon.", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "192.168.15.10", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "zyxelhost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4672-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "VZH", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-1482476501-484061587-839522115-43653", out.get("destinationSecurityID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }

    public void testWindowsEvt4769EventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "MSWinEventLog\t0\tSecurity\t10905\tWed Apr 23 15:51:08 2014\t4769\tMicrosoft-Windows-Security-Auditing\tE8SEC.LAB\\jyria@E8SEC.LAB\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  jyria@E8SEC.LAB   Account Domain:  E8SEC.LAB   Logon GUID:  {AD7B3FA1-ABFF-23FE-F052-73002A41531D}    Service Information:   Service Name:  lab/W2K8R2-SRC.e8sec.lab   Service ID:  S-1-5-21-1908711140-209524994-2501478566-1106    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58742    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.\t4767";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "win");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
        assertEquals("sourcePort", 58742, out.get("sourcePort").get(0));
        assertEquals("destinationProcessName", "lab", out.get("destinationProcessName").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("destinationServiceName", "lab/W2K8R2-SRC.e8sec.lab", out.get("destinationServiceName").get(0));
        assertEquals("startTime", 1398268268000L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "W2K8R2-SRC.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
        assertEquals("logCollectionHost", "win", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("status", "0x0", out.get("status").get(0));
        assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationLogonGUID", "{AD7B3FA1-ABFF-23FE-F052-73002A41531D}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationHostName", "w2k8r2-src", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationServiceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1106", out.get("destinationServiceSecurityID").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("destinationUserName", "jyria@E8SEC.LAB".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("deviceUserName", "E8SEC.LAB\\jyria@E8SEC.LAB".toLowerCase(), out.get("deviceUserName").get(0));
        assertEquals("cefEventName", "A Kerberos service ticket was requested.", out.get("cefEventName").get(0));
        assertEquals("ticketOptions", "0x40810000", out.get("ticketOptions").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4769-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "E8SEC.LAB", out.get("destinationNtDomain").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    }

    public void testWindowsDhcpEpilogEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HETMef);
        Event avroEvent = new Event();
        String msg = "<13>Oct 29 11:46:18 SVI-DC01.ati.pri GenericLog\t0\t11,10/28/14,14: 32:33,Renew,172.16.4.97,MBSVI0094.ati.pri,6003089F10B8,,247694514,0,,,";
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
        assertEquals("startTime", 1414506753000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationNameOrIp", "MBSVI0094.ati.pri", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationMacAddress", "6003089F10B8", out.get("destinationMacAddress").get(0));
        assertEquals("syslogMessage", "GenericLog	0	11,10/28/14,14: 32:33,Renew,172.16.4.97,MBSVI0094.ati.pri,6003089F10B8,,247694514,0,,,", out.get("syslogMessage").get(0));
        assertEquals("eventOutcome", "11 - Renew", out.get("eventOutcome").get(0));
        assertEquals("destinationAddress", "172.16.4.97", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "SVI-DC01.ati.pri", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", "1384693669604", out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "windows-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    public void testDnsmasqDhcpEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HETMef);
        Event avroEvent = new Event();
        String msg = "<30>May  9 09:52:40 dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
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
        //GMT: Fri, 09 May 2014 16:52:40 GMT : 1399654360000L
        assertEquals("startTime", 1462812760000L, out.get("startTime").get(0));
        assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", out.get("destinationMacAddress").get(0));
        assertEquals("deviceProcessId", 304, out.get("deviceProcessId").get(0));
        assertEquals("deviceInterface", "br0", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dnsmasq-dhcp", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "192.168.10.208", out.get("destinationAddress").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "dnsmasq-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    public void testDnsmasqDhcpEventParser2() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HETMef);
        Event avroEvent = new Event();
        String msg = "<30>Jun  9 09:52:40 192.168.14.10 dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1433868760000");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("startTime", 1465491160000L, out.get("startTime").get(0));
        assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", out.get("destinationMacAddress").get(0));
        assertEquals("deviceProcessId", 304, out.get("deviceProcessId").get(0));
        assertEquals("deviceInterface", "br0", out.get("deviceInterface").get(0));
        assertEquals("deviceProcessName", "dnsmasq-dhcp", out.get("deviceProcessName").get(0));
        assertEquals("destinationAddress", "192.168.10.208", out.get("destinationAddress").get(0));
        assertEquals("logCollectionTime", 1433868760000L, out.get("logCollectionTime").get(0));
        assertEquals("deviceNameOrIp", "192.168.14.10", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "dnsmasq-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    }

    public void testZyxelDhcpEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HETMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "Dec  1 11:58:56 zywall-usg-200 src=\"0.0.0.0: 0\" dst=\"0.0.0.0:0\" msg=\"Requested 192.168.2.49 from android-5d9a8e930cf598c9(F4:09:D8:A1:41:A5)\" note=\"DHCP Request\" user=\"unknown\" devID=\"b0b2dcbe45d3\" cat=\"DHCP\"";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "zyxelhost");
        headers.put("timestamp", "1384693669604");
        headers.put("uuid", "my-very-own-uuid");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("startTime", 1480593536000L, out.get("startTime").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceNameOrIp", "zywall-usg-200", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "zyxelhost", out.get("logCollectionHost").get(0));
        assertEquals("sourceHostName", "android-5d9a8e930cf598c9", out.get("sourceHostName").get(0));
        assertEquals("externalLogSourceType", "zyxel-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("sourceMacAddress", "F4:09:D8:A1:41:A5", out.get("sourceMacAddress").get(0));
        assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("sourceAddress", "192.168.2.49", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
        assertEquals("uuid", "my-very-own-uuid", out.get("uuid").get(0));
    }

    public void testWindowsCsvdeExportHeaderParser() throws IOException, Exception {
        this.morphlineFile = "iamdbmef-ms-ad.conf";
        this.morphlineId = "msad-csvde_parser";
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMDBMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String line = "\"DC=@,DC=RootDNSServers,CN=MicrosoftDNS,CN=System,DC=e8sec,DC=lab\",dnsNode,\"DC=@,DC=RootDNSServers,CN=MicrosoftDNS,CN=System,DC=e8sec,DC=lab\",4,20140415101954.0Z,20140415101954.0Z,,12473,12473,@,X'a9111578cee4814c80e3218473dba15f',,,,,,,,,,,,,,,,,,,,,,,\"CN=Dns-Node,CN=Schema,CN=Configuration,DC=e8sec,DC=lab\",,,20140425080456.0Z;20140415101954.0Z;20140415101954.0Z;16010101000001.0Z,,,,,,,,,,,@,,,TRUE,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,X'1600020005080000000000000000000000000000000000001403016d0c726f6f742d73657276657273036e657400';X'1600020005080000000000000000000000000000000000001403016c0c726f6f742d73657276657273036e657400';X'1600020005080000000000000000000000000000000000001403016b0c726f6f742d73657276657273036e657400';X'1600020005080000000000000000000000000000000000001403016a0c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301690c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301680c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301670c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301660c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301650c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301640c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301630c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301620c726f6f742d73657276657273036e657400';X'160002000508000000000000000000000000000000000000140301610c726f6f742d73657276657273036e657400',,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
        String msg = "DN,objectClass,distinguishedName,instanceType,whenCreated,whenChanged,subRefs,uSNCreated,uSNChanged,name,objectGUID,creationTime,forceLogoff,lockoutDuration,lockOutObservationWindow,lockoutThreshold,maxPwdAge,minPwdAge,minPwdLength,modifiedCountAtLastProm,nextRid,pwdProperties,pwdHistoryLength,objectSid,serverState,uASCompat,modifiedCount,auditingPolicy,nTMixedDomain,rIDManagerReference,fSMORoleOwner,systemFlags,wellKnownObjects,objectCategory,isCriticalSystemObject,gPLink,dSCorePropagationData,otherWellKnownObjects,masteredBy,ms-DS-MachineAccountQuota,msDS-Behavior-Version,msDS-PerUserTrustQuota,msDS-AllUsersTrustQuota,msDS-PerUserTrustTombstonesQuota,msDs-masteredBy,msDS-IsDomainFor,msDS-NcType,dc,cn,description,showInAdvancedViewOnly,ou,msDS-TombstoneQuotaFactor,displayName,flags,versionNumber,gPCFunctionalityVersion,gPCFileSysPath,gPCMachineExtensionNames,ipsecName,ipsecID,ipsecDataType,ipsecData,ipsecISAKMPReference,ipsecNFAReference,ipsecOwnersReference,ipsecNegotiationPolicyReference,ipsecFilterReference,iPSECNegotiationPolicyType,iPSECNegotiationPolicyAction,revision,memberOf,userAccountControl,badPwdCount,codePage,countryCode,badPasswordTime,lastLogoff,lastLogon,logonHours,pwdLastSet,primaryGroupID,adminCount,accountExpires,logonCount,sAMAccountName,sAMAccountType,lastLogonTimestamp,groupType,member,samDomainUpdates,localPolicyFlags,operatingSystem,operatingSystemVersion,operatingSystemServicePack,serverReferenceBL,dNSHostName,rIDSetReferences,servicePrincipalName,msDS-SupportedEncryptionTypes,msDFSR-ComputerReferenceBL,rIDAvailablePool,rIDAllocationPool,rIDPreviousAllocationPool,rIDUsedPool,rIDNextRID,dnsRecord,msDFSR-Flags,msDFSR-ReplicationGroupType,msDFSR-FileFilter,msDFSR-DirectoryFilter,serverReference,msDFSR-ComputerReference,msDFSR-MemberReferenceBL,msDFSR-Version,msDFSR-ReplicationGroupGuid,msDFSR-MemberReference,msDFSR-RootPath,msDFSR-StagingPath,msDFSR-Enabled,msDFSR-Options,msDFSR-ContentSetGuid,msDFSR-ReadOnly,lastSetTime,priorSetTime,givenName,managedObjects,userPrincipalName,sn,lockoutTime,managedBy,directReports,userWorkstations,c,l,st,title,postalCode,postOfficeBox,physicalDeliveryOfficeName,telephoneNumber,facsimileTelephoneNumber,initials,otherTelephone,info,co,department,company,streetAddress,wWWHomePage,homeDirectory,scriptPath,profilePath,ipPhone,mail,manager,homePhone,mobile,pager";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "zyxelhost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        //parse header first
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertEquals(null, output);
        buf = ByteBuffer.wrap(line.getBytes());
        avroEvent.setBody(buf);
        //
        output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("creationDate", 1397557194000L, out.get("creationDate").get(0));
        assertEquals("canonicalName", "@.RootDNSServers.e8sec.lab/System/MicrosoftDNS", out.get("canonicalName").get(0));
        assertEquals("objectName", "@", out.get("objectName").get(0));
        assertEquals("objectCategory", "e8sec.lab/Configuration/Schema/Dns-Node", out.get("objectCategory").get(0));
        assertEquals("objectClass", "dnsNode", out.get("objectClass").get(0));
        assertEquals("objectGUID", "781511a9-e4ce-4c81-80e3-218473dba15f", out.get("objectGUID").get(0));
        assertEquals("lastModificationDate", 1397557194000L, out.get("lastModificationDate").get(0));
        assertEquals("objectDistinguishedName", "@.RootDNSServers.e8sec.lab/System/MicrosoftDNS", out.get("objectDistinguishedName").get(0));
        assertEquals("parserOutFormat", "IAMDBMef", out.get("parserOutFormat").get(0));

    }


    public void testWebsenseKvpAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, SupportedFormats.WebProxyMef);
        Event avroEvent = new Event();
        String msg = "May 11 11:22:10 172.19.1.11 vendor=Websense product=Security product_version=7.7.0 action=permitted severity=1 category=76 user=WinNT://TRITON77/username src_host=172.19.1.133 src_port=55863 dst_host=www.google.com dst_ip=74.125.71.103 dst_port=80 bytes_out=635 bytes_in=25128 http_response=200 http_method=GET http_content_type=text/html;_charset=UTF-8 http_user_agent=Mozilla/5.0_(X11;_U;_Linux_x86_64;_en-US;_rv:1.9.2.9)_Gecko/20110412_CentOS/3.6.9-2.el6.centos_Firefox/3.6.9 http_proxy_status_code=200 reason=- disposition=1026 policy=role-8**Default role=8 duration=0 url=http://www.google.com/";


        avroEvent.setBody(ByteBuffer.wrap(msg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out);



        assertEquals("bytesIn", 25128, out.get("bytesIn").get(0));
        assertEquals("bytesOut", 635, out.get("bytesOut").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0_(X11;_U;_Linux_x86_64;_en-US;_rv:1.9.2.9)_Gecko/20110412_CentOS/3.6.9-2.el6.centos_Firefox/3.6.9", out.get("requestClientApplication").get(0));
        assertEquals("destinationAddress", "74.125.71.103", out.get("destinationAddress").get(0));
        assertEquals("destinationNameOrIp", "www.google.com", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
        assertEquals("deviceAction", "permitted", out.get("deviceAction").get(0));
        assertEquals("deviceEventCategory", "76", out.get("deviceEventCategory").get(0));
        assertEquals("devicePolicyAction", "role-8**Default", out.get("devicePolicyAction").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("startTime", 1462965730000L, out.get("startTime").get(0));
//      assertEquals("requestHeaderHostField", "www.google.com", res.get("requestHeaderHostField").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestPath", "/", out.get("requestPath").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("responseContentType", "text/html;_charset=UTF-8", out.get("responseContentType").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("sourceNameOrIp", "172.19.1.133", out.get("sourceNameOrIp").get(0));
        assertEquals("sourcePort", 55863, out.get("sourcePort").get(0));
        assertEquals("sourceUserName", "WinNT://TRITON77/username", out.get("sourceUserName").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "websense", out.get("externalLogSourceType").get(0));

    }
    public void testTaniumHetInformationsAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, SupportedFormats.HETMef);
        Event avroEvent = new Event();
        String line = "{\"question\": \"het informations\", \"measure\": \"\\n<result_sets><now>2015/05/22 07:30:59 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>227</saved_question_id><question_id>168411</question_id><report_count>2</report_count><seconds_since_issued>107</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>2</tested><passed>2</passed><mr_tested>2</mr_tested><mr_passed>2</mr_passed><estimated_total>2</estimated_total><select_count>1</select_count><cs><c><wh>4210360868</wh><dn>het information</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>82443432</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;WIN-OSNMCI3GJJ1&quot;\\nIPAddress = {&quot;10.167.170.124&quot;, &quot;fe80::ccea:b9e7:d8f2:4952&quot;}\\nMACAddress = &quot;22:00:0B:47:0B:A4\\n</v></c><c><v>1</v></c></r><r><id>810612956</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;RDP-GW&quot;\\nIPAddress = {&quot;192.168.12.9&quot;, &quot;fe80::cd4b:f92a:5ce8:6216&quot;}\\nMACAddress = &quot;12:4A:FB:CB:8B:93\\n</v></c><c><v>1</v></c></r><r><id>1270274401</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;RDP-GW&quot;\\nIPAddress = {&quot;192.168.12.27&quot;}\\nMACAddress = &quot;12:7F:C8:56:84:17\\n</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 3, output.size());
        for (int i = 0; i < output.size(); i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s", i, out));
            switch (i) {
                case 0:
                    assertEquals("destinationAddress", "10.167.170.124", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "22:00:0B:47:0B:A4", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "WIN-OSNMCI3GJJ1", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432279859000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    break;

                case 1:
                    assertEquals("destinationAddress", "192.168.12.9", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "12:4A:FB:CB:8B:93", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "RDP-GW", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432279859000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    break;
                case 2:
                    assertEquals("destinationAddress", "192.168.12.27", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "12:7F:C8:56:84:17", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "RDP-GW", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432279859000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));

                    break;
                default:
            }
        }
    }

    public void testTaniumHetInformationsWithMissingResultAvroEventParser() throws IOException, Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "{\"question\": \"het informations\", \"measure\": \"\\n<result_sets><now>2015/05/27 15:15:55 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>227</saved_question_id><question_id>183894</question_id><report_count>3</report_count><seconds_since_issued>67</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>4210360868</wh><dn>het information</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>4</filtered_row_count><filtered_row_count_machines>4</filtered_row_count_machines><item_count>4</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v>1</v></c></r><r><id>82443432</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;WIN-OSNMCI3GJJ1&quot;\\nIPAddress = {&quot;10.167.170.124&quot;, &quot;fe80::ccea:b9e7:d8f2:4952&quot;}\\nMACAddress = &quot;22:00:0B:47:0B:A4\\n</v></c><c><v>1</v></c></r><r><id>762878037</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;RDP-GW&quot;\\nIPAddress = {&quot;fe80::cd4b:f92a:5ce8:6216&quot;}\\nMACAddress = &quot;12:4A:FB:CB:8B:93\\n</v></c><c><v>1</v></c></r><r><id>1270274401</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;RDP-GW&quot;\\nIPAddress = {&quot;192.168.12.27&quot;}\\nMACAddress = &quot;12:7F:C8:56:84:17\\n</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 3, output.size());
        for (int i=0; i < output.size();i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("destinationAddress", "10.167.170.124", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "22:00:0B:47:0B:A4", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "WIN-OSNMCI3GJJ1", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432739755000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    break;

                case 1:
//          assertEquals("destinationAddress", "192.168.12.9", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "12:4A:FB:CB:8B:93", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "RDP-GW", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432739755000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    break;
                case 2:
                    assertEquals("destinationAddress", "192.168.12.27", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "12:7F:C8:56:84:17", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "RDP-GW", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432739755000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));

                    break;
                default:
            }
        }

    }

    //
    public void testTaniumCpuConsumptionsWithMissingResultAvroEventParser() throws IOException, Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HostCpuMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "{\"question\": \"HOST-CPU consumptions\", \"measure\": \"\\n<result_sets><now>2015/05/27 15:20:55 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>233</saved_question_id><question_id>183899</question_id><report_count>4</report_count><seconds_since_issued>0</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>98976143</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>98976143</wh><dn>CPU Consumption</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1661264119</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>2 %</v></c><c><v>1</v></c></r><r><id>3260989120</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>1 %</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 2, output.size());
        for (int i=0; i < output.size();i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("cpuConsumption", 2.0f, out.get("cpuConsumption").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432740055000L, out.get("startTime").get(0));
                    break;

                case 1:
                    assertEquals("cpuConsumption", 1.0f, out.get("cpuConsumption").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432740055000L, out.get("startTime").get(0));
                    break;
                default:
            }
        }

    }
    public void testTaniumLoggedInUsersAvroEventParser() throws IOException, Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.UETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "{\"question\": \"Hosts-Logged in user details\", \"measure\": \"\\n<result_sets><now>2015/05/27 15:20:56 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>235</saved_question_id><question_id>183902</question_id><report_count>3</report_count><seconds_since_issued>87</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>3882709820</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>3882709820</wh><dn>Userid</dn><rt>1</rt></c><c><wh>3882709820</wh><dn>Full Name</dn><rt>1</rt></c><c><wh>3882709820</wh><dn>Email Address</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>2544406072</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WIN-OSNMCI3GJJ1\\\\jyria</v></c><c><v>Unknown or Local User</v></c><c><v>Unknown or Local User</v></c><c><v>1</v></c></r><r><id>3242379506</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>RDP-GW\\\\jyria</v></c><c><v>Unknown or Local User</v></c><c><v>Unknown or Local User</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 2, output.size());
        for (int i=0; i < output.size();i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("deviceUserName", "WIN-OSNMCI3GJJ1\\jyria", out.get("deviceUserName").get(0));
                    assertEquals("deviceUserFullName", "Unknown or Local User", out.get("deviceUserFullName").get(0));
                    assertEquals("deviceUserEmailAddress", "Unknown or Local User", out.get("deviceUserEmailAddress").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Hosts-Logged in user details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "UETMef", out.get("logSourceType").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("startTime", 1432740056000L, out.get("startTime").get(0));
                    break;

                case 1:
                    assertEquals("deviceUserName", "RDP-GW\\jyria", out.get("deviceUserName").get(0));
                    assertEquals("deviceUserFullName", "Unknown or Local User", out.get("deviceUserFullName").get(0));
                    assertEquals("deviceUserEmailAddress", "Unknown or Local User", out.get("deviceUserEmailAddress").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Hosts-Logged in user details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "UETMef", out.get("logSourceType").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("startTime", 1432740056000L, out.get("startTime").get(0));
                    break;
                default:
            }
        }



    }
    public void testTaniumProcessDetailsAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, SupportedFormats.HostProcessMef);
        Event avroEvent = new Event();
        String line = "{\"question\": \"Host-Processes details v2\", \"measure\": \"\\n<result_sets><now>2015/06/08 09:01:30 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>252</saved_question_id><question_id>219864</question_id><report_count>6</report_count><seconds_since_issued>0</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>9</tested><passed>9</passed><mr_tested>9</mr_tested><mr_passed>9</mr_passed><estimated_total>9</estimated_total><select_count>1</select_count><cs><c><wh>2225394095</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessName</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>md5</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessPath</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessPorts</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>93</filtered_row_count><filtered_row_count_machines>99</filtered_row_count_machines><item_count>93</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v>7</v></c></r><r><id>4964764</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sublime_text.exe</v></c><c><v>1a62f67f5d9c292ba8a615ea6797966d c:\\\\users\\\\akshat\\\\desktop\\\\sublime text build 3083 x64\\\\sublime_text.exe</v></c><c><v>C:\\\\Users\\\\akshat\\\\Desktop\\\\Sublime Text Build 3083 x64\\\\sublime_text.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>130135404</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>mmc.exe</v></c><c><v>6aaf3bece2c3d17091bcef37c5a82ac0 c:\\\\windows\\\\system32\\\\mmc.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>135538545</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvvsvc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>200259006</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>LiteAgent.exe</v></c><c><v>78d3824650a866f3c38ae0079fc7e3dd c:\\\\program files\\\\amazon\\\\xentools\\\\liteagent.exe</v></c><c><v>C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>214928457</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>LogonUI.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>240362891</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>rdpclip.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\rdpclip.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>328579443</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>cmd.exe</v></c><c><v>ad7b9c14083b52bc532fba5948342b98 c:\\\\windows\\\\system32\\\\cmd.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\cmd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>362478771</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>fdhost.exe</v></c><c><v>1b100b5fc879b899f9ef85392c90a79c c:\\\\program files\\\\microsoft sql server\\\\mssql12.mssqlserver\\\\mssql\\\\binn\\\\fdhost.exe</v></c><c><v>C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>430230444</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>fdlauncher.exe</v></c><c><v>c5e1fe7db2202d37ba9a634e7f230a44 c:\\\\program files\\\\microsoft sql server\\\\mssql12.mssqlserver\\\\mssql\\\\binn\\\\fdlauncher.exe</v></c><c><v>C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>460833048</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>firefox.exe</v></c><c><v>345b45be09381d2011eb7f9ac11d8ac4 c:\\\\program files (x86)\\\\mozilla firefox\\\\firefox.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe</v></c><c><v>11112 {TCP/127.0.0.1:63700,TCP/127.0.0.1:63701}</v></c><c><v>1</v></c></r><r><id>488066177</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>System</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>489839558</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>explorer.exe</v></c><c><v>332feab1435662fc6c672e25beb37be3 c:\\\\windows\\\\explorer.exe</v></c><c><v>C:\\\\Windows\\\\Explorer.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>525975750</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\\\program files (x86)\\\\tanium\\\\tanium client\\\\taniumclient.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe</v></c><c><v>32 {TCP/10.167.170.124:65185,TCP/127.0.0.1:65150}</v></c><c><v>1</v></c></r><r><id>554946170</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>wscript.exe</v></c><c><v>979d74799ea6c8b8167869a68df5204a c:\\\\windows\\\\system32\\\\wscript.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>587507959</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>winlogon.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>704071111</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>cmd.exe</v></c><c><v>622d21c40a25f9834a03bfd5ff4710c1 c:\\\\windows\\\\system32\\\\cmd.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\cmd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>742390788</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>wscript.exe</v></c><c><v>d536ccce2a7992688db76941506ea970 c:\\\\windows\\\\system32\\\\wscript.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>752500312</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskmgr.exe</v></c><c><v>545bf7eaa24a9e062857d0742ec0b28a c:\\\\windows\\\\system32\\\\taskmgr.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\taskmgr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>806209098</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>powershell.exe</v></c><c><v>ef8fa4f195c6239273c100ab370fcfdc c:\\\\windows\\\\system32\\\\windowspowershell\\\\v1.0\\\\powershell.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\WindowsPowerShell\\\\v1.0\\\\powershell.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>868409369</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>spoolsv.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\spoolsv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>868772106</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>smss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>930388804</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>chrome.exe</v></c><c><v>c4ef32c1c0473392ef4204890af8e457 c:\\\\program files (x86)\\\\google\\\\chrome\\\\application\\\\chrome.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Google\\\\Chrome\\\\Application\\\\chrome.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>950785313</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>XenDpriv.exe</v></c><c><v>637c075ca92a0e272029f53b345d1bfd c:\\\\program files (x86)\\\\citrix\\\\xentools\\\\xendpriv.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>969791269</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>conhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\conhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1027493949</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>wininit.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\wininit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1045242840</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>regedit.exe</v></c><c><v>2f3fed31ac2846d8ad5dbc396a7e3df1 c:\\\\windows\\\\regedit.exe</v></c><c><v>C:\\\\Windows\\\\regedit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1052137475</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>notepad.exe</v></c><c><v>8bab7a5d5c1477d0641195e623db32b4 c:\\\\windows\\\\system32\\\\notepad.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1056487211</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>msdtc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\msdtc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1178014734</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1192001015</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>System</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1197626474</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sqlwriter.exe</v></c><c><v>8fd8ee71d7d639f85805eee4adb2aa15 c:\\\\program files\\\\microsoft sql server\\\\90\\\\shared\\\\sqlwriter.exe</v></c><c><v>C:\\\\Program Files\\\\Microsoft SQL Server\\\\90\\\\Shared\\\\sqlwriter.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1232513977</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\\\program files (x86)\\\\tanium\\\\tanium client\\\\taniumclient.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe</v></c><c><v>4044 {TCP/192.168.12.27:53180,TCP/192.168.12.27:53206,TCP/192.168.12.27:53209}</v></c><c><v>1</v></c></r><r><id>1277035939</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>spoolsv.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\spoolsv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1291572388</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>System Idle Process</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v>0 {TCP/10.167.170.124:9443,TCP/10.167.170.124:17472,TCP/10.167.170.124:65046,TCP/10.167.170.124:65074,TCP/10.167.170.124:65116,TCP/10.167.170.124:65121,TCP/10.167.170.124:65151,TCP/127.0.0.1:9444,TCP/127.0.0.1:17472,TCP/127.0.0.1:65112,TCP/127.0.0.1:65123,TCP/127.0.0.1:65154,TCP/127.0.0.1:65158}</v></c><c><v>1</v></c></r><r><id>1298898850</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>php-cgi.exe</v></c><c><v>0d410f3ebe76bf7c0b45c47e67ca8de1 c:\\\\progra~1\\\\tanium\\\\tanium~1\\\\php55\\\\php-cgi.exe</v></c><c><v>C:\\\\PROGRA~1\\\\Tanium\\\\TANIUM~1\\\\php55\\\\php-cgi.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1333706542</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>conhost.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\\\Windows\\\\System32\\\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1467538242</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>System Idle Process</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v>0 {TCP/192.168.12.27:53078,TCP/192.168.12.27:53079,TCP/192.168.12.27:53111,TCP/192.168.12.27:53114,TCP/192.168.12.27:53115,TCP/192.168.12.27:53144,TCP/192.168.12.27:53148,TCP/192.168.12.27:53149,TCP/192.168.12.27:53183,TCP/192.168.12.27:53184}</v></c><c><v>1</v></c></r><r><id>1467589887</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>services.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1592719234</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>svchost.exe</v></c><c><v>d0abc231c0b3e88c6b612b28abbf734d c:\\\\windows\\\\system32\\\\svchost.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\svchost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1674266488</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>smss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1709652520</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>Ec2Config.exe</v></c><c><v>b3fde4699997759320cba603e2aed2cd c:\\\\program files\\\\amazon\\\\ec2configservice\\\\ec2config.exe</v></c><c><v>C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe</v></c><c><v>1472 {TCP/192.168.12.27:53182}</v></c><c><v>1</v></c></r><r><id>1767938001</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WmiPrvSE.exe</v></c><c><v>330c8cbd4343d04e72834b159d260e78 c:\\\\windows\\\\syswow64\\\\wbem\\\\wmiprvse.exe</v></c><c><v>C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1851379401</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>cb.exe</v></c><c><v>d08f5c01b23aa7f356693c028ab592ba c:\\\\windows\\\\carbonblack\\\\cb.exe</v></c><c><v>C:\\\\Windows\\\\CarbonBlack\\\\cb.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2033331541</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>devenv.exe</v></c><c><v>b2a62caf90a30d233134c9ef7d2c73a7 c:\\\\program files (x86)\\\\microsoft visual studio 10.0\\\\common7\\\\ide\\\\devenv.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Microsoft Visual Studio 10.0\\\\Common7\\\\IDE\\\\devenv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2070809886</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>wininit.exe</v></c><c><v>b5c5dcad3899512020d135600129d665 c:\\\\windows\\\\system32\\\\wininit.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\wininit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2121839234</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\\\program files (x86)\\\\tanium\\\\tanium client\\\\taniumclient.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2179234476</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>Taskmgr.exe</v></c><c><v>9919d598108e8e449d98aba2c43d2f20 c:\\\\windows\\\\system32\\\\taskmgr.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\taskmgr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2203176992</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sh.exe</v></c><c><v>e890d295ab412762b7c3def0fdd9c16a c:\\\\cygwin64\\\\bin\\\\sh.exe</v></c><c><v>C:\\\\cygwin64\\\\bin\\\\sh.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2220592385</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>rdpclip.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\rdpclip.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2335124868</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>mmc.exe</v></c><c><v>b316385fd7c1e1cbad339c33cf3c0409 c:\\\\windows\\\\system32\\\\mmc.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2351261712</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\\\program files (x86)\\\\tanium\\\\tanium client\\\\taniumclient.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2384135557</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>lsass.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\lsass.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2393445266</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>httpd.exe</v></c><c><v>437a04590133ea73fb2078bf114ac542 c:\\\\program files\\\\tanium\\\\tanium server\\\\apache24\\\\bin\\\\httpd.exe</v></c><c><v>C:\\\\Program Files\\\\Tanium\\\\Tanium Server\\\\Apache24\\\\bin\\\\httpd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2407801199</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>taskhostex.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\taskhostex.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2439515127</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>lsm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\lsm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2477658529</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>dwm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\dwm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2615369603</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>SQLAGENT.EXE</v></c><c><v>d72a162f3e75a046def27edaa92cdb67 c:\\\\program files\\\\microsoft sql server\\\\mssql12.mssqlserver\\\\mssql\\\\binn\\\\sqlagent.exe</v></c><c><v>C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\SQLAGENT.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2644508573</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\\\Windows\\\\system32\\\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2667342661</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>rdpinput.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\rdpinput.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2764744017</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>winlogon.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2777407551</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>WmiPrvSE.exe</v></c><c><v>1951c6f1e53079f6b29ecff77eaf9403 c:\\\\windows\\\\syswow64\\\\wbem\\\\wmiprvse.exe</v></c><c><v>C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2818851634</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>2860491008</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>NETSTAT.EXE</v></c><c><v>32297bb17e6ec700d0fc869f9acaf561 c:\\\\windows\\\\system32\\\\netstat.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\netstat.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2866659743</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>Ec2Config.exe</v></c><c><v>f02c17c6679c389cf49fe840f8df8089 c:\\\\program files\\\\amazon\\\\ec2configservice\\\\ec2config.exe</v></c><c><v>C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe</v></c><c><v>2076 {TCP/10.167.170.124:65156}</v></c><c><v>1</v></c></r><r><id>2869899582</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>svchost.exe</v></c><c><v>54a47f6b5e09a77e61649109c6a08866 c:\\\\windows\\\\system32\\\\svchost.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\svchost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2957826223</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvwmi64.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3034446139</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\taskhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3084297043</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>mintty.exe</v></c><c><v>0c4ce674d7a2139d161c349cf468109c c:\\\\cygwin64\\\\bin\\\\mintty.exe</v></c><c><v>C:\\\\cygwin64\\\\bin\\\\mintty.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3092459201</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>msdtc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\System32\\\\msdtc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3108142843</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>WmiPrvSE.exe</v></c><c><v>1951c6f1e53079f6b29ecff77eaf9403 c:\\\\windows\\\\system32\\\\wbem\\\\wmiprvse.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3187118403</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>lsass.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\lsass.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3196297430</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sqlservr.exe</v></c><c><v>5957dad9cec0dd7da81a3fcefe029dce c:\\\\program files\\\\microsoft sql server\\\\mssql12.mssqlserver\\\\mssql\\\\binn\\\\sqlservr.exe</v></c><c><v>C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\sqlservr.exe</v></c><c><v>1352 {TCP/10.167.170.124:1433}</v></c><c><v>1</v></c></r><r><id>3273756515</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>csrss.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\csrss.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3306678053</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WmiPrvSE.exe</v></c><c><v>330c8cbd4343d04e72834b159d260e78 c:\\\\windows\\\\system32\\\\wbem\\\\wmiprvse.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3352665611</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>conhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\conhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3374180811</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\\\Windows\\\\System32\\\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3457634775</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>dwm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\Dwm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3542642798</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumReceiver.exe</v></c><c><v>2ba63ff4db05cbbd8bc93295fc2838f9 c:\\\\program files\\\\tanium\\\\tanium server\\\\taniumreceiver.exe</v></c><c><v>C:\\\\Program Files\\\\Tanium\\\\Tanium Server\\\\TaniumReceiver.exe</v></c><c><v>8064 {TCP/10.167.170.124:17472,TCP/10.167.170.124:49363,TCP/10.167.170.124:49364,TCP/10.167.170.124:49366,TCP/10.167.170.124:49367,TCP/10.167.170.124:49369,TCP/10.167.170.124:49370,TCP/10.167.170.124:49371,TCP/10.167.170.124:49373,TCP/10.167.170.124:49374,TCP/10.167.170.124:49379,TCP/10.167.170.124:49380,TCP/10.167.170.124:49382,TCP/10.167.170.124:49383,TCP/10.167.170.124:49384,TCP/10.167.170.124:49385,TCP/10.167.170.124:49386,TCP/10.167.170.124:49387,TCP/10.167.170.124:49388,TCP/10.167.170.124:49389,TCP/10.167.170.124:49390,TCP/10.167.170.124:49392,TCP/10.167.170.124:49393,TCP/10.167.170.124:49394,TCP/10.167.170.124:49395,TCP/10.167.170.124:49396,TCP/10.167.170.124:49397,TCP/10.167.170.124:49430,TCP/10.167.170.124:49448,TCP/10.167.170.124:53006,TCP/10.167.170.124:59480,TCP/127.0.0.1:17472}</v></c><c><v>1</v></c></r><r><id>3593426093</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>ServerManager.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\ServerManager.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3652869174</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>explorer.exe</v></c><c><v>85d47eb257b06094f052e0c8aefa3bee c:\\\\windows\\\\explorer.exe</v></c><c><v>C:\\\\Windows\\\\Explorer.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3666264833</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>OpenWith.exe</v></c><c><v>1dfe1ed0a9ef0fa4ffe8d08dfb00f121 c:\\\\windows\\\\system32\\\\openwith.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\OpenWith.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3806172439</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>LogonUI.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3869451342</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>firefox.exe</v></c><c><v>14cf73d771fa977a9f1cbaa5c301f912 c:\\\\program files (x86)\\\\mozilla firefox\\\\firefox.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe</v></c><c><v>3364 {TCP/127.0.0.1:56690,TCP/127.0.0.1:56691,TCP/192.168.12.27:57580,TCP/192.168.12.27:57582,TCP/192.168.12.27:57583}</v></c><c><v>1</v></c></r><r><id>3874003054</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskeng.exe</v></c><c><v>4f2659160afcca990305816946f69407 c:\\\\windows\\\\system32\\\\taskeng.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\taskeng.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3902538584</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>plugin_host.exe</v></c><c><v>85cfc4258e6538bc3f425edfe200a5a7 c:\\\\users\\\\akshat\\\\desktop\\\\sublime text build 3083 x64\\\\plugin_host.exe</v></c><c><v>C:\\\\Users\\\\akshat\\\\Desktop\\\\Sublime Text Build 3083 x64\\\\plugin_host.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3920685033</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>services.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\services.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3960152428</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>dllhost.exe</v></c><c><v>cc05c14eeff5e7813a49718ba88e59b0 c:\\\\windows\\\\system32\\\\dllhost.exe</v></c><c><v>C:\\\\Windows\\\\system32\\\\DllHost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4074252443</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>VSSVC.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\\\Windows\\\\SysWow64\\\\vssvc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4198614073</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>XenGuestAgent.exe</v></c><c><v>7eae59832be1c3a08d4f94cebccaf13a c:\\\\program files (x86)\\\\citrix\\\\xentools\\\\xenguestagent.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4252031705</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>notepad.exe</v></c><c><v>d378bffb70923139d6a4f546864aa61c c:\\\\windows\\\\system32\\\\notepad.exe</v></c><c><v>C:\\\\Windows\\\\System32\\\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4254444053</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvSCPAPISvr.exe</v></c><c><v>701bb23989e138c1c8b436c5940a1f6e c:\\\\program files (x86)\\\\nvidia corporation\\\\3d vision\\\\nvscpapisvr.exe</v></c><c><v>C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4263354092</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvxdsync.exe</v></c><c><v>de701bee6a35ecd48c9a582bbff943c0 c:\\\\program files\\\\nvidia corporation\\\\display\\\\nvxdsync.exe</v></c><c><v>C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe</v></c><c><v></v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 115, output.size());
        for (int i = 0; i < output.size(); i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s", i, out));
            switch (i){
                case 0:
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "1a62f67f5d9c292ba8a615ea6797966d", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Users\\akshat\\Desktop\\Sublime Text Build 3083 x64\\sublime_text.exe", out.get("processFilePath").get(0));
                    assertEquals("processName", "sublime_text.exe", out.get("processName").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));

                    break;

                case 1:
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "6aaf3bece2c3d17091bcef37c5a82ac0", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Windows\\system32\\mmc.exe", out.get("processFilePath").get(0));
                    assertEquals("processName", "mmc.exe", out.get("processName").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));

                    break;
                case 82:
                    assertEquals("logCollectionCategory", "tanium", out.get("logCollectionCategory").get(0));
                    assertEquals("processFilePath", "C:\\Windows\\SysWow64\\winlogon.exe", out.get("processFilePath").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionTime", "1384693669604", out.get("logCollectionTime").get(0));
                    assertEquals("recordId", "61", out.get("recordId").get(0));
                    assertEquals("taniumQuestion", "Host-Processes details v2", out.get("taniumQuestion").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("processName", "winlogon.exe", out.get("processName").get(0));
                    assertEquals("_validationLog", "[unexpected value format for field 'processFileMd5' : [[Unable]]", out.get("_validationLog").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "UnMatched-validation", out.get("logSourceType").get(0));
                    assertEquals("taniumTime", "2015/06/08 09:01:30 GMT-0000", out.get("taniumTime").get(0));
                    assertEquals("taniumPorts", "", out.get("taniumPorts").get(0));
                    assertEquals("md5", "[Unable to process MD5]", out.get("md5").get(0));
                    break;
                default:
            }
        }
    }
    public void testFlumeTaniumProcessDetailsAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, SupportedFormats.HostProcessMef);
        Event avroEvent = new Event();
        String line = "<result_set><now>2015/06/08 09:01:30 GMT-0000</now><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>252</saved_question_id><question_id>219864</question_id><report_count>6</report_count><seconds_since_issued>0</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>9</tested><passed>9</passed><mr_tested>9</mr_tested><mr_passed>9</mr_passed><estimated_total>9</estimated_total><select_count>1</select_count><cs><c><wh>2225394095</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessName</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>md5</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessPath</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessPorts</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>93</filtered_row_count><filtered_row_count_machines>99</filtered_row_count_machines><item_count>93</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v>7</v></c></r><r><id>4964764</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sublime_text.exe</v></c><c><v>1a62f67f5d9c292ba8a615ea6797966d c:\\users\\akshat\\desktop\\sublime text build 3083 x64\\sublime_text.exe</v></c><c><v>C:\\Users\\akshat\\Desktop\\Sublime Text Build 3083 x64\\sublime_text.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>130135404</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>mmc.exe</v></c><c><v>6aaf3bece2c3d17091bcef37c5a82ac0 c:\\windows\\system32\\mmc.exe</v></c><c><v>C:\\Windows\\system32\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>135538545</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvvsvc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\nvvsvc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>200259006</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>LiteAgent.exe</v></c><c><v>78d3824650a866f3c38ae0079fc7e3dd c:\\program files\\amazon\\xentools\\liteagent.exe</v></c><c><v>C:\\Program Files\\Amazon\\XenTools\\LiteAgent.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>214928457</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>LogonUI.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\LogonUI.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>240362891</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>rdpclip.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\rdpclip.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>328579443</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>cmd.exe</v></c><c><v>ad7b9c14083b52bc532fba5948342b98 c:\\windows\\system32\\cmd.exe</v></c><c><v>C:\\Windows\\System32\\cmd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>362478771</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>fdhost.exe</v></c><c><v>1b100b5fc879b899f9ef85392c90a79c c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\fdhost.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\fdhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>430230444</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>fdlauncher.exe</v></c><c><v>c5e1fe7db2202d37ba9a634e7f230a44 c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\fdlauncher.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\fdlauncher.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>460833048</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>firefox.exe</v></c><c><v>345b45be09381d2011eb7f9ac11d8ac4 c:\\program files (x86)\\mozilla firefox\\firefox.exe</v></c><c><v>C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe</v></c><c><v>11112 {TCP/127.0.0.1:63700,TCP/127.0.0.1:63701}</v></c><c><v>1</v></c></r><r><id>488066177</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>System</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>489839558</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>explorer.exe</v></c><c><v>332feab1435662fc6c672e25beb37be3 c:\\windows\\explorer.exe</v></c><c><v>C:\\Windows\\Explorer.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>525975750</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v>32 {TCP/10.167.170.124:65185,TCP/127.0.0.1:65150}</v></c><c><v>1</v></c></r><r><id>554946170</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>wscript.exe</v></c><c><v>979d74799ea6c8b8167869a68df5204a c:\\windows\\system32\\wscript.exe</v></c><c><v>C:\\Windows\\System32\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>587507959</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>winlogon.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>704071111</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>cmd.exe</v></c><c><v>622d21c40a25f9834a03bfd5ff4710c1 c:\\windows\\system32\\cmd.exe</v></c><c><v>C:\\Windows\\system32\\cmd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>742390788</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>wscript.exe</v></c><c><v>d536ccce2a7992688db76941506ea970 c:\\windows\\system32\\wscript.exe</v></c><c><v>C:\\Windows\\System32\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>752500312</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskmgr.exe</v></c><c><v>545bf7eaa24a9e062857d0742ec0b28a c:\\windows\\system32\\taskmgr.exe</v></c><c><v>C:\\Windows\\system32\\taskmgr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>806209098</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>powershell.exe</v></c><c><v>ef8fa4f195c6239273c100ab370fcfdc c:\\windows\\system32\\windowspowershell\\v1.0\\powershell.exe</v></c><c><v>C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>868409369</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>spoolsv.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\spoolsv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>868772106</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>smss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>930388804</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>chrome.exe</v></c><c><v>c4ef32c1c0473392ef4204890af8e457 c:\\program files (x86)\\google\\chrome\\application\\chrome.exe</v></c><c><v>C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>950785313</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>XenDpriv.exe</v></c><c><v>637c075ca92a0e272029f53b345d1bfd c:\\program files (x86)\\citrix\\xentools\\xendpriv.exe</v></c><c><v>C:\\Program Files (x86)\\Citrix\\XenTools\\XenDpriv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>969791269</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>conhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\conhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1027493949</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>wininit.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\wininit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1045242840</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>regedit.exe</v></c><c><v>2f3fed31ac2846d8ad5dbc396a7e3df1 c:\\windows\\regedit.exe</v></c><c><v>C:\\Windows\\regedit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1052137475</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>notepad.exe</v></c><c><v>8bab7a5d5c1477d0641195e623db32b4 c:\\windows\\system32\\notepad.exe</v></c><c><v>C:\\Windows\\System32\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1056487211</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>msdtc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\msdtc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1178014734</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\sysWOW64\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1192001015</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>System</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1197626474</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sqlwriter.exe</v></c><c><v>8fd8ee71d7d639f85805eee4adb2aa15 c:\\program files\\microsoft sql server\\90\\shared\\sqlwriter.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\90\\Shared\\sqlwriter.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1232513977</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v>4044 {TCP/192.168.12.27:53180,TCP/192.168.12.27:53206,TCP/192.168.12.27:53209}</v></c><c><v>1</v></c></r><r><id>1277035939</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>spoolsv.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\spoolsv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1291572388</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>System Idle Process</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v>0 {TCP/10.167.170.124:9443,TCP/10.167.170.124:17472,TCP/10.167.170.124:65046,TCP/10.167.170.124:65074,TCP/10.167.170.124:65116,TCP/10.167.170.124:65121,TCP/10.167.170.124:65151,TCP/127.0.0.1:9444,TCP/127.0.0.1:17472,TCP/127.0.0.1:65112,TCP/127.0.0.1:65123,TCP/127.0.0.1:65154,TCP/127.0.0.1:65158}</v></c><c><v>1</v></c></r><r><id>1298898850</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>php-cgi.exe</v></c><c><v>0d410f3ebe76bf7c0b45c47e67ca8de1 c:\\progra~1\\tanium\\tanium~1\\php55\\php-cgi.exe</v></c><c><v>C:\\PROGRA~1\\Tanium\\TANIUM~1\\php55\\php-cgi.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1333706542</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>conhost.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\System32\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1467538242</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>System Idle Process</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v>0 {TCP/192.168.12.27:53078,TCP/192.168.12.27:53079,TCP/192.168.12.27:53111,TCP/192.168.12.27:53114,TCP/192.168.12.27:53115,TCP/192.168.12.27:53144,TCP/192.168.12.27:53148,TCP/192.168.12.27:53149,TCP/192.168.12.27:53183,TCP/192.168.12.27:53184}</v></c><c><v>1</v></c></r><r><id>1467589887</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>services.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\SysWow64\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1592719234</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>svchost.exe</v></c><c><v>d0abc231c0b3e88c6b612b28abbf734d c:\\windows\\system32\\svchost.exe</v></c><c><v>C:\\Windows\\System32\\svchost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1674266488</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>smss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1709652520</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>Ec2Config.exe</v></c><c><v>b3fde4699997759320cba603e2aed2cd c:\\program files\\amazon\\ec2configservice\\ec2config.exe</v></c><c><v>C:\\Program Files\\Amazon\\Ec2ConfigService\\Ec2Config.exe</v></c><c><v>1472 {TCP/192.168.12.27:53182}</v></c><c><v>1</v></c></r><r><id>1767938001</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WmiPrvSE.exe</v></c><c><v>330c8cbd4343d04e72834b159d260e78 c:\\windows\\syswow64\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\sysWOW64\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1851379401</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>cb.exe</v></c><c><v>d08f5c01b23aa7f356693c028ab592ba c:\\windows\\carbonblack\\cb.exe</v></c><c><v>C:\\Windows\\CarbonBlack\\cb.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2033331541</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>devenv.exe</v></c><c><v>b2a62caf90a30d233134c9ef7d2c73a7 c:\\program files (x86)\\microsoft visual studio 10.0\\common7\\ide\\devenv.exe</v></c><c><v>C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\Common7\\IDE\\devenv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2070809886</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>wininit.exe</v></c><c><v>b5c5dcad3899512020d135600129d665 c:\\windows\\system32\\wininit.exe</v></c><c><v>C:\\Windows\\system32\\wininit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2121839234</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2179234476</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>Taskmgr.exe</v></c><c><v>9919d598108e8e449d98aba2c43d2f20 c:\\windows\\system32\\taskmgr.exe</v></c><c><v>C:\\Windows\\system32\\taskmgr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2203176992</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sh.exe</v></c><c><v>e890d295ab412762b7c3def0fdd9c16a c:\\cygwin64\\bin\\sh.exe</v></c><c><v>C:\\cygwin64\\bin\\sh.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2220592385</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>rdpclip.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\rdpclip.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2335124868</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>mmc.exe</v></c><c><v>b316385fd7c1e1cbad339c33cf3c0409 c:\\windows\\system32\\mmc.exe</v></c><c><v>C:\\Windows\\system32\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2351261712</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2384135557</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>lsass.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\lsass.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2393445266</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>httpd.exe</v></c><c><v>437a04590133ea73fb2078bf114ac542 c:\\program files\\tanium\\tanium server\\apache24\\bin\\httpd.exe</v></c><c><v>C:\\Program Files\\Tanium\\Tanium Server\\Apache24\\bin\\httpd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2407801199</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>taskhostex.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\taskhostex.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2439515127</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>lsm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\lsm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2477658529</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>dwm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\dwm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2615369603</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>SQLAGENT.EXE</v></c><c><v>d72a162f3e75a046def27edaa92cdb67 c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\sqlagent.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\SQLAGENT.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2644508573</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\system32\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2667342661</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>rdpinput.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\rdpinput.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2764744017</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>winlogon.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2777407551</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>WmiPrvSE.exe</v></c><c><v>1951c6f1e53079f6b29ecff77eaf9403 c:\\windows\\syswow64\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\sysWOW64\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2818851634</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>2860491008</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>NETSTAT.EXE</v></c><c><v>32297bb17e6ec700d0fc869f9acaf561 c:\\windows\\system32\\netstat.exe</v></c><c><v>C:\\Windows\\System32\\netstat.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2866659743</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>Ec2Config.exe</v></c><c><v>f02c17c6679c389cf49fe840f8df8089 c:\\program files\\amazon\\ec2configservice\\ec2config.exe</v></c><c><v>C:\\Program Files\\Amazon\\Ec2ConfigService\\Ec2Config.exe</v></c><c><v>2076 {TCP/10.167.170.124:65156}</v></c><c><v>1</v></c></r><r><id>2869899582</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>svchost.exe</v></c><c><v>54a47f6b5e09a77e61649109c6a08866 c:\\windows\\system32\\svchost.exe</v></c><c><v>C:\\Windows\\System32\\svchost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2957826223</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvwmi64.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\nvwmi64.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3034446139</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\taskhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3084297043</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>mintty.exe</v></c><c><v>0c4ce674d7a2139d161c349cf468109c c:\\cygwin64\\bin\\mintty.exe</v></c><c><v>C:\\cygwin64\\bin\\mintty.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3092459201</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>msdtc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\msdtc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3108142843</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>WmiPrvSE.exe</v></c><c><v>1951c6f1e53079f6b29ecff77eaf9403 c:\\windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3187118403</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>lsass.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\lsass.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3196297430</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sqlservr.exe</v></c><c><v>5957dad9cec0dd7da81a3fcefe029dce c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\sqlservr.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\sqlservr.exe</v></c><c><v>1352 {TCP/10.167.170.124:1433}</v></c><c><v>1</v></c></r><r><id>3273756515</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>csrss.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\csrss.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3306678053</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WmiPrvSE.exe</v></c><c><v>330c8cbd4343d04e72834b159d260e78 c:\\windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3352665611</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>conhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\conhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3374180811</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\System32\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3457634775</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>dwm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\Dwm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3542642798</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumReceiver.exe</v></c><c><v>2ba63ff4db05cbbd8bc93295fc2838f9 c:\\program files\\tanium\\tanium server\\taniumreceiver.exe</v></c><c><v>C:\\Program Files\\Tanium\\Tanium Server\\TaniumReceiver.exe</v></c><c><v>8064 {TCP/10.167.170.124:17472,TCP/10.167.170.124:49363,TCP/10.167.170.124:49364,TCP/10.167.170.124:49366,TCP/10.167.170.124:49367,TCP/10.167.170.124:49369,TCP/10.167.170.124:49370,TCP/10.167.170.124:49371,TCP/10.167.170.124:49373,TCP/10.167.170.124:49374,TCP/10.167.170.124:49379,TCP/10.167.170.124:49380,TCP/10.167.170.124:49382,TCP/10.167.170.124:49383,TCP/10.167.170.124:49384,TCP/10.167.170.124:49385,TCP/10.167.170.124:49386,TCP/10.167.170.124:49387,TCP/10.167.170.124:49388,TCP/10.167.170.124:49389,TCP/10.167.170.124:49390,TCP/10.167.170.124:49392,TCP/10.167.170.124:49393,TCP/10.167.170.124:49394,TCP/10.167.170.124:49395,TCP/10.167.170.124:49396,TCP/10.167.170.124:49397,TCP/10.167.170.124:49430,TCP/10.167.170.124:49448,TCP/10.167.170.124:53006,TCP/10.167.170.124:59480,TCP/127.0.0.1:17472}</v></c><c><v>1</v></c></r><r><id>3593426093</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>ServerManager.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\ServerManager.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3652869174</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>explorer.exe</v></c><c><v>85d47eb257b06094f052e0c8aefa3bee c:\\windows\\explorer.exe</v></c><c><v>C:\\Windows\\Explorer.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3666264833</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>OpenWith.exe</v></c><c><v>1dfe1ed0a9ef0fa4ffe8d08dfb00f121 c:\\windows\\system32\\openwith.exe</v></c><c><v>C:\\Windows\\system32\\OpenWith.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3806172439</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>LogonUI.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\LogonUI.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3869451342</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>firefox.exe</v></c><c><v>14cf73d771fa977a9f1cbaa5c301f912 c:\\program files (x86)\\mozilla firefox\\firefox.exe</v></c><c><v>C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe</v></c><c><v>3364 {TCP/127.0.0.1:56690,TCP/127.0.0.1:56691,TCP/192.168.12.27:57580,TCP/192.168.12.27:57582,TCP/192.168.12.27:57583}</v></c><c><v>1</v></c></r><r><id>3874003054</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskeng.exe</v></c><c><v>4f2659160afcca990305816946f69407 c:\\windows\\system32\\taskeng.exe</v></c><c><v>C:\\Windows\\system32\\taskeng.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3902538584</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>plugin_host.exe</v></c><c><v>85cfc4258e6538bc3f425edfe200a5a7 c:\\users\\akshat\\desktop\\sublime text build 3083 x64\\plugin_host.exe</v></c><c><v>C:\\Users\\akshat\\Desktop\\Sublime Text Build 3083 x64\\plugin_host.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3920685033</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>services.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\services.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3960152428</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>dllhost.exe</v></c><c><v>cc05c14eeff5e7813a49718ba88e59b0 c:\\windows\\system32\\dllhost.exe</v></c><c><v>C:\\Windows\\system32\\DllHost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4074252443</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>VSSVC.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\vssvc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4198614073</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>XenGuestAgent.exe</v></c><c><v>7eae59832be1c3a08d4f94cebccaf13a c:\\program files (x86)\\citrix\\xentools\\xenguestagent.exe</v></c><c><v>C:\\Program Files (x86)\\Citrix\\XenTools\\XenGuestAgent.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4252031705</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>notepad.exe</v></c><c><v>d378bffb70923139d6a4f546864aa61c c:\\windows\\system32\\notepad.exe</v></c><c><v>C:\\Windows\\System32\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4254444053</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvSCPAPISvr.exe</v></c><c><v>701bb23989e138c1c8b436c5940a1f6e c:\\program files (x86)\\nvidia corporation\\3d vision\\nvscpapisvr.exe</v></c><c><v>C:\\Program Files (x86)\\NVIDIA Corporation\\3D Vision\\nvSCPAPISvr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4263354092</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvxdsync.exe</v></c><c><v>de701bee6a35ecd48c9a582bbff943c0 c:\\program files\\nvidia corporation\\display\\nvxdsync.exe</v></c><c><v>C:\\Program Files\\NVIDIA Corporation\\Display\\nvxdsync.exe</v></c><c><v></v></c><c><v>1</v></c></r></rs></result_set>";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "flume_tanium");
        headers.put("hostname", "somehost");
        headers.put("saved_question_name", "Host-Processes details v2");
        headers.put("result_set_timestamp", "2015/06/08 09:01:30 GMT-0000");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 115, output.size());
        for (int i = 0; i < output.size(); i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s", i, out));
            switch (i){
                case 0:
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "1a62f67f5d9c292ba8a615ea6797966d", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Users\\akshat\\Desktop\\Sublime Text Build 3083 x64\\sublime_text.exe", out.get("processFilePath").get(0));
                    assertEquals("processName", "sublime_text.exe", out.get("processName").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));

                    break;

                case 1:
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "6aaf3bece2c3d17091bcef37c5a82ac0", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Windows\\system32\\mmc.exe", out.get("processFilePath").get(0));
                    assertEquals("processName", "mmc.exe", out.get("processName").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));

                    break;
                case 82:
                    assertEquals("logCollectionCategory", "flume_tanium", out.get("logCollectionCategory").get(0));
                    assertEquals("processFilePath", "C:\\Windows\\SysWow64\\winlogon.exe", out.get("processFilePath").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionTime", "1384693669604", out.get("logCollectionTime").get(0));
                    assertEquals("taniumQuestion", "Host-Processes details v2", out.get("taniumQuestion").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("processName", "winlogon.exe", out.get("processName").get(0));
                    assertEquals("_validationLog", "[unexpected value format for field 'processFileMd5' : [[Unable]]", out.get("_validationLog").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "UnMatched-validation", out.get("logSourceType").get(0));
                    assertEquals("taniumTime", "2015/06/08 09:01:30 GMT-0000", out.get("taniumTime").get(0));
                    assertEquals("md5", "[Unable to process MD5]", out.get("md5").get(0));
                    break;
                default:
            }
        }
    }

    public void testFlumeTaniumProcessDetailsToHostPortMefAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, SupportedFormats.HostPortMef);
        Event avroEvent = new Event();
        String line = "<result_set><now>2015/06/08 09:01:30 GMT-0000</now><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>252</saved_question_id><question_id>219864</question_id><report_count>6</report_count><seconds_since_issued>0</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>9</tested><passed>9</passed><mr_tested>9</mr_tested><mr_passed>9</mr_passed><estimated_total>9</estimated_total><select_count>1</select_count><cs><c><wh>2225394095</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessName</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>md5</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessPath</dn><rt>1</rt></c><c><wh>2225394095</wh><dn>ProcessPorts</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>93</filtered_row_count><filtered_row_count_machines>99</filtered_row_count_machines><item_count>93</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v>7</v></c></r><r><id>4964764</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sublime_text.exe</v></c><c><v>1a62f67f5d9c292ba8a615ea6797966d c:\\users\\akshat\\desktop\\sublime text build 3083 x64\\sublime_text.exe</v></c><c><v>C:\\Users\\akshat\\Desktop\\Sublime Text Build 3083 x64\\sublime_text.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>130135404</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>mmc.exe</v></c><c><v>6aaf3bece2c3d17091bcef37c5a82ac0 c:\\windows\\system32\\mmc.exe</v></c><c><v>C:\\Windows\\system32\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>135538545</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvvsvc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\nvvsvc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>200259006</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>LiteAgent.exe</v></c><c><v>78d3824650a866f3c38ae0079fc7e3dd c:\\program files\\amazon\\xentools\\liteagent.exe</v></c><c><v>C:\\Program Files\\Amazon\\XenTools\\LiteAgent.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>214928457</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>LogonUI.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\LogonUI.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>240362891</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>rdpclip.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\rdpclip.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>328579443</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>cmd.exe</v></c><c><v>ad7b9c14083b52bc532fba5948342b98 c:\\windows\\system32\\cmd.exe</v></c><c><v>C:\\Windows\\System32\\cmd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>362478771</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>fdhost.exe</v></c><c><v>1b100b5fc879b899f9ef85392c90a79c c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\fdhost.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\fdhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>430230444</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>fdlauncher.exe</v></c><c><v>c5e1fe7db2202d37ba9a634e7f230a44 c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\fdlauncher.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\fdlauncher.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>460833048</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>firefox.exe</v></c><c><v>345b45be09381d2011eb7f9ac11d8ac4 c:\\program files (x86)\\mozilla firefox\\firefox.exe</v></c><c><v>C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe</v></c><c><v>11112 {TCP/127.0.0.1:63700,TCP/127.0.0.1:63701}</v></c><c><v>1</v></c></r><r><id>488066177</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>System</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>489839558</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>explorer.exe</v></c><c><v>332feab1435662fc6c672e25beb37be3 c:\\windows\\explorer.exe</v></c><c><v>C:\\Windows\\Explorer.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>525975750</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v>32 {TCP/10.167.170.124:65185,TCP/127.0.0.1:65150}</v></c><c><v>1</v></c></r><r><id>554946170</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>wscript.exe</v></c><c><v>979d74799ea6c8b8167869a68df5204a c:\\windows\\system32\\wscript.exe</v></c><c><v>C:\\Windows\\System32\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>587507959</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>winlogon.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>704071111</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>cmd.exe</v></c><c><v>622d21c40a25f9834a03bfd5ff4710c1 c:\\windows\\system32\\cmd.exe</v></c><c><v>C:\\Windows\\system32\\cmd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>742390788</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>wscript.exe</v></c><c><v>d536ccce2a7992688db76941506ea970 c:\\windows\\system32\\wscript.exe</v></c><c><v>C:\\Windows\\System32\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>752500312</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskmgr.exe</v></c><c><v>545bf7eaa24a9e062857d0742ec0b28a c:\\windows\\system32\\taskmgr.exe</v></c><c><v>C:\\Windows\\system32\\taskmgr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>806209098</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>powershell.exe</v></c><c><v>ef8fa4f195c6239273c100ab370fcfdc c:\\windows\\system32\\windowspowershell\\v1.0\\powershell.exe</v></c><c><v>C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>868409369</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>spoolsv.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\spoolsv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>868772106</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>smss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>930388804</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>chrome.exe</v></c><c><v>c4ef32c1c0473392ef4204890af8e457 c:\\program files (x86)\\google\\chrome\\application\\chrome.exe</v></c><c><v>C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>950785313</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>XenDpriv.exe</v></c><c><v>637c075ca92a0e272029f53b345d1bfd c:\\program files (x86)\\citrix\\xentools\\xendpriv.exe</v></c><c><v>C:\\Program Files (x86)\\Citrix\\XenTools\\XenDpriv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>969791269</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>conhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\conhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1027493949</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>wininit.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\wininit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1045242840</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>regedit.exe</v></c><c><v>2f3fed31ac2846d8ad5dbc396a7e3df1 c:\\windows\\regedit.exe</v></c><c><v>C:\\Windows\\regedit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1052137475</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>notepad.exe</v></c><c><v>8bab7a5d5c1477d0641195e623db32b4 c:\\windows\\system32\\notepad.exe</v></c><c><v>C:\\Windows\\System32\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1056487211</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>msdtc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\msdtc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1178014734</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\sysWOW64\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1192001015</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>System</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1197626474</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sqlwriter.exe</v></c><c><v>8fd8ee71d7d639f85805eee4adb2aa15 c:\\program files\\microsoft sql server\\90\\shared\\sqlwriter.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\90\\Shared\\sqlwriter.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1232513977</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v>4044 {TCP/192.168.12.27:53180,TCP/192.168.12.27:53206,TCP/192.168.12.27:53209}</v></c><c><v>1</v></c></r><r><id>1277035939</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>spoolsv.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\spoolsv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1291572388</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>System Idle Process</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v>0 {TCP/10.167.170.124:9443,TCP/10.167.170.124:17472,TCP/10.167.170.124:65046,TCP/10.167.170.124:65074,TCP/10.167.170.124:65116,TCP/10.167.170.124:65121,TCP/10.167.170.124:65151,TCP/127.0.0.1:9444,TCP/127.0.0.1:17472,TCP/127.0.0.1:65112,TCP/127.0.0.1:65123,TCP/127.0.0.1:65154,TCP/127.0.0.1:65158}</v></c><c><v>1</v></c></r><r><id>1298898850</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>php-cgi.exe</v></c><c><v>0d410f3ebe76bf7c0b45c47e67ca8de1 c:\\progra~1\\tanium\\tanium~1\\php55\\php-cgi.exe</v></c><c><v>C:\\PROGRA~1\\Tanium\\TANIUM~1\\php55\\php-cgi.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1333706542</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>conhost.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\System32\\WScript.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1467538242</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>System Idle Process</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v>0 {TCP/192.168.12.27:53078,TCP/192.168.12.27:53079,TCP/192.168.12.27:53111,TCP/192.168.12.27:53114,TCP/192.168.12.27:53115,TCP/192.168.12.27:53144,TCP/192.168.12.27:53148,TCP/192.168.12.27:53149,TCP/192.168.12.27:53183,TCP/192.168.12.27:53184}</v></c><c><v>1</v></c></r><r><id>1467589887</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>services.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\SysWow64\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1592719234</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>svchost.exe</v></c><c><v>d0abc231c0b3e88c6b612b28abbf734d c:\\windows\\system32\\svchost.exe</v></c><c><v>C:\\Windows\\System32\\svchost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1674266488</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>smss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1709652520</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>Ec2Config.exe</v></c><c><v>b3fde4699997759320cba603e2aed2cd c:\\program files\\amazon\\ec2configservice\\ec2config.exe</v></c><c><v>C:\\Program Files\\Amazon\\Ec2ConfigService\\Ec2Config.exe</v></c><c><v>1472 {TCP/192.168.12.27:53182}</v></c><c><v>1</v></c></r><r><id>1767938001</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WmiPrvSE.exe</v></c><c><v>330c8cbd4343d04e72834b159d260e78 c:\\windows\\syswow64\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\sysWOW64\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1851379401</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>cb.exe</v></c><c><v>d08f5c01b23aa7f356693c028ab592ba c:\\windows\\carbonblack\\cb.exe</v></c><c><v>C:\\Windows\\CarbonBlack\\cb.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2033331541</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>devenv.exe</v></c><c><v>b2a62caf90a30d233134c9ef7d2c73a7 c:\\program files (x86)\\microsoft visual studio 10.0\\common7\\ide\\devenv.exe</v></c><c><v>C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\Common7\\IDE\\devenv.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2070809886</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>wininit.exe</v></c><c><v>b5c5dcad3899512020d135600129d665 c:\\windows\\system32\\wininit.exe</v></c><c><v>C:\\Windows\\system32\\wininit.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2121839234</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2179234476</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>Taskmgr.exe</v></c><c><v>9919d598108e8e449d98aba2c43d2f20 c:\\windows\\system32\\taskmgr.exe</v></c><c><v>C:\\Windows\\system32\\taskmgr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2203176992</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sh.exe</v></c><c><v>e890d295ab412762b7c3def0fdd9c16a c:\\cygwin64\\bin\\sh.exe</v></c><c><v>C:\\cygwin64\\bin\\sh.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2220592385</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>rdpclip.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\rdpclip.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2335124868</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>mmc.exe</v></c><c><v>b316385fd7c1e1cbad339c33cf3c0409 c:\\windows\\system32\\mmc.exe</v></c><c><v>C:\\Windows\\system32\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2351261712</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>TaniumClient.exe</v></c><c><v>0db1ccd7230fdee1adb04e536ea60759 c:\\program files (x86)\\tanium\\tanium client\\taniumclient.exe</v></c><c><v>C:\\Program Files (x86)\\Tanium\\Tanium Client\\TaniumClient.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2384135557</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>lsass.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\lsass.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2393445266</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>httpd.exe</v></c><c><v>437a04590133ea73fb2078bf114ac542 c:\\program files\\tanium\\tanium server\\apache24\\bin\\httpd.exe</v></c><c><v>C:\\Program Files\\Tanium\\Tanium Server\\Apache24\\bin\\httpd.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2407801199</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>taskhostex.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\taskhostex.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2439515127</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>lsm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\lsm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2477658529</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>dwm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\dwm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2615369603</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>SQLAGENT.EXE</v></c><c><v>d72a162f3e75a046def27edaa92cdb67 c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\sqlagent.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\SQLAGENT.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2644508573</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\system32\\mmc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2667342661</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>rdpinput.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\rdpinput.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2764744017</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>winlogon.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\winlogon.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2777407551</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>WmiPrvSE.exe</v></c><c><v>1951c6f1e53079f6b29ecff77eaf9403 c:\\windows\\syswow64\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\sysWOW64\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2818851634</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>2860491008</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>NETSTAT.EXE</v></c><c><v>32297bb17e6ec700d0fc869f9acaf561 c:\\windows\\system32\\netstat.exe</v></c><c><v>C:\\Windows\\System32\\netstat.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2866659743</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>Ec2Config.exe</v></c><c><v>f02c17c6679c389cf49fe840f8df8089 c:\\program files\\amazon\\ec2configservice\\ec2config.exe</v></c><c><v>C:\\Program Files\\Amazon\\Ec2ConfigService\\Ec2Config.exe</v></c><c><v>2076 {TCP/10.167.170.124:65156}</v></c><c><v>1</v></c></r><r><id>2869899582</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>svchost.exe</v></c><c><v>54a47f6b5e09a77e61649109c6a08866 c:\\windows\\system32\\svchost.exe</v></c><c><v>C:\\Windows\\System32\\svchost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>2957826223</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvwmi64.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\nvwmi64.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3034446139</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\taskhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3084297043</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>mintty.exe</v></c><c><v>0c4ce674d7a2139d161c349cf468109c c:\\cygwin64\\bin\\mintty.exe</v></c><c><v>C:\\cygwin64\\bin\\mintty.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3092459201</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>msdtc.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\System32\\msdtc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3108142843</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>WmiPrvSE.exe</v></c><c><v>1951c6f1e53079f6b29ecff77eaf9403 c:\\windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3187118403</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>lsass.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\lsass.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3196297430</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>sqlservr.exe</v></c><c><v>5957dad9cec0dd7da81a3fcefe029dce c:\\program files\\microsoft sql server\\mssql12.mssqlserver\\mssql\\binn\\sqlservr.exe</v></c><c><v>C:\\Program Files\\Microsoft SQL Server\\MSSQL12.MSSQLSERVER\\MSSQL\\Binn\\sqlservr.exe</v></c><c><v>1352 {TCP/10.167.170.124:1433}</v></c><c><v>1</v></c></r><r><id>3273756515</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>csrss.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\csrss.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3306678053</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WmiPrvSE.exe</v></c><c><v>330c8cbd4343d04e72834b159d260e78 c:\\windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v>C:\\Windows\\system32\\wbem\\wmiprvse.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3352665611</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>conhost.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\conhost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3374180811</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>csrss.exe</v></c><c><v>[NO BIN]</v></c><c><v>C:\\Windows\\System32\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3457634775</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>dwm.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\Dwm.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3542642798</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>TaniumReceiver.exe</v></c><c><v>2ba63ff4db05cbbd8bc93295fc2838f9 c:\\program files\\tanium\\tanium server\\taniumreceiver.exe</v></c><c><v>C:\\Program Files\\Tanium\\Tanium Server\\TaniumReceiver.exe</v></c><c><v>8064 {TCP/10.167.170.124:17472,TCP/10.167.170.124:49363,TCP/10.167.170.124:49364,TCP/10.167.170.124:49366,TCP/10.167.170.124:49367,TCP/10.167.170.124:49369,TCP/10.167.170.124:49370,TCP/10.167.170.124:49371,TCP/10.167.170.124:49373,TCP/10.167.170.124:49374,TCP/10.167.170.124:49379,TCP/10.167.170.124:49380,TCP/10.167.170.124:49382,TCP/10.167.170.124:49383,TCP/10.167.170.124:49384,TCP/10.167.170.124:49385,TCP/10.167.170.124:49386,TCP/10.167.170.124:49387,TCP/10.167.170.124:49388,TCP/10.167.170.124:49389,TCP/10.167.170.124:49390,TCP/10.167.170.124:49392,TCP/10.167.170.124:49393,TCP/10.167.170.124:49394,TCP/10.167.170.124:49395,TCP/10.167.170.124:49396,TCP/10.167.170.124:49397,TCP/10.167.170.124:49430,TCP/10.167.170.124:49448,TCP/10.167.170.124:53006,TCP/10.167.170.124:59480,TCP/127.0.0.1:17472}</v></c><c><v>1</v></c></r><r><id>3593426093</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>ServerManager.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\ServerManager.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3652869174</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>explorer.exe</v></c><c><v>85d47eb257b06094f052e0c8aefa3bee c:\\windows\\explorer.exe</v></c><c><v>C:\\Windows\\Explorer.EXE</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3666264833</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>OpenWith.exe</v></c><c><v>1dfe1ed0a9ef0fa4ffe8d08dfb00f121 c:\\windows\\system32\\openwith.exe</v></c><c><v>C:\\Windows\\system32\\OpenWith.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3806172439</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>LogonUI.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\LogonUI.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3869451342</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>firefox.exe</v></c><c><v>14cf73d771fa977a9f1cbaa5c301f912 c:\\program files (x86)\\mozilla firefox\\firefox.exe</v></c><c><v>C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe</v></c><c><v>3364 {TCP/127.0.0.1:56690,TCP/127.0.0.1:56691,TCP/192.168.12.27:57580,TCP/192.168.12.27:57582,TCP/192.168.12.27:57583}</v></c><c><v>1</v></c></r><r><id>3874003054</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>taskeng.exe</v></c><c><v>4f2659160afcca990305816946f69407 c:\\windows\\system32\\taskeng.exe</v></c><c><v>C:\\Windows\\system32\\taskeng.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3902538584</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>plugin_host.exe</v></c><c><v>85cfc4258e6538bc3f425edfe200a5a7 c:\\users\\akshat\\desktop\\sublime text build 3083 x64\\plugin_host.exe</v></c><c><v>C:\\Users\\akshat\\Desktop\\Sublime Text Build 3083 x64\\plugin_host.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3920685033</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>services.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\services.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>3960152428</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>dllhost.exe</v></c><c><v>cc05c14eeff5e7813a49718ba88e59b0 c:\\windows\\system32\\dllhost.exe</v></c><c><v>C:\\Windows\\system32\\DllHost.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4074252443</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>VSSVC.exe</v></c><c><v>[Unable to process MD5]</v></c><c><v>C:\\Windows\\SysWow64\\vssvc.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4198614073</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>XenGuestAgent.exe</v></c><c><v>7eae59832be1c3a08d4f94cebccaf13a c:\\program files (x86)\\citrix\\xentools\\xenguestagent.exe</v></c><c><v>C:\\Program Files (x86)\\Citrix\\XenTools\\XenGuestAgent.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4252031705</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>notepad.exe</v></c><c><v>d378bffb70923139d6a4f546864aa61c c:\\windows\\system32\\notepad.exe</v></c><c><v>C:\\Windows\\System32\\Notepad.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4254444053</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvSCPAPISvr.exe</v></c><c><v>701bb23989e138c1c8b436c5940a1f6e c:\\program files (x86)\\nvidia corporation\\3d vision\\nvscpapisvr.exe</v></c><c><v>C:\\Program Files (x86)\\NVIDIA Corporation\\3D Vision\\nvSCPAPISvr.exe</v></c><c><v></v></c><c><v>1</v></c></r><r><id>4263354092</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>nvxdsync.exe</v></c><c><v>de701bee6a35ecd48c9a582bbff943c0 c:\\program files\\nvidia corporation\\display\\nvxdsync.exe</v></c><c><v>C:\\Program Files\\NVIDIA Corporation\\Display\\nvxdsync.exe</v></c><c><v></v></c><c><v>1</v></c></r></rs></result_set>";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "flume_tanium");
        headers.put("hostname", "somehost");
        headers.put("saved_question_name", "Host-Processes details v2");
        headers.put("result_set_timestamp", "2015/06/08 09:01:30 GMT-0000");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 109, output.size());
        for (int i = 0; i < output.size(); i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s", i, out));
            switch (i){
                case 0:
                    assertEquals("logCollectionCategory", "flume_tanium", out.get("logCollectionCategory").get(0));
                    assertEquals("processFilePath", "C:\\Windows\\SysWow64\\nvvsvc.exe", out.get("processFilePath").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionTime", "1384693669604", out.get("logCollectionTime").get(0));
                    assertEquals("taniumQuestion", "Host-Processes details v2", out.get("taniumQuestion").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("processName", "nvvsvc.exe", out.get("processName").get(0));
                    assertEquals("_validationLog", "[unexpected value format for field 'processFileMd5' : [[Unable]]", out.get("_validationLog").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "UnMatched-validation", out.get("logSourceType").get(0));
                    assertEquals("taniumTime", "2015/06/08 09:01:30 GMT-0000", out.get("taniumTime").get(0));
                    assertEquals("md5", "[Unable to process MD5]", out.get("md5").get(0));
                    break;

                case 3:
                    assertEquals("logCollectionCategory", "flume_tanium", out.get("logCollectionCategory").get(0));
                    assertEquals("processFileMd5", "345b45be09381d2011eb7f9ac11d8ac4", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe", out.get("processFilePath").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Processes details v2", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("processName", "firefox.exe", out.get("processName").get(0));
                    assertEquals("transportProtocol", "TCP", out.get("transportProtocol").get(0));
                    assertEquals("processListenPort", 63700, out.get("processListenPort").get(0));
                    assertEquals("startTime", 1433754090000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HostPortMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HostPortMef", out.get("logSourceType").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    break;
                default:
                    OutUtils.printOut(i+" : "+ out);
            }
        }
    }

    public void testFlumeTaniumHetInformationsWithMissingResultAvroEventParser() throws IOException, Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add( SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "<result_set><now>2015/05/27 15:15:55 GMT-0000</now>\n<age>0</age><archived_question_id>0</archived_question_id><saved_question_id>227</saved_question_id><question_id>183894</question_id><report_count>3</report_count><seconds_since_issued>67</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>4210360868</wh><dn>het information</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>4</filtered_row_count><filtered_row_count_machines>4</filtered_row_count_machines><item_count>4</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v>1</v></c></r><r><id>82443432</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\nDNSHostName = &quot;WIN-OSNMCI3GJJ1&quot;\nIPAddress = {&quot;10.167.170.124&quot;, &quot;fe80::ccea:b9e7:d8f2:4952&quot;}\nMACAddress = &quot;22:00:0B:47:0B:A4\n</v></c><c><v>1</v></c></r><r><id>762878037</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\nDNSHostName = &quot;RDP-GW&quot;\nIPAddress = {&quot;fe80::cd4b:f92a:5ce8:6216&quot;}\nMACAddress = &quot;12:4A:FB:CB:8B:93\n</v></c><c><v>1</v></c></r><r><id>1270274401</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\nDNSHostName = &quot;RDP-GW&quot;\nIPAddress = {&quot;192.168.12.27&quot;}\nMACAddress = &quot;12:7F:C8:56:84:17\n</v></c><c><v>1</v></c></r></rs></result_set>";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "flume_tanium");
        headers.put("hostname", "somehost");
        headers.put("saved_question_name", "het informations");
        headers.put("result_set_timestamp", "2015/05/27 15:15:55 GMT-0000");

        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 3, output.size());
        for (int i=0; i < output.size();i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("destinationAddress", "10.167.170.124", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "22:00:0B:47:0B:A4", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "WIN-OSNMCI3GJJ1", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432739755000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    break;

                case 1:
//          assertEquals("destinationAddress", "192.168.12.9", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "12:4A:FB:CB:8B:93", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "RDP-GW", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432739755000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    break;
                case 2:
                    assertEquals("destinationAddress", "192.168.12.27", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "12:7F:C8:56:84:17", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "RDP-GW", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432739755000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));

                    break;
                default:
            }
        }

    }

    //
    public void testFlumeTaniumCpuConsumptionsWithMissingResultAvroEventParser() throws IOException, Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add( SupportedFormats.HostCpuMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "<result_set><now>2015/05/27 15:20:55 GMT-0000</now>\\n<age>0</age><archived_question_id>0</archived_question_id><saved_question_id>233</saved_question_id><question_id>183899</question_id><report_count>4</report_count><seconds_since_issued>0</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>98976143</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>98976143</wh><dn>CPU Consumption</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1661264119</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>2 %</v></c><c><v>1</v></c></r><r><id>3260989120</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>1 %</v></c><c><v>1</v></c></r></rs></result_set>";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "flume_tanium");
        headers.put("hostname", "somehost");
        headers.put("saved_question_name", "HOST-CPU consumptions");
        headers.put("result_set_timestamp", "2015/05/27 15:20:55 GMT-0000");
        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 2, output.size());
        for (int i=0; i < output.size();i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("cpuConsumption", 2.0f, out.get("cpuConsumption").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432740055000L, out.get("startTime").get(0));
                    break;

                case 1:
                    assertEquals("cpuConsumption", 1.0f, out.get("cpuConsumption").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("startTime", 1432740055000L, out.get("startTime").get(0));
                    break;
                default:
            }
        }

    }
    public void testFlumeTaniumLoggedInUsersAvroEventParser() throws IOException, Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add( SupportedFormats.UETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "<result_set><now>2015/05/27 15:20:56 GMT-0000</now>\\n<age>0</age><archived_question_id>0</archived_question_id><saved_question_id>235</saved_question_id><question_id>183902</question_id><report_count>3</report_count><seconds_since_issued>87</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>3882709820</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>3882709820</wh><dn>Userid</dn><rt>1</rt></c><c><wh>3882709820</wh><dn>Full Name</dn><rt>1</rt></c><c><wh>3882709820</wh><dn>Email Address</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>2544406072</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>WIN-OSNMCI3GJJ1\\jyria</v></c><c><v>Unknown or Local User</v></c><c><v>Unknown or Local User</v></c><c><v>1</v></c></r><r><id>3242379506</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>RDP-GW\\jyria</v></c><c><v>Unknown or Local User</v></c><c><v>Unknown or Local User</v></c><c><v>1</v></c></r></rs></result_set>";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "flume_tanium");
        headers.put("hostname", "somehost");
        headers.put("saved_question_name", "Hosts-Logged in user details");
        headers.put("result_set_timestamp", "2015/05/27 15:20:56 GMT-0000");
        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);

        //OutUtils.printOut(out);

        Assert.assertEquals("number of records", 2, output.size());
        for (int i=0; i < output.size();i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("deviceUserName", "WIN-OSNMCI3GJJ1\\jyria", out.get("deviceUserName").get(0));
                    assertEquals("deviceUserFullName", "Unknown or Local User", out.get("deviceUserFullName").get(0));
                    assertEquals("deviceUserEmailAddress", "Unknown or Local User", out.get("deviceUserEmailAddress").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Hosts-Logged in user details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "UETMef", out.get("logSourceType").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("startTime", 1432740056000L, out.get("startTime").get(0));
                    break;

                case 1:
                    assertEquals("deviceUserName", "RDP-GW\\jyria", out.get("deviceUserName").get(0));
                    assertEquals("deviceUserFullName", "Unknown or Local User", out.get("deviceUserFullName").get(0));
                    assertEquals("deviceUserEmailAddress", "Unknown or Local User", out.get("deviceUserEmailAddress").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Hosts-Logged in user details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "UETMef", out.get("logSourceType").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("startTime", 1432740056000L, out.get("startTime").get(0));
                    break;
                default:
            }
        }

    }

    @Test
    public void test_DhcpdToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "Jun 10 20:00:00 192.168.40.20 /opt/qip/usr/bin/dhcpd[27271]: DHCP_RenewLease: Host=SEPF47F35A29A7A IP=10.1.248.75 MAC=f47f35a29a7a Domain=dhcp.amer.blabla.net ClientID=01f47f35a29a7a";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = null;
        output = instance.parse(avroEvent);
        Assert.assertEquals("number of records", 1, output.size());
        for (int i = 0; i < output.size(); i++) {
            Map<String, List<Object>> out = output.get(i);
            OutUtils.printOut(String.format("%02d : %s", i, out));
            switch (i) {
                case 0:
                    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
                    assertEquals("deviceNameOrIp", "192.168.40.20", out.get("deviceNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("destinationAddress", "10.1.248.75", out.get("destinationAddress").get(0));
                    assertEquals("destinationHostName", "sepf47f35a29a7a", out.get("destinationHostName").get(0));
                    assertEquals("destinationDnsDomain", "dhcp.amer.blabla.net", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "f47f35a29a7a", out.get("destinationMacAddress").get(0));
                    assertEquals("startTime", 1465588800000L, out.get("startTime").get(0));
                    break;
                default:
            }


        }
    }

    //
    @Test
    public void testCefArcsightDHCP() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "Jun 08 16:17:40 stuxbash01.st0ss2.example.com CEF:0|unknown |dhcpd|unknown|DHCPINFORM|unknown|0|cs1Label=deviceInboundInterface cs6Label=raw cs6=Jun  8 15:17:40 stuxbash01 dhcpd: DHCPINFORM from 10.168.172.252 via 10.168.172.253 cs3Label=src_mac cs2=10.168.172.252 cs1=10.168.172.253 dproc=dhcpd cs2Label=src_ip";
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

        assertEquals("deviceDnsDomain", "st0ss2.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("syslogMessage", "CEF:0|unknown |dhcpd|unknown|DHCPINFORM|unknown|0|cs1Label=deviceInboundInterface cs6Label=raw cs6=Jun  8 15:17:40 stuxbash01 dhcpd: DHCPINFORM from 10.168.172.252 via 10.168.172.253 cs3Label=src_mac cs2=10.168.172.252 cs1=10.168.172.253 dproc=dhcpd cs2Label=src_ip", out.get("syslogMessage").get(0));
        assertEquals("sourceAddress", "10.168.172.252", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceInterface", "10.168.172.253", out.get("deviceInterface").get(0));
        assertEquals("cefSignatureId", "DHCPINFORM", out.get("cefSignatureId").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("receiptTime", 1465402660000L, out.get("receiptTime").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("startTime", 1465402660000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "stuxbash01.st0ss2.example.com", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "stuxbash01", out.get("deviceHostName").get(0));
    }


    @Test
    public void testWindowsSnareCEFAD4769ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "Jun 18 16:34:44 host2 CEF:0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4769|A Kerberos service ticket was requested|Low| eventId=975425144 externalId=4769 msg=Account Information:   Account Name:  XX1111111111112$@EXAMPLE.COM   Account Domain:  EXAMPLE.COM   Logon GUID:  {00000000-0000-0000-0000-000000000000}    Service Information:   Service Name:  krbtgt/EXAMPLE.COM   Service ID:  S-1-0-0    Network Information:   Client Address:  ::ffff:10.9.7.5   Client Port:  64304    Additional Information:   Ticket Options:  0x60810010   Ticket Encryption Type: 0xffffffff   Failure Code:  0xe   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.  categorySignificance=/Normal categoryBehavior=/Authentication/Verify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Attempt categoryObject=/Host/Operating System art=1434663284564 cat=Security deviceSeverity=4 rt=1434652483000 spt=64304 dhost=XXXXX.RB.AD.EXAMPLE.COM dntdom=EXAMPLE.COM duser=XX1111111111112$@EXAMPLE.COM destinationServiceName=krbtgt/EXAMPLE.COM cs2=Kerberos Service Ticket Operations cs3=::ffff:10.9.7.5 cs4=0xe cs6={00000000-0000-0000-0000-000000000000} c6a2=0:0:0:0:0:ffff::a5 cs1Label=Accesses cs2Label=EventlogCategory cs3Label=Client Address cs4Label=Reason or Error Code cs5Label=Authentication Package Name cs6Label=Logon GUID cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count c6a2Label=Source IPv6 Address ahost=host1.svr.us.jpmchase.net agt=10.5.137.64 agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.5.7132.0 atz=America/New_York aid=3Kwbn4UkBABCBy0OBjJe9TA\\=\\= at=syslog dvchost=XXXXX.EXAMPLE.COM dtz=America/New_York _cefVer=0.1 ad.ExtraParam0=This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested. ad.Service_,Information:Service_,ID=S-1-0-0 ad.Additional_,InMbHgGQ_~_~Ticket_,Options=0x60810010 ad.Additional_,Inc1+cZg_~_~sited_,Services=- ad.ExtraParam1=This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. ad.ExtraParam2=Ticket options, encryption types, and failure codes are defined in RFC 4120. ad.Additional_,InagWMqg_~_~ncryption_,Type=0xffffffff ad.message=Account Information:   Account Name:  XX1111111111112$@EXAMPLE.COM   Account Domain:  EXAMPLE.COM   Logon GUID:  {00000000-0000-0000-0000-000000000000}    Service Information:   Service Name:  krbtgt/EXAMPLE.COM   Service ID:  S-1-0-0    Network Information:   Client Address:  ::ffff:10.9.7.5   Client Port:  64304    Additional Information:   Ticket Options:  0x60810010   Ticket Encryption Type: 0xffffffff   Failure Code:  0xe   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.     User: EXAMPLE.COM\\\\XX1111111111112$@EXAMPLE.COM     ComputerName: XXXXX.EXAMPLE.COM";
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
        assertEquals("ticketEncryptionType", "0xffffffff", out.get("ticketEncryptionType").get(0));
        assertEquals("sourcePort", 64304, out.get("sourcePort").get(0));
        assertEquals("destinationProcessName", "krbtgt", out.get("destinationProcessName").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "host2", out.get("deviceHostName").get(0));
        assertEquals("destinationServiceName", "krbtgt/EXAMPLE.COM", out.get("destinationServiceName").get(0));
        assertEquals("startTime", 1434663284564L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "EXAMPLE.COM", out.get("destinationNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceAddress", "10.9.7.5", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
        assertEquals("status", "0xe", out.get("status").get(0));
        assertEquals("destinationServiceSecurityID", "S-1-0-0", out.get("destinationServiceSecurityID").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("destinationUserName", "XX1111111111112$@EXAMPLE.COM".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("cefEventName", "A Kerberos service ticket was requested", out.get("cefEventName").get(0));
        assertEquals("ticketOptions", "0x60810010", out.get("ticketOptions").get(0));
        assertEquals("deviceNameOrIp", "host2", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4769-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "EXAMPLE.COM", out.get("destinationNtDomain").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

        // missing
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));

    }

    @Test
    public void testWindowsSnareCEFAD4738ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 15:29:44 xxxxxxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4738|A user account was changed|Low| eventId=2023517772 externalId=4738 msg=Subject:   Security ID:  S-1-5-21-1214440339-1979792683-682003330-1999097   Account Name:  xxxxxxx   Account Domain:  xxxxxx   Logon ID:  0x4e839d82f    Target Account:   Security ID:  S-1-5-21-1214440339-1979792683-682003330-2742626   Account Name:  xxxxx   Account Domain:  xxxxxx    Changed Attributes:   SAM Account Name: -   Display Name:  -   User Principal Name: -   Home Directory:  -   Home Drive:  -   Script Path:  -   Profile Path:  -   User Workstations: -   Password Last Set: 7/29/2015 4:29:42 PM   Account Expires:  -   Primary Group ID: -   AllowedToDelegateTo: -   Old UAC Value:  -   New UAC Value:  -   User Account Control: -   User Parameters: -   SID History:  -   Logon Hours:  -    Additional Information:   Privileges:  -     User: xxxxxx\\\\xxxxxxx     ComputerName: xxxxxxxxx.xxxxxx.AD.EXAMPLE.COM categorySignificance=/Normal categoryBehavior=/Authentication/Modify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438201784342 cat=Security deviceSeverity=4 rt=1438201782000 sntdom=xxxxxx suser=xxxxxxx suid=0x4e839d82f dhost=xxxxxxxxx.xxxxxx.AD.EXAMPLE.COM dntdom=xxxxxx duser=xxxxxxx duid=0x4e839d82f dpriv=- cs2=User Account Management cs3=Microsoft-Windows-Security-Auditing cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count c6a2Label=Source IPv6 Address ahost=psin1p945.svr.us.jpmchase.net agt=xxx.xxx.xx.xx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.5.7132.0 atz=US/Eastern aid=3tWHf4kkBABCBFRQtghAiXw\\=\\= at=syslog dvchost=xxxxxxxxx.xxxxxx.AD.EXAMPLE.COM deviceNtDomain=xxxxxx dtz=US/Eastern _cefVer=0.1 ad.Changed_,Attributes:Display_,Name=- ad.Changed_,Attributes:Primary_,Group_,ID=- ad.Changed_,Attri7DkJjg_~_~edToDelegateTo=- ad.Changed_,Attributes:Profile_,Path=- ad.Changed_,Attributes:User_,Parameters=- ad.Changed_,Attributes:Home_,Drive=- ad.Changed_,AttriZq6lpA_~_~sword_,Last_,Set=7/29/2015 4:29:42 PM ad.Changed_,Attri20xBLQ_~_~Principal_,Name=- ad.Changed_,Attributes:Account_,Expires=- ad.Changed_,Attributes:SID_,History=- ad.Changed_,Attributes:New_,UAC_,Value=- ad.Changed_,Attributes:SAM_,Account_,Name=- ad.Changed_,AttrizoRdMA_~_~r_,Workstations=- ad.Changed_,Attributes:Home_,Directory=- ad.Changed_,Attributes:Old_,UAC_,Value=- ad.Changed_,Attributes:Logon_,Hours=- ad.Changed_,Attributes:Script_,Path=- ad.Changed_,AttrijOTRqQ_~_~ccount_,Control=- ad.Target_,Account:Security_,ID=S-1-5-21-1214440339-1979792683-682003330-2742626";


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
        assertEquals("adDisplayName", "-", out.get("adDisplayName").get(0));
        assertEquals("adSamAccountName", "-", out.get("adSamAccountName").get(0));
        assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("adPasswdLastSet", "7/29/2015 4:29:42 PM", out.get("adPasswdLastSet").get(0));
        assertEquals("deviceHostName", "xxxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
        assertEquals("startTime", 1438201784342L, out.get("startTime").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("sourceSecurityID", "S-1-5-21-1214440339-1979792683-682003330-1999097", out.get("sourceSecurityID").get(0));
        assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
        assertEquals("sourceUserName", "xxxxxxx", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
        assertEquals("sourceNtDomain", "xxxxxx", out.get("sourceNtDomain").get(0));
        assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
        assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
        assertEquals("destinationUserName", "xxxxx", out.get("destinationUserName").get(0));
        assertEquals("sourceLogonID", "0x4e839d82f", out.get("sourceLogonID").get(0));
        assertEquals("cefEventName", "A user account was changed", out.get("cefEventName").get(0));
        assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
        assertEquals("deviceNameOrIp", "xxxxxxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
        assertEquals("cefSignatureId", "Security-4738-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
        assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
        assertEquals("destinationNtDomain", "xxxxxx", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-1214440339-1979792683-682003330-2742626", out.get("destinationSecurityID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testWindowsSnareCEFAD4742EventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 15:15:03 xxxxxxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4742|A computer account was changed|Low| eventId=2955511744 externalId=4742 msg=Subject:   Security ID:  S-1-5-21-1097103103-1991615301-137165372-1200242   Account Name:  xxxxxxx   Account Domain:  xxxxx   Logon ID:  0x26d06fdf8    Computer Account That Was Changed:   Security ID:  S-1-5-21-1097103103-1991615301-137165372-2261256   Account Name:  xxxxxxxxxxxxxx   Account Domain:  xxxxx    Changed Attributes:   SAM Account Name: -   Display Name:  -   User Principal Name: -   Home Directory:  -   Home Drive:  -   Script Path:  -   Profile Path:  -   User Workstations: -   Password Last Set: -   Account Expires:  -   Primary Group ID: -   AllowedToDelegateTo: -   Old UAC Value:  0x81   New UAC Value:  0x80   User Account Control:     Account Enabled   User Parameters: -   SID History:  -   Logon Hours:  -   DNS Host Name:  -   Service Principal Names: -    Additional Information:   Privileges:  -     User: xxxxxx\\\\xxxxxxxxxxxxxx     ComputerName: xxxxxxxxx.xxxxxx.jpmchase.net categorySignificance=/Normal categoryBehavior=/Authentication/Modify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438200902071 cat=Security deviceSeverity=4 rt=1438200476000 sntdom=xxxxxx suser=xxxxxxx suid=0x26d06fdf8 dhost=xxxxxxxxx.xxxxxx.jpmchase.net dntdom=xxxxxx duid=0x26d06fdf8 dpriv=- cs2=Computer Account Management cs3=Microsoft-Windows-Security-Auditing cs6=xxxxxx\\\\xxxxxxxxxxxxxx cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cs6Label=Account Domain and Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count c6a2Label=Source IPv6 Address ahost=xxxxxxxxx.svr.us.jpmchase.net agt=xxx.xxx.xx.xx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.5.7132.0 atz=US/Eastern aid=3Lgi04kkBABCACnrFSLpCYg\\=\\= at=syslog dvchost=xxxxxxxxx.xxxxxx.jpmchase.net deviceNtDomain=xxxxxx dtz=US/Eastern _cefVer=0.1 ad.Service_,Principal_,Names=- ad.Changed_,Attributes:Display_,Name=- ad.Changed_,AttriZq6lpA_~_~sword_,Last_,Set=- ad.Changed_,Attributes:Home_,Drive=- ad.Changed_,Attributes:Account_,Expires=- ad.Changed_,Attri20xBLQ_~_~Principal_,Name=- ad.ExtraParam0=Account Enabled ad.DNS_,Host_,Name= ad.SID_,History= ad.ExtraParam3=- ad.Changed_,Attributes:SAM_,Account_,Name=- ad.Logon_,Hours= ad.ExtraParam1=- ad.Changed_,Attributes:Script_,Path=- ad.ExtraParam2=- ad.Changed_,AttrijOTRqQ_~_~ccount_,Control= ad.Changed_,Attributes:Primary_,Group_,ID=- ad.Changed_,Attri7DkJjg_~_~edToDelegateTo=- ad.Changed_,Attributes:Profile_,Path=- ad.Computer_,AccoveH2KQ_~_~ed:Security_,ID=S-1-5-21-1097103103-1991615301-137165372-2261256 ad.Changed_,Attributes:New_,UAC_,Value=0x80 ad.Changed_,Attributes:Old_,UAC_,Value=0x81 ad.Changed_,Attributes:Home_,Directory=- ad.Changed_,AttrizoRdMA_~_~r_,Workstations=- ad.User_,Parameters=-";
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
        assertEquals("adDisplayName", "-", out.get("adDisplayName").get(0));
        assertEquals("adSamAccountName", "-", out.get("adSamAccountName").get(0));
        assertEquals("adOldUacValue", "0x81", out.get("adOldUacValue").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("adPasswdLastSet", "-", out.get("adPasswdLastSet").get(0));
        assertEquals("deviceHostName", "xxxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
        assertEquals("startTime", 1438200902071L, out.get("startTime").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("sourceSecurityID", "S-1-5-21-1097103103-1991615301-137165372-1200242", out.get("sourceSecurityID").get(0));
        assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
        assertEquals("sourceUserName", "xxxxxxx", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
        assertEquals("sourceNtDomain", "xxxxx", out.get("sourceNtDomain").get(0));
        assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("adDnsHostname", "-", out.get("adDnsHostName").get(0));
        assertEquals("adServerPrincipalNames", "-", out.get("adServerPrincipalNames").get(0));
        assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
        assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
        assertEquals("destinationUserName", "xxxxxxxxxxxxxx", out.get("destinationUserName").get(0));
        assertEquals("sourceLogonID", "0x26d06fdf8", out.get("sourceLogonID").get(0));
        assertEquals("cefEventName", "A computer account was changed", out.get("cefEventName").get(0));
        assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
        assertEquals("deviceNameOrIp", "xxxxxxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
        assertEquals("cefSignatureId", "Security-4742-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("adUserAccountControl", "Account Enabled", out.get("adUserAccountControl").get(0));
        assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
        assertEquals("destinationNtDomain", "xxxxx", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-1097103103-1991615301-137165372-2261256", out.get("destinationSecurityID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testWindowsSnareCEFAD4771EventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 15:32:24 xxxxxxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4771|Kerberos pre-authentication failed|Medium| eventId=1499028956 externalId=4771 msg=Account Information:   Security ID:  S-1-5-21-1715567821-1004336348-725345543-72028   Account Name:  xxxxxxx    Service Information:   Service Name:  xxxxxx/xxxx.AD.EXAMPLE.COM    Network Information:   Client Address:  ::ffff:10.10.30.145   Client Port:  49489    Additional Information:   Ticket Options:  0x40810010   Failure Code:  0x18   Pre-Authentication Type: 2    Certificate Information:   Certificate Issuer Name:     Certificate Serial Number:     Certificate Thumbprint:      Certificate information is only provided if a certificate was used for pre-authentication.    Pre-authentication types, ticket options and failure codes are defined in RFC 4120.    If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.     User: xxxxxxx     ComputerName: xxxxxxxxxxxx.xxxx.AD.EXAMPLE.COM categorySignificance=/Informational/Warning categoryBehavior=/Authentication/Verify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Failure categoryObject=/Host/Operating System art=1438201944435 cat=Security deviceSeverity=4 rt=1438201943000 reason=Pre-authentication information was invalid (Usually bad password) spt=49489 dhost=xxxxxxxxxxxx.xxxx.AD.EXAMPLE.COM dntdom=S-1-5-21-1715567821-1004336348-725345543-72028 duser=xxxxxxx destinationServiceName=xxxxxx/xxxx.AD.EXAMPLE.COM cs2=Kerberos Authentication Service cs3=::ffff:10.10.30.145 cs4=0x18 cs1Label=Accesses cs2Label=EventlogCategory cs3Label=Client Address cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxxxxxx.xxx.xxxx.jpmchase.net agt=xxx.xx.xxx.xxx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-xxx.xx.xxx.xx (ARIN) av=7.0.6.7189.0 atz=xxxxxx/xxxxxxxxxxx aid=3Vx-fyEkBABCA5yN3sE4bug\\=\\= at=syslog dvchost=xxxxxxxxxxxx.xxxx.AD.EXAMPLE.COM dtz=xxxxxx/xxxxxxxxxxx _cefVer=0.1 ad.ExtraParam0=Certificate information is only provided if a certificate was used for pre-authentication. ad.Additional_,InMbHgGQ_~_~Ticket_,Options=0x40810010 ad.ExtraParam1=Pre-authentication types, ticket options and failure codes are defined in RFC 4120. ad.ExtraParam2=If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present. ad.Certificate_,I0tc7xg_~_~te_,Issuer_,Name= ad.Additional_,In0c2UQw_~_~ntication_,Type=2 ad.Certificate_,Serial_,Number= ad.Certificate_,Thumbprint=";
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
        assertEquals("destinationHostName", "xxxx", out.get("destinationHostName").get(0));
        assertEquals("status", "0x18", out.get("status").get(0));
        assertEquals("sourcePort", 49489, out.get("sourcePort").get(0));
        assertEquals("destinationProcessName", "xxxxxx", out.get("destinationProcessName").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("destinationServiceName", "xxxxxx/xxxx.AD.EXAMPLE.COM", out.get("destinationServiceName").get(0));
        assertEquals("destinationUserName", "xxxxxxx", out.get("destinationUserName").get(0));
        assertEquals("startTime", 1438201944435L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "xxxx.AD.EXAMPLE.COM", out.get("destinationNameOrIp").get(0));
        assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
        assertEquals("cefEventName", "Kerberos pre-authentication failed", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "xxxxxxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));
        assertEquals("destinationDnsDomain", "ad.example.com", out.get("destinationDnsDomain").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4771-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("preAuthenticationType", "2", out.get("preAuthenticationType").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-1715567821-1004336348-725345543-72028", out.get("destinationSecurityID").get(0));
        assertEquals("sourceAddress", "10.10.30.145", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }
    @Test
    public void testCrowdStrikeDetectionSummaryEventToHostProcessMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HostProcessMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "CEF:0|CrowdStrike|FalconHost|1.0|DetectionSummaryEvent|Detection Summary Event|4|externalID=212a13eb2f9549b959c1943d509fc97b shost=TheNarrowSea suser=IIS1$ fname=iissvr.exe msg=This file meets the File Attribute ML algorithm's high-confidence threshold for malware. fileHash=e1864a55d5ccb76af4bf7a0ae16279ba cs6=https://falcon.crowdstrike.com/activity/detections/detail/212a13eb2f9549b959c1943d509fc97b/147940788502014 cs1=C:\\Windows\\iissvr.exe cn3Label=Offset sntdom=VICTIMNET cs6Label=FalconHostLink cs1Label=CommandLine deviceCustomDate1Label=ProcessStartTime spid=147958080753573 cn1=147958071097388 cn3=1021199 filePath=\\Device\\HarddiskVolume1\\Windows deviceCustomDate1=2016-01-18 23:41:07 cn1Label=ParentProcessId";
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
        assertEquals("processFileMd5", "e1864a55d5ccb76af4bf7a0ae16279ba", out.get("processFileMd5").get(0));
        assertEquals("processFilePath", "\\Device\\HarddiskVolume1\\Windows\\iissvr.exe", out.get("processFilePath").get(0));
        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("externalLogSourceType", "cef-FalconHost-DetectionSummaryEvent", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceUserName", "IIS1$", out.get("deviceUserName").get(0));
        assertEquals("processName", "iissvr.exe", out.get("processName").get(0));
        assertEquals("startTime", 1453160467000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "TheNarrowSea", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
    }

    @Test
    public void testWindowsSnareCEFAD4672ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 11:12:52 xxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4672|Special privileges assigned to new logon|Low| eventId=1515506328 externalId=4672 msg=Subject:   Security ID:  S-1-5-18   Account Name:  xxxxx   Account Domain:  xxxxx   Logon ID:  0xf621a463    Privileges:  SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeEnableDelegationPrivilege     User: xxxxx\\\\XXXXX     ComputerName: XXXXX.jpmchase.net categorySignificance=/Normal categoryBehavior=/Authorization/Add categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438186370817 cat=Security deviceSeverity=4 rt=1438186368000 dhost=xxxxx.jpmchase.net dntdom=xxxxx duser=xxxxx duid=0xf621a463 dpriv=SeSecurityPrivilege cs2=Special Logon cs3=Microsoft-Windows-Security-Auditing cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxx.jpmchase.net agt=10.1.1.92 agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.0 atz=Asia/Hong_Kong aid=3zG27zkkBABCBDMbAPl6tpw\\=\\= at=syslog dvchost=xxxxx.jpmchase.net deviceNtDomain=xxxxx dtz=Asia/Hong_Kong _cefVer=0.1 ad.ExtraParam0=SeBackupPrivilege ad.ExtraParam3=SeSystemEnvironmentPrivilege ad.ExtraParam4=SeLoadDriverPrivilege ad.ExtraParam1=SeRestorePrivilege ad.ExtraParam2=SeTakeOwnershipPrivilege ad.ExtraParam5=SeImpersonatePrivilege ad.ExtraParam6=SeEnableDelegationPrivilege";
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
        assertEquals("privileges", "SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeEnableDelegationPrivilege", out.get("privileges").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxx", out.get("deviceHostName").get(0));
        assertEquals("destinationUserName", "xxxxx", out.get("destinationUserName").get(0));
        assertEquals("startTime", 1438186370817L, out.get("startTime").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("destinationLogonID", "0xf621a463", out.get("destinationLogonID").get(0));
        assertEquals("cefEventName", "Special privileges assigned to new logon", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "xxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4672-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "xxxxx", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-18", out.get("destinationSecurityID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }
    @Test
    public void testWindowsSnareCEFAD4673ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 11:11:31 xxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4673|A privileged service was called|Medium| eventId=2293856749 externalId=4673 msg=Subject:   Security ID:  S-1-5-18   Account Name:  xxxxx   Account Domain:  xxxxx   Logon ID:  0x3e7    Service:   Server: NT Local Security Authority / Authentication Service   Service Name: LsaRegisterLogonProcess()    Process:   Process ID: 0x260   Process Name: C:\\\\Windows\\\\System32\\\\lsass.exe    Service Request Information:   Privileges:  SeTcbPrivilege     User: xxxxx\\\\xxxxx     ComputerName: xxxxx.EXAMPLE.COM categorySignificance=/Informational/Warning categoryBehavior=/Execute/Query categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Attempt categoryObject=/Host/Application art=1438186289630 cat=Security deviceSeverity=4 rt=1438182688000 dhost=xxxxx.EXAMPLE.COM dntdom=xxxxx duser=xxxxx duid=0x3e7 cs2=Sensitive Privilege Use cs3=Microsoft-Windows-Security-Auditing cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxx.jpmchase.net agt=10.1.1.61 agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.0 atz=US/Eastern aid=3qS8M50kBABCBEvEWU65jZg\\=\\= at=syslog dvchost=xxxxx.EXAMPLE.COM deviceNtDomain=xxxxx dtz=US/Eastern _cefVer=0.1 ad.Process:Process_,ID=0x260 ad.Service:Service_,Name=LsaRegisterLogonProcess() ad.Process:Process_,Name=C:\\\\Windows\\\\System32\\\\lsass.exe ad.Service:Server=NT Local Security Authority / Authentication Service ad.Service_,Reque7CZ5oA_~_~ion:Privileges=SeTcbPrivilege";
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
        assertEquals("sourceProcessName", "C:\\\\Windows\\\\System32\\\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("destinationProcessName", "LsaRegisterLogonProcess()", out.get("destinationProcessName").get(0));
        assertEquals("privileges", "SeTcbPrivilege", out.get("privileges").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("destinationServiceServer", "NT Local Security Authority / Authentication Service", out.get("destinationServiceServer").get(0));
        assertEquals("deviceHostName", "xxxxx", out.get("deviceHostName").get(0));
        assertEquals("startTime", 1438186289630L, out.get("startTime").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessID", "0x260", out.get("sourceProcessID").get(0));
        assertEquals("cefEventName", "A privileged service was called", out.get("cefEventName").get(0));
        assertEquals("sourceUserName", "xxxxx", out.get("sourceUserName").get(0));
        assertEquals("deviceNameOrIp", "xxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4673-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceNtDomain", "xxxxx", out.get("sourceNtDomain").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testWindowsSnareCEFAD4674ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 11:13:02 xxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4674|An operation was attempted on a privileged object|Low| eventId=409141782 externalId=4674 msg=Subject:   Security ID:  S-1-5-21-1292428093-484763869-725345543-500   Account Name:  xxxxx   Account Domain:  xxxxx   Logon ID:  0x2eb3da    Object:   Object Server: Security   Object Type: -   Object Name: -   Object Handle: 0x7c    Process Information:   Process ID: 0x2158   Process Name: C:\\\\Windows\\\\SysWOW64\\\\HOSTNAME.EXE    Requested Operation:   Desired Access: 983103   Privileges:  SeTakeOwnershipPrivilege     User: xxxxx\\\\xxxxx     ComputerName: xxxxx.EXAMPLE.COM categorySignificance=/Informational/Error categoryBehavior=/Authorization categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Failure categoryObject=/Host/Operating System art=1438186382011 cat=Security deviceSeverity=4 rt=1438186378000 dhost=xxxxx.EXAMPLE.COM dntdom=RB duser=xxxxx duid=0x2eb3da dproc=C:\\\\Windows\\\\SysWOW64\\\\HOSTNAME.EXE dpriv=SeTakeOwnershipPrivilege fname=- fileId=0x7c fileType=- cs2=Sensitive Privilege Use cs3=0x2158 cs1Label=Accesses cs2Label=EventlogCategory cs3Label=Process ID cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count c6a2Label=Source IPv6 Address ahost=xxxxx.jpmchase.net agt=10.1.1.64 agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.5.7132.0 atz=America/New_York aid=3uR0K4kkBABCByTo4QDBegA\\=\\= at=syslog dvchost=xxxxx.EXAMPLE.COM deviceNtDomain=RB dtz=America/New_York _cefVer=0.1 ad.Object:Object_,Server=Security ad.Requested_,Operation:Desired_,Access=983103";
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
        assertEquals("destinationObjectName", "-", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "Security", out.get("destinationObjectServer").get(0));
        assertEquals("sourceProcessName", "C:\\\\Windows\\\\SysWOW64\\\\HOSTNAME.EXE", out.get("sourceProcessName").get(0));
        assertEquals("privileges", "SeTakeOwnershipPrivilege", out.get("privileges").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("destinationObjectType", "-", out.get("destinationObjectType").get(0));
        assertEquals("deviceHostName", "xxxxx", out.get("deviceHostName").get(0));
        assertEquals("startTime", 1438186382011L, out.get("startTime").get(0));
        assertEquals("sourceLogonID", "0x2eb3da", out.get("sourceLogonID").get(0));
        assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
        assertEquals("destinationObjectHandle", "0x7c", out.get("destinationObjectHandle").get(0));
        assertEquals("desiredAccess", "983103", out.get("desiredAccess").get(0));
        assertEquals("sourceProcessID", "0x2158", out.get("sourceProcessID").get(0));
        assertEquals("sourceSecurityID", "S-1-5-21-1292428093-484763869-725345543-500", out.get("sourceSecurityID").get(0));
        assertEquals("cefEventName", "An operation was attempted on a privileged object", out.get("cefEventName").get(0));
        assertEquals("sourceUserName", "xxxxx", out.get("sourceUserName").get(0));
        assertEquals("deviceNameOrIp", "xxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4674-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceNtDomain", "xxxxx", out.get("sourceNtDomain").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }
    @Test
    public void testWindowsSnareCEFAD4768ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 11:12:57 xxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4768|A Kerberos authentication ticket (TGT) was requested|Low| eventId=1326189694 externalId=4768 msg=Account Information:   Account Name:  xxxxx   Supplied Realm Name: xxxxx.EXAMPLE.COM   User ID:   S-1-0-0    Service Information:   Service Name:  krbtgt/xxxxx.EXAMPLE.COM   Service ID:  S-1-0-0    Network Information:   Client Address:  ::ffff:10.1.1.48   Client Port:  3879    Additional Information:   Ticket Options:  0x40810010   Result Code:  0x6   Ticket Encryption Type: 0xffffffff   Pre-Authentication Type: -    Certificate Information:   Certificate Issuer Name:     Certificate Serial Number:    Certificate Thumbprint:      Certificate information is only provided if a certificate was used for pre-authentication.    Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.     User: xxxxx.EXAMPLE.COM\\\\xxxxx     ComputerName: xxxxx.EXAMPLE.COM categorySignificance=/Normal categoryBehavior=/Authentication/Verify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438186376876 cat=Security deviceSeverity=4 rt=1438193573000 spt=3879 dhost=xxxxx.EXAMPLE.COM dntdom=xxxxx.EXAMPLE.COM duser=xxxxx destinationServiceName=krbtgt/xxxxx.EXAMPLE.COM cs2=Kerberos Authentication Service cs3=::ffff:10.1.1.48 cs4=0x6 cs5=- c6a2=0:0:0:0:0:ffff:: cs1Label=Accesses cs2Label=EventlogCategory cs3Label=Client Address cs4Label=Result Code cs5Label=Pre-Authentication Type cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count c6a2Label=Source IPv6 Address ahost=xxxxx.jpmchase.net agt=10.1.1.92 agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.0 atz=Asia/Hong_Kong aid=3fUaMzkkBABCA9g09+7Z";
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
        assertEquals("ticketEncryptionType", "0xffffffff", out.get("ticketEncryptionType").get(0));
        assertEquals("sourcePort", 3879, out.get("sourcePort").get(0));
        assertEquals("destinationProcessName", "krbtgt", out.get("destinationProcessName").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxx", out.get("deviceHostName").get(0));
        assertEquals("destinationServiceName", "krbtgt/xxxxx.EXAMPLE.COM", out.get("destinationServiceName").get(0));
        assertEquals("startTime", 1438186376876L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "xxxxx.EXAMPLE.COM", out.get("destinationNameOrIp").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationDnsDomain", "example.com", out.get("destinationDnsDomain").get(0));
        assertEquals("preAuthenticationType", "-", out.get("preAuthenticationType").get(0));
        assertEquals("sourceAddress", "10.1.1.48", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "xxxxx", out.get("destinationHostName").get(0));
        assertEquals("status", "0x6", out.get("status").get(0));
        assertEquals("destinationServiceSecurityID", "S-1-0-0", out.get("destinationServiceSecurityID").get(0));
        assertEquals("destinationUserName", "xxxxx", out.get("destinationUserName").get(0));
        assertEquals("cefEventName", "A Kerberos authentication ticket (TGT) was requested", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "xxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4768-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "xxxxx.EXAMPLE.COM", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-0-0", out.get("destinationSecurityID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testWindowsSnareCEFAD4672Sample2ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Aug  4 13:35:30 xxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4672|Special privileges assigned to new logon|Low| eventId=1901795439 externalId=4672 msg=Subject:   Security ID:  S-1-5-18   Account Name:  SYSTEM   Account Domain:  NT AUTHORITY   Logon ID:  0x3e7    Privileges:  SeAssignPrimaryTokenPrivilege     SeTcbPrivilege     SeSecurityPrivilege     SeTakeOwnershipPrivilege     SeLoadDriverPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeDebugPrivilege     SeAuditPrivilege     SeSystemEnvironmentPrivilege     SeImpersonatePrivilege     User: NT AUTHORITY\\\\SYSTEM     ComputerName: xxxxxxxxxxxx.xxxxxx.jpmchase.net categorySignificance=/Normal categoryBehavior=/Authorization/Add categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438713330191 cat=Security deviceSeverity=4 rt=1438709727000 dhost=xxxxxxxxxxxx.xxxxxx.jpmchase.net dntdom=NT AUTHORITY duser=SYSTEM duid=0x3e7 dpriv=SeAssignPrimaryTokenPrivilege cs2=Special Logon cs3=Microsoft-Windows-Security-Auditing cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxxxxxx.xxx.xx.jpmchase.net agt=xxx.xxx.xx.xx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.0 atz=US/Eastern aid=3mTr+5kkBABCBFAZn0olEsA\\=\\= at=syslog dvchost=xxxxxxxxxxxx.xxxxxx.jpmchase.net deviceNtDomain=NT AUTHORITY dtz=US/Eastern _cefVer=0.1 ad.ExtraParam0=SeTcbPrivilege ad.ExtraParam3=SeLoadDriverPrivilege ad.ExtraParam4=SeBackupPrivilege ad.ExtraParam1=SeSecurityPrivilege ad.ExtraParam2=SeTakeOwnershipPrivilege ad.ExtraParam7=SeAuditPrivilege ad.ExtraParam8=SeSystemEnvironmentPrivilege ad.ExtraParam5=SeRestorePrivilege ad.ExtraParam6=SeDebugPrivilege ad.ExtraParam9=SeImpersonatePrivilege";
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
        assertEquals("privileges", "SeAssignPrimaryTokenPrivilege     SeTcbPrivilege     SeSecurityPrivilege     SeTakeOwnershipPrivilege     SeLoadDriverPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeDebugPrivilege     SeAuditPrivilege     SeSystemEnvironmentPrivilege     SeImpersonatePrivilege", out.get("privileges").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxx", out.get("deviceHostName").get(0));
        assertEquals("destinationUserName", "SYSTEM".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("startTime", 1438713330191L, out.get("startTime").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("destinationLogonID", "0x3e7", out.get("destinationLogonID").get(0));
        assertEquals("cefEventName", "Special privileges assigned to new logon", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "xxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4672-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "NT AUTHORITY", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-18", out.get("destinationSecurityID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testWindowsSnareCEFAD4769EventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 15:32:30 xxxxxxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4769|A Kerberos service ticket was requested|Low| eventId=2023657997 externalId=4769 msg=Account Information:   Account Name:  xxxxxxxxxxxxxx.AD.EXAMPLE.COM   Account Domain:  xxxxxx.AD.EXAMPLE.COM   Logon GUID:  {40FDCD64-17FB-0156-F389-CCE5AF5C4D7C}    Service Information:   Service Name:  xxxxxxxxxx   Service ID:  S-1-5-21-1214440339-1979792683-682003330-2068973    Network Information:   Client Address:  ::ffff:10.10.200.123   Client Port:  57811    Additional Information:   Ticket Options:  0x40800000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined  categorySignificance=/Normal categoryBehavior=/Authentication/Verify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Attempt categoryObject=/Host/Operating System art=1438201949706 cat=Security deviceSeverity=4 rt=1438201948000 spt=57811 dhost=xxxxxxxxx.xxxxxx.AD.EXAMPLE.COM dntdom=xxxxxx.AD.EXAMPLE.COM duser=xxxxxxxxxxxxxx.AD.EXAMPLE.COM destinationServiceName=xxxxxxxxxx cs2=Kerberos Service Ticket Operations cs3=::ffff:10.10.200.123 cs4=0x0 cs6={40FDCD64-17FB-0156-F389-CCE5AF5C4D7C} c6a2=0:0:0:0:0:ffff:xxxx:xxxx cs1Label=Accesses cs2Label=EventlogCategory cs3Label=Client Address cs4Label=Reason or Error Code cs5Label=Authentication Package Name cs6Label=Logon GUID cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count c6a2Label=Source IPv6 Address ahost=xxxxxxxxx.xxx.xx.jpmchase.net agt=xxx.xxx.xx.xx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.5.7132.0 atz=US/Eastern aid=3tWHf4kkBABCBFRQtghAiXw\\=\\= at=syslog dvchost=xxxxxxxxx.xxxxxx.AD.EXAMPLE.COM dtz=xx/xxxxxxx _cefVer=0.1 ad.ExtraParam0=This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested. ad.Service_,Information:Service_,ID=S-1-5-21-1214440339-1979792683-682003330-2068973 ad.Additional_,InMbHgGQ_~_~Ticket_,Options=0x40800000 ad.Additional_,Inc1+cZg_~_~sited_,Services=- ad.ExtraParam1=This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. ad.ExtraParam2=Ticket options, encryption types, and failure codes are defined in RFC 4120. ad.Additional_,InagWMqg_~_~ncryption_,Type=0x12 ad.message=Account Information:   Account Name:  xxxxxxxxxxxxxx.AD.EXAMPLE.COM   Account Domain:  xxxxxx.AD.EXAMPLE.COM   Logon GUID:  {40FDCD64-17FB-0156-F389-CCE5AF5C4D7C}    Service Information:   Service Name:  xxxxxxxxxx   Service ID:  S-1-5-21-1214440339-1979792683-682003330-2068973    Network Information:   Client Address:  ::ffff:10.10.200.123   Client Port:  57811    Additional Information:   Ticket Options:  0x40800000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.     User: xxxxxx.AD.EXAMPLE.COM\\\\xxxxxxxxxxxxxx.AD.EXAMPLE.COM     ComputerName: xxxxxxxxx.xxxxxx.AD.EXAMPLE.COM";

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
        assertEquals("destinationLogonGUID", "{40FDCD64-17FB-0156-F389-CCE5AF5C4D7C}", out.get("destinationLogonGUID").get(0));
        assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
        assertEquals("status", "0x0", out.get("status").get(0));
        assertEquals("sourcePort", 57811, out.get("sourcePort").get(0));
        assertEquals("destinationServiceSecurityID", "S-1-5-21-1214440339-1979792683-682003330-2068973", out.get("destinationServiceSecurityID").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("deviceHostName", "xxxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("destinationServiceName", "xxxxxxxxxx", out.get("destinationServiceName").get(0));
        assertEquals("destinationUserName", "xxxxxxxxxxxxxx.AD.EXAMPLE.COM".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("startTime", 1438201949706L, out.get("startTime").get(0));
        assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
        assertEquals("cefEventName", "A Kerberos service ticket was requested", out.get("cefEventName").get(0));
        assertEquals("deviceNameOrIp", "xxxxxxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("ticketOptions", "0x40800000", out.get("ticketOptions").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Security-4769-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "xxxxxx.AD.EXAMPLE.COM", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "10.10.200.123", out.get("sourceAddress").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

    }

    @Test
    public void testWindowsSnareCEFAD4648ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<13>Jul 29 11:12:57 xxxxx CEF: 0|IntersectAlliance|Snare||Microsoft-Windows-Security-Auditing:4648|A logon was attempted using explicit credentials|Low| eventId=1484636606 externalId=4648 msg=Subject:   Security ID:  S-1-5-18   Account Name:  xxxxx   Account Domain:  XXXXX   Logon ID:  0x3e7   Logon GUID:  {F1DBA820-3067-06F0-E7C4-5E95828C95C7}    Account Whose Credentials Were Used:   Account Name:  xxxxx   Account Domain:  XXXX   Logon GUID:  {00000000-0000-0000-0000-000000000000}    Target Server:   Target Server Name: localhost   Additional Information: localhost    Process Information:   Process ID:  0x2c4   Process Name:  C:\\\\Windows\\\\System32\\\\lsass.exe    Network Information:   Network Address: 10.1.1.18   Port:   56507    This event is generated when a process attempts to log on an account by explicitly specifying that accountï¿½s credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command.     User: XXXXX\\\\xxxxx     ComputerName: xxxxx.EXAMPLE.COM categorySignificance=/Normal categoryBehavior=/Authentication/Verify categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Attempt categoryObject=/Host/Operating System art=1438186377016 cat=Security deviceSeverity=4 rt=1438186374000 src=10.1.1.18 sourceZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/155.0.0.0-162.255.255.255 (ARIN) dhost=xxxxx.EXAMPLE.COM dntdom=XXXXX duser=XXXXX duid=0x3e7 dproc=C:\\\\Windows\\\\System32\\\\lsass.exe cs2=Logon cs3=0x2c4 cs6={00000000-0000-0000-0000-000000000000} cs1Label=Accesses cs2Label=EventlogCategory cs3Label=Process ID cs4Label=Reason or Error Code cs5Label=Authentication Package Name cs6Label=Logon GUID cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxx.jpmchase.net agt=10.1.1.186 agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.";
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
        assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationHostName", "localhost", out.get("destinationHostName").get(0));
        assertEquals("sourcePort", 56507, out.get("sourcePort").get(0));
        assertEquals("sourceProcessName", "C:\\\\Windows\\\\System32\\\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceNameOrIp", "10.1.1.18", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxx", out.get("deviceHostName").get(0));
        assertEquals("destinationUserName", "xxxxx", out.get("destinationUserName").get(0));
        assertEquals("startTime", 1438186377016L, out.get("startTime").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("destinationNameOrIp", "localhost", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
        assertEquals("sourceUserName", "xxxxx", out.get("sourceUserName").get(0));
        assertEquals("deviceNameOrIp", "xxxxx", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-cef", out.get("externalLogSourceType").get(0));
        assertEquals("destinationNtDomain", "XXXX", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "10.1.1.18", out.get("sourceAddress").get(0));
        assertEquals("sourceNtDomain", "XXXXX", out.get("sourceNtDomain").get(0));
        assertEquals("sourceLogonGUID", "{F1DBA820-3067-06F0-E7C4-5E95828C95C7}", out.get("sourceLogonGUID").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));

        // need untruncated to see whether eventOutcome is there or not

    }

    //
    @Test
    public void testWindowsSnareCEFAD__ToIAMMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "MSWinEventLog\t0\tSecurity\t1280394\tWed Feb 17 15:33:43 2016\t4756\tMicrosoft-Windows-Security-Auditing\tE8SEC\\new_universal_security_grp\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSecurity Group Management\t\tA member was added to a security-enabled universal group.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0xeff5912    Member:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  CN=jyria,CN=Users,DC=e8sec,DC=lab    Group:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1138   Account Name:  new_universal_security_grp   Account Domain:  E8SEC    Additional Information:   Privileges:  -\t1280393 ";
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


        // need untruncated to see whether eventOutcome is there or not

    }




    @Test
    public void testTaniumFromSplunkRunningAutoRunProgramDetailsToHostProcessMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg ="{ \"Machine-Info\": { \"Computer-Name\": \"XXXXXXXX.XXXXXX.XX.EXAMPLE.COM\", \"Computer-Serial-Number\": \"2XRCSL1\", \"Client-IP-Address\": \"10.11.12.13\" }, \"Count\": \"1\", \"AutoRun-Program-Details\": { \"Entry-Location\": \"HKLM\\\\SOFTWARE\\\\Wow6432Node\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\", \"Entry\": \"SoundMAXPnP\", \"Category\": \"Logon\", \"Publisher\": \"Analog Devices, Inc.\", \"Image-Path\": \"c:\\\\program files (x86)\\\\analog devices\\\\core\\\\smax4pnp.exe\", \"Version\": \"6.1.7200.178\", \"Launch-String\": \"C:\\\\Program Files (x86)\\\\Analog Devices\\\\Core\\\\smax4pnp.exe\", \"MD5\": \"ec87fe6fc28c21ab9f41112234008522\", \"SHA-1\": \"bf66e188b00c7d62b135c1b09d500d966938e749\", \"SHA-256\": \"ba353ee7afd33aeb483a31f55fa153b41d242954fe56f5a353696b173c97c97b\" }, \"Metadata\": { \"QueryText\": \"Select Machine Info, AutoRun Program Details from iSpace Workstations\", \"Requester\": \"SYSTEM\", \"JobGuid\": \"dfe4ec5b-86ce-4c71-ab76-297f21c57cf2\", \"Timestamp\": \"2015-08-04 00:05:01Z\" } }";

        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium_feed");
        headers.put("taniumQuestion", "AutoRun-Program-Details");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "tanium_feed", out.get("logCollectionCategory").get(0));
        assertEquals("processFileMd5", "ec87fe6fc28c21ab9f41112234008522", out.get("processFileMd5").get(0));
        assertEquals("jobCmd", "C:\\Program Files (x86)\\Analog Devices\\Core\\smax4pnp.exe", out.get("jobCmd").get(0));
        assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
        assertEquals("deviceSerialNumber", "2XRCSL1", out.get("deviceSerialNumber").get(0));
        assertEquals("deviceHostName", "xxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("jobName", "SoundMAXPnP", out.get("jobName").get(0));
        assertEquals("startTime", 1438646701000L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "xxxxxx.xx.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("deviceNameOrIp", "XXXXXXXX.XXXXXX.XX.EXAMPLE.COM", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "tanium-AutoRun-Program-Details", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
        assertEquals("processFilePath", "c:\\program files (x86)\\analog devices\\core\\smax4pnp.exe", out.get("processFilePath").get(0));
        assertEquals("parserOutFormat", "HostJobMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceAddress", "10.11.12.13", out.get("deviceAddress").get(0));
    }

    @Test
    public void testTaniumFromSplunkRunningProcessesWithMd5HashToHostProcessMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "{ \"Machine-Info\": { \"Computer-Name\": \"XXXXXXXXXXXXXXX.XX.XX.EXAMPLE.COM\", \"Computer-Serial-Number\": \"2UA12102DF\", \"Client-IP-Address\": \"10.11.12.13\" }, \"Count\": \"1\", \"Running-Processes-with-MD5-Hash\": { \"Path\": \"c:\\\\windows\\\\sysnative\\\\wininit.exe\", \"MD5-Hash\": \"94355c28c1970635a31b3fe52eb7ceba\" }, \"Metadata\": { \"QueryText\": \"Select Machine Info, Running Processes with MD5 Hash from all machines\", \"Requester\": \"SYSTEM\", \"JobGuid\": \"a5952b6a-bb73-4d50-9229-ae618921ddb3\", \"Timestamp\": \"2015-08-04 01:05:13Z\" } }";

        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("taniumQuestion", "Running-Processes-with-MD5-Hash");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        output = instance.parse(avroEvent);
        output = instance.parse(avroEvent);
        output = instance.parse(avroEvent);

        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "tanium", out.get("logCollectionCategory").get(0));
        assertEquals("startTime", 1438650313000L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "xx.xx.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("deviceNameOrIp", "XXXXXXXXXXXXXXX.XX.XX.EXAMPLE.COM", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceSerialNumber", "2UA12102DF", out.get("deviceSerialNumber").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "tanium-Running-Processes-with-MD5-Hash", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxxxxxxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("processFilePath", "c:\\windows\\sysnative\\wininit.exe", out.get("processFilePath").get(0));
        assertEquals("processName", "wininit.exe", out.get("processName").get(0));
        assertEquals("deviceAddress", "10.11.12.13", out.get("deviceAddress").get(0));
        assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));

    }


    @Test
    public void testTaniumFromSplunkRunningProcessesWithMd5HashToHostProcessMefEventParserUnix() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "{ \"Machine-Info\": { \"Computer-Name\": \"XXXXXXXXXXXXXXX.XX.XX.EXAMPLE.COM\", \"Computer-Serial-Number\": \"2UA12102DF\", \"Client-IP-Address\": \"10.11.12.13\" }, \"Count\": \"1\", \"Running-Processes-with-MD5-Hash\": { \"Path\": \"/usr/bin/ssh\", \"MD5-Hash\": \"94355c28c1970635a31b3fe52eb7ceba\" }, \"Metadata\": { \"QueryText\": \"Select Machine Info, Running Processes with MD5 Hash from all machines\", \"Requester\": \"SYSTEM\", \"JobGuid\": \"a5952b6a-bb73-4d50-9229-ae618921ddb3\", \"Timestamp\": \"2015-08-04 01:05:13Z\" } }";

        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium");
        headers.put("taniumQuestion", "Running-Processes-with-MD5-Hash");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "tanium", out.get("logCollectionCategory").get(0));
        assertEquals("deviceSerialNumber", "2UA12102DF", out.get("deviceSerialNumber").get(0));
        assertEquals("startTime", 1438650313000L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "xx.xx.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("deviceNameOrIp", "XXXXXXXXXXXXXXX.XX.XX.EXAMPLE.COM", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "tanium-Running-Processes-with-MD5-Hash", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "xxxxxxxxxxxxxxx", out.get("deviceHostName").get(0));
        assertEquals("processFilePath", "/usr/bin/ssh", out.get("processFilePath").get(0));
        assertEquals("processName", "ssh", out.get("processName").get(0));
        assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceAddress", "10.11.12.13", out.get("deviceAddress").get(0));

    }

    @Test
    public void testTaniumFromTaniumRunningProcessesWithMd5HashInCsvToHostProcessMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "HOSTNAME.NAEAST.AD.EXAMPLE.COM,0000000,172.29.182.246,c:\\program files (x86)\\symantec\\symantec endpoint protection\\12.1.3001.165.105\\bin\\ccsvchst.exe,94e826672988fbce0979f7800eb770c9,1";

        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium_csv");
        headers.put("taniumQuestion", "csv-Running-Processes-with-MD5-Hash");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "tanium_csv", out.get("logCollectionCategory").get(0));
        assertEquals("processFileMd5", "94e826672988fbce0979f7800eb770c9", out.get("processFileMd5").get(0));
        assertEquals("deviceAddress", "172.29.182.246", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
        assertEquals("deviceSerialNumber", "0000000", out.get("deviceSerialNumber").get(0));
        assertEquals("deviceHostName", "hostname", out.get("deviceHostName").get(0));
        assertEquals("startTime", 1384693669604L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "naeast.ad.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("deviceNameOrIp", "HOSTNAME.NAEAST.AD.EXAMPLE.COM", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "tanium-Running-Processes-with-MD5-Hash-csv", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("processFilePath", "c:\\program files (x86)\\symantec\\symantec endpoint protection\\12.1.3001.165.105\\bin\\ccsvchst.exe", out.get("processFilePath").get(0));
        assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testTaniumFromTaniumListenPortsWithMD5HashInCsvToHostPortMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "LD0510TGJ.NAEAST.AD.EXAMPLE.COM,CND0510TGJ,10.169.24.60,svchost.exe,6f68f63794097e54f36474ed4384b759,Host Process for Windows Services,UDP,127.0.0.1,62845,1";

        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium_csv");
        headers.put("taniumQuestion", "csv-Listen-Ports-with-MD5-Hash");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "tanium_csv", out.get("logCollectionCategory").get(0));
        assertEquals("processListenAddress", "127.0.0.1", out.get("processListenAddress").get(0));
        assertEquals("processName", "Host Process for Windows Services", out.get("processName").get(0));
        assertEquals("processFileMd5", "6f68f63794097e54f36474ed4384b759", out.get("processFileMd5").get(0));
        assertEquals("deviceAddress", "10.169.24.60", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HostPortMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "ld0510tgj", out.get("deviceHostName").get(0));
        assertEquals("startTime", 1384693669604L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "naeast.ad.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("deviceNameOrIp", "LD0510TGJ.NAEAST.AD.EXAMPLE.COM", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "tanium-Listen-Ports-with-MD5-Hash-csv", out.get("externalLogSourceType").get(0));
        assertEquals("processListenPort", 62845, out.get("processListenPort").get(0));
        assertEquals("processFilePath", "svchost.exe", out.get("processFilePath").get(0));
        assertEquals("parserOutFormat", "HostPortMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testTaniumFromTaniumAutoRunProgramDetailsInCsvToHostPortMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "WR8WX21X.EMEA.AD.EXAMPLE.COM,R8WX21X,169.100.10.210,Task Scheduler,\\ProfileBackupScheduler-u169820,Tasks,JPMorgan Chase & Co.,c:\\program files (x86)\\profile-backup-tool]protool.exe,1.1.0.0,\"\"\"C:\\Program Files(x86)\\Profile-Backup-Tool\\ProTool.exe\"\" /s /b\",cc036b7872d43fff220b53ad0a0c4047,30329289be984a5dc29c984d149c157edfb130ed,b2aeb47b41568a1bda4abf2dcb5e542414c101299e23d43f2c25f2d7279b2ed8,1";

        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium_csv");
        headers.put("taniumQuestion", "csv-AutoRun-Program-Details");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("logCollectionCategory", "tanium_csv", out.get("logCollectionCategory").get(0));
        assertEquals("processFileMd5", "cc036b7872d43fff220b53ad0a0c4047", out.get("processFileMd5").get(0));
        assertEquals("jobCmd", "\"C:\\Program Files(x86)\\Profile-Backup-Tool\\ProTool.exe\" /s /b", out.get("jobCmd").get(0));
        assertEquals("deviceAddress", "169.100.10.210", out.get("deviceAddress").get(0));
        assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "wr8wx21x", out.get("deviceHostName").get(0));
        assertEquals("deviceSerialNumber", "R8WX21X", out.get("deviceSerialNumber").get(0));
        assertEquals("jobName", "\\ProfileBackupScheduler-u169820", out.get("jobName").get(0));
        assertEquals("startTime", 1384693669604L, out.get("startTime").get(0));
        assertEquals("deviceDnsDomain", "emea.ad.example.com", out.get("deviceDnsDomain").get(0));
        assertEquals("deviceNameOrIp", "WR8WX21X.EMEA.AD.EXAMPLE.COM", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("externalLogSourceType", "tanium-AutoRun-Program-Details-csv", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("processFilePath", "c:\\program files (x86)\\profile-backup-tool]protool.exe", out.get("processFilePath").get(0));
        assertEquals("parserOutFormat", "HostJobMef", out.get("parserOutFormat").get(0));
    }

    @Test
    public void testMcAfeeWebSecToWebProxyMefEventParserEmptyReqLine() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<30>Jun  8 16:18:17 sscwcgconpr01 mwg: time_stamp=[08/Jun/2016:16:18:17 -0400] auth_user=- src_ip=172.22.38.136 server_ip=255.255.255.255 time_taken=2 status_code=400 cache_status=TCP_MISS req_line=  categories=- rep_level=- media_type=- bytes_to_client=2529 bytes_from_client=68 user_agent=- referrer=- virus_name=- gam_probability=0 block_res=0 geolocation=- application_name=- md5=-";

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

        assertEquals("reason", "-", out.get("reason").get(0));
        assertEquals("destinationAddress", "255.255.255.255", out.get("destinationAddress").get(0));
        assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
        assertEquals("receiptTime", 1465402697000L, out.get("receiptTime").get(0));
        assertEquals("requestReferer", "-", out.get("requestReferer").get(0));
        assertEquals("devicePolicyAction", "0", out.get("devicePolicyAction").get(0));
        assertEquals("startTime", 1465417097000L, out.get("startTime").get(0));
        assertEquals("deviceHostName", "sscwcgconpr01", out.get("deviceHostName").get(0));
        assertEquals("bytesIn", 2529, out.get("bytesIn").get(0));
        assertEquals("bytesOut", 68, out.get("bytesOut").get(0));
        assertEquals("sourceAddress", "172.22.38.136", out.get("sourceAddress").get(0));
        assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("cefSignatureId", "400", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceEventCategory", "-/-", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "-", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "-", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "sscwcgconpr01", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "172.22.38.136", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
    }
        @Test
    public void testMcAfeeWebSecLEEFToWebProxyMefEventParser() throws IOException, Exception {

            AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
            Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
            String msg = "<30>Jun 27 21:38:11 mahproxy1 mwg: LEEF:1.0|McAfee|Web Gateway|7.5.2.7.0|0|devTime=1467063491000|usrName=(BypassAuthentication-Site)|realm=|src=10.9.66.102|srcPort=52682|dst=72.21.81.200|dstPort=443|blockReason=|srcPreNAT=10.9.66.102|srcPreNATPort=52682|dstPreNAT=153.2.246.33|dstPreNATPort=8080|dstPostNAT=72.21.81.200|srcPostNAT=72.21.81.200|srcPostNATPort=48239|dstPostNATPort=443|srcBytes=700|dstBytes=8443|totalBytes=9143|srcBytesPostNAT=700|dstBytesPostNAT=8404|totalBytesPostNAT=9104|httpStatus=200|cacheStatus=TCP_MISS|timeTaken=119930|contentType=|ensuredType=application/x-empty|urlCategories=Business, Software/Hardware|reputation=Minimal Risk/-4|policy=Default|proto=https|method=CONNECT|url=https://iecvlist.microsoft.com|referer=|userAgent=|calCountryOrRegion=US|virusName=false|application= ";

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

            assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
            assertEquals("reason", "false", out.get("reason").get(0));
            assertEquals("sourcePort", 52682, out.get("sourcePort").get(0));
            assertEquals("destinationAddress", "72.21.81.200", out.get("destinationAddress").get(0));
            assertEquals("requestMethod", "CONNECT", out.get("requestMethod").get(0));
            assertEquals("destinationNameOrIp", "iecvlist.microsoft.com", out.get("destinationNameOrIp").get(0));
            assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
            assertEquals("receiptTime", 1467063491000L, out.get("receiptTime").get(0));
            assertEquals("requestReferer", "", out.get("requestReferer").get(0));
            assertEquals("devicePolicyAction", "Default", out.get("devicePolicyAction").get(0));
            assertEquals("applicationProtocol", "https", out.get("applicationProtocol").get(0));
            assertEquals("deviceHostName", "mahproxy1", out.get("deviceHostName").get(0));
            assertEquals("requestPath", "", out.get("requestPath").get(0));
            assertEquals("bytesOut", 8443, out.get("bytesOut").get(0));
            assertEquals("bytesIn", 700, out.get("bytesIn").get(0));
            assertEquals("destinationDnsDomain", "microsoft.com", out.get("destinationDnsDomain").get(0));
            assertEquals("sourceAddress", "10.9.66.102", out.get("sourceAddress").get(0));
            assertEquals("sourceUserName", "(BypassAuthentication-Site)", out.get("sourceUserName").get(0));
            assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
            assertEquals("requestScheme", "https", out.get("requestScheme").get(0));
            assertEquals("destinationHostName", "iecvlist", out.get("destinationHostName").get(0));
            assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
            assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
            assertEquals("deviceEventCategory", "Business, Software/Hardware", out.get("deviceEventCategory").get(0));
            assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
            assertEquals("requestClientApplication", "", out.get("requestClientApplication").get(0));
            assertEquals("responseContentType", "", out.get("responseContentType").get(0));
            assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
            assertEquals("deviceNameOrIp", "mahproxy1", out.get("deviceNameOrIp").get(0));
            assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        }
        @Test
    public void testMcAfeeWebSecLEEFWithLocationToWebProxyMefEventParser() throws IOException, Exception {

            AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
            Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
            String msg = "<30>Jun 27 21:38:11 mahproxy1 mwg: LEEF:1.0|McAfee|Web Gateway|7.5.2.7.0|0|devTime=1467063491000|usrName=(BypassAuthentication-Site)|realm=|src=10.9.66.102|srcPort=52682|dst=72.21.81.200|dstPort=443|blockReason=|srcPreNAT=10.9.66.102|srcPreNATPort=52682|dstPreNAT=153.2.246.33|dstPreNATPort=8080|dstPostNAT=72.21.81.200|srcPostNAT=72.21.81.200|srcPostNATPort=48239|dstPostNATPort=443|srcBytes=700|dstBytes=8443|totalBytes=9143|srcBytesPostNAT=700|dstBytesPostNAT=8404|totalBytesPostNAT=9104|httpStatus=200|cacheStatus=TCP_MISS|timeTaken=119930|contentType=|ensuredType=application/x-empty|urlCategories=Business, Software/Hardware|reputation=Minimal Risk/-4|policy=Default|proto=https|method=CONNECT|url=https://iecvlist.microsoft.com|referer=|userAgent=Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405|location=htttp://google.com|calCountryOrRegion=US|virusName=false|application= ";

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

            assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
            assertEquals("reason", "false", out.get("reason").get(0));
            assertEquals("sourcePort", 52682, out.get("sourcePort").get(0));
            assertEquals("destinationAddress", "72.21.81.200", out.get("destinationAddress").get(0));
            assertEquals("requestMethod", "CONNECT", out.get("requestMethod").get(0));
            assertEquals("destinationNameOrIp", "iecvlist.microsoft.com", out.get("destinationNameOrIp").get(0));
            assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
            assertEquals("receiptTime", 1467063491000L, out.get("receiptTime").get(0));
            assertEquals("requestReferer", "", out.get("requestReferer").get(0));
            assertEquals("devicePolicyAction", "Default", out.get("devicePolicyAction").get(0));
            assertEquals("applicationProtocol", "https", out.get("applicationProtocol").get(0));
            assertEquals("deviceHostName", "mahproxy1", out.get("deviceHostName").get(0));
            assertEquals("requestPath", "", out.get("requestPath").get(0));
            assertEquals("bytesOut", 8443, out.get("bytesOut").get(0));
            assertEquals("bytesIn", 700, out.get("bytesIn").get(0));
            assertEquals("destinationDnsDomain", "microsoft.com", out.get("destinationDnsDomain").get(0));
            assertEquals("sourceAddress", "10.9.66.102", out.get("sourceAddress").get(0));
            assertEquals("sourceUserName", "(BypassAuthentication-Site)", out.get("sourceUserName").get(0));
            assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
            assertEquals("requestScheme", "https", out.get("requestScheme").get(0));
            assertEquals("destinationHostName", "iecvlist", out.get("destinationHostName").get(0));
            assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
            assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
            assertEquals("deviceEventCategory", "Business, Software/Hardware", out.get("deviceEventCategory").get(0));
            assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
            assertEquals("requestClientApplication", "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405", out.get("requestClientApplication").get(0));
            assertEquals("responseContentType", "", out.get("responseContentType").get(0));
            assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
            assertEquals("deviceNameOrIp", "mahproxy1", out.get("deviceNameOrIp").get(0));
            assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        }

    @Test
    public void testMcAfeeWebSecLEEFIDNAToWebProxyMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<30>Sep 20 17:05:02 mahproxy1 mwg: LEEF:1.0|McAfee|Web Gateway|7.5.2.7.0|0|devTime=1474391102000|usrName=wth0xww|realm=mahesds-vip.ups.com|src=10.246.198.202|srcPort=60030|dst=74.125.3.26|dstPort=443|blockReason=|srcPreNAT=10.246.198.202|srcPreNATPort=60030|dstPreNAT=153.2.246.33|dstPreNATPort=8080|dstPostNAT=74.125.3.26|srcPostNAT=74.125.3.26|srcPostNATPort=14167|dstPostNATPort=443|srcBytes=1139|dstBytes=10852|totalBytes=11991|srcBytesPostNAT=1139|dstBytesPostNAT=10813|totalBytesPostNAT=11952|httpStatus=200|cacheStatus=TCP_MISS|timeTaken=316|contentType=|ensuredType=application/x-empty|urlCategories=Streaming Media, Media Sharing|reputation=Minimal Risk/7|policy=Default|proto=https|method=CONNECT|url=https://méxico.googlevideo.com|referer=|userAgent=Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0|calCountryOrRegion=US|virusName=false|application=";

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

        assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
        assertEquals("reason", "false", out.get("reason").get(0));
        assertEquals("sourcePort", 60030, out.get("sourcePort").get(0));
        assertEquals("destinationAddress", "74.125.3.26", out.get("destinationAddress").get(0));
        assertEquals("requestMethod", "CONNECT", out.get("requestMethod").get(0));
        assertEquals("destinationNameOrIp", "méxico.googlevideo.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
        assertEquals("receiptTime", 1474391102000L, out.get("receiptTime").get(0));
        assertEquals("requestReferer", "", out.get("requestReferer").get(0));
        assertEquals("devicePolicyAction", "Default", out.get("devicePolicyAction").get(0));
        assertEquals("startTime", 1474391102000L, out.get("startTime").get(0));
        assertEquals("applicationProtocol", "https", out.get("applicationProtocol").get(0));
        assertEquals("deviceHostName", "mahproxy1", out.get("deviceHostName").get(0));
        assertEquals("requestPath", "", out.get("requestPath").get(0));
        assertEquals("bytesOut", 10852, out.get("bytesOut").get(0));
        assertEquals("bytesIn", 1139, out.get("bytesIn").get(0));
        assertEquals("destinationDnsDomain", "googlevideo.com", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "10.246.198.202", out.get("sourceAddress").get(0));
        assertEquals("sourceUserName", "wth0xww", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "https", out.get("requestScheme").get(0));
        assertEquals("destinationHostName", "xn--mxico-bsa", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceEventCategory", "Streaming Media, Media Sharing", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "mahproxy1", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "10.246.198.202", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));

    }    @Test
    public void testMcAfeeWebSecLEEFMultipleDashToWebProxyMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<30>Sep 20 17:05:02 mahproxy1 mwg: LEEF:1.0|McAfee|Web Gateway|7.5.2.7.0|0|devTime=1474391102000|usrName=wth0xww|realm=mahesds-vip.ups.com|src=10.246.198.202|srcPort=60030|dst=74.125.3.26|dstPort=443|blockReason=|srcPreNAT=10.246.198.202|srcPreNATPort=60030|dstPreNAT=153.2.246.33|dstPreNATPort=8080|dstPostNAT=74.125.3.26|srcPostNAT=74.125.3.26|srcPostNATPort=14167|dstPostNATPort=443|srcBytes=1139|dstBytes=10852|totalBytes=11991|srcBytesPostNAT=1139|dstBytesPostNAT=10813|totalBytesPostNAT=11952|httpStatus=200|cacheStatus=TCP_MISS|timeTaken=316|contentType=|ensuredType=application/x-empty|urlCategories=Streaming Media, Media Sharing|reputation=Minimal Risk/7|policy=Default|proto=https|method=CONNECT|url=https://r4---sn-q4fl6n7e.googlevideo.com|referer=|userAgent=Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0|calCountryOrRegion=US|virusName=false|application=";

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

        assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
        assertEquals("reason", "false", out.get("reason").get(0));
        assertEquals("sourcePort", 60030, out.get("sourcePort").get(0));
        assertEquals("destinationAddress", "74.125.3.26", out.get("destinationAddress").get(0));
        assertEquals("requestMethod", "CONNECT", out.get("requestMethod").get(0));
        assertEquals("destinationNameOrIp", "r4---sn-q4fl6n7e.googlevideo.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
        assertEquals("receiptTime", 1474391102000L, out.get("receiptTime").get(0));
        assertEquals("requestReferer", "", out.get("requestReferer").get(0));
        assertEquals("devicePolicyAction", "Default", out.get("devicePolicyAction").get(0));
        assertEquals("startTime", 1474391102000L, out.get("startTime").get(0));
        assertEquals("applicationProtocol", "https", out.get("applicationProtocol").get(0));
        assertEquals("deviceHostName", "mahproxy1", out.get("deviceHostName").get(0));
        assertEquals("requestPath", "", out.get("requestPath").get(0));
        assertEquals("bytesOut", 10852, out.get("bytesOut").get(0));
        assertEquals("bytesIn", 1139, out.get("bytesIn").get(0));
        assertEquals("destinationDnsDomain", "googlevideo.com", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "10.246.198.202", out.get("sourceAddress").get(0));
        assertEquals("sourceUserName", "wth0xww", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "https", out.get("requestScheme").get(0));
        assertEquals("destinationHostName", "r4---sn-q4fl6n7e", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceEventCategory", "Streaming Media, Media Sharing", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "mahproxy1", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "10.246.198.202", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));

    }

    @Test
    public void testMcAfeeWebSecToWebProxyMefEventParser() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "2015-09-15T01:24:59-04:00 SSCwcgCONqa01 mwg: time_stamp=[15/Sep/2015:01:24:59 -0400] auth_user=- src_ip=172.16.45.106 server_ip=172.16.153.24 time_taken=46 status_code=200 cache_status=TCP_MISS req_line=CONNECT cpliqp0p.example.com:1270 HTTP/1.1 categories=Online Shopping rep_level=Minimal Risk media_type=application/x-empty bytes_to_client=5029 bytes_from_client=2396 user_agent=- referrer=- virus_name=- gam_probability=0 block_res=0 geolocation=- application_name=- md5=-";

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
        assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
        assertEquals("reason", "-", out.get("reason").get(0));
        assertEquals("sourceNameOrIp", "172.16.45.106", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "sscwcgconqa01", out.get("deviceHostName").get(0));
        assertEquals("bytesIn", 5029, out.get("bytesIn").get(0));
        //Tue, 15 Sep 2015 05:24:59 GMT
        assertEquals("startTime", 1442294699000L, out.get("startTime").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "-", out.get("requestClientApplication").get(0));
        assertEquals("bytesOut", 2396, out.get("bytesOut").get(0));
        assertEquals("deviceEventCategory", "Minimal Risk/Online Shopping", out.get("deviceEventCategory").get(0));
        assertEquals("receiptTime", 1442294699000L, out.get("receiptTime").get(0));
        assertEquals("sourceAddress", "172.16.45.106", out.get("sourceAddress").get(0));
        assertEquals("devicePolicyAction", "0", out.get("devicePolicyAction").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("responseContentType", "application/x-empty", out.get("responseContentType").get(0));
        assertEquals("destinationAddress", "172.16.153.24", out.get("destinationAddress").get(0));
        assertEquals("deviceNameOrIp", "SSCwcgCONqa01", out.get("deviceNameOrIp").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "CONNECT", out.get("requestMethod").get(0));
        assertEquals("requestVersion", "HTTP/1.1", out.get("requestVersion").get(0));
        assertEquals("requestReferer", "-", out.get("requestReferer").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));

    }


    @Test
    public void testMcAfeeWebSecToWebProxyMefEventParserReqLineWithoutVersion() throws IOException, Exception {

        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
        String msg = "<30>Jun  8 02:57:44 sscwcgconpr01 mwg: time_stamp=[08/Jun/2016:02:57:44 -0400] auth_user=(BypassAuthentication-IP) src_ip=192.168.181.130 server_ip=52.7.137.24 time_taken=92 status_code=200 cache_status=TCP_MISS_RELOAD req_line=GET http://c7.prod.playlists.ihrhls.com/4314/playlist.m3u8?listeningSessionID=573aa12d01556a25_1867655_LmM7iGCE_0000000Sm6u&downloadSessionID=0&at=0&clientType=web&fb_broadcast=0&init_id=8169&modTime=1465233647340&pname=OrganicWeb&profileid=19864378&birthYear=1971&territory=US&host=webapp.US&listenerId=faac731e8b090acce0d46c1cfd382cc0&uid=1438955911967&gender=2&age=45&amsparams=playerid%3AiHeartRadioWebPlayer%3Bskey%3A1465233647&terminalid=159&zipcode=30064&awparams=g%3A2%3Bn%3A45%3Bccaud%3A%5B%2210037%22%2C%2210038%22%2C%2210161%22%2C%2210147%22%2C%2210473%22%2C%2210301%22%2C%2210092%22%2C%2210374%22%2C%2210006%22%2C%2210126%22%2C%2210504%22%2C%2210098%22%2C%2210426%22%2C%2210400%22%2C%2210039%22%2C%2210081%22%2C%2210082%22%2C%2210331%22%2C%2210008%22%2C%2210051%22%2C%2210079%22%2C%2210267%22%2C%2210108%22%2C%2210130%22%2C%2210123%22%2C%2210093%22%2C%22all%22%2C%2210417%22%2C%2210052%22%2C%2210144%22%2C%2210259%22%2C%2210010%22%2C%2210479%22%2C%2210104%22%2C%2210132%22%2C%2210160%22%2C%2210213%22%2C%2210195%22%2C%2210510%22%2C%2210012%22%2C%2210277%22%2C%2210233%22%2C%2210024%22%2C%2210013%22%2C%2210150%22%2C%2210393%22%2C%2210139%22%2C%2210493%22%2C%2210110%22%2C%2210340%22%2C%2210273%22%2C%2210379%22%2C%2210190%22%2C%2210354%22%2C%2210188%22%2C%2210234%22%2C%2210468%22%2C%2210180%22%2C%2210399%22%2C%2210040%22%2C%2210114%22%2C%2210044%22%2C%2210028%22%2C%2210133%22%2C%2210383%22%2C%2210475%22%2C%2210445%22%2C%2210003%22%2C%2210491%22%2C%2210025%22%2C%2210377%22%2C%2210489%22%2C%2210223%22%2C%2210243%22%2C%2210227%22%2C%2210164%22%2C%2210062%22%2C%2210106%22%2C%2210124%22%2C%2210404%22%2C%2210120%22%2C%2210185%22%2C%2210359%22%2C%2210365%22%2C%2210118%22%2C%2210506%22%2C%2210018%22%2C%2210303%22%2C%2210258%22%2C%2210203%22%2C%2210502%22%2C%2210148%22%2C%2210086%22%2C%2210515%22%2C%221001";

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

        assertEquals("destinationDnsDomain", "prod.playlists.ihrhls.com", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationAddress", "52.7.137.24", out.get("destinationAddress").get(0));
        assertEquals("sourceAddress", "192.168.181.130", out.get("sourceAddress").get(0));
        assertEquals("deviceAction", "TCP_MISS_RELOAD", out.get("deviceAction").get(0));
        assertEquals("sourceUserName", "(BypassAuthentication-IP)", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("destinationHostName", "c7", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("destinationNameOrIp", "c7.prod.playlists.ihrhls.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("requestQuery", "listeningSessionID=573aa12d01556a25_1867655_LmM7iGCE_0000000Sm6u&downloadSessionID=0&at=0&clientType=web&fb_broadcast=0&init_id=8169&modTime=1465233647340&pname=OrganicWeb&profileid=19864378&birthYear=1971&territory=US&host=webapp.US&listenerId=faac731e8b090acce0d46c1cfd382cc0&uid=1438955911967&gender=2&age=45&amsparams=playerid%3AiHeartRadioWebPlayer%3Bskey%3A1465233647&terminalid=159&zipcode=30064&awparams=g%3A2%3Bn%3A45%3Bccaud%3A%5B%2210037%22%2C%2210038%22%2C%2210161%22%2C%2210147%22%2C%2210473%22%2C%2210301%22%2C%2210092%22%2C%2210374%22%2C%2210006%22%2C%2210126%22%2C%2210504%22%2C%2210098%22%2C%2210426%22%2C%2210400%22%2C%2210039%22%2C%2210081%22%2C%2210082%22%2C%2210331%22%2C%2210008%22%2C%2210051%22%2C%2210079%22%2C%2210267%22%2C%2210108%22%2C%2210130%22%2C%2210123%22%2C%2210093%22%2C%22all%22%2C%2210417%22%2C%2210052%22%2C%2210144%22%2C%2210259%22%2C%2210010%22%2C%2210479%22%2C%2210104%22%2C%2210132%22%2C%2210160%22%2C%2210213%22%2C%2210195%22%2C%2210510%22%2C%2210012%22%2C%2210277%22%2C%2210233%22%2C%2210024%22%2C%2210013%22%2C%2210150%22%2C%2210393%22%2C%2210139%22%2C%2210493%22%2C%2210110%22%2C%2210340%22%2C%2210273%22%2C%2210379%22%2C%2210190%22%2C%2210354%22%2C%2210188%22%2C%2210234%22%2C%2210468%22%2C%2210180%22%2C%2210399%22%2C%2210040%22%2C%2210114%22%2C%2210044%22%2C%2210028%22%2C%2210133%22%2C%2210383%22%2C%2210475%22%2C%2210445%22%2C%2210003%22%2C%2210491%22%2C%2210025%22%2C%2210377%22%2C%2210489%22%2C%2210223%22%2C%2210243%22%2C%2210227%22%2C%2210164%22%2C%2210062%22%2C%2210106%22%2C%2210124%22%2C%2210404%22%2C%2210120%22%2C%2210185%22%2C%2210359%22%2C%2210365%22%2C%2210118%22%2C%2210506%22%2C%2210018%22%2C%2210303%22%2C%2210258%22%2C%2210203%22%2C%2210502%22%2C%2210148%22%2C%2210086%22%2C%2210515%22%2C%221001", out.get("requestQuery").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("receiptTime", 1465354664000L, out.get("receiptTime").get(0));
        assertEquals("startTime", 1465369064000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "sscwcgconpr01", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "192.168.181.130", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("requestPath", "/4314/playlist.m3u8", out.get("requestPath").get(0));
        assertEquals("deviceHostName", "sscwcgconpr01", out.get("deviceHostName").get(0));

    }
        //

    @Test
    public void testCefArcsightToSiemIncidentMef() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Jul 29 11:12:57 xxxxx CEF:0|ArcSight|ArcSight|6.0.0.1333.0|rule:105|Possible Network Sweep|High| eventId=-2305843008255647244 type=2 start=1449176771000 end=1449176781000 mrt=1449176832393 sessionId=0 generatorID=3zlyoaVEBABCAA5yQuWRKBA\\=\\= categoryBehavior=/Execute/Response categoryDeviceGroup=/Security Information Manager modelConfidence=0 severity=10 relevance=10 assetCriticality=0 priority=9 art=1449176814276 cat=/Rule/Fire deviceSeverity=Warning rt=1449176781806 src=65.85.126.60 sourceZoneID=MFRvYWj4BABCCAoMqnujYjg\\=\\= sourceZoneURI=/All Zones/Custom Zones/Terminal Server sourceGeoCountryCode=IN slong=78.4553694444 slat=20.5080833333 dpt=22 fname=Possible Network Sweep filePath=/All Rules/Real-time Rules/Intrusion Monitoring/Worm Outbreak/Possible Network Sweep fileType=Rule cnt=20 ruleThreadId=UMyqaVEBABDzqAm2yfxISA\\=\\= cs2=<Resource URI\\=\"/All Rules/Real-time Rules/Intrusion Monitoring/Worm Outbreak/Possible Network Sweep\" ID\\=\"5VWTDCf8AABCSRjFkAIznMA\\=\\=\"/> c6a4=fe80:0:0:0:2073:3a99:96b0:491b locality=1 cs2Label=Configuration Resource c6a4Label=Agent IPv6 Address ahost=ESM60c agt=10.160.142.12 av=5.2.5.6403.0 atz=America/Los_Angeles aid=3zlyoaVEBABCAA5yQuWRKBA\\=\\= at=superagent_ng dvchost=ESM60c dvc=10.160.142.12 deviceZoneID=MFRvYWj4BABCCAoMqnujYjg\\=\\= deviceZoneURI=/All Zones/Custom Zones/Terminal Server deviceAssetId=4tSCTm0IBABDgy99le+embw\\=\\= dtz=America/Los_Angeles deviceFacility=Rules Engine eventAnnotationStageUpdateTime=1449176832847 eventAnnotationModificationTime=1449176832847 eventAnnotationAuditTrail=1,1449176832847,root,Queued,,,,\\n1,1449170786694,root,Queued,,,,\\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1449176781000 eventAnnotationManagerReceiptTime=1449176832393 _cefVer=0.1 ad.arcSightEventPath=32JezIVEBABCJFtP3axXseg\\=\\=";
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
        assertEquals("destinationPort", 22, out.get("destinationPort").get(0));
        assertEquals("severity", "10", out.get("severity").get(0));
        assertEquals("eventId", -2305843008255647244L, out.get("eventId").get(0));
        assertEquals("agentAddress", "10.160.142.12", out.get("agentAddress").get(0));
        assertEquals("baseEventCount", 20, out.get("baseEventCount").get(0));
        assertEquals("sourceAddress", "65.85.126.60", out.get("sourceAddress").get(0));
        assertEquals("type", 2, out.get("type").get(0));
        assertEquals("deviceReceiptTime", 1449176781806L, out.get("deviceReceiptTime").get(0));
        assertEquals("cefSignatureId", "rule:105", out.get("cefSignatureId").get(0));
        assertEquals("agentHostname", "ESM60c", out.get("agentHostName").get(0));
        assertEquals("deviceEventCategory", "/Rule/Fire", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceAddress", "10.160.142.12", out.get("deviceAddress").get(0));
        assertEquals("startTime", 1449176771000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "SIEMIncidentMef", out.get("parserOutFormat").get(0));
        assertEquals("endTime", 1449176781000L, out.get("endTime").get(0));
        assertEquals("cefEventName", "Possible Network Sweep", out.get("cefEventName").get(0));
        assertEquals("deviceHostName", "ESM60c", out.get("deviceHostName").get(0));
    }
    //


//
//  /**
//   * emulate a bluecoat avro record and check the parsing result within the
//   * webproxy format
//   *
//   * @throws IOException
//   * @throws Exception
//   */
//  public void testAvroEventParserBluecoatToWebProxyMef() throws IOException, Exception {
//    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
//    Event avroEvent = new Event();
//    String msg = "17/11/2013 13:07:49 349 45.21.4.244 200 TCP_MISS 40691 320 GET http www.accelacast.com /programs/f5_assetcentric/050411_f5_ab_6.swf - - DIRECT www.accelacast.com application/x-shockwave-flash \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\" PROXIED none - 192.16.170.42 SG-HTTP-Service - none -";
//
//    Charset utf8charset = Charset.forName("UTF-8");
//    CharsetEncoder encoder = utf8charset.newEncoder();
//
//    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
//    avroEvent.setBody(buf);
//    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
//    headers.put("category", "syslog");
//    headers.put("hostname", "somehost");
//
//    headers.put("timestamp", "1384693669604");
//
//    avroEvent.setHeaders(headers);
//    Map<String, List<Object>> out = instance.parse(avroEvent);
//    OutUtils.printOut(out.toString());
//    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
//    assertEquals("startTime", 1384693669000L, out.get("startTime").get(0));
//
//    assertEquals("deviceAction", "TCP_MISS", out.get("deviceAction").get(0));
//    assertEquals("reason", "-, none : -", out.get("reason").get(0));
//    assertEquals("deviceCustomString2", "349", out.get("deviceCustomString2").get(0));
//    assertEquals("sourceNameOrIp", "45.21.4.244", out.get("sourceNameOrIp").get(0));
//    assertEquals("destinationNameOrIp", "www.accelacast.com", out.get("destinationNameOrIp").get(0));
//    assertEquals("deviceAddress", "192.16.170.42", out.get("deviceAddress").get(0));
//    assertEquals("logSourceType", "BlueCoat", out.get("logSourceType").get(0));
//    assertEquals("bytesIn", 320, out.get("bytesIn").get(0));
//    assertEquals("deviceProcessName", "SG-HTTP-Service", out.get("deviceProcessName").get(0));
//    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
//    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
//    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
//    assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
//    assertEquals("requestClientApplication", "\"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)\"", out.get("requestClientApplication").get(0));
//    assertEquals("bytesOut", 40691, out.get("bytesOut").get(0));
//    assertEquals("deviceEventCategory", "none", out.get("deviceEventCategory").get(0));
//    assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
//    assertEquals("sourceAddress", "45.21.4.244", out.get("sourceAddress").get(0));
//    assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
//    assertEquals("requestPath", "/programs/f5_assetcentric/050411_f5_ab_6.swf", out.get("requestPath").get(0));
//    assertEquals("requestQuery", "-", out.get("requestQuery").get(0));
//
//  }
    /**
     * Parsing this event will not provide BlueCoat format, a null ouput is
     * expected
     *
     * @throws IOException
     * @throws Exception
     */
//    public void testAvroEventParserFail() throws IOException, Exception {
//        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.BlueCoat);
//        Event avroEvent = new Event();
//        String msg = "10/31/2013 23:59:56 ,170.144.132.79,Oracle_01,22,6,\"Audit file /fiscaldb/adump/ora_28373.aud Oracle Database 10g Enterprise Edition Release 10.2.0.4.0 - 64bit Production With the Partitioning, Real Application Clusters, OLAP, Data Mining and Real Application Testing options ORACLE_HOME = /var/lpp/oracle/db System name: Linux Node name: slprpordb008 Release: 2.6.5-7.308-smp Version: #1 SMP Mon Dec 10 11:36:40 UTC 2007 Machine: x86_64 Instance name: fiscaldb2 Redo thread mounted by this instance: 2 Oracle process number: 60 Unix process pid: 28373, image: oracle@slprpordb008 TIMESTAMP: Thu Mar 29 12:20:44 2012 LENGTH: \"\"333\"\" SESSIONID:[7] \"\"3517082\"\" ENTRYID:[1] \"\"1\"\" STATEMENT:[1] \"\"1\"\" USERID:[6] \"\"DBSNMP\"\" USERHOST:[12] \"\"slprpordb008\"\" ACTION:[3] \"\"100\"\" ";
//
//        Charset utf8charset = Charset.forName("UTF-8");
//        CharsetEncoder encoder = utf8charset.newEncoder();
//        CharBuffer cbuf = CharBuffer.wrap(msg);
//
//        ByteBuffer buf = encoder.encode(cbuf);
//        avroEvent.setBody(buf);
//        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
//        headers.put("category", "syslog");
//        headers.put("hostnme", "somehost");
//    
//        headers.put("timestamp", "1384693669604");
//
//        avroEvent.setHeaders(headers);
//        Map<String, List<Object>> out = instance.parse(avroEvent);
//        assertEquals("null ouput expected", null, out);
//    }
}
