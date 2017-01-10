package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefInetDnsDomainConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefInetDnsDomainConstraint.class);
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    if (logger.isDebugEnabled())
      logger.debug("DnsDomainConstraint :" +field.getPrettyName()+ " : "+ value);
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    results.put(field, value);
    return results;
  }

}
