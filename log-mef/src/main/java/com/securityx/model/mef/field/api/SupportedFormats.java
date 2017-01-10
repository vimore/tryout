package com.securityx.model.mef.field.api;

import com.securityx.model.external.bluecoat.BluecoatMainToMefMappings;

import java.util.*;

public enum SupportedFormats {

  WebProxyMef(WebProxyMefField.values(), WebProxyMefField.class),
  BlueCoat(BluecoatMainToMefMappings.values(), BluecoatMainToMefMappings.class),
  FlowMef(FlowMefField.values(), FlowMefField.class),
  DnsMef(DnsMefField.values(), DnsMefField.class),
  IAMMef(IAMMefField.values(), IAMMefField.class),
  HETMef(HETMefField.values(), HETMefField.class),
  IAMDBMef(IAMDBMefField.values(), IAMDBMefField.class),
  CertMef(CertMefField.values(), CertMefField.class),
  HostCpuMef(HostCpuMefField.values(),HostCpuMefField.class),
  HostPortMef(HostPortMefField.values(), HostPortMefField.class),
  HostProcessMef(HostProcessMefField.values(), HostProcessMefField.class),
  HostJobMef(HostJobMefField.values(), HostJobMefField.class),
  UETMef(UETMefField.values(),UETMefField.class),
  LogCollectionMef(LogCollectionMefField.values(), LogCollectionMefField.class),
  SIEMIncidentMef(SIEMIncidentMefField.values(), SIEMIncidentMefField.class);

  public static final String AVROOUTPACKAGE = "com.securityx.mef.schema";
  private final Collection<SupportedFormat> supportedFormats;
  private final Class enumClass;

  private SupportedFormats(SupportedFormat[] formats, Class enumClass) {
    this.enumClass = enumClass;
    Map<Class, SupportedFormat> keepOnlyOnePerEnumClass = new HashMap<Class, SupportedFormat>();
    for (SupportedFormat format : formats) {
      if (format.getClass().isEnum()) {
        keepOnlyOnePerEnumClass.put(format.getClass(), format);
      } else {
        throw new IllegalArgumentException("SupportedFormats must be an enum implementation.");
      }
    }
    this.supportedFormats = keepOnlyOnePerEnumClass.values();
  }

  public Collection<SupportedFormat> getSupportedFormatFields() {
    return supportedFormats;
  }
  
  public Class getEnumClass(){
    return this.enumClass;
  }
  public String getAvroDatumClass(){
    return AVROOUTPACKAGE+"."+this.name();
  }

  public static List<SupportedFormats> genSupportedFormatList(){
    List<SupportedFormats> out = new ArrayList<SupportedFormats>();

    for (SupportedFormats f : SupportedFormats.values()){
      out.add(f) ;
    }
    return out;
  }
  

}
