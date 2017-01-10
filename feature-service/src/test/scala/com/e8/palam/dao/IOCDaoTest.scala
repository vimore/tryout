package com.e8.palam.dao

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import org.junit.Before

import scala.collection.mutable.{Map => MutableMap}

/**
 * Created by harish on 12/13/14.
 */
class IOCDaoTest {
  private var conf: FeatureServiceConfiguration = null
  private val zkString: String = "10.10.30.51"

  @Before
  def setup {
    conf = new FeatureServiceConfiguration
    conf.setZkQuorum(zkString)
    conf.setEnvironment("DEV")
  }

}
