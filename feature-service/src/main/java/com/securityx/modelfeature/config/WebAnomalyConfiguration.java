package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by harish on 1/22/15.
 */
public class WebAnomalyConfiguration {

    @Valid
    @NotNull
    @JsonProperty
    List<WebAnomalyProfileNameConfiguration> webAnomalyProfiles = new LinkedList<WebAnomalyProfileNameConfiguration>();

    public List<WebAnomalyProfileNameConfiguration> getWebAnomalyProfiles() {
        return webAnomalyProfiles;
    }

    public void setWebAnomalyProfiles(List<WebAnomalyProfileNameConfiguration> webAnomalyProfiles) {
        this.webAnomalyProfiles = webAnomalyProfiles;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("webAnomalyProfiles", webAnomalyProfiles)
                .toString();
    }
}