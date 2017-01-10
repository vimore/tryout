package com.securityx.modelfeature.dao.impala

import java.sql.Connection

import com.securityx.modelfeature.ImpalaClient
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.BaseDao
import com.securityx.modelfeature.utils.MiscUtils
import org.slf4j.{LoggerFactory, Logger}
import scala.collection.mutable
import scala.collection.mutable.{Map => MutableMap, ListBuffer}

/**
 * Created by harish on 12/17/15.
 */
class HostInfoMefDao (conf: FeatureServiceConfiguration) extends BaseDao(conf){
  private final val Logger: Logger = LoggerFactory.getLogger(classOf[HostInfoMefDao])

  def getProcessPropertiesForHost(hostName: String, md5: String, processName: String, startTime: String, endTime: String) = {
    val buf: ListBuffer[MutableMap[String, Any]] = new ListBuffer[MutableMap[String, Any]]()
    val selectionMap = MutableMap[String, Any]()

    var conn: Connection = null
    try {
      val startYMDHString = MiscUtils.getYMDString(startTime)
      val endYMDHString = MiscUtils.getYMDString(endTime)
      val typeMap = MutableMap[String, MutableMap[String, Any]]()
      conn = ImpalaClient.getImpalaConnection(conf)
        val sql = " SELECT processlistenport, processfilepath  FROM e8sec.host_process_mef where deviceHostName = ?" +
        " AND processname = ? and processfilemd5 = ? AND YMDH >= ? AND YMDH < ? "
      val pstmt = conn.prepareStatement(sql)
      pstmt.setString(1, hostName)
      pstmt.setString(2, processName)
      pstmt.setString(3, md5)
      pstmt.setString(4, startYMDHString)
      pstmt.setString(5, endYMDHString)
      val pathSet: mutable.HashSet[String] = new mutable.HashSet[String]
      val portset: mutable.HashSet[String] = new mutable.HashSet[String]
      val rs = pstmt.executeQuery()
      while(rs.next()){
        val path = rs.getString("processfilepath")
        if(path != null && path.nonEmpty){
          pathSet += path
        }
        val port = rs.getString("processlistenport")
        if(port != null && port.nonEmpty){
          portset += port
        }
      }
      val selectionMap = MutableMap[String, Any]()
      selectionMap += "paths" -> pathSet
      selectionMap += "ports" -> portset
      buf += selectionMap

    } catch {
      case e: Exception => Logger.error("Failed to get process properties for endPoint   => " + e)
    } finally {
      closeConnections(conn)
    }
    buf
  }


}
