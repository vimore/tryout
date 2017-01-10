package com.securityx.model.mef.morphline.command.squid;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.MefBluecoatScriptTest;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class WebProxySquidCombinedTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefBluecoatScriptTest.class);

  public WebProxySquidCombinedTest(String name) {
    super(name);
    this.morphlineId = "squidcombinedfromaccesslog";
    this.confFile = "webproxy-squid.conf";
  }

  @Test
  public void test_http200() throws FileNotFoundException {
    String line = "81.56.112.95 - jyria [28/Feb/2014:08:32:35 -0500] \"GET http://www.squid-cache.org/Doc/config/logformat/ HTTP/1.1\" 304 431 \"http://wiki.squid-cache.org/Features/LogFormat\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0\" TCP_REFRESH_UNMODIFIED:DIRECT";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "304", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationNameOrIp", "www.squid-cache.org", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_REFRESH_UNMODIFIED", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));
    Assert.assertEquals("bytesIn", 431, res.get("bytesIn").get(0));
    Assert.assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0", res.get("requestClientApplication").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/Doc/config/logformat/", res.get("requestPath").get(0));
    Assert.assertEquals("requestReferer", "http://wiki.squid-cache.org/Features/LogFormat", res.get("requestReferer").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "jyria", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1393594355000L, res.get("startTime").get(0));

  }
  public void test_http200_bytesIn_bytesOut() throws FileNotFoundException {
    String line = "81.56.112.95 - jyria [28/Feb/2014:08:32:35 -0500] \"GET http://www.squid-cache.org/Doc/config/logformat/ HTTP/1.1\" 304 123 431 \"http://wiki.squid-cache.org/Features/LogFormat\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0\" TCP_REFRESH_UNMODIFIED:DIRECT";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "304", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationNameOrIp", "www.squid-cache.org", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_REFRESH_UNMODIFIED", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));
    Assert.assertEquals("bytesIn", 431, res.get("bytesIn").get(0));
    Assert.assertEquals("bytesOut", 123, res.get("bytesOut").get(0));
    Assert.assertEquals("requestClientApplication", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0", res.get("requestClientApplication").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/Doc/config/logformat/", res.get("requestPath").get(0));
    Assert.assertEquals("requestReferer", "http://wiki.squid-cache.org/Features/LogFormat", res.get("requestReferer").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "jyria", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1393594355000L, res.get("startTime").get(0));

  }
  public void test_httpTimestamp() throws FileNotFoundException {
    String line = "81.56.112.95 - - [23/Apr/2014:04:00:55 -0400] \"POST http://ocsp.digicert.com/ HTTP/1.1\" 200 958 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0\" TCP_MISS:DIRECT";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
  
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
//          OutUtils.printOut("DO NOT MATCH :" + line);
//        }
//      } // end while
//      OutUtils.printOut("parsed : " + i + " lines");
//
////      String line = "1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
////      boolean result = doTest(line);
////      assertEquals(true, result);
////      Record res = this.outCommand.getRecord(0);
//    } catch (IOException ex) {
//      java.util.logging.Logger.getLogger(WebProxySquidTest.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
}
