package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.MefInetNameOrIpConstraint;
import com.securityx.model.mef.field.constraint.MefIntegerConstraint;
import com.securityx.model.mef.field.constraint.MefMilliSecTimeStampConstraint;
import com.securityx.model.mef.field.constraint.MefStringConstraint;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum WebProxyDpiField implements SupportedFormat {

//WebProxyDpi fields
cefSignatureId(1, MefFieldType.data, "cef", "DPI http field responseStatus",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setCefSignatureId", CharSequence.class, true),
requestScheme(1, MefFieldType.data, "request", "DPI http field application",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestScheme", CharSequence.class, true),
destinationNameOrIp(1, MefFieldType.data, "destination", "DPI http field destinationNameOrIp",
Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class,"setDestinationNameOrIp", CharSequence.class, true),
DestinationPort(1, MefFieldType.data, "destination", "DPI http field DestinationPort",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDestinationPort", CharSequence.class, true),
dpiFlowId(1, MefFieldType.data, null, "DPI http field dpiFlowId",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDpiFlowId", CharSequence.class, true),
dpiSignatureId(1, MefFieldType.data, null, "DPI http field dpiSignatureId",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDpiSignatureId", CharSequence.class, true),
requestClientApplication(1, MefFieldType.data, null, "DPI http field requestClientApplication",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestClientApplication", CharSequence.class, true),
bytesIn(1, MefFieldType.data, null, "DPI http field requestContentLength",
Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), StringMarshall.class,"setBytesOut", Integer.class, true),
bytesOut(1, MefFieldType.data, null, "DPI http field responseContentLength",
Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), StringMarshall.class,"setBytesOut", Integer.class, true),
requestHost(1, MefFieldType.data, null, "DPI http field requestHost",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestHost", CharSequence.class, true),
requestMethod(1, MefFieldType.data, null, "DPI http field requestMethod",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestMethod", CharSequence.class, true),
requestParams(1, MefFieldType.data, "request", "DPI http field requestParams",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestParams", CharSequence.class, true),
requestQuery(1, MefFieldType.data, "request", "from DPI http field requestQuery",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestQuery", CharSequence.class, true),
requestPath(1, MefFieldType.data, "request", "from DPI http field requestQuery",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setRequestPath", CharSequence.class, true),
responseContentLength(1, MefFieldType.data, null, "DPI http field responseContentLength",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setResponseContentLength", CharSequence.class, true),
responseContentType(1, MefFieldType.data, null, "DPI http field responseContentType",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setResponseContentType", CharSequence.class, true),
responseServer(1, MefFieldType.data, null, "DPI http field responseServer",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setResponseServer", CharSequence.class, true),
responseStatus(1, MefFieldType.data, null, "DPI http field responseStatus",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setResponseStatus", CharSequence.class, true),
sourceNameOrIp(1, MefFieldType.data, "source", "DPI http field sourceNameOrIp",
Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class,"setSourceNameOrIp", CharSequence.class, true),
sourcePort(1, MefFieldType.data, "source", "DPI http field sourcePort",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setSourcePort", CharSequence.class, true),
startTime(1, MefFieldType.data, null, "DPI http field startTime",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setStartTime", CharSequence.class, true),
transportProtocol(1, MefFieldType.data, null, "DPI http field transportProtocol",
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setTransportProtocol", CharSequence.class, true),



deviceNameOrIp(1, MefFieldType.data, "device", "CEF device host and Ip  wrapper.",
Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class,"setDeviceNameOrIp", CharSequence.class, true),
logCollectionCategory(1, MefFieldType.metaData,"LogCollection", "Log Collection channel category.", 
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setLogCollectionCategory", CharSequence.class, true),
logCollectionContainer(1, MefFieldType.metaData,"LogCollection", "Log Collection container", 
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setLogCollectionContainer", CharSequence.class, true),
logCollectionHost(1, MefFieldType.metaData,"LogCollection", "Log Collection host", 
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setLogCollectionHost", CharSequence.class, false),
logCollectionType(1, MefFieldType.metaData,"LogCollection", "Log Collection channel Type.", 
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setLogCollectionType", CharSequence.class, false),
logCollectionId(1, MefFieldType.metaData,"LogCollection", "Log Collection record id", 
Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class,"setLogCollectionId", Integer.class, false),
logCollectionTime(1, MefFieldType.metaData,"LogCollection", "Log Collection timestamp", 
Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class,"setLogCollectionTime", Long.class, true),
// logsource fields
logSourceType(1, MefFieldType.metaData,"LogCollection", "Log source Type.", 
Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setLogSourceType", CharSequence.class, true),

// others
//unixEpochTimestampMillis(1, MefFieldType.data, null, "a generic unix ecoch timestamp in millis.",
//Arrays.<MefFieldConstrait>asList(new MefIsEpochTimestampConstraint()), LongMarshall.class,"setUnixEpochTimestampMillis", Long.class, true),
//userIpAddress(1, MefFieldType.data, null, "The ipaddress for a given user.",
//Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class,"setUserIpAddress", CharSequence.class, true),
//_sourceUserName(1, MefFieldType.data, null, "a test field with length enforcing.",
//Arrays.<MefFieldConstrait>asList(new MefIsUserNameConstraint()), StringMarshall.class,"set_sourceUserName", CharSequence.class, true),

;

  private final int numberOfValues;
  private final MefFieldType mefFieldType;
  private final String description;
  private final String namespace;
  private final List<MefFieldConstrait> contraints;
  private final boolean nullable;
  private final Class marshaller;
  private final Class argClass;
  private final String setter;
  private final String avroName;
  private final Map<String,SupportedFormat> supportedFormatfields = new HashMap<String, SupportedFormat>();
  private Map<String, String> stack=null;
  private String avroObjectClass = null;
  private WebProxyDpiField(int numberOfValues,
          MefFieldType mefFieldType,
          String namespace,
          String description,
          List<MefFieldConstrait> contraints,
          Class marshaller,
          String setter, 
          Class argClass,
          boolean nullable) {
    this.numberOfValues = numberOfValues;
    this.mefFieldType = mefFieldType;
    this.description = description;
    this.namespace = namespace;
    this.contraints = contraints;
    this.nullable = nullable;
    this.marshaller = marshaller;
    this.setter = setter;
    this.argClass = argClass;
    this.avroName = this.name();

  }
  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public int getNumberOfValues() {
    return numberOfValues;
  }

   @Override
  public String getDescription() {
    return description;
  }

  @Override
  public List<MefFieldConstrait> getContraints() {
    return contraints;
  }

  @Override
  public boolean isNullable() {
    return nullable;
  }

  public MefFieldMarshall getMarshaller(Class _class) {
    if (this.marshaller == StringMarshall.class) {
      return new StringMarshall();
    } else if (this.marshaller == LongMarshall.class) {
      return new LongMarshall();
    } else if (this.marshaller == IntMarshall.class) {
      return new IntMarshall();
    } else {
      throw new UnsupportedOperationException("not marshaller for type " + this.marshaller.toString());
    }
  }

  @Override
  public String getPrettyName() {
    //enum name is field name for MefField
    return this.name();
  }

  @Override
  public String getSetter() {
    return this.setter;
  }

  @Override
  public Class getArgClass() {
    return this.argClass;
  }

  @Override
  public Collection<SupportedFormat> getSupportedFormatfields() {
    if (this.supportedFormatfields.isEmpty()){
      initSupportedFormatFields();
    }
    return this.supportedFormatfields.values();
  }

  private void initSupportedFormatFields() {
    for (WebProxyDpiField field : values()){
      if (null != field.getPrettyName()){
        this.supportedFormatfields.put(field.getPrettyName(), field);
      }
    }
  }

  @Override
  public String getAvroName() {
    return this.avroName;
  }

  @Override
  public String getAvroObjectClass(SupportedFormats prefix) {
    if (null == this.avroObjectClass){
      this.avroObjectClass = SupportedFormatIntrospectionUtils.genAvroObjectClass(namespace, prefix.name());
    }
    return this.avroObjectClass;
  }
  
  @Override
  public Map<String, String> getStack(String prefix){
    if (null == this.stack){
      this.stack= SupportedFormatIntrospectionUtils.getStack(prefix,namespace);
    }
    return stack;
  }

  @Override
  public SupportedFormat getByPrettyName(String prettyName) {
    //MefField Case :  prettyName = name
    SupportedFormat format = null;
    try{
      format = WebProxyDpiField.valueOf(prettyName);
    }catch (IllegalArgumentException ex){
      // silent
    }
    return format;
  }
}
