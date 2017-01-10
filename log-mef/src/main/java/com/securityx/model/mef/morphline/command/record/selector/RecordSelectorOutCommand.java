package com.securityx.model.mef.morphline.command.record.selector;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Notifications;

import java.util.List;

public class RecordSelectorOutCommand implements Command {

  private final Config config;
  private Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final List<RecordSelectorAction> actions;

  public RecordSelectorOutCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          List<RecordSelectorAction> actions) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.actions = actions;
  }

  public void setParent(Command parent) {
    this.parent = parent;
  }

  public void notify(Record notification) {
    for (Object event : Notifications.getLifecycleEvents(notification)) {
      if (event == Notifications.LifecycleEvent.SHUTDOWN) {
        for (RecordSelectorAction action : this.actions) {
          action.shutdown();
        }
      }
    }
    child.notify(notification);
  }

  public boolean process(Record record) {
    for (RecordSelectorAction actionSelector : this.actions) {
      RSAction action = actionSelector.process(record);
      if (action != null) {
        if (action.passthrough) {
            return child.process(record);
        } else {
          return true;
        }
      }
    }
    return child.process(record);
  }

  public Command getParent() {
    return parent;
  }

//  public static void main (String[] args){
//  }
}
