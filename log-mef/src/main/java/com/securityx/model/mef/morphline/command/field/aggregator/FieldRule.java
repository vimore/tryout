/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.command.field.aggregator;

import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

/**
 *
 * @author jyrialhon
 */
public class FieldRule {
  protected Logger logger = LoggerFactory.getLogger(FieldRule.class);
  private final List<String> fieldList;
  private final String strFormat;

  public FieldRule(List<String> FieldList, String strFormat) {
    this.fieldList = FieldList;
    this.strFormat = strFormat;
  }
  
  public boolean process(Record r, String field){
    if (checkFields(r.getFields().keySet())){
      genFieldValue(r, field);
      return true;
    }else{
      if (logger.isDebugEnabled()){
        logger.debug("ERROR : processing "+field+" checkfields failed" );
        logger.debug("ERROR : record in error "+r );
      }
      return false;
    }
  }

  private boolean checkFields(Set<String> keySet) {
    boolean found = true;
    String field=null;
    Iterator<String> it = this.fieldList.iterator();
    while (found  && it.hasNext()){
      field = it.next();
      found = found && keySet.contains(field);
    }
    if (logger.isDebugEnabled() && ! found)
      logger.debug("DEBUG checkFields failed on "+ field +" "+this.fieldList.toString());
    return found;
  }

  private void genFieldValue(Record r, String outField) {
    String out = this.strFormat;
    if (logger.isDebugEnabled())
      logger.debug("DEBUG : processing "+outField+" format "+this.strFormat );

    for (String field : this.fieldList){
      
      String value =  String.valueOf( r.getFirstValue(field));
        if (logger.isDebugEnabled()){
          logger.debug("DEBUG : replacing field "+field );
          logger.debug("DEBUG : out : \""+out+"\", value="+value);
        }
        out = out.replaceAll("%\\{"+field+"\\}", Matcher.quoteReplacement(value));
    }
    r.put(outField, out);
    
  }
  
}
