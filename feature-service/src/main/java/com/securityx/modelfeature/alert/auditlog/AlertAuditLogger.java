package com.securityx.modelfeature.alert.auditlog;

import com.google.common.collect.Queues;
import com.securityx.modelfeature.FeatureServiceThreadPool;
import com.securityx.modelfeature.alert.AlertHandler;
import com.securityx.modelfeature.common.AlertLog;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.alerts.AlertLogDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class AlertAuditLogger {

    private final Logger LOGGER = LoggerFactory.getLogger(AlertAuditLogger.class);
    BlockingQueue<AlertLog> auditLogs = Queues.newLinkedBlockingQueue(1);
    private final FeatureServiceConfiguration conf;
    FeatureServiceThreadPool featureServiceThreadPool;

    public AlertAuditLogger(FeatureServiceConfiguration conf, FeatureServiceThreadPool featureServiceThreadPool){
        this.conf = conf;
        this.featureServiceThreadPool = featureServiceThreadPool;
        LOGGER.debug("Starting alert audit logger...");
        this.featureServiceThreadPool.scheduleNow(new LoggerThread());
    }

    public void logAlert(AlertLog alertLog) throws InterruptedException {
        auditLogs.put(alertLog);
    }

    public AlertLog getAlertLog() throws InterruptedException {
        return auditLogs.take();
    }

    class LoggerThread implements Runnable {
        private final Logger LOGGER = LoggerFactory.getLogger(LoggerThread.class);
        private boolean shutdown = false;
        private String alertId = null;
        private AlertHandler.ALERT_STATE alertState = null;
        @Override
        public void run() {
            while (!isShutdown()) {
                try {
                    AlertLog alertLog = getAlertLog();
                    alertId = alertLog.getAlert().getAlertId();
                    alertState = alertLog.getAlertState();
                    LOGGER.debug("Logging into audit-log for state => {}", alertState);
                    new AlertLogDao(conf).logAlert(alertLog);
                } catch (InterruptedException e) {
                    LOGGER.error(String.format("Exception while adding audit logging for alert id => {}, state => {}, {}", alertId, alertState.toString(), e.getMessage()), e);
                    setShutdown(true);
                }
            }
            LOGGER.debug("Alert audit logger thread exit");
        }

        public boolean isShutdown(){
            return shutdown;
        }

        public void setShutdown(boolean shutdown){
            this.shutdown = shutdown;
        }
    }

}
