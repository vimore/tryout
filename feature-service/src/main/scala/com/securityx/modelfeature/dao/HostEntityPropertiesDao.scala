package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.securityx.modelfeature.common.EntityInfo
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{EntHostProperties, SqlUtils}
import org.joda.time.format.ISODateTimeFormat
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}


class HostEntityPropertiesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[HostEntityPropertiesDao])
  val iso_format: org.joda.time.format.DateTimeFormatter = ISODateTimeFormat.dateTime()
  val suppressionDao = new SuppressionDao(conf)

  def getHostEntityProperties() = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    //NOTE: Need to refactor common functionality to a separate class. See other DAOs
    val tableName = EntHostProperties.getName(conf)
    var conn: Connection = null
    try {
      val sqlStr = "select * from " + tableName
      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      val rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Host Entity properties => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getHostId(id: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    //NOTE: Need to refactor common functionality to a separate class. See other DAOs
    val tableName = EntHostProperties.getName(conf)
    var conn: Connection = null
    try {
      val suppressionList = suppressionDao.getSuppressionList
      val sqlStr = "select * from " + tableName + " where UUID = ?"
      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, id)
      val rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        val ipAddresses = selectionMap.getOrElse("ipAddresses", "").asInstanceOf[String]
        val ipArray = getArrayFromPropertyList(ipAddresses)
        val hostNames = selectionMap.getOrElse("hostNames", "").asInstanceOf[String]
        val hostNameArray = getArrayFromPropertyList(hostNames)
        val primaryUserId = selectionMap.getOrElse("primaryUserid", "").asInstanceOf[String]
        // Now we need to check all of these to decide whether this row is suppressed or not
        var shouldSuppress = false
        if (suppressionDao.shouldSuppressNoBehaviorInfo(suppressionList, "", "", primaryUserId, false)) {
          shouldSuppress = true
        } else {
          for (ip <- ipArray) {
            if (suppressionDao.shouldSuppressNoBehaviorInfo(suppressionList, ip, "", "", false)) {
              shouldSuppress = true
            }
          }
          if (!shouldSuppress) {
            for (hostName <- hostNameArray) {
              if (suppressionDao.shouldSuppressNoBehaviorInfo(suppressionList, "", hostName, "", false)) {
                shouldSuppress = true
              }
            }
          }
        }
        if (!shouldSuppress) {
          buf += selectionMap
        }
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Host Entity properties from id => " + id + " => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getTopNHosts(topN: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    //NOTE: Need to refactor common functionality to a separate class. See other DAOs
    val tableName = EntHostProperties.getName(conf)
    var conn: Connection = null
    try {
      val sqlStr = "select * from " + tableName + " limit ? "
      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setInt(1, topN)
      val rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        val category = rs.getString(EntHostProperties.CATEGORY.toString)
        //TODO: remove all these special casing from this file
        if(category.equalsIgnoreCase("PAN_FW")){
          selectionMap += "panUserId" -> rs.getString(EntHostProperties.PRIMARY_USERID.toString)
        }
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get " + topN + " Host Entity properties => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getHostIdByIp(ip_address: String, time: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    //NOTE: Need to refactor common functionality to a separate class. See other DAOs
    val tableName = EntHostProperties.getName(conf)
    var conn: Connection = null
    try {
      val sqlStr = "select UUID from " + tableName + " where IP_ADDRESSES LIKE ? AND DATE_TIME = ?"
      conn = getConnection(conf)
      val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, "%" + ip_address + "%")
      pstmt.setString(2, time)
      val rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        val category = rs.getString(EntHostProperties.CATEGORY.toString)
        //TODO: remove all these special casing from this file
        if(category.equalsIgnoreCase("PAN_FW")){
          selectionMap += "panUserId" -> rs.getString(EntHostProperties.PRIMARY_USERID.toString)
        }
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case e: Exception => Logger.error("Failed to get Host Entity properties for ip => " + ip_address + " =>" + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  /**
   * Get host properties for a given ip and time
   * @param ip_address String ip address
   * @param time String specifying dateTime
   * @return ListBuffer[MutableMap[String, Any] which stores the hostName, userName, macAddress, os, city, country,
   *         browserName and browserVersion
   */
  def getEntHostPropsByIp(ip_address: String, time: String): collection.mutable.ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val selectionMap = MutableMap[String, Any]()

    //first, find mac address, hostname and username
    populateDataFromEntityFusionForIp(ip_address, time, selectionMap)
    val macAddress: String = selectionMap("macAddress").asInstanceOf[String]

    //find other info browser, browserVersion
    populateDataFromHostEntPropertiesForIP(macAddress, time, selectionMap)

    selectionMap += "ip" -> ip_address
    buf += selectionMap

    buf
  }

  /**
   * Get host properties for a given ip and time
   * @param hostName String Hostname
   * @param time String specifying dateTime
   * @return ListBuffer[MutableMap[String, Any] which stores the hostName, userName, macAddress, os, city, country,
   *         browserName and browserVersion
   */
  def getEntHostPropsByHostName(hostName: String, time: String): collection.mutable.ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val selectionMap = MutableMap[String, Any]()

    //first, find mac address, hostname and username
    populateDataFromEntityFusionForHostname(hostName, time, selectionMap)
    val macAddress: String = selectionMap("macAddress").asInstanceOf[String]

    //find other info browser, browserVersion
    populateDataFromHostEntPropertiesForIP(macAddress, time, selectionMap)

    selectionMap += "hostName" -> hostName
    buf += selectionMap

    buf
  }

  /**
   *
   * Queries the Ent_host_properties table and extracts browserName and browserVersion for the given ip and time
   *
   * @param time String specifying dateTime
   * @param selectionMap Map[String, Any] which stores the os, city, country, browserName, browserVersion
   *
   */
  private def populateDataFromHostEntPropertiesForIP(macAddress: String, time: String,
                                                     selectionMap: MutableMap[String, Any]) = {
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = EntHostProperties.getName(conf)
    try {
      val sqlStr = "SELECT " + EntHostProperties.columns + " FROM " + tableName +
        " WHERE MAC_ADDRESS = ? AND DATE_TIME <= ? ORDER BY DATE_TIME "
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, macAddress)
      pstmt.setString(2, time)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val browsers = rs.getString(EntHostProperties.BROWSERS.toString)
        val browserjson: org.json.JSONArray = new org.json.JSONArray(browsers)
        selectionMap += "browserName" -> browserjson.getJSONObject(0).get("browser")
        selectionMap += "browserVersion" -> browserjson.getJSONObject(0).get("version")
        selectionMap += "country" -> rs.getString(EntHostProperties.COUNTRY.toString)
        selectionMap += "city" -> rs.getString(EntHostProperties.CITY.toString)
        selectionMap += "os" -> rs.getString(EntHostProperties.OS.toString)
        selectionMap += "primaryUserId" -> rs.getString(EntHostProperties.PRIMARY_USERID.toString)
        val category = rs.getString(EntHostProperties.CATEGORY.toString)
        //TODO: remove all these special casing from this file
        if(category.equalsIgnoreCase("PAN_FW")){
          selectionMap += "panUserId" -> rs.getString(EntHostProperties.PRIMARY_USERID.toString)
        }
      }
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get host entity properties for mac: " + macAddress + " for date_time: " +
          time + ": " + ex)
    } finally {
      closeConnections(conn)
    }
  }

  /**
   *
   * Queries the Entity_Fusion_Hourly_Summary table and extracts userName, hostName and macAddress for the given ip and time
   *
   * @param ip_address String ipAddress
   * @param time String specifying dateTime
   * @param selectionMap Map[String, Any] which stores the userName, hostName and macAddress
   *
   */
  private def populateDataFromEntityFusionForIp(ip_address: String, time: String, selectionMap: MutableMap[String, Any]) = {
    val entityFusionHourlyRollUpDao: EntityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf)
    val entityInfo = entityFusionHourlyRollUpDao.getEntityInfoFromFusionForIp(ip_address, time)

    var macAddress = ""
    var hostName = ""
    var userName = ""
    if (entityInfo != null) {
      macAddress = entityInfo.getMacAddress
      hostName = entityInfo.getHostName
      userName = entityInfo.getUserName
    }

    selectionMap += "userName" -> userName
    selectionMap += "hostName" -> hostName
    selectionMap += "macAddress" -> macAddress
  }

  private def populateDataFromEntityFusionForHostname(hostName: String, time: String, selectionMap: MutableMap[String, Any]) = {
    val entityFusionHourlyRollUpDao: EntityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf)
    val entityInfo = entityFusionHourlyRollUpDao.getEntityInfoFromFusionForHostName(hostName, time)

    var macAddress = ""
    var ip = ""
    var userName = ""
    if (entityInfo != null) {
      macAddress = entityInfo.getMacAddress
      ip = entityInfo.getIpAddress
      userName = entityInfo.getUserName
    }

    selectionMap += "userName" -> userName
    selectionMap += "ip" -> ip
    selectionMap += "macAddress" -> macAddress
  }


  /**
   * Gets the Entity properties for a list of Hosts
   *
   * @param inputIpList comma-separated ip addresses. eg: ""10.1.1.1", "10.2.2.2", "10.1.1.3""
   * @param time String specifying DateTime
   *
   * @return Host Entity Properties as ListBuffer[MutableMap[String, Any] which stores the hostName, userName, macAddress,
   *         os, city, country, browserName and browserVersion for each host
   */
  def getEntHostPropsByIpList(inputIpList: String, time: String): collection.mutable.ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    val entityFusionHourlyRollUpDao: EntityFusionHourlyRollUpDao = new EntityFusionHourlyRollUpDao(conf)
    val ipToEntityInfoMap: MutableMap[String, EntityInfo] = entityFusionHourlyRollUpDao.getIpToEntityInfoMapForIpList(inputIpList, time)

    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = EntHostProperties.getName(conf)

    try {
      val suppressionList = suppressionDao.getSuppressionList
      var suppressionCount = 0

      // TODO: add some kind of verification to make sure that the inputIpList is only strings that can be a ip address, to
      // TODO: protect against sql injection attacks.
      val likeOperatorString = SqlUtils.getLikeOperatorString(inputIpList, EntHostProperties.IP_ADDRESSES.toString)
      val ipToSelectionMap: MutableMap[String, MutableMap[String, Any]] = MutableMap[String, MutableMap[String, Any]]()
      val sqlStr = "SELECT " + EntHostProperties.columns + " FROM " + tableName + " WHERE " + EntHostProperties.DATE_TIME +
        " <= ? and " + likeOperatorString + " ORDER BY DATE_TIME ASC "
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, time)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        var ipAddresses = rs.getString(EntHostProperties.IP_ADDRESSES.toString)
        val primaryUser = rs.getString(EntHostProperties.PRIMARY_USERID.toString)
        ipAddresses = ipAddresses.replaceAll("\\[", "")
        ipAddresses = ipAddresses.replaceAll("\\]", "")
        ipAddresses = ipAddresses.replaceAll("\"", "")
        val ips: Array[String] = ipAddresses.split(",")
        ips.foreach { ipAddress =>
          val ip = ipAddress.trim
          val hostName = if (ipToEntityInfoMap.contains(ip)) ipToEntityInfoMap(ip).getHostName else null
          if (suppressionDao.shouldSuppress(suppressionList, ip, hostName, primaryUser, "", false)) {
            suppressionCount += 1
          } else {
            var selectionMap: MutableMap[String, Any] = null
            if (ip.nonEmpty && inputIpList.contains(ip)) {
              if (ipToSelectionMap.contains(ip)) {
                selectionMap = ipToSelectionMap(ip)
              } else {
                selectionMap = MutableMap[String, Any]()
                ipToSelectionMap += ip -> selectionMap
                buf += selectionMap
              }
              val macAddress = rs.getString(EntHostProperties.MAC_ADDRESS.toString)
              val browsers = rs.getString(EntHostProperties.BROWSERS.toString)
              val browserjson: org.json.JSONArray = new org.json.JSONArray(browsers)
              selectionMap += "browserName" -> browserjson.getJSONObject(0).get("browser")
              selectionMap += "browserVersion" -> browserjson.getJSONObject(0).get("version")
              selectionMap += "country" -> rs.getString(EntHostProperties.COUNTRY.toString)
              selectionMap += "city" -> rs.getString(EntHostProperties.CITY.toString)
              selectionMap += "os" -> rs.getString(EntHostProperties.OS.toString)
              selectionMap += "primaryUserId" -> primaryUser
              selectionMap += "dateTime" -> rs.getString(EntHostProperties.DATE_TIME.toString)
              val category = rs.getString(EntHostProperties.CATEGORY.toString)
              //TODO: remove all these special casing from this file
              if ("PAN_FW".equalsIgnoreCase(category)) {
                selectionMap += "panUserId" -> primaryUser
              }

              //TODO: Once the backend sends Risk as "Double", this should be changed to getDouble()
              val risk = rs.getString(EntHostProperties.RISK.toString)
              if (risk != null) {
                //Risk should be Double.
                selectionMap += "risk" -> risk.toDouble
              }
              selectionMap += "ipAddress" -> ip
              if (ipToEntityInfoMap.contains(ip)) {
                selectionMap += "macAddress" -> ipToEntityInfoMap(ip).getMacAddress
                selectionMap += "hostName" -> hostName
              }
            }
          }
        }
      }

    } catch {
      case ex: Exception => Logger.error(
        "Failed to get host entity properties for a list of ips: " + inputIpList +
          " for date time: " + time + ": " + ex)
    } finally {
      closeConnections(conn)
    }
    buf
  }

  def getEntHostPropsByIpDirect(ip_address: String, time: String): collection.mutable.ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = EntHostProperties.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val sqlStr = "SELECT " + EntHostProperties.columns + " FROM " + tableName + " WHERE IP_ADDRESSES LIKE ? AND DATE_TIME = ?"
      Logger.debug("sqlStr: " + sqlStr)
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, "%" + ip_address + "%")
      pstmt.setString(2, time)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        val browserjson: org.json.JSONArray = new org.json.JSONArray(selectionMap.get("browsers").get.toString)
        val hostjson: org.json.JSONArray = new org.json.JSONArray(selectionMap.get("hostNames").get.toString)
        selectionMap += "browserName" -> browserjson.getJSONObject(0).get("browser")
        selectionMap += "browserVersion" -> browserjson.getJSONObject(0).get("version")
        selectionMap += "hostName" -> hostjson.getString(0)
        val category = rs.getString(EntHostProperties.CATEGORY.toString)
        //TODO: remove all these special casing from this file
        if(category.equalsIgnoreCase("PAN_FW")){
          selectionMap += "panUserId" -> rs.getString(EntHostProperties.PRIMARY_USERID.toString)
        }
        buf += selectionMap
      }
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get host entity properties for ip => " + ip_address + " and time =>" + time + " => " + ex)
    } finally {
      closeConnections(conn)
    }
    Logger.debug("buf: " + buf.toString)
    buf
  }

  /**
   * Takes a string in the form of ["value1", "value2", "value3", ...] and returns an array with value1, value2, value3, etc
   */
  def getArrayFromPropertyList(bracketedList: String): Array[String] = {
    if (bracketedList.isEmpty) {
      new Array[String](0)
    } else {
      var trimmedList = bracketedList.replaceAll("\\[", "")
      trimmedList = trimmedList.replaceAll("\\]", "")
      trimmedList = trimmedList.replaceAll("\"", "")
      val values: Array[String] = trimmedList.split(",")
      values
    }
  }

}

object HostEntityPropertiesDao {}
