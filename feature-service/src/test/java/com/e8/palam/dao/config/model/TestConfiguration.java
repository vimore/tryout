package com.e8.palam.dao.config.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.config.EntityFusionConfiguration;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Created by harish on 1/23/15.
 */
public class TestConfiguration {

    @JsonProperty
    private Map<String, String> testConfigurationConstants = Maps.newHashMap();

    @NotEmpty
    private String zkQuorum;

    @NotEmpty
    private String solrQuorum;

    @NotEmpty
    private String environment;

    @Valid
    @NotNull
    @JsonProperty
    private EntityFusionConfiguration entityFusion = new EntityFusionConfiguration();


    @Valid
    @NotNull
    @JsonProperty
    private FeatureServiceConfiguration.RiskRangeConfiguration riskRanges = new FeatureServiceConfiguration.RiskRangeConfiguration();

    public FeatureServiceConfiguration.RiskRangeConfiguration getRiskRanges() {
        return riskRanges;
    }

    public void setRiskRanges(FeatureServiceConfiguration.RiskRangeConfiguration riskRanges) {
        this.riskRanges = riskRanges;
    }

    public Map<String, String> getTestConfigurationConstants() {
        return testConfigurationConstants;
    }

    public void setTestConfigurationConstants(Map<String, String> testConfigurationConstants) {
        this.testConfigurationConstants = testConfigurationConstants;
    }

    public String getTimeSeriesFilePath() {
        return testConfigurationConstants.get("timeSeriesFilePath");
    }


    public String getWebAnomalyProfileNamesFilePath() {
        return testConfigurationConstants.get("webAnomalyProfileNamesFilePath");
    }


    public String getSecurityEventTypesFilePath() {
        return testConfigurationConstants.get("securityEventTypesFilePath");
    }

    public String getAlertConfFilePath() {
        return testConfigurationConstants.get("alertConfFilePath");
    }


    public String getSearchConfFilePath() {
        return testConfigurationConstants.get("searchConfFilePath");
    }

    public String getZkQuorum() {
        return zkQuorum;
    }

    public void setZkQuorum(String zkQuorum) {
        this.zkQuorum = zkQuorum;
    }

    public String getSolrQuorum() {
        return solrQuorum;
    }

    public void setSolrQuorum(String solrQuorum) {
        this.solrQuorum = solrQuorum;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public EntityFusionConfiguration getEntityFusion() {
        return entityFusion;
    }

    public void setEntityFusion(EntityFusionConfiguration entityFusion) {
        this.entityFusion = entityFusion;
    }
}
