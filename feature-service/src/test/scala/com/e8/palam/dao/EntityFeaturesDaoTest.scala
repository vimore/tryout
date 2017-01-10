package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import javax.validation.{Validation, Validator}

import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{EntityFeaturesDao, PhoenixUtils}
import com.securityx.modelfeature.utils.EntityFeatures
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit.{AfterClass, BeforeClass, Test}

object EntityFeaturesDaoTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator

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
    // Set up mongo server connection
    MongoTestBase.setupMongo(configuration, true)
    featureServiceCache = new FeatureServiceCache(configuration)

    val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
    val enityFeaturesSql = "CREATE TABLE IF NOT EXISTS " + EntityFeatures.getName(configuration) + " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    MODEL_ID INTEGER NOT NULL,\n" +
      "    SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
      "    SOURCE_NAME_OR_IP VARCHAR,\n" +
      "    DESTINATION_NAME_OR_IP VARCHAR,\n" +
      "    FEATURE_LABEL VARCHAR,\n" +
      "    FEATURE_VALUE INTEGER,\n" +
      "    DATA_SOURCE VARCHAR,\n" +
      "    GRANULARITY VARCHAR,\n" +
      "    CONSTRAINT PK PRIMARY KEY ( DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP )\n" +
      ") IMMUTABLE_ROWS=true"
    val enityFeaturesStmt: PreparedStatement = conn.prepareStatement(enityFeaturesSql)
    enityFeaturesStmt.execute

    // Some model 4 data
    var upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 0, '10.10.4.102', '', 'high connections', 8, 'web_proxy_mef', 'hourly')"
    var upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 0, '10.10.4.103', '', 'high connections', 1, 'web_proxy_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 6, '10.10.4.102', '', 'high uploads', 30378, 'web_proxy_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 7, '10.10.4.102', '', 'high downloads', 17815, 'web_proxy_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 7, '10.10.4.103', '', 'high downloads', 0, 'web_proxy_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 7, '10.10.4.42', '', 'high downloads', 686, 'web_proxy_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T18:00:00.000Z', 4, 15, '10.10.4.102', '', 'high number of distinct destinations', 3, 'web_proxy_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // Some model 6 data
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 0, '', 'E8SEC.LAB-w2k8r2-ad$@e8sec.lab', 'Successful Kerberos service ticket requests to many services', 1, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 0, '', 'E8SEC.LAB-w2k8r2-src$@e8sec.lab', 'Successful Kerberos service ticket requests to many services', 3, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 1, '', 'E8SEC.LAB-w2k8r2-ad$@e8sec.lab', 'Successful Kerberos service ticket requests from many source IPs', 0, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 1, '', 'E8SEC.LAB-w2k8r2-src$@e8sec.lab', 'Successful Kerberos service ticket requests from many source IPs', 1, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 2, '', 'E8SEC.LAB-w2k8r2-ad$@e8sec.lab', 'Many successful Kerberos service ticket requests', 8, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 2, '', 'E8SEC.LAB-w2k8r2-src$@e8sec.lab', 'Many successful Kerberos service ticket requests', 8, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T19:00:00.000Z', 6, 6, '', 'E8SEC-w2k12-srv$', 'Successfully logged on from many source IPs', 0, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T20:00:00.000Z', 6, 0, '', 'E8SEC.LAB-jyria@e8sec.lab', 'Successful Kerberos service ticket requests to many services', 3, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T20:00:00.000Z', 6, 0, '', 'E8SEC.LAB-w2k8r2-ad$@e8sec.lab', 'Successful Kerberos service ticket requests to many services', 1, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-01T20:00:00.000Z', 6, 1, '', 'E8SEC.LAB-jyria@e8sec.lab', 'Successful Kerberos service ticket requests from many source IPs', 1, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-02T19:00:00.000Z', 6, 1, '', 'E8SEC.LAB-w2k8r2-src$@e8sec.lab', 'Successful Kerberos service ticket requests from many source IPs', 1, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityFeatures.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      "values ('2016-07-02T19:00:00.000Z', 6, 2, '', 'E8SEC.LAB-w2k8r2-ad$@e8sec.lab', 'Many successful Kerberos service ticket requests', 6, 'iam_mef', 'hourly')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

  }

  // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call HBaseTestBase.HBaseTeardown().  See comments
  // in HBaseTestBase for details.
  // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
  @AfterClass
  def stopServers() {
    try {
      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
      val enityFeaturesSql: String = "DROP TABLE IF EXISTS " + EntityFeatures.getName(configuration)
      val enityFeaturesStmt: PreparedStatement = conn.prepareStatement(enityFeaturesSql)
      enityFeaturesStmt.execute
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw ex
    } finally {
      MongoTestBase.teardownMongo()
      HBaseTestBase.teardownHBase()
    }
  }

}


