package com.securityx.modelfeature.common.inputs;

/**
 *
 * Created by harish on 1/27/15.
 */
public class WebProfileAnomalyInput extends SolrInputBase {

    private String sourceAddress;

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

}
