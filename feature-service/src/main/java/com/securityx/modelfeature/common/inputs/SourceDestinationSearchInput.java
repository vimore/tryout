package com.securityx.modelfeature.common.inputs;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * Solr input class for searching for list of sourceNameOrIps AND destinationNameOrIPs
 * Created by harish on 2/4/15.
 */
public class SourceDestinationSearchInput extends SolrInputBase {

    private List<String> source = Lists.newLinkedList();
    private List<String> destination = Lists.newLinkedList();

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<String> getDestination() {
        return destination;
    }

    public void setDestination(List<String> destination) {
        this.destination = destination;
    }
}
