/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.utils;

import com.typesafe.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Map;

/**
 *
 * @author jyrialhon
 */
public class MorphlineResourceLoader {

  private static String MORPHLINEDIR = "morphline";
  private static ConfigResolveOptions opts = ConfigResolveOptions.defaults();
  private static Logger logger = LoggerFactory.getLogger(MorphlineResourceLoader.class);

  public MorphlineResourceLoader() {

  }
  public static Config getConfFile(String fileName){
    return getConfFile(fileName, opts);
  }
  
  public static Config getConfFile(String fileName, ConfigResolveOptions opts) {
    String resourcePath = MORPHLINEDIR+"/"+fileName;
    ConfigParseOptions confParseOpts = ConfigParseOptions.defaults();
    Config conf = ConfigFactory.load(resourcePath, ConfigParseOptions.defaults(), opts);
    
    if (conf.hasPath(resourcePath)){
      return conf.getConfig(resourcePath);
    }
    if (conf.hasPath("morphlines")){
      return conf;
    }else{
        if (logger.isDebugEnabled()){
            logger.debug("ERROR : resource : "+MORPHLINEDIR+"/"+fileName+" not found");
            logger.debug("ERROR : trying : ./"+MORPHLINEDIR+"/"+fileName+"");
        }
      conf = ConfigFactory.load(MORPHLINEDIR, ConfigParseOptions.defaults(), opts);
      logger.debug("troubleshooting "+MORPHLINEDIR+" resources");
      for (Map.Entry<String, ConfigValue> e : conf.entrySet()){
        logger.debug("available resource : "+ e.getKey());
        
      }
      conf = ConfigFactory.load("./"+MORPHLINEDIR+"/"+fileName, ConfigParseOptions.defaults(), opts);
      return conf;
    }

  }


  public static void main(String[] args) throws URISyntaxException {

    Config conf = ConfigFactory.load(MORPHLINEDIR+"simple_ruleset_collector.conf");
    System.out.println(conf.toString());

  }

}
