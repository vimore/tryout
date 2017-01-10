package com.securityx.logcollection.utils;

import com.securityx.logcollection.parser.avro.EventToRecordConverter;
import com.securityx.model.mef.field.api.*;
import org.kitesdk.morphline.api.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Record cleaner : keeps only fields form the relevant UnMatchedTrackingType
 * 
 * @author jyria
 */
public class UnMatchedRecordToOutFormat implements RecordToOutFormat {
    private final UnMatchedTrackingType type;
    public UnMatchedRecordToOutFormat(UnMatchedTrackingType type){
        this.type = type;
    }

  public Record keepFormat(Record record){
      Record out = null;
      switch(type) {
          case KEEP_ALL:
              out = record;
            break;
          case KEEP_ONLY_MESSAGE:
              out =  new Record();
              getLogCollectionFields(record, out);
              break;
          case KEEP_MESSAGE_AND_ERROR:
              out =  new Record();
              getLogCollectionFields(record, out);
              getValidationErrorField(record, out);

              break;
          default:
              break;

      }
      return out;
  }

    private void getValidationErrorField(Record record, Record out) {
        if(record.getFields().containsKey(ValidationLogger.VALIDATION_LOG)){
          out.put(ValidationLogger.VALIDATION_LOG, record.getFirstValue(ValidationLogger.VALIDATION_LOG));
      }
        if(record.getFields().containsKey(WebProxyMefField.logSourceType.getPrettyName())){
          out.put(WebProxyMefField.logSourceType.getPrettyName(), record.getFirstValue(WebProxyMefField.logSourceType.getPrettyName()));
      }
    }


    private void getLogCollectionFields(Record record, Record out) {
        for (EventToRecordConverter.LogCollectionFields field: EventToRecordConverter.LogCollectionFields.values()){
            if(record.getFields().containsKey(field.getPrettyName())){
              out.put(field.getPrettyName(), record.getFirstValue(field.getPrettyName()));
          }
        }
    }

}
