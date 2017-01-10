package com.securityx.modelfeature.dao.alerts

import java.util

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.BaseDao

/**
 * Created by harish on 5/24/15.
 */
class AlertsBaseDao(conf: FeatureServiceConfiguration) extends BaseDao(conf){

  def getListFromArrayString(arrString: String): util.List[String] = {
    val list: util.List[String] = new util.ArrayList[String]()
    if(arrString != null && arrString.nonEmpty) {
      val str = arrString.replace("[", "").replace("]", "")
      val arr = str.split(",")
      arr.foreach { s =>
        if(s != null && s.trim.nonEmpty) {
          list.add(s.trim)
        }
      }
    }
    list
  }

}
