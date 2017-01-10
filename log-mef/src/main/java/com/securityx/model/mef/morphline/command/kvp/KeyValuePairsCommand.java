package com.securityx.model.mef.morphline.command.kvp;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class KeyValuePairsCommand implements Command {

  private Logger logger = LoggerFactory.getLogger(KeyValuePairsCommand.class);
  private final Config config;
  private final Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final String inputFieldName;
  private final char quoteChar;
  private final boolean trim;
  private final char[] fieldSep;
  private final char[] fieldValueSep;

  public KeyValuePairsCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          String inputFieldName,
          String fieldSep,
          String fieldValueSep,
          String quoteChar,
          boolean trim) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.inputFieldName = inputFieldName;
    this.fieldSep = fieldSep.toCharArray();
    this.fieldValueSep = fieldValueSep.toCharArray();
    this.quoteChar = quoteChar.charAt(0);
    this.trim = trim;

  }

  @Override
  public void notify(Record notification) {
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
      final String str = r.toString();
      List<String> tokens = tokenize(str);
      for (int i = 0; i < tokens.size(); i = i + 2) {
        String key = ((String) tokens.get(i)).trim();
        String value = ((String) tokens.get(i + 1)).trim();
        if (logger.isDebugEnabled()) {
          logger.debug("field=" + key + ", value=" + value);
        }
        record.put(key, (Object) value);
      }
    }
    return child.process(record);
  }

  // change kv pairs into list : field1, value1, field2, value2
  private List<String> tokenize(String str) {
    List<String> out = new ArrayList<String>();
    int id = 0;
    boolean key = true;
    boolean value = false;
    boolean quote = false;
    char[] charStr = str.toCharArray();
    StringBuffer buffer = new StringBuffer();
    while (id < str.length()) {
      char currentChar = charStr[id];
      if (charStr[id] == fieldSep[0] && !quote && value) {
        int id2 = 1;
        boolean isFieldSep = true;
        while (id2 < fieldSep.length && isFieldSep) {
          if (charStr[id + id2] != fieldSep[id2]) {
            isFieldSep = false;
          }
          id2++;
        }
        if (!isFieldSep) {
          buffer.append(charStr[id]);
        } else {

          //old version
          key = true;
          value = false;
          out.add(buffer.toString().trim());
          buffer = new StringBuffer(30);
        }
      } else if (charStr[id] == fieldValueSep[0] && key) {
        int id2 = 1;
        boolean isFieldValueSep = true;
        while (id2 < fieldValueSep.length && isFieldValueSep) {
          if (charStr[id + id2] != fieldSep[id2]) {
            isFieldValueSep = false;
          }
          id2++;
        }
        if (!isFieldValueSep) {
          buffer.append(charStr[id]);
        } else {
          key = false;
          value = true;
          out.add(buffer.toString().trim());
          buffer = new StringBuffer(30);
        }

      } else {
        if (charStr[id] == this.quoteChar) {
          quote = !quote;
        } else {
          buffer.append(charStr[id]);
        }
      }
      id++;
    }
    if (buffer.toString().trim().length() != 0) {
      out.add(buffer.toString().trim());
    } else {
      if (value) {
        out.remove(out.size() - 1);
      }
    }
    if (out.size() % 2 != 0) {
      String tmp = null;
      tmp = (String) out.remove(out.size() - 1);
      tmp = ((String) out.remove(out.size() - 1)).concat(this.fieldSep + tmp);
      out.add(tmp);
    }
    return out;
  }

  @Override
  public Command getParent() {
    return parent;
  }
}
