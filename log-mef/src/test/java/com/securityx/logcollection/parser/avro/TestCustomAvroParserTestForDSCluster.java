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
public class TestCustomAvroParserTestForDSCluster extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForDSCluster() {
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
    public void test_Tanium() throws Exception {
        List<SupportedFormats> formats =SupportedFormats.genSupportedFormatList();
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "{\"Machine-Info\":{\"Computer-Name\":\"laptop804\",\"Client-IP-Address\":\"192.168.1.134\",\"Computer-Serial-Number\":\"ABCDFG123804\"},\"Running-Processes-with-MD5-Hash\":{\"Path\":\"C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe\",\"MD5\":\"78d3824650a866f3c38ae0079fc7e3dd\"},\"Metadata\":{\"QueryText\":\"SYSTEM\",\"Timestamp\":\"2016-10-07T01:00:01.000Z\",\"jobGuid\":\"xxxxxxxx-xxxx-xxxx-xxxx-f52e924c94dd\",\"Requester\":\"SYSTEM\"}}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium_feed");
        headers.put("hostname", "somehost");
        headers.put("taniumQuestion","Running-Processes-with-MD5-Hash");
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
                    assertEquals("logCollectionCategory", "tanium_feed", out.get("logCollectionCategory").get(0));
                    assertEquals("processFileMd5", "78d3824650a866f3c38ae0079fc7e3dd", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Program Files\\Amazon\\XenTools\\LiteAgent.exe", out.get("processFilePath").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "tanium-Running-Processes-with-MD5-Hash", out.get("externalLogSourceType").get(0));
                    assertEquals("deviceSerialNumber", "ABCDFG123804", out.get("deviceSerialNumber").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("processName", "LiteAgent.exe", out.get("processName").get(0));
                    // 1475802001000L -> Fri, 07 Oct 2016 01:00:01 GMT
                    assertEquals("startTime", 1475802001000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "laptop804", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
                    assertEquals("deviceHostName", "laptop804", out.get("deviceHostName").get(0));

                    break;
                default:
            }
        }
    }

