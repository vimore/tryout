package com.securityx.model.mef.morphline.command.script.selector;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.field.api.ValidationLogger;
import com.securityx.model.mef.morphline.command._AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * scriptSelector { scripts : [ { logicalName : "cef", morphlineFile :
 * "bar/bazz", #path to ressource morphlineId : "morphlineId",
 * transferFieldNamedToMessage : "message", // optional default level 'message'
 * as is. Note / warning: If Record doesn't contain transferFieldNamedToMessage
 * the script will not run. accumulative : false // optional means the output
 * from the run script will be feed to subPatterns },.... ] selectors : [ {
 * readFromFieldNamed : "message" // optional field. default is "message"
 * pattern : "regexPattern" writePatternToFieldNamed : null // optional field.
 * default is ""; logicalScriptName : [ "null" ] subPatterns : [ { pattern :
 * "regexPattern" patternMatchingStyle : "greedy" #greedy or std (default)
 * logicalScriptName : [ "null" ] subPatterns : [ { .... } ] },.... ] }, {
 *
 * },.... ] }
 */
public class ScriptSelectorCommandBuilder implements CommandBuilder {

    private Logger logger = LoggerFactory.getLogger(ScriptSelectorCommandBuilder.class);

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("scriptSelector");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        try {
            ValidationLogger validationLogger = new ValidationLogger();
            Map<String, ScriptHarness> scriptForName = parseScripts(parent, config, child, context);

            ConfigList selectors = config.getList("selectors");
            if (selectors.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("ERROR : IllegalStateException : command scriptSelector must have a declared 'selectors' member.");
                }
                //throw new IllegalStateException("command scriptSelector must have a declared 'selectors' member.");
            }
            SelectorPattern rootSelectorPattern = new SelectorPattern("message", Pattern.compile("."), null, SelectorPatternMode.std);
            parseSelectors(scriptForName, rootSelectorPattern, selectors);

