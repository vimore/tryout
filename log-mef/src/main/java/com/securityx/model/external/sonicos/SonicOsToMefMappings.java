package com.securityx.model.external.sonicos;

import com.securityx.model.external.ExternalFieldsToWebProxyMefFields;
import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.WebProxyMefField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum SonicOsToMefMappings implements ExternalFieldsToWebProxyMefFields {
  c(WebProxyMefField.deviceEventCategory),
  dst(WebProxyMefField.destinationIPAndInterfaceAndName),
  fw(WebProxyMefField.deviceNameOrIp),
  m(WebProxyMefField.cefSignatureId),
  msg(WebProxyMefField.cefEventName),
  n(WebProxyMefField.baseEventCount),
  note(WebProxyMefField.reason),
  pri(WebProxyMefField.cefEventSeverity),
  proto(WebProxyMefField.transportProtocol),
  rcvd(WebProxyMefField.bytesIn),
  sent(WebProxyMefField.bytesOut),
  src(WebProxyMefField.sourceIPAndInterfaceAndName),
  time(WebProxyMefField.startTime);

  private static final Map<String, WebProxyMefField> mappings = new HashMap<String, WebProxyMefField>();
  private final WebProxyMefField mefField;
  private static final String NAMESPACE = "SonicOs";
  private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();

  private SonicOsToMefMappings(WebProxyMefField mefField) {
    this.mefField = mefField;
  }

  private void initialize() {
    for (SonicOsToMefMappings field : values()) {
      mappings.put(field.name(), field.mefField);
    }
  }

  @Override
  public Set<String> getExternalFieldNames() {
    if (mappings.isEmpty()) {
      initialize();
    }
    return mappings.keySet();
  }

  @Override
  public WebProxyMefField getMefField(String externalFieldName) {
    return mappings.get(externalFieldName);
  }

  @Override
  public String getPrettyName() {
    return this.name();
  }

  @Override
  public GenericFormat getByPrettyName(String prettyName) {
    if (genericFormatMappings.isEmpty()) {
      initializeSupportedFormatMapping();
    }
    return this.genericFormatMappings.get(prettyName);
  }


  private void initializeSupportedFormatMapping() {
    for (SonicOsToMefMappings field : values()) {
      genericFormatMappings.put(field.getPrettyName(), field);
    }
  }


}
