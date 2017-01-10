/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.logcollection.parser;

import org.kitesdk.morphline.api.Record;

import java.nio.charset.Charset;

/**
 *
 * @author jyrialhon
 */
public class LogCollectionRecordFactory {
  /*
  /headers/category, 
  /headers/timestamp, 
  _attachment_mimetype, 
  /headers/flume.syslog.status, 
  /body, 
  /headers/hostname, 
  _attachment_body, 
  /headers/Facility, 
  /headers/Severity
  */
  public String bytesToString(byte[] b, String charset ){
    return new String(b, Charset.forName(charset));
  }
  
  /**
   * formater 
   * @param r 
   */
  public static void avroRecordToLogCollectionRecord(Record r){
    String cat = (String)r.getFirstValue(HEADERSCATEGORY);
    if ("syslog".equals(cat)){ // process syslog
          byte[] line = (byte[]) r.getFirstValue(BODY);
          String str = new String(line, Charset.forName("UTF-8"));
          
          r.removeAll(BODY);
          r.removeAll(ATTACHMENT_MIMETYPE);
          r.removeAll(HEADERSCATEGORY);
          r.removeAll(HEADERSFLUMESYSLOGSTATUS);
          r.removeAll(HEADERSHOSTNAME);
          r.removeAll(HEADERSFACILITY);
          r.removeAll(HEADERSSEVERITY);
          r.removeAll(HEADERSTIMESTAMP);
          r.removeAll(ATTACHMENT_BODY);
          r.put(LOGCOLLECTION_MESSAGE, str);
          r.put(LOGCOLLECTION_TYPE, cat);
    }else{
      // to be continued
    }
    
  }
  public static final String ATTACHMENT_BODY = "_attachment_body";
  public static final String HEADERSTIMESTAMP = "/headers/timestamp";
  public static final String HEADERSSEVERITY = "/headers/Severity";
  public static final String HEADERSFACILITY = "/headers/Facility";
  public static final String HEADERSHOSTNAME = "/headers/hostname";
  public static final String HEADERSFLUMESYSLOGSTATUS = "/headers/flume.syslog.status";
  public static final String ATTACHMENT_MIMETYPE = "_attachment_mimetype";
  public static final String LOGCOLLECTION_TYPE = "logCollectionType";
  public static final String LOGCOLLECTION_MESSAGE = "message";
  public static final String BODY = "/body";
  public static final String HEADERSCATEGORY = "/headers/category";
}
