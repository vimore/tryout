package com.securityx.model.external.cef;

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
public enum CEFToMefMappings implements ExternalFieldsToWebProxyMefFields {
  ahost("ahost",null),
  agt("agt", null),
  agentZoneURI("agentZoneURI", null),
  av("av",null),
  app("app", WebProxyMefField.applicationProtocol),
  at("at",null),
  atz("atz",null),
  aid("aid", null),
  cnt("cnt", WebProxyMefField.baseEventCount),
  c6a2("c6a2", null),
  c6a2Label("c6a2Label",null),
  in("in", WebProxyMefField.bytesIn),
  out("out", WebProxyMefField.bytesOut),
  dst("dst", WebProxyMefField.destinationAddress),
  dhost("dhost", WebProxyMefField.destinationHostName),
  dmac("dmac", WebProxyMefField.destinationMacAddress),
  dntdom("dntdom", WebProxyMefField.destinationNtDomain),
  dpt("dpt", WebProxyMefField.destinationPort),
  dpid("dpid", WebProxyMefField.destinationProcessId),
  dproc("dproc", WebProxyMefField.destinationProcessName),
  duid("duid", WebProxyMefField.destinationUserId),
  duser("duser", WebProxyMefField.destinationUserName),
  dtz("dtz", null),
  act("act", WebProxyMefField.deviceAction),
  dvc("dvc", WebProxyMefField.deviceAddress),
  cat("cat", WebProxyMefField.deviceEventCategory),
  dvchost("dvchost", WebProxyMefField.deviceNameOrIp),
  dvcpid("dvcpid", WebProxyMefField.deviceProcessId),
  end("end", WebProxyMefField.endTime),
  outcome("outcome", WebProxyMefField.eventOutcome),
  fname("fname", WebProxyMefField.fileName),
  fsize("fsize", WebProxyMefField.fileSize),
  msg("msg", WebProxyMefField.message),
  rt("rt", WebProxyMefField.receiptTime),
  /**
   * art : added to match Snare cef log examples looks to be start
   */
  art("art", null),
  request("request", WebProxyMefField.requestURL),
  src("src", WebProxyMefField.sourceAddress),
  shost("shost", WebProxyMefField.sourceHostName),
  smac("smac", WebProxyMefField.sourceMacAddress),
  sntdom("sntdom", WebProxyMefField.sourceNtDomain),
  spt("spt", WebProxyMefField.sourcePort),
  spid("spid", WebProxyMefField.sourceProcessId),
  sproc("sproc", WebProxyMefField.sourceProcessName),
  suid("suid", WebProxyMefField.sourceUserId),
  suser("suser", WebProxyMefField.sourceUserName),
  start("start", WebProxyMefField.startTime),
  proto("proto", WebProxyMefField.transportProtocol),
  cs1("cs1", WebProxyMefField.deviceCustomString1),
  cs1Label("cs1Label", WebProxyMefField.deviceCustomString1Label),
  cs2("cs2", WebProxyMefField.deviceCustomString2),
  cs2Label("cs2Label", WebProxyMefField.deviceCustomString2Label),
  cs3("cs3", WebProxyMefField.deviceCustomString3),
  cs3Label("cs3Label", WebProxyMefField.deviceCustomString3Label),
  cs4("cs4", WebProxyMefField.deviceCustomString4),
  cs4Label("cs4Label", WebProxyMefField.deviceCustomString4Label),
  cs5("cs5", null),
  cs5Label("cs5Label", null),
  cs6("cs6", null),
  cs6Label("cs6Label", null),
  cn1Label("cn1Label", null),
  cn2Label("cn2Label", null),
  cn3Label("cn3Label", null),
  cn1("cn1", null),
  cn2("cn2", null),
  cn3("cn3", null),
  dpriv("dpriv", null),

  /**
   * cef event we do not use for the moment
   * added to provide the right pattern set for cefExtensionData parsing (see cefKvpSplitter command)
   */
  categorySignificance("categorySignificance", null),
  categoryBehavior("categoryBehavior", null),
  categoryDeviceGroup("categoryDeviceGroup", null),
  catdt("catdt", null),
  categoryOutcome("categoryOutcome", null),
  categoryObject("categoryObject", null),

  _cefVer("_cefVer", null),
  ad_ExtraParam0("ad\\.ExtraParam0", null),
  ad_ExtraParam1("ad\\.ExtraParam1", null),
  ad_ExtraParam2("ad\\.ExtraParam2", null),
  ad_message("ad\\.message", null),
  deviceSeverity("deviceSeverity", null)

;

  private static final Map<String, WebProxyMefField> mappings = new HashMap<String, WebProxyMefField>();
  private static final String NAMESPACE = "cef";
  private final WebProxyMefField mefField;
  private final String fieldName;
  private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();

  private CEFToMefMappings(String fieldName, WebProxyMefField mefField) {
    this.mefField = mefField;
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  private void initialize() {
    
    for (CEFToMefMappings field : values()) {
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
    for (CEFToMefMappings field : values()) {
      genericFormatMappings.put(field.getPrettyName(), field);
    }
  }



}
