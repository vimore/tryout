package com.securityx.model.mef.morphline.command.kvp;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.Collection;
import java.util.Collections;

/**
 * Split key value pairs set 
 * kvpSpliter  {
 *     inputFieldName : "message" # optional Defaults to "message"
 *     fieldSep : " "  # optional Default ","
 *     fieldValueSep : "="  # optional Default "="
 *     quoteChar : "'"  # optional Default "'"
 *     trim : true, # optional. Calls String trim on each value. Default is 'true'
 * }
 */
public class KeyValuePairsCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("kvpSpliter");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        String inputFieldName = config.getString("inputFieldName");
        if (inputFieldName == null) {
            inputFieldName = ",";
        }
        String fieldSep = config.getString("fieldSep");
        if (fieldSep == null) {
            fieldSep = ",";
        }
        String fieldValueSep = config.getString("fieldValueSep");
        if (fieldValueSep == null) {
            fieldValueSep = ",";
        }
        String QuoteChar = config.getString("quoteChar");
        if (QuoteChar == null) {
            QuoteChar = "'";
        }

        boolean trim = true;
        if (config.hasPath("trim")) {
            trim = config.getBoolean("trim");
        }
        
        return new KeyValuePairsCommand(config,
                context,
                parent,
                child,
                inputFieldName,
                fieldSep,
                fieldValueSep,
                QuoteChar,
                trim);
    }
}
