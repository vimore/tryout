package com.e8.palam.dao

import java.io.File
import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}
import javax.validation.{Validation, Validator}

import com.e8.palam.TestBaseDao
import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.utils.EntUserProperties
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{MongoUtils, PhoenixUtils, UserEntityPropertiesDao}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.bson.Document
import org.junit.{AfterClass, Before, BeforeClass, Test}
import org.mockito.Mockito
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map => MutableMap}

object UserEntityPropertiesDaoTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  //val MONGO_DB_SERVER = "10.10.80.77"
  //"10.10.30.20"
  //val MONGO_DB_PORT = 27017


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
    try {
      // Insert suppression entries in Mongo
      val suppression: MongoDatabase = MongoUtils.getDatabase("accounts", configuration)
      val whitelist = suppression.getCollection("whitelistitems")
      val suppressJyriaEntity = new Document
      suppressJyriaEntity.put("suppress", "entity")
      suppressJyriaEntity.put("category", "ui")
      suppressJyriaEntity.put("type", "user")
      suppressJyriaEntity.put("entity", "SRV-jyria")
      suppressJyriaEntity.put("behavior", "Anomalous User Activity")
      whitelist.insertOne(suppressJyriaEntity)
      val suppressEntBeh = new Document
      suppressEntBeh.put("suppress", "entbeh")
      suppressEntBeh.put("category", "ui")
      suppressEntBeh.put("type", "ip")
      suppressEntBeh.put("entity", "192.168.12.18")
      suppressEntBeh.put("behavior", "Exfiltration")
      whitelist.insertOne(suppressEntBeh)
      val suppressEntIp = new Document
      suppressEntIp.put("suppress", "entity")
      suppressEntIp.put("category", "ui")
      suppressEntIp.put("type", "ip")
      suppressEntIp.put("entity", "192.1.12.255")
      suppressEntIp.put("behavior", "Exfiltration")
      whitelist.insertOne(suppressEntIp)
      val suppressBehavior = new Document
      suppressBehavior.put("suppress", "behavior")
      suppressBehavior.put("category", "ui")
      suppressBehavior.put("type", "ip")
      suppressBehavior.put("entity", "192.1.12.255")
      suppressBehavior.put("behavior", "New/Uncategorized Destinations")
      whitelist.insertOne(suppressBehavior)

    }
  }
  // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call HBaseTestBase.HBaseTeardown().  See comments
  // in HBaseTestBase for details.
  // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
  @AfterClass
  def stopServers() {
    try {
      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
      val entityThreatSql: String = "DROP TABLE IF EXISTS ENTITY_THREAT"
      val entityThreatStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
      entityThreatStmt.execute
      val peerGroupSql: String = "DROP TABLE IF EXISTS PEER_GROUP"
      val peerGroupStmt: PreparedStatement = conn.prepareStatement(peerGroupSql)
      peerGroupStmt.execute
      val coordActivitySql: String = "DROP TABLE IF EXISTS COORD_ACTIVITY"
      val coordActivityStmt: PreparedStatement = conn.prepareStatement(coordActivitySql)
      coordActivityStmt.execute
      val entHostPropsSql: String = "DROP TABLE IF EXISTS ENT_HOST_PROPS"
      val entHostPropsStmt: PreparedStatement = conn.prepareStatement(entHostPropsSql)
      entHostPropsStmt.execute
      val entFusRollupSql: String = "DROP TABLE IF EXISTS ENTITY_FUSION_HOURLY_ROLL_UP"
      val entFusRollupStmt: PreparedStatement = conn.prepareStatement(entFusRollupSql)
      entFusRollupStmt.execute
      val behaviorAnomalySql: String = "DROP TABLE IF EXISTS BEHAVIOR_ANOMALY"
      val behaviorAnomalyStmt: PreparedStatement = conn.prepareStatement(behaviorAnomalySql)
      behaviorAnomalyStmt.execute
      val beaconSql: String = "DROP TABLE IF EXISTS BEACONS"
      val beaconStmt: PreparedStatement = conn.prepareStatement(beaconSql)
      beaconStmt.execute
      val c2ModelSql: String = "DROP TABLE IF EXISTS C2_MODEL"
      val c2ModelStmt: PreparedStatement = conn.prepareStatement(c2ModelSql)
      c2ModelStmt.execute
      //      val enityFeaturesSql: String = "DROP TABLE IF EXISTS ENTITY_FEATURES"
      //      val enityFeaturesStmt: PreparedStatement = conn.prepareStatement(enityFeaturesSql)
      //      enityFeaturesStmt.execute
      val customBehaviorSql: String = "DROP TABLE IF EXISTS CUSTOM_BEHAVIOR"
      val customBehaviorSqlStmt: PreparedStatement = conn.prepareStatement(customBehaviorSql)
      customBehaviorSqlStmt.execute
      val entUserPropsSql: String = "DROP TABLE IF EXISTS ENT_USER_PROPS"
      val entUserPropsStmt: PreparedStatement = conn.prepareStatement(entUserPropsSql)
      entUserPropsStmt.execute
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
class UserEntityPropertiesDaoTest extends TestBaseDao {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[UserEntityPropertiesDaoTest])

  var userEntityPropsDao: UserEntityPropertiesDao = null

  //Test Data
  val testUUID = "TEST UUID"
  val testDateTime = "2015-02-04T00:00:00.000Z"
  var testUserName_1 = "bob"
  val testUserName_2 = "alice"
  var testUserName = testUserName_1
  val testCanonicalName = "www.foobarbaz.com. IN CNAME eric.foobarbaz.com."
  val testAccountType = "admin"
  val testSecurityId = "1123-3sp-1123"
  val testIsCritical = "1"
  val testIsCriticalBoolean = true
  val testJobTitle = "Test Engineer"
  val testEmail = "bob@foobar.com"
  val testLocation = "Palo Alto"
  val testDepartment = "IT"
  val testPrimaryHost = "bob's Mac Book"
  val testManager = "aj"
  val testCreationDate = "2015-02-04T00:00:00.000Z"
  val testLastModificationDate = "2015-02-04T00:00:00.000Z"
  val testPasswordLastSetDate = "2015-02-04T00:00:00.000Z"
  val testLastLogonDate = "2015-02-04T00:00:00.000Z"
  val testRiskScore = 0.8

  //test output Map
  var buf = ListBuffer[MutableMap[String, Any]]()
  var testOutputMap = MutableMap[String, Any]()
  var counter = 0

  @Before
  override def setup() = {
    super.setup()

    userEntityPropsDao = new UserEntityPropertiesDao(UserEntityPropertiesDaoTest.configuration) {
      override def getConnection(conf: FeatureServiceConfiguration) = {
        mockConn
      }

      override def getPreparedStatement(conn: Connection, sqlString: String) = {
        mockPreparedStatement
      }

      override def executeQuery(pstmt: PreparedStatement): ResultSet = {
        mockResultSet
      }

      override def getResultSetMetaData(rs: ResultSet): ResultSetMetaData = {
        mockResultSetMetaData
      }

      /**
       * Overriding the method to output sample output data
       * @param rs
       * @param rsMeta
       * @param valMap
       */
      override def appendToMap(rs: ResultSet, rsMeta: ResultSetMetaData, valMap: MutableMap[String, Any]) = {
        if (counter < buf.size) {
          val map = buf(counter)
          for ((k, v) <- map) {
            valMap += k -> v
          }
          counter = counter + 1
        }
      }
    }

    buildResultSetData()
    buildResultSetMetaData()
  }

  override def buildResultSetData() = {
    Mockito.when(mockResultSet.getString(EntUserProperties.UUID.toString)).thenReturn(testUUID)
    Mockito.when(mockResultSet.getString(EntUserProperties.DATE_TIME.toString)).thenReturn(testDateTime)
    Mockito.when(mockResultSet.getString(EntUserProperties.USER_NAME.toString)).thenReturn(testUserName_1)
    Mockito.when(mockResultSet.getString(EntUserProperties.CANONICAL_NAME.toString)).thenReturn(testCanonicalName)
    Mockito.when(mockResultSet.getString(EntUserProperties.ACCOUNT_TYPE.toString)).thenReturn(testAccountType)
    Mockito.when(mockResultSet.getString(EntUserProperties.SECURITY_ID.toString)).thenReturn(testSecurityId)
    Mockito.when(mockResultSet.getString(EntUserProperties.IS_CRITCAL.toString)).thenReturn(testIsCritical)
    Mockito.when(mockResultSet.getString(EntUserProperties.JOB_TITLE.toString)).thenReturn(testJobTitle)
    Mockito.when(mockResultSet.getString(EntUserProperties.EMAIL.toString)).thenReturn(testEmail)
    Mockito.when(mockResultSet.getString(EntUserProperties.LOCATION.toString)).thenReturn(testLocation)
    Mockito.when(mockResultSet.getString(EntUserProperties.DEPARTMENT.toString)).thenReturn(testDepartment)
    Mockito.when(mockResultSet.getString(EntUserProperties.MANAGER.toString)).thenReturn(testManager)
    Mockito.when(mockResultSet.getString(EntUserProperties.PRIMARY_HOST.toString)).thenReturn(testPrimaryHost)
    Mockito.when(mockResultSet.getString(EntUserProperties.CREATION_DATE.toString)).thenReturn(testCreationDate)
    Mockito.when(mockResultSet.getString(EntUserProperties.LAST_MODIFICATION_DATE.toString)).thenReturn(testLastModificationDate)
    Mockito.when(mockResultSet.getString(EntUserProperties.PASSWORD_LAST_SET_DATE.toString)).thenReturn(testPasswordLastSetDate)
    Mockito.when(mockResultSet.getString(EntUserProperties.LAST_LOGON_DATE.toString)).thenReturn(testLastLogonDate)
    Mockito.when(mockResultSet.getDouble(EntUserProperties.RISK.toString)).thenReturn(testRiskScore)
  }

  override def buildResultSetMetaData() = {
    val columns: EntUserProperties.ValueSet = EntUserProperties.values
    var i: Int = 1
    columns.foreach { column =>
      Mockito.when(mockResultSetMetaData.getColumnName(i)).thenReturn(column.toString)
      i = i + 1
    }
    Mockito.when(mockResultSetMetaData.getColumnCount).thenReturn(EntUserProperties.values.size)
  }

  /**
   * populates sample outputMap
   * @return
   */
  def populateOutputMap() = {
    testOutputMap = MutableMap[String, Any]()
    testOutputMap += "uuid" -> testUUID
    testOutputMap += "dateTime" -> testDateTime
    testOutputMap += "userName" -> testUserName
    testOutputMap += "canonicalName" -> testCanonicalName
    testOutputMap += "accountType" -> testAccountType
    testOutputMap += "securityId" -> testSecurityId
    testOutputMap += "isCritical" -> true
    testOutputMap += "jobTittle" -> testJobTitle
    testOutputMap += "email" -> testEmail
    testOutputMap += "location" -> testLocation
    testOutputMap += "department" -> testDepartment
    testOutputMap += "manager" -> testManager
    testOutputMap += "primaryHost" -> testPrimaryHost
    testOutputMap += "creationDate" -> testCreationDate
    testOutputMap += "lastModificationDate" -> testLastModificationDate
    testOutputMap += "passwordLastSetDate" -> testPasswordLastSetDate
    testOutputMap += "lastLogonDate" -> testLastLogonDate
    testOutputMap += "risk" -> testRiskScore
    buf += testOutputMap
  }


  @Test
  def getUserPropertiesBySourceNamesForNullUser() = {
    Logger.debug("Testing Ent_User_Properties for null users...")
    //input
    val peers: String = null

    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    //test
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesBySourceNames(startTime, endTime, peers)

    //assert
    assert(buf != null)
    assert(buf.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserPropertiesBySourceNamesForEmptyUser() = {
    Logger.debug("Testing Ent_User_Properties for Empty users...")
    //input
    val peers: String = ""

    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    //test
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesBySourceNames(startTime, endTime, peers)

    //assert
    assert(buf != null)
    assert(buf.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserIdEmptyId() = {
    Logger.debug("Testing Ent_User_Properties by Id for Empty UUID...")
    val userId: String = ""
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserId(userId)
    Mockito.when(mockResultSet.next()).thenReturn(false)
    assert(buf != null)
    assert(buf.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserIdNullId() = {
    Logger.debug("Testing Ent_User_Properties by Id for null UUID...")
    val userId: String = null
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserId(userId)
    Mockito.when(mockResultSet.next()).thenReturn(false)
    assert(buf != null)
    assert(buf.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserIdEmptyResult() = {
    Logger.debug("Testing Ent_User_Properties for given UUID and Empty ResultSet...")
    val userId: String = "067e6162-3b6f-test-a171-2470b63dff00"
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserId(userId)
    Mockito.when(mockResultSet.next()).thenReturn(false)
    assert(buf != null)
    assert(buf.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserIdNonEmptyResult() = {
    Logger.debug("Testing Ent_User_Properties for given UUID...")
    val userId: String = "067e6162-test-4ae2-a171-2470b63dff00"
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserId(userId)
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)
    assert(buf != null)
    assert(buf.isEmpty)
    assertOutputBuf(buf, 1)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserPropertiesBySourceNamesEmptyResult() = {
    Logger.debug("Testing Ent_User_Properties for empty ResultSet...")
    val peers: String = "\"WSS_NonProd_Content\""
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesBySourceNames(startTime, endTime, peers)

    Mockito.when(mockResultSet.next()).thenReturn(false)
    assert(buf != null)
    assert(buf.isEmpty)
    assertOutputBuf(buf, 1)
    Logger.debug("Test Succeeded")
  }

  @Test
  def getUserPropertiesBySourceNamesForOneUser() = {
    Logger.debug("Testing Ent_User_Properties for Single user...")
    //input
    val peers: String = "\"WSS_NonProd_Content\""

    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    populateOutputMap()

    //test
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesBySourceNames(startTime, endTime, peers)

    //assert
    assert(buf != null)
    assert(!buf.isEmpty)
    assert(buf.size == 1)
    assertOutputBuf(buf, 1)
    Logger.debug("Test Succeeded")

  }


  @Test
  def getUserPropertiesBySourceNamesForMultipleUsers() = {
    Logger.debug("Testing Ent_User_Properties for multiple users...")
    //input
    val peers: String = "\"WSS_NonProd_Content\" , \"VSUS$70\""

    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    //adding user 1
    populateOutputMap()

    Mockito.when(mockResultSet.getString(EntUserProperties.USER_NAME.toString)).thenReturn(testUserName_1) thenReturn(testUserName_2)

    //adding second user
    testUserName = testUserName_2
    populateOutputMap()

    //test
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesBySourceNames(startTime, endTime, peers)

    //assert
    assert(buf != null)
    assert(!buf.isEmpty)
    assert(buf.size == 2)
    assertOutputBuf(buf, 2)
    Logger.debug("Test Succeeded")

  }

  @Test
  def getSourceUserCaseInsensitive() ={

    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    //adding user 1
    populateOutputMap()

    Mockito.when(mockResultSet.getString(EntUserProperties.USER_NAME.toString)).thenReturn(testUserName_1) thenReturn(testUserName_2)

    //adding second user
    testUserName = testUserName_2
    populateOutputMap()

    //test
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesForSourceName(startTime, endTime, "BOB")

    //assert
    assert(buf != null)
    assert(!buf.isEmpty)
    assert(buf.size == 1)
    assertOutputBuf(buf, 1)

    Logger.debug("Test Succeeded")
  }

  @Test
  def getSourceUserCaseInsensitive2() ={

    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    //adding user 1
    populateOutputMap()

    Mockito.when(mockResultSet.getString(EntUserProperties.USER_NAME.toString)).thenReturn(testUserName_1) thenReturn(testUserName_2)

    //adding second user
    testUserName = testUserName_2
    populateOutputMap()

    //test
    val buf: ListBuffer[mutable.Map[String, Any]] = userEntityPropsDao.getUserPropertiesForSourceName(startTime, endTime, "bob")

    //assert
    assert(buf != null)
    assert(!buf.isEmpty)
    assert(buf.size == 1)
    assertOutputBuf(buf, 1)

    Logger.debug("Test Succeeded")
  }
  def assertOutputBuf(maps: ListBuffer[MutableMap[String, Any]], numOfUsers: Int) = {
    maps.foreach { map =>
      assert(map("uuid") == testUUID)
      assert(map("dateTime") == testDateTime)
      assert(map("canonicalName") == testCanonicalName)
      assert(map("accountType") == testAccountType)
      assert(map("securityId") == testSecurityId)
      assert(map("isCritical") == true)
      assert(map("jobTittle") == testJobTitle)
      assert(map("email") == testEmail)
      assert(map("location") == testLocation)
      assert(map("department") == testDepartment)
      assert(map("manager") == testManager)
      assert(map("primaryHost") == testPrimaryHost)
      assert(map("creationDate") == testCreationDate)
      assert(map("lastModificationDate") == testLastModificationDate)
      assert(map("passwordLastSetDate") == testPasswordLastSetDate)
      assert(map("lastLogonDate") == testLastLogonDate)
      assert(map("risk") == testRiskScore)
      for (i <- 1 until numOfUsers) {
        if (numOfUsers == i) {
          assert(map("userName") == testUserName_1)
        }
        if (numOfUsers == i) {
          assert(map("userName") == testUserName_2)
        }
      }
    }
  }

}
