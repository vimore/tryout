package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum SIEMIncidentMefField implements SupportedFormat {

    //CEF fields
    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, true),
    agentAddress(1,MefFieldType.data,"agent","",Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()),CharSequence.class,"setAgentAddress",CharSequence.class,true),
    agentDnsDomain(1,MefFieldType.data,"agent","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setAgentDnsDomain",CharSequence.class,true),
    agentHostName(1,MefFieldType.data,"agent","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setAgentHostName",CharSequence.class,true),
    agentNtDomain(1,MefFieldType.data,"agent","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setAgentNtDomain",CharSequence.class,true),
    agentType(1,MefFieldType.data,"agent","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setAgentType",CharSequence.class,true),
    baseEventCount(1,MefFieldType.data,"event","", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()),IntMarshall.class,"setBaseEventCount",Integer.class,true),
    cefEventName(1,MefFieldType.data,"event","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setCefEventName",CharSequence.class,true),
    cefSignatureId(1,MefFieldType.data,"event","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setCefSignatureId",CharSequence.class,true),
    destinationAddress(1,MefFieldType.data,"destination","",Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()),CharSequence.class,"setDestinationAddress",CharSequence.class,true),
    destinationDnsDomain(1,MefFieldType.data,"destination","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDestinationDnsDomain",CharSequence.class,true),
    destinationHostName(1,MefFieldType.data,"destination","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDestinationHostName",CharSequence.class,true),
    destinationNtDomain(1,MefFieldType.data,"destination","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDestinationNtDomain",CharSequence.class,true),
    destinationPort(1,MefFieldType.data,"destination","", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()),IntMarshall.class,"setDestinationPort",Integer.class,true),
    destinationUserName(1,MefFieldType.data,"destination","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),CharSequence.class,"setDestinationUserName",CharSequence.class,true),
    deviceAddress(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()),CharSequence.class,"setDeviceAddress",CharSequence.class,true),
    deviceDnsDomain(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceDnsDomain",CharSequence.class,true),
    deviceEventCategory(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceEventCategory",CharSequence.class,true),
    deviceExternalId(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceExternalId",CharSequence.class,true),
    deviceHostName(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceHostName",CharSequence.class,true),
    deviceNtDomain(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceNtDomain",CharSequence.class,true),
    deviceProduct(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceProduct",CharSequence.class,true),
    deviceReceiptTime(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()),LongMarshall.class,"setDeviceReceiptTime",Long.class,true),
    deviceVendor(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceVendor",CharSequence.class,true),
    deviceVersion(1,MefFieldType.data,"device","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setDeviceVersion",CharSequence.class,true),
    endTime(1,MefFieldType.data,"event","",Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()),LongMarshall.class,"setEndTime",Long.class,true),
    eventId(1,MefFieldType.data,"event","", Arrays.<MefFieldConstrait>asList(new MefLongConstraint()),LongMarshall.class,"setEventId",Long.class,true),
    externalId(1,MefFieldType.data,"event","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setExternalId",CharSequence.class,true),
    logCollectionHost(1,MefFieldType.metaData,"LogCollection","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setLogCollectionHost",CharSequence.class,true),
    severity(1,MefFieldType.data,"event","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setSeverity",CharSequence.class,true),
    sourceAddress(1,MefFieldType.data,"source","",Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()),StringMarshall.class,"setSourceAddress",CharSequence.class,true),
    sourceDnsDomain(1,MefFieldType.data,"source","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setSourceDnsDomain",CharSequence.class,true),
    sourceHostName(1,MefFieldType.data,"source","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setSourceHostName",CharSequence.class,true),
    sourceNtDomain(1,MefFieldType.data,"source","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setSourceNtDomain",CharSequence.class,true),
    sourcePort(1,MefFieldType.data,"source","", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()),IntMarshall.class,"setSourcePort",Integer.class,true),
    sourceUserName(1,MefFieldType.data,"source","",Arrays.<MefFieldConstrait>asList(new MefStringConstraint()),StringMarshall.class,"setSourceUserName",CharSequence.class,true),
    startTime(1,MefFieldType.data,"event","",Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()),LongMarshall.class,"setStartTime",Long.class,true),
    type(1,MefFieldType.data,"event","", Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()),IntMarshall.class,"setType",Integer.class,true),;

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

    private SIEMIncidentMefField(int numberOfValues,
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
        for (SIEMIncidentMefField field : values()) {
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
            format = SIEMIncidentMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
