package com.securityx.modelfeature.alert;

import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.common.inputs.AlertDestination;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.alerts.AlertsDestinationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.List;
import java.util.Optional;

public class AlertHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(AlertHandler.class);

    public final static String ZOOKEEKER_BASE_PATH = "/organizations/e8/alerts";
    public final static String ZOOKEEPER_PATH_SEPARATOR = "/";
    public static enum ALERT_DESTINATION {SMTP, SPLUNK, ARCSIGHT};
    public static enum AlertFequencies {
        DAILY, WEEKLY, HOURLY;

        public static Optional<Integer> getTimeDurationInHours(String frequencyString){
              if(frequencyString.equalsIgnoreCase(DAILY.name()))
                  return Optional.of(24);
             else if(frequencyString.equalsIgnoreCase(WEEKLY.name()))
                  return Optional.of(24*7);
            else if(frequencyString.equalsIgnoreCase(HOURLY.name()))
                  return Optional.of(1);
            else
                  return Optional.empty();
        }
    };
    public static enum ALERT_STATE{
        CREATED,
        MODIFIED,
        DELETED,
        DISPATCHED,
        DELIVERED,
        FAILED
    }

    /**
     * Registers AlertListener based on the AlertDefinition and AlertDestination saved in Db and.
     * AlertCache and FeatureServiceCache needs to be populated when this method is invoked
     *
     * @param alertNotifier AlertNotifier which sends the alerts to the AlertListener
     * @param alert AlertDefinition Alert
     * @param featureServiceCache  FeatureServiceCache
     * @param conf FeatureServiceConfiguration
     */
    public static void addAlertListeners(AlertNotifier alertNotifier, AlertDefinition alert, FeatureServiceCache featureServiceCache,
            FeatureServiceConfiguration conf, AlertAuditLogger alertLogger){

        LOGGER.debug("Registering Alert Listeners");
        //Alert destination for the alert being created/updated
        List<String> alertDestinationList = alert.getAlertDestination();

        //alert destinations configured in the system
        AlertsDestinationDao dao = new AlertsDestinationDao(conf);
        List<AlertDestination> alertDestinations = JavaConversions.asJavaList(dao.getAlertDestinations());
        for(AlertDestination alertDestination : alertDestinations){
            //unregister each alert.
            alertNotifier.unregister(alertDestination.getAlertDestinationId());

            //only register again if required.
            if(alertDestinationList.contains(alertDestination.getAlertDestinationName())) {
                AlertListener listener = getAlertListener(alertDestination, featureServiceCache, alertLogger);
                //register a new listener
                if (listener != null) {
                    alertNotifier.register(listener);
                }
            }
        }

    }


    public static void deleteNotification(AlertNotifier alertNotifier, AlertDefinition alert, FeatureServiceCache featureServiceCache,
                                         FeatureServiceConfiguration conf){
        LOGGER.debug("Delete Notification: Un-registering all alert-listeners");
        for(String alertDestinationName : alert.getAlertDestination()){
            int alertDestId = featureServiceCache.getAlertDestinationIdFromName(alertDestinationName);
            alertNotifier.unregister(alertDestId);
        }

    }


    public static AlertListener getAlertListener(AlertDestination alertDestination,
                                        FeatureServiceCache featureServiceCache, AlertAuditLogger alertLogger){

        AlertListener listener = null;
        if (alertDestination.getAlertDestinationName().equalsIgnoreCase(ALERT_DESTINATION.SMTP.name())) {
            listener = new EmailAlertListener(alertDestination,
                    featureServiceCache.getEmailSenderConfiguration(), alertLogger );
        } else if (alertDestination.getAlertDestinationName().equalsIgnoreCase(ALERT_DESTINATION.SPLUNK.name())) {
            listener = new SplunkAlertListener(alertDestination.getAlertDestinationId(),
                    alertDestination.getHostName(), alertDestination.getPort(), alertDestination.getTransport(), alertLogger);
        } else if (alertDestination.getAlertDestinationName().equalsIgnoreCase(ALERT_DESTINATION.ARCSIGHT.name())) {
            listener = new ArcSightListner(alertDestination.getAlertDestinationId(),
                    alertDestination.getHostName(), alertDestination.getPort(), alertDestination.getTransport(), alertLogger);
        } else {
            //do nothing
        }

        return listener;
    }

}
