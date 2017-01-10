package com.securityx.modelfeature.config.configloaders;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.config.WebAnomalyConfiguration;
import com.securityx.modelfeature.config.WebAnomalyProfileNameConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harish on 1/23/15.
 */
public class WebAnomalyProfileConfigurationLoader {


    private final Logger LOGGER = LoggerFactory.getLogger(WebAnomalyProfileConfigurationLoader.class);

    private Map<String, Map<String, String>> anomalyProfileMapping = new HashMap<String, Map<String, String>>();

    public void loadWebAnomalyProfileNames(FeatureServiceConfiguration conf) {
        String webAnomalyProfileNamesFilePath = conf.getConfigurationConstants().getWebAnomalyProfileNamesFilePath();
        try {
            InputStream inputStream = new FileInputStream(webAnomalyProfileNamesFilePath);
            Yaml ymlLoader = new Yaml();
            WebAnomalyConfiguration configuration = ymlLoader.loadAs(inputStream, WebAnomalyConfiguration.class);
            List<WebAnomalyProfileNameConfiguration> webAnomalyProfiles = configuration.getWebAnomalyProfiles();

                for (WebAnomalyProfileNameConfiguration webAnomalyProfile : webAnomalyProfiles) {
                    Map<String, String> mapping = new HashMap<String, String>();
                    String type = webAnomalyProfile.getType();
                    String anomalyProfile = webAnomalyProfile.getAnomalyProfile();
                    String anomalyProfileName = webAnomalyProfile.getAnomalyProfileName();
                    String anomalyProfileCategory = webAnomalyProfile.getAnomalyProfileCategory();
                    mapping.put("anomalyProfile", anomalyProfile);
                    mapping.put("type", type);
                    mapping.put("eventType", anomalyProfileName);
                    mapping.put("eventCategory", anomalyProfileCategory);

                    anomalyProfileMapping.put(anomalyProfile, mapping);
                }

        } catch (Exception e) {
            LOGGER.error("Error occurred while loading file => " + webAnomalyProfileNamesFilePath + ": " + e);
            throw new RuntimeException("Error loading Web Anomaly Profile Configuration => ", e);
        }

    }

    public Map<String, Map<String, String>> getAnomalyProfileMapping(){
        return anomalyProfileMapping;
    }

}
