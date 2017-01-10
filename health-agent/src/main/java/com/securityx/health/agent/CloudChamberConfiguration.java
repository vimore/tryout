package com.securityx.health.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.dropwizard.Configuration;
import io.dropwizard.validation.ValidationMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;


public class CloudChamberConfiguration extends Configuration {

    public static final Logger log = LoggerFactory.getLogger(CloudChamberConfiguration.class);

    private Pattern hostPat = Pattern.compile("^[-A-Za-z0-9.]+$");
    private Pattern userIdPat = Pattern.compile("^\\S+$");

    @JsonProperty
    private String host;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String emailId;

    @JsonProperty
    private String X_HMAC_NONCE;

    @JsonProperty
    private String AUTHORIZATION;

    @JsonProperty
    private String GEO_X_HMAC_NONCE;

    @JsonProperty
    private String GEO_AUTHORIZATION;

    @JsonProperty
    private String WHOIS_X_HMAC_NONCE;

    @JsonProperty
    private String WHOIS_AUTHORIZATION;

    @JsonProperty
    private String PROXY_HOST;

    @JsonProperty
    private int PROXY_PORT;

    @JsonProperty
    private String PROXY_USER;

    @JsonProperty
    private String PROXY_PASSWORD;

    @JsonProperty
    private String PROXY_SCHEME;

    @ValidationMethod(message = "is not valid")
    public boolean isValid() {
        if (!hostPat.matcher(host).matches()) {
            return false;
        }
        if (!userIdPat.matcher(userId).matches()) {
            return false;
        }
        if (!userIdPat.matcher(emailId).matches()) {
            return false;
        }
        return true;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getX_HMAC_NONCE() {
        return X_HMAC_NONCE;
    }

    public void setX_HMAC_NONCE(String x_HMAC_NONCE) {
        X_HMAC_NONCE = x_HMAC_NONCE;
    }

    public String getAUTHORIZATION() {
        return AUTHORIZATION;
    }

    public void setAUTHORIZATION(String AUTHORIZATION) {
        this.AUTHORIZATION = AUTHORIZATION;
    }

    public String getGEO_X_HMAC_NONCE() {
        return GEO_X_HMAC_NONCE;
    }

    public void setGEO_X_HMAC_NONCE(String GEO_X_HMAC_NONCE) {
        this.GEO_X_HMAC_NONCE = GEO_X_HMAC_NONCE;
    }

    public String getGEO_AUTHORIZATION() {
        return GEO_AUTHORIZATION;
    }

    public void setGEO_AUTHORIZATION(String GEO_AUTHORIZATION) {
        this.GEO_AUTHORIZATION = GEO_AUTHORIZATION;
    }

    public String getWHOIS_X_HMAC_NONCE() {
        return WHOIS_X_HMAC_NONCE;
    }

    public void setWHOIS_X_HMAC_NONCE(String WHOIS_X_HMAC_NONCE) {
        this.WHOIS_X_HMAC_NONCE = WHOIS_X_HMAC_NONCE;
    }

    public String getWHOIS_AUTHORIZATION() {
        return WHOIS_AUTHORIZATION;
    }

    public void setWHOIS_AUTHORIZATION(String WHOIS_AUTHORIZATION) {
        this.WHOIS_AUTHORIZATION = WHOIS_AUTHORIZATION;
    }

    public String getPROXY_HOST() {
        return PROXY_HOST;
    }

    public void setPROXY_HOST(String _PROXY_HOST) {
        this.PROXY_HOST = _PROXY_HOST;
    }

    public int getPROXY_PORT() {
        return this.PROXY_PORT;
    }

    public void setPROXY_PORT(int _PROXY_PORT) {
        this.PROXY_PORT = _PROXY_PORT;
    }

    public String getPROXY_USER() {
        return PROXY_USER;
    }

    public void setPROXY_USER(String _PROXY_USER) {
        this.PROXY_USER = _PROXY_USER;
    }

    public String getPROXY_PASSWORD() {
        return PROXY_PASSWORD;
    }

    public void setPROXY_PASSWORD(String _PROXY_PASSWORD) {
        this.PROXY_PASSWORD = _PROXY_PASSWORD;
    }

    public String getPROXY_SCHEME() {
        return PROXY_SCHEME;
    }

    public void setPROXY_SCHEME(String _PROXY_SCHEME) {
        this.PROXY_SCHEME = _PROXY_SCHEME;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hostPat", hostPat)
                .add("userIdPat", userIdPat)
                .add("host", host)
                .add("userId", userId)
                .add("emailId", emailId)
                .add("X_HMAC_NONCE", X_HMAC_NONCE)
                .add("AUTHORIZATION", AUTHORIZATION)
                .add("GEO_X_HMAC_NONCE", GEO_X_HMAC_NONCE)
                .add("GEO_AUTHORIZATION", GEO_AUTHORIZATION)
                .add("WHOIS_X_HMAC_NONCE", WHOIS_X_HMAC_NONCE)
                .add("WHOIS_AUTHORIZATION", WHOIS_AUTHORIZATION)
                .toString();
    }
}

