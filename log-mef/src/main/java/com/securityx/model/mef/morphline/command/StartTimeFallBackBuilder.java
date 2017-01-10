/*
 * Copyright 2013 Cloudera Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*
 * Copyright 2013 Cloudera Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.securityx.model.mef.morphline.command;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For each input field value who so not have values, add the value to the given
 * record output field if the value isn't already contained in that field.
 */
public final class StartTimeFallBackBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("startTimeFallBack");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    return new StartTimeFallBackCommand(this, config, parent, child, context);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class StartTimeFallBackCommand extends AbstractCommand {

    private String lastStartTime = null;
    private Matcher startTime = Pattern.compile("^(\\d{8,12})\\.(\\d{1,6})$").matcher("") ;

    public StartTimeFallBackCommand(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);

    }

    protected void putAll(Record record, String key, Collection values) {
      for (Object value : values) {
        put(record, key, value);
      }
    }

    protected void put(Record record, String key, Object value) {
      record.replaceValues(key, value);
    }

    protected void prepare(Record record, String key) {
    }

    @Override
    protected boolean doProcess(Record record) {
      Boolean doReplace=false;
      if (record.getFields().containsKey("startTime")) {
        Object objValue  = record.getFirstValue("startTime");
        String value = null;
        if (objValue instanceof String){
          value = (String)objValue;
        } else{
          value = String.valueOf(objValue);
        }
        startTime.reset(value);
        if ( startTime.matches()){
          lastStartTime = new String(value);
        }else{
          doReplace = true;
        }
      }else {
        doReplace  =true;
      }

      if (doReplace && this.lastStartTime != null){
        record.removeAll("startTime");
        record.put("startTime", lastStartTime);
      }

      // pass record to next command in chain:
      return super.doProcess(record);
    }

  }

}
