/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.morphline.command.record.selector;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jyrialhon
 */
public class RecordSelectorAction {
  private Logger logger = LoggerFactory.getLogger(RecordSelectorAction.class);
  private List<String> fields;
  private Map<String, Matcher> fieldPatterns;
  private final RSAction action;
  private final boolean doWhen;
  private PrintWriter outFileWriter = null;
  private String outFile = null;

  public RecordSelectorAction(Config c) {
    this.fields = parseHasFieldConfig(c);
    this.fieldPatterns = parseHasFieldRegexConfig(c);
    ConfigObject action;
    if (c.hasPath("action")) {
      action = c.getObject("action");
      this.action = RSAction.valueOf(action.toConfig().getString("action"));
      this.doWhen = true;
    } else if (c.hasPath("actionIfCondFails")) {
      action = c.getObject("actionIfCondFails");
      this.action = RSAction.valueOf(action.toConfig().getString("action"));
      this.doWhen = false;
    } else {
      throw new ConfigExceptionImpl("SelectorRecord must contain either action or actionIfCondFail");
    }
    if (null!= this.action && this.action.store) {
      //try {
        outFile = action.toConfig().getString("file");
        // jyria : disabled for MapReduce
        //outFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outFile, true)));
        //logger.info("Appending data to file : "+outFile);
      //} catch (IOException e) {
      //   throw new ConfigExceptionImpl("Expecting a valid file parameter" + e.getCause());
      //}
    }

  }

  public void shutdown() {
    if (null!=this.outFileWriter){
      this.outFileWriter.flush();
      this.outFileWriter.close();
      if(logger.isDebugEnabled())
        logger.debug("INFO : closing RecordSelector file writer "+outFile);
    }
  }

  public RSAction process(Record record) {
    RSAction act = null;
    if (checkConditions(record) == this.doWhen) {
      act = action;
      if (action.store) 
        this.store(record);
    }
    return act;
  }

  private void store(Record r) {
    if(logger.isDebugEnabled())
      logger.debug("ERROR : store : "+outFile+" : "+r.toString());
    if (null!=outFileWriter)
      this.outFileWriter.println(r.toString());
  }

  private boolean checkConditions(Record r) {
    if (this.fields.size() > 0) {
      if (this.fieldPatterns != null) {
        return fieldMatch(r) && fieldPatternMatch(r);
      } else {
        return fieldMatch(r);
      }
    } else {
      return true;
    }
  }

  private boolean fieldPatternMatch(Record r) {
    boolean found = true;
    for (Map.Entry<String, Matcher> field : this.fieldPatterns.entrySet()) {
      field.getValue().reset(r.getFirstValue(field.getKey()).toString());
      found &= field.getValue().find();
    }
    return found;
  }

  private boolean fieldMatch(Record r) {
    boolean found = true;
    for (String field : this.fields) {
      boolean hasField = r.getFields().containsKey(field);
      found = found & hasField;
    }
    return found;
  }

  private List<String> parseHasFieldConfig(Config c) {
    ArrayList<String> out = new ArrayList<String>();
    if (c.hasPath("hasField")) {
      List<String> hasFields = c.getStringList("hasField");
      out.addAll(hasFields);
    }
    return out;
  }

  private Map<String, Matcher> parseHasFieldRegexConfig(Config c) {
    Map<String, Matcher> out = null;
    if (c.hasPath("hasFieldRegex")) {
      out = new HashMap<String, Matcher>();
      Config fieldRegexesConfig = c.getConfig("hasFieldRegex");
      for (Iterator<Map.Entry<String, ConfigValue>> it = fieldRegexesConfig.entrySet().iterator(); it.hasNext();) {
        Map.Entry<String, ConfigValue> v = it.next();
        String k = v.getKey();
        k = k.replaceAll("(?:^\"|\"$)", ""); //dirty remove "surrounding" quote 
        Pattern p = Pattern.compile((String) v.getValue().unwrapped());
        out.put(k, p.matcher(""));
        if (!this.fields.contains(k)) {
          this.fields.add(k);
        }
      }
      this.fields = Collections.unmodifiableList(this.fields);
      out = Collections.unmodifiableMap(out);
    }
    return out;
  }

  private static class ConfigExceptionImpl extends ConfigException {

    public ConfigExceptionImpl(String str) {
      super(str);
    }
  }

}
