package com.securityx.model.mef.morphline.command.script.selector;

import org.kitesdk.morphline.api.Record;

import java.util.ArrayList;
import java.util.List;

public class SelectorPatternOutput {
    protected List<Record> output;
    protected boolean matched =false;

    public SelectorPatternOutput(List<Record> recordList, boolean matched) {
        this.output = recordList;
        this.matched = matched;
    }
    public SelectorPatternOutput(Record record, boolean matched) {
        this.output = new ArrayList<Record>();
        this.output.add(record);
        this.matched = matched;
    }

    public List<Record> getOutput() {
        return output;
    }

    public boolean isMatched() {
        return matched;
    }
}
