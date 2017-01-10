package com.securityx.modelfeature.alert;

import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.utils.CEF;

import java.util.List;

public class AlertNotificationMessage {
    private AlertDefinition alert;
    private List<CEF> cefs;

    public AlertNotificationMessage(AlertDefinition alert, List<CEF> cefs) {
        this.alert = alert;
        this.cefs = cefs;
    }

    public AlertDefinition getAlert() {
        return alert;
    }

    public void setAlert(AlertDefinition alert) {
        this.alert = alert;
    }

    public List<CEF> getCefs() {
        return cefs;
    }

    public void setCefs(List<CEF> cefs) {
        this.cefs = cefs;
    }
}