package com.securityx.model.mef.morphline.command;


import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 *  clone {
 *    inputField: "alternative"
 *    fieldValues: [ "first", "second" ]
 *  }
 */

public final class CloneCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("clone");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new CloneCommand(this, config, parent, child, context);
    }


    ///////////////////////////////////////////////////////////////////////////////
    // Nested classes:
    ///////////////////////////////////////////////////////////////////////////////
    private static final class CloneCommand extends AbstractCommand {

        private final String renderedConfig; // cached value
        private final String FieldName;
        private final List<String> values;


        public CloneCommand(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.FieldName = getConfigs().getString(config, "fieldName", "clone");
            this.values =  getConfigs().getStringList(config, "fieldValues", Arrays.asList("1", "2"));

            this.renderedConfig = config.root().render();
        }

        @Override
        protected boolean doProcess(Record record) {
            boolean processed =  false;
            for (String value : this.values){
                Record out = record.copy();
                out.put(this.FieldName, value);
                processed |= super.doProcess(out);
            }
            return processed;
        }

    }

}


