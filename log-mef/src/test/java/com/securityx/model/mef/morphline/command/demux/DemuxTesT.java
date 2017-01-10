package com.securityx.model.mef.morphline.command.demux;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class DemuxTesT extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(DemuxTesT.class);

  public DemuxTesT(String name) {
    super(name);
    this.morphlineId = "morphline1";
    this.confFile = "test/test-demux-command.conf";
  }

  /*
   <record>
 <firstName>Joe</firstName>
 <lastName>Bubblegum</lastName>
</record>

<record>
 <firstName>Alice</firstName>
 <lastName>Pellegrino</lastName>
</record>

   */
  private Record buildRecord(String line) {
    Record input = new Record();
    input.put("splitable", line);
    return input;
  }

  @Test
  public void test_SimpleTest() throws FileNotFoundException {
    String line = "a, b, c";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    int nbres = this.outCommand.getNumRecords();
    Assert.assertEquals(3, nbres);
    for (int i = 0; i < nbres; i++) {
      Record res = this.outCommand.getRecord(i);
      OutUtils.printOut(res);
      switch (i) {
        case 0:
          Assert.assertEquals("a", res.get("split").get(0));
          break;
        case 1:
          Assert.assertEquals("b", res.get("split").get(0));
          break;
        case 2:
          Assert.assertEquals("c", res.get("split").get(0));
          break;
        default:
      }

    }

  }
  @Test
  public void test_SimpleTest2() throws Exception {
    this.morphlineId = "morphline2";
    this.setUp();
    String line ="a, b,  c";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    int nbres= this.outCommand.getNumRecords();
    Assert.assertEquals(3, nbres);
    for (int i=0;i<nbres;i++){
      Record res = this.outCommand.getRecord(i);
      OutUtils.printOut(res);
      switch(i) {
        case 0:
          Assert.assertEquals("a", res.get("split").get(0));
          break;
        case 1:
          Assert.assertEquals("b", res.get("split").get(0));
          break;
        case 2:
          Assert.assertEquals("c", res.get("split").get(0));
          break;
        default:
      }

    }


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
