package com.securityx.model.mef.morphline.command;

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
public class _AssertRecordOutCommand implements Command {
  private Logger logger = LoggerFactory.getLogger(_AssertRecordOutCommand.class);
  private final MorphlineContext morphlineContext;
  private List<Record> processedRecords = new ArrayList<Record>();
  private boolean stdout;

  public _AssertRecordOutCommand(MorphlineContext morphlineContext) {
    this.morphlineContext = morphlineContext;
    this.stdout = false;
  }


  public _AssertRecordOutCommand(MorphlineContext morphlineContext, boolean stdout) {
    this.morphlineContext = morphlineContext;
    this.stdout = stdout;
  }

  @Override
  public void notify(Record notification) {
    //dead end street, command is not supposed to have child
  }

  @Override
  public boolean process(Record record) {
    processedRecords.add(record);
    if (this.stdout) {
      System.out.println("record:" + record);
    }
    if(logger.isDebugEnabled())
      logger.debug("record:"+record);
    return true;
  }

  public void setStdout(boolean stdout) {
    this.stdout = stdout;
  }

  @Override
  public Command getParent() {
    return null;
  }

  public void clear() {
    processedRecords.clear();
  }

  public int getNumRecords() {
    return processedRecords.size();
  }

  private Record getRecord(int index) {
    return processedRecords.get(index);
  }

  private  Record[] getRecords() {
    return processedRecords.toArray(new Record[processedRecords.size()]);
  }
  public  Record[] flushRecords() {
    Record[] out = processedRecords.toArray(new Record[processedRecords.size()]);
    this.clear();
    return out;
  }

}
