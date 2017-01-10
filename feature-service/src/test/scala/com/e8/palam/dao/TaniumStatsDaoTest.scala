package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import java.util
import javax.validation.{Validation, Validator}
import com.securityx.modelfeature.utils.TaniumStats

import scala.collection.mutable.{Map => MutableMap, ListBuffer}


import com.e8.test.HBaseTestBase
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.cache.FeatureResponseCache
import com.securityx.modelfeature.common.inputs.EndPointAnalytics
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{TaniumStatsDao, DetectorHomeDao, PhoenixUtils}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object TaniumStatsDaoTest extends HBaseTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  private var featureResponseCache: FeatureResponseCache = null



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
    featureResponseCache = new FeatureResponseCache(configuration)
    featureServiceCache = new FeatureServiceCache(configuration)

    val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
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
    val createStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
    createStmt.execute

    // Insert some PORT rows
    var upsertSql: String = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-06-10T00:00:00.000Z', 'PORT', 'TCP/49833', null, null, '0', '0', '2016-02-21T22:39:02Z|2016-05-17T21:51:48Z', " +
      "null, 'RDP-GW|WIN-OSNMCI3GJJ1', null, null, '0db1ccd7230fdee1adb04e536ea60759|f02c17c6679c389cf49fe840f8df8089', null, null, 'System Idle Process|Ec2Config.exe|TaniumClient.exe'," +
      "null, null, null, null, null, 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe', " +
      "null, null, 'TCP/49833')"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-06-10T00:00:00.000Z', 'PORT', 'TCP/49834', null, null, '0', '0', '2016-02-24T01:21:20Z|2016-05-17T16:00:02Z', " +
      "null, 'RDP-GW|WIN-OSNMCI3GJJ1', null, null, '0db1ccd7230fdee1adb04e536ea60759|f02c17c6679c389cf49fe840f8df8089', null, null, 'System Idle Process|TaniumClient.exe|Ec2Config.exe'," +
      "null, null, null, null, null, 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe', " +
      "null, null, 'TCP/49833')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-16T00:00:00.000Z', 'PORT', 'TCP/49169', '', 'Y', '10', null, '2016-07-16T07:11:49Z|2016-07-16T07:11:49Z', " +
      "'WIN-OSNMCI3GJJ1', '', '', '', '', '', 'System Idle Process', '', '', '', '', '', '', '', '', 'TCP/49169', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-16T00:00:00.000Z', 'PORT', 'TCP/49191', '', 'Y', '10', null, '2016-07-16T12:48:31Z|2016-07-16T12:48:31Z', " +
      "'RDP-GW', '', '', '', '', '', 'System Idle Process', '', '', '', '', '', '', '', '', 'TCP/49191', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-16T00:00:00.000Z', 'PORT', 'TCP/49344', '', 'Y', '10', null, '2016-07-16T12:58:34Z|2016-07-16T18:10:07Z', " +
      "'RDP-GW|WIN-OSNMCI3GJJ1', '', '', '', '', '', 'System Idle Process', '', '', '', '', '', '', '', '', 'TCP/49344', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // Insert some HOST rows
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-06-11T00:00:00.000Z', 'HOST', '1c7998846f7ad598b5e39d1c83f348ef', null, null, '0.5', '5', '2016-06-11:21:20Z|2016-06-17T16:00:02Z', " +
      "'laptop801', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\Syste.31\\\\rdpclip.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null, 'laptop801')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-06-11T00:00:00.000Z', 'HOST', 'ad93110b797d293d1810a5306858df4f', null, null, '0', '0', '2016-04-24T01:21:20Z|2016-06-17T16:00:02Z', " +
      "'laptop802', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null, 'laptop802')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-06-11T00:00:00.000Z', 'HOST', '43ff7482082fc9c798cdc62878b9d595', null, null, '0', '0', '2016-02-24T01:21:20Z|2016-06-17T16:00:02Z', " +
      "'laptop803', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null, 'laptop803')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // Insert some MD5 rows
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-06-10T00:00:00.000Z', 'MD5', '1b100b5fc879b899f9ef85392c90a79c', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-24T01:21:20Z|2016-06-17T16:00:02Z', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "null, " +
      "'1b100b5fc879b899f9ef85392c90a79c', '1b100b5fc879b899f9ef85392c90a79c', null, 'fdhost.exe', 'fdhost.exe', null, " +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe', null, " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-06-10T00:00:00.000Z', 'MD5', '0db1ccd7230fdee1adb04e536ea60759', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-24T01:21:20Z|2016-06-17T16:00:02Z', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "null, '0db1ccd7230fdee1adb04e536ea60759', '0db1ccd7230fdee1adb04e536ea60759', null, 'TaniumClient.exe', 'TaniumClient.exe', " +
      "null, null, null, null, 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe', 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe', " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // Insert some data for isNox testing
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-17T00:00:00.000Z', 'MD5', 'a24099cc63c9499b80a362885483d4e6', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-15T06:00:00Z|2016-02-17T18:00:00Z', " +
      "'laptop836', " +
      "'laptop836', " +
      "null, 'a24099cc63c9499b80a362885483d4e6', 'a24099cc63c9499b80a362885483d4e69', null, 'Host Process for Windows Services|Redirector', 'rundll32.dll.exe', " +
      "null, null, null, null, 'rundll32.dll.exe| c:\\\\path\\\\to\\\\rundll32.dll.exe \\\\/startup', 'C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\MicroMedia\\\\rundll32.dll.exe', " +
      "null, 'null/6667', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-16T00:00:00.000Z', 'MD5', 'a24099cc63c9499b80a362885483d4e6', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-02-15T06:00:00Z|2016-02-16T18:00:00Z', " +
      "'laptop836', " +
      "'laptop836', " +
      "null, 'a24099cc63c9499b80a362885483d4e6', 'a24099cc63c9499b80a362885483d4e69', null, 'rundll32.dll.exe', 'rundll32.dll.exe', " +
      "null, null, null, null, 'C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\MicroMedia\\\\rundll32.dll.exe', 'C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\MicroMedia\\\\rundll32.dll.exe', " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // Some data on a different date to test entity cards
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-02-01T00:00:00.000Z', 'HOST', '8a310d9bb0507822493343146ca40e60', null, null, '0', null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop812', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|c5e1fe7db2202d37ba9a634e7f230a44|622d21c40a25f9834a03bfd5ff4710c1|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\Syste.31\\\\rdpclip.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null, 'laptop812')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '0db1ccd7230fdee1adb04e536ea60759', null, 'Default:21/21|Laptop:21/21', '10', '10', '2016-01-30T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "null, '0db1ccd7230fdee1adb04e536ea60759', '0db1ccd7230fdee1adb04e536ea60759', null, 'TaniumClient.exe', 'TaniumClient.exe', " +
      "null, null, null, null, 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe', 'C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe', " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '1b100b5fc879b899f9ef85392c90a79c', null, 'Default:21/21|Laptop:21/21', null, null, '2016-01-30T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop818|laptop807|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop812|laptop801', " +
      "null, " +
      "'1b100b5fc879b899f9ef85392c90a79c', '1b100b5fc879b899f9ef85392c90a79c', null, 'fdhost.exe', 'fdhost.exe', null, " +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe', null, " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '345b45be09381d2011eb7f9ac11d8ac4', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', " +
      "null, " +
      "'345b45be09381d2011eb7f9ac11d8ac4', '345b45be09381d2011eb7f9ac11d8ac4', null, 'firefox.exe', 'firefox.exe', null, " +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\firefox.exe', null, " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '622d21c40a25f9834a03bfd5ff4710c1', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', " +
      "null, " +
      "'622d21c40a25f9834a03bfd5ff4710c1', '622d21c40a25f9834a03bfd5ff4710c1', null, 'cmd.exe', 'cmd.exe', null, " +
      "null, null, null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\cmd.exe', null, " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', '78d3824650a866f3c38ae0079fc7e3dd', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', " +
      "null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd', '78d3824650a866f3c38ae0079fc7e3dd', null, 'LiteAgent.exe', 'LiteAgent.exe', null, " +
      "null, 'test value to cause autorun to be true', null, null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\LiteAgent.exe', null, " +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, " +
      "HOSTS_HISTORICAL, " +
      "MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'MD5', 'c5e1fe7db2202d37ba9a634e7f230a44', null, 'Default:21/21|Laptop:21/21', null, null, '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop806|laptop817|laptop807|laptop818|laptop808|laptop819|laptop809|laptop802|laptop813|laptop836|laptop803|laptop814|laptop815|laptop804|laptop805|laptop816|laptop820|laptop810|laptop811|laptop801|laptop812', " +
      "null, " +
      "'c5e1fe7db2202d37ba9a634e7f230a44', 'c5e1fe7db2202d37ba9a634e7f230a44d', null, 'fdlauncher.exe', 'fdlauncher.exe', null, " +
      "null, null, 'another value to make autorun true', null, 'C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe', null, " +
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
      val taniumStatsSql: String = "DROP TABLE IF EXISTS " + TaniumStats.getName(configuration)
      val taniumStatsStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
      taniumStatsStmt.execute
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw ex
    } finally {
      HBaseTestBase.teardownHBase()
    }
  }

}

