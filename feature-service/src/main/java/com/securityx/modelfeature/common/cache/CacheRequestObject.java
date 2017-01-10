package com.securityx.modelfeature.common.cache;

import com.securityx.modelfeature.dao.BaseDao;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by harish on 12/16/15.
 */
public class CacheRequestObject {

    String featureName;
    String queryString;
    BaseDao baseDao;
    Connection connection;
    PreparedStatement preparedStatement;

    public CacheRequestObject(String featureName, String queryString, BaseDao baseDao, Connection connection, PreparedStatement preparedStatement) {
        this.featureName = featureName;
        this.queryString = queryString;
        this.baseDao = baseDao;
        this.connection = connection;
        this.preparedStatement = preparedStatement;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CacheRequestObject that = (CacheRequestObject) o;

        return new EqualsBuilder()
                .append(featureName, that.featureName)
                .append(queryString, that.queryString)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(featureName)
                .append(queryString)
                .toHashCode();
    }
}
