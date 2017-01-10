package com.e8.palam.dao


import java.io.File
import java.sql.{PreparedStatement, Connection}
import java.util
import java.util.Date
import javax.validation.{Validation, Validator}

import com.e8.sparkle.commons.hbase.HBaseClient
import com.e8.sparkle.storage.hbase.query.QueryJson
import com.e8.sparkle.storage.hbase.rowobjects._
import com.e8.sparkle.storage.hbase.tableobjects.{EntityRelationshipTable, EntityTable}
import com.e8.test.{MongoTestBase, HBaseTestBase}
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{EntityFusionConfiguration, FeatureServiceConfiguration}
import com.securityx.modelfeature.dao.EntityInvestigatorDao
import com.securityx.modelfeature.utils.HBaseAccessConfiguration
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Assert._
import org.junit._

import scala.collection.mutable.ListBuffer

object EntityPropertiesTest extends MongoTestBase {
  private var configuration: FeatureServiceConfiguration = null
  private var featureServiceCache: FeatureServiceCache = null
  private val mapper: ObjectMapper = Jackson.newObjectMapper
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

    val confFile: String = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"
    configuration = new ConfigurationFactory[FeatureServiceConfiguration](classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))
    configuration.setZkQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort)
    configuration.setSolrQuorum("127.0.0.1:" + HBaseTestBase.utility.getZkCluster.getClientPort + "/solr")
    configuration.getEntityFusionConfiguration.setConversionDate("2016-08-01T00:00:00.000Z")
    // Set up mongo server connection
    MongoTestBase.setupMongo(configuration, true)
    featureServiceCache = new FeatureServiceCache(configuration)

    val hBaseConfig = new HBaseAccessConfiguration("127.0.0.1", "127.0.0.1", HBaseTestBase.utility.getZkCluster.getClientPort.toString, configuration.getPhoenix.getSchema)
    val hBaseConn = hBaseClient.getConnection(hBaseConfig)
    val hBaseAdmin = hBaseConn.getAdmin
    val entityTable = new EntityTable
    entityTable.createTableIfNotExists(hBaseConn, hBaseConfig)

    val entity1 = createUserEntity("2016-10-01T14:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e1")
    val entity2 = createUserEntity("2016-10-02T12:00:00.000Z", "10.10.4.72", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e1")
    val entity3 = createUserEntity("2016-10-03T15:00:00.000Z", "10.10.4.99", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e1")
    val entity4 = createUserEntity("2016-10-04T08:00:00.000Z", "10.10.4.69", "B8:E8:56:03:C9:7E", "Steves-Air", null, "e1")
    val host1 = createHostEntity("2016-10-15T16:00:00.000Z", "192.168.1.13", "54-0C-75-00-E2-12", "laptop801", null, "c3c46ec3ab47d5ee70e3f60283015a24")
    val host2 = createHostEntity("2016-10-17T10:00:00.000Z", "192.168.1.2", "D3-3C-A5-11-32-1F", "laptop802", null, "74a3b323531b84c997b8692456b1a5a2")
    val user1 = createUserEntity("2016-10-15T15:00:00.000Z", "10.10.4.63", "A0:99:9B:0D:C6:E9", "Christophes-MBP", "christophe", "e14")
    val user2 = createUserEntity("2016-10-17T10:00:00.000Z", "10.10.1.2", "D3-3C-A5-11-32-1F", null, "mark", "43ff7482082fc9c798cdc62878b9d59")
    val user3 = createUserEntity("2016-10-17T10:00:00.000Z", "10.10.1.2", "D3-3C-A5-11-32-1F", null, "joe", "a21484e782e4d7014c1448d260af3197")
    val entities: Array[BaseRow] = Array(entity1, entity2, entity3, entity4, host1, user1, host2, user2, user3)
    entityTable.putRows(hBaseConn, entities, false, hBaseConfig)

    val entityRelationshipTable = new EntityRelationshipTable
    entityRelationshipTable.createTableIfNotExists(hBaseConn, hBaseConfig)
    val er1 = createEntityRelationship("2016-10-15T16:00:00.000Z", "e14", "c3c46ec3ab47d5ee70e3f60283015a24", Entity.USER_ENTITY)
    val er2 = createEntityRelationship("2016-10-15T15:00:00.000Z", "c3c46ec3ab47d5ee70e3f60283015a24", "e14", Entity.HOST_ENTITY)
    val er3 = createEntityRelationship("2016-10-17T10:00:00.000Z", "74a3b323531b84c997b8692456b1a5a2", "43ff7482082fc9c798cdc62878b9d59", Entity.HOST_ENTITY)
    val er4 = createEntityRelationship("2016-10-17T10:00:00.000Z", "74a3b323531b84c997b8692456b1a5a2", "a21484e782e4d7014c1448d260af3197", Entity.HOST_ENTITY)
    val er5 = createEntityRelationship("2016-10-18T10:00:00.000Z", "74a3b323531b84c997b8692456b1a5a2", "43ff7482082fc9c798cdc62878b9d59", Entity.HOST_ENTITY)
    val er6 = createEntityRelationship("2016-10-19T09:00:00.000Z", "74a3b323531b84c997b8692456b1a5a2", "a21484e782e4d7014c1448d260af3197", Entity.HOST_ENTITY)
    val er7 = createEntityRelationship("2016-10-19T10:00:00.000Z", "74a3b323531b84c997b8692456b1a5a2", "a21484e782e4d7014c1448d260af3197", Entity.HOST_ENTITY)
    val er8 = createEntityRelationship("2016-10-19T11:00:00.000Z", "74a3b323531b84c997b8692456b1a5a2", "a21484e782e4d7014c1448d260af3197", Entity.HOST_ENTITY)
    val entityRelationships: Array[BaseRow] = Array(er1, er2, er3, er4, er5, er6, er7, er8)
    entityRelationshipTable.putRows(hBaseConn, entityRelationships, false, hBaseConfig)
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
    result
  }

  // Note! Every class that extends HBaseTestBase must have an AfterClass method, and it must call HBaseTestBase.HBaseTeardown().  See comments
  // in HBaseTestBase for details.
  // The AfterClass method should also drop all the tables used in this test, so that other tests will start with a clean slate.
  @AfterClass
  def stopServers() {
//    try {
//      val conn: Connection = PhoenixUtils.getPhoenixConnection(configuration)
//      val entityThreatSql: String = "DROP TABLE IF EXISTS " + EntityThreat.getName(configuration)
//      val entityThreatStmt: PreparedStatement = conn.prepareStatement(entityThreatSql)
//      entityThreatStmt.execute
//      val taniumStatsSql: String = "DROP TABLE IF EXISTS " + TaniumStats.getName(configuration)
//      val taniumStatsStmt: PreparedStatement = conn.prepareStatement(taniumStatsSql)
//      taniumStatsStmt.execute
//    }
//    catch {
//      case ex: Exception =>
//        ex.printStackTrace()
//        throw ex
//    }
    MongoTestBase.teardownMongo()
    HBaseTestBase.teardownHBase()
  }
}


