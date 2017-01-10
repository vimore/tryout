package com.securityx.model.mef.morphline.command.qradar;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class QRadarToMefSyslogTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(QRadarToMefSyslogTest.class);

  public QRadarToMefSyslogTest(String name) {
    super(name);
    this.morphlineId = "qradar";
    this.confFile = "logcollection-qradar.conf";
  }

  /*


   */
  private Record buildRecord(String line) {
    Record input = new Record();
    input.put("qradarInput", line);
    input.put("logCollectionHost", "somehost");
    input.put("logCollectionType", "syslog");
    return input;
  }

  @Test
   public void test_QradarDhcpAck() throws FileNotFoundException {
   String line = "SourceIp=192.168.23.11\tAgentLogFile=DhcpSrvLog-Mon\tAgentProtocol=WindowsDHCP\tID=10\tDate=11/16/15\tTime=11:29:44\tDescription=Assign\tIP Address=192.168.23.193\tHost Name=BTP166510.iuser.iroot.adidom.com\tMAC Address=68F72816747B\tUser Name=\tTransactionID=3546000981\tQResult=0\tProbationtime=\tCorrelationID=\tDhcid=\tVendorClass(Hex)=0x4D53465420352E30\tVendorClass(ASCII)=MSFT 5.0\tUserClass(Hex)=\tUserClass(ASCII)=\tRelayAgentInformation=\tDnsRegError=0\t";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out);
      assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
      assertEquals("destinationAddress", "192.168.23.193", out.get("destinationAddress").get(0));
      assertEquals("destinationDnsDomain", "iuser.iroot.adidom.com", out.get("destinationDnsDomain").get(0));
      assertEquals("destinationHostName", "btp166510", out.get("destinationHostName").get(0));
      assertEquals("destinationMacAddress", "68F72816747B", out.get("destinationMacAddress").get(0));
      assertEquals("destinationNameOrIp", "BTP166510.iuser.iroot.adidom.com", out.get("destinationNameOrIp").get(0));
      assertEquals("deviceHostName", "somehost", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
      assertEquals("externalLogSourceType", "qradar-windows-dhcp", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
      assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
      assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
      assertEquals("startTime", 1447673384000L, out.get("startTime").get(0));
   }

    @Test
    public void test_QradarDhcpRenew() throws FileNotFoundException {
        String line = "SourceIp=192.168.23.11\tAgentLogFile=DhcpSrvLog-Fri\tAgentProtocol=WindowsDHCP\tID=11\tDate=11/20/15\tTime=02:16:18\tDescription=Renew\tIP Address=192.168.23.221\tHost Name=CAL-XA-1-03.cyber.lab\tMAC Address=005056AAD63E\tUser Name=\tTransactionID=1818837492\tQResult=0\tProbationtime=\tCorrelationID=\tDhcid=\tVendorClass(Hex)=0x4D53465420352E30\tVendorClass(ASCII)=MSFT 5.0\tUserClass(Hex)=\tUserClass(ASCII)=\tRelayAgentInformation=\tDnsRegError=0\t";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
        assertEquals("destinationAddress", "192.168.23.221", out.get("destinationAddress").get(0));
        assertEquals("destinationDnsDomain", "cyber.lab", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "cal-xa-1-03", out.get("destinationHostName").get(0));
        assertEquals("destinationMacAddress", "005056AAD63E", out.get("destinationMacAddress").get(0));
        assertEquals("destinationNameOrIp", "CAL-XA-1-03.cyber.lab", out.get("destinationNameOrIp").get(0));
        assertEquals("deviceHostName", "somehost", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "qradar-windows-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1447985778000L, out.get("startTime").get(0));
    }

     @Test
    public void test_QradarDhcpRelease() throws FileNotFoundException {
        String line = "SourceIp=192.168.23.11\tAgentLogFile=DhcpSrvLog-Sat\tAgentProtocol=WindowsDHCP\tID=18\tDate=11/14/15\tTime=10:20:33\tDescription=Expired\tIP Address=192.168.23.192\tHost Name=\tMAC Address=\tUser Name=\tTransactionID=0\tQResult=6\tProbationtime=\tCorrelationID=\tDhcid=\tVendorClass(Hex)=\tVendorClass(ASCII)=\tUserClass(Hex)=\tUserClass(ASCII)=\tRelayAgentInformation=\tDnsRegError=0\t";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("cefSignatureId", "DHCPRELEASE", out.get("cefSignatureId").get(0));
        assertEquals("destinationAddress", "192.168.23.192", out.get("destinationAddress").get(0));
        assertEquals("deviceHostName", "somehost", out.get("deviceHostName").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
        assertEquals("externalLogSourceType", "qradar-windows-dhcp", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
        assertEquals("startTime", 1447496433000L, out.get("startTime").get(0));
    }

    @Test
    public void test_QradarWindows4624() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tSource=Microsoft-Windows-Security-Auditing\tComputer=superintendent2.cyber.lab\tUser=\tDomain=\tEventID=4624\tEventIDCode=4624\tEventType=8\tEventCategory=12544\tRecordNumber=227088446\tTimeGenerated=1447931196566\tTimeWritten=1447931196566\tMessage=An account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    Impersonation Level:  %%1840    New Logon:   Security ID:  S-1-5-21-3158436021-3638059681-3118127365-1346   Account Name:  INTEL-EPO-01$   Account Domain:  CYBER   Logon ID:  0xf3b7a3b   Logon GUID:  6f47ba17-15e-4269-556b-8708a67647e6    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name:    Source Network Address: 10.20.10.10   Source Port:  50652    Detailed Authentication Information:   Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).    The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.    The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The impersonation level field indicates the extent to which a process in the logon session can impersonate.    The authentication information fields provide detailed information about this specific logon request.   - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
        assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationLogonGUID", "6f47ba17-15e-4269-556b-8708a67647e6", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationLogonID", "0xf3b7a3b", out.get("destinationLogonID").get(0));
        assertEquals("destinationNtDomain", "CYBER", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-5-21-3158436021-3638059681-3118127365-1346", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "INTEL-EPO-01$".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("eventOutcome", "Success Audit", out.get("eventOutcome").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("sourceAddress", "10.20.10.10", out.get("sourceAddress").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
//        assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
        assertEquals("sourcePort", 50652, out.get("sourcePort").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
//        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));

    }

    @Test
    public void test_QradarWindows4625() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tSource=Microsoft-Windows-Security-Auditing\tComputer=superintendent2.cyber.lab\tUser=\tDomain=\tEventID=4625\tEventIDCode=4625\tEventType=16\tEventCategory=12544\tRecordNumber=227137904\tTimeGenerated=1447935869429\tTimeWritten=1447935869429\tMessage=An account failed to log on.    Subject:   Security ID:  S-1-5-18   Account Name:  SUPERINTENDENT2$   Account Domain:  CYBER   Logon ID:  0x3e7    Logon Type:   3    Account For Which Logon Failed:   Security ID:  S-1-0-0   Account Name:  admin   Account Domain:  CYBER    Failure Information:   Failure Reason:  %%2313   Status:   0xc000006d   Sub Status:  0xc000006a    Process Information:   Caller Process ID: 0x244   Caller Process Name: C:\\Windows\\System32\\lsass.exe    Network Information:   Workstation Name: SUPERINTENDENT2   Source Network Address: 192.168.23.37   Source Port:  44904    Detailed Authentication Information:   Logon Process:  Advapi     Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon request fails. It is generated on the computer where access was attempted.    The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network).    The Process Information fields indicate which account and process on the system requested the logon.    The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The authentication information fields provide detailed information about this specific logon request.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("authenticationPackage", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("authenticationPackage").get(0));
        assertEquals("cefSignatureId", "Security-4625-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNtDomain", "CYBER", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "S-1-0-0", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "admin", out.get("destinationUserName").get(0));
        assertEquals("eventOutcome", "Failure Audit", out.get("eventOutcome").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonProcess", "Advapi", out.get("logonProcess").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("sourceAddress", "192.168.23.37", out.get("sourceAddress").get(0));
        assertEquals("sourceHostName", "superintendent2", out.get("sourceHostName").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("sourceNameOrIp", "SUPERINTENDENT2", out.get("sourceNameOrIp").get(0));
        assertEquals("sourceNtDomain", "CYBER", out.get("sourceNtDomain").get(0));
        assertEquals("sourcePort", 44904, out.get("sourcePort").get(0));
        assertEquals("sourceProcessID", "0x244", out.get("sourceProcessID").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "SUPERINTENDENT2$".toLowerCase(), out.get("sourceUserName").get(0));
        assertEquals("status", "0xc000006d", out.get("status").get(0));
        assertEquals("subStatus", "0xc000006a", out.get("subStatus").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));

    }
    @Test
    public void test_QradarWindows4648() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tSource=Microsoft-Windows-Security-Auditing\tComputer=superintendent.cyber.lab\tUser=\tDomain=\tEventID=4648\tEventIDCode=4648\tEventType=8\tEventCategory=12544\tRecordNumber=557071430\tTimeGenerated=1447931162259\tTimeWritten=1447931162259\tMessage=A logon was attempted using explicit credentials.    Subject:   Security ID:  S-1-5-18   Account Name:  SUPERINTENDENT$   Account Domain:  CYBER   Logon ID:  0x3e7   Logon GUID:  0-0-0-0000-000000000000    Account Whose Credentials Were Used:   Account Name:  Administrator   Account Domain:  CYBER   Logon GUID:  0-0-0-0000-000000000000    Target Server:   Target Server Name: localhost   Additional Information: localhost    Process Information:   Process ID:  0x230   Process Name:  C:\\Windows\\System32\\lsass.exe    Network Information:   Network Address: 192.168.23.37   Port:   60267    This event is generated when a process attempts to log on an account by explicitly specifying that accountâs credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command.";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("cefSignatureId", "Security-4648-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationHostName", "localhost", out.get("destinationHostName").get(0));
        assertEquals("destinationLogonGUID", "0-0-0-0000-000000000000", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationNameOrIp", "localhost", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationNtDomain", "CYBER", out.get("destinationNtDomain").get(0));
        assertEquals("destinationUserName", "Administrator".toLowerCase(), out.get("destinationUserName").get(0));
        assertEquals("eventOutcome", "Success Audit", out.get("eventOutcome").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("sourceAddress", "192.168.23.37", out.get("sourceAddress").get(0));
        assertEquals("sourceLogonGUID", "0-0-0-0000-000000000000", out.get("sourceLogonGUID").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("sourceNameOrIp", "192.168.23.37", out.get("sourceNameOrIp").get(0));
        assertEquals("sourceNtDomain", "CYBER", out.get("sourceNtDomain").get(0));
        assertEquals("sourcePort", 60267, out.get("sourcePort").get(0));
        assertEquals("sourceProcessId", "0x230", out.get("sourceProcessId").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "SUPERINTENDENT$".toLowerCase(), out.get("sourceUserName").get(0));

    }

    @Test
    public void test_QradarWindows4673() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=hostname.firm.fenwick.llp\tOriginatingComputer=\tUser=\tDomain=\tEventID=4673\tEventIDCode=4673\tEventType=8\tEventCategory=13056\tRecordNumber=1630807979\tTimeGenerated=1464726099\tTimeWritten=1464726099\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A privileged service was called.  Subject:  Security ID:  FWNT\\Backup  Account Name:  Backup  Account Domain:  FWNT  Logon ID:  0x2edbdd49a1  Service:  Server: NT Local Security Authority / Authentication Service  Service Name: LsaRegisterLogonProcess()  Process:  Process ID: 0x2ec  Process Name: C:\\Windows\\System32\\lsass.exe  Service Request Information:  Privileges:  SeTcbPrivilege ";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("AgentDevice", "WindowsLog", out.get("AgentDevice").get(0));
        assertEquals("AgentLogFile", "Security", out.get("AgentLogFile").get(0));
        assertEquals("Computer", "hostname.firm.fenwick.llp", out.get("Computer").get(0));
        assertEquals("EventCategory", "13056", out.get("EventCategory").get(0));
        assertEquals("EventID", "4673", out.get("EventID").get(0));
        assertEquals("EventIDCode", "4673", out.get("EventIDCode").get(0));
        assertEquals("EventType", "8", out.get("EventType").get(0));
        assertEquals("Keywords", "0", out.get("Keywords").get(0));
        assertEquals("Level", "0", out.get("Level").get(0));
        assertEquals("Opcode", "0", out.get("Opcode").get(0));
        assertEquals("PluginVersion", "7.2.2.984723", out.get("PluginVersion").get(0));
        assertEquals("RecordNumber", "1630807979", out.get("RecordNumber").get(0));
        assertEquals("Source", "Microsoft-Windows-Security-Auditing", out.get("Source").get(0));
        assertEquals("Task", "0", out.get("Task").get(0));
        assertEquals("TimeGenerated", "1464726099", out.get("TimeGenerated").get(0));
        assertEquals("TimeWritten", "1464726099", out.get("TimeWritten").get(0));
        assertEquals("_MatchedEvent", "windowsTryRules", out.get("_MatchedEvent").get(0));
        assertEquals("cefSignatureId", "Security-4673-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationProcessName", "LsaRegisterLogonProcess()", out.get("destinationProcessName").get(0));
        assertEquals("destinationServiceServer", "NT Local Security Authority / Authentication Service", out.get("destinationServiceServer").get(0));
        assertEquals("eventOutcome", "Success Audit", out.get("eventOutcome").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("privileges", "SeTcbPrivilege", out.get("privileges").get(0));
        assertEquals("snareMsgDesc", "A privileged service was called.", out.get("snareMsgDesc").get(0));
        assertEquals("sourceLogonID", "0x2edbdd49a1", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "FWNT", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessID", "0x2ec", out.get("sourceProcessID").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "FWNT\\Backup", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "backup", out.get("sourceUserName").get(0));
    }

    @Test
    public void test_QradarWindows4674() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=hostname.firm.fenwick.llp\tOriginatingComputer=\tUser=\tDomain=\tEventID=4674\tEventIDCode=4674\tEventType=8\tEventCategory=13056\tRecordNumber=1630812361\tTimeGenerated=1464727051\tTimeWritten=1464727051\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=An operation was attempted on a privileged object.  Subject:  Security ID:  FWNT\\Backup  Account Name:  Backup  Account Domain:  FWNT  Logon ID:  0x2edbe44ca2  Object:  Object Server: Security  Object Type: Mutant  Object Name: \\BaseNamedObjects\\LOADPERF_MUTEX  Object Handle: 0x258  Process Information:  Process ID: 0x5f0c4  Process Name: C:\\Windows\\SysWOW64\\wbem\\WmiPrvSE.exe  Requested Operation:  Desired Access: DELETE      READ_CONTROL      WRITE_DAC      WRITE_OWNER      SYNCHRONIZE      Query mutant state        Privileges:  SeTakeOwnershipPrivilege ";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("AgentDevice", "WindowsLog", out.get("AgentDevice").get(0));
        assertEquals("AgentLogFile", "Security", out.get("AgentLogFile").get(0));
        assertEquals("Computer", "hostname.firm.fenwick.llp", out.get("Computer").get(0));
        assertEquals("EventCategory", "13056", out.get("EventCategory").get(0));
        assertEquals("EventID", "4674", out.get("EventID").get(0));
        assertEquals("EventIDCode", "4674", out.get("EventIDCode").get(0));
        assertEquals("EventType", "8", out.get("EventType").get(0));
        assertEquals("Keywords", "0", out.get("Keywords").get(0));
        assertEquals("Level", "0", out.get("Level").get(0));
        assertEquals("Opcode", "0", out.get("Opcode").get(0));
        assertEquals("PluginVersion", "7.2.2.984723", out.get("PluginVersion").get(0));
        assertEquals("RecordNumber", "1630812361", out.get("RecordNumber").get(0));
        assertEquals("Source", "Microsoft-Windows-Security-Auditing", out.get("Source").get(0));
        assertEquals("Task", "0", out.get("Task").get(0));
        assertEquals("TimeGenerated", "1464727051", out.get("TimeGenerated").get(0));
        assertEquals("TimeWritten", "1464727051", out.get("TimeWritten").get(0));
        assertEquals("_MatchedEvent", "windowsTryRules", out.get("_MatchedEvent").get(0));
        assertEquals("cefSignatureId", "Security-4674-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("desiredAccess", "DELETE      READ_CONTROL      WRITE_DAC      WRITE_OWNER      SYNCHRONIZE      Query mutant state", out.get("desiredAccess").get(0));
        assertEquals("destinationObjectHandle", "0x258", out.get("destinationObjectHandle").get(0));
        assertEquals("destinationObjectName", "\\BaseNamedObjects\\LOADPERF_MUTEX", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "Security", out.get("destinationObjectServer").get(0));
        assertEquals("destinationObjectType", "Mutant", out.get("destinationObjectType").get(0));
        assertEquals("eventOutcome", "Success Audit", out.get("eventOutcome").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("privileges", "SeTakeOwnershipPrivilege", out.get("privileges").get(0));
        assertEquals("snareMsgDesc", "An operation was attempted on a privileged object.", out.get("snareMsgDesc").get(0));
        assertEquals("sourceLogonID", "0x2edbe44ca2", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "FWNT", out.get("sourceNtDomain").get(0));
        assertEquals("sourceProcessID", "0x5f0c4", out.get("sourceProcessID").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\SysWOW64\\wbem\\WmiPrvSE.exe", out.get("sourceProcessName").get(0));
        assertEquals("sourceSecurityID", "FWNT\\Backup", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "backup", out.get("sourceUserName").get(0));
    }

    @Test
    public void test_QradarWindows4738() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=hostname.firm.fenwick.llp\tOriginatingComputer=\tUser=\tDomain=\tEventID=4738\tEventIDCode=4738\tEventType=8\tEventCategory=13824\tRecordNumber=1630812625\tTimeGenerated=1464727111\tTimeWritten=1464727111\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A user account was changed.  Subject:  Security ID:  FWNT\\username  Account Name:  svcqpwdm  Account Domain:  FWNT  Logon ID:  0x2edc49201d  Target Account:  Security ID:  FWNT\\$INF100-2THBQA0NVUU8  Account Name:  $INF100-2THBQA0NVUU8  Account Domain:  FWNT  Changed Attributes:  SAM Account Name: -  Display Name:  -  User Principal Name: -  Home Directory:  -  Home Drive:  -  Script Path:  -  Profile Path:  -  User Workstations: -  Password Last Set: -  Account Expires:  -  Primary Group ID: -  AllowedToDelegateTo: -  Old UAC Value:  -  New UAC Value:  -  User Account Control: -  User Parameters: -  SID History:  -  Logon Hours:  -  Additional Information:  Privileges:  - ";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("AgentDevice", "WindowsLog", out.get("AgentDevice").get(0));
        assertEquals("AgentLogFile", "Security", out.get("AgentLogFile").get(0));
        assertEquals("Computer", "hostname.firm.fenwick.llp", out.get("Computer").get(0));
        assertEquals("EventCategory", "13824", out.get("EventCategory").get(0));
        assertEquals("EventID", "4738", out.get("EventID").get(0));
        assertEquals("EventIDCode", "4738", out.get("EventIDCode").get(0));
        assertEquals("EventType", "8", out.get("EventType").get(0));
        assertEquals("Keywords", "0", out.get("Keywords").get(0));
        assertEquals("Level", "0", out.get("Level").get(0));
        assertEquals("Opcode", "0", out.get("Opcode").get(0));
        assertEquals("PluginVersion", "7.2.2.984723", out.get("PluginVersion").get(0));
        assertEquals("RecordNumber", "1630812625", out.get("RecordNumber").get(0));
        assertEquals("Source", "Microsoft-Windows-Security-Auditing", out.get("Source").get(0));
        assertEquals("Task", "0", out.get("Task").get(0));
        assertEquals("TimeGenerated", "1464727111", out.get("TimeGenerated").get(0));
        assertEquals("TimeWritten", "1464727111", out.get("TimeWritten").get(0));
        assertEquals("_MatchedEvent", "windowsTryRules", out.get("_MatchedEvent").get(0));
        assertEquals("accountExpires", "-", out.get("accountExpires").get(0));
        assertEquals("adDisplayName", "-", out.get("adDisplayName").get(0));
        assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
        assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
        assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
        assertEquals("adPasswdLastSet", "-", out.get("adPasswdLastSet").get(0));
        assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
        assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
        assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
        assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
        assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
        assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
        assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
        assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
        assertEquals("allowedToDelegateTo", "-", out.get("allowedToDelegateTo").get(0));
        assertEquals("destinationNtDomain", "FWNT", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "FWNT\\$INF100-2THBQA0NVUU8", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "$inf100-2thbqa0nvuu8", out.get("destinationUserName").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonHours", "-", out.get("logonHours").get(0));
        assertEquals("newUacValue", "-", out.get("newUacValue").get(0));
        assertEquals("snareMsgDesc", "A user account was changed.", out.get("snareMsgDesc").get(0));
        assertEquals("sourceLogonID", "0x2edc49201d", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "FWNT", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "FWNT\\username", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "svcqpwdm", out.get("sourceUserName").get(0));
    }

    @Test
    public void test_QradarWindows4768ParsingFailure() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=FIRWSVCDCI.firm.example.local\tOriginatingComputer=\tUser=\tDomain=\tEventID=4776\tEventIDCode=4776\tEventType=16\tEventCategory=14336\tRecordNumber=1630610465\tTimeGenerated=1464704999\tTimeWritten=1464704999\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=The computer attempted to validate the credentials for an account.  Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 Logon Account: passwd!user@example.com Source Workstation: FM-4HM74XI Error Code: 0xc0000064 ";
        Record r = buildRecord(line);
        boolean result = doTest(r);

        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

    }



    @Test
    public void test_QradarWindows4742() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=hostname.firm.fenwick.llp\tOriginatingComputer=\tUser=\tDomain=\tEventID=4742\tEventIDCode=4742\tEventType=8\tEventCategory=13825\tRecordNumber=1630813689\tTimeGenerated=1464727342\tTimeWritten=1464727342\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A computer account was changed.  Subject:  Security ID:  FWNT\\SVCSFBWEBAPP2$  Account Name:  SVCSFBWEBAPP2$  Account Domain:  FWNT  Logon ID:  0x2edcd4f10d  Computer Account That Was Changed:  Security ID:  FWNT\\username$  Account Name:  username$  Account Domain:  FWNT  Changed Attributes:  SAM Account Name: -  Display Name:  -  User Principal Name: -  Home Directory:  -  Home Drive:  -  Script Path:  -  Profile Path:  -  User Workstations: -  Password Last Set: -  Account Expires:  -  Primary Group ID: -  AllowedToDelegateTo: -  Old UAC Value:  -  New UAC Value:  -  User Account Control: -  User Parameters: -  SID History:  -  Logon Hours:  -  DNS Host Name:  -  Service Principal Names:    HOST/hostname.firm.fenwick.llp   RestrictedKrbHost/hostname.firm.fenwick.llp   HOST/SVCSFBWEBAPP2   RestrictedKrbHost/SVCSFBWEBAPP2   TERMSRV/SVCSFBWEBAPP2   TERMSRV/hostname.firm.fenwick.llp   WSMAN/hostname.firm.fenwick.llp   WSMAN/hostname  Additional Information:  Privileges:  - ";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

        assertEquals("AgentDevice", "WindowsLog", out.get("AgentDevice").get(0));
        assertEquals("AgentLogFile", "Security", out.get("AgentLogFile").get(0));
        assertEquals("Computer", "hostname.firm.fenwick.llp", out.get("Computer").get(0));
        assertEquals("EventCategory", "13825", out.get("EventCategory").get(0));
        assertEquals("EventID", "4742", out.get("EventID").get(0));
        assertEquals("EventIDCode", "4742", out.get("EventIDCode").get(0));
        assertEquals("EventType", "8", out.get("EventType").get(0));
        assertEquals("Keywords", "0", out.get("Keywords").get(0));
        assertEquals("Level", "0", out.get("Level").get(0));
        assertEquals("Opcode", "0", out.get("Opcode").get(0));
        assertEquals("PluginVersion", "7.2.2.984723", out.get("PluginVersion").get(0));
        assertEquals("RecordNumber", "1630813689", out.get("RecordNumber").get(0));
        assertEquals("Source", "Microsoft-Windows-Security-Auditing", out.get("Source").get(0));
        assertEquals("Task", "0", out.get("Task").get(0));
        assertEquals("TimeGenerated", "1464727342", out.get("TimeGenerated").get(0));
        assertEquals("TimeWritten", "1464727342", out.get("TimeWritten").get(0));
        assertEquals("_MatchedEvent", "windowsTryRules", out.get("_MatchedEvent").get(0));
        assertEquals("accountExpires", "-", out.get("accountExpires").get(0));
        assertEquals("adDnsHostName", "-", out.get("adDnsHostName").get(0));
        assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
        assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
        assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
        assertEquals("adPasswdLastSet", "-", out.get("adPasswdLastSet").get(0));
        assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
        assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
        assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
        assertEquals("adServerPrincipalNames", "HOST/hostname.firm.fenwick.llp   RestrictedKrbHost/hostname.firm.fenwick.llp   HOST/SVCSFBWEBAPP2   RestrictedKrbHost/SVCSFBWEBAPP2   TERMSRV/SVCSFBWEBAPP2   TERMSRV/hostname.firm.fenwick.llp   WSMAN/hostname.firm.fenwick.llp   WSMAN/hostname", out.get("adServerPrincipalNames").get(0));
        assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
        assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
        assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
        assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
        assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
        assertEquals("allowedToDelegateTo", "-", out.get("allowedToDelegateTo").get(0));
        assertEquals("destinationNtDomain", "FWNT", out.get("destinationNtDomain").get(0));
        assertEquals("destinationSecurityID", "FWNT\\username$", out.get("destinationSecurityID").get(0));
        assertEquals("destinationUserName", "username$", out.get("destinationUserName").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("logCollectionType", "syslog", out.get("logCollectionType").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("logonHours", "-", out.get("logonHours").get(0));
        assertEquals("newUacValue", "-", out.get("newUacValue").get(0));
        assertEquals("snareMsgDesc", "A computer account was changed.", out.get("snareMsgDesc").get(0));
        assertEquals("sourceLogonID", "0x2edcd4f10d", out.get("sourceLogonID").get(0));
        assertEquals("sourceNtDomain", "FWNT", out.get("sourceNtDomain").get(0));
        assertEquals("sourceSecurityID", "FWNT\\SVCSFBWEBAPP2$", out.get("sourceSecurityID").get(0));
        assertEquals("sourceUserName", "svcsfbwebapp2$", out.get("sourceUserName").get(0));

    }

    @Test
    public void test_QradarWindows4768() throws FileNotFoundException {
        String line = "AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=hostname.firm.fenwick.llp\tOriginatingComputer=\tUser=\tDomain=\tEventID=4768\tEventIDCode=4742\tEventType=8\tEventCategory=13825\tRecordNumber=1630813689\tTimeGenerated=1464727342\tTimeWritten=1464727342\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A Kerberos authentication ticket (TGT) was requested.    Account Information:   Account Name:  jyria   Supplied Realm Name: e8sec.lab   User ID:   S-1-5-21-1908711140-209524994-2501478566-1104    Service Information:   Service Name:  krbtgt   Service ID:  S-1-5-21-1908711140-209524994-2501478566-502    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58741    Additional Information:   Ticket Options:  0x40810010   Result Code:  0x0   Ticket Encryption Type: 0x12   Pre-Authentication Type: 2    Certificate Information:   Certificate Issuer Name:     Certificate Serial Number:    Certificate Thumbprint:      Certificate information is only provided if a certificate was used for pre-authentication.    Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);

    }
//  public void test_samples() throws FileNotFoundException {
//
//    try {
//      File f = new File("./src/test/resources/access.log");
//      BufferedReader br = new BufferedReader(new FileReader(f));
//      String line;
//      int i = 0;
//      while ((line = br.readLine()) != null) { // while loop begins here
//        i++;
//        boolean result = doTest(line);
//        if (!result) {
//          System.res.println("DO NOT MATCH :" + line);
//        }
//      } // end while
//      System.res.println("parsed : " + i + " lines");
//
////      String line = "1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
////      boolean result = doTest(line);
////      assertEquals(true, result);
////      Record res = this.resCommand.getRecord(0);
//    } catch (IOException ex) {
//      java.util.logging.Logger.getLogger(WebProxySquidTest.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }


//    @Test
//    public void test_DhcpDateIssue() throws FileNotFoundException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat
//                ("MMM  dd HH:mm:ss ", Locale.US);
//        dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
//        SimpleDateFormat dateFormat2 = new SimpleDateFormat
//                ("YYYY MMM  dd HH:mm:ss ", Locale.US);
//        dateFormat2.setTimeZone(TimeZone.getTimeZone("PST"));
//        long epoch = 1446058691000L;
//        for (int i =0; i<24; i++) {
//
//            String dateStr = dateFormat.format(new Date(epoch));
//            //OutUtils.printOut();
//            String line = "<30>"+dateStr+"dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
//            boolean result = doTest(buildRecord(line));
//            assertEquals(true, result);
//            Record res = this.outCommand.getRecord(0);
//            this.outCommand.clear();
//            long parsedEpoch = (Long) res.get("startTime").get(0);
//            OutUtils.printOut("--> "+i+" "+epoch+" '"+dateStr+"'parsed date : "+ parsedEpoch+" : "+ dateFormat2.format(new Date(parsedEpoch))+" "+(parsedEpoch-epoch));
//            epoch+=3600*1000;
//        }
//        assertTrue(false);
//        String line = "<30>May  9 09:52:40 dnsmasq-dhcp[304]: DHCPACK(br0) 192.168.10.208 b8:e8:56:03:c9:7e";
//        boolean result = doTest(buildRecord(line));
//        assertEquals(true, result);
//        Record res = this.outCommand.getRecord(0);
//        assertEquals("destinationAddress", "192.168.10.208", res.get("destinationAddress").get(0));
//        assertEquals("destinationMacAddress", "b8:e8:56:03:c9:7e", res.get("destinationMacAddress").get(0));
//        assertEquals("cefSignatureId", "DHCPACK", res.get("cefSignatureId").get(0));
//        assertEquals("deviceInterface", "br0", res.get("deviceInterface").get(0));
//        assertEquals("deviceNameOrIp", "somehost", res.get("deviceNameOrIp").get(0));
//        assertEquals("deviceProcessId", 304, res.get("deviceProcessId").get(0));
//        assertEquals("deviceProcessName", "dnsmasq-dhcp", res.get("deviceProcessName").get(0));
//        assertEquals("externalLogSourceType", "dnsmasq-dhcp", res.get("externalLogSourceType").get(0));
//        assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
//        assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
//        assertEquals("logSourceType", "HETMef", res.get("logSourceType").get(0));
//        // Sat, 09 May 2015 16:52:40 GMT
//        assertEquals("startTime", 1431190360000L, res.get("startTime").get(0));
//    }
}
