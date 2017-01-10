package com.securityx.modelfeature.dao

import java.sql.{PreparedStatement, Connection}

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils.{CustomBehavior, TaniumStats}
import org.slf4j.{LoggerFactory, Logger}

import scala.collection.mutable.{Map, ListBuffer}
import scala.collection.mutable.{Map => MutableMap, ListBuffer}

/**
 * Created by harish on 3/9/16.
 */
class CustomBehaviorDao (conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[CustomBehaviorDao])
  final val suppressionDao = new SuppressionDao(conf)

  /**
   * Queries CUSTOM_BEHAVIOR Table for an IpAddress over the given time-range
   *
   * @param ipAddress String representing ipAddress
   * @param startTime String representing startTime
   * @param endTime String representing endTime
   *
   * @return CustomBehavior props for the input ipAddress
   */
  def getCustomBehaviorsForIp( ipAddress: String , startTime: String, endTime: String, behaviorTypes: String) = {
    var result: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
    val resultMap = MutableMap[String, Any]()
    var conn: Connection = null
    var parsedBehaviors: String = null
    if (behaviorTypes != null) if (!behaviorTypes.isEmpty) {
      parsedBehaviors  = behaviorTypes.substring(1, behaviorTypes.length - 1)
      parsedBehaviors = parsedBehaviors.replace("\"","'")
    }
      try
          if (ipAddress != null) if (!suppressionDao.shouldSuppressNoBehaviorInfo(ipAddress, null, null)) {
            conn = getConnection(conf)
            val sqlStr = " SELECT * FROM " + CustomBehavior.getName(conf) + " WHERE " +
              CustomBehavior.DATE_TIME + " >=  ? " +
              " AND " + CustomBehavior.DATE_TIME + " < ? " +
              " AND " + CustomBehavior.IP_ADDRESS + " = ? " +
              " AND " + CustomBehavior.CUSTOM_TYPE + " IN ( " + parsedBehaviors + " ) " +
              "  ORDER BY " + CustomBehavior.DATE_TIME + " ASC "
            val pstmt: PreparedStatement = getPreparedStatement(conn, sqlStr)
            pstmt.setString(1, startTime)
            pstmt.setString(2, endTime)
            pstmt.setString(3, ipAddress)
            val rs = executeQuery(pstmt)
            val rsMeta = rs.getMetaData
            var prevType : String = ""
            var buf: ListBuffer[MutableMap[String, Any]] = null
            while (rs.next()) {
              val selectionMap = MutableMap[String, Any]()
              appendToMap(rs, rsMeta, selectionMap)
              val currType: String = selectionMap.getOrElse("customType","").asInstanceOf[String]
              if(currType != null) if(!currType.equals(prevType)) {
                buf = new ListBuffer[MutableMap[String, Any]]()
              }
              buf += selectionMap
              resultMap += currType -> buf
              prevType = currType
            }
          } else Logger.debug("Cannot query Custom Behavior for null ipAddress")
      catch {
        case e: Exception => Logger.error("Failed to get Custom Behavior props for ip   => " + e)
      } finally closeConnections(conn)

    result += resultMap
  }

}
