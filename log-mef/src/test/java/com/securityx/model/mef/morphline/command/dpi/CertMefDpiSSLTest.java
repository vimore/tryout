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

public class CertMefDpiSSLTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(CertMefDpiSSLTest.class);
    private MorphlineHarness morphlineDpi2Json;
    private AssertRecordOutCommand dpi2JsonOutCommand;

    public CertMefDpiSSLTest() throws Exception {
        super(CertMefDpiSSLTest.class.toString());
        this.dpi2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("dpi-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("dpi-json"));
        this.morphlineDpi2Json = new MorphlineHarness(morphlineContext, conf, "dpi-json");
        this.morphlineDpi2Json.startup(dpi2JsonOutCommand);
        this.morphlineId = "dpi-ssl";
        this.confFile = "dpi-ssl.conf";
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
        String line = "{\"data\": {\"startTime\": \"1425684968.764013\", \"crt_md5\": \"FFFFFF5DFF16FF0EFF77FF0BFFFF3CFFFFFFD5\", \"crt_sln\": \"52FF27230842FF065AFF487B39FF6AFF00\", \"crt_issuer\": \"VeriSign Class 3 Secure Server CA - G3\", \"crt_noValidBefore\": \"140811000000Z\", \"crt_noValidAfter\": \"151102235959Z\", \"crt_commonName\": \"textchat.bankofamerica.com\", \"crt_subjAltName\": \"textchat.bankofamerica.com\"}, \"net\": {\"destNameOrIp\": \"208.89.15.8\", \"sourceNameOrIp\": \"192.168.1.56\", \"sourcePort\": \"53232\", \"destPort\": \"443\", \"transportProtocol\": \"tcp\", \"timestamp\": 1425684968, \"application\": \"ssl\", \"dpiFlowId\": \"5\"}, \"dpiSignatureId\": \"SSL CERTIFICATE\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
    }

    @Test
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




}
