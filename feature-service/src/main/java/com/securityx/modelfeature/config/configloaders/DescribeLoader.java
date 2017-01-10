package com.securityx.modelfeature.config.configloaders;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by harish on 12/24/14.
 */
public class DescribeLoader{

    private final Logger LOGGER = LoggerFactory.getLogger(DescribeLoader.class);


    private enum HttpCategoryConstants {
        id, name, typeField, subcategories
    }

    private enum AdCategoryConstants {
        id, name, subcategories
    }

    private enum EventsConstants{
        id, name, groupIds
    }

    private enum AdErrorConstants{
        id, name, groupIds
    }
    //id to name for categories and sub categories
    public Map<Integer, String> localizationMap = new HashMap<Integer, String>();
    //map of catId to subCatId
    public Map<Integer, Integer> subCategoryToCategoryMap = new HashMap<Integer, Integer>();

    //HTTP
    public Map<String,String> httpFieldNameToCategoryName = new HashMap<String, String>();
    public Map<String,String> httpCategoryToFieldName = new HashMap<String, String>();
    public Map<String, Map<String, Integer>> httpFieldToGroupToSubcategory = new HashMap<String, Map<String, Integer>>();
    public List<String> httpRequestSchemeContentTypes = new LinkedList<String>();

    //AD
    //event id to event name
    public Map<Integer, String> eventIdToNameMap = new HashMap<Integer, String>();
    //map of group ID to subCat Id
    public Map<Integer, Integer> groupIdToSubcategoryIdMap = new HashMap<Integer, Integer>();
    //map of category name to list of events
    public Map<String, List<Integer>> categoryToEvent = new HashMap<String, List<Integer>>();
    //AD Error
    //error id to list of group Ids
    public Map<Integer, Set<Integer>> errorStatusMap = new HashMap<Integer, Set<Integer>>();
    //map of error name to error id
    public Map<String, Integer> errorStatusNameToIdMap = new HashMap<String, Integer>();

    //tanium
    public Map<String,String> taniumFieldNameToCategoryName = new HashMap<String, String>();
    public Map<String,String> taniumCategoryToFieldName = new HashMap<String, String>();

    public boolean loadDescribe(FeatureServiceConfiguration conf) {
        try {

            InputStream inputStream = new FileInputStream(conf.getConfigurationConstants().getTimeSeriesFilePath());
            Yaml ymlLoader = new Yaml();
            Map<String, Object> ymlMap = (Map<String, Object>) ymlLoader.load(inputStream);
            eventIdToNameMap = (Map<Integer, String>) ymlMap.get("GroupIdToName");
            loadAdTimeSeriesCategories(ymlMap);
            loadAdTimeSeriesErrors(ymlMap);
            loadHttpTimeSeriesCategories(ymlMap);
            loadTaniumTimeSeriesCategories(ymlMap);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error occurred while loading timeSeries.yml file: " + e);
            throw new RuntimeException("Error loading TimeSeries configuration => ", e);

        }
    }

    private void loadAdTimeSeriesCategories(Map<String, Object> ymlMap) {

        List<LinkedHashMap<String, Object>> adCategories = (List<LinkedHashMap<String, Object>>) ymlMap.get("AdTimeSeriesCategories");

        for(int i = 0; i < adCategories.size(); i++){
            LinkedHashMap<String, Object> category = adCategories.get(i);
            int catId = (Integer)category.get(AdCategoryConstants.id.name());
            String catName = (String)category.get(AdCategoryConstants.name.name());
            localizationMap.put(catId, catName);

            List<LinkedHashMap<String, Object>> subCategories = (List<LinkedHashMap<String, Object>>)category.get(AdCategoryConstants.subcategories.name());

            for (int j = 0; j < subCategories.size(); j++) {
                LinkedHashMap<String, Object> event = subCategories.get(j);
                int subcategoryId = (Integer)event.get(EventsConstants.id.name());

                subCategoryToCategoryMap.put(subcategoryId, catId);

                String subcategoryName = (String)event.get(EventsConstants.name.name());
                localizationMap.put(subcategoryId, subcategoryName);

                List<Integer> groupIds = (List<Integer>)event.get(EventsConstants.groupIds.name());
                for (int groupId : groupIds) {
                    groupIdToSubcategoryIdMap.put(groupId, subcategoryId);
                    List<Integer> list = categoryToEvent.get(catName);
                    if(list == null){
                        list = new LinkedList<Integer>();
                    }
                    list.add(groupId);
                    categoryToEvent.put(catName, list);

                    list = categoryToEvent.get(subcategoryName);
                    if(list == null){
                        list = new LinkedList<Integer>();
                    }
                    list.add(groupId);
                    categoryToEvent.put(subcategoryName, list);
                }
            }

        }
    }

