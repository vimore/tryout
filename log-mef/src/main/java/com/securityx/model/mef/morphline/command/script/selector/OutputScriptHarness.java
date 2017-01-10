package com.securityx.model.mef.morphline.command.script.selector;

import com.securityx.model.mef.morphline.command._AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.model.mef.morphline.utils.RecordUtils;

import org.kitesdk.morphline.base.Notifications;

import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class OutputScriptHarness implements ScriptHarness {

  private Logger logger = LoggerFactory.getLogger(OutputScriptHarness.class);
  final String transferFieldNamedToMessage;
  final String messageFieldName;
  final MorphlineHarness morphlineHarness;
  final _AssertRecordOutCommand finalChild;
  final boolean fullCopyMode;
  private boolean init = false;
  private final List<Record> notifBuffer  = new ArrayList<Record>();

  private void init(){

    try {
      this.startup();
      this.init = true;
    } catch (Exception ex) {
      java.util.logging.Logger.getLogger(OutputScriptHarness.class.getName()).log(Level.SEVERE, "exception raised during harness startup : " + ex.getMessage(), ex);
    }
    if(this.init){
      for (Record notif : notifBuffer){
        this.notify(notif);
      }
      this.notifBuffer.clear();
    }

  }

  public OutputScriptHarness(String transferFieldNamedToMessage,  String messageFieldName, MorphlineHarness morphlineHarness, _AssertRecordOutCommand finalChild, boolean fullCopyMode) {
    this.transferFieldNamedToMessage = transferFieldNamedToMessage;
    this.morphlineHarness = morphlineHarness;
    this.finalChild = finalChild;
    this.fullCopyMode = fullCopyMode;
    this.messageFieldName = messageFieldName;
  }

  @Override
  public String getTransferFieldNamedToMessage() {
    return transferFieldNamedToMessage;
  }

  @Override
  public void startup() throws Exception {
    morphlineHarness.startup(finalChild);
  }

  private Record buildInput(Record in) {
    Record out = in.copy();
    for (String field : in.getFields().keySet()) {
      if (!field.equals(Fields.ATTACHMENT_BODY)
              && !field.equals(Fields.ATTACHMENT_MIME_TYPE)) {
        out.removeAll(field);
      }
    }
    return out;
  }

  @Override
  public List<Record> feed(Record record) {
    Record rawData=null;
    List<Record> out = new ArrayList<Record>();

    List originalMessage=null;
    if (logger.isDebugEnabled()) {
      logger.debug("INFO : feed : " + this.morphlineHarness.getHarnessid());
    }
    originalMessage = record.get(Fields.MESSAGE);
    if (this.fullCopyMode) {
      rawData = record.copy();
      if ( this.messageFieldName != null && transferFieldNamedToMessage != null &&  !messageFieldName.equals(transferFieldNamedToMessage) && record.getFirstValue(transferFieldNamedToMessage) != null) {
        rawData.put(messageFieldName, record.getFirstValue(transferFieldNamedToMessage));
      }
    } else {
      if (this.messageFieldName == null || transferFieldNamedToMessage == null || record.get(transferFieldNamedToMessage) == null) {
        if (logger.isDebugEnabled())
          logger.debug("ERROR : invalid parameters : fullCopyMode: "+this.fullCopyMode+
                ", transferFieldNamedToMessage: "+this.transferFieldNamedToMessage+" value: "+record.get(transferFieldNamedToMessage)+
                ", messageFieldName: "+this.messageFieldName);
        this.finalChild.process(record);
        out.add(record);
        return out;
      }
      /*    if (!transferFieldNamedToMessage.equals(Fields.MESSAGE)) {
       List transfer = record.get(transferFieldNamedToMessage);
       record.removeAll(Fields.MESSAGE);
       for (Object t : transfer) {
       record.put(transferFieldNamedToMessage, t);
       }
       }*/
      rawData = this.buildInput(record);
      rawData.put(messageFieldName, record.getFirstValue(transferFieldNamedToMessage));
    }
    if (logger.isDebugEnabled()) {
        logger.debug("OutScriptHarness : " + this.getName()) ;
        logger.debug("OutScriptHarness processing : " + rawData);
    }
    if (!init) init();
    boolean processed = morphlineHarness.feedRecords(rawData);

    if (processed) {    // flush the results
      int nbRecord = this.finalChild.getNumRecords();
      if (nbRecord >= 1) {
        List<Record> outputRecords = new ArrayList<Record>(Arrays.asList(this.finalChild.flushRecords()));

        for (Record r : outputRecords) {
          if (!this.fullCopyMode) {
            record = RecordUtils.appendToRecord(record, r, transferFieldNamedToMessage);
            if (originalMessage != null) {
              record.removeAll(Fields.MESSAGE);
              for (Object o : originalMessage) {
                record.put(transferFieldNamedToMessage, o);
              }
            }
          } else {
            record = r;
          }
          out.add(record);
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug("ERROR : Expected getNumberRecord() > 1 but got " + nbRecord);
        }
      }
      return out;
    } else {
      this.finalChild.clear();
      return out;
    }
  }

  @Override
  public void shutdown() {
    if (this.init)
      this.morphlineHarness.shutdown();
  }

  public String getName() {
    return this.morphlineHarness.getHarnessid();
  }

  public String toString() {
    return "OutputScriptHarness: " + this.getName();
  }

  private void processNotification(Record notification){
    boolean isShutdown =  false;
    for (Object event : Notifications.getLifecycleEvents(notification)) {
      if (event == Notifications.LifecycleEvent.SHUTDOWN) {
        isShutdown = true;
        break;
      }
    }
    if (! isShutdown){
      this.notifBuffer.add(notification.copy());
    }

  }



  @Override
  public void notify(Record notification) {
    if (!this.init){
      processNotification(notification);
    } else {
      this.morphlineHarness.notify(notification);
    }
  }
}
