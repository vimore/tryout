package com.securityx.model.mef;

import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.utils.OutUtils;
import org.kitesdk.morphline.api.Record;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MorphlineRecordValidatorTest {

    ValidationLogger logger;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        logger = Mockito.mock(ValidationLogger.class);
    }

    @Test
    public void testHappyPath() {
        OutUtils.printOut("validate");
        MorphlineRecordValidator instance = new MorphlineRecordValidator(true);
        Record record = new Record();
        String longGarbage = "sourceUserName";
        for (int i = 0; i < 1024; i++) { // makes name exceed sourceUserName contraint
            longGarbage += "_";
        }
//        Assert.assertTrue(longGarbage.length() > 1024);
//        record.put("sourceUserName", longGarbage);
//        boolean result = instance.validate(logger, record);
//        Assert.assertEquals(result, true);
//        Assert.assertTrue(longGarbage.startsWith(record.getFirstValue("_sourceUserName").toString()));
//        Assert.assertTrue(record.getFirstValue("_sourceUserName").toString().length() <= 1024);
    }
}