package com.securityx.modelfeature.utils

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

object BeaconSample extends Enumeration {
  // EVENT_TIME      |    C_IP    |  CS_HOST   |  RAW_LOG

  val EVENT_TIME = Value("EVENT_TIME")
  val EVENT_HOUR = Value("EVENT_HOUR")
  val C_IP = Value("C_IP")
  val CS_HOST = Value("CS_HOST")
  val PERIOD_SECONDS = Value("PERIOD_SECONDS")
  val RAW_LOG = Value("RAW_LOG")

  def columns = {
    BeaconSample.values.mkString(" , ")
  }

  object Models extends Enumeration {

    val MODEL_ID = Value("MODEL_ID")
    val MODEL_NAME = Value("MODEL_NAME")
  }

}

object IocList extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val SOURCE_ADDRESS = Value("SOURCE_ADDRESS")
  val DESTINATION_ADDRESS = Value("DESTINATION_ADDRESS")
  val DESTINATION_NAME_OR_IP = Value("DESTINATION_NAME_OR_IP")
  val THREAT_TYPE = Value("THREAT_TYPE")
  val THREAT_VALUE = Value("THREAT_VALUE")
  val THREAT_CLASS = Value("THREAT_CLASS")
  val THREAT_NAME = Value("THREAT_NAME")
  val DATA_SOURCE = Value("DATA_SOURCE")
  val FIRST_DATE_TIME = Value("FIRST_DATE_TIME")
  val LAST_DATE_TIME = Value("LAST_DATE_TIME")
  val BITS_IN = Value("BITS_IN")
  val BITS_OUT = Value("BITS_OUT")
  val CONNECTIONS = Value("CONNECTIONS")

  def columns = {
    IocList.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("IOC_LIST", conf)
  }
}


object IocSummary extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val DATE_TIME_RUN = Value("DATE_TIME_RUN")
  val DOMAINS = Value("DOMAINS")
  val NEW_DOMAINS = Value("NEW_DOMAINS")
  val DOMAIN_HITS = Value("DOMAIN_HITS")
  val NEW_DOMAIN_HITS = Value("NEW_DOMAIN_HITS")
  val URLS = Value("URLS")
  val NEW_URLS = Value("NEW_URLS")
  val URL_HITS = Value("URL_HITS")
  val NEW_URL_HITS = Value("NEW_URL_HITS")

  def columns = {
    IocSummary.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("IOC_SUMMARY", conf)
  }
}

object IocErrorStatus extends Enumeration {
  val DATE_TIME = Value("DATE_TIME")
  val FILE_NAME = Value("FILE_NAME")
  val LINE_NUMBER = Value("LINE_NUMBER")
  val INPUT_ROW = Value("INPUT_ROW")
  val ERROR_DESCRIPTION = Value("ERROR_DESCRIPTION")

  def columns = {
    IocErrorStatus.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("IOC_ERROR_STATUS", conf)
  }
}


object DetectorHome extends Enumeration {

  val HOST_NAME = Value("HOST_NAME")
  val IP_ADDRESS = Value("IP_ADDRESS")
  val DATE_TIME = Value("DATE_TIME")
  val CURRENT_SCORE = Value("CURRENT_SCORE")
  val BEACON_RISK_SCORE = Value("BEACON_RISK_SCORE")
  val WEB_BEHAVIOR_RISK_SCORE = Value("WEB_BEHAVIOR_RISK_SCORE")
  val PEER_GROUP_RISK_SCORE = Value("PEER_GROUP_RISK_SCORE")


  def columns = {
    DetectorHome.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("DETECTOR_HOME", conf)
  }
}

object EntityThreat extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val HOST_NAME = Value("HOST_NAME")
  val IP_ADDRESS = Value("IP_ADDRESS")
  val USER_NAME = Value("USER_NAME")
  val MAC_ADDRESS = Value("MAC_ADDRESS")
  val MODEL_ID = Value("MODEL_ID")
  val SECURITY_EVENT_ID = Value("SECURITY_EVENT_ID")
  val RISK_SCORE = Value("RISK_SCORE")
  val HOST_ENTITY_ID = Value("HOST_ENTITY_ID")
  val USER_ENTITY_ID = Value("USER_ENTITY_ID")


  def columns = {
    EntityThreat.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ENTITY_THREAT", conf)
  }
}


