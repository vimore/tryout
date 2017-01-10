package com.securityx.utils;

public class OutUtils {
    private static boolean doPrintOut;
    static{

       boolean b;
        OutUtils.doPrintOut = Boolean.valueOf(System.getProperty("logMefEnableVerboseOutput"));
    }

    public static void printOut(Object o){
       if (doPrintOut){
           System.out.println(o);
       }
    }

}
