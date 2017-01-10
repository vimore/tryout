package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum CertMefField implements SupportedFormat {

    //CEF fields
    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, false),
    startTime(1, MefFieldType.data, null, "CEF start time of the event",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setStartTime", Long.class, true),
    receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setReceiptTime", Long.class, true),
    logBytes(1, MefFieldType.data, null, "raw log byte size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogBytes", Integer.class, true),
    certMd5(1, MefFieldType.data, null, "Certificate MD5",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertMd5", CharSequence.class, true),
    certSerial(1, MefFieldType.data, "cert", "Certificate Serial number",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertSerial", CharSequence.class, true),
    certIssuer(1, MefFieldType.data, "cert", "Certificate issuer",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertIssuer", CharSequence.class, true),
    certCommonName(1, MefFieldType.data, "cert", "Certificate common name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertCommonName", CharSequence.class, true),
    certSubject(1, MefFieldType.data, "cert", "Certificate subject",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCertSubject", CharSequence.class, true),
    certNoValidBefore(1, MefFieldType.data, "cert", "Certificate date of start of validity",
            Arrays.<MefFieldConstrait>asList(new CertMefIsEpochTimestampConstraint()), LongMarshall.class, "setCertNoValidBefore", Long.class, true),
    certNoValidAfter(1, MefFieldType.data, "cert", "Certificate date of end of validity",
            Arrays.<MefFieldConstrait>asList(new CertMefIsEpochTimestampConstraint()), LongMarshall.class, "setCertNoValidAfter", Long.class, true),
    sourcePort(1, MefFieldType.data, "source", "CEF source port [1..65535]",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourcePort", Integer.class, true),
    destinationPort(1, MefFieldType.data, "destination", "CEF destination port [0..65535]",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationPort", Integer.class, true),
    transportProtocol(1, MefFieldType.data, null, "CEF layer 4 protocol used",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTransportProtocol", CharSequence.class, true),
    destinationNameOrIp(1, MefFieldType.data, "destination", "CEF destination host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDestinationNameOrIp", CharSequence.class, true),
    destinationAddress(1, MefFieldType.data, "destination", "CEF IPV4 destination address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDestinationAddress", CharSequence.class, true),
    destinationDnsDomain(1, MefFieldType.data, "destination", "CEF destination host dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationDnsDomain", CharSequence.class, true),
    destinationHostName(1, MefFieldType.data, "destination", "CEF destination host FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDestinationHostName", CharSequence.class, true),
    //destinationMacAddress(1, MefFieldType.data, "destination", "CEF destination host mac address",
//Arrays.<MefFieldConstrait>asList(new MefMacAddressConstraint()), StringMarshall.class,"setDestinationMacAddress", CharSequence.class, true),
//destinationNtDomain(1, MefFieldType.data, "destination", "CEF destination host Windows domain",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDestinationNtDomain", CharSequence.class, true),
//destinationTranslatedAddress(1, MefFieldType.data, "destination", "CEF destination translated IPv4 address",
//Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class,"setDestinationTranslatedAddress", CharSequence.class, true),
//destinationTranslatedPort(1, MefFieldType.data, "destination", "CEF destination translated port",
//Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class,"setDestinationTranslatedPort", Integer.class, true),
    deviceAddress(1, MefFieldType.data, "device", "CEF device IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceAddress", CharSequence.class, true),
    //deviceDirection(1, MefFieldType.data, "device", "CEF direction of event (inbound : 0, outbound : 1) seen by device",
//Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class,"setDeviceDirection", Integer.class, true),
    deviceDnsDomain(1, MefFieldType.data, "device", "CEF device dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceDnsDomain", CharSequence.class, true),
    deviceExternalId(1, MefFieldType.data, "device", "CEF device uniquely identifier",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceExternalId", CharSequence.class, true),
    deviceFacility(1, MefFieldType.data, "device", "CEF facility generating the event",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceFacility", CharSequence.class, true),
    deviceHostName(1, MefFieldType.data, "device", "CEF device FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDeviceHostName", CharSequence.class, true),
    //deviceInboundInterface(1, MefFieldType.data, "device", "CEF device inbound interface",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceInboundInterface", CharSequence.class, true),
    deviceNtDomain(1, MefFieldType.data, "device", "CEF device Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceNtDomain", CharSequence.class, true),
    //deviceOutboundInterface(1, MefFieldType.data, "device", "CEF device outbound interface",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceOutboundInterface", CharSequence.class, true),
    deviceProcessId(1, MefFieldType.data, "device", "CEF device process Id generating the event",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDeviceProcessId", Integer.class, true),
    deviceProcessName(1, MefFieldType.data, "device", "CEF device process name generating the event",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceProcessName", CharSequence.class, true),
    //deviceTranslatedAddress(1, MefFieldType.data, "device", "CEF device IPv4 translated address",
//Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class,"setDeviceTranslatedAddress", CharSequence.class, true),
//endTime(1, MefFieldType.data, null, "CEF event end time",
//Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class,"setEndTime", Long.class, true),
    externalId(1, MefFieldType.data, null, "event Id used by an originated device",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalId", CharSequence.class, true),
    //receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
//Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class,"setReceiptTime", Long.class, true),
    sourceAddress(1, MefFieldType.data, "source", "CEF source IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setSourceAddress", CharSequence.class, true),
    sourceDnsDomain(1, MefFieldType.data, "source", "CEF source dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceDnsDomain", CharSequence.class, true),
    sourceHostName(1, MefFieldType.data, "source", "CEF source FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setSourceHostName", CharSequence.class, true),
    //sourceMacAddress(1, MefFieldType.data, "source", "CEF source mac address",
//Arrays.<MefFieldConstrait>asList(new MefMacAddressConstraint()), StringMarshall.class,"setSourceMacAddress", CharSequence.class, true),
    sourceNtDomain(1, MefFieldType.data, "source", "CEF source Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceNtDomain", CharSequence.class, true),
    //sourceTranslatedAddress(1, MefFieldType.data, "source", "CEF source IPv4 translated address",
//Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class,"setSourceTranslatedAddress", CharSequence.class, true),
//sourceTranslatedPort(1, MefFieldType.data, "source", "CEF source translated port",
//Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class,"setSourceTranslatedPort", Integer.class, true),
// end of CEF fields ---------------------------------------------------------
    destinationMask(1, MefFieldType.data, "destination", "Destination IPv4 mask",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationMask", CharSequence.class, true),

    // NameOrIpWrapper
    deviceNameOrIp(1, MefFieldType.data, "device", "CEF device host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDeviceNameOrIp", CharSequence.class, true),
    sourceNameOrIp(1, MefFieldType.data, "source", "CEF source host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setSourceNameOrIp", CharSequence.class, true),

    // LogCollection fields
    logCollectionCategory(1, MefFieldType.metaData, "LogCollection", "Log Collection channel category.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionCategory", CharSequence.class, true),
    logCollectionContainer(1, MefFieldType.metaData, "LogCollection", "Log Collection container",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionContainer", CharSequence.class, true),
    logCollectionHost(1, MefFieldType.metaData, "LogCollection", "Log Collection host",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionHost", CharSequence.class, false),
    logCollectionType(1, MefFieldType.metaData, "LogCollection", "Log Collection channel Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionType", CharSequence.class, false),
    logCollectionId(1, MefFieldType.metaData, "LogCollection", "Log Collection record id",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogCollectionId", Integer.class, false),
    logCollectionTime(1, MefFieldType.metaData, "LogCollection", "Log Collection timestamp",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setLogCollectionTime", Long.class, true),
    syslogMessage(1, MefFieldType.metaData, "LogCollection", "Syslog message",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSyslogMessage", CharSequence.class, true),
    // logsource fields
    logSourceType(1, MefFieldType.metaData, "LogCollection", "Log source Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogSourceType", CharSequence.class, true),
    externalLogSourceType(1, MefFieldType.metaData, "LogCollection", "External log source Type, before conversion.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalLogSourceType", CharSequence.class, true),
    cefSignatureId(1, MefFieldType.data, "cef", "CEF unique identifier per event types (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefSignatureId", CharSequence.class, true),;

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

    private CertMefField(int numberOfValues,
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
        for (CertMefField field : values()) {
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
            format = CertMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
