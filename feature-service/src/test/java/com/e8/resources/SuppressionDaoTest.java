package com.e8.resources;

import com.e8.test.MongoTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.securityx.modelfeature.common.SuppressionEntry;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.MongoUtils;
import com.securityx.modelfeature.dao.SuppressionDao;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
//import static org.junit.Assert.assertNotNull;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class SuppressionDaoTest extends MongoTestBase {
    private static FeatureServiceConfiguration conf = null;
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static SuppressionDao suppressionDao = null;

    // Note! Every class that extends HBaseTestBase (or MongoTestBase) must implement a BeforeClass method, and that method must call
    // HBaseTestBase.HBaseSetup().  That call will start the local HBase server if it has not already been started,
    // which will allow this method to then continue with setup.
    // We expect HBase to have no tables in it when the test starts. It is up to the test to create any tables it needs
    // and insert any data. At the end of the test, each test must drop all tables so that any other tests that run
    // will start with a clean slate.
    @BeforeClass
    public static void startServers() throws IOException, ConfigurationException {
        String confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml";
        conf = new ConfigurationFactory<FeatureServiceConfiguration>(FeatureServiceConfiguration.class, validator, mapper, "dw").build(new File(confFile));
        MongoTestBase.setupMongo(conf, true);
        suppressionDao = new SuppressionDao(conf);

        // Insert suppression entries in Mongo
        MongoDatabase suppression = MongoUtils.getDatabase("accounts", conf);
        MongoCollection<Document> whitelist = suppression.getCollection("whitelistitems");
        Document suppressJyriaEntity = new Document();
        suppressJyriaEntity.put("suppress", "entity");
        suppressJyriaEntity.put("category", "ui");
        suppressJyriaEntity.put("type", "user");
        suppressJyriaEntity.put("entity", "SRV-jyria");
        suppressJyriaEntity.put("behavior", "Anomalous User Activity");
        whitelist.insertOne(suppressJyriaEntity);
        Document suppressEntBeh = new Document();
        suppressEntBeh.put("suppress", "entbeh");
        suppressEntBeh.put("category", "ui");
        suppressEntBeh.put("type", "ip");
        suppressEntBeh.put("entity", "192.168.12.18");
        suppressEntBeh.put("behavior", "Exfiltration");
        whitelist.insertOne(suppressEntBeh);
        Document suppressEntIp = new Document();
        suppressEntIp.put("suppress", "entity");
        suppressEntIp.put("category", "ui");
        suppressEntIp.put("type", "ip");
        suppressEntIp.put("entity", "192.1.12.255");
        suppressEntIp.put("behavior", "Exfiltration");
        whitelist.insertOne(suppressEntIp);
        Document suppressBehavior = new Document();
        suppressBehavior.put("suppress", "behavior");
        suppressBehavior.put("category", "ui");
        suppressBehavior.put("type", "ip");
        suppressBehavior.put("entity", "192.1.12.255");
        suppressBehavior.put("behavior", "New/Uncategorized Destinations");
        whitelist.insertOne(suppressBehavior);
    }

    // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call
    // HBaseTestBase.HBaseTeardown().  See comments in HBaseTestBase for details.
    // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
    @AfterClass
    public static void stopServers() {
        MongoTestBase.teardownMongo();
    }

    @Test
    public void getSuppressionListTest() {
        List<SuppressionEntry> suppressionEntries = suppressionDao.getSuppressionList();
        assertEquals(4, suppressionEntries.size());
        for (SuppressionEntry se : suppressionEntries) {
            if (se.getSuppress().equals(SuppressionEntry.SUPPRESS_ENTITY)) {
                if (se.getType().equals(SuppressionEntry.USER)) {
                    assertEquals("SRV-jyria", se.getEntity());
                    assertEquals("Anomalous User Activity", se.getBehavior());
                } else if (se.getType().equals(SuppressionEntry.IP)) {
                    assertEquals("192.1.12.255", se.getEntity());
                    assertEquals("Exfiltration", se.getBehavior());
                } else {
                    assertTrue("Unexpected type [" + se.getType() + "]", false);
                }
            } else if (se.getSuppress().equals(SuppressionEntry.SUPPRESS_BEHAVIOR)) {
                assertEquals("ip", se.getType());
                assertEquals("192.1.12.255", se.getEntity());
                assertEquals("New/Uncategorized Destinations", se.getBehavior());
            } else if (se.getSuppress().equals(SuppressionEntry.SUPPRESS_ENTITY_AND_BEHAVIOR)) {
                assertEquals("ip", se.getType());
                assertEquals("192.168.12.18", se.getEntity());
                assertEquals("Exfiltration", se.getBehavior());
            } else {
                assertTrue("Unexpected behavior [" + se.getBehavior() + "]", false);
            }
        }
    }

    @Test
    public void failedConnectionTest() throws IOException, ConfigurationException {
        // We want to test getting a suppression list when we can't successfully get a connection to mongo
        String confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml";
        FeatureServiceConfiguration config = new ConfigurationFactory<FeatureServiceConfiguration>(FeatureServiceConfiguration.class, validator, mapper, "dw").build(new File(confFile));

        // Set the mongo port to an unused port
        config.getMongoDB().setMongoPort(27016);
        MongoClient client = MongoUtils.getClient(config);
        config.getMongoDB().setClient(client);
        long now = System.currentTimeMillis();
        SuppressionDao badPortDao = new SuppressionDao(config);
        List<SuppressionEntry> entries = badPortDao.getSuppressionList();
        long elapsedTime = System.currentTimeMillis() - now;
        assertNotNull(entries);
        assertEquals(0, entries.size());
        // It must take less than 10 seconds to get a response
        assertTrue("Mongo failure took longer than expected [" + elapsedTime + "] ms", elapsedTime < 10000);

        // set the mongo port to a port that mysql may be listening on
        config.getMongoDB().setMongoPort(3307);
        client = MongoUtils.getClient(config);
        config.getMongoDB().setClient(client);
        now = System.currentTimeMillis();
        badPortDao = new SuppressionDao(config);
        entries = badPortDao.getSuppressionList();
        elapsedTime = System.currentTimeMillis() - now;
        assertNotNull(entries);
        assertEquals(0, entries.size());
        // It must take less than 10 seconds to get a response
        assertTrue("Mongo failure took longer than expected [" + elapsedTime + "] ms", elapsedTime < 10000);
    }

    @Test
    public void connectionsTest() {
        List<SuppressionEntry> suppressionEntries = suppressionDao.getSuppressionList();
        int numConnections = suppressionDao.getMongoConnections();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        suppressionEntries = suppressionDao.getSuppressionList();
        numConnections = suppressionDao.getMongoConnections();
        assertTrue("Too many connections [" + numConnections + "]", numConnections < 15);
    }
}
