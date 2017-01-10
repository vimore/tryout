package com.e8.palam.resources

import java.util
import javax.ws.rs.core.Response

import com.e8.palam.TestBase
import com.google.common.collect.{Lists, Maps}
import com.securityx.modelfeature.FeatureServiceCache
import com.securityx.modelfeature.common.EntityModelInfo
import com.securityx.modelfeature.common.cache.AutoCompleteCache
import com.securityx.modelfeature.common.inputs.QueryJson
import com.securityx.modelfeature.dao.DetectorHomeDao
import com.securityx.modelfeature.resources.DetectorHomeFeature
import org.junit.{Before, Test}
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map => MutableMap}

class DetectorHomeFeatureTest extends TestBase{


  val testDateTime = "2015-02-04T00:00:00.000Z"
  val testStartTime = "2015-01-04T00:00:00.000Z"
  val testEndTime = "2015-02-10T00:00:00.000Z"
  val testIpAddress = "10.10.10.10"
  val testUserName = "testUserName"
  val testHostName = "testHost"

  private val detectorHomeFeature: DetectorHomeFeature = new DetectorHomeFeature(mapper, conf, cache, autoCompleteCache)

  private var outputBuff: collection.mutable.ListBuffer[MutableMap[String, Any]] =  _
  private var entityCardDaoOutput : Map[collection.mutable.Map[String, Any], ListBuffer[mutable.Map[String, Any]]] = _

  private var autoCompleteList: java.util.List[String] = new java.util.LinkedList[String]()
  private var autoCompleteMap = new java.util.HashMap[String, util.List[String]]()

  @Before
  override def setup() = {
    super.setup()
    outputBuff = collection.mutable.ListBuffer.empty[MutableMap[String, Any]]

    val detectorHomeDaoMock =  new DetectorHomeDao(conf){
      override def getEntityRiskScores(input: QueryJson, cache: FeatureServiceCache, isSearch: Boolean, returnEntityIds: Boolean) = {
        outputBuff
      }

      override def getKillchainCounts(startTime: String, endTime: String, cache: FeatureServiceCache) = {
        outputBuff
      }

      override def getEntityCards( startTime: String, endTime: String,
                                   ip: String, userName: String, hostName: String, macAddress: String,
                                   cache: FeatureServiceCache):  Map[MutableMap[String, Any],
        ListBuffer[MutableMap[String, Any]]]  ={
        entityCardDaoOutput
      }

      override def getAutoCompleteResults(incomingString: String,
                                          fieldName: String,
                                          autoCompleteCache: AutoCompleteCache): util.List[String] = {
        autoCompleteList
      }

      override def getAutoCompleteResultsOnAll(incomingString: String,
                                               autoCompleteCache: AutoCompleteCache)
      : util.Map[String, util.List[String]] = {

        autoCompleteMap
      }
    }
    detectorHomeFeature.setDetectorHomeDao(detectorHomeDaoMock)

  }

  def prepareAutocompleteForFieldIP()={
    autoCompleteList = new java.util.LinkedList[String]()
    autoCompleteList.add("10.10.4.102")
    autoCompleteList.add("10.10.4.42")
    autoCompleteList.add("10.10.4.54")

  }

  def prepareAutocompleteForFieldUsers()={
    autoCompleteList = new java.util.LinkedList[String]()
    autoCompleteList.add("w2k12-srv$")
    autoCompleteList.add("w2k8r2-ad-2$")
    autoCompleteList.add("w2k8r2-src$")

  }

  def prepareAutocompleteForFieldHosts()={
    autoCompleteList = new java.util.LinkedList[String]()
    autoCompleteList.add("administrator")
    autoCompleteList.add("Alexanders-MBP")
    autoCompleteList.add("android-30868f84ba3fa83f")

  }

