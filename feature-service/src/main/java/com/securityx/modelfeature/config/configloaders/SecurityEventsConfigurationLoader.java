package com.securityx.modelfeature.config.configloaders;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by harish on 1/23/15.
 */
public class SecurityEventsConfigurationLoader {

    private final Logger LOGGER = LoggerFactory.getLogger(SecurityEventsConfigurationLoader.class);

    private Map<Integer, String> modelsMap = Maps.newHashMap();
    private Map<Integer, String> modelsEntityTypeMap = Maps.newHashMap();

    private Map<Integer, String> killChainMap = Maps.newHashMap();

    private List<SecurityEventTypeConfiguration> securityEventTypeList;

    private Map<Integer, String> operatorsMap = Maps.newHashMap();
    private Map<String, Integer> operatorNameToIdMap = Maps.newHashMap();

    private Map<Integer, FilterField> filterFieldMap = Maps.newHashMap();

    private Map<String, FilterField> filterFieldNamesMap = Maps.newHashMap();

    private List<FilterField> filterFields = null;

    private Map<String, C2ModelConfiguration> c2ModelConfigurationFeatureMap = Maps.newHashMap();
    private List<C2ModelConfiguration> c2ModelConfigurations = null;

    public void loadSecurityEventInfo(final FeatureServiceConfiguration conf) {
        try {
            LOGGER.debug("Loading Security Events config file: " + conf.getConfigurationConstants().getSecurityEventTypesFilePath());
            String securityEventConfigFilePath = conf.getConfigurationConstants().getSecurityEventTypesFilePath();
            InputStream inputStream = new FileInputStream(new File(securityEventConfigFilePath));
            Yaml ymlLoader = new Yaml();
            EventTypeConfiguration configuration = ymlLoader.loadAs(inputStream, EventTypeConfiguration.class);
            populateModelsMap(configuration.getModels());
            populateKillChainMap(configuration.getKillchain());
            populateSecurityEventMap(configuration.getSecurityEventTypes());
            populateOperatorsMap(configuration.getQueryOperators());
            populateFilterFieldMap(configuration.getFilterFields());
            populatec2ModelConfigurationMap(configuration.getC2ModelConfig());

        } catch (Exception e) {
            LOGGER.error("Error occurred while loading securityEvents.yml file => " + e);
            throw new RuntimeException("Error loading Security Event Configuration => ", e);
        }
    }

    private void populatec2ModelConfigurationMap(final List<C2ModelConfiguration> c2ModelConfigurations) {
        this.c2ModelConfigurations = c2ModelConfigurations;

        for (C2ModelConfiguration c2ModelConfig: c2ModelConfigurations){
            int id = c2ModelConfig.getId();
            String featureKey = c2ModelConfig.getFeatureKey();
            String featureDescription = c2ModelConfig.getFeatureDescription();
            c2ModelConfigurationFeatureMap.put(featureKey, c2ModelConfig);
        }
    }

    private void populateFilterFieldMap(final List<FilterField> fields) {
        this.filterFields = fields;

        for (FilterField ff: filterFields){
            int id = ff.getId();
            String fieldName = ff.getName();
            filterFieldMap.put(id, ff);
            filterFieldNamesMap.put(fieldName, ff);
        }
    }


    private void populateOperatorsMap(final List<NameIdPair> queryOperators) {
        for (int i = 0; i < queryOperators.size(); i++) {
            NameIdPair operator = queryOperators.get(i);
            operatorsMap.put(operator.getId(), operator.getName());
            operatorNameToIdMap.put(operator.getName(), operator.getId());

        }
    }

    private void populateModelsMap(final List<NameIdPair> models) {
        for (int i = 0; i < models.size(); i++) {
            int modelId = models.get(i).getId();
            String modelName = models.get(i).getName();
            modelsMap.put(modelId, modelName);
            String modelType = models.get(i).getBasedOn();
            modelsEntityTypeMap.put(modelId, modelType);
        }
    }

    private void populateKillChainMap(final List<NameIdPair> killChains) {
        for (int i = 0; i < killChains.size(); i++) {
            NameIdPair killChain = killChains.get(i);
            int id = killChain.getId();
            String category = killChain.getName();
            killChainMap.put(id, category);
        }
    }


    private void populateSecurityEventMap(final List<SecurityEventTypeConfiguration> securityEventTypes) {
        securityEventTypeList = securityEventTypes;
    }


    public Map<Integer, String> getModelsMap() {
        return modelsMap;
    }

    public Map<Integer, String> getModelsEntityTypeMap() {
        return modelsEntityTypeMap;
    }

    public Map<Integer, String> getKillChainMap() {
        return killChainMap;
    }

    public List<SecurityEventTypeConfiguration> getSecurityEventTypeList() {
        return securityEventTypeList;
    }

    public List<FilterField> getFilterFields() {
        return filterFields;
    }

    public Map<Integer, FilterField> getFilterFieldMap() {
        return filterFieldMap;
    }

    public Map<Integer, String> getOperatorsMap() {
        return operatorsMap;
    }

    public Map<String, Integer> getOperatorNameToIdMap() {
        return operatorNameToIdMap;
    }

    public Map<String, FilterField> getFilterFieldNamesMap() {
        return filterFieldNamesMap;
    }

    public List<C2ModelConfiguration> getC2ModelConfigurations() {
        return c2ModelConfigurations;
    }

    public Map<String, C2ModelConfiguration> getC2ModelConfigurationFeatureMap() {
        return c2ModelConfigurationFeatureMap;
    }
}