class TaniumStatsDaoTest {
  private val taniumStatsDao = new TaniumStatsDao(TaniumStatsDaoTest.configuration)

  @Test
  def getFirstEndpointDatapointTest(): Unit = {
    val results = taniumStatsDao.getFirstEndpointDatapoint()
    assertEquals(1, results.size)
    for (resultMap <- results) {
      val date = resultMap.get("firstTimeSeen").get
      assertEquals("2016-02-01T00:00:00.000Z", date)
    }
  }

  @Test
  def getEndpointAnalyticsDataTest(): Unit = {
    // Test get with PORT
    val portInput = new EndPointAnalytics
    portInput.setStartTime("2016-05-20T00:00:00.000Z")
    portInput.setEndTime("2016-06-20T00:00:00.000Z")
    portInput.setModelId(9)
    portInput.setNox(false)
    portInput.setTypeField("PORT")
    portInput.setTypeValue("")
    portInput.setLastSeenTypeValue("")
    portInput.setNumRows(20)
    val result = taniumStatsDao.getEndpointAnalyticsData(portInput, TaniumStatsDaoTest.featureResponseCache)
    assertEquals(1, result.size)
    for (resultMap <- result) {
      val total = resultMap.get("total").get
      assertEquals(2, total)
      val results = resultMap.get("results").get
      results match {
        case resultList: ListBuffer[MutableMap[String, Any]] =>
          assertEquals(2, resultList.size)
          for (resultHash <- resultList) {
            assertTrue(resultHash != null)
            assertEquals(1, resultHash.size)
            val port = resultHash.get("PORT").get
            assertTrue(port.equals("TCP/49833") || port.equals("TCP/49834"))
          }
        case _ => assertTrue(false)
      }
    }

    // Test get with MD5
    val md5Input = new EndPointAnalytics
    md5Input.setStartTime("2016-05-20T00:00:00.000Z")
    md5Input.setEndTime("2016-06-20T00:00:00.000Z")
    md5Input.setModelId(9)
    md5Input.setNox(false)
    md5Input.setTypeField("MD5")
    md5Input.setTypeValue("")
    md5Input.setLastSeenTypeValue("")
    md5Input.setNumRows(20)
    val md5result = taniumStatsDao.getEndpointAnalyticsData(md5Input, TaniumStatsDaoTest.featureResponseCache)
    assertEquals(1, md5result.size)
    for (resultMap <- md5result) {
      val total = resultMap.get("total").get
      assertEquals(2, total)
      val results = resultMap.get("results").get
      results match {
        case resultList: ListBuffer[MutableMap[String, Any]] =>
          assertEquals(2, resultList.size)
          for (resultHash <- resultList) {
            assertTrue(resultHash != null)
            assertEquals(1, resultHash.size)
            val md5 = resultHash.get("MD5").get
            assertTrue(md5.equals("0db1ccd7230fdee1adb04e536ea60759") || md5.equals("1b100b5fc879b899f9ef85392c90a79c"))
          }
        case _ => assertTrue(false)
      }
    }
  }

