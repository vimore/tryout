package com.securityx.model.mef.morphline.command;

import com.google.common.collect.ListMultimap;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class LogCollectionScriptSelectorPatternTest extends LogCollectionAbstractTest{
    
  public LogCollectionScriptSelectorPatternTest(){
      super(LogCollectionScriptSelectorPatternTest.class.toString());
      this.morphlineId = "logcollectionselector";
      this.confFile = "logcollection-script-selector-command-pattern.conf";
  }
    @Test
    public void test() throws FileNotFoundException {
        String  line = "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)";
        boolean result= doTest(line);
        assertTrue("line successfully processed", result);
        OutUtils.printOut(outCommand.getNumRecords());
        Assert.assertTrue(outCommand.getNumRecords() == 1);
        Record r = outCommand.getRecord(0);
        OutUtils.printOut("RESULT : "+r.toString());
        //Assert.assertEquals(r.get("Foo"), "bar");
    }

    
    @Test
    public void test1() throws FileNotFoundException {
        String  line = "Sep 23 10:14:45 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [23/Sep/2013:10:14:43 +0000] \"GET / HTTP/1.1\" 200 1494";
        boolean result= doTest(line); 
        assertTrue("line successfully processed", result);
        OutUtils.printOut(outCommand.getNumRecords());
        Assert.assertTrue(outCommand.getNumRecords() == 1);
        Record r = outCommand.getRecord(0);
        OutUtils.printOut(displayResult(r));
        //Assert.assertEquals(r.get("Foo"), "bar");
    }
    
    private String displayResult(Record r){
      String out=null;
      out="*****************************************************************\n";
      out+="RESULT \n";
    ListMultimap<String, Object> fields = r.getFields();
      for (String key :  fields.keys()) { 
        out+=key+ " : " + fields.get(key) + "+ \n" ;
      }
      return out;
    }

}