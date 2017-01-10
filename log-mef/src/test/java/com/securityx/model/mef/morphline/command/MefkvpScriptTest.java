package com.securityx.model.mef.morphline.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefkvpScriptTest extends LogCollectionAbstractTest {
    private Logger logger = LoggerFactory.getLogger(MefkvpScriptTest.class);
    public MefkvpScriptTest() {
      super(MefkvpScriptTest.class.toString());
      this.morphlineId = "morphline1";
      this.confFile = "test/test-kvp-command.conf";
    }
    
        
    @Test
    public void test_simple() throws FileNotFoundException {
       String line = "cs1Label=Compliancy_Policy_Name cs2Label=Compliancy_Policy_Subrule_Name cs3Label=Host_Compliancy_Status cs4Label=Compliancy_Event_Trigger cs1=Operating_System  cs2=Updatedcs3=yes cs4=CounterAct_Action dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
       boolean result= doTest(line);
       assertEquals(true, result);
    }
    /*@Test
    public void test_perf() throws FileNotFoundException {

        String[] input = new String[10000];
        String line = "cs1Label='Compliancy Policy Name' cs2Label='Compliancy Policy Subrule Name' cs3Label='Host Compliancy Status' cs4Label='Compliancy Event Trigger' cs1='Operating System'  cs2=Updated cs3=yes cs4='CounterAct Action' dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
        for (int i = 0; i < input.length; i++) {
            input[i] = line;
        }
        boolean result = doTest(input);
        assertEquals(true, result);

    } */

    
    @Test
    public void test_quote() throws FileNotFoundException {
       String line = "cs1Label='Compliancy Policy Name' cs2Label='Compliancy Policy Subrule Name' cs3Label='Host Compliancy Status' cs4Label='Compliancy Event Trigger' cs1='Operating System'  cs2=Updated cs3=yes cs4='CounterAct Action' dmac=00:03:47:24:46:65 dst=10.10.1.4 dntdom=loglogicrocks.com dhost=wks-105 duser=Kevin_Mitchell dvc=1.1.1.1 dvchost=forescout-02 rt=1328814052000";
       boolean result= doTest(line);
       assertEquals(true, result);
    }

}