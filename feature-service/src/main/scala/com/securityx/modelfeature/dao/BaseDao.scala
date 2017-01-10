package com.securityx.modelfeature.dao

import java.sql._
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.MiscUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created with IntelliJ IDEA.
 * User: rjurney
 * Date: 6/16/14
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
class BaseDao(val conf: FeatureServiceConfiguration) {
  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[BaseDao])

  @throws(classOf[Exception])
  def getConnection(conf: FeatureServiceConfiguration): Connection = {
    PhoenixUtils.getPhoenixConnection(conf)
  }

  @throws(classOf[Exception])
  def getPreparedStatement(conn: Connection, sqlString: String) = {
    conn.prepareStatement(sqlString)
  }

  def populatePreparedStatementParams(pstmt: PreparedStatement, columns: ListBuffer[ColumnMetaData]) = {
    var i: Int = 1
    for (column <- columns) {
      column.columnType match {
        case "String" => pstmt.setString(i, column.columnValue.asInstanceOf[String])
        case "Int" => pstmt.setInt(i, column.columnValue.toString.toInt)
        case "Double" => pstmt.setDouble(i, column.columnValue.toString.toDouble)
        case "Timestamp" => pstmt.setTimestamp(i, Timestamp.valueOf(column.columnValue.toString))
      }
      i = i + 1
    }

  }


  @throws(classOf[Exception])
  def executeQuery(preparedStatement: PreparedStatement): ResultSet = {
    preparedStatement.executeQuery()
  }

  @throws(classOf[Exception])
  def executeUpdate(preparedStatement: PreparedStatement): Int = {
    preparedStatement.executeUpdate()
  }

  @throws(classOf[Exception])
  def getResultSetMetaData(resultSet: ResultSet): ResultSetMetaData = {
    resultSet.getMetaData
  }

  def closeConnections(conn: Connection, pstmt: PreparedStatement, rs: ResultSet) = {
    if (rs != null) {
      try {
        rs.close()
      } catch {
        case e: Exception => LOGGER.error("Error while closing result set", e)
      }
    }
    if (pstmt != null) {
      try {
        pstmt.close()
      } catch {
        case e: Exception => LOGGER.error("Error while closing prepared statement", e)
      }
    }
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception => LOGGER.error("Error while closing connection", e)
      }
    }
  }

  def closeConnections(conn: Connection) = {
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception => LOGGER.error("Error while closing connection", e)
      }
    }
  }

  /**
   * A generic method to extract column values from resultset
   *
   * @param rs
   * @param rsMeta
   * @param valMap
   */
  def appendToMap(rs: ResultSet, rsMeta: ResultSetMetaData, valMap: MutableMap[String, Any]) = {
    val c = rsMeta.getColumnCount
    (1 to c).foreach { i =>
      val s = rsMeta.getColumnName(i)
      val n = MiscUtils.underscoreToCamel(s.toLowerCase)
      val v = rsMeta.getColumnClassName(i) match {
        case "java.lang.Integer" => rs.getInt(s)
        case "java.lang.String" => rs.getString(s)
        case "java.lang.Double" => rs.getDouble(s)
        case "java.math.BigDecimal" => rs.getBigDecimal(s)
        case "java.lang.Long" => rs.getLong(s)
        case "java.sql.Timestamp" => rs.getTimestamp(s)
        case x => {
          println(" Unhandled type ")
          rs.getString(s)
        }
      }

      valMap += n -> v

    }
  }

  def getEnvTableName(tableName: String) = {
    tableName
  }

  // This is the canonical list of event categories. All incoming data sources need to use exactly these
  // names, which should be validated in any Dao's that consume data. Any relevant API output should be
  // populated using the values of this Enumeration rather than any input data.
  object EventCategory extends Enumeration {
    val RECON = Value("Recon")
    val EXPLOIT = Value("Exploit")
    val CONTROL = Value("Control")
    val MOVE = Value("Move")
    val GATHER = Value("Gather")
    val EXFILTRATE = Value("Exfiltrate")
  }

  def getCanonicalEventCategory(category_string: String): String = {
    // Return one of the EventCategory values as a string, or null if there's no match.
    val category: EventCategory.Value = try {
      EventCategory.withName(category_string)
    } catch {
      case nse: java.util.NoSuchElementException =>
        LOGGER.debug("EventCategory enumeration has no element named " + category_string)
        null
      case e: Exception => throw e
    }
    if (category == null) {
      null
    } else {
      category.toString
    }
  }

  /**
   * Given a column name and corresponding tableName, this method finds the number of distinct values for that column
   *
   * @param tableName String table name
   * @param columnName String column name
   *
   * @return  Int count (distinct counts)
   */
  def getTotalDistinctCount(tableName: String, columnName: String) = {
    var count = 0
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      val sqlStr = "SELECT COUNT  ( DISTINCT " + columnName + " ) " + " AS COUNT "  + " FROM " + tableName
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        count = rs.getInt("COUNT")
      }
    } catch {
      case ex: Exception => LOGGER.error("Failed to get users-count from Entity Fusion => " + ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    count
  }


  def executeSqlQuery(sql : String, startTime: String, endTime: String) ={
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sql)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => LOGGER.error("Failed to get users-count from Entity Fusion => " + ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }
}

case class ColumnMetaData(columnName: String, columnType: String, columnValue: AnyRef) {
}