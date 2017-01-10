package com.securityx.model.mef.morphline.command.field.aggregator;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FieldAggregatorCommand implements Command {
  private Logger logger = LoggerFactory.getLogger(FieldAggregatorCommand.class);
  private final Config config;
  private Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final List<FieldRulesProcessor> rules;

  public FieldAggregatorCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          List<FieldRulesProcessor> rules) {
      this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.rules = rules;
  }

  public void setParent(Command parent) {
    this.parent = parent;
  }

  @Override
  public void notify(Record notification) {
    //    for (Object event : Notifications.getLifecycleEvents(notification)) {
    //      if (event == Notifications.LifecycleEvent.SHUTDOWN) {
    //      }
    //    }
    child.notify(notification);
  }

  @Override
  public boolean process(Record record) {
    
    for (FieldRulesProcessor rule : this.rules) {
      if (rule.process(record)){
        //sucess
          if(logger.isDebugEnabled())
             logger.debug("INFO : Successfully processed field "+rule.getFieldName()+" : "+record.getFirstValue(rule.getFieldName()));
        //System.out.println("successfully processed field "+rule.getFieldName()+" : "+record.getFirstValue(rule.getFieldName()));
      }else{
        //failure
          if(logger.isDebugEnabled())
            logger.debug("ERROR : Failed processing field "+rule.getFieldName());
        //System.out.println("failed processing field "+rule.getFieldName());
      }
    }
    return child.process(record);
  }

  @Override
  public Command getParent() {
    return parent;
  }

//  public static void main (String[] args){
//  }
}
