package com.securityx.modelfeature.dao

import java.sql.{ResultSet, PreparedStatement, Connection}
import java.util.regex.Pattern

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.RawLogs
import org.slf4j.{LoggerFactory, Logger}

import scala.StringBuilder
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.{Map => MutableMap, ListBuffer}


/**
 * This class is used to access the rawlogs table
 */
class RawlogsDao(conf: FeatureServiceConfiguration)  extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[RawlogsDao])
  // A uuid can only have letters, numbers and dashes
  private final val UUID_PATTERN = Pattern.compile("[a-zA-Z0-9\\-]*")

  @deprecated
  @Deprecated
  def getRawlog(Uuid: String) = {
    var result = MutableMap[String, Any]()

    if (validateUuid(Uuid)) {
      val tableName = RawLogs.getName(conf)
      val sqlStr = "select " + "*" + " from " + tableName + " where " + RawLogs.UUID + " = ? "
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, Uuid)
        rs = executeQuery(pstmt)

        if (rs.next()) {
          val uuid = rs.getString(RawLogs.UUID.toString)
          val rawLog = rs.getString(RawLogs.RAWLOG.toString)
          val startTimeISO = rs.getString(RawLogs.START_TIME_ISO.toString)
          val selectionMap = MutableMap[String, Any]()

          selectionMap += "uuid" -> uuid
          selectionMap += "rawLog" -> rawLog
          selectionMap += "startTimeISO" -> startTimeISO
          result = selectionMap
        }

        if (rs.next()) {
          // We expect there to be only one entry in HBase for a given uuid.  If there's
          // more than one, log a warning, but return the first value.
          LOGGER.warn("Got more than one rawlog from [" + tableName + "] for uuid [" + Uuid + "]")
        }
      } catch {
        case e: Exception => LOGGER.error("Failed to get rawlogs for => " + Uuid + e)
      } finally {
        if (rs != null) {
          try {
            rs.close()
          } catch {
            case e: Exception => LOGGER.error("Error while closing result set")
          }
        }
        if (pstmt != null) {
          try {
            pstmt.close()
          } catch {
            case e: Exception => LOGGER.error("Error while closing prepared statement")
          }
        }
        closeConnections(conn)
      }
    }

    result
  }

  @deprecated
  @Deprecated
  def getRawlogList(uuidArray: Array[String]) = {
    var result = new ListBuffer[MutableMap[String, Any]]

    if (uuidArray != null && uuidArray.length > 0) {
      // First validate the uuids
      var allValidated = true
      for (uuid <- uuidArray) {
        if (!validateUuid(uuid)) {
          allValidated = false
          LOGGER.warn("Invalid uuid passed to RawlogsDao.getRawlogList: [" + uuid + "]")
        }
      }

      if (allValidated) {
        val tableName = RawLogs.getName(conf)
        val sql = new StringBuilder()
        // Construct a query with an in clause with as many elements as there are elements
        // in the uuidArray passed in.  This may need to be broken down into pieces at some
        // point - Phoenix sql may have a max character limit on queries.  But for now...
        sql ++= "select * from "
        sql ++= tableName
        sql ++= " where UUID in ("
        for (i <- uuidArray.indices) {
          if (i > 0) {
            sql ++= ", "
          }
          sql ++= "?"
        }
        sql ++= ")"
        var conn: Connection = null
        var pstmt: PreparedStatement = null
        var rs: ResultSet = null
        try {
          conn = getConnection(conf)
          pstmt = getPreparedStatement(conn, sql.toString())
          var element = 1
          for (uuid <- uuidArray) {
            pstmt.setString(element, uuid)
            element += 1
          }
          rs = executeQuery(pstmt)

          while (rs.next()) {
            val uuid = rs.getString(RawLogs.UUID.toString)
            val rawLog = rs.getString(RawLogs.RAWLOG.toString)
            val startTimeISO = rs.getString(RawLogs.START_TIME_ISO.toString)
            val selectionMap = MutableMap[String, Any]()

            selectionMap += "uuid" -> uuid
            selectionMap += "rawLog" -> rawLog
            selectionMap += "startTimeISO" -> startTimeISO
            result += selectionMap
          }
        } catch {
          case e: Exception => LOGGER.error("Failed to get rawlogs for => " + uuidArray.toString + e)
        } finally {
          if (rs != null) {
            try {
              rs.close()
            } catch {
              case e: Exception => LOGGER.error("Error while closing result set")
            }
          }
          if (pstmt != null) {
            try {
              pstmt.close()
            } catch {
              case e: Exception => LOGGER.error("Error while closing prepared statement")
            }
          }
          closeConnections(conn)
        }
      }
    }

    result.toList
  }

  /**
   * Return true iff the passed in string is a valid uuid.  We do this test primarily to protect against
   * sql injection attacks, and any string that can be a sql injection attack should fail.  See RawlogsDaoTest
   * for some strings that should and shouldn't pass.
   *
   * @param uuid the uuid to check
   */
  def validateUuid(uuid: String) = {
    var result = false
    // all uuids must include at least one -.  We currently use 7,
    // but that part of the format has changed in the past,
    // so we'll just check that there's at least one.
    if (uuid != null && uuid.indexOf('-') != -1) {
      // check that the uuid is in the right form.  For now, that just means checking that
      // it only contains certain kinds of characters.  We could be more specific, but I'm
      // not sure we will always maintain the same form.
      val uuidMatcher = UUID_PATTERN.matcher(uuid)
      if (uuidMatcher.matches()) {
        result = true
      }
    }

    result
  }
}
