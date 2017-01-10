/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.parser.avro;


import com.securityx.flume.log.avro.Event;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.kitesdk.morphline.api.Record;

/**
 * @author jyrialhon
 */
public class TestCustomAvroParserTestForPlutus extends TestCase {

    private String morphlineFile;
    private String morphlineId;

    public TestCustomAvroParserTestForPlutus(String testName) {
        super(testName);
// temporarly commented
//    this.morphlineFile = "logcollection-script-selector-command-list.conf";
//    this.morphlineId = "logcollectionselector";
        this.morphlineFile = "logcollection-parser-main.conf";
        this.morphlineId = "parsermain";
    }

    //
//    /**
//     * Test of getEventParser method, of class AvroParser.
//     */
//    public void testGetEventParser() throws Exception {
//        OutUtils.printOut("getEventParser");
//        AvroParser instance = AvroParser.BuildParser();
//        MorphlineParser expResult = null;
//        MorphlineParser result = instance.getEventParser();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of parseOnly method, of class AvroParser.
//     */
//    public void testParseOnly() throws Exception {
//        OutUtils.printOut("parseOnly");
//        Record[] in = null;
//        AvroParser instance = AvroParser.BuildParser();
//        Record[] expResult = null;
//        Record[] result = instance.parseOnly(in);
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of shutdown method, of class AvroParser.
//     */
//    public void testShutdown() throws Exception {
//        OutUtils.printOut("shutdown");
//        AvroParser instance = AvroParser.BuildParser();
//        instance.shutdown();
//    }
//
//    /**
//     * Test from avro src file to avro outFile
//     */


    @Test
    public void testQradarWindows4624() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T14:37:30.342656-07:00 DOMAINCONTROLLERNAME2 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME2.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4624\tEventIDCode=4624\tEventType=8\tEventCategory=55808\tRecordNumber=118410799\tTimeGenerated=1459805819\tTimeWritten=1459805819\tLevel=Log Always\tKeywords=Audit Success\tTask=SE_ADT_LOGON_LOGON\tOpcode=Info\tMessage=An account was successfully logged on.  Subject:  Security ID:  NULL SID  Account Name:  -  Account Domain:  -  Logon ID:  0x0  Logon Type:   3  Impersonation Level:  Impersonation  New Logon:  Security ID:  domainname\\LT-011208$  Account Name:  LT-011208$  Account Domain:  domainname  Logon ID:  0x3C61EDD25  Logon GUID:  {A6BFF108-B4F7-E235-1D5F-AA327162917D}  Process Information:  Process ID:  0x0  Process Name:  -  Network Information:  Workstation Name:   Source Network Address: 10.51.23.117  Source Port:  53039  Detailed Authentication Information:  Logon Process:  Kerberos  Authentication Package: Kerberos  Transited Services: -  Package Name (NTLM only): -  Key Length:  0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).  The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.  The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.  The impersonation level field indicates the extent to which a process in the logon session can impersonate.  The authentication information fields provide detailed information about this specific logon request.  - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.  - Transited services indicate which intermediate services have participated in this logon request.  - Package name indicates which sub-protocol was used among the NTLM protocols.  - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourcePort", 53039, out.get("sourcePort").get(0));
