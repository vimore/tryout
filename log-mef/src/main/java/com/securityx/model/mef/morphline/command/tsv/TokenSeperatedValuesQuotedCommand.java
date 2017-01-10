package com.securityx.model.mef.morphline.command.tsv;

import com.securityx.model.mef.morphline.lifecycle.TsvqNotifications;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class TokenSeperatedValuesQuotedCommand implements Command {

  private final boolean greedy;
  private Logger logger = LoggerFactory.getLogger(TokenSeperatedValuesQuotedCommand.class);
  private final Config config;
  private final Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final String inputFieldName;
  private final String fieldSep;
  private final String quoteChar;
  private List<String> fieldNames;
  private final boolean trim;

  public TokenSeperatedValuesQuotedCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          String inputFieldName,
          String FieldSep,
          String quoteChar,
          List<String> fieldNames,
          boolean trim,
          boolean greedy) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.inputFieldName = inputFieldName;
    this.fieldSep = FieldSep;
    this.quoteChar = quoteChar;
    this.fieldNames = fieldNames;
    this.trim = trim;
    this.greedy = greedy;

  }

  @Override
  public void notify(Record notification) {
    for (Object event : TsvqNotifications.getLifecycleEvents(notification)) {
      if (event == TsvqNotifications.LifecycleEvent.HEADER_UPDATE) {
        this.fieldNames = (List<String>) notification.getFirstValue(TsvqNotifications.TSVQ_HEADER);
        if (logger.isDebugEnabled())
          logger.debug("notify : "+this.fieldNames.toString());
      }
    }

    child.notify(notification);
  }

  @Override
  public boolean process(Record record) {
    final StringBuilder sb = new StringBuilder();
    List got = record.get(inputFieldName);
    if (got == null || got.isEmpty()) {
      return false;
    }
    for (Object r : got) {
      final String line = r.toString();
      ArrayList<String> fields = new ArrayList();
      char[] charFieldSep = this.fieldSep.toCharArray();
      int id = 0;
      int fieldId = 0;
      boolean quoted = false;
      int len = line.length();
      int loop = 0;
      while (id < len ) {
        final char c = line.charAt(id);
        int i = c;
        if(logger.isDebugEnabled())
          logger.debug(id + " char : '" + c + "' : (" + i + ")");
        if (c == this.quoteChar.charAt(0)) {
          if (loop++ >= 3)
            break;
          // protected quote (doubled)
          if (quoted && id < len - 1 && line.charAt(id + 1) == this.quoteChar.charAt(0)) {
            sb.append(c);
            id++;
          } else {
            if (quoted) {
              //end of quote section
              if (id == len - 1 || line.charAt(id + 1) == charFieldSep[fieldId]) {
                quoted = false;
                continue;
              }
            } else { //let's start quote gathering

              if (sb.length() == 0) { //nothing captured at now
                quoted = true;
                continue;
              }
            }
            sb.append(c);
          }
        } else if (c == charFieldSep[fieldId] && !quoted) { // not a quote
          if(logger.isDebugEnabled())
            logger.debug("buffer : "+sb.toString());
          // if we are about to collect the last expected field it's time to be greedy
          if (this.greedy && this.fieldNames.size() - fields.size() == 1 ){
               sb.append(line.substring(id));
               id = len;
               break;
          }
          fields.add(trimQuoteChar(sb.toString()));
          sb.setLength(0);
        } else {
          sb.append(c);
        }
        id++;
        loop=0;
      }
      fields.add(trimQuoteChar(sb.toString()));
      int i = 0;
      String tmpStr;
      if (this.fieldNames.size() <= fields.size()) {
        for (String f : this.fieldNames) {
          tmpStr = fields.get(i).trim();
          if ( tmpStr.length() > 0)
            record.put(f, (Object) tmpStr);
          i++;
        }
      } else {
        i=0;
        for (String s : fields){
          tmpStr = s.trim();
          if (tmpStr.length()>0)
            record.put(this.fieldNames.get(i), tmpStr);
          i++;
        }
        if (logger.isDebugEnabled()){
          logger.debug("ERROR : found  less fields than expected");
          logger.debug("field list : " + this.fieldNames.toString());
          logger.debug("field extracted : "+ fields.toString());
        }
      }
    }
    return child.process(record);
  }

  private String trimQuoteChar(String value){
    if (value.length() >= 2 && value.charAt(0) == this.quoteChar.charAt(0) && value.charAt(value.length()-1) == this.quoteChar.charAt(0))            return value.substring(1, value.length()-1);
    else
        return value;

  }
  @Override 
  public Command getParent() {
    return parent;
  }
}
