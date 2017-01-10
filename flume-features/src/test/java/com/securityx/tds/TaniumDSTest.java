package com.securityx.tds;

import com.securityx.tds.taniumsoap.TaniumSOAPPort;
import com.securityx.tds.taniumsoap.TaniumSOAPRequest;
import com.securityx.tds.taniumsoap.TaniumSOAPResult;
import com.securityx.tds.taniumsoap.TaniumSOAPService;
import org.apache.flume.Context;
import org.apache.flume.FlumeException;
import org.apache.flume.PollableSource;
import org.apache.flume.instrumentation.SourceCounter;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** 
* TaniumDS Tester. 
* 
* @author <Authors name> 
* @since <pre>Jun 5, 2015</pre> 
* @version 1.0 
*/ 
public class TaniumDSTest {
    private TaniumDS _tds = null;
    private Context _ctx = null;
    private String _wsdlFilePath = null;
    private SourceCounter _sc = null;

@Before
public void before() throws Exception {
    //Properties p = new Properties();
    //URL url = this.getClass().getResource("/flume-agent.config");
    //File flume_agent_config_file = new File(url.getFile());
    //FileInputStream fis = new FileInputStream(flume_agent_config_file);
    //p.load(fis);
    File file = new File("src/test/resources/tanium_console.wsdl");
    this._wsdlFilePath = file.getAbsolutePath();

    this._tds = new TaniumDS();
    this._ctx = new Context();
    this._sc = new SourceCounter(this._tds.getName());
    this._ctx.put("saved_questions", "CPU consumptions,  HOST-CPU consumptions, Hosts-Logged in user details ");
    this._ctx.put("wsdl_location", "file://"+this._wsdlFilePath);
    this._ctx.put("user", "suhas  ");
    this._ctx.put("passwd", "new4you");
    this._ctx.put("poll_interval_secs", "5 ");
} 

@After
public void after() throws Exception { 
}

/**
 *
 * Method: configure(Context context)
 *
 */
@Test
public void testConfiguredValues() throws Exception {
    this._tds.configure(this._ctx);
    assert (this._tds._qReq.getObjectList().getSavedQuestion().get(0).getName().contentEquals("CPU consumptions"));
    assert (this._tds._qReq.getObjectList().getSavedQuestion().get(1).getName().contentEquals("HOST-CPU consumptions"));
    assert (this._tds._qReq.getObjectList().getSavedQuestion().get(2).getName().contentEquals("Hosts-Logged in user details"));
    assert (this._tds._poll_interval_secs == TaniumDS.MINIMUM_POLLING_INTERVAL_SECS);
    assert (this._tds._wsdlLocation.contentEquals("file://"+this._wsdlFilePath));
    assert (this._tds._qReq.getAuth().getUsername().contentEquals("suhas"));
    assert (this._tds._qReq.getAuth().getPassword().contentEquals("new4you"));
    assert (this._tds._qReq.getCommand().contentEquals("GetResultData"));
}

/**
 *
 * Method: configure(Context context)
 *
 */
@Test
public void testConfigureWithUnreadableURLFilePath() throws Exception {
    this._ctx.put("wsdl_location", "file:///absdfsfsdf");
    try {
        this._tds.configure(this._ctx);
    } catch (FlumeException f) {
        assert (f.getMessage().startsWith("The wsdl location file path is not readable"));
    }
}

@Test
public void testConfigureWithBadWSDL() throws Exception {
    File file = new File("src/test/resources/tanium_console_junk.wsdl");
    String wsdlFilePath = file.getAbsolutePath();
    this._ctx.put("wsdl_location", "file://"+wsdlFilePath);
    try {
        this._tds.configure(this._ctx);
    } catch (FlumeException f) {
        assert (f.getMessage().startsWith("Unknown exception"));
    }
}

@Test
public void testConfigureWithMalFormedURL() throws Exception {
    File file = new File("src/test/resources/tanium_console.wsdl");
    String wsdlFilePath = file.getAbsolutePath();
    this._ctx.put("wsdl_location", "fie::://"+wsdlFilePath);
    try {
        this._tds.configure(this._ctx);
    } catch (FlumeException f) {
        assert (f.getMessage().startsWith("Tanium WSDL URL Malformed"));
    }
}
    /**
     *
* Method: configure(Context context) 
* 
*/ 
@Test
public void testConfigureWithEmptyPollInterval() throws Exception {
    this._ctx.put("poll_interval_secs", "");
    try {
        this._tds.configure(this._ctx);
    } catch (FlumeException f) {
        assert (
        f.getMessage().contentEquals(
                "SavedQuestionsString or Wsdl location or Polling interval is empty in agent configuration"));
    }
}

/**
 *
 * Method: configure(Context context)
 *
 */
@Test
public void testConfigureWithEmptyWsdl() throws Exception {
    this._ctx.put("wsdl_location", "");
    try {
        this._tds.configure(this._ctx);
    } catch (FlumeException f) {
        assert (f.getMessage().contentEquals(
                "SavedQuestionsString or Wsdl location or Polling interval is empty in agent configuration"));
    }
}

/**
 *
 * Method: configure(Context context)
 *
 */
@Test
public void testConfigureWithEmptySavedQuestions() throws Exception {
    this._ctx.put("saved_questions", "");
    try {
        this._tds.configure(this._ctx);
    } catch (FlumeException f) {
        assert (f.getMessage().contentEquals(
                "SavedQuestionsString or Wsdl location or Polling interval is empty in agent configuration"));
    }
}

/**
* 
* Method: start() 
* 
*/ 
@Test
public void testStart() throws Exception { 
//TODO: Test goes here...
    //System.out.println ("Before Start time : "+this._sc.getStartTime());
    this._sc.start();
    //System.out.println ("After Start time : "+this._sc.getStartTime());
    assert (this._sc.getStartTime() > (System.currentTimeMillis() - 10 * 1000));
} 

/** 
* 
* Method: stop() 
* 
*/ 
@Test
public void testStop() throws Exception {
    //TODO: Test goes here...
    //System.out.println ("Before STop time : "+this._sc.getStopTime());
    this._sc.stop();
    //System.out.println ("After STop time : "+this._sc.getStopTime());
    assert (this._sc.getStopTime() > (System.currentTimeMillis() - 10*1000));
} 

/** 
* 
* Method: process() 
* 
**/
@Test
public void testProcessQueryBeforePollingIntevalxx() throws Exception {
    // This test checks that agent backoff if we poll before expiry of polling
    // period
    this._tds._last_poll_time_ms = System.currentTimeMillis() - 5*1000;
    this._tds.configure(this._ctx);
    assert (this._tds.process() == PollableSource.Status.BACKOFF);
    /*
    TaniumSOAPService tss = Mockito.mock(TaniumSOAPService.class);
    TaniumSOAPPort tsp = tss.getTaniumSOAPPort();
    TaniumSOAPRequest tsr = new TaniumSOAPRequest();
    TaniumSOAPResult tsres = new TaniumSOAPResult();
    Mockito.when(tsp.request(tsr)).thenReturn(tsres);
    String result = new String();
    Mockito.when(tsres.getResultXML()).thenReturn(result);
    Mockito.when(this._tds.getChannelProcessor().processEvent();).the
    */
}

/**
 *
 * Method: process()
 *
 *
@Test
public void testProcessQueryBeforePollingInteval() throws Exception {
    this._tds._last_poll_time_ms = 0;
    Mockito.when(this._tds._tsp.request(this._tds._qReq)).thenReturn(null);
    //Mockito.when(Thread.sleep(TaniumDS.DEFULT_SOAP_REQUEST_RETRY_INTERVAL_SEC)).thenReturn());
    this._tds.configure(this._ctx);
    this._tds.process();

    String result = new String();
    Mockito.when(tsres.getResultXML()).thenReturn(result);
    Mockito.when(this._tds.getChannelProcessor().processEvent();).the

} */

/** 
* 
* Method: extractXMLQueryResults(String result) 
* 
*/ 
@Test
public void testExtractXMLQueryResults() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getStringFromDoc(org.w3c.dom.Node node, org.w3c.dom.Document doc) 
* 
*/ 
@Test
public void testGetStringFromDoc() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: main(String[] args) 
* 
*/ 
@Test
public void testMain() throws Exception { 
//TODO: Test goes here... 
} 


} 
