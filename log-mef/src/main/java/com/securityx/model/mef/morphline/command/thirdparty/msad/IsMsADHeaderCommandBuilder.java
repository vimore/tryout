package com.securityx.model.mef.morphline.command.thirdparty.msad;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.Collection;
import java.util.Collections;

/**
 * isMsADHeader { 
 *        field : "fieldname"
 * } 
 */
public class IsMsADHeaderCommandBuilder implements CommandBuilder {
  

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("isMsADHeader");
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
    return new IsMsADHeaderCommand(config,
            context,
            parent,
            child, field);
  }

}
