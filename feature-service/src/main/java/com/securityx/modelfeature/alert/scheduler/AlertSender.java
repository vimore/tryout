package com.securityx.modelfeature.alert.scheduler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.alert.AlertNotificationMessage;
import com.securityx.modelfeature.alert.AlertNotifier;
import com.securityx.modelfeature.alert.AlertNotifierImpl;
import com.securityx.modelfeature.common.EntityBehavior;
import com.securityx.modelfeature.common.EntityInfo;
import com.securityx.modelfeature.common.EntityModelInfo;
import com.securityx.modelfeature.common.ThresholdDefinition;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.common.inputs.SecurityEventBehavior;
import com.securityx.modelfeature.common.inputs.QueryJson;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.DetectorHomeDao;
import com.securityx.modelfeature.dao.alerts.AlertLogDao;
import com.securityx.modelfeature.utils.CEF;
import com.securityx.modelfeature.utils.CefExtension;
import com.securityx.modelfeature.utils.MiscUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Querying and generating alerts
 */
public class AlertSender implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(AlertSender.class);
    private AlertDefinition alertDefinition;
    private AlertNotifier alertNotifier;
    private FeatureServiceConfiguration conf;
    private FeatureServiceCache featureServiceCache;
    private int queryDuration;

    public AlertSender(AlertDefinition alertDefinition, AlertNotifier alertNotifier, FeatureServiceConfiguration conf,
                       FeatureServiceCache featureServiceCache, int queryDuration) {
        this.alertDefinition = alertDefinition;
        this.alertNotifier = alertNotifier;
        this.conf = conf;
        this.featureServiceCache = featureServiceCache;
        this.queryDuration = queryDuration;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("Running Alert Sender");
            List<CEF> cefs = getAlerts();
            //post message to the alert notifier
            if(cefs != null && !cefs.isEmpty()) {
                sendNotifications(alertDefinition, cefs);
            }   else{
                LOGGER.debug("No entities found. Alert-notification will not be sent.");
            }
        }catch(Exception e){
            LOGGER.error("Exception in Alert sender => ", e);
        }
    }


    private void sendNotifications(AlertDefinition alert, List<CEF> cefs){
        if (alertNotifier instanceof AlertNotifierImpl) {
            AlertNotifierImpl notifier = (AlertNotifierImpl) alertNotifier;
            notifier.postMessage(new AlertNotificationMessage(alert, cefs));
        }

    }

    /**
     *
     * @return List of CEF that will be sent to the AlertListeners to send out the alert.
     *
     */
    private List<CEF> getAlerts(){
        Map<EntityInfo, EntityModelInfo> entityInfoEntityModelInfoMap =  getThreatsForAlerting();
        List<CEF> cefs = Lists.newLinkedList();
        for (Map.Entry<EntityInfo, EntityModelInfo> entry : entityInfoEntityModelInfoMap.entrySet()) {
            //create CEF object
            Map<String, String> entityInfoMap = Maps.newHashMap();
            if (entry.getKey().getDateTime() != null) {
                entityInfoMap.put("dateTime", entry.getKey().getDateTime());
            }
            if (entry.getKey().getIpAddress() != null) {
                entityInfoMap.put("ipAddress", entry.getKey().getIpAddress());
            }
            if (entry.getKey().getHostName() != null) {
                entityInfoMap.put("hostName", entry.getKey().getHostName());
            }
            if (entry.getKey().getMacAddress() != null) {
                entityInfoMap.put("macAddress", entry.getKey().getMacAddress());
            }
            if (entry.getKey().getUserName() != null) {
                entityInfoMap.put("userName", entry.getKey().getUserName());
            }
            EntityModelInfo eventModelInfo = entry.getValue();

            CEF cef = new CEF(eventModelInfo.getEventType(), eventModelInfo.getShortDescription(), eventModelInfo.getRisk(),
                    new CefExtension(entityInfoMap), featureServiceCache);

            cefs.add(cef);

        }

        return cefs;
    }

    /**
     * Queries ThreatView table based on the Alert filter and returns the entities that will be sent out as a part of Alert
     *
     * @return Map of EntityInfo to EntityModelInfo
     */
    private Map<EntityInfo, EntityModelInfo> getThreatsForAlerting(){
        Map<String, List<SecurityEventBehavior>> entityToEntityBehvaiorMap = getEntityToEntityBehvaiorMap();
        QueryJson queryJson = getQueryJson();
        Map<EntityInfo, Double> entityToEntityRiskMap = getEntityToRiskScoreMap(queryJson.getStartTime(), queryJson.getEndTime());
        LOGGER.debug("Querying db to check for alert");
        DetectorHomeDao detectorHomeDao = new DetectorHomeDao(conf);
        return JavaConversions.asJavaMap(
                detectorHomeDao.getThreats(queryJson,entityToEntityBehvaiorMap, entityToEntityRiskMap, featureServiceCache));
    }


    /**
     * Iterates at the filter (Do not Alert List) and
     * returns a Map of IpAddress (ip address of entity) to List of Behavior for which alert should not be sent
     *
     * @return Map of IpAddress (ip address of entity) to List of Behavior for which alert should not be sent
     */
    private Map<EntityInfo, Double> getEntityToRiskScoreMap( String startTime, String endTime ){
        Map<EntityInfo, Double> entityToEntityRiskScoreMap = Maps.newHashMap();
        AlertLogDao alertLogDao = new AlertLogDao(conf);
        LOGGER.debug("Querying Alert-Audit Logs to check for last sent alert");
        String[] logs = alertLogDao.getLogsForLatestAlertSent();
        if(logs != null) {
            for (String syslog : logs) {
                LOGGER.debug("getEntityToRiskScoreMap: processing alert : "+syslog);
                Optional<CEF> cef = CEF.syslogCefToCef(syslog);
                if(cef.isPresent()) {
                    CEF cefObj = cef.get();
                    CefExtension cefExtension = cefObj.getExtension();
                    Map<String, String> extensions = cefExtension.getExtensionFields();
                    String ipAddress = null;
                    String userName = null;
                    String hostName = null;
                    String macAddress = null;

                    //Map Back CEF concept to EntityInfo
                    if (extensions.containsKey("dst")) {
                        ipAddress = extensions.get("dst");
                    }
                    if (extensions.containsKey("duser")) {
                        userName = extensions.get("duser");
                    }
                    if (extensions.containsKey("dhost")) {
                        hostName = extensions.get("dhost");
                    }

                    if (extensions.containsKey("dmac")) {
                        macAddress = extensions.get("dmac");
                    }

                    entityToEntityRiskScoreMap.put(new EntityInfo(null,ipAddress, macAddress, hostName, userName), cefObj.getSeverity());
                }
            }
        }

        return entityToEntityRiskScoreMap;
    }


    /**
     * Iterates at the filter (Do not Alert List) and
     * returns a Map of IpAddress (ip address of entity) to List of Behavior for which alert should not be sent
     *
     * @return Map of IpAddress (ip address of entity) to List of Behavior for which alert should not be sent
     */
    private Map<String, List<SecurityEventBehavior>> getEntityToEntityBehvaiorMap( ){
        Map<String, List<SecurityEventBehavior>> entityToEntityBehvaiorMap = Maps.newHashMap();
        if(alertDefinition != null && alertDefinition.getFilter() != null) {
            for (EntityBehavior entityBehavior : alertDefinition.getFilter()) {
                for (String sourceIp : entityBehavior.getSourceIp()) {
                    entityToEntityBehvaiorMap.put(sourceIp, entityBehavior.getBehaviors());
                }
            }
        }
        return entityToEntityBehvaiorMap;
    }

    /**
     * create the alert query
     *
     * @return QueryJson representing the alert-sql-query
     */
    private QueryJson getQueryJson() throws RuntimeException {
        //Compute DateTime-range for the query
        //get beginning of TODAY
        DateTime endTime = DateTime.now(DateTimeZone.UTC);
        String startTimeStr = MiscUtils.getBeginOfDay(endTime.minusHours(queryDuration).toString());
        DateTime startTime = new DateTime(startTimeStr);
        LOGGER.debug("Alert-Query startTime = " + startTime);
        LOGGER.debug("Alert-Query endTime = " + endTime);

        //Compute Risk score and the Operator
        List<ThresholdDefinition> thresholds = alertDefinition.getThreshold();
        Map<String, Object> filterMap = Maps.newHashMap();
        for(ThresholdDefinition threshold : thresholds){
            filterMap.put("field", threshold.getField());
            filterMap.put("operator", threshold.getOperator());
            filterMap.put("values", threshold.getValues());
        }
        List<Map<String, Object>> queryList = Lists.newLinkedList();
        queryList.add(filterMap);

        //QueryJson
        QueryJson queryJson = new QueryJson();
        queryJson.setStartTime(startTime.toString());
        queryJson.setEndTime(endTime.toString());
        queryJson.setQuery(queryList);
        queryJson.setSortField("risk");
        queryJson.setSortOrder("DESC");

        return queryJson;
    }
}