            return new ScriptSelectorCommand(config, context, parent, child, validationLogger, rootSelectorPattern);
        } catch (Exception x) {
            if (logger.isDebugEnabled()) {
                logger.debug("ERROR : Exception : Failed to create scriptSelector for " + config.toString());
                logger.debug("ERROR : Exception : reason  " + x.getMessage(), x);
            }
            //throw x;
        }
        return null;
    }

    private void parseSelectors(Map<String, ScriptHarness> scriptForName, SelectorPattern parentSelector, ConfigList selectors) {
        for (ConfigValue selector : selectors) {
            if (selector instanceof ConfigObject) {
                ConfigObject selectorDeclaration = (ConfigObject) selector;
                String pattern = (String) selectorDeclaration.get("pattern").unwrapped();
                String readFromFieldNamed = Fields.MESSAGE;
                SelectorPatternMode mode = SelectorPatternMode.std;

                if (selectorDeclaration.containsKey("readFromFieldNamed")) {
                    readFromFieldNamed = (String) selectorDeclaration.get("readFromFieldNamed").unwrapped();
                }
                String writePatternToFieldNamed = null;
                if (selectorDeclaration.containsKey("writePatternToFieldNamed")) {
                    writePatternToFieldNamed = (String) selectorDeclaration.get("writePatternToFieldNamed").unwrapped();
                }
                if (selectorDeclaration.containsKey("patternMatchingStyle")) {
                    String patternMatchingStyle = (String) selectorDeclaration.get("patternMatchingStyle").unwrapped();
                    try {
                        mode = SelectorPatternMode.valueOf(patternMatchingStyle);
                    } catch (IllegalArgumentException e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("ERROR : failed to parse SelectorPatternMode : " + patternMatchingStyle);
                        }
                    }
                }

                Pattern compiledPattern = Pattern.compile(pattern);
                SelectorPattern selectorPattern = new SelectorPattern(readFromFieldNamed,
                  compiledPattern,
                  writePatternToFieldNamed, mode);

                parentSelector.subSeletorPatterns.add(selectorPattern);
                if (selectorDeclaration.containsKey("logicalScriptName")) {
                    ConfigValue scriptNames = selectorDeclaration.get("logicalScriptName");
                    if (scriptNames instanceof ConfigList) {
                        ConfigList names = (ConfigList) scriptNames;
                        for (ConfigValue name : names) {
                            ScriptHarness get = scriptForName.get((String) name.unwrapped());
                            selectorPattern.harnesses.add(get);
                        }
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("ERROR : IllegalStateException : command scriptSelector expects 'logicalScriptName' to be a list of strings.");
                        }
                        //throw new IllegalStateException("command scriptSelector expects 'logicalScriptName' to be a list of strings.");
                    }
                }
                if (selectorDeclaration.containsKey("lazy")) {
                    ConfigValue v =  selectorDeclaration.get("lazy");
                    boolean lazy = (Boolean) v.unwrapped();
                    selectorPattern.setLazyProcessing(lazy);
                }

                if (selectorDeclaration.containsKey("subPatterns")) {
                    ConfigValue subPatterns = selectorDeclaration.get("subPatterns");
                    parseSelectors(scriptForName, selectorPattern, (ConfigList) subPatterns);
                }

            }
        }
    }

    private Map<String, ScriptHarness> parseScripts(Command parent, Config config, Command child, MorphlineContext ctx) throws Exception {
        Map<String, ScriptHarness> scriptForName = new HashMap<String, ScriptHarness>();
        ConfigList scripts = config.getList("scripts");
        if (scripts.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("ERROR : IllegalStateException : command scriptSelector must have a declared 'scripts' member.");
            }
            //throw new IllegalStateException("command scriptSelector must have a declared 'scripts' member.");
        }

        //File cwd = new File (config.origin().filename()).getParentFile().getAbsoluteFile();
        //logger.info("ScriptSelector config file location : "+cwd.getAbsolutePath());
        for (ConfigValue script : scripts) {

            if (script instanceof ConfigObject) {
                ConfigObject scriptDeclaration = (ConfigObject) script;
                if (!scriptDeclaration.containsKey("logicalName")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ERROR : IllegalStateException : a script definition must have a declared 'logicalName' memeber. " + script.toString());
                    }
                    throw new IllegalStateException("a script definition must have a declared 'logicalName' memeber. " + script.toString());
                }
                if (!scriptDeclaration.containsKey("morphlineFile")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ERROR : IllegalStateException : a script definition must have a declared 'morphlineFile' memeber. " + script.toString());
                    }
                    throw new IllegalStateException("a script definition must have a declared 'morphlineFile' memeber. " + script.toString());
                }
                if (!scriptDeclaration.containsKey("morphlineId")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ERROR : IllegalStateException : a script definition must have a declared 'morphlineId' memeber. " + script.toString());
                    }
                    throw new IllegalStateException("a script definition must have a declared 'morphlineId' memeber. " + script.toString());
                }
                String logicalName = (String) scriptDeclaration.get("logicalName").unwrapped();
                String morphlineFile = (String) scriptDeclaration.get("morphlineFile").unwrapped();
                String morphlineId = (String) scriptDeclaration.get("morphlineId").unwrapped();
                boolean accumulative = false;
                if (scriptDeclaration.containsKey("accumulative")) {
                    accumulative = (Boolean) scriptDeclaration.get("accumulative").unwrapped();
                }

                String transferFieldNamedToMessage = Fields.MESSAGE;
                if (scriptDeclaration.containsKey("transferFieldNamedToMessage")) {
                    transferFieldNamedToMessage = (String) scriptDeclaration.get("transferFieldNamedToMessage").unwrapped();
                }
                String messageFieldName = Fields.MESSAGE;
                if (scriptDeclaration.containsKey("messageFieldName")) {
                    messageFieldName = (String) scriptDeclaration.get("messageFieldName").unwrapped();
                }

                Boolean fullCopy = false;
                if (scriptDeclaration.containsKey("fullCopy")) {
                    fullCopy = (Boolean)scriptDeclaration.get("fullCopy").unwrapped();

                }


               // MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
                Config morphlineConf = MorphlineResourceLoader.getConfFile(morphlineFile);

                if (!morphlineConf.hasPath("morphlines")) {
                    throw new IllegalStateException("morphlineFile does not exists : '" + morphlineFile + "' in " + script.toString());
                }
                MorphlineHarness morphlineHarness = new MorphlineHarness(ctx, morphlineConf, morphlineId);

                _AssertRecordOutCommand out = new _AssertRecordOutCommand(ctx);
                ScriptHarness scriptHarness;
                if (accumulative) {
                    scriptHarness = new AccumulativeScriptHarness(parent, transferFieldNamedToMessage, morphlineHarness);
                } else {
                    scriptHarness = new OutputScriptHarness(transferFieldNamedToMessage, messageFieldName,morphlineHarness, out, fullCopy);
                }

                try {
                    // lazyness refactoring  leads to comment following line
                    // scriptHarness.startup();
                    scriptForName.put(logicalName, scriptHarness);
                } catch (Exception x) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ERROR : failed to start script for logicalName:" + logicalName
                          + "morphlineFile:" + morphlineFile + " morphlineId:" + morphlineId);
                        logger.debug("ERROR : exception raised " + x.getMessage());
                    }
                    throw x;
                }

            } else {
                if(logger.isDebugEnabled())
                  logger.debug("ERROR : "+script.getClass() + "?????" + script.unwrapped());
            }
        }
        return scriptForName;
    }
}
