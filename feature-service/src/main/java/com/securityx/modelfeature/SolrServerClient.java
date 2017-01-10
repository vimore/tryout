package com.securityx.modelfeature;

import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrServerClient {

    private static Logger logger = LoggerFactory.getLogger(SolrServerClient.class);
    private String zkHost = null;

    public SolrServerClient(FeatureServiceConfiguration conf) {
        zkHost = conf.getSolrQuorum();
    }

    public CloudSolrServer getSolrServer(String collection) {

        CloudSolrServer solr = null;
        try {
            solr = new CloudSolrServer(zkHost);
            //Specify collection for CloudSolrServer
            solr.setDefaultCollection(collection);
        } catch (Exception exp) {
            logger.debug("Failed to create solr server => {}", exp);
        }
        return  solr;
    }
}
