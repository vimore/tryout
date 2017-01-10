/*
 * Copyright 2013 Cloudera Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.securityx.model.mef.morphline.command.record.maprules;

import com.codahale.metrics.Meter;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.*;
import org.kitesdk.morphline.base.AbstractCommand;

import java.util.*;

/**
 * A mapRules command consists of zero or more rules.
 * 
 * A rule consists of zero or more commands.
 * 
 * The rules of a mapRules command are processed when processing rules exists for a given value.
 */
public final class MapRulesBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("mapRules");
  }
  
  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    return new MapRules(this, config, parent, child, context);
  }
  
  
  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class MapRules extends AbstractCommand {

    private final List<Command> childRules = new ArrayList<Command>();
    private final Map<String, Command> mapRules = new HashMap<String, Command>();
    private final boolean throwExceptionIfAllRulesFailed;
    private final boolean catchExceptions;
    private final boolean copyRecords;
    private final Meter numExceptionsCaught;
    private String fieldName;

    @SuppressWarnings("unchecked")
    public MapRules(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);
      this.throwExceptionIfAllRulesFailed = getConfigs().getBoolean(config, "throwExceptionIfAllRulesFailed", true);
      this.catchExceptions = getConfigs().getBoolean(config, "catchExceptions", false);
      this.copyRecords = getConfigs().getBoolean(config, "copyRecords", true);
      this.fieldName = getConfigs().getString(config, "fieldName", "message");


      List<? extends Config> ruleConfigs = getConfigs().getConfigList(config, "rules", Collections.EMPTY_LIST);
      List<String> entries;
      for (Config ruleConfig : ruleConfigs) {
        List<Command> commands = buildCommandChain(ruleConfig, "commands", child, true);

        entries = getConfigs().getStringList(ruleConfig, "values", Collections.EMPTY_LIST);

        for (String s : entries){
          if (commands.size() > 0 ) {
            if (! mapRules.containsKey(s)) {
              this.mapRules.put(s, commands.get(0));
            }else{
              LOG.error("skipping duplicate map entry for "+s);
              LOG.error("at "+ruleConfig.root().render());
            }
          }
        }
      }
      validateArguments();
      numExceptionsCaught = getMeter("numExceptionsCaught");
    }
    
    @Override
    protected void doNotify(Record notification) {
      for (Command childRule : mapRules.values()) {
        if (!catchExceptions) {
          childRule.notify(notification);
        } else {
          try {
            childRule.notify(notification);
          } catch (RuntimeException e) {
            numExceptionsCaught.mark();
            LOG.warn("tryRules command caught rule exception in doNotify(). Continuing to try other remaining rules", e);
            // continue and try the other remaining rules
          }
        }
      }
      super.doNotify(notification);
    }
  
    @Override
    protected boolean doProcess(Record record) {

      List values = record.get(this.fieldName);
      if (null == values)
        return false;
      else {
        for (Object value : values){
          if (mapRules.containsKey(value.toString())){
             return mapRules.get(value.toString()).process(record);
          }else{
            if (throwExceptionIfAllRulesFailed) {
              throw new MorphlineRuntimeException("mapRules command found no successful rule for record: " + record);
            }
            return false;

          }
        }
      }
      return false;
    }
    
  }
  
}
