package com.securityx.model.mef.morphline.command.discardempty;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;

import java.util.Collection;
import java.util.Collections;

/**
 * Split key value pairs set 
 * discardEmpty  {
 * }
 */
public class DiscardEmptyFieldsCommandBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("discardEmpty");
    }

    @Override
    public Command build(Config config, Command parent, Command child, MorphlineContext context) {
        return new DiscardEmptyFieldsCommand(config,
                context,
                parent,
                child);
    }
}
