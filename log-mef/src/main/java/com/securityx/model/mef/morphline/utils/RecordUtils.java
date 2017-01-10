/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.morphline.utils;

import org.kitesdk.morphline.api.Record;

import java.util.Map;

/**
 *
 * @author jyrialhon
 */
public class RecordUtils {
  
  public static Record clearField(Record record, String... filters){
    for(String filter : filters){
      record.removeAll(filter);
    }
    return record;
  }

  public static Record appendToRecord(Record dst, Record data, String... filters) {
    if (data != null && dst != null) {
      for (Map.Entry<String, Object> entry : data.getFields().entries()) {
        boolean filtered =  false;
        for (String filter : filters){
          if(filter.equals(entry.getKey())){
            filtered = true;
            break;
          }
        }
        if (! filtered)
          dst.put(entry.getKey(), entry.getValue());
      }
      return dst;
    } else {
      return null;
    }
  }
  
  public static Record[] appendToRecords(Record[] dst, Record data, String... filters){
    for (int i = 0; i< dst.length; i++){
      dst[i] = appendToRecord(dst[i], data, filters);
    }
    return dst;
  }
}
