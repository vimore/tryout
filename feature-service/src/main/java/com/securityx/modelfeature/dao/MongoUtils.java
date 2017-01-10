package com.securityx.modelfeature.dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;

public class MongoUtils {

    public static MongoClient getClient(final FeatureServiceConfiguration conf) {
        String mongoServer = conf.getMongoDB().getMongoServer();
        int mongoPort = conf.getMongoDB().getMongoPort();

        // Adding timeout options, based on these pages:
        // http://api.mongodb.com/java/2.4/com/mongodb/MongoOptions.html
        // http://blog.mlab.com/2013/10/do-you-want-a-timeout/
        // http://www.programcreek.com/java-api-examples/index.php?api=com.mongodb.MongoClientOptions

        // We want any timeout that might happen to happen quickly, so that any api call that needs suppression info
        // either gets it quickly or no suppression happens.
        ServerAddress addr = new ServerAddress(mongoServer, mongoPort);
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        // Max wait time to get a connection from the connection pool.
        builder.maxWaitTime(conf.getMongoDB().getMaxWaitTime());
        // Connection timeout is the time in milliseconds to wait when trying to get a socket connection to
        // the mongo server.
        builder.connectTimeout(conf.getMongoDB().getConnectionTimeout());
        // Socket timeout is supposedly the amount of time we wait for results from a query. We don't want this
        // to be too high, but high enough that we won't timeout if there's a lot of data to return.
        builder.socketTimeout(conf.getMongoDB().getSocketTimeout());
        builder.serverSelectionTimeout(conf.getMongoDB().getServerSelectionTimeout());
        MongoClientOptions options = builder.build();
        return new MongoClient(addr, options);
    }

    public static MongoDatabase getDatabase(String databaseName, final FeatureServiceConfiguration conf) {
        MongoClient client = conf.getMongoDB().getClient();
        return (client!=null) ? client.getDatabase(databaseName) : null;
    }
}
