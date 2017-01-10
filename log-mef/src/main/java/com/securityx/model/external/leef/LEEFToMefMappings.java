package com.securityx.model.external.leef;

import com.securityx.model.external.ExternalFieldsToWebProxyMefFields;
import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.WebProxyMefField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * defines CEF aliases and the related MEfField
 * @author jyrialhon
 */
public enum LEEFToMefMappings implements ExternalFieldsToWebProxyMefFields {
  sev("sev",null),
  cat("cat",WebProxyMefField.deviceEventCategory),
  usrName("usrName",WebProxyMefField.sourceUserName),
  src("src",WebProxyMefField.sourceAddress),
  srcPort("srcPort",WebProxyMefField.sourcePort),
  srcBytes("srcBytes",WebProxyMefField.bytesIn),
  dstBytes("dstBytes",WebProxyMefField.bytesOut),
  dst("dst",WebProxyMefField.destinationAddress),
  dstPort("dstPort",WebProxyMefField.destinationPort),
  proxyStatusCode("proxyStatus-code",WebProxyMefField.cefSignatureId),
  serverStatusCode("serverStatus-code",null),
  duration("duration",null),
  method("method",WebProxyMefField.requestMethod),
  disposition("disposition",null),
  contentType("contentType",WebProxyMefField.responseContentType),
  reason("reason",null),
  policy("policy",WebProxyMefField.devicePolicyAction),
  role("role",null),
  userAgent("userAgent",WebProxyMefField.requestClientApplication),
  url("url",null)

;

  private static final Map<String, WebProxyMefField> mappings = new HashMap<String, WebProxyMefField>();
  private static final String NAMESPACE = "leef";
  private final WebProxyMefField mefField;
  private final String fieldName;
  private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();

  private LEEFToMefMappings(String fieldName, WebProxyMefField mefField) {
    this.mefField = mefField;
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  private void initialize() {
    
    for (LEEFToMefMappings field : values()) {
      mappings.put( field.getFieldName(), field.mefField);
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
    return this.fieldName;
  }

  @Override
  public GenericFormat getByPrettyName(String prettyName) {
    if (genericFormatMappings.isEmpty()) {
      initializeSupportedFormatMapping();
    }
    return this.genericFormatMappings.get(prettyName);
  }


  private void initializeSupportedFormatMapping() {
    for (LEEFToMefMappings field : values()) {
      genericFormatMappings.put(field.getPrettyName(), field);
    }
  }



}
