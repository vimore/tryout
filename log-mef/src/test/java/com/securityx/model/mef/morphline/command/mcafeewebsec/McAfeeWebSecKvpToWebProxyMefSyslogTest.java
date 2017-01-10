package com.securityx.model.mef.morphline.command.mcafeewebsec;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class McAfeeWebSecKvpToWebProxyMefSyslogTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(McAfeeWebSecKvpToWebProxyMefSyslogTest.class);

  public McAfeeWebSecKvpToWebProxyMefSyslogTest(String name) {
    super(name);
    this.morphlineId = "mcafeewebsec_selector";
    this.confFile = "webproxymef-mcafee_websecurity.conf";
  }

  /*
   2015-09-15T01:24:59-04:00 SSCwcgCONqa01 mwg: time_stamp=[15/Sep/2015:01:24:59 -0400] auth_user=- src_ip=172.24.45.106 server_ip=172.20.153.24 time_taken=46 status_code=200 cache_status=TCP_MISS req_line=CONNECT cpliqp0p.example.com:1270 HTTP/1.1 categories=Online Shopping rep_level=Minimal Risk media_type=application/x-empty bytes_to_client=5029 bytes_from_client=2396 user_agent=- referrer=- virus_name=- gam_probability=0 block_res=0 geolocation=- application_name=- md5=-
   */
  private Record buildRecord(String line) {
    Record input = new Record();
    input.put("websecMessage", line);
    input.put("logCollectionHost", "somehost");
    input.put("logCollectionType", "syslog");
    input.put("receiptTimeStr", "May 11 11:22:10");
    return input;
  }

  @Test
  public void test_WebsenseKvp() throws FileNotFoundException {
    String line = "mwg: time_stamp=[15/Sep/2015:01:24:59 -0400] auth_user=- src_ip=172.24.45.106 server_ip=172.20.153.24 time_taken=46 status_code=200 cache_status=TCP_MISS req_line=CONNECT cpliqp0p.example.com:1270 HTTP/1.1 categories=Online Shopping rep_level=Minimal Risk media_type=application/x-empty bytes_to_client=5029 bytes_from_client=2396 user_agent=- referrer=- virus_name=- gam_probability=0 block_res=0 geolocation=- application_name=- md5=-";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    OutUtils.printOut(res);
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
