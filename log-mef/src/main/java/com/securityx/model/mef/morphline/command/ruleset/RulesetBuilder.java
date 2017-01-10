package com.securityx.model.mef.morphline.command.ruleset;

import com.securityx.model.mef.morphline.command.record.selector.RecordSelectorAction;
import com.securityx.model.mef.morphline.command.record.selector.RecordSelectorOutCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.kitesdk.morphline.base.Configs;
import org.kitesdk.morphline.shaded.com.google.code.regexp.GroupInfo;
import org.kitesdk.morphline.shaded.com.google.code.regexp.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * The Grok command uses regular expression pattern matching to extract
 * structured fields from unstructured log data.
 * <p>
 * It is perfect for syslog logs, apache and other webserver logs, mysql logs,
 * and in general, any log format that is generally written for humans and not
 * computer consumption.
 */
public final class RulesetBuilder implements CommandBuilder {

  private Logger logger = LoggerFactory.getLogger(RulesetBuilder.class);
  /*
   * Uses a shaded version of com.google.code.regexp-0.2.3 to minimize potential dependency issues.
   * See https://github.com/tony19/named-regexp
   */

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("ruleset");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    Configs configs = new Configs();
    String sampleUnmatchedTo = configs.getString(config, "sampleUnmatchedTo", "unset");
    File f;
    if (!"unset".equals(sampleUnmatchedTo)) {
      //f = new File(sampleUnmatchedTo);
      //if (f.exists() && f.canWrite() || !f.exists() && f.getParentFile().canWrite()) {
        ArrayList<RecordSelectorAction> actions = new ArrayList<RecordSelectorAction>();
        Map<String, String> actionMap = new HashMap<String, String>();
        actionMap.put("action", "acceptStore");
        actionMap.put("file", sampleUnmatchedTo);
        Map<String, String> fieldPatternlist = new HashMap<String, String>();
        fieldPatternlist.put("_matchedRuleset", ".+");
        Config actionsConfig = ConfigFactory.empty().withValue("hasFieldRegex", ConfigValueFactory.fromMap(fieldPatternlist))
                .withValue("actionIfCondFails", ConfigValueFactory.fromMap(actionMap));
        RecordSelectorAction action = new RecordSelectorAction(actionsConfig);
        actions.add(action);
        RecordSelectorOutCommand recordSelectorCommand = new RecordSelectorOutCommand(config, context, null, child, actions);
        Command command = new Ruleset(this, config, parent,
                (Command) recordSelectorCommand, context);
        recordSelectorCommand.setParent(command);
        return command;
      //} else {
      //  logger.error("unable to write to sampleUnmatchedTo file");
      //  return new Ruleset(this, config, parent, child, context);
      //}
    }

    return new Ruleset(this, config, parent, child, context);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class Ruleset extends AbstractCommand {

    private Logger logger = LoggerFactory.getLogger(Ruleset.class);
    private final boolean extract;
    private final boolean extractInPlace;
    private final boolean addEmptyStrings;
    private final String field;
    private final ArrayList<Node> nodes;
    private final String name;
    private final boolean debugMode;

    public Ruleset(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);
      field = getConfigs().getString(config, "field", "message");
      name = getConfigs().getString(config, "name", "unset");
      String str = getConfigs().getString(config, "sampleUnmatchedTo", "unset");
      String extractStr = getConfigs().getString(config, "extract", "true");

      this.extractInPlace = extractStr.equals("inplace");
      if (extractInPlace) {
        this.extract = true;
      } else {
        this.extract = getConfigs().getBoolean(config, "extract", true);
      }
      
      this.debugMode = getConfigs().getBoolean(config, "debugMode", false);
      this.addEmptyStrings = getConfigs().getBoolean(config, "addEmptyStrings", false);
      nodes = new ArrayList();
      List<? extends Config> nodeList = getConfigs().getConfigList(config, "nodes");

      //TODO to be improved : node id to scope at ruleset level 
      Node.reset();
      for (Config c : nodeList) {
        Node n = Node.NewInstance(c, getConfigs(), addEmptyStrings, debugMode);
        nodes.add(n);
      }

      validateArguments();

    }

    @Override
    protected void doNotify(Record notification) {

      super.doNotify(notification); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean doProcess(Record inputRecord) {
      Record outputRecord;
      outputRecord = ((extractInPlace || !extract) ? inputRecord : inputRecord.copy());
      List values = outputRecord.get(field);
      Iterator<Node> nodes = this.nodes.iterator();
      for (Object v : values) {
        boolean match = false;
        Record res = new Record();
        while (nodes.hasNext() && !match) {
          Node n = nodes.next();
          if (n.doProcess(v.toString(), res)) {
            match = true;
            for (Map.Entry<String, Object> o : res.getFields().entries()) {
              outputRecord.put(o.getKey(), o.getValue());
            }
            if(logger.isDebugEnabled())
                logger.debug("INFO : ruleset "+this.name+" matched.");
            outputRecord.put("_matchedRuleset", name);
          }
        }
      }
      // pass record to next command in chain:
      return super.doProcess(outputRecord);
    }

    private boolean doMatch(Record inputRecord, Record outputRecord, boolean doExtract) {
      return true;
    }

    private void extract(Record outputRecord, Matcher matcher, boolean doExtract) {
      if (doExtract) {
        extractFast(outputRecord, matcher);
      }
    }

    private void extractFast(Record outputRecord, Matcher matcher) {
      for (Map.Entry<String, List<GroupInfo>> entry : matcher.namedPattern().groupInfo().entrySet()) {
        String groupName = entry.getKey();
        List<GroupInfo> list = entry.getValue();
        int idx = list.get(0).groupIndex();
        int group = idx > -1 ? idx + 1 : -1; // TODO cache that number (perf)?
        String value = matcher.group(group);
        if (value != null && (value.length() > 0 || addEmptyStrings)) {
          outputRecord.put(groupName, value);
        }
      }
    }

    private Map<String, ? extends Object> List(String message) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ///////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    ///////////////////////////////////////////////////////////////////////////////
    private static enum NumRequiredMatches {

      atLeastOnce,
      once,
      all
    }

  }

}
