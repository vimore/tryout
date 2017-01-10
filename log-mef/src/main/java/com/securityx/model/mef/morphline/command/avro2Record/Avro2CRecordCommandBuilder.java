package com.securityx.model.mef.morphline.command.avro2Record;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Split key value pairs set 
 * Avro2Record  {
 *     filterKey : "/headers/category" # optional Defaults to "/headers/category"
 *     filterValue : "syslog"          # optional Default to "syslog"
 *     fields : {
 *         message : "/body"           # add other entries : format : destinationKey : avro path   
 *     }
 * }
 */
public class Avro2CRecordCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("avro2record");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        String filterKey = null;
        String filterValue = null;
        if (config.hasPath("filterKey")){
        filterKey = config.getString("filterKey");
        filterValue = config.getString("filterValue");
        }

        ConfigObject fieldsConfig = config.getObject("fields");
        Map<String, String> fields = new HashMap<String, String>();
        if (fieldsConfig != null){
          for (Map.Entry<String, ConfigValue> entry : fieldsConfig.entrySet()){
              fields.put(entry.getKey(), entry.getValue().unwrapped().toString());
          }
        }else{
          fields.put("message","/Body");
        }
        
        return new Avro2RecordCommand(config,
                context,
                parent,
                child,
                filterKey,
                filterValue,
                fields);
    }
}