  def prepareAutocompleteForAll()={
    val ipResult = new java.util.LinkedList[String]()
    ipResult.add("10.10.4.102")
    ipResult.add("10.10.4.42")
    ipResult.add("10.10.4.54")

    val hostsResult = new java.util.LinkedList[String]()
    hostsResult.add("administrator")
    hostsResult.add("Alexanders-MBP")
    hostsResult.add("android-30868f84ba3fa83f")

    val usersResult = new java.util.LinkedList[String]()
    usersResult.add("a2k12-srv$")
    usersResult.add("a2k8r2-ad-2$")
    usersResult.add("a2k8r2-src$")

    autoCompleteMap = new java.util.HashMap[String, util.List[String]]()
    autoCompleteMap.put("ipAddresses", ipResult)
    autoCompleteMap.put("hostNames", hostsResult)
    autoCompleteMap.put("userNames", usersResult)
  }

  def prepareEntityCardOutputData() = {
    val buf = new ListBuffer[MutableMap[String, Any]]()
    val selectionMap = collection.mutable.Map[String, Any]()
    selectionMap += "dateTime" -> testDateTime
    selectionMap += "sourceIp" -> testIpAddress
    selectionMap += "sourceUserName" -> testUserName
    selectionMap += "sourcePort" -> 8080
    selectionMap += "destinationIp" -> "193.12.12.3"
    selectionMap += "destinationUserName" -> "destinationuser"
    selectionMap += "destinationPort" -> 9090
    selectionMap +=  "winEventId" -> 111
    selectionMap +=  "winEventType" -> "Test Win Event Type"
    selectionMap +=  "bytesIn" -> 111
    selectionMap +=  "bytesOut" -> 1
    selectionMap +=  "httpMethod" -> "GET"
    selectionMap +=  "userAgent" -> "TEST USER AGENT"
    selectionMap +=  "httpResponseCode" -> 201
    selectionMap += "modelId" -> 1
    selectionMap += "eventId" -> 1
    selectionMap += "featureDesc" -> "Test Feature Description"
    selectionMap += "killchainId" -> 3
    selectionMap += "cardId" -> 1
    selectionMap += "interval" -> 4800
    selectionMap += "riskScore" -> "0.9"
    selectionMap += "eventDescription" -> 1
    selectionMap += "sourceUserName" -> "user"
    selectionMap += "riskScore" -> "0.9"
    buf += selectionMap

    entityCardDaoOutput =
      buf.groupBy(x => collection.mutable.Map[String, Any](
        "modelId" -> x.get("modelId"),
        "killchainId" -> x.get("killchainId"),
        "cardId" -> x.get("cardId"),
        "eventId" -> x.get("eventId"),
        "featureDesc" -> x.get("featureDesc"),
        "riskScore" -> 0
      )
      )



  }

