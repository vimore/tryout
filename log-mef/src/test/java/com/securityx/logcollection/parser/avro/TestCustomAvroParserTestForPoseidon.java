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
import java.util.List;
import java.util.Map;

//import org.kitesdk.morphline.api.Record;

/**
 *
 * @author jyrialhon
 */
public class TestCustomAvroParserTestForPoseidon extends TestCase {

  private String morphlineFile;
  private String morphlineId;

  public TestCustomAvroParserTestForPoseidon(String testName) {
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
public void testWindowsSnareAD4672ToIAMMef() throws IOException, Exception {
  AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
  Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
  String msg = "<13>Mar 28 09:00:01 sjchqdcvw02p.example.local MSWinEventLog\t0\tSecurity\t1081741 Mar 28 09:00:00 2016\t4672\tMicrosoft-Windows-Security-Auditing\tEXAMPLE\\svc-itfw\tN/A\tSuccess Audit\tsjchqdcvw02p.example.local\tSpecial Logon\t\tSpecial privileges assigned to new logon.    Subject:   Security ID:  S-1-5-21-298559873-944822716-1524291848-23111   Account Name:  svc-itfw   Account Domain:  EXAMPLE   Logon ID:  0x49E9AF08    Privileges:  SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeDebugPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeEnableDelegationPrivilege\t1081674";
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
  assertEquals("privileges", "SeSecurityPrivilege     SeBackupPrivilege     SeRestorePrivilege     SeTakeOwnershipPrivilege     SeDebugPrivilege     SeSystemEnvironmentPrivilege     SeLoadDriverPrivilege     SeImpersonatePrivilege     SeEnableDelegationPrivilege", out.get("privileges").get(0));
  assertEquals("deviceDnsDomain", "example.local", out.get("deviceDnsDomain").get(0));
  assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
  assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
  assertEquals("cefSignatureId", "Security-4672-Success Audit", out.get("cefSignatureId").get(0));
  assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
  assertEquals("destinationSecurityID", "S-1-5-21-298559873-944822716-1524291848-23111", out.get("destinationSecurityID").get(0));
  assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
  assertEquals("destinationUserName", "svc-itfw", out.get("destinationUserName").get(0));
  assertEquals("receiptTime", 1459155601000L, out.get("receiptTime").get(0));
  assertEquals("deviceUserName", "example\\svc-itfw", out.get("deviceUserName").get(0));
  assertEquals("startTime", 1459155600000L, out.get("startTime").get(0));
  assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
  assertEquals("deviceNameOrIp", "sjchqdcvw02p.example.local", out.get("deviceNameOrIp").get(0));
  assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
  assertEquals("cefEventName", "Special privileges assigned to new logon.", out.get("cefEventName").get(0));
  assertEquals("deviceHostName", "sjchqdcvw02p", out.get("deviceHostName").get(0));
  assertEquals("destinationNtDomain", "EXAMPLE", out.get("destinationNtDomain").get(0));
}

  @Test
  public void testWindowsSnareAD4624ToIAMMef() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg = "<13>Mar 29 11:59:50 sjchqdcvw01p.example.local MSWinEventLog\t0\tSecurity\t1267311 Mar 29 11:59:49 2016\t4624\tMicrosoft-Windows-Security-Auditing\tEXAMPLE\\panmn0c7ft4phq$\tN/A\tSuccess Audit\tsjchqdcvw01p.example.local\tLogon\t\tAn account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    Impersonation Level:  Impersonation    New Logon:   Security ID:  S-1-5-21-298559873-944822716-1524291848-61112   Account Name:  panmn0c7ft4phq$   Account Domain:  EXAMPLE   Logon ID:  0x43422713   Logon GUID:  {88EC9465-31B9-7B60-7F16-5AB43AB306E6}    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name:    Source Network Address: 10.52.64.82   Source Port:  50046    Detailed Authentication Information:   Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).    The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.    The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The impersonation level field indicates the extent to which a process in the logon session can impersonate.    The authentication information fields provide detailed information about this specific logon request.   - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.   - Transited services ";
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

    assertEquals("deviceDnsDomain", "example.local", out.get("deviceDnsDomain").get(0));
    assertEquals("sourcePort", 50046, out.get("sourcePort").get(0));
//    assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
    assertEquals("destinationLogonGUID", "{88EC9465-31B9-7B60-7F16-5AB43AB306E6}", out.get("destinationLogonGUID").get(0));
    assertEquals("destinationLogonID", "0x43422713", out.get("destinationLogonID").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
    assertEquals("destinationUserName", "panmn0c7ft4phq$", out.get("destinationUserName").get(0));
    assertEquals("receiptTime", 1459252790000L, out.get("receiptTime").get(0));
    assertEquals("keyLength", 0, out.get("keyLength").get(0));
    assertEquals("transitedService", "-", out.get("transitedService").get(0));
    assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
    assertEquals("startTime", 1459252789000L, out.get("startTime").get(0));
    assertEquals("packageName", "-", out.get("packageName").get(0));
    assertEquals("cefEventName", "An account was successfully logged on.", out.get("cefEventName").get(0));
    assertEquals("deviceHostName", "sjchqdcvw01p", out.get("deviceHostName").get(0));
    assertEquals("destinationNtDomain", "EXAMPLE", out.get("destinationNtDomain").get(0));
    assertEquals("sourceAddress", "10.52.64.82", out.get("sourceAddress").get(0));
//    assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
    assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
    assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
    assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
    assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
    assertEquals("destinationSecurityID", "S-1-5-21-298559873-944822716-1524291848-61112", out.get("destinationSecurityID").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("deviceUserName", "example\\panmn0c7ft4phq$", out.get("deviceUserName").get(0));
    assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
    assertEquals("deviceNameOrIp", "sjchqdcvw01p.example.local", out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));
  }


  @Test
  public void testWindowsSnareAD4624WithTabAndSpaceToIAMMef() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.IAMMef);
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg = "<13>Mar 31 11:45:05 sjchqdcvw01p.example.local MSWinEventLog\t0\tSecurity\t121266\t Mar 31 11:45:05 2016\t4624\tMicrosoft-Windows-Security-Auditing\tEXAMPLE\\SJCHQDCVW02P$\tN/A\tSuccess Audit\tsjchqdcvw01p.example.local\tLogon\t\tAn account was successfully logged on.    Subject:   Security ID:  S-1-0-0   Account Name:  -   Account Domain:  -   Logon ID:  0x0    Logon Type:   3    Impersonation Level:  Impersonation    New Logon:   Security ID:  S-1-5-21-298559873-944822716-1524291848-57242   Account Name:  SJCHQDCVW02P$   Account Domain:  EXAMPLE   Logon ID:  0x47115340   Logon GUID:  {80F308B3-79E6-7153-1B33-4E582071DCA7}    Process Information:   Process ID:  0x0   Process Name:  -    Network Information:   Workstation Name:    Source Network Address: 10.52.4.11   Source Port:  65530    Detailed Authentication Information:   Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0    This event is generated when a logon session is created. It is generated on the computer that was accessed.    The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.    The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).    The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.    The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.    The impersonation level field indicates the extent to which a process in the logon session can impersonate.    The authentication information fields provide detailed information about this specific logon request.   - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.   - Transited services indica";
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
    assertEquals("deviceDnsDomain", "example.local", out.get("deviceDnsDomain").get(0));
    assertEquals("sourcePort", 65530, out.get("sourcePort").get(0));
//    assertEquals("sourceNtDomain", "-", out.get("sourceNtDomain").get(0));
    assertEquals("destinationLogonGUID", "{80F308B3-79E6-7153-1B33-4E582071DCA7}", out.get("destinationLogonGUID").get(0));
    assertEquals("destinationLogonID", "0x47115340", out.get("destinationLogonID").get(0));
    assertEquals("externalLogSourceType", "MSWinEventLog", out.get("externalLogSourceType").get(0));
    assertEquals("destinationUserName", "sjchqdcvw02p$", out.get("destinationUserName").get(0));
    assertEquals("receiptTime", 1459424705000L, out.get("receiptTime").get(0));
    assertEquals("keyLength", 0, out.get("keyLength").get(0));
    assertEquals("transitedService", "-", out.get("transitedService").get(0));
    assertEquals("logonProcess", "Kerberos", out.get("logonProcess").get(0));
    assertEquals("startTime", 1459424705000L, out.get("startTime").get(0));
    assertEquals("packageName", "-", out.get("packageName").get(0));
    assertEquals("cefEventName", "An account was successfully logged on.", out.get("cefEventName").get(0));
    assertEquals("deviceHostName", "sjchqdcvw01p", out.get("deviceHostName").get(0));
    assertEquals("destinationNtDomain", "EXAMPLE", out.get("destinationNtDomain").get(0));
    assertEquals("sourceAddress", "10.52.4.11", out.get("sourceAddress").get(0));
  //  assertEquals("sourceUserName", "-", out.get("sourceUserName").get(0));
    assertEquals("eventLogType", "Success Audit", out.get("eventLogType").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("sourceSecurityID", "S-1-0-0", out.get("sourceSecurityID").get(0));
    assertEquals("sourceProcessName", "-", out.get("sourceProcessName").get(0));
    assertEquals("cefSignatureId", "Security-4624-Success Audit", out.get("cefSignatureId").get(0));
    assertEquals("sourceLogonID", "0x0", out.get("sourceLogonID").get(0));
    assertEquals("authenticationPackage", "Kerberos", out.get("authenticationPackage").get(0));
    assertEquals("destinationSecurityID", "S-1-5-21-298559873-944822716-1524291848-57242", out.get("destinationSecurityID").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("deviceUserName", "example\\sjchqdcvw02p$", out.get("deviceUserName").get(0));
    assertEquals("sourceLogonType", "3", out.get("sourceLogonType").get(0));
    assertEquals("parserOutFormat", "IAMMef", out.get("parserOutFormat").get(0));
    assertEquals("sourceProcessID", "0x0", out.get("sourceProcessID").get(0));
    assertEquals("deviceNameOrIp", "sjchqdcvw01p.example.local", out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType", "IAMMef", out.get("logSourceType").get(0));

  }


  @Test
  public void testWindowsEpilogDhcpToHetMMef() throws IOException, Exception {
    AvroParser instance = AvroParser.BuildParser(morphlineFile, morphlineId, SupportedFormats.HETMef);
    Event avroEvent = new Event(); //Wed, 05 Nov 2014 16:58:56 GMT
    String msg = "<13>Mar 31 00:00:12 sjchqsrvvw02p.paloaltonetworks.local GenericLog010,03/30/16,23: 54:47,Assign,10.52.97.46,AVXB27312.paloaltonetworks.local,50CD22B27312,,301555992,0,,,,0x6363702E61766179612E636F6D,ccp.avaya.com,,,,0";
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
    assertEquals("destinationAddress", "10.52.97.46", out.get("destinationAddress").get(0));
    assertEquals("logCollectionCategory", "syslog", out.get("logCollectionCategory").get(0));
    assertEquals("cefSignatureId", "DHCPACK", out.get("cefSignatureId").get(0));
    assertEquals("destinationNameOrIp", "AVXB27312.paloaltonetworks.local", out.get("destinationNameOrIp").get(0));
    assertEquals("externalLogSourceType", "windows-dhcp", out.get("externalLogSourceType").get(0));
    assertEquals("logCollectionTime","1384693669604", out.get("logCollectionTime").get(0));
    assertEquals("logCollectionHost", "somehost", out.get("logCollectionHost").get(0));
    assertEquals("receiptTime", 1459382412000L, out.get("receiptTime").get(0));
    assertEquals("eventOutcome", "10 - Assign", out.get("eventOutcome").get(0));
    assertEquals("startTime", 1459382087000L, out.get("startTime").get(0));
    assertEquals("parserOutFormat", "HETMef", out.get("parserOutFormat").get(0));
    assertEquals("deviceNameOrIp", "sjchqsrvvw02p.paloaltonetworks.local", out.get("deviceNameOrIp").get(0));
    assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
    assertEquals("destinationMacAddress", "50CD22B27312", out.get("destinationMacAddress").get(0));
  }

}