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
public enum E8CustomCEFToMefMappings implements ExternalFieldsToWebProxyMefFields {
  e8ProcessID("e8ProcessID",null),
  e8LogonProcess("e8LogonProcess",null),
  e8PackageName("e8PackageName",null),
  e8Status("e8Status",null),
  e8SubStatus("e8SubStatus",null),
  e8TargetServerName("e8TargetServerName",null),
  e8SAMAccountName("e8SAMAccountName",null),
  e8DisplayName("e8DisplayName",null),
  e8UserPrincipalName("e8UserPrincipalName",null),
  e8HomeDir("e8HomeDir",null),
  e8HomeDrive("e8HomeDrive",null),
  e8ScriptPath("e8ScriptPath",null),
  e8ProfilePath("e8ProfilePath",null),
  e8UserWorkstation("e8UserWorkstation",null),
  e8PasswdLastSet("e8PasswdLastSet",null),
  e8AccountExpires("e8AccountExpires",null),
  e8PrimaryGroupID("e8PrimaryGroupID",null),
  e8AllowedToDelegateTo("e8AllowedToDelegateTo",null),
  e8OldUACValue("e8OldUACValue",null),
  e8NewUACValue("e8NewUACValue",null),
  e8UserAccountControl("e8UserAccountControl",null),
  e8UserParameters("e8UserParameters",null),
  e8SIDHistory("e8SIDHistory",null),
  e8LogonHours("e8LogonHours",null),
  e8GroupName("e8GroupName",null),
  e8GroupDomain("e8GroupDomain",null),
  e8ShareName("e8ShareName",null),
  e8SrcProcessID("e8SrcProcessID",null),
  e8ObjName("e8ObjName",null),
  e8ObjServer("e8ObjServer",null),
  e8ObjType("e8ObjType", null),
  e8Accesses("e8Accesses",null),
  e8SuppliedRealmName("e8SuppliedRealmName", null),
  e8UserID("e8UserID", null),
  e8ServiceName("e8ServiceName", null),
  e8ServiceID("e8ServiceID", null),
  e8ClientAddress("e8ClientAddress", null),
  e8ClientPort("e8ClientPort", null),
  e8TicketOptions("e8TicketOptions", null),
  e8ResultCode("e8ResultCode", null),
  e8TicketEncryptionType("e8TicketEncryptionType", null),
  e8PreAuthType("e8PreAuthType", null),
  e8SrcAddr("e8SrcAddr", null),
  deviceProcessID("deviceProcessID", null),
  e8SrcAcc("e8SrcAcc",null)

;

  private static final Map<String, WebProxyMefField> mappings = new HashMap<String, WebProxyMefField>();
  private static final String NAMESPACE = "e8customcef";
  private final WebProxyMefField mefField;
  private final String fieldName;
  private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();

  private E8CustomCEFToMefMappings(String fieldName, WebProxyMefField mefField) {
    this.mefField = mefField;
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  private void initialize() {
    
    for (E8CustomCEFToMefMappings field : values()) {
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
    for (E8CustomCEFToMefMappings field : values()) {
      genericFormatMappings.put(field.getPrettyName(), field);
    }
  }



}
