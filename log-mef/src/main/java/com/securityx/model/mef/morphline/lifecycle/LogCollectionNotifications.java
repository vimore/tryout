/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.lifecycle;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import java.util.List;

/**
 *
 * @author jyrialhon
 */
public class LogCollectionNotifications {
   public static final String LIFE_CYLCLE = "logCollectionLifecycle";
  
  public static List getLifecycleEvents(Record notification) {
    return notification.get(LIFE_CYLCLE);
  }

  private static Record genRecord(String name){
    Record out = new Record();
    out.put(Fields.ATTACHMENT_NAME, name);
    return out;
  }
  
  public static void notifyBeginParsing(Command command, String name ) {
    notify(command, LifecycleEvent.BEGIN_PARSING, genRecord(name));
  }

  public static void notifyEndParsing(Command command) {
    notify(command, LifecycleEvent.END_PARSING);
  }
  
  
  private static void notify(Command command, LifecycleEvent event) {
    Record notification = new Record();
    notification.put(LIFE_CYLCLE, event);
    command.notify(notification);
  }
  private static void notify(Command command, LifecycleEvent event, Record notification) {
    notification.put(LIFE_CYLCLE, event);
    command.notify(notification);
  }
  
  public static boolean containsLifecycleEvent(Record notification, LifecycleEvent event) {
    return getLifecycleEvents(notification).contains(event);
  }
  
  ///////////////////////////////////////////////////////////////////////////////
  // Nested classes:
  ///////////////////////////////////////////////////////////////////////////////
  public static enum LifecycleEvent {
    BEGIN_PARSING, 
    END_PARSING;
  }     

}
 

