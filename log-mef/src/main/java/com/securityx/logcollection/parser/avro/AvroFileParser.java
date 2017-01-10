
package com.securityx.logcollection.parser.avro;

import com.securityx.model.mef.field.api.SupportedFormats;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.kitesdk.morphline.api.Record;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * is dedicated to avro logs container. it extracts avro records from the avro 
 * container running a morphline script. Once extracted, each record can be 
 * processed by another morphline script for data processing.
 *
 * @author jyrialhon
 */
public class AvroFileParser extends AvroParser {


  /**
   * call parent constructor
   * @throws Exception 
   */
  public AvroFileParser() throws Exception {
    super("logcollectionavro", "logcollection-avro.conf", SupportedFormats.WebProxyMef);
  }

  /**
   * call parent constructor
   * @param morphlineConfFile
   *   string describing the morphline configuration file
   * @param morphlineId
   *   id of the morphline script ( from the morphline configuration file) to use
   * @throws Exception 
   */
  public AvroFileParser(String morphlineConfFile, String morphlineId) throws Exception {
   super(morphlineId, morphlineConfFile, SupportedFormats.WebProxyMef);
  }

  /**
   * call parent constructor 
   * @param morphlineFile
   *    name of the file (expected to be located in resources morphline/*)
   * @param morphlineId
   * @param verbose
   * @throws Exception 
   */
  public AvroFileParser(String morphlineFile, String morphlineId,  List<SupportedFormats> expectedFormats) throws Exception {
    super(morphlineFile, morphlineId, expectedFormats, 3);
  }

  /**
   * extracts avro record from aaavro container File
   * @param f
   *   avro container File
   * @return 
   *   a set of morphline record describing the avro records.
   * @throws IOException 
   */
  public Record[] readOnly(File f) throws IOException {
    return readOnlyAvroContainerAsInputStream(f.getAbsolutePath(), new FileInputStream(f));
  }

  /**
   * Exctract avro record data from an avro container File and run the conversion morphline script on it
   * @param f
   *   avro container File
   * @param outFile
   * @throws IOException 
   */
  public void readAndParse(File f, File outFile) throws IOException {
      Record in[] = this.readOnly(f);
      Record[] results = super.parseOnly(in);
      dump(results, outFile);
  }
  
  private void dump(Record [] results , File outFile) throws IOException{
    for (SupportedFormats format : SupportedFormats.values()){
      toFile(results, new File(outFile.getParentFile(), format.name()+outFile.getName()));
    }
  }
  
  private void toFile(Record[] out, File outFile) throws IOException{
    if(out!=null && out.length >0){
       if (logger.isDebugEnabled())
         logger.debug ("INFO : saving to ");
      FileOutputStream streamer =  new FileOutputStream(outFile);
      for (Record r : out){
           streamer.write(r.toString().getBytes());
      }
      
      streamer.close();
    }else{
      if (logger.isDebugEnabled())
         logger.debug("ERROR : no result to dump to "+outFile.toString());
    }
  }

  /**
   * main documentation
   * @return 
   */
  public static String usage() {
    return "AvroFileParser <confFile> <sourceFile> <outFile>\n"
            + "\tconfFile : jsonFile containing parser config : \n"
            + "\t\tparserConfig : {\n"
            + "\t\tmorphlineScript : \"../../model/mef/src/main/morphline/logcollection-bluecoat.conf\",\n"
            + "\t\tmorphlineId : \"bluecoat\",\n"
            + "\t\t}\n"
            + "\tsourceFile : avro source file\n"
            + "\tdestFile : avro output file\n";
  }

  /**
   * command line expecting 
   *    a typesafe config file describing conversion chain
   *    path to an avro container 
   *    path to an output file will be provided to 
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception  {
    File cwd = new File(".");
    System.out.println(cwd.getAbsolutePath());

    if (args.length < 3) {
      System.out.println(AvroFileParser.usage());
    } else {
      String confFile = args[0];
      String srcFile = args[1];
      String destFile = args[2];
      File configFile = new File(confFile);
      if (!configFile.exists()) {
        System.out.println("config file '" + confFile + "' not found");
        System.out.println(AvroFileParser.usage());
        System.exit(1);
      }
      Config c = ConfigFactory.parseFile(configFile);
      AvroFileParser reader = AvroFileParser.build(c);
      File src = new File(srcFile);
      File dest = new File(destFile);
      if (!src.canRead()) {
        System.out.println("unable to read src file : '" + srcFile + "'");
        System.exit(1);
      }
      if (dest.exists() && !dest.canWrite()) {
        System.out.println("unable to write to dest file " + destFile + "' either already exist or not writable");
      }
      reader.readAndParse(src, dest);
    }
  }
  public static AvroFileParser build(String configFile) throws Exception{
          Config c = ConfigFactory.parseFile(new File(configFile));
          return build(c);
  }
  public static AvroFileParser build(Config c) throws Exception{
    ArrayList supportedFormats = new ArrayList();
        supportedFormats.add(SupportedFormats.WebProxyMef);
        supportedFormats.add(SupportedFormats.FlowMef);
        supportedFormats.add(SupportedFormats.DnsMef);
        supportedFormats.add(SupportedFormats.IAMMef);
        supportedFormats.add(SupportedFormats.HETMef);
      Config parserConfig = c.getConfig("parserConfig");
      String morphlineFileStr = parserConfig.getString("morphlineScript");
      String morphlineId = parserConfig.getString("morphlineId");
      String verboseStr;
      boolean verbose = false;
      if (parserConfig.hasPath("verbose")) {
        verboseStr = parserConfig.getString("verbose");
        verbose = "true".equalsIgnoreCase(verboseStr);
      }
      return new AvroFileParser(morphlineFileStr, morphlineId, supportedFormats);
  }
}
