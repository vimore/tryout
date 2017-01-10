package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util
import java.util.Date
import com.e8.sparkle.commons.hbase.HBaseClient
import com.e8.sparkle.storage.hbase.rowobjects.Entity
import com.e8.sparkle.storage.hbase.tableobjects.{BaseTable, EntityTable}
import com.securityx.modelfeature.common.EntityInfo
import com.securityx.modelfeature.utils.{HBaseAccessConfiguration, EntityFusionHourlyRollUp}
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, EntityFusionConfiguration}
import org.apache.hadoop.hbase.client.Table
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.util.control.Breaks
import scala.collection.JavaConversions._

/**
 * Created by harish on 1/13/15.
 */
class EntityFusionHourlyRollUpDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[EntityFusionHourlyRollUpDao])
  private val hBaseConfig = new HBaseAccessConfiguration(conf)


  /**
   * Queries EntityFusion table to get the EntityInfo for a specified ip/username/hostname/macAddress
   * The entityFusion data is queried from (endtime - 48 hours)  to endtime
   * @param columnName Name of the EntityFusionHourlySummary column to query on
   * @param value String value of the column to query for
   * @param dateTime String dateTime which is the endTime of the query. Starttime to be used is (endTime - 48 hours)
   * @return
   */
  private def getEntityInfoFromFusion(columnName: String, value: String, dateTime: String): EntityInfo = {
    if(columnName == null || columnName.isEmpty ||
      ( !columnName.equals(EntityFusionHourlyRollUp.IP_ADDRESS.toString)
        && !columnName.equals(EntityFusionHourlyRollUp.HOST_NAME.toString)
        && !columnName.equals(EntityFusionHourlyRollUp.MAC_ADDRESS.toString)
        && !columnName.equals(EntityFusionHourlyRollUp.USER_NAME.toString)) )  {
      Logger.error("Invalid Column-name used for entity-fusion " + columnName)
      return null
    }
    var result: EntityInfo = null
    val tableName = EntityFusionHourlyRollUp.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val newStartTime = getEntityFusionBackOffTime(dateTime)
      val sqlStr = "SELECT " + EntityFusionHourlyRollUp.columns + " FROM " + tableName + " WHERE " +
        columnName + " = ? AND " + EntityFusionHourlyRollUp.FUSION_TIME + " >= ? AND " +
        EntityFusionHourlyRollUp.FUSION_TIME + " <= ? " +
        " ORDER BY " + EntityFusionHourlyRollUp.FUSION_TIME + " ASC "
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, value)
      pstmt.setString(2, newStartTime.toString)
      pstmt.setString(3, dateTime)
      rs = executeQuery(pstmt)
      var dateTimeStr = ""
      var ipAddress = ""
      var hostName = ""
      var userName = ""
      var macAddress = ""

      //find the lastest known hostName,
      // latest known username and latest known macAddress for the given IP
      while (rs.next()) {
        val date = rs.getString(EntityFusionHourlyRollUp.FUSION_TIME.toString)
        val ip = rs.getString(EntityFusionHourlyRollUp.IP_ADDRESS.toString)
        val mac = rs.getString(EntityFusionHourlyRollUp.MAC_ADDRESS.toString)
        val host = rs.getString(EntityFusionHourlyRollUp.HOST_NAME.toString)
        val user = rs.getString(EntityFusionHourlyRollUp.USER_NAME.toString)
        dateTimeStr =  if(date != null && date.nonEmpty) date else dateTime
        macAddress =  if(mac != null && mac.nonEmpty) mac else macAddress
        hostName =  if(host != null && host.nonEmpty) host else hostName
        userName =  if(user != null && user.nonEmpty) user else userName
        ipAddress = if(ip != null && ip.nonEmpty) ip else ipAddress
      }
      result = new EntityInfo(dateTimeStr, ipAddress, macAddress, hostName, userName)
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get entity properties from entity fusion hourly summary for " + columnName + " : " + value +
          " and date_time: " + dateTime + ": " + ex.getMessage, ex)
    } finally {
      closeConnections(conn)
    }
    result
  }

  private def getEntityInfoFromFusionHBase(columnName: String, value: String, dateTime: String): EntityInfo = {
    if(columnName == null || columnName.isEmpty ||
      ( !columnName.equals(Entity.IP_ADDRESS_FIELD)
        && !columnName.equals(Entity.HOST_NAME_FIELD)
        && !columnName.equals(Entity.MAC_ADDRESS_FIELD)
        && !columnName.equals(Entity.USER_NAME_FIELD)) )  {
      Logger.error("Invalid Column-name used for entity-fusion " + columnName)
      return null
    }
    val entityTable = new EntityTable
    val hBaseConn = hBaseConfig.getConnection
    var table: Table = null
    var result: EntityInfo = null
    try {
      table = entityTable.getTable(hBaseConn, hBaseConfig)
      val newStartTime = getEntityFusionBackOffTime(dateTime)
      val query = HBaseAccessConfiguration.constructEqualQuery(newStartTime, dateTime, columnName, value)
      val rows = entityTable.getRows(table, query, hBaseConn, false, hBaseConfig)
      var dateTimeStr = ""
      var ipAddress = ""
      var hostName = ""
      var userName = ""
      var macAddress = ""

      for (row <- rows.toList) {
        row match {
          case e: Entity =>
            val date = EntityFusionConfiguration.dateToString(new Date(e.getFusionTime))
            if (date != null && date.nonEmpty && date > dateTimeStr) {
              val ip = e.getIpAddress
              val mac = e.getMacAddress
              val host = e.getHostName
              val user = e.getUserName
              dateTimeStr = if (date != null && date.nonEmpty) date else dateTime
              macAddress = if (mac != null && mac.nonEmpty) mac else macAddress
              hostName = if (host != null && host.nonEmpty) host else hostName
              userName = if (user != null && user.nonEmpty) user else userName
              ipAddress = if (ip != null && ip.nonEmpty) ip else ipAddress
            }
          case _ =>
            Logger.warn("Unexpected row type in getEntityInfoFromFusionHBase [" + row.getClass.toString + "]")
        }
      }
      result = new EntityInfo(dateTimeStr, ipAddress, macAddress, hostName, userName)
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get entity properties from entity fusion hourly summary for " + columnName + " : " + value +
          " and date_time: " + dateTime + ": " + ex.getMessage, ex)
    } finally {
      hBaseConfig.close(table)
    }
    result
  }

  /**
   * Queries Entity_fusion_hourly_summary and returns the entity details for a given Ip address for a given time
   *
   * @param ip String ipAddress of the entity
   * @param dateTime String dateTime
   * @return EntityInfo object representing entity details (mac, ip, hostname, username)
   */
  def getEntityInfoFromFusionForIp(ip: String, dateTime: String): EntityInfo = {
    if (isBeforeConversionDate(dateTime)) {
      getEntityInfoFromFusion(EntityFusionHourlyRollUp.IP_ADDRESS.toString, ip, dateTime)
    } else {
      getEntityInfoFromFusionHBase(Entity.IP_ADDRESS_FIELD, ip, dateTime)
    }
  }

  /**
   * Queries Entity_fusion_hourly_summary and returns the entity details for a given hostname for a given time
   *
   * @param hostName String hostname of the entity
   * @param dateTime String dateTime
   * @return EntityInfo object representing entity details (mac, ip, hostname, username)
   */
  def getEntityInfoFromFusionForHostName(hostName: String, dateTime: String): EntityInfo = {
    if (isBeforeConversionDate(dateTime)) {
      getEntityInfoFromFusion(EntityFusionHourlyRollUp.HOST_NAME.toString, hostName, dateTime)
    } else {
      getEntityInfoFromFusionHBase(Entity.HOST_NAME_FIELD, hostName, dateTime)
    }
  }


  /**
   * Queries Entity_fusion_hourly_summary and returns the entity details for a given MacAddress for a given time
   *
   * @param macAddress String macAddress of the entity
   * @param dateTime String dateTime
   * @return EntityInfo object representing entity details (mac, ip, hostname, username)
   */
  def getEntityInfoFromFusionForMacAddress(macAddress: String, dateTime: String): EntityInfo = {
    if (isBeforeConversionDate(dateTime)) {
      getEntityInfoFromFusion(EntityFusionHourlyRollUp.MAC_ADDRESS.toString, macAddress, dateTime)
    } else {
      getEntityInfoFromFusionHBase(Entity.MAC_ADDRESS_FIELD, macAddress, dateTime)
    }
  }

  /**
   * Queries Entity_fusion_hourly_summary and returns the entity details for a given username for a given time
   *
   * @param username String ipAddress of the entity
   * @param dateTime String dateTime
   * @return EntityInfo object representing entity details (mac, ip, hostname, username)
   */
  def getEntityInfoFromFusionForUsername(username: String, dateTime: String): EntityInfo = {
    if (isBeforeConversionDate(dateTime)) {
      getEntityInfoFromFusion(EntityFusionHourlyRollUp.USER_NAME.toString, username, dateTime)
    } else {
      getEntityInfoFromFusionHBase(Entity.USER_NAME_FIELD, username, dateTime)
    }
  }

  def isBeforeConversionDate(dateTime: String) = {
    if (dateTime == null) {
      // In theory this should never happen...
      false
    } else {
      val conversionDate = conf.getEntityFusionConfiguration.getConversionDate
      if (conversionDate == null) {
        // If there was no conversion date in the config file, all dates are "before" the conversion date
        true
      } else {
        val date = EntityFusionConfiguration.convertDate(dateTime)
        date.before(conversionDate)
      }
    }
  }

  def getIpToEntityInfoMapForIpList(ipList: String, dateTime: String): MutableMap[String, EntityInfo] = {
    if (isBeforeConversionDate(dateTime)) {
      getIpToEntityInfoMapForIpListPhoenix(ipList, dateTime)
    } else {
      getIpToEntityInfoMapForIpListHBase(ipList, dateTime)
    }
  }

  def getIpToEntityInfoMapForIpListHBase(ipList: String, dateTime: String): MutableMap[String, EntityInfo] = {
    val map = MutableMap[String, EntityInfo]()
    val entityTable = new EntityTable
    val hBaseConn = hBaseConfig.getConnection
    var table: Table = null
    try {
      table = entityTable.getTable(hBaseConn, hBaseConfig)
      val newStartTime = getEntityFusionBackOffTime(dateTime)
      val valuesArray = ipList.split(",")
      for (ip <- valuesArray) {
        // For each ip, query the table.  Not the most efficient answer, but it's what the hbase access layer currently supports
        val query = HBaseAccessConfiguration.constructEqualQuery(newStartTime, dateTime, Entity.IP_ADDRESS_FIELD, ip)
        val rows = entityTable.getRows(table, query, hBaseConn, false, hBaseConfig)
        // Note that we iterate the list in reverse.  This is because the old code gets the list in ascending order - I'm not
        // sure why, that seems wrong. But...
        for (row <- rows.reverse) {
          row match {
            case e: Entity =>
              val date = EntityFusionConfiguration.dateToString(new Date(e.getFusionTime))
              val ipAddress = e.getIpAddress
              val macAddress = e.getMacAddress
              val hostName = e.getHostName
              val userName = e.getUserName
              if (hostName != null || macAddress != null || userName != null) {
                if (!map.contains(ipAddress)) {
                  map += ipAddress -> new EntityInfo(date, ipAddress, macAddress, hostName, userName)
                }else {
                  val entityInfo: EntityInfo = map(ipAddress)
                  //set host name
                  if(hostName != null && hostName.nonEmpty){
                    entityInfo.setHostName(hostName)
                  }

                  //set username
                  if(userName != null && userName.nonEmpty){
                    entityInfo.setUserName(userName)
                  }

                  //set mac address
                  if(macAddress != null && macAddress.nonEmpty){
                    entityInfo.setMacAddress(macAddress)
                  }
                }
              }
            case _ =>
              Logger.warn("Unexpected row type in getIpToEntityInfoMapForIpListHBase [" + row.getClass.toString + "]")
          }
        }
      }
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get IP to entity properties mapping for ips: " + ipList + " and time:" +
          dateTime + ": " + ex.getMessage, ex)
    } finally {
      hBaseConfig.close(table)
    }
    map
  }

  def getIpToEntityInfoMapForIpListPhoenix(ipList: String, dateTime: String): MutableMap[String, EntityInfo] = {
    val map = MutableMap[String, EntityInfo]()
    val tableName = EntityFusionHourlyRollUp.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val newStartTime = getEntityFusionBackOffTime(dateTime)
      val columnName =  EntityFusionHourlyRollUp.IP_ADDRESS.toString
      val valuesArray = ipList.split(",")
      var queryString = ""
      val size = valuesArray.size
      for(i <- 0 to size - 1){
        var str = valuesArray(i).replace("\"", "")
        str = str.trim
        str = columnName + " = '" + str + "'"
        if (queryString.isEmpty) {
          queryString = str
        } else {
          queryString = queryString + " OR " + str
        }

        //querying 10 ips at a time, to avoid query time-out.
        if((i == ( size - 1 )) || ( i != 0 && i % 10 == 0 ) ){

          val sqlStr = "SELECT " + EntityFusionHourlyRollUp.columns + " FROM " + tableName +
            " WHERE " + EntityFusionHourlyRollUp.FUSION_TIME + " >= ? AND " + EntityFusionHourlyRollUp.FUSION_TIME + " <= ? AND " +
            "( " + queryString + " )" +
            " ORDER BY " + EntityFusionHourlyRollUp.IP_ADDRESS + "," + EntityFusionHourlyRollUp.FUSION_TIME + " ASC "
          conn = getConnection(conf)
          pstmt = getPreparedStatement(conn, sqlStr)
          pstmt.setString(1, newStartTime.toString)
          pstmt.setString(2, dateTime)

          rs = executeQuery(pstmt)
          while (rs.next()) {
            val ipAddress = rs.getString(EntityFusionHourlyRollUp.IP_ADDRESS.toString)
            val macAddress = rs.getString(EntityFusionHourlyRollUp.MAC_ADDRESS.toString)
            val hostName = rs.getString(EntityFusionHourlyRollUp.HOST_NAME.toString)
            val userName = rs.getString(EntityFusionHourlyRollUp.USER_NAME.toString)
            val time = rs.getString(EntityFusionHourlyRollUp.FUSION_TIME.toString)

            if(hostName != null || macAddress != null || userName != null){
              if (!map.contains(ipAddress)) {
                map += ipAddress -> new EntityInfo(time, ipAddress, macAddress, hostName, userName)
              }else{
                val entityInfo : EntityInfo = map(ipAddress)
                //set host name
                if(hostName != null && hostName.nonEmpty){
                  entityInfo.setHostName(hostName)
                }

                //set username
                if(userName != null && userName.nonEmpty){
                  entityInfo.setUserName(userName)
                }

                //set mac address
                if(macAddress != null && macAddress.nonEmpty){
                  entityInfo.setMacAddress(macAddress)
                }
              }
            }

          }
          //reset query String
          queryString = ""
        }  //end of if

      }
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get IP to entity properties mapping for ips: " + ipList + " and time:" +
          dateTime + ": " + ex.getMessage, ex)
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    map
  }

  def getIpToEntityInfo(startTime: String, endTime: String): MutableMap[String, ListBuffer[EntityInfo]] = {
    if (isBeforeConversionDate(endTime)) {
      getIpToEntityInfoPhoenix(startTime, endTime)
    } else {
      getIpToEntityInfoHBase(startTime, endTime)
    }
  }

  def getIpToEntityInfoHBase(startTime: String, endTime: String): MutableMap[String, ListBuffer[EntityInfo]] = {
    val map = MutableMap[String, ListBuffer[EntityInfo]]()
    val entityTable = new EntityTable
    val hBaseClient = new HBaseClient
    val hBaseConn = hBaseConfig.getConnection
    var table: Table = null
    try {
      table = entityTable.getTable(hBaseConn, hBaseConfig)
      val newStartTime = getEntityFusionBackOffTime(endTime)
      // We're going to get all the rows between start and end time, and then filter out rows with
      // null macAddress or hostName.
      val query = new com.e8.sparkle.storage.hbase.query.QueryJson
      query.setStartTime(newStartTime)
      query.setEndTime(endTime)
      val rows = entityTable.getRows(table, query, hBaseConn, false, hBaseConfig)
      for (row <- rows) {
        row match {
          case e: Entity =>
            val macAddress = e.getMacAddress
            val hostName = e.getHostName
            if (macAddress != null && !macAddress.isEmpty && hostName != null && !hostName.isEmpty) {
              val date = EntityFusionConfiguration.dateToString(new Date(e.getFusionTime))
              val ipAddress = e.getIpAddress
              val userName = e.getUserName

              var list: ListBuffer[EntityInfo] = if (map.contains(ipAddress)) map(ipAddress) else null
              if (list == null) {
                list = new ListBuffer[EntityInfo]()
                map += ipAddress -> list
              }
              list += new EntityInfo(date, ipAddress, macAddress, hostName, userName)
              // In the new entity fusion world, the entries we find will be host entities, since they
              // are the only ones that have non-null mac addresses and host names.  But they don't
              // generally have user names.  So I'm removing the qualification that we only create
              // and add an EntityInfo if we have a user name.
//              if (userName != null) {
//                list += new EntityInfo(date, ipAddress, macAddress, hostName, userName)
//              }
            }
          case _ =>
            Logger.warn("Unexpected row type in getIpToEntityInfoHBase [" + row.getClass.toString + "]")
        }
      }
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get IP to entity mappings between: " + startTime + " and " +
          endTime + ": " + ex.getMessage, ex)
    } finally {
      hBaseConfig.close(table)
    }
    map
  }
  /**
   * Queries Entity_fusion_hourly_summary and returns a Map of Ip address to List of EntityInfo in sorted order of time
   *
   * @param startTime String dateTime
   * @param endTime String dateTime
   *
   * @return MutableMap[String, ListBuffer[String], which is a Map of IpAddress to EntityInfo sorted in order of time.
   *
   */
  def getIpToEntityInfoPhoenix(startTime: String, endTime: String): MutableMap[String, ListBuffer[EntityInfo]] = {
    val map = MutableMap[String, ListBuffer[EntityInfo]]()
    val tableName = EntityFusionHourlyRollUp.getName(conf)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val newStartTime = getEntityFusionBackOffTime(endTime)

      val sqlStr = "SELECT " + EntityFusionHourlyRollUp.columns + " FROM " + tableName +
        " WHERE " + EntityFusionHourlyRollUp.FUSION_TIME + " >= ? AND " + EntityFusionHourlyRollUp.FUSION_TIME + " < ? " +
        " AND " + EntityFusionHourlyRollUp.HOST_NAME + " IS NOT NULL " + " AND " + EntityFusionHourlyRollUp.MAC_ADDRESS +
        " IS NOT NULL " +
        " ORDER BY " + EntityFusionHourlyRollUp.IP_ADDRESS + "," + EntityFusionHourlyRollUp.FUSION_TIME + " ASC "
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, newStartTime.toString)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      while (rs.next()) {
        val ipAddress = rs.getString(EntityFusionHourlyRollUp.IP_ADDRESS.toString)
        val macAddress = rs.getString(EntityFusionHourlyRollUp.MAC_ADDRESS.toString)
        val hostName = rs.getString(EntityFusionHourlyRollUp.HOST_NAME.toString)
        val userName = rs.getString(EntityFusionHourlyRollUp.USER_NAME.toString)
        val time = rs.getString(EntityFusionHourlyRollUp.FUSION_TIME.toString)
        val dateTime: DateTime = new DateTime(time, DateTimeZone.UTC)

        var list: ListBuffer[EntityInfo] = if (map.contains(ipAddress)) map(ipAddress) else null
        if (list == null) {
          list = new ListBuffer[EntityInfo]()
          map += ipAddress -> list
        }
        if (userName != null) {
          list += new EntityInfo(time, ipAddress, macAddress, hostName, userName)
        }
      }
    } catch {
      case ex: Exception => Logger.error(
        "Failed to get IP to entity mappings between: " + startTime + " and " +
          endTime + ": " + ex.getMessage, ex)
    } finally {
      closeConnections(conn)
    }
    map
  }


  /**
   *
   * Finds the entityInfo for a given ip based on the last known (last known from the input dateTime) information in the db.
   * @param ipToUserNameMap MutableMap[String, ListBuffer[String], which is a Map of IpAddress to EntityInfo sorted in order
   *                        of time.
   * @param ip String ipAddress
   * @param dateTime String specifying datetime
   * @return EntityInfo which represents the entity details
   */
  def getEntityInfoFromIpToUserMap(ipToUserNameMap: MutableMap[String, ListBuffer[EntityInfo]], ip: String,
                                   dateTime: String): EntityInfo = {
    var resultEntity: EntityInfo = null
    if (ipToUserNameMap.contains(ip)) {
      val list: ListBuffer[EntityInfo] = ipToUserNameMap(ip)
      val requiredDateTime: DateTime = new DateTime(dateTime, DateTimeZone.UTC)

      if (list != null || list.nonEmpty) {
        val loop = new Breaks
        loop.breakable {
          var prev: EntityInfo = null

          for (i <- 0 until list.size) {
            val currEntity = list(i)
            val currDateTime = new DateTime(currEntity.getDateTime, DateTimeZone.UTC)
            if (requiredDateTime.equals(currDateTime)) {
              resultEntity = currEntity
              loop.break()
            } else {
              if (prev == null) {
                if (requiredDateTime.isAfter(currDateTime)) {
                  resultEntity = currEntity
                  loop.break()
                }
              } else {
                val prevDateTime = new DateTime(prev.getDateTime, DateTimeZone.UTC)
                if (requiredDateTime.isAfter(prevDateTime) && requiredDateTime.isBefore(currDateTime)) {
                  resultEntity = prev
                  loop.break()
                }
              }
              prev = currEntity
            }
          }
        }
      }
    }

    resultEntity
  }


  private def getEntityFusionBackOffTime(dateTime: String): String = {
    //Going back 48 hours from the endTime.
    val time: DateTime = new DateTime(dateTime, DateTimeZone.UTC)
    time.minusHours(conf.getEntityFusionConfiguration.getBackoffPeriodHours).toString
  }
}