object SecurityEventTimeSeries extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val MIN_DATE_TIME = Value("MIN_DATE_TIME")
  val MAX_DATE_TIME = Value("MAX_DATE_TIME")
  val MODEL_ID = Value("MODEL_ID")
  val EVENT_COUNT= Value("EVENT_COUNT")

  def columns = {
    SecurityEventTimeSeries.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("SECURITY_EVENT_TIME_SERIES", conf)
  }
}

object PeerEntityCardDetails extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val MODEL_ID = Value("MODEL_ID")
  val SECURITY_EVENT_ID = Value("SECURITY_EVENT_ID")
  val EVENT_DESCRIPTION = Value("EVENT_DESCRIPTION")
  val SOURCE_IP = Value("SOURCE_IP")
  val SOURCE_USER_NAME = Value("SOURCE_USER_NAME")
  val SOURCE_PORT = Value("SOURCE_PORT")
  val DESTINATION_IP = Value("DESTINATION_IP")
  val DESTINATION_USER_NAME = Value("DESTINATION_USER_NAME")
  val DESTINATION_PORT = Value("DESTINATION_PORT")
  val WIN_EVENT_ID = Value("WIN_EVENT_ID")
  val WIN_EVENT_TYPE = Value("WIN_EVENT_TYPE")
  val BYTES_IN = Value("BYTES_IN")
  val BYTES_OUT = Value("BYTES_OUT")
  val URL = Value("URL")
  val HTTP_METHOD = Value("HTTP_METHOD")
  val USER_AGENT = Value("USER_AGENT")
  val HTTP_RESPONSE_CODE = Value("HTTP_RESPONSE_CODE")
  val SOURCE_ENTITY_ID = Value("SOURCE_ENTITY_ID")
  val DESTINATION_ENTITY_ID = Value("DESTINATION_ENTITY_ID")

  def columns = {
    PeerEntityCardDetails.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("PEER_ENTITY_CARD_DETAILS", conf)
  }
}

object EntityFusionHourlyRollUp extends Enumeration {

  val FUSION_TIME = Value("FUSION_TIME")
  val IP_ADDRESS = Value("IP_ADDRESS")
  val USER_NAME = Value("USER_NAME")
  val HOST_NAME = Value("HOST_NAME")
  val MAC_ADDRESS = Value("MAC_ADDRESS")
  val FIRST_SEEN_TIME = Value("FIRST_SEEN_TIME")
  val LAST_SEEN_TIME = Value("LAST_SEEN_TIME")


  def columns = {
    EntityFusionHourlyRollUp.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ENTITY_FUSION_HOURLY_ROLL_UP", conf)
  }
}


object EntityFeatures extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val SOURCE_NAME_OR_IP = Value("SOURCE_NAME_OR_IP")
  val DESTINATION_NAME_OR_IP = Value("DESTINATION_NAME_OR_IP")
  val MODEL_ID = Value("MODEL_ID")
  val FEATURE_LABEL = Value("FEATURE_LABEL")
  val SECURITY_EVENT_ID = Value("SECURITY_EVENT_ID")
  val FEATURE_VALUE = Value("FEATURE_VALUE")
  val DATA_SOURCE = Value("DATA_SOURCE")
  val GRANULARITY = Value("GRANULARITY")


  def columns = {
    EntityFeatures.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ENTITY_FEATURES", conf)
  }
}



object GlobalFeatures extends Enumeration {

  val FIELD_NAME = Value("FIELD_NAME")
  val FIELD_VALUE = Value("FIELD_VALUE")
  val DATE_TIME = Value("DATE_TIME")


  def columns = {
    GlobalFeatures.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("GLOBAL_FEATURES", conf)
  }
}

object EntHostProperties extends Enumeration {
  val UUID = Value("UUID")
  val DATE_TIME = Value("DATE_TIME")
  val MAC_ADDRESS = Value("MAC_ADDRESS")
  val IP_ADDRESSES = Value("IP_ADDRESSES")
  val COUNTRY = Value("COUNTRY")
  val CITY = Value("CITY")
  val OS = Value("OS")
  val BROWSERS = Value("BROWSERS")
  val HOST_NAMES = Value("HOST_NAMES")
  val PRIMARY_USERID = Value("PRIMARY_USERID")
  val CATEGORY = Value("CATEGORY")
  val RISK = Value("RISK")


