/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.model.mef.morphline.command.record.selector;

/**
 *
 * @author jyrialhon
 */
 enum RSAction {
  accept(true, false), acceptStore(true, true), discard(false, false), discardStore(false, true);
  protected boolean passthrough;
  protected boolean store;

  RSAction(boolean passThrough, boolean store) {
    this.passthrough = passThrough;
    this.store = store;
  }
  
}
