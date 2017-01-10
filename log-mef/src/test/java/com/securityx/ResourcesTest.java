/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx;

import com.securityx.logcollection.parser.configchecker.ConfigChecker;
import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 *
 * @author jyrialhon
 */
public class ResourcesTest extends TestCase {
  
  public void testGetResource() throws URISyntaxException, IOException{
     
    Config out = MorphlineResourceLoader.getConfFile("epilog-to-mef.conf");
    for (Map.Entry<String, ConfigValue> e : out.entrySet()){
      OutUtils.printOut(e.getKey());
    }
    Assert.assertTrue("has morphline", out.hasPath("morphlines"));
  
  }
  
  public void testGetConfigResource() throws URISyntaxException, IOException{
	     
	   ConfigChecker.checkConfig();
	  }
	  

  public void testGetResource2() throws URISyntaxException, IOException{
     
    Config out = MorphlineResourceLoader.getConfFile("logcollection-script-selector-command-list.conf");
    for (Map.Entry<String, ConfigValue> e : out.entrySet()){
      OutUtils.printOut(e.getKey());
    }
    Assert.assertTrue("has morphline", out.hasPath("morphlines"));
    
  
  }
 
}
