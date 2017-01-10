
package com.securityx.model.mef.morphline.command.script.selector;

import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * NOT THREAD SAFE!!!
 */
class AccumulativeScriptHarness implements ScriptHarness, Command { 
    final Command parent;
    final String transferFieldNamedToMessage; 
    final MorphlineHarness morphlineHarness;
    private final AtomicReference<Record> lastRecord = new AtomicReference<Record>();
    private boolean init=false;

    public AccumulativeScriptHarness(Command parent, String transferFieldNamedToMessage, MorphlineHarness morphlineHarness) {
        this.parent = parent;
        this.transferFieldNamedToMessage = transferFieldNamedToMessage;
        this.morphlineHarness = morphlineHarness;
    }

    private void init(){

        try {
            this.startup();
            this.init = true;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AccumulativeScriptHarness.class.getName()).log(Level.SEVERE, "exception raised during harness startup : " + ex.getMessage(), ex);
        }

    }


    @Override
    public String getTransferFieldNamedToMessage() {
        return transferFieldNamedToMessage;
    }
    
    @Override
    public void startup() throws Exception{
        morphlineHarness.startup(this);
    }

    @Override
    public void shutdown() {
        if (this.init)
            this.morphlineHarness.shutdown();
    }

    @Override
    public List<Record> feed(Record record) {
        List<Record> out=new ArrayList<Record>();
        if (record == null) {
            return null;
        }
        try {
            if (record.get(transferFieldNamedToMessage) == null) {
                out.add(record);
                return out;
            }
            
            List originalMessage = record.get(Fields.MESSAGE);
            if (!transferFieldNamedToMessage.equals(Fields.MESSAGE)) {
                List transfer = record.get(transferFieldNamedToMessage);
                record.removeAll(Fields.MESSAGE);
                for (Object t : transfer) {
                    record.put(transferFieldNamedToMessage, t);
                }
            }
            if (!init) init();
            boolean processed = morphlineHarness.feedRecords(record);
            if (processed) {
                Record result = lastRecord.get();
                if (originalMessage != null) {
                    result.removeAll(Fields.MESSAGE);
                    for(Object o:originalMessage) {
                        result.put(transferFieldNamedToMessage, o);
                    }
                }
                out.add(result);
                return out;
            }
            return null;
        } finally {
            lastRecord.set(null);
        }
    }

    @Override
    public void notify(Record notification) {
        this.morphlineHarness.notify(notification);
    }

    @Override
    public boolean process(Record record) {
        lastRecord.set(record);
        return true;
    }

    @Override
    public Command getParent() {
        return parent;
    }

  @Override
  public String getName() {
    return this.morphlineHarness.getHarnessid();
  }

  
}
