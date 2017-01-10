package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import java.util
import javax.validation.{Validation, Validator}

import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.{SecurityEventBehavior, QueryJson}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.{MongoUtils, TaniumHostGroupInfo, DetectorHomeDao, PhoenixUtils}
import com.securityx.modelfeature.utils._
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

object DetectorHomeDaoEntityCardsTest extends MongoTestBase {
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
    dropTables()
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
      "    HOST_ENTITY_ID VARCHAR,\n" +
      "    USER_ENTITY_ID VARCHAR,\n" +
    "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID )\n" +
      ") IMMUTABLE_ROWS=true"
    val createStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
    createStmt.execute

    var upsertSql: String = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-01T00:00:00.000Z', '192.168.1.150', '7C-12-F1-53-5D-76', 'laptop850', 'e\\\\8\\\\jdoe', 3, 8, 0.17337, null, 'e-user1')"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-01T00:00:00.000Z', '192.168.1.151', '63-A6-D2-AB-F2-B2', 'laptop851', 'kenbrell_thompkins', 3, 14, 0.4135, null, 'e-user2')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-02-02T00:00:00.000Z', '192.168.1.174', 'E4-EB-C9-03-9B-41', 'laptop874', 'e\\\\8\\\\jdoe', 7, 502, 0.08, null, 'e-user1')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-21T00:00:00.000Z', '192.168.12.27', '12:7F:C8:56:84:17', 'RDP-GW', 'NULL_VALUE', 9, 0, 0.2773, 'e-host1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-21T00:00:00.000Z', '192.168.12.27', '12:7F:C8:56:84:17', 'RDP-GW', 'NULL_VALUE', 10, 0, 1.0, 'e-host1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.102', 'CC:7F:C8:56:84:17', 'sharedHost', 'NULL_VALUE', 8, 0, 0.4, 'e-host3', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-07T00:00:00.000Z', '192.168.1.13', '54-0C-75-00-E2-12', 'laptop801', 'NULL_VALUE', 2, 11, 0.5007282761847072, 'c3c46ec3ab47d5ee70e3f60283015a24', 'fad81700446912bf5063a3368ffe3920')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-07T00:00:00.000Z', '192.168.1.23', '29-1F-4E-A7-FA-F5', 'laptop802', 'NULL_VALUE', 2, 11, 0.5007282761847072, '1c7998846f7ad598b5e39d1c83f348ef', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    val peerEntityCardDetails: String = "CREATE TABLE IF NOT EXISTS " + PeerEntityCardDetails.getName(configuration) + " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    MODEL_ID INTEGER NOT NULL,\n" +
      "    SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
      "    SOURCE_IP VARCHAR,\n" +
      "    SOURCE_USER_NAME VARCHAR,\n" +
      "    SOURCE_PORT INTEGER,\n" +
      "    DESTINATION_IP VARCHAR,\n" +
      "    DESTINATION_USER_NAME VARCHAR,\n" +
      "    DESTINATION_PORT INTEGER,\n" +
      "    EVENT_DESCRIPTION VARCHAR,\n" +
      "    WIN_EVENT_ID INTEGER,\n" +
      "    WIN_EVENT_TYPE VARCHAR,\n" +
      "    BYTES_IN BIGINT,\n" +
      "    BYTES_OUT BIGINT,\n" +
      "    URL VARCHAR,\n" +
      "    HTTP_METHOD VARCHAR,\n" +
      "    USER_AGENT VARCHAR,\n" +
      "    HTTP_RESPONSE_CODE VARCHAR,\n" +
      "    SOURCE_ENTITY_ID VARCHAR,\n" +
      "    DESTINATION_ENTITY_ID VARCHAR\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, DESTINATION_IP, DESTINATION_USER_NAME )\n" +
      ") IMMUTABLE_ROWS=true"
    val peerEntityCardDetailsCreate: PreparedStatement = conn.prepareStatement(peerEntityCardDetails)
    peerEntityCardDetailsCreate.execute

    upsertSql = "upsert into " + PeerEntityCardDetails.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, SOURCE_PORT, DESTINATION_IP, DESTINATION_USER_NAME, " +
      "DESTINATION_PORT, EVENT_DESCRIPTION, WIN_EVENT_ID, WIN_EVENT_TYPE, BYTES_IN, BYTES_OUT, URL, HTTP_METHOD, USER_AGENT, HTTP_RESPONSE_CODE, SOURCE_ENTITY_ID, DESTINATION_ENTITY_ID) " +
      "values ('2016-02-01T00:00:00.000Z', 3, 8, 'LAPTOP850-192.168.1.150', '', 0, '', 'acmebank.com-e\\\\8\\\\jdoe', 0, 'Anomalous User Activity', 4624, 'An account was successfully logged on', 0, 0, '', '', '', '', 'e-user1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerEntityCardDetails.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, SOURCE_PORT, DESTINATION_IP, DESTINATION_USER_NAME, " +
      "DESTINATION_PORT, EVENT_DESCRIPTION, WIN_EVENT_ID, WIN_EVENT_TYPE, BYTES_IN, BYTES_OUT, URL, HTTP_METHOD, USER_AGENT, HTTP_RESPONSE_CODE, SOURCE_ENTITY_ID, DESTINATION_ENTITY_ID) " +
      "values ('2016-02-01T08:00:00.000Z', 3, 8, 'LAPTOP850-192.168.1.150', '', 0, '', 'acmebank.com-e\\\\8\\\\jdoe', 0, 'Anomalous User Activity', 4624, 'An account was successfully logged on', 0, 0, '', '', '', '', 'e-user1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerEntityCardDetails.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, SOURCE_PORT, DESTINATION_IP, DESTINATION_USER_NAME, " +
      "DESTINATION_PORT, EVENT_DESCRIPTION, WIN_EVENT_ID, WIN_EVENT_TYPE, BYTES_IN, BYTES_OUT, URL, HTTP_METHOD, USER_AGENT, HTTP_RESPONSE_CODE, SOURCE_ENTITY_ID, DESTINATION_ENTITY_ID) " +
      "values ('2016-02-01T09:00:00.000Z', 3, 8, 'LAPTOP850-192.168.1.150', '', 0, '', 'acmebank.com-e\\\\8\\\\jdoe', 0, 'Anomalous User Activity', 4624, 'An account was successfully logged on', 0, 0, '', '', '', '', 'e-user1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerEntityCardDetails.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, SOURCE_PORT, DESTINATION_IP, DESTINATION_USER_NAME, " +
      "DESTINATION_PORT, EVENT_DESCRIPTION, WIN_EVENT_ID, WIN_EVENT_TYPE, BYTES_IN, BYTES_OUT, URL, HTTP_METHOD, USER_AGENT, HTTP_RESPONSE_CODE, SOURCE_ENTITY_ID, DESTINATION_ENTITY_ID) " +
      "values ('2016-02-01T11:00:00.000Z', 3, 8, 'LAPTOP850-192.168.1.150', '', 0, '', 'acmebank.com-e\\\\8\\\\jdoe', 0, 'Anomalous User Activity', 4624, 'An account was successfully logged on', 0, 0, '', '', '', '', 'e-user1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerEntityCardDetails.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, SOURCE_PORT, DESTINATION_IP, DESTINATION_USER_NAME, " +
      "DESTINATION_PORT, EVENT_DESCRIPTION, WIN_EVENT_ID, WIN_EVENT_TYPE, BYTES_IN, BYTES_OUT, URL, HTTP_METHOD, USER_AGENT, HTTP_RESPONSE_CODE, SOURCE_ENTITY_ID, DESTINATION_ENTITY_ID) " +
      "values ('2016-02-01T15:00:00.000Z', 3, 8, 'LAPTOP850-192.168.1.150', '', 0, '', 'acmebank.com-e\\\\8\\\\jdoe', 0, 'Anomalous User Activity', 4624, 'An account was successfully logged on', 0, 0, '', '', '', '', 'e-user1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + PeerEntityCardDetails.getName(configuration) + " (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_IP, SOURCE_USER_NAME, SOURCE_PORT, DESTINATION_IP, DESTINATION_USER_NAME, " +
      "DESTINATION_PORT, EVENT_DESCRIPTION, WIN_EVENT_ID, WIN_EVENT_TYPE, BYTES_IN, BYTES_OUT, URL, HTTP_METHOD, USER_AGENT, HTTP_RESPONSE_CODE, SOURCE_ENTITY_ID, DESTINATION_ENTITY_ID) " +
      "values ('2016-02-01T17:00:00.000Z', 3, 8, 'LAPTOP850-192.168.1.150', '', 0, '', 'acmebank.com-e\\\\8\\\\jdoe', 0, 'Anomalous User Activity', 4624, 'An account was successfully logged on', 0, 0, '', '', '', '', 'e-user1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    val noveltyDetector: String = "CREATE TABLE IF NOT EXISTS " + NoveltyDetector.getName(configuration) + " (\n" +
      "     DATE_TIME VARCHAR NOT NULL,\n" +
      "     ENTITY VARCHAR NOT NULL,\n" +
      "     SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
      "     MODEL_ID INTEGER NOT NULL,\n" +
      "     USER_TOTAL INTEGER,\n" +
      "     FEATURE_KEY VARCHAR NULL,\n" +
      "     CEF_SIGNATURE_ID VARCHAR,\n" +
      "     FEATURE_KEY_DESCRIPTION VARCHAR,\n" +
      "     FEATURE_KEY_LABEL VARCHAR,\n" +
      "     FEATURE_KEY_COUNT INTEGER,\n" +
      "     NEWLY_OBSERVED VARCHAR,\n" +
      "     RARITY_SCORE DOUBLE,\n" +
      "     ANOMALY_SCORE DOUBLE,\n" +
      "     TARGET_DESCRIPTION VARCHAR NOT NULL,\n" +
      "     ENTITY_ID VARCHAR\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, ENTITY, SECURITY_EVENT_ID, MODEL_ID, FEATURE_KEY, TARGET_DESCRIPTION )\n)"
    val noveltyDetectorCreate: PreparedStatement = conn.prepareStatement(noveltyDetector)
    noveltyDetectorCreate.execute

    upsertSql = "upsert into " + NoveltyDetector.getName(configuration) + " (DATE_TIME, ENTITY, SECURITY_EVENT_ID, MODEL_ID, USER_TOTAL, FEATURE_KEY, CEF_SIGNATURE_ID, FEATURE_KEY_DESCRIPTION, " +
      "FEATURE_KEY_LABEL, FEATURE_KEY_COUNT, NEWLY_OBSERVED, RARITY_SCORE, ANOMALY_SCORE, TARGET_DESCRIPTION, ENTITY_ID) " +
      "values ('2016-02-02T00:00:00.000Z', 'acmebank.com-e\\\\8\\\\jdoe', 502, 7, 67, '(2)', 'Security-4625-Failure Audit', " +
      "'(destinationNtDomain,destinationUserName,sourceLogonType)', 'Newly observed logon type for failed login', 1, '(2)', 56.0, 0.08, 'Logon type', 'e-user1')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

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
      "values ('2016-07-21T00:00:00.000Z', 'HOST', 'e-host1', '', '', '0.2773', '1.6636', '2016-07-12T15:03:41Z|2016-07-21T23:58:15Z', " +
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
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-20T00:00:00.000Z', 'HOST', 'RDP-GW', '', '', '0', '', '2016-07-12T15:03:41Z|2016-07-20T23:58:45Z', " +
      "'RDP-GW', 'RDP-GW', '', " +
      "'ad7b9c14083b52bc532fba5948342b98|b3fde4699997759320cba603e2aed2cd|701bb23989e138c1c8b436c5940a1f6e|1951c6f1e53079f6b29ecff77eaf9403|de701bee6a35ecd48c9a582bbff943c0|773212b2aaa24c1e31f10246b15b276c|d08f5c01b23aa7f356693c028ab592ba|f9120481520d8202fa0a02dfdb575ba7|4f2659160afcca990305816946f69407|af29e58a2f20d0bffd3eaec34a1ea10c|6aaf3bece2c3d17091bcef37c5a82ac0|545bf7eaa24a9e062857d0742ec0b28a|0db1ccd7230fdee1adb04e536ea60759|332feab1435662fc6c672e25beb37be3|637c075ca92a0e272029f53b345d1bfd|7eae59832be1c3a08d4f94cebccaf13a|a3a35ee79c64a640152b3113e6e254e2|32297bb17e6ec700d0fc869f9acaf561|4676aaa9ddf52a50c829fedb4ea81e54|825fb6de39fe63b3f59b78d760f0619c|b5c5dcad3899512020d135600129d665|979d74799ea6c8b8167869a68df5204a|54a47f6b5e09a77e61649109c6a08866', " +
      "'ad7b9c14083b52bc532fba5948342b98|701bb23989e138c1c8b436c5940a1f6e|1951c6f1e53079f6b29ecff77eaf9403|de701bee6a35ecd48c9a582bbff943c0|773212b2aaa24c1e31f10246b15b276c|4f2659160afcca990305816946f69407|f9120481520d8202fa0a02dfdb575ba7|d08f5c01b23aa7f356693c028ab592ba|af29e58a2f20d0bffd3eaec34a1ea10c|545bf7eaa24a9e062857d0742ec0b28a|332feab1435662fc6c672e25beb37be3|637c075ca92a0e272029f53b345d1bfd|6bd4d7f68924301051c22e8a951aecba|54a47f6b5e09a77e61649109c6a08866|b3fde4699997759320cba603e2aed2cd|0a44e47ff51f97dd9c5195d12c59c9ec|6aaf3bece2c3d17091bcef37c5a82ac0|0db1ccd7230fdee1adb04e536ea60759|7eae59832be1c3a08d4f94cebccaf13a|a3a35ee79c64a640152b3113e6e254e2|32297bb17e6ec700d0fc869f9acaf561|4676aaa9ddf52a50c829fedb4ea81e54|825fb6de39fe63b3f59b78d760f0619c|979d74799ea6c8b8167869a68df5204a|b5c5dcad3899512020d135600129d665', " +
      "'', 'XenGuestAgent.exe|WmiPrvSE.exe|spoolsv.exe|NETSTAT.EXE|nvvsvc.exe|csrss.exe|mmc.exe|nvSCPAPISvr.exe|rdpclip.exe|wininit.exe|services.exe|taskeng.exe|wscript.exe|Ec2Config.exe|XenDpriv.exe|msdtc.exe|LogonUI.exe|mstsc.exe|winlogon.exe|dwm.exe|svchost.exe|VSSVC.exe|System Idle Process|smss.exe|System|firefox.exe|explorer.exe|nwiz|TrustedInstaller.exe|nvxdsync.exe|cmd.exe|lsm.exe|conhost.exe|putty.exe|TaniumClient.exe|taskhost.exe|sppsvc.exe|taskmgr.exe|cscript.exe|cb.exe|lsass.exe|nvwmi64.exe', 'XenGuestAgent.exe|WmiPrvSE.exe|spoolsv.exe|NETSTAT.EXE|nvvsvc.exe|csrss.exe|mmc.exe|nvSCPAPISvr.exe|rdpclip.exe|wininit.exe|services.exe|taskeng.exe|wscript.exe|Ec2Config.exe|XenDpriv.exe|msdtc.exe|LogonUI.exe|mstsc.exe|winlogon.exe|dwm.exe|svchost.exe|VSSVC.exe|System Idle Process|smss.exe|System|firefox.exe|explorer.exe|nwiz|aitagent.exe|MpCmdRun.exe|TrustedInstaller.exe|nvxdsync.exe|cmd.exe|lsm.exe|conhost.exe|putty.exe|TaniumClient.exe|taskhost.exe|sppsvc.exe|cscript.exe|taskmgr.exe|cb.exe|PortTester.exe|lsass.exe|nvwmi64.exe', " +
      "'', '64-bit/\\\\Software\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\\\\', '64-bit/\\\\Software\\\\Microsoft\\\\Windows\\\\CurrentVersion\\\\Run\\\\', '', 'C:\\\\Windows\\\\SysWow64\\\\sppsvc.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe|C:\\\\Windows\\\\system32\\\\wininit.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe|C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe|C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe|C:\\\\Windows\\\\system32\\\\cscript.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Windows\\\\SysWOW64\\\\cscript.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe|C:\\\\Windows\\\\CarbonBlack\\\\cb.exe|C:\\\\Windows\\\\SysWow64\\\\lsass.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe /installquiet|C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe|C:\\\\Windows\\\\servicing\\\\TrustedInstaller.exe|C:\\\\Windows\\\\SysWow64\\\\csrss.exe|C:\\\\Windows\\\\system32\\\\mstsc.exe|C:\\\\Windows\\\\SysWOW64\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe|C:\\\\Windows\\\\system32\\\\taskeng.exe|C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe|C:\\\\Windows\\\\system32\\\\svchost.exe|C:\\\\Program Files (x86)\\\\PuTTY\\\\putty.exe|C:\\\\Windows\\\\System32\\\\WScript.exe|C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\System32\\\\spoolsv.exe|C:\\\\Windows\\\\Explorer.EXE|C:\\\\Windows\\\\System32\\\\msdtc.exe|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\mmc.exe|C:\\\\Windows\\\\system32\\\\taskmgr.exe|C:\\\\Windows\\\\SysWow64\\\\winlogon.exe|C:\\\\Windows\\\\System32\\\\svchost.exe|C:\\\\Windows\\\\SysWow64\\\\vssvc.exe|C:\\\\Windows\\\\SysWow64\\\\Dwm.exe|C:\\\\Windows\\\\SysWow64\\\\taskhost.exe|C:\\\\Windows\\\\SysWow64\\\\services.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\netstat.exe|C:\\\\Windows\\\\SysWow64\\\\conhost.exe|C:\\\\Windows\\\\SysWow64\\\\lsm.exe', 'C:\\\\Windows\\\\SysWow64\\\\sppsvc.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenDpriv.exe|C:\\\\Windows\\\\system32\\\\wininit.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\Display\\\\nvxdsync.exe|C:\\\\Windows\\\\system32\\\\wbem\\\\wmiprvse.exe|C:\\\\Program Files (x86)\\\\NVIDIA Corporation\\\\3D Vision\\\\nvSCPAPISvr.exe|C:\\\\Windows\\\\system32\\\\cscript.exe|C:\\\\Windows\\\\SysWOW64\\\\cscript.exe|C:\\\\Program Files (x86)\\\\Mozilla Firefox\\\\firefox.exe|C:\\\\Windows\\\\SysWow64\\\\LogonUI.exe|C:\\\\Program Files\\\\Amazon\\\\Ec2ConfigService\\\\Ec2Config.exe|C:\\\\Windows\\\\CarbonBlack\\\\cb.exe|C:\\\\Windows\\\\SysWow64\\\\lsass.exe|c:\\\\program files\\\\windows defender\\\\MpCmdRun.exe|C:\\\\Program Files\\\\NVIDIA Corporation\\\\nView\\\\nwiz.exe /installquiet|C:\\\\Windows\\\\SysWow64\\\\nvwmi64.exe|C:\\\\Windows\\\\servicing\\\\TrustedInstaller.exe|C:\\\\Windows\\\\SysWow64\\\\csrss.exe|C:\\\\Windows\\\\system32\\\\mstsc.exe|C:\\\\Windows\\\\SysWOW64\\\\cmd.exe|C:\\\\Program Files (x86)\\\\Citrix\\\\XenTools\\\\XenGuestAgent.exe|C:\\\\Windows\\\\system32\\\\taskeng.exe|C:\\\\Windows\\\\SysWow64\\\\nvvsvc.exe|C:\\\\Windows\\\\system32\\\\svchost.exe|C:\\\\Program Files (x86)\\\\PuTTY\\\\putty.exe|C:\\\\Windows\\\\System32\\\\WScript.exe|C:\\\\Windows\\\\sysWOW64\\\\wbem\\\\wmiprvse.exe|C:\\\\Windows\\\\System32\\\\spoolsv.exe|C:\\\\Windows\\\\System32\\\\msdtc.exe|C:\\\\Windows\\\\Explorer.EXE|C:\\\\Windows\\\\System32\\\\rdpclip.exe|C:\\\\Windows\\\\system32\\\\mmc.exe|C:\\\\Windows\\\\system32\\\\taskmgr.exe|C:\\\\Windows\\\\SysWow64\\\\winlogon.exe|C:\\\\Windows\\\\System32\\\\svchost.exe|C:\\\\Windows\\\\SysWow64\\\\vssvc.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\Tools\\\\porttester.exe|C:\\\\Windows\\\\SysWow64\\\\Dwm.exe|C:\\\\Windows\\\\SysWow64\\\\taskhost.exe|C:\\\\Windows\\\\SysWow64\\\\services.exe|C:\\\\Program Files (x86)\\\\Tanium\\\\Tanium Client\\\\TaniumClient.exe|C:\\\\Windows\\\\System32\\\\netstat.exe|C:\\\\Windows\\\\SysWow64\\\\conhost.exe|C:\\\\Windows\\\\SysWow64\\\\lsm.exe', " +
      "'TCP/59341|TCP/63393', 'TCP/58090|TCP/58091', 'TCP/61163|TCP/50589')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    // the next two rows we're adding are used to validate that we handle the newly_observed column correctly and generate the
    // right groupInfo in the result
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-20T00:00:00.000Z', 'MD5', 'fef2e008b08376d5a16c650ce0190609', '', '', '10', '', '2016-07-12T15:03:41Z|2016-07-20T23:58:45Z', 'WIN-OSNMCI3GJJ1', 'WIN-OSNMCI3GJJ1', '', 'fef2e008b08376d5a16c650ce0190609', 'fef2e008b08376d5a16c650ce0190609', '', 'bash.exe', 'bash.exe', '', '', '', '', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-21T00:00:00.000Z', 'MD5', 'fef2e008b08376d5a16c650ce0190609', '', 'Default:2/2', '0', '', '2016-07-12T15:03:41Z|2016-07-21T23:58:15Z', 'RDP-GW|WIN-OSNMCI3GJJ1', 'WIN-OSNMCI3GJJ1', '', 'fef2e008b08376d5a16c650ce0190609', 'fef2e008b08376d5a16c650ce0190609', '', 'bash.exe', 'bash.exe', '', '', '', '', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', 'C:\\\\cygwin64\\\\bin\\\\bash.exe', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-21T00:00:00.000Z', 'MD5', '690374e7ec63b7addfa57817d120b2f1', '', 'Default:1/2', '0.1386', '', '2016-07-21T21:35:50Z|2016-07-21T21:37:11Z', 'RDP-GW', '', '690374e7ec63b7addfa57817d120b2f1', '690374e7ec63b7addfa57817d120b2f1', '', 'mintty.exe', 'mintty.exe', '', '', '', '', 'C:\\\\cygwin64\\\\bin\\\\mintty.exe', 'C:\\\\cygwin64\\\\bin\\\\mintty.exe', '', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + TaniumStats.getName(configuration) + " (DATE_TIME, TYPE, TYPE_VALUE, PIVOTS, NEWLY_OBSERVED, RISK_SCORE, RISK_SCORE_GLOBAL, DATES_SEEN," +
      "HOSTS_CURRENT, HOSTS_HISTORICAL, MD5S_NEW, MD5S_CURRENT, MD5S_HISTORICAL, PROCESSES_NEW, PROCESSES_CURRENT, PROCESSES_HISTORICAL, " +
      "KEYS_NEW, KEYS_CURRENT, KEYS_HISTORICAL, PATHS_NEW, PATHS_CURRENT, PATHS_HISTORICAL, " +
      "PORTS_NEW, PORTS_CURRENT, PORTS_HISTORICAL) " +
      "values ('2016-07-21T00:00:00.000Z', 'MD5', '61ac3efdfacfdd3f0f11dd4fd4044223', '', 'Default:1/2', '0.1386', '', '2016-07-21T21:25:40Z|2016-07-21T21:27:08Z', 'RDP-GW', '', '61ac3efdfacfdd3f0f11dd4fd4044223', '61ac3efdfacfdd3f0f11dd4fd4044223', '', 'userinit.exe', 'userinit.exe', '', '', '', '', 'C:\\\\Windows\\\\system32\\\\userinit.exe', 'C:\\\\Windows\\\\system32\\\\userinit.exe', '', '', '', '')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    val c2ModelSql: String = "CREATE TABLE IF NOT EXISTS " + C2Model.getName(configuration) + " (\n" +
      "    DATE_TIME VARCHAR NOT NULL,\n" +
      "    SOURCE_NAME_OR_IP VARCHAR NOT NULL,\n" +
      "    DESTINATION_NAME_OR_IP VARCHAR NOT NULL,\n" +
      "    SLD VARCHAR,\n" +
      "    RISK_SCORE DOUBLE,\n" +
      "    C2_FACTORS VARCHAR,\n" +
      "    C2_FACTOR_VALUES VARCHAR,\n" +
      "    C2_TRAFFIC_HOURS VARCHAR,\n" +
      "    SOURCE_ENTITY_ID VARCHAR\n" +
      "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP )\n) IMMUTABLE_ROWS=true, SALT_BUCKETS=10"
    val c2ModelCreateStmt: PreparedStatement = conn.prepareStatement(c2ModelSql)
    c2ModelCreateStmt.execute

    upsertSql = "upsert into " + C2Model.getName(configuration) + " (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS, SOURCE_ENTITY_ID)" +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.102', '64.4.55.45', '64.4.55.45', 0.17143, '(1, 0, 0, 0, 0, 1, 1, 1, 0)', " +
      "'(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(17),(8),(9),(10),(19)}', 'e-host3')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + C2Model.getName(configuration) + " (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS, SOURCE_ENTITY_ID)" +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.102', 'oostkamp.org', 'oostkamp.org', 0.17143, '(1, 0, 0, 0, 0, 1, 1, 1, 0)', " +
      "'(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(2)}', 'e-host3')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + C2Model.getName(configuration) + " (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS, SOURCE_ENTITY_ID)" +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.102', 'ww3.komotv.com', 'komotv.com', 0.17143, '(1, 0, 0, 0, 0, 1, 1, 1, 0)', " +
      "'(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(1)}', 'e-host3')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + C2Model.getName(configuration) + " (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS, SOURCE_ENTITY_ID)" +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.102', 'www.excaliburcasino.com', 'www.excaliburcasino.com', 0.2, '(1, 0, 0, 0, 0, 1, 1, 1, 0)', " +
      "'(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(2)}', 'e-host3')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
  }

  // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call HBaseTestBase.HBaseTeardown().  See comments
  // in HBaseTestBase for details.
  // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
  @AfterClass
  def stopServers() {
    dropTables()
    MongoTestBase.teardownMongo()
    HBaseTestBase.teardownHBase()
  }
  def dropTables(): Unit ={
    try {
      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
      val entityThreatSql: String = "DROP TABLE IF EXISTS " + EntityThreat.getName(configuration)

      val entityThreatStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
      entityThreatStmt.execute
      val peerEntityCardDetailsSql: String = "DROP TABLE IF EXISTS " + PeerEntityCardDetails.getName(configuration)
      val peerEntityCardDetailsStmt: PreparedStatement = conn.prepareStatement(peerEntityCardDetailsSql)
      peerEntityCardDetailsStmt.execute
      val entUserPropsSql: String = "DROP TABLE IF EXISTS " + NoveltyDetector.getName(configuration)
      val entUserPropsStmt: PreparedStatement = conn.prepareStatement(entUserPropsSql)
      entUserPropsStmt.execute
      val taniumStatsSql: String = "DROP TABLE IF EXISTS " + TaniumStats.getName(configuration)
      val taniumStatsStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
      taniumStatsStmt.execute
      val customBehaviorSql: String = "DROP TABLE IF EXISTS " + CustomBehavior.getName(configuration)
      val customBehaviorStmt: PreparedStatement = conn.prepareStatement(customBehaviorSql)
      customBehaviorStmt.execute
      val c2ModelSql: String = "DROP TABLE IF EXISTS " + C2Model.getName(configuration)
      val c2ModelStmt: PreparedStatement = conn.prepareStatement(c2ModelSql)
      c2ModelStmt.execute
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


// The tests in this class test both the old getEntityCards() method and the new getEntityCardsByEntityId() method.  Both tests are important
// at the moment (in the early testing phase of Bellatrix), since we are going to support both the old and the new apis for the moment.  But
// once the old UI is no longer in use, we can remove the tests of the getEntityCards() method.
class DetectorHomeDaoEntityCardsTest {
  private val detectorHomeDao = new DetectorHomeDao(DetectorHomeDaoEntityCardsTest.configuration)

  // Test that we can get entity cards for the peer anomaly model even when the user name
  // contains a backslash
  @Test
  def getEntityCardsPeerAnomalyBackslash() ={
    val entityCards = detectorHomeDao.getEntityCards("2016-02-01T00:00:00.000Z", "2016-02-02T00:00:00.000Z", "192.168.1.150", "e\\8\\jdoe", "laptop850", "7C-12-F1-53-5D-76", DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(1, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(3, mId)
        case _ => assertTrue(false)
      }
      val eventId = map.getOrElse("eventId", -1)
      eventId match {
        case Some(eId) => assertEquals(8, eId)
        case _ => assertTrue(false)
      }
      assertEquals(6, list.size)
    }
  }

  @Test
  def getEntityCardsPeerAnomalyById() ={
    val entityCards = detectorHomeDao.getEntityCardsByEntityId("2016-02-01T00:00:00.000Z", "2016-02-02T00:00:00.000Z", "e-user1", 0.001, 1.0, DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(1, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(3, mId)
        case _ => assertTrue(false)
      }
      val eventId = map.getOrElse("eventId", -1)
      eventId match {
        case Some(eId) => assertEquals(8, eId)
        case _ => assertTrue(false)
      }
      assertEquals(6, list.size)
    }
  }

  // Test that we can get entity cards for the novelty detector model even when the user name
  // contains a backslash
  @Test
  def getEntityCardsNoveltyDetectorBackslash() ={
    val entityCards = detectorHomeDao.getEntityCards("2016-02-02T00:00:00.000Z", "2016-02-03T00:00:00.000Z", "192.168.1.174", "e\\8\\jdoe", "laptop874", "E4-EB-C9-03-9B-41", DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(1, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(7, mId)
        case _ => assertTrue(false)
      }
      val eventId = map.getOrElse("eventId", -1)
      eventId match {
        case Some(eId) => assertEquals(502, eId)
        case _ => assertTrue(false)
      }
      assertEquals(1, list.size)
    }
  }

  @Test
  def getEntityCardsNoveltyDetectorById() ={
    val entityCards = detectorHomeDao.getEntityCardsByEntityId("2016-02-02T00:00:00.000Z", "2016-02-03T00:00:00.000Z", "e-user1", 0.001, 1.0, DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(1, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(7, mId)
        case _ => assertTrue(false)
      }
      val eventId = map.getOrElse("eventId", -1)
      eventId match {
        case Some(eId) => assertEquals(502, eId)
        case _ => assertTrue(false)
      }
      assertEquals(1, list.size)
    }
  }

  @Test
  def getEntityCardsTaniumTest() = {
    val entityCards = detectorHomeDao.getEntityCards("2016-07-20T00:00:00.000Z", "2016-07-22T00:00:00.000Z", "192.168.12.27", "", "RDP-GW", "12:7F:C8:56:84:17", DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(2, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      val secEventType = map.getOrElse("securityEventType", "")
      modelId match {
        case Some(mId) => {
          if (mId == 9) {
            secEventType match {
              case Some(secEType) => assertEquals ("Local Newly Observed Process", secEType)
              case _ => assertTrue(false)
            }
            assertEquals(3, list.size)
          } else if (mId == 10) {
            secEventType match {
              case Some(secEType) => assertEquals ("Global Newly Observed Process", secEType)
              case _ => assertTrue(false)
            }
            assertEquals(2, list.size)
          } else {
            assertTrue("unexpected model id [" + mId + "]", false)
          }
        }
        case _ => assertTrue(false)
      }
      for (md5Map <- list) {
        val md5 = md5Map.getOrElse("md5", "")
        val groupInfo = md5Map.getOrElse("groupInfo", null).asInstanceOf[ListBuffer[TaniumHostGroupInfo]]
        assertTrue(md5.equals("690374e7ec63b7addfa57817d120b2f1") || md5.equals("61ac3efdfacfdd3f0f11dd4fd4044223") || md5.equals("fef2e008b08376d5a16c650ce0190609") )
        assertEquals(1, groupInfo.size)
        for (group <- groupInfo) {
          assertEquals("Default", group.groupName)
          assertEquals(2, group.totalHosts)
          if (md5.equals("fef2e008b08376d5a16c650ce0190609")) {
            assertEquals(2, group.hostSeen)
          } else {
            assertEquals(1, group.hostSeen)
          }
        }
      }
    }
  }

  @Test
  def getEntityCardsTaniumByIdTest() = {
    val entityCards = detectorHomeDao.getEntityCardsByEntityId("2016-07-20T00:00:00.000Z", "2016-07-22T00:00:00.000Z", "e-host1", 0.1, 1.0, DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(2, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      val secEventType = map.getOrElse("securityEventType", "")
      modelId match {
        case Some(mId) => {
          if (mId == 9) {
            secEventType match {
              case Some(secEType) => assertEquals ("Local Newly Observed Process", secEType)
              case _ => assertTrue(false)
            }
            assertEquals(3, list.size)
          } else if (mId == 10) {
            secEventType match {
              case Some(secEType) => assertEquals ("Global Newly Observed Process", secEType)
              case _ => assertTrue(false)
            }
            assertEquals(2, list.size)
          } else {
            assertTrue("unexpected model id [" + mId + "]", false)
          }
        }
        case _ => assertTrue(false)
      }
      for (md5Map <- list) {
        val md5 = md5Map.getOrElse("md5", "")
        val groupInfo = md5Map.getOrElse("groupInfo", null).asInstanceOf[ListBuffer[TaniumHostGroupInfo]]
        assertTrue(md5.equals("690374e7ec63b7addfa57817d120b2f1") || md5.equals("61ac3efdfacfdd3f0f11dd4fd4044223") || md5.equals("fef2e008b08376d5a16c650ce0190609") )
        assertEquals(1, groupInfo.size)
        for (group <- groupInfo) {
          assertEquals("Default", group.groupName)
          assertEquals(2, group.totalHosts)
          if (md5.equals("fef2e008b08376d5a16c650ce0190609")) {
            assertEquals(2, group.hostSeen)
          } else {
            assertEquals(1, group.hostSeen)
          }
        }
      }
    }
  }

  @Test
  def getEntityCardsC2Test() = {
    val entityCards = detectorHomeDao.getEntityCards("2016-07-01T00:00:00.000Z", "2016-07-02T00:00:00.000Z", "192.168.15.102", "", "sharedHost", "CC:7F:C8:56:84:17", DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(2, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(8, mId)
        case _ => assertTrue(false)
      }
      val secEventType = map.getOrElse("securityEventType", "")
      secEventType match {
        case Some(secET) => assertEquals("Command and Control", secET)
        case _ => assertTrue(false)
      }
    }
  }

  @Test
  def getEntityCardsC2ByEntityIdTest() = {
    val entityCards = detectorHomeDao.getEntityCardsByEntityId("2016-07-01T00:00:00.000Z", "2016-07-04T00:00:00.000Z", "e-host3", 0.1, 1.0, DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(2, entityCards.size)
    for (card <- entityCards) {
      val map = card._1
      val list = card._2
      val modelId = map.getOrElse("modelId", -1)
      modelId match {
        case Some(mId) => assertEquals(8, mId)
        case _ => assertTrue(false)
      }
      val secEventType = map.getOrElse("securityEventType", "")
      secEventType match {
        case Some(secET) => assertEquals("Command and Control", secET)
        case _ => assertTrue(false)
      }
    }
  }

  /******************************************************************************************/
  /*  Tests for getTopNRiskyEntitiesByBehavior()                                            */
  /******************************************************************************************/

  // Test with the old style ip/mac/user/host exclusion method
  @Test
  def getTopNRiskyEntitiesByBehaviorByHostUserIpMacTest() = {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-11-07T00:00:00.000Z")
    query.setEndTime("2016-11-08T00:00:00.000Z")
    val behaviors: util.List[SecurityEventBehavior] = new util.ArrayList[SecurityEventBehavior]()
    val securityEventBehavior: SecurityEventBehavior = new SecurityEventBehavior
    securityEventBehavior.setModelId(2)
    val secEventIds: util.List[Integer] = new util.ArrayList[Integer]()
    secEventIds.add(11)
    securityEventBehavior.setSecurityEventId(secEventIds)
    behaviors.add(securityEventBehavior)
    query.setBehaviors(behaviors)
    val ignored: util.List[util.Map[String, String]] = new util.ArrayList[util.Map[String, String]]()
    val ignoredEntry: util.Map[String, String] = new util.HashMap[String, String]()
    ignoredEntry.put("sourceIp", "192.168.1.23")
    ignoredEntry.put("hostName", "laptop802")
    ignoredEntry.put("userName", "")
    ignoredEntry.put("macAddress", "29-1F-4E-A7-FA-F5")
    ignored.add(ignoredEntry)
    query.setIngnoredEntities(ignored)
    query.setLimit(6)
    query.setSortField("risk")
    query.setSortOrder("DESC")

    val riskyEntities = detectorHomeDao.getTopNRiskyEntitiesByBehavior(query, DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(1, riskyEntities.getOrElse("total", 0))
    val entities = riskyEntities.getOrElse("entities", null).asInstanceOf[ListBuffer[MutableMap[String, String]]]
    assertEquals(1, entities.size)
    val entity = entities.head
    assertEquals("c3c46ec3ab47d5ee70e3f60283015a24", entity.getOrElse("entityId", null))
    assertEquals("host", entity.getOrElse("entityType", null))
    assertEquals("54-0C-75-00-E2-12", entity.getOrElse("macAddress", null))
    assertEquals("192.168.1.13", entity.getOrElse("ipAddress", null))
    assertEquals("", entity.getOrElse("userName", null))
    assertEquals("laptop801", entity.getOrElse("hostName", null))
  }

  // Test with the new style entityId exclusion
  @Test
  def getTopNRiskyEntitiesByBehaviorByEntityId() = {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-11-07T00:00:00.000Z")
    query.setEndTime("2016-11-08T00:00:00.000Z")
    val behaviors: util.List[SecurityEventBehavior] = new util.ArrayList[SecurityEventBehavior]()
    val securityEventBehavior: SecurityEventBehavior = new SecurityEventBehavior
    securityEventBehavior.setModelId(2)
    val secEventIds: util.List[Integer] = new util.ArrayList[Integer]()
    secEventIds.add(11)
    securityEventBehavior.setSecurityEventId(secEventIds)
    behaviors.add(securityEventBehavior)
    query.setBehaviors(behaviors)
    val ignored: util.List[util.Map[String, String]] = new util.ArrayList[util.Map[String, String]]()
    val ignoredEntry: util.Map[String, String] = new util.HashMap[String, String]()
    ignoredEntry.put("entityId", "1c7998846f7ad598b5e39d1c83f348ef")
    ignored.add(ignoredEntry)
    query.setIngnoredEntities(ignored)
    query.setLimit(6)
    query.setSortField("risk")
    query.setSortOrder("DESC")

    val riskyEntities = detectorHomeDao.getTopNRiskyEntitiesByBehavior(query, DetectorHomeDaoEntityCardsTest.featureServiceCache)
    assertEquals(1, riskyEntities.getOrElse("total", 0))
    val entities = riskyEntities.getOrElse("entities", null).asInstanceOf[ListBuffer[MutableMap[String, String]]]
    assertEquals(1, entities.size)
    val entity = entities.head
    assertEquals("c3c46ec3ab47d5ee70e3f60283015a24", entity.getOrElse("entityId", null))
    assertEquals("host", entity.getOrElse("entityType", null))
    assertEquals("54-0C-75-00-E2-12", entity.getOrElse("macAddress", null))
    assertEquals("192.168.1.13", entity.getOrElse("ipAddress", null))
    assertEquals("", entity.getOrElse("userName", null))
    assertEquals("laptop801", entity.getOrElse("hostName", null))
  }
}
