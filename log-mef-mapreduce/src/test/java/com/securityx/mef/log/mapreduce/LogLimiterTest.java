package com.securityx.mef.log.mapreduce;

import com.securityx.mef.log.mapreduce.logutils.LogLimiter;
import org.junit.Assert;
import org.junit.Test;

public class LogLimiterTest {

    @Test
    public void testLogCounter(){
        LogLimiter limiter = new LogLimiter();

        for(int i=1; i<=100; i++){

            Assert.assertTrue(limiter.isSubmitAllowed(LogLimiter.LogCategory.EXCEPTIONS_RAISED));
        }
        Assert.assertFalse(limiter.isSubmitAllowed(LogLimiter.LogCategory.EXCEPTIONS_RAISED)) ;
    }
}
