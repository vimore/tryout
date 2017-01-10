package com.securityx.modelfeature.dao

import java.sql.{Connection, PreparedStatement, ResultSet}
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.TimeSeriesInput
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.solr.{TimeSeriesFacetDao, TimeSeriesSearchDao}

import com.securityx.modelfeature.utils._
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
  * File for fetching the time series data from solr
  * Created by harish on 12/26/14.
  */
class TimeSeriesDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[TimeSeriesDao])
  //private val searchDao: SearchDao = null
  private val facetDao: FacetDao = new FacetDao
  private val timeSeriesSearchDao: TimeSeriesSearchDao = new TimeSeriesSearchDao()
  private val timeSeriesFacetDao: TimeSeriesFacetDao = new TimeSeriesFacetDao()

  /**
   *
   * This is used to support the auto-complete feature for TimeSeries
   *
   * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
   * @param iamSolrClient CloudSolrServer connection for iam_mef collection
   * @param modelId Integer model Id
   * @param fieldType String field type: can be "source" or "destination"
   * @param incomingString String user Input for which we provide auto-complete
   * @param startTime String specifying the startTime
   * @param endTime String specifying the endTime
   * @param pageNo Integer page no.
   * @param pageSize Integer page size
   *
   * @return List of String records as suggestions
   */
  def getAutoCompleteRecords(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                             taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
                             taniumUetSolrClient: CloudSolrServer,
                             modelId: Int, fieldType: String, incomingString: String,
                             startTime: String, endTime: String, pageNo: Int, pageSize: Int, conf: FeatureServiceConfiguration): util.Set[String] = {
    timeSeriesSearchDao.getAutoCompleteRecords(webProxySolrClient, iamSolrClient,
      taniumHostInfoSolrClient,taniumHetSolrClient,taniumUetSolrClient,
        modelId, fieldType, incomingString, startTime, endTime, pageNo,
        pageSize, conf)
  }


  /**
   * Returns the top N facet results from the solr query for the specified input "filed"
   *
   * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
   * @param iamSolrClient CloudSolrServer connection for iam_mef collection
   * @param modelId Integer model Id
   * @param fieldName String field name to facet by
   * @param startTime String specifying the startTime
   * @param endTime String specifying the endTime
   * @param facetMinCount Integer specifying min count for facets
   * @param facetLimit Integer facet limit
   * @param facetStartRows Integer start Row no.
   * @param facetEndRows Integer end Row no.
   *
   * @return
   */
  def getTopN(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
              taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
              taniumUetSolrClient: CloudSolrServer,
              modelId: Int, fieldName: String,
              startTime: String, endTime: String,
              facetMinCount: Int, facetLimit: Int, facetStartRows: Int,
              facetEndRows: Int): util.List[util.Map[String, AnyRef]] = {

    timeSeriesFacetDao.getTopN(webProxySolrClient, iamSolrClient,
      taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,
      modelId, fieldName, startTime, endTime, facetMinCount,
      facetLimit, facetStartRows, facetEndRows)
  }

  /**
   * Returns the top N facet results from the solr query for the specified input "filed"
   *
   * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
   * @param iamSolrClient CloudSolrServer connection for iam_mef collection
   * @param modelId Integer model Id
   * @param fieldType String field type; can be "source" or "destination"
   * @param startTime String specifying the startTime
   * @param endTime String specifying the endTime
   * @param facetMinCount Integer specifying min count for facets
   * @param facetLimit Integer facet limit
   * @param facetStartRows Integer start Row no.
   * @param facetEndRows Integer end Row no.
   *
   * @return
   */
  def getTopNSourceDestination(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                               taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
                               taniumUetSolrClient: CloudSolrServer,
                               modelId: Int, fieldType: String,
                               startTime: String, endTime: String,
                               facetMinCount: Int, facetLimit: Int, facetStartRows: Int,
                               facetEndRows: Int,
                               conf: FeatureServiceConfiguration): util.List[util.Map[String, AnyRef]] = {
    timeSeriesFacetDao.getTopNSourceDestination(webProxySolrClient, iamSolrClient,
      taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,modelId, fieldType, startTime, endTime,
      facetMinCount,
      facetLimit, facetStartRows, facetEndRows, conf)
  }


  /**
   * Returns the top N newly observed domains in the last 30 days from the solr query for the specified input Collection
   *
   * @param webProxySolrClient CloudSolrServer connection for web_proxy_mef collection
   * @param iamSolrClient CloudSolrServer connection for iam_mef collection
   * @param modelId Integer model Id
   * @param startTime String specifying the startTime
   * @param endTime String specifying the endTime
   * @param facetMinCount Integer specifying min count for facets
   * @param facetLimit Integer facet limit
   * @param facetStartRows Integer start Row no.
   * @param facetEndRows Integer end Row no.
   *
   * @return top N newly observed Domain (NOD) with stats (count and %age)
   */
  def getNewlyObservedDomains(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                              modelId: Int,
                              startTime: String, endTime: String,
                              facetMinCount: Int, facetLimit: Int, facetStartRows: Int,
                              facetEndRows: Int): util.List[util.Map[String, AnyRef]] = {
    timeSeriesFacetDao
    .getNODs(webProxySolrClient, iamSolrClient, modelId, startTime, endTime, facetMinCount, facetLimit, facetStartRows,
        facetEndRows, conf)
  }


  def getTypes(modelId: Int, startTime: String, endTime: String) = {
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
      getTypesForAd(startTime, endTime)
    }
  }


  private def getTypesForAd(startTime: String, endTime: String) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = this.getEnvTableName(ADTimeSeries.getName(conf))
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sqlStr = "select DISTINCT " + ADTimeSeries.TYPE + " from " + tableName + " where " + ADTimeSeries.DATE_TIME +
        " >= ?   and " + ADTimeSeries.DATE_TIME + "< ?"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => LOGGER.error("Exception while querying Ad Time Series Types: " + ex)
    } finally {
      closeConnections(conn,pstmt,rs)
    }
    buf
  }


  def getFacetHierarchy(modelId: Int, startTime: String, endTime: String, cache: FeatureServiceCache) = {
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
      getFacetHierarchyForAdSeries(startTime, endTime, cache).values
    } else if (Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
      getFacetHierarchyForHttpSeries(startTime, endTime, cache).values
    } else if(Constants.TaniumModelTuple._1.equals(modelId)){
      getFacetHierarchyForTaniumSeries(startTime, endTime, cache).values
    }
  }

  def getTimeSeries(modelId: Int, typeField: String, startTime: String, endTime: String, cache: FeatureServiceCache) = {
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
      getTimeSeriesForAd(typeField, startTime, endTime, cache)
    } else if (Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
      getTimeSeriesForHttp(typeField, startTime, endTime, cache)
    }
  }

  private def getTimeSeriesForHttp(typeField: String, startTime: String, endTime: String, cache: FeatureServiceCache) = {

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    if (typeField != null && cache.getDescribeLoader().httpCategoryToFieldName.containsKey(typeField)) {
      val typeName = cache.getDescribeLoader().httpCategoryToFieldName.get(typeField)
      val tableName = this.getEnvTableName(HttpTimeSeries.getName(conf))
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)
        val sqlStr =
          "select date_time, type, group_field, bits_In_per_second, bits_Out_per_second, connections_per_second  from " +
            tableName +
            " where date_time >= ? and date_time < ?  and type = ?  order by date_time asc"
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, typeName)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          buf += selectionMap
        }
      } catch {
        case ex: Exception => LOGGER.error("Exception while querying Ad Time Series Types: " + ex)
      } finally {
        closeConnections(conn,pstmt,rs)
      }

    }

    buf
  }

  private def getTimeSeriesForAd(typeField: String, startTime: String, endTime: String, cache: FeatureServiceCache) = {
    var typeName: String = typeField
    if (typeField.equalsIgnoreCase("error") || typeField.equalsIgnoreCase("Error Status") ||
      typeField.equalsIgnoreCase("error_description")) {
      typeName = "error_description"
    } else if (typeField.equalsIgnoreCase("Events") || typeField.equalsIgnoreCase("Event") ||
      typeField.equalsIgnoreCase("event_id")) {
      typeName = "event_id"
    } else {
      typeName = "all"
    }

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = this.getEnvTableName(ADTimeSeries.getName(conf))
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)
      val sqlStr = "select date_time, type, group_field, source_user_count, destination_user_count, event_count from " +
        tableName +
        " where date_time >= ? and date_time < ?  and type = ?  order by date_time asc"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setString(3, typeName)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception => LOGGER.error("Exception while querying Ad Time Series Types: " + ex)
    } finally {
      closeConnections(conn,pstmt,rs)
    }
    buf
  }


  def getTimeSeriesTypeGroup(modelId: Int, startTime: String, endTime: String,
                             typeField: String, group: String, groupField: String, cache: FeatureServiceCache) = {
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
      getTimeSeriesTypeGroupforAd(startTime, endTime, typeField, group, groupField, cache)
    } else if (Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
      getTimeSeriesTypeGroupforHttp(startTime, endTime, typeField, group, groupField, cache)
    }else if (Constants.TaniumModelTuple._1.equals(modelId) || Constants.EndpointGlobalModelTuple._1.equals(modelId) || Constants.EndpointLocalModelTuple._1.equals(modelId)) {
      getTimeSeriesForTanium(modelId, startTime, endTime, typeField, group, groupField, cache)
    } else if (Constants.EndpointGlobalModelTuple._1.equals(modelId) || Constants.EndpointLocalModelTuple._1.equals(modelId)) {
      LOGGER.error("Need to handle global vs local endpoints")
    }
  }

  private def getTimeSeriesTypeGroupforHttp(startTime: String, endTime: String,
                                            typeField: String, group: String, groupField: String,
                                            cache: FeatureServiceCache) = {
    //example: typeField = "Http Response Code", group = "2XX", groupField="201"
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    if (typeField != null) {
      val tableName = this.getEnvTableName(HttpTimeSeries.getName(conf))
      var sqlStr = ""
      var typeString = typeField
      if (groupField == null) {
        if (group != null) {
          //get all the corresponding groupIds from given group
          val localization = cache.getDescribeLoader().localizationMap
          val typeToGroupFieldToGroup = scala.collection.JavaConversions
                                        .mapAsScalaMap(cache.getDescribeLoader().httpFieldToGroupToSubcategory)
          var inOperatorString = ""
          //get map of groupID to groupfield
          val map: MutableMap[String, Integer] = if (typeToGroupFieldToGroup.contains(typeString)) {
            scala.collection.JavaConversions.mapAsScalaMap(typeToGroupFieldToGroup(typeString))
          } else {
            null
          }

          //GroupIds are dynamic for responseContentType and requestScheme. We don't store any groupId for these in config
          // file: timeSeries.yml.
          //so the "map" here will be null.
          if (map != null) {
            for ((k: String, v: Integer) <- map) {
              if (localization.containsKey(v)) {
                val groupName = localization.get(v)
                if (groupName.equals(group)) {
                  val str = "'" + k + "'"
                  if (inOperatorString.isEmpty) {
                    inOperatorString = str
                  } else {
                    inOperatorString = inOperatorString + "," + str
                  }
                }
              }
            }
            inOperatorString = "(" + inOperatorString + ")"
            sqlStr =
              "select date_time, type, sum(bits_In_per_second) as bits_In_per_second, " +
                "sum(bits_Out_per_second) as bits_Out_per_second, sum(connections_per_second) as connections_per_second from " +
                tableName +
                " where date_time >= ? and date_time < ?  and type = ?  and group_field IN " + inOperatorString +
                " group by date_time, type order by date_time asc"

          } else {
            //This is the case when group can be dynamically obtained from the db.
            //eg: GroupIds are dynamic for types "responseContentType" and "requestScheme".
            sqlStr =
              "select date_time, type, sum(bits_In_per_second) as bits_In_per_second, " +
                "sum(bits_Out_per_second) as bits_Out_per_second, sum(connections_per_second) as connections_per_second from " +
                tableName +
                " where date_time >= ? and date_time < ?  and type = ?  and group_field LIKE '" + group +
                "%' group by date_time, type order by date_time asc"
          }

        } else {
          sqlStr =
            "select date_time, type, sum(bits_In_per_second) as bits_In_per_second, " +
              "sum(bits_Out_per_second) as bits_Out_per_second, sum(connections_per_second) as connections_per_second from " +
              tableName +
              " where date_time >= ? and date_time < ?  and type = ? group by date_time, type order by date_time asc"
        }
      } else {
        if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
          typeString = getTopFieldNameFromType(typeField.toLowerCase(), Constants.WebPeerAnomaliesModelTuple._1)
        }
        val groupSpaceString = groupField.replace(" ", "%20")
        sqlStr =
          "select date_time, type, sum(bits_In_per_second) as bits_In_per_second, " +
            "sum(bits_Out_per_second) as bits_Out_per_second, sum(connections_per_second) as connections_per_second from " +
            tableName +
            " where date_time >= ? and date_time < ?  and type = ? and group_field IN" + " ('" + groupField + "','" +
            groupSpaceString + "') " + " group by date_time, type order by date_time asc"
      }
      LOGGER.debug("Sql String is : " + sqlStr)
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)

        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, typeString)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          buf += selectionMap
        }
      } catch {
        case ex: Exception => LOGGER.error("Exception while querying Ad Time Series: " + ex)
      } finally {
        closeConnections(conn,pstmt,rs)
      }
      LOGGER.debug("Result Size: " + buf.size)

    }
    buf
  }

  private def getTimeSeriesForTanium(modelId: Int, startTime: String, endTime: String,
                                     typeField: String, group: String, groupField: String,
                                     cache: FeatureServiceCache) = {
    val taniumStatsDao: TaniumStatsDao = new TaniumStatsDao(conf)
    taniumStatsDao.getTimeSeriesByTypeValue(modelId, typeField, group, groupField, startTime, endTime)

  }

  //TODO: Should we remove/clean/keep Old implementation of Tanium time series which had CPU USAGE and MEM USAGE?
  private def getTimeSeriesTypeGroupforTanium(startTime: String, endTime: String,
                                            typeField: String, group: String, groupField: String,
                                            cache: FeatureServiceCache) = {
    //example: typeField = "deviceNameOrIp", group = "deviceNameOrIp", groupField="deviceNameOrIp"
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    if (typeField != null) {
      var selectString = "select type, date_time, AVG( " + TaniumTimeSeries.CPU_USAGE + " ) as CPU_USAGE, " +
        "AVG(" + TaniumTimeSeries.MEM_USAGE + ") as MEM_USAGE, AVG( " + TaniumTimeSeries.DISTINCT_USERS + " ) as DISTINCT_USERS " + " from " +
        TaniumTimeSeries.getName(conf) +
        " where date_time >= ? AND date_time < ? "
      val groupByString = " group by type, date_time order by date_time asc"

      var isTypePresent = true
      var sqlStr = new StringBuilder()
      var typeString = typeField
      if (groupField == null && !typeField.equalsIgnoreCase("newlyObserved")) {
        if (group != null) {
            sqlStr = sqlStr.append(selectString).
              append(" AND type = ?  AND group_field = '").append( group ).append("'").
              append(" AND group_field != 'all'").append( groupByString )
        } else {
          selectString = "select  type, date_time, AVG( CPU_USAGE ) as CPU_USAGE, AVG(MEM_USAGE) as MEM_USAGE, " +
          " AVG( DISTINCT_USERS ) as DISTINCT_USERS from TANIUM_TIME_SERIES where date_time >= ?  and date_time < ? " +
            " AND type = ? AND group_field != 'all'  group by  type, date_time order by date_time asc  "
          sqlStr = sqlStr.append(selectString)
        }
      } else {
        if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
          typeString = getTopFieldNameFromType(typeField.toLowerCase, Constants.TaniumModelTuple._1)
          sqlStr.append(selectString).append(" AND type = ? ").
            append(" AND group_field IN").append(" ('").append(groupField).append("') ").
            append(" AND group_field != 'all' ").append(groupByString)
        } else if(typeField.equalsIgnoreCase("newlyObserved")){
          isTypePresent = false
          //filterString is for filterings based on FIELD_NAME in HBASE TABLE - NEWLY_OBSERVED
          val filterStringBuilder = new StringBuilder()
          if (groupField != null) {
            val fieldNames = groupField.split(",")
            fieldNames.foreach { fieldName =>
              if(filterStringBuilder.isEmpty){
                filterStringBuilder.append(" AND (").append(NewlyObserved.FIELD_NAME).append(" =  '").append(fieldName.trim).append("' ")
              } else{
                filterStringBuilder.append(" OR ").append(NewlyObserved.FIELD_NAME).append(" =  '").append(fieldName.trim).append("' ")
              }

            }
            if(filterStringBuilder.nonEmpty){
              filterStringBuilder.append(" ) ")
            }
          }
          var s = ""
          if(group == null){
            s = "select " + NewlyObserved.DATE_TIME + " , " + NewlyObserved.FIELD_NAME + ", " +
              " SUM( " + NewlyObserved.COUNT + " ) AS COUNT" +
              " from  " + NewlyObserved.getName(conf) +
              " where " + NewlyObserved.DATE_TIME + " >= ? "  + " AND  " + NewlyObserved.DATE_TIME + " < ? " +
              " AND " + NewlyObserved.TYPE  + " IS NOT NULL " +
            filterStringBuilder +
              " GROUP BY " + NewlyObserved.DATE_TIME + " , " +  NewlyObserved.FIELD_NAME
          } else{
            s = "select " + NewlyObserved.DATE_TIME + " , " + NewlyObserved.FIELD_NAME + ", " +
              " SUM( " + NewlyObserved.COUNT + " ) AS COUNT" +
              " from  " + NewlyObserved.getName(conf) +
              " where " + NewlyObserved.DATE_TIME + " >= ? "  + " AND  " + NewlyObserved.DATE_TIME + " < ? " +
              " AND " + NewlyObserved.TYPE  + " =  '" + group + "' " +
              filterStringBuilder +
              " GROUP BY " + NewlyObserved.DATE_TIME + " , " +  NewlyObserved.FIELD_NAME
          }
          sqlStr.append(s)
        }else if(groupField.equals("all")) {
          //TODO: change this query. Right now, it is hardcoded to have type = deviceNameOrIp
          selectString = "select  type, date_time, CPU_USAGE,  MEM_USAGE, " +
            "  DISTINCT_USERS from TANIUM_TIME_SERIES where date_time >= ?  and date_time < ? " +
            " AND type = 'deviceNameOrIp' AND group_field != 'all' order by date_time asc  "
          sqlStr.append(selectString)
          isTypePresent = false
        }

      }
      LOGGER.debug("Sql String is : " + sqlStr)
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {
        conn = getConnection(conf)

        pstmt = getPreparedStatement(conn, sqlStr.toString())
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        if(isTypePresent) {
          pstmt.setString(3, typeString)
        }
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        val map : MutableMap[String, MutableMap[String, Any]] = MutableMap[String, MutableMap[String, Any]]()
        while (rs.next()) {
          val dateTime = rs.getString(NewlyObserved.DATE_TIME.toString)
          var selectionMap = MutableMap[String, Any]()
          if(typeField.equalsIgnoreCase("newlyObserved")){
            val count = rs.getInt("COUNT")
            val fieldName = rs.getString(NewlyObserved.FIELD_NAME.toString)
            var processCount = 0
            var portCount = 0
            var md5Count = 0
              if(map.contains(dateTime)) {
                  selectionMap = map(dateTime)
              } else{
                map += dateTime -> selectionMap
                selectionMap += "dateTime" -> dateTime
                buf += selectionMap
              }
            if(fieldName.toLowerCase.contains("port")){
              selectionMap += "portCount" -> count
            } else if(fieldName.toLowerCase.contains("process")){
              selectionMap += "processCount" -> count
            } else if(fieldName.toLowerCase.contains("md5")){
              selectionMap += "md5Count" -> count
            }

          }else {
            appendToMap(rs, rsMeta, selectionMap)
            buf += selectionMap
          }
        }
      } catch {
        case ex: Exception => LOGGER.error("Exception while querying Ad Time Series: " + ex)
      } finally {
        closeConnections(conn,pstmt,rs)
      }
      LOGGER.debug("Result Size: " + buf.size)
    }
    buf
  }

  private def getTimeSeriesTypeGroupforAd(startTime: String, endTime: String,
                                          typeField: String, group: String, groupField: String, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var sqlStr = ""
    val tableName = this.getEnvTableName(ADTimeSeries.getName(conf))
    var typeName: String = typeField
    var inOperatorString: String = ""
    if (typeField.equalsIgnoreCase("error") || typeField.equalsIgnoreCase("Error Status") ||
      typeField.equalsIgnoreCase("error_description")) {
      typeName = "error_description"
      if (groupField == null) {
        if (group != null) {
          if (group.equalsIgnoreCase("success")) {
            sqlStr =
              "select date_time, type, sum(source_user_count) as source_user_count, " +
                "sum(destination_user_count) as destination_user_count, sum(event_count) as event_count from " +
                tableName +
                " where date_time >= ? and date_time < ?  and type = ?  and group_field like 'Success%' group by date_time, " +
                "type order by date_time asc"
          } else if (group.equalsIgnoreCase("failure")) {
            sqlStr =
              "select date_time, type, sum(source_user_count) as source_user_count, " +
                "sum(destination_user_count) as destination_user_count, sum(event_count) as event_count from " +
                tableName +
                " where date_time >= ? and date_time < ?  and type = ?  and group_field like 'Failure%' group by date_time, " +
                "type order by date_time asc "
          }
        } else {
          sqlStr =
            "select date_time, type, sum(source_user_count) as source_user_count, " +
              "sum(destination_user_count) as destination_user_count, sum(event_count) as event_count from " +
              tableName +
              " where date_time >= ? and date_time < ?  and type = ? group by date_time, type order by date_time asc "
        }
      } else {
        val isInt = groupField.forall(_.isDigit)
        var groupFieldName = groupField
        if (isInt) {
          groupFieldName = cache.getDescribeLoader().localizationMap.get(groupField.toInt)
        }
        sqlStr = "select date_time, type, group_field, source_user_count, destination_user_count, event_count from " +
          tableName +
          " where date_time >= ? and date_time < ?  and type = ?  and group_field like '%" + groupFieldName +
          "%' order by date_time asc"
      }

    } else if (typeField.equalsIgnoreCase("Events") || typeField.equalsIgnoreCase("Event") ||
      typeField.equalsIgnoreCase("event_id")) {
      typeName = "event_id"
      if (groupField == null) {
        if (group != null) {
          val catToEvent: util.Map[String, util.List[Integer]] = cache.describeLoader.categoryToEvent
          if (catToEvent.containsKey(group)) {
            val list: util.List[Integer] = catToEvent.get(group)
            for (i <- 0 to list.size() - 1) {
              if (inOperatorString.equals("")) {
                inOperatorString = "'" + list.get(i) + "'"
              } else {
                inOperatorString = inOperatorString + "," + "'" + list.get(i) + "'"
              }
            }
            inOperatorString = "(" + inOperatorString + ")"
            sqlStr =
              "select date_time, type, sum(source_user_count) as source_user_count, " +
                "sum(destination_user_count) as destination_user_count, sum(event_count) as event_count from " +
                tableName +
                " where date_time >= ? and date_time < ?  and type = ?  and group_field In " + inOperatorString +
                " group by date_time, type order by date_time asc "
          }
        } else {
          sqlStr =
            "select date_time, type, sum(source_user_count) as source_user_count, " +
              "sum(destination_user_count) as destination_user_count, sum(event_count) as event_count from " +
              tableName +
              " where date_time >= ? and date_time < ?  and type = ? group by date_time, type order by date_time asc"
        }
      } else {
        sqlStr = "select date_time, type, group_field, source_user_count, destination_user_count, event_count from " +
          tableName +
          " where date_time >= ? and date_time < ?  and type = ?  and group_field = '" + groupField + "' order by date_time asc"
      }

    } else if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topdestination")) {
      typeName = getTopFieldNameFromType(typeField.toLowerCase(), Constants.ADPeerAnomaliesModelTuple._1)
      sqlStr = "select date_time, type, group_field, source_user_count, destination_user_count, event_count from " + tableName +
        " where date_time >= ? and date_time < ?  and type = ?  and group_field like '%" + groupField +
        "%' order by date_time asc"
    } else {
      typeName = "all"
      sqlStr = "select date_time, type, group_field, source_user_count, destination_user_count, event_count from " + tableName +
        " where date_time >= ? and date_time < ?  and type = ?  and group_field = 'all'" + " order by date_time asc"
    }


    LOGGER.debug("Sql String is : " + sqlStr)
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      conn = getConnection(conf)

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      pstmt.setString(3, typeName)
      rs = executeQuery(pstmt)
      val rsMeta = getResultSetMetaData(rs)
      while (rs.next()) {
        val selectionMap = MutableMap[String, Any]()
        appendToMap(rs, rsMeta, selectionMap)
        buf += selectionMap
      }
    } catch {
      case ex: Exception =>  LOGGER.error("Exception while querying Ad Time Series: " + ex, ex)
    } finally {
      closeConnections(conn,pstmt,rs)
    }
    LOGGER.debug("Result Size: " + buf.size)
    buf

  }

  private def getTopFieldNameFromType(typeField: String, modelId: Int): String = {
    var fieldName: String = null

    //get fieldname from fieldType
    if (Constants.ADPeerAnomaliesModelTuple._1 == modelId) {
      if (typeField.contains("source")) {
        fieldName = "source_user"
      }
      else {
        fieldName = "destination_user"
      }
    }
    else if (Constants.WebPeerAnomaliesModelTuple._1 == modelId){
      if (typeField.contains("source")) {
        fieldName = "sourceNameOrIp"
      }
      else {
        fieldName = "destinationNameOrIp"
      }
    } else if(Constants.TaniumModelTuple._1 == modelId || Constants.EndpointGlobalModelTuple._1 == modelId || Constants.EndpointLocalModelTuple._1 == modelId) {
      fieldName = "deviceNameOrIp"
    }
    fieldName
  }

  /**
   * Solr search for time series data
   * @param webProxySolrClient
   * @param iamSolrClient
   * @param taniumHostInfoSolrClient
   * @param taniumHetSolrClient
   * @param taniumUetSolrClient
   * @param input
   * @param cache
   * @return
   */
  def getTimeSeriesSearchResults(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                                 taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
                                 taniumUetSolrClient: CloudSolrServer,
                                 input: TimeSeriesInput, cache: FeatureServiceCache) = {
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(input.getModelId)) {
      getTimeSeriesSearchResultsForAd(webProxySolrClient, iamSolrClient,
        taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, input, cache)
    } else if (Constants.WebPeerAnomaliesModelTuple._1.equals(input.getModelId)) {
      getTimeSeriesSearchResultsForHttpSeries(webProxySolrClient, iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, input, cache)
    } else if (Constants.TaniumModelTuple._1.equals(input.getModelId) || Constants.EndpointGlobalModelTuple._1.equals(input.getModelId) ||
        Constants.EndpointLocalModelTuple._1.equals(input.getModelId)) {
      getTimeSeriesSearchResultsForTaniumSeries(input)
    }
  }

  private def getTimeSeriesSearchResultsForHttpSeries(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                                                      taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
                                                      taniumUetSolrClient: CloudSolrServer,
                                                      input: TimeSeriesInput, cache: FeatureServiceCache) = {

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var solrQueryString = ""
    val startTime = input.getStartTime
    val endTime = input.getEndTime
    val modelId = input.getModelId
    val searchForList: util.List[util.Map[String, String]] = input.getFacets
    for (i <- 0 to searchForList.size() - 1) {
      val map = searchForList.get(i)
      val typeField = map.get("typeField")
      val group = map.get("group")
      val groupField = map.get("groupId")
      var queryString = ""
      if (typeField != null) {
        val typeString = typeField
        if(typeField.equalsIgnoreCase("responseContentType") || typeField.equalsIgnoreCase("requestScheme")) {
           if(groupField == null){
             queryString = typeString + ":*"
           }else{
             queryString = typeString + ":( *" + groupField + "* )"
           }
        }else if (groupField == null) {
          if (group != null) {
            //get all the corresponding groupIds from given group
            val localization = cache.getDescribeLoader().localizationMap
            val typeToGroupFieldToGroup = scala.collection.JavaConversions
                                          .mapAsScalaMap(cache.getDescribeLoader().httpFieldToGroupToSubcategory)
            var orOperatorString = ""
            //get map of groupID to groupfield
            val map: MutableMap[String, Integer] = scala.collection.JavaConversions
                                                   .mapAsScalaMap(typeToGroupFieldToGroup(typeString))

            for ((k: String, v: Integer) <- map) {
              if (localization.containsKey(v)) {
                val groupName = localization.get(v)
                if (groupName.equals(group)) {
                  val str = "\"" + k + "\"'"
                  if (orOperatorString.isEmpty) {
                    orOperatorString = str
                  } else {
                    orOperatorString = orOperatorString + " OR " + str
                  }
                }
              }
            }
            queryString = typeString + ":(" + orOperatorString + ")"
          } else {
            queryString = typeString + ":*"
          }
        } else {
          if (typeString.equalsIgnoreCase("all") && groupField.equalsIgnoreCase("all")) {
            queryString = "*:*"
          } else if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
            val solrfield = SearchUtils.getSrcDestFieldName(Constants.WebPeerAnomaliesModelTuple._1, typeField.toLowerCase())
            queryString = solrfield + ":*" + groupField + "*"
          } else {
            queryString = typeString + ":" + "\"" + groupField + "\""
          }
        }
        if (queryString.nonEmpty) {
          if (solrQueryString.isEmpty) {
            solrQueryString = "( " + queryString + " ) "
          } else {
            solrQueryString = "" + solrQueryString + " OR (" + queryString + ")"
          }
        }

      }
    }

    LOGGER.debug(" SOLR QUERY is: " + solrQueryString)
    timeSeriesSearchDao
    .searchFromQuery(webProxySolrClient, iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, modelId, solrQueryString, input.getQueryParams, startTime, endTime,
        input.getNumRows, input.getPageNo, input.getFacetLimit, input.getSortField, input.getSortOrder,
        input.isSummarize, cache)

  }


  private def getTimeSeriesSearchResultsForTaniumSeries(input: TimeSeriesInput) = {
    val taniumStatsDao: TaniumStatsDao = new TaniumStatsDao(conf)
    taniumStatsDao.searchData(input)
  }

  //TODO: clean up
  @deprecated
  private def getTimeSeriesSearchResultsForTaniumSeries(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                                                        taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
                                                        taniumUetSolrClient: CloudSolrServer,
                                                        input: TimeSeriesInput, cache: FeatureServiceCache) = {

    var solrQueryString = ""
    val startTime = input.getStartTime
    val endTime = input.getEndTime
    val modelId = input.getModelId
    val typeString = input.getTypeField
    var solrSearch = true
    var sql = ""
    val searchForList: util.List[util.Map[String, String]] = input.getFacets
    for (i <- 0 to searchForList.size() - 1) {
      val map = searchForList.get(i)
      val typeField = map.get("typeField")
      val group = map.get("group")
      val groupField = map.get("groupId")
      var queryString = ""
      if (typeField != null) {
        val typeString = typeField
        if (groupField == null) {
          if (group != null) {
            queryString = typeString + ":(" + group + ")"
          } else {
            queryString = typeString + ":*"
          }
        } else {
          if (typeString.equalsIgnoreCase("all") && groupField.equalsIgnoreCase("all")) {
            queryString = "*:*"
          } else if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
            val solrfield = SearchUtils.getSrcDestFieldName(Constants.TaniumModelTuple._1, typeField.toLowerCase)
            queryString = solrfield + ":*" + groupField + "*"
          } else {
            queryString = typeString + ":" + "\"" + groupField + "\""
          }
        }
        if (queryString.nonEmpty) {
          if (solrQueryString.isEmpty) {
            solrQueryString = "( " + queryString + " ) "
          } else {
            solrQueryString = "" + solrQueryString + " OR (" + queryString + ")"
          }
        }

      }
    }

    LOGGER.debug(" SOLR QUERY is: " + solrQueryString)
    if(solrQueryString.isEmpty && typeString.equals("newlyObserved")){
      val groupField = input.getGroupId
      val group = input.getGroup
        solrSearch = false

        //filterString is for filterings based on FIELD_NAME in HBASE TABLE - NEWLY_OBSERVED
        val filterStringBuilder = new StringBuilder()
        if (groupField != null) {
          val fieldNames = groupField.split(",")
          fieldNames.foreach { fieldName =>
            if(filterStringBuilder.isEmpty){
              filterStringBuilder.append(" AND (").append(NewlyObserved.FIELD_NAME).append(" =  '").append(fieldName.trim).append("' ")
            } else{
              filterStringBuilder.append(" OR ").append(NewlyObserved.FIELD_NAME).append(" =  '").append(fieldName.trim).append("' ")
            }

          }
          if(filterStringBuilder.nonEmpty){
            filterStringBuilder.append(" ) ")
          }
        }
        if(group == null){
          sql = "select * " +
            " from  " + NewlyObserved.getName(conf) +
            " where " + NewlyObserved.DATE_TIME + " >= ? "  + " AND  " + NewlyObserved.DATE_TIME + " < ? " +
            " AND " + NewlyObserved.TYPE  + " IS NOT NULL " +
            filterStringBuilder +
            " ORDER BY " + NewlyObserved.DATE_TIME + " ASC "
        } else{
          sql = "select *" +
            " from  " + NewlyObserved.getName(conf) +
            " where " + NewlyObserved.DATE_TIME + " >= ? "  + " AND  " + NewlyObserved.DATE_TIME + " < ? " +
            " AND " + NewlyObserved.TYPE  + " =  '" + group + "' " +
            filterStringBuilder +
          " ORDER BY " + NewlyObserved.DATE_TIME + " ASC "
        }
    }
    if(solrSearch) {
      timeSeriesSearchDao
        .searchFromQuery(webProxySolrClient, iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient,
          modelId, solrQueryString, input.getQueryParams, startTime, endTime,
          input.getNumRows, input.getPageNo, input.getFacetLimit, input.getSortField, input.getSortOrder,
          input.isSummarize, cache)
    } else{
      executeSqlQuery(sql, input.getStartTime, input.getEndTime)
    }

  }

  private def getTimeSeriesSearchResultsForAd(webProxySolrClient: CloudSolrServer, iamSolrClient: CloudSolrServer,
                                              taniumHostInfoSolrClient: CloudSolrServer, taniumHetSolrClient: CloudSolrServer,
                                              taniumUetSolrClient: CloudSolrServer,
                                              input: TimeSeriesInput, cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    var solrQueryString = ""
    val startTime = input.getStartTime
    val endTime = input.getEndTime
    val modelId = input.getModelId
    val searchForList: util.List[util.Map[String, String]] = input.getFacets

    for (i <- 0 to searchForList.size() - 1) {
      val map = searchForList.get(i)
      val typeField = map.get("typeField")
      val group = map.get("group")
      val groupField = map.get("groupId")
      val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
      var queryString = ""
      val errorStatusMap = cache.getDescribeLoader().errorStatusMap
      if (typeField.equalsIgnoreCase("error") || typeField.equalsIgnoreCase("Error Status") ||
        typeField.equalsIgnoreCase("error_description")) {
        if (group != null && group.equalsIgnoreCase("success")) {
          queryString = "eventLogType:" + "\"Success Audit\""
        } else {
          if (groupField == null) {
            if (group != null) {
              if (group.equalsIgnoreCase("failure")) {
                //if failure, look at the event id
                queryString = "eventLogType:" + "\"Failure Audit\""
              }
            } else {
              queryString = "eventLogType:" + "(\"Success Audit\"" + " OR " + "\"Failure Audit\")"
            }
          } else {
            if (group != null) {
              if (group.equalsIgnoreCase("failure")) {
                //if failure, look at the event id
                val isInt = groupField.forall(_.isDigit)
                if (isInt) {
                  val buffer: StringBuffer = new StringBuffer()
                  val eventSet: util.Set[Integer] = cache.getDescribeLoader().errorStatusMap.get(groupField.toInt)
                  val iter: util.Iterator[Integer] = eventSet.iterator()
                  val queryBuf: StringBuffer = new StringBuffer()
                  while (iter.hasNext) {
                    val event: Int = iter.next()
                    val queryString = "*" + event + "*"
                    if (queryBuf.length() == 0) {
                      queryBuf.append(queryString)
                    } else {
                      queryBuf.append(" OR " + queryString)
                    }
                  }
                  queryString = "cefSignatureId:(" + queryBuf.toString + ")"
                }

              }
            } else {
              queryString = "eventLogType:" + "(\"Success Audit\"" + " OR " + "\"Failure Audit\")"
            }
          }
        }

      } else if (typeField.equalsIgnoreCase("Events") || typeField.equalsIgnoreCase("Event") ||
        typeField.equalsIgnoreCase("event_id")) {
        val catToEvent: util.Map[String, util.List[Integer]] = cache.describeLoader.categoryToEvent
        if (groupField == null) {
          if (group != null) {
            //if we know the group, we will find the valid cefSignatureIds
            if (catToEvent.containsKey(group)) {
              val list: util.List[Integer] = catToEvent.get(group)
              val queryBuf = new StringBuffer()
              for (i <- 0 to list.size() - 1) {
                val queryString = "*" + list.get(i) + "*"
                if (queryBuf.length() == 0) {
                  queryBuf.append(queryString)
                } else {
                  queryBuf.append(" OR " + queryString)
                }
              }
              queryString = "cefSignatureId:(" + queryBuf.toString + ")"
            }
          } else {
            //we will query for all the cer signature ids.
            val eventSet: util.Set[Integer] = cache.getDescribeLoader().eventIdToNameMap.keySet()
            val iter: util.Iterator[Integer] = eventSet.iterator()
            val queryBuf: StringBuffer = new StringBuffer()
            while (iter.hasNext) {
              val event: Int = iter.next()
              val queryString = "*" + event + "*"
              if (queryBuf.length() == 0) {
                queryBuf.append(queryString)
              } else {
                queryBuf.append(" OR " + queryString)
              }
            }
            queryString = "cefSignatureId:(" + queryBuf.toString + ")"
          }
        } else {
          //groupField = event id. So use the group field directly for cefSignatureQuery
          queryString = "cefSignatureId:" + "*" + groupField + "*"
        }

      } else if (typeField.equalsIgnoreCase("topSource") || typeField.equalsIgnoreCase("topDestination")) {
        val solrfield = SearchUtils.getSrcDestFieldName(Constants.ADPeerAnomaliesModelTuple._1, typeField.toLowerCase())
        queryString = solrfield + ":*" + groupField + "*"
      } else {
        queryString = "*:*"
      }

      if (queryString.nonEmpty) {
        if (solrQueryString.isEmpty) {
          solrQueryString = "( " + queryString + " )"
        } else {
          solrQueryString = solrQueryString + " OR ( " + queryString + " )"
        }
      }

    }

    timeSeriesSearchDao
    .searchFromQuery(webProxySolrClient, iamSolrClient, taniumHostInfoSolrClient, taniumHetSolrClient, taniumUetSolrClient, modelId, solrQueryString, input.getQueryParams, startTime, endTime,
        input.getNumRows, input.getPageNo, input.getFacetLimit, input.getSortField, input.getSortOrder,
        input.isSummarize, cache)
  }

  private def getFacetHierarchyForAdSeries(startTime: String, endTime: String,
                                           cache: FeatureServiceCache): MutableMap[String, ParentJson] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    //map of Type -> Group -> subgroup -> list of events
    var resultMap: MutableMap[String, ParentJson] = MutableMap[String, ParentJson]()
    val tableName = this.getEnvTableName(ADTimeSeries.getName(conf))
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {
      val groupToSubCategory: util.Map[Integer, Integer] = cache.getDescribeLoader().groupIdToSubcategoryIdMap
      val subCategoryToCategory: util.Map[Integer, Integer] = cache.getDescribeLoader().subCategoryToCategoryMap
      val groupLocal: util.Map[Integer, String] = cache.getDescribeLoader().eventIdToNameMap
      val categoryLocal: util.Map[Integer, String] = cache.getDescribeLoader().localizationMap
      conn = getConnection(conf)
      val sqlStr =
        "select type, group_field, Sum(source_user_count) as srcUserCount, Sum(destination_user_count) as destUserCount, " +
          "sum(event_count) as eventCount  from " +
          tableName +
          " where date_time >= ? and date_time < ? group by type, group_field"

      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)

      var allSrcCount = 0
      var allDestCount = 0
      var allEventCount = 0
      while (rs.next()) {

        val typeString: String = rs.getString("type")
        var groupField = rs.getString("group_field")
        val srcUserCount: Int = rs.getInt("srcUserCount")
        val destUserCount: Int = rs.getInt("destUserCount")
        val eventCount: Int = rs.getInt("eventCount")

        if (typeString.equals("event_id")) {
          var outputJson: ParentJson = null
          if (resultMap.contains("Events")) {
            outputJson = resultMap("Events")
          } else {
            outputJson =  ParentJson("Events", 0, 0, new ListBuffer[ChildrenJson])
            resultMap += "Events" -> outputJson
          }

          val groupInt = Integer.parseInt(groupField)
          if (groupToSubCategory.containsKey(groupInt)) {
            val subCategory: Int = groupToSubCategory.get(groupInt)
            val category: Int = subCategoryToCategory.get(subCategory)
            val categoryName: String = categoryLocal.get(category)
            val subcategoryName: String = categoryLocal.get(subCategory)
            val eventName: String = groupLocal.get(groupInt)

            var categories: ListBuffer[ChildrenJson] = outputJson.children

            var categoryFound = false
            categories.foreach { categoryJson =>
              if (categoryJson.name.equals(categoryName)) {
                categoryFound = true
                var subCategoriesJson = categoryJson.children
                var subCategoryFound = false
                subCategoriesJson.foreach { subCategory =>
                  if (subCategory.name.equals(subcategoryName)) {
                    subCategoryFound = true
                    var events: ListBuffer[ChildrenJson] = subCategory.children
                    var eventFound = false
                    events.foreach { e =>
                      if (e.name.equals(eventName)) {
                        eventFound = true
                        e.total = e.total + eventCount
                        e.percent = MiscUtils.getPercentage(e.total, allSrcCount)
                        subCategory.total = subCategory.total + eventCount
                        subCategory.percent = MiscUtils.getPercentage(subCategory.total, allSrcCount)
                      }
                    }
                    if (!eventFound) {
                      val eventJson =  ChildrenJson(eventName, groupInt.toString, eventCount,
                        MiscUtils.getPercentage(eventCount, allSrcCount), "event", categoryName,
                        groupInt.toString, null)
                      events += eventJson
                      subCategory.total = subCategory.total + eventCount
                      subCategory.percent = MiscUtils.getPercentage(subCategory.total, allSrcCount)
                    }

                  }
                }
                if (!subCategoryFound) {
                  var eventJson =  ChildrenJson(eventName, groupInt.toString, eventCount,
                    MiscUtils.getPercentage(eventCount, allSrcCount), "event", categoryName,
                    groupInt.toString, null)
                  subCategoriesJson += eventJson
                }

                categoryJson.total = categoryJson.total + eventCount
                categoryJson.percent = MiscUtils.getPercentage(categoryJson.total, allSrcCount)
              }
            }
            if (!categoryFound) {
              val percent = MiscUtils.getPercentage(eventCount, allSrcCount)
              var categoryJson: ChildrenJson =  ChildrenJson(categoryName, categoryName, eventCount, percent, "Event", null,
                null, new ListBuffer[ChildrenJson])
              categories += categoryJson
              var subCategory: ChildrenJson =  ChildrenJson(subcategoryName, subcategoryName, eventCount, percent, "event",
                categoryName, null, new ListBuffer[ChildrenJson])
              categoryJson.children += subCategory

              var eventJson =  ChildrenJson(eventName, groupInt.toString, eventCount, percent, "event", categoryName,
                groupInt.toString, null)
              subCategory.children += eventJson
            }

            outputJson.total = outputJson.total + eventCount
            outputJson.percent = MiscUtils.getPercentage(outputJson.total, allSrcCount)
          }
        } else if (typeString.equals("error_description")) {
          var eventJson: ParentJson = null
          if (resultMap.contains("Status")) {
            eventJson = resultMap("Status")
          } else {
            //Top level Facet. Start with count = 0, percent = 0
            eventJson =  ParentJson("Status", 0, 0, new ListBuffer[ChildrenJson])
            resultMap += "Status" -> eventJson
          }
          if (groupField.contains("Success Audit") || groupField.contains("Audit Success")) {
            // Group "Success Audit" and "Audit Success" together
            val categories = eventJson.children
            var successFound = false
            categories.foreach { category =>
              if (category.name.equalsIgnoreCase("success")) {
                successFound = true
                category.total = category.total + eventCount
                category.percent = MiscUtils.getPercentage(category.total, allSrcCount)
              }
            }

            if (!successFound) {
              var successSubCatJson =  ChildrenJson("Success", groupField, eventCount,
                MiscUtils.getPercentage(eventCount, allSrcCount),
                "Error", "success", groupField, null)
              categories += successSubCatJson
            }
            eventJson.total = eventJson.total + eventCount
            eventJson.percent = MiscUtils.getPercentage(eventJson.total, allSrcCount)

          } else {
            //Error Event
            val errorStatusNameToId: util.Map[String, Integer] = cache.describeLoader.errorStatusNameToIdMap
            if (groupField.contains("Failure Audit:")) {
              groupField = groupField.substring(groupField.indexOf(":") + 1)
            }
            val errorStatusId: String = if (errorStatusNameToId.get(groupField) != null) {
              Integer.toString(errorStatusNameToId.get(groupField))
            } else if (groupField.equalsIgnoreCase("Failure Audit")) {
              // We will group "Failure Audit" and "Audit Failure" together
              "Audit Failure"
            } else groupField

            val categories = eventJson.children
            var failureFound = false
            categories.foreach { category =>
              if (category.name.equalsIgnoreCase("failure")) {
                failureFound = true
                var failureChildren = category.children
                var failureChildFound = false
                failureChildren.foreach { child =>
                  if (child.id.equals(errorStatusId)) {
                    failureChildFound = true
                    child.total = child.total + eventCount
                    child.percent = MiscUtils.getPercentage(child.total, allSrcCount)
                    category.total = category.total + eventCount
                    category.percent = MiscUtils.getPercentage(category.total, allSrcCount)
                  }

                }
                if (!failureChildFound) {
                  val child =  ChildrenJson(groupField, errorStatusId, eventCount,
                    MiscUtils.getPercentage(eventCount, allSrcCount), "Error", "Failure",
                    errorStatusId, null)
                  failureChildren += child
                  category.total = category.total + eventCount
                  category.percent = MiscUtils.getPercentage(category.total, allSrcCount)
                }


              }
            }
            if (!failureFound) {
              val failureJson =  ChildrenJson("Failure", "Failure", eventCount,
                MiscUtils.getPercentage(eventCount, allSrcCount),
                "Error", "Failure", null, new ListBuffer[ChildrenJson])
              var child =  ChildrenJson(groupField, errorStatusId, eventCount,
                MiscUtils.getPercentage(eventCount, allSrcCount),
                "Error", "Failure", errorStatusId, null)
              failureJson.children += child
              categories += failureJson
            }

            eventJson.total = eventJson.total + eventCount
            eventJson.percent = MiscUtils.getPercentage(eventJson.total, allSrcCount)
          }

        } else if (typeString.equals("all")) {
          resultMap += "all" ->  ParentJson("all", eventCount, 100, null)
          allSrcCount = eventCount
          allDestCount = destUserCount
          allEventCount = srcUserCount
        }

      }

      // Check the totals.  This whole section exists because we can't rely on the "all" row in the database to have
      // the accurate number.
      var all: ParentJson = null
      var total:Double = 0
      resultMap.foreach { child=>
        if (child._1.equals("all")) {
          all = child._2
        } else {
          total += child._2.total
        }
      }
      if (total != all.total) {
        // The total is incorrect.  Fix it up.
        LOGGER.warn("TimeSeriesDao.getFacetHierarchyForAdSeries: Incorrect total for all got [" + all.total + "] expected [" + total + "]")

        // Now recalculate all the percentages
        resultMap.foreach { child =>
          if (child._1.equals("all")) {
            // Reset the "all" total to whatever the sum of the other children was
            child._2.total = total
          } else {
            child._2.percent = MiscUtils.getPercentage(child._2.total, total)
            child._2.children.foreach { child =>
              calculatePercentage(child, total)
            }
          }
        }
      }
    } catch {
      case ex: Exception => LOGGER.error("Failed to fetch facet hierarchy for AD time series => {} " + ex)
    } finally {
      closeConnections(conn,pstmt,rs)
    }
    resultMap

  }

  private def calculatePercentage(obj: ChildrenJson, totalCount: Double):Unit = {
    obj.percent = MiscUtils.getPercentage(obj.total, totalCount)
    if (obj.children != null) {
      obj.children.foreach { child =>
        calculatePercentage(child, totalCount)
      }
    }
  }

  private def getFacetHierarchyForHttpSeries(startTime: String, endTime: String,
                                             cache: FeatureServiceCache): MutableMap[String, ParentJson] = {
    var resultMap: MutableMap[String, ParentJson] = MutableMap[String, ParentJson]()
    val tableName = this.getEnvTableName(HttpTimeSeries.getName(conf))

    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      conn = getConnection(conf)
      val sqlStr =
        "select type, group_field, Sum(BITS_IN_PER_SECOND * PERIOD_SECONDS) as BITS_IN, " +
          "Sum(BITS_OUT_PER_SECOND * PERIOD_SECONDS) as BITS_OUT, sum(CONNECTIONS_PER_SECOND * PERIOD_SECONDS) as CONNECTIONS" +
          "  from " +
          tableName +
          " where date_time >= ? and date_time < ? group by type, group_field order by type, group_field asc"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)

      val typeFieldToCategory = cache.getDescribeLoader().httpFieldNameToCategoryName
      val typeToGroupToSubCategory: util.Map[String, util.Map[String, Integer]] = cache.getDescribeLoader()
                                                                                  .httpFieldToGroupToSubcategory
      val localization: util.Map[Integer, String] = cache.getDescribeLoader().localizationMap
      var allBitsIn: Double = 0
      var allBitsOut: Double = 0
      var allConnections: Double = 0
      while (rs.next()) {
        val typeString = rs.getString("type")
        var groupField = rs.getString("group_field")
        groupField = groupField.replace("%20", " ")
        val bitsIn = rs.getDouble("BITS_IN")
        val bitsOut = rs.getDouble("BITS_OUT")
        val connections = rs.getDouble("CONNECTIONS")

        if (connections > 0) {


          if (typeFieldToCategory.containsKey(typeString)) {
            val categoryName = typeFieldToCategory.get(typeString)

            if (typeString.equalsIgnoreCase("responseContentType") || typeString.equalsIgnoreCase("requestScheme") ||
              typeToGroupToSubCategory.containsKey(typeString)) {
              var subCategoryString = ""
              var groupname = ""
              if (typeString.equalsIgnoreCase("requestScheme")) {
                subCategoryString = groupField
                groupname = groupField

              } else if (typeString.equalsIgnoreCase("responseContentType") && !groupField.isEmpty &&
                groupField.contains("/")) {
                val firstHalf = groupField.substring(0, groupField.indexOf("/"))
                if (!firstHalf.isEmpty) {
                  if (cache.getDescribeLoader().httpRequestSchemeContentTypes.contains(firstHalf.toLowerCase())) {
                    subCategoryString = firstHalf
                    groupname = groupField.substring(groupField.indexOf("/") + 1)
                  } else {
                    subCategoryString = "other"
                    groupname = groupField
                  }

                }

              } else if (typeToGroupToSubCategory.containsKey(typeString)) {
                val groupIdToSubCategory: util.Map[String, Integer] = typeToGroupToSubCategory.get(typeString)
                if (groupIdToSubCategory.containsKey(groupField)) {
                  val subCategoryId: Int = groupIdToSubCategory.get(groupField)
                  subCategoryString = localization.get(subCategoryId)
                  groupname = groupField
                }
              }

              if (!subCategoryString.isEmpty) {
                var children: ListBuffer[ChildrenJson] = null
                if (resultMap.contains(categoryName)) {
                  val categoryJson = resultMap(categoryName)
                  children = resultMap(categoryName).children
                  var subCategoryFound = false
                  children.foreach { child =>
                    if (child.name.equals(subCategoryString)) {
                      subCategoryFound = true
                      var subChildren = child.children
                      var groupFound = false
                      subChildren.foreach { subChild =>
                        if (subChild.name.equals(groupname)) {
                          groupFound = true
                          subChild.total = subChild.total + connections
                          subChild.percent = MiscUtils.getPercentage(subChild.total, allConnections)
                        }
                      }
                      if (!groupFound) {
                        //case class ChildrenJson(var name: String,var id: String, var total: Long,
                        // var percent: Double,var typeField: String, var group: String, groupId: String,
                        // var children: ListBuffer[ChildrenJson] ){
                        var childJson =  ChildrenJson(groupname, groupField, connections,
                          MiscUtils.getPercentage(connections, allConnections), typeString,
                          subCategoryString, groupField, null)
                        subChildren += childJson
                        child.total = child.total + connections
                        child.percent = MiscUtils.getPercentage(child.total, allConnections)
                      }
                    }
                  }
                  if (!subCategoryFound) {
                    val percent = MiscUtils.getPercentage(connections, allConnections)
                    var subCatJson =  ChildrenJson(subCategoryString, subCategoryString, connections, percent, typeString,
                      subCategoryString, null, new ListBuffer[ChildrenJson])
                    children += subCatJson
                    var subChild =  ChildrenJson(groupname, groupname, connections, percent, typeString, subCategoryString,
                      groupField, null)
                    subCatJson.children += subChild
                  }
                  categoryJson.total = categoryJson.total + connections
                  categoryJson.percent = MiscUtils.getPercentage(categoryJson.total, allConnections)
                } else {
                  val percent = MiscUtils.getPercentage(connections, allConnections)
                  val outputJson: ParentJson =  ParentJson(categoryName, connections, percent, new ListBuffer[ChildrenJson])

                  var child: ChildrenJson =  ChildrenJson(subCategoryString, subCategoryString, connections, percent,
                    typeString,
                    subCategoryString, null, new ListBuffer[ChildrenJson])
                  outputJson.children += child

                  var subChild: ChildrenJson =  ChildrenJson(groupname, groupname, connections, percent, typeString,
                    subCategoryString, groupField, null)
                  child.children += subChild

                  resultMap += categoryName -> outputJson
                }
              }

            }
          } else if (typeString.equals("all")) {
            resultMap += "all" ->  ParentJson("all", connections, 100, null)
            allBitsIn = bitsIn
            allBitsOut = bitsOut
            allConnections = connections

          }


        }
      }
    } catch {
      case ex: Exception => LOGGER.error("Exception while querying Ad Time Series Types: " + ex)
    } finally {
      closeConnections(conn,pstmt,rs)
    }
    resultMap
  }

  private def getFacetHierarchyForTaniumSeries(startTime: String, endTime: String,
                                             cache: FeatureServiceCache): MutableMap[String, ParentJson] = {
    var resultMap: MutableMap[String, ParentJson] = MutableMap[String, ParentJson]()
    var countMap: MutableMap[String, Int] = MutableMap[String, Int]()
    var conn: Connection = null
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    try {

      conn = getConnection(conf)
      val sqlStr =
        "select group_field, type, AVG( " + TaniumTimeSeries.CPU_USAGE + " ) as CPU_USAGE, " +
          "AVG(" + TaniumTimeSeries.MEM_USAGE + ") as MEM_USAGE, AVG( " + TaniumTimeSeries.DISTINCT_USERS + " ) as DISTINCT_USERS" +
          " from " +
          TaniumTimeSeries.getName(conf) +
          " where date_time >= ? and date_time < ? group by group_field, type order by type, group_field asc"
      pstmt = getPreparedStatement(conn, sqlStr)
      pstmt.setString(1, startTime)
      pstmt.setString(2, endTime)
      rs = executeQuery(pstmt)

      val typeFieldToCategory = cache.getDescribeLoader().taniumFieldNameToCategoryName
      var allCpuUsage = 0d
      var allMemUsage = 0d
      var allDistinctUserCount = 0d

      while (rs.next()) {
        val typeString = rs.getString(TaniumTimeSeries.TYPE.toString)
        val groupField = rs.getString(TaniumTimeSeries.GROUP_FIELD.toString)
        val cpuUsage = rs.getDouble(TaniumTimeSeries.CPU_USAGE.toString)
        val memUsage = rs.getDouble(TaniumTimeSeries.MEM_USAGE.toString)
        val distinctUserCount = rs.getInt(TaniumTimeSeries.DISTINCT_USERS.toString)

          if (!groupField.equalsIgnoreCase("all") && typeFieldToCategory.containsKey(typeString)) {
            val categoryName = typeFieldToCategory.get(typeString)
                var children: ListBuffer[ChildrenJson] = null
                if (resultMap.contains(categoryName)) {
                  val categoryJson = resultMap(categoryName)
                  children = resultMap(categoryName).children
                  var subCategoryFound = false
                  children.foreach { child =>
                    if (child.name.equals(typeString)) {
                      subCategoryFound = true
                      child.total = child.total + cpuUsage
                      child.percent = MiscUtils.getPercentage(child.total, allCpuUsage)
                    }
                  }
                  if (!subCategoryFound) {
                    val percent = MiscUtils.getPercentage(cpuUsage, allCpuUsage)
                    var subCatJson =  ChildrenJson(typeString, typeString, cpuUsage, percent, typeString,
                      null, null, new ListBuffer[ChildrenJson])
                    children += subCatJson
                  }

                  categoryJson.total = categoryJson.total + cpuUsage
                  categoryJson.percent = MiscUtils.getPercentage(categoryJson.total, allCpuUsage)
                  countMap += categoryName -> (countMap(categoryName) + 1)

                } else {
                  val percent = MiscUtils.getPercentage(cpuUsage, allCpuUsage)
                  val outputJson: ParentJson =  ParentJson(categoryName, cpuUsage, percent, new ListBuffer[ChildrenJson])
                  var child: ChildrenJson =  ChildrenJson(typeString, typeString, cpuUsage, percent,
                    typeString,
                    null, null, null)
                  outputJson.children += child

                  resultMap += categoryName -> outputJson
                  countMap += categoryName -> 1
                }

          } else if (groupField.equalsIgnoreCase("all")) {
            allCpuUsage = cpuUsage
            allMemUsage = memUsage
            allDistinctUserCount = distinctUserCount
            resultMap += "all" ->  ParentJson("all", allCpuUsage, 100, null)

          }

      }

      for((k,v) <- countMap){
        if(!k.equals("all")){
          val json: ParentJson = resultMap(k)
          json.total = json.total / v
          json.percent =  MiscUtils.getPercentage(json.total, allCpuUsage).toInt
          val children : ListBuffer[ChildrenJson] = json.children

          children.foreach{ child =>
            child.total = child.total / v
            child.percent = MiscUtils.getPercentage(child.total, allCpuUsage ).toInt
          }
        }

      }
    } catch {
      case ex: Exception => LOGGER.error("Exception while querying Ad Time Series Types: " + ex)
    } finally {
      closeConnections(conn,pstmt,rs)
    }


    resultMap
  }


  /**
   * get time series for source or destination
   * @param modelId
   * @param startTime
   * @param endTime
   * @param fieldType
   * @param cache
   */
  def getTimeSeriesForTopN(modelId: Int, startTime: String, endTime: String, fieldType: String, value: String,
                           cache: FeatureServiceCache) = {
    if (Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
      getTimeSeriesForTopNForHttpSeries(startTime, endTime, fieldType, value, cache)
    }
  }

  def getTimeSeriesForTopNForHttpSeries(startTime: String, endTime: String, fieldType: String, value: String,
                                        cache: FeatureServiceCache) = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val tableName = this.getEnvTableName(HttpTimeSeries.getName(conf))

    var fieldName = ""
    if (fieldType != null) {
      if (fieldType.equalsIgnoreCase("source")) {
        fieldName = "sourceNameOrIp"
      } else if (fieldType.equalsIgnoreCase("destination")) {
        fieldName = "destinationNameOrIp"
      } else {
        LOGGER.error("Invalid Field Type Found")
      }
    }
    if (!fieldName.isEmpty) {
      var conn: Connection = null
      var pstmt: PreparedStatement = null
      var rs: ResultSet = null
      try {

        conn = getConnection(conf)
        val sqlStr =
          "select date_time, type, group_field, Sum(BITS_IN_PER_SECOND) as BITS_IN_PER_SECOND, " +
            "Sum(BITS_OUT_PER_SECOND) as BITS_OUT_PER_SECOND, sum(CONNECTIONS_PER_SECOND) as CONNECTIONS_PER_SECOND  from " +
            tableName +
            " where date_time >= ? and date_time < ? and type = ? and group_field = ? group by date_time, type, " +
            "group_field order by date_time asc"
        pstmt = getPreparedStatement(conn, sqlStr)
        pstmt.setString(1, startTime)
        pstmt.setString(2, endTime)
        pstmt.setString(3, fieldName)
        pstmt.setString(4, value)
        rs = executeQuery(pstmt)
        val rsMeta = getResultSetMetaData(rs)
        while (rs.next()) {
          val selectionMap = MutableMap[String, Any]()
          appendToMap(rs, rsMeta, selectionMap)
          buf += selectionMap
        }
      } catch {
        case ex: Exception => LOGGER.error("Exception while querying Ad Time Series Types: " + ex)
      } finally {
        closeConnections(conn,pstmt,rs)
      }
    }
    buf
  }
}

