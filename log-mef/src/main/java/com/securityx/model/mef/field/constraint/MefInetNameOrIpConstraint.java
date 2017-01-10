package com.securityx.model.mef.field.constraint;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ListMultimap;
import com.securityx.logcollection.utils.IpUtils;
import com.securityx.model.mef.field.api.InputTuplizer;
import com.securityx.model.mef.field.api.StringTuplizer;
import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.ValidationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class MefInetNameOrIpConstraint extends AbstractNameSpacedConstraint {
  private Logger logger = LoggerFactory.getLogger(MefInetNameOrIpConstraint.class);
  private boolean hasBackSlash = false;
  private int bashSlashPos = -1;
  private StringBuilder hostnameBuf =  new StringBuilder();
  private StringBuilder dnsdomainBuf =  new StringBuilder();

  private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.anyOf("\\".concat(String.valueOf('\uFFFF')));

  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  private void backSlashCheck(String value){
    String out = BACKSLASH_MATCHER.trimLeadingFrom(value);
    this.bashSlashPos = BACKSLASH_MATCHER.indexIn(value, value.length() - out.length());
    this.hasBackSlash = this.bashSlashPos >= 0;
  }

  private boolean isNT(String value){
    return  this.hasBackSlash &&
            this.bashSlashPos > 2 &&
            this.bashSlashPos < ( value.length() - 2 );
  }

  private void cleanup(){
    hostnameBuf.delete(0, hostnameBuf.capacity());
    dnsdomainBuf.delete(0, dnsdomainBuf.capacity());
  }

  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    initFromNamespace(field);
    cleanup();
    if (logger.isDebugEnabled())
      logger.debug("NameOrIpConstraint :" +field.getPrettyName()+ " : "+ value);
    value = value.trim();
    backSlashCheck(value);
    if (! this.hasBackSlash && IpUtils.isValidIpv4Address(value)) {
      try {
        InetAddress ip = InetAddress.getByName(value);
        if (!context.containsKey(this.address))
          results.put(this.address, value);
          results.put(field, value);
      } catch (Exception e) {
        //silent failure
      }
    }else if (! this.hasBackSlash && IpUtils.isFqdn(value, true, hostnameBuf, dnsdomainBuf)) {
      //split into HostName and DnsDomain
      if (logger.isDebugEnabled())
        logger.debug("is FQDN :" + value);

        int dotIndex = value.indexOf('.');
        String dns = value;
        //dns sanitycheck :

        int count = value.length() - value.replace(".", "").length();

        if (!value.endsWith(".") && count > 1 || value.endsWith(".") && count > 2){
          if (!context.containsKey(this.hostName))
            results.put(this.hostName, hostnameBuf.toString());
          if (!context.containsKey(this.dnsDomain))
            results.put(this.dnsDomain, dnsdomainBuf.toString());

        }else{
          if (!context.containsKey(this.dnsDomain))
            results.put(this.dnsDomain, hostnameBuf.toString()+"."+dnsdomainBuf.toString());
        }

      if(logger.isDebugEnabled())
        logger.debug("results :" + String.valueOf(results));
      results.put(field, value);
      //clean up


    }else if(isNT(value)){
      results.put(this.ntDomain, value.substring(0, this.bashSlashPos));
      results.put(this.hostName, escapeValue(value.substring(this.bashSlashPos+1,value.length())));
      if (this.user !=  null)
        results.put(this.user, value.substring(this.bashSlashPos+1,value.length()));
      results.put(field, value);
    }
    else {
      String v = escapeValue(BACKSLASH_MATCHER.trimLeadingFrom(value)).toLowerCase();
      results.put(this.hostName, v);
       results.put(field, escapeValue(value));
    }

    return results;
  }

  private String escapeValue(String value){
    return value.replace("\t", "\\t").
            replace("\r", "\\r").replace("\n", "\\n").
            replace(" ", "\\s");
  }

}
