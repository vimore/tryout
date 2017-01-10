package com.securityx.modelfeature.common;

import com.securityx.modelfeature.alert.AlertHandler;
import com.securityx.modelfeature.common.inputs.AlertDefinition;

public class AlertLog {
    private String dateTime;
    private AlertDefinition alert;
    private String alertDestination;
    private AlertHandler.ALERT_STATE alertState;
    private String alertLog;
    private String errorLog;

    public AlertLog(String dateTime, AlertDefinition alert,String alertDestination, AlertHandler.ALERT_STATE alertState,
                    String alertLog, String errorLog) {
        this.dateTime = dateTime;
        this.alert = alert;
        this.alertDestination = alertDestination;
        this.alertState = alertState;
        this.alertLog = alertLog;
        this.errorLog = errorLog;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public AlertDefinition getAlert() {
        return alert;
    }

    public void setAlert(AlertDefinition alert) {
        this.alert = alert;
    }

    public String getAlertDestination() {
        return alertDestination;
    }

    public void setAlertDestination(String alertDestination) {
        this.alertDestination = alertDestination;
    }

    public AlertHandler.ALERT_STATE getAlertState() {
        return alertState;
    }

    public void setAlertState(AlertHandler.ALERT_STATE alertState) {
        this.alertState = alertState;
    }

    public String getAlertLog() {
        return alertLog;
    }

    public void setAlertLog(String alertLog) {
        this.alertLog = alertLog;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }
}
