package com.securityx.model.mef.morphline.command.tsv;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.base.Fields;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fast way to turn a stably order string of token separated values in to name record
 * tsv  {
 *     inputFieldName " "message" # optional Defaults to "message"
 *     splitRegex : ","  # optional Default ","
 *     trim : true, # optional. Calls String trim on each value. Default is 'true'
 *     fieldNames : [ # mandatory
 *          "f1",
 *          "f2"
 *     ]
 * }
 */
public class TokenSeperatedValuesCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("tsv");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        String splitRegex = config.getString("splitRegex");
        if (splitRegex == null) {
            splitRegex = ",";
        }
        String inputFieldName = config.getString("inputFieldName");
        if (inputFieldName == null) {
            inputFieldName = Fields.MESSAGE;
        }
        boolean trim = true;
        if (config.hasPath("trim")) {
            trim = config.getBoolean("trim");
        }
        
        List<String> fieldNames = config.getStringList("fieldNames");
        if (fieldNames == null || fieldNames.isEmpty()) {
            throw new IllegalStateException("tsv most have fieldNames declared.");
        }
        return new TokenSeperatedValuesCommand(config,
                context,
                parent,
                child,
                inputFieldName,
                splitRegex,
                fieldNames,
                trim);
    }
}
