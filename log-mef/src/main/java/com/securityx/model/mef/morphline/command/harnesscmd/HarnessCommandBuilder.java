package com.securityx.model.mef.morphline.command.harnesscmd;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command._AssertRecordOutCommand;
import com.securityx.model.mef.morphline.command.script.selector.OutputScriptHarness;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 *
 * harnessCmd { 
 *    morphlineFile : "" 
 *    morphlineId : "" 
 *    verbose : false
 * }
 */
public class HarnessCommandBuilder implements CommandBuilder {
  private final org.slf4j.Logger logger = LoggerFactory.getLogger(HarnessCommandBuilder.class);
  public Collection<String> getNames() {
    return Collections.singletonList("harnessCmd");
  }

  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    String morphlineCmd = config.getString("morphlineFile");
    String morphlineId = config.getString("morphlineId");
    String transferFieldNamedToMessage="undef";
    String messageFieldName="undef";
    if (!config.hasPath("fullCopy")){
      transferFieldNamedToMessage = config.getString("transferFieldNamedToMessage");
      messageFieldName = config.getString("messageFieldName");
    }
    boolean fullCopy = false;
    if (config.hasPath("fullCopy")){
      fullCopy = config.getBoolean("fullCopy");
    }
    Config morphlineConf = MorphlineResourceLoader.getConfFile(morphlineCmd);
    boolean verbose =  Boolean.valueOf(System.getProperty("logMefEnableVerboseOutput"));
       
    MorphlineHarness harness = new MorphlineHarness(context, morphlineConf, morphlineId);
    _AssertRecordOutCommand outCmd = new _AssertRecordOutCommand(context, verbose);
    OutputScriptHarness outHarness = new OutputScriptHarness(transferFieldNamedToMessage, messageFieldName,harness, outCmd, fullCopy);

    return new HarnessCommand(config, context, parent, child, outHarness);

  }

}
