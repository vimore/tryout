package com.securityx.model.mef.morphline.command.record.selector;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * discard 
 * recordSelector  {
 *     actions : [
 *         {
      *     hasField : ["field1", "field2", ...],  #optionnal
 *          hasFieldRegex : {                      #optionnal
 *              "Field1" : "pattern1", 
 *              ...
 *           }
 *           action|actionIfCondFails : {
 *                action : "accept|discard|acceptStore|discardStore" # *Store a filename for storage
 *                file : "path_to_the_file"
 *            }
*         },
 *     ]
 *    }
 *     precision : "ms"  # optional s, ms, ns Default "ms"
 *     timeZone : "UTC"  # optional, default "UTC"
 * }
 */
public class RecordSelectorOutCommandBuilder implements CommandBuilder {

    public Collection<String> getNames() {
        return Collections.singletonList("recordSelector");
    }

    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
      

        ArrayList<RecordSelectorAction> actions = processActions(config);
        
        return new RecordSelectorOutCommand(config,
                context,
                parent,
                child, actions);
    }
    
    private ArrayList<RecordSelectorAction> processActions(Config config){
      ConfigList actionsConfig = config.getList("actions");
      ArrayList<RecordSelectorAction> actions = new ArrayList<RecordSelectorAction>();
      for(ConfigValue c : actionsConfig){
        if (c instanceof ConfigObject) {
          ConfigObject configAction = (ConfigObject)c;
          RecordSelectorAction action = new RecordSelectorAction(configAction.toConfig());
          actions.add(action);
        }
      }
      return actions;
    }

}
