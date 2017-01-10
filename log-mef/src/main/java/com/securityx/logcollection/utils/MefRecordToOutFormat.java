package com.securityx.logcollection.utils;

import org.kitesdk.morphline.api.Record;
import com.securityx.model.mef.field.api.SupportedFormat;
import com.securityx.model.mef.field.api.SupportedFormats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Record cleaner : keeps only fields form the relevant SupportedFormat
 * 
 * @author jyria
 */
public class MefRecordToOutFormat implements RecordToOutFormat {
    private Map<String, SupportedFormat> keys;
    private SupportedFormats outFormat;
    public MefRecordToOutFormat(SupportedFormats format){
        this.keys = new HashMap<String, SupportedFormat>();
        for (SupportedFormat f : format.getSupportedFormatFields()){
            for (SupportedFormat field : f.getSupportedFormatfields()){
                keys.put(field.getPrettyName(), field);
            }
        }
        this.outFormat = format;
    }

  public Record keepFormat(Record record){
      Record out =  new Record();
      for (String key :  record.getFields().keySet()){
          if( this.keys.containsKey(key)){
              List values = record.get(key);
              for (Object value : values){
                out.put(this.keys.get(key).getAvroName(), value);
              }
          }
      }
      out.put("parserOutFormat", this.outFormat.name());
      return out;
  }
    
}
