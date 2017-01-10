package com.securityx.model.external.msad;

import com.securityx.model.external.ExternalFieldsToIAMDBMefFields;
import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.IAMDBMefField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum MsADToIAMDbMefMappings implements ExternalFieldsToIAMDBMefFields {
DN(IAMDBMefField.canonicalName),
distinguishedName(IAMDBMefField.objectDistinguishedName),
whenChange(IAMDBMefField.lastModificationDate),
displayName(IAMDBMefField.displayName),
objectGUID(IAMDBMefField.objectGUID),
lockoutDuration(IAMDBMefField.lockoutDuration),
lockoutThreshold(IAMDBMefField.lockoutThreshold),
objectSid(IAMDBMefField.objectSid),
objectCategory(IAMDBMefField.objectCategory),
description(IAMDBMefField.objectDescription),
objectDescription(IAMDBMefField.objectDescription),
userAccountControl(IAMDBMefField.userAccountControl),
badPwdCount(IAMDBMefField.badPwdCount),
badPasswordTime(IAMDBMefField.badPasswordTime),
lastLogoff(IAMDBMefField.lastLogoff),
lastLogon(IAMDBMefField.lastLogon),
pwdLastSet(IAMDBMefField.pwdLastSet),
accountExpires(IAMDBMefField.accountExpires),
logonCount(IAMDBMefField.logonCount),
sAMAccountName(IAMDBMefField.samAccountName),
sAMAccountType(IAMDBMefField.samAccountType),
lockoutTime(IAMDBMefField.lockoutTime),
title(IAMDBMefField.title),
department(IAMDBMefField.division),
mail(IAMDBMefField.objectMail),
userWorkstation(IAMDBMefField.userWorkstation),
isCriticalSystemObject(IAMDBMefField.isCriticalSystemObject),
objectClass(IAMDBMefField.objectClass),
name(IAMDBMefField.objectName),
cn(IAMDBMefField.objectCN),
memberOf(IAMDBMefField.memberOf),
primaryGroupID(IAMDBMefField.primaryGroupID),
servicePrincipalName(IAMDBMefField.servicePrincipalName),
userCertificate(IAMDBMefField.userCertificate),
operatingSystem(IAMDBMefField.operatingSystem),
operatingSystemVersion(IAMDBMefField.operatingSystemVersion),
operatingSystemServicePack(IAMDBMefField.operatingSystemServicePack),
serverReferenceBL(IAMDBMefField.serverReferenceBL),
dnsHostName(IAMDBMefField.dnsHostName),
managedBy(IAMDBMefField.managedBy),
rIDSetReferences(IAMDBMefField.rIdSetReferences),
frsComputerReferenceBL(IAMDBMefField.frsComputerReferenceBL),
networkAddress(IAMDBMefField.networkAddress),
whenChanged(IAMDBMefField.lastModificationDate),
whenCreated(IAMDBMefField.creationDate),
manager(IAMDBMefField.objectManager)
;

  private static final Map<String, IAMDBMefField> mappings = new HashMap<String, IAMDBMefField>();
  private final IAMDBMefField mefField;
  private static final String NAMESPACE = "SonicOs";
  private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();

  private MsADToIAMDbMefMappings(IAMDBMefField mefField) {
    this.mefField = mefField;
  }

  private void initialize() {
    for (MsADToIAMDbMefMappings field : values()) {
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
  public IAMDBMefField getMefField(String externalFieldName) {
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
    for (MsADToIAMDbMefMappings field : values()) {
      genericFormatMappings.put(field.getPrettyName(), field);
    }
  }


}
