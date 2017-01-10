package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefLongConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefLongConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if(logger.isDebugEnabled())
        logger.debug("LongConstraint :"+field.getPrettyName()+ " : "+ value);
      try {
      results.put(field, Long.valueOf(value));
      }catch (NumberFormatException e){
        return null;
      }
      return results;
  }



}
