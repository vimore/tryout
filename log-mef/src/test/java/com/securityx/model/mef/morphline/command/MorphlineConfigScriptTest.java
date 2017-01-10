package com.securityx.model.mef.morphline.command;

import com.securityx.logcollection.utils.ConfigLoader;
import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class MorphlineConfigScriptTest extends TestCase {



  @Test
  public void test_simple2() throws FileNotFoundException {

    Config defaultSettings = ConfigFactory.load("morphline/parsing_config-test.conf");
    assertEquals("has expected value", "#", defaultSettings.getString("mytest.kvpSpliter.fieldValueSep"));
    assertEquals("has expected value", "'", defaultSettings.getString("mytest.kvpSpliter.quoteChar"));

    ConfigLoader.main(new String[]{});
    Config userSettings = ConfigFactory.load("test/logparsingjob-test.conf");
    assertEquals("has expected value", "%%", userSettings.getString("mytest.kvpSpliter.fieldValueSep"));
    assertEquals("has expected value", "'", userSettings.getString("mytest.kvpSpliter.quoteChar"));

    Config c = MorphlineResourceLoader.getConfFile("test/test-command-config.conf");

    assertEquals("has expected value", "%%", c.getString("mytest.kvpSpliter.fieldValueSep"));
    assertEquals("has expected value", "'", c.getString("mytest.kvpSpliter.quoteChar"));

  }

}
