package com.securityx.modelfeature.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

public class ImpalaColumnNameMapper {
    private BiMap<String, String> iamParquetBiMap = HashBiMap.create();
    private BiMap<String, String> webProxyParquetBiMap = HashBiMap.create();
    private static volatile ImpalaColumnNameMapper singleton = new ImpalaColumnNameMapper();
    private ImpalaColumnNameMapper(){
        initIamParquetBiMap();
        initwebProxyParquetBiMap();
    }
    private void initwebProxyParquetBiMap(){
        webProxyParquetBiMap.put("uuid","uuid");
        webProxyParquetBiMap.put("outputformat","outputFormat");
        webProxyParquetBiMap.put("logsourcetype","logSourceType");
        webProxyParquetBiMap.put("externallogsourcetype","externalLogSourceType");
        webProxyParquetBiMap.put("rawlog","rawLog");
        webProxyParquetBiMap.put("starttimeiso","startTimeISO");
        webProxyParquetBiMap.put("applicationprotocol","applicationProtocol");
        webProxyParquetBiMap.put("bytesin","bytesIn");
        webProxyParquetBiMap.put("bytesout","bytesOut");
        webProxyParquetBiMap.put("cefsignatureid","cefSignatureId");
        webProxyParquetBiMap.put("destinationaddress","destinationAddress");
        webProxyParquetBiMap.put("destinationdnsdomain","destinationDnsDomain");
        webProxyParquetBiMap.put("destinationhostname","destinationHostName");
        webProxyParquetBiMap.put("destinationmacaddress","destinationMacAddress");
        webProxyParquetBiMap.put("destinationnameorip","destinationNameOrIp");
        webProxyParquetBiMap.put("destinationport","destinationPort");
        webProxyParquetBiMap.put("deviceaction","deviceAction");
        webProxyParquetBiMap.put("deviceaddress","deviceAddress");
        webProxyParquetBiMap.put("devicednsdomain","deviceDnsDomain");
        webProxyParquetBiMap.put("deviceeventcategory","deviceEventCategory");
        webProxyParquetBiMap.put("devicehostname","deviceHostName");
        webProxyParquetBiMap.put("deviceprocessname","deviceProcessName");
        webProxyParquetBiMap.put("devicenameorip","deviceNameOrIp");
        webProxyParquetBiMap.put("endtime","endTime");
        webProxyParquetBiMap.put("message","message");
        webProxyParquetBiMap.put("requestmethod","requestMethod");
        webProxyParquetBiMap.put("requestclientapplication","requestClientApplication");
        webProxyParquetBiMap.put("sourceaddress","sourceAddress");
        webProxyParquetBiMap.put("sourcednsdomain","sourceDnsDomain");
        webProxyParquetBiMap.put("sourcehostname","sourceHostName");
        webProxyParquetBiMap.put("sourcemacaddress","sourceMacAddress");
        webProxyParquetBiMap.put("sourcenameorip","sourceNameOrIp");
        webProxyParquetBiMap.put("sourceport","sourcePort");
        webProxyParquetBiMap.put("sourceusername","sourceUsername");
        webProxyParquetBiMap.put("starttime","startTime");
        webProxyParquetBiMap.put("transportprotocol","transportProtocol");
        webProxyParquetBiMap.put("responsecontenttype","responseContentType");
        webProxyParquetBiMap.put("reason","reason");
        webProxyParquetBiMap.put("requestscheme","requestScheme");
        webProxyParquetBiMap.put("requestpath","requestPath");
        webProxyParquetBiMap.put("requestquery","requestQuery");
        webProxyParquetBiMap.put("requestreferer","requestReferer");
        webProxyParquetBiMap.put("devicepolicyaction","devicePolicyAction");
        webProxyParquetBiMap.put("destinationurl", "destinationUrl");
        webProxyParquetBiMap.put("sourceusernameauto", "sourceUserNameAuto");
        webProxyParquetBiMap.put("sourcenameoripauto", "sourceNameOrIpAuto");
        webProxyParquetBiMap.put("destinationusernameauto", "destinationUserNameAuto");
        webProxyParquetBiMap.put("destinationnameoripauto", "destinationNameOrIpAuto");
        webProxyParquetBiMap.put("destinationhostnameauto", "destinationHostNameAuto");


        webProxyParquetBiMap.put("dt","dt");
    }
    private void initIamParquetBiMap(){
        iamParquetBiMap.put("uuid","uuid");
        iamParquetBiMap.put("outputformat","outputFormat");
        iamParquetBiMap.put("logsourcetype","logSourceType");
        iamParquetBiMap.put("externallogsourcetype","externalLogSourceType");
        iamParquetBiMap.put("rawlog","rawLog");
        iamParquetBiMap.put("starttimeiso","startTimeISO");
        iamParquetBiMap.put("auditsource","auditSource");
        iamParquetBiMap.put("cefeventname","cefEventName");
        iamParquetBiMap.put("cefsignatureid","cefSignatureId");
        iamParquetBiMap.put("destinationlogonguid","destinationLogonGUID");
        iamParquetBiMap.put("destinationlogonid","destinationLogonID");
        iamParquetBiMap.put("destinationnameorip","destinationNameOrIp");
        iamParquetBiMap.put("destinationprocessname","destinationProcessName");
        iamParquetBiMap.put("destinationsharename","destinationShareName");
        iamParquetBiMap.put("destinationgroup","destinationGroup");
        iamParquetBiMap.put("destinationgroupsecurityid","destinationGroupSecurityID");
        iamParquetBiMap.put("destinationsecurityid","destinationSecurityID");
        iamParquetBiMap.put("destinationusername","destinationUserName");
        iamParquetBiMap.put("devicenameorip","deviceNameOrIp");
        iamParquetBiMap.put("deviceusername","deviceUsername");
        iamParquetBiMap.put("eventsidtype","eventsIdType");
        iamParquetBiMap.put("sourceaddress","sourceAddress");
        iamParquetBiMap.put("sourcelogonid","sourceLogonID");
        iamParquetBiMap.put("sourcelogontype","sourceLogonType");
        iamParquetBiMap.put("sourcenameorip","sourceNameOrIp");
        iamParquetBiMap.put("sourceprocessid","sourceProcessID");
        iamParquetBiMap.put("sourceprocessname","sourceProcessName");
        iamParquetBiMap.put("destinationservicesecurityid","destinationServiceSecurityId");
        iamParquetBiMap.put("destinationservicename","destinationServiceName");
        iamParquetBiMap.put("sourcesecurityid","sourceSecurityID");
        iamParquetBiMap.put("sourceusername","sourceUserName");
        iamParquetBiMap.put("starttime","startTime");
        iamParquetBiMap.put("adaccountexpires","adaccountExpires");
        iamParquetBiMap.put("adallowedtodelegateto","adAllowedToDelegateTo");
        iamParquetBiMap.put("authenticationpackage","authenticationPackage");
        iamParquetBiMap.put("certificateissuer","certificateIssuer");
        iamParquetBiMap.put("certificateserialnumber","certificateSerialNumber");
        iamParquetBiMap.put("certificatethumbprint","certificateThumbPrint");
        iamParquetBiMap.put("desiredaccess","desiredAccess");
        iamParquetBiMap.put("destinationntdomain","destinationNtDomain");
        iamParquetBiMap.put("destinationobjecthandle","destinationObjectHandle");
        iamParquetBiMap.put("destinationobjectname","destinationObjectName");
        iamParquetBiMap.put("destinationobjectserver","destinationObjectServer");
        iamParquetBiMap.put("destinationobjecttype","destinationObjectType");
        iamParquetBiMap.put("destinationserviceserver","destinationServiceServer");
        iamParquetBiMap.put("addisplayname","adDisplayName");
        iamParquetBiMap.put("addnshostname","adDnsHostName");
        iamParquetBiMap.put("adhomedirectory","adHomeDirectory");
        iamParquetBiMap.put("adhomedrive","adHomeDrive");
        iamParquetBiMap.put("logonprocess","logonProcess");
        iamParquetBiMap.put("transitedservice","transitedService");
        iamParquetBiMap.put("adolduacvalue","adOldUacValue");
        iamParquetBiMap.put("packagename","packageName");
        iamParquetBiMap.put("adpasswdlastset","adPasswdLastSet");
        iamParquetBiMap.put("preauthenticationtype","preauthenticationType");
        iamParquetBiMap.put("adprimarygroupid","adPrimaryGroupId");
        iamParquetBiMap.put("privileges","privileges");
        iamParquetBiMap.put("adprofilepath","adProfilePath");
        iamParquetBiMap.put("resultcode","resultCode");
        iamParquetBiMap.put("adsamaccountname","adSamAccountName");
        iamParquetBiMap.put("adscriptpath","adScriptPath");
        iamParquetBiMap.put("adserverprincipalnames","adServerPrincipalNames");
        iamParquetBiMap.put("adsidhistory","adSidHistory");
        iamParquetBiMap.put("sourcentdomain","sourceNtDomain");
        iamParquetBiMap.put("sourcelogonguid","sourceLogonGUID");
        iamParquetBiMap.put("status","status");
        iamParquetBiMap.put("substatus","subStatus");
        iamParquetBiMap.put("ticketencryptiontype","ticketEncryptionType");
        iamParquetBiMap.put("ticketoptions","ticketOptions");
        iamParquetBiMap.put("aduseraccountcontrol","adUserAccountControl");
        iamParquetBiMap.put("aduserparameters","adUserParameters");
        iamParquetBiMap.put("aduserprincipalname","adUserPrincipalName");
        iamParquetBiMap.put("aduserworkstation","adUserWorkstation");
        iamParquetBiMap.put("eventseverity","eventSeverity");
        iamParquetBiMap.put("sourceport","sourcePort");
        iamParquetBiMap.put("keylength","keyLength");
        iamParquetBiMap.put("deviceaddress","deviceAddress");
        iamParquetBiMap.put("devicehostname","deviceHostName");
        iamParquetBiMap.put("destinationhostname","destinationHostName");
        iamParquetBiMap.put("sourcehostname","sourceHostname");
        iamParquetBiMap.put("devicednsdomain","deviceDnsDomain");
        iamParquetBiMap.put("destinationdnsdomain","destinationDnsDomain");
        iamParquetBiMap.put("sourcednsdomain","sourceDnsDomain");
        iamParquetBiMap.put("destinationaddress","destinationAddress");
        iamParquetBiMap.put("destinationport","destinationPort");
        iamParquetBiMap.put("eventlogtype","eventLogType");
        iamParquetBiMap.put("dt","dt");
        iamParquetBiMap.put("destinationurl", "destinationUrl");
        iamParquetBiMap.put("sourceusernameauto", "sourceUserNameAuto");
        iamParquetBiMap.put("sourcenameoripauto", "sourceNameOrIpAuto");
        iamParquetBiMap.put("destinationusernameauto", "destinationUserNameAuto");
        iamParquetBiMap.put("destinationnameoripauto", "destinationNameOrIpAuto");
        iamParquetBiMap.put("destinationhostnameauto", "destinationHostNameAuto");
    }
    public static ImpalaColumnNameMapper createInstance(){
        if(singleton == null){
            synchronized (ImpalaColumnNameMapper.class) {
                if(singleton == null){
                    singleton = new ImpalaColumnNameMapper();
                }
            }
        }
        return singleton;
    }
    public Map<String, String> mapWebProxyColumnNames(Map<String,String> input){
        return mapColumnNames(input, webProxyParquetBiMap);
    }
    public Map<String, String> mapIamColumnNames(Map<String,String> input){
        return mapColumnNames(input, iamParquetBiMap);
    }
    public String mapWebProxyColumnName(String input){
        return webProxyParquetBiMap.get(input);
    }
    public String mapIamColumnName(String input){
        return iamParquetBiMap.get(input);
    }
    private Map<String, String> mapColumnNames(Map<String,String> input, BiMap<String,String> columnNames){
        Map<String, String> ret = new HashMap<>();
        input.keySet().forEach(key -> {
            String newKey = columnNames.get(key);
            ret.put(newKey, input.get(key));
        });
        return ret;
    }
}
