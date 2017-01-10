/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.command.field.aggregator;

import org.kitesdk.morphline.api.Record;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jyrialhon
 */
public class FieldRulesProcessor {
  private final String fieldName;
  private final List<FieldRule> rules;
  
  
public FieldRulesProcessor(String FieldName, List<FieldRule> rules){
  this.fieldName = FieldName;
  this.rules = rules;
}

public boolean process (Record r){
  boolean match = false;
  Iterator<FieldRule> it = this.rules.iterator();
  while (!match && it.hasNext()){
    FieldRule rule = it.next();
    match = rule.process(r, fieldName);
  }
  return match;
}

  public String getFieldName() {
    return fieldName;
  }
}
