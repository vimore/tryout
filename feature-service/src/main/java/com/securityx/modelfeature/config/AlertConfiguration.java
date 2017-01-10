package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class AlertConfiguration {

    private List<NameIdPair> alertDestinations = new LinkedList<NameIdPair>();

    private CefConfiguration cefConfig = new CefConfiguration();

    private EmailSenderConfiguration emailSenderConfiguration = new EmailSenderConfiguration();

    private AlertSchedulerConfiguration alertSchedulerConfiguration = new AlertSchedulerConfiguration();

    public List<NameIdPair> getAlertDestinations() {
        return alertDestinations;
    }

    public void setAlertDestinations(List<NameIdPair> alertDestinations) {
        this.alertDestinations = alertDestinations;
    }

    public CefConfiguration getCefConfig() {
        return cefConfig;
    }

    public void setCefConfig(CefConfiguration cefConfig) {
        this.cefConfig = cefConfig;
    }

    public EmailSenderConfiguration getEmailSenderConfiguration() {
        return emailSenderConfiguration;
    }

    public void setEmailSenderConfiguration(EmailSenderConfiguration emailSenderConfiguration) {
        this.emailSenderConfiguration = emailSenderConfiguration;
    }

    public AlertSchedulerConfiguration getAlertSchedulerConfiguration() {
        return alertSchedulerConfiguration;
    }

    public void setAlertSchedulerConfiguration(AlertSchedulerConfiguration alertSchedulerConfiguration) {
        this.alertSchedulerConfiguration = alertSchedulerConfiguration;
    }

    public static class CefConfiguration{

        @JsonProperty
        private String cefVersion;
        @JsonProperty
        private String deviceVersion;
        @JsonProperty
        private String vendor;
        @JsonProperty
        private String product;

        private static String dvcHost;
        static {
            try {
                dvcHost = java.net.InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public String getCefVersion() {
            return cefVersion;
        }
        public String getDvcHost() {
            return dvcHost;
        }


        public void setCefVersion(String cefVersion) {
            this.cefVersion = cefVersion;
        }

        public String getDeviceVersion() {
            return deviceVersion;
        }

        public void setDeviceVersion(String deviceVersion) {
            this.deviceVersion = deviceVersion;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }
    }


    /**
     * Email Config
     */
    public static class EmailSenderConfiguration{
        private String emailTemplateFilePath;
        private boolean oneEmailPerEntity;

        public String getEmailTemplateFilePath() {
            return emailTemplateFilePath;
        }

        public void setEmailTemplateFilePath(String emailTemplateFilePath) {
            this.emailTemplateFilePath = emailTemplateFilePath;
        }

        public boolean isOneEmailPerEntity() {
            return oneEmailPerEntity;
        }

        public void setOneEmailPerEntity(boolean oneEmailPerEntity) {
            this.oneEmailPerEntity = oneEmailPerEntity;
        }
    }

    /**
     * Alert scheduler Config
     */
    public static class AlertSchedulerConfiguration{
        private boolean overrideUser;
        private String schedule;
        private int queryDuration;

        public boolean isOverrideUser() {
            return overrideUser;
        }

        public void setOverrideUser(boolean overrideUser) {
            this.overrideUser = overrideUser;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public int getQueryDuration() {
            return queryDuration;
        }

        public void setQueryDuration(int queryDuration) {
            this.queryDuration = queryDuration;
        }
    }
}
