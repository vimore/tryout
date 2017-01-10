package com.securityx.modelfeature.common.cache;

import com.google.common.collect.Maps;
import com.securityx.modelfeature.alert.zookeeper.ZookeeperClient;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.alerts.AlertsDao;
import com.securityx.modelfeature.dao.alerts.ZooKeeperAlertStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;
import scala.collection.mutable.ListBuffer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AlertCache {


    private static final Logger LOGGER = LoggerFactory.getLogger(AlertCache.class);
    private Map<String, AlertDefinition> alertsMap = Maps.newHashMap();

    public Map<String, AlertDefinition> getAlertsMap(){
        return alertsMap;
    }

    public void addToAlertsMap(String alertId, AlertDefinition alert){
        alertsMap.put(alertId, alert);
    }

    public void removeAlertFromCache(String alertId){
        alertsMap.remove(alertId);
    }

    public Set<String> getKeys() {
        return alertsMap.keySet();
    }

}
