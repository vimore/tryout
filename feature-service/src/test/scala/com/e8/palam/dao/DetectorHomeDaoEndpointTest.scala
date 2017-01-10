package com.e8.palam.dao

import java.io.File
import java.sql.{Connection, PreparedStatement}
import javax.validation.{Validation, Validator}

import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.cache.{AutoCompleteCache, FeatureResponseCache}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{MongoUtils, DetectorHomeDao, PhoenixUtils}
import com.securityx.modelfeature.utils.{TaniumStats, EntityThreat}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DetectorHomeDaoEndpointTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = _ // initializing to _ in Scala is more concise and portable
  private var featureServiceCache: FeatureServiceCache = _
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  private var featureResponseCache: FeatureResponseCache = _

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
    val client = MongoUtils.getClient(configuration)
    configuration.getMongoDB.setClient(client)

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
    val createEntityThreat: PreparedStatement = conn.prepareStatement(entityThreatSql)
    createEntityThreat.execute

    // Add some model 9 (local endpoint) rows
    var upsertSql: String = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-01T00:00:00.000Z', '192.168.1.31', '54-0C-75-00-E2-12', 'laptop801', 'andre_brown', 9, 0, 0)"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-01T00:00:00.000Z', '192.168.1.32', '29-1F-4E-A7-FA-F5', 'laptop802', 'andre_geim', 9, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // Add some model 10 (global endpoint) rows
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.66', 'B9-BA-E0-A7-82-35', 'laptop836', 'mark_ingram', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.31', '54-0C-75-00-E2-12', 'laptop801', 'andre_brown', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.32', '29-1F-4E-A7-FA-F5', 'laptop802', 'andre_geim', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // add new records for testing Auto complete

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.33', '29-1F-4E-A7-FA-03', 'laptop803', 'johnmac03', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.34', '29-1F-4E-A7-FA-04', 'laptop804', 'johnmac04', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.35', '29-1F-4E-A7-FA-05', 'laptop905', 'johnmac05', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.36', '29-1F-4E-A7-FA-06', 'laptop706', 'johnmac06', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.37', '29-1F-4E-A7-FA-07', 'laptop507', 'johnmac07', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.38', '29-1F-4E-A7-FA-08', 'laptop408', 'johnmac08', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-09', 'laptop309', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-09', 'laptop309', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-09', 'pene-olde', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-09', 'pere-noel', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-09', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-0A', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-0B', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-0C', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.40', '29-1F-4E-A7-FA-09', 'WIN_OSNMCI3GJJ2', 'brian-quick', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.41', '29-1F-4E-A7-FA-09', 'WIN_OSNMCI3GJJ3', 'carson_palmer', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.42', '29-1F-4E-A7-FA-09', 'WIN_OSNMCI3GJJ4', 'charles_clay', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-0A', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-0B', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.39', '29-1F-4E-A7-FA-0C', 'WIN-OSNMCI3GJJ1', 'johnmac09', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.40', '29-1F-4E-A7-FA-09', 'WIN_OSNMCI3GJJ2', 'brian-quick', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.41', '29-1F-4E-A7-FA-09', 'WIN_OSNMCI3GJJ3', 'carson_palmer', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.42', '29-1F-4E-A7-FA-09', 'WIN_OSNMCI3GJJ4', 'charles_clay', 10, 0, 0)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // Some data for tanium_stats table
    val taniumStatsSql: String = "CREATE TABLE IF NOT EXISTS " + TaniumStats.getName(configuration) + " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    TYPE VARCHAR NOT NULL,\n" +
      "    TYPE_VALUE VARCHAR NOT NULL,\n" +
      "    PIVOTS VARCHAR,\n" +
      "    NEWLY_OBSERVED VARCHAR,\n" +
      "    RISK_SCORE VARCHAR,\n" +
      "    RISK_SCORE_GLOBAL VARCHAR,\n" +
      "    DATES_SEEN VARCHAR,\n" +
      "    HOSTS_CURRENT VARCHAR,\n" +
      "    HOSTS_HISTORICAL VARCHAR,\n" +
      "    MD5S_NEW VARCHAR,\n" +
      "    MD5S_CURRENT VARCHAR,\n" +
      "    MD5S_HISTORICAL VARCHAR,\n" +
      "    PROCESSES_NEW VARCHAR,\n" +
      "    PROCESSES_CURRENT VARCHAR,\n" +
      "    PROCESSES_HISTORICAL VARCHAR,\n" +
      "    KEYS_NEW VARCHAR,\n" +
      "    KEYS_CURRENT VARCHAR,\n" +
      "    KEYS_HISTORICAL VARCHAR,\n" +
      "    PATHS_NEW VARCHAR,\n" +
      "    PATHS_CURRENT VARCHAR,\n" +
      "    PATHS_HISTORICAL VARCHAR,\n" +
      "    PORTS_NEW VARCHAR,\n" +
      "    PORTS_CURRENT VARCHAR,\n" +
      "    PORTS_HISTORICAL VARCHAR,\n" +
      "    HOST_NAME VARCHAR,\n" +
      "    CONSTRAINT PK PRIMARY KEY ( DATE_TIME,  TYPE, TYPE_VALUE, PIVOTS )\n)"
    val createTaniumStatsStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
    createTaniumStatsStmt.execute

    // Some data for andre brown (laptop801)
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-02-01T00:00:00.000Z', 'HOST', '8a310d9bb0507822493343146ca40e60', null, null, '0', '0', '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop801', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\Syste.31\\\\rdpclip.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null, 'laptop801')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // And the md5 rows to match up with the md5s_new in the andre_brown HOST row
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '622d21c40a25f9834a03bfd5ff4710c1', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', null, " +
      "'622d21c40a25f9834a03bfd5ff4710c1', " +
      "'622d21c40a25f9834a03bfd5ff4710c1', " +
      "null, 'cmd.exe', 'cmd.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Windows\\\\system32\\\\cmd.exe', null," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '78d3824650a866f3c38ae0079fc7e3dd', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop804|laptop815|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd', " +
      "'78d3824650a866f3c38ae0079fc7e3dd', " +
      "null, 'LiteAgent.exe', 'LiteAgent.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '0db1ccd7230fdee1adb04e536ea60759', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop804|laptop815|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', null, " +
      "'0db1ccd7230fdee1adb04e536ea60759', " +
      "'0db1ccd7230fdee1adb04e536ea60759', " +
      "null, 'TaniumClient.exe', 'TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe', null," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '1b100b5fc879b899f9ef85392c90a79c', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', null, " +
      "'1b100b5fc879b899f9ef85392c90a79c', " +
      "'1b100b5fc879b899f9ef85392c90a79c', " +
      "null, 'fdhost.exe', 'fdhost.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe', null," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', 'c5e1fe7db2202d37ba9a634e7f230a44', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', null, " +
      "'c5e1fe7db2202d37ba9a634e7f230a44', " +
      "'c5e1fe7db2202d37ba9a634e7f230a44', " +
      "null, 'fdlauncher.exe', 'fdlauncher.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe', null," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '345b45be09381d2011eb7f9ac11d8ac4', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', null, " +
      "'345b45be09381d2011eb7f9ac11d8ac4', " +
      "'345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'firefox.exe', 'firefox.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe', null," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // Some data for mark ingram (laptop836)
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-02-02T00:00:00.000Z', 'HOST', '1c7998846f7ad598b5e39d1c83f348ef', null, null, '0', '0', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop836', 'laptop836', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|c5e1fe7db2202d37ba9a634e7f230a44|622d21c40a25f9834a03bfd5ff4710c1|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|fdhost.exe|firefox.exe|rdpclip.exe|TaniumClient.exe|cmd.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe'," +
      "null, null, null, 'laptop836')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // And the md5 data to match up to the md5s above
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'MD5', '78d3824650a866f3c38ae0079fc7e3dd', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', 'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'78d3824650a866f3c38ae0079fc7e3dd', " +
      "'78d3824650a866f3c38ae0079fc7e3dd', " +
      "null, 'LiteAgent.exe', 'LiteAgent.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', 'C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe'," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'MD5', '0db1ccd7230fdee1adb04e536ea60759', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop804|laptop815|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', 'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop804|laptop815|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'0db1ccd7230fdee1adb04e536ea60759', " +
      "'0db1ccd7230fdee1adb04e536ea60759', " +
      "null, 'TaniumClient.exe', 'TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe', 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe'," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'MD5', '1b100b5fc879b899f9ef85392c90a79c', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', 'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'1b100b5fc879b899f9ef85392c90a79c', " +
      "'1b100b5fc879b899f9ef85392c90a79c', " +
      "null, 'fdhost.exe', 'fdhost.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe', 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe'," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'MD5', 'c5e1fe7db2202d37ba9a634e7f230a44', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', 'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'c5e1fe7db2202d37ba9a634e7f230a44', " +
      "'c5e1fe7db2202d37ba9a634e7f230a44', " +
      "null, 'fdlauncher.exe', 'fdlauncher.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe', 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe'," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'MD5', '622d21c40a25f9834a03bfd5ff4710c1', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', 'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'622d21c40a25f9834a03bfd5ff4710c1', " +
      "'622d21c40a25f9834a03bfd5ff4710c1', " +
      "null, 'cmd.exe', 'cmd.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Windows\\\\system32\\\\cmd.exe', 'C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Windows\\\\system32\\\\cmd.exe'," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'MD5', '345b45be09381d2011eb7f9ac11d8ac4', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', 'laptop806|laptop817|laptop807|laptop818|laptop819|laptop808|laptop809|laptop813|laptop802|laptop814|laptop803|laptop836|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'345b45be09381d2011eb7f9ac11d8ac4', " +
      "'345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'firefox.exe', 'firefox.exe', null," +
      "null, null, null, null, 'C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe', 'C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe'," +
      "null, null, null)"
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
      val entityThreatSql: String = "DROP TABLE IF EXISTS " + EntityThreat.getName(configuration)
      val entityThreatStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
      if(entityThreatStmt!=null) entityThreatStmt.execute
      val taniumStatsSql: String = "DROP TABLE IF EXISTS " + TaniumStats.getName(configuration)
      val taniumStatsStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
      taniumStatsStmt.execute
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

