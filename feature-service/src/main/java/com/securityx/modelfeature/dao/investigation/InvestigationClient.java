package com.securityx.modelfeature.dao.investigation;

import java.util.Map;

public interface InvestigationClient {
    // Field Names
    public static final String SOURCE = "source";
    public static final String MD5 = "md5";
    public static final String RESPONSE_SUMMARY = "responseSummary";
    public static final String DATE = "date";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String MESSAGE = "message";
    public static final String EXTERNAL_LINK = "externalLink";
    public static final String POSITIVES = "positives";
    public static final String TOTAL = "total";
    public static final String CONFIDENCE_LEVEL = "confidenceLevel";

    // Strings that may be returned in the responseSummary field
    public static final String NO_ISSUES = "no known issues";
    public static final String MALWARE_INDICATED = "malware indicated";

    // Strings used for returning client errors which should be returned to the UI
    public static final String CLIENT_ERROR = "ClientError";
    public static final String CLIENT_ERROR_CODE = "ClientErrorCode";


    public Map<String, String> getHashInfo(String hash);

}
