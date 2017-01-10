package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum HostJobMefField implements SupportedFormat {

    //CEF fields
    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, true),
    receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setReceiptTime", Long.class, true),
    logBytes(1, MefFieldType.data, null, "raw log byte size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogBytes", Integer.class, true),
    cefSignatureId(1, MefFieldType.data, null, "Unique identifier for an event type. EventID + eventlogType(Success Audit, Failure Audit, …)", Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefSignatureId", CharSequence.class, true),
    startTime(1, MefFieldType.data, null, "CEF start time of the event",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setStartTime", Long.class, true),
    deviceAddress(1, MefFieldType.data, "device", "CEF device IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceAddress", CharSequence.class, true),
    //deviceDirection(1, MefFieldType.data, "device", "CEF direction of event (inbound : 0, outbound : 1) seen by device",
//Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class,"setDeviceDirection", Integer.class, true),
    deviceDnsDomain(1, MefFieldType.data, "device", "CEF device dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceDnsDomain", CharSequence.class, true),
    //deviceExternalId(1, MefFieldType.data, "device", "CEF device uniquely identifier",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceExternalId", CharSequence.class, true),
//deviceFacility(1, MefFieldType.data, "device", "CEF facility generating the event",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceFacility", CharSequence.class, true),
    deviceHostName(1, MefFieldType.data, "device", "CEF device FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDeviceHostName", CharSequence.class, true),
    deviceUserName(1, MefFieldType.data, "device", "username that will run the job  (login)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceUserName", CharSequence.class, true),

    //deviceInboundInterface(1, MefFieldType.data, "device", "CEF device inbound interface",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceInboundInterface", CharSequence.class, true),
//deviceNtDomain(1, MefFieldType.data, "device", "CEF device Windows domain",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceNtDomain", CharSequence.class, true),
//deviceOutboundInterface(1, MefFieldType.data, "device", "CEF device outbound interface",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceOutboundInterface", CharSequence.class, true),
//deviceProcessId(1, MefFieldType.data, "device", "CEF device process Id generating the event",
//Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class,"setDeviceProcessId", Integer.class, true),
//deviceProcessName(1, MefFieldType.data, "device", "CEF device process name generating the event",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setDeviceProcessName", CharSequence.class, true),
//deviceTranslatedAddress(1, MefFieldType.data, "device", "CEF device IPv4 translated address",
//Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class,"setDeviceTranslatedAddress", CharSequence.class, true),
//endTime(1, MefFieldType.data, null, "CEF event end time",
//Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class,"setEndTime", Long.class, true),
    externalId(1, MefFieldType.data, null, "event Id used by an originated device",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalId", CharSequence.class, true),

    // NameOrIpWrapper
    deviceNameOrIp(1, MefFieldType.data, "device", "CEF device host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDeviceNameOrIp", CharSequence.class, true),
    deviceSerialNumber(1, MefFieldType.data, "device", "CEF device serial number",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceSerialNumber", CharSequence.class, true),
    processFilePath(1, MefFieldType.data, "process", "process file full path",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setProcessFilePath", CharSequence.class, true),
    processFileMd5(1, MefFieldType.data, "process", "md5 of the file",
            Arrays.<MefFieldConstrait>asList(new MefMd5StringConstraint()), StringMarshall.class, "setProcessFileMd5", CharSequence.class, true),
    processName(1, MefFieldType.data, "process", "name of the process",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setProcessName", CharSequence.class, true),
    jobCmd(1, MefFieldType.data, "job", "the entire command executed in the job",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setJobCmd", CharSequence.class, true),
    ProcessFileVersion(1, MefFieldType.data, "process", "version of the bin file",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setProcessFileVersion", CharSequence.class, false),
    jobName(1, MefFieldType.data, "job", "name of the job",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setJobName", CharSequence.class, false),
    jobLocation(1, MefFieldType.data, "job", "location of the job",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setJobLocation", CharSequence.class, false),

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

    private HostJobMefField(int numberOfValues,
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
        for (HostJobMefField field : values()) {
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
            format = HostJobMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
