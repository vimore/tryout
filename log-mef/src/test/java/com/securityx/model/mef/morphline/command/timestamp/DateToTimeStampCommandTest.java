package com.securityx.model.mef.morphline.command.timestamp;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class DateToTimeStampCommandTest extends LogCollectionAbstractTest {
    private Logger logger = LoggerFactory.getLogger(DateToTimeStampCommandTest.class);
    public DateToTimeStampCommandTest() {
      super(DateToTimeStampCommandTest.class.toString());
      this.morphlineId = "morphline1";
      this.confFile = "test/test-date2timestamp-command.conf";
    }
    
        
    @Test
    public void test0() throws FileNotFoundException {
       String line = "1970-01-01 00:00:00"; //epoch 
       Record input =new Record();
       input.put("startTime", line);
       input.put("logCollectionTimeZone", "UTC");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
       Assert.assertEquals(0L, 
                        r.get("startTime").get(0));

    }
    
    @Test
    public void test1() throws FileNotFoundException {
       String line = "1970-01-01 00:00:00"; //epoch 
       Record input =new Record();
       input.put("startTime", line);
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
       Assert.assertEquals(0L, 
                        r.get("startTime").get(0));

    }
  
    @Test
    public void test2() throws FileNotFoundException {
       String line = "1970-01-01 01:00:00"; //epoch 
       Record input =new Record();
       input.put("startTime", line);
       input.put("logCollectionTimeZone", "GMT+1");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
       Assert.assertEquals(0L, 
                        r.get("startTime").get(0));

    }

    
    
        @Test
    public void test3() throws FileNotFoundException {
       String line = "2013-11-17 01:04:39"; //epoch 
       Record input =new Record();
       input.put("startTime", line);
       input.put("logCollectionTimeZone", "UTC");
       boolean result= doTest(input);
       assertEquals(true, result);
       Assert.assertTrue(this.outCommand.getNumRecords() == 1);
       Record r = this.outCommand.getRecord(0);
       Assert.assertEquals(1384650279000L, 
                        r.get("startTime").get(0));

    }

}