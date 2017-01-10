/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.logcollection.parser.configchecker;

import com.securityx.logcollection.utils.ConfigLoader;
import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jyria <jean-yves@e8security.com>
 */
public class ConfigChecker {
  private static Matcher m=Pattern.compile("morphline/([^/]+\\.conf)").matcher("");
  private static Matcher include=Pattern.compile("^include\\s+classpath\\(\"([^\"]+)\"\\)").matcher("");
  private static Logger logger= LoggerFactory.getLogger(ConfigChecker.class);

  public static void checkConfig() throws IOException{
    boolean isOK=true;
    Collection<String> list = ConfigLoader.getResources(Pattern.compile(".*\\.conf"));
    for (String l : list){
      m.reset(l);
      if (m.find()){
        //System.out.println(l+" : matched !");
        try {
        Config c = MorphlineResourceLoader.getConfFile(m.group(1));
        
        }catch( ConfigException e){
          if (logger.isDebugEnabled())
            logger.debug("trying to get resource: morphline/"+m.group(1));
          URL u = ConfigChecker.class.getClassLoader().getResource("morphline/"+m.group(1));
          
          String res = IOUtils.toString(ConfigChecker.class.getClassLoader().getResourceAsStream("morphline/"+m.group(1)));
          include.reset(res);
          while (include.find()){
            URL inc = ConfigChecker.class.getClassLoader().getResource(include.group(1));
            if(inc == null){
              logger.error("unable to find include resource "+include.group(1)+", check whether classpath contains such a config resource");
              isOK=false;
            }
          }
          
        }
      }
      //else
        //System.out.println(" : Failed !");
    }
    
    list = ConfigLoader.getResources(Pattern.compile("logparsingjob.conf"));
    for (String l : list){
      m.reset(l);
      if (m.find()){
        //System.out.println(l+" : matched !");
        try {
        Config c = MorphlineResourceLoader.getConfFile(m.group(1));
        
        }catch( ConfigException e){
          if (logger.isDebugEnabled())
            logger.debug("trying to get resource: morphline/"+m.group(1));
          URL u = ConfigChecker.class.getClassLoader().getResource("morphline/"+m.group(1));
          
          String res = IOUtils.toString(ConfigChecker.class.getClassLoader().getResourceAsStream("morphline/"+m.group(1)));
          include.reset(res);
          while (include.find()){
            URL inc = ConfigChecker.class.getClassLoader().getResource(include.group(1));
            if(inc == null){
              logger.error("unable to find include resource "+include.group(1)+", check whether classpath contains such a config resource");
              isOK=false;
            }
          }
          
        }
      }
      //else
        //System.out.println(" : Failed !");
    }
    if (! isOK){
      logger.error("config check failed !");
      System.exit(-1);
    }else{
      logger.info("config checked successfully");
    }
  }
  
  public static void main (String[] args) throws IOException{
    ConfigChecker.checkConfig();
  }
  
}
