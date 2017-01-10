package com.securityx.modelfeature.utils;

import java.text.DecimalFormat;

/**
 * Created by harish on 10/5/15.
 */
public class MathUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    /**
     * Formats the Double to 2 decimal digits
     * @param d
     *
     * @return
     */
    public static double formatDecimal(double d) throws Exception{
        try {
            String s = DECIMAL_FORMAT.format(d);
            return new Double(s);
        }catch(Exception e){
            throw e;
        }
    }
}
