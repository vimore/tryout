package com.e8.palam.dao

import java.io.File
import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}
import java.util
import javax.validation.{Validation, Validator}

import com.e8.palam.TestBaseDao
import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.{Lists, Maps}
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.EntityModelInfo
import com.securityx.modelfeature.common.cache.AutoCompleteCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.utils.{Constants, EntityThreat}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{ColumnMetaData, DetectorHomeDao, MongoUtils, PhoenixUtils}
import com.securityx.modelfeature.queryengine.{DataTypes, QueryGenerator}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.bson.Document
import org.junit.{AfterClass, Before, BeforeClass, Test}
import org.junit.Assert.{assertEquals, assertTrue}
import org.mockito.Mockito
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

object DetectorHomeDaoTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = _
  private var featureServiceCache: FeatureServiceCache = _
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
class DetectorHomeDaoTest extends TestBaseDao {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[DetectorHomeDaoTest])

  var detectorHomeDao: DetectorHomeDao = _

  //Test Data
  val testUUID = "TEST UUID"
  val testDateTime_1 = "2015-02-04T00:00:00.000Z"
  val testDateTimeYmdSeparated_1 = "2015-02-04"
  val testDateTime_2 = "2015-02-05T00:00:00.000Z"
  val testDateTimeYmdSeparated_2 = "2015-02-05"
  val testStartTime = "2015-01-04T00:00:00.000Z"
  val testEndTime = "2015-02-10T00:00:00.000Z"
  var testUserName = "bob"
  val testIpAddress = "192.168.1.101"
  val testHostName = "Bob's MacBook Pro"
  val testMacAddress = "6a:00:00:22:91:01"
  val testModelId_1 = 3
  val testModelId_2 = 0
  val testSecurityEventTypeId_1 = 2
  val testSecurityEventTypeId_2 = 0
  val testFeatureLabel = "E8-IAM-Accounts-2"
  val testTypeRefix = "IAM"
  val testEventType_1 = "Multiple Logons"
  val testEventDescription_1 = "This user has requested an unusual number of Kerberos service tickets"
  val testEventDescription_2 = "Source Host has displayed a suspicious repeated pattern of network traffic"
  val testRiskScore = 0.8
  val testTopN = 100
  val testKillchainId_1 = 1
  val testKillchainId_2 = 3


  //test output Map
  var buf = ListBuffer[MutableMap[String, Any]]()
  var testOutputMap = MutableMap[String, Any]()
  var counter = 0

  private var autoCompleteList: java.util.List[String] = new java.util.LinkedList[String]()
  private var autoCompleteMap = new java.util.HashMap[String, util.List[String]]()

  @Before
  override def setup() = {
    super.setup()

    detectorHomeDao = new DetectorHomeDao(DetectorHomeDaoTest.configuration) {
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

      //This method calls the Tanium stats dao, which should have units of its own.
      override def populateTopNTaniumThreats(startTime: String, endTime: String, n: Int,
                                             buf: collection.mutable.ListBuffer[MutableMap[String, Any]],
                                             cache: FeatureServiceCache, hosts: Array[String]) = {
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

      override def getAutoCompleteResults(incomingString: String,
                                          fieldName: String,
                                          autoCompleteCache: AutoCompleteCache): util.List[String] = {
        autoCompleteList
      }

      override def getAutoCompleteResultsOnAll(incomingString: String,
                                               autoCompleteCache: AutoCompleteCache)
      : java.util.Map[String, util.List[String]] = {
        autoCompleteMap
      }
    }

    buildResultSetData()
    buildResultSetMetaData()
  }

  override def buildResultSetData() = {
    Mockito.when(mockResultSet.getString(EntityThreat.DATE_TIME.toString)).thenReturn(testDateTime_1).thenReturn(testDateTime_2)
    Mockito.when(mockResultSet.getString(EntityThreat.USER_NAME.toString)).thenReturn(testUserName)
    Mockito.when(mockResultSet.getString(EntityThreat.IP_ADDRESS.toString)).thenReturn(testIpAddress)
    Mockito.when(mockResultSet.getString(EntityThreat.HOST_NAME.toString)).thenReturn(testHostName)
    Mockito.when(mockResultSet.getString(EntityThreat.MAC_ADDRESS.toString)).thenReturn(testMacAddress)
    Mockito.when(mockResultSet.getInt(EntityThreat.MODEL_ID.toString)).thenReturn(testModelId_1).thenReturn(testModelId_2)
    Mockito.when(mockResultSet.getInt(EntityThreat.SECURITY_EVENT_ID.toString)).thenReturn(testSecurityEventTypeId_1).
      thenReturn (testSecurityEventTypeId_2)
    Mockito.when(mockResultSet.getDouble(EntityThreat.RISK_SCORE.toString)).thenReturn(testRiskScore)
    Mockito.when(mockResultSet.getDouble("RISK")).thenReturn(testRiskScore)
    Mockito.when(mockResultSet.getInt("COUNT")).thenReturn(20).thenReturn(30)
  }

  override def buildResultSetMetaData() = {
    val columns: EntityThreat.ValueSet = EntityThreat.values
    var i: Int = 1
    columns.foreach { column =>
      Mockito.when(mockResultSetMetaData.getColumnName(i)).thenReturn(column.toString)
      i = i + 1
    }
    Mockito.when(mockResultSetMetaData.getColumnCount).thenReturn(EntityThreat.values.size)
  }

  /**
   * populates sample outputMap
   * @return
   */
  def populateOutputMap() = {
    testOutputMap = MutableMap[String, Any]()
    testOutputMap += "dateTime" -> testDateTime_1
    testOutputMap += "ipAddress" -> testIpAddress
    testOutputMap += "userName" -> testUserName
    testOutputMap += "hostName" -> testHostName
    testOutputMap += "modelId" -> testModelId_1
    testOutputMap += "securityEventId" -> testSecurityEventTypeId_1
    testOutputMap += "riskScore" -> testRiskScore
    buf += testOutputMap
  }

  def prepareAutocompleteForFieldIP()={
    autoCompleteList = new java.util.LinkedList[String]()
    autoCompleteList.add("10.10.4.102")
    autoCompleteList.add("10.10.4.42")
    autoCompleteList.add("10.10.4.54")

  }

  def prepareAutocompleteForFieldUsers()={
    autoCompleteList = new java.util.LinkedList[String]()
    autoCompleteList.add("w2k12-srv$")
    autoCompleteList.add("w2k8r2-ad-2$")
    autoCompleteList.add("w2k8r2-src$")

  }

  def prepareAutocompleteForFieldHosts()={
    autoCompleteList = new java.util.LinkedList[String]()
    autoCompleteList.add("administrator")
    autoCompleteList.add("Alexanders-MBP")
    autoCompleteList.add("android-30868f84ba3fa83f")

  }

  def prepareAutocompleteForAll()={
    val ipResult = new java.util.LinkedList[String]()
    ipResult.add("10.10.4.102")
    ipResult.add("10.10.4.42")
    ipResult.add("10.10.4.54")

    val hostsResult = new java.util.LinkedList[String]()
    hostsResult.add("administrator")
    hostsResult.add("Alexanders-MBP")
    hostsResult.add("android-30868f84ba3fa83f")

    val usersResult = new java.util.LinkedList[String]()
    usersResult.add("a2k12-srv$")
    usersResult.add("a2k8r2-ad-2$")
    usersResult.add("a2k8r2-src$")

    autoCompleteMap = new java.util.HashMap[String, util.List[String]]()
    autoCompleteMap.put("ipAddresses", ipResult)
    autoCompleteMap.put("hostNames", hostsResult)
    autoCompleteMap.put("userNames", usersResult)
  }
  // Ram: Commenting out these tests because they don't exercise the AutoCompleteCache code!
  // It is very hard to catch this in code review because it looks like the test is doing the right thing
  // When I set a break point in AutoCompleteCache code, it wasn't even invoked.
  // It turns out the tests are invoking the mocked up DetectorHomeDao in this file.
  // So, what are we testing? and why are we testing?
/*
  @Test
  def testAutocompleteForIP(): Unit = {
    prepareAutocompleteForFieldIP()
    val result = detectorHomeDao.getAutoCompleteResults("1","sourceIp", autoCompleteCache)
    assert(result.size() == autoCompleteList.size())

    var a = 0;
    for( a <- 0 to result.size() - 1){
      assert(result.get(a) == autoCompleteList.get(a))
    }
  }

  @Test
  def testAutocompleteForUsers(): Unit = {
    prepareAutocompleteForFieldUsers()
    val result = detectorHomeDao.getAutoCompleteResults("w","userName", autoCompleteCache)
    assert(result.size() == autoCompleteList.size())

    var a = 0;
    for( a <- 0 to result.size() - 1){
      assert(result.get(a) == autoCompleteList.get(a))
    }
  }

  @Test
  def testAutocompleteForHosts(): Unit = {
    prepareAutocompleteForFieldHosts()
    val result = detectorHomeDao.getAutoCompleteResults("a","hostName", autoCompleteCache)
    assert(result.size() == autoCompleteList.size())

    var a = 0;
    for( a <- 0 to result.size() - 1){
      assert(result.get(a) == autoCompleteList.get(a))
    }
  }

  @Test
  def testAutocompleteForAll(): Unit = {
    var a = 0;
    prepareAutocompleteForAll()
    val result = detectorHomeDao.getAutoCompleteResultsOnAll("a", autoCompleteCache)

    assert(result != null)

    val inputIpAddresses = autoCompleteMap.get("ipAddresses").get
    val resultIpAddresses = result.get("ipAddresses").get

    assert(resultIpAddresses.size() == inputIpAddresses.size())
    for( a <- 0 to resultIpAddresses.size() - 1){
      assert(resultIpAddresses.get(a) == inputIpAddresses.get(a))
    }

    val inputHostNames = autoCompleteMap.get("hostNames").get
    val resultHostNames = result.get("hostNames").get

    assert(resultHostNames.size() == inputHostNames.size())
    for( a <- 0 to resultHostNames.size() - 1){
      assert(resultHostNames.get(a) == inputHostNames.get(a))
    }

    val inputUserNames = autoCompleteMap.get("userNames").get
    val resultUserNames = result.get("userNames").get

    assert(resultUserNames.size() == inputUserNames.size())
    for( a <- 0 to resultUserNames.size() - 1){
      assert(resultUserNames.get(a) == inputUserNames.get(a))
    }
  }
*/
  @Test
  def testGetSqlPredicateString() : Unit= {

    val testInputJson: QueryJson = getTestInputJson()

    val pstmtList: ListBuffer[ColumnMetaData] = ListBuffer[ColumnMetaData]()
    val whereClause = detectorHomeDao.getSqlPredicateString(testInputJson, pstmtList, null, null, null, null, cache, false)
    val expectedWhereClauseString = " where DATE_TIME  >=  ?  AND DATE_TIME  <  ?  AND ( KILLCHAIN_ID = ? OR KILLCHAIN_ID = ? OR KILLCHAIN_ID = ? ) AND ( RISK_SCORE >= ? ) AND ( MODEL_ID  !=  ? ) ORDER BY HOST_NAME asc LIMIT  ? "
    assertEquals(expectedWhereClauseString, whereClause)
    assert( pstmtList.size.equals(8) )

    var i = 0
    pstmtList.foreach{ column =>
      if( i.equals(0)) {
        assert(column.columnName.equals("DATE_TIME"))
        assert(column.columnType.equals(DataTypes.STRING.toString))
        assert(column.columnValue.equals(testStartTime))
      }
      if(i.equals(1)){
        assert(column.columnName.equals("DATE_TIME"))
          assert(column.columnType.equals(DataTypes.STRING.toString))
        assert(column.columnValue.equals(testEndTime))
      }
      if(i.equals(2)){
        assert(column.columnName.equals("KILLCHAIN_ID"))
        assert(column.columnType.equals(DataTypes.INTEGER.toString))
        assert(column.columnValue.equals("1"))
      }
      if(i.equals(3)){
        assert(column.columnName.equals("KILLCHAIN_ID"))
        assert(column.columnType.equals(DataTypes.INTEGER.toString))
        assert(column.columnValue.equals("2"))
      }
      if(i.equals(4)){
        assert(column.columnName.equals("KILLCHAIN_ID"))
        assert(column.columnType.equals(DataTypes.INTEGER.toString))
        assert(column.columnValue.equals("3"))
      }
      if(i.equals(5)){
        assert(column.columnName.equals("RISK_SCORE"))
        assert(column.columnType.equals(DataTypes.DOUBLE.toString))
        assert(column.columnValue.equals(testRiskScore.toString))
      }
      if(i.equals(6)){
        assert(column.columnName.equals("MODEL_ID"))
        assert(column.columnType.equals(DataTypes.INTEGER.toString))
//        assert(column.columnValue.equals(Constants.WebAnomalyProfileTuple._1.toString))
      }
      if(i.equals(7)){
        assert(column.columnName.equals(""))
        assert(column.columnType.equals(DataTypes.INTEGER.toString))
        assert(column.columnValue.equals("100"))
      }
      i = i + 1
    }
  }

  @Test
  def getEntityScoresTest()  = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    populateOutputMap()

    val testInputJson: QueryJson = new QueryJson()
    // set limit to fetch one entry
    testInputJson.setLimit(1)
    val hostname: util.Map[String, Object] = new util.HashMap[String, Object]()
    hostname.put("field","userName")
    hostname.put("operator", "equals")
    val hostnameValues: util.List[String] = new util.ArrayList[String]()
    hostnameValues.add(testUserName)
    hostname.put("values", hostnameValues)
    testInputJson.getQuery.add(hostname)

    val result : ListBuffer[MutableMap[String, Any]] = detectorHomeDao.getEntityRiskScores(testInputJson, cache, isSearchQuery = true, returnEntityIds = false)
    assert(result != null)
    assert(result.nonEmpty)
    val map: MutableMap[String, Any] = result(0)
    assert(map("hostName").equals(testHostName))
    assert(map("userName").equals(testUserName))
    assert(map("ipAddress").equals(testIpAddress))
    assert(map("currentScore").equals(testRiskScore))

    assert(map.contains("modelScores"))
    val entityModelInfoList : ListBuffer[EntityModelInfo] = map("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
    assert(entityModelInfoList.nonEmpty)
    val entityModelInfo : EntityModelInfo = entityModelInfoList(0)
    assert(entityModelInfo.getRisk.equals(testRiskScore))
    assert(entityModelInfo.getModel.equals(testModelId_1))
    assert(entityModelInfo.getSecurityEventTypeId.equals(testSecurityEventTypeId_1))
    assert(entityModelInfo.getEventDescription.equals(testEventDescription_1))

  }
  // curl -XPOST -H 'Content-Type: application/json' http://localhost:9080/service/detector/home -d '{"startTime":"2016-07-21T00:00:00.000Z","endTime":"2016-07-26T00:00:00.000Z","query":[{"field":"hostName","operator":"equals","values":["RDP-GW"]},{"field":"sourceIp","operator":"equals","values":["192.168.12.27"]},{"field":"userName","operator":"equals","values":[null]},{"field":"macAddress","operator":"equals","values":["12:7F:C8:56:84:17"]},{"field":"risk","operator":"greater than equal","values":[0.000001]},{"field":"risk","operator":"less than equal","values":[1]}],"sortField":"","sortOrder":"","limit":20}'
  @Test
  def getEntityScoresE82376() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)
    val testInputJson: QueryJson = new QueryJson()
    testInputJson.setStartTime("2016-07-21T00:00:00.000Z")
    testInputJson.setEndTime("2016-07-26T00:00:00.000Z")
    testInputJson.setLimit(20)
    var query = testInputJson.getQuery
    val values = new util.ArrayList[String]()
    // {"field":"hostName","operator":"equals","values":["RDP-GW"]},
    values.add("RDP-GW")
    query = populateQuery(query, "hostName", "equals", values)
    // {"field":"sourceIp","operator":"equals","values":["192.168.12.27"]},
    values.remove(0)
    values.add("192.168.12.27")
    query = populateQuery(query, "sourceIp", "equals", values)
    // {"field":"userName","operator":"equals","values":[null]},
    values.remove(0)
    values.add(null)
    query = populateQuery(query, "userName", "equals", values)
    // {"field":"macAddress","operator":"equals","values":["12:7F:C8:56:84:17"]},
    values.remove(0)
    values.add("12:7F:C8:56:84:17")
    query = populateQuery(query, "macAddress", "equals", values)
    // {"field":"risk","operator":"greater than equal","values":[0.000001]},
    values.remove(0)
    values.add("0.000001")
    query = populateQuery(query, "risk", "greater than equal", values)
    // {"field":"risk","operator":"less than equal","values":[1]}],"sortField":"","sortOrder":"","limit":20}
    values.remove(0)
    values.add("1")
    query = populateQuery(query, "risk", "leass than equal", values)
    testInputJson.setQuery(query)
    val q:QueryGenerator = new QueryGenerator
    val pstmtList : ListBuffer[ColumnMetaData] = new ListBuffer[ColumnMetaData]
    //pstmtList.addString()
    val aliasMap = MutableMap[String, Any]()
    aliasMap += EntityThreat.RISK_SCORE.toString -> "RISK"
    val result = q.getSqlPredicateString(testInputJson, pstmtList, " GROUP BY IP_ADDRESS, USER_NAME, HOST_NAME, MAC_ADDRESS, DATE_TIME, MODEL_ID, SECURITY_EVENT_ID", aliasMap, cache, false, conf.getFixNullValue)
    assert(result.nonEmpty)
    assertEquals(" where DATE_TIME  >=  ?  AND DATE_TIME  <  ?  AND ( RISK_SCORE >= ? ) AND ( RISK_SCORE  ? ) AND ( ( HOST_NAME = ? ) AND ( IP_ADDRESS = ? ) AND ( USER_NAME = ? ) AND ( MAC_ADDRESS = ? ) )  GROUP BY IP_ADDRESS, USER_NAME, HOST_NAME, MAC_ADDRESS, DATE_TIME, MODEL_ID, SECURITY_EVENT_ID LIMIT  ? ", result)
    assertTrue(pstmtList(0).columnName.equals("DATE_TIME"))
    assertTrue(pstmtList(1).columnName.equals("DATE_TIME"))
    assertTrue(pstmtList(2).columnName.equals("RISK_SCORE"))
    assertTrue(pstmtList(3).columnName.equals("RISK_SCORE"))
    assertTrue(pstmtList(4).columnName.equals("HOST_NAME"))
    assertTrue(pstmtList(5).columnName.equals("IP_ADDRESS"))
    assertTrue(pstmtList(6).columnName.equals("USER_NAME"))
    assertTrue(pstmtList(7).columnName.equals("MAC_ADDRESS"))

  }
  def populateQuery(query: util.List[util.Map[String, AnyRef]],
                    field: String, operator: String,
                    values: util.ArrayList[String]): util.List[util.Map[String, AnyRef]] ={
    val map: util.Map[String, Object] = new util.HashMap[String, Object]()
    map.put("field", field)
    map.put("operator", operator)
    map.put("values", values)
    query.add(map)
    query
  }
  @Test
  def testGetKillchainCounts() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    val result : ListBuffer[MutableMap[String, Any]] = detectorHomeDao.getKillchainCounts(testStartTime, testEndTime, cache)
    assert(result != null)
    assert(cache.getKillchainMap.size.equals(result.size))

  }

  @Test
  def testGetRiskScoreBasedTimeSeriesForEntityEmptyOutput() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val testInputJson: QueryJson = new QueryJson()
    val result : List[MutableMap[String, Any]] = detectorHomeDao.getRiskScoreBasedTimeSeriesForEntity(testInputJson, cache)
    assert(result != null)
    assert(result.isEmpty)
  }

  @Test
  def testGetRiskScoreBasedTimeSeriesForEntitySingleRecord() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    val testInputJson: QueryJson = new QueryJson()
    val result : List[MutableMap[String, Any]] = detectorHomeDao.getRiskScoreBasedTimeSeriesForEntity(testInputJson, cache)
    assert(result != null)
    assert(result.nonEmpty)
    val map: MutableMap[String, Any] = result.head
    assert(map("dateTime").equals(testDateTimeYmdSeparated_1))
    assert(map("currentScore").equals(testRiskScore))

    assert(map.contains("modelScores"))
    val entityModelInfoList : ListBuffer[EntityModelInfo] = map("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
    assert(entityModelInfoList.nonEmpty)
    val entityModelInfo : EntityModelInfo = entityModelInfoList.head
    assert(entityModelInfo.getRisk.equals(testRiskScore))
    assert(entityModelInfo.getModel.equals(testModelId_1))
    assert(entityModelInfo.getSecurityEventTypeId.equals(testSecurityEventTypeId_1))
    assert(entityModelInfo.getEventDescription.equals(testEventDescription_1))

  }


  @Test
  def testGetRiskScoreBasedTimeSeriesForEntityMultipleRecords() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    val testInputJson: QueryJson = new QueryJson()
    val result : List[MutableMap[String, Any]] = detectorHomeDao.getRiskScoreBasedTimeSeriesForEntity(testInputJson, cache)
    assert(result != null)
    assert(result.nonEmpty)
    var map: MutableMap[String, Any] = result.head
    assert(map("dateTime").equals(testDateTimeYmdSeparated_1))
    assert(map("currentScore").equals(testRiskScore))

    assert(map.contains("modelScores"))
    var entityModelInfoList : ListBuffer[EntityModelInfo] = map("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
    assert(entityModelInfoList.nonEmpty)
    var entityModelInfo : EntityModelInfo = entityModelInfoList.head
    assert(entityModelInfo.getRisk.equals(testRiskScore))
    assert(entityModelInfo.getModel.equals(testModelId_1))
    assert(entityModelInfo.getSecurityEventTypeId.equals(testSecurityEventTypeId_1))
    assert(entityModelInfo.getEventDescription.equals(testEventDescription_1))

    map= result(1)
    assert(map("dateTime").equals(testDateTimeYmdSeparated_2))
    assert(map("currentScore").equals(testRiskScore))

    assert(map.contains("modelScores"))
    entityModelInfoList = map("modelScores").asInstanceOf[ListBuffer[EntityModelInfo]]
    assert(entityModelInfoList.nonEmpty)
    entityModelInfo = entityModelInfoList.head
    assert(entityModelInfo.getRisk.equals(testRiskScore))
    assert(entityModelInfo.getModel.equals(testModelId_2))
    assert(entityModelInfo.getSecurityEventTypeId.equals(testSecurityEventTypeId_2))
    assert(entityModelInfo.getEventDescription.equals(testEventDescription_2))

  }

  /**
   *
   * @return
   */
  def getTestInputJson() : QueryJson = {
    val testInputJson: QueryJson = new QueryJson()
    testInputJson.setStartTime(testStartTime)
    testInputJson.setEndTime(testEndTime)

    val queryList : util.List[util.Map[String, AnyRef]] = Lists.newLinkedList()

    var filterMap : util.Map[String, AnyRef] = Maps.newHashMap()

    //filtering by killchain ids
    filterMap.put("field", "killchainId".asInstanceOf[AnyRef])
    filterMap.put("operator", "equals".asInstanceOf[AnyRef])

    var values: util.ArrayList[String] = Lists.newArrayList()
    values.add("1")
    values.add("2")
    values.add("3")
    filterMap.put("values", values)

    queryList.add(filterMap)

    filterMap = Maps.newHashMap()

    //filtering by RISK
    filterMap.put("field", "risk".asInstanceOf[AnyRef])
    filterMap.put("operator", "greater than equal".asInstanceOf[AnyRef])

    values = Lists.newArrayList()
    values.add(testRiskScore.toString)
    filterMap.put("values", values)

    queryList.add(filterMap)

    testInputJson.setQuery(queryList)
    testInputJson.setSortField("hostName")
    testInputJson.setSortOrder("asc")
    testInputJson.setLimit(100)

    testInputJson
  }

}
