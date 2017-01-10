package com.securityx.model.mef.morphline.command.field.aggregator;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class FieldAggregatorCommandTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(FieldAggregatorCommandTest.class);

  public FieldAggregatorCommandTest() {
    super(FieldAggregatorCommandTest.class.toString());
    this.morphlineId = "morphline1";
    this.confFile = "test/test-fieldaggregator-command.conf";
  }


  @Test
  public void test0() throws FileNotFoundException {
    String v1 = "toto";
    String v2 = "titi";
    String v3 = "blabla";
    String v4 = "blibli";
    Record input = new Record();
    input.put("sub_1", v1);
    input.put("sub_2", v2);
    input.put("will", v3);
    input.put("match", v4);
    boolean result = doTest(input);
    assertEquals(true, result);
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record r = this.outCommand.getRecord(0);
    Assert.assertEquals("toto/titi",
            r.get("field1").get(0));
    Assert.assertEquals("blabla - blibli",
            r.get("field2").get(0));
  }

}
