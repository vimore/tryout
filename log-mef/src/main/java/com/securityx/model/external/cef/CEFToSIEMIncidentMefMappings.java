package com.securityx.model.external.cef;

import com.securityx.model.external.ExternalFieldsToSIEMIncidentMefFields;
import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.SIEMIncidentMefField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * defines CEF aliases and the related MEfField
 * @author jyrialhon
 */
public enum CEFToSIEMIncidentMefMappings implements ExternalFieldsToSIEMIncidentMefFields {
  act("act",null),
  agt("agt", SIEMIncidentMefField.agentAddress),
  ahost("ahost",SIEMIncidentMefField.agentHostName),
  aid("aid",null),
  amac("amac",null),
  app("app",null),
  art("art",null),
  at("at",null),
  atz("atz",null),
  av("av",null),
  c6a1("c6a1",null),
  c6a1Label("c6a1Label",null),
  c6a2("c6a2",null),
  c6a2Label("c6a2Label",null),
  c6a3("c6a3",null),
  c6a3Label("c6a3Label",null),
  c6a4("c6a4",null),
  c6a4Label("c6a4Label",null),
  cat("cat",SIEMIncidentMefField.deviceEventCategory),
  cfp1("cfp1",null),
  cfp1Label("cfp1Label",null),
  cfp2("cfp2",null),
  cfp2Label("cfp2Label",null),
  cfp3("cfp3",null),
  cfp3Label("cfp3Label",null),
  cfp4("cfp4",null),
  cfp4Label("cfp4Label",null),
  cn1("cn1",null),
  cn1Label("cn1Label",null),
  cn2("cn2",null),
  cn2Label("cn2Label",null),
  cn3("cn3",null),
  cn3Label("cn3Label",null),
  cnt("cnt",SIEMIncidentMefField.baseEventCount),
  cs1("cs1",null),
  cs1Label("cs1Label",null),
  cs2("cs2",null),
  cs2Label("cs2Label",null),
  cs3("cs3",null),
  cs3Label("cs3Label",null),
  cs4("cs4",null),
  cs4Label("cs4Label",null),
  cs5("cs5",null),
  cs5Label("cs5Label",null),
  cs6("cs6",null),
  cs6Label("cs6Label",null),
  dhost("dhost",null),
  dlat("dlat",null),
  dlong("dlong",null),
  dmac("dmac",null),
  dntdom("dntdom",null),
  dpid("dpid",null),
  dpriv("dpriv",null),
  dproc("dproc",null),
  dpt("dpt",SIEMIncidentMefField.destinationPort),
  dst("dst",null),
  dtz("dtz",null),
  duid("duid",null),
  duser("duser",null),
  dvc("dvc",SIEMIncidentMefField.deviceAddress),
  dvchost("dvchost",SIEMIncidentMefField.deviceHostName),
  dvcmac("dvcmac",null),
  dvcpid("dvcpid",null),
  end("end",SIEMIncidentMefField.endTime),
  fname("fname",null),
  fsize("fsize",null),
  in("in",null),
  msg("msg",null),
  out("out",null),
  outcome("outcome",null),
  proto("proto",null),
  request("request",null),
  rt("rt",SIEMIncidentMefField.deviceReceiptTime),
  shost("shost",null),
  slat("slat",null),
  slong("slong",null),
  smac("smac",null),
  sntdom("sntdom",null),
  spid("spid",null),
  spriv("spriv",null),
  sproc("sproc",null),
  spt("spt",null),
  src("src",SIEMIncidentMefField.sourceAddress),
  start("start",SIEMIncidentMefField.startTime),
  suid("suid",null),
  suser("suser",null),


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

  private static final Map<String, SIEMIncidentMefField> mappings = new HashMap<String, SIEMIncidentMefField>();
  private static final String NAMESPACE = "cef";
  private final SIEMIncidentMefField mefField;
  private final String fieldName;
  private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();

  private CEFToSIEMIncidentMefMappings(String fieldName, SIEMIncidentMefField mefField) {
    this.mefField = mefField;
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  private void initialize() {
    
    for (CEFToSIEMIncidentMefMappings field : values()) {
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
  public SIEMIncidentMefField getMefField(String externalFieldName) {
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
    for (CEFToSIEMIncidentMefMappings field : values()) {
      genericFormatMappings.put(field.getPrettyName(), field);
    }
  }



}
