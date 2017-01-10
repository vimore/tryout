package com.securityx.modelfeature.common.inputs;

import javax.validation.constraints.NotNull;

/**
 * Created by harish on 1/30/15.
 */
public class PeerGroupSearchInput extends SolrInputBase{

    @NotNull
    int modelId;
    @NotNull
    int securityEventId;
    @NotNull
    String[] selectedEntities;
    //@NotNull
    String[] keywords;

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }


    public String[] getSelectedEntities() {
        return selectedEntities;
    }

    public void setSelectedEntities(String[] selectedEntities) {
        this.selectedEntities = selectedEntities;
    }

    public int getSecurityEventId() {
        return securityEventId;
    }

    public void setSecurityEventId(int securityEventId) {
        this.securityEventId = securityEventId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }
}
