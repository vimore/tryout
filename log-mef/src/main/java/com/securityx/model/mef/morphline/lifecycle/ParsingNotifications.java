/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.lifecycle;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;

import java.io.File;
import java.util.List;

/**
 * Notification events are used to provide data to morphline command through
 * the notification channel 
 * used by : 
 *   @see com.securityx.model.mef.morphline.command.stream  
 * @author jyrialhon
 */
public class ParsingNotifications {
   public static final String LIFE_CYCLE = "ParsingNotifications";
   public static final String PARSING_DEST = "ParsingDestination";
  /**
   * return the notification event of a notification record
   * @param notification
   *  a notication record 
   * @return 
   */
  public static List getLifecycleEvents(Record notification) {
    return notification.get(LIFE_CYCLE);
  }
  
  /**
   * forges the morphline record
   * @param name
   *  morphline record containing the notification
   * @return 
   */
  private static Record genRecord(File name){
    Record out = new Record();
    out.put(PARSING_DEST, name);
    return out;
  }
  /**
   * generates event to notify commands parsing begins
   * @param command
   * @param outfile 
   *   the output file for command handling it.
   */
  public static void notifyBeginParsing(Command command, File outfile ) {
    notify(command, LifecycleEvent.BEGIN_PARSING, genRecord(outfile));
  }
  /**
   * generates event to notify commands parsing completed
   * @param command 
   */
  public static void notifyEndParsing(Command command) {
    notify(command, LifecycleEvent.END_PARSING);
  }
  
  /**
   * creates a new notification record and appends a notification event to it
   * @param command
   *   a morphline command
   * @param event 
   *    an event
   */
  private static void notify(Command command, LifecycleEvent event) {
    Record notification = new Record();
    notification.put(LIFE_CYCLE, event);
    command.notify(notification);
  }
  /**
   * takes a notification record and appends a notification event to it
   * @param command
   *   a morphline command
   * @param event
   *   an event
   * @param notification 
   *   a morphline record
   */
  private static void notify(Command command, LifecycleEvent event, Record notification) {
    notification.put(LIFE_CYCLE, event);
    command.notify(notification);
  }
  
  /**
   * test is notification record is contains event
   * @param notification
   * @param event
   * @return 
   */
  public static boolean containsLifecycleEvent(Record notification, LifecycleEvent event) {
    return getLifecycleEvents(notification).contains(event);
  }
  
  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  /**
   * Enum describing supported events
   */
  public static enum LifecycleEvent {
    /**
     * before first record injection in the conversion line
     */
    BEGIN_PARSING, 
    /**
     * after last record parsing result
     */
    END_PARSING;
  }     

}
 

