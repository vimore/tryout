package com.e8.palam

import java.sql.{Connection, PreparedStatement, ResultSet, ResultSetMetaData}

import org.junit.Before
import org.mockito.Mockito._


/**
 * Created by harish on 2/12/15.
 */
abstract class TestBaseDao extends TestBase {

  val mockConn: Connection = mock(classOf[Connection])
  val mockPreparedStatement = mock(classOf[PreparedStatement])
  val mockResultSet = mock(classOf[ResultSet])
  val mockResultSetMetaData = mock(classOf[ResultSetMetaData])

  //startTime and endTime are common for all our database operations
  val startTime: String = "2015-02-03T00:00:00.000Z"
  val endTime: String = "2015-02-04T00:00:00.000Z"

  @Before
  override def setup() = {
    super.setup()
  }

  def buildResultSetMetaData()

  def buildResultSetData()
}
