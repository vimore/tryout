package com.securityx.model.mef.morphline.command.windows;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.MefBluecoatScriptTest;
import com.securityx.utils.OutUtils;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class WindowsSnareTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(WindowsSnareTest.class);

  public WindowsSnareTest(String name) {
    super(name);
    this.morphlineId = "windowssnare";
    this.confFile = "windowssnare-to-mef.conf";
    try {
 	   setUp();
    } catch (Exception ex) {
 	   OutUtils.printOut("santanu: exception: "+ex.getMessage());
    }
  }


  private Record buildRecord(String line){
    Record r = new Record();
    r.put("logCollectionHost", "someHost");
    r.put("snareInput", line);
    return r;

  }


      @Test
public void testSecurity4768SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10904\tWed Apr 23 15:51:07 2014\t4768\tMicrosoft-Windows-Security-Auditing\te8sec.lab\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tKerberos Authentication Service\t\tA Kerberos authentication ticket (TGT) was requested.    Account Information:   Account Name:  jyria   Supplied Realm Name: e8sec.lab   User ID:   S-1-5-21-1908711140-209524994-2501478566-1104    Service Information:   Service Name:  krbtgt   Service ID:  S-1-5-21-1908711140-209524994-2501478566-502    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58741    Additional Information:   Ticket Options:  0x40810010   Result Code:  0x0   Ticket Encryption Type: 0x12   Pre-Authentication Type: 2    Certificate Information:   Certificate Issuer Name:     Certificate Serial Number:    Certificate Thumbprint:      Certificate information is only provided if a certificate was used for pre-authentication.    Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.\t10903";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
   assertEquals("sourcePort", 58741, out.get("sourcePort").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("preAuthenticationType", "2", out.get("preAuthenticationType").get(0));
   assertEquals("status", "0x0", out.get("status").get(0));
   assertEquals("destinationServiceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-502", out.get("destinationServiceSecurityID").get(0));
   assertEquals("destinationServiceName", "krbtgt", out.get("destinationServiceName").get(0));
   assertEquals("snareCategoryString", "Kerberos Authentication Service", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10903", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10904", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4768", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "e8sec.lab\\jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268267000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A Kerberos authentication ticket (TGT) was requested.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
   assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
   assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));
   assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("destinationSecurityID").get(0));
   assertEquals("cefSignatureId", "Security-4768-Success Audit", out.get("cefSignatureId").get(0));
}


   @Test
   public void testSecurity4771FailureAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10744\tThu Apr 24 14:34:14 2014\t4771\tMicrosoft-Windows-Security-Auditing\tjyria\tN/A\tFailure Audit\tw2k8r2-AD.e8sec.lab\tKerberos Authentication Service\t\tKerberos pre-authentication failed.    Account Information:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria    Service Information:   Service Name:  krbtgt/e8sec.lab    Network Information:   Client Address:  ::1   Client Port:  0    Additional Information:   Ticket Options:  0x40810010   Failure Code:  0x18   Pre-Authentication Type: 2    Certificate Information:   Certificate Issuer Name:     Certificate Serial Number:     Certificate Thumbprint:      Certificate information is only provided if a certificate was used for pre-authentication.    Pre-authentication types, ticket options and failure codes are defined in RFC 4120.    If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present.\t10743 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   assertEquals("sourceNameOrIp", "::1", out.get("sourceNameOrIp").get(0));
   assertEquals("sourcePort", 0, out.get("sourcePort").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("preAuthenticationType", "2", out.get("preAuthenticationType").get(0));
   assertEquals("status", "0x18", out.get("status").get(0));
   assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("destinationSecurityID").get(0));
   assertEquals("destinationServiceName", "krbtgt/e8sec.lab", out.get("destinationServiceName").get(0));
   assertEquals("snareCategoryString", "Kerberos Authentication Service", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10743", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10744", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4771", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398350054000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "Kerberos pre-authentication failed.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));
   }

  
   @Test
   public void testSecurity4769SuccessAudit() throws FileNotFoundException {
	   //   String line = "MSWinEventLog\t0\tSecurity\t10905\tWed Apr 23 15:51:08 2014\t4769\tMicrosoft-Windows-Security-Auditing\tE8SEC.LAB\\jyria@E8SEC.LAB\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  jyria@E8SEC.LAB   Account Domain:  E8SEC.LAB   Logon GUID:  {AD7B3FA1-ABFF-23FE-F052-73002A41531D}    Service Information:   Service Name:  W2K8R2-SRC$   Service ID:  S-1-5-21-1908711140-209524994-2501478566-1106    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58742    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.\t4767";
   String line = "MSWinEventLog\t0\tSecurity\t10905\tWed Apr 23 15:51:08 2014\t4769\tMicrosoft-Windows-Security-Auditing\tE8SEC.LAB\\jyria@E8SEC.LAB\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  jyria@E8SEC.LAB   Account Domain:  E8SEC.LAB   Logon GUID:  {AD7B3FA1-ABFF-23FE-F052-73002A41531D}    Service Information:   Service Name:  W2K8R2-SRC$   Service ID:  S-1-5-21-1908711140-209524994-2501478566-1106    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58742    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.\t2147403191";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationNtDomain", "E8SEC.LAB", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria@E8SEC.LAB".toLowerCase(), out.get("destinationUserName").get(0));
   assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
   assertEquals("sourcePort", 58742, out.get("sourcePort").get(0));
   assertEquals("status", "0x0", out.get("status").get(0));
   assertEquals("destinationLogonGUID", "{AD7B3FA1-ABFF-23FE-F052-73002A41531D}", out.get("destinationLogonGUID").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("destinationServiceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1106", out.get("destinationServiceSecurityID").get(0));
   assertEquals("destinationServiceName", "W2K8R2-SRC$", out.get("destinationServiceName").get(0));
   assertEquals("snareCategoryString", "Kerberos Service Ticket Operations", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "2147403191", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10905", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4769", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC.LAB\\jyria@E8SEC.LAB", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268268000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A Kerberos service ticket was requested.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
   assertEquals("ticketOptions", "0x40810000", out.get("ticketOptions").get(0));
   assertEquals("transitedService", "-", out.get("transitedService").get(0));
   }
   @Test
   public void testSecurity4769SuccessAuditServiceNameAndHost() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10905\tWed Apr 23 15:51:08 2014\t4769\tMicrosoft-Windows-Security-Auditing\tE8SEC.LAB\\jyria@E8SEC.LAB\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  jyria@E8SEC.LAB   Account Domain:  E8SEC.LAB   Logon GUID:  {AD7B3FA1-ABFF-23FE-F052-73002A41531D}    Service Information:   Service Name:  lab/W2K8R2-SRC.e8sec.lab   Service ID:  S-1-5-21-1908711140-209524994-2501478566-1106    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58742    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.\t4767";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationNtDomain", "E8SEC.LAB", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria@E8SEC.LAB".toLowerCase(), out.get("destinationUserName").get(0));
   assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
   assertEquals("sourcePort", 58742, out.get("sourcePort").get(0));
   assertEquals("status", "0x0", out.get("status").get(0));
   assertEquals("destinationLogonGUID", "{AD7B3FA1-ABFF-23FE-F052-73002A41531D}", out.get("destinationLogonGUID").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("destinationServiceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1106", out.get("destinationServiceSecurityID").get(0));
   assertEquals("destinationServiceName", "lab/W2K8R2-SRC.e8sec.lab", out.get("destinationServiceName").get(0));
   assertEquals("destinationProcessName", "lab", out.get("destinationProcessName").get(0));
   assertEquals("snareCategoryString", "Kerberos Service Ticket Operations", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "4767", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10905", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4769", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC.LAB\\jyria@E8SEC.LAB", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268268000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A Kerberos service ticket was requested.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
   assertEquals("ticketOptions", "0x40810000", out.get("ticketOptions").get(0));
   assertEquals("transitedService", "-", out.get("transitedService").get(0));
   }
@Test
   public void testSecurity4769SuccessAuditDefaut() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10905\tWed Apr 23 15:51:08 2014\t4769\tMicrosoft-Windows-Security-Auditing\tE8SEC.LAB\\jyria@E8SEC.LAB\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tKerberos Service Ticket Operations\t\tA Kerberos service ticket was requested.    Account Information:   Account Name:  jyria@E8SEC.LAB   Account Domain:  E8SEC.LAB   Logon GUID:  {AD7B3FA1-ABFF-23FE-F052-73002A41531D}    Service Information:   Service Name:  someuserorservice   Service ID:  S-1-5-21-1908711140-209524994-2501478566-1106    Network Information:   Client Address:  ::ffff:192.168.12.21   Client Port:  58742    Additional Information:   Ticket Options:  0x40810000   Ticket Encryption Type: 0x12   Failure Code:  0x0   Transited Services: -    This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.    This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.    Ticket options, encryption types, and failure codes are defined in RFC 4120.\t4767";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationNtDomain", "E8SEC.LAB", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria@E8SEC.LAB".toLowerCase(), out.get("destinationUserName").get(0));
   assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
   assertEquals("sourcePort", 58742, out.get("sourcePort").get(0));
   assertEquals("status", "0x0", out.get("status").get(0));
   assertEquals("destinationLogonGUID", "{AD7B3FA1-ABFF-23FE-F052-73002A41531D}", out.get("destinationLogonGUID").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("destinationServiceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1106", out.get("destinationServiceSecurityID").get(0));
   assertEquals("destinationServiceName", "someuserorservice", out.get("destinationServiceName").get(0));
   assertEquals("snareCategoryString", "Kerberos Service Ticket Operations", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "4767", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10905", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4769", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC.LAB\\jyria@E8SEC.LAB", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268268000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A Kerberos service ticket was requested.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("ticketEncryptionType", "0x12", out.get("ticketEncryptionType").get(0));
   assertEquals("ticketOptions", "0x40810000", out.get("ticketOptions").get(0));
   assertEquals("transitedService", "-", out.get("transitedService").get(0));
   }

   @Test
   public void testSecurity4672SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t9676\tThu Apr 24 12:43:48 2014\t4672\tMicrosoft-Windows-Security-Auditing\tE8SEC\\W2K8R2-AD$\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSpecial Logon\t\tSpecial privileges assigned to new logon.    Subject:   Security ID:  S-1-5-18   Account Name:  W2K8R2-AD$   Account Domain:  E8SEC   Logon ID:  0x285d611    Privileges:  SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeDebugPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeEnableDelegationPrivilege\t9675";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationUserName", "W2K8R2-AD$".toLowerCase(), out.get("destinationUserName").get(0));
   assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
   assertEquals("targetLoginID", "0x285d611", out.get("targetLoginID").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("privileges", "SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeDebugPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeEnableDelegationPrivilege", out.get("privileges").get(0));
   assertEquals("destinationSecurityID", "S-1-5-18", out.get("destinationSecurityID").get(0));
   assertEquals("snareCategoryString", "Special Logon", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "9675", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "9676", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4672", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\W2K8R2-AD$", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398343428000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "Special privileges assigned to new logon.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   }

   @Test
   public void testSecurity4776SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10902\tWed Apr 23 15:51:05 2014\t4776\tMicrosoft-Windows-Security-Auditing\tjyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tCredential Validation\t\tThe computer attempted to validate the credentials for an account.    Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0  Logon Account: jyria  Source Workstation: W2K8R2LAB  Error Code: 0x0\t10901";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("authenticationPackage", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("authenticationPackage").get(0));
   assertEquals("status", "0x0", out.get("status").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("snareCategoryString", "Credential Validation", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10901", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10902", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4776", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268265000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "The computer attempted to validate the credentials for an account.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("sourceNameOrIp", "W2K8R2LAB", out.get("sourceNameOrIp").get(0));
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   }
  
   @Test
   public void testSecurity4634SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10929\tWed Apr 23 15:51:31 2014\t4634\tMicrosoft-Windows-Security-Auditing\tE8SEC\\W2K8R2-AD$\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tLogoff\t\tAn account was logged off.    Subject:   Security ID:  S-1-5-18   Account Name:  W2K8R2-AD$   Account Domain:  E8SEC   Logon ID:  0x9ddae3    Logon Type:   3    This event is generated when a logon session is destroyed. It may be positively correlated with a logon event using the Logon ID value. Logon IDs are only unique between reboots on the same computer.\t10928";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("snareCategoryString", "Logoff", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10928", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10929", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4634", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\W2K8R2-AD$", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268291000L, out.get("startTime").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));

   }
   
   @Test
   public void testSecurity4647SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10810\tThu Apr 24 14:35:08 2014\t4647\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tLogoff\t\tUser initiated logoff:    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0x2b20e0e    This event is generated when a logoff is initiated. No further user-initiated activity can occur. This event can be interpreted as a logoff event.\t10809 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   assertEquals("destinationLogonID", "0x2b20e0e", out.get("destinationLogonID").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("destinationSecurityID").get(0));
   assertEquals("snareCategoryString", "Logoff", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10809", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10810", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4647", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398350108000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "User initiated logoff", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));

   }
   @Test
   public void testSecurity4648SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10586\tThu Apr 24 14:31:58 2014\t4648\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tLogon\t\tA logon was attempted using explicit credentials.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-500   Account Name:  Administrator   Account Domain:  E8SEC   Logon ID:  0x9b4e1f   Logon GUID:  {95B1BAFF-60FC-A163-133B-5386CB2C54F5}    Account Whose Credentials Were Used:   Account Name:  jyria   Account Domain:  E8SEC   Logon GUID:  {00000000-0000-0000-0000-000000000000}    Target Server:   Target Server Name: w2k8r2-AD.e8sec.lab   Additional Information: w2k8r2-AD.e8sec.lab    Process Information:   Process ID:  0x1e8   Process Name:  C:\\Windows\\System32\\lsass.exe    Network Information:   Network Address: -   Port:   -    This event is generated when a process attempts to log on an account by explicitly specifying that accountӳ credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command.\t10585 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
   assertEquals("destinationNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("destinationNameOrIp").get(0));
   assertEquals("sourceProcessId", "0x1e8", out.get("sourceProcessId").get(0));
   assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("snareCategoryString", "Logon", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10585", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10586", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4648", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398349918000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A logon was attempted using explicit credentials.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
   assertEquals("sourceUserName", "Administrator".toLowerCase(), out.get("sourceUserName").get(0));
   assertEquals("sourceLogonGUID", "{95B1BAFF-60FC-A163-133B-5386CB2C54F5}", out.get("sourceLogonGUID").get(0));
   assertEquals("sourceLogonID", "0x9b4e1f", out.get("sourceLogonID").get(0));
   assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-500", out.get("sourceSecurityID").get(0));

   }



   @Test
   public void testSecurity4742SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t7957\tFri Apr 25 08:01:38 2014\t4742\tMicrosoft-Windows-Security-Auditing\tE8SEC\\WINDHCPCLIENT$\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tComputer Account Management\t\tA computer account was changed.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0x2ee7a7b    Computer Account That Was Changed:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1116   Account Name:  WINDHCPCLIENT$   Account Domain:  E8SEC    Changed Attributes:   SAM Account Name: -   Display Name:  -   User Principal Name: -   Home Directory:  -   Home Drive:  -   Script Path:  -   Profile Path:  -   User Workstations: -   Password Last Set: 4/25/2014 8:01:38 AM   Account Expires:  -   Primary Group ID: -   AllowedToDelegateTo: -   Old UAC Value:  -   New UAC Value:  -   User Account Control: -   User Parameters: -   SID History:  -   Logon Hours:  -   DNS Host Name:  -   Service Principal Names: -    Additional Information:   Privileges:  -\t7956 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("accountExpires", "-", out.get("accountExpires").get(0));
   assertEquals("allowedToDelegateTo", "-", out.get("allowedToDelegateTo").get(0));
   assertEquals("destinationUserName", "WINDHCPCLIENT$".toLowerCase(), out.get("destinationUserName").get(0));
   assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
   assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1116", out.get("destinationSecurityID").get(0));
   assertEquals("adDnsHostName", "-", out.get("adDnsHostName").get(0));
   assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
   assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
   assertEquals("logonHours", "-", out.get("logonHours").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("newUacValue", "-", out.get("newUacValue").get(0));
   assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
   assertEquals("adPasswdLastSet", "4/25/2014 8:01:38 AM", out.get("adPasswdLastSet").get(0));
   assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
   assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
   assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
   assertEquals("adServerPrincipalNames", "-", out.get("adServerPrincipalNames").get(0));
   assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
   assertEquals("snareCategoryString", "Computer Account Management", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "7956", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "7957", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4742", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\WINDHCPCLIENT$", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398412898000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A computer account was changed.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
   assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
   assertEquals("sourceLogonID", "0x2ee7a7b", out.get("sourceLogonID").get(0));
   assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
   assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
   assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
   assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
   assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
   }
   @Test
   public void testSecurity4738SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t1085\tMon Apr 28 14:32:51 2014\t4738\tMicrosoft-Windows-Security-Auditing\tE8SEC\\officehours\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tUser Account Management\t\tA user account was changed.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-500   Account Name:  Administrator   Account Domain:  E8SEC   Logon ID:  0x9b4e1f    Target Account:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1118   Account Name:  officehours   Account Domain:  E8SEC    Changed Attributes:   SAM Account Name: -   Display Name:  -   User Principal Name: -   Home Directory:  -   Home Drive:  -   Script Path:  -   Profile Path:  -   User Workstations: -   Password Last Set: 4/28/2014 2:32:51 PM   Account Expires:  -   Primary Group ID: -   AllowedToDelegateTo: -   Old UAC Value:  -   New UAC Value:  -   User Account Control: -   User Parameters: -   SID History:  -   Logon Hours:  -    Additional Information:   Privileges:  -\t1084 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("destinationUserName", "officehours", out.get("destinationUserName").get(0));
   assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
   assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1118", out.get("destinationSecurityID").get(0));
   assertEquals("adDisplayName", "-", out.get("adDisplayName").get(0));
   assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
   assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
   assertEquals("logonHours", "-", out.get("logonHours").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("newUacValue", "-", out.get("newUacValue").get(0));
   assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
   assertEquals("adPasswdLastSet", "4/28/2014 2:32:51 PM", out.get("adPasswdLastSet").get(0));
   assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
   assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
   assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
   assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
   assertEquals("snareCategoryString", "User Account Management", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "1084", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "1085", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4738", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\officehours", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398695571000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A user account was changed.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
   assertEquals("sourceUserName", "Administrator".toLowerCase(), out.get("sourceUserName").get(0));
   assertEquals("sourceLogonID", "0x9b4e1f", out.get("sourceLogonID").get(0));
   assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-500", out.get("sourceSecurityID").get(0));
   assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
   assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
   assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
   assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
   }
   @Test
   public void testSecurity4624SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10909\tWed Apr 23 15:51:08 2014\t4624\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tLogon\t\tAn account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    New Logon:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0x9db891   Logon GUID:  {256434D9-27A8-8DD4-DC5D-E6466FFA6773}    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name:    Source Network Address: 192.168.12.21   Source Port:  58743    Detailed Authentication Information:   Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).    The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.    The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The authentication information fields provide detailed information about this specific logon request.   - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.\t10908 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
   assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   assertEquals("destinationLogonGUID", "{256434D9-27A8-8DD4-DC5D-E6466FFA6773}", out.get("destinationLogonGUID").get(0));
   assertEquals("destinationLogonID", "0x9db891", out.get("destinationLogonID").get(0));
   assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("destinationSecurityID").get(0));
   assertEquals("keyLength", 0, out.get("keyLength").get(0));
   assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("packageName", "-", out.get("packageName").get(0));
   assertEquals("snareCategoryString", "Logon", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10908", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10909", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4624", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398268268000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "An account was successfully logged on.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
//   assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
//   assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
   assertEquals("sourceAddress", "192.168.12.21", out.get("sourceAddress").get(0));
   assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
   assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
   assertEquals("sourcePort", 58743, out.get("sourcePort").get(0));
   assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
   assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
   assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
   assertEquals("transitedService", "-", out.get("transitedService").get(0));
   } 
   @Test
   public void testSecurity4625FailureAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t10496\tThu Apr 24 14:25:16 2014\t4625\tMicrosoft-Windows-Security-Auditing\te8sec\\jyria\tN/A\tFailure Audit\tw2k8r2-AD.e8sec.lab\tLogon\t\tAn account failed to log on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    Account For Which Logon Failed:   Security ID:  S-1-0-0   Account Name:  jyria   Account Domain:  e8sec    Failure Information:   Failure Reason:  Unknown user name or bad password.   Status:   0xc000006d   Sub Status:  0xc000006a    Process Information:   Caller Process ID: 0x0   Caller Process Name: -    Network Information:   Workstation Name: W2K8R2LAB   Source Network Address: -   Source Port:  -    Detailed Authentication Information:   Logon Process:  NtLmSsp    Authentication Package: NTLM   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon request fails. It is generated on the computer where access was attempted.    The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network).    The Process Information fields indicate which account and process on the system requested the logon.    The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The authentication information fields provide detailed information about this specific logon request.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.\t10495 ";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("authenticationPackage", "NTLM", out.get("authenticationPackage").get(0));
   assertEquals("destinationNtDomain", "e8sec", out.get("destinationNtDomain").get(0));
   assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));
   assertEquals("destinationSecurityID", "S-1-0-0", out.get("destinationSecurityID").get(0));
   assertEquals("keyLength", 0, out.get("keyLength").get(0));
   assertEquals("logonProcess", "NtLmSsp", out.get("logonProcess").get(0));
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("packageName", "-", out.get("packageName").get(0));
   assertEquals("snareCategoryString", "Logon", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "10495", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "10496", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4625", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "e8sec\\jyria", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398349516000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "An account failed to log on.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
//   assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
//   assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
   assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
   assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
   assertEquals("sourceNameOrIp", "W2K8R2LAB", out.get("sourceNameOrIp").get(0));
   assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
   assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
   assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
   assertEquals("status", "0xc000006d", out.get("status").get(0));
   assertEquals("subStatus", "0xc000006a", out.get("subStatus").get(0));
   assertEquals("transitedService", "-", out.get("transitedService").get(0));
   }

   //
   @Test
   public void testSecurity4625FailureAuditBis() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t1766999\tThu Feb 18 10:51:19 2016\t4625\tMicrosoft-Windows-Security-Auditing\t\\fax\tN/A\tFailure Audit\tw2k8r2-AD.e8sec.lab\tLogon\t\tAn account failed to log on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    Account For Which Logon Failed:   Security ID:  S-1-0-0   Account Name:  fax   Account Domain:      Failure Information:   Failure Reason:  Unknown user name or bad password.   Status:   0xc000006d   Sub Status:  0xc0000064    Process Information:   Caller Process ID: 0x0   Caller Process Name: -    Network Information:   Workstation Name: Windows7   Source Network Address: -   Source Port:  -    Detailed Authentication Information:   Logon Process:  NtLmSsp    Authentication Package: NTLM   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon request fails. It is generated on the computer where access was attempted.    The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network).    The Process Information fields indicate which account and process on the system requested the logon.    The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The authentication information fields provide detailed information about this specific logon request.   - Transited services indicate which intermediate services have participated in this logon request.   - Package name indicates which sub-protocol was used among the NTLM protocols.   - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.\t1766998";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
   }

   
   @Test
   public void testSecurity4673SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t4992\tFri Apr 25 03:20:05 2014\t4673\tMicrosoft-Windows-Security-Auditing\tE8SEC\\W2K8R2-AD$\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSensitive Privilege Use\t\tA privileged service was called.    Subject:   Security ID:  S-1-5-18   Account Name:  W2K8R2-AD$   Account Domain:  E8SEC   Logon ID:  0x3e7    Service:   Server: NT Local Security Authority / Authentication Service   Service Name: LsaRegisterLogonProcess()    Process:   Process ID: 0x1e8   Process Name: C:\\Windows\\System32\\lsass.exe    Service Request Information:   Privileges:  SeTcbPrivilege\t4991";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
   assertEquals("privileges", "SeTcbPrivilege", out.get("privileges").get(0));
   assertEquals("snareCategoryString", "Sensitive Privilege Use", out.get("snareCategoryString").get(0));
   assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
   assertEquals("snareCrc", "4991", out.get("snareCrc").get(0));
   assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
   assertEquals("snareEventCounter", "4992", out.get("snareEventCounter").get(0));
   assertEquals("snareEventId", "4673", out.get("snareEventId").get(0));
   assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
   assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
   assertEquals("snareEventUser", "E8SEC\\W2K8R2-AD$", out.get("snareEventUser").get(0));
   assertEquals("startTime", 1398396005000L, out.get("startTime").get(0));
   assertEquals("snareMsgDesc", "A privileged service was called.", out.get("snareMsgDesc").get(0));
   assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
   assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
   assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
   assertEquals("sourceUserName", "W2K8R2-AD$".toLowerCase(), out.get("sourceUserName").get(0));
   assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
   assertEquals("sourceProcessID", "0x1e8", out.get("sourceProcessID").get(0));
   assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
   assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
   assertEquals("destinationProcessName", "LsaRegisterLogonProcess()", out.get("destinationProcessName").get(0));
   assertEquals("destinationServiceServer", "NT Local Security Authority / Authentication Service", out.get("destinationServiceServer").get(0));
   }
   
  @Test
  public void testSecurity4674SuccessAudit() throws FileNotFoundException {
    String line = "MSWinEventLog\t0\tSecurity\t10214\tTue Apr 29 09:16:30 2014\t4674\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSensitive Privilege Use\t\tAn operation was attempted on a privileged object.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0x2ee7a7b    Object:   Object Server: Security   Object Type: File   Object Name: C:\\Users\\Public\\Music\\Sample Music   Object Handle: 0x160    Process Information:   Process ID: 0x764   Process Name: C:\\Windows\\System32\\dllhost.exe    Requested Operation:   Desired Access: READ_CONTROL      WRITE_DAC      WRITE_OWNER      ACCESS_SYS_SEC         Privileges:  SeSecurityPrivilege     SeTakeOwnershipPrivilege\t10213 ";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record out = this.outCommand.getRecord(0);
    assertEquals("desiredAccess", "READ_CONTROL      WRITE_DAC      WRITE_OWNER      ACCESS_SYS_SEC", out.get("desiredAccess").get(0));
    assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
    assertEquals("destinationObjectHandle", "0x160", out.get("destinationObjectHandle").get(0));
    assertEquals("destinationObjectName", "C:\\Users\\Public\\Music\\Sample Music", out.get("destinationObjectName").get(0));
    assertEquals("destinationObjectServer", "Security", out.get("destinationObjectServer").get(0));
    assertEquals("destinationObjectType", "File", out.get("destinationObjectType").get(0));
    assertEquals("privileges", "SeSecurityPrivilege     SeTakeOwnershipPrivilege", out.get("privileges").get(0));
    assertEquals("snareCategoryString", "Sensitive Privilege Use", out.get("snareCategoryString").get(0));
    assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
    assertEquals("snareCrc", "10213", out.get("snareCrc").get(0));
    assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
    assertEquals("snareEventCounter", "10214", out.get("snareEventCounter").get(0));
    assertEquals("snareEventId", "4674", out.get("snareEventId").get(0));
    assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
    assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
    assertEquals("snareEventUser", "E8SEC\\jyria", out.get("snareEventUser").get(0));
    assertEquals("startTime", 1398762990000L, out.get("startTime").get(0));
    assertEquals("snareMsgDesc", "An operation was attempted on a privileged object.", out.get("snareMsgDesc").get(0));
    assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
    assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
    assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
    assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
    assertEquals("sourceLogonID", "0x2ee7a7b", out.get("sourceLogonID").get(0));
    assertEquals("sourceProcessID", "0x764", out.get("sourceProcessID").get(0));
    assertEquals("sourceProcessName", "C:\\Windows\\System32\\dllhost.exe", out.get("sourceProcessName").get(0));
    assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
  }

   @Test
   public void testSecurity4723SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 5 30 6 2012 \t4723\tMicrosoft-Windows-Security-Auditing\tCarl.Mundy\tUser\tSuccess Audit\tTPAP1CUADCV01.vdsi.ent.example.com\tUser Account Management\t\tAn attempt was made to change an account's password.    Subject:   Security ID:  S-1-5-21-1757981266-413027322-725345543-10177   Account Name:  Carl.Mundy   Account Domain:  VDSI   Logon ID:  0x3b4aa08f    Target Account:   Security ID:  S-1-5-21-1757981266-413027322-725345543-95563   Account Name:  Z515541   Account Domain:  VDSI    Additional Information:   Privileges  -\t229492194";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "An attempt was made to change an account's password.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4723-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationNtDomain", "VDSI", out.get("destinationNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1757981266-413027322-725345543-95563", out.get("destinationSecurityID").get(0));
      assertEquals("destinationUserName", "z515541", out.get("destinationUserName").get(0));
      assertEquals("deviceDnsDomain", "vdsi.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cuadcv01", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1CUADCV01.vdsi.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "carl.mundy", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "User Account Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1CUADCV01.vdsi.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "229492194", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4723", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "Carl.Mundy", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "User", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x3b4aa08f", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "VDSI", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1757981266-413027322-725345543-10177", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "carl.mundy", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342762206000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4723FailureAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 11 48 31 2012 \t4723\tMicrosoft-Windows-Security-Auditing\tV248906\tUser\tFailure Audit\tTPAP1CUADC105.us1.ent.example.com\tUser Account Management\t\tAn attempt was made to change an account's password.    Subject:   Security ID:  S-1-5-21-1801674531-2049760794-725345543-1097969   Account Name:  V248906   Account Domain:  US1   Logon ID:  0x5e425880    Target Account:   Security ID:  S-1-5-21-1801674531-2049760794-725345543-1097969   Account Name:  V248906   Account Domain:  US1    Additional Information:   Privileges  -\t1652231798";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "An attempt was made to change an account's password.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4723-Failure Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationNtDomain", "US1", out.get("destinationNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1801674531-2049760794-725345543-1097969", out.get("destinationSecurityID").get(0));
      assertEquals("destinationUserName", "v248906", out.get("destinationUserName").get(0));
      assertEquals("deviceDnsDomain", "us1.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cuadc105", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1CUADC105.us1.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "v248906", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "User Account Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1CUADC105.us1.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "1652231798", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4723", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "V248906", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "User", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x5e425880", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "US1", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1801674531-2049760794-725345543-1097969", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "v248906", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342784911000L, out.get("startTime").get(0));

   }
   @Test
   public void testSecurity4724SuccessAudit() throws FileNotFoundException {
   String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 3 26 29 2012 \t4724\tMicrosoft-Windows-Security-Auditing\tsvc-vista-wksadd\tUser\tSuccess Audit\tTPAP1CUADC105.us1.ent.example.com\tUser Account Management\t\tAn attempt was made to reset an account's password.    Subject:   Security ID:  S-1-5-21-1801674531-2049760794-725345543-476317   Account Name:  svc-vista-wksadd   Account Domain:  US1   Logon ID:  0x504edf73    Target Account:   Security ID:  S-1-5-21-1801674531-2049760794-725345543-1212289   Account Name:  TUSFL031DKVT01F$   Account Domain:  US1\t1650262152";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "An attempt was made to reset an account's password.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4724-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationNtDomain", "US1", out.get("destinationNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1801674531-2049760794-725345543-1212289", out.get("destinationSecurityID").get(0));
      assertEquals("destinationUserName", "tusfl031dkvt01f$", out.get("destinationUserName").get(0));
      assertEquals("deviceDnsDomain", "us1.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cuadc105", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1CUADC105.us1.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "svc-vista-wksadd", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "User Account Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1CUADC105.us1.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "1650262152", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4724", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "svc-vista-wksadd", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "User", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x504edf73", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "US1", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1801674531-2049760794-725345543-476317", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "svc-vista-wksadd", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342754789000L, out.get("startTime").get(0));
}
   @Test
   public void testSecurity4724FailureAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 3 26 29 2012 \t4724\tMicrosoft-Windows-Security-Auditing\tsvc-vista-wksadd\tUser\tFailure Audit\tTPAP1CUADC105.us1.ent.example.com\tUser Account Management\t\tAn attempt was made to reset an account's password.    Subject:   Security ID:  S-1-5-21-1801674531-2049760794-725345543-476317   Account Name:  svc-vista-wksadd   Account Domain:  US1   Logon ID:  0x504edf73    Target Account:   Security ID:  S-1-5-21-1801674531-2049760794-725345543-1212289   Account Name:  TUSFL031DKVT01F$   Account Domain:  US1\t1650262152";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "An attempt was made to reset an account's password.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4724-Failure Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationNtDomain", "US1", out.get("destinationNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1801674531-2049760794-725345543-1212289", out.get("destinationSecurityID").get(0));
      assertEquals("destinationUserName", "tusfl031dkvt01f$", out.get("destinationUserName").get(0));
      assertEquals("deviceDnsDomain", "us1.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cuadc105", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1CUADC105.us1.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "svc-vista-wksadd", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "User Account Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1CUADC105.us1.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "1650262152", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4724", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "svc-vista-wksadd", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "User", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x504edf73", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "US1", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1801674531-2049760794-725345543-476317", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "svc-vista-wksadd", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342754789000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4663SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t1279709\tWed Feb 17 15:21:07 2016\t4663\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tFile System\t\tAn attempt was made to access an object.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0xeff5adc    Object:   Object Server: Security   Object Type: File   Object Name: C:\\Windows\\servicing   Handle ID: 0xa48    Process Information:   Process ID: 0x394   Process Name: C:\\Windows\\explorer.exe    Access Request Information:   Accesses: READ_CONTROL         Access Mask: 0x20000\t1279708 ";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "An attempt was made to access an object.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4663-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("desiredAccess", "READ_CONTROL", out.get("desiredAccess").get(0));
      assertEquals("destinationObjectHandle", "0xa48", out.get("destinationObjectHandle").get(0));
      assertEquals("destinationObjectName", "C:\\Windows\\servicing", out.get("destinationObjectName").get(0));
      assertEquals("destinationObjectServer", "Security", out.get("destinationObjectServer").get(0));
      assertEquals("destinationObjectType", "File", out.get("destinationObjectType").get(0));
      assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "e8sec\\jyria", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "File System", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "1279708", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "1279709", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4663", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "E8SEC\\jyria", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0xeff5adc", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
      assertEquals("sourceProcessID", "0x394", out.get("sourceProcessID").get(0));
      assertEquals("sourceProcessName", "C:\\Windows\\explorer.exe", out.get("sourceProcessName").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1455722467000L, out.get("startTime").get(0));
   }

   //
   @Test
   public void testSecurity4757FailureAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t53016\tThu May 19 17:30:18 2016\t4757\tMicrosoft-Windows-Security-Auditing\tE8SEC\\__new_sec_universal_grp\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSecurity Group Management\t\tA member was removed from a security-enabled universal group.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0x9a665    Member:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1123   Account Name:  CN=lindsey,CN=Users,DC=e8sec,DC=lab    Group:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-47210   Group Name:  __new_sec_universal_grp   Group Domain:  E8SEC    Additional Information:   Privileges:  -\t53015 ";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());
      assertEquals("cefEventName", "A member was removed from a security-enabled universal group.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4757-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationGroup", "__new_sec_universal_grp", out.get("destinationGroup").get(0));
      assertEquals("destinationGroupSecurityID", "S-1-5-21-1908711140-209524994-2501478566-47210", out.get("destinationGroupSecurityID").get(0));
      assertEquals("destinationLdapUser", "CN=lindsey,CN=Users,DC=e8sec,DC=lab", out.get("destinationLdapUser").get(0));
      assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
      assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1123", out.get("destinationSecurityID").get(0));
      assertEquals("destinationUserName", "lindsey", out.get("destinationUserName").get(0));
      assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "e8sec\\__new_sec_universal_grp", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "53015", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "53016", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4757", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "E8SEC\\__new_sec_universal_grp", out.get("snareEventUser").get(0));
      assertEquals("snareMsgDesc", "A member was removed from a security-enabled universal group.", out.get("snareMsgDesc").get(0));
      assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x9a665", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1463679018000L, out.get("startTime").get(0));
   }

      @Test
   public void testSecurity4661FailureAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 5 32 33 2012 \t4661\tMicrosoft-Windows-Security-Auditing\tANONYMOUS LOGON\tWell Known Group\tFailure Audit\tTPAP1CUADCE02.ent.example.com\tSAM\t\tA handle to an object was requested.    Subject :   Security ID:  S-1-5-7   Account Name:  ANONYMOUS LOGON   Account Domain:  NT AUTHORITY   Logon ID:  0x24493567    Object:   Object Server: Security Account Manager   Object Type: SAM_SERVER   Object Name: CN=Server,CN=System,DC=ent,DC=example,DC=com   Handle ID: 0x0    Process Information:   Process ID: 0x9000a000d002d   Process Name: {bf967aad-0de6-11d0-a285-00aa003049e2}      Access Request Information:   Transaction ID: {00000000-0000-0000-0000-000000000000}   Accesses: MAX_ALLOWED         Access Mask: 0x2d   Privileges Used for Access Check:    Properties: \u0200-   Restricted SID Count: 2949165\t208146305";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A handle to an object was requested.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4661-Failure Audit", out.get("cefSignatureId").get(0));
      assertEquals("desiredAccess", "MAX_ALLOWED", out.get("desiredAccess").get(0));
      assertEquals("destinationObjectHandle", "0x0", out.get("destinationObjectHandle").get(0));
      assertEquals("destinationObjectName", "CN=Server,CN=System,DC=ent,DC=example,DC=com", out.get("destinationObjectName").get(0));
      assertEquals("destinationObjectServer", "Security Account Manager", out.get("destinationObjectServer").get(0));
      assertEquals("destinationObjectType", "SAM_SERVER", out.get("destinationObjectType").get(0));
      assertEquals("deviceDnsDomain", "ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cuadce02", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1CUADCE02.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "anonymous logon", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "SAM", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1CUADCE02.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "208146305", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4661", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "ANONYMOUS LOGON", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "Well Known Group", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x24493567", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "NT AUTHORITY", out.get("sourceNtDomain").get(0));
      assertEquals("sourceProcessID", "0x9000a000d002d", out.get("sourceProcessID").get(0));
      assertEquals("sourceProcessName", "{bf967aad-0de6-11d0-a285-00aa003049e2}", out.get("sourceProcessName").get(0));
      assertEquals("sourceSecurityID", "S-1-5-7", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "anonymous logon", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342762353000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4661SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t1302408\tWed Feb 17 16:06:21 2016\t4661\tMicrosoft-Windows-Security-Auditing\tE8SEC\\W2K8R2-AD$\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSAM\t\tA handle to an object was requested.    Subject :   Security ID:  S-1-5-18   Account Name:  W2K8R2-AD$   Account Domain:  E8SEC   Logon ID:  0x3e7    Object:   Object Server: Security Account Manager   Object Type: SAM_DOMAIN   Object Name: DC=e8sec,DC=lab   Handle ID: 0x43ac670    Process Information:   Process ID: 0x1f0   Process Name: C:\\Windows\\System32\\lsass.exe    Access Request Information:   Transaction ID: {00000000-0000-0000-0000-000000000000}   Accesses: DELETE      READ_CONTROL      WRITE_DAC      WRITE_OWNER      ReadPasswordParameters      ReadOtherParameters      WriteOtherParameters      CreateUser      CreateGlobalGroup      CreateLocalGroup      GetLocalGroupMembership      ListAccounts         Access Reasons:  -   Access Mask: 0xf01fd   Privileges Used for Access Check: -   Properties: ---   {19195a5a-6da0-11d0-afd3-00c04fd930c9}  DELETE  READ_CONTROL  WRITE_DAC  WRITE_OWNER  ReadPasswordParameters  ReadOtherParameters  WriteOtherParameters  CreateUser  CreateGlobalGroup  CreateLocalGroup  GetLocalGroupMembership  ListAccounts    {c7407360-20bf-11d0-a768-00aa006e0529}     {bf9679a4-0de6-11d0-a285-00aa003049e2}     {bf9679a5-0de6-11d0-a285-00aa003049e2}     {bf9679a6-0de6-11d0-a285-00aa003049e2}     {bf9679bb-0de6-11d0-a285-00aa003049e2}     {bf9679c2-0de6-11d0-a285-00aa003049e2}     {bf9679c3-0de6-11d0-a285-00aa003049e2}     {bf967a09-0de6-11d0-a285-00aa003049e2}     {bf967a0b-0de6-11d0-a285-00aa003049e2}    {b8119fd0-04f6-4762-ab7a-4986c76b3f9a}     {bf967a34-0de6-11d0-a285-00aa003049e2}     {bf967a33-0de6-11d0-a285-00aa003049e2}     {bf9679c5-0de6-11d0-a285-00aa003049e2}     {bf967a61-0de6-11d0-a285-00aa003049e2}     {bf967977-0de6-11d0-a285-00aa003049e2}     {bf96795e-0de6-11d0-a285-00aa003049e2}     {bf9679ea-0de6-11d0-a285-00aa003049e2}    {ab721a52-1e2f-11d0-9819-00aa0040529b}     Restricted S";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A handle to an object was requested.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4661-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("desiredAccess", "DELETE      READ_CONTROL      WRITE_DAC      WRITE_OWNER      ReadPasswordParameters      ReadOtherParameters      WriteOtherParameters      CreateUser      CreateGlobalGroup      CreateLocalGroup      GetLocalGroupMembership      ListAccounts", out.get("desiredAccess").get(0));
      assertEquals("destinationObjectHandle", "0x43ac670", out.get("destinationObjectHandle").get(0));
      assertEquals("destinationObjectName", "DC=e8sec,DC=lab", out.get("destinationObjectName").get(0));
      assertEquals("destinationObjectServer", "Security Account Manager", out.get("destinationObjectServer").get(0));
      assertEquals("destinationObjectType", "SAM_DOMAIN", out.get("destinationObjectType").get(0));
      assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "e8sec\\w2k8r2-ad$", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "SAM", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "1302408", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4661", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "E8SEC\\W2K8R2-AD$", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
      assertEquals("sourceProcessID", "0x1f0", out.get("sourceProcessID").get(0));
      assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
      assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "w2k8r2-ad$", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1455725181000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4656FailureAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t4\tSecurity\t2142679\tThu Feb 18 10:52:53 2016\t4656\tMicrosoft-Windows-Security-Auditing\tE8SEC\\jyria2\tN/A\tFailure Audit\tW2K8R2-SRC.e8sec.lab\tOther Object Access Events\t\tA handle to an object was requested.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1120   Account Name:  jyria2   Account Domain:  E8SEC   Logon ID:  0x58e8a467    Object:   Object Server:  PlugPlayManager   Object Type:  Security   Object Name:  PlugPlaySecurityObject   Handle ID:  0x0    Process Information:   Process ID:  0x268   Process Name:  C:\\Windows\\System32\\svchost.exe    Access Request Information:   Transaction ID:  {00000000-0000-0000-0000-000000000000}   Accesses:  Unknown specific access (bit 1)         Access Reasons:  -   Access Mask:  0x2   Privileges Used for Access Check: -   Restricted SID Count: 0\t2142678 ";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A handle to an object was requested.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4656-Failure Audit", out.get("cefSignatureId").get(0));
      assertEquals("desiredAccess", "Unknown specific access (bit 1)", out.get("desiredAccess").get(0));
      assertEquals("destinationObjectHandle", "0x0", out.get("destinationObjectHandle").get(0));
      assertEquals("destinationObjectName", "PlugPlaySecurityObject", out.get("destinationObjectName").get(0));
      assertEquals("destinationObjectServer", "PlugPlayManager", out.get("destinationObjectServer").get(0));
      assertEquals("destinationObjectType", "Security", out.get("destinationObjectType").get(0));
      assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "w2k8r2-src", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "W2K8R2-SRC.e8sec.lab", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "e8sec\\jyria2", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "Other Object Access Events", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "W2K8R2-SRC.e8sec.lab", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "2142678", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "4", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "2142679", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4656", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "E8SEC\\jyria2", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x58e8a467", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
      assertEquals("sourceProcessID", "0x268", out.get("sourceProcessID").get(0));
      assertEquals("sourceProcessName", "C:\\Windows\\System32\\svchost.exe", out.get("sourceProcessName").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1120", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "jyria2", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1455792773000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4656SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t4\tSecurity\t2142763\tThu Feb 18 10:52:54 2016\t4656\tMicrosoft-Windows-Security-Auditing\tE8SEC\\W2K8R2-SRC$\tN/A\tSuccess Audit\tW2K8R2-SRC.e8sec.lab\tOther Object Access Events\t\tA handle to an object was requested.    Subject:   Security ID:  S-1-5-18   Account Name:  W2K8R2-SRC$   Account Domain:  E8SEC   Logon ID:  0x3e7    Object:   Object Server:  PlugPlayManager   Object Type:  Security   Object Name:  PlugPlaySecurityObject   Handle ID:  0x0    Process Information:   Process ID:  0x268   Process Name:  C:\\Windows\\System32\\svchost.exe    Access Request Information:   Transaction ID:  {00000000-0000-0000-0000-000000000000}   Accesses:  Unknown specific access (bit 1)         Access Reasons:  -   Access Mask:  0x2   Privileges Used for Access Check: -   Restricted SID Count: 0\t2142762 ";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A handle to an object was requested.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4656-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("desiredAccess", "Unknown specific access (bit 1)", out.get("desiredAccess").get(0));
      assertEquals("destinationObjectHandle", "0x0", out.get("destinationObjectHandle").get(0));
      assertEquals("destinationObjectName", "PlugPlaySecurityObject", out.get("destinationObjectName").get(0));
      assertEquals("destinationObjectServer", "PlugPlayManager", out.get("destinationObjectServer").get(0));
      assertEquals("destinationObjectType", "Security", out.get("destinationObjectType").get(0));
      assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "w2k8r2-src", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "W2K8R2-SRC.e8sec.lab", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "e8sec\\w2k8r2-src$", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "Other Object Access Events", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "W2K8R2-SRC.e8sec.lab", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "2142762", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "4", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "2142763", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4656", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "E8SEC\\W2K8R2-SRC$", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
      assertEquals("sourceProcessID", "0x268", out.get("sourceProcessID").get(0));
      assertEquals("sourceProcessName", "C:\\Windows\\System32\\svchost.exe", out.get("sourceProcessName").get(0));
      assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "w2k8r2-src$", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1455792774000L, out.get("startTime").get(0));

   }

   //Mar 11 01:19:50 w2k8r2-AD.e8sec.lab MSWinEventLog\t0\tSecurity\t768329\tFri Mar 11 01:19:50 2016\t5157\tMicrosoft-Windows-Security-Auditing\tN/A\tN/A\tFailure Audit\tw2k8r2-AD.e8sec.lab\tFiltering Platform Connection\t\tThe Windows Filtering Platform has blocked a connection.    Application Information:   Process ID:  3576   Application Name: \\device\\harddiskvolume1\\program files (x86)\\winscp\\winscp.exe    Network Information:   Direction:  Outbound   Source Address:  192.168.12.18   Source Port:  51243   Destination Address: 172.16.1.252   Destination Port:  22   Protocol:  6    Filter Information:   Filter Run-Time ID: 68291   Layer Name:  Connect   Layer Run-Time ID: 48\t768328
   @Test
   public void testSecurity5157FailureAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 4 0 19 2012 \t5157\tMicrosoft-Windows-Security-Auditing\t\tUnknown\tFailure Audit\tTPAP1CUADCE02.ent.example.com\tFiltering Platform Connection\t\tThe Windows Filtering Platform has blocked a connection.    Application Information:   Process ID:  688   Application Name: deviceharddiskvolume2windowssystem32lsass.exe    Network Information:   Direction:  Outbound   Source Address:  138.83.127.112   Source Port:  53773   Destination Address: 10.170.26.127   Destination Port:  135   Protocol:  6    Filter Information:   Filter Run-Time ID: 0   Layer Name:  Connect   Layer Run-Time ID: 48\t208135556";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

       //prio decreased for this one
      assertEquals(true, out.get("logSourceType").get(0).equals("UnMatched-IAMMef"));

   }


   //
   @Test
   public void testSecurity4727SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t1280162\tWed Feb 17 15:26:49 2016\t4727\tMicrosoft-Windows-Security-Auditing\tE8SEC\\new_global_security_grp\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSecurity Group Management\t\tA security-enabled global group was created.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0xeff5912    New Group:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1136   Group Name:  new_global_security_grp   Group Domain:  E8SEC    Attributes:   SAM Account Name: new_global_security_grp   SID History:  -    Additional Information:   Privileges:  -\t1280161";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

       assertEquals("cefEventName", "A security-enabled global group was created.", out.get("cefEventName").get(0));
       assertEquals("cefSignatureId", "Security-4727-Success Audit", out.get("cefSignatureId").get(0));
       assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
       assertEquals("destinationGroupSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1136", out.get("destinationGroupSecurityID").get(0));
       assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
       assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
       assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
       assertEquals("deviceUserName", "e8sec\\new_global_security_grp", out.get("deviceUserName").get(0));
       assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
       assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
       assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
       assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
       assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
       assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
       assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
       assertEquals("snareCrc", "1280161", out.get("snareCrc").get(0));
       assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
       assertEquals("snareEventCounter", "1280162", out.get("snareEventCounter").get(0));
       assertEquals("snareEventId", "4727", out.get("snareEventId").get(0));
       assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
       assertEquals("snareEventUser", "E8SEC\\new_global_security_grp", out.get("snareEventUser").get(0));
       assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
       assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
       assertEquals("sourceLogonID", "0xeff5912", out.get("sourceLogonID").get(0));
       assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
       assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
       assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
       assertEquals("startTime", 1455722809000L, out.get("startTime").get(0));   }

   @Test
   public void testSecurity4728SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 4 2 36 2012 \t4728\tMicrosoft-Windows-Security-Auditing\tCarl.Mundy\tUser\tSuccess Audit\tTPAP1CUADCV01.vdsi.ent.example.com\tSecurity Group Management\t\tA member was added to a security-enabled global group.    Subject:   Security ID:  S-1-5-21-1757981266-413027322-725345543-10177   Account Name:  Carl.Mundy   Account Domain:  VDSI   Logon ID:  0x3a341e9b    Member:   Security ID:  S-1-5-21-1757981266-413027322-725345543-43635   Account Name:  CN=Kommavarapu, MohanaMurali X,OU=Users,OU=Accounts,DC=vdsi,DC=ent,DC=example,DC=com    Group:   Security ID:  S-1-5-21-1757981266-413027322-725345543-113300   Group Name:  DS_2248_VSII_CR   Group Domain:  VDSI    Additional Information:   Privileges:  -\t229410089";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());



   }


   @Test
   public void testSecurity4729SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 12 29 22 2012 \t4729\tMicrosoft-Windows-Security-Auditing\tCarl.Mundy\tUser\tSuccess Audit\tTPAP1CUADCV01.vdsi.ent.example.com\tSecurity Group Management\t\tA member was removed from a security-enabled global group.    Subject:   Security ID:  S-1-5-21-1757981266-413027322-725345543-10177   Account Name:  Carl.Mundy   Account Domain:  VDSI   Logon ID:  0x4063c08b    Member:   Security ID:  S-1-5-21-1757981266-413027322-725345543-24241   Account Name:  CN=Venkatasubbu, Selvakumar X,OU=Users,OU=Accounts,DC=vdsi,DC=ent,DC=example,DC=com    Group:   Security ID:  S-1-5-21-1757981266-413027322-725345543-67988   Group Name:  PA-FW_CR_Proj-126667   Group Domain:  VDSI    Additional Information:   Privileges:  -\t229984944";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A member was removed from a security-enabled global group.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4729-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationGroup", "PA-FW_CR_Proj-126667", out.get("destinationGroup").get(0));
      assertEquals("destinationGroupSecurityID", "S-1-5-21-1757981266-413027322-725345543-67988", out.get("destinationGroupSecurityID").get(0));
      assertEquals("destinationNtDomain", "vdsi.ent.example.com", out.get("destinationNtDomain").get(0));
      assertEquals("destinationGroupNtDomain", "VDSI", out.get("destinationGroupNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1757981266-413027322-725345543-24241", out.get("destinationSecurityID").get(0));
      assertEquals("destinationLdapUser", "CN=Venkatasubbu, Selvakumar X,OU=Users,OU=Accounts,DC=vdsi,DC=ent,DC=example,DC=com", out.get("destinationLdapUser").get(0));
      assertEquals("destinationUserName", "venkatasubbu, selvakumar x", out.get("destinationUserName").get(0));
      assertEquals("deviceDnsDomain", "vdsi.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cuadcv01", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1CUADCV01.vdsi.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "carl.mundy", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1CUADCV01.vdsi.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "229984944", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4729", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "Carl.Mundy", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "User", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x4063c08b", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "VDSI", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-21-1757981266-413027322-725345543-10177", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "carl.mundy", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342787362000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4731SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t1280190\tWed Feb 17 15:28:17 2016\t4731\tMicrosoft-Windows-Security-Auditing\tE8SEC\\new_local_security_grp\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSecurity Group Management\t\tA security-enabled local group was created.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0xeff5912    New Group:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1137   Group Name:  new_local_security_grp   Group Domain:  E8SEC    Attributes:   SAM Account Name: new_local_security_grp   SID History:  -    Additional Information:   Privileges:  -\t1280189 ";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

       assertEquals("destinationGroup", "new_local_security_grp", out.get("destinationGroup").get(0));
       assertEquals("cefEventName", "A security-enabled local group was created.", out.get("cefEventName").get(0));
       assertEquals("cefSignatureId", "Security-4731-Success Audit", out.get("cefSignatureId").get(0));
       assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
       assertEquals("destinationGroupSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1137", out.get("destinationGroupSecurityID").get(0));
       assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
       assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
       assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
       assertEquals("deviceUserName", "e8sec\\new_local_security_grp", out.get("deviceUserName").get(0));
       assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
       assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
       assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
       assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
       assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
       assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
       assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
       assertEquals("snareCrc", "1280189", out.get("snareCrc").get(0));
       assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
       assertEquals("snareEventCounter", "1280190", out.get("snareEventCounter").get(0));
       assertEquals("snareEventId", "4731", out.get("snareEventId").get(0));
       assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
       assertEquals("snareEventUser", "E8SEC\\new_local_security_grp", out.get("snareEventUser").get(0));
       assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
       assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
       assertEquals("sourceLogonID", "0xeff5912", out.get("sourceLogonID").get(0));
       assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
       assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
       assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
       assertEquals("startTime", 1455722897000L, out.get("startTime").get(0));
   }


   @Test
   public void testSecurity4732SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 15 27 38 2012 \t4732\tMicrosoft-Windows-Security-Auditing\tSYSTEM\tWell Known Group\tSuccess Audit\ttpap1cvcehd01v.abh.ent.example.com\tSecurity Group Management\t\tA member was added to a security-enabled local group.    Subject:   Security ID:  S-1-5-18   Account Name:  TPAP1CVCEHD01V$   Account Domain:  ABH   Logon ID:  0x3e7    Member:   Security ID:  S-1-5-21-1482476501-484061587-839522115-5454   Account Name:  -    Group:   Security ID:  S-1-5-32-545   Group Name:  Users   Group Domain:  Builtin    Additional Information:   Privileges:  -\t5640";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A member was added to a security-enabled local group.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4732-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationGroup", "Users", out.get("destinationGroup").get(0));
      assertEquals("destinationGroupSecurityID", "S-1-5-32-545", out.get("destinationGroupSecurityID").get(0));
      assertEquals("destinationGroupNtDomain", "Builtin", out.get("destinationGroupNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1482476501-484061587-839522115-5454", out.get("destinationSecurityID").get(0));
      assertEquals("deviceDnsDomain", "abh.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1cvcehd01v", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "tpap1cvcehd01v.abh.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("deviceUserName", "system", out.get("deviceUserName").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "tpap1cvcehd01v.abh.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "5640", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4732", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareEventUser", "SYSTEM", out.get("snareEventUser").get(0));
      assertEquals("snareSIDType", "Well Known Group", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "ABH", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "tpap1cvcehd01v$", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342798058000L, out.get("startTime").get(0));
   }

   @Test
   public void testSecurity4733SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 9 6 52 2012 \t4733\tMicrosoft-Windows-Security-Auditing\t\tUnknown\tSuccess Audit\tTPAP1LZBECA02V.abh.ent.example.com\tSecurity Group Management\t\tA member was removed from a security-enabled local group.    Subject:   Security ID:  S-1-5-18   Account Name:  TPAP1LZBECA02V$   Account Domain:  ABH   Logon ID:  0x3e7    Member:   Security ID:  S-1-5-21-1757981266-413027322-725345543-85341   Account Name:  -    Group:   Security ID:  S-1-5-32-545   Group Name:  Users   Group Domain:  Builtin    Additional Information:   Privileges:  -\t856154";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

      assertEquals("cefEventName", "A member was removed from a security-enabled local group.", out.get("cefEventName").get(0));
      assertEquals("cefSignatureId", "Security-4733-Success Audit", out.get("cefSignatureId").get(0));
      assertEquals("destinationGroup", "Users", out.get("destinationGroup").get(0));
      assertEquals("destinationGroupSecurityID", "S-1-5-32-545", out.get("destinationGroupSecurityID").get(0));
      assertEquals("destinationGroupNtDomain", "Builtin", out.get("destinationGroupNtDomain").get(0));
      assertEquals("destinationSecurityID", "S-1-5-21-1757981266-413027322-725345543-85341", out.get("destinationSecurityID").get(0));
      assertEquals("deviceDnsDomain", "abh.ent.example.com", out.get("deviceDnsDomain").get(0));
      assertEquals("deviceHostName", "tpap1lzbeca02v", out.get("deviceHostName").get(0));
      assertEquals("deviceNameOrIp", "TPAP1LZBECA02V.abh.ent.example.com", out.get("deviceNameOrIp").get(0));
      assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
      assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
      assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
      assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
      assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
      assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
      assertEquals("snareComputerName", "TPAP1LZBECA02V.abh.ent.example.com", out.get("snareComputerName").get(0));
      assertEquals("snareCrc", "856154", out.get("snareCrc").get(0));
      assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
      assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
      assertEquals("snareEventId", "4733", out.get("snareEventId").get(0));
      assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
      assertEquals("snareSIDType", "Unknown", out.get("snareSIDType").get(0));
      assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
      assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
      assertEquals("sourceNtDomain", "ABH", out.get("sourceNtDomain").get(0));
      assertEquals("sourceSecurityID", "S-1-5-18", out.get("sourceSecurityID").get(0));
      assertEquals("sourceUserName", "tpap1lzbeca02v$", out.get("sourceUserName").get(0));
      assertEquals("startTime", 1342775212000L, out.get("startTime").get(0));
   }


   //
   @Test
   public void testSecurity4754SuccessAudit() throws FileNotFoundException {
       String line = "MSWinEventLog\t0\tSecurity\t1280306\tWed Feb 17 15:30:23 2016\t4754\tMicrosoft-Windows-Security-Auditing\tE8SEC\\new_universal_security_grp\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSecurity Group Management\t\tA security-enabled universal group was created.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0xeff5912    Group:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1138   Group Name:  new_universal_security_grp   Group Domain:  E8SEC    Attributes:   SAM Account Name: new_universal_security_grp   SID History:  -    Additional Information:   Privileges:  -\t1280305";
       boolean result = doTest(buildRecord(line));
       assertEquals(true, result);
       Record out = this.outCommand.getRecord(0);
       OutUtils.printOut(out.toString());

       assertEquals("cefEventName", "A security-enabled universal group was created.", out.get("cefEventName").get(0));
       assertEquals("cefSignatureId", "Security-4754-Success Audit", out.get("cefSignatureId").get(0));
       assertEquals("destinationGroup", "new_universal_security_grp", out.get("destinationGroup").get(0));
       assertEquals("destinationNtDomain", "E8SEC", out.get("destinationNtDomain").get(0));
       assertEquals("destinationGroupSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1138", out.get("destinationGroupSecurityID").get(0));
       assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
       assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
       assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
       assertEquals("deviceUserName", "e8sec\\new_universal_security_grp", out.get("deviceUserName").get(0));
       assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
       assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
       assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
       assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
       assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
       assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
       assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
       assertEquals("snareCrc", "1280305", out.get("snareCrc").get(0));
       assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
       assertEquals("snareEventCounter", "1280306", out.get("snareEventCounter").get(0));
       assertEquals("snareEventId", "4754", out.get("snareEventId").get(0));
       assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
       assertEquals("snareEventUser", "E8SEC\\new_universal_security_grp", out.get("snareEventUser").get(0));
       assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
       assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
       assertEquals("sourceLogonID", "0xeff5912", out.get("sourceLogonID").get(0));
       assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
       assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
       assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
       assertEquals("startTime", 1455723023000L, out.get("startTime").get(0));
   }

    //MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 9 35 40 2012 \t5140\tMicrosoft-Windows-Security-Auditing\tTPAP1LVSMS01V$\tUser\tFailure Audit\tTPAP1CVRDPA01V.abh.ent.example.com\tFile Share\t\tA network share object was accessed.     Subject:   Security ID:  S-1-5-21-1482476501-484061587-839522115-23757   Account Name:  TPAP1LVSMS01V$   Account Domain:  ABH   Logon ID:  0x9473274    Network Information:    Object Type:  File   Source Address:  138.83.24.79   Source Port:  3586     Share Information:   Share Name:  \\*ADMIN$   Share Path:  ??C:Windows    Access Request Information:   Access Mask:  0x1   Accesses:  ReadData (or ListDirectory)        \t22268355
    @Test
    public void testSecurity5140SuccessAudit() throws FileNotFoundException {
        String line = "MSWinEventLog\t0\tSecurity\t0\tFri Jul 20 9 35 40 2012 \t5140\tMicrosoft-Windows-Security-Auditing\tTPAP1LVSMS01V$\tUser\tFailure Audit\tTPAP1CVRDPA01V.abh.ent.example.com\tFile Share\t\tA network share object was accessed.     Subject:   Security ID:  S-1-5-21-1482476501-484061587-839522115-23757   Account Name:  TPAP1LVSMS01V$   Account Domain:  ABH   Logon ID:  0x9473274    Network Information:    Object Type:  File   Source Address:  138.83.24.79   Source Port:  3586     Share Information:   Share Name:  \\\\*\\ADMIN$   Share Path:  ??C:Windows    Access Request Information:   Access Mask:  0x1   Accesses:  ReadData (or ListDirectory)        \t22268355";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out.toString());

       assertEquals("cefEventName", "A network share object was accessed.", out.get("cefEventName").get(0));
       assertEquals("cefSignatureId", "Security-5140-Failure Audit", out.get("cefSignatureId").get(0));
       assertEquals("destinationObjectType", "File", out.get("destinationObjectType").get(0));
       assertEquals("destinationShareName", "\\\\*\\ADMIN$", out.get("destinationShareName").get(0));
       assertEquals("deviceDnsDomain", "abh.ent.example.com", out.get("deviceDnsDomain").get(0));
       assertEquals("deviceHostName", "tpap1cvrdpa01v", out.get("deviceHostName").get(0));
       assertEquals("deviceNameOrIp", "TPAP1CVRDPA01V.abh.ent.example.com", out.get("deviceNameOrIp").get(0));
       assertEquals("deviceUserName", "tpap1lvsms01v$", out.get("deviceUserName").get(0));
       assertEquals("eventLogType", "Failure Audit", out.get("eventLogType").get(0));
       assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
       assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
       assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
       assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
       assertEquals("snareCategoryString", "File Share", out.get("snareCategoryString").get(0));
       assertEquals("snareComputerName", "TPAP1CVRDPA01V.abh.ent.example.com", out.get("snareComputerName").get(0));
       assertEquals("snareCrc", "22268355", out.get("snareCrc").get(0));
       assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
       assertEquals("snareEventCounter", "0", out.get("snareEventCounter").get(0));
       assertEquals("snareEventId", "5140", out.get("snareEventId").get(0));
       assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
       assertEquals("snareEventUser", "TPAP1LVSMS01V$", out.get("snareEventUser").get(0));
       assertEquals("snareSIDType", "User", out.get("snareSIDType").get(0));
       assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
       assertEquals("sourceAddress", "138.83.24.79", out.get("sourceAddress").get(0));
       assertEquals("sourceLogonID", "0x9473274", out.get("sourceLogonID").get(0));
       assertEquals("sourceNameOrIp", "138.83.24.79", out.get("sourceNameOrIp").get(0));
       assertEquals("sourceNtDomain", "ABH", out.get("sourceNtDomain").get(0));
       assertEquals("sourcePort", 3586, out.get("sourcePort").get(0));
       assertEquals("sourceSecurityID", "S-1-5-21-1482476501-484061587-839522115-23757", out.get("sourceSecurityID").get(0));
       assertEquals("sourceUserName", "tpap1lvsms01v$", out.get("sourceUserName").get(0));
       assertEquals("startTime", 1342776940000L, out.get("startTime").get(0));
    }

    @Test
   public void testSecurity4756SuccessAudit() throws FileNotFoundException {
      String line = "MSWinEventLog\t0\tSecurity\t1280394\tWed Feb 17 15:33:43 2016\t4756\tMicrosoft-Windows-Security-Auditing\tE8SEC\\new_universal_security_grp\tN/A\tSuccess Audit\tw2k8r2-AD.e8sec.lab\tSecurity Group Management\t\tA member was added to a security-enabled universal group.    Subject:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  jyria   Account Domain:  E8SEC   Logon ID:  0xeff5912    Member:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1104   Account Name:  CN=jyria,CN=Users,DC=e8sec,DC=lab    Group:   Security ID:  S-1-5-21-1908711140-209524994-2501478566-1138   Account Name:  new_universal_security_grp   Account Domain:  E8SEC    Additional Information:   Privileges:  -\t1280393 ";
      boolean result = doTest(buildRecord(line));
      assertEquals(true, result);
      Record out = this.outCommand.getRecord(0);
      OutUtils.printOut(out.toString());

       assertEquals("cefEventName", "A member was added to a security-enabled universal group.", out.get("cefEventName").get(0));
       assertEquals("cefSignatureId", "Security-4756-Success Audit", out.get("cefSignatureId").get(0));
       assertEquals("destinationGroup", "new_universal_security_grp", out.get("destinationGroup").get(0));
       assertEquals("destinationGroupSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1138", out.get("destinationGroupSecurityID").get(0));
       assertEquals("destinationGroupNtDomain", "E8SEC", out.get("destinationGroupNtDomain").get(0));
       assertEquals("destinationSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("destinationSecurityID").get(0));
       assertEquals("destinationUserName", "jyria", out.get("destinationUserName").get(0));

       assertEquals("destinationNtDomain", "e8sec.lab", out.get("destinationNtDomain").get(0));
       assertEquals("destinationLdapUser", "CN=jyria,CN=Users,DC=e8sec,DC=lab", out.get("destinationLdapUser").get(0));
       assertEquals("deviceDnsDomain", "e8sec.lab", out.get("deviceDnsDomain").get(0));
       assertEquals("deviceHostName", "w2k8r2-ad", out.get("deviceHostName").get(0));
       assertEquals("deviceNameOrIp", "w2k8r2-AD.e8sec.lab", out.get("deviceNameOrIp").get(0));
       assertEquals("deviceUserName", "e8sec\\new_universal_security_grp", out.get("deviceUserName").get(0));
       assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
       assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
       assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
       assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
       assertEquals("msWinEventLogHeader", "MSWinEventLog", out.get("msWinEventLogHeader").get(0));
       assertEquals("snareCategoryString", "Security Group Management", out.get("snareCategoryString").get(0));
       assertEquals("snareComputerName", "w2k8r2-AD.e8sec.lab", out.get("snareComputerName").get(0));
       assertEquals("snareCrc", "1280393", out.get("snareCrc").get(0));
       assertEquals("snareCriticality", "0", out.get("snareCriticality").get(0));
       assertEquals("snareEventCounter", "1280394", out.get("snareEventCounter").get(0));
       assertEquals("snareEventId", "4756", out.get("snareEventId").get(0));
       assertEquals("snareEventLogName", "Microsoft-Windows-Security-Auditing", out.get("snareEventLogName").get(0));
       assertEquals("snareEventUser", "E8SEC\\new_universal_security_grp", out.get("snareEventUser").get(0));
       assertEquals("snareSIDType", "N/A", out.get("snareSIDType").get(0));
       assertEquals("snareSourceName", "Security", out.get("snareSourceName").get(0));
       assertEquals("sourceLogonID", "0xeff5912", out.get("sourceLogonID").get(0));
       assertEquals("sourceNtDomain", "E8SEC", out.get("sourceNtDomain").get(0));
       assertEquals("sourceSecurityID", "S-1-5-21-1908711140-209524994-2501478566-1104", out.get("sourceSecurityID").get(0));
       assertEquals("sourceUserName", "jyria", out.get("sourceUserName").get(0));
       assertEquals("startTime", 1455723223000L, out.get("startTime").get(0));
   }
    
  /*@Test
  public void test_samples() throws FileNotFoundException {

    try {
      //File f = new File("/users/macadmin/tmp/accelops-AD-supported.txt");
      File f = new File("/tmp/accelops-AD-supported.txt");
      BufferedReader br = new BufferedReader(new FileReader(f));
      String line;
      int i = 0;

       Pattern p = Pattern.compile(".*?(MSW.*)");
      while ((line = br.readLine()) != null) { // while loop begins here
        i++;
         Matcher m = p.matcher(line);
         if (m.matches()){
            line=m.group(1);
         }
        boolean result = doTest(buildRecord(line));
        if (!result) {
          OutUtils.printOut("\n\nDO NOT MATCH !!!\n\n" + line);
        }
      } // end while
      OutUtils.printOut("parsed : " + i + " lines");

//      String line = "1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
//      boolean result = doTest(buildRecord(line));
//      assertEquals(true, result);
//      Record res = this.outCommand.getRecord(0);
    } catch (IOException ex) {
      java.util.logging.Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
    }
  }  */
}
