package com.securityx.model.mef.morphline.command.script.selector;

import org.kitesdk.morphline.api.Record;

import java.util.List;

interface ScriptHarness {

    void startup() throws Exception;
    
    String getTransferFieldNamedToMessage();

    List<Record> feed(Record record);

    void shutdown();
    
    String getName();
    
    void notify(Record notification); 
    
}
