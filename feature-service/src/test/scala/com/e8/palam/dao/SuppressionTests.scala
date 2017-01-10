package com.e8.palam.dao

import java.io.File
import java.sql.{Connection, PreparedStatement}
import java.util
import javax.validation.{Validation, Validator}

import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.securityx.modelfeature.common.EntityModelInfo
import org.bson.Document
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao._
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable.ListBuffer

object SuppressionTests extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  //val MONGO_DB_SERVER = "10.10.80.77" //"10.10.30.20"
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

      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
      val entityThreatSql: String = "CREATE TABLE IF NOT EXISTS ENTITY_THREAT (\n" +
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

      // Insert some data
      // Data for 5/26 will not include any data that will be suppressed
      var upsertSql: String = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '10.10.4.57', '10:02:B5:D9:E3:60', 'DESKTOP-9UIVQ3V', ' ', 2, 11, 0.16843)"
      var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // This row should not be returned, since the previous row is for the same entity but has a higher risk score.
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '10.10.4.57', '10:02:B5:D9:E3:60', 'DESKTOP-9UIVQ3V', ' ', 2, 17, 0.15631)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '10.10.4.64', 'A4:5E:60:DD:AF:F9', 'MacBook-Pro', ' ', 2, 7, 0.07478)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '10.10.4.74', 'C4:8E:8F:F8:B5:21', 'LAPTOP-QKGG9QVJ', ' ', 2, 7, 0.07479)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      // Data for 5/27 will include suppressed data
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-27T00:00:00.000Z', '10.10.4.85', 'C4:FE:8F:F7:B5:01', 'WINDOWS-CR', ' ', 2, 7, 0.55967)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // This entry has a correct entity, but not the right behavior. So this should pass suppression and return
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-27T00:00:00.000Z', '192.168.12.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', ' ', 2, 11, 0.22413)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be suppressed
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-27T00:00:00.000Z', '10.10.4.101', '18:4F:32:F7:BE:2B', 'jyria-MBP-2', 'SRV-jyria', 2, 12, 0.63913)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      // Data for 5/28 will include more suppressed data
      // Should be suppressed, has both the correct entity and the correct behavior
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-28T00:00:00.000Z', '192.168.12.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', ' ', 2, 6, 0.49432)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned, not suppressed
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-28T00:00:00.000Z', '10.10.4.85', 'C4:FE:8F:F7:B5:01', 'WINDOWS-CR', ' ', 2, 7, 0.21146)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Correct entity, but wrong behavior - should be returned, not suppressed.  Note that if suppression doesn't work, the first row for 5/28
      // should be returned, but not this row, since this has a lower risk score than that row.
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-28T00:00:00.000Z', '192.168.12.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', ' ', 4, 18, 0.01934)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      // Data for 5/29 will be used in the search test.  The search will look for userName mark_ingram.
      // Should be suppressed, because of the ip/behavior combination
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.168.12.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', 'mark_ingram', 2, 17, 0.08975)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned - ip matches suppression, but behavior does not
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.168.12.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', 'mark_ingram', 2, 12, 0.33059)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.168.12.99', '10:02:B5:D9:E3:60', 'jlucky-MBP', 'mark_ingram', 2, 12, 0.012125)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.168.19.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', 'mark_ingram', 4, 7, 0.60443)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should not be returned - wrong user name
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.168.19.18', '10:02:B5:D9:E3:60', 'jlucky-MBP', 'matt_ingram', 4, 7, 0.011477)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be suppressed because of ip address
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.1.12.255', '11:02:F4:D9:33:63', 'elvis-MBP', 'elvis_ingram', 4, 7, 0.04396)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be suppressed because of behavior
      upsertSql = "upsert into ENTITY_THREAT (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE) " +
        "values ('2016-05-29T00:00:00.000Z', '192.1.12.117', 'D1:04:AC:D8:2C:91', 'mjordan-MBP', 'michael_jordan', 2, 13, 0.04396)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val peerGroupSql: String = "CREATE TABLE IF NOT EXISTS PEER_GROUP (\n" +
        "     DATE_TIME VARCHAR NOT NULL,\n" +
        "     PEER_ID INTEGER NOT NULL,\n" +
        "     PEER_TYPE INTEGER NOT NULL,\n" +
        "     GROUP_TYPE VARCHAR,\n" +
        "     PEER_TOTAL INTEGER,\n" +
        "     ANOMALY_SCORE DOUBLE,\n" +
        "     ENTITY_ID VARCHAR,\n" +
        "     PEER_TOP_FEATURES VARCHAR,\n" +
        "     PEER_TOP_FEATURES_DESC VARCHAR,\n" +
        "     PEER_POSITION VARCHAR,\n" +
        "     FEATURE_SCORES VARCHAR,\n" +
        "     FEATURE_SCORES_NORM VARCHAR,\n" +
        "     FEATURE_SCORES_DESC VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, PEER_ID, PEER_TYPE ))"
      val createPeerGroupStmt: PreparedStatement = conn.prepareStatement(peerGroupSql)
      createPeerGroupStmt.execute

      // This row will be returned, because while the ip matches, the behavior will not
      upsertSql = "upsert into PEER_GROUP (DATE_TIME, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, ENTITY_ID, PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, " +
        "PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
        "values ('2016-05-26T00:00:00.000Z', 101001, 2, 'peergroup', 1, 0.30061, '[\"192.168.12.18\"]', '[0]', '[High Number of Connections]', " +
        "'[0,0]', '[[21, 1.0], [1, 2.0], [8, 2.0], [12, 4.0], [15, 115.0], [0, 478.0], [18, 9.0], [9, 0.0], [20, 7.0], [19, 9.0]]', '[[21, 1.0], [1, 2.0], [8, 2.0], [12, 4.0], [15, 115.0], [0, 478.0], [18, 9.0], [9, 0.0], [20, 7.0], [19, 9.0]]', " +
        //  "'[[''high connections with nonstandard requestMethods'', 8], [''high traffic with anomalous useragents'', 10], [''high number of denied connections'', 1], [''high post uploads'', 16], [''high number of distinct requestMethods'', 9], [''high connections'', 0], [''too many useragents'', 11], [''high post downloads'', 17], [''high number of server error destinations'', 21], [''high count of nonstandard dest ports'', 5], ['high number of clienterrors', 18], [''high number of distinct uncategorized destinations'', 13], [''high number of client error destinations'', 20], [''high number of distinct destinations'', 15], [''high downloads'', 7], [''high number of distinct denied destinations'', 12], [''high distinct nonstandard dest ports'', 4], [''high number of distinct malware destinations'', 14], [''high uploads'', 6], [''high number of uncategorized connections'', 2], [''high number of servererrors'', 19], [''high number of malware/spyware connections'', 3]]')"
        "'should be a list')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // This row should be suppressed, both ip and behavior should match
      upsertSql = "upsert into PEER_GROUP (DATE_TIME, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, ENTITY_ID, PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, " +
        "PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
        "values ('2016-05-26T00:00:00.000Z', 101002, 2, 'peergroup', 1, 0.30061, '[\"192.168.12.18\"]', '[6]', '[High Number of Connections]', " +
        "'[0,0]', '[[21, 1.0], [1, 2.0], [8, 2.0], [12, 4.0], [15, 115.0], [0, 478.0], [18, 9.0], [9, 0.0], [20, 7.0], [19, 9.0]]', '[[21, 1.0], [1, 2.0], [8, 2.0], [12, 4.0], [15, 115.0], [0, 478.0], [18, 9.0], [9, 0.0], [20, 7.0], [19, 9.0]]', " +
        //  "'[[''high connections with nonstandard requestMethods'', 8], [''high traffic with anomalous useragents'', 10], [''high number of denied connections'', 1], [''high post uploads'', 16], [''high number of distinct requestMethods'', 9], [''high connections'', 0], [''too many useragents'', 11], [''high post downloads'', 17], [''high number of server error destinations'', 21], [''high count of nonstandard dest ports'', 5], ['high number of clienterrors', 18], [''high number of distinct uncategorized destinations'', 13], [''high number of client error destinations'', 20], [''high number of distinct destinations'', 15], [''high downloads'', 7], [''high number of distinct denied destinations'', 12], [''high distinct nonstandard dest ports'', 4], [''high number of distinct malware destinations'', 14], [''high uploads'', 6], [''high number of uncategorized connections'', 2], [''high number of servererrors'', 19], [''high number of malware/spyware connections'', 3]]')"
        "'should be a list')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // This row should be returned, since the user does not match
      upsertSql = "upsert into PEER_GROUP (DATE_TIME, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, ENTITY_ID, PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, " +
        "PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
        "values ('2016-05-26T00:00:00.000Z', 102001, 3, 'AD peer group', 1, 0.36873, 'acmebank.com-paul_flory', '[22]', '[Privileged Access Anomaly]', " +
        "'[0, 1]', '[[22, 2.0], [26, 0.0]]', '[[22, 2.0], [26, 0.0]]', " +
        //  "'[[''Many successful Kerberos TGT requests'', 19], [''Successfully logged on from many source IPs'', 6], [''Failed logon by many source users'', 13], [''Successfully logged on by many source users'', 7], [''Performed RUNAS on many hosts'', 28], [''Failed Kerberos service ticket requests on many services'', 3], [''Failed Kerberos service ticket requests from many source IPs'', 4], [''Many superuser logons'', 22], [''RUNAS on many destination hosts'', 24], [''Successful Kerberos service ticket requests to many services'', 0], [''Performed failed logons as many destination users'', 16], [''Many failed Kerberos service ticket requests'', 5], [''Performed successful logons as many destination users'', 10], [''RUNAS by many source users'', 23], [''Successful Kerberos TGT requests from many source IPs'', 18], [''Many successful logons'', 8], [''Successful Kerberos service ticket requests from many source IPs'', 1], [''Many RUNAS logons'', 26], [''Many failed logons'', 14], [''Performed failed logons from many source IPs'', 15], [''Performed RUNAS from many source IPs'', 29], [''Performed many failed logons'', 17], [''Performed RUNAS as many destination users'', 27], [''Failed logon from many source IPs'', 12], [''Performed many RUNAS logons'', 30], [''Performed many successful logons'', 11], [''Performed successful logons from many source IPs'', 9], [''Many failed Kerberos TGT requests'', 21], [''RUNAS from many source IPs'', 25], [''Failed Kerberos TGT requests from many source IPs'', 20], [''Many successful Kerberos service ticket requests'', 2]]')"
        "'should be a list')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // This row should be suppressed, since the user does match
      upsertSql = "upsert into PEER_GROUP (DATE_TIME, PEER_ID, PEER_TYPE, GROUP_TYPE, PEER_TOTAL, ANOMALY_SCORE, ENTITY_ID, PEER_TOP_FEATURES, PEER_TOP_FEATURES_DESC, " +
        "PEER_POSITION, FEATURE_SCORES, FEATURE_SCORES_NORM, FEATURE_SCORES_DESC) " +
        "values ('2016-05-26T00:00:00.000Z', 102007, 3, 'AD peer group', 1, 0.56873, 'acmebank.com-SRV-jyria', '[22]', '[Privileged Access Anomaly]', " +
        "'[0, 1]', '[[22, 2.0], [26, 0.0]]', '[[22, 2.0], [26, 0.0]]', " +
        //  "'[[''Many successful Kerberos TGT requests'', 19], [''Successfully logged on from many source IPs'', 6], [''Failed logon by many source users'', 13], [''Successfully logged on by many source users'', 7], [''Performed RUNAS on many hosts'', 28], [''Failed Kerberos service ticket requests on many services'', 3], [''Failed Kerberos service ticket requests from many source IPs'', 4], [''Many superuser logons'', 22], [''RUNAS on many destination hosts'', 24], [''Successful Kerberos service ticket requests to many services'', 0], [''Performed failed logons as many destination users'', 16], [''Many failed Kerberos service ticket requests'', 5], [''Performed successful logons as many destination users'', 10], [''RUNAS by many source users'', 23], [''Successful Kerberos TGT requests from many source IPs'', 18], [''Many successful logons'', 8], [''Successful Kerberos service ticket requests from many source IPs'', 1], [''Many RUNAS logons'', 26], [''Many failed logons'', 14], [''Performed failed logons from many source IPs'', 15], [''Performed RUNAS from many source IPs'', 29], [''Performed many failed logons'', 17], [''Performed RUNAS as many destination users'', 27], [''Failed logon from many source IPs'', 12], [''Performed many RUNAS logons'', 30], [''Performed many successful logons'', 11], [''Performed successful logons from many source IPs'', 9], [''Many failed Kerberos TGT requests'', 21], [''RUNAS from many source IPs'', 25], [''Failed Kerberos TGT requests from many source IPs'', 20], [''Many successful Kerberos service ticket requests'', 2]]')"
        "'should be a list')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val coordActivitySql: String = "CREATE TABLE IF NOT EXISTS COORD_ACTIVITY (\n" +
        "    DATE_TIME VARCHAR NOT NULL,\n" +
        "    EDGE_ID INTEGER NOT NULL,\n" +
        "    CLUSTER_ID INTEGER,\n" +
        "    ANOMALY_CLUSTER_SCORE DOUBLE,\n" +
        "    ANOMALY_EDGE_SCORE DOUBLE,\n" +
        "    SOURCE_NAME_OR_IP VARCHAR,\n" +
        "    DESTINATION_NAME_OR_IP VARCHAR,\n" +
        "    SELECTED_FEATURES VARCHAR,\n" +
        "    FEATURE_VALUES VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, EDGE_ID ))"
      val coordActivityGroupStmt: PreparedStatement = conn.prepareStatement(coordActivitySql)
      coordActivityGroupStmt.execute

      // Should be returned
      upsertSql = "upsert into COORD_ACTIVITY (DATE_TIME, EDGE_ID, CLUSTER_ID, ANOMALY_CLUSTER_SCORE, ANOMALY_EDGE_SCORE, SOURCE_NAME_OR_IP, " +
        "DESTINATION_NAME_OR_IP, SELECTED_FEATURES, FEATURE_VALUES) " +
        "values ('2016-05-26T00:00:00.000Z', 0, 0, 0.23651, 0.2, '192.168.1.176', 'glidewelldental.com', '[12, 8]', '[[38790.0, 7], [6.0, 0], [3874.0, 6], [4.0, 12], [2.0, 8], [0.0, 13], [0.0, 9], [0.0, 1], [0.0, 3], [0.0, 2]]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned
      upsertSql = "upsert into COORD_ACTIVITY (DATE_TIME, EDGE_ID, CLUSTER_ID, ANOMALY_CLUSTER_SCORE, ANOMALY_EDGE_SCORE, SOURCE_NAME_OR_IP, " +
        "DESTINATION_NAME_OR_IP, SELECTED_FEATURES, FEATURE_VALUES) " +
        "values ('2016-05-26T00:00:00.000Z', 1, 0, 0.23651, 0.3, '192.168.1.41', 'glidewelldental.com', '[0, 12, 8]', '[[13746.0, 7], [10.0, 0], [7988.0, 6], [4.0, 12], [4.0, 8], [0.0, 13], [0.0, 9], [0.0, 1], [0.0, 3], [0.0, 2]]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be suppressed
      upsertSql = "upsert into COORD_ACTIVITY (DATE_TIME, EDGE_ID, CLUSTER_ID, ANOMALY_CLUSTER_SCORE, ANOMALY_EDGE_SCORE, SOURCE_NAME_OR_IP, " +
        "DESTINATION_NAME_OR_IP, SELECTED_FEATURES, FEATURE_VALUES) " +
        "values ('2016-05-26T00:00:00.000Z', 2, 0, 0.23651, 0.1, '192.1.12.255', 'rcma.org', '[6]', '[[3070.0, 7], [4.0, 0], [30466.0, 6], [0.0, 12], [0.0, 8], [0.0, 13], [0.0, 9], [0.0, 1], [0.0, 3], [0.0, 2]]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      // To test the coordactivity/entites api, we also need the ENT_HOST_PROPS table
      val entHostPropsSql: String = "CREATE TABLE IF NOT EXISTS ENT_HOST_PROPS (\n" +
        "    UUID VARCHAR NOT NULL,\n" +
        "    DATE_TIME VARCHAR NOT NULL,\n" +
        "    MAC_ADDRESS VARCHAR,\n" +
        "    IP_ADDRESSES VARCHAR,\n" +
        "    COUNTRY VARCHAR,\n" +
        "    CITY VARCHAR,\n" +
        "    OS VARCHAR,\n" +
        "    BROWSERS VARCHAR,\n" +
        "    HOST_NAMES VARCHAR,\n" +
        "    PRIMARY_USERID VARCHAR,\n" +
        "    CATEGORY VARCHAR,\n" +
        "    RISK VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( UUID, DATE_TIME )\n" +
        ") IMMUTABLE_ROWS=true"
      val entHostPropsStmt: PreparedStatement = conn.prepareStatement(entHostPropsSql)
      entHostPropsStmt.execute

      // We need entries for each of the ips in the SOURCE_NAME_OR_IP column of the coord_activity table.
      upsertSql = "upsert into ENT_HOST_PROPS (UUID, DATE_TIME, MAC_ADDRESS, IP_ADDRESSES, COUNTRY, CITY, OS, BROWSERS, " +
        "HOST_NAMES, PRIMARY_USERID, CATEGORY, RISK) " +
        "values ('00028e12-1925-470d-9a4e-6093d0d8814f', '2016-05-26T00:00:00.000Z', 'A4:5E:60:C8:2E:FF', '192.168.1.176', 'None', 'None', 'N/A', " +
        "'[{\"version\": \"40.1\", \"browser\": \"Firefox\"}]', '[\"laptop860\"]', '-', 'Dpi-Http', '0.7')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENT_HOST_PROPS (UUID, DATE_TIME, MAC_ADDRESS, IP_ADDRESSES, COUNTRY, CITY, OS, BROWSERS, " +
        "HOST_NAMES, PRIMARY_USERID, CATEGORY, RISK) " +
        "values ('00028e12-1925-470d-9a4e-6093d0d8814e', '2016-05-26T00:00:00.000Z', 'A4:5E:60:C8:2E:EE', '192.168.1.41', 'None', 'None', 'Windows', " +
        "'[{\"version\": \"40.1\", \"browser\": \"Firefox\"}]', '[\"laptop861\"]', null, 'Dpi-Http', '0.6')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENT_HOST_PROPS (UUID, DATE_TIME, MAC_ADDRESS, IP_ADDRESSES, COUNTRY, CITY, OS, BROWSERS, " +
        "HOST_NAMES, PRIMARY_USERID, CATEGORY, RISK) " +
        "values ('00028e12-1925-470d-9a4e-6093d0d8814d', '2016-05-26T00:00:00.000Z', 'A4:5E:60:C8:2E:DD', '192.1.12.255', 'None', 'None', 'N/A', " +
        "'[{\"version\": \"40.1\", \"browser\": \"Firefox\"}]', '[\"\"]', '-', 'BlueCoat', '0.5')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Add one extra row with an IP that does not appear in coord_activity
      upsertSql = "upsert into ENT_HOST_PROPS (UUID, DATE_TIME, MAC_ADDRESS, IP_ADDRESSES, COUNTRY, CITY, OS, BROWSERS, " +
        "HOST_NAMES, PRIMARY_USERID, CATEGORY, RISK) " +
        "values ('00028e12-1925-470d-9a4e-e093d0d8814c', '2016-05-26T00:00:00.000Z', 'A4:5E:60:C8:2E:CC', '192.1.12.1', 'None', 'None', 'Windows', " +
        "'[{\"version\": \"40.1\", \"browser\": \"Firefox\"}]', '[\"tpap2cbabsw01\"]', '-', 'BlueCoat', '0.4')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // add a row that should be suppressed
      upsertSql = "upsert into ENT_HOST_PROPS (UUID, DATE_TIME, MAC_ADDRESS, IP_ADDRESSES, COUNTRY, CITY, OS, BROWSERS, " +
        "HOST_NAMES, PRIMARY_USERID, CATEGORY, RISK) " +
        "values ('10028e12-4925-c70d-9a4e-e093d0d9314a', '2016-05-20T00:00:00.000Z', 'A4:5E:60:C8:2E:CC', '192.1.12.255', 'None', 'None', 'Windows', " +
        "'[{\"version\": \"40.1\", \"browser\": \"Firefox\"}]', '[\"tpap2cbabsw01\"]', '-', 'BlueCoat', '0.4')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val entFusionHourlyRollupSql = "CREATE TABLE IF NOT EXISTS ENTITY_FUSION_HOURLY_ROLL_UP (\n" +
        "    FUSION_TIME VARCHAR NOT NULL,\n" +
        "    IP_ADDRESS VARCHAR NOT NULL,\n" +
        "    MAC_ADDRESS VARCHAR,\n" +
        "    HOST_NAME VARCHAR,\n " +
        "   USER_NAME VARCHAR,\n" +
        "    FIRST_SEEN_TIME VARCHAR,\n" +
        "    LAST_SEEN_TIME VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( FUSION_TIME, IP_ADDRESS )\n" +
        ") SALT_BUCKETS=10"
      val entFusionHourlyRollupStmt: PreparedStatement = conn.prepareStatement(entFusionHourlyRollupSql)
      entFusionHourlyRollupStmt.execute

      // We need an entry for every row in the ent_host_props table
      upsertSql = "upsert into ENTITY_FUSION_HOURLY_ROLL_UP (FUSION_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, FIRST_SEEN_TIME, LAST_SEEN_TIME) " +
        "values ('2016-05-26T00:00:00.000Z', '192.168.1.176', 'A4:5E:60:C8:2E:FF', 'laptop860', null, '2016-05-25T00:00:00.000Z', '2016-05-26T00:00:00.000Z')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENTITY_FUSION_HOURLY_ROLL_UP (FUSION_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, FIRST_SEEN_TIME, LAST_SEEN_TIME) " +
        "values ('2016-05-26T00:00:00.000Z', '192.168.1.41', 'A4:5E:60:C8:2E:EE', 'laptop861', null, '2016-05-25T00:00:00.000Z', '2016-05-26T00:00:00.000Z')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENTITY_FUSION_HOURLY_ROLL_UP (FUSION_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, FIRST_SEEN_TIME, LAST_SEEN_TIME) " +
        "values ('2016-05-26T00:00:00.000Z', '192.1.12.255', 'A4:5E:60:C8:2E:DD', 'bveve000lkvb009.na.dsmain.com', 'bveve000lkvb009$', '2016-05-25T00:00:00.000Z', '2016-05-26T00:00:00.000Z')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENTITY_FUSION_HOURLY_ROLL_UP (FUSION_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, FIRST_SEEN_TIME, LAST_SEEN_TIME) " +
        "values ('2016-05-26T00:00:00.000Z', '192.1.12.1', 'A4:5E:60:C8:2E:CC', 'tpap2cbabsw01', 'tpap2cbabsw01$', '2016-05-25T00:00:00.000Z', '2016-05-26T00:00:00.000Z')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val behaviorAnomalySql = "CREATE TABLE IF NOT EXISTS BEHAVIOR_ANOMALY (\n" +
        "    DATE_TIME VARCHAR NOT NULL,\n" +
        "    ENTITY VARCHAR NOT NULL,\n" +
        "    FEATURE_LABEL VARCHAR NOT NULL,\n" +
        "    MODEL_ID INTEGER,\n" +
        "    RISK_SCORE DOUBLE\n" +
        "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, ENTITY, FEATURE_LABEL )\n)"
      val behaviorAnomalyStmt: PreparedStatement = conn.prepareStatement(behaviorAnomalySql)
      behaviorAnomalyStmt.execute

      // Should be suppressed
      upsertSql = "upsert into BEHAVIOR_ANOMALY (DATE_TIME, ENTITY, FEATURE_LABEL, MODEL_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '192.168.12.18', 'high number of distinct uncategorized destinations', 4, 0.10673)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be suppressed
      upsertSql = "upsert into BEHAVIOR_ANOMALY (DATE_TIME, ENTITY, FEATURE_LABEL, MODEL_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '192.1.12.255', 'high number of distinct destinations', 4, 0.19048)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned
      upsertSql = "upsert into BEHAVIOR_ANOMALY (DATE_TIME, ENTITY, FEATURE_LABEL, MODEL_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', '192.168.1.150', 'high downloads', 4, 0.10673)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned
      upsertSql = "upsert into BEHAVIOR_ANOMALY (DATE_TIME, ENTITY, FEATURE_LABEL, MODEL_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', 'US1-svc-wks-difs', 'RUNAS on many destination hosts', 6, 0.99949)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be suppressed
      upsertSql = "upsert into BEHAVIOR_ANOMALY (DATE_TIME, ENTITY, FEATURE_LABEL, MODEL_ID, RISK_SCORE) " +
        "values ('2016-05-26T00:00:00.000Z', 'SRV-jyria', 'Successfully logged on from many source IPs', 6, 0.99949)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val beaconSql = "CREATE TABLE IF NOT EXISTS BEACONS  (\n" +
        "    EVENT_TIME VARCHAR NOT NULL,\n" +
        "    SOURCE_NAME_OR_IP VARCHAR NOT NULL,\n" +
        "    DESTINATION_NAME_OR_IP VARCHAR NOT NULL,\n" +
        "    PERIOD_SECONDS INTEGER NOT NULL,\n" +
        "    SPARSE_HISTOGRAM_JSON VARCHAR,\n" +
        "    INTERVAL DOUBLE,\n" +
        "    ANOMALY_ALEXA DOUBLE,\n" +
        "    CONFIDENCE DOUBLE,\n" +
        "    SLD_ANOMALY DOUBLE,\n" +
        "    REQUEST_METHOD_ANOMALY DOUBLE,\n" +
        "    REQUEST_CLIENT_APPLICATION_ANOMALY DOUBLE,\n" +
        "    RISK DOUBLE,\n" +
        "    SYSLOG_JSON VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( EVENT_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, PERIOD_SECONDS )\n" +
        ") IMMUTABLE_ROWS=true, SALT_BUCKETS=10"
      val beaconStmt: PreparedStatement = conn.prepareStatement(beaconSql)
      beaconStmt.execute

      // Should be returned
      upsertSql = "upsert into BEACONS (EVENT_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, PERIOD_SECONDS, SPARSE_HISTOGRAM_JSON, INTERVAL, ANOMALY_ALEXA, CONFIDENCE, SLD_ANOMALY, " +
        "REQUEST_METHOD_ANOMALY, REQUEST_CLIENT_APPLICATION_ANOMALY, RISK, SYSLOG_JSON) " +
        "values ('2016-02-02T09:00:00.000Z', '192.168.1.35', 'wingateservices.com', 3600, '{\"120.0\": 3}', 120, 1, 1, 1, 1, 1, 1, '[\"\", \"\", \"\"]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should not be returned
      upsertSql = "upsert into BEACONS (EVENT_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, PERIOD_SECONDS, SPARSE_HISTOGRAM_JSON, INTERVAL, ANOMALY_ALEXA, CONFIDENCE, SLD_ANOMALY, " +
        "REQUEST_METHOD_ANOMALY, REQUEST_CLIENT_APPLICATION_ANOMALY, RISK, SYSLOG_JSON) " +
        "values ('2016-02-02T09:00:00.000Z', '192.1.12.255', 'someotherdomain.com', 3600, '{\"120.0\": 3}', 120, 1, 1, 1, 1, 1, 0.55, '[\"\", \"\", \"\"]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Should be returned
      upsertSql = "upsert into BEACONS (EVENT_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, PERIOD_SECONDS, SPARSE_HISTOGRAM_JSON, INTERVAL, ANOMALY_ALEXA, CONFIDENCE, SLD_ANOMALY, " +
        "REQUEST_METHOD_ANOMALY, REQUEST_CLIENT_APPLICATION_ANOMALY, RISK, SYSLOG_JSON) " +
        "values ('2016-02-06T14:00:00.000Z', '192.168.1.174', 'doubleclick.net', 3600, '{\"120.0\": 1, \"300.0\": 3, \"420.0\": 1}', 300.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.78, '[\"\", \"\", \"\", \"\", \"\", \"\"]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into BEACONS (EVENT_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, PERIOD_SECONDS, SPARSE_HISTOGRAM_JSON, INTERVAL, ANOMALY_ALEXA, CONFIDENCE, SLD_ANOMALY, " +
        "REQUEST_METHOD_ANOMALY, REQUEST_CLIENT_APPLICATION_ANOMALY, RISK, SYSLOG_JSON) " +
        "values ('2016-02-15T00:00:00.000Z', '192.168.1.99', 'xha-mster.com', 86400, '{\"7188.0\": 1, \"10.5\": 4, \"3598.0\": 20}', 3598.0, 1.0, 0.97, 1.0, 1.0, 1.0, 0.825, '[\"\", \"\", \"\"]')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val c2ModelSql = "CREATE TABLE IF NOT EXISTS C2_MODEL (\n" +
        "    DATE_TIME VARCHAR NOT NULL,\n" +
        "    SOURCE_NAME_OR_IP VARCHAR NOT NULL,\n" +
        "    DESTINATION_NAME_OR_IP VARCHAR NOT NULL,\n" +
        "    SLD VARCHAR,\n" +
        "    RISK_SCORE DOUBLE,\n" +
        "    C2_FACTORS VARCHAR,\n" +
        "    C2_FACTOR_VALUES VARCHAR,\n" +
        "    C2_TRAFFIC_HOURS VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP )\n" +
        ") IMMUTABLE_ROWS=true, SALT_BUCKETS=10"
      val c2ModelStmt: PreparedStatement = conn.prepareStatement(c2ModelSql)
      c2ModelStmt.execute

      upsertSql = "upsert into C2_MODEL (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS) " +
        "values ('2016-02-01T00:00:00.000Z', '192.168.1.150', '128.165.129.54', '128.165.129.54', 0.0571428571429, '(1, 0, 0, 0, 0, 1, 1, 1, 0)', '(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(15),(8)}')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into C2_MODEL (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS) " +
        "values ('2016-02-01T00:00:00.000Z', '192.168.1.150', '159.148.95.76', '159.148.95.76', 0.0714285714286, '(1, 0, 0, 0, 0, 1, 1, 1, 0)', '(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(11),(4),(9),(14)}')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into C2_MODEL (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS) " +
        "values ('2016-02-01T00:00:00.000Z', '192.168.1.151', 'address.yahoo.com', 'yahoo.com', 0.0571428571429, '(1, 0, 0, 0, 0, 1, 0, 0, 0)', '(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(0),(1),(10)}')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // This entry should be suppressed
      upsertSql = "upsert into C2_MODEL (DATE_TIME, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, SLD, RISK_SCORE, C2_FACTORS, C2_FACTOR_VALUES, C2_TRAFFIC_HOURS) " +
        "values ('2016-02-01T00:00:00.000Z', '192.1.12.255', 'address.yahoo.com', 'yahoo.com', 0.0571428571429, '(1, 0, 0, 0, 0, 1, 0, 0, 0)', '(\\'cond_every\\', \\'cond_caternary_ratio\\', \\'cond_newness_ratio\\', \\'cond_persistence_ratio\\', \\'cond_sld_source_count\\', \\'cond_sld_lacking_infrastructure\\', \\'cond_rank\\', \\'cond_sld_distinct_ua_count\\', \\'cond_riqr\\')', '{(0),(1),(10)}')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      //      val enityFeaturesSql = "CREATE TABLE IF NOT EXISTS ENTITY_FEATURES (\n" +
      //        "    DATE_TIME VARCHAR NOT NULL,\n" +
      //        "    MODEL_ID INTEGER NOT NULL,\n" +
      //        "    SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
      //        "    SOURCE_NAME_OR_IP VARCHAR,\n" +
      //        "    DESTINATION_NAME_OR_IP VARCHAR,\n" +
      //        "    FEATURE_LABEL VARCHAR,\n" +
      //        "    FEATURE_VALUE INTEGER,\n" +
      //        "    DATA_SOURCE VARCHAR,\n" +
      //        "    GRANULARITY VARCHAR,\n" +
      //        "    CONSTRAINT PK PRIMARY KEY ( DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP )\n" +
      //        ") IMMUTABLE_ROWS=true"
      //      val enityFeaturesStmt: PreparedStatement = conn.prepareStatement(enityFeaturesSql)
      //      enityFeaturesStmt.execute

      //      // Some model 4 data
      //      upsertSql = "upsert into ENTITY_FEATURES (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      //        "values ('2016-06-24T16:00:00.000Z', 4, 0, '10.11.21.52', '', 'high connections', 4, 'web_proxy_mef', 'hourly')"
      //      upsertStmt = conn.prepareStatement(upsertSql)
      //      upsertStmt.execute
      //      upsertSql = "upsert into ENTITY_FEATURES (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      //        "values ('2016-06-24T16:00:00.000Z', 4, 2, '10.13.141.38', '', 'high number of uncategorized connections', 2, 'web_proxy_mef', 'hourly')"
      //      upsertStmt = conn.prepareStatement(upsertSql)
      //      upsertStmt.execute
      //      upsertSql = "upsert into ENTITY_FEATURES (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      //        "values ('2016-06-24T16:00:00.000Z', 4, 6, '10.12.21.32', '', 'high uploads', 0, 'web_proxy_mef', 'hourly')"
      //      upsertStmt = conn.prepareStatement(upsertSql)
      //      upsertStmt.execute
      //      // Some model 6 data
      //      upsertSql = "upsert into ENTITY_FEATURES (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      //        "values ('2016-06-24T00:00:00.000Z', 6, 6, '', 'E8SEC-w2k8r2-src$', 'Successfully logged on from many source IPs', 0, 'iam_mef', 'hourly')"
      //      upsertStmt = conn.prepareStatement(upsertSql)
      //      upsertStmt.execute
      //      upsertSql = "upsert into ENTITY_FEATURES (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      //        "values ('2016-06-24T00:00:00.000Z', 6, 22, 'E8SEC-jyria', '', 'Many superuser logons', 2, 'iam_mef', 'hourly')"
      //      upsertStmt = conn.prepareStatement(upsertSql)
      //      upsertStmt.execute
      //      upsertSql = "upsert into ENTITY_FEATURES (DATE_TIME, MODEL_ID, SECURITY_EVENT_ID, SOURCE_NAME_OR_IP, DESTINATION_NAME_OR_IP, FEATURE_LABEL, FEATURE_VALUE, DATA_SOURCE, GRANULARITY) " +
      //        "values ('2016-06-24T00:00:00.000Z', 6, 22, 'NT AUTHORITY-system', '', 'Many superuser logons', 12, 'iam_mef', 'hourly')"
      //      upsertStmt = conn.prepareStatement(upsertSql)
      //      upsertStmt.execute

      val customBehaviorSql = "CREATE TABLE IF NOT EXISTS CUSTOM_BEHAVIOR (\n" +
        "    DATE_TIME VARCHAR NOT NULL,\n" +
        "    IP_ADDRESS VARCHAR NOT NULL,\n" +
        "    MAC_ADDRESS VARCHAR,\n" +
        "    HOST_NAME VARCHAR,\n" +
        "    USER_NAME VARCHAR,\n" +
        "    SCAN_DETECTION INTEGER,\n" +
        "    FLOOD_DETECTION INTEGER,\n" +
        "    URL_FILTERING_LOG INTEGER,\n" +
        "    SPYWARE_PHONE_HOME_DETECTION INTEGER,\n" +
        "    SPYWARE_DOWNLOAD_DETECTION INTEGER,\n" +
        "    VULNERABILITY_EXPLOIT_DETECTION INTEGER,\n" +
        "    FILETYPE_DETECTION INTEGER,\n" +
        "    DATA_FILTERING_DETECTION INTEGER,\n" +
        "    VIRUS_DETECTION INTEGER,\n" +
        "    WILDFIRE_SIGNATURE_FEED INTEGER,\n" +
        "    DNS_BOTNET_SIGNATURES INTEGER,\n" +
        "    CUSTOM_TYPE VARCHAR\n" +
        "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, IP_ADDRESS )\n" +
        ") IMMUTABLE_ROWS=true, SALT_BUCKETS=10"
      val customBehaviorSqlStmt: PreparedStatement = conn.prepareStatement(customBehaviorSql)
      customBehaviorSqlStmt.execute

      upsertSql = "upsert into CUSTOM_BEHAVIOR (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, SCAN_DETECTION, FLOOD_DETECTION, URL_FILTERING_LOG, SPYWARE_PHONE_HOME_DETECTION," +
        "SPYWARE_DOWNLOAD_DETECTION, VULNERABILITY_EXPLOIT_DETECTION, FILETYPE_DETECTION, DATA_FILTERING_DETECTION, VIRUS_DETECTION, WILDFIRE_SIGNATURE_FEED, DNS_BOTNET_SIGNATURES, CUSTOM_TYPE) " +
        "values ('2016-02-14T17:00:00.000Z', '192.168.1.66', '', '', '', 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 'pan')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into CUSTOM_BEHAVIOR (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, SCAN_DETECTION, FLOOD_DETECTION, URL_FILTERING_LOG, SPYWARE_PHONE_HOME_DETECTION," +
        "SPYWARE_DOWNLOAD_DETECTION, VULNERABILITY_EXPLOIT_DETECTION, FILETYPE_DETECTION, DATA_FILTERING_DETECTION, VIRUS_DETECTION, WILDFIRE_SIGNATURE_FEED, DNS_BOTNET_SIGNATURES, CUSTOM_TYPE) " +
        "values ('2016-02-15T17:00:00.000Z', '192.168.1.99', '', '', '', 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 'pan')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into CUSTOM_BEHAVIOR (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, SCAN_DETECTION, FLOOD_DETECTION, URL_FILTERING_LOG, SPYWARE_PHONE_HOME_DETECTION," +
        "SPYWARE_DOWNLOAD_DETECTION, VULNERABILITY_EXPLOIT_DETECTION, FILETYPE_DETECTION, DATA_FILTERING_DETECTION, VIRUS_DETECTION, WILDFIRE_SIGNATURE_FEED, DNS_BOTNET_SIGNATURES, CUSTOM_TYPE) " +
        "values ('2016-02-15T18:00:00.000Z', '192.168.1.99', '', '', '', 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 'pan')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into CUSTOM_BEHAVIOR (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, SCAN_DETECTION, FLOOD_DETECTION, URL_FILTERING_LOG, SPYWARE_PHONE_HOME_DETECTION," +
        "SPYWARE_DOWNLOAD_DETECTION, VULNERABILITY_EXPLOIT_DETECTION, FILETYPE_DETECTION, DATA_FILTERING_DETECTION, VIRUS_DETECTION, WILDFIRE_SIGNATURE_FEED, DNS_BOTNET_SIGNATURES, CUSTOM_TYPE) " +
        "values ('2016-02-16T18:00:00.000Z', '192.168.1.99', '', '', '', 0, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 'pan')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      // Add a row that should be suppressed
      upsertSql = "upsert into CUSTOM_BEHAVIOR (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, SCAN_DETECTION, FLOOD_DETECTION, URL_FILTERING_LOG, SPYWARE_PHONE_HOME_DETECTION," +
        "SPYWARE_DOWNLOAD_DETECTION, VULNERABILITY_EXPLOIT_DETECTION, FILETYPE_DETECTION, DATA_FILTERING_DETECTION, VIRUS_DETECTION, WILDFIRE_SIGNATURE_FEED, DNS_BOTNET_SIGNATURES, CUSTOM_TYPE) " +
        "values ('2016-02-16T18:00:00.000Z', '192.1.12.255', '', '', '', 0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 0, 'pan')"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

      val endUserPropsSql = "CREATE TABLE IF NOT EXISTS ENT_USER_PROPS (\n" +
        "    UUID VARCHAR NOT NULL,\n" +
        "    DATE_TIME VARCHAR NOT NULL,\n" +
        "    USER_NAME VARCHAR,\n" +
        "    CANONICAL_NAME VARCHAR,\n" +
        "    ACCOUNT_TYPE VARCHAR,\n" +
        "    SECURITY_ID VARCHAR,\n" +
        "    IS_CRITICAL VARCHAR,\n" +
        "    JOB_TITLE VARCHAR,\n" +
        "    EMAIL VARCHAR,\n" +
        "    LOCATION VARCHAR,\n" +
        "    DEPARTMENT VARCHAR,\n" +
        "    MANAGER VARCHAR,\n" +
        "    PRIMARY_HOST VARCHAR,\n" +
        "    CREATION_DATE VARCHAR,\n" +
        "    LAST_MODIFICATION_DATE VARCHAR,\n" +
        "    PASSWORD_LAST_SET_DATE VARCHAR,\n" +
        "    LAST_LOGON_DATE VARCHAR,\n" +
        "    RISK DOUBLE\n" +
        "CONSTRAINT PK PRIMARY KEY ( UUID, DATE_TIME )\n" +
        ") IMMUTABLE_ROWS=true, SALT_BUCKETS=10"
      val endUserPropsStmt: PreparedStatement = conn.prepareStatement(endUserPropsSql)
      endUserPropsStmt.execute

      upsertSql = "upsert into ENT_USER_PROPS (UUID, DATE_TIME, USER_NAME, CANONICAL_NAME, ACCOUNT_TYPE, SECURITY_ID, IS_CRITICAL, JOB_TITLE, EMAIL, LOCATION, DEPARTMENT, MANAGER, " +
        "PRIMARY_HOST, CREATION_DATE, LAST_MODIFICATION_DATE, PASSWORD_LAST_SET_DATE, LAST_LOGON_DATE, RISK) " +
        "values ('0-00000eee-40b9-11e6-817b-0cc47a5881ac-f31e-3ae3122ff7a9683255d63d74546a5983', '2016-05-26T00:00:00.000Z', 'TUSTX000DKVT0NM', 'example.com/ent/us1/TUSTX000DKVT0NMOU=_Computers', 'computer', 'S-1-5-5-21-1767303650-1696908375-35229093-254479059', '', '', '', '', '', '', '', '', '', '', '', 0.7)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute
      upsertSql = "upsert into ENT_USER_PROPS (UUID, DATE_TIME, USER_NAME, CANONICAL_NAME, ACCOUNT_TYPE, SECURITY_ID, IS_CRITICAL, JOB_TITLE, EMAIL, LOCATION, DEPARTMENT, MANAGER, " +
        "PRIMARY_HOST, CREATION_DATE, LAST_MODIFICATION_DATE, PASSWORD_LAST_SET_DATE, LAST_LOGON_DATE, RISK) " +
        "values ('0-0000a100-2602-11e6-9e19-0cc47a5880a6-f31e-d467b9e5874c21187dfeb65488fe01cf', '2016-05-26T00:00:00.000Z', 'joe', 'example.com/_Service Accounts/ENT/VDSI/v175993', 'user', '', '0', 'VP Product Management- Consumer Products', '', '', '', '', '', '', '', '', '', 0.7)"
      upsertStmt = conn.prepareStatement(upsertSql)
      upsertStmt.execute

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

class SuppressionTests {
  private val detectorHomeDao = new DetectorHomeDao(SuppressionTests.configuration)
  private val entityInvestigatorDao = new EntityInvestigatorDao(SuppressionTests.configuration)
  private val peerGroupDao = new PeerGroupDao(SuppressionTests.configuration)
  private val coordActivityDao = new CoordActivityDao(SuppressionTests.configuration)
  private val behaviorAnomalyDao = new BehaviorAnomalyDao(SuppressionTests.configuration)
  private val beaconsDao = new BeaconsDao(SuppressionTests.configuration)
  private val c2modelDao = new C2ModelDao(SuppressionTests.configuration)
  private val entityFeaturesDao = new EntityFeaturesDao(SuppressionTests.configuration)
  private val hostEntityPropertiesDao = new HostEntityPropertiesDao(SuppressionTests.configuration)

  @Test
  def getEntityScoresTest() {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-05-26T00:00:00.000Z")
    query.setEndTime("2016-05-27T00:00:00.000Z")
    query.setLimit(10)
    val scores26 = detectorHomeDao.getEntityScores(query, false, SuppressionTests.featureServiceCache);
    // We expect three of the four rows inserted above for 5/26 - none of them matches suppression criteria, but
    // two of them are for the same entity, and getEntityScores returns only the top entry for any entity.
    assertEquals(3, scores26.size)
    for (result <- scores26) {
      val ip = result.get("ipAddress").get
      val macAddress = result.get("macAddress").get
      val risk = result.get("currentScore").get
      if (ip == "10.10.4.57") {
        assertEquals("10:02:B5:D9:E3:60", macAddress)
        assertEquals(0.16843, risk)
      } else if (ip == "10.10.4.64") {
        assertEquals("A4:5E:60:DD:AF:F9", macAddress)
        assertEquals(0.07478, risk)
      } else if (ip == "10.10.4.74") {
        assertEquals("C4:8E:8F:F8:B5:21", macAddress)
        assertEquals(0.07479, risk)
      } else {
        assertTrue("Got unexpected ip address [" + ip + "]", false)
      }
    }

    val query27 = new QueryJson
    query27.setStartTime("2016-05-27T00:00:00.000Z")
    query27.setEndTime("2016-05-28T00:00:00.000Z")
    query27.setLimit(10)
    val scores27 = detectorHomeDao.getEntityScores(query27, false, SuppressionTests.featureServiceCache);
    // We expect two of the three rows - one should be suppressed
    assertEquals(2, scores27.size)
    for (result <- scores27) {
      val ip = result.get("ipAddress").get
      val macAddress = result.get("macAddress").get
      val risk = result.get("currentScore").get
      if (ip == "10.10.4.85") {
        assertEquals("C4:FE:8F:F7:B5:01", macAddress)
        assertEquals(0.55967, risk)
      } else if (ip == "192.168.12.18") {
        assertEquals("10:02:B5:D9:E3:60", macAddress)
        assertEquals(0.22413, risk)
      } else {
        assertTrue("Got unexpected ip address [" + ip + "]", false)
      }
    }

    val query28 = new QueryJson
    query28.setStartTime("2016-05-28T00:00:00.000Z")
    query28.setEndTime("2016-05-29T00:00:00.000Z")
    query28.setLimit(10)
    val scores28 = detectorHomeDao.getEntityScores(query28, false, SuppressionTests.featureServiceCache);
    // We expect two of the three rows - one should be suppressed
    assertEquals(2, scores28.size)
    for (result <- scores28) {
      val ip = result.get("ipAddress").get
      val macAddress = result.get("macAddress").get
      val risk = result.get("currentScore").get
      if (ip == "10.10.4.85") {
        assertEquals("C4:FE:8F:F7:B5:01", macAddress)
        assertEquals(0.21146, risk)
      } else if (ip == "192.168.12.18") {
        assertEquals("10:02:B5:D9:E3:60", macAddress)
        assertEquals(0.49432, risk)
      } else {
        assertTrue("Got unexpected ip address [" + ip + "]", false)
      }
    }
  }

  @Test
  def getSearchedEntityScoresTest() {
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-05-29T00:00:00.000Z")
    query.setEndTime("2016-05-30T00:00:00.000Z")
    // Search only for
    val map: util.Map[String, Object] = new util.HashMap[String, Object]()
    map.put("field","userName")
    map.put("operator", "equals")
    val values: util.List[String] = new util.ArrayList[String]()
    values.add("mark_ingram")
    map.put("values", values)
    query.getQuery.add(map)
    query.setLimit(10)
    val scores26 = detectorHomeDao.getSearchedEntityScores(query, false, SuppressionTests.featureServiceCache);
    // We expect three of the four rows inserted above for 5/26 - none of them matches suppression criteria, but
    // two of them are for the same entity, and getEntityScores returns only the top entry for any entity.
    assertEquals(3, scores26.size)
    for (result <- scores26) {
      val ip = result.get("ipAddress").get
      val macAddress = result.get("macAddress").get
      val risk = result.get("currentScore").get
      val userName = result.get("userName").get
      assertEquals("Got user [" + userName + "] ip [" + ip + "]", "mark_ingram", userName)
      if (ip == "192.168.12.18") {
        assertEquals("10:02:B5:D9:E3:60", macAddress)
        assertEquals(0.33059, risk)
      } else if (ip == "192.168.12.99") {
        assertEquals("10:02:B5:D9:E3:60", macAddress)
        assertEquals(0.012125, risk)
      } else if (ip == "192.168.19.18") {
        assertEquals("10:02:B5:D9:E3:60", macAddress)
        assertEquals(0.60443, risk)
      } else {
        assertTrue("Got unexpected ip address [" + ip + "]", false)
      }
    }
  }

  @Test
  def getEntityCardsTest() {
    // When we query for a suppressed entity, we should get nothing back
    val cards27 = detectorHomeDao.getEntityCards("2016-05-27T00:00:00.000Z", "2016-05-28T00:00:00.000Z", "10.10.4.101", "SRV-jyria",
      "jyria-MBP-2", "18:4F:32:F7:BE:2B", SuppressionTests.featureServiceCache)
    assertEquals(0, cards27.size)

    // This would be a great test, but it would require creating and adding some data to PEER_ENTITY_CARD_DETAILS, which I'm not
    // ready to do at the moment. Someday...
    // Check that when we query for a non-suppressed entity, we get something back
    //    val cards28 = detectorHomeDao.getEntityCards("2016-05-28T00:00:00.000Z", "2016-05-29T00:00:00.000Z", "10.10.4.85", " ",
    //      "WINDOWS-CR", "C4:FE:8F:F7:B5:01", SuppressionTests.featureServiceCache)
    //    assertEquals(1, cards28.size)
  }

  @Test
  def getRiskScoreBasedTimeSeriesForEntity(): Unit = {
    // Query for something that should be returned
    val query: QueryJson = new QueryJson
    query.setStartTime("2016-05-29T00:00:00.000Z")
    query.setEndTime("2016-05-30T00:00:00.000Z")
    val map: util.Map[String, Object] = new util.HashMap[String, Object]()
    map.put("field","sourceIp")
    map.put("operator", "equals")
    val values: util.List[String] = new util.ArrayList[String]()
    values.add("192.168.12.18")
    map.put("values", values)
    query.getQuery.add(map)
    query.setLimit(10)
    val results = detectorHomeDao.getRiskScoreBasedTimeSeriesForEntity(query, SuppressionTests.featureServiceCache)
    assertEquals(1, results.size)
    for (resultMap <- results) {
      assertNotNull(resultMap)
      val dateTime = resultMap.getOrElse("dateTime", "")
      val riskScore = resultMap.getOrElse("currentScore", -0.1).asInstanceOf[Double]
      assertEquals("2016-05-29", dateTime)
      assertEquals(0.3305, riskScore, 0.001)
      val modelScores: ListBuffer[EntityModelInfo] = resultMap.getOrElse("modelScores", null).asInstanceOf[ListBuffer[EntityModelInfo]]
      assertEquals(2, modelScores.size)
      for (entModelInfo <- modelScores) {
        assertEquals("2016-05-29T00:00:00.000Z", entModelInfo.getDateTime)
        if (entModelInfo.getSecurityEventTypeId == 12) {
          assertEquals(0.3305, entModelInfo.getRisk, 0.001)
        } else if (entModelInfo.getSecurityEventTypeId == 17) {
          assertEquals(0.08975, entModelInfo.getRisk, 0.001)
        } else {
          assertTrue("got unexpected security event type id [" + entModelInfo.getSecurityEventTypeId + "]", false)
        }
      }
    }

    // Query for something that should be suppressed
    val querySuppress: QueryJson = new QueryJson
    querySuppress.setStartTime("2016-05-29T00:00:00.000Z")
    querySuppress.setEndTime("2016-05-30T00:00:00.000Z")
    val mapSuppress: util.Map[String, Object] = new util.HashMap[String, Object]()
    mapSuppress.put("field","sourceIp")
    mapSuppress.put("operator", "equals")
    val valuesSuppress: util.List[String] = new util.ArrayList[String]()
    valuesSuppress.add("192.1.12.255")
    mapSuppress.put("values", valuesSuppress)
    querySuppress.getQuery.add(mapSuppress)
    querySuppress.setLimit(10)
    val resultsSuppress = detectorHomeDao.getRiskScoreBasedTimeSeriesForEntity(querySuppress, SuppressionTests.featureServiceCache)
    assertEquals(0, resultsSuppress.size)
  }

  @Test
  def getTopNSecurityEventsTest(): Unit = {
    val secEvents = detectorHomeDao.getTopNSecurityEvents("2016-05-29T00:00:00.000Z", "2016-05-30T00:00:00.000Z", 5, SuppressionTests.featureServiceCache)
    assertEquals(2, secEvents.size)
    for (eventMap <- secEvents) {
      val eventType = eventMap.getOrElse("eventType", "").asInstanceOf[String]
      val count = eventMap.getOrElse("count", -1).asInstanceOf[Int]
      if (eventType.equals("Downloads")) {
        assertEquals(4, count)
      } else if (eventType.equals("Denied Destinations")) {
        assertEquals(2, count)
      }
    }
  }

  @Test
  def getEntityPropertiesSingleViewTest() {
    // Check that when an entity is suppressed we get an empty map
    val result = entityInvestigatorDao.getEntityPropertiesSingleView("10.10.4.101", "jyria-MBP-2", "SRV-jyria", "2016-05-27T00:00:00.000Z", "2016-05-28T00:00:00.000Z");
    assertEquals(1, result.size)
    for (resultMap <- result) {
      // All fields should be empty
      assertEquals("", resultMap.get("ip").get)
      assertEquals("", resultMap.get("userName").get)
      assertEquals("", resultMap.get("risk").get)
      assertEquals("", resultMap.get("hostName").get)
    }

    // As with getEntityCardsTest, it would be nice to check that we get something back when an entity isn't suppressed.  But that
    // would require adding more tables with data...
  }

  @Test
  def getEntityCustomBehaviorTest(): Unit = {
    // First a test that should return results
    val customBehaviorResult = entityInvestigatorDao.getEntityCustomBehaviors("192.168.1.66", "", "", "2016-02-14T00:00:00.000Z", "2016-02-17T00:00:00.000Z", "[\"pan\"]")
    assertEquals(1, customBehaviorResult.size)
    for (resultMap <- customBehaviorResult) {
      resultMap.get("pan") match {
        case Some(i) => {
          val iter =  i.asInstanceOf[ListBuffer[scala.collection.mutable.HashMap[String, String]]].iterator
          while(iter.hasNext){
            val map = iter.next()
            val ip = map.getOrElse("ipAddress", "")
            val virusDet = map.getOrElse("virusDetection", -1)
            assertEquals("192.168.1.66", ip)
            assertEquals(0, virusDet)
          }
        }
        case None => assertFalse("That didn't work.", true)
      }
    }

    // Now query for something that should be suppressed
    val customBehaviorResultSuppressed = entityInvestigatorDao.getEntityCustomBehaviors("192.1.12.255", "", "", "2016-02-14T00:00:00.000Z", "2016-02-17T00:00:00.000Z", "[\"pal\"]")
    assertEquals(1, customBehaviorResultSuppressed.size)
  }

  @Test
  def getPeerGroupAnomaliesByModelIdTest() {
    val resultModel2 = peerGroupDao.getPeerGroupAnomaliesByModelId("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 2, 10, SuppressionTests.featureServiceCache)
    assertEquals(2, resultModel2.size)
    for (resultMap <- resultModel2) {
      assertTrue( resultMap.get("peerId").get.equals(101002) || resultMap.get("peerId").get.equals(101001))
    }

    val resultModel3 = peerGroupDao.getPeerGroupAnomaliesByModelId("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 3, 10, SuppressionTests.featureServiceCache)
    assertEquals(1, resultModel3.size)
    for (resultMap <- resultModel3) {
      assertEquals(102001, resultMap("peerId"))
    }
  }

  @Test
  def getPeerGroupAnomaliesTest(): Unit = {
    val peerGroupAnomalies = peerGroupDao.getPeerGroupAnomalies("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 2, SuppressionTests.featureServiceCache)
    assertNotNull(peerGroupAnomalies)
    assertEquals(3, peerGroupAnomalies.size)
    for (peerGroupMap <- peerGroupAnomalies) {
      val peerId = peerGroupMap.getOrElse("peerId", -1).asInstanceOf[Int]
      assertTrue(peerId == 101001 || peerId == 102001 || peerId == 101002)
    }

    // Should get the same two results with a higher topN param
    val peerGroupAnomaliesHigher = peerGroupDao.getPeerGroupAnomalies("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 5, SuppressionTests.featureServiceCache)
    assertNotNull(peerGroupAnomaliesHigher)
    assertEquals(3, peerGroupAnomaliesHigher.size)
    for (peerGroupMap <- peerGroupAnomaliesHigher) {
      val peerId = peerGroupMap.getOrElse("peerId", -1).asInstanceOf[Int]
      assertTrue(peerId == 101001 || peerId == 102001 || peerId == 101002)
    }
  }

  @Test
  def getPeerGroupAnomaliesFromIdTest(): Unit = {
    val peerGroupAnomalies = peerGroupDao.getPeerGroupAnomaliesFromId("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 2, 101001, 5, SuppressionTests.featureServiceCache)
    assertEquals(1, peerGroupAnomalies.size)
    for (peerGroupMap <- peerGroupAnomalies) {
      val peerId = peerGroupMap.getOrElse("peerId", -1).asInstanceOf[Int]
      assertTrue(peerId == 101001)
    }

    val peerGroupAnomaliesSuppressed = peerGroupDao.getPeerGroupAnomaliesFromId("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 2, 101002, 5, SuppressionTests.featureServiceCache)
    assertEquals(1, peerGroupAnomaliesSuppressed.size)
  }

  @Test
  def getEventsInTimeTest()  {
    val results = coordActivityDao.getEventsInTime("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 10)
    assertEquals(2, results.size)
    for (resultMap <- results) {
      val sourceNameOrIp = resultMap.get("sourceNameOrIp").get
      val destNameOrIp = resultMap.get("destinationNameOrIp").get
      assertTrue(sourceNameOrIp.equals("192.168.1.176") || sourceNameOrIp.equals("192.168.1.41"))
      assertEquals("glidewelldental.com", destNameOrIp)
    }
  }

  @Test
  def getCoordActivityEntitiesParallelTest() {
    val results = coordActivityDao.getCoordActivityEntitiesParallel("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 0)
    assertEquals(2, results.size)
    for (resultMap <- results) {
      val ip = resultMap.get("ipAddress").get
      assertTrue(ip.equals("192.168.1.176") || ip.equals("192.168.1.41"))
    }
  }

  @Test
  def getAnomaliesByModelIdTest(): Unit = {
    val resultsModel4 = behaviorAnomalyDao.getAnomaliesByModelId("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 4, SuppressionTests.featureServiceCache)
    assertEquals(1, resultsModel4.size)
    for (resultMap <- resultsModel4) {
      val details = resultMap("behaviorDetails")
      details match {
        case lb: ListBuffer[BehaviorDetails] => {
          assertEquals(2, lb.size)
          for (behaviorDetails: BehaviorDetails <- lb) {
            assertTrue("192.168.1.150" == behaviorDetails.entity || "192.168.12.18" ==  behaviorDetails.entity)
            assertEquals(0.10673, behaviorDetails.riskScore, 0.0001)
          }
        }
        case _ => assertTrue(false)
      }
      //      val details: ListBuffer[BehaviorDetails] = resultMap.get("behaviorDetails").asInstanceOf[ListBuffer[BehaviorDetails]]
    }

    val resultsModel6 = behaviorAnomalyDao.getAnomaliesByModelId("2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z", 6, SuppressionTests.featureServiceCache)
    assertEquals(1, resultsModel6.size)
    for (resultMap <- resultsModel6) {
      val details = resultMap.get("behaviorDetails").get
      details match {
        case lb: ListBuffer[BehaviorDetails] => {
          assertEquals(1, lb.size)
          for (behaviorDetails: BehaviorDetails <- lb) {
            // Note! The entity we compare to is not exactly what's in the entity column in the test data.  This is because
            // getAnomaliesByModelId() trims everything up to the first -.
            assertEquals("svc-wks-difs", behaviorDetails.entity)
            assertEquals(0.99949, behaviorDetails.riskScore, 0.0001)
          }
        }
        case _ => assertTrue(false)
      }
    }
  }

  @Test
  def getBeaconsTest(): Unit = {
    val beaconResults = beaconsDao.getBeacons("2016-02-02T09:00:00.000Z", 3600)
    assertEquals(1, beaconResults.size)
    for (result <- beaconResults) {
      val eventTime = result.getOrElse("eventTime", null)
      val sourceNameOrIp = result.getOrElse("sourceNameOrIp", null)
      val destinationNameOrIp = result.getOrElse("destinationNameOrIp", null)
      val risk = result.getOrElse("risk", null).asInstanceOf[Double]
      assertEquals("2016-02-02T09:00:00.000Z", eventTime)
      assertEquals("192.168.1.35", sourceNameOrIp)
      assertEquals("wingateservices.com", destinationNameOrIp)
      assertEquals(1.0, risk, 0.01)
    }
  }

  @Test
  def BeaconsDayTest: Unit = {
    val beaconDayResults = beaconsDao.BeaconsDay("2016-02-02T00:00:00.000Z", "2016-02-07T00:00:00.000Z")

    assertNotNull(beaconDayResults)
    assertEquals(2, beaconDayResults.size)
    for (result <- beaconDayResults) {
      val sourceNameOrIp = result.getOrElse("sourceNameOrIp", null)
      val destinationNameOrIp = result.getOrElse("destinationNameOrIp", null)
      val risk = result.getOrElse("risk", null).asInstanceOf[Double]
      assertTrue("192.168.1.35".equals(sourceNameOrIp) || "192.168.1.174".equals(sourceNameOrIp))
      assertTrue("wingateservices.com".equals(destinationNameOrIp) || "doubleclick.net".equals(destinationNameOrIp))
      assertTrue(risk - 1.0 < 0.01 || risk - 0.78 < 0.01)
    }
  }

  @Test
  def getBeaconingSeriesTest: Unit = {
    val beaconSeriesResults = beaconsDao.getBeaconingSeries("2016-02-02T00:00:00.000Z", "2016-02-07T00:00:00.000Z", 3600)
    assertEquals(2, beaconSeriesResults.size)
    for (result <- beaconSeriesResults) {
      val sourceNameOrIp = result.getOrElse("sourceNameOrIp", null)
      val destinationNameOrIp = result.getOrElse("destinationNameOrIp", null)
      val risk = result.getOrElse("risk", null).asInstanceOf[Double]
      assertTrue("192.168.1.35".equals(sourceNameOrIp) || "192.168.1.174".equals(sourceNameOrIp))
      assertTrue("wingateservices.com".equals(destinationNameOrIp) || "doubleclick.net".equals(destinationNameOrIp))
      assertTrue(risk - 1.0 < 0.01 || risk - 0.78 < 0.01)
    }
  }

  @Test
  def getC2AnomaliesTest: Unit = {
    val c2AnomaliesResult = c2modelDao.getC2Anomalies("2016-02-01T00:00:00.000Z", "2016-02-07T00:00:00.000Z", 20, SuppressionTests.featureServiceCache)
    assertEquals(3, c2AnomaliesResult.size)
    for (result <- c2AnomaliesResult) {
      val sourceNameOrIp = result.getOrElse("sourceNameOrIp", null)
      val destinationNameOrIp = result.getOrElse("destinationNameOrIp", null)
      val risk = result.getOrElse("riskScore", null).asInstanceOf[Double]
      assertTrue("192.168.1.150".equals(sourceNameOrIp) || "192.168.1.151".equals(sourceNameOrIp))
      assertTrue("128.165.129.54".equals(destinationNameOrIp) || "address.yahoo.com".equals(destinationNameOrIp) || "159.148.95.76".equals(destinationNameOrIp) )
      assertTrue(risk - 0.057 < 0.01 || risk - 0.071 < 0.01)
    }
  }

  @Test
  def getEntityFeaturesByFeatureLabelTest(): Unit = {
    // getEntityFeaturesByFeatureLabel() returns aggregate results - I don't think we want to try to create a where clause that will eliminate suppressed items from
    // the aggregation, so I'm going to just stub this out for now.  May want to revisit it again later.
    //    val entityFeaturesModel4 = entityFeaturesDao.getEntityFeaturesByFeatureLabel("2016-06-24T00:00:00.000Z", "2016-06-25T00:00:00.000Z", "", "", 4, "", SuppressionTests.featureServiceCache)
    //    assertEquals(3, entityFeaturesModel4.size)

    //    val entityFeaturesModel6 = entityFeaturesDao.getEntityFeaturesByFeatureLabel("2016-06-24T00:00:00.000Z", "2016-06-25T00:00:00.000Z", "", "", 6, "Many superuser logons", SuppressionTests.featureServiceCache)
    //    assertEquals(1, entityFeaturesModel6.size)
  }

  @Test
  def getEntityPropertiesTest(): Unit = {
    // Check that we get stuff back when not suppressed
    val entityProps = entityInvestigatorDao.getEntityProperties("192.168.1.41", "laptop861", "joe", "2016-05-26T00:00:00.000Z", "2016-05-27T00:00:00.000Z")
    assertEquals(1, entityProps.size)
    for (entityMap <- entityProps) {
      val ipProperties = entityMap.getOrElse("ipProperties", null)
      assertEquals(1, ipProperties.size)
      for (ipMap <- ipProperties) {
        val ip = ipMap.getOrElse("ip", "")
        val macAddr = ipMap.getOrElse("macAddress", "")
        assertEquals("192.168.1.41", ip)
        assertEquals("A4:5E:60:C8:2E:EE", macAddr)
      }
      val hostProperties = entityMap.getOrElse("hostProperties", null)
      assertEquals(1, hostProperties.size)
      for (hostMap <- hostProperties) {
        val ip = hostMap.getOrElse("ip", "")
        val macAddr = hostMap.getOrElse("macAddress", "")
        val hostName = hostMap.getOrElse("hostName", "")
        assertEquals("192.168.1.41", ip)
        assertEquals("A4:5E:60:C8:2E:EE", macAddr)
        assertEquals("laptop861", hostName)

      }
      val userProperties = entityMap.getOrElse("userProperties", null)
      assertEquals(1, userProperties.size)
      for (userMap <- userProperties) {
        val uuid = userMap.getOrElse("uuid", "")
        val userName = userMap.getOrElse("userName", "")
        assertEquals("0-0000a100-2602-11e6-9e19-0cc47a5880a6-f31e-d467b9e5874c21187dfeb65488fe01cf", uuid)
        assertEquals("joe", userName)
      }
    }

    // Check for suppressed entry
    val entityPropsSuppresed = entityInvestigatorDao.getEntityProperties("10.10.4.101", "jyria-MBP-2", "SRV-jyria", "2016-05-27T00:00:00.000Z", "2016-05-28T00:00:00.000Z")
    assertEquals(1, entityPropsSuppresed.size)
    for (entityMap <- entityPropsSuppresed) {
      assertEquals(0, entityMap.size)
    }
  }

  @Test
  def getHostIdTest(): Unit = {
    // Get unsuppressed result
    val resultUnsuppressed = hostEntityPropertiesDao.getHostId("00028e12-1925-470d-9a4e-6093d0d8814f")
    assertNotNull(resultUnsuppressed)
    for (resultMap <- resultUnsuppressed) {
      val uuid = resultMap.getOrElse("uuid", "")
      val ips = resultMap.getOrElse("ipAddresses", "")
      assertEquals("00028e12-1925-470d-9a4e-6093d0d8814f", uuid)
      assertEquals("192.168.1.176", ips)
    }

    // 10028e12-4925-c70d-9a4e-e093d0d9314a
    val resultSuppressed = hostEntityPropertiesDao.getHostId("10028e12-4925-c70d-9a4e-e093d0d9314a")
    assertNotNull(resultSuppressed)
    assertEquals(0, resultSuppressed.size)
  }
}
