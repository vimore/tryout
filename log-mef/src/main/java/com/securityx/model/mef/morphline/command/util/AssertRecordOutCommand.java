package com.securityx.model.mef.morphline.command.util;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class AssertRecordOutCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(AssertRecordOutCommand.class);

    private final MorphlineContext morphlineContext;
    private List<Record> processedRecords = new ArrayList<Record>();
    private boolean isVerbose;

    public AssertRecordOutCommand(MorphlineContext morphlineContext) {
        this(morphlineContext, Boolean.valueOf(System.getProperty("logMefEnableVerboseOutput")));
    }
    public AssertRecordOutCommand(MorphlineContext morphlineContext, boolean isVerbose) {
        this.morphlineContext = morphlineContext;
        this.isVerbose = isVerbose;
        if (logger.isDebugEnabled())
            logger.info("AssertRecordOutCommand: logMefEnableVerboseOutput set to "+System.getProperty("logMefEnableVerboseOutput"));

    }

    @Override
    public void notify(Record notification) {
    }

    @Override
    public boolean process(Record record) {
        processedRecords.add(record);
        if (isVerbose)
         logger.info("record:" + record);
        return true;
    }

    @Override
    public Command getParent() {
        return null;
    }

    public Record[] flushRecords(){
        Record[] out = this.getRecords();
        this.clear();
        return out;
    }
    public void clear() {
        processedRecords.clear();
    }

    public int getNumRecords() {
        return processedRecords.size();
    }

    public Record getRecord(int index) {
        return processedRecords.get(index);
    }
    
    private Record[] getRecords(){
        Record[] out = processedRecords.toArray(new Record[processedRecords.size()]);
        return out;
    }

    public boolean assertValue(int index, String field, String value) {
        Record r = getRecord(0);
        List got = r.get(field);
        return got.get(0).equals(value);
    }
}
