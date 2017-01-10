package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum IAMMefField implements SupportedFormat {


    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, true), auditSource(1, MefFieldType.data, null, "Audit source name for instance Security, provided by snare)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAuditSource", CharSequence.class, true),
    receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setReceiptTime", Long.class, true),
    logBytes(1, MefFieldType.data, null, "raw log byte size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogBytes", Integer.class, true),
    cefEventName(1, MefFieldType.data, null, "A human readable description of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefEventName", CharSequence.class, true),
    cefSignatureId(1, MefFieldType.data, null, "Unique identifier for an event type. EventID + eventlogType(Success Audit, Failure Audit, …)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefSignatureId", CharSequence.class, true),
    destinationLogonGUID(1, MefFieldType.data, "destination", "Logon GUID as a destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationLogonGUID", CharSequence.class, true),
    destinationLogonID(1, MefFieldType.data, "destination", "Logon ID as a destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationLogonID", CharSequence.class, true),
    destinationNameOrIp(1, MefFieldType.data, "destination", "Destination host or IP wrapper", Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDestinationNameOrIp", CharSequence.class, true),
    destinationProcessName(1, MefFieldType.data, "destination", "Destination process name", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationProcessName", CharSequence.class, true),
    destinationSecurityID(1, MefFieldType.data, "destination", "Destination Secuity ID (run cmd line 'wmic useraccount get name,domain,sid')", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationSecurityID", CharSequence.class, true),
    destinationLdapUser(1, MefFieldType.data, "destination", "Destination user name", Arrays.<MefFieldConstrait>asList(new IAMMefLdapStringConstraint()), StringMarshall.class, "setDestinationLdapUser", CharSequence.class, true),
    destinationUserName(1, MefFieldType.data, "destination", "Destination user name", Arrays.<MefFieldConstrait>asList(new MefLowerCaseStringConstraint()), StringMarshall.class, "setDestinationUserName", CharSequence.class, true),
    destinationGroup(1, MefFieldType.data, "destination", "Destination group, group affected by the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationGroup", CharSequence.class, true),
    destinationGroupSecurityID(1, MefFieldType.data, "destination", "Destination group Secuity ID (run cmd line 'wmic useraccount get name,domain,sid')", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationGroupSecurityID", CharSequence.class, true),
    destinationGroupNtDomain(1, MefFieldType.data, "destination", "Domain of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationGroupNtDomain", CharSequence.class, true),
    destinationShareName(1, MefFieldType.data, "destination", "Destination group, group affected by the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationShareName", CharSequence.class, true),
    destinationServiceName(1, MefFieldType.data, "source", "service name  of the source", Arrays.<MefFieldConstrait>asList(new IAMMefServiceNameConstraint()), StringMarshall.class, "setDestinationServiceName", CharSequence.class, true),
    destinationServiceSecurityID(1, MefFieldType.data, "source", "service name  of the source", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationServiceSecurityID", CharSequence.class, true),
    deviceNameOrIp(1, MefFieldType.data, "device", "Host and IP wrapper of the device generating the event", Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDeviceNameOrIp", CharSequence.class, true),
    deviceUserName(1, MefFieldType.data, "device", "User involved in event (often as destination but not always relevant)", Arrays.<MefFieldConstrait>asList(new MefLowerCaseStringConstraint()), StringMarshall.class, "setDeviceUserName", CharSequence.class, true),
    eventLogType(1, MefFieldType.data, "event", "event log type (Success Audit, failure audit, ...)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setEventLogType", CharSequence.class, true),
    eventSidType(1, MefFieldType.data, null, "User Type of deviceUserName", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setEventSidType", CharSequence.class, true),
    sourceAddress(1, MefFieldType.data, "source", "IP address of the source of the event", Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setSourceAddress", CharSequence.class, true),
    sourceLogonID(1, MefFieldType.data, "source", "logonID of the source", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceLogonID", CharSequence.class, true),
    sourceLogonType(1, MefFieldType.data, "source", "Logon type of the source", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceLogonType", CharSequence.class, true),
    sourceServiceName(1, MefFieldType.data, "source", "service name  of the source", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceServiceName", CharSequence.class, true),
    sourceServiceSecurityID(1, MefFieldType.data, "source", "service name  of the source", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceServiceSecurityID", CharSequence.class, true),
    sourceNameOrIp(1, MefFieldType.data, "source", "Host and IP wrapper of the source of the event", Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setSourceNameOrIp", CharSequence.class, true),
    sourceProcessID(1, MefFieldType.data, "source", "ProcessId of the source of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceProcessID", CharSequence.class, true),
    sourceProcessName(1, MefFieldType.data, "source", "Process name of the source of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceProcessName", CharSequence.class, true),
    sourceSecurityID(1, MefFieldType.data, "source", "Security ID of the source of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceSecurityID", CharSequence.class, true),
    sourceUserName(1, MefFieldType.data, "source", "Username as source of the event", Arrays.<MefFieldConstrait>asList(new MefLowerCaseStringConstraint()), StringMarshall.class, "setSourceUserName", CharSequence.class, true),
    startTime(1, MefFieldType.data, null, "ms sec time event has been generated", Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setStartTime", Long.class, true),
    adAccountExpires(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdAccountExpires", CharSequence.class, true),
    adAllowedToDelegateTo(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdAllowedToDelegateTo", CharSequence.class, true),
    authenticationPackage(1, MefFieldType.data, null, "Authentication package involved in authentication", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAuthenticationPackage", CharSequence.class, true),
    certificateIssuer(1, MefFieldType.data, null, "Cerrtificate issuer of the certificate involved in event (as source)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertificateIssuer", CharSequence.class, true),
    certificateSerialNumber(1, MefFieldType.data, null, "Cerrtificate serial number of the certificate involved in event (as source)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertificateSerialNumber", CharSequence.class, true),
    certificateThumbPrint(1, MefFieldType.data, null, "Cerrtificate thumbprint of the certificate involved in event (as source)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertificateThumbPrint", CharSequence.class, true),
    desiredAccess(1, MefFieldType.data, null, "Desired Access used (attempted on a privileged object)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDesiredAccess", CharSequence.class, true),
    destinationNtDomain(1, MefFieldType.data, "destination", "Domain of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationNtDomain", CharSequence.class, true),
    destinationObjectHandle(1, MefFieldType.data, null, "Object handle of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationObjectHandle", CharSequence.class, true),
    destinationObjectName(1, MefFieldType.data, null, "Object name of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationObjectName", CharSequence.class, true),
    destinationObjectServer(1, MefFieldType.data, null, "Object object server of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationObjectServer", CharSequence.class, true),
    destinationObjectType(1, MefFieldType.data, null, "Object type of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationObjectType", CharSequence.class, true),
    destinationServiceServer(1, MefFieldType.data, null, "System component affected by privileged service call", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationServiceServer", CharSequence.class, true),
    adDisplayName(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdDisplayName", CharSequence.class, true),
    adDnsHostName(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdDnsHostName", CharSequence.class, true),
    adHomeDirectory(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdHomeDirectory", CharSequence.class, true),
    adHomeDrive(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdHomeDrive", CharSequence.class, true),
    logonProcess(1, MefFieldType.data, null, "Trusted logon process name involved in auth", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogonProcess", CharSequence.class, true),
    transitedService(1, MefFieldType.data, null, "server application pending kerberos auth", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTransitedService", CharSequence.class, true),
    adOldUacValue(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdOldUacValue", CharSequence.class, true),
    packageName(1, MefFieldType.data, null, "NTLM version involved in auth when authenticated via the NTLM ", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setPackageName", CharSequence.class, true),
    adPasswdLastSet(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdPasswdLastSet", CharSequence.class, true),
    preAuthenticationType(1, MefFieldType.data, null, "", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setPreAuthenticationType", CharSequence.class, true),
    adPrimaryGroupID(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdPrimaryGroupID", CharSequence.class, true),
    privileges(1, MefFieldType.data, null, "Privileges involved in event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setPrivileges", CharSequence.class, true),
    adProfilePath(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdProfilePath", CharSequence.class, true),
    resultCode(1, MefFieldType.data, null, "operation result code (sometime names failure code)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setResultCode", CharSequence.class, true),
    adSamAccountName(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdSamAccountName", CharSequence.class, true),
    adScriptPath(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdScriptPath", CharSequence.class, true),
    adServerPrincipalNames(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdServerPrincipalNames", CharSequence.class, true),
    adSidHistory(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdSidHistory", CharSequence.class, true),
    sourceNtDomain(1, MefFieldType.data, "source", "NT domain of the source of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceNtDomain", CharSequence.class, true),
    sourceLogonGUID(1, MefFieldType.data, "source", "Logon GUID of the source of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceLogonGUID", CharSequence.class, true),
    status(1, MefFieldType.data, null, "status on auth failure", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setStatus", CharSequence.class, true),
    subStatus(1, MefFieldType.data, null, "sub status on auth failure", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSubStatus", CharSequence.class, true),
    ticketEncryptionType(1, MefFieldType.data, null, "kerberos encryption type", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTicketEncryptionType", CharSequence.class, true),
    ticketOptions(1, MefFieldType.data, null, "kerberos ticket options", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTicketOptions", CharSequence.class, true),
    adUserAccountControl(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdUserAccountControl", CharSequence.class, true),
    adUserParameters(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdUserParameters", CharSequence.class, true),
    adUserPrincipalName(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdUserPrincipalName", CharSequence.class, true),
    adUserWorkstation(1, MefFieldType.data, null, "AD Account Field", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setAdUserWorkstation", CharSequence.class, true),
    eventSeverity(1, MefFieldType.data, null, "severity of the event as seen by snare", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setEventSeverity", Integer.class, true),
    sourcePort(1, MefFieldType.data, "source", "port of the source of the event", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourcePort", Integer.class, true),
    keyLength(1, MefFieldType.data, null, "Length of key protecting the 'secure channel' on auth", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setKeyLength", Integer.class, true),
    deviceAddress(1, MefFieldType.data, "device", "IP address of the device generating the event", Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceAddress", CharSequence.class, true),
    deviceHostName(1, MefFieldType.data, "device", "hostname of the device", Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDeviceHostName", CharSequence.class, true),
    destinationHostName(1, MefFieldType.data, "destination", "hostname of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDestinationHostName", CharSequence.class, true),
    sourceHostName(1, MefFieldType.data, "source", "hostname of the source of the event", Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setSourceHostName", CharSequence.class, true),
    deviceDnsDomain(1, MefFieldType.data, "device", "hostname of the device", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceDnsDomain", CharSequence.class, true),
    destinationDnsDomain(1, MefFieldType.data, "destination", "hostname of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationDnsDomain", CharSequence.class, true),
    sourceDnsDomain(1, MefFieldType.data, "source", "hostname of the source of the event", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceDnsDomain", CharSequence.class, true),
    destinationAddress(1, MefFieldType.data, "destination", "IP address of the destination of the event", Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDestinationAddress", CharSequence.class, true),
    // LogCollection fields
    logCollectionCategory(1, MefFieldType.metaData, "LogCollection", "Log Collection channel category.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionCategory", CharSequence.class, true),
    logCollectionContainer(1, MefFieldType.metaData, "LogCollection", "Log Collection container",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionContainer", CharSequence.class, true),
    logCollectionHost(1, MefFieldType.metaData, "LogCollection", "Log Collection host",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionHost", CharSequence.class, false),
    logCollectionType(1, MefFieldType.metaData, "LogCollection", "Log Collection channel Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionType", CharSequence.class, false),

    // logsource fields
    logSourceType(1, MefFieldType.metaData, "LogCollection", "Log source Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogSourceType", CharSequence.class, true),
    externalLogSourceType(1, MefFieldType.metaData, "LogCollection", "External log source Type, before conversion.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalLogSourceType", CharSequence.class, true);

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

    private IAMMefField(int numberOfValues,
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
        for (IAMMefField field : values()) {
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
            format = IAMMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
