package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import java.util
import javax.validation.{Validation, Validator}

import com.e8.sparkle.commons.hbase.HBaseClient
import com.e8.sparkle.storage.hbase.rowobjects.{EntityRelationship, ColumnProperties, Entity, BaseRow}
import com.e8.sparkle.storage.hbase.tableobjects.EntityTable
import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{EntityFusionConfiguration, FeatureServiceConfiguration}
import com.securityx.modelfeature.dao.{PeerGroupDao, PhoenixUtils}
import com.securityx.modelfeature.utils.{PeerGroup, TaniumStats, EntityThreat, HBaseAccessConfiguration}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable.ListBuffer

object PeerGroupDaoTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = _
  private var featureServiceCache: FeatureServiceCache = _
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  private val hBaseClient: HBaseClient = new HBaseClient


  // Note! Every class that extends HBaseTestBase must implement a BeforeClass method, and that method must call
  // HBaseTestBase.HBaseSetup().  That call will start the local HBase server if it has not already been started,
  // which will allow this method to then continue with setup.
  // We expect HBase to have no tables in it when the test starts. It is up to the test to create any tables it needs
  // and insert any data. At the end of the test, each test must drop all tables so that any other tests that run
  // will start with a clean slate.
  @BeforeClass
  def startServers() {
    HBaseTestBase.setupHBase()

    val confFile: String = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"
    configuration = new ConfigurationFactory[FeatureServiceConfiguration](classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))
    configuration.setZkQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort)
    configuration.setSolrQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort + "/solr")
    configuration.getMongoDB.setMongoServer(MongoTestBase.serverName)
    configuration.getMongoDB.setMongoPort(MongoTestBase.mongoPort)
    // Set up mongo server connection
    MongoTestBase.setupMongo(configuration, true)
    featureServiceCache = new FeatureServiceCache(configuration)

    // Create HBase access objects
    val hBaseConfig = new HBaseAccessConfiguration("127.0.0.1", "127.0.0.1", HBaseTestBase.utility.getZkCluster.getClientPort.toString, configuration.getPhoenix.getSchema)
    val hBaseConn = hBaseClient.getConnection(hBaseConfig)
    val hBaseAdmin = hBaseConn.getAdmin

    // Create entity tables via HBase access
    val entityTable = new EntityTable
    entityTable.createTableIfNotExists(hBaseConn, hBaseConfig)
    val entity1 = createHostEntity("2016-12-04T00:00:00.000Z", "192.168.1.13", "54-0C-75-00-E2-13", "laptop813", null, "c3c46ec3ab47d5ee70e3f60283015a24")
    val entity2 = createHostEntity("2016-12-04T00:00:00.000Z", "192.168.1.31", "54-0C-75-00-E2-31", "laptop831", null, "0cf37ad4ff7f99b91945f4af0902da83")
    val entity3 = createHostEntity("2016-12-04T00:00:00.000Z", "192.168.1.11", "54-0C-75-00-E2-11", "laptop811", null, "51bc218c5231af67be4db41b362833a2")
    entity3.setIpAddresses(genPropertiesList("1.1.1.1", "2016-12-04T00:00:00.000Z", "source1", "2.2.2.2", "2016-12-04T10:00:00.000Z", "source2"))
    val entities: Array[BaseRow] = Array(entity1, entity2, entity3)
    entityTable.putRows(hBaseConn, entities, false, hBaseConfig)

    val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
    val entityThreatSql: String = "CREATE TABLE IF NOT EXISTS " + PeerGroup.getName(configuration) +  " (\n" +
      "     DATE_TIME VARCHAR NOT NULL,\n" +
      "     ENTITY_ID VARCHAR NOT NULL,\n" +
      "     PEER_ID INTEGER NOT NULL,\n " +
      "     PEER_TYPE INTEGER NOT NULL,\n" +
      "     GROUP_TYPE VARCHAR,\n" +
      "     PEER_TOTAL INTEGER,\n" +
      "     ANOMALY_SCORE DOUBLE,\n" +
      "     PEER_USERS VARCHAR,\n" +
      "     PEER_TOP_FEATURES VARCHAR,\n" +
      "     PEER_TOP_FEATURES_DESC VARCHAR,\n" +
      "     PEER_POSITION VARCHAR,\n" +
      "     FEATURE_SCORES VARCHAR,\n" +
      "     FEATURE_SCORES_NORM VARCHAR,\n" +
      "     FEATURE_SCORES_DESC VARCHAR,\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, ENTITY_ID, PEER_ID, PEER_TYPE )" +
      ")"
    val createStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
    createStmt.execute

    var upsertSql: String = "upsert into " + PeerGroup.getName(configuration) + " (DATE_TIME, ENTITY_ID, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, " +
      "PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
      "values ('2016-12-04T00:00:00.000Z', 'c3c46ec3ab47d5ee70e3f60283015a24', 204002, 3, 'AD peer group', 1, 0.5767094071178456, '[8]', '[Anomalous User Activity]', " +
      "'[1, 3]', '[[8, 17.0], [14, 0.0]]', '[[8, 17.0], [14, 0.0]]', " +
      "'[[\\'Many successful Kerberos TGT requests\\', 19], [\\'Successfully logged on from many source IPs\\', 6]]')"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerGroup.getName(configuration) + " (DATE_TIME, ENTITY_ID, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, " +
      "PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
      "values ('2016-12-04T00:00:00.000Z', '0cf37ad4ff7f99b91945f4af0902da83', 101001, 2, 'peergroup', 1, 0.3067094071178456, '[10]', '[Anomalous User Agent]', " +
      "'[1, 3]', '[[8, 17.0], [14, 0.0]]', '[[8, 17.0], [14, 0.0]]', " +
      "'[[\\'Many successful Kerberos TGT requests\\', 19], [\\'Successfully logged on from many source IPs\\', 6]]')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerGroup.getName(configuration) + " (DATE_TIME, ENTITY_ID, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, " +
      "PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
      "values ('2016-12-04T00:00:00.000Z', '51bc218c5231af67be4db41b362833a2', 102001, 2, 'peergroup', 1, 0.44885525239894464, '[6]', '[High Uploads]', '[0, 1]', " +
      "'[[6, 44982.0]]', '[[6, 44982.0]]', '[[\\'high connections with nonstandard requestMethods\\', 8], [\\'high traffic with anomalous useragents\\', 10]]')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
  }

  def createUserEntity(fusionTime: String, ip: String, mac: String, host: String, user: String, entityId: String): Entity = {
    val fusionDate = EntityFusionConfiguration.convertDate(fusionTime)
    val e: Entity = new Entity(entityId, fusionDate.getTime,
      new ColumnProperties(user, fusionTime, "source"),
      new ColumnProperties(ip, fusionTime, "source"),
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    e.setMacAddress(mac)
    e.setHostName(host)
    e
  }

  def createHostEntity(fusionTime: String, ip: String, mac: String, host: String, user: String, entityId: String): Entity = {
    val fusionDate = EntityFusionConfiguration.convertDate(fusionTime)
    val e: Entity = new Entity(entityId, fusionDate.getTime,
      new ColumnProperties(ip, fusionTime, "source"),
      new ColumnProperties(host, fusionTime, "source"),
      new ColumnProperties(mac, fusionTime, "source"),
      null,
      null,
      null,
      null
    )
    e
  }

  def createEntityRelationship(date: String, entity: String, relatedEntity: String, entityType: String): EntityRelationship = {
    val dateTime = EntityFusionConfiguration.convertDate(date)
    val er: EntityRelationship = new EntityRelationship(entity, entityType, relatedEntity, dateTime.getTime)
    er
  }

  protected def genPropertiesList(val1: String, ts1: String, source1: String, val2: String, ts2: String, source2: String): util.ArrayList[ColumnProperties] = {
    val result = new util.ArrayList[ColumnProperties]
    result.add(new ColumnProperties(val1, ts1, source1))
    result.add(new ColumnProperties(val2, ts2, source2))
    result
  }

  // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call HBaseTestBase.HBaseTeardown().  See comments
  // in HBaseTestBase for details.
  // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
  @AfterClass
  def stopServers() {
    try {
      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
      val entityThreatSql: String = "DROP TABLE IF EXISTS " + EntityThreat.getName(configuration)
      val entityThreatStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
      entityThreatStmt.execute
      val taniumStatsSql: String = "DROP TABLE IF EXISTS " + TaniumStats.getName(configuration)
      val taniumStatsStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
      taniumStatsStmt.execute
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
    MongoTestBase.teardownMongo()
    HBaseTestBase.teardownHBase()
  }
}

class PeerGroupDaoTest {
  private val peerGroupDao = new PeerGroupDao(PeerGroupDaoTest.configuration)

  @Test
  def getPeerGroupAnomaliesByModelIdTest() = {
    val anomaliesList = peerGroupDao.getPeerGroupAnomaliesByModelId("2016-12-04T00:00:00.000Z", "2016-12-05T00:00:00.000Z", 2, 10, PeerGroupDaoTest.featureServiceCache)
    assertEquals(2, anomaliesList.size)
    for (anomaly <- anomaliesList) {
      val entityId = anomaly.getOrElse("entityId", "")
      val peerId = anomaly.getOrElse("peerId", -1)
      val securityEventId = anomaly.getOrElse("securityEventId", -1)
      val featureId = anomaly.getOrElse("featureId", -1)
      val entityIps = anomaly.getOrElse("entityIps", null).asInstanceOf[ListBuffer[String]]
      val entityHostNames = anomaly.getOrElse("entityHostNames", null).asInstanceOf[ListBuffer[String]]
      if (entityId == "51bc218c5231af67be4db41b362833a2") {
        assertEquals(102001, peerId)
        assertEquals(6, securityEventId)
        assertEquals(6, featureId)
        assertEquals(2, entityIps.size)
        assertTrue(entityIps.contains("1.1.1.1"))
        assertTrue(entityIps.contains("2.2.2.2"))
        assertEquals(1, entityHostNames.size)
        assertTrue(entityHostNames.contains("laptop811"))
      } else if (entityId == "0cf37ad4ff7f99b91945f4af0902da83") {
        assertEquals(101001, peerId)
        assertEquals(10, securityEventId)
        assertEquals(10, featureId)
        assertEquals(1, entityIps.size)
        assertTrue(entityIps.contains("192.168.1.31"))
        assertEquals(1, entityHostNames.size)
        assertTrue(entityHostNames.contains("laptop831"))
      } else {
        assertTrue("Got unexpected entity id [" + entityId + "]", false)
      }
    }
  }
}
