package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefSanitizedStringConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefSanitizedStringConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if(logger.isDebugEnabled())
        logger.debug("StringConstraint :"+field.getPrettyName()+ " : "+ value);
      String out = value.replace("\r", "\\r");
      out = out.replace("\n", "\\n");
      out = out.replace("\t", "\\t");

      results.put(field, out);
      return results;
  }



}
