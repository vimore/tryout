package com.securityx.modelfeature.common.inputs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.securityx.modelfeature.common.EntityBehavior;
import com.securityx.modelfeature.common.ThresholdDefinition;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertDefinition {

    private String alertId;
    private List<String> alertDestination = Lists.newArrayList();
    private String frequency;
    private List<ThresholdDefinition> threshold;
    private List<EntityBehavior> filter;


    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public List<String> getAlertDestination() {
        return alertDestination;
    }

    public void setAlertDestination(List<String> alertDestination) {
        this.alertDestination = alertDestination;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<ThresholdDefinition> getThreshold() {
        return threshold;
    }

    public void setThreshold(List<ThresholdDefinition> threshold) {
        this.threshold = threshold;
    }

    public List<EntityBehavior> getFilter() {
        return filter;
    }

    public void setFilter(List<EntityBehavior> filter) {
        this.filter = filter;
    }

}
