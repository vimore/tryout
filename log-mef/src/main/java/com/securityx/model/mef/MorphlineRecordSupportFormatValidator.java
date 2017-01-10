package com.securityx.model.mef;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MorphlineRecordSupportFormatValidator {


  private final Logger logger = LoggerFactory.getLogger(MorphlineRecordSupportFormatValidator.class);
  private final boolean failOnUnknownFields;
  private Method getByPrettyName = null;
  private Object instance=null;
  private SupportedFormat format;
  

  public MorphlineRecordSupportFormatValidator(SupportedFormats supportedFormat, boolean failOnUnknownFields) {
    this.failOnUnknownFields = failOnUnknownFields;
    Collection<SupportedFormat> fields =  supportedFormat.getSupportedFormatFields();
    Map<String, Object> types = new HashMap<String, Object>();
    for(SupportedFormat f : fields){
      this.format = f;
      break;
    }
    if(logger.isDebugEnabled())
      logger.debug("INFO : "+types.keySet().toString());
    
  }

  private boolean isSkipable(String key){
    return key.equals("_validationError");
  }

  public boolean validate(ValidationLogger logger, Record record) {
    if (record == null) {
      return false;
    }

    ListMultimap<String, Object> fields = ArrayListMultimap.create(record.getFields());
    boolean isValid = true;
    for (String key : fields.keySet()) {
      if (this.logger.isDebugEnabled()){
        this.logger.debug("validating: "+key);
        this.logger.debug("current value : "+ record.get(key).toString());
      }  
      if (!validateRecordField(logger, record, key, fields)) {
        isValid = false;
        break;
      }
    }
    logger.addValidationLog(record);
    return isValid;
  }

  private boolean validateRecordField(ValidationLogger validationLogger,
          Record record, String key, ListMultimap<String, Object> fields) {
    // foreach supported type
    if (isSkipable(key))
      return true;
    Object field = null;
        field = this.format.getByPrettyName(key);
    if (field == null) {
      if (failOnUnknownFields) {
        validationLogger.undeclaredField(record, key);
        return false;
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug("unknown field : " + key);
        }
        return true;
      }
    } else {
      List<Object> values = fields.get(key);
      SupportedFormat supportedFormatField = (SupportedFormat) field;
      if (values == null) {
        if (supportedFormatField.isNullable()) {
          // field is valid
          return true;
        } else {
          validationLogger.unexpectedNull(record, key, supportedFormatField);
          return false;
        }
      } else {
        int expectedNumberOfValues = supportedFormatField.getNumberOfValues();
        if (expectedNumberOfValues > -1) {
          if (expectedNumberOfValues == values.size()) {
            return validateValues(validationLogger, record, supportedFormatField, values, fields);
          } else {
            validationLogger.unexpectedNumberOfValues(record, key, supportedFormatField, values);
            return false;
          }
        } else {
          return validateValues(validationLogger, record, supportedFormatField, values, fields);
        }
      }
    }
  }

  private boolean validateValues(ValidationLogger validationLogger, Record record,
          SupportedFormat field, List<Object> values, ListMultimap<String, Object> context) {
    record.removeAll(field.getPrettyName());
    long t0=System.currentTimeMillis();
    for (MefFieldConstrait constrant : field.getContraints()) {
      InputTuplizer inputTuplizer = constrant.getInputTuplizer();
      for (Object t : inputTuplizer.tuplize(values)) {
        Map<SupportedFormat, Object> v = constrant.validate(validationLogger, field, t, context);
        if (v != null) {
          for (Entry<SupportedFormat, Object> e : v.entrySet()) {
            if (e.getKey() != field) {
              for (MefFieldConstrait constraint : e.getKey().getContraints()) {
                
                Map<SupportedFormat, Object> v2 = constraint.validate(validationLogger, e.getKey(), e.getValue(), context);                  
                if (v2 != null) {
                  for (Entry<SupportedFormat, Object> e2 : v2.entrySet()) {
                    if (field.getNumberOfValues()>record.get(e2.getKey().getPrettyName()).size()){
                      record.put(e2.getKey().getPrettyName(), e2.getValue());
                    }else{
                      if(logger.isDebugEnabled())
                        logger.debug(e2.getKey().getPrettyName()+" is already defined ! "+record.get(e2.getKey().getPrettyName()).toString());
                    }
                  }
                }

              }
            } else {
              record.replaceValues(field.getPrettyName(), e.getValue());
              //record.put(field.getPrettyName(), e.getValue());
            }
          }
        }
      }
    }
    long duration = System.currentTimeMillis() - t0;
    
    if (logger.isDebugEnabled())
        logger.debug("duration "+field.getPrettyName()+" : "+String.valueOf(duration));
    List validated = record.get(field.getPrettyName());
    if (validated==null || (validated != null && validated.isEmpty())) {
      if (logger.isDebugEnabled())
        logger.debug("ERROR : validating field " + field.getPrettyName());
      validationLogger.unexpectedFormatValue(record, field, values);
    }
    return validated != null && !validated.isEmpty();
  }
  
  private static Map<String, SupportedFormat> init() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
