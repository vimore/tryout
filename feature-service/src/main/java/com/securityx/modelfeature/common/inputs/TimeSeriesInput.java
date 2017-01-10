package com.securityx.modelfeature.common.inputs;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Created by harish on 12/30/14.
 */
public class TimeSeriesInput extends SolrInputBase{

    //By default, ad time series
    private int modelId = 3;

    private String typeField;
    private String group;
    private String groupId;

    //used for Searching logs. 
    public List<Map<String, String>> facets = Lists.newLinkedList();


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTypeField() {
        return typeField;
    }

    public void setTypeField(String typeField) {
        this.typeField = typeField;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public List<Map<String, String>> getFacets() {
        return facets;
    }

    public void setFacets(List<Map<String, String>> facets) {
        this.facets = facets;
    }
}
