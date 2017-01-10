package com.securityx.model.mef.morphline.command.dhcp;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MefCefDhcpdSplunkCefScriptTest extends LogCollectionAbstractTest {

    private Logger logger = LoggerFactory.getLogger(MefCefDhcpdSplunkCefScriptTest.class);

    public MefCefDhcpdSplunkCefScriptTest() {
        super(MefCefDhcpdSplunkCefScriptTest.class.toString());
        this.morphlineId = "cefnew";
        //this.morphlineId = "ceffromsyslogmissinghost";
        this.confFile = "logcollection-cef.conf";
    }

    @Test
    public void test0() throws FileNotFoundException {
        String line = "CEF: 0|unknown |dhcpd|unknown|DHCPACK|unknown|0|shost=HD7082DE32448B cs1Label=deviceInboundInterface cs6Label=raw cs6=Nov  9 15:38:10 stuxsh01 dhcpd: DHCPACK on 10.195.189.142 to 40:83:de:32:44:8b (HD7082DE32448B) via eth0:2 cs3Label=src_mac cs2=10.195.189.142 cs3=40:83:de:32:44:8b cs1=eth0:2 dproc=dhcpd cs2Label=src_ip";
        //     line = "10/31/2013 23:59:12 CEF:0|ForeScout Technologies|CounterAct|6.3.4|NONCOMPLIANCE|host is not compliant|5|cs1Label=Compliancy Policy Name cs2Label=Compliancy Policy Subrule Name cs3Label=Host Compliancy Status cs4Label=Compliancy Event Trigger cs1=Security policy  cs2=Test failed cs3=no cs4=CounterAct Action dmac=00:03:47:24:46:10 dst=10.10.1.8 dntdom=mydomain.com dhost=wks-105 duser=Dick_Dietz dvc=1.1.1.2 dvchost=forescout-02 rt=1328814511000";
        Record r = new Record();
        r.put("syslogMessage", line);
        boolean result = doTest(r);

        assertEquals(true, result);

    }



}
