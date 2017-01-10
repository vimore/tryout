package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by harish on 1/23/15.
 */
public class EventTypeConfiguration{

    @Valid
    @NotNull
    @JsonProperty
    private List<NameIdPair> models = new LinkedList<NameIdPair>();

    @Valid
    @NotNull
    @JsonProperty
    private List<NameIdPair> killchain = Lists.newLinkedList();

    @Valid
    @NotNull
    @JsonProperty
    private List<NameIdPair> entityCards = Lists.newLinkedList();

    @Valid
    @NotNull
    @JsonProperty
    private List<NameIdPair> queryOperators = Lists.newLinkedList();

    @Valid
    @NotNull
    @JsonProperty
    private List<FilterField> filterFields = Lists.newLinkedList();

    @Valid
    @NotNull
    @JsonProperty
    private List<C2ModelConfiguration> c2ModelConfig = Lists.newLinkedList();


    @Valid
    @NotNull
    @JsonProperty
    public List<SecurityEventTypeConfiguration> securityEventTypes = new LinkedList<SecurityEventTypeConfiguration>();

    public List<NameIdPair> getModels() {
        return models;
    }

    public void setModels(final List<NameIdPair> models) {
        this.models = models;
    }

    public List<NameIdPair> getKillchain() {
        return killchain;
    }

    public void setKillchain(final List<NameIdPair> killchain) {
        this.killchain = killchain;
    }

    public List<NameIdPair> getEntityCards() {
        return entityCards;
    }

    public void setEntityCards(List<NameIdPair> entityCards) {
        this.entityCards = entityCards;
    }

    public List<SecurityEventTypeConfiguration>  getSecurityEventTypes() {
        return securityEventTypes;
    }

    public void setSecurityEventTypes(final List<SecurityEventTypeConfiguration> securityEventTypes) {
        this.securityEventTypes = securityEventTypes;
    }

    public List<NameIdPair> getQueryOperators() {
        return queryOperators;
    }

    public void setQueryOperators(final List<NameIdPair> queryOperators) {
        this.queryOperators = queryOperators;
    }

    public List<FilterField> getFilterFields() {
        return filterFields;
    }

    public void setFilterFields(final List<FilterField> filterFields) {
        this.filterFields = filterFields;
    }

    public List<C2ModelConfiguration> getC2ModelConfig() {
        return c2ModelConfig;
    }

    public void setC2ModelConfig(List<C2ModelConfiguration> c2ModelConfig) {
        this.c2ModelConfig = c2ModelConfig;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("models", models)
                .add("killchain", killchain)
                .add("entityCards", entityCards)
                .add("queryOperators", queryOperators)
                .add("filterFields", filterFields)
                .add("securityEventTypes", securityEventTypes)
                .toString();
    }
}