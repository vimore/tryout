package com.securityx.model.mef.morphline.command;

import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class LogCollectionScriptSelectorTest extends LogCollectionAbstractTest{
  
  
    private Logger logger = LoggerFactory.getLogger(LogCollectionScriptSelectorTest.class);
    public LogCollectionScriptSelectorTest() {
      super(LogCollectionScriptSelectorTest.class.toString());
      this.morphlineId = "morphline1";
      this.confFile = "test/test-script-selector-command.conf";
    }

    
    @Test
    public void test() throws FileNotFoundException {
        String line = "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
        boolean result = doTest(line);
        OutUtils.printOut(outCommand.getNumRecords());
        Assert.assertTrue(outCommand.getNumRecords() == 1);
        Record r = outCommand.getRecord(0);
        assertTrue("morphline script match", result);
        //Assert.assertEquals(r.get("Foo"), "bar");
    }

}