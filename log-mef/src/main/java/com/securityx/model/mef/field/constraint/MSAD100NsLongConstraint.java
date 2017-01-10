package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MSAD100NsLongConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MSAD100NsLongConstraint.class);
  
  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if(logger.isDebugEnabled())
        logger.debug("MSAD100NsLongConstraint :"+field.getPrettyName()+ " : "+ value);
      
      try {
        long fileDateTimestamp = Long.valueOf(value);
        // /10000 to convert 100ns to ms 
        long v = fileDateTimestamp / 10000;
        if(logger.isDebugEnabled())
          logger.debug("MSAD100NsLongConstraint :"+v);
        
      results.put(field, v);
      }catch (NumberFormatException e){
        return null;
      }
      return results;
  }



}
