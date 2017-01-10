package com.e8.palam

import com.securityx.modelfeature.config.FeatureServiceConfiguration
import com.securityx.modelfeature.utils._
import java.io.File
import javax.validation.{Validation, Validator}

import com.fasterxml.jackson.databind.ObjectMapper
import io.dropwizard.configuration.ConfigurationFactory
import io.dropwizard.jackson.Jackson
import org.junit.Test
import org.junit.Assert._
/**
  * Created by ramv on 7/26/16.
  */
class MiscUtilsTest{

  @Test
  def getQueryFragmmentReplaceNullValue() = {
    val mapper: ObjectMapper = Jackson.newObjectMapper
    val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
    val confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"
    val conf = new ConfigurationFactory (classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))

    val (sql, exists) = MiscUtils.getQueryFragmmentReplaceNullValueExists("10.10.89.12", EntityThreat.IP_ADDRESS, "SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123", conf)
    assertTrue(exists)
    assertEquals("SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123 AND IP_ADDRESS = ? ", sql)

    val (sql1, exists1) = MiscUtils.getQueryFragmmentReplaceNullValueExists("", EntityThreat.IP_ADDRESS, "SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123", conf)
    assertTrue(exists1)
    assertEquals("SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123 AND IP_ADDRESS = 'NULL_VALUE' ", sql1)

    conf.getFixNullValue.setEnabled(false)
    val (sql2, exists2) = MiscUtils.getQueryFragmmentReplaceNullValueExists("", EntityThreat.IP_ADDRESS, "SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123", conf)
    assertTrue(exists2)
    assertEquals("SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123 AND IP_ADDRESS = '' ", sql2)

    conf.getFixNullValue.setEnabled(true)
    val (sql3, exists3) = MiscUtils.getQueryFragmmentReplaceNullValueExists(null, EntityThreat.IP_ADDRESS, "SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123", conf)
    assertFalse(exists3)
    assertEquals("SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123 AND IP_ADDRESS IS NULL ", sql3)

    conf.getFixNullValue.setEnabled(false)
    val (sql4, exists4) = MiscUtils.getQueryFragmmentReplaceNullValueExists(null, EntityThreat.IP_ADDRESS, "SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123", conf)
    assertFalse(exists4)
    assertEquals("SELECT * FROM "+EntityThreat.getName(conf)+" WHERE RISK_SCORE = 0.123 AND IP_ADDRESS IS NULL ", sql4)

  }

  @Test
  def stringReplaceNullValueTestNull(): Unit ={
    val mapper: ObjectMapper = Jackson.newObjectMapper
    val validator: Validator = Validation.buildDefaultValidatorFactory.getValidator
    val confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml"
    val conf = new ConfigurationFactory (classOf[FeatureServiceConfiguration], validator, mapper, "dw").build(new File(confFile))
    val str : String = MiscUtils.stringReplaceNullValue(null, conf)
    assertEquals("", str)
    // Should not throw a NullPointerException
  }
}
