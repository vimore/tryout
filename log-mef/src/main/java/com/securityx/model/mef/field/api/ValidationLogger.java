package com.securityx.model.mef.field.api;

import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ValidationLogger {

    public static final String VALIDATION_LOG = "_validationLog";
    private Logger logger = LoggerFactory.getLogger(ValidationLogger.class);
    private final List<String> log = new ArrayList<String>();


    public boolean asLog(){
        return log.size()>0;
    }

    public void addValidationLog(Record r){
        if (log.size() > 0){
            r.put(VALIDATION_LOG, log.toString());
            log.clear();
        }
    }

    public void clear(){
        log.clear();
    }

    public void undeclaredField(Record record, String key) {
        log.add ("Field: '" + key + "' is not declared in format.");
        if (logger.isDebugEnabled()) {
            logger.debug("ERROR : Field: '" + key + "' is not declared in format.");
            logger.debug("ERROR : record in error : " + record.toString());
        }
    }

    public void unexpectedNull(Record record, String key, SupportedFormat field) {
        log.add("Nulls are NOT supported by field '" + field.getPrettyName() + "'");

        if (logger.isDebugEnabled()) {
            logger.debug("ERROR : Nulls are NOT supported by field '" + field.getPrettyName() + "'");
            logger.debug("ERROR : record in error : " + record.toString());
        }
    }

    public void unexpectedNumberOfValues(Record record, String key, SupportedFormat field, List<Object> values) {
        log.add("Field '" + field.getPrettyName() + "' expects " + field.getNumberOfValues()
                + " values but encoutered " + values.size());
        if (logger.isDebugEnabled()) {
            logger.debug("ERROR : Field '" + field.getPrettyName() + "' expects " + field.getNumberOfValues()
              + " values but encoutered " + values.size());
            logger.debug("ERROR : record in error : " + record.toString());
        }

    }

    public void unexpectedNull(Record record, String key, WebProxyMefField mefField) {
        log.add("Nulls are NOT supported by field '" + mefField.name() + "'");
        if (logger.isDebugEnabled()) {
            logger.debug(" ERROR : Nulls are NOT supported by field '" + mefField.name() + "'");
            logger.debug("ERROR : record in error : " + record.toString());
        }

    }

    public void unexpectedFormatValue(Record record, SupportedFormat mefField, Object values) {
        log.add("unexpected value format for field '" + mefField.getPrettyName() + "' : "+values.toString());
        if (logger.isDebugEnabled()) {
            logger.debug(" ERROR : goes not match constraint for '" + mefField.getPrettyName() + "'");
            logger.debug("ERROR : record in error : " + record.toString());
        }

    }


    public void unexpectedNumberOfValues(Record record, String key, WebProxyMefField mefField, List<Object> values) {
        log.add("Field '" + mefField.name() + "' expects " + mefField.getNumberOfValues()
                + " values but encoutered " + values.size());
        if (logger.isDebugEnabled()) {
            logger.debug("ERROR : Field '" + mefField.name() + "' expects " + mefField.getNumberOfValues()
              + " values but encoutered " + values.size());
            logger.debug("ERROR : record in error : " + record.toString());
        }
    }
}
