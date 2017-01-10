package com.securityx.model.external.mcafeewebsec;

import com.securityx.model.external.ExternalFieldsToWebProxyMefFields;
import com.securityx.model.mef.field.api.GenericFormat;
import com.securityx.model.mef.field.api.WebProxyMefField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * defines CEF aliases and the related MEfField
 *
 * @author jyrialhon
 */
public enum McAfeeWebSecToMefMappings implements ExternalFieldsToWebProxyMefFields {
    application_name("application_name", null),
    auth_user("auth_user", WebProxyMefField.sourceUserName),
    block_res("block_res", WebProxyMefField.devicePolicyAction),
    bytes_from_client("bytes_from_client", WebProxyMefField.bytesOut),
    bytes_to_client("bytes_to_client", WebProxyMefField.bytesIn),
    cache_status("cache_status", WebProxyMefField.deviceAction),
    categories("categories", null),
    gam_probability("gam_probability", null),
    geolocation("geolocation", null),
    md5("md5", null),
    media_type("media_type", WebProxyMefField.responseContentType),
    referrer("referrer", WebProxyMefField.requestReferer),
    rep_level("rep_level", null),
    req_line("req_line", null),
    server_ip("server_ip", WebProxyMefField.destinationAddress),
    src_ip("src_ip", WebProxyMefField.sourceNameOrIp),
    status_code("status_code", WebProxyMefField.cefSignatureId),
    time_stamp("time_stamp", WebProxyMefField.startTime),
    time_taken("time_taken", null),
    user_agent("user_agent", WebProxyMefField.requestClientApplication),
    virus_name("virus_name", WebProxyMefField.reason),
    url("url", null),
    usrName("usrName", WebProxyMefField.sourceUserName),
    realm("realm", null),
    src("src", WebProxyMefField.sourceAddress),
    srcPort("srcPort", WebProxyMefField.sourcePort),
    dst("dst", WebProxyMefField.destinationAddress),
    dstPort("dstPort", WebProxyMefField.destinationPort),
    blockReason("blockReason", null),
    srcPreNAT("srcPreNAT", null),
    srcPreNATPort("srcPreNATPort", null),
    dstPreNAT("dstPreNAT", null),
    dstPreNATPort("dstPreNATPort", null),
    dstPostNAT("dstPostNAT", null),
    srcPostNAT("srcPostNAT", null),
    srcPostNATPort("srcPostNATPort", null),
    dstPostNATPort("dstPostNATPort", null),
    srcBytes("srcBytes", WebProxyMefField.bytesIn),
    dstBytes("dstBytes", WebProxyMefField.bytesOut),
    totalBytes("totalBytes", null),
    srcBytesPostNAT("srcBytesPostNAT", null),
    dstBytesPostNAT("dstBytesPostNAT", null),
    totalBytesPostNAT("totalBytesPostNAT", null),
    httpStatus("httpStatus", WebProxyMefField.cefSignatureId),
    cacheStatus("cacheStatus", null),
    timeTaken("timeTaken", null),
    contentType("contentType", WebProxyMefField.responseContentType),
    ensuredType("ensuredType", null),
    urlCategories("urlCategories", WebProxyMefField.deviceEventCategory),
    reputation("reputation", null),
    policy("policy", WebProxyMefField.devicePolicyAction),
    proto("proto", WebProxyMefField.applicationProtocol),
    method("method", WebProxyMefField.requestMethod),
    referer("referer", WebProxyMefField.requestReferer),
    userAgent("userAgent", WebProxyMefField.requestClientApplication),
    calCountryOrRegion("calCountryOrRegion", null),
    virusName("virusName", WebProxyMefField.reason),
    application("application", null),
    location("location", null);


    private static final Map<String, WebProxyMefField> mappings = new HashMap<String, WebProxyMefField>();
    private static final String NAMESPACE = "mcafeeWebSec";
    private static final Map<String, GenericFormat> genericFormatMappings = new HashMap<String, GenericFormat>();
    private final WebProxyMefField mefField;
    private final String fieldName;

    private McAfeeWebSecToMefMappings(String fieldName, WebProxyMefField mefField) {
        this.mefField = mefField;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    private void initialize() {

        for (McAfeeWebSecToMefMappings field : values()) {
            mappings.put(field.getFieldName(), field.mefField);
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
    public WebProxyMefField getMefField(String externalFieldName) {
        return mappings.get(externalFieldName);
    }


    @Override
    public String getPrettyName() {
        return this.fieldName;
    }

    @Override
    public GenericFormat getByPrettyName(String prettyName) {
        if (genericFormatMappings.isEmpty()) {
            initializeSupportedFormatMapping();
        }
        return this.genericFormatMappings.get(prettyName);
    }


    private void initializeSupportedFormatMapping() {
        for (McAfeeWebSecToMefMappings field : values()) {
            genericFormatMappings.put(field.getPrettyName(), field);
        }
    }


    }