case class BaseTimeSeriesEvent(var id: String, var name: String) {

}

class AdEventInfo(id: String, name: String, var srcUserCount: Long, var srcUserCountPercent: Double, var destUserCount: Long,
                  var destUserCountPercent: Double,
                  var eventCount: Long, var eventCountPercent: Double) extends BaseTimeSeriesEvent(id, name) {

}

class HttpEventInfo(id: String, name: String, var bitsIn: Long, var bitsInPercent: Double, var bitsOut: Long,
                    var bitsOutPercent: Double,
                    var connections: Long, var connectionPersont: Double) extends BaseTimeSeriesEvent(id, name) {
}


/**
 * Class representing the Top level Facet (Category) for any time series.
 * eg: For HttpTimeSeries:  "Http Response Code" will be represent by this class
 * For ADTimeSeries: "Event" will be represented by it
 * @param name
 * @param total
 * @param percent
 * @param children
 */
case class ParentJson(var name: String, var total: Double, var percent: Double, var children: ListBuffer[ChildrenJson]) {

}

/**
 * Everything under Top Level Facet will be represented by this class
 * Eg: For HttpTimeSeries: "2XX" will be represented by this class
 * For Ad: "Logon\LogOff" or "Logon" etc will be represented by ChildrenJson Object
 *
 * @param name
 * @param id
 * @param total
 * @param percent
 * @param typeField
 * @param group
 * @param groupId
 * @param children
 */
case class ChildrenJson(var name: String, var id: String, var total: Double, var percent: Double, var typeField: String,
                        var group: String, groupId: String, var children: ListBuffer[ChildrenJson]) {

}
