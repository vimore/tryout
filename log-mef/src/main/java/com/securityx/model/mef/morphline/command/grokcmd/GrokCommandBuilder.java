package com.securityx.model.mef.morphline.command.grokcmd;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.stdlib.GrokBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper for grok allowing relative file reference usage replaces the relatif
 * path by absolute one to let grok find the file
 *
 */
public class GrokCommandBuilder implements CommandBuilder {
  private static Logger logger = LoggerFactory.getLogger(GrokCommandBuilder.class);
  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("grokcmd");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    List<String> dictList =  new ArrayList<String>();
    Config relocatedConfig;
    if (config.hasPath("dictionaryFiles")){
        if(logger.isDebugEnabled())
           logger.debug("ERROR : dictionaryFiles are no more supported since morphline conf relocated to resources");
      //    for (String dictionaryFile : config.getStringList("dictionaryFiles")) {
      //      File f = new File(cwd, dictionaryFile);
      //      
      //      dictList.add (f.getAbsolutePath());
      //    }
      //      relocatedConfig = config.withValue("dictionaryFiles", ConfigValueFactory.fromIterable(dictList));
    }else {
      relocatedConfig = config;
            }
    GrokBuilder builder = new GrokBuilder();
    return builder.build(config,
              parent,
              child,
              context);
  }
}
