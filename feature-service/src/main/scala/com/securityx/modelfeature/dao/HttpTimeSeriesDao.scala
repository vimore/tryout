package com.securityx.modelfeature.dao

import java.sql.{ResultSet, Connection, PreparedStatement}

import com.securityx.modelfeature.utils.HttpTimeSeries
import com.securityx.modelfeature.config.FeatureServiceConfiguration

import scala.collection.mutable.{Map => MutableMap}


/**
 * DAO to access http time series table.
 *
 */
class HttpTimeSeriesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  def getHttpTimeSeriesByTimestamps(startTimeStr: String, endTimeStr: String, typeStr: String, groupFieldStr: String,
                                    periodInt: Integer) = {
    //NOTE: Need to refactor common functionality to a separate class. See other DAOs
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val tableName = this.getEnvTableName("HTTP_TIME_SERIES")
      val sqlStr = "SELECT " + HttpTimeSeries.columns + " FROM " + tableName + " WHERE DATE_TIME >= ? " +
        " AND DATE_TIME <= ? AND TYPE = ? AND GROUP_FIELD = ? AND PERIOD_SECONDS = ? " +
        " ORDER BY DATE_TIME"
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setString(3, typeStr)
      pstmt.setString(4, groupFieldStr)
      pstmt.setInt(5, periodInt)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }

  def getTypes(startTimeStr: String, endTimeStr: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = this.getEnvTableName("HTTP_TIME_SERIES")
    var sqlStr = "select DISTINCT(TYPE) from " + tableName + " where DATE_TIME > ? and DATE_TIME < ? "
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }

  def getGroups(startTimeStr: String, endTimeStr: String, typeStr: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = this.getEnvTableName("HTTP_TIME_SERIES")
    var sqlStr = "select DISTINCT(GROUP_FIELD) from " + tableName + " where DATE_TIME > ? AND DATE_TIME < ? and TYPE = ? "
    System.err.println("ERR: " + sqlStr)
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setString(3, typeStr)
      System.err.printf("startTimeStr: %s, endTimeStr: %s, typeStr: %s\n", startTimeStr, endTimeStr, typeStr)
      System.err.println("ERR: " + pstmt.toString())
      rs = executeQuery(pstmt)
      System.err.println("buf size: %d", buf.length)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }

  def getTimeSeriesForAllGroups(startTimeStr: String, endTimeStr: String, typeStr: String, periodSecondsInt: Integer) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    val tableName = this.getEnvTableName("HTTP_TIME_SERIES")
    val sqlStr = "select " +
      " DATE_TIME, TYPE, GROUP_FIELD, PERIOD_SECONDS, BITS_IN_PER_SECOND, BITS_OUT_PER_SECOND, CONNECTIONS_PER_SECOND " +
      " from " + tableName + " where DATE_TIME >= ? and DATE_TIME <= ? and PERIOD_SECONDS = ? and TYPE = ? "
    try {
      conn = getConnection(conf)
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTimeStr)
      pstmt.setString(2, endTimeStr)
      pstmt.setInt(3, periodSecondsInt)
      pstmt.setString(4, typeStr)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } finally {
      closeConnections(conn, pstmt, rs)
    }
    buf
  }
}

object HttpTimeSeriesDao {}
