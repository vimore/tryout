package com.securityx.model.mef.morphline.command.dpi;


import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.model.mef.morphline.command.MefBluecoatScriptTest;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

public class WebProxyDpiHttpTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(MefBluecoatScriptTest.class);
  private MorphlineHarness morphlineDpi2Json;
  private AssertRecordOutCommand dpi2JsonOutCommand;
  
  public WebProxyDpiHttpTest(String name) throws Exception {
    super(name);
        this.dpi2JsonOutCommand = new AssertRecordOutCommand(morphlineContext);
    Config conf = MorphlineResourceLoader.getConfFile("dpi-json.conf");
    assertTrue("conf file " + conf.origin(),conf.origin().toString().contains("dpi-json"));
    this.morphlineDpi2Json = new MorphlineHarness(morphlineContext, conf, "dpi-json");
    this.morphlineDpi2Json.startup(dpi2JsonOutCommand);
    this.morphlineId = "dpi-http";
    this.confFile = "dpi-http.conf";
  }
    private Record buildRecord(String line){
    Record input = new Record();
    input.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(line.getBytes()));
    input.put(Fields.ATTACHMENT_MIME_TYPE, "application/json");
    boolean result = this.morphlineDpi2Json.feedRecords(input);
    assertEquals(true, result);
    OutUtils.printOut(this.dpi2JsonOutCommand.getNumRecords());
    Assert.assertTrue(this.dpi2JsonOutCommand.getNumRecords() == 1);
    Record out = this.dpi2JsonOutCommand.getRecord(0);
    this.dpi2JsonOutCommand.clear();
    return out;
  }


  @Test
  public void test_httpDpi() throws FileNotFoundException {
    String line = "{\"data\":{\"startTime\":\"1359931603.14201\",\"requestClientApplication\":\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\",\"requestContentLength\":0,\"requestHost\":\"google.com\",\"requestMethod\":\"GET\",\"requestParams\":[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}],\"requestQuery\":\"/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB\",\"requestVersion\":\"1.1\",\"responseContentLength\":3826,\"responseContentType\":\"text/html\",\"responseServer\":\"Microsoft-IIS/5.0\",\"responseStatus\":\"200\"},\"dpiSignatureId\":\"URL request\",\"net\":{\"application\":\"http\",\"destNameOrIp\":\"51.78.4.1\",\"destPort\":80,\"dpiFlowId\":\"1893479\",\"sourceNameOrIp\":\"192.168.1.1\",\"sourcePort\":4190,\"transportProtocol\":\"tcp\"}}";
    Record input = buildRecord(line);     
    boolean result = doTest(input);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("startTime", 1359931603142L, res.get("startTime").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("destinationNameOrIp", "google.com", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
    Assert.assertEquals("dpiFlowId", "1893479", res.get("dpiFlowId").get(0));
    Assert.assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
    Assert.assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)", res.get("requestClientApplication").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestQuery", "/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB", res.get("requestQuery").get(0));
    Assert.assertEquals("bytesIn", 0, res.get("bytesIn").get(0));
    Assert.assertEquals("bytesOut", 3826, res.get("bytesOut").get(0));
    Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
    Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
    Assert.assertEquals("sourceNameOrIp", "192.168.1.1", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourcePort", 4190, res.get("sourcePort").get(0));
    Assert.assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));

  }

    @Test
    public void test_httpDpiStartTimeAsLong() throws FileNotFoundException {
        String line = "{\"data\":{\"startTime\":1463503245000,\"requestClientApplication\":\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\",\"requestContentLength\":0,\"requestHost\":\"google.com\",\"requestMethod\":\"GET\",\"requestParams\":[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}],\"requestQuery\":\"/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB\",\"requestVersion\":\"1.1\",\"responseContentLength\":3826,\"responseContentType\":\"text/html\",\"responseServer\":\"Microsoft-IIS/5.0\",\"responseStatus\":\"200\"},\"dpiSignatureId\":\"URL request\",\"net\":{\"application\":\"http\",\"destNameOrIp\":\"51.78.4.1\",\"destPort\":80,\"dpiFlowId\":\"1893479\",\"sourceNameOrIp\":\"192.168.1.1\",\"sourcePort\":4190,\"transportProtocol\":\"tcp\"}}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        Record res = this.outCommand.getRecord(0);
        Assert.assertEquals("startTime", 1463503245000L, res.get("startTime").get(0));
        Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
        Assert.assertEquals("destinationNameOrIp", "google.com", res.get("destinationNameOrIp").get(0));
        Assert.assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
        Assert.assertEquals("dpiFlowId", "1893479", res.get("dpiFlowId").get(0));
        Assert.assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
        Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
        Assert.assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
        Assert.assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)", res.get("requestClientApplication").get(0));
        Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
        Assert.assertEquals("requestQuery", "/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB", res.get("requestQuery").get(0));
        Assert.assertEquals("bytesIn", 0, res.get("bytesIn").get(0));
        Assert.assertEquals("bytesOut", 3826, res.get("bytesOut").get(0));
        Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
        Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
        Assert.assertEquals("sourceNameOrIp", "192.168.1.1", res.get("sourceNameOrIp").get(0));
        Assert.assertEquals("sourcePort", 4190, res.get("sourcePort").get(0));
        Assert.assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));

    }

    @Test
    public void test_httpDpiWrongTimestamp() throws FileNotFoundException {
        String line = "{\"data\":{\"startTime\":\"1359931603.14201\",\"requestClientApplication\":\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\",\"requestContentLength\":0,\"requestHost\":\"google.com\",\"requestMethod\":\"GET\",\"requestParams\":[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}],\"requestQuery\":\"/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB\",\"requestVersion\":\"1.1\",\"responseContentLength\":3826,\"responseContentType\":\"text/html\",\"responseServer\":\"Microsoft-IIS/5.0\",\"responseStatus\":\"200\"},\"dpiSignatureId\":\"URL request\",\"net\":{\"application\":\"http\",\"destNameOrIp\":\"51.78.4.1\",\"destPort\":80,\"dpiFlowId\":\"1893479\",\"sourceNameOrIp\":\"192.168.1.1\",\"sourcePort\":4190,\"transportProtocol\":\"tcp\"}}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        line = "{\"data\":{\"startTime\":\"1359.14201\",\"requestClientApplication\":\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\",\"requestContentLength\":0,\"requestHost\":\"google.com\",\"requestMethod\":\"GET\",\"requestParams\":[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}],\"requestQuery\":\"/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB\",\"requestVersion\":\"1.1\",\"responseContentLength\":3826,\"responseContentType\":\"text/html\",\"responseServer\":\"Microsoft-IIS/5.0\",\"responseStatus\":\"200\"},\"dpiSignatureId\":\"URL request\",\"net\":{\"application\":\"http\",\"destNameOrIp\":\"51.78.4.1\",\"destPort\":80,\"dpiFlowId\":\"1893479\",\"sourceNameOrIp\":\"192.168.1.1\",\"sourcePort\":4190,\"transportProtocol\":\"tcp\"}}";
        input = buildRecord(line);
        assertEquals(true, result);
        result = doTest(input);
        assertEquals(true, result);
        Record res = this.outCommand.getRecord(0);
        Assert.assertEquals("startTime", 1359931603142L, res.get("startTime").get(0));
        Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
        Assert.assertEquals("destinationNameOrIp", "google.com", res.get("destinationNameOrIp").get(0));
        Assert.assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
        Assert.assertEquals("dpiFlowId", "1893479", res.get("dpiFlowId").get(0));
        Assert.assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
        Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
        Assert.assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
        Assert.assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)", res.get("requestClientApplication").get(0));
        Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
        Assert.assertEquals("requestQuery", "/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB", res.get("requestQuery").get(0));
        Assert.assertEquals("bytesIn", 0, res.get("bytesIn").get(0));
        Assert.assertEquals("bytesOut", 3826, res.get("bytesOut").get(0));
        Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
        Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
        Assert.assertEquals("sourceNameOrIp", "192.168.1.1", res.get("sourceNameOrIp").get(0));
        Assert.assertEquals("sourcePort", 4190, res.get("sourcePort").get(0));
        Assert.assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));
        res = this.outCommand.getRecord(1);
        Assert.assertEquals("startTime", 1359931603142L, res.get("startTime").get(0));
        Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
        Assert.assertEquals("destinationNameOrIp", "google.com", res.get("destinationNameOrIp").get(0));
        Assert.assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
        Assert.assertEquals("dpiFlowId", "1893479", res.get("dpiFlowId").get(0));
        Assert.assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
        Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
        Assert.assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
        Assert.assertEquals("requestClientApplication", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)", res.get("requestClientApplication").get(0));
        Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
        Assert.assertEquals("requestQuery", "/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB", res.get("requestQuery").get(0));
        Assert.assertEquals("bytesIn", 0, res.get("bytesIn").get(0));
        Assert.assertEquals("bytesOut", 3826, res.get("bytesOut").get(0));
        Assert.assertEquals("responseContentType", "text/html", res.get("responseContentType").get(0));
        Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
        Assert.assertEquals("sourceNameOrIp", "192.168.1.1", res.get("sourceNameOrIp").get(0));
        Assert.assertEquals("sourcePort", 4190, res.get("sourcePort").get(0));
        Assert.assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));
    }


    //{\"data\": {\"startTime\": \"1775.680000\", \"transaction_id\": \"34432\", \"reply_code\": \"No such name\", \"rtt\": \"0.001\", \"query\": \"Zch68fil01.am.mot-mobility.com\", \"query_type\": \"Host address\", \"name\": \"am.mot-mobility.com\", \"ttl\": \"93\", \"host\": \"de01mdnsp1.am.mot-mobility.com\", \"host_type\": \"primary name server\"}, \"net\": {\"destNameOrIp\": \"10.45.2.90\", \"sourceNameOrIp\": \"100.64.21.50\", \"sourcePort\": \"53\", \"destPort\": \"52400\", \"transportProtocol\": \"udp\", \"timestamp\": 1463763241, \"application\": \"dns\", \"dpiFlowId\": \"6439504\"}, \"dpiSignatureId\": \"DNS response\"}
    @Test
    public void test_httpDpi2_() throws FileNotFoundException {
        String line = "{\"data\":{\"startTime\":\"1359931603.14201\",\"requestClientApplication\":\"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)\",\"requestContentLength\":0,\"requestHost\":\"google.com\",\"requestMethod\":\"GET\",\"requestParams\":[{\"name\":\"ly\",\"value\":\"khcijg111D308CB9EB\"},{\"name\":\"xtrns\",\"value\":\"khcijg111D308CB9EB\"}],\"requestQuery\":\"/page.jsp?ly=khcijg111D308CB9EB&xtrns=khcijg111D308CB9EB\",\"requestVersion\":\"1.1\",\"responseContentLength\":3826,\"responseContentType\":\"text/html\",\"responseServer\":\"Microsoft-IIS/5.0\",\"responseStatus\":\"200\"},\"dpiSignatureId\":\"URL request\",\"net\":{\"application\":\"http\",\"destNameOrIp\":\"51.78.4.1\",\"destPort\":80,\"dpiFlowId\":\"1893479\",\"sourceNameOrIp\":\"192.168.1.1\",\"sourcePort\":4190,\"transportProtocol\":\"tcp\"}}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
         line ="{\"data\": {\"startTime\": \"1412.680000\", \"transaction_id\": \"34432\", \"reply_code\": \"No such name\", \"rtt\": \"0.001\", \"query\": \"Zch68fil01.am.mot-mobility.com\", \"query_type\": \"Host address\", \"name\": \"am.mot-mobility.com\", \"ttl\": \"93\", \"host\": \"de01mdnsp1.am.mot-mobility.com\", \"host_type\": \"primary name server\"}, \"net\": {\"destNameOrIp\": \"10.45.2.90\", \"sourceNameOrIp\": \"100.64.21.50\", \"sourcePort\": \"53\", \"destPort\": \"52400\", \"transportProtocol\": \"udp\", \"timestamp\": 1463763241, \"application\": \"dns\", \"dpiFlowId\": \"6439504\"}, \"dpiSignatureId\": \"DNS response\"}";
         input = buildRecord(line);
         result = doTest(input);
        assertEquals(true, result);
        Record res = this.outCommand.getRecord(0);
        OutUtils.printOut(res);

    }

        @Test
  public void test_httpDpi2() throws FileNotFoundException {
    String line = "{\"data\": {\"requestMethod\": \"GET\", \"startTime\": \"1412894012.55064\", \"requestPath\": \"/activeupdate/pattern/vsapi947.zip\", \"requestVersion\": \"1.0\", \"requestHost\": \"housecall-p.activeupdate.trendmicro.com:80\", \"requestClientApplication\": \"Mozilla/4.0 (compatible;MSIE 5.0; Windows 98)\", \"responseContentLength\": \"4462022\", \"responseStatus\": \"200\", \"responseServer\": \"Apache\", \"responseContentType\": \"application/zip\"}, \"net\": {\"destNameOrIp\": \"63.236.1.138\", \"sourceNameOrIp\": \"192.168.3.183\", \"sourcePort\": \"3121\", \"destPort\": \"80\", \"transportProtocol\": \"tcp\", \"timestamp\": 1412890727, \"application\": \"http\", \"dpiFlowId\": \"4454338\"}, \"dpiSignatureId\": \"URL request\"}";
    Record input = buildRecord(line);     
    boolean result = doTest(input);
    assertEquals(true, result);
    Record res = this.outCommand.getRecord(0);
    Assert.assertEquals("startTime", 1412894012550L, res.get("startTime").get(0));
    Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
    Assert.assertEquals("destinationNameOrIp", "housecall-p.activeupdate.trendmicro.com", res.get("destinationNameOrIp").get(0));
    Assert.assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
    Assert.assertEquals("dpiFlowId", "4454338", res.get("dpiFlowId").get(0));
    Assert.assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
    Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
    Assert.assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
    Assert.assertEquals("requestClientApplication", "Mozilla/4.0 (compatible;MSIE 5.0; Windows 98)", res.get("requestClientApplication").get(0));
    Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
    Assert.assertEquals("requestPath", "/activeupdate/pattern/vsapi947.zip", res.get("requestPath").get(0));
    Assert.assertEquals("bytesOut", 4462022, res.get("bytesOut").get(0));
    Assert.assertEquals("responseContentType", "application/zip", res.get("responseContentType").get(0));
    Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
    Assert.assertEquals("sourceNameOrIp", "192.168.3.183", res.get("sourceNameOrIp").get(0));
    Assert.assertEquals("sourcePort", 3121, res.get("sourcePort").get(0));
    Assert.assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));

  }

    @Test
    public void test_httpDpiBadValue() throws FileNotFoundException {
        String line = "{\"data\": {\"requestMethod\": \"GET\", \"startTime\": \"1412894012.55064\", \"requestPath\": \"/activeupdate/pattern/vsapi947.zip\", \"requestVersion\": \"1.0\", \"requestHost\": \"housecall-p.active\\tupdate.trendmicro.com:80\", \"requestClientApplication\": \"Mozilla/4.0 (compatible;MSIE 5.0; Windows 98)\", \"responseContentLength\": \"4462022\", \"responseStatus\": \"200\", \"responseServer\": \"Apache\", \"responseContentType\": \"application/zip\"}, \"net\": {\"destNameOrIp\": \"63.236.1.138\", \"sourceNameOrIp\": \"192.168.3.183\", \"sourcePort\": \"3121\", \"destPort\": \"80\", \"transportProtocol\": \"tcp\", \"timestamp\": 1412890727, \"application\": \"http\", \"dpiFlowId\": \"4454338\"}, \"dpiSignatureId\": \"URL request\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        Record res = this.outCommand.getRecord(0);
        Assert.assertEquals("startTime", 1412894012550L, res.get("startTime").get(0));
        Assert.assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
        Assert.assertEquals("destinationNameOrIp", "housecall-p.active\\tupdate.trendmicro.com", res.get("destinationNameOrIp").get(0));
        Assert.assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
        Assert.assertEquals("dpiFlowId", "4454338", res.get("dpiFlowId").get(0));
        Assert.assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
        Assert.assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
        Assert.assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
        Assert.assertEquals("requestClientApplication", "Mozilla/4.0 (compatible;MSIE 5.0; Windows 98)", res.get("requestClientApplication").get(0));
        Assert.assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
        Assert.assertEquals("requestPath", "/activeupdate/pattern/vsapi947.zip", res.get("requestPath").get(0));
        Assert.assertEquals("bytesOut", 4462022, res.get("bytesOut").get(0));
        Assert.assertEquals("responseContentType", "application/zip", res.get("responseContentType").get(0));
        Assert.assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
        Assert.assertEquals("sourceNameOrIp", "192.168.3.183", res.get("sourceNameOrIp").get(0));
        Assert.assertEquals("sourcePort", 3121, res.get("sourcePort").get(0));
        Assert.assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));

    }

    @Test
    public void testDnsUrlRequestFailing() throws FileNotFoundException {
        //
        String line = "{\"data\": {\"requestMethod\": \"POST\", \"startTime\": \"1426009777.838395\", \"requestPath\": \"/x2\", \"requestParams\": [{\"name\": \"v\", \"value\": \"1\"}, {\"name\": \"c\", \"value\": \"6a4d7b1004e395a492f220abc4a80abc\"}, {\"name\": \"spid\", \"value\": \"mcafeemac2.0\"}, {\"name\": \"o\", \"value\": \"1\"}], \"requestVersion\": \"1.0\", \"requestHost\": \"lookup.sa-live.com\", \"requestClientApplication\": \"WebKitPluginHost/10600.2.1 CFNetwork/720.1.1 Darwin/14.0.0 (x86_64)\", \"responseStatus\": \"200\", \"responseContentLength\": \"129\", \"requestContentLength\": \"272\"}, \"net\": {\"destNameOrIp\": \"161.69.13.44\", \"sourceNameOrIp\": \"192.168.1.95\", \"sourcePort\": \"49728\", \"destPort\": \"80\", \"transportProtocol\": \"tcp\", \"timestamp\": 1426009777, \"application\": \"http\", \"dpiFlowId\": \"109816\"}, \"dpiSignatureId\": \"URL request\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);

        Record res = this.outCommand.getRecord(0);
        OutUtils.printOut("record: " + res.toString());
        assertEquals("bytesIn", 272, res.get("bytesIn").get(0));
assertEquals("bytesOut", 129, res.get("bytesOut").get(0));
assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
assertEquals("destinationDnsDomain", "sa-live.com", res.get("destinationDnsDomain").get(0));
assertEquals("destinationHostName", "lookup", res.get("destinationHostName").get(0));
        assertEquals("destinationDnsDomain", "sa-live.com", res.get("destinationDnsDomain").get(0));
assertEquals("destinationNameOrIp", "lookup.sa-live.com", res.get("destinationNameOrIp").get(0));
assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
assertEquals("dpiFlowId", "109816", res.get("dpiFlowId").get(0));
assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
assertEquals("requestClientApplication", "WebKitPluginHost/10600.2.1 CFNetwork/720.1.1 Darwin/14.0.0 (x86_64)", res.get("requestClientApplication").get(0));
assertEquals("requestHost", "lookup.sa-live.com", res.get("requestHost").get(0));
assertEquals("requestMethod", "POST", res.get("requestMethod").get(0));
assertEquals("requestParams", "[{\"name\":\"v\",\"value\":\"1\"},{\"name\":\"c\",\"value\":\"6a4d7b1004e395a492f220abc4a80abc\"},{\"name\":\"spid\",\"value\":\"mcafeemac2.0\"},{\"name\":\"o\",\"value\":\"1\"}]", res.get("requestParams").get(0));
assertEquals("requestPath", "/x2", res.get("requestPath").get(0));
assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
assertEquals("sourceAddress", "192.168.1.95", res.get("sourceAddress").get(0));
assertEquals("sourceNameOrIp", "192.168.1.95", res.get("sourceNameOrIp").get(0));
assertEquals("sourcePort", 49728, res.get("sourcePort").get(0));
assertEquals("startTime", 1426009777838L, res.get("startTime").get(0));
assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));

    }
    
    
    //
  @Test
    public void testDnsUrlRequestFailing2() throws FileNotFoundException {
        //
        String line = "{\"data\": {\"requestMethod\": \"GET\", \"startTime\": \"1426009681.698796\", \"requestPath\": \"/bg/api/Pickup.ashx\", \"requestParams\": [{\"name\": \"_\", \"value\": \"1426007892760\"}], \"requestVersion\": \"1.1\", \"requestHost\": \"mwstream.wsj.net\", \"requestClientApplication\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36\", \"requestReferer\": \"http://www.marketwatch.com/story/e8-security-emerges-from-stealth-raises-98m-in-series-a-funding-2015-03-10\", \"responseStatus\": \"200\", \"responseContentType\": \"application/json\", \"responseServer\": \"Microsoft-IIS/7.5\", \"responseContentLength\": \"469\", \"requestContentLength\": \"566\"}, \"net\": {\"destNameOrIp\": \"205.203.131.203\", \"sourceNameOrIp\": \"192.168.1.95\", \"sourcePort\": \"49590\", \"destPort\": \"80\", \"transportProtocol\": \"tcp\", \"timestamp\": 1426007893, \"application\": \"http\", \"dpiFlowId\": \"104159\"}, \"dpiSignatureId\": \"URL request\"}";
        Record input = buildRecord(line);
        boolean result = doTest(input);
        assertEquals(true, result);
        OutUtils.printOut(this.outCommand.getNumRecords());
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);

        Record res = this.outCommand.getRecord(0);
        OutUtils.printOut("record: "+res.toString());

        assertEquals("bytesIn", 566, res.get("bytesIn").get(0));
