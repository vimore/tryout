package com.securityx.model.mef.morphline.command.util;

import com.securityx.model.mef.field.api.WebProxyMefField;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Split key value pairs set
 * urlExtract {
 *   inputFieldName : "message" #optional Defaults to "message"
 *   destFields : [protocol, authority, host, port, path, query] # optional Default ","
 * }
 */
public class UrlExtractCommandBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("urlExtract");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    List<String> inputFieldNames = null;
    List<String> destFields = null;
    String inputFieldName = null;
    if (config.hasPath("inputFieldNames")) {
      inputFieldNames = config.getStringList("inputFieldNames");
    }
    if (config.hasPath("inputFieldName")) {
      inputFieldName = config.getString("inputFieldName");
    }
    if (inputFieldNames == null || inputFieldNames.isEmpty()) {
      inputFieldNames = new ArrayList();
    }
    if (inputFieldName == null || "".equals(inputFieldName)) {
      if ( inputFieldNames.isEmpty())
       inputFieldNames.add("time");
    } else {
      inputFieldNames.add(inputFieldName);
    }

    if (config.hasPath("destFields")) {
         destFields = config.getStringList("destFields");
         if (destFields.size() != 6 ) {
             LoggerFactory.getLogger(getClass()).error("wrong number of items in destFields, expected 6 at " + config.root().render());
         }
    } else {
      destFields = new ArrayList<String>();
      destFields.add(WebProxyMefField.requestScheme.getPrettyName());
      destFields.add(WebProxyMefField.destinationUserName.getPrettyName());
      destFields.add(WebProxyMefField.destinationNameOrIp.getPrettyName());
      destFields.add(WebProxyMefField.destinationPort.getPrettyName());
      destFields.add(WebProxyMefField.requestPath.getPrettyName());
      destFields.add(WebProxyMefField.requestQuery.getPrettyName());
    }

    return new UrlExtractCommand(config,
            context,
            parent,
            child,
            inputFieldNames,
            destFields);
  }
}
