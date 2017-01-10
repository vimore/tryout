package com.securityx.model.mef.morphline.command.util;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class DateToTimeStampCommand implements Command {
    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final List<String> inputFieldNames;
    private final HashMap<String, SimpleDateFormat> dateFormat;
    private final Precision ratio;
    private final String defaultTimeZoneId;
    private final boolean guessYear;
    private final boolean forceTz;
    private Logger logger = LoggerFactory.getLogger(DateToTimeStampCommand.class);
    private GregorianCalendar calendar;

    public DateToTimeStampCommand(Config config,
                                  MorphlineContext context,
                                  Command parent,
                                  Command child,
                                  List<String> inputFieldName,
                                  SimpleDateFormat dateFormat,
                                  String precision, boolean guessYear, boolean forceTz) {
        this.config = config;
        this.parent = parent;
        this.child = child;
        this.context = context;
        this.inputFieldNames = inputFieldName;
        this.dateFormat = new HashMap<String, SimpleDateFormat>();
        this.defaultTimeZoneId = dateFormat.getTimeZone().getID();
        this.dateFormat.put(this.defaultTimeZoneId, dateFormat);
        this.ratio = Precision.valueOf(precision);
        this.calendar = new GregorianCalendar(dateFormat.getTimeZone());
        this.guessYear = guessYear;
        this.forceTz = forceTz;
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat t = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
//    t.setTimeZone(TimeZone.getTimeZone("UTC"));
//    t.setDateFormatSymbols(DateFormatSymbols.getInstance(Locale.forLanguageTag("en")));
//
//    System.out.println(t.getTimeZone().toString());
//    //t.setTimeZone(TimeZone.getDefault());
//    GregorianCalendar c = new GregorianCalendar();
//    Date d = t.parse("2013-Jul-17 01:04:39");
//    c.setTime(d);
//    System.out.println(c.getTimeInMillis());

//    t = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
//    t.setTimeZone(TimeZone.getTimeZone("UTC"));
//    t.setDateFormatSymbols(DateFormatSymbols.getInstance(Locale.forLanguageTag("en")));
//
//    System.out.println(t.getTimeZone().toString());
//    //t.setTimeZone(TimeZone.getDefault());
//    GregorianCalendar c = new GregorianCalendar();
//    Date d = t.parse("28/Feb/2014:08:32:35 -0500");
//    c.setTime(d);
//    System.out.println(d.toString());
//    System.out.println(c.getTimeInMillis());


    }

    @Override
    public void notify(Record notification) {
        child.notify(notification);
    }

    @Override
    public boolean process(Record record) {
        for (String inputFieldName : this.inputFieldNames) {
            List got = record.get(inputFieldName);
            if (got == null || got.isEmpty()) {
                if (logger.isDebugEnabled())
                    logger.debug("ERROR : " + this.inputFieldNames + " is null or empty. " + record.toString());
                return false;
            }
            //copy before removing
            got = new ArrayList(got);
            record.removeAll(inputFieldName);
            for (Object r : got) {
                final String str = r.toString();
                SimpleDateFormat df = null;
                try {
                    df = this.getDateFormatConverter(record);
                    Date d = df.parse(str);
                    this.calendar.setTime(d);
                    if (this.guessYear && this.calendar.get(Calendar.YEAR) == 1970) {
                        Calendar now = Calendar.getInstance();   // This gets the current date and time.
                        int year = now.get(Calendar.YEAR);
                        if (this.calendar.get(Calendar.MONTH) < now.get(Calendar.MONTH)) {
                            this.calendar.set(Calendar.YEAR, year);
                        } else {
                            if (this.calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                                if (this.calendar.get(Calendar.DAY_OF_MONTH) <= now.get(Calendar.DAY_OF_MONTH)) {
                                    this.calendar.set(Calendar.YEAR, year);
                                } else {
                                    this.calendar.set(Calendar.YEAR, year - 1);
                                }
                            } else {
                                this.calendar.set(Calendar.YEAR, year - 1);
                            }
                        }
                    }
                    record.put(inputFieldName, (long) this.ratio.getScale() * this.calendar.getTimeInMillis());
                } catch (ParseException ex) {
                    logger.error("dateformat failed for " + str + " with pattern " + df.toPattern() + " tz:" + df.getTimeZone().getID() + ex.getMessage());
                    record.put(inputFieldName, got);
                }
            }
        }
        return child.process(record);
    }

    private SimpleDateFormat getDateFormatConverter(Record r) {
        String tzStr = (String) r.getFirstValue("logCollectionTimeZone");
        if (! this.forceTz && tzStr != null) {
            TimeZone tz = TimeZone.getTimeZone(tzStr);
            if (this.dateFormat.containsKey(tz.getID())) {
                return this.dateFormat.get(tz.getID());
            } else {
                SimpleDateFormat df = new SimpleDateFormat(this.dateFormat.get(defaultTimeZoneId).toPattern());
                df.setTimeZone(tz);
                this.dateFormat.put(tz.getID(), df);
                return df;
            }
        } else
            return this.dateFormat.get(defaultTimeZoneId);
    }

    @Override
    public Command getParent() {
        return parent;
    }


    private enum Precision {
        s(new Float(0.001)),
        ms(1),
        ns(1000);

        private float scale;

        private Precision(float scale) {
            this.scale = scale;
        }

        public float getScale() {
            return this.scale;
        }
    }
}
