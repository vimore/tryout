package com.securityx.modelfeature.queryengine

import java.sql.PreparedStatement
import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, FilterField}
import com.securityx.modelfeature.dao.ColumnMetaData
import com.securityx.modelfeature.utils.SqlUtils

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

object DataTypes extends Enumeration {

  val STRING = Value("String")
  val INTEGER = Value("Int")
  val DOUBLE = Value("Double")
  val TIMESTAMP = Value("TIMESTAMP")
}

/**
 * Created by harish on 2/19/15.
 */
class QueryGenerator {

  //These are column names to be used across all the tables
  private val COLUMN_DATE_TIME: String = "DATE_TIME"
  private val COLUMN_SECURITY_EVENT_ID: String = "SECURITY_EVENT_ID"
  private val COLUMN_MODEL_ID: String = "MODEL_ID"

  //sql operators
  private val AND_STRING = " AND "
  private val OR_STRING = " OR "
  private val LIKE_STRING = " LIKE "
  private val IN_OPERATOR_STRING = " IN "


  /**
   * Takes the input QueryJson and forms a Sql query
   *
   * @param queryJson input json which specifies the startTime, endTime and other filters
   * @param pstmtList  List of params, Types and their Values; in the order they come in the sql query
   * @param cache FeatureServiceCache
    * @return  String Sql query
   */
  def getSqlPredicateString(queryJson: QueryJson, pstmtList: ListBuffer[ColumnMetaData],
                            groupByString: String, aliasMap: MutableMap[String, Any], cache: FeatureServiceCache,
                            isSearchQuery: Boolean, fixNullValue: FeatureServiceConfiguration.FixNullValue): String = {
    var resultWhereClauseString = ""

    val whereClause: StringBuffer = new StringBuffer()

    val startTime: String = queryJson.getStartTime
    val endTime: String = queryJson.getEndTime
    appendDatetimeBoundToQuery(whereClause, startTime, endTime, pstmtList)

    //going over the Filters and Forming the query
    val queries: util.List[util.Map[String, AnyRef]] = queryJson.getQuery
    val entityQueryStrBuffer = new StringBuffer()
    if(queries != null & !queries.isEmpty) {
      for (i <- 0 until queries.size()) {
        val query: util.Map[String, AnyRef] = queries.get(i)
        val filterName: String = query.get("field").asInstanceOf[String]
        val operatorString: String = query.get("operator").asInstanceOf[String]
        val values: util.ArrayList[String] = query.get("values").asInstanceOf[util.ArrayList[String]]

        val filter: FilterField = cache.getFilterFieldFromFilterName(filterName)
        val columnName = filter.getFieldName
        val columnType = filter.getType

        //get operator string
        val operator = SqlUtils.getOperatorFromOperatorString(operatorString)

        if(!isEntityString(filterName)){
          appendColumnToWhereClause(whereClause, columnName, operator, values, columnType, pstmtList, AND_STRING)
        }
      }
    }
    // NOTE we are iterating over the query values twice because the arguments to pstmt are positional.
    // if we don't iterate twice we will stuff the values in the wrong position leading to errors such as
    // ERROR [2016-07-27 18:15:59,049] com.securityx.modelfeature.dao.DetectorHomeDao: Failed to get Entity Scores => org.apache.phoenix.schema.TypeMismatchException: ERROR 203 (22005): Type mismatch. DOUBLE and CHAR for RISK_SCORE >= 'RDP-GW'
    if(queries != null & !queries.isEmpty) {
      for (i <- 0 until queries.size()) {
        val query: util.Map[String, AnyRef] = queries.get(i)
        val filterName: String = query.get("field").asInstanceOf[String]
        val operatorString: String = query.get("operator").asInstanceOf[String]
        val values: util.ArrayList[String] = query.get("values").asInstanceOf[util.ArrayList[String]]

        val filter: FilterField = cache.getFilterFieldFromFilterName(filterName)
        val columnName = filter.getFieldName
        val columnType = filter.getType

        //get operator string
        val operator = SqlUtils.getOperatorFromOperatorString(operatorString)

        if(isEntityString(filterName)){
          if(fixNullValue.isEnabled){
            for (j <- 0 until values.size()) {
              if (values.get(j) == null || values.get(j).equals("")) {
                values.set(j, fixNullValue.getNullValue)
              }
            }
          }
          if(isSearchQuery) {
            appendColumnToWhereClause(entityQueryStrBuffer, columnName, operator, values, columnType, pstmtList, OR_STRING)
          }else{
            appendColumnToWhereClause(entityQueryStrBuffer, columnName, operator, values, columnType, pstmtList, AND_STRING)
          }
        }
      }
    }

    if(entityQueryStrBuffer.length() > 0 ){
      joinWhereClause(whereClause, AND_STRING, "( " + entityQueryStrBuffer.toString() + " )")
    }

    val behaviours = queryJson.getBehaviors
    if(behaviours != null && !behaviours.isEmpty){
      val stringBuilder = new StringBuilder()
      for (i <- 0 until behaviours.size()) {
         val behaviour = behaviours.get(i)
        val modelId = behaviour.getModelId
        val secEventIds: util.List[Integer] = behaviour.getSecurityEventId
        for(j <- 0 until secEventIds.size()){
          val secEventId = secEventIds.get(j)
          if(stringBuilder.nonEmpty){
            stringBuilder.append(" OR ")
          }
          stringBuilder.append(" ( ").append(COLUMN_SECURITY_EVENT_ID).append(" = ? AND ").
            append(COLUMN_MODEL_ID).append(" = ? )")
          populatePreparedStatementList(pstmtList, COLUMN_SECURITY_EVENT_ID, DataTypes.INTEGER.toString, String.valueOf(secEventId))
          populatePreparedStatementList(pstmtList, COLUMN_MODEL_ID, DataTypes.INTEGER.toString, String.valueOf(modelId))
        }

      }
      if(stringBuilder.nonEmpty){
         joinWhereClause(whereClause, AND_STRING, "( " + stringBuilder.toString() + " )")
      }
    }

    if (whereClause.length() > 0) {
      resultWhereClauseString = " where " + whereClause.toString
    }

    if(groupByString != null && groupByString.nonEmpty){
      resultWhereClauseString = resultWhereClauseString + " "  + groupByString
    }

    //Set Sort Order
    val sortField : String = queryJson.getSortField
    if(sortField != null && sortField.nonEmpty) {
      val filter: FilterField = cache.getFilterFieldFromFilterName(sortField)
      if(filter != null) {
        var columnName = filter.getFieldName
        if(aliasMap != null && aliasMap.nonEmpty && aliasMap.contains(columnName)){
          columnName = aliasMap(columnName).toString
        }
        val sortOrder = queryJson.getSortOrder
        resultWhereClauseString = resultWhereClauseString + " ORDER BY " + columnName + " " + sortOrder
      }
    }
    //topN
    val n = queryJson.getLimit
    if(n != null && !n.equals(0)) {
      resultWhereClauseString = resultWhereClauseString + " LIMIT " + " ? "
      populatePreparedStatementList(pstmtList, "", DataTypes.INTEGER.toString, n.toString)
    }


    resultWhereClauseString
  }

