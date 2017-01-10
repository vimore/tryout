package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CertMefIsEpochTimestampConstraint implements MefFieldConstrait<String> {
    private Logger logger = LoggerFactory.getLogger(MefIsEpochTimestampConstraint.class);
    private Matcher format  = Pattern.compile("(\\d+)Z").matcher("");
    private String timestamp;
    private SimpleDateFormat  before2050 = new SimpleDateFormat("yyMMddhhmmss");
    private SimpleDateFormat  after2050 = new SimpleDateFormat("yyyyMMddhhmmss");
    TimeZone tz = TimeZone.getTimeZone("UTC");
    private Calendar calendar = new GregorianCalendar(tz);
    private Date date;

    public CertMefIsEpochTimestampConstraint(){
        before2050 = new SimpleDateFormat("yyMMddhhmmss");
        before2050.setTimeZone(TimeZone.getTimeZone("UTC"));
        after2050 = new SimpleDateFormat("yyyyMMddhhmmss");
        after2050.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public InputTuplizer<String> getInputTuplizer() {
        return new StringTuplizer();
    }

    @Override
    public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
                                                 ListMultimap<String, Object> context) {
        Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
        if (logger.isDebugEnabled())
            logger.debug("CertMefIsEpochTimestampConstraint : "+field.getPrettyName()+ " : "+ value);
        //does string match format ?
        format.reset(value);
        this.timestamp = null;
        if (format.matches()){
            timestamp = format.group(1);
            // timestamp length let us get how to process
            if (this.timestamp.length() == 12){
                try {
                    date = before2050.parse(timestamp);
                    this.calendar.setTime(date);
                    results.put(field, this.calendar.getTimeInMillis());
                    return results;

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (this.timestamp.length() == 14){
                try {
                    date = after2050.parse(timestamp);
                    this.calendar.setTime(date);
                    results.put(field, this.calendar.getTimeInMillis());
                    return results;

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else{
                logger.error("CertMefIsEpochTimestampConstraint: Timestamp string does not meet length constraints");
            }
            return null;
        }else{
            logger.error("CertMefIsEpochTimestampConstraint: Timestamp string does not meet format constraints");
            return null;

        }
    }
}
