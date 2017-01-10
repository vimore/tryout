package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.logcollection.utils.IpUtils;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class MefInetIPAndPortAndInterfaceAndNameConstraint implements MefFieldConstrait<String> {

  private Logger logger = LoggerFactory.getLogger(MefInetIPAndPortAndInterfaceAndNameConstraint.class);

  private WebProxyMefField address;
  private WebProxyMefField port;
  private WebProxyMefField hostName;
  private WebProxyMefField interfaceName;

  @Override
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  private void initFromNamespace(SupportedFormat f) {
    //TODO : improve namespace management
    if (f.getNamespace().equals("destination")) {
      this.address = WebProxyMefField.destinationAddress;
      this.port = WebProxyMefField.destinationPort;
      this.hostName = WebProxyMefField.destinationNameOrIp;
      this.interfaceName = WebProxyMefField.deviceOutboundInterface;
    } else if (f.getNamespace().equals("source")) {
      this.address = WebProxyMefField.sourceAddress;
      this.port = WebProxyMefField.sourcePort;
      this.hostName = WebProxyMefField.sourceHostName;
      this.interfaceName = WebProxyMefField.deviceInboundInterface;
    }

  }

  @Override
  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
    Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
    initFromNamespace(field);
    if (logger.isDebugEnabled())
      logger.debug("MefInetIPAndPortAndInterfaceAndNameConstraint :" +field.getPrettyName()+ " : "+ value);
    String[] subparts = value.split(":");
    if (this.isValidFormat(value, subparts)) {
      if (IpUtils.isValidIpv4Address(subparts[0])) {
        try {
          InetAddress ip = InetAddress.getByName(subparts[0]);
          results.put(this.address, subparts[0]);
        } catch (Exception e) {
            if(logger.isDebugEnabled())
              logger.debug("ERROR : failure converting ip : " + value);
        }
      }
      if (subparts.length >= 2 && null != subparts[1]) {
        results.put(this.port, subparts[1]);
      }
      if (subparts.length >= 3 && null != subparts[2]) {
        results.put(this.interfaceName, subparts[2]);
      }

      if (subparts.length >= 4 && IpUtils.isFqdn(subparts[3])) {
        //split into HostName and DnsDomain
        if(logger.isDebugEnabled())
          logger.debug("is FQDN :" + subparts[3]);
        int dotIndex = subparts[3].indexOf('.');
        String dns = subparts[3].substring(dotIndex + 1, subparts[3].length());
        results.put(this.hostName, subparts[3]);
        if(logger.isDebugEnabled())
          logger.debug("results :" + String.valueOf(results));
      }
      results.put(field, value);
      return results;
    } else {
      results.put(field, value);
      return results;

    }
  }

  private boolean isValidFormat(String value, String[] subparts) {
    // only known case : sonicwall format : 
    //71.6.1.234:3188:X1: 
    //66.7.224.17:53:X1:sj-cns1.telepacific.net 
    int cpt = 0;
    int idx = 0;

    while ((idx = value.indexOf(":", idx)) != -1) {
      idx = idx + 1;
      cpt++;
    }
    return cpt >= 3;
  }
}
