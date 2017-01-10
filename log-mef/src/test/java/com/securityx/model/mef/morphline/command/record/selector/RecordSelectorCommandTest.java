package com.securityx.model.mef.morphline.command.record.selector;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.timestamp.DateToTimeStampCommandTest;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class RecordSelectorCommandTest extends LogCollectionAbstractTest {
    private Logger logger = LoggerFactory.getLogger(DateToTimeStampCommandTest.class);
    public RecordSelectorCommandTest() {
      super(DateToTimeStampCommandTest.class.toString());
      this.morphlineId = "morphline1";
      this.confFile = "test/test-recordselector-command.conf";
    }
    
        
    @Test
    public void testFieldPatern0() throws FileNotFoundException {
       Record input =new Record();
       input.put("action", "accept");
       input.put("store", "false");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
    }
    @Test
    public void testFieldPatern1() throws FileNotFoundException, IOException {
       Record input =new Record();
       input.put("action", "accept");
       input.put("store", "true");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
       this.morphlineHarness.shutdown(); //forcing flush
       //assertEquals(r.toString(), checkFile("./target/actionaccept-storetrue.txt"));
    }
    @Test
    public void testFieldPatern2()  {
       Record input =new Record();
       input.put("action", "discard");
       input.put("store", "false");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 0);
    }
    @Test
    public void testFieldPatern3() throws FileNotFoundException, IOException {
       Record input =new Record();
       input.put("action", "discard");
       input.put("store", "true");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 0);
       this.morphlineHarness.shutdown(); //forcing flush
       //assertEquals(input.toString(), checkFile("./target/actiondiscard-storetrue.txt"));
    }
    @Test
    public void testField1() {
       Record input =new Record();
       input.put("action", "bla");
       input.put("store", "bla");
       boolean result= doTest(input);
       assertEquals(true, result);
       //filtered by rule expecting field action
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    }
    @Test
    public void testField2()  {
       Record input =new Record();
       input.put("action", "bla");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 0);
    }
    @Test
    public void testField3()  {
       Record input =new Record();
       input.put("bla", "b");
       input.put("bla", "b");
       boolean result= doTest(input);
       assertEquals(true, result);
       //filtered by rule expecting field action
       Assert.assertTrue(this.outCommand.getNumRecords() == 0);
    }

}