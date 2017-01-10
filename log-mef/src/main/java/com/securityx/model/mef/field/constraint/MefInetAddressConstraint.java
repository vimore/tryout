package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class MefInetAddressConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefInetAddressConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    try {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      if (logger.isDebugEnabled())
        logger.debug("MefInetAddressConstraint :"+field.getPrettyName()+" : "+value);
      results.put(field, InetAddress.getByName(value).getHostAddress());
      return results;
    } catch (UnknownHostException ex) {
      return null;
    }
  }



}
