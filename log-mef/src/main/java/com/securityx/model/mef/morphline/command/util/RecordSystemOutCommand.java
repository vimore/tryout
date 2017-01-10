package com.securityx.model.mef.morphline.command.util;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

/**
 *
 * This class is intended to be a vanilla example of how to make a final step
 * morphline command.
 *
 */
public class RecordSystemOutCommand implements Command {

    private final MorphlineContext morphlineContext;
    private final Boolean verbose;

    public RecordSystemOutCommand(MorphlineContext morphlineContext) {
        this.morphlineContext = morphlineContext;
        this.verbose = Boolean.valueOf(System.getProperty("logMefEnableVerboseOutput"))  ;
    }

    @Override
    public void notify(Record notification) {
    }

    @Override
    public boolean process(Record record) {
        if (this.verbose)
            System.out.println("record:" + record);
        return true;
    }

    @Override
    public Command getParent() {
        return null;
    }
}
