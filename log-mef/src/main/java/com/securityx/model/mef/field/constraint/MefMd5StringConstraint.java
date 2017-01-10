package com.securityx.model.mef.field.constraint;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefMd5StringConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefMd5StringConstraint.class);
    private static final CharMatcher AuthorizedChars = CharMatcher.anyOf("abcdefABCDEF0123456789");

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if(logger.isDebugEnabled())
        logger.debug("MefMd5StringConstraint :"+field.getPrettyName()+ " : "+ value);
      if (value.length() <= 32 && AuthorizedChars.matchesAllOf(value))
          results.put(field, String.valueOf(value));
      else
          logger.error("invalid MD5 format : "+value);
      return results;
  }



}