class DetectorHomeDaoEndpointTest {
  private val detectorHomeDao = new DetectorHomeDao(DetectorHomeDaoEndpointTest.configuration)

  @Test
  def testGetEntityCards(): Unit = {
    // Test for local endpoint card
    val localEndpointCards: Map[collection.mutable.Map[String, Any], ListBuffer[mutable.Map[String, Any]]] = detectorHomeDao.getEntityCards("2016-02-01T00:00:00.000Z",
      "2016-02-02T00:00:00.000Z", "192.168.1.31", "andre_brown", "laptop801", "54-0C-75-00-E2-12", DetectorHomeDaoEndpointTest.featureServiceCache)
    localEndpointCards.foreach { case (key, value) =>
      val featureDesc = key.getOrElse("featureDesc", null).asInstanceOf[Option[String]]
      val modelId = key.getOrElse("modelId", null).asInstanceOf[Option[Int]]
      val riskScore = key.getOrElse("riskScore", null).asInstanceOf[Option[Double]]
      val minDate = key.getOrElse("minDate", null)
      val maxDate = key.getOrElse("maxDate", null)
      assertEquals("Local Newly Observed Process", featureDesc.get)
      assertEquals(9, modelId.get)
      assertEquals(0.0, riskScore.get, 0.01)
      assertEquals("2016-02-01T00:00:00.000Z", minDate)
      assertEquals("2016-02-01T00:00:00.000Z", maxDate)
      for (resultMap <- value) {
        val featureDesc = resultMap.getOrElse("featureDesc", null)
        val md5 = resultMap.getOrElse("md5", null)
        assertEquals("Local Newly Observed Process", featureDesc)
        assertTrue(md5.equals("78d3824650a866f3c38ae0079fc7e3dd") || md5.equals("0db1ccd7230fdee1adb04e536ea60759") || md5.equals("1b100b5fc879b899f9ef85392c90a79c") ||
          md5.equals("622d21c40a25f9834a03bfd5ff4710c1") || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44") || md5.equals("345b45be09381d2011eb7f9ac11d8ac4") )
      }
    }

    // test global endpoint card
    val globalEndpointCards: Map[collection.mutable.Map[String, Any], ListBuffer[mutable.Map[String, Any]]] = detectorHomeDao.getEntityCards("2016-01-02T00:00:00.000Z",
      "2016-02-03T00:00:00.000Z", "192.168.1.66", "mark_ingram", "laptop836", "B9-BA-E0-A7-82-35", DetectorHomeDaoEndpointTest.featureServiceCache)
    assertEquals(1, globalEndpointCards.size)
    globalEndpointCards.foreach { case (key, value) =>
      val featureDesc = key.getOrElse("featureDesc", null).asInstanceOf[Option[String]]
      val modelId = key.getOrElse("modelId", null).asInstanceOf[Option[Int]]
      val riskScore = key.getOrElse("riskScore", null).asInstanceOf[Option[Double]]
      val minDate = key.getOrElse("minDate", null)
      val maxDate = key.getOrElse("maxDate", null)
      assertEquals("Global Newly Observed Process", featureDesc.get)
      assertEquals(10, modelId.get)
      assertEquals(0.0, riskScore.get, 0.01)
      assertEquals("2016-02-01T00:00:00.000Z", minDate)
      assertEquals("2016-02-01T00:00:00.000Z", maxDate)
      for (resultMap <- value) {
        val featureDesc = resultMap.getOrElse("featureDesc", null)
        val md5 = resultMap.getOrElse("md5", null)
        assertEquals("Global Newly Observed Process", featureDesc)
        assertTrue(md5.equals("78d3824650a866f3c38ae0079fc7e3dd") || md5.equals("0db1ccd7230fdee1adb04e536ea60759") || md5.equals("1b100b5fc879b899f9ef85392c90a79c") ||
          md5.equals("622d21c40a25f9834a03bfd5ff4710c1") || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44") || md5.equals("345b45be09381d2011eb7f9ac11d8ac4") )
      }
    }
  }
  @Test
  def testAutoCompleteHostName(): Unit ={
    val autoCompleteCache : AutoCompleteCache  = new AutoCompleteCache(DetectorHomeDaoEndpointTest.configuration)
    var success: java.util.List[String] = null

    success = detectorHomeDao.getAutoCompleteResults("l", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("la", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("lap", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("lapt", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("lapto", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("laptop", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("laptop8", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(5, success.size())

    success = detectorHomeDao.getAutoCompleteResults("laptop80", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(4, success.size())

    success = detectorHomeDao.getAutoCompleteResults("laptop802", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("802", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())
  }

  @Test
  def testAutoCompleteHostNameOnAll(): Unit ={
    val autoCompleteCache : AutoCompleteCache  = new AutoCompleteCache(DetectorHomeDaoEndpointTest.configuration)
    var success: java.util.Map[String, java.util.List[String]] = null

    success = detectorHomeDao.getAutoCompleteResultsOnAll("l", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("la", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("lap", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("lapt", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("lapto", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("laptop", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("laptop8", autoCompleteCache)
    assertNotNull(success)
    assertEquals(5, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("laptop80", autoCompleteCache)
    assertNotNull(success)
    assertEquals(4, success.get("hostNames").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("laptop802", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.get("hostNames").size())
    assertEquals("laptop802", success.get("hostNames").get(0))

    success = detectorHomeDao.getAutoCompleteResultsOnAll("win-", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.get("hostNames").size())
    assertEquals("WIN-OSNMCI3GJJ1", success.get("hostNames").get(0))

    success = detectorHomeDao.getAutoCompleteResultsOnAll("win_", autoCompleteCache)
    assertNotNull(success)
    assertEquals(3, success.get("hostNames").size())
    assertEquals("WIN_OSNMCI3GJJ2", success.get("hostNames").get(0))
    //ensure that only matched fields are populated
    assertNull(success.get("ipAddresses"))
    assertNull(success.get("macAddresses"))
    assertNull(success.get("userNames"))
  }
  @Test
  def testAutoCompleteIpAddress(): Unit ={
    val autoCompleteCache : AutoCompleteCache  = new AutoCompleteCache(DetectorHomeDaoEndpointTest.configuration)
    var success: java.util.List[String] = null

    val map : java.util.Map[String, java.util.List[String]] = detectorHomeDao.getAutoCompleteResultsOnAll("192.168.1.33", autoCompleteCache)
    assertNotNull(map)
    assertNotNull(map.get("ipAddresses"))
    assertEquals(1, map.get("ipAddresses").size())
    assertEquals("192.168.1.33", map.get("ipAddresses").get(0))

    success = detectorHomeDao.getAutoCompleteResults("1", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("19", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.1", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.16", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.168", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.168.", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.168.1", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.168.1.", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.168.1.3", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(9, success.size())

    success = detectorHomeDao.getAutoCompleteResults("192.168.1.33", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("33", "sourceIp", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())
  }

  @Test
  def testAutoCompleteIpAddressAll(): Unit ={
    val autoCompleteCache : AutoCompleteCache  = new AutoCompleteCache(DetectorHomeDaoEndpointTest.configuration)
    var success:java.util.Map[String, java.util.List[String]] = null

    success = detectorHomeDao.getAutoCompleteResultsOnAll("1",  autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("19", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.1", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.16", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.168", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.168.", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.168.1", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.168.1.",  autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.get("ipAddresses").size())

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.168.1.3", autoCompleteCache)
    assertNotNull(success)
    assertEquals(9, success.get("ipAddresses").size())
    assertEquals("192.168.1.31", success.get("ipAddresses").get(0))

    success = detectorHomeDao.getAutoCompleteResultsOnAll("192.168.1.33", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.get("ipAddresses").size())
    assertEquals("192.168.1.33", success.get("ipAddresses").get(0))

  }
  @Test
  def autoCompleteResultsTest() = {
    val mapper: ObjectMapper = Jackson.newObjectMapper
    val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
    val confFile: String = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"

    val failConf: FeatureServiceConfiguration = new ConfigurationFactory[FeatureServiceConfiguration](classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))
    failConf.setZkQuorum("127.0.0.1:" + "2020")
    failConf.setSolrQuorum("127.0.0.1:" + "2020" + "/solr")
    failConf.getPhoenix.setHbaseRpcTimeout(0)
    failConf.getPhoenix.setQueryTimeoutMs(0)
    failConf.getPhoenix.setHbaseClientRetriesNumber("0")
    failConf.getPhoenix.setHbaseClientPause("0")
    failConf.getPhoenix.setZookeeperRecoveryRetry("0")
    failConf.getPhoenix.setZookeeperRecoveryRetryIntervalmill("0")

    val detectorHomeDao = new DetectorHomeDao(failConf)

    val autoCompleteCache : AutoCompleteCache  = new AutoCompleteCache(failConf)

    val fail: java.util.List[String] = detectorHomeDao.getAutoCompleteResults("laptop802", "hostName",autoCompleteCache)
    assertNotNull(fail)
    // this change is because now we are storing the data on the disk
    // so there is pretty much no way to say the data returned is 0
    // earlier we used to save in memory, so it was possible to assert to 0
    assertEquals(1, fail.size())

    //chang the configuration to success configuration
    autoCompleteCache.setConf(DetectorHomeDaoEndpointTest.configuration)
    val success: java.util.List[String] = detectorHomeDao.getAutoCompleteResults("laptop802", "hostName",autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())
    assertEquals("laptop802", success.get(0))
  }

  @Test
  def testBugE82679(): Unit ={

    val autoCompleteCache : AutoCompleteCache  = new AutoCompleteCache(DetectorHomeDaoEndpointTest.configuration)

    val map : java.util.Map[String, java.util.List[String]] = detectorHomeDao.getAutoCompleteResultsOnAll("WIN-", autoCompleteCache)
    assertNotNull(map)
    assertNotNull(map.get("hostNames"))
    assertEquals(1, map.get("hostNames").size())
    assertEquals("WIN-OSNMCI3GJJ1", map.get("hostNames").get(0))

    var success: java.util.List[String] = null

    success = detectorHomeDao.getAutoCompleteResults("L", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LA", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAP", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAPT", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAPTO", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAPTOP", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(10, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAPTOP8", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(5, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAPTOP80", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(4, success.size())

    success = detectorHomeDao.getAutoCompleteResults("LAPTOP802", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("802", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("WIN-", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("WIN-OSNMCI3GJJ1", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("win-", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("win-osnmci3GJJ1", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("win_osnmci3GJJ2", "hostName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("bri", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("brian-", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("brian-quick", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("carson", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("carson_", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("carson_palmer", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())

    success = detectorHomeDao.getAutoCompleteResults("charles_clay", "userName", autoCompleteCache)
    assertNotNull(success)
    assertEquals(1, success.size())
  }
}
