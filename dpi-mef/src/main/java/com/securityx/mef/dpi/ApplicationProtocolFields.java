package com.securityx.mef.dpi;

import java.util.EnumSet;

public class ApplicationProtocolFields {
    public enum HttpField {
        METHOD("http/request/method"),
        REQUEST_TS("http/request/request_ts"),
        URI_PATH("http/request/uri_path"),
        URI_PARAM_NAME("http/request/uri_param/uri_param_name"),
        URI_PARAM_VALUE("http/request/uri_param/uri_param_value"),
        USER_AGENT("http/request/user_agent"),
        HOST("http/request/host"),
        CONNECTION("http/request/Connection"),
        CACHE_CONTROL("http/request/Cache-Control"),
        REQUEST_SIZE("http/request/request_size"),
        VERSION("http/request/version"),
        RESPONSE_TS("http/request/response_ts"),
        CODE("http/request/code"),
        SERVER("http/request/Server"),
        SERVER_AGENT("http/request/Server_agent"),
        DATE("http/request/Date"),
        CONTENT_LENGTH("http/request/Content-Length"),
        MIME_TYPE("http/request/mime_type"),
        CONTENT_TYPE("http/request/Content-Type"),
        COOKIE("http/request/cookie"),
        TRANSFER_ENCODING("http/request/Transfer-Encoding")
        ;

        private String fieldName ;
        private HttpField(String fieldName) {
            this.fieldName = fieldName ;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public static boolean contains(String fieldName)  {
            for (HttpField h :EnumSet.allOf(HttpField.class)) {
                if (h.fieldName.equalsIgnoreCase(fieldName)) {
                    return true;
                }
            }
            return false;
        }

        public static HttpField toEnum(String fieldName)  {
            for (HttpField h :EnumSet.allOf(HttpField.class)) {
                if (h.fieldName.equalsIgnoreCase(fieldName)) {
                    return h;
                }
            }
            return null;
        }
    }

    public  enum DnsField {
        REPLY_CODE("dns/dns_query/reply_code"),
        QUERY("dns/dns_query/query"),
        QUERY_TYPE("dns/dns_query/query_type"),
        QUERY_ENTRY_NAME("dns/dns_query/dns_entry/name"),
        QUERY_ENTRY_TTL("dns/dns_query/dns_entry/ttl"),
        QUERY_HOST_ADDR("dns/dns_query/dns_entry/host_addr"),
        QUERY_HOST_TYPE("dns/dns_query/dns_entry/host_type"),
        MESSAGE_TYPE("dns/dns_query/message_type"),
        CLASSIFICATION_MATCH("dns/classification_match"),
        ;

        private String fieldName ;

        private DnsField(String fieldName) {
            this.fieldName = fieldName ;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public static boolean contains(String fieldName)  {
            for (DnsField h :EnumSet.allOf(DnsField.class)) {
                if (h.fieldName.equalsIgnoreCase(fieldName)) {
                    return true;
                }
            }
            return false;
        }

        public static DnsField toEnum(String fieldName)  {
            for (DnsField h :EnumSet.allOf(DnsField.class)) {
                if (h.fieldName.equalsIgnoreCase(fieldName)) {
                    return h;
                }
            }
            return null;
        }
    }


    public  enum SslField {
        VERSION("ssl/version");

        private String fieldName ;

        private SslField(String fieldName) {
            this.fieldName = fieldName ;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public static boolean contains(String fieldName)  {
            for (SslField h :EnumSet.allOf(SslField.class)) {
                if (h.fieldName.equalsIgnoreCase(fieldName)) {
                    return true;
                }
            }
            return false;
        }

        public static SslField toEnum(String fieldName)  {
            for (SslField h :EnumSet.allOf(SslField.class)) {
                if (h.fieldName.equalsIgnoreCase(fieldName)) {
                    return h;
                }
            }
            return null;
        }

    }
}
