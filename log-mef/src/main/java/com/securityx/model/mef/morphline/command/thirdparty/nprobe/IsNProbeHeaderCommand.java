package com.securityx.model.mef.morphline.command.thirdparty.nprobe;

import com.securityx.model.external.flow.NProbeToFlowMefMappings;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsNProbeHeaderCommand implements Command {

  private Logger logger = LoggerFactory.getLogger(IsNProbeHeaderCommand.class);
  protected static Pattern headerMatcher = Pattern.compile("^([A-Z]\\w+,){3,}[A-Z]\\w+");
  private static int SUCCESSFULL_THRESHOLD = 3;
  private final Config config;
  private Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final String field;

  public IsNProbeHeaderCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          String field) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.field = field;
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
    String value = String.valueOf(record.getFirstValue(field));
    Matcher m = this.headerMatcher.matcher(value);
    boolean isValidheader = false;
    if (m.find()) {
      //sucess - parse and propagate data per notification
      // String str = value.substring(m.end());
      if (isValid(value)) {
        isValidheader = true;
        if(logger.isDebugEnabled())
          logger.debug("successfully processed nprobe header ");
      } else {
        if(logger.isDebugEnabled())
          logger.debug("does not recognize a nprobe header ");
      }
    } else {
      //failure
      if(logger.isDebugEnabled())
        logger.debug("ERROR : failed processing nprobe header : " + value);
    }
    if (!isValidheader) {
      return false;
    }
    return child.process(record);
  }

  @Override
  public Command getParent() {
    return parent;
  }

//  public static void main (String[] args){
//  }
  private boolean isValid(String str) {
    //lazy function to guess if were facing a read header : SUCCESSFULL_THRESHOLD matches successfull 
    String searchStr = new String(str);
    int cpt = 0;
    int i = 0;
    NProbeToFlowMefMappings[] fields = NProbeToFlowMefMappings.values();
    while (cpt < SUCCESSFULL_THRESHOLD && i < fields.length) {
      if (searchStr.contains(fields[i].getPrettyName())) {
        cpt++;
        searchStr = searchStr.replaceAll("(?:^|\\s)" + fields[i].getPrettyName() + "(?=\\s|$)", "");
      }
      i++;
    }
    return cpt == SUCCESSFULL_THRESHOLD;
  }

}
