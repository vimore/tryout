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
public class TestCustomAvroParserTestForTriton extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForTriton() {
    super("TestCustomAvroParserTestForTriton");
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

  //<30>Aug 16 13:27:21 proxy5 mwg: LEEF:1.0|McAfee|Web Gateway|7.5.2.7.0|0|devTime=1471354041000|usrName=yxs1jgl|realm=mahesds-vip.examle.com|src=10.23.193.63|srcPort=50783|dst=192.168.254.131|dstPort=80|blockReason=|srcPreNAT=10.23.193.63|srcPreNATPort=50783|dstPreNAT=153.2.246.37|dstPreNATPort=8080|dstPostNAT=192.168.254.131|srcPostNAT=192.168.254.131|srcPostNATPort=27110|dstPostNATPort=80|srcBytes=3082|dstBytes=10839|totalBytes=13921|srcBytesPostNAT=3054|dstBytesPostNAT=10776|totalBytesPostNAT=13830|httpStatus=200|cacheStatus=TCP_MISS_RELOAD|timeTaken=267|contentType=text/javascript|ensuredType=text/plain|urlCategories=Web Ads, Internet Services|reputation=Minimal Risk/11|policy=Default|proto=http|method=GET|url=http://m.adnxs.com/ttj?member=280&inv_code=SPOUSEN13&imp_id=1471354041|12387351620385654&cb=1471354041&size=300x250&providerid=b4penbif6&ext_inv_code=US&rid=8a49cd1693d94a54f0c481bd7c2e8c56&external_uid=020dd712396e66d70eddd0343d6e623a&traffic_source_code=pg%3ASPOUSEN13%3Bp%3Ab4penbif6%3Br%3A8a49cd1693d94a54f0c481bd7c2e8c56|referer=http://www.msn.com/en-us/sports/olympics/french-pole-vaulter-rips-brazilian-fans/ar-BBvFR25?li=BBnb7Kz|userAgent=Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36|calCountryOrRegion=US|virusName=false|application=


