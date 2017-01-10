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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.kitesdk.morphline.api.Record;

/**
 *
 * @author jyrialhon
 */
public class TestCustomAvroParserTestForMorpheus extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForMorpheus(String testName) {
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
    public void test_FakeDhcpdFromMorpheusToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "<13>jul 25 16:29:01 fakedhcp.from.splunk dhcp: DHCPOFFER on 100.64.144.94 to 00:0a:f7:4b:0a:39 (TS-PQA-123456) via 0.0.0.0";

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
                    assertEquals("deviceDnsDomain", "from.splunk", out.get("deviceDnsDomain").get(0));
                    assertEquals("destinationAddress", "100.64.144.94", out.get("destinationAddress").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("deviceInterface", "0.0.0.0", out.get("deviceInterface").get(0));
                    assertEquals("destinationHostName", "TS-PQA-123456", out.get("destinationHostName").get(0));
                    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
                    assertEquals("destinationNameOrIp", "TS-PQA-123456", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "dhcpd", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("receiptTime", 1469464141000L, out.get("receiptTime").get(0));
                    assertEquals("deviceProcessName", "dhcp", out.get("deviceProcessName").get(0));
                    assertEquals("startTime", 1469464141000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "fakedhcp.from.splunk", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("deviceHostName", "fakedhcp", out.get("deviceHostName").get(0));
                    assertEquals("destinationMacAddress", "00:0a:f7:4b:0a:39", out.get("destinationMacAddress").get(0));
                    break;
                default:
            }


        }
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