package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum IAMDBMefField implements SupportedFormat {

    //CEF fields
    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, true),
    receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setReceiptTime", Long.class, true),
    logBytes(1, MefFieldType.data, null, "raw log byte size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogBytes", Integer.class, true),
    deviceAddress(1, MefFieldType.data, "device", "CEF device IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceAddress", CharSequence.class, true),
    deviceDnsDomain(1, MefFieldType.data, "device", "CEF device dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceDnsDomain", CharSequence.class, true),
    deviceHostName(1, MefFieldType.data, "device", "CEF device FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDeviceHostName", CharSequence.class, true),
    deviceNtDomain(1, MefFieldType.data, "device", "CEF device Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceNtDomain", CharSequence.class, true),
    deviceProcessId(1, MefFieldType.data, "device", "CEF device process Id generating the event",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDeviceProcessId", Integer.class, true),
    deviceProcessName(1, MefFieldType.data, "device", "CEF device process name generating the event",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceProcessName", CharSequence.class, true),
    canonicalName(1, MefFieldType.data, null, "object canonical name : somedomain.com/Users/<Object Name>",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setCanonicalName", CharSequence.class, true),
    objectDistinguishedName(1, MefFieldType.data, null, "object distinguished name",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setObjectDistinguishedName", CharSequence.class, true),
    objectManager(1, MefFieldType.data, null, "manager of the object",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setObjectManager", CharSequence.class, true),
    creationDate(1, MefFieldType.data, null, "date of creation of object",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setCreationDate", Long.class, true),
    lastModificationDate(1, MefFieldType.data, null, "date of last change on object",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setLastModificationDate", Long.class, true),
    displayName(1, MefFieldType.data, null, "object display name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDisplayName", CharSequence.class, true),
    objectGUID(1, MefFieldType.data, null, "GUID of the object",
            Arrays.<MefFieldConstrait>asList(new MSADGUIDConstraint()), StringMarshall.class, "setObjectGUID", CharSequence.class, true),
    lockoutDuration(1, MefFieldType.data, null, "object GUID",
            Arrays.<MefFieldConstrait>asList(new MSAD100NsLongConstraint()), LongMarshall.class, "setLockoutDuration", Long.class, true),
    lockoutThreshold(1, MefFieldType.data, null, "Number of invalid logon attempts permitted before account is account is locked out",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), StringMarshall.class, "setLockoutThreshold", CharSequence.class, true),
    objectSid(1, MefFieldType.data, null, "object Security ID",
            Arrays.<MefFieldConstrait>asList(new MSADSIDConstraint()), StringMarshall.class, "setObjectSid", CharSequence.class, true),
    objectCategory(1, MefFieldType.data, null, "object category canonical name",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setObjectCategory", CharSequence.class, true),
    objectDescription(1, MefFieldType.data, null, "object description",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setObjectDescription", CharSequence.class, true),
    commonName(1, MefFieldType.data, null, "Common name of the object",
            Arrays.<MefFieldConstrait>asList(new MefLowerCaseStringConstraint()), StringMarshall.class, "setCommonName", CharSequence.class, true),
    userAccountControl(1, MefFieldType.data, null, "control user account behavior",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setUserAccountControl", Integer.class, true),
    badPwdCount(1, MefFieldType.data, null, "number of time a user tried to log on using an incorrect passwd",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), StringMarshall.class, "setBadPwdCount", CharSequence.class, true),
    badPasswordTime(1, MefFieldType.data, null, "last time an attempt to log on was made with an invalid password",
            Arrays.<MefFieldConstrait>asList(new MSADFileDateLongConstraint()), LongMarshall.class, "setBadPasswordTime", Long.class, true),
    lastLogoff(1, MefFieldType.data, null, "last successfull log off time",
            Arrays.<MefFieldConstrait>asList(new MSADFileDateLongConstraint()), LongMarshall.class, "setLastLogoff", Long.class, true),
    lastLogon(1, MefFieldType.data, null, "last successfull log on time",
            Arrays.<MefFieldConstrait>asList(new MSADFileDateLongConstraint()), LongMarshall.class, "setLastLogon", Long.class, true),
    pwdLastSet(1, MefFieldType.data, null, "last password changeg time",
            Arrays.<MefFieldConstrait>asList(new MSADFileDateLongConstraint()), LongMarshall.class, "setPwdLastSet", Long.class, true),
    accountExpires(1, MefFieldType.data, null, "account expiration date",
            Arrays.<MefFieldConstrait>asList(new MSADFileDateLongConstraint()), LongMarshall.class, "setAccountExpires", Long.class, true),
    logonCount(1, MefFieldType.data, null, "number of successfull log on",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogonCount", Integer.class, true),
    samAccountName(1, MefFieldType.data, null, "backward compatibility account name for Win NT, Win 95, Win 98 and Lan manager",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSamAccountName", CharSequence.class, true),
    samAccountType(1, MefFieldType.data, null, "backward compatibility account type for Win NT, Win 95, Win 98 and Lan manager",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSamAccountType", CharSequence.class, true),
    lockoutTime(1, MefFieldType.data, null, "date account has been locked out",
            Arrays.<MefFieldConstrait>asList(new MSADFileDateLongConstraint()), LongMarshall.class, "setLockoutTime", Long.class, true),
    title(1, MefFieldType.data, null, "user's job title",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTitle", CharSequence.class, true),
    locationStr(1, MefFieldType.data, null, "user's location",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLocation", CharSequence.class, true),
    division(1, MefFieldType.data, null, "entity in which user works",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDivision", CharSequence.class, true),
    objectMail(1, MefFieldType.data, null, "email of the object",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setObjectMail", CharSequence.class, true),
    userWorkstation(1, MefFieldType.data, null, "user's workstation name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUserWorkstation", CharSequence.class, true),
    isCriticalSystemObject(1, MefFieldType.data, null, "DOC TO DO",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setIsCriticalSystemObject", CharSequence.class, true),
    objectClass(1, MefFieldType.data, null, "object type ; user, group, computer",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setObjectClass", CharSequence.class, true),
    /*distinguishedName(1, MefFieldType.data, null, "canonical distinguished name of the object",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setDistinguishedName", CharSequence.class, true),*/
    objectName(1, MefFieldType.data, null, "object name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setObjectName", CharSequence.class, true),
    objectCN(1, MefFieldType.data, null, "object cn ",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setObjectCN", CharSequence.class, true),
    memberOf(1, MefFieldType.data, null, "list of canonical group object belongs to",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setMemberOf", CharSequence.class, true),
    primaryGroupID(1, MefFieldType.data, null, "RID of the primary group of the oject",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setPrimaryGroupID", CharSequence.class, true),
    servicePrincipalName(1, MefFieldType.data, null, "SPN bound to an object",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setServicePrincipalName", CharSequence.class, true),
    userCertificate(1, MefFieldType.data, null, "public key of user or computer",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUserCertificate", CharSequence.class, true),
    operatingSystem(1, MefFieldType.data, null, "computer's operating system",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOperatingSystem", CharSequence.class, true),
    operatingSystemVersion(1, MefFieldType.data, null, "computer's operating system version",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOperatingSystemVersion", CharSequence.class, true),
    operatingSystemServicePack(1, MefFieldType.data, null, "computer's operating system service pack version",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOperatingSystemServicePack", CharSequence.class, true),
    serverReferenceBL(1, MefFieldType.data, null, "server reference back link",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setServerReferenceBL", CharSequence.class, true),
    dnsHostName(1, MefFieldType.data, null, "computer's dns name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDnsHostName", CharSequence.class, true),
    managedBy(1, MefFieldType.data, null, "user managing the computer or the group",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setManagedBy", CharSequence.class, true),
    rIdSetReferences(1, MefFieldType.data, null, "RID management location",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setRIdSetReferences", CharSequence.class, true),
    frsComputerReferenceBL(1, MefFieldType.data, null, "location of replicat set to which computer belongs",
            Arrays.<MefFieldConstrait>asList(new MSADCanonicalConstraint()), StringMarshall.class, "setFrsComputerReferenceBL", CharSequence.class, true),
    networkAddress(1, MefFieldType.data, null, "computer's IP adress",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setNetworkAddress", CharSequence.class, true),
    logSourceType(1, MefFieldType.metaData, "LogCollection", "Log source Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogSourceType", CharSequence.class, true),
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
    private final Map<String, SupportedFormat> supportedFormatfields = new HashMap<String, SupportedFormat>();
    private Map<String, String> stack = null;
    private String avroObjectClass = null;

    private IAMDBMefField(int numberOfValues,
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
        if (this.supportedFormatfields.isEmpty()) {
            initSupportedFormatFields();
        }
        return this.supportedFormatfields.values();
    }

    private void initSupportedFormatFields() {
        for (IAMDBMefField field : values()) {
            if (null != field.getPrettyName()) {
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
        if (null == this.avroObjectClass) {
            this.avroObjectClass = SupportedFormatIntrospectionUtils.genAvroObjectClass(namespace, prefix.name());
        }
        return this.avroObjectClass;
    }

    @Override
    public Map<String, String> getStack(String prefix) {
        if (null == this.stack) {
            this.stack = SupportedFormatIntrospectionUtils.getStack(prefix, namespace);
        }
        return stack;
    }

    @Override
    public SupportedFormat getByPrettyName(String prettyName) {
        //MefField Case :  prettyName = name
        SupportedFormat format = null;
        try {
            format = IAMDBMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
