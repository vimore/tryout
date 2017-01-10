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
 * tsvq  {
 *     inputFieldName " "message" # optional Defaults to "message"
 *     fieldSep : ","  # optional Default ","
 *     quoteChar : "'"  # optional Default "'"
 *     trim : true, # optional. Calls String trim on each value. Default is 'true'
 *     t
 *     fieldNames : [ # mandatory
 *          "f1",
 *          "f2"
 *     ]
 * }
 */
public class TokenSeperatedValuesQuotedCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("tsvq");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        String fieldSep = config.getString("fieldSep");
        if (fieldSep == null) {
            fieldSep = ",";
        }
        String QuoteChar = config.getString("quoteChar");
        if (QuoteChar == null) {
            QuoteChar = "'";
        }

        String inputFieldName = config.getString("inputFieldName");
        if (inputFieldName == null) {
            inputFieldName = Fields.MESSAGE;
        }
        boolean trim = true;
        if (config.hasPath("trim")) {
            trim = config.getBoolean("trim");
        }

        boolean greedy = false;
        if (config.hasPath("greedy")) {
            greedy = config.getBoolean("greedy");
        }

        List<String> fieldNames = config.getStringList("fieldNames");
        if (fieldNames == null || fieldNames.isEmpty()) {
            throw new IllegalStateException("tsv must have fieldNames declared.");
        }
        return new TokenSeperatedValuesQuotedCommand(config,
                context,
                parent,
                child,
                inputFieldName,
                fieldSep,
                QuoteChar,
                fieldNames,
                trim, greedy);
    }
}
