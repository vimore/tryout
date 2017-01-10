package com.securityx.modelfeature.utils;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** 
* CEF Tester. 
* 
* @author akshat_harit
* @since <pre>07/30/2015</pre> 
* @version 1.0 
*/ 
public class CEFTest extends TestCase {
    public CEFTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * Method: toSyslogCefString()
     */
    public void testToSyslogCefString() throws Exception {
        Map<String, String> cef_map = new HashMap<String, String>();
        cef_map.put("ipAddress", "123.123.123.123");

        CefExtension CefExtens = new CefExtension(cef_map);
        CEF test = new CEF("0.0", "Security", "threatmanager", "1.0", "100", "worm successfully stopped", .95, "somehost", CefExtens);
        //System.out.println(test.toSyslogCefString());

        assertEquals(true, test.toSyslogCefString().startsWith("CEF:0|Security|threatmanager|1.0|100|worm successfully stopped|10|dst=123.123.123.123"));
    }

    public void testToSyslogCefString2() throws Exception {
        Map<String, String> cef_map = new HashMap<String, String>();
        cef_map.put("ipAddress", "123.123.123.123");
        cef_map.put("dateTime", "1470532045535");

        CefExtension CefExtens = new CefExtension(cef_map);
        CEF test = new CEF("0.0", "Security", "threatmanager", "1.0", "100", "worm successfully stopped", .95, "somehost", CefExtens);
        //System.out.println(test.toSyslogCefString());
        assertEquals(test.toSyslogCefString(), "CEF:0|Security|threatmanager|1.0|100|worm successfully stopped|10|rt=1470532045535 dst=123.123.123.123 dvchost=somehost");
    }

    // this is a bad feature

    /*
    rt deviceReceiptTime Time Stamp The time at which the event related to the activity wasreceived. The format is MMM dd yyyy HH:mm:ss or millisecondssince epoch (Jan 1st 1970).
    */
    public void test_rt() throws Exception {
        String syslog = "CEF:0|E8|EntityAnalytics|0.0|Beaconing|Source Host has displayed a suspicious repeated pattern of network traffic.|10|rt=Aug 10 2015 08:08:08 dst=10.11.12.13";
        Optional<CEF> cef = CEF.syslogCefToCef(syslog);
        //System.out.println(cef.toString());
        CEF c = cef.get();
        //System.out.println(c.toSyslogCefString());
        String cefsyslog = c.toSyslogCefString();
        cef = CEF.syslogCefToCef(syslog);
        //System.out.println(cef.toString());
        c = cef.get();
        //System.out.println(c.toSyslogCefString());
        assertEquals(cefsyslog, c.toSyslogCefString());

    }

    // the syslog lines are in the DB in the table ALERT_AUDIT_LOGS
    // SELECT * FROM ALERT_AUDIT_LOGS LIMIT 10;
    /*

        DATE_TIME                                 ALERT                                     ALERT_DESTINATION                         ALERT_STATE                               ALERT_LOG                                 ERROR_LOG
    ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    2016-08-07T01:07:25.535Z                  {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=uvo15nuy45mzl3j  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Command and  <null>
    2016-08-08T11:35:26.058-07:00             {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=10.10.80.62, po  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Anomalous U  <null>
    2016-08-08T11:42:23.657-07:00             {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=10.10.80.62, po  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Anomalous U  <null>
    2016-08-08T13:05:24.758-07:00             {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=10.10.80.62, po  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Anomalous U  <null>
    2016-08-08T14:22:27.521-07:00             {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=10.10.80.62, po  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Anomalous U  <null>
    2016-08-08T19:47:41.321Z                  {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=10.10.80.62, po  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Anomalous U  <null>
    2016-08-09T19:17:02.697Z                  {"alertId":"26804667-811a-4a6e-955e-bb6b  ArcSightListner{hostName=10.10.80.62, po  DISPATCHED                                CEF:0|E8|EntityAnalytics|0.0|Anomalous U  <null>
     */
    // Each alert log cell can contain multiple log lines as shown below
    /*
    CEF:0|E8|EntityAnalytics|0.0|Command and Control|This host has displayed characteristics similar to those used in command and control activities to the destination hosts.|5|dhost= dmac= dst=192.168.12.47 duser= rt=1470532045535 dvchost=cluster6-e8.e8sec.com
    CEF:0|E8|EntityAnalytics|0.0|Command and Control|This host has displayed characteristics similar to those used in command and control activities to the destination hosts.|2|dhost= dmac= dst=192.168.12.22 duser= rt=1470532045535 dvchost=cluster6-e8.e8sec.com
    CEF:0|E8|EntityAnalytics|0.0|Command and Control|This host has displayed characteristics similar to those used in command and control activities to the destination hosts.|2|dhost= dmac= dst=192.168.12.30 duser= rt=1470532045535 dvchost=cluster6-e8.e8sec.com
     */
    public void test_rt_timeStamp() throws Exception {
        String syslog = "CEF:0|E8|EntityAnalytics|0.0|Command and Control|This host has displayed characteristics similar to those used in command and control activities to the destination hosts.|5|dhost= dmac= dst=192.168.12.47 duser= rt=1470532045535 dvchost=cluster6-e8.e8sec.com";

        //        "CEF:0|E8|EntityAnalytics|0.0|Command and Control|This host has displayed characteristics similar to those used in command and control activities to the destination hosts.|2|dhost= dmac= dst=192.168.12.22 duser= rt=1470532045535 dvchost=cluster6-e8.e8sec.com\n" +
        //        "CEF:0|E8|EntityAnalytics|0.0|Command and Control|This host has displayed characteristics similar to those used in command and control activities to the destination hosts.|2|dhost= dmac= dst=192.168.12.30 duser= rt=1470532045535 dvchost=cluster6-e8.e8sec.com\n";
        Optional<CEF> cef = CEF.syslogCefToCef(syslog);
        //System.out.println(cef.toString());
        CEF c = cef.get();
        //System.out.println(c.toSyslogCefString());
        String cefsyslog = c.toSyslogCefString();
        cef = CEF.syslogCefToCef(syslog);
        //System.out.println(cef.toString());
        c = cef.get();
        //System.out.println(c.toSyslogCefString());
        assertEquals(cefsyslog, c.toSyslogCefString());

    }
}


