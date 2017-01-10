package com.securityx.model.mef.morphline.command;

import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class WebProxyMefBlueCoatJPMCScriptTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(WebProxyMefBlueCoatJPMCScriptTest.class);

    public WebProxyMefBlueCoatJPMCScriptTest() {
        super(WebProxyMefBlueCoatJPMCScriptTest.class.toString());
        this.morphlineId = " bluecoat-jpmc_parser";
        this.confFile = "logcollection-bluecoat.conf";
    }

    private Record buildRecord(String input) {
        Record r = new Record();
        r.put("logCollectionHost", "someHost");
        r.put("message", input);
        return r;
    }

    @Test
    public void testbluecoat() throws FileNotFoundException {
        String line = "2015-05-13 19:13:23 382 10.1.1.2 200 TCP_NC_MISS 13336 3682 GET http url1.chase.com 80 /Rewards/RewardsDetail.aspx ?AI=507902554 381 10.1.2.225 - \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36\" none - 10.1.3.52 10.1.4.11";
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);
        OutUtils.printOut(out);
        assertEquals("bytesIn", 3682, out.get("bytesIn").get(0));
        assertEquals("bytesOut", 13336, out.get("bytesOut").get(0));
        assertEquals("cefSignatureId", "200", out.get("cefSignatureId").get(0));
        assertEquals("destinationAddress", "10.1.2.225", out.get("destinationAddress").get(0));
        assertEquals("destinationDnsDomain", "chase.com", out.get("destinationDnsDomain").get(0));
        assertEquals("destinationHostName", "url1", out.get("destinationHostName").get(0));
        assertEquals("destinationNameOrIp", "url1.chase.com", out.get("destinationNameOrIp").get(0));
        assertEquals("destinationPort", 80, out.get("destinationPort").get(0));
        assertEquals("deviceAction", "TCP_NC_MISS", out.get("deviceAction").get(0));
        assertEquals("deviceAddress", "10.1.3.52", out.get("deviceAddress").get(0));
        assertEquals("deviceCustomString2", "382", out.get("deviceCustomString2").get(0));
        assertEquals("deviceEventCategory", "none", out.get("deviceEventCategory").get(0));
        assertEquals("externalLogSourceType", "BlueCoat", out.get("externalLogSourceType").get(0));
        assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));
        assertEquals("logSourceType", "WebProxyMef", out.get("logSourceType").get(0));
        assertEquals("reason", "-", out.get("reason").get(0));
        assertEquals("requestClientApplication", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36", out.get("requestClientApplication").get(0));
        assertEquals("requestMethod", "GET", out.get("requestMethod").get(0));
        assertEquals("requestPath", "/Rewards/RewardsDetail.aspx", out.get("requestPath").get(0));
        assertEquals("requestQuery", "?AI=507902554", out.get("requestQuery").get(0));
        assertEquals("requestScheme", "http", out.get("requestScheme").get(0));
        assertEquals("sourceAddress", "10.1.4.11", out.get("sourceAddress").get(0));
        assertEquals("sourceNameOrIp", "10.1.4.11", out.get("sourceNameOrIp").get(0));
        assertEquals("startTime", 1431544403000L, out.get("startTime").get(0));


    }


}
