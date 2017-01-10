package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MefInetProtoAndPortConstraint implements MefFieldConstrait<String> {
  private Logger logger = LoggerFactory.getLogger(MefInetProtoAndPortConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    if(logger.isDebugEnabled())
      logger.debug("ProtoAndPortConstraint :" +field.getPrettyName()+ " : "+ value);
    String[] subparts = value.split("/");
    if (subparts.length>=2) {
        results.put(WebProxyMefField.transportProtocol, subparts[0]);
        results.put(WebProxyMefField.applicationProtocol, subparts[1]);
    }else{
        results.put(WebProxyMefField.transportProtocol, value);
    }
    return results;
  }

}
