package com.securityx.model.mef.morphline.command.util;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

class UrlExtractCommand implements Command {
    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final List<String> inputFieldNames;
    private final List<String> destFieldsNames;
    private Logger logger = LoggerFactory.getLogger(UrlExtractCommand.class);

    public UrlExtractCommand(Config config,
                             MorphlineContext context,
                             Command parent,
                             Command child,
                             List<String> inputFieldName,
                             List<String> destFieldNames) {
        this.config = config;
        this.parent = parent;
        this.child = child;
        this.context = context;
        this.inputFieldNames = inputFieldName;
        this.destFieldsNames = destFieldNames;
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

    private void addIfNotNull(String value, String field, Record record){
       if(null != value){
           record.put(field, value);
       }
    }

    private void doProcessing(String url, Record record) throws MalformedURLException {
        URL urlObj = new URL(url);
        addIfNotNull(urlObj.getProtocol(), this.destFieldsNames.get(0), record);
        if (urlObj.getAuthority().indexOf(":") > 1 || urlObj.getAuthority().indexOf("@") > 1) {
            String[] parts = urlObj.getAuthority().split("[:\\@]", 2);
            addIfNotNull(parts[0], this.destFieldsNames.get(1), record);
        }
        addIfNotNull(urlObj.getHost(), this.destFieldsNames.get(2), record);
        if (urlObj.getPort() != -1) {
            addIfNotNull(String.valueOf(urlObj.getPort()), this.destFieldsNames.get(3), record);
        }else{
            addIfNotNull(String.valueOf(urlObj.getDefaultPort()), this.destFieldsNames.get(3), record);
        }
        addIfNotNull(urlObj.getPath(), this.destFieldsNames.get(4), record);
        if (urlObj.getQuery() != null) {
            addIfNotNull(urlObj.getQuery() + (urlObj.getRef() != null ? "#" + urlObj.getRef() : ""), this.destFieldsNames.get(5), record);
        }else if (urlObj.getRef() != null) {
            addIfNotNull("#" + urlObj.getRef(), this.destFieldsNames.get(5), record);
        }


        // could used as well
        //System.out.println("filename = " + aURL.getFile());
        //System.out.println("ref = " + aURL.getRef());
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
            for (Object r : got) {
                final String str = r.toString();
                try {
                     doProcessing(str, record);
                } catch (MalformedURLException ex) {
                    logger.error("urlExtract failed to parse an url with  " + str + " : " + ex.getMessage());
                }
            }
        }
        return child.process(record);
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
