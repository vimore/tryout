package com.securityx.model.mef.morphline.command;

import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MefSshdRulesetTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefSshdRulesetTest.class);

  public MefSshdRulesetTest() {
    super(MefSshdRulesetTest.class.toString());
    this.morphlineId = "sshd";
    this.confFile = "ssh-to-mef.conf";
  }

  @Test
  public void test0() throws FileNotFoundException {
    String line = "sshd[26510]: Invalid user user1 from 218.6.125.166";
    line = "sshd[26510]: Invalid user user1 from 218.6.125.166";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals(true, null != r.getFirstValue("_matchedRuleset"));
    assertEquals("Invalid user",
            r.getFirstValue("cefEventName"));
    assertEquals("user1",
            r.getFirstValue("destinationUserName"));
    assertEquals("26510",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));

  }

  @Test
  public void test1() throws FileNotFoundException {
    String line = "sshd[21939]: pam_unix(sshd:session): session opened for user hivedata by (uid=0)";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals(true, null != r.getFirstValue("_matchedRuleset"));
    assertEquals("session opened",
            r.getFirstValue("cefEventName"));
    assertEquals("hivedata",
            r.getFirstValue("destinationUserName"));
    assertEquals("0",
            r.getFirstValue("sourceUserId"));

  }

  @Test
  public void test2() throws FileNotFoundException {
                 //sshd[27651]: Failed password for invalid user mysql from 123.232.122.162 port 39172 ssh2
    String line = "sshd[17116]: Failed password for root from 61.139.54.71 port 34513 ssh2";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals(true, null != r.getFirstValue("_matchedRuleset"));
    assertEquals("Failed password",
            r.getFirstValue("cefEventName"));
    assertEquals("root",
            r.getFirstValue("destinationUserName"));
    assertEquals("61.139.54.71",
            r.getFirstValue("sourceNameOrIp"));
    assertEquals("34513",
            r.getFirstValue("sourcePort"));
  }
  @Test
  public void test2bis() throws FileNotFoundException {
                 //
    String line = "sshd[27651]: Failed password for invalid user mysql from 123.232.122.162 port 39172 ssh2";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals(true, null != r.getFirstValue("_matchedRuleset"));
    assertEquals("Failed password",
            r.getFirstValue("cefEventName"));
    assertEquals("mysql",
            r.getFirstValue("destinationUserName"));
    assertEquals("123.232.122.162",
            r.getFirstValue("sourceNameOrIp"));
    assertEquals("39172",
            r.getFirstValue("sourcePort"));
  }

  @Test
  public void test3() throws FileNotFoundException {
    String line = "sshd[4924]: Disconnecting: Too many authentication failures for root";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("Disconnecting: Too many authentication failures",
            r.getFirstValue("cefEventName"));
    assertEquals("root",
            r.getFirstValue("destinationUserName"));
    assertEquals("4924",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));

  }

  @Test
  public void test4() throws FileNotFoundException {
    String line = "sshd[3648]: Address 85.233.64.4 maps to ns.citrt.ru, but this does not map back to the address - POSSIBLE BREAK-IN ATTEMPT!";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("POSSIBLE BREAK-IN ATTEMPT!",
            r.getFirstValue("cefEventName"));
    assertEquals("3648",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("85.233.64.4",
            r.getFirstValue("sourceAddress"));
    assertEquals("ns.citrt.ru",
            r.getFirstValue("sourceSuspectHostName"));

  }

  @Test
  public void test5() throws FileNotFoundException {
    String line = "sshd[8283]: reverse mapping checking getaddrinfo for chn-static-142-176.196.203.direct.net.in [203.196.176.142] failed - POSSIBLE BREAK-IN ATTEMPT!";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("POSSIBLE BREAK-IN ATTEMPT!",
            r.getFirstValue("cefEventName"));
    assertEquals("8283",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("203.196.176.142",
            r.getFirstValue("sourceAddress"));

  }

  @Test
  public void test6() throws FileNotFoundException {
    //             sshd[26957]: pam_unix(sshd:auth): authentication failure; logname= uid=0 euid=0 tty=ssh ruser= rhost=123.232.122.162 
    String line = "sshd[17116]: pam_unix(sshd:auth): authentication failure; logname= uid=0 euid=0 tty=ssh ruser= rhost=61.139.54.71  user=root";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("authentication failure",
            r.getFirstValue("cefEventName"));
    assertEquals("0",
            r.getFirstValue("destinationUserEid"));
    assertEquals("0",
            r.getFirstValue("destinationUserId"));
    assertEquals("root",
            r.getFirstValue("destinationUserName"));
    assertEquals("17116",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("61.139.54.71",
            r.getFirstValue("sourceNameOrIp"));

  }
  @Test
  public void test6bis() throws FileNotFoundException {
    String line = "sshd[26957]: pam_unix(sshd:auth): authentication failure; logname= uid=0 euid=0 tty=ssh ruser= rhost=123.232.122.162 ";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("authentication failure",
            r.getFirstValue("cefEventName"));
    assertEquals("0",
            r.getFirstValue("destinationUserEid"));
    assertEquals("0",
            r.getFirstValue("destinationUserId"));
    assertEquals("26957",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("123.232.122.162",
            r.getFirstValue("sourceNameOrIp"));

  }

  @Test
  public void test7() throws FileNotFoundException {
    String line = "sshd[17753]: input_userauth_request: invalid user ryback";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("invalid user",
            r.getFirstValue("cefEventName"));
    assertEquals("ryback",
            r.getFirstValue("destinationUserName"));
    assertEquals("17753",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));

  }

  @Test
  public void test8() throws FileNotFoundException {
    String line = "sshd[20731]: Did not receive identification string from 79.132.248.74";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("79.132.248.74",
            r.getFirstValue("SourceNameOrIp"));
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("Did not receive identification string",
            r.getFirstValue("cefEventName"));
    assertEquals("20731",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));

  }

  @Test
  public void test9() throws FileNotFoundException {
    String line = "sshd[16884]: Received disconnect from 168.63.219.203: 11: Bye Bye ";
    line = "sshd[16884]: Received disconnect from 168.63.219.203: 11: Bye Bye ";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("Received disconnect",
            r.getFirstValue("cefEventName"));
    assertEquals("16884",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("11: Bye Bye ",
            r.getFirstValue("reason"));
    assertEquals("168.63.219.203",
            r.getFirstValue("sourceNameOrIp"));

  }

  @Test
  public void test10() throws FileNotFoundException {
    String line = "sshd[8284]: Connection closed by 203.196.176.142";
    line = "sshd[8284]: Connection closed by 203.196.176.142";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("Connection closed",
            r.getFirstValue("cefEventName"));
    assertEquals("8284",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("203.196.176.142",
            r.getFirstValue("sourceNameOrIp"));

  }

  @Test
  public void test11() throws FileNotFoundException {
    String line = "sshd[26957]: pam_unix(sshd:auth): check pass; user unknown";
    line = "sshd[26957]: pam_unix(sshd:auth): check pass; user unknown";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("check pass",
            r.getFirstValue("cefEventName"));
    assertEquals("26957",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));

  }
  
  @Test
  public void test12() throws FileNotFoundException {
    String line = "sshd[26957]: pam_succeed_if(sshd:auth): error retrieving information about user a";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("error retrieving information",
            r.getFirstValue("cefEventName"));
    assertEquals("26957",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("a",
            r.getFirstValue("destinationUserName"));

  }
  @Test
  public void test13() throws FileNotFoundException {
    String line = "sshd[32611]: PAM 2 more authentication failures; logname= uid=0 euid=0 tty=ssh ruser= rhost=200.186.145.218  user=root";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("more authentication failures",
            r.getFirstValue("cefEventName"));
    assertEquals("32611",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("0",
            r.getFirstValue("destinationUserEid"));
    assertEquals("0",
            r.getFirstValue("destinationUserId"));
    assertEquals("32611",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("200.186.145.218",
            r.getFirstValue("sourceNameOrIp"));
  }
  @Test
  public void test14() throws FileNotFoundException {
    String line = "sshd[21609]: Accepted publickey for ec2-user from 67.148.60.198 port 54117 ssh2";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("Accepted publickey",
            r.getFirstValue("cefEventName"));
    assertEquals("21609",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("ec2-user",
            r.getFirstValue("destinationUserName"));
    assertEquals("67.148.60.198",
            r.getFirstValue("sourceNameOrIp"));
    assertEquals("54117",
            r.getFirstValue("sourcePort"));
  }

  @Test
  public void test15() throws FileNotFoundException {
    String line = "sshd[600]: pam_unix(sshd:session): session closed for user ec2-user";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("session closed",
            r.getFirstValue("cefEventName"));
    assertEquals("600",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("ec2-user",
            r.getFirstValue("destinationUserName"));
  }
  @Test
  public void test16() throws FileNotFoundException {
    String line = "sshd[16845]: PAM service(sshd) ignoring max retries; 6 > 3";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals("sshd",
            r.getFirstValue("_matchedRuleset"));
    assertEquals("service(sshd) ignoring max retries",
            r.getFirstValue("cefEventName"));
    assertEquals("16845",
            r.getFirstValue("deviceProcessId"));
    assertEquals("sshd",
            r.getFirstValue("deviceProcessName"));
    assertEquals("6 > 3",
            r.getFirstValue("reason"));
  }
  

  @Test
  public void testHS() throws FileNotFoundException {
    String line = "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record data = new Record();
    data.put("syslogMessage", line);
    boolean result = doTest(data);

    assertEquals(true, result);
    Record r = this.outCommand.getRecord(0);
    assertEquals(null, r.getFirstValue("_matchRuleset"));
  }

  @Test
   public void testHS2() throws FileNotFoundException, IOException {
   String line = "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
   line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
    Record data = new Record();
    data.put("syslogMessage", line);
   boolean result= doTest(data);
   assertEquals(true, result);
   this.morphlineHarness.shutdown(); //force flush
  // assertEquals(data.toString(), checkFile("./target/sshd-unmatched.log"));
   
   }
   

   /*@Test
   public void test4() throws FileNotFoundException {
   // 
   String line = "%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
   boolean result= doTest(line);
   assertEquals(true, result);
   OutUtils.printOut(this.outCommand.getNumRecords());
   Assert.assertTrue(this.outCommand.getNumRecords() == 1);
   Record r = this.outCommand.getRecord(0);
   Assert.assertEquals("302015", 
   r.get("ciscoEventId").get(0));
   Assert.assertEquals("6", 
   r.get("ciscoEventSeverity").get(0));
   Assert.assertEquals("Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)", 
   r.get("ciscoMessage").get(0));
   Assert.assertEquals("%PIX", 
   r.get("deviceType").get(0));
   Assert.assertEquals("%PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)", 
   r.get("message").get(0));
   }
   */
}
