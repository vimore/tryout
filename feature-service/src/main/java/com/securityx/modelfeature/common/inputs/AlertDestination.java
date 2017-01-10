package com.securityx.modelfeature.common.inputs;

import com.sun.istack.NotNull;

import java.util.List;

public class AlertDestination {

    @NotNull
    private String dateTime;

    @NotNull
    private int alertDestinationId;

    @NotNull
    private String alertDestinationName;

    private List<String> emailFrom;
    private List<String> emailTo;
    private List<String> emailCc;
    private List<String> emailBcc;

    @NotNull
    private String hostName;

    @NotNull
    private int port;

    private String transport;
    private String authUserName;
    private String authPassword;



    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getAlertDestinationId() {
        return alertDestinationId;
    }

    public void setAlertDestinationId(int alertDestinationId) {
        this.alertDestinationId = alertDestinationId;
    }

    public String getAlertDestinationName() {
        return alertDestinationName;
    }

    public void setAlertDestinationName(String alertDestinationName) {
        this.alertDestinationName = alertDestinationName;
    }

    public List<String> getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(List<String> emailFrom) {
        this.emailFrom = emailFrom;
    }

    public List<String> getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(List<String> emailTo) {
        this.emailTo = emailTo;
    }

    public List<String> getEmailCc() {
        return emailCc;
    }

    public void setEmailCc(List<String> emailCc) {
        this.emailCc = emailCc;
    }

    public List<String> getEmailBcc() {
        return emailBcc;
    }

    public void setEmailBcc(List<String> emailBcc) {
        this.emailBcc = emailBcc;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }
}
