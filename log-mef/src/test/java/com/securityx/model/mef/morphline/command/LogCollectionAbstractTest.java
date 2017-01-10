/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.model.mef.morphline.command;

import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.securityx.utils.OutUtils;
import com.typesafe.config.Config;
import junit.framework.TestCase;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import java.io.*;

/**
 *
 * @author jyrialhon
 */
public abstract class LogCollectionAbstractTest extends TestCase {

  protected MorphlineContext morphlineContext;
  protected MorphlineHarness morphlineHarness;
  public String morphlineId;
  public String confFile;
  protected AssertRecordOutCommand outCommand;
  private MorphlineResourceLoader configLoader;
  protected boolean verboseOutputCommand = Boolean.valueOf(System.getProperty("logMefEnableVerboseOutput"));

  public LogCollectionAbstractTest(String name) {
    super(name);
    this.configLoader=new MorphlineResourceLoader();
    this.morphlineContext = new MorphlineContext.Builder().build();
  }

  protected boolean doTest(String line) {
    return doTest(new String[]{line});
  }

  protected boolean doTest(Record r) {
    return doTest(new Record[]{r});
  }

  protected boolean doTest(Record[] r) {
    boolean result = true;
    long t1 = System.nanoTime();
    int i = r.length;
    do {
      result &= this.morphlineHarness.feedRecords(r[r.length-i]);
      i--;
    } while (i >0);
      long t2 = System.nanoTime();
    int nbLines = r.length;

    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    String className = elements[2].getClassName();
    String functionName = elements[2].getMethodName();

    OutUtils.printOut("Test Lines : " + nbLines);
    OutUtils.printOut("Time elapsed for " + className + "." + functionName + ": " + (t2 - t1) / Math.pow(10, 6) + "ms");
    OutUtils.printOut("Average time elapsed perf injection " + className + "." + functionName + ": " + (t2 - t1) / (nbLines * Math.pow(10, 6)) + "ms");
    return result;
  }

  protected boolean doTest(String[] lines) {
    boolean result = false;
    long t1 = System.nanoTime();
    result = this.morphlineHarness.feedLines(1, lines);
    long t2 = System.nanoTime();
    int nbLines = lines.length;

    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    String className = elements[2].getClassName();
    String functionName = elements[2].getMethodName();

    OutUtils.printOut("Test Lines : " + nbLines);
    OutUtils.printOut("Time elapsed for " + className + "." + functionName + ": " + (t2 - t1) / Math.pow(10, 6) + "ms");
    OutUtils.printOut("Average time elapsed perf injection " + className + "." + functionName + ": " + (t2 - t1) / (nbLines * Math.pow(10, 6)) + "ms");
    return result;
  }

  protected void setUp() throws Exception {
    super.setUp();
    //this.morphlineContext = new MorphlineContext.Builder().build();
    this.outCommand = new AssertRecordOutCommand(morphlineContext, this.verboseOutputCommand);
    Config conf = MorphlineResourceLoader.getConfFile(confFile);
    assertTrue("conf file " + conf.origin(),conf.origin().toString().contains(confFile));
    this.morphlineHarness = new MorphlineHarness(morphlineContext, conf, morphlineId);
    this.morphlineHarness.startup(outCommand);

  }

  protected void tearDown() {
    this.morphlineHarness.shutdown();
    this.morphlineContext = null;
    this.outCommand = null;
  }

  protected String checkFile(String filename) throws FileNotFoundException, IOException {
    File f = new File(filename);
    assertTrue(f.exists());
    BufferedReader input = new BufferedReader(new FileReader(f));
    String last = null;
    String line;
    while ((line = input.readLine()) != null) {
      last = line;
    }
    return last;
  }

}
