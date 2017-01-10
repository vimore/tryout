package com.securityx.modelfeature.dao;

import com.securityx.modelfeature.FeatureServiceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class SearchDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchDao.class);
    public static final int DEFAULT_START_ROWS = 0;

    public abstract List<Map<String, Object>> getSearchResultsForSourceDestination(int modelId, List<String> sources, List<String> destinations,
                                                                                   Map<String, List<String>> queryParams,
                                                                                   String startTime, String endTime, int summaryFacetLimit, int endRows, int pageNo,
                                                                                   String sortField, String sortOrder, boolean summarize,
                                                                                   FeatureServiceCache cache);

    public abstract List<Map<String, Object>> getFacetedSearchResults(int modelId, int securityEventId,
                                                             Map<String, List<String>> queryParams, String[] selectedEntities, String[] keywords,
                                                             String startTime, String endTime, int summaryFacetLimit, int endRows, int pageNo,
                                                             String sortField, String sortOrder, boolean summarize,
                                                             FeatureServiceCache cache);
}

