package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EntityFusionConfiguration {
    protected static final SimpleDateFormat dateTimeAndZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    protected static final SimpleDateFormat dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static final SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
    static {
        dateTimeAndZone.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateAndTime.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateOnly.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(GraphiteConfiguration.class);

    @JsonProperty
    private int backoffPeriodHours;

    @JsonProperty
    private String conversionDate;

    public int getBackoffPeriodHours() {
        return backoffPeriodHours;
    }

    public void setBackoffPeriodHours(int backoffPeriodHours) {
        this.backoffPeriodHours = backoffPeriodHours;
    }

    public Date getConversionDate() {
        if (conversionDate == null)
            return null;

        return convertDate(conversionDate);
    }

    public static Date convertDate(String dateString) {
        Date conversion = null;
        try {
            conversion = dateTimeAndZone.parse(dateString);
        } catch (ParseException e) {
            try {
                conversion = dateAndTime.parse(dateString);
            } catch (ParseException e1) {
                try  {
                    conversion = dateOnly.parse(dateString);
                } catch (ParseException e2) {
                    LOGGER.error("Could not parse conversion date [" + dateString + "]");
                }
            }
        }

        return conversion;
    }

    public static String dateToString(Date date) {
        return dateTimeAndZone.format(date);
    }

    public static String longToString(long timestamp) {
        Date d = new Date(timestamp);
        return dateToString(d);
    }

    public void setConversionDate(String conversionDate) {
        this.conversionDate = conversionDate;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("backoffPeriodHours", backoffPeriodHours)
                .add("conversionDate", conversionDate)
                .toString();
    }
}