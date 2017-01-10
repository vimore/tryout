package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import java.util
import javax.validation.{Validation, Validator}

import com.e8.test.HBaseTestBase
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.cache.FeatureResponseCache
import com.securityx.modelfeature.common.inputs.TimeSeriesInput
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{HostPropertiesTracker, TimeSeriesDao, PhoenixUtils}
import com.securityx.modelfeature.utils.TaniumStats
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable._

object TimeSeriesDaoTest extends HBaseTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private val featureServiceCache: FeatureServiceCache = null
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
      "    CONSTRAINT PK PRIMARY KEY ( DATE_TIME,  TYPE, TYPE_VALUE, PIVOTS )\n)"
    val createStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
    createStmt.execute

    var upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-01T00:00:00.000Z', 'HOST', 'laptop801', null, null, '0', '0', '2016-02-01T06:00:00Z|2016-02-01T18:00:00Z', " +
      "'laptop801', null, " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\Syste.31\\\\rdpclip.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Windows\\\\syste.31\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', null," +
      "null, null, null)"
    var upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-02T00:00:00.000Z', 'HOST', 'laptop802', null, null, '0', '0', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop802', 'laptop802', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|c5e1fe7db2202d37ba9a634e7f230a44|622d21c40a25f9834a03bfd5ff4710c1|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|c5e1fe7db2202d37ba9a634e7f230a44|622d21c40a25f9834a03bfd5ff4710c1|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|fdhost.exe|firefox.exe|rdpclip.exe|TaniumClient.exe|cmd.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', " +
      "'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe'," +
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
      "values ('2016-02-02T00:00:00.000Z', 'HOST', 'laptop836', null, null, '2', '1', '2016-02-01T06:00:00Z|2016-02-02T18:00:00Z', " +
      "'laptop836', 'laptop836', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|c5e1fe7db2202d37ba9a634e7f230a44|622d21c40a25f9834a03bfd5ff4710c1|345b45be09381d2011eb7f9ac11d8ac4', " +
      "'78d3824650a866f3c38ae0079fc7e3dd|0db1ccd7230fdee1adb04e536ea60759|1b100b5fc879b899f9ef85392c90a79c|622d21c40a25f9834a03bfd5ff4710c1|c5e1fe7db2202d37ba9a634e7f230a44|345b45be09381d2011eb7f9ac11d8ac4', " +
      "null, 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|fdhost.exe|firefox.exe|rdpclip.exe|TaniumClient.exe|cmd.exe', 'LiteAgent.exe|fdlauncher.exe|LogonUI.exe|firefox.exe|fdhost.exe|rdpclip.exe|cmd.exe|TaniumClient.exe', null," +
      "null, null, null, null, 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe', 'C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Windows\\\\system32\\\\cmd.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdlauncher.exe|C:\\\\Program Files\\\\Microsoft SQL Server\\\\MSSQL12.MSSQLSERVER\\\\MSSQL\\\\Binn\\\\fdhost.exe|C:\\\\Program Files\\\\Amazon\\\\XenTools\\\\LiteAgent.exe'," +
      "null, null, null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // Data for getTimeSeriesSearchResultsTest()
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, " +
      "MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-15T00:00:00.000Z', 'PATH', 'C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\MicroMedia\\\\rundll32.dll.exe', null, 'Y', '2', '1', '2016-02-15T06:00:00Z|2016-02-15T18:00:00Z', " +
      "'laptop836', null, " +
      "null, " +
      "'a24099cc63c9499b80a362885483d4e6', " +
      "null, null, 'rundll32.dll.exe', null," +
      "null, null, null, null, 'C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\MicroMedia\\\\rundll32.dll.exe', null," +
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
      "values ('2016-02-15T00:00:00.000Z', 'PROC', 'rundll32.dll.exe', null, 'Y', '4', '3', '2016-02-15T06:00:00Z|2016-02-15T18:00:00Z', " +
      "'laptop836', null, " +
      "null, " +
      "'a24099cc63c9499b80a362885483d4e6', " +
      "null, null, 'rundll32.dll.exe', null," +
      "null, null, null, null, 'C:\\\\Users\\\\user\\\\AppData\\\\Local\\\\Temp\\\\MicroMedia\\\\rundll32.dll.exe', null," +
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
      "values ('2016-02-15T00:00:00.000Z', 'PORT', 'null/6667', null, 'Y', '4', '3', '2016-02-15T06:00:00Z|2016-02-15T18:00:00Z', " +
      "'laptop836', null, " +
      "null, " +
      "'a24099cc63c9499b80a362885483d4e6', " +
      "null, null, 'Host Process for Windows Services', null," +
      "null, null, null, null, 'rundll32.dll.exe', null," +
      "null, 'null/6667', null)"
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

class TimeSeriesDaoTest {
  val timeSeriesDao = new TimeSeriesDao(TimeSeriesDaoTest.configuration)