assertEquals("bytesOut", 469, res.get("bytesOut").get(0));
assertEquals("cefSignatureId", "200", res.get("cefSignatureId").get(0));
assertEquals("destinationDnsDomain", "wsj.net", res.get("destinationDnsDomain").get(0));
assertEquals("destinationHostName", "mwstream", res.get("destinationHostName").get(0));
assertEquals("destinationNameOrIp", "mwstream.wsj.net", res.get("destinationNameOrIp").get(0));
assertEquals("destinationPort", 80, res.get("destinationPort").get(0));
assertEquals("dpiFlowId", "104159", res.get("dpiFlowId").get(0));
assertEquals("dpiSignatureId", "URL request", res.get("dpiSignatureId").get(0));
assertEquals("externalLogSourceType", "Dpi-Http", res.get("externalLogSourceType").get(0));
assertEquals("logSourceType", "WebProxyMef", res.get("logSourceType").get(0));
assertEquals("requestClientApplication", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36", res.get("requestClientApplication").get(0));
assertEquals("requestHost", "mwstream.wsj.net", res.get("requestHost").get(0));
assertEquals("requestMethod", "GET", res.get("requestMethod").get(0));
assertEquals("requestParams", "[{\"name\":\"_\",\"value\":\"1426007892760\"}]", res.get("requestParams").get(0));
assertEquals("requestPath", "/bg/api/Pickup.ashx", res.get("requestPath").get(0));
assertEquals("requestReferer", "http://www.marketwatch.com/story/e8-security-emerges-from-stealth-raises-98m-in-series-a-funding-2015-03-10", res.get("requestReferer").get(0));
assertEquals("requestScheme", "http", res.get("requestScheme").get(0));
assertEquals("responseContentType", "application/json", res.get("responseContentType").get(0));
assertEquals("sourceAddress", "192.168.1.95", res.get("sourceAddress").get(0));
assertEquals("sourceNameOrIp", "192.168.1.95", res.get("sourceNameOrIp").get(0));
assertEquals("sourcePort", 49590, res.get("sourcePort").get(0));
assertEquals("startTime", 1426009681698L, res.get("startTime").get(0));
assertEquals("transportProtocol", "tcp", res.get("transportProtocol").get(0));

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
//          OutUtils.printOut("DO NOT MATCH :" + line);
//        }
//      } // end while
//      OutUtils.printOut("parsed : " + i + " lines");
//
////      String line = "1392123548.479 179813 81.56.112.95 TCP_MISS/504 3182 GET http://mafreebox.free.fr/favicon.ico - DIRECT/212.27.38.253 text/html";
////      boolean result = doTest(line);
////      assertEquals(true, result);
////      Record res = this.outCommand.getRecord(0);
//    } catch (IOException ex) {
//      java.util.logging.Logger.getLogger(WebProxySquidTest.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }

}
