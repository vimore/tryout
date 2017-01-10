/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.parser.morphline;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command._AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

/**
 * transformer
 *
 * @author jyrialhon
 */
public class MorphlineTransformer {

  private MorphlineContext morphlineContext;
  private MorphlineHarness morphlineHarness;
  private _AssertRecordOutCommand outCommand;

  /**
   * constructor
   *
   * @param morphlineId morphline script id
   * @param confFile file containing morphline script
   * @throws Exception
   */
  public MorphlineTransformer(String morphlineId, String confFile) throws Exception {
    this.morphlineContext = new MorphlineContext.Builder().build();
    
    Config morphlineConf = MorphlineResourceLoader.getConfFile(confFile);
    this.outCommand = new _AssertRecordOutCommand(morphlineContext);
    this.morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, morphlineId);
    this.morphlineHarness.startup(outCommand);
  }

  /**
   * feed morphline convertion chain with a set of record representing the
   * content of the a container identified by name.
   *
   * @param name name or id of the original container of data
   * @param records a set of morphline records describing data content to be
   * converted by morphline script
   * @return the parsing result, true if successfull, false otherwise
   */
  public boolean feedRecords(String name, Record... records) {
    return this.morphlineHarness.feedRecords(name, records);
  }

  /**
   * get parsing result and flush processing cache
   *
   * @return an array of morphline record decribing parsed data.
   */
  public Record[] GetRecords() {
    return this.outCommand.flushRecords();
  }

  /**
   * shutdown morphline conversion chain
   */
  public void shutdown() {
    this.morphlineHarness.shutdown();
  }

}
