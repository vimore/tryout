package com.e8.test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import de.flapdoodle.embed.mongo.Command;
import com.securityx.modelfeature.dao.MongoUtils;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MongoTestBase extends HBaseTestBase {
    public static final Integer mongoPort = 12345;
    public static final String serverName = "localhost";
    protected static MongodExecutable mongodExecutable = null;
    protected static MongodProcess mongodProcess = null;

    public static void setupMongo(FeatureServiceConfiguration conf, Boolean initialize) throws IOException {
        Logger logger = LoggerFactory.getLogger(MongoTestBase.class.getName());

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaultsWithLogger(Command.MongoD, logger)
                .build();

        MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(mongoPort, Network.localhostIsIPv6()))
                .build();

        mongodExecutable = starter.prepare(mongodConfig);
        mongodProcess = mongodExecutable.start();

        // update the configuration to have the right stuff for creating the Mongo client
        conf.getMongoDB().setMongoServer(serverName);
        conf.getMongoDB().setMongoPort(mongoPort);

        if (initialize) {
            MongoClient client = new MongoClient(serverName, mongoPort);
            MongoDatabase local = client.getDatabase("local");
            MongoDatabase accounts = client.getDatabase("accounts");
            accounts.createCollection("system.indexes");
            accounts.createCollection("users");
            accounts.createCollection("userhistories");
            accounts.createCollection("whitelistitems");
            conf.getMongoDB().setClient(client);
        }

        if (conf.getMongoDB().getClient() == null) {
            MongoClient client = MongoUtils.getClient(conf);
            conf.getMongoDB().setClient(client);
        }
    }

    public static void teardownMongo() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }
}
