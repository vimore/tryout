package com.securityx.modelfeature.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.securityx.modelfeature.common.inputs.SecurityEventBehavior;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityBehavior {

    private List<String> sourceIp;

    private List<SecurityEventBehavior> behaviors;

    public List<String> getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(List<String> sourceIp) {
        this.sourceIp = sourceIp;
    }

    public List<SecurityEventBehavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<SecurityEventBehavior> behaviors) {
        this.behaviors = behaviors;
    }

}