    @Test
    public void test_McAfeeWebSecToWebProxyMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "<30>Aug 16 13:27:21 proxy5 mwg: LEEF:1.0|McAfee|Web Gateway|7.5.2.7.0|0|devTime=1471354041000|usrName=yxs1jgl|realm=mahesds-vip.examle.com|src=10.23.193.63|srcPort=50783|dst=192.168.254.131|dstPort=80|blockReason=|srcPreNAT=10.23.193.63|srcPreNATPort=50783|dstPreNAT=153.2.246.37|dstPreNATPort=8080|dstPostNAT=192.168.254.131|srcPostNAT=192.168.254.131|srcPostNATPort=27110|dstPostNATPort=80|srcBytes=3082|dstBytes=10839|totalBytes=13921|srcBytesPostNAT=3054|dstBytesPostNAT=10776|totalBytesPostNAT=13830|httpStatus=200|cacheStatus=TCP_MISS_RELOAD|timeTaken=267|contentType=text/javascript|ensuredType=text/plain|urlCategories=Web Ads, Internet Services|reputation=Minimal Risk/11|policy=Default|proto=http|method=GET|url=http://m.adnxs.com/ttj?member=280&inv_code=SPOUSEN13&imp_id=1471354041|12387351620385654&cb=1471354041&size=300x250&providerid=b4penbif6&ext_inv_code=US&rid=8a49cd1693d94a54f0c481bd7c2e8c56&external_uid=020dd712396e66d70eddd0343d6e623a&traffic_source_code=pg%3ASPOUSEN13%3Bp%3Ab4penbif6%3Br%3A8a49cd1693d94a54f0c481bd7c2e8c56|referer=http://www.msn.com/en-us/sports/olympics/french-pole-vaulter-rips-brazilian-fans/ar-BBvFR25?li=BBnb7Kz|userAgent=Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36|calCountryOrRegion=US|virusName=false|application=";

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
                    assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
                    assertEquals("reason", "false", out.get("reason").get(0));
                    assertEquals("sourcePort", 50783, out.get("sourcePort").get(0));
                    assertEquals("destinationAddress", "192.168.254.131", out.get("destinationAddress").get(0));
                    assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
                    assertEquals("destinationNameOrIp", "m.adnxs.com", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "mcafeewebsec", out.get("externalLogSourceType").get(0));
                    assertEquals("requestQuery", "member=280&inv_code=SPOUSEN13&imp_id=1471354041|12387351620385654&cb=1471354041&size=300x250&providerid=b4penbif6&ext_inv_code=US&rid=8a49cd1693d94a54f0c481bd7c2e8c56&external_uid=020dd712396e66d70eddd0343d6e623a&traffic_source_code=pg%3ASPOUSEN13%3Bp%3Ab4penbif6%3Br%3A8a49cd1693d94a54f0c481bd7c2e8c56", out.get("requestQuery").get(0));
                    assertEquals("receiptTime", 1471354041000L, out.get("receiptTime").get(0));
                    assertEquals("requestReferer", "http://www.msn.com/en-us/sports/olympics/french-pole-vaulter-rips-brazilian-fans/ar-BBvFR25?li=BBnb7Kz", out.get("requestReferer").get(0));
                    assertEquals("devicePolicyAction", "Default", out.get("devicePolicyAction").get(0));
                    assertEquals("applicationProtocol", "http", out.get("applicationProtocol").get(0));
                    assertEquals("deviceHostName", "proxy5", out.get("deviceHostName").get(0));
                    assertEquals("requestPath", "/ttj", out.get("requestPath").get(0));
                    assertEquals("bytesOut", 10839, out.get("bytesOut").get(0));
                    assertEquals("bytesIn", 3082, out.get("bytesIn").get(0));
                    assertEquals("destinationDnsDomain", "adnxs.com", out.get("destinationDnsDomain").get(0));
                    assertEquals("sourceNameOrIp", "10.23.193.63", out.get("sourceNameOrIp").get(0));
                    assertEquals("sourceAddress", "10.23.193.63", out.get("sourceAddress").get(0));
                    assertEquals("sourceUserName", "yxs1jgl", out.get("sourceUserName").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
                    assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
                    assertEquals("logCollectionTime", 1384693669604L, out.get("logCollectionTime").get(0));
                    assertEquals("deviceEventCategory", "Web Ads, Internet Services", out.get("deviceEventCategory").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36", out.get("requestClientApplication").get(0));
                    assertEquals("responseContentType", "text/javascript", out.get("responseContentType").get(0));
                    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "proxy5", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
                    break;
                default:
            }


        }
    }





    @Test
    public void test_DhcpAckromMorpheusToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "Oct 16 10:49:20 wpasgpsdhcp001 LEEF:1.0|BCN|Adonis|7.1.0|DHCP_Message|cat=ACK	srcMAC=00:80:a3:94:39:3c	src=10.120.5.120	 ";

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
                    assertEquals("destinationAddress", "10.120.5.120", out.get("destinationAddress").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "adonis-dhcp", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionTime", "1384693669604", out.get("logCollectionTime").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("receiptTime", 1444992560000L, out.get("receiptTime").get(0));
                    assertEquals("startTime", 1444992560000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "wpasgpsdhcp001", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("destinationMacAddress", "00:80:a3:94:39:3c", out.get("destinationMacAddress").get(0));                    break;
                default:
            }


        }
    }


    @Test
    public void test_DhcpRequestromMorpheusToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "Oct 16 10:49:20 wpasgpsdhcp002 LEEF:1.0|BCN|Adonis|7.1.0|DHCP_Message|cat=REQUEST\tsrcMAC=00:80:a3:94:39:3c\tsrc=10.120.5.120\t";

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
                    assertEquals("syslogMessage", "LEEF:1.0|BCN|Adonis|7.1.0|DHCP_Message|cat=REQUEST	srcMAC=00:80:a3:94:39:3c	src=10.120.5.120	", out.get("syslogMessage").get(0));
                    assertEquals("destinationAddress", "10.120.5.120", out.get("destinationAddress").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("cefSignatureId", "DHCPREQUEST", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "adonis-dhcp", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("receiptTime", 1444992560000L, out.get("receiptTime").get(0));
                    assertEquals("startTime", 1444992560000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "wpasgpsdhcp002", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("destinationMacAddress", "00:80:a3:94:39:3c", out.get("destinationMacAddress").get(0));
                    break;
                default:
            }


        }
    }
    @Test
    public void test_DhcpOfferromMorpheusToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "Oct 16 10:49:20 wpasgpsdhcp001 LEEF:1.0|BCN|Adonis|7.1.0|DHCP_Message|cat=OFFER\tsrcMAC=00:80:a3:94:39:3c\tsrc=10.120.5.120\t ";

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
                    assertEquals("destinationAddress", "10.120.5.120", out.get("destinationAddress").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("cefSignatureId", "DHCPOFFER", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "adonis-dhcp", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("receiptTime", 1444992560000L, out.get("receiptTime").get(0));
                    assertEquals("startTime", 1444992560000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "wpasgpsdhcp001", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("destinationMacAddress", "00:80:a3:94:39:3c", out.get("destinationMacAddress").get(0));
                    break;
                default:
            }


        }
    }
    @Test
    public void test_DhcpDiscoverfromMorpheusToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "Oct 16 10:49:20 wpasgpsdhcp002 LEEF:1.0|BCN|Adonis|7.1.0|DHCP_Message|cat=DISCOVER\tsrcMAC=00:80:a3:94:39:3c\t ";

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
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("cefSignatureId", "DHCPDISCOVER", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "adonis-dhcp", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("receiptTime", 1444992560000L, out.get("receiptTime").get(0));
                    assertEquals("startTime", 1444992560000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "wpasgpsdhcp002", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("destinationMacAddress", "00:80:a3:94:39:3c", out.get("destinationMacAddress").get(0));
                    break;
                default:
            }


        }
    }
    @Test
    public void test_DhcpInformfromMorpheusToHetMef() throws Exception {
        List<SupportedFormats> formats = new ArrayList<SupportedFormats>();
        formats.add(SupportedFormats.WebProxyMef);
        formats.add(SupportedFormats.HETMef);
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "Oct 16 10:49:20 wpasgpsdhcp001 LEEF:1.0|BCN|Adonis|7.1.0|DHCP_Message|cat=INFORM\tsrc=10.121.141.71\t ";

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
                    assertEquals("destinationAddress", "10.121.141.71", out.get("destinationAddress").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("cefSignatureId", "DHCPINFORM", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "adonis-dhcp", out.get("externalLogSourceType").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("receiptTime", 1444992560000L, out.get("receiptTime").get(0));
                    assertEquals("startTime", 1444992560000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "wpasgpsdhcp001", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
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

    InputStream fis = new FileInputStream("/Users/macadmin/Downloads/Mahwah BlueCat Adonis DHCP Sample.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ;
    String line;
    long cpt = 0;
      String datePrefix = "Oct 16 10:49:20 ";
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

      if ((! line.startsWith("#")) && line.length() > 0 && line.contains("dhcp")) {
          line = datePrefix.concat(line.replace("\"",""));
          avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));

          List<Map<String, List<Object>>> output = instance.parse(avroEvent);
          try {
                Map<String, List<Object>> out = output.get(0);
                OutUtils.printOut(out.toString());

                assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
               assertEquals(false, out.get("cefSignatureId").get(0).equals("Unkown"));
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