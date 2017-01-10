package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.utils.EntUserProperties
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.collection.mutable.{Map => MutableMap}

class UserEntityPropertiesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[UserEntityPropertiesDao])

  def getUserId(id: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    if(id != null && !id.isEmpty) {
      val tableName = EntUserProperties.getName(conf)
      val sqlStr = "select * from " + tableName + " where " + EntUserProperties.UUID + " = ? "
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, id)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          buf += selectionMap
        }
      } catch {
        case e: Exception => Logger.error("Failed to get user properties for user id => " + id + "  => " + e)
      } finally {
        conn.close()
      }
    }
    buf
  }

  /**
   * Queries the ENT_USER_PROPS table and returns the Ad user Info for given peers.
   *
   * @param startTime String specifying startTime of the query
   * @param endTime String specifying endTime
   * @param peers String comma-separated peers eg format: ""username1", "userName2""
   *
   * @return UserEntityProperties (username, fullname, accountType and isCritical) for the given peers.
   */
  def getUserPropertiesBySourceNames(startTime: String, endTime: String, peers: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (peers != null && !peers.isEmpty) {
      val tableName = EntUserProperties.getName(conf)

      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)
        val userNameMap: MutableMap[String, MutableMap[String, Any]] = MutableMap[String, MutableMap[String, Any]]()
        val userNameSet = new mutable.HashSet[String]()
        val queryString: String = "(" + peers.replaceAll("\"", "'") + ")"
        val sqlStr = "select * from " + tableName + " where " +
          EntUserProperties.DATE_TIME + " >= ? and " + EntUserProperties.DATE_TIME + " < ? and " +
          EntUserProperties.USER_NAME + " IN " + queryString + " ORDER BY " + EntUserProperties.DATE_TIME + " ASC "
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val userName = rs.getString(EntUserProperties.USER_NAME.toString)
          var selectionMap : MutableMap[String, Any] = null
          if(userNameMap.contains(userName)){
            selectionMap = userNameMap(userName)
          } else{
            selectionMap =  MutableMap[String, Any]()
            userNameMap += userName -> selectionMap
            buf += selectionMap
          }
            appendToMap(rs, rsMeta, selectionMap)
            val fullName = rs.getString(EntUserProperties.CANONICAL_NAME.toString)
            val isCritical: Boolean = {
              if (rs.getString(EntUserProperties.IS_CRITCAL.toString).equals("1")) true else false
            }
            selectionMap += "fullName" -> fullName
            selectionMap += "isCritical" -> isCritical
        }
      } catch {
        case ex: Exception => Logger.error("Failed to get user entity properties for source names => " + peers +
          " with exception => " + ex)
      } finally {
        closeConnections(conn)
      }
    }
    buf
  }

  /**
   * Queries the ENT_USER_PROPS table and returns the Ad user Info for given peers.
   *
   * @param startTime String specifying startTime of the query
   * @param endTime String specifying endTime
   * @param sourceUserName String username
   *
   * @return UserEntityProperties (username, fullname, accountType and isCritical) for the given sourceUserName.
   */
  def getUserPropertiesForSourceName(startTime: String, endTime: String, sourceUserName: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    if (sourceUserName != null && sourceUserName.nonEmpty) {
      val tableName = EntUserProperties.getName(conf)

      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        val userNameMap: MutableMap[String, MutableMap[String, Any]] = MutableMap[String, MutableMap[String, Any]]()
        val selectionMap = MutableMap[String, Any]()
        populateDataFromEntityFusionForUserName(sourceUserName,endTime,selectionMap)
        conn = getConnection(conf)
        val sqlStr = "select * from " + tableName + " where " +
          EntUserProperties.DATE_TIME + " >= ? and " + EntUserProperties.DATE_TIME + " <= ? and UPPER(" +
          EntUserProperties.USER_NAME + ") = UPPER(?) "  + " ORDER BY " + EntUserProperties.DATE_TIME + " ASC "
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, sourceUserName)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          appendToMap(rs, rsMeta, selectionMap)
          val fullName = rs.getString(EntUserProperties.CANONICAL_NAME.toString)
          val isCritical: Boolean = {
            val str = rs.getString(EntUserProperties.IS_CRITCAL.toString)
            if (str !=null && str.equals("1")) true else false
          }
          selectionMap += "fullName" -> fullName
          selectionMap += "isCritical" -> isCritical
        }
        if(selectionMap.nonEmpty){
          buf += selectionMap
        }
      } catch {
        case ex: Exception => Logger.error("Failed to get user entity properties for source name => " + sourceUserName +
          " with exception => " + ex.getMessage(), ex)
      } finally {
        closeConnections(conn)
      }
    }
    buf
  }


  /**
   * Returns the counts of each user type between the over a given period
   *
   * @param startTime String specifying startTime
   * @param endTime String specifying endTime
   * @return
   */
  def getUserTypes(startTime: String, endTime: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try{
      val sqlStr = "select distinct " + EntUserProperties.ACCOUNT_TYPE + ", count(1) as COUNT from " +  EntUserProperties.getName(conf) +
        " where " + EntUserProperties.DATE_TIME + " >= ? " +
        " AND " +  EntUserProperties.DATE_TIME + "  < ? " +
        " AND " + EntUserProperties.ACCOUNT_TYPE + " IS NOT NULL " +
        " GROUP BY " + EntUserProperties.ACCOUNT_TYPE
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      val rsMeta = rs.getMetaData
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs,rsMeta,selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error("Failed to get account types from user entity properties => " + ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   *
   * Queries the Entity_Fusion_Hourly_Summary table and extracts userName, hostName and macAddress for the given ip and time
   *
   * @param username String username
   * @param time String specifying dateTime
   * @param selectionMap Map[String, Any] which stores the userName, hostName and macAddress
   *
   */
  private def populateDataFromEntityFusionForUserName(username: String, time: String, selectionMap: MutableMap[String, Any]) = {
    val entityFusionHourlyRollUpDao: EntityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf)
    val entityInfo = entityFusionHourlyRollUpDao.getEntityInfoFromFusionForUsername(username, time)

    var macAddress = ""
    var hostName = ""
    var ip = ""
    if (entityInfo != null) {
      macAddress = entityInfo.getMacAddress
      hostName = entityInfo.getHostName
      ip = entityInfo.getIpAddress
    }

    selectionMap += "userName" -> username
    selectionMap += "ip" -> ip
    selectionMap += "hostName" -> hostName
    selectionMap += "macAddress" -> macAddress
  }
}