  /**
   * Uses the columnName and Operator to find the "where clause" of the sql query
   *
   * @param buffer  StringBuffer which stores the where clause String
   * @param columnName  String specifying column Name
   * @param operatorString  String specifying operator. eg: "=", "<=", ">=" etc
   * @param values Values for the filter column (OR)
   * @param columnType Datatype to be used for a particular column in the filter
   * @param pstmtList List of params, Types and their Values; in the order they come in the sql query
    * @return
   */
  private def appendColumnToWhereClause(buffer: StringBuffer, columnName: String, operatorString: String,
                                values: util.ArrayList[String], columnType: String, pstmtList: ListBuffer[ColumnMetaData],
                                        andOrString: String) = {
    val tempBuffer = new StringBuffer()
    if (operatorString.equalsIgnoreCase("like")) {
      appendLikeOperatorString(tempBuffer, columnName, values)
    } else {
      for (i <- 0 until values.size()) {
        if(values.get(i) == null){
          val appendQuery: String = " NOT ( " + columnName + "  IS NOT NULL ) "
          joinWhereClause(tempBuffer, OR_STRING, appendQuery)
        }else {
          val appendQuery: String = columnName + " " + operatorString + " ?"
          populatePreparedStatementList(pstmtList, columnName, columnType, String.valueOf(values.get(i)))
          joinWhereClause(tempBuffer, OR_STRING, appendQuery)
        }
      }
    }

    if (tempBuffer.length() > 0) {
      joinWhereClause(buffer, andOrString, "( " + tempBuffer + " )")
    }
  }


  /**
   * populates the SQL query if any of the Operators is a LIKE OPERATOR
   *
   * @param buffer StringBuffer which stores the where clause String
   * @param columnName String specifying column Name
   * @param values Values for the filter column (OR)
   */
  private def appendLikeOperatorString(buffer: StringBuffer, columnName: String, values: util.ArrayList[String]) = {

    for (i <- 0 until values.size()) {
      //Like operator will always be used for String
      val appendQuery: String = columnName + LIKE_STRING + " '%" + values.get(i) + "%' "

      //Multiple "like": use OR
      joinWhereClause(buffer, OR_STRING, appendQuery)

    }
  }

  /**
   * Method to append to "where clause" of the Sql String
    *
    * @param buffer
   * @param andOrLabel
   * @param value
   * @return
   */
  private def joinWhereClause(buffer: StringBuffer, andOrLabel: String, value: String) = {
    if (buffer.length == 0) {
      buffer.append(value)
    } else {
      buffer.append(andOrLabel + value)
    }
  }

