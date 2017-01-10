
package com.securityx.model.mef.morphline.command.harnesscmd;

import com.securityx.model.mef.morphline.command.record.selector.RecordSelectorAction;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import com.securityx.model.mef.morphline.command.script.selector.OutputScriptHarness;
import com.typesafe.config.Config;
import org.kitesdk.morphline.base.Notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

class HarnessCommand implements Command {
    private final Config config;
    private final Command parent;
    private final Command child;
    private final MorphlineContext context;
    private final OutputScriptHarness harness;
    private final boolean lazzyness;
    private boolean init = false;
    private Logger logger = LoggerFactory.getLogger(HarnessCommand.class);
    private final List<Record> notifBuffer = new ArrayList<Record>();

    public HarnessCommand(Config config, MorphlineContext context, Command parent, Command child, OutputScriptHarness harness, boolean lazzyness) {
        this.config = config;
        this.parent = parent;
        this.child = child;
        this.context = context;
        this.harness = harness;
        this.lazzyness = lazzyness;
        if (!this.lazzyness){
            this.init();
        }

    }

    public HarnessCommand(Config config, MorphlineContext context, Command parent, Command child, OutputScriptHarness harness) {
        this(config, context, parent, child, harness, true);
    }

    private boolean init(){
        try {
            harness.startup();
            this.init = true;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(HarnessCommandBuilder.class.getName()).log(Level.SEVERE, "exception raised during harness startup : " + ex.getMessage(), ex);
        }
        if (this.init) {
            for (Record notif : this.notifBuffer) {
                this.harness.notify(notif);
            }
            this.notifBuffer.clear();
        }
        return this.init;

    }

    private void processNotification(Record notification){
        boolean isShutdown =  false;
        for (Object event : Notifications.getLifecycleEvents(notification)) {
            if (event == Notifications.LifecycleEvent.SHUTDOWN) {
                isShutdown = true;
                break;
            }
        }
        if (! isShutdown){
            this.notifBuffer.add(notification.copy());
        }

    }

    @Override
    public void notify(Record notification) {

        if (! this.init){
            processNotification(notification);
      } else {
          this.harness.notify(notification);
//      for (Object event : Notifications.getLifecycleEvents(notification)) {
//        if (event == Notifications.LifecycleEvent.SHUTDOWN) {
//          this.harness.shutdown();
//        }
//      }
      }
      child.notify(notification);
    }

    public boolean process(Record record) {
        if (!init && !init()) {
            logger.error( "init failed at "+harness.toString());
            return false;
        }
        if(logger.isDebugEnabled())
          logger.debug("HarnessCommand processing "+harness.toString());
        List<Record> out =  this.harness.feed(record);
        if (null == out || out.size()==0){
          if(logger.isDebugEnabled())
          logger.debug("HarnessCommand "+harness.toString()+" no result after processing");
          return false;
        }
        boolean processed = true;
        Iterator<Record> it = out.iterator();
        while (it.hasNext() &&  processed) {
            processed = processed && child.process(it.next());
        }
        return processed;
    }

    public Command getParent() {
        return parent;
    }

}
