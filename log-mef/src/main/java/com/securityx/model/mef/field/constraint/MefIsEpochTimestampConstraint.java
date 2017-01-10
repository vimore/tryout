package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefIsEpochTimestampConstraint implements MefFieldConstrait<String> {
    private Logger logger = LoggerFactory.getLogger(MefIsEpochTimestampConstraint.class);
    @Override
    public InputTuplizer<String> getInputTuplizer() {
        return new StringTuplizer();
    }

    @Override
    public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
            ListMultimap<String, Object> context) {
      if (logger.isDebugEnabled())
        logger.debug("MefIsEpochTimestampConstraint : "+field.getPrettyName()+ " : "+ value);
        try {
            Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
            results.put(field, Long.parseLong(value));
            return results;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
