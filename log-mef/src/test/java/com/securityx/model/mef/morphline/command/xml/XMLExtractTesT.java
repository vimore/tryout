package com.securityx.model.mef.morphline.command.xml;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class XMLExtractTesT extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(XMLExtractTesT.class);

  public XMLExtractTesT(String name) {
    super(name);
    this.morphlineId = "morphline1";
    this.confFile = "test/test-xquery-command.conf";
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
    InputStream in = new ByteArrayInputStream(line.getBytes());
    input.put(Fields.ATTACHMENT_BODY, in);
    return input;
  }

  @Test
  public void test_XMLExtractTest() throws FileNotFoundException {
    String line ="<result_sets><now>2015/05/20 12:57:05 GMT-0000</now>\n<result_set><age>0</age><archived_question_id>0</archived_question_id><saved_question_id>233</saved_question_id><question_id>163409</question_id><report_count>2</report_count><seconds_since_issued>57</seconds_since_issued><issue_seconds>120</issue_seconds><expire_seconds>600</expire_seconds><tested>2</tested><passed>2</passed><mr_tested>2</mr_tested><mr_passed>2</mr_passed><estimated_total>2</estimated_total><select_count>1</select_count><cs><c><wh>98976143</wh><dn>Hostname</dn><rt>1</rt></c><c><wh>98976143</wh><dn>CPU Consumption</dn><rt>1</rt></c><c><wh>0</wh><dn>Count</dn><rt>3</rt></c></cs><filtered_row_count>2</filtered_row_count><filtered_row_count_machines>2</filtered_row_count_machines><item_count>2</item_count><rs><r><id>1920312274</id><cid>0</cid><c><v>RDP-GW</v></c><c><v>1 %</v></c><c><v>1</v></c></r><r><id>3355441105</id><cid>0</cid><c><v>WIN-OSNMCI3GJJ1</v></c><c><v>2 %</v></c><c><v>1</v></c></r></rs></result_set></result_sets>\n\n";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    int nbres= this.outCommand.getNumRecords();
    for (int i=0;i<nbres;i++){
      Record res = this.outCommand.getRecord(i);
      OutUtils.printOut(res);
      switch(i) {
        case 0:
          Assert.assertEquals("RDP-GW", res.get("hostname").get(0));
          Assert.assertEquals("1 %", res.get("value").get(0));
          break;
        case 1:
          Assert.assertEquals("WIN-OSNMCI3GJJ1", res.get("hostname").get(0));
          Assert.assertEquals("2 %", res.get("value").get(0));
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
