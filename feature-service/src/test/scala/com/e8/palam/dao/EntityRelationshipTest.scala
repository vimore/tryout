package com.e8.palam.dao

import java.io.File
import java.sql.{PreparedStatement, Connection}
import javax.validation.{Validation, Validator}

import com.e8.sparkle.commons.hbase.HBaseClient
import com.e8.sparkle.storage.hbase.rowobjects.{ColumnProperties, BaseRow, EntityRelationship, Entity}
import com.e8.sparkle.storage.hbase.tableobjects.{EntityRelationshipTable, EntityTable}
import com.e8.test.{HBaseTestBase, MongoTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{EntityFusionConfiguration, FeatureServiceConfiguration}
import com.securityx.modelfeature.dao.{EntityInvestigatorDao, PhoenixUtils}
import com.securityx.modelfeature.utils.{EntityThreat, HBaseAccessConfiguration}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

object EntityRelationshipTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
  private val mapper2: ObjectMapper = new ObjectMapper()
  private val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
  private val hBaseClient: HBaseClient = new HBaseClient

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

    //val hBaseConfig = new HBaseConfiguration(configuration.getZkQuorum, configuration.getZkQuorum, HBaseTestBase.utility.getZkCluster.getClientPort.toString)
    // Create HBase access objects
    val hBaseConfig = new HBaseAccessConfiguration("127.0.0.1", "127.0.0.1", HBaseTestBase.utility.getZkCluster.getClientPort.toString, configuration.getPhoenix.getSchema)
    val hBaseConn = hBaseClient.getConnection(hBaseConfig)
    val hBaseAdmin = hBaseConn.getAdmin

    // Create entity tables via HBase access
    val entityTable = new EntityTable
    val entityRelationshipTable = new EntityRelationshipTable
    entityTable.createTableIfNotExists(hBaseConn, hBaseConfig)
    entityRelationshipTable.createTableIfNotExists(hBaseConn, hBaseConfig)

    // Create some host and user entities, and also entity relationships for them, to test getting entity relationships.
    val user1 = createUserEntity("2016-07-01T00:00:00.000Z", "192.168.15.100", "AA:7F:C8:56:84:17", "host1-MBP", "user1", "e-user1")
    val user2 = createUserEntity("2016-07-01T00:00:00.000Z", "192.168.15.101", "BB:7F:C8:56:84:17", "user2-MBP", "user2", "e-user2")
    val user3 = createUserEntity("2016-07-01T00:00:00.000Z", "192.168.15.2", "CC:AA:C8:56:84:17", "user3-MBP", "user3", "e-user3")
    val host1 = createHostEntity("2016-07-01T00:00:00.000Z", "192.168.15.100", "AA:7F:C8:56:84:17", "host1-MBP", null, "e-host1")
    val host2 = createHostEntity("2016-07-01T00:00:00.000Z", "192.168.15.101", "BB:7F:C8:56:84:17", "user2-MBP", null, "e-host2")
    val host3 = createHostEntity("2016-07-01T00:00:00.000Z", "192.168.15.102", "CC:7F:C8:56:84:17", "sharedHost", null, "e-host3")
    val host4 = createHostEntity("2016-07-01T00:00:00.000Z", "192.168.15.2", "CC:AA:C8:56:84:17", "user3-MBP", null, "e-host4")
    val user1Nov = createUserEntity("2016-11-01T00:00:00.000Z", "192.168.15.100", "AA:7F:C8:56:84:17", "host1-MBP", "user1", "e-user1")
    val host1Nov = createHostEntity("2016-11-01T00:00:00.000Z", "192.168.15.100", "AA:7F:C8:56:84:17", "host1-MBP", null, "e-host1")
    val entities: Array[BaseRow] = Array(user1, user2, user3, host1, host2, host3, host4, user1Nov, host1Nov)
    entityTable.putRows(hBaseConn, entities, false, hBaseConfig)

    val er1 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-user1", "e-host1", Entity.USER_ENTITY)
    val er2 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-host1", "e-user1", Entity.HOST_ENTITY)
    val er3 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-user2", "e-host2", Entity.USER_ENTITY)
    val er4 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-host2", "e-user2", Entity.HOST_ENTITY)
    val er5 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-host3", "e-user1", Entity.HOST_ENTITY)
    val er6 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-host3", "e-user2", Entity.HOST_ENTITY)
    val er7 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-user1", "e-host3", Entity.USER_ENTITY)
    val er8 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-user2", "e-host3", Entity.USER_ENTITY)
    val er9 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-user3", "e-host4", Entity.USER_ENTITY)
    val er10 = createEntityRelationship("2016-07-01T00:00:00.000Z", "e-user3", "e-host3", Entity.USER_ENTITY)
    val er1Nov1 = createEntityRelationship("2016-11-01T00:00:00.000Z", "e-user1", "e-host1", Entity.USER_ENTITY)
    val er2Nov1 = createEntityRelationship("2016-11-01T00:00:00.000Z", "e-host1", "e-user1", Entity.HOST_ENTITY)
    val er1Nov2 = createEntityRelationship("2016-11-02T00:00:00.000Z", "e-user1", "e-host1", Entity.USER_ENTITY)
    val er2Nov2 = createEntityRelationship("2016-11-02T00:00:00.000Z", "e-host1", "e-user1", Entity.HOST_ENTITY)
    val er1Nov3 = createEntityRelationship("2016-11-03T00:00:00.000Z", "e-user1", "e-host1", Entity.USER_ENTITY)
    val er2Nov3 = createEntityRelationship("2016-11-03T00:00:00.000Z", "e-host1", "e-user1", Entity.HOST_ENTITY)
    val er1Nov4 = createEntityRelationship("2016-11-04T00:00:00.000Z", "e-user1", "e-host1", Entity.USER_ENTITY)
    val er2Nov4 = createEntityRelationship("2016-11-04T00:00:00.000Z", "e-host1", "e-user1", Entity.HOST_ENTITY)
    val entityRelationships: Array[BaseRow] = Array(er1, er2, er3, er4, er5, er6, er7, er8, er9, er10, er1Nov1, er2Nov1, er1Nov2, er2Nov2,
      er1Nov3, er2Nov3, er1Nov4, er2Nov4)
    entityRelationshipTable.putRows(hBaseConn, entityRelationships, false, hBaseConfig)

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
      "values ('2016-07-01T00:00:00.000Z', '192.168.1.150', '7C-12-F1-53-5D-76', 'laptop850', 'john_doe', 3, 8, 0.87337, null, 'e-user1')"
    var upsertStmt: PreparedStatement = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.1.151', '63-A6-D2-AB-F2-B2', 'laptop851', 'kenbrell_thompkins', 3, 14, 0.7135, null, 'e-user2')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.1.174', 'E4-EB-C9-03-9B-41', 'laptop874', 'john_doe', 7, 502, 0.98, null, 'e-user1')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.12.27', '12:7F:C8:56:84:17', 'RDP-GW', 'NULL_VALUE', 9, 0, 0.7773, 'e-host1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.12.27', '12:7F:C8:56:84:17', 'RDP-GW', 'NULL_VALUE', 10, 0, 1.0, 'e-host1', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.19.73', 'AA:7F:C9:BB:85:37', 'WIN-OSNMCI3GJJ1', 'NULL_VALUE', 10, 0, 1.0, 'e-host3', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.19.86', 'D4:8F:A7:CB:85:43', 'WIN-OSNMCI3FXX7', 'NULL_VALUE', 10, 0, 1.0, 'e-host2', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.2', 'NULL_VALUE', 'NULL_VALUE', 'NULL_VALUE', 8, 0, 0.24286, 'e-host4', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.2', 'CC:AA:C8:56:84:17', 'user3-MBP', 'NULL_VALUE', 2, 6, 0.46474236144777503, 'e-host4', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.2', 'CC:AA:C8:56:84:17', 'user3-MBP', 'NULL_VALUE', 2, 18, 0.86017, 'e-host4', null)"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-07-01T00:00:00.000Z', '192.168.15.2', 'NULL_VALUE', 'NULL_VALUE', 'user3', 7, 501, 0.4, null, 'e-user3')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute

    // The next three rows are used to check that we do the right thing when both host and user entity ids are set.  If we
    // are looking for entities related to a host, we should only get user related threats - so only the first one if we're
    // looking for entities related to e-host1
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-01T00:00:00.000Z', '192.168.1.150', '7C-12-F1-53-5D-76', 'laptop850', 'john_doe', 3, 8, 0.87337, 'e-host1', 'e-user1')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-01T00:00:00.000Z', '192.168.15.2', 'NULL_VALUE', 'NULL_VALUE', 'NULL_VALUE', 8, 0, 0.24286, 'e-host1', 'e-user1')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
    upsertSql = "upsert into " + EntityThreat.getName(configuration) + " (DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID, RISK_SCORE, HOST_ENTITY_ID, USER_ENTITY_ID) " +
      "values ('2016-11-01T00:00:00.000Z', '192.168.1.151', '63-A6-D2-AB-F2-B2', 'laptop851', 'kenbrell_thompkins', 3, 14, 0.7135, 'e-host2', 'e-user2')"
    upsertStmt = conn.prepareStatement(upsertSql)
    upsertStmt.execute
  }

  def createUserEntity(fusionTime: String, ip: String, mac: String, host: String, user: String, entityId: String): Entity = {
    val fusionDate = EntityFusionConfiguration.convertDate(fusionTime)
    val e: Entity = new Entity(entityId, fusionDate.getTime,
      new ColumnProperties(user, fusionTime, "source"),
      new ColumnProperties(ip, fusionTime, "source"),
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
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

class EntityRelationshipTest {
  private val entityInvestigatorDao = new EntityInvestigatorDao(EntityRelationshipTest.configuration)

  @Test
  def getRelatedEntitiesUserTest() = {
    // query getRelatedEntities with an id that identifies a user
    val relatedEntities = entityInvestigatorDao.getRelatedEntities("2016-06-30:00:00.000Z", "2016-07-02T00:00:00.000Z", "e-user1", EntityRelationshipTest.featureServiceCache)
    assertEquals(2, relatedEntities.size)
    for (entity <- relatedEntities) {
      val entityId = entity.getOrElse("entityId", null)
      val ipAddress = entity.getOrElse("ipAddress", null)
      val macAddress = entity.getOrElse("macAddress", null)
      val hostName = entity.getOrElse("hostName", null)
      if (entityId == "e-host1") {
        assertEquals("192.168.12.27", ipAddress)
        assertEquals("12:7F:C8:56:84:17", macAddress)
        assertEquals("RDP-GW", hostName)
      } else if (entityId == "e-host3") {
        assertEquals("192.168.19.73", ipAddress)
        assertEquals("AA:7F:C9:BB:85:37", macAddress)
        assertEquals("WIN-OSNMCI3GJJ1", hostName)
      } else {
        assertTrue("got unexpected entity [" + entityId + "]", false)
      }
    }
  }

  @Test
  def getRelatedEntitiesHostTest() = {
    // query getRelatedEntities with an id that identifies a host
    val relatedEntities = entityInvestigatorDao.getRelatedEntities("2016-06-30:00:00.000Z", "2016-07-02T00:00:00.000Z", "e-host2", EntityRelationshipTest.featureServiceCache)
    assertEquals(1, relatedEntities.size)
    val entity = relatedEntities.head
    val entityId = entity.getOrElse("entityId", null)
    val ipAddress = entity.getOrElse("ipAddress", null)
    val macAddress = entity.getOrElse("macAddress", null)
    val hostName = entity.getOrElse("hostName", null)
    assertEquals("e-user2", entityId)
    assertEquals("192.168.1.151", ipAddress)
    assertEquals("63-A6-D2-AB-F2-B2", macAddress)
    assertEquals("laptop851", hostName)
  }

  @Test
  def getRelatedEntityListTest() = {
    val relatedEntityMap_h1h2 = entityInvestigatorDao.getRelatedEntityList("2016-06-30:00:00.000Z", "2016-07-02T00:00:00.000Z", "e-host1, e-host2", EntityRelationshipTest.featureServiceCache)
    assertEquals(2, relatedEntityMap_h1h2.size)
    for ((entityId, relatedEntities) <- relatedEntityMap_h1h2) {
      if (entityId == "e-host1") {
        assertEquals(1, relatedEntities.size)
        val entityThreatInfo = relatedEntities.head
        assertEquals("e-user1", entityThreatInfo.getOrElse("entityId", null))
        assertEquals("john_doe", entityThreatInfo.getOrElse("userName", null))
        assertEquals(7, entityThreatInfo.getOrElse("ModelId", null))
        assertEquals(502, entityThreatInfo.getOrElse("SecurityEventId", null))
        assertEquals("user", entityThreatInfo.getOrElse("entityType", null))
      } else if (entityId == "e-host2") {
        assertEquals(1, relatedEntities.size)
        val entityThreatInfo = relatedEntities.head
        assertEquals("e-user2", entityThreatInfo.getOrElse("entityId", null))
        assertEquals("kenbrell_thompkins", entityThreatInfo.getOrElse("userName", null))
        assertEquals(3, entityThreatInfo.getOrElse("ModelId", null))
        assertEquals(14, entityThreatInfo.getOrElse("SecurityEventId", null))
        assertEquals("user", entityThreatInfo.getOrElse("entityType", null))
      } else {
        assertTrue("unexpected entity id [" + entityId + "]", false)
      }
    }

    val relatedEntitiesMap_u1u3 = entityInvestigatorDao.getRelatedEntityList("2016-06-30:00:00.000Z", "2016-07-02T00:00:00.000Z", "e-user1, e-user3", EntityRelationshipTest.featureServiceCache)
    assertEquals(2, relatedEntitiesMap_u1u3.size)
    for ((entityId, relatedEntities) <- relatedEntitiesMap_u1u3) {
      if (entityId == "e-user1") {
        assertEquals(2, relatedEntities.size)
        for (entityInfo <- relatedEntities) {
          val relatedEntityId = entityInfo.getOrElse("entityId", null)
          assertEquals("host", entityInfo.getOrElse("entityType", null))
          assertEquals(10, entityInfo.getOrElse("ModelId", null))
          assertEquals(0, entityInfo.getOrElse("SecurityEventId", null))
          if (relatedEntityId == "e-host1") {
            assertEquals("12:7F:C8:56:84:17", entityInfo.getOrElse("macAddress", null))
            assertEquals("192.168.12.27", entityInfo.getOrElse("ipAddress", null))
          } else if (relatedEntityId == "e-host3") {
            assertEquals("AA:7F:C9:BB:85:37", entityInfo.getOrElse("macAddress", null))
            assertEquals("192.168.19.73", entityInfo.getOrElse("ipAddress", null))
          } else {
            assertTrue("unexpected entity id [" + relatedEntityId + "]", false)
          }
        }
      } else if (entityId == "e-user3") {
        assertEquals(2, relatedEntities.size)
        for (entityInfo <- relatedEntities) {
          val relatedEntityId = entityInfo.getOrElse("entityId", null)
          assertEquals("host", entityInfo.getOrElse("entityType", null))
          if (relatedEntityId == "e-host4") {
            assertEquals("", entityInfo.getOrElse("macAddress", null))
            assertEquals("192.168.15.2", entityInfo.getOrElse("ipAddress", null))
            assertEquals(8, entityInfo.getOrElse("ModelId", null))
            assertEquals(0, entityInfo.getOrElse("SecurityEventId", null))
          } else if (relatedEntityId == "e-host3") {
            assertEquals("AA:7F:C9:BB:85:37", entityInfo.getOrElse("macAddress", null))
            assertEquals("192.168.19.73", entityInfo.getOrElse("ipAddress", null))
            assertEquals(10, entityInfo.getOrElse("ModelId", null))
            assertEquals(0, entityInfo.getOrElse("SecurityEventId", null))
          } else {
            assertTrue("unexpected entity id [" + relatedEntityId + "]", false)
          }
        }
      } else {
        assertTrue("unexpected entity id [" + entityId + "]", false)
      }
    }
  }

  // This test is for what we get when we query and HBase has multiple entries for the same relationship
  @Test
  def getRelatedEntitiesMultipleVersions() = {
    val relatedEntityMap = entityInvestigatorDao.getRelatedEntityList("2016-10-30T00:00:00.000Z", "2016-11-05T00:00:00.000Z", "e-host1, e-host2", EntityRelationshipTest.featureServiceCache)
    assertEquals(2, relatedEntityMap.size)
    for ((entityName, entityList) <- relatedEntityMap) {
      if (entityName == "e-host1") {
        assertEquals(1, entityList.size)
        val entity = entityList.head
        assertEquals("e-user1", entity.getOrElse("entityId", null))
        assertEquals("user", entity.getOrElse("entityType", null))
        assertEquals(3, entity.getOrElse("ModelId", null))
        assertEquals("192.168.1.150", entity.getOrElse("ipAddress", null))
      } else if (entityName == "e-host2") {
        assertEquals(0, entityList.size)
      } else {
        assertTrue("got unexpected entity [" + entityName + "]", false)
      }
    }

  }
}
