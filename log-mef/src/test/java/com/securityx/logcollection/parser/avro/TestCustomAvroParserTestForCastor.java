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
public class TestCustomAvroParserTestForCastor extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForCastor(String testName) {
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
  public void testDhcpdForCastor() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<13>Jun 28 13:22:54 DC01-MID-PRD.conchoresources.com 11,06/28/16,13:22:53,Renew,10.11.11.17,Bryans-iPhone-2.conchoresources.com,787E614D9DA3,,3344130468,0,,,\r";
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

    assertEquals("destinationAddress", "10.11.11.17", out.get("destinationAddress").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
    assertEquals("destinationNameOrIp", "Bryans-iPhone-2.conchoresources.com", out.get("destinationNameOrIp").get(0));
    assertEquals("externalLogSourceType", "windows-dhcp", out.get("externalLogSourceType").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("receiptTime", 1467120174000L, out.get("receiptTime").get(0));
    assertEquals("eventOutcome", "11 - Renew", out.get("eventOutcome").get(0));
    assertEquals("startTime", 1467120173000L, out.get("startTime").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    assertEquals("deviceNameOrIp", "DC01-MID-PRD.conchoresources.com", out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("destinationMacAddress", "787E614D9DA3", out.get("destinationMacAddress").get(0));


  }

  @Test
  public void testWindows4776ForCastor() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    String msg = "<14>Jun 29 08:59:58 DC01-MID-PRD.conchoresources.com MSWinEventLog\t1\tSecurity\t3192885\tWed Jun 29 08:59:58 2016\t4776\tMicrosoft-Windows-Security-Auditing\tN/A\tN/A\tSuccess Audit\tDC01-MID-PRD.conchoresources.com\tCredential Validation\t\tThe computer attempted to validate the credentials for an account.    Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0  Logon Account: svc-bobjds-p  Source Workstation: BOE02-MID-PRD  Error Code: 0x0\t2120695027\r";
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



  /*@Test
  public void testUnMactchedFile() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);

    InputStream fis = new FileInputStream("/Users/macadmin/tmp/castor/windows-supported.json");
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
      //avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));

      List<Map<String, List<Object>>> output = instance.parse(avroEvent);
      if ((! line.startsWith("#")) && line.length() > 0) {
            try {
                Map<String, List<Object>> out = output.get(0);
                OutUtils.printOut(out.toString());

                assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
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