package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MSADFileDateLongConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MSADFileDateLongConstraint.class);
  private static long AnsiDateToEpochDeltaMs = 11644473600000L; // 1601/01/01 (ANSI date) to 1970/01/01 (unix epoch) in millisec

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if(logger.isDebugEnabled())
        logger.debug("MSADFileDateLongConstraint :"+field.getPrettyName()+ " : "+ value);
      
      try {
        long fileDateTimestamp = Long.valueOf(value);
        if (fileDateTimestamp != 0 && fileDateTimestamp != 9223372036854775807L){
          // /10000 to convert 100ns to ms and then shift to epoch relative time
          long v = fileDateTimestamp / 10000 - AnsiDateToEpochDeltaMs; 
          if(logger.isDebugEnabled())
            logger.debug("MSADFileDateLongConstraint :"+v);
          results.put(field, v);
        }else{
          // let say that at now, 1970/01/01 or 1601/01/01 does not make any difference
          // keeping same max value as well 
          results.put(field, fileDateTimestamp);
        }
      }catch (NumberFormatException e){
        return null;
      }
      return results;
  }



}
