package com.securityx.modelfeature.common;

import com.securityx.modelfeature.config.SecurityEventTypeConfiguration;

/**
 * Represents the Event Information for an Entity
 *
 * Created by harish on 3/6/15.
 */
public class EntityModelInfo extends SecurityEventTypeConfiguration {

    private double risk;
    private String dateTime;

    public EntityModelInfo(int killchainId, int securityEventId, String featureLabel,
                           String typePrefix, String eventyType, String eventDescription, String shortDescription,
                           int modelId, int cardId, double risk, String dateTime) {
        super(killchainId, securityEventId, featureLabel, typePrefix, eventyType, eventDescription, shortDescription, modelId, cardId);
        this.risk = risk;
        this.dateTime = dateTime;
    }

    public double getRisk() {
        return risk;
    }

    public void setRisk(double risk) {
        this.risk = risk;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
