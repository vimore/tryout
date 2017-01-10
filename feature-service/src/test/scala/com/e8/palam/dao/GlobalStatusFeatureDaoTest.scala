package com.e8.palam.dao

import java.io.File
import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}
import javax.validation.{Validation, Validator}
import com.securityx.modelfeature.utils.{NewlyObserved, GlobalFeatures, EntityThreat}

import scala.collection.mutable.{Map => MutableMap}
import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{GlobalStatusDao, PhoenixUtils}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.{Before, BeforeClass}
import org.junit.Assert._
import org.junit._
import org.slf4j.{Logger, LoggerFactory}

object GlobalStatusFeatureDaoTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[GlobalStatusFeatureDaoTest])

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
    configuration.setZkQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort)
    configuration.setSuppressStatisticsForModels("")
    // Set up mongo server connection, though this test will not put anything in it.
    MongoTestBase.setupMongo(configuration, true)
    featureServiceCache = new FeatureServiceCache(configuration)

    val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
    val entityThreatSql: String = "CREATE TABLE IF NOT EXISTS " + EntityThreat.getName(configuration) + " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    IP_ADDRESS VARCHAR NOT NULL,\n" +
      "    MAC_ADDRESS VARCHAR NOT NULL,\n" +
      "    HOST_NAME VARCHAR NOT NULL,\n" +
      "    USER_NAME VARCHAR NOT NULL,\n" +
      "    MODEL_ID INTEGER NOT NULL,\n" +
      "    SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
      "    RISK_SCORE DOUBLE,\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID )\n" +
      ") IMMUTABLE_ROWS=true"
    val createStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
    createStmt.execute

    // Some data with low risk scores
    var upsertSql: String = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.4.57', '10:02:B5:D9:E3:60', 'DESKTOP-9UIVQ3V', ' ', 2, 11, 0.16843)"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.4.57', '10:02:B5:D9:E3:60', 'DESKTOP-9UIVQ3V', ' ', 2, 17, 0.15631)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.4.64', 'A4:5E:60:DD:AF:F9', 'MacBook-Pro', ' ', 2, 7, 0.07478)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '192.168.12.12', 'C4:8E:8F:F8:B5:21', 'LAPTOP-QKGG9QVJ', ' ', 2, 7, 0.57479)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // Some data with higher (above 7) risk scores
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.4.115', '1C:A2:39:DE:E3:70', 'DESKTOP-DFF1IKKJ', ' ', 2, 14, 0.76843)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '192.168.12.19', '12:02:B7:AA:E3:60', 'DESKTOP-9UIVQ3V', ' ', 2, 17, 0.95732)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '192.168.12.101', 'A4:56:6F:DD:A1:F9', 'MacBook-Pro', ' ', 2, 7, 0.76120)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.4.74', 'C4:8E:93:00:B5:31', 'LAPTOP-KHS34H', ' ', 2, 7, 0.84309)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // Some entity threat data for models 9 and 10 (which we will test for exclusion)
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.1.153', '13:78:88:BE:f5:39', 'kmchale-MBP', ' ', 9, 6, 0.03653)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.1.74', 'A3:88:83:0E:f5:41', 'mjordan-MBP', ' ', 9, 6, 0.10234)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-05-26T00:00:00.000Z', '10.10.1.9', '32:4B:8F:3E:75:C9', 'djohnson-MBP', ' ', 10, 6, 0.74331)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    val globalFeaturesSql: String = "CREATE TABLE IF NOT EXISTS " + GlobalFeatures.getName(configuration) + " (\n" +
      "        DATE_TIME VARCHAR NOT NULL,\n" +
      "        FIELD_NAME VARCHAR NOT NULL,\n" +
      "        FIELD_VALUE VARCHAR NOT NULL\n" +
      "    CONSTRAINT PK PRIMARY KEY ( DATE_TIME, FIELD_NAME, FIELD_VALUE )\n    )"
    val globalFeaturesStmt: PreparedStatement = conn.prepareStatement(globalFeaturesSql)
    globalFeaturesStmt.execute

    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'destinationUserName', 'andre_brown')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'destinationUserName', 'boris_pasternak')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'destinationUserName', 'carson_palmer')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'destinationUserName', 'charles_clay')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'destinationUserName', 'devin_hester')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'hostName', '192.168.1.150')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'hostName', '192.168.1.151')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'hostName', '192.168.1.152')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'hostName', '192.168.1.153')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'hostName', '192.168.1.154')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + GlobalFeatures.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE) values ('2016-05-26T00:00:00.000Z', 'hostName', '192.168.1.155')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    val newlyObservedSql: String = "CREATE TABLE IF NOT EXISTS " + NewlyObserved.getName(configuration) + " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    FIELD_NAME VARCHAR,\n" +
      "    FIELD_VALUE VARCHAR,\n" +
      "    COUNT INTEGER\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, FIELD_NAME, FIELD_VALUE )\n" +
      ") IMMUTABLE_ROWS=true"
    val newlyObservedStmt: PreparedStatement = conn.prepareStatement(newlyObservedSql)
    newlyObservedStmt.execute

    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationDnsDomain', 'avantgo.net', 1)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationDnsDomain', 'docsearls.com', 1)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationDnsDomain', 'cs.uiuc.edu', 1)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationNameOrIp', '216.7.87.108', 1)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationNameOrIp', 'garage.docsearls.com', 1)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationNameOrIp', 'voiploop.com', 2)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + NewlyObserved.getName(configuration) + " (DATE_TIME, FIELD_NAME, FIELD_VALUE, COUNT) values ('2016-05-26T00:00:00.000Z', 'destinationNameOrIp', '9p.org.uk', 4)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
  }

  // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call HBaseTestBase.HBaseTeardown().  See comments
  // in HBaseTestBase for details.
  // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
  @AfterClass
  def stopServers() {
    try{
      MongoTestBase.teardownMongo()
    }
    catch {
      case ex: Exception => Logger.error(ex.getMessage, ex)
    }
    try {
      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
      val entityThreatSql: String = "DROP TABLE IF EXISTS " + EntityThreat.getName(configuration)
      val entityThreatStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
      entityThreatStmt.execute
      val globalFeaturesSql: String = "DROP TABLE IF EXISTS " + GlobalFeatures.getName(configuration)
      val globalFeaturesStmt: PreparedStatement = conn.prepareStatement(globalFeaturesSql)
      globalFeaturesStmt.execute
      val newlyObservedSql: String = "DROP TABLE IF EXISTS " + NewlyObserved.getName(configuration)
      val newlyObservedStmt: PreparedStatement = conn.prepareStatement(newlyObservedSql)
      newlyObservedStmt.execute
    }
    catch {
      case ex: Exception =>
        Logger.error(ex.getMessage, ex)
        throw ex
    }finally {
      HBaseTestBase.teardownHBase()
    }
  }
}