    private void loadHttpTimeSeriesCategories(Map<String, Object> ymlMap) {

        List<LinkedHashMap<String, Object>> httpCategories = (List<LinkedHashMap<String, Object>>) ymlMap.get("HttpTimeSeriesCategories");
        for(int i = 0; i < httpCategories.size(); i++){
            LinkedHashMap<String, Object> category = httpCategories.get(i);
            int catId = (Integer) category.get(HttpCategoryConstants.id.name());
            String catName = (String)category.get(HttpCategoryConstants.name.name());
            localizationMap.put(catId, catName);
            String fieldName = (String)category.get(HttpCategoryConstants.typeField.name());
            httpFieldNameToCategoryName.put(fieldName, catName);
            httpCategoryToFieldName.put(catName, fieldName);

            List<LinkedHashMap<String, Object>> subCategories = (List<LinkedHashMap<String, Object>>)category.get(HttpCategoryConstants.subcategories.name());
            for(int j = 0 ; j < subCategories.size(); j ++){
                LinkedHashMap<String, Object> event = subCategories.get(j);
                int subCategoryId = (Integer)event.get(EventsConstants.id.name());
                String subcategoryName = (String)event.get(EventsConstants.name.name());
                localizationMap.put(subCategoryId, subcategoryName);

                if(fieldName.equalsIgnoreCase("responseContentType") || fieldName.equalsIgnoreCase("requestScheme")){
                    httpRequestSchemeContentTypes.add(subcategoryName.toLowerCase());
                }
                List<String> groupIds = (List<String>)event.get(EventsConstants.groupIds.name());
                for (String groupId : groupIds) {
                    Map<String, Integer> map = httpFieldToGroupToSubcategory.get(fieldName);
                    if(map == null){
                        map = new HashMap<String, Integer>();
                        httpFieldToGroupToSubcategory.put(fieldName, map);
                    }
                    map.put(groupId, subCategoryId);
                }
            }
        }
    }

    private void loadTaniumTimeSeriesCategories(Map<String, Object> ymlMap) {

        List<LinkedHashMap<String, Object>> taniumCategories = (List<LinkedHashMap<String, Object>>) ymlMap.get("TaniumTimeSeriesCategories");
        for(int i = 0; i < taniumCategories.size(); i++){
            LinkedHashMap<String, Object> category = taniumCategories.get(i);
            int catId = (Integer) category.get(HttpCategoryConstants.id.name());
            String catName = (String)category.get(HttpCategoryConstants.name.name());
            localizationMap.put(catId, catName);
            String fieldName = (String)category.get(HttpCategoryConstants.typeField.name());
            taniumFieldNameToCategoryName.put(fieldName, catName);
            taniumCategoryToFieldName.put(catName, fieldName);
        }
    }


    /**
     * loads the AdTimeSeriesError related info
     * @param ymlMap
     */
    private void loadAdTimeSeriesErrors(Map<String, Object> ymlMap) {

        List<LinkedHashMap<String, Object>> errors = (List<LinkedHashMap<String, Object>>) ymlMap.get("AdTimeSeriesError");

        for(int i = 0; i < errors.size(); i++){
            LinkedHashMap<String, Object> error = errors.get(i);
            int id = (Integer)error.get(AdErrorConstants.id.name());
            String name = (String) error.get(AdErrorConstants.name.name());
            errorStatusNameToIdMap.put(name, id);
            localizationMap.put(id, name);
            List<Integer> errorList = (List<Integer>) error.get(AdErrorConstants.groupIds.name());
            Set<Integer> set = errorStatusMap.get(id);
            if(set == null){
                set = new HashSet<Integer>();
            }
            set.addAll(errorList);
            errorStatusMap.put(id, set);
        }
    }

}