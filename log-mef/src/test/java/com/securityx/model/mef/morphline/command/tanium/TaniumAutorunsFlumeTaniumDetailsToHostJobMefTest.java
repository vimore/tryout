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

public class TaniumAutorunsFlumeTaniumDetailsToHostJobMefTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(TaniumAutorunsFlumeTaniumDetailsToHostJobMefTest.class);
    private MorphlineHarness morphlineTanium2Json;
    private AssertRecordOutCommand tanium2JsonOutCommand;

    public TaniumAutorunsFlumeTaniumDetailsToHostJobMefTest() throws Exception {
        super(TaniumAutorunsFlumeTaniumDetailsToHostJobMefTest.class.toString());
        this.tanium2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
        Config conf = MorphlineResourceLoader.getConfFile("tanium-json.conf");
        assertTrue("conf file " + conf.origin(), conf.origin().toString().contains("tanium-json"));
        this.morphlineTanium2Json = new MorphlineHarness(morphlineContext, conf, "tanium-json");
        this.morphlineTanium2Json.startup(tanium2JsonOutCommand);
        this.morphlineId = "tanium-autoruns_details";
        this.confFile = "hostprocessmef-tanium-autoruns_details.conf";
    }

    private Record buildRecord(String line) {
        Record input = new Record();
        input.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(line.getBytes()));
        input.put(Fields.ATTACHMENT_MIME_TYPE,"application/xml");
        input.put("taniumQuestion","Host-Autoruns details");
        input.put("logCollectionCategory", "flume_tanium");

        return input;
    }

    @Test
    public void test_simple() throws FileNotFoundException {
        String line = "\n<result_sets><now>2015/06/12 14:19:37 GMT-0000</now>\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>255</saved_question_id><question_id>236065</question_id><report_count>5</report_count><seconds_since_issued>120</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>9</tested><passed>9</passed><mr_tested>9</mr_tested><mr_passed>9</mr_passed><estimated_total>9</estimated_total><select_count>1</select_count><cs><c><wh>2717397933</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>UserName</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>AutorunEntry</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>RegistryIntArch</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>RegistryKey</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>RegistryValue</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>FileMd5</dn><rt>1</rt></c><c><wh>2717397933</wh><dn>FilePath</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>5</filtered_row_count><filtered_row_count_machines>11</filtered_row_count_machines><item_count>5</item_count><rs><r><id>0</id><cid>0</cid><c><v>[no results]</v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v></v></c><c><v>7</v></c></r><r><id>1944769335</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>jyria</v></c><c><v>jyria</v></c><c><v>64-bit</v></c><c><v>\\S-1-5-21-3279101367-1625492503-1626788204-1013\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\</v></c><c><v>c:\\cygwin64\\bin\\true.exe /someparam</v></c><c><v>c1b3260b61dca970144a3c9362383d5d</v></c><c><v>c:\\cygwin64\\bin\\true.exe</v></c><c><v>1</v></c></r><r><id>2116362154</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>jyria</v></c><c><v>jyria</v></c><c><v>32-bit</v></c><c><v>\\S-1-5-21-3279101367-1625492503-1626788204-1013\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\</v></c><c><v>c:\\cygwin64\\bin\\true.exe /someparam</v></c><c><v>c1b3260b61dca970144a3c9362383d5d</v></c><c><v>c:\\cygwin64\\bin\\true.exe</v></c><c><v>1</v></c></r><r><id>2217548849</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>N/A</v></c><c><v>nwiz</v></c><c><v>64-bit</v></c><c><v>\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\</v></c><c><v>C:\\Program Files\\NVIDIA Corporation\\nView\\nwiz.exe /installquiet</v></c><c><v>af29e58a2f20d0bffd3eaec34a1ea10c</v></c><c><v>c:\\program files\\nvidia corporation\\nview\\nwiz.exe</v></c><c><v>1</v></c></r><r><id>3400111324</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>N/A</v></c><c><v>jyria</v></c><c><v>64-bit</v></c><c><v>\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\</v></c><c><v>C:\\cygwin64\\bin\\true.exe</v></c><c><v>c1b3260b61dca970144a3c9362383d5d</v></c><c><v>c:\\cygwin64\\bin\\true.exe</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\n";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertEquals("number of records", 4, this.outCommand.getNumRecords());
        for (int i=0; i < this.outCommand.getNumRecords();i++) {
            Record out = this.outCommand.getRecord(i);
            OutUtils.printOut(String.format("%02d : %s",i, out));
            switch (i){
                case 0:
                    assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceUserName", "jyria", out.get("deviceUserName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Autoruns details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
                    assertEquals("jobName", "jyria", out.get("jobName").get(0));
                    assertEquals("jobLocation", "64-bit/\\S-1-5-21-3279101367-1625492503-1626788204-1013\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\", out.get("jobLocation").get(0));
                    assertEquals("processFileMd5", "c1b3260b61dca970144a3c9362383d5d", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "c:\\cygwin64\\bin\\true.exe", out.get("processFilePath").get(0));
                    assertEquals("jobCmd", "c:\\cygwin64\\bin\\true.exe /someparam", out.get("jobCmd").get(0));
                    assertEquals("startTime", 1434118777000L, out.get("startTime").get(0));
                    assertEquals("taniumQuestion", "Host-Autoruns details", out.get("taniumQuestion").get(0));

                    break;

                case 1:
                    assertEquals("{cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceUserName", "jyria", out.get("deviceUserName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Autoruns details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "c1b3260b61dca970144a3c9362383d5d", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "c:\\cygwin64\\bin\\true.exe", out.get("processFilePath").get(0));
                    assertEquals("jobName", "jyria", out.get("jobName").get(0));
                    assertEquals("jobLocation", "32-bit/\\S-1-5-21-3279101367-1625492503-1626788204-1013\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\", out.get("jobLocation").get(0));
                    assertEquals("jobCmd", "c:\\cygwin64\\bin\\true.exe /someparam", out.get("jobCmd").get(0));
                    assertEquals("startTime", 1434118777000L, out.get("startTime").get(0));
                    assertEquals("taniumQuestion", "Host-Autoruns details", out.get("taniumQuestion").get(0));


                    break;
                case 2:
                    assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
                    assertEquals("deviceHostName", "rdp-gw", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "RDP-GW", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceUserName", "N/A", out.get("deviceUserName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Autoruns details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "af29e58a2f20d0bffd3eaec34a1ea10c", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "c:\\program files\\nvidia corporation\\nview\\nwiz.exe", out.get("processFilePath").get(0));
                    assertEquals("jobName", "nwiz", out.get("jobName").get(0));
                    assertEquals("jobLocation", "64-bit/\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\", out.get("jobLocation").get(0));
                    assertEquals("jobCmd", "C:\\Program Files\\NVIDIA Corporation\\nView\\nwiz.exe /installquiet", out.get("jobCmd").get(0));
                    assertEquals("startTime", 1434118777000L, out.get("startTime").get(0));
                    assertEquals("taniumQuestion", "Host-Autoruns details", out.get("taniumQuestion").get(0));

                    break;
                case 3:
                    assertEquals("cefSignatureId", "Autorun", out.get("cefSignatureId").get(0));
                    assertEquals("deviceHostName", "win-osnmci3gjj1", out.get("deviceHostName").get(0));
                    assertEquals("deviceNameOrIp", "WIN-OSNMCI3GJJ1", out.get("deviceNameOrIp").get(0));
                    assertEquals("deviceUserName", "N/A", out.get("deviceUserName").get(0));
                    assertEquals("externalLogSourceType", "tanium-Host-Autoruns details", out.get("externalLogSourceType").get(0));
                    assertEquals("logSourceType", "HostJobMef", out.get("logSourceType").get(0));
                    assertEquals("processFileMd5", "c1b3260b61dca970144a3c9362383d5d", out.get("processFileMd5").get(0));
                    assertEquals("processFilePath", "c:\\cygwin64\\bin\\true.exe", out.get("processFilePath").get(0));
                    assertEquals("jobName", "jyria", out.get("jobName").get(0));
                    assertEquals("jobLocation", "64-bit/\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\", out.get("jobLocation").get(0));
                    assertEquals("jobCmd", "C:\\cygwin64\\bin\\true.exe", out.get("jobCmd").get(0));
                    assertEquals("startTime", 1434118777000L, out.get("startTime").get(0));
                    assertEquals("taniumQuestion", "Host-Autoruns details", out.get("taniumQuestion").get(0));


                    break;
                default:
            }
        }
    }



}
