package com.securityx.model.mef.morphline.command;

import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefEpilogApacheScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefEpilogApacheScriptTest.class);

  public MefEpilogApacheScriptTest() {
    super(MefEpilogApacheScriptTest.class.toString());
    this.morphlineId = "epilog";
    this.confFile = "epilog-to-mef.conf";
  }

  @Test
  public void test0() throws FileNotFoundException {
    String line = null;//"Sep 20 13:40:23 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    line = "ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test1() throws FileNotFoundException {
    String line = null;//"Sep 20 13:40:23 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    line = "ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test2() throws FileNotFoundException {
    String line = null;//"Sep 20 13:40:23 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    line = "ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  @Test
  public void test3() throws FileNotFoundException {
    String line = null;//"Sep 20 13:40:23 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    line = "ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
  }

  //@Test
  public void test4() throws FileNotFoundException {
    String line = "Sep 20 13:40:23 WIN-AGUQJ5MTTKO ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    line = "ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494";
    Record r = new Record();
    r.put("syslogMessage", line);
    boolean result = doTest(r);

    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494",
            res.get("epilogMessage").get(0));
    Assert.assertEquals("ApacheLog",
            res.get("epilogType").get(0));
    Assert.assertEquals("ApacheLog	0	127.0.0.1 - - [20/Sep/2013:13:40:21 +0000] \"GET / HTTP/1.1\" 200 1494",
            res.get("syslogMessage").get(0));
  }

}