  @Test
  def getEndpointAnalyticsDataWithNoxTest(): Unit = {
    val md5NoxInput = new EndPointAnalytics
    md5NoxInput.setStartTime("2016-02-01T00:00:00.000Z")
    md5NoxInput.setEndTime("2016-02-20T00:00:00.000Z")
    md5NoxInput.setModelId(9)
    md5NoxInput.setNox(false)
    md5NoxInput.setTypeField("MD5")
    md5NoxInput.setTypeValue("")
    md5NoxInput.setLastSeenTypeValue("")
    md5NoxInput.setNumRows(20)
    md5NoxInput.setNox(true)
    val md5Noxresult = taniumStatsDao.getEndpointAnalyticsData(md5NoxInput, TaniumStatsDaoTest.featureResponseCache)
    assertEquals(1, md5Noxresult.size)
    for (resultMap <- md5Noxresult) {
      val total = resultMap.get("total").get
      assertEquals(7, total)
      val results = resultMap.get("results").get
      results match {
        case resultList: ListBuffer[MutableMap[String, Any]] =>
          assertEquals(7, resultList.size)
          for (resultHash <- resultList) {
            assertTrue(resultHash != null)
            assertEquals(1, resultHash.size)
            val md5 = resultHash.get("MD5").get
            assertTrue(md5.equals("a24099cc63c9499b80a362885483d4e6") || md5.equals("0db1ccd7230fdee1adb04e536ea60759") || md5.equals("622d21c40a25f9834a03bfd5ff4710c1")
              || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44") || md5.equals("345b45be09381d2011eb7f9ac11d8ac4")
              || md5.equals("1b100b5fc879b899f9ef85392c90a79c") || md5.equals("78d3824650a866f3c38ae0079fc7e3dd"))
          }
        case _ => assertTrue(false)
      }
    }
  }

