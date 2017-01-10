package com.securityx.model.mef.morphline.command.avro2Record;

import com.securityx.model.mef.field.api.WebProxyMefField;
import com.securityx.model.mef.morphline.lifecycle.LogCollectionNotifications;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

class Avro2RecordCommand implements Command {

  private final Config config;
  private final Command parent;
  private final Command child;
  private final MorphlineContext context;
  private final String filterKey;
  private final String filterValue;
  private final Map<String, String> fields;
  private String currentContainer = null;

  public Avro2RecordCommand(Config config,
          MorphlineContext context,
          Command parent,
          Command child,
          String filterKey,
          String filterValue,
          Map<String, String> fields) {
    this.config = config;
    this.parent = parent;
    this.child = child;
    this.context = context;
    this.filterKey = filterKey;
    this.filterValue = filterValue;
    this.fields = fields;
  }

  @Override
  public void notify(Record notification) {
    for (Object event : LogCollectionNotifications.getLifecycleEvents(notification)) {
      if (event == LogCollectionNotifications.LifecycleEvent.BEGIN_PARSING) {
        this.currentContainer = (String) notification.getFirstValue(Fields.ATTACHMENT_NAME);
      }else if(event == LogCollectionNotifications.LifecycleEvent.END_PARSING) {
        this.currentContainer = null;
      }
    }
    child.notify(notification);
  }

  @Override
  public boolean process(Record record) {
    if (null!= this.filterKey){
      String filteredValue = record.getFirstValue(this.filterKey).toString();
      if (this.filterValue.equals(filteredValue)) { // process
        return child.process(avroRecordToLogCollectionRecord(record));
      } 
      return child.process(record);
    }else{
      return child.process(avroRecordToLogCollectionRecord(record));
    }
  }

  public Record avroRecordToLogCollectionRecord(Record r) {
    Record out = new Record();
    if (this.currentContainer!=null)
      out.put(WebProxyMefField.logCollectionContainer.getPrettyName(), this.currentContainer);
    for (Map.Entry<String, String> f : this.fields.entrySet()) {
      for (Object o : r.get(f.getValue())) {
        if (FIELD_TRANSFORMATIONS.containsKey(f.getValue())) {
          out.put(f.getKey(), FIELD_TRANSFORMATIONS.get(f.getValue()).doTransformation(o));
        } else //default
        {
          out.put(f.getKey(), o);
        }
      }
    }
    return out;
  }

  @Override
  public Command getParent() {
    return parent;
  }

  public String bytesToString(byte[] b, String charset) {
    return new String(b, Charset.forName(charset));
  }

  public interface RecordTranformation<I, O> {
    /**
     *
     * @param input
     * @return output
     */
    public O doTransformation(I input);
  }
  public static Map<String, RecordTranformation> FIELD_TRANSFORMATIONS;

  static {
    FIELD_TRANSFORMATIONS = new HashMap<String, RecordTranformation>();
    FIELD_TRANSFORMATIONS.put("/body",
            new RecordTranformation<Object, String>() {
              public String doTransformation(Object b) {
                return new String((byte[]) b, Charset.forName("UTF-8"));
              }
            }
    );
  }
}
