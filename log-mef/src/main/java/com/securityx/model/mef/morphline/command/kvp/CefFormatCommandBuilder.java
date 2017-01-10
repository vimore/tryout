package com.securityx.model.mef.morphline.command.kvp;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.Collection;
import java.util.Collections;

/**
 * Split key value pairs set 
 * cefKvpCommand  {
 *     inputFieldName : "message" # optional Defaults to "message"
 *     trim : true, # optional. Calls String trim on each value. Default is 'true'
 * }
 */
public class CefFormatCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("cefKvpCommand");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        String inputFieldName = config.getString("inputFieldName");
        if (inputFieldName == null) {
            inputFieldName = ",";
        }

        String fieldHeaderPattern;
        if (config.hasPath("fieldHeaderPattern")) {
            fieldHeaderPattern= config.getString("fieldHeaderPattern");
        }else{
            fieldHeaderPattern = "(?:\\s+|^|#011)";
        }

        boolean trim = true;
        if (config.hasPath("trim")) {
            trim = config.getBoolean("trim");
        }

        return new CefFormatCommand(config,
                context,
                parent,
                child,
                inputFieldName,
                fieldHeaderPattern,
                trim);
    }
}
