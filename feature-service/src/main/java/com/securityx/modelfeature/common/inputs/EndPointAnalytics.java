package com.securityx.modelfeature.common.inputs;

import com.google.common.collect.Lists;
import com.securityx.modelfeature.common.Entity;
import com.securityx.modelfeature.common.EntityInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by harish on 12/4/15.
 */
public class EndPointAnalytics {

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;
    private int modelId = 5;

    private boolean nox = true;
    private String typeField;
    private String typeValue;
    private String lastSeenTypeValue;
    private int numRows = 10;
    private List<Entity> entities = Lists.newLinkedList();


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

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public boolean isNox() {
        return nox;
    }

    public void setNox(boolean nox) {
        this.nox = nox;
    }

    public String getTypeField() {
        return typeField;
    }

    public void setTypeField(String typeField) {
        this.typeField = typeField;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public String getLastSeenTypeValue() {
        return lastSeenTypeValue;
    }

    public void setLastSeenTypeValue(String lastSeenTypeValue) {
        this.lastSeenTypeValue = lastSeenTypeValue;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }
}
