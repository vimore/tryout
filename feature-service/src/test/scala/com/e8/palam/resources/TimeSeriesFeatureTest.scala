package com.e8.palam.resources

import java.io.{ByteArrayInputStream, File, InputStream}
import java.nio.charset.StandardCharsets
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.validation.{Validation, Validator}
import javax.ws.rs.core.Response

import com.fasterxml.jackson.databind.ObjectMapper
import com.securityx.modelfeature.{FeatureServiceCache, SolrServerClient}
import com.securityx.modelfeature.common.cache.{AutoCompleteCache, FeatureResponseCache}
import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.resources.TimeSeriesFeature
import com.securityx.modelfeature.utils.{Constants, SearchUtils}
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.apache.solr.client.solrj.impl.CloudSolrServer
import org.junit.{Before, Test}
import org.junit.Assert._
import org.mockito.Mockito
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by ramv on 7/22/16.
  */
class TimeSeriesFeatureTest {
  private val Logger: Logger = LoggerFactory.getLogger(classOf[TimeSeriesFeatureTest])

  object TimeSeriesFeatureTest{
    val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
    val mapper: ObjectMapper = Jackson.newObjectMapper
    val confFile: String = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"
    val configuration = new ConfigurationFactory[FeatureServiceConfiguration](classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))

    var featureServiceCache: FeatureServiceCache = new FeatureServiceCache(configuration)
    var autoCompleteCache: AutoCompleteCache = new AutoCompleteCache(configuration)
    var featureResponseCache: FeatureResponseCache = new FeatureResponseCache(configuration)
    val solrClient: SolrServerClient = new SolrServerClient(configuration)
    val webProxySolrClient: CloudSolrServer = solrClient.getSolrServer(SearchUtils.getCollectionName(Constants.WebPeerAnomaliesModelTuple._1))
    val iamSolrClient: CloudSolrServer = solrClient.getSolrServer(SearchUtils.getCollectionName(Constants.ADPeerAnomaliesModelTuple._1))
    val taniumUetSolrClient: CloudSolrServer = solrClient.getSolrServer(Constants.TANIUM_UET_MEF_COLLECTION)
    val taniumHetSolrClient: CloudSolrServer = solrClient.getSolrServer(Constants.TANIUM_HET_MEF_COLLECTION)
    val taniumHostInfoSolrClient: CloudSolrServer = solrClient.getSolrServer(Constants.TANIUM_HOST_INFO_MEF_COLLECTION)
  }
  /**
    * A Mock ServletInputStream. Pass in any ol InputStream like a ByteArrayInputStream.
    *
    * @author Steve Jenson (stevej@pobox.com)
    */
  class MockServletInputStream(is: InputStream) extends ServletInputStream {
    def read() = is.read()
    def isFinished(): Boolean = is.available() > 0
    def isReady(): Boolean = true
  }

  @Test
  def getEndPointAnalyticsFailure(): Unit ={
    val is : InputStream = new ByteArrayInputStream("{ blah }".getBytes(StandardCharsets.UTF_8))
    val request : HttpServletRequest = Mockito.mock(classOf[HttpServletRequest])
    Mockito.when(request.getInputStream).thenReturn(new MockServletInputStream(is))
    val tsf = new TimeSeriesFeature(TimeSeriesFeatureTest.mapper,
                                    TimeSeriesFeatureTest.webProxySolrClient,
                                    TimeSeriesFeatureTest.iamSolrClient,
                                    TimeSeriesFeatureTest.taniumHostInfoSolrClient,
                                    TimeSeriesFeatureTest.taniumHetSolrClient,
                                    TimeSeriesFeatureTest.taniumUetSolrClient,
                                    TimeSeriesFeatureTest.configuration, TimeSeriesFeatureTest.featureServiceCache,
                                    TimeSeriesFeatureTest.featureResponseCache)
    val resp : Response = tsf.getEndPointAnalytics(request)
    assertEquals(400, resp.getStatus)
    val body  = resp.getEntity().toString
    assertTrue(body.contains("400"))
  }
}
