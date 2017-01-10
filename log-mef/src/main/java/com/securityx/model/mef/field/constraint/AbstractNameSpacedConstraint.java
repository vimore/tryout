package com.securityx.model.mef.field.constraint;

import com.securityx.model.mef.field.api.MefFieldConstrait;
import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.WebProxyMefField;

public abstract class AbstractNameSpacedConstraint implements MefFieldConstrait<String> {
    protected WebProxyMefField address;
    protected WebProxyMefField hostName;
    protected WebProxyMefField dnsDomain;
    protected WebProxyMefField ntDomain;
    protected WebProxyMefField user;
    protected WebProxyMefField tld;

    protected void initFromNamespace(SupportedFormat f) {
      //TODO : improve namespace management
      if (f.getNamespace().equals("destination")) {
        this.address = WebProxyMefField.destinationAddress;
        this.hostName = WebProxyMefField.destinationHostName;
        this.dnsDomain = WebProxyMefField.destinationDnsDomain;
        this.tld = WebProxyMefField.destinationDnsDomainTLD;
        this.ntDomain = WebProxyMefField.destinationNtDomain;
        this.user = WebProxyMefField.destinationUserName;
      } else if (f.getNamespace().equals("source")) {
        this.address = WebProxyMefField.sourceAddress;
        this.hostName = WebProxyMefField.sourceHostName;
        this.dnsDomain = WebProxyMefField.sourceDnsDomain;
        this.tld = null;
        this.ntDomain = WebProxyMefField.sourceNtDomain;
        this.user = WebProxyMefField.sourceUserName;

      } else if (f.getNamespace().equals("device")) {
        this.address = WebProxyMefField.deviceAddress;
        this.hostName = WebProxyMefField.deviceHostName;
        this.dnsDomain = WebProxyMefField.deviceDnsDomain;
        this.tld = null;
        this.ntDomain = WebProxyMefField.deviceNtDomain;
        this.user = null;


      }

    }
}
