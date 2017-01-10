package com.securityx.model.mef.morphline.command;

import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class IAMDbMsAdScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(IAMDbMsAdScriptTest.class);

  public IAMDbMsAdScriptTest() {
    super(IAMDbMsAdScriptTest.class.toString());
    this.morphlineId = "msad-csvde";
    this.confFile = "iamdbmef-ms-ad.conf";
  }

  private Record buildRecord(String input) {
    Record r = new Record();
    r.put("logCollectionHost", "someHost");
    r.put("msadInput", input);
    return r;
  }

  @Test
   public void testcomputer() throws FileNotFoundException {
   String line = "\"CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab\",computer,\"CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab\",4,20140415101912.0Z,20141126041122.0Z,,12293,155799,W2K8R2-AD,X'0b818fd05ca3a849a4cdc6923dfc031e',,,,,,,,,,,,,X'010500000000000515000000e49ec47102197d0ca6881995e9030000',,,,,,,,,,\"CN=Computer,CN=Schema,CN=Configuration,DC=e8sec,DC=lab\",TRUE,,20140425080456.0Z;16010101000001.0Z,,,,,,,,,,,,W2K8R2-AD,,,,,,,,,,,,,,,,,,,,,,,,532480,0,0,0,0,0,130622615486066000,,130606520741426000,516,,9223372036854775807,65535,W2K8R2-AD$,805306369,130614486825890000,,,,0,Windows Server 2008 R2 Datacenter,6.1 (7601),Service Pack 1,\"CN=W2K8R2-AD,CN=Servers,CN=Default-First-Site-Name,CN=Sites,CN=Configuration,DC=e8sec,DC=lab\",w2k8r2-AD.e8sec.lab,\"CN=RID Set,CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab\",Dfsr-12F9A27C-BF97-4787-9364-D31B6C55EB04/w2k8r2-AD.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/ForestDnsZones.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/DomainDnsZones.e8sec.lab;TERMSRV/W2K8R2-AD;TERMSRV/w2k8r2-AD.e8sec.lab;DNS/w2k8r2-AD.e8sec.lab;GC/w2k8r2-AD.e8sec.lab/e8sec.lab;RestrictedKrbHost/w2k8r2-AD.e8sec.lab;RestrictedKrbHost/W2K8R2-AD;HOST/W2K8R2-AD/E8SEC;HOST/w2k8r2-AD.e8sec.lab/E8SEC;HOST/W2K8R2-AD;HOST/w2k8r2-AD.e8sec.lab;HOST/w2k8r2-AD.e8sec.lab/e8sec.lab;E3514235-4B06-11D1-AB04-00C04FC2DCD2/0a16206e-5719-4bbb-b805-2e755954be99/e8sec.lab;ldap/W2K8R2-AD/E8SEC;ldap/0a16206e-5719-4bbb-b805-2e755954be99._msdcs.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/E8SEC;ldap/W2K8R2-AD;ldap/w2k8r2-AD.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/e8sec.lab,31,\"CN=W2K8R2-AD,CN=Topology,CN=Domain System Volume,CN=DFSR-GlobalSettings,CN=System,DC=e8sec,DC=lab\",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
   line = "\"CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab\",computer,\"CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab\",4,20140415101912.0Z,20141126041122.0Z,,12293,155799,W2K8R2-AD,X'0b818fd05ca3a849a4cdc6923dfc031e',,,,,,,,,,,,,X'010500000000000515000000A065CF7E784B9B5FE77C8770091C0100',,,,,,,,,,\"CN=Computer,CN=Schema,CN=Configuration,DC=e8sec,DC=lab\",TRUE,,20140425080456.0Z;16010101000001.0Z,,,,,,,,,,,,W2K8R2-AD,,,,,,,,,,,,,,,,,,,,,,,,532480,0,0,0,0,0,130622615486066000,,130606520741426000,516,,9223372036854775807,65535,W2K8R2-AD$,805306369,130614486825890000,,,,0,Windows Server 2008 R2 Datacenter,6.1 (7601),Service Pack 1,\"CN=W2K8R2-AD,CN=Servers,CN=Default-First-Site-Name,CN=Sites,CN=Configuration,DC=e8sec,DC=lab\",w2k8r2-AD.e8sec.lab,\"CN=RID Set,CN=W2K8R2-AD,OU=Domain Controllers,DC=e8sec,DC=lab\",Dfsr-12F9A27C-BF97-4787-9364-D31B6C55EB04/w2k8r2-AD.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/ForestDnsZones.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/DomainDnsZones.e8sec.lab;TERMSRV/W2K8R2-AD;TERMSRV/w2k8r2-AD.e8sec.lab;DNS/w2k8r2-AD.e8sec.lab;GC/w2k8r2-AD.e8sec.lab/e8sec.lab;RestrictedKrbHost/w2k8r2-AD.e8sec.lab;RestrictedKrbHost/W2K8R2-AD;HOST/W2K8R2-AD/E8SEC;HOST/w2k8r2-AD.e8sec.lab/E8SEC;HOST/W2K8R2-AD;HOST/w2k8r2-AD.e8sec.lab;HOST/w2k8r2-AD.e8sec.lab/e8sec.lab;E3514235-4B06-11D1-AB04-00C04FC2DCD2/0a16206e-5719-4bbb-b805-2e755954be99/e8sec.lab;ldap/W2K8R2-AD/E8SEC;ldap/0a16206e-5719-4bbb-b805-2e755954be99._msdcs.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/E8SEC;ldap/W2K8R2-AD;ldap/w2k8r2-AD.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/e8sec.lab,31,\"CN=W2K8R2-AD,CN=Topology,CN=Domain System Volume,CN=DFSR-GlobalSettings,CN=System,DC=e8sec,DC=lab\",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   Assert.assertTrue(this.outCommand.getNumRecords() == 1);
   Record res = this.outCommand.getRecord(0);
   assertEquals("canonicalName", "e8sec.lab/Domain Controllers/W2K8R2-AD", res.get("canonicalName").get(0));
   assertEquals("accountExpires", 9223372036854775807L, res.get("accountExpires").get(0));
   assertEquals("badPasswordTime", 0L, res.get("badPasswordTime").get(0));
   assertEquals("badPwdCount", 0, res.get("badPwdCount").get(0));
   assertEquals("cn", "W2K8R2-AD", res.get("cn").get(0));
   assertEquals("codePage", "0", res.get("codePage").get(0));
   assertEquals("countryCode", "0", res.get("countryCode").get(0));
   assertEquals("dNSHostName", "w2k8r2-AD.e8sec.lab", res.get("dNSHostName").get(0));
   assertEquals("dSCorePropagationData", "20140425080456.0Z;16010101000001.0Z", res.get("dSCorePropagationData").get(0));
   assertEquals("objectDistinguishedName", "e8sec.lab/Domain Controllers/W2K8R2-AD", res.get("objectDistinguishedName").get(0));
   assertEquals("instanceType", "4", res.get("instanceType").get(0));
   assertEquals("isCriticalSystemObject", "TRUE", res.get("isCriticalSystemObject").get(0));
   assertEquals("lastLogoff", 0L, res.get("lastLogoff").get(0));
   assertEquals("lastLogon", 1417787948606L, res.get("lastLogon").get(0));
   assertEquals("lastLogonTimestamp", "130614486825890000", res.get("lastLogonTimestamp").get(0));
   assertEquals("localPolicyFlags", "0", res.get("localPolicyFlags").get(0));
   assertEquals("logSourceType", "IAMDBMef", res.get("logSourceType").get(0));
   assertEquals("externalLogSourceType", "csvde", res.get("externalLogSourceType").get(0));
   assertEquals("logonCount", 65535, res.get("logonCount").get(0));
   assertEquals("msDS-SupportedEncryptionTypes", "31", res.get("msDS-SupportedEncryptionTypes").get(0));
   assertEquals("name", "W2K8R2-AD", res.get("name").get(0));
   assertEquals("objectCategory", "e8sec.lab/Configuration/Schema/Computer", res.get("objectCategory").get(0));
   assertEquals("objectClass", "computer", res.get("objectClass").get(0));
   assertEquals("objectGUID", "d08f810b-a35c-49a8-a4cd-c6923dfc031e", res.get("objectGUID").get(0));
   assertEquals("objectSid", "S-1-5-5-21-2127521184-1604012920-1887927527-72713", res.get("objectSid").get(0));
   assertEquals("operatingSystem", "Windows Server 2008 R2 Datacenter", res.get("operatingSystem").get(0));
   assertEquals("operatingSystemServicePack", "Service Pack 1", res.get("operatingSystemServicePack").get(0));
   assertEquals("operatingSystemVersion", "6.1 (7601)", res.get("operatingSystemVersion").get(0));
   assertEquals("primaryGroupID", "516", res.get("primaryGroupID").get(0));
   assertEquals("pwdLastSet", 1416178474142L, res.get("pwdLastSet").get(0));
   assertEquals("rIdSetReferences", "e8sec.lab/Domain Controllers/W2K8R2-AD/RID Set", res.get("rIdSetReferences").get(0));
   assertEquals("sAMAccountName", "W2K8R2-AD$", res.get("sAMAccountName").get(0));
   assertEquals("sAMAccountType", "805306369", res.get("sAMAccountType").get(0));
   assertEquals("serverReferenceBL", "e8sec.lab/Configuration/Sites/Default-First-Site-Name/Servers/W2K8R2-AD", res.get("serverReferenceBL").get(0));
   assertEquals("servicePrincipalName", "Dfsr-12F9A27C-BF97-4787-9364-D31B6C55EB04/w2k8r2-AD.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/ForestDnsZones.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/DomainDnsZones.e8sec.lab;TERMSRV/W2K8R2-AD;TERMSRV/w2k8r2-AD.e8sec.lab;DNS/w2k8r2-AD.e8sec.lab;GC/w2k8r2-AD.e8sec.lab/e8sec.lab;RestrictedKrbHost/w2k8r2-AD.e8sec.lab;RestrictedKrbHost/W2K8R2-AD;HOST/W2K8R2-AD/E8SEC;HOST/w2k8r2-AD.e8sec.lab/E8SEC;HOST/W2K8R2-AD;HOST/w2k8r2-AD.e8sec.lab;HOST/w2k8r2-AD.e8sec.lab/e8sec.lab;E3514235-4B06-11D1-AB04-00C04FC2DCD2/0a16206e-5719-4bbb-b805-2e755954be99/e8sec.lab;ldap/W2K8R2-AD/E8SEC;ldap/0a16206e-5719-4bbb-b805-2e755954be99._msdcs.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/E8SEC;ldap/W2K8R2-AD;ldap/w2k8r2-AD.e8sec.lab;ldap/w2k8r2-AD.e8sec.lab/e8sec.lab", res.get("servicePrincipalName").get(0));
   assertEquals("uSNChanged", "155799", res.get("uSNChanged").get(0));
   assertEquals("uSNCreated", "12293", res.get("uSNCreated").get(0));
   assertEquals("userAccountControl", 532480, res.get("userAccountControl").get(0));
   assertEquals("lastModificationDate", 1416975082000L, res.get("lastModificationDate").get(0));
   assertEquals("creationDate", 1397557152000L, res.get("creationDate").get(0));

   }
  @Test
  public void testuser() throws FileNotFoundException {
    String line = "\"CN=jyria2,CN=Users,DC=e8sec,DC=lab\",user,\"CN=jyria2,CN=Users,DC=e8sec,DC=lab\",4,20140429094245.0Z,20141205144600.0Z,,28721,163319,jyria2,X'2ac45bffd5e13b4594a146964583d8da',,,,,,,,,,,,,X'010500000000000515000000e49ec47102197d0ca688199560040000',,,,,,,,,,\"CN=Person,CN=Schema,CN=Configuration,DC=e8sec,DC=lab\",,,16010101000000.0Z,,,,,,,,,,,,jyria2,user's description,,,,jyria display name,,,,,,,,,,,,,,,,,,\"CN=RDP power users,CN=Users,DC=e8sec,DC=lab\",66048,0,0,250,0,0,0,,130432381651524000,513,,9223372036854775807,0,jyria2,805306368,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,jean-Yves,,jyria2@e8sec.lab,jyrialastname,,,,,FR,Echirolles,Isere,Capture and parsing engineer,38130,po box,user's office,+33661187266,0977575346,JYR,+33476225982,telephone notes,France,Engineering,Axessio,337 E 39th Avenue,www.e8sec.lab/blog/jyria,c:\\\\yia2home,c:\\\\temp\\\\start.cmd,c:\\\\temp,12341234,jyria@e8sec.lab,\"CN=Christophe Briguet,CN=Users,DC=e8sec,DC=lab\",0477575346,0761187266,0661187266";

    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record res = this.outCommand.getRecord(0);
    assertEquals("DN", "CN=jyria2,CN=Users,DC=e8sec,DC=lab", res.get("DN").get(0));
    assertEquals("accountExpires", 9223372036854775807L, res.get("accountExpires").get(0));
    assertEquals("badPasswordTime", 0L, res.get("badPasswordTime").get(0));
    assertEquals("badPwdCount", 0, res.get("badPwdCount").get(0));
    assertEquals("canonicalName", "e8sec.lab/Users/jyria2", res.get("canonicalName").get(0));
    assertEquals("creationDate", 1398764565000L, res.get("creationDate").get(0));
    assertEquals("description", "user's description", res.get("description").get(0));
    assertEquals("displayName", "jyria display name", res.get("displayName").get(0));
    assertEquals("objectDistinguishedName", "e8sec.lab/Users/jyria2", res.get("objectDistinguishedName").get(0));
    assertEquals("division", "Engineering", res.get("division").get(0));
    assertEquals("externalLogSourceType", "csvde", res.get("externalLogSourceType").get(0));
    assertEquals("facsimileTelephoneNumber", "0977575346", res.get("facsimileTelephoneNumber").get(0));
    assertEquals("givenName", "jean-Yves", res.get("givenName").get(0));
    assertEquals("homeDirectory", "c:\\\\yia2home", res.get("homeDirectory").get(0));
    assertEquals("homePhone", "0477575346", res.get("homePhone").get(0));
    assertEquals("info", "telephone notes", res.get("info").get(0));
    assertEquals("initials", "JYR", res.get("initials").get(0));
    assertEquals("instanceType", "4", res.get("instanceType").get(0));
    assertEquals("ipPhone", "12341234", res.get("ipPhone").get(0));
    assertEquals("l", "Echirolles", res.get("l").get(0));
    assertEquals("lastLogoff", 0L, res.get("lastLogoff").get(0));
    assertEquals("lastLogon", 0L, res.get("lastLogon").get(0));
    assertEquals("lastModificationDate", 1417790760000L, res.get("lastModificationDate").get(0));
    assertEquals("locationStr", "337 E 39th Avenue\nAxessio\n38130 France", res.get("locationStr").get(0));
    assertEquals("logCollectionHost", "someHost", res.get("logCollectionHost").get(0));
    assertEquals("logSourceType", "IAMDBMef", res.get("logSourceType").get(0));
    assertEquals("logonCount", 0, res.get("logonCount").get(0));
    assertEquals("mail", "jyria@e8sec.lab", res.get("mail").get(0));
    assertEquals("manager", "CN=Christophe Briguet,CN=Users,DC=e8sec,DC=lab", res.get("manager").get(0));
    assertEquals("memberOf", "e8sec.lab/Users/RDP power users", res.get("memberOf").get(0));
    assertEquals("mobile", "0761187266", res.get("mobile").get(0));
    assertEquals("name", "jyria2", res.get("name").get(0));
    assertEquals("objectCN", "jyria2", res.get("objectCN").get(0));
    assertEquals("objectCategory", "e8sec.lab/Configuration/Schema/Person", res.get("objectCategory").get(0));
    assertEquals("objectClass", "user", res.get("objectClass").get(0));
    assertEquals("objectGUID", "ff5bc42a-e1d5-453b-94a1-46964583d8da", res.get("objectGUID").get(0));
    assertEquals("objectMail", "jyria@e8sec.lab", res.get("objectMail").get(0));
    assertEquals("objectName", "jyria2", res.get("objectName").get(0));
    assertEquals("objectSid", "S-1-5-5-21-1908711140-209524994-2501478566-1120", res.get("objectSid").get(0));
    assertEquals("otherTelephone", "+33476225982", res.get("otherTelephone").get(0));
    assertEquals("pager", "0661187266", res.get("pager").get(0));
    assertEquals("physicalDeliveryOfficeName", "user's office", res.get("physicalDeliveryOfficeName").get(0));
    assertEquals("postOfficeBox", "po box", res.get("postOfficeBox").get(0));
    assertEquals("postalCode", "38130", res.get("postalCode").get(0));
    assertEquals("primaryGroupID", "513", res.get("primaryGroupID").get(0));
    assertEquals("profilePath", "c:\\\\temp", res.get("profilePath").get(0));
    assertEquals("pwdLastSet", 1398764565152L, res.get("pwdLastSet").get(0));
    assertEquals("sAMAccountName", "jyria2", res.get("sAMAccountName").get(0));
    assertEquals("sAMAccountType", "805306368", res.get("sAMAccountType").get(0));
    assertEquals("scriptPath", "c:\\\\temp\\\\start.cmd", res.get("scriptPath").get(0));
    assertEquals("sn", "jyrialastname", res.get("sn").get(0));
    assertEquals("st", "Isere", res.get("st").get(0));
    assertEquals("streetAddress", "337 E 39th Avenue", res.get("streetAddress").get(0));
    assertEquals("telephoneNumber", "+33661187266", res.get("telephoneNumber").get(0));
    assertEquals("title", "Capture and parsing engineer", res.get("title").get(0));
    assertEquals("uSNChanged", "163319", res.get("uSNChanged").get(0));
    assertEquals("uSNCreated", "28721", res.get("uSNCreated").get(0));
    assertEquals("userAccountControl", 66048, res.get("userAccountControl").get(0));
    assertEquals("userPrincipalName", "jyria2@e8sec.lab", res.get("userPrincipalName").get(0));
    assertEquals("wWWHomePage", "www.e8sec.lab/blog/jyria", res.get("wWWHomePage").get(0));

  }
 
  @Test
  public void testbug() throws FileNotFoundException {
    String line = "\"CN=CN=V599734,OU=US1,OU=ENT,OU=_Service Accounts,DC=verizon,DC=com\",user,\"CN=CN=V599734,OU=US1,OU=ENT,OU=_Service Accounts,DC=verizon,DC=com\",4,20060720211912.0Z,20141026045227.0Z,,12026,,,,11256715,V599734,X'937598e48fd746bda1e1db1797aca203',,,,,,,,,,,,,,X'010500000000000515000000e2e9566957c42465a58d1902d30a32c8',,,,,,,,,,\"CN=Person,CN=Schema,CN=Configuration,DC=ati,DC=pri\",TRUE,,\"20140506150213.0Z;20130604195332.0Z;20130528204612.0Z;16010101000000.0Z\",,,,,,,,,,,,,Built-in account for administering the computer/domain,,V599734,,,,###############################################################################################################################################################################################################################################################,,66048,0,0,0,130546840494592000,0,130590648000937000,logon.cmd,129107411679016000,512,1,9223372036854770000,65535,NetAdmin,805306368,0,,130587727370751000,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,v599734@us1.ent.verizon.com,,,,,,,,,,,,,,,,,,,,v599734@us1.ent.verizon.com,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
String header ="DN,objectClass,distinguishedName,instanceType,whenCreated,whenChanged,subRefs,uSNCreated,dSASignature,repsTo,repsFrom,uSNChanged,name,objectGUID,replUpToDateVector,creationTime,forceLogoff,lockoutDuration,lockOutObservationWindow,lockoutThreshold,maxPwdAge,minPwdAge,minPwdLength,modifiedCountAtLastProm,nextRid,pwdProperties,pwdHistoryLength,objectSid,serverState,uASCompat,modifiedCount,auditingPolicy,nTMixedDomain,rIDManagerReference,fSMORoleOwner,systemFlags,wellKnownObjects,objectCategory,isCriticalSystemObject,gPLink,dSCorePropagationData,otherWellKnownObjects,masteredBy,ms-DS-MachineAccountQuota,msDS-Behavior-Version,msDS-PerUserTrustQuota,msDS-AllUsersTrustQuota,msDS-PerUserTrustTombstonesQuota,msDs-masteredBy,msDS-IsDomainFor,msDS-NcType,dc,ou,description,showInAdvancedViewOnly,cn,sn,givenName,displayName,memberOf,extensionName,userAccountControl,badPwdCount,codePage,countryCode,badPasswordTime,lastLogoff,lastLogon,scriptPath,pwdLastSet,primaryGroupID,adminCount,accountExpires,logonCount,sAMAccountName,sAMAccountType,lockoutTime,servicePrincipalName,lastLogonTimestamp,userCertificate,localPolicyFlags,operatingSystem,operatingSystemVersion,operatingSystemServicePack,serverReferenceBL,dNSHostName,managedBy,rIdSetReferences,frsComputerReferenceBL,msDS-SupportedEncryptionTypes,rIDAvailablePool,msDS-TombstoneQuotaFactor,flags,versionNumber,gPCFunctionalityVersion,gPCFileSysPath,gPCMachineExtensionNames,gPCUserExtensionNames,ipsecName,ipsecID,ipsecDataType,ipsecData,ipsecISAKMPReference,ipsecNFAReference,ipsecOwnersReference,ipsecNegotiationPolicyReference,ipsecFilterReference,iPSECNegotiationPolicyType,iPSECNegotiationPolicyAction,groupType,member,userParameters,logonHours,comment,adminDescription,revision,samDomainUpdates,lastSetTime,priorSetTime,managedObjects,userPrincipalName,gPOptions,c,l,st,title,postalCode,physicalDeliveryOfficeName,telephoneNumber,co,department,company,streetAddress,directReports,wWWHomePage,homeDirectory,homeDrive,msTSExpireDate,msTSLicenseVersion,msTSManagingLS,mail,manager,mobile,fRSReplicaSetType,fRSVersionGUID,fRSFileFilter,fRSReplicaSetGUID,msWMI-Author,msWMI-ChangeDate,msWMI-CreationDate,msWMI-ID,msWMI-Name,msWMI-Parm2,dNSProperty,dnsRecord,dNSTombstoned,rpcNsBindings,rpcNsInterfaceID,rpcNsTransferSyntax,objectVersion,homePhone,userSMIMECertificate,msExchRequireAuthToSendTo,info,proxyAddresses,deliveryMechanism,homeMDB,targetAddress,mailNickname,legacyExchangeDN,textEncodedORAddress,msExchALObjectVersion,msExchHideFromAddressLists,msExchMailboxSecurityDescriptor,msExchPFTreeType,msExchPoliciesIncluded,msRRASAttribute,initials,keywords,serviceClassName,serviceBindingInformation,serviceDNSName,serviceDNSNameType,userWorkstations,serverReference,frsComputerReference,fRSMemberReferenceBL,fRSWorkingPath,fRSRootPath,fRSStagingPath,fRSMemberReference,rIDAllocationPool,rIDPreviousAllocationPool,rIDUsedPool,rIDNextRID,uNCName,location,serverName,portName,driverName,priority,printStartTime,printEndTime,printMaxResolutionSupported,printOrientationsSupported,printCollate,printColor,printShareName,printSpooling,printKeepPrintedJobs,driverVersion,printMaxXExtent,printMaxYExtent,printMinXExtent,printMinYExtent,printStaplingSupported,printRateUnit,printMediaReady,printMediaSupported,printerName,printPagesPerMinute,url,shortServerName,printDuplexSupported,networkAddress,ms-net-ieee-80211-GP-PolicyGUID,ms-net-ieee-80211-GP-PolicyData,lastUpdateSequence,appSchemaVersion,msiScriptPath,cOMClassID,localeID,machineArchitecture,packageType,packageName,packageFlags,versionNumberHi,versionNumberLo,msiFileList,upgradeProductCode,productCode,msiScriptName,installUiLevel,printBinNames";
    boolean resultheader = doTest(buildRecord(header));
    assertEquals(true, resultheader);
    boolean result = doTest(buildRecord(line));
    assertEquals(true, result);
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    Record res = this.outCommand.getRecord(0);
    
  }

    //

    @Test
    public void testIAMDBForDemo() throws FileNotFoundException {
        String line = "\"CN=Andre_Brown,OU=Users,DC=redwood, DC=e8\",user,\"CN=Andre_Brown,OU=Users,DC=redwood, DC=e8\",4,20140720211912.0Z,20141026045227.0Z,,1,,,,1,Andre_Brown,X'937598e48fd746bda1e1db1797aca204',,,,,,,,,,,,,,X'010500000000000515000000e2e9566957c42465a58d1902d30a32c9',,,,,,,,,,\"CN=Person,CN=Schema,CN=Configuration,DC=redwood,DC=e8\",TRUE,,20150706150213.0Z;20150604195332.0Z;20150528204612.0Z;16010101000000.0Z,,,,,,,,,,,,,Product Marketing Manager,,Andre_Brown,,,,\"CN=Domain Admins,CN=Users,DC=redwood,DC=e8\",,66050,1,,840,130713846974592000,,130808503330000000,logon.cmd,130700726500838000,513,1,130851453001676000,13778,Andre_Brown,805306368,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,Andre_Brown@redwood.e8,,US,,CA,,94065,Marine,,United States,,,100 Marine Parkway - Suite 300,,,,,,,,Andre_Brown@redwood.e8,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
        String header ="DN,objectClass,distinguishedName,instanceType,whenCreated,whenChanged,subRefs,uSNCreated,dSASignature,repsTo,repsFrom,uSNChanged,name,objectGUID,replUpToDateVector,creationTime,forceLogoff,lockoutDuration,lockOutObservationWindow,lockoutThreshold,maxPwdAge,minPwdAge,minPwdLength,modifiedCountAtLastProm,nextRid,pwdProperties,pwdHistoryLength,objectSid,serverState,uASCompat,modifiedCount,auditingPolicy,nTMixedDomain,rIDManagerReference,fSMORoleOwner,systemFlags,wellKnownObjects,objectCategory,isCriticalSystemObject,gPLink,dSCorePropagationData,otherWellKnownObjects,masteredBy,ms-DS-MachineAccountQuota,msDS-Behavior-Version,msDS-PerUserTrustQuota,msDS-AllUsersTrustQuota,msDS-PerUserTrustTombstonesQuota,msDs-masteredBy,msDS-IsDomainFor,msDS-NcType,dc,ou,description,showInAdvancedViewOnly,cn,sn,givenName,displayName,memberOf,extensionName,userAccountControl,badPwdCount,codePage,countryCode,badPasswordTime,lastLogoff,lastLogon,scriptPath,pwdLastSet,primaryGroupID,adminCount,accountExpires,logonCount,sAMAccountName,sAMAccountType,lockoutTime,servicePrincipalName,lastLogonTimestamp,userCertificate,localPolicyFlags,operatingSystem,operatingSystemVersion,operatingSystemServicePack,serverReferenceBL,dNSHostName,managedBy,rIdSetReferences,frsComputerReferenceBL,msDS-SupportedEncryptionTypes,rIDAvailablePool,msDS-TombstoneQuotaFactor,flags,versionNumber,gPCFunctionalityVersion,gPCFileSysPath,gPCMachineExtensionNames,gPCUserExtensionNames,ipsecName,ipsecID,ipsecDataType,ipsecData,ipsecISAKMPReference,ipsecNFAReference,ipsecOwnersReference,ipsecNegotiationPolicyReference,ipsecFilterReference,iPSECNegotiationPolicyType,iPSECNegotiationPolicyAction,groupType,member,userParameters,logonHours,comment,adminDescription,revision,samDomainUpdates,lastSetTime,priorSetTime,managedObjects,userPrincipalName,gPOptions,c,l,st,title,postalCode,physicalDeliveryOfficeName,telephoneNumber,co,department,company,streetAddress,directReports,wWWHomePage,homeDirectory,homeDrive,msTSExpireDate,msTSLicenseVersion,msTSManagingLS,mail,manager,mobile,fRSReplicaSetType,fRSVersionGUID,fRSFileFilter,fRSReplicaSetGUID,msWMI-Author,msWMI-ChangeDate,msWMI-CreationDate,msWMI-ID,msWMI-Name,msWMI-Parm2,dNSProperty,dnsRecord,dNSTombstoned,rpcNsBindings,rpcNsInterfaceID,rpcNsTransferSyntax,objectVersion,homePhone,userSMIMECertificate,msExchRequireAuthToSendTo,info,proxyAddresses,deliveryMechanism,homeMDB,targetAddress,mailNickname,legacyExchangeDN,textEncodedORAddress,msExchALObjectVersion,msExchHideFromAddressLists,msExchMailboxSecurityDescriptor,msExchPFTreeType,msExchPoliciesIncluded,msRRASAttribute,initials,keywords,serviceClassName,serviceBindingInformation,serviceDNSName,serviceDNSNameType,userWorkstations,serverReference,frsComputerReference,fRSMemberReferenceBL,fRSWorkingPath,fRSRootPath,fRSStagingPath,fRSMemberReference,rIDAllocationPool,rIDPreviousAllocationPool,rIDUsedPool,rIDNextRID,uNCName,location,serverName,portName,driverName,priority,printStartTime,printEndTime,printMaxResolutionSupported,printOrientationsSupported,printCollate,printColor,printShareName,printSpooling,printKeepPrintedJobs,driverVersion,printMaxXExtent,printMaxYExtent,printMinXExtent,printMinYExtent,printStaplingSupported,printRateUnit,printMediaReady,printMediaSupported,printerName,printPagesPerMinute,url,shortServerName,printDuplexSupported,networkAddress,ms-net-ieee-80211-GP-PolicyGUID,ms-net-ieee-80211-GP-PolicyData,lastUpdateSequence,appSchemaVersion,msiScriptPath,cOMClassID,localeID,machineArchitecture,packageType,packageName,packageFlags,versionNumberHi,versionNumberLo,msiFileList,upgradeProductCode,productCode,msiScriptName,installUiLevel,printBinNames";
        boolean resultheader = doTest(buildRecord(header));
        assertEquals(true, resultheader);
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record res = this.outCommand.getRecord(0);

    }
    @Test
    public void testIAMDBForDemo2() throws FileNotFoundException {
        //String line = "\"CN=Andre_Brown,OU=Users,DC=new-york, DC=acmebank\",user,\"CN=Andre_Brown,OU=Users,DC=new-york, DC=acmebank\",4,20140720211912.0Z,20160101045227.0Z,,1001,,,,1001,Andre_Brown,X'937598e48fd746bda1e1db1797aca201',,,,,,,,,,,,,,X'010500000000000515000000e2e9566957c42465a58d1902d30a3201',,,,,,,,,,\"CN=Person,CN=Schema,CN=Configuration,DC=new-york,DC=acmebank\",TRUE,,20150706150213.0Z;20150604195332.0Z;20150528204612.0Z;16010101000000.0Z,,,,,,,,,,,,,Administrator,,Andre_Brown,,,,\"CN=Domain Admins,CN=Users,DC=new-york,DC=acmebank\",,66050,5,,11,131000000000000000,,131000000000000000,logon.cmd,131000000000000000,513,1,131000000000000000,6787,Andre_Brown,805306368,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,Andre_Brown,,US,,CA,System admin,10005,Wall Street,,United States,Operation,AcmeBank,1234 Wall Street,,,,,,,,Andre_Brown@acmebank.com,Kevin_Smith,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n";
        String line = "\"CN=Andre_Brown,OU=Users,DC=new-york, DC=acmebank\",user,\"CN=Andre_Brown,OU=Users,DC=new-york, DC=acmebank\",4,20140720211912.0Z,20160101045227.0Z,,1001,,,,1001,Andre_Brown,X'937598e48fd746bda1e1db1797aca201',,,,,,,,,,,,,,X'010500000000000515000000e2e9566957c42465a58d1902d30a3201',,,,,,,,,,\"CN=Person,CN=Schema,CN=Configuration,DC=new-york,DC=acmebank\",TRUE,,20150706150213.0Z;20150604195332.0Z;20150528204612.0Z;16010101000000.0Z,,,,,,,,,,,,,Administrator,,Andre_Brown,,,,\"CN=Domain Admins,CN=Users,DC=new-york,DC=acmebank\",,66050,5,,11,131000000000000000,,131000000000000000,logon.cmd,131000000000000000,513,1,131000000000000000,6787,Andre_Brown,805306368,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,Andre_Brown,,US,,CA,System admin,10005,Wall Street,,United States,Operation,AcmeBank,1234 Wall Street,,,,,,,,Andre_Brown@acmebank.com,\"CN=Kevin_Smith,OU=Users,DC=new-york, DC=acmebank\",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n";
        String header ="DN,objectClass,distinguishedName,instanceType,whenCreated,whenChanged,subRefs,uSNCreated,dSASignature,repsTo,repsFrom,uSNChanged,name,objectGUID,replUpToDateVector,creationTime,forceLogoff,lockoutDuration,lockOutObservationWindow,lockoutThreshold,maxPwdAge,minPwdAge,minPwdLength,modifiedCountAtLastProm,nextRid,pwdProperties,pwdHistoryLength,objectSid,serverState,uASCompat,modifiedCount,auditingPolicy,nTMixedDomain,rIDManagerReference,fSMORoleOwner,systemFlags,wellKnownObjects,objectCategory,isCriticalSystemObject,gPLink,dSCorePropagationData,otherWellKnownObjects,masteredBy,ms-DS-MachineAccountQuota,msDS-Behavior-Version,msDS-PerUserTrustQuota,msDS-AllUsersTrustQuota,msDS-PerUserTrustTombstonesQuota,msDs-masteredBy,msDS-IsDomainFor,msDS-NcType,dc,ou,description,showInAdvancedViewOnly,cn,sn,givenName,displayName,memberOf,extensionName,userAccountControl,badPwdCount,codePage,countryCode,badPasswordTime,badPasswordTime,lastLogon,scriptPath,pwdLastSet,primaryGroupID,adminCount,accountExpires,logonCount,sAMAccountName,sAMAccountType,lockoutTime,servicePrincipalName,lastLogonTimestamp,userCertificate,localPolicyFlags,operatingSystem,operatingSystemVersion,operatingSystemServicePack,serverReferenceBL,dNSHostName,managedBy,rIdSetReferences,frsComputerReferenceBL,msDS-SupportedEncryptionTypes,rIDAvailablePool,msDS-TombstoneQuotaFactor,flags,versionNumber,gPCFunctionalityVersion,gPCFileSysPath,gPCMachineExtensionNames,gPCUserExtensionNames,ipsecName,ipsecID,ipsecDataType,ipsecData,ipsecISAKMPReference,ipsecNFAReference,ipsecOwnersReference,ipsecNegotiationPolicyReference,ipsecFilterReference,iPSECNegotiationPolicyType,iPSECNegotiationPolicyAction,groupType,member,userParameters,logonHours,comment,adminDescription,revision,samDomainUpdates,lastSetTime,priorSetTime,managedObjects,userPrincipalName,gPOptions,c,l,st,title,postalCode,physicalDeliveryOfficeName,telephoneNumber,co,department,company,streetAddress,directReports,wWWHomePage,homeDirectory,homeDrive,msTSExpireDate,msTSLicenseVersion,msTSManagingLS,mail,manager,mobile,fRSReplicaSetType,fRSVersionGUID,fRSFileFilter,fRSReplicaSetGUID,msWMI-Author,msWMI-ChangeDate,msWMI-CreationDate,msWMI-ID,msWMI-Name,msWMI-Parm2,dNSProperty,dnsRecord,dNSTombstoned,rpcNsBindings,rpcNsInterfaceID,rpcNsTransferSyntax,objectVersion,homePhone,userSMIMECertificate,msExchRequireAuthToSendTo,info,proxyAddresses,deliveryMechanism,homeMDB,targetAddress,mailNickname,legacyExchangeDN,textEncodedORAddress,msExchALObjectVersion,msExchHideFromAddressLists,msExchMailboxSecurityDescriptor,msExchPFTreeType,msExchPoliciesIncluded,msRRASAttribute,initials,keywords,serviceClassName,serviceBindingInformation,serviceDNSName,serviceDNSNameType,userWorkstations,serverReference,frsComputerReference,fRSMemberReferenceBL,fRSWorkingPath,fRSRootPath,fRSStagingPath,fRSMemberReference,rIDAllocationPool,rIDPreviousAllocationPool,rIDUsedPool,rIDNextRID,uNCName,location,serverName,portName,driverName,priority,printStartTime,printEndTime,printMaxResolutionSupported,printOrientationsSupported,printCollate,printColor,printShareName,printSpooling,printKeepPrintedJobs,driverVersion,printMaxXExtent,printMaxYExtent,printMinXExtent,printMinYExtent,printStaplingSupported,printRateUnit,printMediaReady,printMediaSupported,printerName,printPagesPerMinute,url,shortServerName,printDuplexSupported,networkAddress,ms-net-ieee-80211-GP-PolicyGUID,ms-net-ieee-80211-GP-PolicyData,lastUpdateSequence,appSchemaVersion,msiScriptPath,cOMClassID,localeID,machineArchitecture,packageType,packageName,packageFlags,versionNumberHi,versionNumberLo,msiFileList,upgradeProductCode,productCode,msiScriptName,installUiLevel,printBinNames";
        boolean resultheader = doTest(buildRecord(header));
        assertEquals(true, resultheader);
        boolean result = doTest(buildRecord(line));
        assertEquals(true, result);
        Assert.assertTrue(this.outCommand.getNumRecords() == 1);
        Record out = this.outCommand.getRecord(0);

        assertEquals("DN", "CN=Andre_Brown,OU=Users,DC=new-york, DC=acmebank", out.get("DN").get(0));
        assertEquals("canonicalName", "new-york/Users/Andre_Brown", out.get("canonicalName").get(0));


        assertEquals("accountExpires", 1455526400000L, out.get("accountExpires").get(0));

        //not used
        assertEquals("adminCount", "1", out.get("adminCount").get(0));

        assertEquals("badPasswordTime", 1455526400000L, out.get("badPasswordTime").get(0));

        assertEquals("badPwdCount", 5, out.get("badPwdCount").get(0));

        //not used
        assertEquals("c", "US", out.get("c").get(0));

        assertEquals("cn", "Andre_Brown", out.get("cn").get(0));
        assertEquals("objectCN", "Andre_Brown", out.get("objectCN").get(0));

        //not used
        assertEquals("countryCode", "11", out.get("countryCode").get(0));

        assertEquals("whenCreated", "20140720211912.0Z", out.get("whenCreated").get(0));
        assertEquals("creationDate", 1405891152000L, out.get("creationDate").get(0));

        //not used
        assertEquals("dSCorePropagationData", "20150706150213.0Z;20150604195332.0Z;20150528204612.0Z;16010101000000.0Z", out.get("dSCorePropagationData").get(0));

        assertEquals("department", "Operation", out.get("department").get(0));
        assertEquals("division", "Operation", out.get("division").get(0));

        //not used
        assertEquals("description", "Administrator", out.get("description").get(0));
        assertEquals("objectDescription", "Administrator", out.get("objectDescription").get(0));

        //assertEquals("distinguishedName", "new-york/Users/Andre_Brown", out.get("distinguishedName").get(0));
        assertEquals("distinguishedName", "CN=Andre_Brown,OU=Users,DC=new-york, DC=acmebank", out.get("distinguishedName").get(0));
        assertEquals("objectDistinguishedName", "new-york/Users/Andre_Brown", out.get("objectDistinguishedName").get(0));

        assertEquals("externalLogSourceType", "csvde", out.get("externalLogSourceType").get(0));

        //not used
        assertEquals("instanceType", "4", out.get("instanceType").get(0));

        assertEquals("isCriticalSystemObject", "TRUE", out.get("isCriticalSystemObject").get(0));

        assertEquals("lastLogon", 1455526400000L, out.get("lastLogon").get(0));

        assertEquals("whenChanged", "20160101045227.0Z", out.get("whenChanged").get(0));
        assertEquals("lastModificationDate", 1451623947000L, out.get("lastModificationDate").get(0));

        assertEquals("physicalDeliveryOfficeName", "Wall Street", out.get("physicalDeliveryOfficeName").get(0));
        assertEquals("company", "AcmeBank", out.get("company").get(0));
        assertEquals("postalCode", "10005", out.get("postalCode").get(0));
        assertEquals("co", "United States", out.get("co").get(0));
        assertEquals("locationStr", "1234 Wall Street\nAcmeBank\n10005 United States", out.get("locationStr").get(0));

        assertEquals("lockoutTime", 0L, out.get("lockoutTime").get(0));

        assertEquals("logCollectionHost", "someHost", out.get("logCollectionHost").get(0));

        assertEquals("logSourceType", "IAMDBMef", out.get("logSourceType").get(0));

        assertEquals("logonCount", 6787, out.get("logonCount").get(0));

        assertEquals("mail", "Andre_Brown@acmebank.com", out.get("mail").get(0));
        assertEquals("objectMail", "Andre_Brown@acmebank.com", out.get("objectMail").get(0));


        assertEquals("manager", "CN=Kevin_Smith,OU=Users,DC=new-york, DC=acmebank", out.get("manager").get(0));
        assertEquals("objectManager", "new-york/Users/Kevin_Smith", out.get("objectManager").get(0));

        assertEquals("memberOf", "new-york.acmebank/Users/Domain Admins", out.get("memberOf").get(0));

        assertEquals("name", "Andre_Brown", out.get("name").get(0));
        assertEquals("objectName", "Andre_Brown", out.get("objectName").get(0));

        assertEquals("objectCategory", "new-york.acmebank/Configuration/Schema/Person", out.get("objectCategory").get(0));

        assertEquals("objectClass", "user", out.get("objectClass").get(0));

        assertEquals("objectGUID", "e4987593-d78f-bd46-a1e1-db1797aca201", out.get("objectGUID").get(0));

        assertEquals("objectSid", "S-1-5-5-21-1767303650-1696908375-35229093-20056787", out.get("objectSid").get(0));

        assertEquals("primaryGroupID", "513", out.get("primaryGroupID").get(0));

        assertEquals("pwdLastSet", 1455526400000L, out.get("pwdLastSet").get(0));

        assertEquals("sAMAccountName", "Andre_Brown", out.get("sAMAccountName").get(0));
        assertEquals("samAccountName", "Andre_Brown", out.get("samAccountName").get(0));

        assertEquals("sAMAccountType", "805306368", out.get("sAMAccountType").get(0));
        assertEquals("samAccountType", "805306368", out.get("samAccountType").get(0));

        //not used
        assertEquals("scriptPath", "logon.cmd", out.get("scriptPath").get(0));

        //not used
        assertEquals("st", "CA", out.get("st").get(0));

        //not used
        assertEquals("streetAddress", "1234 Wall Street", out.get("streetAddress").get(0));

        assertEquals("title", "System admin", out.get("title").get(0));

        // not used
        assertEquals("uSNChanged", "1001", out.get("uSNChanged").get(0));

        //not used
        assertEquals("uSNCreated", "1001", out.get("uSNCreated").get(0));

        assertEquals("userAccountControl", 66050, out.get("userAccountControl").get(0));

        //not used
        assertEquals("userPrincipalName", "Andre_Brown", out.get("userPrincipalName").get(0));

    }


    /*@Test
    public void testIAMDBFileAtOnce() throws Exception {
        AvroParser instance = AvroParser.BuildParser(confFile, "msad-csvde_parser", SupportedFormats.genSupportedFormatList());
        Event avroEvent = new Event();
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("category", "syslog");
        headers.put("hostname", "somehost");
        headers.put("timestamp", "1384693669604");

        avroEvent.setHeaders(headers);

        InputStream fis = new FileInputStream("/Volumes/Volume 1000Go 1/JY/SecurityX/Project/201412-AD_import/msvde-VRZ_alt.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ;
        String line;
        long cpt = 0;
        while ((line = br.readLine()) !=  null){

            // InputStream ais = new ByteArrayInputStream(line.getBytes());
            // DataInputStream dais = new DataInputStream(ais);
            // Decoder decoder = DecoderFactory.get().jsonDecoder(Event.SCHEMA$, dais);
            // DatumReader<Event> reader = new SpecificDatumReader<Event>(Event.SCHEMA$);
            // avroEvent = reader.read(null, decoder);

            // JsonReader reader = Json.createReader(new StringReader(line));
            // JsonObject object = reader.readObject();
            // reader.close();

            // OutUtils.printOut(object.getString("rawLog"));
            // ByteBuffer buf = ByteBuffer.wrap(object.getString("rawLog").getBytes());
            avroEvent.setBody(ByteBuffer.wrap(line.getBytes()));
            try {
                List<Map<String, List<Object>>> output = instance.parse(avroEvent);

                if (output != null && output.size() > 0) {
                    Map<String, List<Object>> out = output.get(0);
                    OutUtils.printOut(out.toString());
                }else{
                    System.err.println("unmatched :"+line);
                }
                //assertEquals("logSourceType", "HETMef", out.get("logSourceType").get(0));
            }catch (Exception e ){
                OutUtils.printOut("failed on line " +cpt+" ("+e.getMessage()+")");
                throw e;
            }
            cpt+=1;
        }


    } */

  /*@Test
   public void testheader() throws FileNotFoundException {
   String line1 = "#Fields: date time time-taken c-ip sc-status s-action";
   String line2 = "2005-04-12 21:03:45 1 192.16.170.46 503 TCP_ERR_MISS";
   String[] data = {line1, line2};

   boolean result = doTest(buildRecord(line1));
   assertEquals(true, result);
   result = doTest(buildRecord(line2));
   assertEquals(true, result);
   OutUtils.printOut(this.outCommand.getNumRecords());
   Assert.assertEquals(1, this.outCommand.getNumRecords());
   Record r = this.outCommand.getRecord(0);
   Assert.assertEquals(1113339825000L,
   r.get("startTime").get(0));
   Assert.assertEquals("192.16.170.46",
   r.get("c-ip").get(0));
    
   line1 = "#Fields: date time cs-bytes cs-method cs-uri-scheme cs-host cs-uri-path cs-uri-query cs-username s-hierarchy s-supplier-name ";
   line2 = "2005-04-12 21:03:45 430 GET http www.yahoo.com / - - NONE 192.16.170.42 - ";
   String[] data2 = {line1, line2};
   result = doTest(buildRecord(line1));
   assertEquals(true, result);
   result = doTest(buildRecord(line2));
   assertEquals(true, result);
   Assert.assertEquals(2, this.outCommand.getNumRecords());
   r = this.outCommand.getRecord(1);
   Assert.assertEquals(1113339825000L,
   r.get("startTime").get(0));
   Assert.assertEquals("192.16.170.42",
   r.get("s-supplier-name").get(0));
   Assert.assertEquals("http://www.yahoo.com/?-",
   r.get("requestUrl").get(0));

   }

   @Test
   public void test1() throws FileNotFoundException {
   // 
   String line = "2005-04-12 21:03:45 74603 192.16.170.46 503 TCP_ERR_MISS 1736 430 GET http www.yahoo.com / - - NONE 192.16.170.42 - \"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6) Gecko/20050317 Firefox/1.0.2\" DENIED none - 192.16.170.42 SG-HTTP-Service - server_unavailable \"Server unavailable: No ICAP server is available to process request.\"";
    
   boolean result = doTest(buildRecord(line));
   assertEquals(true, result);
   OutUtils.printOut(this.outCommand.getNumRecords());
   Assert.assertTrue(this.outCommand.getNumRecords() == 1);
   Record r = this.outCommand.getRecord(0);
   Assert.assertEquals("-, server_unavailable : Server unavailable: No ICAP server is available to process request.",
   r.get("reason").get(0));
   Assert.assertEquals(1113339825000L,
   r.get("startTime").get(0));
   Assert.assertEquals("192.16.170.46",
   r.get("c-ip").get(0));
   Assert.assertEquals("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6) Gecko/20050317 Firefox/1.0.2",
   r.get("cs(User-Agent)").get(0));
   Assert.assertEquals("430",
   r.get("cs-bytes").get(0));
   Assert.assertEquals("www.yahoo.com",
   r.get("cs-host").get(0));
   Assert.assertEquals("GET",
   r.get("cs-method").get(0));
   Assert.assertEquals("/",
   r.get("cs-uri-path").get(0));
   Assert.assertEquals("-",
   r.get("cs-uri-query").get(0));
   Assert.assertEquals("http",
   r.get("cs-uri-scheme").get(0));
   Assert.assertEquals("-",
   r.get("cs-username").get(0));
   Assert.assertEquals("2005-04-12",
   r.get("date").get(0));
   Assert.assertEquals("http://www.yahoo.com/?-",
   r.get("requestUrl").get(0));
   Assert.assertEquals("-",
   r.get("rs(Content-Type)").get(0));
   Assert.assertEquals("TCP_ERR_MISS",
   r.get("s-action").get(0));
   Assert.assertEquals("NONE",
   r.get("s-hierarchy").get(0));
   Assert.assertEquals("192.16.170.42",
   r.get("s-ip").get(0));
   Assert.assertEquals("SG-HTTP-Service",
   r.get("s-sitename").get(0));
   Assert.assertEquals("192.16.170.42",
   r.get("s-supplier-name").get(0));
   Assert.assertEquals("1736",
   r.get("sc-bytes").get(0));
   Assert.assertEquals("none",
   r.get("sc-filter-category").get(0));
   Assert.assertEquals("DENIED",
   r.get("sc-filter-result").get(0));
   Assert.assertEquals("503",
   r.get("sc-status").get(0));
   Assert.assertEquals("21:03:45",
   r.get("time").get(0));
   Assert.assertEquals("74603",
   r.get("time-taken").get(0));
   Assert.assertEquals("server_unavailable",
   r.get("x-icap-error-code").get(0));
   Assert.assertEquals("Server unavailable: No ICAP server is available to process request.",
   r.get("x-icap-error-details").get(0));
   Assert.assertEquals("-",
   r.get("x-virus-details").get(0));
   Assert.assertEquals("-",
   r.get("x-virus-id").get(0));

   }
   */
}
