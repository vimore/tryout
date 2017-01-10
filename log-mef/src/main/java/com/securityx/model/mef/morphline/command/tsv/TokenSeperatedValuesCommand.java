package com.securityx.model.mef.morphline.command.tsv;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

class TokenSeperatedValuesCommand implements Command {

  private Logger logger = LoggerFactory.getLogger(TokenSeperatedValuesCommand.class);
  private final Config config;
  private final Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final String inputFieldName;
  private final String splitRegex;
  private final List<String> fieldNames;
  private final boolean trim;

  public TokenSeperatedValuesCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          String inputFieldName,
          String splitRegex,
          List<String> fieldNames,
          boolean trim) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.inputFieldName = inputFieldName;
    this.splitRegex = splitRegex;
    this.fieldNames = fieldNames;
    this.trim = trim;
  }

  @Override
  public void notify(Record notification) {
    child.notify(notification);
  }

  @Override
  public boolean process(Record record) {
    List got = record.get(inputFieldName);
    if (got == null || got.isEmpty()) {
      return false;
    }
    for (Object r : got) {
      String[] values = r.toString().split(splitRegex);
      if(logger.isDebugEnabled())
        logger.debug("values:"+Arrays.deepToString(values));
      if (values.length != fieldNames.size()) {
      if (logger.isDebugEnabled())
        logger.debug("wrong number of tokens:"+values.length+" != "+fieldNames.size());
        return false;
      }
      int i = 0;
      for (String fieldName : fieldNames) {
        String value = values[i];
        if (trim) {
          value = value.trim();
        }
        record.put(fieldName, value);
        i++;
      }
    }
    return child.process(record);
  }

  @Override
  public Command getParent() {
    return parent;
  }
}
