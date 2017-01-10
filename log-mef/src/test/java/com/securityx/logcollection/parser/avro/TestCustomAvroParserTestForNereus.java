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
public class TestCustomAvroParserTestForNereus extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForNereus(String testName) {
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
  public void testDhcpdDiscoverForNereus() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "2016-06-27T00:32:22-07:00 local6 m1.infoblox.com dhcpd[26673]: info DHCPDISCOVER from a9:5a:b2:0f:ed:76 via 192.168.1.101 ";
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

    // 2016-06-27T00:32:23-07:00 local6 m1.infoblox.com dhcpd[26673]: info DHCPREQUEST for 172.16.100.197 (192.168.1.3) from a9:5a:b2:0f:ed:76 via 192.168.1.101
    @Test
    public void testDhcpdRequestrForNereus() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "2016-06-27T00:32:23-07:00 local6 m1.infoblox.com dhcpd[26673]: info DHCPREQUEST for 172.16.100.197 (192.168.1.3) from a9:5a:b2:0f:ed:76 via 192.168.1.101";
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

    @Test
    public void testQradarWindowsForNereus() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "<30>Jul 27 12:09:58 10.128.25.240 dhcpd[9311]: DHCPINFORM from 10.128.24.129 via 10.128.24.252 ";
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


    @Test
    public void testDhcpdOfferForNereus() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "2016-06-27T00:32:23-07:00 local6 m1.infoblox.com dhcpd[26673]: info DHCPOFFER on 172.16.100.197 to a9:5a:b2:0f:ed:76 via eth1 relay 192.168.1.101 lease-duration 119 offered-duration 900";
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

        assertEquals("deviceProcessId", "9311", out.get("deviceProcessId").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("deviceInterface", "10.128.24.252", out.get("deviceInterface").get(0));
        assertEquals("cefSignatureId", "DHCPINFORM", out.get("cefSignatureId").get(0));
        assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceAddress", "10.128.25.240", out.get("deviceAddress").get(0));
        assertEquals("receiptTime", 1469621398000L, out.get("receiptTime").get(0));
        assertEquals("deviceProcessName", "dhcpd", out.get("deviceProcessName").get(0));
        assertEquals("startTime", 1469621398000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "10.128.25.240", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));

    }


    @Test
    public void testDhcpdExpiredForNereus() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        String msg = "2016-06-27T00:32:23-07:00 local6 m1.infoblox.com dhcpd[26673]: info DHCPEXPIRE on 172.16.100.184 to a7:b2:ec:3e:50:df";
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

    InputStream fis = new FileInputStream("/Users/macadmin/Downloads/Mahwah McAfee Web Gateway Sample.txt");
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
  }   */



}