//        assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
        assertEquals("destinationLogonGUID", "{A6BFF108-B4F7-E235-1D5F-AA327162917D}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationLogonID", "0x3C61EDD25", out.get("destinationLogonID").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationUserName", "lt-011208$", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459805850342L, out.get("receiptTime").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
        assertEquals("startTime", 1459805850342L, out.get("startTime").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME2", out.get("deviceHostName").get(0));
        assertEquals("destinationNtDomain", "domainname", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "10.51.23.117", out.get("sourceAddress").get(0));
//        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "NULL SID", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
        assertEquals("destinationSecurityID", "domainname\\LT-011208$", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME2", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));

    }

    @Test
    public void testQradarWindows4625() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T15:32:42.099148-07:00 DOMAINCONTROLLERNAME2 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME2.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4625\tEventIDCode=4625\tEventType=16\tEventCategory=54528\tRecordNumber=1845246378\tTimeGenerated=1459809140\tTimeWritten=1459809140\tLevel=Log Always\tKeywords=Audit Failure\tTask=SE_ADT_LOGON_LOGON\tOpcode=Info\tMessage=An account failed to log on.  Subject:  Security ID:  NULL SID  Account Name:  -  Account Domain:  -  Logon ID:  0x0  Logon Type:   3  Account For Which Logon Failed:  Security ID:  NULL SID  Account Name:  servername$  Account Domain:  domainname  Failure Information:  Failure Reason:  Unknown user name or bad password.  Status:   0xc000006d  Sub Status:  0xc000006a  Process Information:  Caller Process ID: 0x0  Caller Process Name: -  Network Information:  Workstation Name: servername  Source Network Address: 10.32.14.51  Source Port:  63641  Detailed Authentication Information:  Logon Process:  NtLmSsp   Authentication Package: NTLM  Transited Services: -  Package Name (NTLM only): -  Key Length:  0  This event is generated when a logon request fails. It is generated on the computer where access was attempted.  The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network).  The Process Information fields indicate which account and process on the system requested the logon.  The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.  The authentication information fields provide detailed information about this specific logon request.  - Transited services indicate which intermediate services have participated in this logon request.  - Package name indicates which sub-protocol was used among the NTLM protocols.  - Key length indicates the length of the generated session key. This will be 0 if no session key was requested. ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourcePort", 63641, out.get("sourcePort").get(0));
//        assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
        assertEquals("sourceHostName", "servername", out.get("sourceHostName").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationUserName", "servername$", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459809162099L, out.get("receiptTime").get(0));
        assertEquals("keyLength", 0, out.get("keyLength").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("logonProcess", "NtLmSsp", out.get("logonProcess").get(0));
        assertEquals("startTime", 1459809162099L, out.get("startTime").get(0));
        assertEquals("packageName", "-", out.get("packageName").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME2", out.get("deviceHostName").get(0));
        assertEquals("destinationNtDomain", "domainname", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "10.32.14.51", out.get("sourceAddress").get(0));
//        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "NULL SID", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("cefSignatureId", "Security-4625-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("subStatus", "0xc000006a", out.get("subStatus").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("authenticationPackage", "NTLM", out.get("authenticationPackage").get(0));
        assertEquals("destinationSecurityID", "NULL SID", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME2", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "servername", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("status", "0xc000006d", out.get("status").get(0));
    }

    @Test
    public void testQradarWindows4648() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T14:41:19.456121-07:00 DOMAINCONTROLLERNAME1 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME1.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4648\tEventIDCode=4648\tEventType=8\tEventCategory=12544\tRecordNumber=1624465120\tTimeGenerated=1459806070\tTimeWritten=1459806070\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A logon was attempted using explicit credentials.  Subject:  Security ID:  NT AUTHORITY\\SYSTEM  Account Name:  DOMAINCONTROLLERNAME1$  Account Domain:  domainname  Logon ID:  0x3e7  Logon GUID:  {8D96EBC6-BC8C-0757-3A99-8FCB689B0059}  Account Whose Credentials Were Used:  Account Name:  saldap  Account Domain:  domainname  Logon GUID:  {00000000-0000-0000-0000-000000000000}  Target Server:  Target Server Name: localhost  Additional Information: localhost  Process Information:  Process ID:  0x208  Process Name:  C:\\Windows\\System32\\lsass.exe  Network Information:  Network Address: 10.8.1.133  Port:   57895  This event is generated when a process attempts to log on an account by explicitly specifying that account’s credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command. ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourcePort", 57895, out.get("sourcePort").get(0));
        assertEquals("sourceNtDomain", "domainname", out.get("sourceNtDomain").get(0));
        assertEquals("sourceLogonGUID", "{8D96EBC6-BC8C-0757-3A99-8FCB689B0059}", out.get("sourceLogonGUID").get(0));
        assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationNameOrIp", "localhost", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationUserName", "saldap", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459806079456L, out.get("receiptTime").get(0));
        assertEquals("startTime", 1459806079456L, out.get("startTime").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME1", out.get("deviceHostName").get(0));
        assertEquals("destinationNtDomain", "domainname", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "10.8.1.133", out.get("sourceAddress").get(0));
        assertEquals("sourceUserName", "domaincontrollername1$", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("destinationHostName", "localhost", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "Security-4648-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME1", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "10.8.1.133", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));

    }

    @Test
    public void testQradarWindows4768() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T14:45:36.928050-07:00 DOMAINCONTROLLERNAME3 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME3.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4768\tEventIDCode=4768\tEventType=16\tEventCategory=54272\tRecordNumber=39073352\tTimeGenerated=1459806303\tTimeWritten=1459806303\tLevel=Log Always\tKeywords=Audit Failure\tTask=SE_ADT_ACCOUNTLOGON_KERBCREDENTIALVALIDATION\tOpcode=Info\tMessage=A Kerberos authentication ticket (TGT) was requested.  Account Information:  Account Name:  extest_3552e103d9dc4@domainname.com  Supplied Realm Name: DOMAINNAME.COM  User ID:   NULL SID  Service Information:  Service Name:  krbtgt/DOMAINNAME.COM  Service ID:  NULL SID  Network Information:  Client Address:  ::ffff:10.6.8.75  Client Port:  39646  Additional Information:  Ticket Options:  0x40810010  Result Code:  0x6  Ticket Encryption Type: 0xFFFFFFFF  Pre-Authentication Type: -  Certificate Information:  Certificate Issuer Name:    Certificate Serial Number:   Certificate Thumbprint:    Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120. ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourcePort", 39646, out.get("sourcePort").get(0));
        assertEquals("destinationNameOrIp", "DOMAINCONTROLLERNAME3.domainname.com", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationUserName", "extest_3552e103d9dc4@domainname.com", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459806336928L, out.get("receiptTime").get(0));
        assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));
        assertEquals("startTime", 1459806336928L, out.get("startTime").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME3", out.get("deviceHostName").get(0));
        assertEquals("destinationNtDomain", "DOMAINNAME.COM", out.get("destinationNtDomain").get(0));
        assertEquals("destinationServiceSecurityID", "NULL SID", out.get("destinationServiceSecurityID").get(0));
        assertEquals("destinationDnsDomain", "domainname.com", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceAddress", "10.6.8.75", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationServiceName", "krbtgt/DOMAINNAME.COM", out.get("destinationServiceName").get(0));
        assertEquals("destinationSecurityID", "NULL SID", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("preAuthenticationType", "-", out.get("preAuthenticationType").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("destinationProcessName", "krbtgt", out.get("destinationProcessName").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME3", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "::ffff:10.6.8.75", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("status", "0x6", out.get("status").get(0));
        assertEquals("ticketEncryptionType", "0xFFFFFFFF", out.get("ticketEncryptionType").get(0));

    }

    @Test
    public void testQradarWindows4769PlutusParsingFailure() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Jun 14 09:33:25 10.50.1.21 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=FIRMSVCDC1.firm.example.local\tOriginatingComputer=\tUser=\tDomain=\tEventID=4769\tEventIDCode=4769\tEventType=16\tEventCategory=14337\tRecordNumber=1641976640\tTimeGenerated=1465910344\tTimeWritten=1465910344\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A Kerberos service ticket was requested.  Account Information:  Account Name:  SVCSPSQL1$@FIRM.EXAMPLE.LOCAL  Account Domain:  FIRM.EXAMPLE.LOCAL  Logon GUID:  {00000000-0000-0000-0000-000000000000}  Service Information:  Service Name:  ccnelson  Service ID:  NULL SID  Network Information:  Client Address:  ::ffff:10.70.1.101  Client Port:  50004  Additional Information:  Ticket Options:  0x40810000  Ticket Encryption Type: 0xffffffff  Failure Code:  0x1b  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120. ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

    }

    @Test
    public void testQradarWindows4769() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T14:37:13.107992-07:00 DOMAINCONTROLLERNAME3 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME3.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4769\tEventIDCode=4769\tEventType=16\tEventCategory=54272\tRecordNumber=39072763\tTimeGenerated=1459805809\tTimeWritten=1459805809\tLevel=Log Always\tKeywords=Audit Failure\tTask=SE_ADT_ACCOUNTLOGON_KERBEROS\tOpcode=Info\tMessage=A Kerberos service ticket was requested.  Account Information:  Account Name:  account1$@DOMAINNAME.COM  Account Domain:  DOMAINNAME.COM  Logon GUID:  {00000000-0000-0000-0000-000000000000}  Service Information:  Service Name:  accountname05  Service ID:  NULL SID  Network Information:  Client Address:  ::ffff:10.6.8.19  Client Port:  62421  Additional Information:  Ticket Options:  0x40810000  Ticket Encryption Type: 0xFFFFFFFF  Failure Code:  0x1B  Transited Services: -  This event is generated every time access is requested to a resource such as a computer or a Windows service.  The service name indicates the resource to which access was requested.  This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event.  The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket.  Ticket options, encryption types, and failure codes are defined in RFC 4120. ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("destinationServiceSecurityID", "NULL SID", out.get("destinationServiceSecurityID").get(0));
        assertEquals("sourcePort", 62421, out.get("sourcePort").get(0));
        assertEquals("sourceAddress", "10.6.8.19", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationServiceName", "accountname05", out.get("destinationServiceName").get(0));
        assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
        assertEquals("cefSignatureId", "Security-4769-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationUserName", "account1$@domainname.com", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459805833107L, out.get("receiptTime").get(0));
        assertEquals("ticketOptions", "0x40810000", out.get("ticketOptions").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("startTime", 1459805833107L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME3", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME3", out.get("deviceHostName").get(0));
        assertEquals("destinationNtDomain", "DOMAINNAME.COM", out.get("destinationNtDomain").get(0));
        assertEquals("status", "0x1B", out.get("status").get(0));
        assertEquals("ticketEncryptionType", "0xFFFFFFFF", out.get("ticketEncryptionType").get(0));
    }


    @Test
    public void testQradarWindows4661() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T14:38:12.071041-07:00 DOMAINCONTROLLERNAME1 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME1.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4661\tEventIDCode=4661\tEventType=8\tEventCategory=54784\tRecordNumber=58663080\tTimeGenerated=1459805881\tTimeWritten=1459805881\tLevel=Log Always\tKeywords=Audit Success\tTask=SE_ADT_OBJECTACCESS_SAM\tOpcode=Info\tMessage=A handle to an object was requested.  Subject :  Security ID:  domainname\\wsdospasswords  Account Name:  wsdospasswords  Account Domain:  domainname  Logon ID:  0x31426C442  Object:  Object Server: Security Account Manager  Object Type: SAM_SERVER  Object Name: CN=Server,CN=System,DC=websense,DC=com  Handle ID: 0xcbde7e2550  Process Information:  Process ID: 0x1dc  Process Name: C:\\Windows\\System32\\lsass.exe  Access Request Information:  Transaction ID: {00000000-0000-0000-0000-000000000000}  Accesses: DELETE     READ_CONTROL     WRITE_DAC     WRITE_OWNER     ConnectToServer     ShutdownServer     InitializeServer     CreateDomain     EnumerateDomains     LookupDomain     Undefined Access (no effect) Bit 6     Undefined Access (no effect) Bit 7     Undefined Access (no effect) Bit 8       Access Reasons:  -  Access Mask: 0xF01FF  Privileges Used for Access Check: -  Properties: ---  {bf967aad-0de6-11d0-a285-00aa003049e2}   Restricted SID Count: 0 ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourceNtDomain", "domainname", out.get("sourceNtDomain").get(0));
        assertEquals("sourceUserName", "wsdospasswords", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "domainname\\wsdospasswords", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("destinationObjectType", "SAM_SERVER", out.get("destinationObjectType").get(0));
        assertEquals("cefSignatureId", "Security-4661-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceLogonID", "0x31426C442", out.get("sourceLogonID").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationObjectName", "CN=Server,CN=System,DC=websense,DC=com", out.get("destinationObjectName").get(0));
        assertEquals("destinationObjectServer", "Security Account Manager", out.get("destinationObjectServer").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("desiredAccess", "DELETE     READ_CONTROL     WRITE_DAC     WRITE_OWNER     ConnectToServer     ShutdownServer     InitializeServer     CreateDomain     EnumerateDomains     LookupDomain     Undefined", out.get("desiredAccess").get(0));
        assertEquals("receiptTime", 1459805892071L, out.get("receiptTime").get(0));
        assertEquals("destinationObjectHandle", "0xcbde7e2550", out.get("destinationObjectHandle").get(0));
        assertEquals("startTime", 1459805892071L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceProcessID", "0x1dc", out.get("sourceProcessID").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME1", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME1", out.get("deviceHostName").get(0));
    }

    @Test
    public void testQradarWindows4771() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T15:57:06.476225-07:00 DOMAINCONTROLLERNAME1 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME1.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4771\tEventIDCode=4771\tEventType=16\tEventCategory=54272\tRecordNumber=1760641319\tTimeGenerated=1459810555\tTimeWritten=1459810555\tLevel=Log Always\tKeywords=Audit Failure\tTask=SE_ADT_ACCOUNTLOGON_KERBCREDENTIALVALIDATION\tOpcode=Info\tMessage=Kerberos pre-authentication failed.  Account Information:  Security ID:  domainname\\accountname70  Account Name:  accountname70  Service Information:  Service Name:  krbtgt/domainname  Network Information:  Client Address:  ::ffff:10.11.1.8  Client Port:  4481  Additional Information:  Ticket Options:  0x40810010  Failure Code:  0x18  Pre-Authentication Type: 2  Certificate Information:  Certificate Issuer Name:    Certificate Serial Number:    Certificate Thumbprint:    Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options and failure codes are defined in RFC 4120.  If the ticket was malformed or damaged during transit and could not be decrypted, then many fields in this event might not be present. ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourcePort", 4481, out.get("sourcePort").get(0));
        assertEquals("sourceAddress", "10.11.1.8", out.get("sourceAddress").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationServiceName", "krbtgt/domainname", out.get("destinationServiceName").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationSecurityID", "domainname\\accountname70", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationUserName", "accountname70", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459810626476L, out.get("receiptTime").get(0));
        assertEquals("ticketOptions", "0x40810010", out.get("ticketOptions").get(0));
        assertEquals("preAuthenticationType", "2", out.get("preAuthenticationType").get(0));
        assertEquals("startTime", 1459810626476L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("destinationProcessName", "krbtgt", out.get("destinationProcessName").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME1", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME1", out.get("deviceHostName").get(0));
        assertEquals("status", "0x18", out.get("status").get(0));
    }

    @Test
    public void testQradarWindows4625UnusualWorkstationName() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-07T09:56:16.356107-07:00 SSDMNC5 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=SSDDMNC5.websense.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4625\tEventIDCode=4625\tEventType=16\tEventCategory=54784\tRecordNumber=72097735453\tTimeGenerated=1460048145\tTimeWritten=1460048145\tLevel=Log Always\tKeywords=Audit Failure\tTask=SE_ADT_LOGON_ACCOUNTLOCKOUT\tOpcode=Info\tMessage=An account failed to log on.  Subject:  Security ID:  NULL SID  Account Name:  -  Account Domain:  -  Logon ID:  0x0  Logon Type:   3  Account For Which Logon Failed:  Security ID:  NULL SID  Account Name:  wsdoshealth_fail  Account Domain:    Failure Information:  Failure Reason:  Account locked out.  Status:   0xc0000234  Sub Status:  0x0  Process Information:  Caller Process ID: 0x0  Caller Process Name: -  Network Information:  Workstation Name: SSDSFCSSO1A-2  Source Network Address: 10.64.80.50  Source Port:  58713  Detailed Authentication Information:  Logon Process:  NtLmSsp   Authentication Package: NTLM  Transited Services: -  Package Name (NTLM only): -  Key Length:  0  This event is generated when a logon request fails. It is generated on the computer where access was attempted.  The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network).  The Process Information fields indicate which account and process on the system requested the logon.  The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.  The authentication information fields provide detailed information about this specific logon request.  - Transited services indicate which intermediate services have participated in this logon request.  - Package name indicates which sub-protocol was used among the NTLM protocols.  - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

    }


    @Test
    public void testQradarWindows4624UnusualWorkstationName() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-07T09:59:21.443987-07:00 SSDMNC5 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=SSDDMNC5.websense.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4624\tEventIDCode=4624\tEventType=8\tEventCategory=54784\tRecordNumber=72097740789\tTimeGenerated=1460048328\tTimeWritten=1460048328\tLevel=Log Always\tKeywords=Audit Success\tTask=SE_ADT_LOGON_LOGON\tOpcode=Info\tMessage=An account was successfully logged on.  Subject:  Security ID:  NULL SID  Account Name:  -  Account Domain:  -  Logon ID:  0x0  Logon Type:   3  New Logon:  Security ID:  NT AUTHORITY\\ANONYMOUS LOGON  Account Name:  ANONYMOUS LOGON  Account Domain:  NT AUTHORITY  Logon ID:  0x11f5816a  Logon GUID:  {00000000-0000-0000-0000-000000000000}  Process Information:  Process ID:  0x0  Process Name:  -  Network Information:  Workstation Name: WNT AUTHORITY\\ANONYMOUS LOGON  Source Network Address: 10.34.132.26  Source Port:  61358  Detailed Authentication Information:  Logon Process:  NtLmSsp   Authentication Package: NTLM  Transited Services: -  Package Name (NTLM only): NTLM V1  Key Length:  128  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).  The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.  The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.  The authentication information fields provide detailed information about this specific logon request.  - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.  - Transited services indicate which intermediate services have participated in this logon request.  - Package name indicates which sub-protocol was used among the NTLM protocols.  - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourcePort", 61358, out.get("sourcePort").get(0));
        assertEquals("sourceNtDomain", "WNT AUTHORITY", out.get("sourceNtDomain").get(0));
        assertEquals("destinationLogonGUID", "{00000000-0000-0000-0000-000000000000}", out.get("destinationLogonGUID").get(0));
        assertEquals("destinationLogonID", "0x11f5816a", out.get("destinationLogonID").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("destinationUserName", "anonymous logon", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1460048361443L, out.get("receiptTime").get(0));
        assertEquals("keyLength", 128, out.get("keyLength").get(0));
        assertEquals("transitedService", "-", out.get("transitedService").get(0));
        assertEquals("logonProcess", "NtLmSsp", out.get("logonProcess").get(0));
        assertEquals("startTime", 1460048361443L, out.get("startTime").get(0));
        assertEquals("packageName", "NTLM V1", out.get("packageName").get(0));
        assertEquals("deviceHostName", "SSDMNC5", out.get("deviceHostName").get(0));
        assertEquals("destinationNtDomain", "NT AUTHORITY", out.get("destinationNtDomain").get(0));
        assertEquals("sourceAddress", "10.34.132.26", out.get("sourceAddress").get(0));
        assertEquals("sourceUserName", "ANONYMOUS LOGON", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "NULL SID", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
        assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
        assertEquals("authenticationPackage", "NTLM", out.get("authenticationPackage").get(0));
        assertEquals("destinationSecurityID", "NT AUTHORITY\\ANONYMOUS LOGON", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
        assertEquals("deviceNameOrIp", "SSDMNC5", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "WNT AUTHORITY\\ANONYMOUS LOGON", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));

    }

    @Test
    public void testQradarWindows4673() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Jun 19 11:00:11 10.x.x.x AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=host1.firm.example.local\tOriginatingComputer=\tUser=\tDomain=\tEventID=4673\tEventIDCode=4673\tEventType=8\tEventCategory=13056\tRecordNumber=1645840458\tTimeGenerated=1466359205\tTimeWritten=1466359205\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A privileged service was called.  Subject:  Security ID:  NT AUTHORITY\\SYSTEM  Account Name:  host1$  Account Domain:  EXAMPLENT  Logon ID:  0x3e7  Service:  Server: NT Local Security Authority / Authentication Service  Service Name: LsaRegisterLogonProcess()  Process:  Process ID: 0x2ec  Process Name: C:\\Windows\\System32\\lsass.exe  Service Request Information:  Privileges:  SeTcbPrivilege ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("privileges", "SeTcbPrivilege", out.get("privileges").get(0));
        assertEquals("destinationDnsDomain", "firm.example.local", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceNtDomain", "EXAMPLENT", out.get("sourceNtDomain").get(0));
        assertEquals("destinationServiceServer", "NT Local Security Authority / Authentication Service", out.get("destinationServiceServer").get(0));
        assertEquals("sourceUserName", "host1$", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\SYSTEM", out.get("sourceSecurityID").get(0));
        assertEquals("sourceProcessName", "C:\\Windows\\System32\\lsass.exe", out.get("sourceProcessName").get(0));
        assertEquals("destinationHostName", "host1", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "Security-4673-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("destinationNameOrIp", "host1.firm.example.local", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceLogonID", "0x3e7", out.get("sourceLogonID").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("receiptTime", 1466334011000L, out.get("receiptTime").get(0));
        assertEquals("startTime", 1466334011000L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("destinationProcessName", "LsaRegisterLogonProcess()", out.get("destinationProcessName").get(0));
        assertEquals("sourceProcessID", "0x2ec", out.get("sourceProcessID").get(0));
        assertEquals("deviceNameOrIp", "10.x.x.x", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    }

        @Test
    public void testQradarWindows4742() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Jun 19 11:04:12 10.x.x.x AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=host1.firm.example.local\tOriginatingComputer=\tUser=\tDomain=\tEventID=4742\tEventIDCode=4742\tEventType=8\tEventCategory=13825\tRecordNumber=1645841547\tTimeGenerated=1466359450\tTimeWritten=1466359450\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=A computer account was changed.  Subject:  Security ID:  NT AUTHORITY\\ANONYMOUS LOGON  Account Name:  ANONYMOUS LOGON  Account Domain:  NT AUTHORITY  Logon ID:  0x3e6  Computer Account That Was Changed:  Security ID:  EXAMPLENT\\FW-47DXRL1$  Account Name:  YYYYYYY$  Account Domain:  EXAMPLENT  Changed Attributes:  SAM Account Name: -  Display Name:  -  User Principal Name: -  Home Directory:  -  Home Drive:  -  Script Path:  -  Profile Path:  -  User Workstations: -  Password Last Set: 6/19/2016 11:04:10 AM  Account Expires:  -  Primary Group ID: -  AllowedToDelegateTo: -  Old UAC Value:  -  New UAC Value:  -  User Account Control: -  User Parameters: -  SID History:  -  Logon Hours:  -  DNS Host Name:  -  Service Principal Names: -  Additional Information:  Privileges:  - ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
            assertTrue(output.size()>0);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourceNtDomain", "NT AUTHORITY", out.get("sourceNtDomain").get(0));
        assertEquals("adProfilePath", "-", out.get("adProfilePath").get(0));
        assertEquals("adHomeDrive", "-", out.get("adHomeDrive").get(0));
        assertEquals("adServerPrincipalNames", "-", out.get("adServerPrincipalNames").get(0));
        assertEquals("adUserParameters", "-", out.get("adUserParameters").get(0));
        assertEquals("adUserPrincipalName", "-", out.get("adUserPrincipalName").get(0));
        assertEquals("destinationNameOrIp", "host1.firm.example.local", out.get("destinationNameOrIp").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("adOldUacValue", "-", out.get("adOldUacValue").get(0));
        assertEquals("destinationUserName", "yyyyyyy$", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1466334252000L, out.get("receiptTime").get(0));
        assertEquals("adScriptPath", "-", out.get("adScriptPath").get(0));
        assertEquals("startTime", 1466334252000L, out.get("startTime").get(0));
        assertEquals("adSidHistory", "-", out.get("adSidHistory").get(0));
        assertEquals("destinationNtDomain", "EXAMPLENT", out.get("destinationNtDomain").get(0));
        assertEquals("adUserWorkstation", "-", out.get("adUserWorkstation").get(0));
        assertEquals("destinationDnsDomain", "firm.example.local", out.get("destinationDnsDomain").get(0));
        assertEquals("sourceUserName", "anonymous logon", out.get("sourceUserName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("sourceSecurityID", "NT AUTHORITY\\ANONYMOUS LOGON", out.get("sourceSecurityID").get(0));
        assertEquals("adPrimaryGroupID", "-", out.get("adPrimaryGroupID").get(0));
        assertEquals("destinationHostName", "host1", out.get("destinationHostName").get(0));
        assertEquals("cefSignatureId", "Security-4742-Success Audit", out.get("cefSignatureId").get(0));
        assertEquals("sourceLogonID", "0x3e6", out.get("sourceLogonID").get(0));
        assertEquals("destinationSecurityID", "EXAMPLENT\\FW-47DXRL1$", out.get("destinationSecurityID").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("adUserAccountControl", "-", out.get("adUserAccountControl").get(0));
        assertEquals("adHomeDirectory", "-", out.get("adHomeDirectory").get(0));
        assertEquals("adPasswdLastSet", "6/19/2016 11:04:10 AM", out.get("adPasswdLastSet").get(0));
        assertEquals("adDnsHostName", "-", out.get("adDnsHostName").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "10.x.x.x", out.get("deviceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
    }




    @Test
    public void testQradarWindows4776Unmatched() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<13>Jun 19 11:47:38 10.x.x.x AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=host1.firm.example.local\tOriginatingComputer=\tUser=\tDomain=\tEventID=4776\tEventIDCode=4776\tEventType=16\tEventCategory=14336\tRecordNumber=1645853191\tTimeGenerated=1466362054\tTimeWritten=1466362054\tLevel=0\tKeywords=0\tTask=0\tOpcode=0\tMessage=The computer attempted to validate the credentials for an account.  Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 Logon Account:  Source Workstation:  Error Code: 0xc0000064 ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
    }


    @Test
    public void testQradarWindows4776() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "2016-04-04T14:45:36.930174-07:00 DOMAINCONTROLLERNAME3 AgentDevice=WindowsLog\tAgentLogFile=Security\tPluginVersion=7.2.2.984723\tSource=Microsoft-Windows-Security-Auditing\tComputer=DOMAINCONTROLLERNAME3.domainname.com\tOriginatingComputer=\tUser=\tDomain=\tEventID=4776\tEventIDCode=4776\tEventType=16\tEventCategory=54272\tRecordNumber=39073360\tTimeGenerated=1459806311\tTimeWritten=1459806311\tLevel=Log Always\tKeywords=Audit Failure\tTask=SE_ADT_ACCOUNTLOGON_CREDENTIALVALIDATION\tOpcode=Info\tMessage=The computer attempted to validate the credentials for an account.  Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 Logon Account: username Source Workstation: UNKNOWN Error Code: 0xC000006A ";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("sourceHostName", "UNKNOWN", out.get("sourceHostName").get(0));
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("cefSignatureId", "Security-4776-Failure Audit", out.get("cefSignatureId").get(0));
        assertEquals("authenticationPackage", "MICROSOFT_AUTHENTICATION_PACKAGE_V1_0", out.get("authenticationPackage").get(0));
        assertEquals("externalLogSourceType", "MSWinEventLog-qradar", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("destinationUserName", "username", out.get("destinationUserName").get(0));
        assertEquals("receiptTime", 1459806336930L, out.get("receiptTime").get(0));
        assertEquals("startTime", 1459806336930L, out.get("startTime").get(0));
        assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
        assertEquals("deviceNameOrIp", "DOMAINCONTROLLERNAME3", out.get("deviceNameOrIp").get(0));
        assertEquals("sourceNameOrIp", "UNKNOWN", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
        assertEquals("deviceHostName", "DOMAINCONTROLLERNAME3", out.get("deviceHostName").get(0));
        assertEquals("status", "0xC000006A", out.get("status").get(0));
    }

    @Test
    public void testInfobloxDHCP() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();

        String msg = "<30>Aug 25 12:12:12 svc-ipam.firm.example.com dhcpd[12345]: DHCPACK to 10.10.10.10 (aa:bb:cc:dd:ee:ff) via eth2";
        ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
        avroEvent.setBody(buf);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

    }

  /*
  @Test
  public void testUnMactchedFile() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList());
    Event avroEvent = new Event();
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", "1384693669604");

    avroEvent.setHeaders(headers);

    InputStream fis = new FileInputStream("/Volumes/Volume 1000Go 1/JY/SecurityX/Project/201510-mcafee_web_sec/mcafee_wg_sample.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ;
    String line;
    long cpt = 0;
    while ((line = br.readLine()) !=  null){
      InputStream ais = new ByteArrayInputStream(line.getBytes());
      DataInputStream dais = new DataInputStream(ais);
      Decoder decoder = DecoderFactory.get().jsonDecoder(Event.SCHEMA$, dais);
     DatumReader<Event> reader = new SpecificDatumReader<Event>(Event.SCHEMA$);
      avroEvent = reader.read(null, decoder);
      //JsonReader reader = Json.createReader(new StringReader(line));
      //JsonObject object = reader.readObject();
      //reader.close();

      //OutUtils.printOut(object.getString("rawLog"));
      //ByteBuffer buf = ByteBuffer.wrap(object.getString("rawLog").getBytes());
      avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));

      List<Map<String, List<Object>>> output = instance.parse(avroEvent);
      try {
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());

        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
      }catch (Exception e ){
        OutUtils.printOut("failed on line " +cpt+" ("+e.getMessage()+")");
        throw e;
      }
      cpt+=1;
    }


  }
  */

    @Test
    public void testWSAWebProxyLogPlutusAvroEventListParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        if (instance == null) {
        	System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
        	return;
        }
        String line;
        Event avroEvent = new Event();        
        List<String> logList = new LinkedList<String>();

        logList.add("<38>Sep 26 11:36:04 10.55.90.60 <14>Sep 26 11:36:06 ACCESS_LOGS: Info: 1474914966.011 6 10.55.13.113 TCP_MISS/304 285 GET http://crl.microsoft.com/pki/crl/products/WinPCA.crl - DIRECT/crl.microsoft.com application/pkix-crl DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_comp,4.0,0,\"-\",0,0,0,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,IW_comp,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",380.00,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - \"Microsoft-CryptoAPI/6.1\"");
        logList.add("<38>Sep 26 11:36:04 10.55.90.60 <14>Sep 26 11:36:06 ACCESS_LOGS: Info: 1474914966.011 6 10.55.13.113 TCP_MISS/304 285 GET http://crl.microsoft.com/pki/crl/products/WinPCA.crl - DIRECT/crl.microsoft.com application/pkix-crl DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_comp,4.0,0,\"-\",0,0,0,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,IW_comp,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",380.00,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - \"VfAgent 4.5.44.498\"");

        logList.add("<38>Sep 26 12:30:00 10.44.90.25 <14>Sep 26 12:29:52 ACCESS_LOGS: Info: 1474918191.900 47 10.40.5.12 TCP_MISS/200 302 GET http://svc103.viewfinity.com/VFWIU/wiu.ashx?a=4ec9de79-4951-4134-a043-4386a673efac&s=0bf984e9-7894-4e7c-916d-b690b50a2de3&c=1496966054 - DIRECT/svc103.viewfinity.com text/html DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_comp,0.0,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_comp,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",51.40,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"");
        logList.add("<38>Sep 26 12:49:29 10.55.90.60 <14>Sep 26 12:49:31 ACCESS_LOGS: Info: 1474919370.098 9 10.55.5.58 TCP_MEM_HIT/200 17516 GET http://us-ads.openx.net/w/1.0/jstag - NONE/- text/javascript DEFAULT_CASE_12-BLOCK_ADS-DefaultGroup-DefaultGroup-NONE-NONE-NONE <IW_adv,-2.3,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_adv,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",15569.78,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - - 80 559 \"http://my.xfinity.com/adframe/home/x31\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\"");
        logList.add("<38>Sep 26 12:30:01 10.119.90.60 <14>Sep 26 12:30:03 ACCESS_LOGS: Info: 1474918202.299 232 10.119.10.44 TCP_MISS/200 23364 GET http://ml314.com/tag.aspx?2682016 - DIRECT/ml314.com application/javascript DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_busi,-4.3,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_busi,-,\"Unknown\",\"adware\",\"Unknown\",\"Unknown\",\"-\",\"-\",805.66,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\" 54.84.65.133 80 518 \"http://m.seattlepi.com/sports/moore/article/Jim-Moore-A-goobye-letter-to-Willie-the-Go-2-Pup-9236781.php?cmpid=twitter-mobile\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\"");

        logList.add("<38>Sep 26 12:30:01 10.119.90.60 <14>Sep 26 12:30:03 ACCESS_LOGS: Info: 1474918202.414 14 10.119.10.44 TCP_MISS/200 661 GET http://beacon.krxd.net/optout_check?callback=Krux.ns.dura.kxjsonp_optOutCheck - DIRECT/beacon.krxd.net text/javascript DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_ref,-0.8,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_ref,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",377.71,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 54.214.12.148 80 912 \"http://m.seattlepi.com/sports/moore/article/Jim-Moore-A-goobye-letter-to-Willie-the-Go-2-Pup-9236781.php?cmpid=twitter-mobile\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\"");		
        logList.add("<38>Sep 26 12:30:01 10.119.90.60 <14>Sep 26 12:29:53 ACCESS_LOGS: Info: 1474918191.928 43 10.119.10.236 TCP_MISS/304 268 GET http://cdn.espn.com/core/now?render=true&partial=nowfeed&xhr=1&sport=&offset=0&device=desktop - DIRECT/cdn.espn.com application/json DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_sprt,5.0,0,\"-\",0,0,0,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,IW_sprt,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",49.86,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 63.243.228.74 80 452 \"http://www.espn.com/\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\"");

        for (String log : logList) {
        	String wsaMsg = log;
        	avroEvent.setBody(ByteBuffer.wrap(wsaMsg.getBytes()));
        	Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        	headers.put("category", "syslog");
        	headers.put("hostname", "somehost");

        	headers.put("timestamp", "1472658172726");

        	avroEvent.setHeaders(headers);
        	List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        	Map<String, List<Object>> out = output.get(0);
        	OutUtils.printOut(out.toString());
  		}
    }            

    @Test
    public void testWSAWebProxyLogContentTypeTestPlutusAvroEventListParser() throws IOException, Exception {
    	AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
    	if (instance == null) {
    		System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
    		return;
    	}
    
    	String wsaMsgCT_TEXT = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:38 ACCESS_LOGS: Info: 1473366217.541 1300 10.40.5.210 TCP_CLIENT_REFRESH_MISS/200 1040 POST http://intelliconnect.cch.com/scion/secure/ctx_4260093/treeModelService.rpc - DIRECT/intelliconnect.cch.com text/plain DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_busi,3.9,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_busi,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",6.40,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> -  198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"";    			
    	String wsaMsgCT_DASH = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:47 ACCESS_LOGS: Info: 1473366225.616 4306 10.44.1.134 TCP_CLIENT_REFRESH_MISS/200 234 GET http://ortc-ws2-useast1-s0003.realtime.co/broadcast/080/21d6eevc/websocket - DIRECT/ortc-ws2-useast1-s0003.realtime.co - DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_comp,-0.3,0,\"-\",0,0,0,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,IW_comp,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",0.43,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> -  198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"";        	
    	String wsaMsgCT_OCTET_DASH = "<38>Sep 13 08:16:51 10.44.90.25 <14>Sep 13 08:16:42 ACCESS_LOGS: Info: 1473779801.308 15 10.44.1.65 TCP_MISS/200 7540 GET http://v4.download.windowsupdate.com/c/msdownload/update/others/2016/09/22174105_d21f082ec9bb00ee85194730974990f384a3e41c.cab - DIRECT/v4.download.windowsupdate.com application/octet-stream DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_swup,6.1,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_swup,-,\"Unknown\",\"-\",\"Windows Update\",\"Software Updates\",\"-\",\"-\",4021.33,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> -  198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"";
    	String wsaMsgCT_MULTI_DASH = "<38>Sep 13 08:16:51 10.44.90.25 <14>Sep 13 08:16:42 ACCESS_LOGS: Info: 1473779801.308 15 10.44.1.65 TCP_MISS/200 7540 GET http://v4.download.windowsupdate.com/c/msdownload/update/others/2016/09/22174105_d21f082ec9bb00ee85194730974990f384a3e41c.cab - DIRECT/v4.download.windowsupdate.com application/vnd.ms-cab-compressed DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_swup,6.1,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_swup,-,\"Unknown\",\"-\",\"Windows Update\",\"Software Updates\",\"-\",\"-\",4021.33,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> -  198.11.230.98 80 257 - \"VfAgent 4.5.44.498\""; 
    	String wsaMsgCT_PLUS = "<38>Sep 13 10:40:33 10.44.90.25 <14>Sep 13 10:40:26 ACCESS_LOGS: Info: 1473788426.258 88 10.40.3.56 TCP_REFRESH_HIT/200 1059 GET http://images.trvl-media.com/media/content/expus/graphics/other/rewards/icons/member-pricing-unlock-icon-yellow.svg - DIRECT/images.trvl-media.com image/svg+xml DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_trvl,-3.0,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_trvl,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",96.27,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> -  198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"";   
    	String wsaMsgCT_EQUALS = "<38>Sep 13 10:48:12 10.55.90.60 <14>Sep 13 10:48:03 ACCESS_LOGS: Info: 1473788883.175 99 10.55.13.146 TCP_MISS/200 662 POST http://www.delta.com/dlhome/shared/components/TealeafTarget.jsp - DIRECT/www.delta.com text/html:charset=UTF-8 DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_trvl,2.1,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_trvl,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",53.49,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"";   

    	List<String> wsaMsgList = new LinkedList<String>();
    	wsaMsgList.add(wsaMsgCT_TEXT);
    	wsaMsgList.add(wsaMsgCT_DASH);
    	wsaMsgList.add(wsaMsgCT_OCTET_DASH);
    	wsaMsgList.add(wsaMsgCT_MULTI_DASH);
    	wsaMsgList.add(wsaMsgCT_PLUS);
    	wsaMsgList.add(wsaMsgCT_EQUALS);
    	
    	for (String wsaMsg : wsaMsgList) {
    		//OutUtils.printOut("test input: "+wsaMsg);
    		Event avroEvent = new Event();        
    		avroEvent.setBody(ByteBuffer.wrap(wsaMsg.getBytes()));
    		Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    		headers.put("category", "syslog");
    		headers.put("hostname", "somehost");
    		headers.put("timestamp", "1472658172726");
    		avroEvent.setHeaders(headers);
    		List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    		Map<String, List<Object>> out = output.get(0);        	
    		OutUtils.printOut(out.toString());
    	}
    }

    @Test
    public void testWSAWebProxyLogBadPlutusAvroEventParser() throws IOException, Exception {
    	AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
    	if (instance == null) {
    		System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
    		return;
    	}
    	
    	List<String> badLogList = new LinkedList<String>();

    	//badLogList.add("<38>Sep 12 10:00:13 10.44.90.25 <14>Sep 12 10:00:04 ACCESS_LOGS: Info: 1473699604.036 11 10.40.6.216 TCP_MISS/200 1114 GET http://qsearch.media.net/log?logid=adlog&dn=www.nytimes.com&cid=8CU2553YN&ugd=4&vid=0000147369...crid[]=723554252&dfpDiv[]=marketing-ad&actltime[]=604&dfpPos[]=mktg&pvid[]=11&pvnm[]=OPENX&prspt[]=headerBid&prvReqId[]=388870560466086551473699433562&prvAccId[]=538163562&prvApiId[]=&ogbdp[]=0.39&adj1[]=0&adj2[]=15&bdp[]=0.39&cbdp[]=0.33&bId[]=&cmpid[]=&advId[]=152d07b1-da41-4c40-ad18-fe74c269534a%3A537073219&advNm[]=&advUrl[]=&fact[]=headerBid&sc_pvid[]=&sc_pvnm[]=&sc_ogbdp[]=0&sc_adj1[]=0&sc_adj2[]=0&sact[]=&sc_bdp[]=0&sc_cbdp[]=0&sc_bId[]=&sc_cmpid[]=&sc_advId[]=&sc_advNm[]=&sc_advUrl[]=&clsPrc[]=0.39&dfpBd[]=0.39&lper[]=1&brf[]=0&ltime[]=583&td[]=%7C&sbdrid[]=&mnet_aat[]=O&mnet_abd[]=4.25&mnet_apc[]=news-desk&mnet_asz[]=300x250&mnet_alt[]=285&requrl=http%3A%2F%2Fwww.nytimes.com%2F2016%2F09%2F10%2Fopinion%2");
    	badLogList.add( "<38>Sep 13 10:40:33 10.44.90.25 <14>Sep 13 10:40:26 ACCESS_LOGS: Info: 1473788426.258 88 10.40.3.56 TCP_REFRESH_HIT/200 1059 GET http://images.trvl-media.com/media/content/expus/graphics/other/rewards/icons/member-pricing-unlock-icon-yellow.svg - DIRECT/images.trvl-media.com image/svg+xml DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_trvl,-3.0,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",0,0,\"-\",\"-\",-,-,IW_trvl,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",96.27,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - \"Microsoft-CryptoAPI/6.1\"");
    	badLogList.add( "<38>Sep 13 10:48:12 10.55.90.60 <14>Sep 13 10:48:03 ACCESS_LOGS: Info: 1473788883.175 99 10.55.13.146 TCP_MISS/200 662 POST http://www.delta.com/dlhome/shared/components/TealeafTarget.jsp - DIRECT/www.delta.com text/html:charset=UTF-8 DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_trvl,2.1,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_trvl,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",53.49,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - -");

    	/*  These are not being parsed - no plans to parse them since they are denied logs
    	 *  Discussed with Lindsey and he agreed this may not be needed
    	 //badLogList.add("<38>Sep 13 09:12:02 10.44.90.25 <14>Sep 13 09:12:02 ACCESS_LOGS: Info: 1473783123.017 0 10.40.109.236 TCP_DENIED/403 0 TCP_CONNECT 52.16.85.4:80 - NONE/- - BLOCK_ADMIN_TUNNELING-NONE-NONE-NONE-NONE-NONE-NONE <-,-,-,\"-\",-,-,-,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,-,-,\"-\",\"-\",\"-\",\"-\",\"-\",\"-\",0.00,0,Local,\"-\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - \"mybrwoser\"");
    	//badLogList.add("<38>Sep 13 09:37:22 10.55.90.60 <14>Sep 13 09:37:23 ACCESS_LOGS: Info: 1473784641.523 0 10.55.5.101 TCP_DENIED/403 0 TCP_CONNECT 194.68.30.52:80 - NONE/- - BLOCK_ADMIN_TUNNELING-NONE-NONE-NONE-NONE-NONE-NONE <-,-,-,\"-\",-,-,-,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,-,-,\"-\",\"-\",\"-\",\"-\",\"-\",\"-\",0.00,0,Local,\"-\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - \"Microsoft-CryptoAPI/6.1\"");
    	//badLogList.add("<38>Sep 13 09:50:10 10.55.90.60 <14>Sep 13 09:50:12 ACCESS_LOGS: Info: 1473785411.658 0 10.55.5.118 TCP_DENIED/403 0 TCP_CONNECT 194.68.28.116:80 - NONE/- - BLOCK_ADMIN_TUNNELING-NONE-NONE-NONE-NONE-NONE-NONE <-,-,-,\"-\",-,-,-,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,-,-,\"-\",\"-\",\"-\",\"-\",\"-\",\"-\",0.00,0,Local,\"-\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 165.254.47.137 80 259 - \"Microsoft-CryptoAPI/6.1\"");
	*/
    	for (String log : badLogList) {
    		//OutUtils.printOut("test input: "+log);
    		Event avroEvent = new Event();        
    		avroEvent.setBody(ByteBuffer.wrap(log.getBytes()));
    		Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    		headers.put("category", "syslog");
    		headers.put("hostname", "somehost");
    		headers.put("timestamp", "1472658172726");
    		avroEvent.setHeaders(headers);
    		List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    		Map<String, List<Object>> out = output.get(0);        	
    		OutUtils.printOut(out.toString());
    	}
    }
        
    @Test 
    public void testWSAWebProxyLogPlutusAllNullCustomAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        if (instance == null) {
        	System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
        	return;
        }
        Event avroEvent = new Event();
       
        String wsaMsg = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:38 ACCESS_LOGS: Info: 1473366217.541 1300 10.40.5.210 TCP_CLIENT_REFRESH_MISS/200 1040 POST http://intelliconnect.cch.com/scion/secure/ctx_4260093/treeModelService.rpc - DIRECT/intelliconnect.cch.com text/plain DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_busi,3.9,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_busi,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",6.40,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - - - - - -";
        //String wsaMsg = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:38 ACCESS_LOGS: Info: 1473366217.541 1300 10.40.5.210 TCP_CLIENT_REFRESH_MISS/200 1040 POST http://intelliconnect.cch.com/scion/secure/ctx_4260093/treeModelService.rpc - DIRECT/intelliconnect.cch.com text/plain DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_busi,3.9,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_busi,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",6.40,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - - 8080 518 - -";
        //OutUtils.printOut("msg size: "+wsaMsg.length());
        avroEvent.setBody(ByteBuffer.wrap(wsaMsg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1472658172726");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("devicePolicyAction", "TCP_CLIENT_REFRESH_MISS", out.get("devicePolicyAction").get(0));        
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "intelliconnect", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "cch.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));       
        assertEquals("requestPath", "/scion/secure/ctx_4260093/treeModelService.rpc", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "10.40.5.210", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Cisco_WSA", out.get("externalLogSourceType").get(0));
        assertEquals("startTime", 1473366217541L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "intelliconnect.cch.com", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionTime", 1472658172726L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "POST", out.get("requestMethod").get(0));
        assertEquals("sourceAddress", "10.40.5.210", out.get("sourceAddress").get(0));
        assertEquals("responseContentType","text/plain", out.get("responseContentType").get(0));
        assertEquals("deviceEventCategory","IW_busi", out.get("deviceEventCategory").get(0));
        assertEquals("requestReferer","-",out.get("requestReferer").get(0));
        //assertEquals("byesOut","518", out.get("byesOut").get(0));
        //assertEquals("destinationAddress","54.84.65.133", out.get("destinationAddress").get(0));
        //assertEquals("destinationPort","8080", out.get("destinationPort").get(0));
        assertEquals("requestClientApplication","-",out.get("requestClientApplication").get(0));               
    }            
    
    @Test
    public void testWSAWebProxyLogNoCTNoRefPlutusAvroEventParser() throws IOException, Exception {
    	AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
    	if (instance == null) {
    		System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
    		return;
    	}
    	// This one has a - in the content-type definition
    	String wsaMsg = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:47 ACCESS_LOGS: Info: 1473366225.616 4306 10.44.1.134 TCP_CLIENT_REFRESH_MISS/200 234 GET http://ortc-ws2-useast1-s0003.realtime.co/broadcast/080/21d6eevc/websocket - DIRECT/ortc-ws2-useast1-s0003.realtime.co - DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <nc,-0.3,0,\"-\",0,0,0,-,\"-\",-,-,-,\"-\",-,-,\"-\",\"-\",-,-,IW_comp,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",0.43,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> - 198.11.230.98 80 257 - \"VfAgent 4.5.44.498\"";    
    	//OutUtils.printOut("test input: "+wsaMsg);
    	Event avroEvent = new Event();        
    	avroEvent.setBody(ByteBuffer.wrap(wsaMsg.getBytes()));
    	Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    	headers.put("category", "syslog");
    	headers.put("hostname", "somehost");
    	headers.put("timestamp", "1472658172726");

    	avroEvent.setHeaders(headers);
    	List<Map<String, List<Object>>> output = instance.parse(avroEvent);
    	Map<String, List<Object>> out = output.get(0);        	
    	OutUtils.printOut(out.toString());
    	assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("devicePolicyAction", "TCP_CLIENT_REFRESH_MISS", out.get("devicePolicyAction").get(0));        
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "ortc-ws2-useast1-s0003", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "realtime.co", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));       
        assertEquals("requestPath", "/broadcast/080/21d6eevc/websocket", out.get("requestPath").get(0));
        //assertEquals("destinationPort", 8080, out.get("destinationPort").get(0));
        assertEquals("sourceNameOrIp", "10.44.1.134", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Cisco_WSA", out.get("externalLogSourceType").get(0));
        assertEquals("startTime", 1473366225616L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "ortc-ws2-useast1-s0003.realtime.co", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        //assertEquals("destinationAddress", "50.112.37.9", out.get("destinationAddress").get(0));
        assertEquals("logCollectionTime", 1472658172726L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("sourceAddress", "10.44.1.134", out.get("sourceAddress").get(0));
        assertEquals("responseContentType","-", out.get("responseContentType").get(0));
        assertEquals("deviceEventCategory","nc", out.get("deviceEventCategory").get(0));
        assertEquals("requestReferer","-", out.get("requestReferer").get(0));
    }

    @Test
    public void testWSAWebProxyLogNoReqClientPlutusAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        if (instance == null) {
        	System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
        	return;
        }
        Event avroEvent = new Event();
        String wsaMsg = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:38 ACCESS_LOGS: Info: 1473366217.541 1300 10.40.5.210 TCP_CLIENT_REFRESH_MISS/200 1040 POST http://intelliconnect.cch.com/scion/secure/ctx_4260093/treeModelService.rpc - DIRECT/intelliconnect.cch.com text/plain DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_busi,3.9,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_busi,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",6.40,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\" 54.84.65.133 8080 518 \"http://m.seattlepi.com/sports/moore/article/Jim-Moore-A-goobye-letter-to-Willie-the-Go-2-Pup-9236781.php?cmpid=twitter-mobile\" -";    			
        //String msg = "(squid): 1392831663.466     114 81.56.112.95 TCP_MISS/200 4656 GET http://perspectives4.networknotary.org:8080/? - DIRECT/50.112.37.9 text/xml";
        //System.out.println("msg size: "+wsaMsg.length());
        avroEvent.setBody(ByteBuffer.wrap(wsaMsg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1472658172726");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        System.out.println(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("devicePolicyAction", "TCP_CLIENT_REFRESH_MISS", out.get("devicePolicyAction").get(0));        
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "intelliconnect", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "cch.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));       
        assertEquals("requestPath", "/scion/secure/ctx_4260093/treeModelService.rpc", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "10.40.5.210", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Cisco_WSA", out.get("externalLogSourceType").get(0));
        assertEquals("startTime", 1473366217541L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "intelliconnect.cch.com", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionTime", 1472658172726L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "POST", out.get("requestMethod").get(0));
        assertEquals("sourceAddress", "10.40.5.210", out.get("sourceAddress").get(0));
        assertEquals("responseContentType","text/plain", out.get("responseContentType").get(0));
        assertEquals("deviceEventCategory","IW_busi", out.get("deviceEventCategory").get(0));        
        assertEquals("requestReferer",
        		"http://m.seattlepi.com/sports/moore/article/Jim-Moore-A-goobye-letter-to-Willie-the-Go-2-Pup-9236781.php?cmpid=twitter-mobile",
        		out.get("requestReferer").get(0));        
        //assertEquals("byesOut","518", out.get("bytesOut").get(0));
        //assertEquals("byesOut",518, (int)((Integer)out.get("bytesOut").get(0)));
        assertEquals("byesOut",518, out.get("bytesOut").get(0));
        assertEquals("destinationAddress","54.84.65.133", out.get("destinationAddress").get(0));        
        assertEquals("destinationPort", 8080, out.get("destinationPort").get(0));        
        assertEquals("requestClientApplication","-", out.get("requestClientApplication").get(0));        	
    }            
    
    @Test
    public void testWSAWebProxyLogPlutusAvroEventParser() throws IOException, Exception {
        AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
        if (instance == null) {
        	System.err.println("testWSAWebProxyLogAvroEventParser: instance is null");
        	return;
        }
        Event avroEvent = new Event();
        String wsaMsg = "<38>Sep  8 13:23:46 10.44.90.25 <14>Sep 08 13:23:38 ACCESS_LOGS: Info: 1473366217.541 1300 10.40.5.210 TCP_CLIENT_REFRESH_MISS/200 1040 POST http://intelliconnect.cch.com/scion/secure/ctx_4260093/treeModelService.rpc - DIRECT/intelliconnect.cch.com text/plain DEFAULT_CASE_12-DefaultGroup-DefaultGroup-DefaultGroup-NONE-NONE-DefaultGroup <IW_busi,3.9,0,\"-\",0,0,0,1,\"-\",-,-,-,\"-\",1,-,\"-\",\"-\",-,-,IW_busi,-,\"Unknown\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",6.40,0,Local,\"Unknown\",\"-\",-,\"-\",-,-,\"-\",\"-\"> \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\" 54.84.65.133 8080 518 \"http://m.seattlepi.com/sports/moore/article/Jim-Moore-A-goobye-letter-to-Willie-the-Go-2-Pup-9236781.php?cmpid=twitter-mobile\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36\"";    			
        //String msg = "(squid): 1392831663.466     114 81.56.112.95 TCP_MISS/200 4656 GET http://perspectives4.networknotary.org:8080/? - DIRECT/50.112.37.9 text/xml";
        //OutUtils.printOut("msg size: "+wsaMsg.length());
        avroEvent.setBody(ByteBuffer.wrap(wsaMsg.getBytes()));
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");

        headers.put("timestamp", "1472658172726");

        avroEvent.setHeaders(headers);
        List<Map<String, List<Object>>> output = instance.parse(avroEvent);
        Map<String, List<Object>> out = output.get(0);
        OutUtils.printOut(out.toString());
        assertEquals("parserOutFormat", "WebProxyMef", out.get("parserOutFormat").get(0));
        assertEquals("devicePolicyAction", "TCP_CLIENT_REFRESH_MISS", out.get("devicePolicyAction").get(0));        
        assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
        assertEquals("destinationHostName", "intelliconnect", out.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "cch.com", out.get("destinationDnsDomain").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("deviceAction", "DIRECT", out.get("deviceAction").get(0));       
        assertEquals("requestPath", "/scion/secure/ctx_4260093/treeModelService.rpc", out.get("requestPath").get(0));
        assertEquals("sourceNameOrIp", "10.40.5.210", out.get("sourceNameOrIp").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("externalLogSourceType", "Cisco_WSA", out.get("externalLogSourceType").get(0));
        assertEquals("startTime", 1473366217541L, out.get("startTime").get(0));
        assertEquals("destinationNameOrIp", "intelliconnect.cch.com", out.get("destinationNameOrIp").get(0));
        assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
        assertEquals("logCollectionTime", 1472658172726L, out.get("logCollectionTime").get(0));
        assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
        assertEquals("deviceNameOrIp", "somehost", out.get("deviceNameOrIp").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("requestMethod", "POST", out.get("requestMethod").get(0));
        assertEquals("sourceAddress", "10.40.5.210", out.get("sourceAddress").get(0));
        assertEquals("responseContentType","text/plain", out.get("responseContentType").get(0));
        assertEquals("deviceEventCategory","IW_busi", out.get("deviceEventCategory").get(0));
        
        assertEquals("requestReferer",
        		"http://m.seattlepi.com/sports/moore/article/Jim-Moore-A-goobye-letter-to-Willie-the-Go-2-Pup-9236781.php?cmpid=twitter-mobile",
        		out.get("requestReferer").get(0));
        
        //assertEquals("byesOut","518", out.get("bytesOut").get(0));
        //assertEquals("byesOut",518, (int)((Integer)out.get("bytesOut").get(0)));
        assertEquals("byesOut",518, out.get("bytesOut").get(0));
        assertEquals("destinationAddress","54.84.65.133", out.get("destinationAddress").get(0));        
        assertEquals("destinationPort", 8080, out.get("destinationPort").get(0));
        
        assertEquals("requestClientApplication",
        		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36", 
        		out.get("requestClientApplication").get(0));        	
    }            
}