  def columns = {
    EntHostProperties.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ENT_HOST_PROPS", conf)
  }
}

object EntUserProperties extends Enumeration {

  val UUID = Value("UUID")
  val DATE_TIME = Value("DATE_TIME")
  val USER_NAME = Value("USER_NAME")
  val CANONICAL_NAME = Value("CANONICAL_NAME")
  val ACCOUNT_TYPE = Value("ACCOUNT_TYPE")
  val SECURITY_ID = Value("SECURITY_ID")
  val IS_CRITCAL = Value("IS_CRITICAL")
  val JOB_TITLE = Value("JOB_TITTLE")
  val EMAIL = Value("EMAIL")
  val LOCATION = Value("LOCATION")
  val DEPARTMENT = Value("DEPARTMENT")
  val MANAGER = Value("MANAGER")
  val PRIMARY_HOST = Value("PRIMARY_HOST")
  val CREATION_DATE = Value("CREATION_DATE")
  val LAST_MODIFICATION_DATE = Value("LAST_MODIFICATION_DATE")
  val PASSWORD_LAST_SET_DATE = Value("PASSWORD_LAST_SET_DATE")
  val LAST_LOGON_DATE = Value("LAST_LOGON_DATE")
  val RISK = Value("RISK")

  def columns = {
    EntUserProperties.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ENT_USER_PROPS", conf)
  }
}

object ADTimeSeries extends Enumeration {

  val TYPE = Value("TYPE")
  val GROUP_FIELD = Value("GROUP_FIELD")
  val PERIOD_SECONDS = Value("PERIOD_SECONDS")
  val DATE_TIME = Value("DATE_TIME")
  val SOURCE_USER_COUNT = Value("SOURCE_USER_COUNT")
  val DESTINATION_USER_COUNT = Value("DESTINATION_USER_COUNT")
  val EVENT_COUNT = Value("EVENT_COUNT")

  def columns = {
    ADTimeSeries.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("AD_TIME_SERIES", conf)
  }
}

object HttpTimeSeries extends Enumeration {

  val TYPE = Value("TYPE")
  val GROUP_FIELD = Value("GROUP_FIELD")
  val PERIOD_SECONDS = Value("PERIOD_SECONDS")
  val DATE_TIME = Value("DATE_TIME")
  val BITS_IN_PER_SECOND = Value("BITS_IN_PER_SECOND")
  val BITS_OUT_PER_SECOND = Value("BITS_OUT_PER_SECOND")
  val CONNECTIONS_PER_SECOND = Value("CONNECTIONS_PER_SECOND")

  def columns = {
    ADTimeSeries.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("HTTP_TIME_SERIES", conf)
  }
}

object TaniumTimeSeries extends Enumeration {

  val TYPE = Value("TYPE")
  val GROUP_FIELD = Value("GROUP_FIELD")
  val PERIOD_SECONDS = Value("PERIOD_SECONDS")
  val DATE_TIME = Value("DATE_TIME")
  val CPU_USAGE = Value("CPU_USAGE")
  val MEM_USAGE = Value("MEM_USAGE")
  val DISTINCT_USERS = Value("DISTINCT_USERS")

  def columns = {
    TaniumTimeSeries.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("TANIUM_TIME_SERIES", conf)
  }
}


