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

public class TaniumCpuConsumptionToHostCpuMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(TaniumCpuConsumptionToHostCpuMefTest.class);
    private MorphlineHarness morphlineTanium2Json;
    private AssertRecordOutCommand tanium2JsonOutCommand;

    public TaniumCpuConsumptionToHostCpuMefTest() throws Exception {
        super(TaniumCpuConsumptionToHostCpuMefTest.class.toString());
        this.tanium2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("tanium-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("tanium-json"));
        this.morphlineTanium2Json = new MorphlineHarness(morphlineContext, conf, "tanium-json");
        this.morphlineTanium2Json.startup(tanium2JsonOutCommand);
        this.morphlineId = "tanium-cpu_consumptions";
        this.confFile = "hostcpumef-tanium-cpu_consumption.conf";
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
        String line = "{\"question\": \"HOST-CPU consumptions\", \"measure\": \"\\n<result_sets><now>2015/05/27 15:20:55 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>233</saved_question_id><question_id>183899</question_id><report_count>4</report_count><seconds_since_issued>0</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>98976143</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>98976143</wh><dn>CPU Consumption</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v>1</v></c></r><r><id>1661264119</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>2 %</v></c><c><v>1</v></c></r><r><id>3260989120</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>1 %</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertEquals("number of records", 2, this.outCommand.getNumRecords());
        for (int i=0; i < this.outCommand.getNumRecords();i++) {
            Record out = this.outCommand.getRecord(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("cpuConsumption", 2.0f, out.get("cpuConsumption").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("taniumQuestion", "HOST-CPU consumptions", out.get("taniumQuestion").get(0));
                    assertEquals("startTime", 1432740055000L, out.get("startTime").get(0));
                    break;

                case 1:
                    assertEquals("cpuConsumption", 1.0f, out.get("cpuConsumption").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("taniumQuestion", "HOST-CPU consumptions", out.get("taniumQuestion").get(0));
                    assertEquals("startTime", 1432740055000L, out.get("startTime").get(0));
                    break;
                default:
            }
        }
    }

    @Test
    public void test_CpuAndMem() throws FileNotFoundException {
        String line = "{\"question\": \"HOST-CPU consumptions\", \"measure\": \"<result_sets><now>2015/05/29 14:38:51 GMT-0000</now>\\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>233</saved_question_id><question_id>189709</question_id><report_count>3</report_count><seconds_since_issued>109</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>3</tested><passed>3</passed><mr_tested>3</mr_tested><mr_passed>3</mr_passed><estimated_total>3</estimated_total><select_count>1</select_count><cs><c><wh>98976143</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>98976143</wh><dn>CPU Consumption</dn><rt>1</rt></c><c><wh>98976143</wh><dn>Mem Consumption</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>3</filtered_row_count><filtered_row_count_machines>3</filtered_row_count_machines><item_count>3</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v>1</v></c></r><r><id>1582003653</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>2.15 %</v></c><c><v>34.97 %</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\\n\\n\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertEquals("number of records", 1, this.outCommand.getNumRecords());
        for (int i=0; i < this.outCommand.getNumRecords();i++) {
            Record out = this.outCommand.getRecord(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:

                    assertEquals("cpuConsumption", 2.15f, out.get("cpuConsumption").get(0));
                    assertEquals("memConsumption", 34.97f, out.get("memConsumption").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("externalLogSourceType", "tanium-HOST-CPU consumptions", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostCpuMef", out.get("logSourceType").get(0));
                    assertEquals("taniumQuestion", "HOST-CPU consumptions", out.get("taniumQuestion").get(0));
                    assertEquals("startTime", 1432910331000L, out.get("startTime").get(0));
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
