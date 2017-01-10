package com.securityx.model.mef.field.api;

import com.securityx.model.mef.field.api.utils.SupportedFormatIntrospectionUtils;
import com.securityx.model.mef.field.constraint.*;
import com.securityx.model.mef.field.marshaller.IntMarshall;
import com.securityx.model.mef.field.marshaller.LongMarshall;
import com.securityx.model.mef.field.marshaller.StringMarshall;

import java.util.*;

public enum WebProxyMefField implements SupportedFormat {

    //CEF fields
    uuid(1, MefFieldType.data, null, "Event uuid collected from rawlog or crafted",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setUuid", CharSequence.class, true),
    logBytes(1, MefFieldType.data, null, "raw log byte size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogBytes", Integer.class, true),
    applicationProtocol(1, MefFieldType.data, null, "Application level protocol such as: HTTP, HTTPS, SSHv2, Telnet, POP, IMAP, IMAPS, etc.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setApplicationProtocol", CharSequence.class, true),
    baseEventCount(1, MefFieldType.data, null, "Number of occurence of the same event",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setBaseEventCount", Integer.class, true),
    bytesIn(1, MefFieldType.data, null, "Number of bytes transfererd inbound",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setBytesIn", Integer.class, true),
    bytesOut(1, MefFieldType.data, null, "Number of bytes transfered outbound",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setBytesOut", Integer.class, true),
    cefDeviceProduct(1, MefFieldType.data, "cef", "CEF device product (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefDeviceProduct", CharSequence.class, true),
    cefDeviceVendor(1, MefFieldType.data, "cef", "CEF device vendor (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefDeviceVendor", CharSequence.class, true),
    cefDeviceVersion(1, MefFieldType.data, "cef", "CEF device version (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefDeviceVersion", CharSequence.class, true),
    cefEventName(1, MefFieldType.data, "cef", "CEF Event name (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefEventName", CharSequence.class, true),
    cefEventSeverity(1, MefFieldType.data, "cef", "CEF Event severity [0(lowest)..10(highest)] (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefEventSeverity", CharSequence.class, true),
    cefHeaderVersion(1, MefFieldType.data, "cef", "CEF format version (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setCefHeaderVersion", Integer.class, true),
    cefSignatureId(1, MefFieldType.data, "cef", "CEF unique identifier per event types (in CEF header)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setCefSignatureId", CharSequence.class, true),
    destinationAddress(1, MefFieldType.data, "destination", "CEF IPV4 destination address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDestinationAddress", CharSequence.class, true),
    destinationDnsDomain(1, MefFieldType.data, "destination", "CEF destination host dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint(), new MefTLDNormalizedConstraint()), StringMarshall.class, "setDestinationDnsDomain", CharSequence.class, true),
    destinationDnsDomainTLD(1, MefFieldType.data, "destination", "CEF destination top level dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationDnsDomainTLD", CharSequence.class, true),
    destinationHostName(1, MefFieldType.data, "destination", "CEF destination host FQDN",
            Arrays.<MefFieldConstrait>asList(new MefInetHostNameConstraint()), StringMarshall.class, "setDestinationHostName", CharSequence.class, true),
    destinationMacAddress(1, MefFieldType.data, "destination", "CEF destination host mac address",
            Arrays.<MefFieldConstrait>asList(new MefMacAddressConstraint()), StringMarshall.class, "setDestinationMacAddress", CharSequence.class, true),
    destinationNtDomain(1, MefFieldType.data, "destination", "CEF destination host Windows domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationNtDomain", CharSequence.class, true),
    destinationPort(1, MefFieldType.data, "destination", "CEF destination port [0..65535]",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationPort", Integer.class, true),
    destinationProcessId(1, MefFieldType.data, "destination", "CEF destination process ID",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationProcessId", Integer.class, true),
    destinationProcessName(1, MefFieldType.data, "destination", "CEF destination process name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationProcessName", CharSequence.class, true),
    destinationServiceName(1, MefFieldType.data, "destination", "CEF destination service name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationServiceName", CharSequence.class, true),
    destinationTranslatedAddress(1, MefFieldType.data, "destination", "CEF destination translated IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDestinationTranslatedAddress", CharSequence.class, true),
    destinationTranslatedPort(1, MefFieldType.data, "destination", "CEF destination translated port",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDestinationTranslatedPort", Integer.class, true),
    destinationUserId(1, MefFieldType.data, "destination", "CEF destination user id",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationUserId", CharSequence.class, true),
    destinationUserName(1, MefFieldType.data, "destination", "CEF destination user name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDestinationUserName", CharSequence.class, true),
    deviceAction(1, MefFieldType.data, "device", "CEF device action",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceAction", CharSequence.class, true),
    deviceAddress(1, MefFieldType.data, "device", "CEF device IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setDeviceAddress", CharSequence.class, true),
    deviceCustomString1(1, MefFieldType.data, "device", "CEF custom string 1",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString1", CharSequence.class, true),
    deviceCustomString1Label(1, MefFieldType.data, "device", "CEF custom string 1 label",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString1Label", CharSequence.class, true),
    deviceCustomString2(1, MefFieldType.data, "device", "CEF custom string 2",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString2", CharSequence.class, true),
    deviceCustomString2Label(1, MefFieldType.data, "device", "CEF custom string 2 label",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString2Label", CharSequence.class, true),
    deviceCustomString3(1, MefFieldType.data, "device", "CEF custom string 3",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString3", CharSequence.class, true),
    deviceCustomString3Label(1, MefFieldType.data, "device", "CEF custom string 3 label",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString3Label", CharSequence.class, true),
    deviceCustomString4(1, MefFieldType.data, "device", "CEF custom string 4",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString4", CharSequence.class, true),
    deviceCustomString4Label(1, MefFieldType.data, "device", "CEF custom string 4 label",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceCustomString4Label", CharSequence.class, true),
    deviceDirection(1, MefFieldType.data, "device", "CEF direction of event (inbound : 0, outbound : 1) seen by device",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setDeviceDirection", Integer.class, true),
    deviceDnsDomain(1, MefFieldType.data, "device", "CEF device dns domain",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceDnsDomain", CharSequence.class, true),
    deviceEventCategory(1, MefFieldType.data, "device", "CEF event category proposed by device",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDeviceEventCategory", CharSequence.class, true),
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
    eventOutcome(1, MefFieldType.data, null, "CEF outcome usually 'success' or 'failure'",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setEventOutcome", CharSequence.class, true),
    externalId(1, MefFieldType.data, null, "event Id used by an originated device",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalId", CharSequence.class, true),
    fileCreateTime(1, MefFieldType.data, "file", "CEF file create time",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setFileCreateTime", Long.class, true),
    fileId(1, MefFieldType.data, "file", "CEF file id (can be inode)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setFileId", CharSequence.class, true),
    fileModificationTime(1, MefFieldType.data, "file", "CEF file modification time",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setFileModificationTime", Long.class, true),
    fileHash(1, MefFieldType.data, "file", "CEF file hash",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setFileHash", CharSequence.class, true),
    fileName(1, MefFieldType.data, "file", "CEF file name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setFileName", CharSequence.class, true),
    filePath(1, MefFieldType.data, "file", "CEF file path",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setFilePath", CharSequence.class, true),
    filePermission(1, MefFieldType.data, "file", "CEF file permission",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setFilePermission", CharSequence.class, true),
    fileSize(1, MefFieldType.data, "file", "CEF file size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setFileSize", Integer.class, true),
    fileType(1, MefFieldType.data, "file", "CEF file type",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setFileType", CharSequence.class, true),
    message(1, MefFieldType.data, null, "CEF message giving more detail on event",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setMessage", CharSequence.class, true),
    oldFileCreateTime(1, MefFieldType.data, "oldfile", "CEF old file create time",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setOldFileCreateTime", Long.class, true),
    oldFileId(1, MefFieldType.data, "oldfile", "CEF old file Id (can be inode)",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOldFileId", CharSequence.class, true),
    oldFileModificationTime(1, MefFieldType.data, "oldfile", "CEF old file modification time",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setOldFileModificationTime", Long.class, true),
    oldFileHash(1, MefFieldType.data, "oldfile", "CEF old file hash",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOldFileHash", CharSequence.class, true),
    oldFileName(1, MefFieldType.data, "oldfile", "CEF old file name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOldFileName", CharSequence.class, true),
    oldFilePermission(1, MefFieldType.data, "oldfile", "CEF old file permission",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOldFilePermission", CharSequence.class, true),
    oldFileSize(1, MefFieldType.data, "oldfile", "CEF old file size",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setOldFileSize", Integer.class, true),
    oldFileType(1, MefFieldType.data, "oldfile", "CEF old file type",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setOldFileType", CharSequence.class, true),
    reason(1, MefFieldType.data, null, "CEF reason the audit event has been generated",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setReason", CharSequence.class, true),
    receiptTime(1, MefFieldType.data, null, "CEF time at which the event of activity was received",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setReceiptTime", Long.class, true),
    requestClientApplication(1, MefFieldType.data, "request", "CEF user agent associated with event",
            Arrays.<MefFieldConstrait>asList(new MefSanitizedStringConstraint()), StringMarshall.class, "setRequestClientApplication", CharSequence.class, true),
    requestCookies(1, MefFieldType.data, "request", "CEF cookies associated with request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestCookies", CharSequence.class, true),
    requestMethod(1, MefFieldType.data, "request", "CEF method associated with a request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestMethod", CharSequence.class, true),
    requestReferer(1, MefFieldType.data, "request", "Referer of the request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setReferer", CharSequence.class, true),
    requestURL(1, MefFieldType.data, "request", "CEF url associated with a request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestURL", CharSequence.class, true), 
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
    sourceProcessId(1, MefFieldType.data, "source", "CEF source process ID",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourceProcessId", Integer.class, true),
    sourceProcessName(1, MefFieldType.data, "source", "CEF source process name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceProcessName", CharSequence.class, true),
    sourceServiceName(1, MefFieldType.data, "source", "CEf source service name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceServiceName", CharSequence.class, true),
    sourceTranslatedAddress(1, MefFieldType.data, "source", "CEF source IPv4 translated address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setSourceTranslatedAddress", CharSequence.class, true),
    sourceTranslatedPort(1, MefFieldType.data, "source", "CEF source translated port",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setSourceTranslatedPort", Integer.class, true),
    sourceUserId(1, MefFieldType.data, "source", "CEF source user Id",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceUserId", CharSequence.class, true),
    sourceUserName(1, MefFieldType.data, "source", "CEF source user name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSourceUserName", CharSequence.class, true),
    startTime(1, MefFieldType.data, null, "CEF start time of the event",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setStartTime", Long.class, true),
    transportProtocol(1, MefFieldType.data, null, "CEF layer 4 protocol used",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setTransportProtocol", CharSequence.class, true),
    //WebProxy Specific (added to keep details instead aggregated requestUrl
    requestScheme(1, MefFieldType.data, "request", "shema used into the request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestSheme", CharSequence.class, true),
    requestPath(1, MefFieldType.data, "request", "path to the resource into the request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestPath", CharSequence.class, true),
    requestQuery(1, MefFieldType.data, "request", "arguments of the request",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestQuery", CharSequence.class, true),
    devicePolicyAction(1, MefFieldType.data, "device", "action taken by the device due to a policy",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestQuery", CharSequence.class, true),

    // end of CEF fields ---------------------------------------------------------
    // NameOrIpWrapper
    destinationNameOrIp(1, MefFieldType.data, "destination", "CEF destination host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDestinationNameOrIp", CharSequence.class, true),
    sourceNameOrIp(1, MefFieldType.data, "source", "CEF source host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setSourceNameOrIp", CharSequence.class, true),
    deviceNameOrIp(1, MefFieldType.data, "device", "CEF device host and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class, "setDeviceNameOrIp", CharSequence.class, true),
    // IPAndInterfaceAndName - sonicOs
    destinationIPAndInterfaceAndName(1, MefFieldType.data, "destination", "CEF destination host, interface and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetIPAndPortAndInterfaceAndNameConstraint()), StringMarshall.class, "setDestinationIPAndInterfaceAndName", CharSequence.class, true),
    sourceIPAndInterfaceAndName(1, MefFieldType.data, "source", "CEF source host, interface and Ip  wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetIPAndPortAndInterfaceAndNameConstraint()), StringMarshall.class, "setSourceIPAndInterfaceAndName", CharSequence.class, true),
    // ProtoAndPort - sonicOs
    destinationProtoAndPort(1, MefFieldType.data, "destination", "CEF destination port and protocol wrapper.",
            Arrays.<MefFieldConstrait>asList(new MefInetProtoAndPortConstraint()), StringMarshall.class, "setDestinationProtoAndPort", CharSequence.class, true),
    // LogCollection fields
    logCollectionCategory(1, MefFieldType.metaData, "LogCollection", "Log Collection channel category.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionCategory", CharSequence.class, true),
    logCollectionContainer(1, MefFieldType.metaData, "LogCollection", "Log Collection container",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionContainer", CharSequence.class, true),
    logCollectionHost(1, MefFieldType.metaData, "LogCollection", "Log Collection host",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionHost", CharSequence.class, false),
    logCollectionType(1, MefFieldType.metaData, "LogCollection", "Log Collection channel Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogCollectionType", CharSequence.class, false),
    externalLogSourceType(1, MefFieldType.metaData, "LogCollection", "External log source Type, before conversion.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setExternalLogSourceType", CharSequence.class, true),
    logCollectionId(1, MefFieldType.metaData, "LogCollection", "Log Collection record id",
            Arrays.<MefFieldConstrait>asList(new MefIntegerConstraint()), IntMarshall.class, "setLogCollectionId", Integer.class, false),
    logCollectionTime(1, MefFieldType.metaData, "LogCollection", "Log Collection timestamp",
            Arrays.<MefFieldConstrait>asList(new MefMilliSecTimeStampConstraint()), LongMarshall.class, "setLogCollectionTime", Long.class, true),
    //syslogMessage(1, MefFieldType.metaData,"LogCollection", "Syslog message",
//Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class,"setSyslogMessage", CharSequence.class, true),
// logsource fields
    logSourceType(1, MefFieldType.metaData, "LogCollection", "Log source Type.",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setLogSourceType", CharSequence.class, true),

    //dpi-http related fields
    dpiFlowId(1, MefFieldType.data, null, "DPI http field dpiFlowId",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDpiFlowId", CharSequence.class, true),
    dpiSignatureId(1, MefFieldType.data, null, "DPI http field dpiSignatureId",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setDpiSignatureId", CharSequence.class, true),
    responseContentType(1, MefFieldType.data, "response", "DPI http field responseContentType",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setResponseContentType", CharSequence.class, true),
    requestParams(-1, MefFieldType.data, "request", "DPI http field requestParams",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestParam", CharSequence.class, true),
    requestVersion(-1, MefFieldType.data, "request", "Protocol and version from the client's request, e.g. HTTP/1.1",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setRequestVersion", CharSequence.class, true),  
    
    // Security Gateway fields
    sgThreatID(1, MefFieldType.data, null, "SG Threat ID Field",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSgThreatID", CharSequence.class, true),    
    sgSeverity(1, MefFieldType.data, null, "SG Severity Field",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSgSeverity", CharSequence.class, true),
    sgRuleName(1, MefFieldType.data, null, "SG Rule Name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSgRuleName", CharSequence.class, true),
    sgApplication(1, MefFieldType.data, null, "SG Application Name",
            Arrays.<MefFieldConstrait>asList(new MefStringConstraint()), StringMarshall.class, "setSgApplication", CharSequence.class, true),
    sgSourceAddress(1, MefFieldType.data, "source", "SG source IPv4 address",
            Arrays.<MefFieldConstrait>asList(new MefInetAddressConstraint()), StringMarshall.class, "setSgSourceAddress", CharSequence.class, true),
    
// others
//unixEpochTimestampMillis(1, MefFieldType.data, null, "a generic unix ecoch timestamp in millis.",
//Arrays.<MefFieldConstrait>asList(new MefIsEpochTimestampConstraint()), LongMarshall.class,"setUnixEpochTimestampMillis", Long.class, true),
//userIpAddress(1, MefFieldType.data, null, "The ipaddress for a given user.",
//Arrays.<MefFieldConstrait>asList(new MefInetNameOrIpConstraint()), StringMarshall.class,"setUserIpAddress", CharSequence.class, true),
//_sourceUserName(1, MefFieldType.data, null, "a test field with length enforcing.",
//Arrays.<MefFieldConstrait>asList(new MefIsUserNameConstraint()), StringMarshall.class,"set_sourceUserName", CharSequence.class, true),

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

    private WebProxyMefField(int numberOfValues,
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
        for (WebProxyMefField field : values()) {
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
            format = WebProxyMefField.valueOf(prettyName);
        } catch (IllegalArgumentException ex) {
            // silent
        }
        return format;
    }
}
