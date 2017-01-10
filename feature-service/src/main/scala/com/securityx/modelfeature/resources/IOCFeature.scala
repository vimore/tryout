package com.securityx.modelfeature.resources

import java.util
import javax.validation.Valid
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response}

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.common.inputs.DestinationSearch
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.dao.IOCDao
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.apache.solr.common.SolrDocument
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

/**
 * Created by harish on 12/5/14.
 */
@Path("/ioc")
@JsonInclude(Include.NON_NULL)
@Produces(Array(MediaType.APPLICATION_JSON))
class IOCFeature (val mapper: ObjectMapper, val solrClient:CloudSolrServer, val conf: FeatureServiceConfiguration) {

  private final val LOGGER: Logger = LoggerFactory.getLogger(classOf[IOCFeature])
  private val iocDao = new IOCDao(conf, solrClient)


  @GET
  @Timed
  def getIOCList() = {

    val buf = iocDao.getIOCList()
    val iocs = buf.map(x => scala.collection.mutable.Map[String, Any](
      "sourceAddress" -> x.get("sourceaddress"),
      "destinationAddress" -> x.get("destinationaddress"),
      "destinationNameOrIP" -> x.get("destinationnameorip"),
      "firstDateTime" -> x.get("firstDateTime"),
      "lastDateTime" -> x.get("lastDateTime"),
      "bitsIn" -> x.get("bitsIn"),
      "bitsOut" -> x.get("bitsOut"),
      "connections" -> x.get("connections")
    ))

    val a = mapper.writeValueAsString(iocs)
    Response.ok(a).build()
  }

  @GET
  @Path("/home")
  @Timed
  def getIOCData(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) = {

    val buf = iocDao.getIOCResults(startTime, endTime)
    val groups = buf.groupBy(x => Map[String, Any](
      "userName" -> { if(x.contains("userName") && !x.get("userName").equals(Some(null))) x.get ("userName") else  null },
      "hostName" -> { if(x.contains("hostName") && !x.get("hostName").equals(Some(null))) x.get ("hostName") else  null },
      "sourceAddress" -> { if(x.contains("sourceAddress") && !x.get("sourceAddress").equals(Some(null))) x.get ("sourceAddress") else  null },
      "threatClass" -> { if(x.contains("threatClass") && !x.get("threatClass").equals(Some(null))) x.get ("threatClass") else  null },
      "threatName" -> { if(x.contains("threatName") && !x.get("threatName").equals(Some(null))) x.get ("threatName") else  null }
    )
    )

    val answer = groups.map { case (key, value) => (
      Map(
        "key" -> key,
        "value" -> value.map {
          x => Map(
            "sourceAddress" -> { if(x.contains("sourceAddress") && !x.get("sourceAddress").equals(Some(null))) x.get ("sourceAddress") else  null },
            "destinationAddress" -> { if(x.contains("destinationAddress") && !x.get("destinationAddress").equals(Some(null))) x.get ("destinationAddress") else  null },
            "destinationNameOrIp" -> { if(x.contains("destinationNameOrIp") && !x.get("destinationNameOrIp").equals(Some(null))) x.get ("destinationNameOrIp") else  null },
            "firstDateTime" -> { if(x.contains("firstDateTime") && !x.get("firstDateTime").equals(Some(null))) x.get ("firstDateTime") else  null },
            "lastDateTime" -> { if(x.contains("lastDateTime") && !x.get("lastDateTime").equals(Some(null))) x.get ("lastDateTime") else  null },
            "dataSource" -> { if(x.contains("dataSource") && !x.get("dataSource").equals(Some(null))) x.get ("dataSource") else  null },
            "bitsIn" -> { if(x.contains("bitsIn") && !x.get("bitsIn").equals(Some(null))) x.get ("bitsIn") else  null },
            "bitsOut" -> { if(x.contains("bitsOut") && !x.get("bitsOut").equals(Some(null))) x.get ("bitsOut") else  null },
            "connections" -> { if(x.contains("connections") && !x.get("connections").equals(Some(null))) x.get ("connections") else  null },
            "threatValue" -> { if(x.contains("threatValue") && !x.get("threatValue").equals(Some(null))) x.get ("threatValue") else  null },
            "threatType" -> { if(x.contains("threatType") && !x.get("threatType").equals(Some(null))) x.get ("threatType") else  null },
            "history" -> { if(x.contains("history") && !x.get("history").equals(Some(null))) x.get ("history") else  null }

          )
        }
      )
      )
    }

    val a = mapper.writeValueAsString(answer)
    Response.ok(a).build()
  }

  @POST
  @Path("/counts")
  @Timed
  def getIocCounts(@Valid input : DestinationSearch) = {
    val result = iocDao.getIocCounts(input.getDateTime,input.getUrls, input.getDomains, input.getIps, input.getSourceAddress)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  @GET
  @Path("/summary")
  @Timed
  def getIocSummary(@QueryParam("startTime") startTime: String, @QueryParam("endTime") endTime: String) ={
    val result = iocDao.getIocSummary(startTime, endTime)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  @POST
  @Path("/history")
  @Timed
  def getHistoricalEventsForUrlDomains(@Valid input : DestinationSearch) ={
    val result: util.List[SolrDocument] = iocDao.getHistoryForIOC(input.getIps, input.getDomains, input.getUrls,
      input.getDateTime, input.getSourceAddress, input.getLastNDays)
    val a: String = this.mapper.writeValueAsString(result)
    Response.ok(a).build()
  }

  @POST
  @Path("/search")
  @Timed
  def getSearchIOC (blob: String, @QueryParam("startTime") startTime: String, @QueryParam("lastNDays") lastNDays: Int) = {
    val buf: mutable.Buffer[util.Map[String, AnyRef]] = scala.collection.JavaConversions.asScalaBuffer(
      iocDao.getIOCDataFromBlob(blob, startTime, lastNDays, conf))

    val groups = buf.groupBy(x => Map[String, Any](
      "userName" -> x.get("userName"),
      "hostName" -> x.get("hostName"),
      "sourceAddress" -> x.get("sourceAddress")
    )
    )

    val answer = groups.map { case (key, value) => (
      Map(
        "key" -> key,
        "value" -> value.map {
          x => Map(
            "destinationIp" -> x.get("destinationIp"),
            "firstDateTime" -> x.get("firstSeen"),
            "lastDateTime" -> x.get("lastSeen"),
            "bitsIn" -> x.get("bitsIn"),
            "bitsOut" -> x.get("bitsOut"),
            "connections" -> x.get("connections"),
            "threatValue" -> x.get("threatValue"),
            "history" -> x.get("history"),
            "threatType" -> x.get("threatType")

          )
        }
      )
      )
    }

    val a = mapper.writeValueAsString(answer)
    Response.ok(a).build()
  }
}
