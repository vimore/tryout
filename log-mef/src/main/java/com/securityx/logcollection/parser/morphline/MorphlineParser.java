package com.securityx.logcollection.parser.morphline;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

/**
 * runs a morphline conversion chain assuming that chains includes a command
 * handling com.securityx.model.mef.morphline.lifecycle.ParsingNotification to
 * configure output file
 *
 * @author jyrialhon
 */
public class MorphlineParser {

  private final MorphlineContext morphlineContext;
  private final MorphlineHarness morphlineHarness;
  private AssertRecordOutCommand streamCommand;
  

  public AssertRecordOutCommand getStreamCommand() {
    return streamCommand;
  }

  /**
   * constructor
   *
   * @param morphlineId id of the morphline
   * @param confFile file containing morphline script
   * @param verbose (de)activate record content dump as output
   * @throws Exception
   */
  public MorphlineParser(String morphlineId, String confFile, boolean verbose) throws Exception {
    this.morphlineContext = new MorphlineContext.Builder().build();

    Config morphlineConf = MorphlineResourceLoader.getConfFile(confFile);
    //this.outCommand = new AssertRecordOutCommand(morphlineContext, verbose);
    this.streamCommand = new AssertRecordOutCommand(morphlineContext);
    this.morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, morphlineId);
    this.morphlineHarness.startup(streamCommand);
  }

  /**
   * runs morphline conversion chain on records, morphline script expected to
   * contain a command handling ParsingNotification
   *
   * @see com.securityx.model.mef.morphline.lifecycle.ParsingNotifications
   * @param records
   * @return parsing result true if everything runs well, false otherwise
   */
  public boolean feedRecords(Record... records) {

    return this.morphlineHarness.feedRecords(records);
  }

  /**
   * shutdown morphline conversion chain
   */
  public void shutdown() {
    this.morphlineHarness.shutdown();
  }
}
