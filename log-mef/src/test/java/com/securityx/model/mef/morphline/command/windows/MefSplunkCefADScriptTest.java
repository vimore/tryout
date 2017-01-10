package com.securityx.model.mef.morphline.command.windows;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefSplunkCefADScriptTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(MefSplunkCefADScriptTest.class);

    public MefSplunkCefADScriptTest() {
        super(MefSplunkCefADScriptTest.class.toString());
        //this.morphlineId = "cefnew";
        //this.morphlineId = "ceffromsyslogmissinghost";
        //this.confFile = "logcollection-cef.conf";
        this.confFile = "logcollection-parser-main.conf";
        this.morphlineId = "parsermain";
    }

    private Record buildRecord(String msg){
        Record out = new Record();
        out.put("logCollectionHost", "somehost");
        out.put("category", "syslog");
        out.put("message", msg);
        return out;

    }


        @Test
    public void testWindowsEventLogSecurity4624() throws FileNotFoundException {
        String line = "Mar 01 20:43:38 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs4=0x0\\n0x105dd935 cs6Label=Authentication_Package deviceExternalId=125858000 e8PackageName=- cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID e8ProcessID=0x0 dhost=w2k8r2-AD.e8sec.lab cn2=3 cn3=0 cn3Label=Key_Length spt=55963 cs1=Audit Success cs3=NULL SID\\nNT AUTHORITY\\SYSTEM cs2=Security src=127.0.0.1 cs4Label=Logon_ID cs6=Kerberos sourceServiceName=- deviceProcessName=- cs2Label=journalName deviceFacility=Microsoft Windows security auditing. e8LogonProcess=Kerberos duid={F101FD31-AF04-DD47-DF11-0FAEAC4EB894} duser=-\\nW2K8R2-AD$ dntdom=-\\nE8SEC externalId=4624 cs5Label=Sub_Status ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out) ;
        assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefExtensionData", "cs4=0x0\\n0x105dd935 cs6Label=Authentication_Package deviceExternalId=125858000 e8PackageName=- cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID e8ProcessID=0x0 dhost=w2k8r2-AD.e8sec.lab cn2=3 cn3=0 cn3Label=Key_Length spt=55963 cs1=Audit Success cs3=NULL SID\\nNT AUTHORITY\\SYSTEM cs2=Security src=127.0.0.1 cs4Label=Logon_ID cs6=Kerberos sourceServiceName=- deviceProcessName=- cs2Label=journalName deviceFacility=Microsoft Windows security auditing. e8LogonProcess=Kerberos duid={F101FD31-AF04-DD47-DF11-0FAEAC4EB894} duser=-\\nW2K8R2-AD$ dntdom=-\\nE8SEC externalId=4624 cs5Label=Sub_Status", out.get("cefExtensionData").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4624-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2", "3", out.get("cn2").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3", "0", out.get("cn3").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NULL SID\\nNT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x0\\n0x105dd935", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6", "Kerberos", out.get("cs6").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationLogonGUID", "{F101FD31-AF04-DD47-DF11-0FAEAC4EB894}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationLogonID", "0x105dd935", out.get("destinationLogonID").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "NT AUTHORITY\\SYSTEM", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "w2k8r2-ad$", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "125858000", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceProcessName", "-", out.get("deviceProcessName").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "-\\nE8SEC", out.get("dntdom").get(0));
        assertEquals("duid", "{F101FD31-AF04-DD47-DF11-0FAEAC4EB894}", out.get("duid").get(0));
        assertEquals("duser", "-\\nW2K8R2-AD$", out.get("duser").get(0));
        assertEquals("e8LogonProcess", "Kerberos", out.get("e8LogonProcess").get(0));
        assertEquals("e8PackageName", "-", out.get("e8PackageName").get(0));
        assertEquals("e8ProcessID", "0x0", out.get("e8ProcessID").get(0));
        assertEquals("externalId", "4624", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("receiptTime", 1456865018000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 01 20:43:38", out.get("receiptTimeStr").get(0));
        assertEquals("sourceAddress", "127.0.0.1", out.get("sourceAddress").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
        assertEquals("sourcePort", 55963, out.get("sourcePort").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "NULL SID", out.get("sourceSecurityID").get(0));
        assertEquals("sourceServiceName", "-", out.get("sourceServiceName").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("spt", "55963", out.get("spt").get(0));
        assertEquals("src", "127.0.0.1", out.get("src").get(0));
        assertEquals("startTime", 1456865018000L, out.get("startTime").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
    }


    @Test
    public void testWindowsEventLogSecurity4625() throws FileNotFoundException {
        String line = "Mar 02 06:28:59 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs4=0x3e7 cs6Label=Authentication_Package deviceExternalId=125901016 e8PackageName=- dproc=C:\\Windows\\System32\\winlogon.exe cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID dhost=w2k8r2-AD.e8sec.lab shost=W2K8R2-AD cn2=10 cn3=0 cn3Label=Key_Length spt=59551 cs1=Audit Failure cs3=NT AUTHORITY\\SYSTEM\\nNULL SID cs2=Security cs5=0xc0000064 src=50.75.191.74 cs4Label=Logon_ID cs6=Negotiate sourceServiceName=- cs2Label=journalName deviceFacility=Microsoft Windows security auditing. e8LogonProcess=User32 e8Status=0xc000006d duser=W2K8R2-AD$\\nadministrator dntdom=E8SEC\\nW2K8R2-AD externalId=4625 cs5Label=Sub_Status ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out) ;

        assertEquals("authenticationPackage", "Negotiate", out.get("authenticationPackage").get(0));
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "An account failed to log on", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4625-Audit Failure", out.get("cefSignatureId").get(0));
        assertEquals("cn2", "10", out.get("cn2").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3", "0", out.get("cn3").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Failure", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\SYSTEM\\nNULL SID", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x3e7", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5", "0xc0000064", out.get("cs5").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6", "Negotiate", out.get("cs6").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "W2K8R2-AD", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "NULL SID", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "administrator", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "125901016", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC\\nW2K8R2-AD", out.get("dntdom").get(0));
        assertEquals("dproc", "C:\\Windows\\System32\\winlogon.exe", out.get("dproc").get(0));
        assertEquals("duser", "W2K8R2-AD$\\nadministrator", out.get("duser").get(0));
        assertEquals("e8LogonProcess", "User32", out.get("e8LogonProcess").get(0));
        assertEquals("e8PackageName", "-", out.get("e8PackageName").get(0));
        assertEquals("e8Status", "0xc000006d", out.get("e8Status").get(0));
        assertEquals("externalId", "4625", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonProcess", "User32", out.get("logonProcess").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("receiptTime", 1456900139000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 02 06:28:59", out.get("receiptTimeStr").get(0));
        assertEquals("shost", "W2K8R2-AD", out.get("shost").get(0));
        assertEquals("sourceAddress", "50.75.191.74", out.get("sourceAddress").get(0));
        assertEquals("sourceHostName", "w2k8r2-ad", out.get("sourceHostName").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("sourceLogonType", "10", out.get("sourceLogonType").get(0));
        assertEquals("sourceNameOrIp", "W2K8R2-AD", out.get("sourceNameOrIp").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourcePort", 59551, out.get("sourcePort").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\winlogon.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceServiceName", "-", out.get("sourceServiceName").get(0));
        assertEquals("sourceUserName", "w2k8r2-ad$", out.get("sourceUserName").get(0));
        assertEquals("spt", "59551", out.get("spt").get(0));
        assertEquals("src", "50.75.191.74", out.get("src").get(0));
        assertEquals("startTime", 1456900139000L, out.get("startTime").get(0));
        assertEquals("status", "0xc000006d", out.get("status").get(0));
        assertEquals("subStatus", "0xc0000064", out.get("subStatus").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
    }


    @Test
    public void testWindowsEventLogSecurity4648() throws FileNotFoundException {
        String line = "Mar 02 03:18:12 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs4=0x3e7 cs6Label=Authentication_Package dst=- deviceExternalId=125890464 e8TargetServerName=w2k8r2-ad$ cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID e8ProcessID=0xc94 dhost=w2k8r2-AD.e8sec.lab dpt=- cn3Label=Key_Length cs1=Audit Success cs3=NT AUTHORITY\\SYSTEM cs2=Security cs4Label=Logon_ID deviceProcessName=C:\\Windows\\System32\\taskhost.exe cs2Label=journalName deviceFacility=Microsoft Windows security auditing. duid={00000000-0000-0000-0000-000000000000}\\n{0E4D55BC-9EA2-DD73-06CB-56BBF27CFFBD} duser=W2K8R2-AD$\\nW2K8R2-AD$ dntdom=E8SEC\\nE8SEC.LAB externalId=4648 cs5Label=Sub_Status ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4648-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x3e7", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationLogonGUID", "{0E4D55BC-9EA2-DD73-06CB-56BBF27CFFBD}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "E8SEC.LAB", out.get("destinationNtDomain").get(0));
        assertEquals("destinationUserName", "w2k8r2-ad$", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "125890464", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceProcessName", "C:\\Windows\\System32\\taskhost.exe", out.get("deviceProcessName").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC\\nE8SEC.LAB", out.get("dntdom").get(0));
        assertEquals("dpt", "-", out.get("dpt").get(0));
        assertEquals("dst", "-", out.get("dst").get(0));
        assertEquals("duid", "{00000000-0000-0000-0000-000000000000}\\n{0E4D55BC-9EA2-DD73-06CB-56BBF27CFFBD}", out.get("duid").get(0));
        assertEquals("duser", "W2K8R2-AD$\\nW2K8R2-AD$", out.get("duser").get(0));
        assertEquals("e8ProcessID", "0xc94", out.get("e8ProcessID").get(0));
        assertEquals("e8TargetServerName", "w2k8r2-ad$", out.get("e8TargetServerName").get(0));
        assertEquals("externalId", "4648", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1456888692000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 02 03:18:12", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("sourceLogonGUID").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessId", "0xc94", out.get("sourceProcessId").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\taskhost.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "w2k8r2-ad$", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1456888692000L, out.get("startTime").get(0));

    }

    @Test
    public void testWindowsEventLogSecurity4661() throws FileNotFoundException {
        String line = "Mar 15 15:37:46 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=w2k8r2-AD.e8sec.lab e8ObjType=SAM_USER deviceExternalId=129624690 cs4Label=Logon_ID cn2Label=Logon_Type e8ObjName=E8SEC\\Administrator externalId=4661 e8Accesses=DELETE dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=NT AUTHORITY\\SYSTEM duser=W2K8R2-AD$ deviceProcessName=C:\\Windows\\System32\\lsass.exe deviceProcessID=0x1f0 cs1Label=eventOutcome e8ObjServer=Security Account Manager cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x3e7 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4661-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x3e7", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("desiredAccess", "DELETE", out.get("desiredAccess").get(0));
        assertEquals("destinationObjectName", "E8SEC\\Administrator", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "Security Account Manager", out.get("destinationObjectServer").get(0));
        assertEquals("destinationObjectType", "SAM_USER", out.get("destinationObjectType").get(0));
        assertEquals("deviceExternalId", "129624690", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceProcessID", "0x1f0", out.get("deviceProcessID").get(0));
        assertEquals("deviceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("deviceProcessName").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("duser", "W2K8R2-AD$", out.get("duser").get(0));
        assertEquals("e8Accesses", "DELETE", out.get("e8Accesses").get(0));
        assertEquals("e8ObjName", "E8SEC\\Administrator", out.get("e8ObjName").get(0));
        assertEquals("e8ObjServer", "Security Account Manager", out.get("e8ObjServer").get(0));
        assertEquals("e8ObjType", "SAM_USER", out.get("e8ObjType").get(0));
        assertEquals("externalId", "4661", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458056266000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 15 15:37:46", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "w2k8r2-ad$", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1458056266000L, out.get("startTime").get(0));

    }

    //
    @Test
    public void testWindowsEventLogSecurity4672() throws FileNotFoundException {
        String line = "Mar 01 20:43:38 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs4=0x105dd935 cs6Label=Authentication_Package deviceExternalId=125857999 cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID dhost=w2k8r2-AD.e8sec.lab dpriv=SeSecurityPrivilege cn3Label=Key_Length cs1=Audit Success cs3=NT AUTHORITY\\SYSTEM cs2=Security cs4Label=Logon_ID cs2Label=journalName deviceFacility=Microsoft Windows security auditing. duser=W2K8R2-AD$ dntdom=E8SEC externalId=4672 cs5Label=Sub_Status";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4672-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x105dd935", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationLogonID", "0x105dd935", out.get("destinationLogonID").get(0));
        assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "NT AUTHORITY\\SYSTEM", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "w2k8r2-ad$", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "125857999", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "SeSecurityPrivilege", out.get("dpriv").get(0));
        assertEquals("duser", "W2K8R2-AD$", out.get("duser").get(0));
        assertEquals("externalId", "4672", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("privileges", "SeSecurityPrivilege", out.get("privileges").get(0));
        assertEquals("receiptTime", 1456865018000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 01 20:43:38", out.get("receiptTimeStr").get(0));
        assertEquals("startTime", 1456865018000L, out.get("startTime").get(0));

    }

    @Test
    public void testWindowsEventLogSecurity4723() throws FileNotFoundException {
        String line = "Mar 02 14:02:13 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs4=0x1198c0fa cs6Label=Authentication_Package deviceExternalId=125931991 cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID dhost=w2k8r2-AD.e8sec.lab cn3Label=Key_Length cs1=Audit Failure cs3=E8SEC\\jyria2\\nE8SEC\\jyria2 cs2=Security cs4Label=Logon_ID cs2Label=journalName deviceFacility=Microsoft Windows security auditing. duser=jyria2\\njyria2 dntdom=E8SEC\\nE8SEC externalId=4723 cs5Label=Sub_Status ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "An attempt was made to change an account's password.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4723-Audit Failure", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Failure", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria2\\nE8SEC\\jyria2", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x1198c0fa", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria2", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria2", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "125931991", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC\\nE8SEC", out.get("dntdom").get(0));
        assertEquals("duser", "jyria2\\njyria2", out.get("duser").get(0));
        assertEquals("externalId", "4723", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1456927333000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 02 14:02:13", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x1198c0fa", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria2", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria2", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1456927333000L, out.get("startTime").get(0));


    }


    //
    @Test
    public void testWindowsEventLogSecurity4724() throws FileNotFoundException {
        String line = "Mar 01 20:20:30 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs4=0xf97b238 cs6Label=Authentication_Package deviceExternalId=125856566 cn2Label=Logon_Type cs1Label=eventOutcome cs3Label=Security_ID dhost=w2k8r2-AD.e8sec.lab cn3Label=Key_Length cs1=Audit Success cs3=E8SEC\\jyria\\nE8SEC\\jyria2 cs2=Security cs4Label=Logon_ID cs2Label=journalName deviceFacility=Microsoft Windows security auditing. duser=jyria\\njyria2 dntdom=E8SEC\\nE8SEC externalId=4724 cs5Label=Sub_Status";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "An attempt was made to reset an account's password.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4724-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria2", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0xf97b238", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria2", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria2", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "125856566", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC\\nE8SEC", out.get("dntdom").get(0));
        assertEquals("duser", "jyria\\njyria2", out.get("duser").get(0));
        assertEquals("externalId", "4724", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1456863630000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 01 20:20:30", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0xf97b238", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1456863630000L, out.get("startTime").get(0));

    }


    @Test
    public void testWindowsEventLogSecurity4728() throws FileNotFoundException {
        String line = "Mar 04 14:37:27 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=126306829 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4728 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_global_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED duser=jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0xf97b238";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A member was added to a security-enabled global group.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4728-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0xf97b238", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_global_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupSecurityID", "NONE_MAPPED", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "126306829", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_global_grp", out.get("e8GroupName").get(0));
        assertEquals("externalId", "4728", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457102247000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 04 14:37:27", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0xf97b238", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457102247000L, out.get("startTime").get(0));


    }

    //
    @Test
    public void testWindowsEventLogSecurity4729() throws FileNotFoundException {
        String line = "Mar 04 14:37:27 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=126306835 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4729 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_global_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED duser=jyria\\n- cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0xf97b238 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A member was removed from a security-enabled global group.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4729-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0xf97b238", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_global_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupSecurityID", "NONE_MAPPED", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "-", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "126306835", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria\\n-", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_global_grp", out.get("e8GroupName").get(0));
        assertEquals("externalId", "4729", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457102247000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 04 14:37:27", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0xf97b238", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457102247000L, out.get("startTime").get(0));

    }
    //
    @Test
    public void testWindowsEventLogSecurity4732() throws FileNotFoundException {
        String line = "Mar 04 14:37:27 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=126306831 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4732 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_local_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED duser=jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0xf97b238 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A member was added to a security-enabled local group.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4732-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0xf97b238", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_local_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupSecurityID", "NONE_MAPPED", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "126306831", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_local_grp", out.get("e8GroupName").get(0));
        assertEquals("externalId", "4732", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457102247000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 04 14:37:27", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0xf97b238", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457102247000L, out.get("startTime").get(0));

    }

    @Test
    public void testWindowsEventLogSecurity4733() throws FileNotFoundException {
        String line = "Mar 04 14:37:28 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=126306837 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4733 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_local_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED duser=jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0xf97b238 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A member was removed from a security-enabled local group.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4733-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria\\nNONE_MAPPED", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0xf97b238", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_local_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupSecurityID", "NONE_MAPPED", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "126306837", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_local_grp", out.get("e8GroupName").get(0));
        assertEquals("externalId", "4733", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457102248000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 04 14:37:28", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0xf97b238", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457102248000L, out.get("startTime").get(0));

    }

    //Mar 04 14:37:29 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=126306867 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4738 dntdom=E8SEC\\nE8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\resetmypasswd duser=jyria\\nresetmypasswd cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0xf97b238
    @Test
    public void testWindowsEventLogSecurity4738() throws FileNotFoundException {
        String line = "Mar 04 17:30:11 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=126318029 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4738 dntdom=E8SEC\\nE8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\jyria2 duser=jyria\\njyria2 cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package e8SAMAccountName=- e8DisplayName=- e8UserPrincipalName=- e8HomeDir=- e8HomeDrive=- e8ScriptPath=- e8ProfilePath=- e8PasswdLastSet=3/4/2016 5:30:11 PM e8AccountExpires=- e8PrimaryGroupID=- e8AllowedToDelegateTo=- e8OldUACValue=- e8NewUACValue=- e8UserAccountControl=- e8UserParameters=- e8SIDHistory=- e8LogonHours=- cs4=0xf97b238 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("accountExpires", "-", out.get("accountExpires").get(0));
        assertEquals("adDisplayName", "-", out.get("adDisplayName").get(0));
        assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
        assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
        assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
        assertEquals("adPasswdLastSet", "3/4/2016 5:30:11 PM", out.get("adPasswdLastSet").get(0));
        assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
        assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
        assertEquals("adSamAccountName", "-", out.get("adSamAccountName").get(0));
        assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
        assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
        assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
        assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
        assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
        assertEquals("allowedToDelegateTo", "-", out.get("allowedToDelegateTo").get(0));
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A logon was attempted using explicit credentials", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4738-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria2", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0xf97b238", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria2", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria2", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "126318029", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC\\nE8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria\\njyria2", out.get("duser").get(0));
        assertEquals("e8AccountExpires", "-", out.get("e8AccountExpires").get(0));
        assertEquals("e8AllowedToDelegateTo", "-", out.get("e8AllowedToDelegateTo").get(0));
        assertEquals("e8DisplayName", "-", out.get("e8DisplayName").get(0));
        assertEquals("e8HomeDir", "-", out.get("e8HomeDir").get(0));
        assertEquals("e8HomeDrive", "-", out.get("e8HomeDrive").get(0));
        assertEquals("e8LogonHours", "-", out.get("e8LogonHours").get(0));
        assertEquals("e8NewUACValue", "-", out.get("e8NewUACValue").get(0));
        assertEquals("e8OldUACValue", "-", out.get("e8OldUACValue").get(0));
        assertEquals("e8PasswdLastSet", "3/4/2016 5:30:11 PM", out.get("e8PasswdLastSet").get(0));
        assertEquals("e8PrimaryGroupID", "-", out.get("e8PrimaryGroupID").get(0));
        assertEquals("e8ProfilePath", "-", out.get("e8ProfilePath").get(0));
        assertEquals("e8SAMAccountName", "-", out.get("e8SAMAccountName").get(0));
        assertEquals("e8SIDHistory", "-", out.get("e8SIDHistory").get(0));
        assertEquals("e8ScriptPath", "-", out.get("e8ScriptPath").get(0));
        assertEquals("e8UserAccountControl", "-", out.get("e8UserAccountControl").get(0));
        assertEquals("e8UserParameters", "-", out.get("e8UserParameters").get(0));
        assertEquals("e8UserPrincipalName", "-", out.get("e8UserPrincipalName").get(0));
        assertEquals("externalId", "4738", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonHours", "-", out.get("logonHours").get(0));
        assertEquals("newUacValue", "-", out.get("newUacValue").get(0));
        assertEquals("privileges", "-", out.get("privileges").get(0));
        assertEquals("receiptTime", 1457112611000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 04 17:30:11", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0xf97b238", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457112611000L, out.get("startTime").get(0));

    }

    //
    @Test
    public void testWindowsEventLogSecurity5140() throws FileNotFoundException {
        String line = "Mar 10 23:00:32 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=w2k8r2-AD.e8sec.lab e8ObjType=File deviceExternalId=129000282 spt=50023 cs4Label=Logon_ID cn2Label=Logon_Type externalId=5140 e8Accesses=ReadData (or ListDirectory) dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=NT AUTHORITY\\SYSTEM duser=W2K8R2-AD$ e8ShareName=\\\\*\\SYSVOL cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x7334805 e8SrcAddr=192.168.12.12";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-5140-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x7334805", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationObjectType", "File", out.get("destinationObjectType").get(0));
        assertEquals("destinationShareName", "\\\\*\\SYSVOL", out.get("destinationShareName").get(0));
        assertEquals("deviceExternalId", "129000282", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("duser", "W2K8R2-AD$", out.get("duser").get(0));
        assertEquals("e8Accesses", "ReadData (or ListDirectory)", out.get("e8Accesses").get(0));
        assertEquals("e8ObjType", "File", out.get("e8ObjType").get(0));
        assertEquals("e8ShareName", "\\\\*\\SYSVOL", out.get("e8ShareName").get(0));
        assertEquals("e8SrcAddr", "192.168.12.12", out.get("e8SrcAddr").get(0));
        assertEquals("externalId", "5140", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457650832000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 10 23:00:32", out.get("receiptTimeStr").get(0));
        assertEquals("sourceAddress", "192.168.12.12", out.get("sourceAddress").get(0));
        assertEquals("sourceLogonID", "0x7334805", out.get("sourceLogonID").get(0));
        assertEquals("sourceNameOrIp", "192.168.12.12", out.get("sourceNameOrIp").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourcePort", 50023, out.get("sourcePort").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "w2k8r2-ad$", out.get("sourceUserName").get(0));
        assertEquals("spt", "50023", out.get("spt").get(0));
        assertEquals("startTime", 1457650832000L, out.get("startTime").get(0));


    }
    //Mar 14 13:06:28 W2K8R2-SRC CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=W2K8R2-SRC.e8sec.lab e8ObjType=Security deviceExternalId=30095344 cs4Label=Logon_ID cn2Label=Logon_Type e8ObjName=PlugPlaySecurityObject externalId=4656 e8Accesses=Unknown specific access (bit 1) dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=NT AUTHORITY\\SYSTEM duser=W2K8R2-SRC$ deviceProcessName=C:\\Windows\\System32\\svchost.exe deviceProcessID=0x268 cs1Label=eventOutcome e8ObjServer=PlugPlayManager cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x3e7
    @Test
    public void testWindowsEventLogSecurity4656() throws FileNotFoundException {
        String line = "Mar 14 13:06:28 W2K8R2-SRC CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=W2K8R2-SRC.e8sec.lab e8ObjType=Security deviceExternalId=30095344 cs4Label=Logon_ID cn2Label=Logon_Type e8ObjName=PlugPlaySecurityObject externalId=4656 e8Accesses=Unknown specific access (bit 1) dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=NT AUTHORITY\\SYSTEM duser=W2K8R2-SRC$ deviceProcessName=C:\\Windows\\System32\\svchost.exe deviceProcessID=0x268 cs1Label=eventOutcome e8ObjServer=PlugPlayManager cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x3e7 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4656-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x3e7", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("desiredAccess", "Unknown specific access (bit 1)", out.get("desiredAccess").get(0));
        assertEquals("destinationObjectName", "PlugPlaySecurityObject", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "PlugPlayManager", out.get("destinationObjectServer").get(0));
        assertEquals("destinationObjectType", "Security", out.get("destinationObjectType").get(0));
        assertEquals("deviceExternalId", "30095344", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-src", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "W2K8R2-SRC", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceProcessName", "C:\\Windows\\System32\\svchost.exe", out.get("deviceProcessName").get(0));
        assertEquals("dhost", "W2K8R2-SRC.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("duser", "W2K8R2-SRC$", out.get("duser").get(0));
        assertEquals("e8Accesses", "Unknown specific access (bit 1)", out.get("e8Accesses").get(0));
        assertEquals("e8ObjName", "PlugPlaySecurityObject", out.get("e8ObjName").get(0));
        assertEquals("e8ObjServer", "PlugPlayManager", out.get("e8ObjServer").get(0));
        assertEquals("e8ObjType", "Security", out.get("e8ObjType").get(0));
        assertEquals("externalId", "4656", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457960788000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 14 13:06:28", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\svchost.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "w2k8r2-src$", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457960788000L, out.get("startTime").get(0));


    }

    @Test
    public void testWindowsEventLogSecurity4768() throws FileNotFoundException {
        String line = "Mar 11 10:41:02 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|e8ServiceID=E8SEC\\krbtgt dhost=w2k8r2-AD.e8sec.lab e8SuppliedRealmName=E8SEC.LAB deviceFacility=Microsoft Windows security auditing. e8TicketOptions=0x40810010 e8PreAuthType=2 e8UserID=E8SEC\\W2K8R2-SRC$ cs6Label=Authentication_Package cn2Label=Logon_Type cs1Label=eventOutcome cs2Label=journalName e8ServiceName=krbtgt cs3Label=Security_ID e8TicketEncryptionType=0x12 externalId=4768 cs2=Security cn3Label=Key_Length cs4Label=Logon_ID e8ClientAddress=::ffff:192.168.12.21 duser=W2K8R2-SRC$ cs5Label=Sub_Status cs1=Audit Success e8ResultCode=0x0 e8ClientPort=52183 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4768-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationNtDomain", "E8SEC.LAB", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\W2K8R2-SRC$", out.get("destinationSecurityID").get(0));
        assertEquals("destinationServiceName", "krbtgt", out.get("destinationServiceName").get(0));
        assertEquals("destinationServiceSecurityID", "E8SEC\\krbtgt", out.get("destinationServiceSecurityID").get(0));
        assertEquals("destinationUserName", "w2k8r2-src$", out.get("destinationUserName").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("duser", "W2K8R2-SRC$", out.get("duser").get(0));
        assertEquals("e8ClientAddress", "::ffff:192.168.12.21", out.get("e8ClientAddress").get(0));
        assertEquals("e8ClientPort", "52183", out.get("e8ClientPort").get(0));
        assertEquals("e8PreAuthType", "2", out.get("e8PreAuthType").get(0));
        assertEquals("e8ResultCode", "0x0", out.get("e8ResultCode").get(0));
        assertEquals("e8ServiceID", "E8SEC\\krbtgt", out.get("e8ServiceID").get(0));
        assertEquals("e8ServiceName", "krbtgt", out.get("e8ServiceName").get(0));
        assertEquals("e8SuppliedRealmName", "E8SEC.LAB", out.get("e8SuppliedRealmName").get(0));
        assertEquals("e8TicketEncryptionType", "0x12", out.get("e8TicketEncryptionType").get(0));
        assertEquals("e8TicketOptions", "0x40810010", out.get("e8TicketOptions").get(0));
        assertEquals("e8UserID", "E8SEC\\W2K8R2-SRC$", out.get("e8UserID").get(0));
        assertEquals("externalId", "4768", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("preAuthenticationType", "2", out.get("preAuthenticationType").get(0));
        assertEquals("receiptTime", 1457692862000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 11 10:41:02", out.get("receiptTimeStr").get(0));
        assertEquals("sourcePort", 52183, out.get("sourcePort").get(0));
        assertEquals("startTime", 1457692862000L, out.get("startTime").get(0));
        assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
        assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));


    }

    //
    @Test
    public void testWindowsEventLogSecurity4769() throws FileNotFoundException {
        String line = "Mar 11 10:41:02 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|e8ServiceID=E8SEC\\W2K8R2-SRC$ dhost=w2k8r2-AD.e8sec.lab deviceFacility=Microsoft Windows security auditing. e8TicketOptions=0x40810000 sourceServiceName=- duid={9C67E0E5-4D50-8DAA-FE8F-7081DEDB63C4} cs6Label=Authentication_Package cn2Label=Logon_Type cs1Label=eventOutcome cs2Label=journalName e8ServiceName=W2K8R2-SRC$ cs3Label=Security_ID dntdom=E8SEC.LAB e8TicketEncryptionType=0x12 externalId=4769 cs2=Security cn3Label=Key_Length cs4Label=Logon_ID e8ClientAddress=::ffff:192.168.12.21 duser=W2K8R2-SRC$@E8SEC.LAB cs5Label=Sub_Status cs1=Audit Success e8ClientPort=52185";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4769-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationLogonGUID", "{9C67E0E5-4D50-8DAA-FE8F-7081DEDB63C4}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationServiceName", "W2K8R2-SRC$", out.get("destinationServiceName").get(0));
        assertEquals("destinationServiceSecurityID", "E8SEC\\W2K8R2-SRC$", out.get("destinationServiceSecurityID").get(0));
        assertEquals("destinationUserName", "w2k8r2-src$@e8sec.lab", out.get("destinationUserName").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC.LAB", out.get("dntdom").get(0));
        assertEquals("duid", "{9C67E0E5-4D50-8DAA-FE8F-7081DEDB63C4}", out.get("duid").get(0));
        assertEquals("duser", "W2K8R2-SRC$@E8SEC.LAB", out.get("duser").get(0));
        assertEquals("e8ClientAddress", "::ffff:192.168.12.21", out.get("e8ClientAddress").get(0));
        assertEquals("e8ClientPort", "52185", out.get("e8ClientPort").get(0));
        assertEquals("e8ServiceID", "E8SEC\\W2K8R2-SRC$", out.get("e8ServiceID").get(0));
        assertEquals("e8ServiceName", "W2K8R2-SRC$", out.get("e8ServiceName").get(0));
        assertEquals("e8TicketEncryptionType", "0x12", out.get("e8TicketEncryptionType").get(0));
        assertEquals("e8TicketOptions", "0x40810000", out.get("e8TicketOptions").get(0));
        assertEquals("externalId", "4769", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457692862000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 11 10:41:02", out.get("receiptTimeStr").get(0));
        assertEquals("sourcePort", 52185, out.get("sourcePort").get(0));
        assertEquals("sourceServiceName", "-", out.get("sourceServiceName").get(0));
        assertEquals("startTime", 1457692862000L, out.get("startTime").get(0));
        assertEquals("ticketOptions", "0x40810000", out.get("ticketOptions").get(0));
        assertEquals("transitedService", "0x12", out.get("transitedService").get(0));
    }

    //
    @Test
    public void testWindowsEventLogSecurity4663() throws FileNotFoundException {
        String line = "Mar 12 21:56:02 W2K8R2-SRC CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=W2K8R2-SRC.e8sec.lab e8ObjType=File deviceExternalId=29887869 cs4Label=Logon_ID cn2Label=Logon_Type e8ObjName=C:\\Windows\\System32\\dhcp\\j50tmp.log externalId=4663 e8Accesses=WriteData (or AddFile) dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=NT AUTHORITY\\NETWORK SERVICE duser=W2K8R2-SRC$ deviceProcessName=C:\\Windows\\System32\\svchost.exe deviceProcessID=0x4e0 cs1Label=eventOutcome e8ObjServer=Security cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x3e4 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4663-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "NT AUTHORITY\\NETWORK SERVICE", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x3e4", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("desiredAccess", "WriteData (or AddFile)", out.get("desiredAccess").get(0));
        assertEquals("destinationObjectName", "C:\\Windows\\System32\\dhcp\\j50tmp.log", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "Security", out.get("destinationObjectServer").get(0));
        assertEquals("destinationObjectType", "File", out.get("destinationObjectType").get(0));
        assertEquals("deviceExternalId", "29887869", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-src", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "W2K8R2-SRC", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceProcessName", "C:\\Windows\\System32\\svchost.exe", out.get("deviceProcessName").get(0));
        assertEquals("dhost", "W2K8R2-SRC.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("duser", "W2K8R2-SRC$", out.get("duser").get(0));
        assertEquals("e8Accesses", "WriteData (or AddFile)", out.get("e8Accesses").get(0));
        assertEquals("e8ObjName", "C:\\Windows\\System32\\dhcp\\j50tmp.log", out.get("e8ObjName").get(0));
        assertEquals("e8ObjServer", "Security", out.get("e8ObjServer").get(0));
        assertEquals("e8ObjType", "File", out.get("e8ObjType").get(0));
        assertEquals("externalId", "4663", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1457819762000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 12 21:56:02", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x3e4", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\svchost.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\NETWORK SERVICE", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "w2k8r2-src$", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457819762000L, out.get("startTime").get(0));

    }

    @Test
    public void testWindowsEventLogSecurity4674() throws FileNotFoundException {
        String line = "Mar 13 19:58:20 W2K8R2-SRC CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=SeShutdownPrivilege dhost=W2K8R2-SRC.e8sec.lab e8ObjType=- deviceExternalId=30004720 cs4Label=Logon_ID cn2Label=Logon_Type e8ObjName=- externalId=4674 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=E8SEC\\jyria2 duser=jyria2 deviceProcessName=C:\\Windows\\System32\\wininit.exe deviceProcessID=0x1a4 cs1Label=eventOutcome e8ObjServer=Win32 SystemShutdown module cs1=Audit Failure cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x7ad1917e ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "An operation was attempted on a privileged object", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4674-Audit Failure", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Failure", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria2", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x7ad1917e", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-src", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "W2K8R2-SRC.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationObjectName", "-", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "Win32 SystemShutdown module", out.get("destinationObjectServer").get(0));
        assertEquals("destinationObjectType", "-", out.get("destinationObjectType").get(0));
        assertEquals("deviceExternalId", "30004720", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-src", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "W2K8R2-SRC", out.get("deviceNameOrIp").get(0));
        assertEquals("deviceProcessName", "C:\\Windows\\System32\\wininit.exe", out.get("deviceProcessName").get(0));
        assertEquals("dhost", "W2K8R2-SRC.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "SeShutdownPrivilege", out.get("dpriv").get(0));
        assertEquals("duser", "jyria2", out.get("duser").get(0));
        assertEquals("e8ObjName", "-", out.get("e8ObjName").get(0));
        assertEquals("e8ObjServer", "Win32 SystemShutdown module", out.get("e8ObjServer").get(0));
        assertEquals("e8ObjType", "-", out.get("e8ObjType").get(0));
        assertEquals("externalId", "4674", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("privileges", "SeShutdownPrivilege", out.get("privileges").get(0));
        assertEquals("receiptTime", 1457899100000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 13 19:58:20", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x7ad1917e", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\wininit.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria2", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria2", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1457899100000L, out.get("startTime").get(0));


    }

    //
    @Test
    public void testWindowsEventLogSecurity4727() throws FileNotFoundException {
        String line = "Mar 21 15:43:30 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131307525 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4727 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_global_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\__new_sec_global_grp duser=jyria cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package e8SAMAccountName=__new_sec_global_grp e8SIDHistory=- cs4=0x21ec36a5 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A security-enabled global group was created", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4727-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\__new_sec_global_grp", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x21ec36a5", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_global_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
        assertEquals("destinationGroupSecurityID", "E8SEC\\__new_sec_global_grp", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceExternalId", "131307525", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_global_grp", out.get("e8GroupName").get(0));
        assertEquals("e8SAMAccountName", "__new_sec_global_grp", out.get("e8SAMAccountName").get(0));
        assertEquals("e8SIDHistory", "-", out.get("e8SIDHistory").get(0));
        assertEquals("externalId", "4727", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458575010000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 21 15:43:30", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x21ec36a5", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1458575010000L, out.get("startTime").get(0));


    }

    @Test
    public void testWindowsEventLogSecurity4731() throws FileNotFoundException {
        String line = "Mar 21 15:55:17 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131308520 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4731 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_local_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\__new_sec_local_grp duser=jyria cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package e8SAMAccountName=__new_sec_local_grp e8SIDHistory=- cs4=0x21ec36a5 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A security-enabled local group was created", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4731-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\__new_sec_local_grp", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x21ec36a5", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_local_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
        assertEquals("destinationGroupSecurityID", "E8SEC\\__new_sec_local_grp", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceExternalId", "131308520", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_local_grp", out.get("e8GroupName").get(0));
        assertEquals("e8SAMAccountName", "__new_sec_local_grp", out.get("e8SAMAccountName").get(0));
        assertEquals("e8SIDHistory", "-", out.get("e8SIDHistory").get(0));
        assertEquals("externalId", "4731", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458575717000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 21 15:55:17", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x21ec36a5", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1458575717000L, out.get("startTime").get(0));


    }

    //Mar 21 15:43:30 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131307527 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4754 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_universal_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\__new_sec_universal_grp duser=jyria cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package e8SAMAccountName=__new_sec_universal_grp e8SIDHistory=- cs4=0x21ec36a5
    @Test
    public void testWindowsEventLogSecurity4754() throws FileNotFoundException {
        String line = "Mar 21 15:43:30 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131307527 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4754 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName e8GroupName=__new_sec_universal_grp e8GroupDomain=E8SEC cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\__new_sec_universal_grp duser=jyria cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package e8SAMAccountName=__new_sec_universal_grp e8SIDHistory=- cs4=0x21ec36a5";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A security-enabled universal group was created", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4754-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\__new_sec_universal_grp", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x21ec36a5", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_universal_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
        assertEquals("destinationGroupSecurityID", "E8SEC\\__new_sec_universal_grp", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceExternalId", "131307527", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria", out.get("duser").get(0));
        assertEquals("e8GroupDomain", "E8SEC", out.get("e8GroupDomain").get(0));
        assertEquals("e8GroupName", "__new_sec_universal_grp", out.get("e8GroupName").get(0));
        assertEquals("e8SAMAccountName", "__new_sec_universal_grp", out.get("e8SAMAccountName").get(0));
        assertEquals("e8SIDHistory", "-", out.get("e8SIDHistory").get(0));
        assertEquals("externalId", "4754", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458575010000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 21 15:43:30", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x21ec36a5", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1458575010000L, out.get("startTime").get(0));


    }

    //
    @Test
    public void testWindowsEventLogSecurity4756() throws FileNotFoundException {
        String line = "Mar 21 16:05:15 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dpriv=- dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131309361 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4756 dntdom=E8SEC\\nE8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=E8SEC\\jyria\\nE8SEC\\jyria\\nE8SEC\\__new_sec_universal_grp duser=jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab\\n__new_sec_universal_grp cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x21f238cd ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "A member was added to a security-enabled universal group.", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4756-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria\\nE8SEC\\jyria\\nE8SEC\\__new_sec_universal_grp", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x21f238cd", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationGroup", "__new_sec_universal_grp", out.get("destinationGroup").get(0));
        assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
        assertEquals("destinationGroupSecurityID", "E8SEC\\__new_sec_universal_grp", out.get("destinationGroupSecurityID").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationLdapUser", "CN=jyria,CN=Users,DC=e8sec,DC=lab", out.get("destinationLdapUser").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "131309361", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("dntdom", "E8SEC\\nE8SEC", out.get("dntdom").get(0));
        assertEquals("dpriv", "-", out.get("dpriv").get(0));
        assertEquals("duser", "jyria\\nCN=jyria,CN=Users,DC=e8sec,DC=lab\\n__new_sec_universal_grp", out.get("duser").get(0));
        assertEquals("externalId", "4756", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458576315000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 21 16:05:15", out.get("receiptTimeStr").get(0));
        assertEquals("sourceLogonID", "0x21f238cd", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "E8SEC\\jyria", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
        assertEquals("startTime", 1458576315000L, out.get("startTime").get(0));


    }
    //
    @Test
    public void testWindowsEventLogSecurity4771() throws FileNotFoundException {
        String line = "Mar 21 15:51:22 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|cs6Label=Authentication_Package e8ClientPort=0 cs1=Audit Failure cs3=E8SEC\\jyria cs2=Security externalId=4771 e8TicketOptions=0x40810010 duser=jyria cs1Label=eventOutcome cn3Label=Key_Length deviceFacility=Microsoft Windows security auditing. e8ServiceName=krbtgt/E8SEC cs4Label=Logon_ID cs3Label=Security_ID cs5Label=Sub_Status e8PreAuthType=2 dhost=w2k8r2-AD.e8sec.lab e8ClientAddress=::1 cn2Label=Logon_Type cs2Label=journalName";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4771-Audit Failure", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Failure", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "E8SEC\\jyria", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs5Label", "Sub_Status", out.get("cs5Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationProcessName", "krbtgt", out.get("destinationProcessName").get(0));
        assertEquals("destinationSecurityID", "E8SEC\\jyria", out.get("destinationSecurityID").get(0));
        assertEquals("destinationServiceName", "krbtgt/E8SEC", out.get("destinationServiceName").get(0));
        assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("duser", "jyria", out.get("duser").get(0));
        assertEquals("e8ClientAddress", "::1", out.get("e8ClientAddress").get(0));
        assertEquals("e8ClientPort", "0", out.get("e8ClientPort").get(0));
        assertEquals("e8PreAuthType", "2", out.get("e8PreAuthType").get(0));
        assertEquals("e8ServiceName", "krbtgt/E8SEC", out.get("e8ServiceName").get(0));
        assertEquals("e8TicketOptions", "0x40810010", out.get("e8TicketOptions").get(0));
        assertEquals("externalId", "4771", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458575482000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 21 15:51:22", out.get("receiptTimeStr").get(0));
        assertEquals("sourceNameOrIp", "::1", out.get("sourceNameOrIp").get(0));
        assertEquals("sourcePort", 0, out.get("sourcePort").get(0));
        assertEquals("startTime", 1458575482000L, out.get("startTime").get(0));
        assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));

    }

    //Mar 24 07:41:10 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131878409 cs4Label=Logon_ID e8SrcAcc=actuser cn2Label=Logon_Type externalId=4776 cn3Label=Key_Length cs2Label=journalName cs2=Security cs6=MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 cs1Label=eventOutcome cs1=Audit Failure cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package
    @Test
    public void testWindowsEventLogSecurity4776() throws FileNotFoundException {
        String line = "Mar 24 07:41:10 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=w2k8r2-AD.e8sec.lab deviceExternalId=131878409 cs4Label=Logon_ID e8SrcAcc=actuser cn2Label=Logon_Type externalId=4776 cn3Label=Key_Length cs2Label=journalName cs2=Security cs6=MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 cs1Label=eventOutcome cs1=Audit Failure cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("authenticationPackage", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("authenticationPackage").get(0));
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4776-Audit Failure", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Failure", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("cs6").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationDnsDomain", "e8sec.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "w2k8r2-ad", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceExternalId", "131878409", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
        assertEquals("eventOutcome", "/Success", out.get("eventOutcome").get(0));
        assertEquals("externalId", "4776", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1458805270000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "Mar 24 07:41:10", out.get("receiptTimeStr").get(0));
        assertEquals("startTime", 1458805270000L, out.get("startTime").get(0));



    }


    @Test
    public void testWindowsEventLogSecurity4634DoubleSecID() throws FileNotFoundException {
        String line = "May 16 06:26:59 ILCWADLOG03 CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=ILCWADLOG04.dsa.example.com deviceExternalId=629552974 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4634 dntdom=DSA cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=S-1-5-21-1503781981-2815224856-594536586-17831706 duser=ILCWAPP17$ cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x293ca8c1 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("category", "syslog", out.get("category").get(0));
        assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
        assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
        assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
        assertEquals("cefEventName", "An account was logged off", out.get("cefEventName").get(0));
        assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
        assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
        assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
        assertEquals("cefSignatureId", "Security-4634-Audit Success", out.get("cefSignatureId").get(0));
        assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
        assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
        assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
        assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
        assertEquals("cs2", "Security", out.get("cs2").get(0));
        assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
        assertEquals("cs3", "S-1-5-21-1503781981-2815224856-594536586-17831706", out.get("cs3").get(0));
        assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
        assertEquals("cs4", "0x293ca8c1", out.get("cs4").get(0));
        assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
        assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
        assertEquals("destinationLogonID", "0x293ca8c1", out.get("destinationLogonID").get(0));
        assertEquals("destinationNtDomain", "DSA", out.get("destinationNtDomain").get(0));
        assertEquals("destinationUserName", "ilcwapp17$", out.get("destinationUserName").get(0));
        assertEquals("deviceExternalId", "629552974", out.get("deviceExternalId").get(0));
        assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
        assertEquals("deviceHostName", "ilcwadlog03", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "ILCWADLOG03", out.get("deviceNameOrIp").get(0));
        assertEquals("dhost", "ILCWADLOG04.dsa.example.com", out.get("dhost").get(0));
        assertEquals("dntdom", "DSA", out.get("dntdom").get(0));
        assertEquals("duser", "ILCWAPP17$", out.get("duser").get(0));
        assertEquals("eventOutcome", "/Success", out.get("eventOutcome").get(0));
        assertEquals("externalId", "4634", out.get("externalId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
        assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("receiptTime", 1463380019000L, out.get("receiptTime").get(0));
        assertEquals("receiptTimeStr", "May 16 06:26:59", out.get("receiptTimeStr").get(0));
        assertEquals("sourceSecurityID", "S-1-5-21-1503781981-2815224856-594536586-17831706", out.get("sourceSecurityID").get(0));
        assertEquals("startTime", 1463380019000L, out.get("startTime").get(0));

    }

        @Test
    public void testWindowsEventLogSecurity4634() throws FileNotFoundException {
        String line = "Apr 04 08:55:46 w2k8r2-AD CEF:0|Windows|EventLog|unknown|unknown|unknown|5|dhost=w2k8r2-AD.e8sec.lab deviceExternalId=135818531 cs4Label=Logon_ID cn2Label=Logon_Type externalId=4634 dntdom=E8SEC cn3Label=Key_Length cs2Label=journalName cs2=Security cs3=NT AUTHORITY\\SYSTEM duser=W2K8R2-AD$ cs1Label=eventOutcome cs1=Audit Success cs3Label=Security_ID deviceFacility=Microsoft Windows security auditing. cs6Label=Authentication_Package cs4=0x4f980c05 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

            assertEquals("category", "syslog", out.get("category").get(0));
            assertEquals("cefDeviceProduct", "EventLog", out.get("cefDeviceProduct").get(0));
            assertEquals("cefDeviceVendor", "Windows", out.get("cefDeviceVendor").get(0));
            assertEquals("cefDeviceVersion", "unknown", out.get("cefDeviceVersion").get(0));
            assertEquals("cefEventName", "An account was logged off", out.get("cefEventName").get(0));
            assertEquals("cefEventSeverity", "5", out.get("cefEventSeverity").get(0));
            assertEquals("cefHeader", "CEF:0", out.get("cefHeader").get(0));
            assertEquals("cefHeaderVersion", "0", out.get("cefHeaderVersion").get(0));
            assertEquals("cefSignatureId", "Security-4634-Audit Success", out.get("cefSignatureId").get(0));
            assertEquals("cn2Label", "Logon_Type", out.get("cn2Label").get(0));
            assertEquals("cn3Label", "Key_Length", out.get("cn3Label").get(0));
            assertEquals("cs1", "Audit Success", out.get("cs1").get(0));
            assertEquals("cs1Label", "eventOutcome", out.get("cs1Label").get(0));
            assertEquals("cs2", "Security", out.get("cs2").get(0));
            assertEquals("cs2Label", "journalName", out.get("cs2Label").get(0));
            assertEquals("cs3", "NT AUTHORITY\\SYSTEM", out.get("cs3").get(0));
            assertEquals("cs3Label", "Security_ID", out.get("cs3Label").get(0));
            assertEquals("cs4", "0x4f980c05", out.get("cs4").get(0));
            assertEquals("cs4Label", "Logon_ID", out.get("cs4Label").get(0));
            assertEquals("cs6Label", "Authentication_Package", out.get("cs6Label").get(0));
            assertEquals("destinationLogonID", "0x4f980c05", out.get("destinationLogonID").get(0));
            assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
            assertEquals("destinationUserName", "w2k8r2-ad$", out.get("destinationUserName").get(0));
            assertEquals("deviceExternalId", "135818531", out.get("deviceExternalId").get(0));
            assertEquals("deviceFacility", "Microsoft Windows security auditing.", out.get("deviceFacility").get(0));
            assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
            assertEquals("deviceNameOrIp", "w2k8r2-AD", out.get("deviceNameOrIp").get(0));
            assertEquals("dhost", "w2k8r2-AD.e8sec.lab", out.get("dhost").get(0));
            assertEquals("dntdom", "E8SEC", out.get("dntdom").get(0));
            assertEquals("duser", "W2K8R2-AD$", out.get("duser").get(0));
            assertEquals("eventOutcome", "/Success", out.get("eventOutcome").get(0));
            assertEquals("externalId", "4634", out.get("externalId").get(0));
            assertEquals("externalLogSourceType", "MSWinEventLog-splunkcef", out.get("externalLogSourceType").get(0));
            assertEquals("journalName", "Microsoft Windows security auditing", out.get("journalName").get(0));
            assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
            assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
            assertEquals("receiptTime", 1459760146000L, out.get("receiptTime").get(0));
            assertEquals("receiptTimeStr", "Apr 04 08:55:46", out.get("receiptTimeStr").get(0));
            assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
            assertEquals("startTime", 1459760146000L, out.get("startTime").get(0));

    }

    }
