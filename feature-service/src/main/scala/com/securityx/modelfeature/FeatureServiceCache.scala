package com.securityx.modelfeature

import java.util

import com.securityx.modelfeature.utils._
import com.securityx.modelfeature.config._
import com.securityx.modelfeature.config.configloaders._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}

/**
 * Cache for the Api service
 * Created by harish on 11/17/14.
 */
class FeatureServiceCache {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[FeatureServiceCache])

  //Models
  private var modelsMap: MutableMap[Integer, String] = MutableMap[Integer, String]()
  private var modelsEntityTypeMap: MutableMap[Integer, String] = MutableMap[Integer, String]()

  //killchain Id -> killchain category name
  private var killchainMap: MutableMap[Integer, String] = MutableMap[Integer, String]()

  //SecurityEvent Id -> SecurityEvent data which includes event Type, type prefix, killchain id
  private val securityEventMap: MutableMap[Int, collection.mutable.ListBuffer[SecurityEventTypeConfiguration]] = MutableMap[Int, collection.mutable
  .ListBuffer[SecurityEventTypeConfiguration]]()

  //Killchain iD -> list of Security Events
  private val killchainSecurityEventIdMapping: MutableMap[Int, collection.mutable.ListBuffer[Int]] = MutableMap[Int, collection.mutable
  .ListBuffer[Int]]()

  //Killchain iD -> list of Security Event Data
  private val killchainSecurityEventDataListMapping: MutableMap[Int, collection.mutable.ListBuffer[SecurityEventTypeConfiguration]] = MutableMap[Int,
    collection.mutable.ListBuffer[SecurityEventTypeConfiguration]]()

  private var adPeerGroupFacetMap: MutableMap[Int, AdFacetHelper] = null
  private var webPeerGroupFacetMap: MutableMap[Int, WebFacetHelper] = null
  private var webPeerGroupFacetMapImpala: MutableMap[Int, WebFacetHelper] = null
  private var adNoveltyDetectorFacetMap: MutableMap[Int, AdNoveltyDectectorFacetHelper] = null

  //Ad explorer related maps
  val describeLoader: DescribeLoader = new DescribeLoader();

  //Web Anomaly Profile Names
  val webAnomalyProfileConfig: WebAnomalyProfileConfigurationLoader = new WebAnomalyProfileConfigurationLoader()

  val securityEventTypesConfig: SecurityEventsConfigurationLoader = new SecurityEventsConfigurationLoader()

  val searchConfigLoader: SearchConfigurationLoader = new SearchConfigurationLoader()

  val alertConfig: AlertConfigurationLoader = new AlertConfigurationLoader()


  def this(conf: FeatureServiceConfiguration) {
    this()
    try{
      populateCaches(conf)
    } catch{
      case ex : Exception => throw new RuntimeException("Exception while populating caches => " + ex)
    }
  }

  private def populateCaches(conf: FeatureServiceConfiguration) = {
    populatePeerGroupMap()
    describeLoader.loadDescribe(conf)
    webAnomalyProfileConfig.loadWebAnomalyProfileNames(conf)
    securityEventTypesConfig.loadSecurityEventInfo(conf)
    searchConfigLoader.loadSearchConfigInfo(conf)
    alertConfig.loadAlertConfigInfo(conf)
    populateModelsMap()
    populateKillchainMap()
    populateSecurityEventMap()
  }


  def getDescribeLoader() = {
    describeLoader
  }

  def getWebAnomalyProfileConfigCache = {
    webAnomalyProfileConfig
  }

  private def populatePeerGroupMap() = {
    val peerUtil: PeerGroupUtil = new PeerGroupUtil()
    adPeerGroupFacetMap = peerUtil.getAdPeerGroupFacetMapMap()
    webPeerGroupFacetMap = peerUtil.getWebPeerGroupFacetMapMap()
    webPeerGroupFacetMapImpala = peerUtil.getWebPeerGroupFacetImpalaMap()
    adNoveltyDetectorFacetMap = peerUtil.getAdNoveltyDetectorFacetMapMap()
  }

  private def populateModelsMap() = {
    modelsMap = scala.collection.JavaConversions.mapAsScalaMap(securityEventTypesConfig.getModelsMap)
    modelsEntityTypeMap = scala.collection.JavaConversions.mapAsScalaMap(securityEventTypesConfig.getModelsEntityTypeMap)
  }

  private def populateKillchainMap() {
    killchainMap = scala.collection.JavaConversions.mapAsScalaMap(securityEventTypesConfig.getKillChainMap)
  }

  private def populateSecurityEventMap() {
    val securityEventList: util.List[SecurityEventTypeConfiguration] = securityEventTypesConfig.getSecurityEventTypeList
    for (i <- 0 to securityEventList.size() - 1) {
      val securityEvent: SecurityEventTypeConfiguration = securityEventList.get(i)
      val secEventId: Int = securityEvent.getSecurityEventTypeId
      val killchainId = securityEvent.getKillchainId
      val modelId: Int = securityEvent.getModel
      val eventDescription: String = securityEvent.getEventDescription
      val featureLabel: String = securityEvent.getFeatureLabel
      val typePrefix: String = securityEvent.getTypePrefix
      val eventType: String = securityEvent.getEventType
      val cardId: Int = securityEvent.getCardId



      val list: collection.mutable.ListBuffer[SecurityEventTypeConfiguration] = {
        if (securityEventMap.contains(secEventId))
          securityEventMap(secEventId)
        else
          collection.mutable.ListBuffer[SecurityEventTypeConfiguration]()
      }

      list += securityEvent
      securityEventMap += secEventId -> list

      addKillchainToEventTypeMapping(killchainId, secEventId)
      addKillchainToSecurityEventDataMapping(killchainId, securityEvent)
    }

  }

  def getOperatorNameToIdMap: util.Map[String, Integer] = {
    securityEventTypesConfig.getOperatorNameToIdMap
  }

  def getOperatorMap : util.Map[Integer, String] = {
    securityEventTypesConfig.getOperatorsMap
  }


  def getFilterFields: util.List[FilterField] = {
    securityEventTypesConfig.getFilterFields
  }

  def getFilterFieldMap: util.Map[Integer, FilterField] = {
    securityEventTypesConfig.getFilterFieldMap
  }

  def getFilterFieldNamesMap : util.Map[String, FilterField] = {
    securityEventTypesConfig.getFilterFieldNamesMap
  }

  def getC2ModelConfigurations: util.List[C2ModelConfiguration] = {
    securityEventTypesConfig.getC2ModelConfigurations
  }

  def getC2ModelConfigurationFeatureMap : util.Map[String, C2ModelConfiguration] = {
    securityEventTypesConfig.getC2ModelConfigurationFeatureMap
  }

  def getFilterFieldFromFilterId(id: Int): FilterField = {
    var ff: FilterField = null;
    val map = getFilterFieldMap;
    if (map.containsKey(id)) {
      ff = map.get(id)
    }
    ff
  }

  def getFilterFieldFromFilterName(fieldName: String): FilterField = {
    var ff: FilterField = null;
    val map = getFilterFieldNamesMap;
    if (map.containsKey(fieldName)) {
      ff = map.get(fieldName)
    }
    ff
  }

  def addKillchainToSecurityEventDataMapping(killchainId: Int, data: SecurityEventTypeConfiguration) = {
    val list: ListBuffer[SecurityEventTypeConfiguration] = {
      if (killchainSecurityEventDataListMapping.contains(killchainId)) {
        killchainSecurityEventDataListMapping(killchainId)
      }
      else new ListBuffer[SecurityEventTypeConfiguration]
    }
    list += data

    killchainSecurityEventDataListMapping += killchainId -> list
  }

  private def addKillchainToEventTypeMapping(killchainId: Int, securityEventId: Int) = {
    val list: collection.mutable.ListBuffer[Int] = if (killchainSecurityEventIdMapping.contains(killchainId))
      killchainSecurityEventIdMapping(killchainId) else collection.mutable.ListBuffer[Int]()

    if (!list.contains(securityEventId)) {
      list += securityEventId
    }
    killchainSecurityEventIdMapping += killchainId -> list

  }

  def getKillchainMap: MutableMap[Integer, String] = {
    killchainMap
  }

  def getSecurityEventDataMap: MutableMap[Int, collection.mutable.ListBuffer[SecurityEventTypeConfiguration]] = {
    securityEventMap
  }

  def getKillchainSecurityEventMapping: MutableMap[Int, collection.mutable.ListBuffer[Int]] = {
    killchainSecurityEventIdMapping
  }


  def getKillchainToSecurityEventDataMap(): MutableMap[Int, ListBuffer[SecurityEventTypeConfiguration]] = {
    killchainSecurityEventDataListMapping
  }

  def getModelsMap: MutableMap[Integer, String] = {
    modelsMap
  }

  def getModelsEntityTypeMap: MutableMap[Integer, String] = {
    modelsEntityTypeMap
  }

  def getKillchainIdFromSecurityEventModelIds(securityEventId: Int, modelId: Int): Integer = {
    val secDataList: ListBuffer[SecurityEventTypeConfiguration] = {
      if (getSecurityEventDataMap.contains(securityEventId))
        getSecurityEventDataMap(securityEventId)
      else
        null
    }
    var killchainId: Integer = null
    secDataList.foreach { securityData =>
      if (securityData.getModel.equals(modelId))
        killchainId = securityData.getKillchainId
    }
    killchainId
  }

  def getEntityCardIdFromSecurityEventModelIds(securityEventId: Int, modelId: Int): Integer = {
    val secDataList: ListBuffer[SecurityEventTypeConfiguration] = {
      if (getSecurityEventDataMap.contains(securityEventId))
        getSecurityEventDataMap(securityEventId)
      else
        null
    }
    var cardId: Integer = null
    secDataList.foreach { securityData =>
      if (securityData.getModel.equals(modelId))
        cardId = securityData.getCardId
    }
    cardId
  }

  /**
   *
   * @param securityEventId Int security Event Id
   * @param modelId Int modelId
   * @return  Tuple formed by (killchainId, cardId)
   */
  def getEntityCardKillchainIdFromEventModelIds(securityEventId: Int, modelId: Int): (Integer, Integer) = {
    val secDataList: ListBuffer[SecurityEventTypeConfiguration] = {
      if (getSecurityEventDataMap.contains(securityEventId))
        getSecurityEventDataMap(securityEventId)
      else
        null
    }
    var cardId: Integer = null
    var killchainId: Integer = null
    secDataList.foreach { securityData =>
      if (securityData.getModel.equals(modelId))
        cardId = securityData.getCardId
        killchainId = securityData.getKillchainId
    }
    (killchainId, cardId)
  }

  def getSecurityEventDataFromFeatureIdModelId(securityEventId: Int, modelId: Int): SecurityEventTypeConfiguration = {
    var securityEventData: SecurityEventTypeConfiguration = null
    if (securityEventMap.contains(securityEventId)) {
      val dataList: ListBuffer[SecurityEventTypeConfiguration] = securityEventMap(securityEventId)
      dataList.foreach { data =>
        if (data.getModel.equals(modelId))
          securityEventData = data
      }
    }
    securityEventData
  }


  // Note that this method has an issue - event types are not always unique in the same model.  For instance,
  // there is more than one securityEventData object with an eventType of 'Exfiltration' for model 4.  This
  // method will simply return the first one it comes across, and users must accept that.
  def getSecurityEventDataFromEventTypeModelId(eventType: String, modelId: Int): SecurityEventTypeConfiguration = {
    var securityEventData: SecurityEventTypeConfiguration = null
    for((k,v) <- securityEventMap){
      v.foreach{secEventData =>
        val model = secEventData.getModel
        val eventTypeStr = secEventData.getEventType
        if(model.equals(modelId) && eventTypeStr.equals(eventType)){
          securityEventData = secEventData
        }
      }
    }
    securityEventData
  }

  def getSecurityEventDataFromFeatureLabelModelId(featureLabel: String, modelId: Int): SecurityEventTypeConfiguration = {
    var securityEventData: SecurityEventTypeConfiguration = null
    for((k,v) <- securityEventMap){
      v.foreach{secEventData =>
        val model = secEventData.getModel
        val featureLabelStr = secEventData.getFeatureLabel
        if(model.equals(modelId) && featureLabelStr.equals(featureLabel)){
          securityEventData = secEventData
        }
      }
    }
    securityEventData
  }

  def getModelNameFromId(modelId: Int): String = {
    val modelMap = getModelsMap
    if (modelMap.contains(modelId))
      modelMap(modelId)
    else
      null

  }

  def getAdPeerGroupFacetMap(): MutableMap[Int, AdFacetHelper] = {
    adPeerGroupFacetMap
  }

  def getWebPeerGroupFacetMap(): MutableMap[Int, WebFacetHelper] = {
    webPeerGroupFacetMap
  }

  def getWebPeerGroupFacetMapImpala(): MutableMap[Int, WebFacetHelper] = {
    webPeerGroupFacetMapImpala
  }

  def getAdNoveltyDetectorFacetMap(): MutableMap[Int, AdNoveltyDectectorFacetHelper] = {
    adNoveltyDetectorFacetMap
  }

  def getWebSummaryFieldMap: util.Map[String, String] = {
    searchConfigLoader.getWebSearchSummaryMap
  }

  def getAdSummaryFieldMap: util.Map[String, String] = {
    searchConfigLoader.getAdSearchSummaryMap
  }

  def getTaniumHostInfoMefSummaryFieldMap(): util.Map[String, String] = {
    searchConfigLoader.getTaniumHostInfoMefSearchSummaryMap
  }

  def getSearchConfiguration: SearchConfiguration = {
    searchConfigLoader.getSearchConfiguration
  }


  def getAdPeerGroupFromSecEventId(securityEventId: Int): AdFacetHelper = {
    if (getAdPeerGroupFacetMap().contains(securityEventId)) adPeerGroupFacetMap(securityEventId)
    else null
  }


  def getPeerFacetHelperFromModelIdSecEventId(modelId: Int, securityEventId: Int): PeerGroupFacetHelper = {
    var result: PeerGroupFacetHelper = null
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
      if (getAdPeerGroupFacetMap().contains(securityEventId))
        result = adPeerGroupFacetMap(securityEventId)
    } else if (Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
      if (getWebPeerGroupFacetMap().contains(securityEventId))
        result = webPeerGroupFacetMap(securityEventId)
    } else if(Constants.AdNoveltyDetectorTuple._1.equals(modelId)){
      if (getAdNoveltyDetectorFacetMap().contains(securityEventId))
        result = adNoveltyDetectorFacetMap(securityEventId)
    }
    result
  }

  def getPeerFacetHelperFromModelIdSecEventIdForImpala(modelId: Int, securityEventId: Int): PeerGroupFacetHelper = {
    var result: PeerGroupFacetHelper = null
    if (Constants.ADPeerAnomaliesModelTuple._1.equals(modelId)) {
      if (getAdPeerGroupFacetMap().contains(securityEventId))
        result = adPeerGroupFacetMap(securityEventId)
    } else if (Constants.WebPeerAnomaliesModelTuple._1.equals(modelId)) {
      if (getWebPeerGroupFacetMapImpala().contains(securityEventId))
        result = webPeerGroupFacetMapImpala(securityEventId)
    } else if(Constants.AdNoveltyDetectorTuple._1.equals(modelId)){
      if (getAdNoveltyDetectorFacetMap().contains(securityEventId))
        result = adNoveltyDetectorFacetMap(securityEventId)
    }
    result
  }
  def getAlertDestinationsMap = {
    alertConfig.getAlertDestinationMap
  }

  def getAlertDestinationIdFromName(alertDestinationName: String): Int ={
    alertConfig.getAlertDestinationIdFromDestinationName(alertDestinationName)
  }

  def getCefConfiguration = {
    alertConfig.getCefConfiguration
  }

  def getEmailSenderConfiguration = {
    alertConfig.getEmailSenderConfiguration
  }

  def getAlertSChedulerConfiguration = {
    alertConfig.getAlertSchedulerConfiguration
  }

  def getAlertConfiguration() = {
    alertConfig
  }
}



