package com.securityx.model.mef.morphline.command.thirdparty.nprobe;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.Collection;
import java.util.Collections;

/**
 * nProbeHeaderExtractor { 
 *        field : "fieldname"
 * } 
 */
public class NProbeHeaderFieldExtractorCommandBuilder implements CommandBuilder {
  

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("nProbeHeaderExtractor");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    String field=null;
    if (config.hasPath("field")){
      field = config.getString("field");
      
    }
    if (field == null) {
      field="message";
    }
    return new NProbeHeaderFieldExtractorCommand(config,
            context,
            parent,
            child, field);
  }

}
