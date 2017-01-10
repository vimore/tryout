package com.e8.palam.resources

import javax.ws.rs.core.Response

import com.e8.palam.TestBase
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.GlobalStatusDao
import com.securityx.modelfeature.resources.GlobalStatusFeature
import org.junit.{Before, Test}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.util.parsing.json.JSON

/**
 * Created by sachinkapse on 05/07/16.
 */
class GlobalStatusFeatureTest extends TestBase {

  private final val Logger: Logger = LoggerFactory.getLogger(classOf[GlobalStatusFeatureTest])

  private var globalStatusFeature: GlobalStatusFeature = null

  private var entityMap: MutableMap[String, Any] = null

  @Before
  override def setup() = {
    super.setup()

    globalStatusFeature = new GlobalStatusFeature(mapper, conf, cache)

    entityMap = MutableMap[String, Any]()

    val globalStatusDaoMock = new GlobalStatusDao(conf) {
      override def getEntityCounts(input: QueryJson, cache: FeatureServiceCache, conf: FeatureServiceConfiguration) = {
        entityMap
      }
    }
    globalStatusFeature.setGlobalStatusDao(globalStatusDaoMock)

  }

  def populateEntityMap() = {

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    var selectionMap = MutableMap[String, Any]()
    selectionMap += "ipAddress" -> "192.168.12.30"
    selectionMap += "macAddress" -> "60:F8:1D:C4:55:0A"
    selectionMap += "hostName" -> "WIN-OSNMCI3GJJ1"
    selectionMap += "userName" -> "alexi"
    selectionMap += "riskScore" -> 0.142857142857
    buf += selectionMap


    selectionMap += "ipAddress" -> "10.10.4.52"
    selectionMap += "macAddress" -> "A4:5E:60:E9:A3:DD"
    selectionMap += "hostName" -> "RDP-GW"
    selectionMap += "userName" -> "rdp"
    selectionMap += "riskScore" -> 0.903157142857
    buf += selectionMap


    selectionMap += "ipAddress" -> "10.10.4.41"
    selectionMap += "macAddress" -> "10:02:B5:D9:E3:60"
    selectionMap += "hostName" -> "DESKTOP-9UIVQ3V"
    selectionMap += "userName" -> "jyria"
    selectionMap += "riskScore" -> 0.502857142857
    buf += selectionMap

    // reset entityMap first
    entityMap = MutableMap[String, Any]()

    entityMap.put("entities", buf)
    entityMap.put("totalEntityCount", buf.size)
    entityMap.put("highRiskEntityCount", 1)
    entityMap.put("mediumRiskEntityCount", 1)
    entityMap.put("lowRiskEntityCount", 1)
  }

  def populateEntityMap2() = {

    val buf = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    var selectionMap = MutableMap[String, Any]()
    selectionMap += "ipAddress" -> "192.168.12.30"
    selectionMap += "macAddress" -> "60:F8:1D:C4:55:0A"
    selectionMap += "hostName" -> "WIN-OSNMCI3GJJ1"
    selectionMap += "userName" -> "alexi"
    selectionMap += "riskScore" -> 0.142857142857
    buf += selectionMap

    selectionMap += "ipAddress" -> "10.10.4.24"
    selectionMap += "macAddress" -> "A4:5E:56:C9:B3:D1"
    selectionMap += "hostName" -> "JACK-WS"
    selectionMap += "userName" -> "jack"
    selectionMap += "riskScore" -> 0.5
    buf += selectionMap


    selectionMap += "ipAddress" -> "10.10.4.12"
    selectionMap += "macAddress" -> "A4:5E:56:E9:A3:AA"
    selectionMap += "hostName" -> "RDP"
    selectionMap += "userName" -> "rdp"
    selectionMap += "riskScore" -> 0.0
    buf += selectionMap


    selectionMap += "ipAddress" -> "10.10.4.23"
    selectionMap += "macAddress" -> "A4:5B:60:D9:C3:DD"
    selectionMap += "hostName" -> "RDP-GW123"
    selectionMap += "userName" -> "rdpgw123"
    selectionMap += "riskScore" -> 0.7
    buf += selectionMap

    selectionMap += "ipAddress" -> "10.10.4.52"
    selectionMap += "macAddress" -> "A4:5E:60:E9:A3:DD"
    selectionMap += "hostName" -> "RDP-GW"
    selectionMap += "userName" -> "rdpgw"
    selectionMap += "riskScore" -> 0.903157142857
    buf += selectionMap


    selectionMap += "ipAddress" -> "10.10.4.33"
    selectionMap += "macAddress" -> "A4:5E:60:B9:F3:C1"
    selectionMap += "hostName" -> "RUDRA"
    selectionMap += "userName" -> "rudra"
    selectionMap += "riskScore" -> 0.602152142359
    buf += selectionMap


    selectionMap += "ipAddress" -> "10.10.4.16"
    selectionMap += "macAddress" -> "A4:5E:60:D9:B3:CA"
    selectionMap += "hostName" -> "John-WS"
    selectionMap += "userName" -> "john"
    selectionMap += "riskScore" -> 1.0
    buf += selectionMap

    // reset entityMap first
    entityMap = MutableMap[String, Any]()

    entityMap.put("entities", buf)
    entityMap.put("totalEntityCount", buf.size)
    entityMap.put("highRiskEntityCount", 3)
    entityMap.put("mediumRiskEntityCount", 2)
    entityMap.put("lowRiskEntityCount", 2)
  }

