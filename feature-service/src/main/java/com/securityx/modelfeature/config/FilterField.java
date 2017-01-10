package com.securityx.modelfeature.config;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;

public class FilterField extends NameIdPair {

    private String fieldName;

    private String type;

    private List<Integer> operatorIds = Lists.newLinkedList();

    private boolean defaultDisplay;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getOperatorIds() {
        return operatorIds;
    }

    public void setOperatorIds(List<Integer> operatorIds) {
        this.operatorIds = operatorIds;
    }

    public boolean isDefaultDisplay() {
        return defaultDisplay;
    }

    public void setDefaultDisplay(boolean defaultDisplay) {
        this.defaultDisplay = defaultDisplay;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("fieldName", fieldName)
                .add("type", type)
                .add("operatorIds", operatorIds)
                .add("defaultDisplay", defaultDisplay)
                .toString();
    }
}


