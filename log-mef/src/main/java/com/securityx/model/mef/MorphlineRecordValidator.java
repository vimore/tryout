package com.securityx.model.mef;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.InputTuplizer;
import com.securityx.model.mef.field.api.MefFieldConstrait;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MorphlineRecordValidator {

  private final Logger logger = LoggerFactory.getLogger(MorphlineRecordValidator.class);
  private final boolean failOnUnknownFields;

  public MorphlineRecordValidator(boolean failOnUnknownFields) {
    this.failOnUnknownFields = failOnUnknownFields;
  }

  public boolean validate(ValidationLogger logger, Record record) {
    if (record == null) {
      return false;
    }

    ListMultimap<String, Object> fields = ArrayListMultimap.create(record.getFields());
    for (String key : fields.keySet()) {
      if (!validateRecordField(logger, record, key, fields)) {
        return false;
      }
    }
    return true;
  }

  private boolean validateRecordField(ValidationLogger validationLogger,
          Record record, String key, ListMultimap<String, Object> fields) {
    WebProxyMefField mefField = null;
    try {
      mefField = WebProxyMefField.valueOf(key);
    } catch (IllegalArgumentException iae) {
      // Swallow this exception in because null is the pivot.
    }
    if (mefField == null) {
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
      if (values == null) {
        if (mefField.isNullable()) {
          // field is valid
          return true;
        } else {
          validationLogger.unexpectedNull(record, key, mefField);
          return false;
        }
      } else {
        int expectedNumberOfValues = mefField.getNumberOfValues();
        if (expectedNumberOfValues > -1) {
          if (expectedNumberOfValues == values.size()) {
            return validateValues(validationLogger, record, mefField, values, fields);
          } else {
            validationLogger.unexpectedNumberOfValues(record, key, mefField, values);
            return false;
          }
        } else {
          return validateValues(validationLogger, record, mefField, values, fields);
        }
      }
    }
  }

  private boolean validateValues(ValidationLogger validationLogger, Record record,
          WebProxyMefField mefField, List<Object> values, ListMultimap<String, Object> context) {
    record.removeAll(mefField.name());
    for (MefFieldConstrait constrant : mefField.getContraints()) {
      InputTuplizer inputTuplizer = constrant.getInputTuplizer();
      for (Object t : inputTuplizer.tuplize(values)) {
        Map<WebProxyMefField, Object> v = constrant.validate(validationLogger, mefField, t, context);
        if (v != null) {
          for (Entry<WebProxyMefField, Object> e : v.entrySet()) {
            if (e.getKey() != mefField) {
              for (MefFieldConstrait constraint : e.getKey().getContraints()) {
                Map<WebProxyMefField, Object> v2 = constraint.validate(validationLogger, e.getKey(), e.getValue(), context);
                if (v2 != null) {
                  for (Entry<WebProxyMefField, Object> e2 : v2.entrySet()) {

                    record.put(e2.getKey().name(), e2.getValue());
                  }
                }

              }
            } else {
              record.put(mefField.name(), e.getValue());
            }
          }
        }
      }
    }
    List validated = record.get(mefField.name());
    return validated != null && !validated.isEmpty();
  }
}