  /**
   *  String is used to specify startTime and endTime of the sql query (this will also be in the where clause)
   *
   * @param buff StringBuffer which stores the where clause String
   * @param startTime  String specifying startTime
   * @param endTime String specifying endTime
   * @param pstmtList List of params, Types and their Values; in the order they come in the sql query
   * @return
   */
  private def appendDatetimeBoundToQuery(buff: StringBuffer, startTime: String, endTime: String,
                                         pstmtList: ListBuffer[ColumnMetaData]) = {
    if(startTime != null && !startTime.isEmpty) {
      appendToQuery(buff, COLUMN_DATE_TIME, " >= ", startTime)
      populatePreparedStatementList(pstmtList, COLUMN_DATE_TIME, DataTypes.STRING.toString, startTime)
    }

    if(endTime != null && !endTime.isEmpty) {
      appendToQuery(buff, COLUMN_DATE_TIME, " < ", endTime)
      populatePreparedStatementList(pstmtList, COLUMN_DATE_TIME, DataTypes.STRING.toString, endTime)
    }
  }

  /**
   *  Adds to the where clause, the column, operator and String value
   *
   * @param buff StringBuffer which stores the where clause String
   * @param columnName String column Name
   * @param operator  String operator
   * @param value String value
   */
  private def appendToQuery(buff: StringBuffer, columnName: String, operator: String, value: String) {
    if (buff != null) {
      val appendQuery: String = columnName + " " + operator + " ? "
      joinWhereClause(buff, AND_STRING, appendQuery)
    }
  }

  /**
   * Goes over the killchain ids, find the corresponding security event Ids and adds them to the sql qurey
   *
   * @param buffer StringBuffer which stores the where clause String
   * @param killchainIds List of killchain Ids
   * @param pstmtList List of params, Types and their Values; in the order they come in the sql query
   * @param cache FeatureServiceCache
   * @return
   */
  private def appendKillchainFilterToQuery(buffer: StringBuffer, killchainIds: util.List[Integer], pstmtList: ListBuffer[ColumnMetaData],
                                           cache: FeatureServiceCache) = {

    val inOperator: StringBuffer = new StringBuffer()
    for (i <- 0 until killchainIds.size()) {

      val id = killchainIds.get(i)


      if (inOperator.length() == 0) {
        inOperator.append(id)
      } else {
        inOperator.append("," + id)
      }

    }
    if (inOperator.length() > 0) {
      val q: String = "KILLCHAIN_ID" + IN_OPERATOR_STRING + "( " + inOperator.toString + " )"
      joinWhereClause(buffer, AND_STRING, "( " + q + " )")
    }
  }

  def isEntityString(str: String): Boolean = {
    if(str != null && ( str.equalsIgnoreCase("hostName") ||  str.equalsIgnoreCase("userName") ||
      str.equalsIgnoreCase("macAddress") ||  str.equalsIgnoreCase("sourceIp"))){
      true
    }else{
      false
    }
  }

  /**
   *
   * @param pstmtList List of params, Types and their Values; in the order they come in the sql query
   * @param columnName String column Name
   * @param columnType String columnType
   * @param columnValue String value (will be casted to correct type depending on the column type, when being used)
    * @return
   */
  private def populatePreparedStatementList(pstmtList: ListBuffer[ColumnMetaData],
                                            columnName: String, columnType: String, columnValue: String) = {
    pstmtList += new ColumnMetaData(columnName, columnType, columnValue)
  }

  /**
   * Create a string that can be an in clause in a prepared statement.  That is, generate a string
   * like "(?, ?, ..., ?)", where the string has as many ? as the number of elements in the ListBuffer
   * passed in.  Although there's nothing technically wrong with constructing massive in clauses, it
   * would probably be better if this were done only with lists of about ten or fewer elements.
   *
   * @param inElements List of strings that will ultimately be assigned to the in clause
   * @return the like clause string
   */
  def createInClause(inElements: ListBuffer[String]): String = {
    var inClause: String = "("
    for (i <- inElements.indices) {
      if (i > 0) {
        inClause += ", "
      }
      inClause += "?"
    }

    inClause += ")"
    inClause
  }

  /**
   * This method will assign the elements of a ListBuffer to a prepared statement that has an in clause in it.
   *
   * @param inElements The list of values to be assigned to the prepared statement
   * @param pstmt Prepared statement to assign the values to
   * @param index index of the place in the prepared statement to begin assigning the values.  Essentially, this
   *              is a count of the number of ? that occur prior to the in clause in the prepared statement.
   */
  def populateInClause(inElements: ListBuffer[String], pstmt: PreparedStatement, index: Int): Unit = {
    for (i <- inElements.indices) {
      val value = inElements(i)
      pstmt.setString(index+i+1, value)
    }
  }
}

