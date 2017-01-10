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
import org.kitesdk.morphline.base.Configs;
import org.kitesdk.morphline.base.FieldExpression;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * For each input field value who so not have values, add the value to the given
 * record output field if the value isn't already contained in that field.
 */
public final class AddKeyValuesIfMissingBuilder implements CommandBuilder {

  @Override
  public Collection<String> getNames() {
    return Collections.singletonList("addKeyValuesIfMissing");
  }

  @Override
  public Command build(Config config, Command parent, Command child, MorphlineContext context) {
    return new AddKeyValuesIfMissing(this, config, parent, child, context);
  }

  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  private static final class AddKeyValuesIfMissing extends AbstractCommand {

    private final Set<Map.Entry<String, Object>> entrySet;

    public AddKeyValuesIfMissing(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
      super(builder, config, parent, child, context);
      entrySet = new Configs().getEntrySet(config);

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
      for (Map.Entry<String, Object> entry : entrySet) {
        
        String fieldName = entry.getKey();
        prepare(record, fieldName);
        if ( ! record.getFields().keySet().contains(entry.getKey())){
          Object entryValue = entry.getValue();
          Collection results;
          if (entryValue instanceof Collection) {
            results = (Collection) entryValue;
          } else {
            results = new FieldExpression(entryValue.toString(), getConfig()).evaluate(record);
          }
          putAll(record, fieldName, results);
          
        }
      }
      // pass record to next command in chain:
      return super.doProcess(record);
    }

  }

}