class EntityPropertiesTest {
  private val entityInvestigatorDao = new EntityInvestigatorDao(EntityPropertiesTest.configuration)

  @Test
  def changingIpTest() = {
    // We've got three different ips over four different days.  Check that each of the days has the expected ip
    val properties10_1 = entityInvestigatorDao.getEntityPropertiesByEntityId("e1", "2016-10-01T00:00:00.000Z", "2016-10-02T00:00:00.000Z")
    assertEquals(1, properties10_1.size)
    val entityProps10_1 = properties10_1.head
    val ipList10_1 = entityProps10_1.getOrElse("ip", null).asInstanceOf[ListBuffer[Map[String, String]]]
    assertEquals(1, ipList10_1.size)
    assertEquals("10.10.4.69", ipList10_1(0).getOrElse("value", null))

    val properties10_2 = entityInvestigatorDao.getEntityPropertiesByEntityId("e1", "2016-10-02T00:00:00.000Z", "2016-10-03T00:00:00.000Z")
    assertEquals(1, properties10_2.size)
    val entityProps10_2 = properties10_2.head
    val ipList10_2 = entityProps10_2.getOrElse("ip", null).asInstanceOf[ListBuffer[Map[String, String]]]
    assertEquals(1, ipList10_2.size)
    assertEquals("10.10.4.72", ipList10_2(0).getOrElse("value", null))

    val properties10_3 = entityInvestigatorDao.getEntityPropertiesByEntityId("e1", "2016-10-03T00:00:00.000Z", "2016-10-04T00:00:00.000Z")
    assertEquals(1, properties10_3.size)
    val entityProps10_3 = properties10_3.head
    val ipList10_3 = entityProps10_3.getOrElse("ip", null).asInstanceOf[ListBuffer[Map[String, String]]]
    assertEquals(1, ipList10_3.size)
    assertEquals("10.10.4.99", ipList10_3(0).getOrElse("value", null))

    val properties10_4 = entityInvestigatorDao.getEntityPropertiesByEntityId("e1", "2016-10-04T00:00:00.000Z", "2016-10-05T00:00:00.000Z")
    assertEquals(1, properties10_4.size)
    val entityProps10_4 = properties10_4.head
    val ipList10_4 = entityProps10_4.getOrElse("ip", null).asInstanceOf[ListBuffer[Map[String, String]]]
    assertEquals(1, ipList10_4.size)
    assertEquals("10.10.4.69", ipList10_4(0).getOrElse("value", null))

    // Now check to see what happens when we query across the date range.  We should get all three ips back, newest first
    val properties10_all = entityInvestigatorDao.getEntityPropertiesByEntityId("e1", "2016-10-01T00:00:00.000Z", "2016-10-05T00:00:00.000Z")
    assertEquals(1, properties10_all.size)
    val entityProps10_all = properties10_all.head
    val ipList10_all = entityProps10_all.getOrElse("ip", null).asInstanceOf[ListBuffer[Map[String, String]]]
    assertEquals(3, ipList10_all.size)
    assertEquals("10.10.4.69", ipList10_all(0).getOrElse("value", null))
    assertEquals("10.10.4.99", ipList10_all(1).getOrElse("value", null))
    assertEquals("10.10.4.72", ipList10_all(2).getOrElse("value", null))
  }

