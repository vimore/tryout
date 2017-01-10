
package com.securityx.model.mef.morphline.command.script.selector;

import com.securityx.model.mef.field.api.ValidationLogger;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

class ScriptSelectorCommand implements Command {
    private Logger logger = LoggerFactory.getLogger(ScriptSelectorCommand.class);
    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final ValidationLogger validationLogger;
    private final SelectorPattern rootSelectorPattern;

    public ScriptSelectorCommand(Config config, MorphlineContext context, Command parent, Command child, ValidationLogger validationLogger, SelectorPattern rootSelectorPattern) {
        this.config = config;
        this.parent = parent;
        this.child = child;
        this.context = context;
        this.validationLogger = validationLogger;
        this.rootSelectorPattern = rootSelectorPattern;
    }

    @Override
    public void notify(Record notification) {
      for (Object event : Notifications.getLifecycleEvents(notification)) {
      if (event == Notifications.LifecycleEvent.SHUTDOWN) {
          this.rootSelectorPattern.shutdown();
      }
    }
      child.notify(notification);
    }

    @Override
    public boolean process(Record record) {
        boolean processed = false;
        SelectorPatternOutput process = null;
        ParsingMetadata parsingMeta = new ParsingMetadata();
        record.put(_PARSING_META, parsingMeta);
        if(logger.isDebugEnabled())
           logger.debug("INFO : ScriptSelectorCommand record: "+record.toString());
        for (SelectorPattern selectorPattern : rootSelectorPattern.subSeletorPatterns) {
            if (logger.isDebugEnabled()){
              logger.debug("selectorPattern : "+selectorPattern.toString());
            }
            process = selectorPattern.process(record);
            if (process != null && process.isMatched()) {
                processed = true;
                if(logger.isDebugEnabled())
                  logger.debug("selectorPattern : "+selectorPattern.toString()+" matched");

                break;
            }else{
              if(logger.isDebugEnabled())
                logger.debug("selectorPattern : "+selectorPattern.toString()+" did not match");

            }
        }
        if(processed) {
            Iterator<Record> it = process.getOutput().iterator();
            while (processed && it.hasNext()){
                processed &= child.process(it.next());
            }

            return processed;
        }else{
            return child.process(record);
        }
    }
  public static final String _PARSING_META = "_parsingMeta";

    @Override
    public Command getParent() {
        return parent;
    }

}