  @Test
  def getTimeSeriesTypeGroupTest(): Unit = {
    val resultModel9 = timeSeriesDao.getTimeSeriesTypeGroup(9, "2016-02-02T00:00:00.000Z", "2016-02-03T00:00:00.000Z", "HOST", "", "", TimeSeriesDaoTest.featureServiceCache)
    assertNotNull(resultModel9)
    resultModel9 match {
      case listResult: List[HostPropertiesTracker] =>
        for (hostPropTracker <- listResult) {
          assertEquals("2016-02-02", hostPropTracker.dateTime)
          assertEquals(2.0, hostPropTracker.riskScore, 0.01)
          assertEquals(6, hostPropTracker.newAutoMd5Count)
          for (md5 <- hostPropTracker.allAutoMd5Set) {
            assertTrue(md5 == "78d3824650a866f3c38ae0079fc7e3dd" || md5 == "0db1ccd7230fdee1adb04e536ea60759" || md5 == "1b100b5fc879b899f9ef85392c90a79c" || md5 == "622d21c40a25f9834a03bfd5ff4710c1" ||
              md5 == "c5e1fe7db2202d37ba9a634e7f230a44" || md5 == "345b45be09381d2011eb7f9ac11d8ac4")
          }
        }
      case _ => assertTrue("unexpected result from getTimeSeriesGroup", false)
    }

    val resultModel10 = timeSeriesDao.getTimeSeriesTypeGroup(10, "2016-02-02T00:00:00.000Z", "2016-02-03T00:00:00.000Z", "HOST", "", "", TimeSeriesDaoTest.featureServiceCache)
    assertNotNull(resultModel10)
    resultModel10 match {
      case listResult: List[HostPropertiesTracker] =>
        for (hostPropTracker <- listResult) {
          assertEquals("2016-02-02", hostPropTracker.dateTime)
          assertEquals(1.0, hostPropTracker.riskScore, 0.01)
          assertEquals(6, hostPropTracker.newAutoMd5Count)
          for (md5 <- hostPropTracker.allAutoMd5Set) {
            assertTrue(md5 == "78d3824650a866f3c38ae0079fc7e3dd" || md5 == "0db1ccd7230fdee1adb04e536ea60759" || md5 == "1b100b5fc879b899f9ef85392c90a79c" || md5 == "622d21c40a25f9834a03bfd5ff4710c1" ||
              md5 == "c5e1fe7db2202d37ba9a634e7f230a44" || md5 == "345b45be09381d2011eb7f9ac11d8ac4")
          }
        }
      case _ => assertTrue("unexpected result from getTimeSeriesGroup", false)
    }
  }

  @Test
  def getTimeSeriesSearchResultsTest(): Unit = {
    val inputModel9 = new TimeSeriesInput()
    inputModel9.setModelId(9)
    inputModel9.setStartTime("2016-02-15T00:00:00.000Z")
    inputModel9.setEndTime("2016-02-16T00:00:00.000Z")
    val facetList: util.ArrayList[util.Map[String, String]] = new util.ArrayList[util.Map[String, String]]()
    val facet: util.Map[String, String] = new util.HashMap[String, String]()
    facet.put("typeField", "new")
    facet.put("group", "all")
    facet.put("groupId", null)
    facetList.add(facet)
    inputModel9.setFacets(facetList)

    // In spite of getTimeSeriesSearchResults taking many solr client objects, none of them will be used for model 9 or 10.  So they can all be null
    val resultModel9 = timeSeriesDao.getTimeSeriesSearchResults(null, null, null, null, null, inputModel9, TimeSeriesDaoTest.featureServiceCache)
    assertNotNull(resultModel9)
    resultModel9 match {
      case listResult: ListBuffer[Map[String, Any]] =>
        assertEquals(1, listResult.size)
        for (map <- listResult) {
          assertEquals("Y", map.getOrElse("newlyObserved", null))
          assertEquals("null/6667", map.getOrElse("portsCurrent", null))
          assertEquals("4", map.getOrElse("riskScore", null))
        }
      case _ => assertTrue("unexpected result from getTimeSeriesGroup", false)
    }

    val inputModel10 = new TimeSeriesInput()
    inputModel10.setModelId(10)
    inputModel10.setStartTime("2016-02-15T00:00:00.000Z")
    inputModel10.setEndTime("2016-02-16T00:00:00.000Z")
    val facetList10: util.ArrayList[util.Map[String, String]] = new util.ArrayList[util.Map[String, String]]()
    val facet10: util.Map[String, String] = new util.HashMap[String, String]()
    facet10.put("typeField", "new")
    facet10.put("group", "all")
    facet10.put("groupId", null)
    facetList10.add(facet)
    inputModel10.setFacets(facetList10)

    // In spite of getTimeSeriesSearchResults taking many solr client objects, none of them will be used for model 9 or 10.  So they can all be null
    val resultModel10 = timeSeriesDao.getTimeSeriesSearchResults(null, null, null, null, null, inputModel10, TimeSeriesDaoTest.featureServiceCache)
    assertNotNull(resultModel10)
    resultModel10 match {
      case listResult: ListBuffer[Map[String, Any]] =>
        assertEquals(1, listResult.size)
        for (map <- listResult) {
          assertEquals("Y", map.getOrElse("newlyObserved", null))
          assertEquals("null/6667", map.getOrElse("portsCurrent", null))
          assertEquals("3", map.getOrElse("riskScore", null))
        }
      case _ => assertTrue("unexpected result from getTimeSeriesGroup", false)
    }
  }

}
