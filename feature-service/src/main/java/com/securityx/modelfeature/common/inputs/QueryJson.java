package com.securityx.modelfeature.common.inputs;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class QueryJson {
    private String startTime;
    private String endTime;
    private String sortField;
    private String sortOrder;
    private int limit;
    private List<Map<String, Object>> query = Lists.newLinkedList();
    private List<SecurityEventBehavior> behaviors = Lists.newLinkedList();
    private List<Map<String, String>> ignoredEntities = Lists.newLinkedList();

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<Map<String, Object>> getQuery() {
        return query;
    }

    public void setQuery(List<Map<String, Object>> query) {
        this.query = query;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<SecurityEventBehavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<SecurityEventBehavior> behaviors) {
        this.behaviors = behaviors;
    }

    public List<Map<String, String>> getIgnoredEntities() {
        return ignoredEntities;
    }

    public void setIngnoredEntities(List<Map<String, String>> ingnoredEntities) {
        this.ignoredEntities = ingnoredEntities;
    }
}