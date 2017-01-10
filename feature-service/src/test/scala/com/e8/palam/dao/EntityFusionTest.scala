package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import java.{lang, util}
import javax.validation.{Validation, Validator}

import com.e8.sparkle.commons.hbase.HBaseClient
import com.e8.sparkle.storage.hbase.rowobjects.{EntityRelationship, ColumnProperties, Entity, BaseRow}
import com.e8.sparkle.storage.hbase.tableobjects.{EntityRelationshipTable, EntityTable}
import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.EntityModelInfo
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.{EntityFusionConfiguration, FeatureServiceConfiguration}
import com.securityx.modelfeature.dao._
import com.securityx.modelfeature.resources.DetectorHomeFeature
import com.securityx.modelfeature.utils.{TaniumStats, EntityThreat, HBaseAccessConfiguration}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.apache.hadoop.hbase.{TableNotFoundException, TableNotEnabledException}
import org.junit.Assert._
import org.junit._

import scala.collection.mutable.ListBuffer


object EntityFusionTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val mapper2: ObjectMapper = new ObjectMapper()
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  private val hBaseClient: HBaseClient = new HBaseClient

  private var dhf: DetectorHomeFeature = null
  val propTestEntityId = "propTestEntity1"
  val propTestFusionDate1 = "2016-09-01T18:00:00.000Z"
  val propTestFusionDate2 = "2016-09-01T19:00:00.000Z"

  // Note! Every class that extends HBaseTestBase must implement a BeforeClass method, and that method must call
  // HBaseTestBase.HBaseSetup().  That call will start the local HBase server if it has not already been started,
  // which will allow this method to then continue with setup.
  // We expect HBase to have no tables in it when the test starts. It is up to the test to create any tables it needs
  // and insert any data. At the end of the test, each test must drop all tables so that any other tests that run
  // will start with a clean slate.
  @BeforeClass
  def startServers() {
    HBaseTestBase.setupHBase()

    mapper2.registerModule(new DefaultScalaModule)
    val confFile: String = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"
    configuration = new ConfigurationFactory[FeatureServiceConfiguration](classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))
    configuration.setZkQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort)
    configuration.setSolrQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort + "/solr")
    configuration.getEntityFusionConfiguration.setConversionDate("2016-08-01T00:00:00.000Z")
    // Set up mongo server connection
    MongoTestBase.setupMongo(configuration, true)
    featureServiceCache = new FeatureServiceCache(configuration)
    dhf = new DetectorHomeFeature(mapper2, configuration, featureServiceCache, null)

    //val hBaseConfig = new HBaseConfiguration(configuration.getZkQuorum, configuration.getZkQuorum, HBaseTestBase.utility.getZkCluster.getClientPort.toString)
    // Create HBase access objects
    val hBaseConfig = new HBaseAccessConfiguration("127.0.0.1", "127.0.0.1", HBaseTestBase.utility.getZkCluster.getClientPort.toString, configuration.getPhoenix.getSchema)
    val hBaseConn = hBaseClient.getConnection(hBaseConfig)
    val hBaseAdmin = hBaseConn.getAdmin

    // Create entity tables via HBase access
    val entityTable = new EntityTable
    entityTable.createTableIfNotExists(hBaseConn, hBaseConfig)
    val entity1 = createUserEntity("2016-08-01T14:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e1")
    val entity2 = createUserEntity("2016-08-01T15:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e2")
    val entity3 = createUserEntity("2016-08-02T14:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e3")
    val entity4 = createUserEntity("2016-08-02T15:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e4")
    val entity5 = createUserEntity("2016-08-02T16:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "MacBook-Air", null, "e5")
    val entity6 = createUserEntity("2016-08-02T17:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "MacBook-Air", null, "e6")
    val entity7 = createUserEntity("2016-08-01T06:00:00.000Z", "0:0:0:0:0:0:0:1", null, null, "w2k8r2-ad$", "e7")
    val entity8 = createUserEntity("2016-08-01T06:00:00.000Z", "127.0.0.1", null, null, "w2k8r2-ad$", "e8")
    val entity9 = createUserEntity("2016-08-01T14:00:00.000Z", "192.168.12.18", null, null, "w2k8r2-ad$", "e9")
    val entity10 = createUserEntity("2016-08-01T09:00:00.000Z", "0:0:0:0:0:0:0:1", null, null, "w2k8r2-ad$", "e10")
    val entity11 = createUserEntity("2016-08-01T09:00:00.000Z", "127.0.0.1", null, null, "w2k8r2-ad$", "e11")
    val entity12 = createUserEntity("2016-08-01T09:00:00.000Z", "192.168.12.18", null, null, "w2k8r2-ad$", "e12")
    val entity13 = createUserEntity("2016-08-01T09:00:00.000Z", "10.10.4.63", "A0:99:9B:0D:C6:E9", "Christophes-MBP", "christophe", "e13")
    val entity14 = createUserEntity("2016-08-02T16:00:00.000Z", "10.10.4.63", "A0:99:9B:0D:C6:E9", "Christophes-MBP", "christophe", "e14")
    val entity15 = createUserEntity("2016-08-02T16:00:00.000Z", "192.168.12.27", "12:7F:C8:56:84:17", "RDP-GW", "jyria", "e15")
    val entity16 = createUserEntity("2016-08-02T17:00:00.000Z", "10.10.4.63", "A0:99:9B:0D:C6:E9", "Christophes-MBP", "christophe", "e16")
    val entity17 = createUserEntity("2016-08-02T17:00:00.000Z", "192.168.12.27", "12:7F:C8:56:84:17", "RDP-GW", "administrator", "e17")
    val entity18 = createUserEntity("2016-08-02T18:00:00.000Z", "192.168.12.27", "12:7F:C8:56:84:17", "RDP-GW", "administrator", "e18")
    val entity19 = createHostEntity("2016-11-03T16:00:00.000Z", "192.168.1.13", "54-0C-75-00-E2-12", "laptop801", null, "c3c46ec3ab47d5ee70e3f60283015a24")
    val entity20 = createHostEntity("2016-11-04T18:00:00.000Z", "192.168.1.31", "54-0C-75-00-E2-12", "laptop801", null, "c3c46ec3ab47d5ee70e3f60283015a24")
    val entity21 = createUserEntity("2016-11-03T18:00:00.000Z", "192.168.1.29", "AA:7F:C8:56:94:71", "RDP-GW", "administrator", "e21")
    val entities: Array[BaseRow] = Array(entity1, entity2, entity3, entity4, entity5, entity6, entity7, entity8, entity9, entity10, entity11, entity12,
      entity13, entity14, entity15, entity16, entity17, entity18, entity19, entity20, entity21)
    entityTable.putRows(hBaseConn, entities, false, hBaseConfig)

    // Create an entity that has multiple versions, for testing getEntityProperties.  This is more or less a host entity, but
    // for the purposes of this test, it doesn't really matter.
    val propTestEntity: Entity = new Entity
    propTestEntity.setEntityId(propTestEntityId)
    propTestEntity.setEntityType(Entity.HOST_ENTITY)
    val fusionDates = new util.ArrayList[lang.Long]()
    fusionDates.add(EntityFusionConfiguration.convertDate(propTestFusionDate2).getTime)
    fusionDates.add(EntityFusionConfiguration.convertDate(propTestFusionDate1).getTime)
    propTestEntity.setFusionTimes(fusionDates)
    propTestEntity.setIpAddresses(genPropertiesList("1.1.1.1", propTestFusionDate2, "source1", "2.2.2.2", propTestFusionDate1, "source2"))
    // We're only going to have one mac and host name
    propTestEntity.setMacAddress("11:22:C8:56:84:17")
    propTestEntity.setHostNames(genPropertiesList("host1", propTestFusionDate2, "sourceA", "host2", propTestFusionDate1, "sourceB"))
    propTestEntity.setUserNames(genPropertiesList("user1", propTestFusionDate2, "sourceA", "user2", propTestFusionDate1, "sourceB"))
    propTestEntity.setOs("Windows10")
    propTestEntity.setBrowsers(genPropertiesList("Safari", propTestFusionDate2, "source1", "Firefox", propTestFusionDate1, "source1"))
    propTestEntity.setUserLastLogonDates(genPropertiesList(propTestFusionDate2, propTestFusionDate2, "source1", propTestFusionDate1, propTestFusionDate1, "source1"))
    val propTestEntityList: Array[BaseRow] = Array(propTestEntity)
    entityTable.putRows(hBaseConn, propTestEntityList, false, hBaseConfig)

    // Create host and user entity mapping tables
    val entityRelationshipTable = new EntityRelationshipTable
    entityRelationshipTable.createTableIfNotExists(hBaseConn, hBaseConfig)

    val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
    val entityThreatSql: String = "CREATE TABLE IF NOT EXISTS " + EntityThreat.getName(configuration) +  " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    IP_ADDRESS VARCHAR NOT NULL,\n" +
      "    MAC_ADDRESS VARCHAR NOT NULL,\n" +
      "    HOST_NAME VARCHAR NOT NULL,\n" +
      "    USER_NAME VARCHAR NOT NULL,\n" +
      "    MODEL_ID INTEGER NOT NULL,\n" +
      "    SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
      "    RISK_SCORE DOUBLE,\n" +
      "    HOST_ENTITY_ID VARCHAR,\n" +
      "    USER_ENTITY_ID VARCHAR,\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID )\n" +
      ") IMMUTABLE_ROWS=true"
    val createStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
    createStmt.execute

    var upsertSql: String = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-01T00:00:00.000Z', '192.168.1.150', '7C-12-F1-53-5D-76', 'laptop850', 'e\\\\8\\\\jdoe', 3, 8, 0.87337, null, 'e1')"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-01T00:00:00.000Z', '192.168.1.151', '63-A6-D2-AB-F2-B2', 'laptop851', 'kenbrell_thompkins', 3, 14, 0.7135, null, 'e2')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.174', 'E4-EB-C9-03-9B-41', 'laptop874', 'e\\\\8\\\\jdoe', 7, 502, 0.98, null, 'e3')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-03T00:00:00.000Z', '192.168.12.27', '12:7F:C8:56:84:17', 'RDP-GW', 'NULL_VALUE', 9, 0, 0.7773, 'e4', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-03T00:00:00.000Z', '192.168.12.27', '12:7F:C8:56:84:17', 'RDP-GW', 'NULL_VALUE', 10, 0, 1.0, 'e4', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-03T00:00:00.000Z', '192.168.1.13', '54-0C-75-00-E2-12', 'laptop801', 'NULL_VALUE', 8, 0, 0.585714285714, 'c3c46ec3ab47d5ee70e3f60283015a24', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-04T00:00:00.000Z', '192.168.1.31', '54-0C-75-00-E2-12', 'laptop801', 'NULL_VALUE', 8, 0, 0.585714285714, 'c3c46ec3ab47d5ee70e3f60283015a24', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-03T00:00:00.000Z', '192.168.1.29', 'AA:7F:C8:56:94:71', 'RDP-GW', 'administrator', 3, 14, 0.6135, null, 'e21')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // For the moment we only need tanium_stats, since that's the only table we get entity cards from
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
    val taniumStatsCreateStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
    taniumStatsCreateStmt.execute

    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, " +
      "PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, " +
      "PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-02-03T00:00:00.000Z', 'HOST', 'e4', '', '', '0.2773', '1.6636', '2016-02-03T15:03:41Z|2016-02-03T23:58:15Z', " +
      "'RDP-GW', 'RDP-GW', '690374e7ec63b7addfa57817d120b2f1|fef2e008b08376d5a16c650ce0190609|61ac3efdfacfdd3f0f11dd4fd4044223', " +
      "'ad7b9c14083b52bc532fba5948342b98|701bb23989e138c1c8b436c5940a1f6e|de701bee6a35ecd48c9a582bbff943c0|1951c6f1e53079f6b29ecff77eaf9403|fef2e008b08376d5a16c650ce0190609|773212b2aaa24c1e31f10246b15b276c|d08f5c01b23aa7f356693c028ab592ba|f9120481520d8202fa0a02dfdb575ba7|4f2659160afcca990305816946f69407|af29e58a2f20d0bffd3eaec34a1ea10c|545bf7eaa24a9e062857d0742ec0b28a|332feab1435662fc6c672e25beb37be3|637c075ca92a0e272029f53b345d1bfd|54a47f6b5e09a77e61649109c6a08866|b3fde4699997759320cba603e2aed2cd|61ac3efdfacfdd3f0f11dd4fd4044223|6aaf3bece2c3d17091bcef37c5a82ac0|0db1ccd7230fdee1adb04e536ea60759|690374e7ec63b7addfa57817d120b2f1|7eae59832be1c3a08d4f94cebccaf13a|a3a35ee79c64a640152b3113e6e254e2|32297bb17e6ec700d0fc869f9acaf561|4676aaa9ddf52a50c829fedb4ea81e54|825fb6de39fe63b3f59b78d760f0619c|979d74799ea6c8b8167869a68df5204a|b5c5dcad3899512020d135600129d665', " +
      "'ad7b9c14083b52bc532fba5948342b98|701bb23989e138c1c8b436c5940a1f6e|de701bee6a35ecd48c9a582bbff943c0|1951c6f1e53079f6b29ecff77eaf9403|773212b2aaa24c1e31f10246b15b276c|d08f5c01b23aa7f356693c028ab592ba|f9120481520d8202fa0a02dfdb575ba7|4f2659160afcca990305816946f69407|af29e58a2f20d0bffd3eaec34a1ea10c|545bf7eaa24a9e062857d0742ec0b28a|332feab1435662fc6c672e25beb37be3|637c075ca92a0e272029f53b345d1bfd|6bd4d7f68924301051c22e8a951aecba|54a47f6b5e09a77e61649109c6a08866|b3fde4699997759320cba603e2aed2cd|0a44e47ff51f97dd9c5195d12c59c9ec|6aaf3bece2c3d17091bcef37c5a82ac0|0db1ccd7230fdee1adb04e536ea60759|7eae59832be1c3a08d4f94cebccaf13a|a3a35ee79c64a640152b3113e6e254e2|32297bb17e6ec700d0fc869f9acaf561|4676aaa9ddf52a50c829fedb4ea81e54|825fb6de39fe63b3f59b78d760f0619c|b5c5dcad3899512020d135600129d665|979d74799ea6c8b8167869a68df5204a', " +
      "'mintty.exe|bash.exe|userinit.exe|nwiz.exe', 'WmiPrvSE.exe|XenGuestAgent.exe|spoolsv.exe|NETSTAT.EXE|nvvsvc.exe|csrss.exe|mintty.exe|mmc.exe|nvSCPAPISvr.exe|rdpclip.exe|wininit.exe|taskeng.exe|services.exe|wscript.exe|Ec2Config.exe|XenDpriv.exe|msdtc.exe|LogonUI.exe|mstsc.exe|winlogon.exe|dwm.exe|bash.exe|svchost.exe|VSSVC.exe|System Idle Process|smss.exe|System|firefox.exe|explorer.exe|nwiz|TrustedInstaller.exe|nvxdsync.exe|cmd.exe|lsm.exe|nwiz.exe|conhost.exe|TaniumClient.exe|putty.exe|taskhost.exe|sppsvc.exe|taskmgr.exe|cscript.exe|cb.exe|userinit.exe|lsass.exe|nvwmi64.exe', 'WmiPrvSE.exe|XenGuestAgent.exe|NETSTAT.EXE|spoolsv.exe|nvvsvc.exe|csrss.exe|nvSCPAPISvr.exe|mmc.exe|rdpclip.exe|wininit.exe|taskeng.exe|services.exe|wscript.exe|Ec2Config.exe|XenDpriv.exe|msdtc.exe|LogonUI.exe|mstsc.exe|winlogon.exe|dwm.exe|svchost.exe|VSSVC.exe|System Idle Process|smss.exe|System|firefox.exe|explorer.exe|nwiz|aitagent.exe|TrustedInstaller.exe|MpCmdRun.exe|cmd.exe|nvxdsync.exe|lsm.exe|conhost.exe|TaniumClient.exe|putty.exe|taskhost.exe|sppsvc.exe|taskmgr.exe|cscript.exe|cb.exe|nvwmi64.exe|lsass.exe|PortTester.exe', " +
      "'', '64-bit/\\\\Software\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\\\\', '64-bit/\\\\Software\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\\\\', " +
      "'C:\\\\cygwin64\\\\bin\\\\mintty.exe|C:\\\\Windows\\\\system32\\\\userinit.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe|C:\\\\cygwin64\\\\bin\\\\bash.exe', 'C:\\\\Windows\\\\SysWow64\\\\sppsvc.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe|C:\\\\Windows\\\\system32\\\\wininit.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe|C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe|C:\\\\cygwin64\\\\bin\\\\mintty.exe|C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe|C:\\\\Windows\\\\system32\\\\cscript.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Windows\\\\SysWOW64\\\\cscript.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe|C:\\\\Windows\\\\CarbonBlack\\\\cb.exe|C:\\\\Windows\\\\SysWow64\\\\lsass.exe|C:\\\\cygwin64\\\\bin\\\\bash.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe /installquiet|C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe|C:\\\\Windows\\\\servicing\\\\TrustedInstaller.exe|C:\\\\Windows\\\\SysWow64\\\\csrss.exe|C:\\\\Windows\\\\system32\\\\mstsc.exe|C:\\\\Windows\\\\SysWOW64\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe|C:\\\\Windows\\\\system32\\\\taskeng.exe|C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe|C:\\\\Windows\\\\system32\\\\svchost.exe|C:\\\\Program Files (x86)\\\\PuTTY\\\\putty.exe|C:\\\\Windows\\\\System32\\\\WScript.exe|C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\System32\\\\spoolsv.exe|C:\\\\Windows\\\\System32\\\\msdtc.exe|C:\\\\Windows\\\\Explorer.EXE|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\mmc.exe|C:\\\\Windows\\\\system32\\\\taskmgr.exe|C:\\\\Windows\\\\SysWow64\\\\winlogon.exe|C:\\\\Windows\\\\System32\\\\svchost.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe|C:\\\\Windows\\\\SysWow64\\\\vssvc.exe|C:\\\\Windows\\\\SysWow64\\\\Dwm.exe|C:\\\\Windows\\\\SysWow64\\\\taskhost.exe|C:\\\\Windows\\\\SysWow64\\\\services.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\netstat.exe|C:\\\\Windows\\\\SysWow64\\\\conhost.exe|C:\\\\Windows\\\\system32\\\\userinit.exe|C:\\\\Windows\\\\SysWow64\\\\lsm.exe', 'C:\\\\Windows\\\\SysWow64\\\\sppsvc.exe|C:\\\\Windows\\\\system32\\\\wininit.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe|C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\system32\\\\cscript.exe|C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Windows\\\\SysWOW64\\\\cscript.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe|C:\\\\Windows\\\\CarbonBlack\\\\cb.exe|C:\\\\Windows\\\\SysWow64\\\\lsass.exe|c:\\\\program files\\\\windows defender\\\\MpCmdRun.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe /installquiet|C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe|C:\\\\Windows\\\\servicing\\\\TrustedInstaller.exe|C:\\\\Windows\\\\SysWow64\\\\csrss.exe|C:\\\\Windows\\\\system32\\\\mstsc.exe|C:\\\\Windows\\\\SysWOW64\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe|C:\\\\Windows\\\\system32\\\\taskeng.exe|C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe|C:\\\\Windows\\\\system32\\\\svchost.exe|C:\\\\Program Files (x86)\\\\PuTTY\\\\putty.exe|C:\\\\Windows\\\\System32\\\\WScript.exe|C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\System32\\\\spoolsv.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\Explorer.EXE|C:\\\\Windows\\\\System32\\\\msdtc.exe|C:\\\\Windows\\\\system32\\\\taskmgr.exe|C:\\\\Windows\\\\system32\\\\mmc.exe|C:\\\\Windows\\\\SysWow64\\\\winlogon.exe|C:\\\\Windows\\\\System32\\\\svchost.exe|C:\\\\Windows\\\\SysWow64\\\\vssvc.exe|" +
      "C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\Tools\\\\porttester.exe|C:\\\\Windows\\\\SysWow64\\\\Dwm.exe|C:\\\\Windows\\\\SysWow64\\\\taskhost.exe|C:\\\\Windows\\\\SysWow64\\\\services.exe|C:\\\\Windows\\\\System32\\\\netstat.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\SysWow64\\\\conhost.exe|C:\\\\Windows\\\\SysWow64\\\\lsm.exe', " +
      "'TCP/59341|TCP/63393', 'TCP/58090|TCP/58091', 'TCP/61163|TCP/50589', 'RDP-GW')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, " +
      "MD5S_CURRENT, " +
      "MD5S_HISTORICAL, " +
      "PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, " +
      "PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL, HOST_NAME) " +
      "values ('2016-09-01T00:00:00.000Z', 'HOST', '" + EntityFusionTest.propTestEntityId  + "', '', '', '0.2773', '1.6636', '2016-02-03T15:03:41Z|2016-02-03T23:58:15Z', " +
      "'RDP-GW', 'RDP-GW', '690374e7ec63b7addfa57817d120b2f1|fef2e008b08376d5a16c650ce0190609|61ac3efdfacfdd3f0f11dd4fd4044223', " +
      "'ad7b9c14083b52bc532fba5948342b98|701bb23989e138c1c8b436c5940a1f6e|de701bee6a35ecd48c9a582bbff943c0|1951c6f1e53079f6b29ecff77eaf9403|fef2e008b08376d5a16c650ce0190609|773212b2aaa24c1e31f10246b15b276c|d08f5c01b23aa7f356693c028ab592ba|f9120481520d8202fa0a02dfdb575ba7|4f2659160afcca990305816946f69407|af29e58a2f20d0bffd3eaec34a1ea10c|545bf7eaa24a9e062857d0742ec0b28a|332feab1435662fc6c672e25beb37be3|637c075ca92a0e272029f53b345d1bfd|54a47f6b5e09a77e61649109c6a08866|b3fde4699997759320cba603e2aed2cd|61ac3efdfacfdd3f0f11dd4fd4044223|6aaf3bece2c3d17091bcef37c5a82ac0|0db1ccd7230fdee1adb04e536ea60759|690374e7ec63b7addfa57817d120b2f1|7eae59832be1c3a08d4f94cebccaf13a|a3a35ee79c64a640152b3113e6e254e2|32297bb17e6ec700d0fc869f9acaf561|4676aaa9ddf52a50c829fedb4ea81e54|825fb6de39fe63b3f59b78d760f0619c|979d74799ea6c8b8167869a68df5204a|b5c5dcad3899512020d135600129d665', " +
      "'ad7b9c14083b52bc532fba5948342b98|701bb23989e138c1c8b436c5940a1f6e|de701bee6a35ecd48c9a582bbff943c0|1951c6f1e53079f6b29ecff77eaf9403|773212b2aaa24c1e31f10246b15b276c|d08f5c01b23aa7f356693c028ab592ba|f9120481520d8202fa0a02dfdb575ba7|4f2659160afcca990305816946f69407|af29e58a2f20d0bffd3eaec34a1ea10c|545bf7eaa24a9e062857d0742ec0b28a|332feab1435662fc6c672e25beb37be3|637c075ca92a0e272029f53b345d1bfd|6bd4d7f68924301051c22e8a951aecba|54a47f6b5e09a77e61649109c6a08866|b3fde4699997759320cba603e2aed2cd|0a44e47ff51f97dd9c5195d12c59c9ec|6aaf3bece2c3d17091bcef37c5a82ac0|0db1ccd7230fdee1adb04e536ea60759|7eae59832be1c3a08d4f94cebccaf13a|a3a35ee79c64a640152b3113e6e254e2|32297bb17e6ec700d0fc869f9acaf561|4676aaa9ddf52a50c829fedb4ea81e54|825fb6de39fe63b3f59b78d760f0619c|b5c5dcad3899512020d135600129d665|979d74799ea6c8b8167869a68df5204a', " +
      "'mintty.exe|bash.exe|userinit.exe|nwiz.exe', 'WmiPrvSE.exe|XenGuestAgent.exe|spoolsv.exe|NETSTAT.EXE|nvvsvc.exe|csrss.exe|mintty.exe|mmc.exe|nvSCPAPISvr.exe|rdpclip.exe|wininit.exe|taskeng.exe|services.exe|wscript.exe|Ec2Config.exe|XenDpriv.exe|msdtc.exe|LogonUI.exe|mstsc.exe|winlogon.exe|dwm.exe|bash.exe|svchost.exe|VSSVC.exe|System Idle Process|smss.exe|System|firefox.exe|explorer.exe|nwiz|TrustedInstaller.exe|nvxdsync.exe|cmd.exe|lsm.exe|nwiz.exe|conhost.exe|TaniumClient.exe|putty.exe|taskhost.exe|sppsvc.exe|taskmgr.exe|cscript.exe|cb.exe|userinit.exe|lsass.exe|nvwmi64.exe', 'WmiPrvSE.exe|XenGuestAgent.exe|NETSTAT.EXE|spoolsv.exe|nvvsvc.exe|csrss.exe|nvSCPAPISvr.exe|mmc.exe|rdpclip.exe|wininit.exe|taskeng.exe|services.exe|wscript.exe|Ec2Config.exe|XenDpriv.exe|msdtc.exe|LogonUI.exe|mstsc.exe|winlogon.exe|dwm.exe|svchost.exe|VSSVC.exe|System Idle Process|smss.exe|System|firefox.exe|explorer.exe|nwiz|aitagent.exe|TrustedInstaller.exe|MpCmdRun.exe|cmd.exe|nvxdsync.exe|lsm.exe|conhost.exe|TaniumClient.exe|putty.exe|taskhost.exe|sppsvc.exe|taskmgr.exe|cscript.exe|cb.exe|nvwmi64.exe|lsass.exe|PortTester.exe', " +
      "'', '64-bit/\\\\Software\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\\\\', '64-bit/\\\\Software\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\\\\', " +
      "'C:\\\\cygwin64\\\\bin\\\\mintty.exe|C:\\\\Windows\\\\system32\\\\userinit.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe|C:\\\\cygwin64\\\\bin\\\\bash.exe', 'C:\\\\Windows\\\\SysWow64\\\\sppsvc.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe|C:\\\\Windows\\\\system32\\\\wininit.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe|C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe|C:\\\\cygwin64\\\\bin\\\\mintty.exe|C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe|C:\\\\Windows\\\\system32\\\\cscript.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Windows\\\\SysWOW64\\\\cscript.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe|C:\\\\Windows\\\\CarbonBlack\\\\cb.exe|C:\\\\Windows\\\\SysWow64\\\\lsass.exe|C:\\\\cygwin64\\\\bin\\\\bash.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe /installquiet|C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe|C:\\\\Windows\\\\servicing\\\\TrustedInstaller.exe|C:\\\\Windows\\\\SysWow64\\\\csrss.exe|C:\\\\Windows\\\\system32\\\\mstsc.exe|C:\\\\Windows\\\\SysWOW64\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe|C:\\\\Windows\\\\system32\\\\taskeng.exe|C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe|C:\\\\Windows\\\\system32\\\\svchost.exe|C:\\\\Program Files (x86)\\\\PuTTY\\\\putty.exe|C:\\\\Windows\\\\System32\\\\WScript.exe|C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\System32\\\\spoolsv.exe|C:\\\\Windows\\\\System32\\\\msdtc.exe|C:\\\\Windows\\\\Explorer.EXE|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\mmc.exe|C:\\\\Windows\\\\system32\\\\taskmgr.exe|C:\\\\Windows\\\\SysWow64\\\\winlogon.exe|C:\\\\Windows\\\\System32\\\\svchost.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe|C:\\\\Windows\\\\SysWow64\\\\vssvc.exe|C:\\\\Windows\\\\SysWow64\\\\Dwm.exe|C:\\\\Windows\\\\SysWow64\\\\taskhost.exe|C:\\\\Windows\\\\SysWow64\\\\services.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\netstat.exe|C:\\\\Windows\\\\SysWow64\\\\conhost.exe|C:\\\\Windows\\\\system32\\\\userinit.exe|C:\\\\Windows\\\\SysWow64\\\\lsm.exe', 'C:\\\\Windows\\\\SysWow64\\\\sppsvc.exe|C:\\\\Windows\\\\system32\\\\wininit.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe|C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\system32\\\\cscript.exe|C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Windows\\\\SysWOW64\\\\cscript.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe|C:\\\\Windows\\\\CarbonBlack\\\\cb.exe|C:\\\\Windows\\\\SysWow64\\\\lsass.exe|c:\\\\program files\\\\windows defender\\\\MpCmdRun.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe /installquiet|C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe|C:\\\\Windows\\\\servicing\\\\TrustedInstaller.exe|C:\\\\Windows\\\\SysWow64\\\\csrss.exe|C:\\\\Windows\\\\system32\\\\mstsc.exe|C:\\\\Windows\\\\SysWOW64\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe|C:\\\\Windows\\\\system32\\\\taskeng.exe|C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe|C:\\\\Windows\\\\system32\\\\svchost.exe|C:\\\\Program Files (x86)\\\\PuTTY\\\\putty.exe|C:\\\\Windows\\\\System32\\\\WScript.exe|C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\System32\\\\spoolsv.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\Explorer.EXE|C:\\\\Windows\\\\System32\\\\msdtc.exe|C:\\\\Windows\\\\system32\\\\taskmgr.exe|C:\\\\Windows\\\\system32\\\\mmc.exe|C:\\\\Windows\\\\SysWow64\\\\winlogon.exe|C:\\\\Windows\\\\System32\\\\svchost.exe|C:\\\\Windows\\\\SysWow64\\\\vssvc.exe|" +
      "C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\Tools\\\\porttester.exe|C:\\\\Windows\\\\SysWow64\\\\Dwm.exe|C:\\\\Windows\\\\SysWow64\\\\taskhost.exe|C:\\\\Windows\\\\SysWow64\\\\services.exe|C:\\\\Windows\\\\System32\\\\netstat.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\SysWow64\\\\conhost.exe|C:\\\\Windows\\\\SysWow64\\\\lsm.exe', " +
      "'TCP/59341|TCP/63393', 'TCP/58090|TCP/58091', 'TCP/61163|TCP/50589', 'RDP-GW')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // the next two rows we're adding are used to validate that we handle the newly_observed column correctly and generate the
    // right groupInfo in the result
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-03T00:00:00.000Z', 'MD5', 'fef2e008b08376d5a16c650ce0190609', '', '', '10', '', '2016-02-03T15:03:41Z|2016-02-03T23:58:45Z', 'WIN-OSNMCI3GJJ1', 'WIN-OSNMCI3GJJ1', '', 'fef2e008b08376d5a16c650ce0190609', 'fef2e008b08376d5a16c650ce0190609', '', 'bash.exe', 'bash.exe', '', '', '', '', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-03T00:00:00.000Z', 'MD5', 'fef2e008b08376d5a16c650ce0190609', '', 'Default:2/2', '0', '', '2016-02-03T15:03:41Z|2016-02-03T23:58:15Z', 'RDP-GW|WIN-OSNMCI3GJJ1', 'WIN-OSNMCI3GJJ1', '', 'fef2e008b08376d5a16c650ce0190609', 'fef2e008b08376d5a16c650ce0190609', '', 'bash.exe', 'bash.exe', '', '', '', '', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-03T00:00:00.000Z', 'MD5', '690374e7ec63b7addfa57817d120b2f1', '', 'Default:1/2', '0.1386', '', '2016-02-03T21:35:50Z|2016-02-03T21:37:11Z', 'RDP-GW', '', '690374e7ec63b7addfa57817d120b2f1', '690374e7ec63b7addfa57817d120b2f1', '', 'mintty.exe', 'mintty.exe', '', '', '', '', 'C:\\\\cygwin64\\\\bin\\\\mintty.exe', 'C:\\\\cygwin64\\\\bin\\\\mintty.exe', '', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-02-03T00:00:00.000Z', 'MD5', '61ac3efdfacfdd3f0f11dd4fd4044223', '', 'Default:1/2', '0.1386', '', '2016-02-03T21:25:40Z|2016-02-03T21:27:08Z', 'RDP-GW', '', '61ac3efdfacfdd3f0f11dd4fd4044223', '61ac3efdfacfdd3f0f11dd4fd4044223', '', 'userinit.exe', 'userinit.exe', '', '', '', '', 'C:\\\\Windows\\\\system32\\\\userinit.exe', 'C:\\\\Windows\\\\system32\\\\userinit.exe', '', '', '', '')"
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
    return result
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

      //      val efHourlyRollupSql: String = "DROP TABLE IF EXISTS ENTITY_FUSION_HOURLY_ROLL_UP"
