package com.e8.palam.dao

import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}

import com.e8.palam.TestBaseDao
import com.securityx.modelfeature.common.EntityInfo
import com.securityx.modelfeature.utils.EntityFusionHourlyRollUp
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.EntityFusionHourlyRollUpDao
import org.junit.{Before, Test}
import org.mockito.Mockito

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 1/13/15.
 */
class EntityFusionHourlyRollUpDaoTest extends TestBaseDao{

  val testDateTime = "2015-02-04T00:00:00.000Z"
  val testEndDateTime = "2015-02-07T00:00:00.000Z"
  var testIpAddress_1 = "10.10.10.1"
  var testIpAddress_2 = "10.10.10.2"
  val testMacAddress_1 = "00:0a:95:9d:68:16"
  val testMacAddress_2 = "01:0a:95:9d:68:16"
  val testHostName_1 = "Bob-Macbook-pro"
  val testHostName_2 = "Alice-Macbook-pro"
  var testUserName_1 = "bob"
  val testUserName_2 = "alice"

  var entityFusionHourlyRollUpDao: EntityFusionHourlyRollUpDao = null;

  //test output Map
  var buf = ListBuffer[MutableMap[String, Any]]()
  var testOutputMap = MutableMap[String, Any]()
  var counter = 0

  @Before
  override def setup = {
    super.setup()

    entityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf) {
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

  override def buildResultSetMetaData(): Unit = {
    Mockito.when(mockResultSet.getString(EntityFusionHourlyRollUp.FUSION_TIME.toString)).thenReturn(testDateTime)
    Mockito.when(mockResultSet.getString(EntityFusionHourlyRollUp.IP_ADDRESS.toString)).thenReturn(testIpAddress_1).
      thenReturn (testIpAddress_2)
    Mockito.when(mockResultSet.getString(EntityFusionHourlyRollUp.MAC_ADDRESS.toString)).thenReturn(testMacAddress_1).
      thenReturn(testMacAddress_2)
    Mockito.when(mockResultSet.getString(EntityFusionHourlyRollUp.HOST_NAME.toString)).thenReturn(testHostName_1).
      thenReturn(testHostName_2)
    Mockito.when(mockResultSet.getString(EntityFusionHourlyRollUp.USER_NAME.toString)).thenReturn(testUserName_1).
      thenReturn(testUserName_2)
  }

  override def buildResultSetData(): Unit = {
    val columns: EntityFusionHourlyRollUp.ValueSet = EntityFusionHourlyRollUp.values
    var i: Int = 1
    columns.foreach { column =>
      Mockito.when(mockResultSetMetaData.getColumnName(i)).thenReturn(column.toString)
      i = i + 1
    }
    Mockito.when(mockResultSetMetaData.getColumnCount).thenReturn(EntityFusionHourlyRollUp.values.size)
  }

  @Test
  def testGetEntityInfoFromFusionNonEmptyResult() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false)

    //test
    val entityInfo: EntityInfo = entityFusionHourlyRollUpDao.getEntityInfoFromFusionForIp(testIpAddress_1, testDateTime)
    assert(entityInfo != null)
    assert(entityInfo.getHostName == testHostName_1)
    assert(entityInfo.getMacAddress == testMacAddress_1)
    assert(entityInfo.getUserName == testUserName_1)
    assert(entityInfo.getIpAddress == testIpAddress_1)
  }

  @Test
  def testGetIpToEntityInfoMapForIpList() = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    val ipList = "10.10.10.1, 10.10.10.2, 10.10.10.3, 10.10.10.4"

    //test
    val entityInfoMap: MutableMap[String, EntityInfo] = entityFusionHourlyRollUpDao.getIpToEntityInfoMapForIpList(ipList, testDateTime)
    assert(entityInfoMap != null)
    assert(entityInfoMap.contains(testIpAddress_1))
    var entityInfo : EntityInfo = entityInfoMap(testIpAddress_1)
    assert(entityInfo.getHostName == testHostName_1)
    assert(entityInfo.getMacAddress == testMacAddress_1)
    assert(entityInfo.getUserName == testUserName_1)
    assert(entityInfo.getIpAddress == testIpAddress_1)

    assert(entityInfoMap.contains(testIpAddress_2))
    entityInfo = entityInfoMap(testIpAddress_2)
    assert(entityInfo.getHostName == testHostName_2)
    assert(entityInfo.getMacAddress == testMacAddress_2)
    assert(entityInfo.getUserName == testUserName_2)
    assert(entityInfo.getIpAddress == testIpAddress_2)
  }


  @Test
  def testGetIpToEntityInfo() : Unit = {
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    val entityInfoMap: MutableMap[String, ListBuffer[EntityInfo]] = entityFusionHourlyRollUpDao.getIpToEntityInfo(testDateTime,
      testEndDateTime)

    assert(entityInfoMap.nonEmpty)
    assert(entityInfoMap != null)
    assert(entityInfoMap.contains(testIpAddress_1))
    var entityInfoList : ListBuffer[EntityInfo] = entityInfoMap(testIpAddress_1)
    assert(entityInfoList != null)
    assert(entityInfoList.nonEmpty)
    var entityInfo : EntityInfo = entityInfoList(0)
    assert(entityInfo.getHostName == testHostName_1)
    assert(entityInfo.getMacAddress == testMacAddress_1)
    assert(entityInfo.getUserName == testUserName_1)
    assert(entityInfo.getIpAddress == testIpAddress_1)

    assert(entityInfoMap.contains(testIpAddress_2))
    entityInfoList = entityInfoMap(testIpAddress_2)
    assert(entityInfoList != null)
    assert(entityInfoList.nonEmpty)
    entityInfo = entityInfoList(0)
    assert(entityInfo.getHostName == testHostName_2)
    assert(entityInfo.getMacAddress == testMacAddress_2)
    assert(entityInfo.getUserName == testUserName_2)
    assert(entityInfo.getIpAddress == testIpAddress_2)

  }
}
