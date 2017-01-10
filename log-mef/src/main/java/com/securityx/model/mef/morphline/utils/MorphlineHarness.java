package com.securityx.model.mef.morphline.utils;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineCompilationException;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Configs;
import org.kitesdk.morphline.base.Fields;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
/**
 * harness to run a morphline conversion chain
 * @author jyrialhon
 */
public class MorphlineHarness {
  private final Logger logger = LoggerFactory.getLogger(MorphlineHarness.class);
  private final Config morphlineConfig;
  private final String morphlineId;
  private final MorphlineContext morphlineContext;
  private Command morphline;
  /**
   * Constructor
   * @param morphlineContext
   *   the morphline context used to create the conversion chain
   * @param morphlineConfig
   *   the file containing the description of morphline script
   * @param morphlineId 
   *   the id of the morphline script
   */
  public MorphlineHarness(MorphlineContext morphlineContext, Config morphlineConfig, String morphlineId) {
    Preconditions.checkNotNull(morphlineConfig);
    Preconditions.checkNotNull(morphlineId);
    this.morphlineContext = morphlineContext;
    
    this.morphlineConfig = find(morphlineId, morphlineConfig, "Getting morphline command");
    this.morphlineId = morphlineId;
  }
  
  /**
   * user friendly naming of the harness
   * @return 
   *   harness name in format morphlineFile.morphlineId
   */
  public String getHarnessid(){
    return this.morphlineConfig.origin()+"."+this.morphlineId;
  }
  
  /**
   * Init of the morphline conversion chain. up to finalChild command to handle the result.
   * @param finalChild
   *   the tail command of the conversion chain 
   * @throws Exception 
   */
  public void startup(Command finalChild) throws Exception{
    
    try {
      morphline = new org.kitesdk.morphline.base.Compiler().compile(morphlineConfig, morphlineContext, finalChild);

    } catch (Exception x) {
      //TODO figure out what logging framework we are going to use.
      logger.error("failed to init "+this.getHarnessid());
      logger.error("ERROR : Failed to compile " + morphlineConfig + " : "+x.getMessage(), x);
      if(logger.isDebugEnabled())
          logger.debug("ERROR : Failed to compile " + morphlineConfig + " : "+x.getMessage() );

      throw x;
    }

  }
  /**
   * Finds the given morphline id within the given morphline config, using the given nameForErrorMsg
   * for error reporting.
   */
  private Config find(String morphlineId, Config config, String nameForErrorMsg) {
    List<? extends Config> morphlineConfigs = null;
    if (config != null && config.hasPath("morphlines")){
      morphlineConfigs = config.getConfigList("morphlines");
      if (morphlineConfigs.isEmpty()) {
        throw new MorphlineCompilationException(
            "Morphline file must contain at least one morphline: " + nameForErrorMsg, null);
      }
      if (morphlineId != null) {
        morphlineId = morphlineId.trim();
      }
      if (morphlineId != null && morphlineId.length() == 0) {
        morphlineId = null;
      }
    }else{
      throw new MorphlineCompilationException(
            "not a valid morphline config : " + 
                    (config!=null?config.toString():""), null);

    }
    Config morphlineCfg = null;
    if (morphlineId == null) {
      morphlineCfg = morphlineConfigs.get(0);
      Preconditions.checkNotNull(morphlineCfg);
    } else {
      for (Config candidate : morphlineConfigs) {
        if (morphlineId.equals(new Configs().getString(candidate, "id", null))) {
          morphlineCfg = candidate;
          break;
        }
      }
      if (morphlineCfg == null) {
        throw new MorphlineCompilationException(
            "Morphline id '" + morphlineId + "' not found in morphline file: " + nameForErrorMsg, null);
      }
    }
    return morphlineCfg; 
  }

  /**
   * generic morphline processing
   * implements default morphlines notifications : beginTransaction, commitTransaction or RollBackTransaction
   * @param <IN>
   *   generic type of the input
   * @param input
   *   input to be processed by morphline conversion chain
   * @param feed
   *   feeder finfing the morphline conversion chain with the input data
   * @return 
   *   the result of the morphline processing, true is successful, false otherwise
   */
  private <IN> boolean feed(IN input, Feed<IN> feed) {
    //Notifications.notifyBeginTransaction(morphline);
    boolean success = false;
    try {
      success = feed.feed(input);
    } catch (Exception e) {
      logger.error("Exception raison in feed : "+e.getMessage(), e);
      morphlineContext.getExceptionHandler().handleException(e, null);
    } finally {
      //if (success) {
      //  Notifications.notifyCommitTransaction(morphline);
      //} else {
      //  Notifications.notifyRollbackTransaction(morphline);
      //}
    }
    return success;
  }

  public boolean feedLines(final int initialId, String[] lines) {
    return feed(lines, new Feed<String[]>() {
      public boolean feed(String[] input) throws Exception {
        int id = initialId;
        for (String input1 : input) {
          boolean success = processLine(id, input1);
          if (!success) {
            return false;
          }
          id++;
        }
        return true;
      }
    });
  }
      public boolean feedRecords(Record... records) {
        return feed(records, new Feed<Record[]>() {
            public boolean feed(Record[] input) throws Exception {
                for (Record i : input) {
                    //Notifications.notifyStartSession(morphline);
                    boolean success = morphline.process(i);
                    if (!success) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
  
  /**
   * process data belonging to a specific container
   * @param container
   *   the name or id of the converter
   * @param records
   *   a set of record describing the named container
   * @return 
   *   the result of the processing true if successfull
   */    
  public boolean feedRecords(String container, Record... records) {
    LogCollectionNamedFeed f = new LogCollectionNamedFeed(container, morphline);
    return feed(records, f);
  }

  private boolean processLine(int id, String line) {
    if(logger.isDebugEnabled())
      logger.debug("Processing line:" + line);
    Record record = new Record();
    record.put(Fields.TIMESTAMP, new Date().toString());
    record.put(Fields.MESSAGE, line);
    record.put(Fields.ID, id);
    Notifications.notifyStartSession(morphline);
    boolean success = morphline.process(record);
    if (!success) {
      if(logger.isDebugEnabled())
        logger.debug("ERROR : Morphline failed to process record: " + record);
    }
    return success;
  }

  public void shutdown() {
    Notifications.notifyShutdown(morphline);
  }
  
  public void notify(Record notification){
    if (logger.isDebugEnabled())
      logger.debug("DEBUG notify "+ this.getHarnessid());
    this.morphline.notify(notification);
  }

}
