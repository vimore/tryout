package com.securityx.model.mef.morphline.command.websense;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class WebsenseKvpToWebProxyMefSyslogTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(WebsenseKvpToWebProxyMefSyslogTest.class);

  public WebsenseKvpToWebProxyMefSyslogTest(String name) {
    super(name);
    this.morphlineId = "websense_kvp";
    this.confFile = "webproxymef-websense-kvp.conf";
  }

  /*
   May 22 11:22:10 172.19.1.11 vendor=Websense product=Security product_version=7.7.0 action=permitted severity=1 category=76 user=WinNT://TRITON77/username src_host=172.19.1.133 src_port=55863 dst_host=www.google.com dst_ip=74.125.71.103 dst_port=80 bytes_out=635 bytes_in=25128 http_response=200 http_method=GET http_content_type=text/html;_charset=UTF-8 http_user_agent=Mozilla/5.0_(X11;_U;_Linux_x86_64;_en-US;_rv:1.9.2.9)_Gecko/20110412_CentOS/3.6.9-2.el6.centos_Firefox/3.6.9 http_proxy_status_code=200 reason=- disposition=1026 policy=role-8**Default role=8 duration=0 url=http://www.google.com/
   */
  private Record buildRecord(String line) {
    Record input = new Record();
    input.put("websenseMessage", line);
    input.put("logCollectionHost", "somehost");
    input.put("logCollectionType", "syslog");
    input.put("receiptTimeStr", "May 11 11:22:10");
    return input;
  }

  @Test
  public void test_WebsenseKvp() throws FileNotFoundException {
    String line = "vendor=Websense product=Security product_version=7.7.0 action=permitted severity=1 category=76 user=WinNT://TRITON77/username src_host=172.19.1.133 src_port=55863 dst_host=www.google.com dst_ip=74.125.71.103 dst_port=80 bytes_out=635 bytes_in=25128 http_response=200 http_method=GET http_content_type=text/html;_charset=UTF-8 http_user_agent=Mozilla/5.0_(X11;_U;_Linux_x86_64;_en-US;_rv:1.9.2.9)_Gecko/20110412_CentOS/3.6.9-2.el6.centos_Firefox/3.6.9 http_proxy_status_code=200 reason=- disposition=1026 policy=role-8**Default role=8 duration=0 url=http://www.google.com/";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    assertEquals("bytesIn", 25128, res.get("bytesIn").get(0));
    assertEquals("bytesOut", 635, res.get("bytesOut").get(0));
    assertEquals("requestClientApplication", "Mozilla/5.0_(X11;_U;_Linux_x86_64;_en-US;_rv:1.9.2.9)_Gecko/20110412_CentOS/3.6.9-2.el6.centos_Firefox/3.6.9", res.get("requestClientApplication").get(0));
    assertEquals("destinationAddress", "74.125.71.103", res.get("destinationAddress").get(0));
    assertEquals("destinationNameOrIp", "www.google.com", res.get("destinationNameOrIp").get(0));
    assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
    assertEquals("deviceAction", "permitted", res.get("deviceAction").get(0));
    assertEquals("deviceEventCategory", "76", res.get("deviceEventCategory").get(0));
    assertEquals("devicePolicyAction", "role-8**Default", res.get("devicePolicyAction").get(0));
    assertEquals("logCollectionHost", "somehost", res.get("logCollectionHost").get(0));
    assertEquals("logCollectionType", "syslog", res.get("logCollectionType").get(0));
    assertEquals("startTime", 1462965730000L, res.get("startTime").get(0));
    assertEquals("requestHeaderHostField", "www.google.com", res.get("requestHeaderHostField").get(0));
    assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    assertEquals("requestPath", "/", res.get("requestPath").get(0));
    assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    assertEquals("responseContentType", "text/html;_charset=UTF-8", res.get("responseContentType").get(0));
    assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
    assertEquals("sourceNameOrIp", "172.19.1.133", res.get("sourceNameOrIp").get(0));
    assertEquals("sourcePort", 55863, res.get("sourcePort").get(0));
    assertEquals("sourceUserName", "WinNT://TRITON77/username", res.get("sourceUserName").get(0));
    assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    assertEquals("externalLogSourceType", "websense", res.get("externalLogSourceType").get(0));
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
}
