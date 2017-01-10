package com.securityx.modelfeature.config.configloaders;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.config.SearchConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by harish on 2/2/15.
 */
public class SearchConfigurationLoader {

    private final Logger LOGGER = LoggerFactory.getLogger(SearchConfigurationLoader.class);

    private SearchConfiguration searchConfiguration = null;

    private Map<String, String> webSearchSummaryMap = Maps.newHashMap();

    private Map<String, String> adSearchSummaryMap = Maps.newHashMap();

    private Map<String, String> taniumHostInfoMefSearchSummaryMap = Maps.newHashMap();

    public void loadSearchConfigInfo(final FeatureServiceConfiguration conf) {
        try {
            String searchConfigFilePath = conf.getConfigurationConstants().getSearchConfFilePath();
            InputStream inputStream = new FileInputStream(new File(searchConfigFilePath));
            Yaml ymlLoader = new Yaml();
            searchConfiguration = ymlLoader.loadAs(inputStream, SearchConfiguration.class);
            populateSummaryMaps(searchConfiguration.getWebSearchSummary(), webSearchSummaryMap);
            populateSummaryMaps(searchConfiguration.getAdSearchSummary(), adSearchSummaryMap);
            populateSummaryMaps(searchConfiguration.getTaniumHostInfoMefSummary(), taniumHostInfoMefSearchSummaryMap);
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading search-configuration file => " + e);
            throw new RuntimeException("Error loading Search Configuration => ", e);
        }
    }


    private void populateSummaryMaps(final List<SearchConfiguration.FieldInfo> webFields, final Map<String, String> map) {
        for (int i = 0; i < webFields.size(); i++) {
            String field = webFields.get(i).getField();
            String modelName = webFields.get(i).getName();
            map.put(field, modelName);
        }
    }

    public SearchConfiguration getSearchConfiguration() {
        return searchConfiguration;
    }

    public Map<String, String> getWebSearchSummaryMap() {
        return webSearchSummaryMap;
    }

    public Map<String, String> getAdSearchSummaryMap() {
        return adSearchSummaryMap;
    }

    public Map<String, String> getTaniumHostInfoMefSearchSummaryMap() {
        return taniumHostInfoMefSearchSummaryMap;
    }
}
