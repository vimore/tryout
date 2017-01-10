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

public class TaniumListenPortsToHostPortMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(TaniumListenPortsToHostPortMefTest.class);
    private MorphlineHarness morphlineTanium2Json;
    private AssertRecordOutCommand tanium2JsonOutCommand;

    public TaniumListenPortsToHostPortMefTest() throws Exception {
        super(TaniumListenPortsToHostPortMefTest.class.toString());
        this.tanium2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("tanium-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("tanium-json"));
        this.morphlineTanium2Json = new MorphlineHarness(morphlineContext, conf, "tanium-json");
        this.morphlineTanium2Json.startup(tanium2JsonOutCommand);
        this.morphlineId = "tanium-listen_ports_with_md5";
        this.confFile = "hostportmef-tanium-listen_ports_with_md5.conf";
    }

    private Record buildRecord(String line) {
        Record input = new Record();
        input.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(line.getBytes()));
        input.put(Fields.ATTACHMENT_MIME_TYPE,"application/json");

        return input;
    }

    @Test
    public void test_simple() throws FileNotFoundException {
        String line = "{\n" +
                "    \"Machine-Info\": {\n" +
                "        \"Computer-Name\": \"HOSTNAME\",\n" +
                "        \"Client-IP-Address\": \"0.0.0.0\",\n" +
                "        \"Computer-Serial-Number\": \"ABCDFG123456\"\n" +
                "    },\n" +
                "    \"Metadata\": {\n" +
                "        \"QueryText\": \"...\",\n" +
                "        \"Timestamp\": \"2015-05-15 15:20:00Z\",\n" +
                "        \"JobGuid\": \"xxxxxxxx-xxxx-xxxx-xxxx-f52e924c94dd\",\n" +
                "        \"Requester\": \"SYSTEM\"\n" +
                "    },\n" +
                "    \"Listen-Ports-with-MD5-Hash\": {\n" +
                "        \"MD5-Hash\": \"6f68f63794097e54f36474ed4384b759\",\n" +
                "        \"Protocol\": \"TCP\",\n" +
                "        \"Name\": \"Host Process for Windows Services\",\n" +
                "        \"Process\": \"svchost.exe\",\n" +
                "        \"IP-Address\": \"0.0.0.0\",\n" +
                "        \"Port\": \"49153\"\n" +
                "    }\n" +
                "}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertEquals("number of records", 1, this.outCommand.getNumRecords());
        Record out = this.outCommand.getRecord(0);

        assertEquals("cefSignatureId", "Seen", out.get("cefSignatureId").get(0));
        assertEquals("deviceHostName", "hostname", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "HOSTNAME", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceSerialNumber", "ABCDFG123456", out.get("deviceSerialNumber").get(0));
        assertEquals("externalLogSourceType", "tanium-Listen-Ports-with-MD5-Hash", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "HostPortMef", out.get("logSourceType").get(0));
        assertEquals("processFileMd5", "6f68f63794097e54f36474ed4384b759", out.get("processFileMd5").get(0));
        assertEquals("processFilePath", "svchost.exe", out.get("processFilePath").get(0));
        assertEquals("processListenPort", 49153, out.get("processListenPort").get(0));
        assertEquals("processListenAddress", "0.0.0.0", out.get("processListenAddress").get(0));
        assertEquals("tranportProtocol", "TCP", out.get("tranportProtocol").get(0));
        assertEquals("startTime", 1431703200000L, out.get("startTime").get(0));
}


}
