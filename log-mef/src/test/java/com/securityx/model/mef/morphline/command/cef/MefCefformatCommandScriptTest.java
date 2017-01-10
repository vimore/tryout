package com.securityx.model.mef.morphline.command.cef;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefCefformatCommandScriptTest extends LogCollectionAbstractTest {
    private Logger logger = LoggerFactory.getLogger(MefCefformatCommandScriptTest.class);
    public MefCefformatCommandScriptTest() {
      super(MefCefformatCommandScriptTest.class.toString());
        this.morphlineId = "cefKvpCommand";
        this.confFile = "test/test-kvp-command.conf";
        this.verboseOutputCommand = false;
    }
    /*
     //perf test not usefull to run each time
    @Test
    public void testCefFormatCommand() throws FileNotFoundException {

        String[] input = new String[100000];
        String line = " eventId=1901795439 externalId=4672 msg=Subject:   Security ID:  S-1-5-18   Account Name:  SYSTEM   Account Domain:  NT AUTHORITY   Logon ID:  0x3e7    Privileges:  SeAssignPrimaryTokenPrivilege     SeTcbPrivilege     SeSecurityPrivilege     SeTakeOwnershipPrivilege     SeLoadDriverPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeDebugPrivilege     SeAuditPrivilege     SeSystemEnvironmentPrivilege     SeImpersonatePrivilege     User: NT AUTHORITY\\\\SYSTEM     ComputerName: xxxxxxxxxxxx.xxxxxx.jpmchase.net categorySignificance=/Normal categoryBehavior=/Authorization/Add categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438713330191 cat=Security deviceSeverity=4 rt=1438709727000 dhost=xxxxxxxxxxxx.xxxxxx.jpmchase.net dntdom=NT AUTHORITY duser=SYSTEM duid=0x3e7 dpriv=SeAssignPrimaryTokenPrivilege cs2=Special Logon cs3=Microsoft-Windows-Security-Auditing cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxxxxxx.xxx.xx.jpmchase.net agt=xxx.xxx.xx.xx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.0 atz=US/Eastern aid=3mTr+5kkBABCBFAZn0olEsA\\=\\= at=syslog dvchost=xxxxxxxxxxxx.xxxxxx.jpmchase.net deviceNtDomain=NT AUTHORITY dtz=US/Eastern _cefVer=0.1 ad.ExtraParam0=SeTcbPrivilege ad.ExtraParam3=SeLoadDriverPrivilege ad.ExtraParam4=SeBackupPrivilege ad.ExtraParam1=SeSecurityPrivilege ad.ExtraParam2=SeTakeOwnershipPrivilege ad.ExtraParam7=SeAuditPrivilege ad.ExtraParam8=SeSystemEnvironmentPrivilege ad.ExtraParam5=SeRestorePrivilege ad.ExtraParam6=SeDebugPrivilege ad.ExtraParam9=SeImpersonatePrivilege";
        for (int i = 0; i < input.length; i++) {
            input[i] = line;
        }

        for (int j=0; j< 10; j++) {

            boolean result = doTest(input);
            assertEquals(true, result);
            this.outCommand.clear();
        }
    }
    */

    @Test
    public void testCefFormatCommandEscapedChar() throws FileNotFoundException {

        String line = "cs1Label='Compliancy Policy Name' cs2Label='Compliancy Policy Subrule Name' cs3Label='Host Compliancy Status' cs4Label='Compliancy Event Trigger' cs1='Operating System'  cs2=Updated cs3=yes cs4='CounterAct Action' dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogi\\=crocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
        boolean result= doTest(line);
        assertEquals(true, result);
    }

    @Test
    public void testCefFormatCommandEscapedChar_() throws FileNotFoundException {

        String line = "cs1Label='Compliancy Policy Name' cs2Label='Compliancy Policy Subrule Name' cs3Label='Host Compliancy Status' cs4Label='Compliancy Event Trigger' cs1='Operating System'  cs2=Updated cs3=yes cs4='CounterAct Action' dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogi\\=crocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=";
        boolean result= doTest(line);
        assertEquals(true, result);
    }


    //

    @Test
    public void testCefFormatCommandTrueCef() throws FileNotFoundException {

        String line =  " eventId=1901795439 externalId=4672 msg=Subject:   Security ID:  S-1-5-18   Account Name:  SYSTEM   Account Domain:  NT AUTHORITY   Logon ID:  0x3e7    Privileges:  SeAssignPrimaryTokenPrivilege     SeTcbPrivilege     SeSecurityPrivilege     SeTakeOwnershipPrivilege     SeLoadDriverPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeDebugPrivilege     SeAuditPrivilege     SeSystemEnvironmentPrivilege     SeImpersonatePrivilege     User: NT AUTHORITY\\\\SYSTEM     ComputerName: xxxxxxxxxxxx.xxxxxx.jpmchase.net categorySignificance=/Normal categoryBehavior=/Authorization/Add categoryDeviceGroup=/Operating System catdt=Log Consolidator categoryOutcome=/Success categoryObject=/Host/Operating System art=1438713330191 cat=Security deviceSeverity=4 rt=1438709727000 dhost=xxxxxxxxxxxx.xxxxxx.jpmchase.net dntdom=NT AUTHORITY duser=SYSTEM duid=0x3e7 dpriv=SeAssignPrimaryTokenPrivilege cs2=Special Logon cs3=Microsoft-Windows-Security-Auditing cs1Label=Accesses cs2Label=EventlogCategory cs3Label=EventSource cs4Label=Reason or Error Code cs5Label=Authentication Package Name cn1Label=LogonType cn2Label=CrashOnAuditFail cn3Label=Count ahost=xxxxxxxxx.xxx.xx.jpmchase.net agt=xxx.xxx.xx.xx agentZoneURI=/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN) av=7.0.6.7189.0 atz=US/Eastern aid=3mTr+5kkBABCBFAZn0olEsA\\=\\= at=syslog dvchost=xxxxxxxxxxxx.xxxxxx.jpmchase.net deviceNtDomain=NT AUTHORITY dtz=US/Eastern _cefVer=0.1 ad.ExtraParam0=SeTcbPrivilege ad.ExtraParam3=SeLoadDriverPrivilege ad.ExtraParam4=SeBackupPrivilege ad.ExtraParam1=SeSecurityPrivilege ad.ExtraParam2=SeTakeOwnershipPrivilege ad.ExtraParam7=SeAuditPrivilege ad.ExtraParam8=SeSystemEnvironmentPrivilege ad.ExtraParam5=SeRestorePrivilege ad.ExtraParam6=SeDebugPrivilege ad.ExtraParam9=SeImpersonatePrivilege";
        boolean result= doTest(line);
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        assertEquals("_cefVer", "0.1", out.get("_cefVer").get(0));
        assertEquals("ad.ExtraParam0", "SeTcbPrivilege", out.get("ad.ExtraParam0").get(0));
        assertEquals("ad.ExtraParam1", "SeSecurityPrivilege", out.get("ad.ExtraParam1").get(0));
        assertEquals("ad.ExtraParam2", "SeTakeOwnershipPrivilege", out.get("ad.ExtraParam2").get(0));
        assertEquals("ad.ExtraParam3", "SeLoadDriverPrivilege", out.get("ad.ExtraParam3").get(0));
        assertEquals("ad.ExtraParam4", "SeBackupPrivilege", out.get("ad.ExtraParam4").get(0));
        assertEquals("ad.ExtraParam5", "SeRestorePrivilege", out.get("ad.ExtraParam5").get(0));
        assertEquals("ad.ExtraParam6", "SeDebugPrivilege", out.get("ad.ExtraParam6").get(0));
        assertEquals("ad.ExtraParam7", "SeAuditPrivilege", out.get("ad.ExtraParam7").get(0));
        assertEquals("ad.ExtraParam8", "SeSystemEnvironmentPrivilege", out.get("ad.ExtraParam8").get(0));
        assertEquals("ad.ExtraParam9", "SeImpersonatePrivilege", out.get("ad.ExtraParam9").get(0));
        assertEquals("agentZoneURI", "/All Zones/ArcSight System/Public Address Space Zones/ARIN/164.0.0.0-169.253.255.255 (ARIN)", out.get("agentZoneURI").get(0));
        assertEquals("agt", "xxx.xxx.xx.xx", out.get("agt").get(0));
        assertEquals("ahost", "xxxxxxxxx.xxx.xx.jpmchase.net", out.get("ahost").get(0));
        assertEquals("aid", "3mTr+5kkBABCBFAZn0olEsA\\=\\=", out.get("aid").get(0));
        assertEquals("art", "1438713330191", out.get("art").get(0));
        assertEquals("at", "syslog", out.get("at").get(0));
        assertEquals("atz", "US/Eastern", out.get("atz").get(0));
        assertEquals("av", "7.0.6.7189.0", out.get("av").get(0));
        assertEquals("cat", "Security", out.get("cat").get(0));
        assertEquals("catdt", "Log Consolidator", out.get("catdt").get(0));
        assertEquals("categoryBehavior", "/Authorization/Add", out.get("categoryBehavior").get(0));
        assertEquals("categoryDeviceGroup", "/Operating System", out.get("categoryDeviceGroup").get(0));
        assertEquals("categoryObject", "/Host/Operating System", out.get("categoryObject").get(0));
        assertEquals("categoryOutcome", "/Success", out.get("categoryOutcome").get(0));
        assertEquals("categorySignificance", "/Normal", out.get("categorySignificance").get(0));
        assertEquals("cn1Label", "LogonType", out.get("cn1Label").get(0));
        assertEquals("cn2Label", "CrashOnAuditFail", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Count", out.get("cn3Label").get(0));
        assertEquals("cs1Label", "Accesses", out.get("cs1Label").get(0));
        assertEquals("cs2", "Special Logon", out.get("cs2").get(0));
        assertEquals("cs2Label", "EventlogCategory", out.get("cs2Label").get(0));
        assertEquals("cs3", "Microsoft-Windows-Security-Auditing", out.get("cs3").get(0));
        assertEquals("cs3Label", "EventSource", out.get("cs3Label").get(0));
        assertEquals("cs4Label", "Reason or Error Code", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Authentication Package Name", out.get("cs5Label").get(0));
        assertEquals("deviceNtDomain", "NT AUTHORITY", out.get("deviceNtDomain").get(0));
        assertEquals("deviceSeverity", "4", out.get("deviceSeverity").get(0));
        assertEquals("dhost", "xxxxxxxxxxxx.xxxxxx.jpmchase.net", out.get("dhost").get(0));
        assertEquals("dntdom", "NT AUTHORITY", out.get("dntdom").get(0));
        assertEquals("dpriv", "SeAssignPrimaryTokenPrivilege", out.get("dpriv").get(0));
        assertEquals("dtz", "US/Eastern", out.get("dtz").get(0));
        assertEquals("duid", "0x3e7", out.get("duid").get(0));
        assertEquals("duser", "SYSTEM", out.get("duser").get(0));
        assertEquals("dvchost", "xxxxxxxxxxxx.xxxxxx.jpmchase.net", out.get("dvchost").get(0));
        assertEquals("eventId", "1901795439", out.get("eventId").get(0));
        assertEquals("externalId", "4672", out.get("externalId").get(0));
        assertEquals("msg", "Subject:   Security ID:  S-1-5-18   Account Name:  SYSTEM   Account Domain:  NT AUTHORITY   Logon ID:  0x3e7    Privileges:  SeAssignPrimaryTokenPrivilege     SeTcbPrivilege     SeSecurityPrivilege     SeTakeOwnershipPrivilege     SeLoadDriverPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeDebugPrivilege     SeAuditPrivilege     SeSystemEnvironmentPrivilege     SeImpersonatePrivilege     User: NT AUTHORITY\\\\SYSTEM     ComputerName: xxxxxxxxxxxx.xxxxxx.jpmchase.net", out.get("msg").get(0));
        assertEquals("rt", "1438709727000", out.get("rt").get(0));
    }
}