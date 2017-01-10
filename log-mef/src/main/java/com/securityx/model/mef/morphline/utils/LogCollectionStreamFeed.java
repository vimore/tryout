/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.morphline.utils;

import com.securityx.model.mef.morphline.lifecycle.LogCollectionNotifications;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;

/**
 * log collection feeder, use a morphline script to extract data from a stream
 *  of morphline records. Processing chain has to be aware of the record
 * format to be able to process de data
 *
 * @author jyrialhon
 */
public class LogCollectionStreamFeed implements Feed<Record[]> {

  private final String name;
  private final Command morphline;

  /**
   * constructor
   *
   * @param name the name of the input container
   * @param morphline the morphline conversion chain root command
   */
  public LogCollectionStreamFeed(String name, Command morphline) {
    this.name = name;
    this.morphline = morphline;
  }
  /**
   * runs the conversion chain on record contained in input
   * @param input
   *   an array of morphline records containing data
   * @return
   *   true if processing sucessfully applied to all input data, false otherwise (returned on first failure)
   *   
   * @throws Exception 
   */
  @Override
  public boolean feed(Record[] input) throws Exception {
    for (Record i : input) {

      LogCollectionNotifications.notifyBeginParsing(morphline, name);
      boolean success = morphline.process(i);
      LogCollectionNotifications.notifyEndParsing(morphline);
      if (!success) {
        return false;
      }
    }
    return true;

  }

}
