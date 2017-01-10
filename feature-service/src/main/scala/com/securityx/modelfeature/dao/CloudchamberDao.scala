package com.securityx.modelfeature.dao

import com.cloudera.org.joda.time.DateTimeZone
import com.securityx.modelfeature.HttpClientWrapper
import com.securityx.modelfeature.config.{CloudChamberConfiguration, FeatureServiceConfiguration}
import org.apache.http.HttpResponse
import org.apache.http.impl.client.DefaultHttpClient
import org.joda.time.format.DateTimeFormat
import org.json.{JSONException, JSONObject}

// To be used when we convert to using the joda time handling.
//import org.joda.time.format.{ISODateTimeFormat, DateTimeFormatter}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by mdeshon on 9/24/14.
 */
class CloudChamberDao (conf:FeatureServiceConfiguration) extends BaseDao(conf) {
  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[CloudChamberDao])

  var cc: CloudChamberConfiguration = conf.getCloudchamber
  val CLOUD_CHAMBER_METRIC_API = new URIBuilder()
      .setScheme(conf.getCloudchamber.getScheme())
      .setHost(cc.getHost)
      .setPort(conf.getCloudchamber.getPort.toInt)
      .setPath("/whois/metrics")
  val CLOUD_CHAMBER_DOMAINWHOIS_API = new URIBuilder()
    .setScheme(conf.getCloudchamber.getScheme())
    .setHost(cc.getHost)
    .setPort(conf.getCloudchamber.getPort.toInt)
    .setPath("/adv/domainwhois")
    .setParameter("userid", cc.getUserId)
    .setParameter("emailid", cc.getEmailId)
  val CLOUD_CHAMBER_WHOIS_IP_API = new URIBuilder()
    .setScheme(conf.getCloudchamber.getScheme())
    .setHost(cc.getHost)
    .setPort(conf.getCloudchamber.getPort.toInt)
    .setPath("/adv/whois/ip")
    .setParameter("userid", cc.getUserId)
    .setParameter("emailid", cc.getEmailId)

  def getDomainWhois(domain: String) : String = {
    val uribuilder: URIBuilder = CLOUD_CHAMBER_DOMAINWHOIS_API
    val uri = uribuilder.setParameter("domain", domain).build()
    LOGGER.debug("URL is: " + uri.toString)
    val httpGet = new HttpGet(uri)
    val httpClient = HttpClientWrapper.wrapCloudChamberClient(new DefaultHttpClient(), cc.getPROXY_SCHEME, cc.getPROXY_HOST, cc.getPROXY_PORT, cc.getPROXY_USER, cc.getPROXY_PASSWORD) // Deprecated, but until org.apache.httpcomponents 4.3 we can't upgrade.
    httpGet.addHeader(cc.getX_HMAC_NONCE, cc.getWHOIS_X_HMAC_NONCE)
    httpGet.addHeader(cc.getAUTHORIZATION, cc.getWHOIS_AUTHORIZATION)
    var response : String = "{}"
    try {
      val httpResponse : HttpResponse = httpClient.execute(httpGet)
      val content = httpResponse.getEntity.getContent
      response = scala.io.Source.fromInputStream(content).getLines.mkString("\n")
      content.close()
      LOGGER.debug("Response is: " + response)
    } catch {
      case ex: Exception => LOGGER.error("Could not reach cloudchamber: ", ex)
    }

    response
  }

  def getDomainWhoisJSON(domain: String) : org.json.JSONObject = {
    var responseString = getDomainWhois(domain)

    var jsonResponse: JSONObject = null
    //return scala.util.parsing.json.JSON.parseRaw(response).get
    try{
      jsonResponse = new org.json.JSONObject(responseString)
    }catch{
      case ex: JSONException => LOGGER.error("Invalid Json. Failed to get DomainWhoIs => " + ex)
    }
    jsonResponse
  }

  def getWhoisByIp(hostIp: String) : String = {
    val uribuilder: URIBuilder = CLOUD_CHAMBER_WHOIS_IP_API
    val uri = uribuilder.setParameter("ip", hostIp).build()
    LOGGER.debug("URL is: " + uri.toString)
    val httpGet = new HttpGet(uri)
    val httpClient = HttpClientWrapper.wrapCloudChamberClient(new DefaultHttpClient(), cc.getPROXY_SCHEME, cc.getPROXY_HOST, cc.getPROXY_PORT, cc.getPROXY_USER, cc.getPROXY_PASSWORD) // Deprecated, but until org.apache.httpcomponents 4.3 we can't upgrade.
    httpGet.addHeader(cc.getX_HMAC_NONCE, cc.getWHOIS_X_HMAC_NONCE)
    httpGet.addHeader(cc.getAUTHORIZATION, cc.getWHOIS_AUTHORIZATION)
    var response : String = "{}"
    try {
      val httpResponse: HttpResponse = httpClient.execute(httpGet)
      val content = httpResponse.getEntity.getContent
      response = scala.io.Source.fromInputStream(content).getLines.mkString("\n")
      content.close()
      LOGGER.debug("Response is: " + response)
    } catch {
      case ex: Exception => LOGGER.error("Could not reach cloudchamber: ", ex)
    }

    response
  }

  def getDomainDate(domain: String) : Map[String, String] = {
    val whois: org.json.JSONObject = this.getDomainWhoisJSON(domain)
    val domainDateKey: String = "domainDate"
    val creationDateKey: String = "creationDate"
    val updatedDateKey: String = "updatedDate"
    val lastUpdatedDateKey: String = "lastUpdatedDate"
    var response: MutableMap[String, String] = MutableMap[String, String]()

    // Check dates in this order: creationDate, updatedDate, lastUpdatedDate. First date that
    // exists is the one returned.
    if (whois.has(creationDateKey)) {
      val creationDate: String = whois.getString(creationDateKey)
      if (creationDate != null && creationDate != "") {
        val date = this.standardizeDate(creationDate)
        if (date != null) {
          response += domainDateKey -> this.standardizeDate(creationDate)
          return response.toMap
        }
      }
    }

    if (whois.has(updatedDateKey)) {
      val updatedDate: String = whois.getString(updatedDateKey)
      if (updatedDate != null && updatedDate != "") {
        val date = this.standardizeDate(updatedDate)
        if (date != null) {
          response += domainDateKey -> this.standardizeDate(updatedDate)
          return response.toMap
        }
      }
    }

    // If none of the above dates exists, return a null record.
    response += domainDateKey -> null
    response.toMap
  }

  /**
   * Gets the Creation dateTime string for an input domain.
   * This is obtained by querying the CloudChamber. If no information is available, the method returns a null value
   *
   * @param domain String specifying Domain
   * @return String Creation dateTime for the Domain. null if no information available
   */
  def getDomainCreationDate(domain: String) : String = {
    val whois: org.json.JSONObject = this.getDomainWhoisJSON(domain)
    val creationDateKey: String = "creationDate"
    var date: String = null
    if (whois != null && whois.has(creationDateKey)) {
      val creationDate: String = whois.getString(creationDateKey)
      if (creationDate != null && creationDate != "") {
        date = this.standardizeDate(creationDate)
      }
    }
    date
  }

  private def standardizeDate(date_string: String) : String = {
    val datePattern = new scala.util.matching.Regex(
      """^(\d{4})-(\d{2})-(\d{2})(?:T| )(\d{2}):(\d{2}):(\d{2})(?:\.\d{3})? *([-+]\d+|UTC)""",
      "year", "month", "day", "hour", "minute", "second", "utcoffset")
    val formatted_date = datePattern.findFirstIn(date_string) match {
      case Some(datePattern(year, month, day, hour, minute, second, utcoffset)) => (year + "-" + month + "-"
        + day + "T" + hour + ":" + minute + ":" + second + {if(utcoffset.equalsIgnoreCase("utc")) ".000Z" else utcoffset})
      case None => null
    }
    if (formatted_date == null) {
      return null
    } else {
      val output_format = "yyyy-MM-dd'T'hh:mm:ss'.000Z'"
      val fmt = DateTimeFormat.forPattern(output_format).withZoneUTC()
      val output_string: String = fmt.print(DateTime.parse(formatted_date))
      return output_string
    }
  }

  /**
   * Returns the Cloud Chamber Server status. Returns true if server is up/running, false otherwise.
   *
   * @return Boolean
   */
  def getCloudChamberStatus() : Boolean = {
    var isCloudChamberUp = false;

    try{
      val uriBuilder: URIBuilder = CLOUD_CHAMBER_METRIC_API
      val uri = uriBuilder.build()
      LOGGER.debug("CC Metric URL is: " + uri.toString)
      val httpGet = new HttpGet(uri)
      val httpClient = HttpClientWrapper.wrapCloudChamberClient(new DefaultHttpClient(), cc.getPROXY_SCHEME, cc.getPROXY_HOST, cc.getPROXY_PORT, cc.getPROXY_USER, cc.getPROXY_PASSWORD) // Deprecated, but until org.apache.httpcomponents 4.3 we can't upgrade.
      val httpResponse : HttpResponse = httpClient.execute(httpGet)
      val content = httpResponse.getEntity.getContent
      val response = scala.io.Source.fromInputStream(content).getLines.mkString("\n")
      content.close()
      LOGGER.debug("CC Metric Response is: " + response)

      // if response is not empty then server is up
      if(!response.isEmpty){
        isCloudChamberUp = true;
      }
    }catch {
      case e: Exception => LOGGER.error("Failed to get Cloud Chamber Server Status => " + e)
    }

    isCloudChamberUp
  }
}

object CloudChamberDao { }
