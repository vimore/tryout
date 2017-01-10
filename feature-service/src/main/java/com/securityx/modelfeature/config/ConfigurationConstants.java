package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by harish on 1/23/15.
 */
public class ConfigurationConstants {

    @JsonProperty
    public String timeSeriesFilePath;

    @JsonProperty
    public String webAnomalyProfileNamesFilePath;

    @JsonProperty
    public String securityEventTypesFilePath;

    @JsonProperty
    public String searchConfFilePath;

    @JsonProperty
    public String alertConfFilePath;

    public String getTimeSeriesFilePath() {
        return timeSeriesFilePath;
    }

    public void setTimeSeriesFilePath(String timeSeriesFilePath) {
        this.timeSeriesFilePath = timeSeriesFilePath;
    }

    public String getWebAnomalyProfileNamesFilePath() {
        return webAnomalyProfileNamesFilePath;
    }

    public void setWebAnomalyProfileNamesFilePath(String webAnomalyProfileNamesFilePath) {
        this.webAnomalyProfileNamesFilePath = webAnomalyProfileNamesFilePath;
    }

    public String getSecurityEventTypesFilePath() {
        return securityEventTypesFilePath;
    }

    public void setSecurityEventTypesFilePath(String securityEventTypesFilePath) {
        this.securityEventTypesFilePath = securityEventTypesFilePath;
    }

    public String getSearchConfFilePath() {
        return searchConfFilePath;
    }

    public void setSearchConfFilePath(String searchConfFilePath) {
        this.searchConfFilePath = searchConfFilePath;
    }

    public String getAlertConfFilePath() {
        return alertConfFilePath;
    }

    public void setAlertConfFilePath(String alertConfFilePath) {
        this.alertConfFilePath = alertConfFilePath;
    }
}