class GlobalStatusFeatureDaoTest {
  var globalStatusDao: GlobalStatusDao = new GlobalStatusDao(GlobalStatusFeatureDaoTest.configuration)

  @Test
  def getStatisticsTest() = {
    val stats = globalStatusDao.getStatistics("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", GlobalStatusFeatureDaoTest.featureServiceCache)
    val threatsMap = stats.getOrElse("threats", null).asInstanceOf[MutableMap[String, Any]]
    val totalthreats = threatsMap.getOrElse("total", -1).asInstanceOf[Int]
    assertEquals(5, totalthreats)
    val anomaliesMap = stats.getOrElse("anomalies", null).asInstanceOf[MutableMap[String, Any]]
    val totalAnomalies = anomaliesMap.getOrElse("total", -1).asInstanceOf[Int]
    assertEquals(1, totalAnomalies)
    val hostsMap = stats.getOrElse("hosts", null).asInstanceOf[MutableMap[String, Any]]
    val totalHosts = hostsMap.getOrElse("total", null).asInstanceOf[Some[Int]]
    assertEquals(6, totalHosts.get)
    val usersMap = stats.getOrElse("users", null).asInstanceOf[MutableMap[String, Any]]
    val totalUsers = usersMap.getOrElse("total", null).asInstanceOf[Some[Int]]
    assertEquals(5, totalUsers.get)

    GlobalStatusFeatureDaoTest.configuration.setSuppressStatisticsForModels("9,10")
    val statsExclude = globalStatusDao.getStatistics("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", GlobalStatusFeatureDaoTest.featureServiceCache)
    val threatsMapExclude = statsExclude.getOrElse("threats", null).asInstanceOf[MutableMap[String, Any]]
    val totalthreatsExclude = threatsMapExclude.getOrElse("total", -1).asInstanceOf[Int]
    assertEquals(4, totalthreatsExclude)
    val anomaliesMapExclude = statsExclude.getOrElse("anomalies", null).asInstanceOf[MutableMap[String, Any]]
    val totalAnomaliesExclude = anomaliesMapExclude.getOrElse("total", -1).asInstanceOf[Int]
    assertEquals(1, totalAnomaliesExclude)
    val hostsMapExclude = statsExclude.getOrElse("hosts", null).asInstanceOf[MutableMap[String, Any]]
    val totalHostsExclude = hostsMapExclude.getOrElse("total", null).asInstanceOf[Some[Int]]
    assertEquals(6, totalHostsExclude.get)
    val usersMapExclude = statsExclude.getOrElse("users", null).asInstanceOf[MutableMap[String, Any]]
    val totalUsersExclude = usersMapExclude.getOrElse("total", null).asInstanceOf[Some[Int]]
    assertEquals(5, totalUsersExclude.get)
  }
}