object TaniumStats extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val TYPE = Value("TYPE")
  val TYPE_VALUE = Value("TYPE_VALUE")
  val NEWLY_OBSERVED = Value("NEWLY_OBSERVED")
  val PIVOTS = Value("PIVOTS")
  val RISK_SCORE = Value("RISK_SCORE")
  val RISK_SCORE_GLOBAL = Value("RISK_SCORE_GLOBAL")
  val DATES_SEEN = Value("DATES_SEEN")
  val HOSTS_CURRENT = Value("HOSTS_CURRENT")
  val HOSTS_HISTORICAL = Value("HOSTS_HISTORICAL")
  val MD5S_NEW = Value("MD5S_NEW")
  val MD5S_CURRENT = Value("MD5S_CURRENT")
  val MD5S_HISTORICAL = Value("MD5S_HISTORICAL")
  val PROCESSES_NEW = Value("PROCESSES_NEW")
  val PROCESSES_CURRENT = Value("PROCESSES_CURRENT")
  val PROCESSES_HISTORICAL = Value("PROCESSES_HISTORICAL")
  val KEYS_NEW = Value("KEYS_NEW")
  val KEYS_CURRENT = Value("KEYS_CURRENT")
  val KEYS_HISTORICAL = Value("KEYS_HISTORICAL")
  val PATHS_NEW = Value("PATHS_NEW")
  val PATHS_CURRENT = Value("PATHS_CURRENT")
  val PATHS_HISTORICAL = Value("PATHS_HISTORICAL")
  val PORTS_NEW = Value("PORTS_NEW")
  val PORTS_CURRENT = Value("PORTS_CURRENT")
  val PORTS_HISTORICAL = Value("PORTS_HISTORICAL")
  val HOST_NAME = Value("HOST_NAME")

  def columns = {
    TaniumStats.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("TANIUM_STATS", conf)
  }
}

object PeerGroup extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val PEER_ID = Value("PEER_ID")
  val PEER_TYPE = Value("PEER_TYPE")
  val GROUP_TYPE = Value("GROUP_TYPE")
  val PEER_TOTAL = Value("PEER_TOTAL")
  val ANOMALY_SCORE = Value("ANOMALY_SCORE")
  val PEER_USERS = Value("PEER_USERS")
  val PEER_TOP_FEATURES = Value("PEER_TOP_FEATURES")
  val PEER_TOP_FEATURES_DESC = Value("PEER_TOP_FEATURES_DESC")
  val PEER_POSITION = Value("PEER_POSITION")
  val FEATURE_SCORES = Value("FEATURE_SCORES")
  val FEATURE_SCORES_NORM = Value("FEATURE_SCORES_NORM")
  val FEATURE_SCORES_DESC = Value("FEATURE_SCORES_DESC")
  val ENTITY_ID = Value("ENTITY_ID")

  def columns = {
    PeerGroup.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("PEER_GROUP", conf)
  }
}

object NoveltyDetector extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val SECURITY_EVENT_ID = Value("SECURITY_EVENT_ID")
  val MODEL_ID = Value("MODEL_ID")
  val GROUP_TYPE = Value("GROUP_TYPE")
  val USER_TOTAL = Value("USER_TOTAL")
  val FEATURE_KEY = Value("FEATURE_KEY")
  val CEF_SIGNATURE_ID = Value("CEF_SIGNATURE_ID")
  val FEATURE_KEY_DESCRIPTION = Value("FEATURE_KEY_DESCRIPTION")
  val FEATURE_KEY_LABEL = Value("FEATURE_KEY_LABEL")
  val FEATURE_KEY_COUNT = Value("FEATURE_KEY_COUNT")
  val NEWLY_OBSERVED = Value("NEWLY_OBSERVED")
  val RARITY_SCORE = Value("RARITY_SCORE")
  val ANOMALY_SCORE = Value("ANOMALY_SCORE")
  val TARGET_DESCRIPTION = Value("TARGET_DESCRIPTION")
  val ENTITY_ID = Value("ENTITY_ID")

  def columns = {
    NoveltyDetector.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("NOVELTY_DETECTOR", conf)
  }
}

object BehaviorAnomaly extends Enumeration{

  val DATE_TIME = Value("DATE_TIME")
  val ENTITY = Value("ENTITY")
  val FEATURE_LABEL = Value("FEATURE_LABEL")
  val MODEL_ID = Value("MODEL_ID")
  val RISK_SCORE = Value("RISK_SCORE")
  val ENTITY_ID = Value("ENTITY_ID")

  def columns = {
    BehaviorAnomaly.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("BEHAVIOR_ANOMALY", conf)
  }
}

object WebBehaviour extends Enumeration {

  val IP_ADDRESS = Value("IP_ADDRESS")
  val DATE_TIME = Value("DATE_TIME")
  val PERIOD_SECONDS = Value("PERIOD_SECONDS")
  val RISK_SCORE = Value("RISK_SCORE")
  val ANOMALY_PROFILE = Value("ANOMALY_PROFILE")

