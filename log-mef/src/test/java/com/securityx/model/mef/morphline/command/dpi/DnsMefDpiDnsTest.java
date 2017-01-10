package com.securityx.model.mef.morphline.command.dpi;

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

public class DnsMefDpiDnsTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(DnsMefDpiDnsTest.class);
    private MorphlineHarness morphlineDpi2Json;
    private AssertRecordOutCommand dpi2JsonOutCommand;

    public DnsMefDpiDnsTest() throws Exception {
        super(DnsMefDpiDnsTest.class.toString());
        this.dpi2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("dpi-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("dpi-json"));
        this.morphlineDpi2Json = new MorphlineHarness(morphlineContext, conf, "dpi-json");
        this.morphlineDpi2Json.startup(dpi2JsonOutCommand);
        this.morphlineId = "dpi-dns";
        this.confFile = "dpi-dns.conf";
    }

    private Record buildRecord(String line) {
        Record input = new Record();
        input.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(line.getBytes()));
        input.put(Fields.ATTACHMENT_MIME_TYPE, "application/json");
        boolean result = this.morphlineDpi2Json.feedRecords(input);
        assertEquals(true, result);
        OutUtils.printOut(this.dpi2JsonOutCommand.getNumRecords());
        Assert.assertTrue(this.dpi2JsonOutCommand.getNumRecords() == 1);
        Record out = this.dpi2JsonOutCommand.getRecord(0);
        this.dpi2JsonOutCommand.clear();
        return out;
    }

    @Test
    public void test_simple() throws FileNotFoundException {
        String line = "{\"data\": {\"startTime\": \"1397202576.372667\", \"transaction_id\": \"2859\", \"rtt\": \"0.00909\", \"query\": \"refer.ccbill.com\", \"query_type\": \"Host address\", \"name\": \"refer.ccbill.com\", \"ttl\": \"30\", \"host_addr\": \"64.38.212.57\", \"host_type\": \"IP address\"}, \"net\": {\"destNameOrIp\": \"10.240.62.89\", \"sourceNameOrIp\": \"172.16.0.23\", \"sourcePort\": \"35418\", \"destPort\": \"53\", \"transportProtocol\": \"udp\", \"timestamp\": 1397202506, \"application\": \"dns\"}, \"dpiSignatureId\": \"DNS response\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
    }

        @Test
    public void test1() throws FileNotFoundException {
        //
        String line = "{\"data\": {\"startTime\": \"1397202576.372667\", \"transaction_id\": \"2859\", \"rtt\": \"0.00909\", \"query\": \"refer.ccbill.com\", \"query_type\": \"Host address\", \"name\": \"refer.ccbill.com\", \"ttl\": \"30\", \"host_addr\": \"64.38.212.57\", \"host_type\": \"IP address\"}, \"net\": {\"destNameOrIp\": \"10.240.62.89\", \"sourceNameOrIp\": \"172.16.0.23\", \"sourcePort\": \"35418\", \"destPort\": \"53\", \"transportProtocol\": \"udp\", \"timestamp\": 1397202506, \"application\": \"dns\"}, \"dpiSignatureId\": \"DNS response\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);

        Record out = this.outCommand.getRecord(0);

        assertEquals("destinationNameOrIp", "10.240.62.89", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 53, out.get("destinationPort").get(0));
        assertEquals("dnsRecordIp", "64.38.212.57", out.get("dnsRecordIp").get(0));
        assertEquals("dnsRecordName", "refer.ccbill.com", out.get("dnsRecordName").get(0));
        assertEquals("dnsRecordTTL", 30, out.get("dnsRecordTTL").get(0));
        assertEquals("dnsRecordType", "IP address", out.get("dnsRecordType").get(0));
        assertEquals("dpiSignatureId", "DNS response", out.get("dpiSignatureId").get(0));
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

    }



    //
    public void test2() throws FileNotFoundException {
        //
        String line = "{\"data\": {\"startTime\": \"1397202576.372667\", \"transaction_id\": \"2859\", \"rtt\": \"0.00909\", \"query\": \"refer.ccbill.com\", \"query_type\": \"Host address\", \"name\": \"refer.ccbill.com\", \"ttl\": \"30\", \"host_addr\": \"64.38.212.57\", \"host_type\": \"IP address\"}, \"net\": {\"destNameOrIp\": \"10.240.62.89\", \"sourceNameOrIp\": \"172.16.0.23\", \"sourcePort\": \"35418\", \"destPort\": \"53\", \"transportProtocol\": \"udp\", \"timestamp\": 1397202506, \"application\": \"dns\"}, \"dpiSignatureId\": \"DNS response\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);


        line = "{\"data\": {\"startTime\": \"1397.372667\", \"transaction_id\": \"2859\", \"rtt\": \"0.00909\", \"query\": \"refer.ccbill.com\", \"query_type\": \"Host address\", \"name\": \"refer.ccbill.com\", \"ttl\": \"30\", \"host_addr\": \"64.38.212.57\", \"host_type\": \"IP address\"}, \"net\": {\"destNameOrIp\": \"10.240.62.89\", \"sourceNameOrIp\": \"172.16.0.23\", \"sourcePort\": \"35418\", \"destPort\": \"53\", \"transportProtocol\": \"udp\", \"timestamp\": 1397202506, \"application\": \"dns\"}, \"dpiSignatureId\": \"DNS response\"}";
        input = buildRecord(line);
        result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 2);

        Record out = this.outCommand.getRecord(0);

        assertEquals("destinationNameOrIp", "10.240.62.89", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 53, out.get("destinationPort").get(0));
        assertEquals("dnsRecordIp", "64.38.212.57", out.get("dnsRecordIp").get(0));
        assertEquals("dnsRecordName", "refer.ccbill.com", out.get("dnsRecordName").get(0));
        assertEquals("dnsRecordTTL", 30, out.get("dnsRecordTTL").get(0));
        assertEquals("dnsRecordType", "IP address", out.get("dnsRecordType").get(0));
        assertEquals("dpiSignatureId", "DNS response", out.get("dpiSignatureId").get(0));
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

        out = this.outCommand.getRecord(1);

        assertEquals("destinationNameOrIp", "10.240.62.89", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 53, out.get("destinationPort").get(0));
        assertEquals("dnsRecordIp", "64.38.212.57", out.get("dnsRecordIp").get(0));
        assertEquals("dnsRecordName", "refer.ccbill.com", out.get("dnsRecordName").get(0));
        assertEquals("dnsRecordTTL", 30, out.get("dnsRecordTTL").get(0));
        assertEquals("dnsRecordType", "IP address", out.get("dnsRecordType").get(0));
        assertEquals("dpiSignatureId", "DNS response", out.get("dpiSignatureId").get(0));
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

    }



    @Test
    public void dnsQueryWithError() throws FileNotFoundException {
        //
        String line = "{\"data\": {\"startTime\": \"1425603939.471620\", \"transaction_id\": \"10333\", \"reply_code\": \"No such name\", \"rtt\": \"0.020431\", \"query\": \"sb_invalid_host.yahoo.com\", \"query_type\": \"Host address\", \"name\": \"yahoo.com\", \"ttl\": \"600\", \"host\": \"ns1.yahoo.com\", \"host_type\": \"primary name server\"}, \"net\": {\"destNameOrIp\": \"75.75.75.75\", \"sourceNameOrIp\": \"192.168.1.97\", \"sourcePort\": \"23546\", \"destPort\": \"53\", \"transportProtocol\": \"udp\", \"timestamp\": 1425603939, \"application\": \"dns\", \"dpiFlowId\": \"1\"}, \"dpiSignatureId\": \"DNS response\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);

        Record out = this.outCommand.getRecord(0);

        assertEquals("destinationNameOrIp", "75.75.75.75", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 53, out.get("destinationPort").get(0));
        assertEquals("dnsRecordName", "yahoo.com", out.get("dnsRecordName").get(0));
        assertEquals("dnsRecordTTL", 600, out.get("dnsRecordTTL").get(0));
        assertEquals("dnsRecordType", "primary name server", out.get("dnsRecordType").get(0));
        assertEquals("dpiSignatureId", "DNS response", out.get("dpiSignatureId").get(0));
        assertEquals("externalLogSourceType", "Dpi-Dns", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "DnsMef", out.get("logSourceType").get(0));
        assertEquals("query", "sb_invalid_host.yahoo.com", out.get("query").get(0));
        assertEquals("queryRTT", 0.020431f, out.get("queryRTT").get(0));
        assertEquals("queryType", "Host address", out.get("queryType").get(0));
        assertEquals("sourceNameOrIp", "192.168.1.97", out.get("sourceNameOrIp").get(0));
        assertEquals("sourcePort", 23546, out.get("sourcePort").get(0));
        assertEquals("startTime", 1425603939471L, out.get("startTime").get(0));
        assertEquals("transactionId", "10333", out.get("transactionId").get(0));
        assertEquals("transportProtocol", "udp", out.get("transportProtocol").get(0));

    }

    //{\"data\": {\"requestVersion\": \"1.1\", \"responseStatus\": \"200\", \"responseServer\": \"Apache\", \"responseContentType\": \"image/gif\", \"responseContentLength\": \"606\"}, \"net\": {\"destNameOrIp\": \"66.179.217.49\", \"sourceNameOrIp\": \"192.168.15.4\", \"sourcePort\": \"34780\", \"destPort\": \"80\", \"transportProtocol\": \"tcp\", \"timestamp\": 1426009781, \"application\": \"http\", \"dpiFlowId\": \"1947\"}, \"dpiSignatureId\": \"URL request\"}



}
