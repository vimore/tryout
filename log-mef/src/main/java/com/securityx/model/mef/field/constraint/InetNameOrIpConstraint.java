package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.logcollection.utils.IpUtils;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class InetNameOrIpConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(InetNameOrIpConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    if (logger.isDebugEnabled())
      logger.debug("InetNameOrIpConstraint :" +field.getPrettyName()+ " : "+ value);
    if (IpUtils.isValidIpv4Address(value)) {
      try {
        InetAddress ip = InetAddress.getByName(value);
        results.put(field, value);
      } catch (Exception e) {
        //silent failure
      }
    }
    if (IpUtils.isFqdn(value)) {
      //split into HostName and DnsDomain
      if (logger.isDebugEnabled())
        logger.debug("is FQDN :" + value);
      int dotIndex = value.indexOf('.');
      String dns = value.substring(dotIndex + 1, value.length());
      results.put(field, value);
      if(logger.isDebugEnabled())
        logger.debug("results :" + String.valueOf(results));
    }
    results.put(field, value);
    return results;
  }

}
