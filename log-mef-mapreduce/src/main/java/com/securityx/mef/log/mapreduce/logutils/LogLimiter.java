package com.securityx.mef.log.mapreduce.logutils;

import java.util.HashMap;

public class LogLimiter {
    public static enum LogCategory{
        SUCCESS_PARSING,
        NO_OUTPUT_FORMAT,
        NULL_RETURNED,
        EXCEPTIONS_RAISED,
        LOGSAMPLER;

    }

    private HashMap<LogCategory, Integer> logCounters;
    private final Integer MAX;

    public LogLimiter() {
        this.logCounters = new HashMap<LogCategory, Integer>();
        MAX = 100;
    }
    public LogLimiter(Integer max) {
        this.logCounters = new HashMap<LogCategory, Integer>();
        this.MAX = max;
    }

    public boolean isSubmitAllowed(LogCategory type){
        Integer cpt;
       if (! this.logCounters.containsKey(type)){
           cpt = new Integer(0);
       }else{
           cpt = this.logCounters.get(type);
       }
        cpt +=1;
        this.logCounters.put(type, cpt);
       return cpt <= MAX;
    }
}
