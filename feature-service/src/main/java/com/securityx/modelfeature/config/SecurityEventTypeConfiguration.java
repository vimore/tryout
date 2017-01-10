package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Created by harish on 1/23/15.
 */

public class SecurityEventTypeConfiguration {

    @JsonProperty
    private int securityEventTypeId;
    @JsonProperty
    private int killchainId;
    @JsonProperty
    private String featureLabel;
    @JsonProperty
    private String typePrefix;
    @JsonProperty
    private String eventType;
    @JsonProperty
    private String eventDescription;
    @JsonProperty
    private String shortDescription;
    @JsonProperty
    private int model;
    @JsonProperty
    private int cardId;

    SecurityEventTypeConfiguration(){

    }

    public SecurityEventTypeConfiguration(int securityEventTypeId, int killchainId, String featureLabel, String typePrefix, String eventType, String eventDescription,
                                          String shortDescription, int model, int cardId) {
        this.securityEventTypeId = securityEventTypeId;
        this.killchainId = killchainId;
        this.featureLabel = featureLabel;
        this.typePrefix = typePrefix;
        this.eventType = eventType;
        this.eventDescription = eventDescription;
        this.shortDescription = shortDescription;
        this.model = model;
        this.cardId = cardId;
    }

    public int getSecurityEventTypeId() {
        return securityEventTypeId;
    }

    public void setSecurityEventTypeId(int securityEventTypeId) {
        this.securityEventTypeId = securityEventTypeId;
    }

    public int getKillchainId() {
        return killchainId;
    }

    public void setKillchainId(int killchainId) {
        this.killchainId = killchainId;
    }

    public String getFeatureLabel() {
        return featureLabel;
    }

    public void setFeatureLabel(String featureLabel) {
        this.featureLabel = featureLabel;
    }

    public String getTypePrefix() {
        return typePrefix;
    }

    public void setTypePrefix(String typePrefix) {
        this.typePrefix = typePrefix;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("securityEventTypeId", securityEventTypeId)
                .add("killchainId", killchainId)
                .add("featureLabel", featureLabel)
                .add("typePrefix", typePrefix)
                .add("eventType", eventType)
                .add("eventDescription", eventDescription)
                .add("shortDescription", shortDescription)
                .add("model", model)
                .add("cardId", cardId)
                .toString();
    }
}