  @Test
  def testPrimaryHost() = {
    val userPropertiesList = entityInvestigatorDao.getEntityPropertiesByEntityId("e14", "2016-10-15T00:00:00.000Z", "2016-10-16T00:00:00.000Z")
    assertEquals(1, userPropertiesList.size)
    val userProps = userPropertiesList.head
    assertEquals("laptop801", userProps.getOrElse("primaryHost", null))
  }

  @Test
  def testPrimaryUser() = {
    val hostPropertiesList = entityInvestigatorDao.getEntityPropertiesByEntityId("c3c46ec3ab47d5ee70e3f60283015a24", "2016-10-15T00:00:00.000Z", "2016-10-16T00:00:00.000Z")
    assertEquals(1, hostPropertiesList.size)
    val hostProps = hostPropertiesList.head
    assertEquals("christophe", hostProps.getOrElse("primaryUser", null))
  }

  @Test
  def testMultipleRelationships() = {
    val hostPropertiesList = entityInvestigatorDao.getEntityPropertiesByEntityId("74a3b323531b84c997b8692456b1a5a2", "2016-10-17T00:00:00.000Z", "2016-10-19T00:00:00.000Z")
    assertEquals(1, hostPropertiesList.size)
    val hostProps = hostPropertiesList.head
    assertEquals("mark", hostProps.getOrElse("primaryUser", null))

    val hostPropertiesList2 = entityInvestigatorDao.getEntityPropertiesByEntityId("74a3b323531b84c997b8692456b1a5a2", "2016-10-17T00:00:00.000Z", "2016-10-20T00:00:00.000Z")
    assertEquals(1, hostPropertiesList2.size)
    val hostProps2 = hostPropertiesList2.head
    assertEquals("joe", hostProps2.getOrElse("primaryUser", null))
  }
}
