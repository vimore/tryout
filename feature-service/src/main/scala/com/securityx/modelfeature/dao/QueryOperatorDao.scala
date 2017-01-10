package com.securityx.modelfeature.dao

import java.util

import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.config.{FeatureServiceConfiguration, FilterField}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Created by harish on 2/18/15.
 */
class QueryOperatorDao(conf: FeatureServiceConfiguration) extends BaseDao(conf) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[QueryOperatorDao])



  def getOperators(cache: FeatureServiceCache) : ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val map: MutableMap[Integer, String]  =  scala.collection.JavaConversions.mapAsScalaMap(cache.getOperatorMap)


    for ((k,v) <- map){
      val selectionMap = MutableMap[String, Any]()
      selectionMap += "name" -> v
      selectionMap += "id" -> k
      buf += selectionMap
    }

    buf
  }


  private def getOperatorInfo(operators: util.List[Integer], map: MutableMap[Integer, String]) = {
    val operatorBuf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    for( i <- 0 until operators.size()){
      val operatorMap = MutableMap[String, Any]()
      val operatorId = operators.get(i)
      val operatorName = map(operatorId)
      operatorMap += "name" -> operatorName
      operatorMap += "id" -> operatorId

      operatorBuf += operatorMap
    }
    operatorBuf
  }

  def getQueryFields(cache: FeatureServiceCache) : ListBuffer[MutableMap[String, Any]] = {
    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]
    val map: MutableMap[Integer, String]  =  scala.collection.JavaConversions.mapAsScalaMap(cache.getOperatorMap)

    val filterFields: util.List[FilterField] = cache.getFilterFields;
    for( i <- 0 until filterFields.size()){
      var selectionMap = MutableMap[String, Any]()
      val filterField: FilterField = filterFields.get(i)
      if(filterField.isDefaultDisplay) {
        selectionMap += "id" -> filterField.getId
        selectionMap += "name" -> filterField.getName
        selectionMap += "operators" -> getOperatorInfo(filterField.getOperatorIds, map)
        buf += selectionMap
      }
    }

    buf
  }

}
