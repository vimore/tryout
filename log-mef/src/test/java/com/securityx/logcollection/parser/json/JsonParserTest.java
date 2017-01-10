/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.parser.json;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import java.io.ByteArrayInputStream;

/**
 * testing conversion from avro record (as json string) to Morphline record
 *
 * @author jyria
 */
public class JsonParserTest extends LogCollectionAbstractTest {

    public JsonParserTest(String name) {

        super(name);
        this.morphlineId = "jsonavrotomorphline";
        this.confFile = "logcollection-avro.conf";
        Config morphlineConf = MorphlineResourceLoader.getConfFile(confFile);
        this.morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, morphlineId);
    }

    public void testJsonToRecord() {
        String line = "{\"headers\":{\"timestamp\":\"1384679104697\",\"hostname\":\"hivedev1.labs.lan\",\"category\":\"firewall\",\"Severity\":\"6\",\"Facility\":\"16\"},\"body\":\"id=firewall sn=0006B129195C time=\\\"2013-11-17 01:05:04\\\" fw=71.6.1.234 pri=6 c=262144 m=98 msg=\\\"Connection Opened\\\" n=5283286 src=99.9.44.224:28520:X1:99-9-44-224.lightspeed.cicril.sbcglobal.net dst=10.10.30.100:53:X0: proto=udp/dns \"}";
        Record r_in= new Record();
        r_in.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(line.getBytes()));
        boolean result = doTest(r_in);
        assertEquals(true, result);
        Record r = this.outCommand.getRecord(0);
        assertEquals("logCollectionCategory", "firewall", r.get("logCollectionCategory").get(0));
        assertEquals("logCollectionHost", "hivedev1.labs.lan", r.get("logCollectionHost").get(0));
        assertEquals("message", "id=firewall sn=0006B129195C time=\"2013-11-17 01:05:04\" fw=71.6.1.234 pri=6 c=262144 m=98 msg=\"Connection Opened\" n=5283286 src=99.9.44.224:28520:X1:99-9-44-224.lightspeed.cicril.sbcglobal.net dst=10.10.30.100:53:X0: proto=udp/dns ", r.get("message").get(0));
        assertEquals("logCollectionTime", "1384679104697", r.get("logCollectionTime").get(0));

    }

}
