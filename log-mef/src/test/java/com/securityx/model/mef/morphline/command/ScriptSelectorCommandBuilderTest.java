package com.securityx.model.mef.morphline.command;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.Assert;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class ScriptSelectorCommandBuilderTest {

    @Test
    public void test() throws FileNotFoundException, Exception {
        Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-script-selector-command.conf");
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
        MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
        morphlineHarness.startup(outCommand);
        morphlineHarness.feedLines(1, new String[]{
            "Oct 16 10:49:20 ciscopix %PIX-6-302015: Built inbound UDP connection 13287824 for network_10_11_10:10.11.100.151/51842 (10.11.100.151/51842) to network_10_11_1:10.11.0.101/53 (10.11.0.101/53)"});
        morphlineHarness.shutdown();
        
        OutUtils.printOut(outCommand.getNumRecords());
        Assert.assertTrue(outCommand.getNumRecords() == 1);
        Record r = outCommand.getRecord(0);
        //Assert.assertEquals(r.get("Foo"), "bar");
    }

}