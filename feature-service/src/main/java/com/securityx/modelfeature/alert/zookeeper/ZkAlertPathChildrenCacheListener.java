package com.securityx.modelfeature.alert.zookeeper;


import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.alert.AlertHandler;
import com.securityx.modelfeature.alert.AlertNotifier;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.alert.scheduler.AlertNotificationScheduler;
import com.securityx.modelfeature.common.cache.AlertCache;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.utils.JsonUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ZkAlertPathChildrenCacheListener implements PathChildrenCacheListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkAlertPathChildrenCacheListener.class);
    private final AlertNotifier alertNotifier;
    private final AlertNotificationScheduler alertNotificationScheduler;
    private final FeatureServiceCache featureServiceCache;
    private final AlertCache alertCache;
    private final FeatureServiceConfiguration conf;
    private final AlertAuditLogger alertLogger;

    public ZkAlertPathChildrenCacheListener(AlertNotifier alertNotifier, AlertNotificationScheduler alertNotificationScheduler,
                                            FeatureServiceCache featureServiceCache,
                                            AlertCache alertCache, FeatureServiceConfiguration conf, AlertAuditLogger alertLogger){
        this.alertNotifier = alertNotifier;
        this.alertNotificationScheduler = alertNotificationScheduler;
        this.featureServiceCache = featureServiceCache;
        this.alertCache = alertCache;
        this.conf = conf;
        this.alertLogger = alertLogger;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        switch ( event.getType() ){
            case CHILD_ADDED: {
                LOGGER.debug("Zookeeper CHILD_ADDED Notification");
                handleUpdateEvent(event);
                break;
            }
            case CHILD_UPDATED: {
                LOGGER.debug("Zookeeper CHILD_UPDATED Notification");
                handleUpdateEvent(event);
                break;
            }
            case CHILD_REMOVED: {
                LOGGER.debug("Zookeeper DELETE Notification");
                ChildData childData = event.getData();

                if(childData != null) {
                    byte[] dataByteArr = childData.getData();
                    Optional<AlertDefinition> alert = JsonUtils.getObjectFromJson(dataByteArr, AlertDefinition.class);
                    if(alert.isPresent()) {
                        alertCache.removeAlertFromCache(alert.get().getAlertId());
                        AlertHandler.deleteNotification(alertNotifier, alert.get(), featureServiceCache, conf);
                        alertNotificationScheduler.unschedule();
                    }else{
                        LOGGER.error("Error during handling delete-event => " + event);
                    }
                }
                break;
            }
        }
    }


    private void handleUpdateEvent(PathChildrenCacheEvent event){
        ChildData childData = event.getData();

        if(childData != null) {
            byte [] dataByteArr = childData.getData();
            Optional<AlertDefinition> alert = JsonUtils.getObjectFromJson(dataByteArr, AlertDefinition.class);
            if(alert.isPresent()) {
                //update cache
                alertCache.addToAlertsMap(alert.get().getAlertId(), alert.get());
                AlertHandler.addAlertListeners(alertNotifier, alert.get(), featureServiceCache, conf, alertLogger);
                alertNotificationScheduler.schedule(alert.get(), featureServiceCache, conf);
            } else{
                LOGGER.error("Error during handling Event => " + event);
            }
        }
    }
}
