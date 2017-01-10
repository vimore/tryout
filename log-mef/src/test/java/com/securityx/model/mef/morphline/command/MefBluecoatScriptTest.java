package com.securityx.model.mef.morphline.command;

import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefBluecoatScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefBluecoatScriptTest.class);

  public MefBluecoatScriptTest() {
    super(MefBluecoatScriptTest.class.toString());
    this.morphlineId = "bluecoat";
    this.confFile = "logcollection-bluecoat.conf";
  }

  @Test
  public void test0() throws FileNotFoundException {
    String line = "2005-04-12 21:03:45 74603 192.16.170.46 503 TCP_ERR_MISS 1736 430 GET http www.yahoo.com / - - NONE 192.16.170.42 - \"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6) Gecko/20050317 Firefox/1.0.2\" DENIED none - 192.16.170.42 SG-HTTP-Service - server_unavailable \"Server unavailable: No ICAP server is available to process request.\"";
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
  }

  @Test
  public void testheader() throws FileNotFoundException {
    String line1 = "#Fields: date time time-taken c-ip sc-status s-action";
    String line2 = "2005-04-12 21:03:45 1 192.16.170.46 503 TCP_ERR_MISS";
    String[] data = {line1, line2};

    boolean result = doTest(buildRecord(line1));
    assertEquals(true, result);
    result = doTest(buildRecord(line2));
    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertEquals(1, this.outCommand.getNumRecords());
    Record r = this.outCommand.getRecord(0);
    Assert.assertEquals(1113339825000L,
            r.get("startTime").get(0));
    Assert.assertEquals("192.16.170.46",
            r.get("c-ip").get(0));
    
    line1 = "#Fields: date time cs-bytes cs-method cs-uri-scheme cs-host cs-uri-path cs-uri-query cs-username s-hierarchy s-supplier-name ";
    line2 = "2005-04-12 21:03:45 430 GET http www.yahoo.com / - - NONE 192.16.170.42 - ";
    String[] data2 = {line1, line2};
    result = doTest(buildRecord(line1));
    assertEquals(true, result);
    result = doTest(buildRecord(line2));
    assertEquals(true, result);
    Assert.assertEquals(2, this.outCommand.getNumRecords());
    r = this.outCommand.getRecord(1);
    Assert.assertEquals(1113339825000L,
            r.get("startTime").get(0));
    Assert.assertEquals("192.16.170.42",
            r.get("s-supplier-name").get(0));
    Assert.assertEquals("http://www.yahoo.com/?-",
            r.get("requestUrl").get(0));

  }
  private Record buildRecord(String input){
    Record r = new Record();
    r.put("logCollectionHost", "someHost");
    r.put("bluecoatInput", input);
    return r;
  }

  @Test
  public void test1() throws FileNotFoundException {
    // 
    String line = "2005-04-12 21:03:45 74603 192.16.170.46 503 TCP_ERR_MISS 1736 430 GET http www.yahoo.com / - - NONE 192.16.170.42 - \"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6) Gecko/20050317 Firefox/1.0.2\" DENIED none - 192.16.170.42 SG-HTTP-Service - server_unavailable \"Server unavailable: No ICAP server is available to process request.\"";
    
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record r = this.outCommand.getRecord(0);
    Assert.assertEquals("-, server_unavailable : Server unavailable: No ICAP server is available to process request.",
            r.get("reason").get(0));
    Assert.assertEquals(1113339825000L,
            r.get("startTime").get(0));
    Assert.assertEquals("192.16.170.46",
            r.get("c-ip").get(0));
    Assert.assertEquals("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6) Gecko/20050317 Firefox/1.0.2",
            r.get("cs(User-Agent)").get(0));
    Assert.assertEquals("430",
            r.get("cs-bytes").get(0));
    Assert.assertEquals("www.yahoo.com",
            r.get("cs-host").get(0));
    Assert.assertEquals("GET",
            r.get("cs-method").get(0));
    Assert.assertEquals("/",
            r.get("cs-uri-path").get(0));
    Assert.assertEquals("-",
            r.get("cs-uri-query").get(0));
    Assert.assertEquals("http",
            r.get("cs-uri-scheme").get(0));
    Assert.assertEquals("-",
            r.get("cs-username").get(0));
    Assert.assertEquals("2005-04-12",
            r.get("date").get(0));
    Assert.assertEquals("http://www.yahoo.com/?-",
            r.get("requestUrl").get(0));
    Assert.assertEquals("-",
            r.get("rs(Content-Type)").get(0));
    Assert.assertEquals("TCP_ERR_MISS",
            r.get("s-action").get(0));
    Assert.assertEquals("NONE",
            r.get("s-hierarchy").get(0));
    Assert.assertEquals("192.16.170.42",
            r.get("s-ip").get(0));
    Assert.assertEquals("SG-HTTP-Service",
            r.get("s-sitename").get(0));
    Assert.assertEquals("192.16.170.42",
            r.get("s-supplier-name").get(0));
    Assert.assertEquals("1736",
            r.get("sc-bytes").get(0));
    Assert.assertEquals("none",
            r.get("sc-filter-category").get(0));
    Assert.assertEquals("DENIED",
            r.get("sc-filter-result").get(0));
    Assert.assertEquals("503",
            r.get("sc-status").get(0));
    Assert.assertEquals("21:03:45",
            r.get("time").get(0));
    Assert.assertEquals("74603",
            r.get("time-taken").get(0));
    Assert.assertEquals("server_unavailable",
            r.get("x-icap-error-code").get(0));
    Assert.assertEquals("Server unavailable: No ICAP server is available to process request.",
            r.get("x-icap-error-details").get(0));
    Assert.assertEquals("-",
            r.get("x-virus-details").get(0));
    Assert.assertEquals("-",
            r.get("x-virus-id").get(0));

  }

}
