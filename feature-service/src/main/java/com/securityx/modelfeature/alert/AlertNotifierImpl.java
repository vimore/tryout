package com.securityx.modelfeature.alert;

import com.google.common.collect.Maps;
import com.securityx.modelfeature.alert.auditlog.AlertAuditLogger;
import com.securityx.modelfeature.common.AlertLog;
import com.securityx.modelfeature.utils.CEF;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AlertNotifierImpl implements  AlertNotifier {
    private final Logger LOGGER  = LoggerFactory.getLogger(AlertNotifierImpl.class);

    private final AlertAuditLogger alertAuditLogger;
    private Map<Integer, AlertListener> listeners;
    private AlertNotificationMessage alertNotificationMessage;
    private boolean changed = false;

    public  AlertNotifierImpl(AlertAuditLogger alertAuditLogger) {
        this.alertAuditLogger = alertAuditLogger;
        this.listeners = Maps.newHashMap();
    }

    @Override
    public void register(AlertListener listener) {
        if (listener != null) {
            LOGGER.debug("Registering alert-listener : " + listener);
            listeners.put(listener.getListenerId(), listener);
        } else {
            LOGGER.debug("Unable to register null alert listener");
        }
        LOGGER.debug("registered listerner(s): "+listeners.size());
    }

    @Override
    public void unregister(int listenerId) {
        if (listeners.containsKey(listenerId)) {
            LOGGER.debug("Un-registering alert-listener : " + listeners.get(listenerId));
            listeners.remove(listenerId);
        }
    }

    @Override
    public void notifyObservers() {
        if (!changed)
            return;

        this.changed = false;
        LOGGER.debug("notifying listerner(s) count : "+listeners.size());
        int i = 0;
        for (Map.Entry<Integer, AlertListener> listener : listeners.entrySet()) {
            i++;
            try {
                LOGGER.debug("Sending notification to listener => " + listener.getValue());
                alertAuditLogger.logAlert(new AlertLog(DateTime.now().toString(),
                        alertNotificationMessage.getAlert(),listener.getValue().toString(),
                        AlertHandler.ALERT_STATE.DISPATCHED,
                        CEF.getLogs(alertNotificationMessage.getCefs()), null));
            } catch (InterruptedException e) {
                LOGGER.error("Error while logging dispatch => " + e);
            }
            sendAlert(listener.getValue());
        }
        LOGGER.debug("notified listerner count : "+i);
    }

    private void sendAlert(AlertListener listener) {
        String errorMessage = null;
        AlertHandler.ALERT_STATE alertState = AlertHandler.ALERT_STATE.DELIVERED;
        try {
            listener.sendAlert(alertNotificationMessage);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            alertState = AlertHandler.ALERT_STATE.FAILED;
        }
        AlertLog alertLog = new AlertLog(DateTime.now().toString(), alertNotificationMessage.getAlert(), listener.toString(),
                alertState, CEF.getLogs(alertNotificationMessage.getCefs()), errorMessage);

        try {
            alertAuditLogger.logAlert(alertLog);
        } catch (InterruptedException e) {
            LOGGER.error("Error while logging Delivery status => " + e);
        }
    }

    //method to post message to the listener
    public void postMessage(AlertNotificationMessage alertNotificationMessage){
        this.alertNotificationMessage = alertNotificationMessage;
        this.changed=true;
        notifyObservers();
    }
}
