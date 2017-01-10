package com.securityx.model.mef.morphline.command.tanium;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

public class TaniumHetInformationToHetMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(TaniumHetInformationToHetMefTest.class);
    private MorphlineHarness morphlineTanium2Json;
    private AssertRecordOutCommand tanium2JsonOutCommand;

    public TaniumHetInformationToHetMefTest() throws Exception {
        super(TaniumHetInformationToHetMefTest.class.toString());
        this.tanium2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("tanium-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("tanium-json"));
        this.morphlineTanium2Json = new MorphlineHarness(morphlineContext, conf, "tanium-json");
        this.morphlineTanium2Json.startup(tanium2JsonOutCommand);
        this.morphlineId = "tanium-het_informations";
        this.confFile = "hetmef-tanium-het_information.conf";
    }

    private Record buildRecord(String line) {
        Record input = new Record();
        input.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(line.getBytes()));
        input.put(Fields.ATTACHMENT_MIME_TYPE,"application/json");
        boolean result = this.morphlineTanium2Json.feedRecords(input);
        assertEquals(true, result);
        OutUtils.printOut(this.tanium2JsonOutCommand.getNumRecords());
        Assert.assertTrue(this.tanium2JsonOutCommand.getNumRecords() == 1);
        Record out = this.tanium2JsonOutCommand.getRecord(0);
        this.tanium2JsonOutCommand.clear();
        return out;
    }

    @Test
    public void test_simple() throws FileNotFoundException {
        String line = "{\"question\": \"het informations\", \"measure\": \"\\n<result_sets><now>2015/05/22 07:30:59 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>227</saved_question_id><question_id>168411</question_id><report_count>2</report_count><seconds_since_issued>107</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>2</tested><passed>2</passed><mr_tested>2</mr_tested><mr_passed>2</mr_passed><estimated_total>2</estimated_total><select_count>1</select_count><cs><c><wh>4210360868</wh><dn>het information</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>82443432</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;WIN-OSNMCI3GJJ1&quot;\\nIPAddress = {&quot;10.167.170.124&quot;, &quot;fe80::ccea:b9e7:d8f2:4952&quot;}\\nMACAddress = &quot;22:00:0B:47:0B:A4\\n</v></c><c><v>1</v></c></r><r><id>810612956</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;RDP-GW&quot;\\nIPAddress = {&quot;192.168.12.9&quot;, &quot;fe80::cd4b:f92a:5ce8:6216&quot;}\\nMACAddress = &quot;12:4A:FB:CB:8B:93\\n</v></c><c><v>1</v></c></r><r><id>1270274401</id><cid>0</cid><c><v>DNSDomain = &quot;ec2.internal&quot;\\nDNSHostName = &quot;RDP-GW&quot;\\nIPAddress = {&quot;192.168.12.27&quot;}\\nMACAddress = &quot;12:7F:C8:56:84:17\\n</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertEquals("number of records", 3, this.outCommand.getNumRecords());
        for (int i=0; i < this.outCommand.getNumRecords();i++) {
            Record out = this.outCommand.getRecord(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("destinationAddress", "10.167.170.124", out.get("destinationAddress").get(0));
                    assertEquals("destinationDnsDomain", "ec2.internal", out.get("destinationDnsDomain").get(0));
                    assertEquals("destinationMacAddress", "22:00:0B:47:0B:A4", out.get("destinationMacAddress").get(0));
                    assertEquals("destinationNameOrIp", "WIN-OSNMCI3GJJ1", out.get("destinationNameOrIp").get(0));
                    assertEquals("externalLogSourceType", "tanium-het informations", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
                    assertEquals("taniumQuestion", "het informations", out.get("taniumQuestion").get(0));
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
                    assertEquals("taniumQuestion", "het informations", out.get("taniumQuestion").get(0));
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
                    assertEquals("taniumQuestion", "het informations", out.get("taniumQuestion").get(0));
                    assertEquals("startTime", 1432279859000L, out.get("startTime").get(0));
                    assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));

                    break;
                default:
            }
        }
    }

    /*@Test
    public void test1() throws FileNotFoundException {
        //
        String line = "{\"data\": {\"startTime\": \"1425684968.764013\", \"crt_md5\": \"FFFFFF5DFF16FF0EFF77FF0BFFFF3CFFFFFFD5\", \"crt_sln\": \"52FF27230842FF065AFF487B39FF6AFF00\", \"crt_issuer\": \"VeriSign Class 3 Secure Server CA - G3\", \"crt_noValidBefore\": \"140811000000Z\", \"crt_noValidAfter\": \"151102235959Z\", \"crt_commonName\": \"textchat.bankofamerica.com\", \"crt_subjAltName\": \"textchat.bankofamerica.com\"}, \"net\": {\"destNameOrIp\": \"208.89.15.8\", \"sourceNameOrIp\": \"192.168.1.56\", \"sourcePort\": \"53232\", \"destPort\": \"443\", \"transportProtocol\": \"tcp\", \"timestamp\": 1425684968, \"application\": \"ssl\", \"dpiFlowId\": \"5\"}, \"dpiSignatureId\": \"SSL CERTIFICATE\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);

        Record out = this.outCommand.getRecord(0);

        assertEquals("certCommonName", "textchat.bankofamerica.com", out.get("certCommonName").get(0));
        assertEquals("certIssuer", "VeriSign Class 3 Secure Server CA - G3", out.get("certIssuer").get(0));
        assertEquals("certMd5", "FFFFFF5DFF16FF0EFF77FF0BFFFF3CFFFFFFD5", out.get("certMd5").get(0));
        //Mon, 02 Nov 2015 23:59:59 GMT
        assertEquals("certNoValidAfter", 1446508799000L, out.get("certNoValidAfter").get(0));
        //Mon, 11 Aug 2014 00:00:00 GMT
        assertEquals("certNoValidBefore", 1407715200000L, out.get("certNoValidBefore").get(0));
        assertEquals("certSerial", "52FF27230842FF065AFF487B39FF6AFF00", out.get("certSerial").get(0));
        assertEquals("certSubject", "textchat.bankofamerica.com", out.get("certSubject").get(0));
        assertEquals("destinationNameOrIp", "208.89.15.8", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 443, out.get("destinationPort").get(0));
        assertEquals("dpiSignatureId", "SSL CERTIFICATE", out.get("dpiSignatureId").get(0));
        assertEquals("externalLogSourceType", "Dpi-ssl", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "CertMef", out.get("logSourceType").get(0));
        assertEquals("sourceAddress", "192.168.1.56", out.get("sourceAddress").get(0));
        assertEquals("sourceNameOrIp", "192.168.1.56", out.get("sourceNameOrIp").get(0));
        assertEquals("sourcePort", 53232, out.get("sourcePort").get(0));
        assertEquals("startTime", 1425684968764L, out.get("startTime").get(0));
        assertEquals("transportProtocol", "tcp", out.get("transportProtocol").get(0));
    }

      */


}
