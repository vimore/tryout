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

public class TaniumAutorunProgramDetailToHostJobMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(TaniumAutorunProgramDetailToHostJobMefTest.class);
    private MorphlineHarness morphlineTanium2Json;
    private AssertRecordOutCommand tanium2JsonOutCommand;

    public TaniumAutorunProgramDetailToHostJobMefTest() throws Exception {
        super(TaniumAutorunProgramDetailToHostJobMefTest.class.toString());
        this.tanium2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("tanium-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("tanium-json"));
        this.morphlineTanium2Json = new MorphlineHarness(morphlineContext, conf, "tanium-json");
        this.morphlineTanium2Json.startup(tanium2JsonOutCommand);
        this.morphlineId = "tanium-autorun_program_details";
        this.confFile = "hostjobmef-tanium-autorun_program_details.conf";
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
                "    \"AutoRun-Program-Details\": {\n" +
                "        \"Category\": \"Logon\",\n" +
                "        \"Publisher\": \"Publisher Name\",\n" +
                "        \"SHA-1-Hash\": \"47267f943f060e36604d56c8895a6eece063d9a1\",\n" +
                "        \"Enabled\": \"enabled\",\n" +
                "        \"Launch-String\": \" c:\\\\path\\\\to\\\\executable.exe \\\\/startup\",\n" +
                "        \"Version\": \"1.0.13\",\n" +
                "        \"Image-Path\": \" c:\\\\path\\\\to\\\\executable.exe\",\n" +
                "        \"Entry\": \"Redirector\",\n" +
                "        \"MD5\": \"6f68f63794097e54f36474ed4384b759\",\n" +
                "        \"Description\": \"Description\",\n" +
                "        \"SHA-256-Hash\": \"9834876dcfb05cb167a5c24953eba58c4ac89b1adf57f28f2f9d09af107ee8f0\",\n" +
                "        \"Entry-Location\": \"HKLM\\\\SOFTWARE\\\\Wow6432Node\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\"\n" +
                "    },\n" +
                "    \"Metadata\": {\n" +
                "        \"QueryText\": \"...\",\n" +
                "        \"Timestamp\": \"2015-05-15 15:05:04Z\",\n" +
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

        assertEquals("JobLocation", "HKLM\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Run", out.get("JobLocation").get(0));
        assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
        assertEquals("deviceHostName", "hostname", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "HOSTNAME", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "tanium-AutoRun-Program-Details", out.get("externalLogSourceType").get(0));
        assertEquals("jobCmd", " c:\\path\\to\\executable.exe \\/startup", out.get("jobCmd").get(0));
        assertEquals("jobName", "Redirector", out.get("jobName").get(0));
        assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
        assertEquals("processFileMd5", "6f68f63794097e54f36474ed4384b759", out.get("processFileMd5").get(0));
        assertEquals("processFilePath", " c:\\path\\to\\executable.exe", out.get("processFilePath").get(0));
        assertEquals("processFileVersion", "1.0.13", out.get("processFileVersion").get(0));
        assertEquals("deviceSerialNumber", "ABCDFG123456", out.get("deviceSerialNumber").get(0));
        assertEquals("startTime", 1431702304000L, out.get("startTime").get(0));
}


}
