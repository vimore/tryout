package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum FlowMefField implements SupportedFormat {

    //CEF fields
    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, true),
    applicationProtocol(1, MefFieldType.data, null, "Application level protocol such as: HTTP, HTTPS, SSHv2, Telnet, POP, IMAP, IMAPS, etc.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setApplicationProtocol", CharSequence.class, true),
    logBytes(1, MefFieldType.data, null, "raw log byte size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogBytes", Integer.class, true),
    bytesIn(1, MefFieldType.data, null, "Number of bytes transfererd inbound",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setBytesIn", Long.class, true),
    bytesOut(1, MefFieldType.data, null, "Number of bytes transfered outbound",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setBytesOut", Long.class, true),
    //cefDeviceProduct(1, MefFieldType.data, "cef", "CEF device product (in CEF header)",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setCefDeviceProduct", CharSequence.class, true),
//cefDeviceVendor(1, MefFieldType.data, "cef", "CEF device vendor (in CEF header)",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setCefDeviceVendor", CharSequence.class, true),
//cefDeviceVersion(1, MefFieldType.data, "cef", "CEF device version (in CEF header)",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setCefDeviceVersion", CharSequence.class, true),
//cefEventName(1, MefFieldType.data, "cef", "CEF Event name (in CEF header)",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setCefEventName", CharSequence.class, true),
    destinationAddress(1, MefFieldType.data, "destination", "CEF IPV4 destination address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDestinationAddress", CharSequence.class, true),
    destinationDnsDomain(1, MefFieldType.data, "destination", "CEF destination host dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationDnsDomain", CharSequence.class, true),
    destinationHostName(1, MefFieldType.data, "destination", "CEF destination host FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDestinationHostName", CharSequence.class, true),
    destinationMacAddress(1, MefFieldType.data, "destination", "CEF destination host mac address",
            Arrays.<MefFieldConstrait>asList(new MefMacAddressConstraint()), StringMarshall.class, "setDestinationMacAddress", CharSequence.class, true),
    destinationNtDomain(1, MefFieldType.data, "destination", "CEF destination host Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationNtDomain", CharSequence.class, true),
    destinationPort(1, MefFieldType.data, "destination", "CEF destination port [0..65535]",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationPort", Integer.class, true),
    destinationTranslatedAddress(1, MefFieldType.data, "destination", "CEF destination translated IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDestinationTranslatedAddress", CharSequence.class, true),
    destinationTranslatedPort(1, MefFieldType.data, "destination", "CEF destination translated port",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationTranslatedPort", Integer.class, true),
    deviceAddress(1, MefFieldType.data, "device", "CEF device IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceAddress", CharSequence.class, true),
    deviceDirection(1, MefFieldType.data, "device", "CEF direction of event (inbound : 0, outbound : 1) seen by device",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDeviceDirection", Integer.class, true),
    deviceDnsDomain(1, MefFieldType.data, "device", "CEF device dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceDnsDomain", CharSequence.class, true),
    deviceExternalId(1, MefFieldType.data, "device", "CEF device uniquely identifier",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceExternalId", CharSequence.class, true),
    deviceFacility(1, MefFieldType.data, "device", "CEF facility generating the event",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceFacility", CharSequence.class, true),
    deviceHostName(1, MefFieldType.data, "device", "CEF device FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDeviceHostName", CharSequence.class, true),
    deviceInboundInterface(1, MefFieldType.data, "device", "CEF device inbound interface",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceInboundInterface", CharSequence.class, true),
    deviceNtDomain(1, MefFieldType.data, "device", "CEF device Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceNtDomain", CharSequence.class, true),
    deviceOutboundInterface(1, MefFieldType.data, "device", "CEF device outbound interface",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceOutboundInterface", CharSequence.class, true),
    deviceProcessId(1, MefFieldType.data, "device", "CEF device process Id generating the event",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDeviceProcessId", Integer.class, true),
    deviceProcessName(1, MefFieldType.data, "device", "CEF device process name generating the event",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceProcessName", CharSequence.class, true),
    deviceTranslatedAddress(1, MefFieldType.data, "device", "CEF device IPv4 translated address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceTranslatedAddress", CharSequence.class, true),
    endTime(1, MefFieldType.data, null, "CEF event end time",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setEndTime", Long.class, true),
    externalId(1, MefFieldType.data, null, "event Id used by an originated device",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalId", CharSequence.class, true),
    receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setReceiptTime", Long.class, true),
    sourceAddress(1, MefFieldType.data, "source", "CEF source IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setSourceAddress", CharSequence.class, true),
    sourceDnsDomain(1, MefFieldType.data, "source", "CEF source dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceDnsDomain", CharSequence.class, true),
    sourceHostName(1, MefFieldType.data, "source", "CEF source FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setSourceHostName", CharSequence.class, true),
    sourceMacAddress(1, MefFieldType.data, "source", "CEF source mac address",
            Arrays.<MefFieldConstrait>asList(new MefMacAddressConstraint()), StringMarshall.class, "setSourceMacAddress", CharSequence.class, true),
    sourceNtDomain(1, MefFieldType.data, "source", "CEF source Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceNtDomain", CharSequence.class, true),
    sourcePort(1, MefFieldType.data, "source", "CEF source port [1..65535]",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourcePort", Integer.class, true),
    sourceTranslatedAddress(1, MefFieldType.data, "source", "CEF source IPv4 translated address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setSourceTranslatedAddress", CharSequence.class, true),
    sourceTranslatedPort(1, MefFieldType.data, "source", "CEF source translated port",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourceTranslatedPort", Integer.class, true),
    startTime(1, MefFieldType.data, null, "CEF start time of the event",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setStartTime", Long.class, true),
    transportProtocol(1, MefFieldType.data, null, "CEF layer 4 protocol used",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTransportProtocol", CharSequence.class, true),
    // end of CEF fields ---------------------------------------------------------
// FLOW dedicated fields 
    destinationAutonomousSystem(1, MefFieldType.data, "destination", "Destination BGP autonomous system number where N could be 2 or 4",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationAutonomousSystem", Integer.class, true),
    destinationMask(1, MefFieldType.data, "destination", "Destination IPv4 mask",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationMask", CharSequence.class, true),
    destinationVlan(1, MefFieldType.data, "destination", "Vlan used to reach the destination",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationVlan", Integer.class, true),
    direction(1, MefFieldType.data, null, "direction in which sample has been taken",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDirection", CharSequence.class, true),
    engineId(1, MefFieldType.data, "engine", "Flow switching engine type",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setEngineId", CharSequence.class, true),
    engineType(1, MefFieldType.data, "engine", "Id of the flow switching engine",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setEngineType", CharSequence.class, true),
    icmpType(1, MefFieldType.data, "ttl", "Internet Control Message Protocol (ICMP) packet type; reported as ((ICMP Type*256) + ICMP code)",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setIcmpType", Integer.class, true),
    interfaceDescription(1, MefFieldType.data, "interface", "Description of the interface trough which activity is observed",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), StringMarshall.class, "setInterfaceDescription", CharSequence.class, true),
    interfaceName(1, MefFieldType.data, "interface", "Name of the interface through which activity is observed",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setInterfaceName", CharSequence.class, true),
    packetsIn(1, MefFieldType.data, "packets", "Number of packets transferred inbount. Inbound relative to the source to destination relationship, meaning that data was flowing from source to destination",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setPacketsIn", Long.class, true),
    maxTTL(1, MefFieldType.data, "ttl", "Max flow TTL",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSamplingInterval", Integer.class, true),
    minTTL(1, MefFieldType.data, "ttl", "Min flow TTL",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSamplingInterval", Integer.class, true),
    nextHopAddress(1, MefFieldType.data, null, "Identifies the next hop of the observed flow",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setNextHopAddress", CharSequence.class, true),
    numberFlows(1, MefFieldType.data, null, "Number of flows",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setNumberFlows", Long.class, true),
    packetsOut(1, MefFieldType.data, null, "Number of packets transferred outbound. Outbound relative to the source to destination relationship, meaning that data was flowing from source to destination",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setPacketsOut", Long.class, true),
    samplingInterval(1, MefFieldType.data, "sampling", "When using sampled NetFlow, the rate at which packets are sampled i.e.: a value of 100 indicates that one of every 100 packets is sampled",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSamplingInterval", Integer.class, true),
    samplingAlgorithm(1, MefFieldType.data, "sampling", "Sampling type (deterministic/random)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSamplingAlgorithm", String.class, true),
    sourceAutonomousSystem(1, MefFieldType.data, "source", "Source BGP autonomous system number where N could be 2 or 4",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourceAutonomousSystem", Integer.class, true),
    sourceMask(1, MefFieldType.data, "source", "Source IPv4 mask",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceMask", CharSequence.class, true),
    sourceTos(1, MefFieldType.data, "source", "Type of Service byte setting when entering incoming interface",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourceTos", Integer.class, true),
    sourceVlan(1, MefFieldType.data, "source", "Type of Service byte setting when entering incoming interface",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourceVlan", Integer.class, true),
    tcpFlags(1, MefFieldType.data, null, "Cumulative of all the TCP flags seen for this flow",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setTcpFlags", Integer.class, true),
    totalBytesExp(1, MefFieldType.data, "total", "Counter with length N x 8 bits for bytes for the number of bytes exported by the Observation Domain",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setTotalBytesExp", Long.class, true),
    totalFlowsExp(1, MefFieldType.data, "total", "Counter with length N x 8 bits for bytes for the number of flows exported by the Observation Domain",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setTotalFlowsExp", Long.class, true),
    totalPacketsExp(1, MefFieldType.data, "total", "Counter with length N x 8 bits for bytes for the number of packets exported by the Observation Domain",
            Arrays.<MefFieldConstrait>asList(new MefLongConstraint()), LongMarshall.class, "setTotalPacketsExp", Long.class, true),

    // NameOrIpWrapper
    destinationNameOrIp(1, MefFieldType.data, "destination", "CEF destination host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDestinationNameOrIp", CharSequence.class, true),
    sourceNameOrIp(1, MefFieldType.data, "source", "CEF source host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setSourceNameOrIp", CharSequence.class, true),
    deviceNameOrIp(1, MefFieldType.data, "device", "CEF device host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDeviceNameOrIp", CharSequence.class, true),
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

    private FlowMefField(int numberOfValues,
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
        for (FlowMefField field : values()) {
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
            format = FlowMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