  @Test
  def getEndpointAnalyticsDataByTypeValue = {
    val md5Input = new EndPointAnalytics
    md5Input.setStartTime("2016-02-01T00:00:00.000Z")
    md5Input.setEndTime("2016-02-20T00:00:00.000Z")
    md5Input.setModelId(9)
    md5Input.setNox(false)
    md5Input.setTypeField("MD5")
    md5Input.setTypeValue("0db1ccd7230fdee1adb04e536ea60759")
    md5Input.setLastSeenTypeValue("")
    md5Input.setNumRows(20)
    md5Input.setNox(true)
    val md5result = taniumStatsDao.getEndpointAnalyticsData(md5Input, TaniumStatsDaoTest.featureResponseCache)
    assertEquals(1, md5result.size)
    for (md5Details <- md5result) {
      val firstTimeSeen = md5Details.getOrElse("firstTimeSeen", "")
      assertEquals("2016-01-30T06:00:00Z", firstTimeSeen)
      val processes = md5Details.getOrElse("processes", null).asInstanceOf[mutable.HashSet[String]]
      assertEquals(1, processes.size)
      for (process <- processes) {
        assertEquals("TaniumClient.exe", process)
      }
      val md5s = md5Details.getOrElse("md5s", null).asInstanceOf[mutable.HashSet[String]]
      assertEquals(1, md5s.size)
      for (md5 <- md5s) {
        assertEquals("0db1ccd7230fdee1adb04e536ea60759", md5)
      }
    }

    val portInput = new EndPointAnalytics
    portInput.setStartTime("2016-07-01T00:00:00.000Z")
    portInput.setEndTime("2016-07-20T00:00:00.000Z")
    portInput.setModelId(9)
    portInput.setNox(false)
    portInput.setTypeField("PORT")
    portInput.setTypeValue("TCP/49344")
    portInput.setLastSeenTypeValue("")
    portInput.setNumRows(20)
    portInput.setNox(true)
    val portResult = taniumStatsDao.getEndpointAnalyticsData(portInput, TaniumStatsDaoTest.featureResponseCache)
    assertEquals(1, portResult.size)
    for (portDetails <- portResult) {
      val firstTimeSeen = portDetails.getOrElse("firstTimeSeen", "")
      assertEquals("2016-07-16T12:58:34Z", firstTimeSeen)
      val ports = portDetails.getOrElse("ports", null).asInstanceOf[mutable.HashSet[String]]
      assertEquals(1, ports.size)
      for (port <- ports) {
        assertEquals("TCP/49344", port)
      }
      val processes = portDetails.getOrElse("processes", null).asInstanceOf[mutable.HashSet[String]]
      assertEquals(1, processes.size)
      for (process <- processes) {
        assertEquals("System Idle Process", process)
      }
      val hosts = portDetails.getOrElse("hosts", null).asInstanceOf[mutable.HashSet[String]]
      assertEquals(2, hosts.size)
      for (host <- hosts) {
        assertTrue(host == "RDP-GW" || host == "WIN-OSNMCI3GJJ1")
      }
    }
  }

