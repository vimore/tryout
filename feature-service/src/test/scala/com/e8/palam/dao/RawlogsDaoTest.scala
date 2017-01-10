package com.e8.palam.dao

import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}

import com.e8.palam.TestBaseDao
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.RawlogsDao
import com.securityx.modelfeature.utils.RawLogs
import org.junit.{Test, Before}
import org.mockito.Mockito
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

class RawlogsDaoTest extends TestBaseDao {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[RawlogsDaoTest])

  var rawlogsDao : RawlogsDao = null
  val testUuid = "00003c96-17de-4a3a-b22e-3629a5b23886"
  val testStartTimeISO = "2015-12-10T16:09:14.000Z"
  val testRawlog = "<134>Dec 10 16:09:14 192.168.14.10 2015-12-10 16:09:14 306 10.0.1.6 200 TCP_NC_MISS 185 230 GET http www.ositis.com /tests/testconnectivity.asp ?Test=1517 - DIRECT www.ositis.com text/html \"Mozilla/4.0 (compatible; ProxyAV)\" PROXIED Computers/Internet - 192.168.13.10 SG-HTTP-Service - none -73222"
  val testUuid2 = "0000d16b-ccfb-4f9d-8b73-a202f027056e"
  val testStartTimeISO2 = "2015-12-10T20:10:54.000Z"
  val testRawlog2 = "<134>Dec 10 20:10:54 192.168.15.11 2015-12-10 20:10:54 153 138.83.69.84 200 TCP_MISS 7440 239 GET http v4.windowsupdate.microsoft.com /selfupdate/AU/x86/W2K/en/wuaucomp.cab ?0504301510 - DIRECT v4.windowsupdate.microsoft.com application/octet-stream \"Industry Update Control\" PROXIED Computers/Internet - 192.168.15.11 SG-HTTP-Service - none -5748"
  val goodUuidArray = Array(testUuid, testUuid2, "001a1e0f-96a0-4643-859a-6835517631f6",
    "004051f6-88fa-421f-ac20-870f27380e4f",
    "1-94e4fce4-be3f-11e5-8b91-22000a939717-7bd9-120b0821671d6c5d49d19ff2f80e4f93",
    "1-979b42c3-be3f-11e5-8b91-22000a939717-7bd9-d7b68ed54f78ee665144fba713e82a14")
  val badUuidArray = Array("nodashesatleastsomemustexistorinvalid", "-some*bad'characters,we)must(not&accept",
    // The next set of bad uuids include specific characters that we must not accept
    "0061ffc7-e4bf-4c'8-a76d-0732cdb24982", "0061*fc7-e4bf-4cc8-a76d-0732cdb24982", "0061ffc7-e4bf-4cc8-a76d-0732cdb\"4982", "006c6d1c-c3a7-4(6d-a38b-ada4930b3d6e",
    "0)6c6d1c-c3a7-496d-a38b-ada4930b3d6e", "00703449-2192-43a6-8f9e-c034 b200428", "00764478-65a8-4cbe-ac&d-2ebcc80518e6")
  val invalidUuid = "1234-5678"
  val invalidUuid2 = "00703449-2192-43a6-8f9e-c034 b200428"

  @Before
  override def setup() = {
    super.setup()

    rawlogsDao = new RawlogsDao(conf) {
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
    }

    buildResultSetData()
    buildResultSetMetaData()
  }

  override def buildResultSetMetaData(): Unit = {
    val columns: RawLogs.ValueSet = RawLogs.values
    var i: Int = 1
    columns.foreach { column =>
      Mockito.when(mockResultSetMetaData.getColumnName(i)).thenReturn(column.toString)
      i = i + 1
    }
    Mockito.when(mockResultSetMetaData.getColumnCount).thenReturn(RawLogs.values.size)
    Mockito.when(mockResultSetMetaData.getColumnClassName(1)).thenReturn("java.lang.String")
    Mockito.when(mockResultSetMetaData.getColumnClassName(2)).thenReturn("java.lang.String")
    Mockito.when(mockResultSetMetaData.getColumnClassName(3)).thenReturn("java.lang.String")
  }

  override def buildResultSetData(): Unit = {
    Mockito.when(mockResultSet.getString(RawLogs.UUID.toString)).thenReturn(testUuid).thenReturn(testUuid2)
    Mockito.when(mockResultSet.getString(RawLogs.RAWLOG.toString)).thenReturn(testRawlog).thenReturn(testRawlog2)
    Mockito.when(mockResultSet.getString(RawLogs.START_TIME_ISO.toString)).thenReturn(testStartTimeISO).thenReturn(testStartTimeISO2)
  }

  /*******************************************************************/
  // Tests for the getRawlog() method
  /*******************************************************************/

  @Test
  def testGetByUUID() = {
    Logger.debug("get Rawlog by uuid")
    // Note that returning true twice checks that getRawlog() does the right thing when it gets
    // multiple results for a given uuid
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    val resultMap: MutableMap[String, Any] = rawlogsDao.getRawlog(testUuid)

    assert(resultMap != null)
    assert(resultMap.nonEmpty)
    assert(resultMap("uuid") == testUuid)
    assert(resultMap("rawLog") == testRawlog)
    assert(resultMap("startTimeISO") == testStartTimeISO)

    Logger.debug("Test Succeeded")
  }

  @Test
  def testGetByUUIDNotFound() = {
    Logger.debug("get Rawlog by uuid not in table")
    //mock result set
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val resultMap: MutableMap[String, Any] = rawlogsDao.getRawlog(testUuid)

    assert(resultMap != null)
    assert(resultMap.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def testGetByNullUUID() = {
    Logger.debug("get Rawlog with null uuid")
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val resultMap: MutableMap[String, Any] = rawlogsDao.getRawlog(null)

    assert(resultMap != null)
    assert(resultMap.isEmpty)
    Logger.debug("Test Succeeded")
  }

  @Test
  def testGetByEmptyUUID() = {
    Logger.debug("get Rawlog with empty uuid")
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val resultMap: MutableMap[String, Any] = rawlogsDao.getRawlog("")

    assert(resultMap != null)
    assert(resultMap.isEmpty)
    Logger.debug("Test Succeeded")
  }

  /*******************************************************************/
  // Tests for the validateUuid() method
  /*******************************************************************/

  @Test
  def testUuidValidation() = {
    Logger.debug("test uuid validation")
    for (good <- goodUuidArray) {
      assert(rawlogsDao.validateUuid(good), "testing good uuid [" + good + "]")
    }

    for (bad <- badUuidArray) {
      assert(!rawlogsDao.validateUuid(bad), "testing bad uuid [" + bad + "]")
      Logger.debug("Test Succeeded")
    }
  }

  /*******************************************************************/
  // Tests for the getRawlogList() method
  /*******************************************************************/

  @Test
  def testGetRawlogList() = {
    Logger.debug("get rawlog from uuid list")
    Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)

    val uuidArray = Array(testUuid, testUuid2)
    var resultList = rawlogsDao.getRawlogList(uuidArray)

    assert(resultList != null)
    assert(resultList.length == 2)
    val first: MutableMap[String, Any] = resultList.head
    resultList = resultList.tail
    assert(first("uuid") == testUuid)
    assert(first("rawLog") == testRawlog)
    assert(first("startTimeISO") == testStartTimeISO)
    val second: MutableMap[String, Any] = resultList.head
    assert(second("uuid") == testUuid2)
    assert(second("rawLog") == testRawlog2)
    assert(second("startTimeISO") == testStartTimeISO2)

    Logger.debug("Test Succeeded")
  }

  @Test
  def testGetRawlogListWithNullList() = {
    Logger.debug("get rawlog from uuid list with null list")
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val resultList = rawlogsDao.getRawlogList(null)

    assert(resultList != null)
    assert(resultList.isEmpty)

    Logger.debug("Test Succeeded")
  }

  @Test
  def testGetRawlogListWithEmptyList() = {
    Logger.debug("get rawlog from uuid list with empty list")
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val uuidArray: Array[String] = Array()
    val resultList = rawlogsDao.getRawlogList(uuidArray)

    assert(resultList != null)
    assert(resultList.isEmpty)

    Logger.debug("Test Succeeded")
  }

  @Test
  def testGetRawlogListWithInvalidUuids() = {
    Logger.debug("get rawlog from uuid list with invalid uuids")
    Mockito.when(mockResultSet.next()).thenReturn(false)

    val uuidArray: Array[String] = Array(invalidUuid, invalidUuid2)
    val resultList = rawlogsDao.getRawlogList(uuidArray)

    assert(resultList != null)
    assert(resultList.isEmpty)

    Logger.debug("Test Succeeded")
  }
}
