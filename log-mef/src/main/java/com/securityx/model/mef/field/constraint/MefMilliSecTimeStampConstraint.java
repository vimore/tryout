package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MefMilliSecTimeStampConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefMilliSecTimeStampConstraint.class);
  private final static Pattern microSecPattern = Pattern.compile(
          "^(\\d{8,12})\\.(\\d{1,6})$");


  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      //TODO, to be implemented after investigation on content format : can be a real timestamp 
      // until proved it's not the case, we can expect to see date in this field.
      if(logger.isDebugEnabled())
        logger.debug("MefMilliSecTimeStapConstraint :"+field.getPrettyName()+ " : "+ value);
      Matcher m = microSecPattern.matcher(value);
      if (m.matches()) {
        String microStr = m.group(2);
        while (microStr.length() < 6)
          microStr = microStr  + "0";
        value = m.group(1)+ microStr.substring(0,3);
        if(logger.isDebugEnabled())
          logger.debug("MefMilliSecTimeStapConstraint :converted to  : "+ m.group(1)+" "+m.group(2).substring(0, 2)); 
      }
        
      try {
        results.put(field, new Long(value));
      }catch (Exception e){
        return null;
      }
      return results;
  }



}
