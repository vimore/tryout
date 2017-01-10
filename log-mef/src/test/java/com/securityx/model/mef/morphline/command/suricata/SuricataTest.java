package com.securityx.model.mef.morphline.command.suricata;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.MefBluecoatScriptTest;
import com.securityx.utils.OutUtils;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class SuricataTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefBluecoatScriptTest.class);

  public SuricataTest(String name) {
    super(name);
    this.morphlineId = "suricata";
    this.confFile = "suricata_to_mef.conf";
  }


  private Record buildRecord(String line){
    Record r = new Record();
    r.put("logCollectionHost", "someHost");
    r.put("suricataLine", line);
    return r;

  }
@Test
public void testSuricata() throws FileNotFoundException {
   String line = "02/26/2015-19:12:23.858548  [**] [1:10808080:1] ZeuS RAT default URI [**] [Classification: (null)] [Priority: 3] {TCP} 192.168.1.94:1051 -> 64.50.186.17:80";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Record out = this.outCommand.getRecord(0);
   OutUtils.printOut("Record: " + out);
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
//        boolean result = doTest(buildRecord(line));
//        if (!result) {
//          OutUtils.printOut("DO NOT MATCH :" + line);
//        }
//      } // end while
//      OutUtils.printOut("parsed : " + i + " lines");
//
////      String line = "1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
////      boolean result = doTest(buildRecord(line));
////      assertEquals(true, result);
////      Record res = this.outCommand.getRecord(0);
//    } catch (IOException ex) {
//      java.util.logging.Logger.getLogger(WebProxySquidTest.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
}
