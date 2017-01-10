package com.securityx.model.mef.morphline.command.cef;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefCefKvpSplitterScriptTest extends LogCollectionAbstractTest {
    private Logger logger = LoggerFactory.getLogger(MefCefKvpSplitterScriptTest.class);
    public MefCefKvpSplitterScriptTest() {
      super(MefCefKvpSplitterScriptTest.class.toString());
      this.morphlineId = "morphline1";
      this.confFile = "test/test-cefkvpsplitter-command.conf";
    }
    
        
    @Test
    public void test_simple() throws FileNotFoundException {
       String line = "cs1Label=Compliancy_Policy_Name cs2Label=Compliancy_Policy_Subrule_Name cs3Label=Host_Compliancy_Status cs4Label=Compliancy_Event_Trigger cs1=Operating_System  cs2=Updatedcs3=yes cs4=CounterAct_Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
       boolean result= doTest(line);
       assertEquals(true, result);
    }
    
    @Test
    public void test_quote() throws FileNotFoundException {
       String line = "cs1Label='Compliancy Policy Name' cs2Label='Compliancy Policy Subrule Name' cs3Label='Host Compliancy Status' cs4Label='Compliancy Event Trigger' cs1='Operating System'  cs2=Updated cs3=yes cs4='CounterAct Action' dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
       boolean result= doTest(line);
       assertEquals(true, result);
    }

    @Test
    public void test_truelog() throws FileNotFoundException {
       String line = "cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Operating System  cs2=Updated cs3=yes cs4=CounterAct Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
       boolean result= doTest(line);
       assertEquals(true, result);
    }

    @Test
    public void test4() throws FileNotFoundException {
        // 
        String line = "cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Operating System  cs2=Updated cs3=yes cs4=CounterAct Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
       boolean result= doTest(line);
       assertEquals(true, result);
       OutUtils.printOut(this.outCommand.getNumRecords());
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
          Assert.assertEquals("Operating System", 
                            r.get("cs1").get(0));
          Assert.assertEquals("Compliancy Policy Name", 
                            r.get("cs1Label").get(0));
          Assert.assertEquals("Updated", 
                            r.get("cs2").get(0));
          Assert.assertEquals("Compliancy Policy Subrule Name", 
                            r.get("cs2Label").get(0));
          Assert.assertEquals("yes", 
                            r.get("cs3").get(0));
          Assert.assertEquals("Host Compliancy Status", 
                            r.get("cs3Label").get(0));
          Assert.assertEquals("CounterAct Action", 
                            r.get("cs4").get(0));
          Assert.assertEquals("wks-105", 
                            r.get("dhost").get(0));
          Assert.assertEquals("00:03:47:24:46:65", 
                            r.get("dmac").get(0));
          Assert.assertEquals("loglogicrocks.com", 
                            r.get("dntdom").get(0));
          Assert.assertEquals("10.10.1.4", 
                            r.get("dst").get(0));
          Assert.assertEquals("Kevin_Mitchell", 
                            r.get("duser").get(0));
          Assert.assertEquals("1.1.1.1", 
                            r.get("dvc").get(0));
          Assert.assertEquals("forescout-02", 
                            r.get("dvchost").get(0));
          Assert.assertEquals("1328814052000", 
                            r.get("rt").get(0));


    }
  

}