  @Test
  def testAutocompleteForIP(): Unit = {
    prepareAutocompleteForFieldIP()
    val response : Response = detectorHomeFeature.getAutoCompleteResults("1","sourceIp")
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(autoCompleteList)))

  }

  @Test
  def testAutocompleteForUsers(): Unit = {
    prepareAutocompleteForFieldUsers()
    val response : Response = detectorHomeFeature.getAutoCompleteResults("w","userName")
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(autoCompleteList)))

  }

  @Test
  def testAutocompleteForHosts(): Unit = {
    prepareAutocompleteForFieldHosts()
    val response : Response = detectorHomeFeature.getAutoCompleteResults("a","hostName")
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(autoCompleteList)))

  }

  @Test
  def testAutocompleteForAll(): Unit = {
    prepareAutocompleteForAll()
    val response : Response = detectorHomeFeature.getAutoCompleteResults("a","")
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(autoCompleteMap)))

  }

  @Test
  def testGetEntityInfo(): Unit = {
    val map: MutableMap[String, Any] =  MutableMap[String, Any] ()
    map += "ipAddress" -> "10.10.10.10"
    map += "hostName" -> "testHost"
    map += "userName" -> "testUser"
    map += "macAddress" -> ""
    map += "currentScore" -> "0.9"
    map += "processes" -> ""
    map += "md5s" -> ""
    map += "currentSecurityEventId" -> ""
    map += "currentModelId" -> ""
    map += "currentCardId" -> ""
    map += "currentDateTime" -> ""
    val list: ListBuffer[EntityModelInfo] = new ListBuffer[EntityModelInfo]()
    val eventInfo: EntityModelInfo = new EntityModelInfo(1, 3, "test feature label",
      "IAM", "MULTIPLE LOGONS", "test descriptions", "test short desc", 3, 1, 0.9,"")
    list += eventInfo
    map += "modelScores" -> list
    outputBuff += map

    val response : Response = detectorHomeFeature.getEntityRiskInfo(getTestInputJson)
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(outputBuff)))

  }

  @Test
  def testGetEntityInfoEmptyResponse(): Unit = {
    val response : Response = detectorHomeFeature.getEntityRiskInfo(getTestInputJson)
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(outputBuff)))

  }

  @Test
  def testGetKillchainCounts() = {

    val mapping = MutableMap[Int, MutableMap[String,Any]]()

    val killchainIdKey = "killchainId"
    val countKey = "count"

    //iterate over all the killchainIds and add count = 0 for each.
    // This is because, we want to show count = 0 even when any killchain is not found in our data
    cache.getKillchainMap.keySet.foreach{i =>
      val selectionMap = MutableMap[String, Any]()
      selectionMap += killchainIdKey -> i
      selectionMap += countKey -> Math.random()
      outputBuff += selectionMap
      mapping += i.intValue() ->selectionMap
    }

    val response : Response = detectorHomeFeature.getKillchainCounts(testStartTime, testEndTime)
    assert(response != null)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assert(entity.toString.equals(mapper.writeValueAsString(outputBuff)))
  }

  @Test
  def testGetEntityCards() = {
    prepareEntityCardOutputData()
    val answer = entityCardDaoOutput.map { case (key, value) =>
      Map(
        "key" -> key,
        "value" -> value.map {
          x => Map(
            "dateTime" ->  x.get("dateTime"),
            "sourceIp" ->  { if(x.contains("sourceIp") && !x.get("sourceIp").equals(Some(null))) x.get("sourceIp") else "N/A" },
            "sourceUserName" -> { if(x.contains("sourceUserName") && !x.get("sourceUserName").equals(Some(null)) ) x.get("sourceUserName") else "N/A" },
            "destinationIp" -> { if(x.contains("destinationIp") && !x.get("destinationIp").equals(Some(null))) x.get("destinationIp") else "N/A" },
            "destinationUserName" ->{ if(x.contains("destinationUserName") && !x.get("destinationUserName").equals(Some(null))) x.get("destinationUserName") else "N/A" },
            "sourcePort" -> { if(x.contains("sourcePort") && !x.get("sourcePort").equals(Some(null))) x.get("sourcePort") else "N/A" },
            "destinationPort" -> { if(x.contains("destinationPort") && !x.get("destinationPort").equals(Some(null))) x.get("destinationPort") else "N/A" },
            "winEventType" -> { if(x.contains("winEventType") && !x.get("winEventType").equals(Some(null))) x.get("winEventType") else "N/A" },
            "winEventId" -> { if(x.contains("winEventId") && !x.get("winEventId").equals(Some(null))) x.get("winEventId") else "N/A" },
            "bytesIn" -> { if(x.contains("bytesIn") && !x.get("bytesIn").equals(Some(null))) x.get("bytesIn") else "N/A" },
            "bytesOut" -> { if(x.contains("bytesOut") && !x.get("bytesOut").equals(Some(null))) x.get("bytesOut") else "N/A" },
            "url" -> { if(x.contains("url") && !x.get("url").equals(Some(null))) x.get("url") else "N/A" },
            "isDaily" -> { if(x.contains("isDaily") && !x.get("isDaily").equals(Some(null))) x.get("isDaily") else true },
            "httpMethod" -> { if(x.contains("httpMethod") && !x.get("httpMethod").equals(Some(null))) x.get("httpMethod") else "N/A" },
            "httpResponseCode" -> { if(x.contains("httpResponseCode") && !x.get("httpResponseCode").equals(Some(null))) x.get("httpResponseCode") else "N/A" },
            "userAgent" -> { if(x.contains("userAgent") && !x.get("userAgent").equals(Some(null))) x.get("userAgent") else "N/A" },
            "interval" -> { if(x.contains("interval") && !x.get("interval").equals(Some(null))) x.get("interval") else "N/A" },     //applicable only for beacons
            "targetId" -> { if(x.contains("targetId") && !x.get("targetId").equals(Some(null))) x.get("targetId") else "N/A" },
            "targetDescription" -> { if(x.contains("targetDescription") && !x.get("targetDescription").equals(Some(null))) x.get("targetDescription") else "N/A" },
            "featureKeyCount" -> { if(x.contains("featureKeyCount") && !x.get("featureKeyCount").equals(Some(null))) x.get("featureKeyCount") else "N/A" },
            "risk" -> { if(x.contains("riskScore")) x.get("riskScore") else 0.0 },
            "processes" ->  { if(x.contains("processes") && !x.get("processes").equals(Some(null))) x.get("processes") else "N/A" },
          "md5s" -> { if(x.contains("md5s") && !x.get("md5s").equals(Some(null))) x.get("md5s") else "N/A" },
          "ports" -> { if(x.contains("ports") && !x.get("ports").equals(Some(null))) x.get("ports") else "N/A" },
          "hosts" -> { if(x.contains("hosts") && !x.get("hosts").equals(Some(null))) x.get("hosts") else "N/A" },
          "paths" -> { if(x.contains("paths") && !x.get("paths").equals(Some(null))) x.get("paths") else "N/A" },
          "datesSeen" -> { if(x.contains("datesSeen") && !x.get("datesSeen").equals(Some(null))) x.get("datesSeen") else "N/A" },
          "firstTimeSeen" -> { if(x.contains("firstTimeSeen") && !x.get("firstTimeSeen").equals(Some(null))) x.get("firstTimeSeen") else "N/A" },
          "nox" -> { if(x.contains("nox") && !x.get("nox").equals(Some(null))) x.get("nox") else "N/A" },
          "groupInffo" ->  { if(x.contains("groupInffo") && !x.get("groupInffo").equals(Some(null))) x.get("groupInffo") else "N/A" },
            "groupInfo" ->  { if(x.contains("groupInfo") && !x.get("groupInfo").equals(Some(null))) x.get("groupInfo") else "N/A" },
          "isAutorun" ->  { if(x.contains("isAutorun") && !x.get("isAutorun").equals(Some(null))) x.get("isAutorun") else "N/A" },
          "isDaily" -> true
          )
        }
      )
    }
    val response : Response  = detectorHomeFeature.getEntityCards(testStartTime, testEndTime, testIpAddress, testUserName, testHostName, null)
    assertNotNull(response)
    val status : Int = response.getStatus
    assert(status == 200)
    val entity = response.getEntity
    assertEquals(mapper.writeValueAsString(answer), entity.toString)
  }

  /**
   *
   * @return
   */
  def getTestInputJson : QueryJson = {
    val testInputJson: QueryJson = new QueryJson()
    testInputJson.setStartTime(testStartTime)
    testInputJson.setEndTime(testEndTime)

    val queryList : util.List[util.Map[String, AnyRef]] = Lists.newLinkedList()

    var filterMap : util.Map[String, AnyRef] = Maps.newHashMap()

    //filtering by killchain ids
    filterMap.put("field", "killchainId".asInstanceOf[AnyRef])
    filterMap.put("operator", "equals".asInstanceOf[AnyRef])

    var values: util.ArrayList[String] = Lists.newArrayList()
    values.add("1")
    values.add("2")
    values.add("3")
    filterMap.put("values", values)

    queryList.add(filterMap)

    filterMap = Maps.newHashMap()

    //filtering by RISK
    filterMap.put("field", "risk".asInstanceOf[AnyRef])
    filterMap.put("operator", "greater than equal".asInstanceOf[AnyRef])

    values = Lists.newArrayList()
    values.add("0.8")
    filterMap.put("values", values)

    queryList.add(filterMap)

    testInputJson.setQuery(queryList)
    testInputJson.setSortField("Hostname")
    testInputJson.setSortOrder("asc")
    testInputJson.setLimit(100)

    testInputJson
  }

}

object DetectorHomeFeatureTest{

}
