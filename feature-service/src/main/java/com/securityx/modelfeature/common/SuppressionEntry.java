package com.securityx.modelfeature.common;

import com.google.common.base.Objects;
import org.bson.types.ObjectId;

public class SuppressionEntry {

    // Allowed values for suppress field, indicates what kind of suppression to do
    public static final String SUPPRESS_ENTITY = "entity";
    public static final String SUPPRESS_BEHAVIOR = "behavior";
    public static final String SUPPRESS_ENTITY_AND_BEHAVIOR = "entbeh";

    // Allowed values for type fields
    public static final String IP = "ip";
    public static final String HOST = "host";
    public static final String USER = "user";


    ObjectId objectId;
    String behavior;
    String category;
    String entity;
    String suppress;
    String type;

    public SuppressionEntry(ObjectId objectId, String behavior, String category, String entity, String suppress, String type) {
        this.objectId = objectId;
        this.behavior = behavior;
        this.category = category;
        this.entity = entity;
        this.suppress = suppress;
        this.type = type;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getSuppress() {
        return suppress;
    }

    public void setSuppress(String suppress) {
        this.suppress = suppress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return Objects.toStringHelper(this)
                .add("objectId", objectId)
                .add("behavior", behavior)
                .add("category", category)
                .add("entity", entity)
                .add("suppress", suppress)
                .add("type", type)
                .toString();
    }
}
