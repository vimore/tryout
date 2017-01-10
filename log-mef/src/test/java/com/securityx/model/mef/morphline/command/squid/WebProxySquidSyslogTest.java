package com.securityx.model.mef.morphline.command.squid;


import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.MefBluecoatScriptTest;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class WebProxySquidSyslogTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefBluecoatScriptTest.class);

  public WebProxySquidSyslogTest(String name) {
    super(name);
    this.morphlineId = "squidfromrsyslog";
    this.confFile = "webproxy-squid.conf";
  }

  @Test
  public void test_http200() throws FileNotFoundException {
    String line = "(squid): 1392121179.563    515 81.56.112.95 TCP_MISS/200 74364 GET http://www.cloudera.com/content/support/en/downloads/download-components/download-products.html? - DIRECT/65.50.196.141 text/html";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationAddress", "65.50.196.141", res.get("destinationAddress").get(0));
    Assert.assertEquals("destinationNameOrIp", "www.cloudera.com", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_MISS", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 74364, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/content/support/en/downloads/download-components/download-products.html?", res.get("requestPath").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392121179563L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

  }

  @Test
  public void test_http404() throws FileNotFoundException {
    String line = "(squid): 1392052987.632     27 81.56.112.95 TCP_MISS/404 1015 GET http://nine-eyes.herokuapp.com/? - DIRECT/54.243.157.122 text/html";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "404", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationAddress", "54.243.157.122", res.get("destinationAddress").get(0));
    Assert.assertEquals("destinationNameOrIp", "nine-eyes.herokuapp.com", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_MISS", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 1015, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/?", res.get("requestPath").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392052987632L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

  }

  @Test
  public void test_ssl() throws FileNotFoundException {
    String line = "(squid): 1392068647.385 117047 81.56.112.95 TCP_MISS/200 4814 CONNECT hivedata.jiveon.com:443 - DIRECT/23.218.66.131 -";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationAddress", "23.218.66.131", res.get("destinationAddress").get(0));
    Assert.assertEquals("destinationNameOrIp", "hivedata.jiveon.com", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("destinationPort", 443, res.get("destinationPort").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_MISS", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 4814, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "CONNECT", res.get("requestMethod").get(0));
    Assert.assertEquals("responseContentType", "-", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392068647385L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

  }

  @Test
  public void test_httpcustomport() throws FileNotFoundException {
    String line = "(squid): 1392123313.883    511 81.56.112.95 TCP_MISS/200 1257 GET http://portquiz.net:8080/ - DIRECT/178.33.250.62 text/html";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationAddress", "178.33.250.62", res.get("destinationAddress").get(0));
    Assert.assertEquals("destinationNameOrIp", "portquiz.net", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("destinationPort", 8080, res.get("destinationPort").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_MISS", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 1257, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/", res.get("requestPath").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392123313883L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

  }

  @Test
  public void test_denied() throws FileNotFoundException {
    String line = "(squid): 1392123510.839      0 81.56.112.95 TCP_DENIED/403 3109 GET http://portquiz.net:666/ - NONE/- text/html";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "403", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationNameOrIp", "portquiz.net", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("destinationPort", 666, res.get("destinationPort").get(0));
    Assert.assertEquals("deviceAction", "NONE", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_DENIED", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 3109, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/", res.get("requestPath").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392123510839L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

  }

  @Test
  public void test_serverunreachable() throws FileNotFoundException {
    String line = "(squid): 1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "504", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationAddress", "212.27.38.253", res.get("destinationAddress").get(0));
    Assert.assertEquals("destinationNameOrIp", "mafreebox.free.fr", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_MISS", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 3182, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/favicon.ico", res.get("requestPath").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392123548479L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

  }

  @Test
  public void test_badhost() throws FileNotFoundException {
    String line = "(squid): 1392051431.025      6 81.56.112.95 TCP_MISS/503 3480 GET http://www.notrefamille.comhttp//actualite.benchmark.fr/depeche/une/312c4e2df631c40dcf9845720ff1b9ea789460bcdiaporama480x320.jpg - DIRECT/www.notrefamille.comhttp text/html";
    boolean result = doTest(line);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("cefSignatureId", "503", res.get("cefSignatureId").get(0));
    Assert.assertEquals("destinationNameOrIp", "www.notrefamille.comhttp", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("deviceAction", "DIRECT", res.get("deviceAction").get(0));
    Assert.assertEquals("devicePolicyAction", "TCP_MISS", res.get("devicePolicyAction").get(0));
    Assert.assertEquals("bytesIn", 3480, res.get("bytesIn").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "//actualite.benchmark.fr/depeche/une/312c4e2df631c40dcf9845720ff1b9ea789460bcdiaporama480x320.jpg", res.get("requestPath").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("sourceNameOrIp", "81.56.112.95", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourceUserName", "-", res.get("sourceUserName").get(0));
    Assert.assertEquals("startTime", 1392051431025L, res.get("startTime").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Squid", res.get("externalLogSourceType").get(0));

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
