package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Created by harish on 1/23/15.
 */
public class WebAnomalyProfileNameConfiguration {

    @JsonProperty
    private  String type;

    @JsonProperty
    private String anomalyProfile;

    @JsonProperty
    private String anomalyProfileName;

    @JsonProperty
    private String anomalyProfileCategory;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getAnomalyProfile() {
        return anomalyProfile;
    }

    public void setAnomalyProfile(final String anomalyProfile) {
        this.anomalyProfile = anomalyProfile;
    }

    public String getAnomalyProfileName() {
        return anomalyProfileName;
    }

    public void setAnomalyProfileName(final String anomalyProfileName) {
        this.anomalyProfileName = anomalyProfileName;
    }

    public String getAnomalyProfileCategory() {
        return anomalyProfileCategory;
    }

    public void setAnomalyProfileCategory(final String anomalyProfileCategory) {
        this.anomalyProfileCategory = anomalyProfileCategory;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("type", type)
                .add("anomalyProfile", anomalyProfile)
                .add("anomalyProfileName", anomalyProfileName)
                .add("anomalyProfileCategory", anomalyProfileCategory)
                .toString();
    }
}
