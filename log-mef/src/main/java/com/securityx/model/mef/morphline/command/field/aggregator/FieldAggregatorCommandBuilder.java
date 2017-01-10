package com.securityx.model.mef.morphline.command.field.aggregator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * fieldAggregator { 
 *        fieldName : [ 
 *            "", 
 *            ... 
 *        ], 
 *        fieldName2 : [ 
 *        ] 
 * } 
 */
public class FieldAggregatorCommandBuilder implements CommandBuilder {
  private static Pattern fieldMatcher = Pattern.compile("%\\{[^\\}]+\\}");

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("fieldAggregator");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {

    ArrayList<FieldRulesProcessor> Fields = processFields(config);

    return new FieldAggregatorCommand(config,
            context,
            parent,
            child, Fields);
  }

  private ArrayList<FieldRulesProcessor> processFields(Config config) {
    ArrayList<FieldRulesProcessor> fields = new ArrayList<FieldRulesProcessor>();

    for (Map.Entry<String, ConfigValue> entry : config.entrySet()) {
         List<String> rules = config.getStringList(entry.getKey());
         List<FieldRule> fieldRules = new ArrayList<FieldRule>();
         for (String format : rules){
           FieldRule rule = processRule(format);
           fieldRules.add(rule);
         }
         FieldRulesProcessor p = new FieldRulesProcessor(entry.getKey(), fieldRules);
         fields.add(p);
    }
    return fields;
  }

  private FieldRule processRule(String format) {
    List<String> fieldList = new ArrayList<String>();
    Matcher m = FieldAggregatorCommandBuilder.fieldMatcher.matcher(format);
    while (m.find()){
      String field = m.group(0);
      field = field.replaceAll("%\\{|\\}", "");
      fieldList.add(field);
    }
    return new FieldRule(fieldList, format);
    
  }

}
