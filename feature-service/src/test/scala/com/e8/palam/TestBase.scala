package com.e8.palam

import java.io.{File, FileInputStream, InputStream}

import com.e8.palam.dao.config.model.TestConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.securityx.modelfeature.common.cache.AutoCompleteCache
import com.securityx.modelfeature.dao.MongoUtils
import com.securityx.modelfeature.utils.{SearchUtils, Constants}
import com.securityx.modelfeature.config._

import com.securityx.modelfeature.{FeatureServiceCache, SolrServerClient}
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.junit.Before

/**
 * Created by harish on 2/12/15.
 */
class TestBase {

  val mapper: ObjectMapper = new ObjectMapper()
  protected var cache: FeatureServiceCache = null
  var autoCompleteCache: AutoCompleteCache = null
  var conf: FeatureServiceConfiguration = null
  val configFilePath = "src/test/config/test_cfg.yml"
  var webSolrServer: CloudSolrServer = null
  var iamSolrServer: CloudSolrServer = null


  //cloud chamber
  val host =  "cloudchamber.e8security.com"
  val scheme = "https"
  val userId =  "user2"
  val emailId =  "user2@auth.com"
  val X_HMAC_NONCE =  "X-HMAC-Nonce"
  val AUTHORIZATION =  "Authorization"
  val GEO_X_HMAC_NONCE =  "1398322270633"
  val GEO_AUTHORIZATION =  "3a57d231604f567b029184e28d3944c50f6ce4a1"
  val WHOIS_X_HMAC_NONCE =  "1398322270633"
  val WHOIS_AUTHORIZATION =  "3a57d231604f567b029184e28d3944c50f6ce4a1"

  //val MONGO_DB_SERVER = "10.10.80.77" //"10.10.30.20"
  //val MONGO_DB_PORT = 27017

  @throws(classOf[Exception])
  @Before
  def setup() = {
    mapper.registerModule(DefaultScalaModule)
    conf = new FeatureServiceConfiguration
    val entityFusionConfiguration: EntityFusionConfiguration = new EntityFusionConfiguration
    entityFusionConfiguration.setBackoffPeriodHours(48)
    conf.setEntityFusionConfiguration(entityFusionConfiguration)
    webSolrServer = new SolrServerClient(conf).getSolrServer(SearchUtils.getCollectionName(Constants.WebCoordinatedBehaviorModelTuple._1))
    iamSolrServer = new SolrServerClient(conf).getSolrServer(SearchUtils.getCollectionName(Constants.ADPeerAnomaliesModelTuple._1))
    loadConfigConstants(conf)
    cache = new FeatureServiceCache(conf)
    autoCompleteCache = new AutoCompleteCache(conf)
    loadGlobalStatusConf()
    loadCloadChamberCacheConf()
    //loadMongoDBConf(conf)
  }

  def loadConfigConstants(conf: FeatureServiceConfiguration) = {
    val inputStream: InputStream = new FileInputStream(new File(configFilePath))
    val ymlLoader: Yaml = new Yaml()
    val constants: TestConfiguration = ymlLoader.loadAs(inputStream, classOf[TestConfiguration])
    val configuration = new ConfigurationConstants()
    configuration.setSearchConfFilePath(constants.getSearchConfFilePath)
    configuration.setSecurityEventTypesFilePath(constants.getSecurityEventTypesFilePath)
    configuration.setTimeSeriesFilePath(constants.getTimeSeriesFilePath)
    configuration.setWebAnomalyProfileNamesFilePath(constants.getWebAnomalyProfileNamesFilePath)
    configuration.setAlertConfFilePath(constants.getAlertConfFilePath)
    conf.setConfigurationConstants(configuration)
    conf.setZkQuorum(constants.getZkQuorum)
    conf.setSolrQuorum(constants.getSolrQuorum)
    conf.setEnvironment(constants.getEnvironment)
    conf.setEntityFusionConfiguration(constants.getEntityFusion)
    conf.setRiskRanges(constants.getRiskRanges)

  }


  def loadCloadChamberCacheConf() = {
    val cloudChamberConfiguration: CloudChamberConfiguration = new CloudChamberConfiguration
    cloudChamberConfiguration.setHost(host)
    cloudChamberConfiguration.setScheme(scheme)
    cloudChamberConfiguration.setAUTHORIZATION(AUTHORIZATION)
    cloudChamberConfiguration.setEmailId(emailId)
    cloudChamberConfiguration.setUserId(userId)
    cloudChamberConfiguration.setWHOIS_X_HMAC_NONCE(WHOIS_X_HMAC_NONCE)
    cloudChamberConfiguration.setX_HMAC_NONCE(X_HMAC_NONCE)
    cloudChamberConfiguration.setWHOIS_AUTHORIZATION(WHOIS_AUTHORIZATION)
    cloudChamberConfiguration.setGEO_AUTHORIZATION(GEO_AUTHORIZATION)
    cloudChamberConfiguration.setGEO_X_HMAC_NONCE(GEO_X_HMAC_NONCE)
    conf.setCloudchamber(cloudChamberConfiguration)
  }

  def loadGlobalStatusConf() = {
    val globalStatusConf: GlobalStatusConfiguration = new GlobalStatusConfiguration
    globalStatusConf.setExcludeUserLikeTerms("%$")
    globalStatusConf.setExcludeHostLikeTerms("")
    conf.setGlobalStatus(globalStatusConf)
  }

  def getCache() = {
    cache
  }

  def getConf() = {
    conf
  }
}
