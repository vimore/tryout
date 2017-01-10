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

public class TaniumRunningProcessesToHostProcessMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(TaniumRunningProcessesToHostProcessMefTest.class);
    private MorphlineHarness morphlineTanium2Json;
    private AssertRecordOutCommand tanium2JsonOutCommand;

    public TaniumRunningProcessesToHostProcessMefTest() throws Exception {
        super(TaniumRunningProcessesToHostProcessMefTest.class.toString());
        this.tanium2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("tanium-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("tanium-json"));
        this.morphlineTanium2Json = new MorphlineHarness(morphlineContext, conf, "tanium-json");
        this.morphlineTanium2Json.startup(tanium2JsonOutCommand);
        this.morphlineId = "tanium-running_process";
        this.confFile = "hostprocessmef-tanium-running_processes.conf";
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
                "    \"Running-Processes-with-MD5-Hash\": {\n" +
                "        \"Path\": \"c:\\\\path\\\\to\\\\executable.exe\",\n" +
                "        \"MD5\": \"de39fc9b8a4eec9408ca46566a4b4df4\"\n" +
                "    },\n" +
                "    \"Metadata\": {\n" +
                "        \"QueryText\": \"...\",\n" +
                "        \"Timestamp\": \"2015-05-15 16:25:00Z\",\n" +
                "        \"JobGuid\": \"xxxxxxxx-xxxx-xxxx-xxxx-f52e924c94dd\",\n" +
                "        \"Requester\": \"SYSTEM\"\n" +
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
        assertEquals("externalLogSourceType", "tanium-Running-Processes-with-MD5-Hash", out.get("externalLogSourceType").get(0));
        assertEquals("logSourceType", "HostProcessMef", out.get("logSourceType").get(0));
        assertEquals("processFileMd5", "de39fc9b8a4eec9408ca46566a4b4df4", out.get("processFileMd5").get(0));
        assertEquals("processFilePath", "c:\\path\\to\\executable.exe", out.get("processFilePath").get(0));
        assertEquals("startTime", 1431707100000L, out.get("startTime").get(0));
        assertEquals("deviceAddress", "0.0.0.0", out.get("deviceAddress").get(0));
}


}
