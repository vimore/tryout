package com.securityx.modelfeature.alert.scheduler;

import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.FeatureServiceThreadPool;
import com.securityx.modelfeature.alert.AlertHandler;
import com.securityx.modelfeature.alert.AlertNotifier;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.common.cache.AlertCache;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.config.AlertConfiguration;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AlertNotificationScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(AlertNotificationScheduler.class);
    private final AlertNotifier alertNotifier;
    FeatureServiceThreadPool threadPool;
    ScheduledFuture<?> scheduleFuture;

    public AlertNotificationScheduler(AlertNotifier alertNotifier, AlertAuditLogger alertLogger, FeatureServiceThreadPool featureServiceThreadPool) {
        this.alertNotifier = alertNotifier;
        this.threadPool = featureServiceThreadPool;
    }

    public void schedule(AlertDefinition alert, FeatureServiceCache cache, FeatureServiceConfiguration conf) {
        try {
            if (alert == null) {
                return;
            }
            scheduleNotification(alert, cache, conf);
        } catch (Exception e) {
            LOGGER.error("Alert-Scheduling failed => " + e);
        }
    }

    private void scheduleNotification(AlertDefinition alert, FeatureServiceCache cache, FeatureServiceConfiguration conf) {
        LOGGER.debug("Scheduling Alert Notification...");
        //cancel old tasks
        cancelIfExists();

        //get frequency
        long freq = 0;
        Optional<Integer> timeDurationInHours = getSchedulingDurationInHours(alert, cache);
        if (timeDurationInHours.isPresent())
            freq = timeDurationInHours.get();

        Optional<Integer> queryDuration = getQueryDuration(alert, cache);
        int qduration = 24;
        if(queryDuration.isPresent()){
            qduration = queryDuration.get();
        }
        AlertSender alertSender = new AlertSender(alert, alertNotifier, conf, cache, qduration);

        //now submit a new job
        scheduleFuture = threadPool.scheduleAtFixedRate(alertSender, 0L, freq, TimeUnit.HOURS);
        LOGGER.debug("Alert scheduled");
    }

    public void unschedule() {
        cancelIfExists();
    }


    private void cancelIfExists() {
        if (scheduleFuture != null) {
            LOGGER.debug("Cancelling old schedule");
            scheduleFuture.cancel(true);
        }
    }

    private Optional<Integer> getSchedulingDurationInHours(AlertDefinition alert, FeatureServiceCache cache){
        //get frequency
        String frequencyString = alert.getFrequency();
        AlertConfiguration.AlertSchedulerConfiguration alertSchedulerConfiguration = cache.getAlertSChedulerConfiguration();
        if(alertSchedulerConfiguration.isOverrideUser()){
            frequencyString = alertSchedulerConfiguration.getSchedule();
        }
        return AlertHandler.AlertFequencies.getTimeDurationInHours(frequencyString);
    }

    private Optional<Integer> getQueryDuration(AlertDefinition alert, FeatureServiceCache cache){
        String frequencyString = alert.getFrequency();
        AlertConfiguration.AlertSchedulerConfiguration alertSchedulerConfiguration = cache.getAlertSChedulerConfiguration();
        if(alertSchedulerConfiguration.isOverrideUser()){
            return Optional.of(alertSchedulerConfiguration.getQueryDuration());
        }
        Optional<Integer> duration = AlertHandler.AlertFequencies.getTimeDurationInHours(frequencyString);
        if(duration.isPresent()){
            int d = duration.get();
            //Sql query should be performed for last 24 hours
            //This is because after 23th hour, peer group and other daily model produce output.
            // The timestamp here may not be of last 1 hour.
            // It can be 00th hour of the day. So we go back atleast 24hours back.
            if(d < 24){
                d = 24;
            }
            return Optional.of(d);
        }
        return Optional.empty();
    }
}