//      val efHourlyRollupStmt: PreparedStatement = conn.prepareStatement(efHourlyRollupSql)
//      efHourlyRollupStmt.execute
//      val entHostPropsSql: String = "DROP TABLE IF EXISTS ENT_HOST_PROPS"
//      val entHostPropsStmt: PreparedStatement = conn.prepareStatement(entHostPropsSql)
//      entHostPropsStmt.execute
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        //throw ex
    }
    MongoTestBase.teardownMongo()
    HBaseTestBase.teardownHBase()
  }
}

class EntityFusionTest {
  private val entityFusionHourRollupDao = new EntityFusionHourlyRollUpDao(EntityFusionTest.configuration)
  private val entityInvestigatorDao = new EntityInvestigatorDao(EntityFusionTest.configuration)
  private val detectorHomeDao = new DetectorHomeDao(EntityFusionTest.configuration)


  /////////////////////////////////////////////////
  // Entity Fusion Hourly Rollup tests
  /////////////////////////////////////////////////
  @Test
  def getEntityInfoFromFusionForIpTest = {
    var entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForIp("10.10.4.69", "2016-08-02T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-01T15:00:00.000Z", entityInfo.getDateTime)
    assertEquals("10.10.4.69", entityInfo.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    assertEquals("Steves-Air", entityInfo.getHostName)
    assertEquals("", entityInfo.getUserName)

    entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForIp("10.10.4.69", "2016-08-03T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-02T17:00:00.000Z", entityInfo.getDateTime)
    assertEquals("10.10.4.69", entityInfo.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    assertEquals("MacBook-Air", entityInfo.getHostName)
    assertEquals("", entityInfo.getUserName)

    entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForIp("192.168.12.18", "2016-08-02T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-01T14:00:00.001Z", entityInfo.getDateTime)
    assertEquals("192.168.12.18", entityInfo.getIpAddress)
    assertEquals("", entityInfo.getMacAddress)
    assertEquals("", entityInfo.getHostName)
    assertEquals("w2k8r2-ad$", entityInfo.getUserName)
  }

  @Test
  def getEntityInfoFromFusionForHostNameTest = {
    var entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForHostName("MacBook-Air", "2016-08-03T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-02T17:00:00.000Z", entityInfo.getDateTime)
    assertEquals("10.10.4.69", entityInfo.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    assertEquals("MacBook-Air", entityInfo.getHostName)
    assertEquals("", entityInfo.getUserName)

    entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForHostName("Steves-Air", "2016-08-03T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-02T15:00:00.000Z", entityInfo.getDateTime)
    assertEquals("10.10.4.69", entityInfo.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    assertEquals("Steves-Air", entityInfo.getHostName)
    assertEquals("", entityInfo.getUserName)
  }

  @Test
  def getEntityInfoFromFusionForMacAddressTest = {
    var entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForMacAddress("B8:E8:56:03:C9:7E", "2016-08-03T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-02T17:00:00.000Z", entityInfo.getDateTime)
    assertEquals("10.10.4.69", entityInfo.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    assertEquals("MacBook-Air", entityInfo.getHostName)
    assertEquals("", entityInfo.getUserName)

    entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForMacAddress("B8:E8:56:03:C9:7E", "2016-08-02T00:00:00.000Z")
    assertNotNull(entityInfo)
    assertEquals("2016-08-01T15:00:00.000Z", entityInfo.getDateTime)
    assertEquals("10.10.4.69", entityInfo.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    assertEquals("Steves-Air", entityInfo.getHostName)
    assertEquals("", entityInfo.getUserName)
  }

  @Test
  def getEntityInfoFromFusionForUsernameTest = {
    val entityInfo = entityFusionHourRollupDao.getEntityInfoFromFusionForUsername("w2k8r2-ad$", "2016-08-02T00:00:00.000Z")
    assertNotNull(entityInfo)
    // The test data has several rows in the date range.  It would be legitimate for us to pick any of them,
    // so we can't check to see what the ip or time is.
//    assertEquals("2016-08-01T14:00:00.000Z", entityInfo.getDateTime)
//    assertEquals("192.168.12.18", entityInfo.getIpAddress)
    assertEquals("", entityInfo.getMacAddress)
    assertEquals("", entityInfo.getHostName)
    assertEquals("w2k8r2-ad$", entityInfo.getUserName)
  }

  @Test
  def getIpToEntityInfoMapForIpListTest = {
    val entities = entityFusionHourRollupDao.getIpToEntityInfoMapForIpList("10.10.4.69,192.168.12.18", "2016-08-02T00:00:00.000Z")
    assertEquals(2, entities.size)
    val entityInfo69 = entities.getOrElse("10.10.4.69", null)
    assertNotNull(entityInfo69)
    assertEquals("2016-08-01T14:00:00.000Z", entityInfo69.getDateTime)
    assertEquals("10.10.4.69", entityInfo69.getIpAddress)
    assertEquals("B8:E8:56:03:C9:7E", entityInfo69.getMacAddress)
    assertEquals("Steves-Air", entityInfo69.getHostName)
    assertEquals(null, entityInfo69.getUserName)

    val entityInfo18 = entities.getOrElse("192.168.12.18", null)
    assertNotNull(entityInfo18)
    assertEquals("2016-08-01T09:00:00.002Z", entityInfo18.getDateTime)
    assertEquals("192.168.12.18", entityInfo18.getIpAddress)
    assertEquals(null, entityInfo18.getMacAddress)
    assertEquals(null, entityInfo18.getHostName)
    assertEquals("w2k8r2-ad$", entityInfo18.getUserName)
  }

  @Test
  def getIpToEntityInfoTest = {
    val entities = entityFusionHourRollupDao.getIpToEntityInfo("2016-08-02T00:00:00.000Z", "2016-08-03T00:00:00.000Z")
    assertEquals(3, entities.size)
    val entity27 = entities.getOrElse("192.168.12.27", null)
    assertNotNull(entity27)
    assertEquals(3, entity27.size)
    for (entityInfo <- entity27) {
      assertEquals("RDP-GW", entityInfo.getHostName)
      val userName = entityInfo.getUserName
      assertTrue(userName == "jyria" || userName == "administrator")
      assertEquals("12:7F:C8:56:84:17", entityInfo.getMacAddress)
    }
    val entity63 = entities.getOrElse("10.10.4.63", null)
    assertNotNull(entity63)
    assertEquals(2, entity63.size)
    for (entityInfo <- entity63) {
      assertEquals("Christophes-MBP", entityInfo.getHostName)
      assertEquals("christophe", entityInfo.getUserName)
      assertEquals("A0:99:9B:0D:C6:E9", entityInfo.getMacAddress)
    }
    val entity69 = entities.getOrElse("10.10.4.69", null)
    assertNotNull(entity69)
    assertEquals(4, entity69.size)
    for (entityInfo <- entity69) {
      assertNull(entityInfo.getUserName)
      assertEquals("B8:E8:56:03:C9:7E", entityInfo.getMacAddress)
    }
  }

  /////////////////////////////////////////////////
  // entitySearch tests
  /////////////////////////////////////////////////
  @Test
  def getEntityScoresTest = {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-02-01T00:00:00.000Z")
    query.setEndTime("2016-02-04T00:00:00.000Z")
    query.setLimit(20)
    val lowerBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    lowerBound.put("field","risk")
    lowerBound.put("operator", "greater than equal")
    val lowerValues: util.List[String] = new util.ArrayList[String]()
    lowerValues.add("0.7")
    lowerBound.put("values", lowerValues)
    query.getQuery.add(lowerBound)
    val upperBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    upperBound.put("field","risk")
    upperBound.put("operator", "less than equal")
    val upperValues: util.List[String] = new util.ArrayList[String]()
    upperValues.add("1")
    upperBound.put("values", upperValues)
    query.getQuery.add(upperBound)
    query.setSortField("risk")
    query.setSortOrder("DESC")
    val scores = detectorHomeDao.getEntityScores(query, true, EntityFusionTest.featureServiceCache)
    assertEquals(4, scores.size)
    for (entity <- scores) {
      val ip = entity.getOrElse("ipAddress", null)
      val entityId = entity.getOrElse("entityId", null)
      val entityType = entity.getOrElse("entityType", null)
      if (ip == "192.168.12.27") {
        assertEquals("e4", entityId)
        assertEquals("host", entityType)
      } else if (ip == "192.168.1.174") {
        assertEquals("e3", entityId)
        assertEquals("user", entityType)
      } else if (ip == "192.168.1.151") {
        assertEquals("e2", entityId)
        assertEquals("user", entityType)
      } else if (ip == "192.168.1.150") {
        assertEquals("e1", entityId)
        assertEquals("user", entityType)
      } else {
        assertTrue("unexpected id/ip combination", false)
      }
    }
//    val r = EntityFusionTest.dhf.getRiskyEntities(query)
//      val r = EntityFusionTest.dhf.getModelScoresById("", "", "", "", "")
  }

  @Test
  def getSearchedEntityScoresTest = {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-02-01T00:00:00.000Z")
    query.setEndTime("2016-02-04T00:00:00.000Z")
    query.setLimit(20)
    val lowerBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    lowerBound.put("field","risk")
    lowerBound.put("operator", "greater than equal")
    val lowerValues: util.List[String] = new util.ArrayList[String]()
    lowerValues.add("0.7")
    lowerBound.put("values", lowerValues)
    query.getQuery.add(lowerBound)
    val upperBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    upperBound.put("field","risk")
    upperBound.put("operator", "less than equal")
    val upperValues: util.List[String] = new util.ArrayList[String]()
    upperValues.add("1")
    upperBound.put("values", upperValues)
    query.getQuery.add(upperBound)
    val hostname: util.Map[String, Object] = new util.HashMap[String, Object]()
    hostname.put("field","userName")
    hostname.put("operator", "equals")
    val hostnameValues: util.List[String] = new util.ArrayList[String]()
    hostnameValues.add("kenbrell_thompkins")
    hostname.put("values", hostnameValues)
    query.getQuery.add(hostname)
    query.setSortField("risk")
    query.setSortOrder("DESC")
    val scores = detectorHomeDao.getSearchedEntityScores(query, true, EntityFusionTest.featureServiceCache)
    assertEquals(1, scores.size)
    for (entity <- scores) {
      val ip = entity.getOrElse("ipAddress", null)
      assertEquals("192.168.1.151", ip)
      val entityId = entity.getOrElse("entityId", null)
      assertEquals("e2", entityId)
      val macAddr = entity.getOrElse("macAddress", null)
      assertEquals("63-A6-D2-AB-F2-B2", macAddr)
      val userName = entity.getOrElse("userName", null)
      assertEquals("kenbrell_thompkins", userName)
    }
//    val r = EntityFusionTest.dhf.entitySearch(query)
  }

  // This test checks to make sure that we correctly handle the case where multiple rows in entity threat point to the same entity id.
  // We should return only the highest risk row, and details are sorted out by entity properties and entity card apis.
  @Test
  def getSearchedEntitiesSameEntityId = {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-11-03T00:00:00.000Z")
    query.setEndTime("2016-11-05T00:00:00.000Z")
    query.setLimit(20)
    val lowerBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    lowerBound.put("field","risk")
    lowerBound.put("operator", "greater than equal")
    val lowerValues: util.List[String] = new util.ArrayList[String]()
    lowerValues.add("0.4")
    lowerBound.put("values", lowerValues)
    query.getQuery.add(lowerBound)
    val upperBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    upperBound.put("field","risk")
    upperBound.put("operator", "less than equal")
    val upperValues: util.List[String] = new util.ArrayList[String]()
    upperValues.add("1")
    upperBound.put("values", upperValues)
    query.getQuery.add(upperBound)
    val hostname: util.Map[String, Object] = new util.HashMap[String, Object]()
    hostname.put("field","hostName")
    hostname.put("operator", "equals")
    val hostnameValues: util.List[String] = new util.ArrayList[String]()
    hostnameValues.add("laptop801")
    hostname.put("values", hostnameValues)
    query.getQuery.add(hostname)
    query.setSortField("risk")
    query.setSortOrder("DESC")
    val scores = detectorHomeDao.getSearchedEntityScores(query, true, EntityFusionTest.featureServiceCache)
    assertEquals(1, scores.size)
    val threat = scores.head
    val ip = threat.getOrElse("ipAddress", null)
    assertTrue(ip == "192.168.1.31" || ip == "192.168.1.13")
    assertEquals("54-0C-75-00-E2-12", threat.getOrElse("macAddress", null))
    assertEquals("laptop801", threat.getOrElse("hostName", null))
    assertEquals(8, threat.getOrElse("currentModelId", null))
    val modelScores = threat.getOrElse("modelScores", null).asInstanceOf[ListBuffer[EntityModelInfo]]
    assertEquals(2, modelScores.size)
    for (modelInfo <- modelScores) {
      assertEquals(8, modelInfo.getModel)
      assertEquals(0, modelInfo.getSecurityEventTypeId)
      assertTrue(modelInfo.getRisk - 0.5857 < 0.01)
    }
  }

  // This test checks that when we get threats without a search, we unifiy results for a given entity.  That is, we should
  // only return one result for each unique entity.
  @Test
  def getEntityScoresSameEntityId = {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-11-03T00:00:00.000Z")
    query.setEndTime("2016-11-05T00:00:00.000Z")
    query.setLimit(20)
    val lowerBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    lowerBound.put("field","risk")
    lowerBound.put("operator", "greater than equal")
    val lowerValues: util.List[String] = new util.ArrayList[String]()
    lowerValues.add("0.3")
    lowerBound.put("values", lowerValues)
    query.getQuery.add(lowerBound)
    val upperBound: util.Map[String, Object] = new util.HashMap[String, Object]()
    upperBound.put("field","risk")
    upperBound.put("operator", "less than equal")
    val upperValues: util.List[String] = new util.ArrayList[String]()
    upperValues.add("1")
    upperBound.put("values", upperValues)
    query.getQuery.add(upperBound)
    query.setSortField("risk")
    query.setSortOrder("DESC")
    val threats = detectorHomeDao.getEntityScores(query, true, EntityFusionTest.featureServiceCache)
    assertEquals(2, threats.size)
    for (threat <- threats) {
      val entityId = threat.getOrElse("entityId", null)
      if (entityId == "c3c46ec3ab47d5ee70e3f60283015a24") {
        val ip = threat.getOrElse("ipAddress", null)
        assertTrue(ip == "192.168.1.31" || ip == "192.168.1.13")
        assertEquals("54-0C-75-00-E2-12", threat.getOrElse("macAddress", null))
        assertEquals("laptop801", threat.getOrElse("hostName", null))
        assertEquals(8, threat.getOrElse("currentModelId", null))
        assertEquals("host", threat.getOrElse("entityType", null))
      } else if (entityId == "e21") {
        assertEquals("192.168.1.29", threat.getOrElse("ipAddress", null))
        assertEquals("AA:7F:C8:56:94:71", threat.getOrElse("macAddress", null))
        assertEquals("RDP-GW", threat.getOrElse("hostName", null))
        assertEquals("administrator", threat.getOrElse("userName", null))
        assertEquals(3, threat.getOrElse("currentModelId", null))
        assertEquals("user", threat.getOrElse("entityType", null))
      } else {
        assertTrue("Unexpected entity [" + entityId + "]", false)
      }
    }
  }

  /////////////////////////////////////////////////
  // Entity properties tests
  /////////////////////////////////////////////////

  @Test
  def getEntityCardsByEntityIdTest = {
    val groups = detectorHomeDao.getEntityCardsByEntityId("2016-02-01T00:00:00.000Z", "2016-02-04T00:00:00.000Z", "e4", 0.7, 1.0, EntityFusionTest.featureServiceCache)
    assertEquals(2, groups.size)
    for (card <- groups) {
      val map = card._1
      val modelId = map.getOrElse("modelId", -1)
      val risk = map.getOrElse("riskScore", 0.0)
      modelId match {
        case Some(mId) =>
          if (mId == 9) {
            risk match {
              case Some(mRisk) =>
                val dRisk = mRisk.asInstanceOf[Double]
                assertTrue(dRisk - 0.7773 < 0.01)
              case _ => assertTrue(false)
            }
          } else if (mId == 10) {
            risk match {
              case Some(mRisk) =>
                val dRisk = mRisk.asInstanceOf[Double]
                assertTrue(dRisk - 1.0 < 0.01)
              case _ => assertTrue(false)
            }
          } else {
            assertTrue(false)
          }
        case _ => assertTrue(false)
      }
      val list = card._2
      assertEquals(3, list.size)
    }

    val g1 = detectorHomeDao.getEntityCardsByEntityId("2016-02-01T00:00:00.000Z", "2016-02-04T00:00:00.000Z", "e4", 0.8, 1.0, EntityFusionTest.featureServiceCache)
    assertEquals(1, g1.size)
    for (card <- g1) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(10, mId)
        case _ => assertTrue(false)
      }
      assertEquals(3, list.size)
    }
    //val result = EntityFusionTest.dhf.getEntityCardsById("2016-02-01T00:00:00.000Z", "2016-02-04T00:00:00.000Z", "e4", "0.8", "1.0")
  }

  /////////////////////////////////////////////////
  // Entity properties tests
  /////////////////////////////////////////////////

  @Test
  def getEntityPropertiesByEntityIdTest() {
    // Test endpoint data
    val endpointResult = entityInvestigatorDao.getEntityPropertiesByEntityId(EntityFusionTest.propTestEntityId, "2016-09-01T00:00:00.000Z", "2016-09-02T00:00:00.000Z")
    assertEquals(1, endpointResult.size)
    for (entity <- endpointResult) {
      val entityId = entity.getOrElse("entityId", null)
      assertEquals(EntityFusionTest.propTestEntityId, entityId)
      val entityType = entity.getOrElse("entityType", null)
      assertEquals(Entity.HOST_ENTITY, entityType)
      val fusionTimes = entity.getOrElse("dateTime", null).asInstanceOf[util.List[Long]]
      assertNotNull(fusionTimes)
      assertEquals(2, fusionTimes.size)
      assertEquals(1472756400000l, fusionTimes.get(0))
      assertEquals(1472752800000l, fusionTimes.get(1))
      val ips = entity.getOrElse("ip", null).asInstanceOf[ListBuffer[Map[String, String]]]
      assertNotNull(ips)
      assertEquals(2, ips.size)
      assertEquals("1.1.1.1", ips(0).getOrElse("value", null))
      assertEquals("2.2.2.2", ips(1).getOrElse("value", null))
      val browserNames = entity.getOrElse("browserName", null).asInstanceOf[ListBuffer[Map[String, String]]]
      assertNotNull(browserNames)
      assertEquals(2, browserNames.size)
      assertEquals("Safari", browserNames(0).getOrElse("value", null))
      assertEquals("Firefox", browserNames(1).getOrElse("value", null))
      val macAddresses = entity.getOrElse("macAddress", null).asInstanceOf[ListBuffer[Map[String, String]]]
      assertNotNull(macAddresses)
      assertEquals(1, macAddresses.size)
      assertEquals("11:22:C8:56:84:17", macAddresses(0).getOrElse("value", null))
      val userNames = entity.getOrElse("userName", null).asInstanceOf[ListBuffer[Map[String, String]]]
      assertNotNull(userNames)
      assertEquals(2, userNames.size)
      assertEquals("user1", userNames(0).getOrElse("value", null))
      assertEquals("user2", userNames(1).getOrElse("value", null))
      val hostNames = entity.getOrElse("hostName", null).asInstanceOf[ListBuffer[Map[String, String]]]
      assertNotNull(hostNames)
      assertEquals(2, hostNames.size)
      assertEquals("host1", hostNames(0).getOrElse("value", null))
      assertEquals("host2", hostNames(1).getOrElse("value", null))
      val osNames = entity.getOrElse("os", null).asInstanceOf[ListBuffer[Map[String, String]]]
      assertNotNull(osNames)
      assertEquals(1, osNames.size)
      assertEquals("Windows10", osNames(0).getOrElse("value", null))
    }

    // Test query for an entity that doesn't exists, to ensure we don't fail.
    val emptyResult = entityInvestigatorDao.getEntityPropertiesByEntityId("doesntexist",  "2016-09-01T00:00:00.000Z", "2016-09-02T00:00:00.000Z")
    assertEquals(1, emptyResult.size)
    for (entity <- emptyResult) {
      assertEquals(0, entity.size)
    }
  }

  @Test
  def getEndpointEntityPropertiesByEntityIdTest() = {
    val endpointProps = entityInvestigatorDao.getEndpointEntityPropertiesByEntityId(EntityFusionTest.propTestEntityId, "md5", "processName", "2016-09-01T00:00:00.000Z", "2016-09-02T00:00:00.000Z")
    assertEquals(1, endpointProps.size)
    for (propMap <- endpointProps) {
      val hostProps = propMap.getOrElse("hostProperties", null)
      assertEquals(1, hostProps.size)
      val hostPropMap = hostProps.head
      assertEquals(EntityFusionTest.propTestEntityId, hostPropMap.getOrElse("entityId", null))
      assertEquals(Entity.HOST_ENTITY, hostPropMap.getOrElse("entityType", null))
      val ipList = hostPropMap.getOrElse("ip", null).asInstanceOf[ListBuffer[scala.collection.mutable.Map[String, String]]]
      for (ipMap <- ipList) {
        val ip = ipMap.getOrElse("value", null)
        if (ip == "1.1.1.1") {
          assertEquals("source1", ipMap.getOrElse("source", null))
        } else if (ip == "2.2.2.2") {
          assertEquals("source2", ipMap.getOrElse("source", null))
        } else {
          assertTrue("got unexpected ip [" + ip + "]", false)
        }
      }
      val createDateList = hostPropMap.getOrElse("lastLogonDate", null).asInstanceOf[ListBuffer[scala.collection.mutable.Map[String, String]]]
      assertEquals(1, createDateList.size)
      val date0 = createDateList(0).getOrElse("value", null)
      assertEquals(EntityFusionTest.propTestFusionDate2, date0)
      val procProps = propMap.getOrElse("processProperties", null)
      assertEquals(0, procProps.size) // The procProps are empty because we can't actually get data via impala in a test.
      val riskProps = propMap.getOrElse("riskScoreProperties", null)
      assertEquals(1, riskProps.size)
      val riskMap = riskProps.head
      val globalRisk = riskMap.getOrElse("globalRiskScore", -1.0).asInstanceOf[Double]
      assertTrue(Math.abs(globalRisk - 1.0) < 0.001)
      val localRisk = riskMap.getOrElse("riskScore", -1.0).asInstanceOf[Double]
      assertTrue(Math.abs(localRisk - 0.2773) < 0.001)
    }
  }

  /////////////////////////////////////////////////
  // Model scores tests
  /////////////////////////////////////////////////

  @Test
  def getModelScoresTest = {
    val modelScores = detectorHomeDao.getModelScoresById("e4", "2016-02-01T00:00:00.000Z", "2016-02-04T00:00:00.000Z", 0.5, 1.0, EntityFusionTest.featureServiceCache)
    assertEquals(1, modelScores.size)
    val scoreMap = modelScores.head
    assertNotNull(scoreMap)
    val scoreList = scoreMap.getOrElse("modelScores", null).asInstanceOf[ListBuffer[EntityModelInfo]]
    assertEquals(2, scoreList.size)
    for (score <- scoreList) {
      if (score.getModel == 9) {
        assertTrue(math.abs(score.getRisk - 0.7773) < 0.001)
        assertEquals("Local Newly Observed Process", score.getEventType)
      } else if (score.getModel == 10) {
        assertTrue(math.abs(score.getRisk - 1.0) < 0.001)
        assertEquals("Global Newly Observed Process", score.getEventType)
      } else {
        assertTrue("got unexpected model in scores [" + score.getModel + "]", false)
      }
    }
  }

}
