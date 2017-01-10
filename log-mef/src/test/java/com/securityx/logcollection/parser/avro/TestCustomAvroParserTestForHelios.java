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
public class TestCustomAvroParserTestForHelios extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForHelios(String testName) {
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
    public void testQradarDhcpForHelios() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-07T13:05:08.725378-07:00 SSDDMNC5-DHCP AgentDevice=WindowsDHCP\tAgentLogFile=DhcpSrvLog-Thu.log\tPluginVersion=7.2.0.984723\tID=30\tDate=04/07/16\tTime=13: 04:55\tDescription=DNS Update Request\tIP Address=10.64.142.75\tHost Name=LT-012991.websense.com\tMAC Address=\tUser Name=\t TransactionID=0\t QResult=6\tProbationtime=\t CorrelationID=\tDhcid=";
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

        assertEquals("destinationDnsDomain", "websense.com", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationAddress", "10.64.142.75", out.get("destinationAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "LT-012991", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("destinationNameOrIp", "LT-012991.websense.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "qradar-windows-dhcp", out.get("externalLogSourceType").get(0));
        //1460059508.
        //
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("receiptTime", 1460059508725L, out.get("receiptTime").get(0));
        assertEquals("startTime", 1460034295000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "SSDDMNC5-DHCP", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "SSDDMNC5-DHCP", out.get("deviceHostName").get(0));
    }

    @Test
    public void testQradarDhcpParsingFailureForHelios() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-08T07:14:12.551394-07:00 SSDDMNC5-DHCP AgentDevice=WindowsDHCP\tAgentLogFile=DhcpSrvLog-Fri.log\tPluginVersion=7.2.0.984723\tID=11\tDate=04/08/16\tTime=07: 14:00\tDescription=Renew\tIP Address=10.64.138.23\tHost Name=LT-012447.websense.com\tMAC Address=F8CAB801CBC3\tUser Name=\t TransactionID=254450765\t QResult=0\tProbationtime=\t CorrelationID=\tDhcid=";
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

        assertEquals("destinationDnsDomain", "websense.com", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationAddress", "10.64.142.75", out.get("destinationAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "LT-012991", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "Unknown", out.get("cefSignatureId").get(0));
        assertEquals("destinationNameOrIp", "LT-012991.websense.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "qradar-windows-dhcp", out.get("externalLogSourceType").get(0));
        //1460059508.
        //
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("receiptTime", 1460059508725L, out.get("receiptTime").get(0));
        assertEquals("startTime", 1460034295000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "SSDDMNC5-DHCP", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "SSDDMNC5-DHCP", out.get("deviceHostName").get(0));
    }

    //
    @Test
    public void testWebsenseBadParsingForHelios() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-11T16:52:01.469915-07:00 10.34.12.16 LEEF: 1.0|Websense|Security|8.1.0|transaction:permitted|sev=1cat=29usrName=LDAP://ssddmnc5.websense.com OU=Users,OU=LosGatos,DC=websense,DC=com/Johnson\\, Andreasrc=10.34.143.10srcPort=57005srcBytes=3180dstBytes=1163dst=204.13.194.235dstPort=443proxyStatus-code=200serverStatus-code=200duration=4method=GETdisposition=1026contentType=text/htmlreason=-policy=Super Administrator**- Allow Software download,Super Administrator**Remote Users Policyrole=8userAgent=Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36url=https://oascentral.comcast.net/RealMedia/ads/adstream_sx.ads/dev.comcast.net/com-mail/inbox/@x32?AdParam\\=adx";
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
        assertEquals("sourcePort", 57005, out.get("sourcePort").get(0));
        assertEquals("destinationAddress", "204.13.194.235", out.get("destinationAddress").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("destinationNameOrIp", "oascentral.comcast.net", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "leefWebsenseSecurity", out.get("externalLogSourceType").get(0));
        assertEquals("requestQuery", "AdParam\\=adx", out.get("requestQuery").get(0));
        assertEquals("deviceAddress", "10.34.12.16", out.get("deviceAddress").get(0));
        assertEquals("receiptTime", 1460418721469L, out.get("receiptTime").get(0));
        assertEquals("devicePolicyAction", "Super Administrator**- Allow Software download,Super Administrator**Remote Users Policy", out.get("devicePolicyAction").get(0));
        assertEquals("startTime", 1460418721469L, out.get("startTime").get(0));
        assertEquals("requestPath", "/RealMedia/ads/adstream_sx.ads/dev.comcast.net/com-mail/inbox/@x32", out.get("requestPath").get(0));
        assertEquals("bytesOut", 1163, out.get("bytesOut").get(0));
        assertEquals("bytesIn", 3180, out.get("bytesIn").get(0));
        assertEquals("destinationDnsDomain", "comcast.net", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "10.34.143.10", out.get("sourceAddress").get(0));
        assertEquals("deviceAction", "permitted", out.get("deviceAction").get(0));
        assertEquals("sourceUserName", "LDAP://ssddmnc5.websense.com OU=Users,OU=LosGatos,DC=websense,DC=com/Johnson\\, Andrea", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("requestScheme", "https", out.get("requestScheme").get(0));
        assertEquals("destinationHostName", "oascentral", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
        assertEquals("deviceEventCategory", "29", out.get("deviceEventCategory").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36", out.get("requestClientApplication").get(0));
        assertEquals("responseContentType", "text/html", out.get("responseContentType").get(0));
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "10.34.12.16", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
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