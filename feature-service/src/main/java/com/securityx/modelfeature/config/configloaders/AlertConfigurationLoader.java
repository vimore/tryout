package com.securityx.modelfeature.config.configloaders;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.config.AlertConfiguration;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.config.NameIdPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class AlertConfigurationLoader {

    private final Logger LOGGER = LoggerFactory.getLogger(AlertConfigurationLoader.class);
    private Map<Integer, String> alertDestinationMap = Maps.newHashMap();
    private AlertConfiguration.CefConfiguration cefConfiguration;
    private AlertConfiguration.EmailSenderConfiguration emailSenderConfiguration;
    private AlertConfiguration.AlertSchedulerConfiguration alertSchedulerConfiguration;

    public void loadAlertConfigInfo(final FeatureServiceConfiguration conf) {
        try {
            String alertConfigPath = conf.getConfigurationConstants().getAlertConfFilePath();
            InputStream inputStream = new FileInputStream(new File(alertConfigPath));
            Yaml ymlLoader = new Yaml();
            AlertConfiguration alertConfiguration = ymlLoader.loadAs(inputStream, AlertConfiguration.class);
            populateAlertDestinationMap(alertConfiguration.getAlertDestinations());
            cefConfiguration = alertConfiguration.getCefConfig();
            emailSenderConfiguration = alertConfiguration.getEmailSenderConfiguration();
            alertSchedulerConfiguration = alertConfiguration.getAlertSchedulerConfiguration();
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading alert-configuration file => " + e);
            throw new RuntimeException("Error loading Alert Configuraion => ", e);
        }
    }

    private void populateAlertDestinationMap(final List<NameIdPair> alertDestinations) {
        for (int i = 0; i < alertDestinations.size(); i++) {
            NameIdPair operator = alertDestinations.get(i);
            alertDestinationMap.put(operator.getId(), operator.getName());
        }
    }

    public Map<Integer, String> getAlertDestinationMap() {
        return alertDestinationMap;
    }

    public Integer getAlertDestinationIdFromDestinationName(String alertDestinationName) {
        for(Map.Entry<Integer, String> entry : alertDestinationMap.entrySet()){
            if(entry.getValue().equalsIgnoreCase(alertDestinationName))
                return entry.getKey();
        }
        return null;
    }

    public AlertConfiguration.CefConfiguration getCefConfiguration() {
        return cefConfiguration;
    }

    public AlertConfiguration.EmailSenderConfiguration getEmailSenderConfiguration() {
        return emailSenderConfiguration;
    }

    public AlertConfiguration.AlertSchedulerConfiguration getAlertSchedulerConfiguration() {
        return alertSchedulerConfiguration;
    }
}