    @Test
    public void test_Tanium2() throws Exception {
        List<SupportedFormats> formats =SupportedFormats.genSupportedFormatList();
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "{\"Machine-Info\":{\"Computer-Name\":\"laptop804\",\"Client-IP-Address\":\"192.168.1.134\",\"Computer-Serial-Number\":\"ABCDFG123804\"},\"Running-Processes-with-MD5-Hash\":{\"Path\":\"C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe\",\"MD5\":\"78d3824650a866f3c38ae0079fc7e3dd\"},\"Metadata\":{\"QueryText\":\"SYSTEM\",\"Timestamp\":\"2016-10-29T01:00:01.000Z\",\"jobGuid\":\"xxxxxxxx-xxxx-xxxx-xxxx-f52e924c94dd\",\"Requester\":\"SYSTEM\"}}";

        avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "tanium_feed");
        headers.put("hostname", "somehost");
        headers.put("taniumQuestion","Running-Processes-with-MD5-Hash");
        headers.put("timestamp", "1384693669604");
        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = null;
        output = instance.parse(avroEvent);
        Assert.assertEquals("number of records", 1, output.size());
        for (int i = 0; i < output.size(); i++) {
            Map<String, List<Object>> out = output.get(i);
            System.out.println(String.format("%02d : %s", i, out));
            switch (i) {
                case 0:
                    assertEquals("logCollectionCategory", "tanium_feed", out.get("logCollectionCategory").get(0));
                    assertEquals("processFileMd5", "78d3824650a866f3c38ae0079fc7e3dd", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "C:\\Program Files\\Amazon\\XenTools\\LiteAgent.exe", out.get("processFilePath").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
                    assertEquals("externalLogSourceType", "tanium-Running-Processes-with-MD5-Hash", out.get("externalLogSourceType").get(0));
                    assertEquals("deviceSerialNumber", "ABCDFG123804", out.get("deviceSerialNumber").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("processName", "LiteAgent.exe", out.get("processName").get(0));
                    // 1475802001000L -> Fri, 07 Oct 2016 01:00:01 GMT
                    assertEquals("startTime", 1477702801000L, out.get("startTime").get(0));
                    assertEquals("parserOutFormat", "HostProcessMef", out.get("parserOutFormat").get(0));
                    assertEquals("deviceNameOrIp", "laptop804", out.get("deviceNameOrIp").get(0));
                    assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
                    assertEquals("deviceHostName", "laptop804", out.get("deviceHostName").get(0));

                    break;
                default:
            }
        }
    }



    @Test
    public void test_BlueCoat() throws Exception {
        List<SupportedFormats> formats = SupportedFormats.genSupportedFormatList();
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "2016-10-07 01:00:01 789 192.168.1.42 laptop812.acmebank.xyz 304 TCP_MISS 225 294 GET http www.acmebank.xyz /demo/dataset/webpage.xml - Eric_Decker DIRECT www.acmebank.xyz text/plain \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1\" PROXIED Web%20Advertisements - 192.168.13.24 SG-HTTP-Service - none -";

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
                    //1475802001000L -> Fri, 07 Oct 2016 01:00:01 GMT
                    assertEquals("startTime", 1475802001000L, out.get("startTime").get(0));
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
                    assertEquals("destinationDnsDomainTLD", "acmebank.xyz", out.get("destinationDnsDomainTLD").get(0));
                    assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
                    assertEquals("sourceNameOrIp", "laptop812.acmebank.xyz", out.get("sourceNameOrIp").get(0));
                    assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
                    break;
                default:
            }
        }
    }

    @Test
    public void test_Windows() throws Exception {
        List<SupportedFormats> formats =SupportedFormats.genSupportedFormatList();
        AvroParser instance = AvroParser.BuildParser(this.morphlineFile, this.morphlineId, formats);
        Event avroEvent = new Event();
        String line = "MSWinEventLog\t0\tSecurity\t0\tFri Oct 07 01 00 01 2016\t4624\tMicrosoft-Windows-Security-Auditing\tNULL SID\tWell Known Group\tSuccess Audit\tacmebank.xyz\tLogon\tAn account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    New Logon:   Security ID:  S-1-5-21-1085031214-1563985344-725345818   Account Name:  Ivan_Bunin@acmebank.xyz    Account Domain:  acmebank.xyz   Logon ID:  0x221ebb06f   Logon GUID:  {6A904B52-6626-9211-C58E-A18CE597005F}    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name: laptop818    Source Network Address: 192.168.1.48   Source Port:  1234    Detailed Authentication Information:   Logon Process:  NtLmSsp   Authentication Package: NTLM   Transited Services: -   Package Name (NTLM only): NTLM V1   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service";

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
                    assertEquals("deviceDnsDomain", "acmebank.xyz", out.get("deviceDnsDomain").get(0));
                    assertEquals("sourcePort", 1234, out.get("sourcePort").get(0));
                    assertEquals("sourceHostName", "laptop818", out.get("sourceHostName").get(0));
                    assertEquals("destinationLogonGUID", "{6A904B52-6626-9211-C58E-A18CE597005F}", out.get("destinationLogonGUID").get(0));
                    assertEquals("destinationLogonID", "0x221ebb06f", out.get("destinationLogonID").get(0));
                    assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
                    assertEquals("destinationUserName", "ivan_bunin@acmebank.xyz", out.get("destinationUserName").get(0));
                    assertEquals("keyLength", 0, out.get("keyLength").get(0));
                    assertEquals("transitedService", "-", out.get("transitedService").get(0));
                    assertEquals("logonProcess", "NtLmSsp", out.get("logonProcess").get(0));
                    // 1475802001000L -> Fri, 07 Oct 2016 01:00:01 GMT
                    assertEquals("startTime", 1475802001000L, out.get("startTime").get(0));
                    assertEquals("packageName", "NTLM V1", out.get("packageName").get(0));
                    assertEquals("cefEventName", "An account was successfully logged on.", out.get("cefEventName").get(0));
                    assertEquals("destinationNtDomain", "acmebank.xyz", out.get("destinationNtDomain").get(0));
                    assertEquals("sourceAddress", "192.168.1.48", out.get("sourceAddress").get(0));
                    assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
                    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
                    assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
                    assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
                    assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
                    assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
                    assertEquals("authenticationPackage", "NTLM", out.get("authenticationPackage").get(0));
                    assertEquals("destinationSecurityID", "S-1-5-21-1085031214-1563985344-725345818", out.get("destinationSecurityID").get(0));
                    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
                    assertEquals("deviceUserName", "null sid", out.get("deviceUserName").get(0));
                    assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
                    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
                    assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
                    assertEquals("deviceNameOrIp", "acmebank.xyz", out.get("deviceNameOrIp").get(0));
                    assertEquals("sourceNameOrIp", "laptop818", out.get("sourceNameOrIp").get(0));
                    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
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