  @Test
  def getRiskScoreForHostTest(): Unit = {
    val risks = taniumStatsDao.getRiskScoreForHost("2016-05-11T00:00:00.000Z", "2016-07-12T00:00:00.000Z", "laptop801")
    assertEquals(1, risks.size)
    for (riskMap <- risks) {
      val riskScore = riskMap.getOrElse("riskScore", null).asInstanceOf[Double]
      val globalRiskScore = riskMap.getOrElse("globalRiskScore", null).asInstanceOf[Double]
      assertEquals(0.5d, riskScore, 0.01)
      assertEquals(1.0d, globalRiskScore, 0.01)
    }
  }

  @Test
  def getTaniumEntityCardsTest(): Unit = {
    val model9Cards = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    taniumStatsDao.getTaniumEntityCards("2016-02-01T00:00:00.000Z", "2016-02-02T00:00:00.000Z", "laptop812", "", useEntityId = false, 0.1, 9, TaniumStatsDaoTest.featureServiceCache, model9Cards)
    assertEquals(6, model9Cards.size)
    for (md5Map <- model9Cards) {
      val md5 = md5Map.getOrElse("md5", "")
      val isAutorun = md5Map.getOrElse("isAutorun", false).asInstanceOf[Boolean]

      assertTrue(md5.equals("78d3824650a866f3c38ae0079fc7e3dd") || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44")
        || md5.equals("0db1ccd7230fdee1adb04e536ea60759") || md5.equals("1b100b5fc879b899f9ef85392c90a79c")
        || md5.equals("622d21c40a25f9834a03bfd5ff4710c1") || md5.equals("345b45be09381d2011eb7f9ac11d8ac4"))

      if (md5.equals("78d3824650a866f3c38ae0079fc7e3dd") || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44")) {
        assertEquals(true, isAutorun)
      } else {
        assertEquals(false, isAutorun)
      }
    }

    val model10Cards = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    taniumStatsDao.getTaniumEntityCards("2016-02-01T00:00:00.000Z", "2016-02-02T00:00:00.000Z", "laptop812", "", useEntityId = false, 0.1, 10, TaniumStatsDaoTest.featureServiceCache, model10Cards)
    assertEquals(4, model10Cards.size)
    for (md5Map <- model10Cards) {
      val md5 = md5Map.getOrElse("md5", "")
      val isAutorun = md5Map.getOrElse("isAutorun", false).asInstanceOf[Boolean]

      assertTrue(md5.equals("78d3824650a866f3c38ae0079fc7e3dd") || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44")
        || md5.equals("622d21c40a25f9834a03bfd5ff4710c1") || md5.equals("345b45be09381d2011eb7f9ac11d8ac4"))

      if (md5.equals("78d3824650a866f3c38ae0079fc7e3dd") || md5.equals("c5e1fe7db2202d37ba9a634e7f230a44")) {
        assertEquals(true, isAutorun)
      } else {
        assertEquals(false, isAutorun)
      }
    }
  }
}