class EntityFeaturesDaoTest {
  private val entityFeaturesDao = new EntityFeaturesDao(EntityFeaturesDaoTest.configuration)

  @Test
  def getEntityFeaturesByFeatureLabelTest(): Unit = {
    // There is some strange stuff we're doing with the feature label for model 4 in the code - it looks like we're expecting the feature label in the table
    // to actually be an EventType.  But it's not...  Until I understand why, I'm not including a test for model 4.
//    val entityFeaturesModel4 = entityFeaturesDao.getEntityFeaturesByFeatureLabel("2016-07-01T00:00:00.000Z", "2016-07-02T00:00:00.000Z", "", "",
//      4, "high downloads'", EntityFeaturesDaoTest.featureServiceCache)
//    assertEquals(3, entityFeaturesModel4.size)

    val entityFeaturesModel6 = entityFeaturesDao.getEntityFeaturesByFeatureLabel("2016-07-01T00:00:00.000Z", "2016-07-03T00:00:00.000Z", "", "",
      6, "Many successful Kerberos service ticket requests", EntityFeaturesDaoTest.featureServiceCache)
    assertEquals(2, entityFeaturesModel6.size)
    for (resultsMap <- entityFeaturesModel6) {
      val day = resultsMap.getOrElse("day", "")
      val count = resultsMap.getOrElse("count", -1).asInstanceOf[Long]
      if (day.equals("2016-07-01")) {
        assertEquals(16, count)
      } else if (day.equals("2016-07-02")) {
        assertEquals(6, count)
      } else {
        assertTrue("got unexpected day [" + day + "]", false)
      }
    }

    val entityFeaturesModel6manyServices = entityFeaturesDao.getEntityFeaturesByFeatureLabel("2016-07-01T00:00:00.000Z", "2016-07-03T00:00:00.000Z", "", "",
      6, "Successful Kerberos service ticket requests to many services", EntityFeaturesDaoTest.featureServiceCache)
    assertEquals(1, entityFeaturesModel6manyServices.size)
    for (resultsMap <- entityFeaturesModel6manyServices) {
      val day = resultsMap.getOrElse("day", "")
      val count = resultsMap.getOrElse("count", -1).asInstanceOf[Long]
      if (day.equals("2016-07-01")) {
        assertEquals(8, count)
      } else {
        assertTrue("got unexpected day [" + day + "]", false)
      }
    }

    val entityFeaturesModel6Dest = entityFeaturesDao.getEntityFeaturesByFeatureLabel("2016-07-01T00:00:00.000Z", "2016-07-03T00:00:00.000Z", "", "E8SEC.LAB-w2k8r2-ad$@e8sec.lab",
      6, "Many successful Kerberos service ticket requests", EntityFeaturesDaoTest.featureServiceCache)
    assertEquals(2, entityFeaturesModel6Dest.size)
    for (resultsMap <- entityFeaturesModel6Dest) {
      val day = resultsMap.getOrElse("day", "")
      val count = resultsMap.getOrElse("count", -1).asInstanceOf[Long]
      if (day.equals("2016-07-01")) {
        assertEquals(8, count)
      } else if (day.equals("2016-07-02")) {
        assertEquals(6, count)
      } else {
        assertTrue("got unexpected day [" + day + "]", false)
      }
    }

  }
}