  def columns = {
    WebBehaviour.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("WEB_BEHAVIOR", conf)
  }
}

object C2Model extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val SOURCE_NAME_OR_IP = Value("SOURCE_NAME_OR_IP")
  val DESTINATION_NAME_OR_IP = Value("DESTINATION_NAME_OR_IP")
  val SLD = Value("SLD")
  val RISK_SCORE = Value("RISK_SCORE")
  val C2_FACTORS = Value("C2_FACTORS")
  val C2_FACTOR_VALUES = Value("C2_FACTOR_VALUES")
  val C2_TRAFFIC_HOURS = Value("C2_TRAFFIC_HOURS")
  val SOURCE_ENTITY_ID = Value("SOURCE_ENTITY_ID")

  def columns = {
    C2Model.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("C2_MODEL", conf)
  }
}


object Beacons extends Enumeration {

  val EVENT_TIME = Value("EVENT_TIME")
  val SOURCE_NAME_OR_IP = Value("SOURCE_NAME_OR_IP")
  val DESTINATION_NAME_OR_IP = Value("DESTINATION_NAME_OR_IP")
  val PERIOD_SECONDS = Value("PERIOD_SECONDS")
  val SPARSE_HISTOGRAM_JSON = Value("SPARSE_HISTOGRAM_JSON")
  val INTERVAL = Value("INTERVAL")
  val ANOMALY_ALEXA = Value("ANOMALY_ALEXA")
  val CONFIDENCE = Value("CONFIDENCE")
  val SLD_ANOMALY = Value("SLD_ANOMALY")
  val REQUEST_METHOD_ANOMALY = Value("REQUEST_METHOD_ANOMALY")
  val REQUEST_CLIENT_APPLICATION_ANOMALY = Value("REQUEST_CLIENT_APPLICATION_ANOMALY")
  val RISK = Value("RISK")
  val SYSLOG_JSON = Value("SYSLOG_JSON")

  def columns = {
    Beacons.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("BEACONS", conf)
  }
}

object CoordActivity extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val EDGE_ID = Value("EDGE_ID")
  val CLUSTER_ID = Value("CLUSTER_ID")
  val ANOMALY_CLUSTER_SCORE = Value("ANOMALY_CLUSTER_SCORE")
  val ANOMALY_EDGE_SCORE = Value("ANOMALY_EDGE_SCORE")
  val SOURCE_NAME_OR_IP = Value("SOURCE_NAME_OR_IP")
  val DESTINATION_NAME_OR_IP = Value("DESTINATION_NAME_OR_IP")
  val SELECTED_FEATURES = Value("SELECTED_FEATURES")
  val FEATURE_VALUES = Value("FEATURE_VALUES")
  val ENTITY_ID = Value("ENTITY_ID")

  def columns = {
    CoordActivity.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("COORD_ACTIVITY", conf)
  }
}

object NewlyObserved extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val FIELD_NAME = Value("FIELD_NAME")
  val FIELD_VALUE = Value("FIELD_VALUE")
  val COUNT = Value("COUNT")
  val TYPE = Value("TYPE")

  def columns = {
    NewlyObserved.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("NEWLY_OBSERVED", conf)
  }
}

object AlertDestinations extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val DESTINATION_ID = Value("DESTINATION_ID")
  val DESTINATION_NAME = Value("DESTINATION_NAME")
  val EMAIL_FROM = Value("EMAIL_FROM")
  val EMAIL_TO= Value("EMAIL_TO")
  val EMAIL_CC = Value("EMAIL_CC")
  val EMAIL_BCC = Value("EMAIL_BCC")
  val HOST_NAME= Value("HOST_NAME")
  val PORT = Value("PORT")
  val TRANSPORT = Value("TRANSPORT")
  val AUTH_USERNAME = Value("AUTH_USERNAME")
  val AUTH_PASSWORD = Value("AUTH_PASSWORD")

  def columns = {
    AlertDestinations.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ALERT_DESTINATIONS", conf)
  }
}

object Alert extends Enumeration {

