/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.datacapture;

/**
 *
 * @author jyria <jean-yves@e8security.com>
 */
public class E8RawLogUUID {
  String region;
  String uuidType1;
  String src;
  String log;
  public E8RawLogUUID(String uuid){
    String[] parts = uuid.split("-");
    region = parts[0];
    uuidType1 = parts[1]+"-"+parts[2]+"-"+parts[3]+"-"+parts[4]+"-"+parts[5];
    src = parts[6];
    log= parts[7];
    }

  public String getRegion() {
    return region;
  }

  public String getUuidType1() {
    return uuidType1;
  }

  public String getSrc() {
    return src;
  }

  public String getLog() {
    return log;
  }
  
  
}
