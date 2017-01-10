package com.securityx.modelfeature;

import com.securityx.modelfeature.config.FeatureServiceConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

public class ImpalaClient {

    public static Connection getImpalaConnection(FeatureServiceConfiguration conf) throws Exception {
        Class.forName(conf.getImpalaConfiguration().getJdbcDriver());
        String connectionURL = String.format(conf.getImpalaConfiguration().getConnectionUrl(),
                conf.getImpalaConfiguration().getHost(),
                conf.getImpalaConfiguration().getPort());
        return DriverManager.getConnection(connectionURL);
    }
}