  val ALERT_ID = Value("ALERT_ID")
  val ALERT_DESTINATION = Value("ALERT_DESTINATION")
  val BLACK_LIST = Value("BLACK_LIST")
  val RISK_THRESHOLD = Value("RISK_THRESHOLD")
  val RISK_THRESHOLD_OPERATOR= Value("RISK_THRESHOLD_OPERATOR")
  val FREQUENCY = Value("FREQUENCY")

  def columns = {
    Alert.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ALERT", conf)
  }
}

object AlertAuditLog extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val ALERT = Value("ALERT")
  val ALERT_DESTINATION = Value("ALERT_DESTINATION")
  val ALERT_STATE = Value("ALERT_STATE")
  val ALERT_LOG = Value("ALERT_LOG")
  val ERROR_LOG= Value("ERROR_LOG")

  def columns = {
    AlertAuditLog.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("ALERT_AUDIT_LOGS", conf)
  }
}

object RawLogs extends Enumeration {

  val UUID = Value("UUID")
  val RAWLOG = Value("RAWLOG")
  val START_TIME_ISO = Value("START_TIME_ISO")

  def columns = {
    RawLogs.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("RAWLOGS", conf)
  }
}

object DataIngestionStats extends Enumeration {

  val DATE_TIME = Value("DATE_TIME")
  val LOG_TYPE = Value("LOG_TYPE")
  val BYTES_INGESTED = Value("BYTES_INGESTED")

  def columns = {
    DataIngestionStats.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("DATA_INGESTION_STATS", conf)
  }
}


object CustomBehavior extends Enumeration {
  val DATE_TIME = Value("DATE_TIME")
  val IP_ADDRESS = Value("IP_ADDRESS")
  val MAC_ADDRESS = Value("MAC_ADDRESS")
  val HOST_NAME = Value("HOST_NAME")
  val USER_NAME = Value("USER_NAME")
  val SCAN_DETECTION = Value("SCAN_DETECTION")
  val FLOOD_DETECTION = Value("FLOOD_DETECTION")
  val URL_FILTERING_LOG = Value("URL_FILTERING_LOG")
  val SPYWARE_PHONE_HOME_DETECTION = Value("SPYWARE_PHONE_HOME_DETECTION")
  val SPYWARE_DOWNLOAD_DETECTION = Value("SPYWARE_DOWNLOAD_DETECTION")
  val VULNERABILITY_EXPLOIT_DETECTION = Value("VULNERABILITY_EXPLOIT_DETECTION")
  val DATA_FILTERING_DETECTION = Value("DATA_FILTERING_DETECTION")
  val VIRUS_DETECTION = Value("SPYWARE_DOWNLOAD_DETECTION")
  val WILDFIRE_SIGNATURE_FEED = Value("VULNERABILITY_EXPLOIT_DETECTION")
  val DNS_BOTNET_SIGNATURES = Value("DATA_FILTERING_DETECTION")
  val CUSTOM_TYPE = Value("CUSTOM_TYPE")
  def columns = {
    CustomBehavior.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("CUSTOM_BEHAVIOR", conf)
  }
}

/**
 * Created by sachinkapse on 24/05/16.
 */
object AppUser extends Enumeration {

  val USER_NAME = Value("USER_NAME")
  val DISPLAY_NAME = Value("DISPLAY_NAME")
  val PASSWORD = Value("PASSWORD")
  val USER_ROLE = Value("USER_ROLE")
  val CREATED_DATE_TIME = Value("CREATED_DATE_TIME")
  val ACTIVE_STATUS = Value("ACTIVE_STATUS")
  val CREATED_FOR_APP = Value("CREATED_FOR_APP")
  val IS_AUTO_GENERATED = Value("IS_AUTO_GENERATED")

  def columns = {
    AppUser.values.mkString(" , ")
  }

  def getName(conf: FeatureServiceConfiguration) = {
    TableSchemas.getFullTableName("APP_USERS", conf)
  }
}

object TableSchemas {

  def getFullTableName(tableName: String, conf: FeatureServiceConfiguration): String = {
    var table = tableName
    if (conf.getPhoenix.getSchema != null && conf.getPhoenix.getSchema.nonEmpty) {
      table = conf.getPhoenix.getSchema + table
    }
    table
  }
}
/**
 * Created by harish on 11/17/14.
 */
class TableSchemas(val conf: FeatureServiceConfiguration) {
  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[TableSchemas])
}

