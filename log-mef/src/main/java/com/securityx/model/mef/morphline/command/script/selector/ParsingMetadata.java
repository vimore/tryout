/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.command.script.selector;

import java.util.HashMap;

/**
 *
 * @author jyrialhon
 */
public class ParsingMetadata {
  private HashMap<String,Integer> lastMatched;
  

  public ParsingMetadata() {
    lastMatched = new HashMap<String, Integer>();
  }

  public int getLastMatched(String key) {
    return lastMatched.get(key);
  }

  public void setLastMatched(String key, int lastMatched) {
    this.lastMatched.put(key, lastMatched);
  }
  
  public String getParsableStr(String key, String str){
    if (lastMatched.containsKey(key)){
      return str.substring(lastMatched.get(key));
    }else{
      lastMatched.put(key, 0);
      return str;
    }
    
  }
}
