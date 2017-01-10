/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.lifecycle;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;

import java.util.List;

/**
 *
 * @author jyrialhon
 */
public class TsvqNotifications {
   public static final String LIFE_CYLCLE = "blueCoatNotification";
   public static final String TSVQ_HEADER = "blueCoatHeader";
  
  public static List getLifecycleEvents(Record notification) {
    return notification.get(LIFE_CYLCLE);
  }

  private static Record genRecord(List<String> fields){
    Record out = new Record();
    out.put(TSVQ_HEADER, fields);
    return out;
  }
  
  public static void notifyHeaderUpdate(Command command, List<String> fields ) {
    notify(command, LifecycleEvent.HEADER_UPDATE, genRecord(fields));
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
    HEADER_UPDATE, 
    ;
  }     

}
 