  @Test
  def getEntityCountGlobal() = {

    populateEntityMap()

    val input: QueryJson = new QueryJson()

    val response: Response = globalStatusFeature.getEntityCounts(input)

    assert(response != null)
    assert(response.getStatus == 200)
    assert(response.getEntity != null)

    val responseMap = JSON.parseFull(response.getEntity.toString).get.asInstanceOf[Map[String, Any]]

    assert(responseMap.get("highRiskEntityCount").get == entityMap.get("highRiskEntityCount").get)
    assert(responseMap.get("mediumRiskEntityCount").get == entityMap.get("mediumRiskEntityCount").get)
    assert(responseMap.get("lowRiskEntityCount").get == entityMap.get("lowRiskEntityCount").get)
    assert(responseMap.get("totalEntityCount").get == entityMap.get("totalEntityCount").get)
    assert(responseMap.get("entities").get.asInstanceOf[List[Map[String, Any]]].size ==
      entityMap.get("entities").get.asInstanceOf[ListBuffer[Map[String, Any]]].size)

    Logger.debug("Test Succeeded")
  }

  // This test checks whether cached result is used if request is made within same cache time interval
  @Test
  def getEntityCountGlobalSameCacheDuration() = {

    val input: QueryJson = new QueryJson()

    populateEntityMap()
    val responseFirst: Response = globalStatusFeature.getEntityCounts(input)

    // Change the data
    populateEntityMap2()
    val responseLater: Response = globalStatusFeature.getEntityCounts(input)

    val responseFirstMap = JSON.parseFull(responseFirst.getEntity.toString).get.asInstanceOf[Map[String, Any]]
    val responseLaterMap = JSON.parseFull(responseLater.getEntity.toString).get.asInstanceOf[Map[String, Any]]

    assert(responseFirst != null)
    assert(responseFirst.getStatus == 200)
    assert(responseFirst.getEntity != null)
    assert(responseLater != null)
    assert(responseLater.getStatus == 200)
    assert(responseLater.getEntity != null)

    assert(responseFirstMap.get("highRiskEntityCount").get == responseLaterMap.get("highRiskEntityCount").get)
    assert(responseFirstMap.get("mediumRiskEntityCount").get == responseLaterMap.get("mediumRiskEntityCount").get)
    assert(responseFirstMap.get("lowRiskEntityCount").get == responseLaterMap.get("lowRiskEntityCount").get)
    assert(responseFirstMap.get("totalEntityCount").get == responseLaterMap.get("totalEntityCount").get)
    assert(responseFirstMap.get("entities").get.asInstanceOf[List[Map[String, Any]]].size ==
      responseLaterMap.get("entities").get.asInstanceOf[List[Map[String, Any]]].size)

    Logger.debug("Test Succeeded")
  }

  @Test
  def getEntityCountForGivenTime() = {

    populateEntityMap2()

    val input: QueryJson = new QueryJson()
    input.setStartTime("2016-06-25T00:00:00.000Z")
    input.setEndTime("2016-06-26T00:00:00.000Z")

    val response: Response = globalStatusFeature.getEntityCounts(input)

    assert(response != null)
    assert(response.getStatus == 200)
    assert(response.getEntity != null)

    val responseMap = JSON.parseFull(response.getEntity.toString).get.asInstanceOf[Map[String, Any]]

    assert(responseMap.get("highRiskEntityCount").get == entityMap.get("highRiskEntityCount").get)
    assert(responseMap.get("mediumRiskEntityCount").get == entityMap.get("mediumRiskEntityCount").get)
    assert(responseMap.get("lowRiskEntityCount").get == entityMap.get("lowRiskEntityCount").get)
    assert(responseMap.get("totalEntityCount").get == entityMap.get("totalEntityCount").get)
    assert(responseMap.get("entities").get.asInstanceOf[List[Map[String, Any]]].size ==
      entityMap.get("entities").get.asInstanceOf[ListBuffer[Map[String, Any]]].size)

    Logger.debug("Test Succeeded")
  }

}

object GlobalStatusFeatureTest {

}