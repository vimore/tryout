package com.securityx.modelfeature.utils

/**
 * Created by harish on 11/20/14.
 */
class Constants {

}

object Constants {

  //Model Constants
  val BeaconModelTuple: (Integer, String) = (0, "BEACON")
  val WebCoordinatedBehaviorModelTuple: (Integer, String) = (1, "WEB COORDINATED BEHAVIOR")
  val WebPeerAnomaliesModelTuple: (Integer, String) = (2, "WEB PEER ANOMALIES")
  val ADPeerAnomaliesModelTuple: (Integer, String) = (3, "AD PEER ANOMALIES")
  val WebAnomalyProfileTuple: (Integer, String) = (4, "Web Anomaly Profile")
  val TaniumModelTuple: (Integer, String) = (5, "Tanium")
  val AdAnomalyProfileTuple: (Integer, String) = (6, "Ad Anomaly Profile")
  val AdNoveltyDetectorTuple: (Integer, String) = (7, "Ad Novelty Detector")
  val C2ModelTuple: (Integer, String) = (8, "C2")
  val EndpointLocalModelTuple: (Integer, String) = (9, "Local Endpoint")
  val EndpointGlobalModelTuple: (Integer, String) = (10, "Global Endpoint")

  val WEB_PROXY_MEF_COLLECTION: String = "web_proxy_mef"
  val IAM_MEF_COLLECTION: String = "iam_mef"
  val TANIUM_UET_MEF_COLLECTION = "uet_mef"
  val TANIUM_HET_MEF_COLLECTION = "het_mef"
  val TANIUM_HOST_INFO_MEF_COLLECTION = "host_info_mef"

  val BeaconBotSecurityEventId = 0

  val LAST_N_DAYS: Int = 30

  //class types
  val ClassInteger = "java.lang.Integer"
  val ClassString = "java.lang.String"
  val ClassDouble = "java.lang.Double"
  val ClassLong = "java.lang.Long"
  val ClassTimestamp = "java.sql.Timestamp"
}

object IocThreatType extends Enumeration {
  val URL = Value("URL")
  val DOMAIN = Value("Domain")
  val IP = Value("IP")
}

object Operators extends Enumeration {
  val EQUALS = Value("EQUALS")
  val NOT_EQUALS = Value("NOT EQUALS")
  val CONTAINS = Value("CONTAINS")
  val LESS_THAN = Value("LESS THAN")
  val GREATER_THAN = Value("GREATER THAN")
  val LESS_THAN_EQUAL = Value("LESS THAN EQUAL")
  val GREATER_THAN_EQUAL = Value("GREATER THAN EQUAL")
}