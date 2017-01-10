package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MSADCanonicalConstraint implements MefFieldConstrait<String> {

  private Logger logger = LoggerFactory.getLogger(MSADCanonicalConstraint.class);

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    boolean containsCommaInValue = false;
    boolean containsSemicolonInValue = false;
    if (logger.isDebugEnabled()) {
      logger.debug("MSADCanonicalConstraintConstraint :" + field.getPrettyName() + " : " + value);
    }
    //CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab
    if (value.contains("\\,")) {
      value = value.replace("\\,", ___MY_DIRTY_COMMA_TOKEN__);
      containsCommaInValue = true;
    }
    if (value.contains("\\;")) {
      value = value.replace("\\,", ___MY_DIRTY_SEMICOLON_TOKEN__);
      containsSemicolonInValue = true;

    }
    String canonicalNames = null;
    for(String object : value.split(";")) {
      String[] parts = object.split(",");
      //("(?:DC|CV|OU|O|STREET|L|ST|C|UID)=");
      String canonicalName = null;
      for (int i = parts.length - 1; i >= 0; i--) {
        if (parts[i].startsWith("DC=")) {
          canonicalName = (canonicalName == null) ? parts[i].replace("DC=", "") : parts[i].replace("DC=", "") + "." + canonicalName;
          continue;
        }
        if (parts[i].startsWith("OU=")) {
          canonicalName = (canonicalName == null) ? parts[i].replace("OU=", "") : canonicalName + "/" + parts[i].replace("OU=", "");
          continue;
        }
        if (parts[i].startsWith("CN=")) {
          canonicalName = (canonicalName == null) ? parts[i].replace("CN=", "") : canonicalName + "/" + parts[i].replace("CN=", "");
          continue;
        }
      }
      if (containsCommaInValue) {
        canonicalName = canonicalName.replace(___MY_DIRTY_COMMA_TOKEN__, "\\,");
      }
      if (containsSemicolonInValue) {
        canonicalName = canonicalName.replace(___MY_DIRTY_SEMICOLON_TOKEN__, "\\,");
      }
      canonicalNames = (canonicalNames==null?canonicalName:canonicalNames.concat(";".concat(canonicalName)));
      logger.debug("MSADCanonicalConstraintConstraint value :" + canonicalName);
    }
    results.put(field, String.valueOf(canonicalNames));
    return results;
  }
  public static final String ___MY_DIRTY_COMMA_TOKEN__ = "-__My_dirty_token__-";
  public static final String ___MY_DIRTY_SEMICOLON_TOKEN__ = "-__My_dirty_token2__-";

}
