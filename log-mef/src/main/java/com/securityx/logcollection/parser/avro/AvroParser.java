package com.securityx.logcollection.parser.avro;

import com.securityx.logcollection.utils.MefRecordToOutFormat;
import com.securityx.logcollection.utils.UnMatchedRecordToOutFormat;
import com.securityx.logcollection.utils.UnMatchedTrackingType;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import com.securityx.flume.log.avro.Event;
import com.securityx.logcollection.parser.configchecker.ConfigChecker;
import com.securityx.logcollection.parser.morphline.MorphlineParser;
import com.securityx.logcollection.parser.morphline.MorphlineTransformer;
import com.securityx.logcollection.utils.RecordToOutFormat;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.apache.commons.io.IOUtils;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * AvroParser is dedicated to avro logs container. it extracts avro records from
 * the avro container running a morphline script. Once extracted, each record
 * can be processed by another morphline script for data processing
 *
 * @author jyrialhon
 */
public class AvroParser extends LogParser {

  private final UnMatchedRecordToOutFormat unMatchedFieldsSelector;
  protected Logger logger = LoggerFactory.getLogger(AvroFileParser.class);
  /**
   * the transformer runs a morphline script to extract data from avro container
   * to Record[]
   */
  protected MorphlineTransformer avroContainerParser;

  /**
   * the transformer runs a morphline script to extract data from a json avro
   * record (as inputStream) to Record
   */
  protected MorphlineTransformer jsonAvroRecordParser;

  /**
   * the parser runs a morphline convertion chain on record extracted by
   * Transformer
   */
  protected MorphlineParser eventParser;
  /**
   * fields selector to get only fields of the defined SupportedFormat
   */
  //private RecordToOutFormat recordFieldSelector;
  private Map<String, MefRecordToOutFormat> recordFieldsSelectors;
  
  //private SupportedFormats expectedFormat;
  private List<SupportedFormats> expectedFormats;

  private List<String> supportedFormats = genSupportedFormat();
  private Record in;
  private final EventToRecordConverter eventToRecordConverter;

  private List<String> genSupportedFormat() {
    List<String> out = new ArrayList<String>();
    for (SupportedFormats f:  SupportedFormats.values()){
       out.add(f.name());
    }
    return out;
  }

  /**
   * avro converter read only constructor, at now use a staticaly defined
   * default output format : SupportedFormats.LogMef script :
   * logcollection-avro.conf
   *
   * @throws java.lang.Exception
   */
  public AvroParser() throws Exception {
    this.eventToRecordConverter = new EventToRecordConverter(3);
    this.expectedFormats = new ArrayList(1);
    this.expectedFormats.add(SupportedFormats.WebProxyMef);
    
    this.avroContainerParser = new MorphlineTransformer("logcollectionavro", "logcollection-avro.conf");
    this.jsonAvroRecordParser = new MorphlineTransformer("jsonavrotomorphline", "logcollection-avro.conf");
    this.recordFieldsSelectors  = new HashMap<String, MefRecordToOutFormat>();
    this.recordFieldsSelectors.put(this.expectedFormats.get(0).name(), new MefRecordToOutFormat(this.expectedFormats.get(0)));
    this.unMatchedFieldsSelector = new UnMatchedRecordToOutFormat(UnMatchedTrackingType.KEEP_ALL);
  }

  /**
   * avro converter read and parse constructor : requires a morphline
   * configuration file and a morphline Id this morphline script is used to
   * convert avro records once avro container has been parsed
   *
   * @param morphlineConfFile the file as a string.
   * @param morphlineId the id of the the morphline script to use ( within the
   * morphlineConfFile )
   * @param expectedFormat the output keyset to be returned
   * @throws java.lang.Exception
   */
  public AvroParser(String morphlineConfFile, String morphlineId, SupportedFormats expectedFormat) throws Exception {
    this.eventToRecordConverter = new EventToRecordConverter(3);
    this.expectedFormats = new ArrayList(1);
    this.expectedFormats.add(expectedFormat);
    this.avroContainerParser = new MorphlineTransformer("logcollectionavro", "logcollection-avro.conf");
    this.jsonAvroRecordParser = new MorphlineTransformer("jsonavrotomorphline", "logcollection-avro.conf");
    this.eventParser = new MorphlineParser(morphlineId, morphlineConfFile, true);
    this.recordFieldsSelectors  = new HashMap<String, MefRecordToOutFormat>();
    this.recordFieldsSelectors.put(expectedFormat.name(), new MefRecordToOutFormat(expectedFormat));
    this.unMatchedFieldsSelector = new UnMatchedRecordToOutFormat(UnMatchedTrackingType.KEEP_ALL);
  }
  
  /**
   * avro converter read and parse constructor : requires a morphline
   * configuration file and a morphline Id this morphline script is used to
   * convert avro records once avro container has been parsed
   * @param morphlineConfFile
   * @param morphlineId
   * @param expectedFormats
   * @throws Exception
   */
  public AvroParser(String morphlineConfFile, String morphlineId, List<SupportedFormats> expectedFormats, int nbRegion) throws Exception {
    this.eventToRecordConverter = new EventToRecordConverter(nbRegion);
    this.expectedFormats = new ArrayList(1);
    //this.avroContainerParser = new MorphlineTransformer("logcollectionavro", "logcollection-avro.conf");
    //this.jsonAvroRecordParser = new MorphlineTransformer("jsonavrotomorphline", "logcollection-avro.conf");
    ConfigChecker.checkConfig();
    this.eventParser = new MorphlineParser(morphlineId, morphlineConfFile, true);
    this.recordFieldsSelectors  = new HashMap<String, MefRecordToOutFormat>();
    for (SupportedFormats expectedFormat : expectedFormats){
      this.expectedFormats.add(expectedFormat);
      this.recordFieldsSelectors.put(expectedFormat.name(), new MefRecordToOutFormat(expectedFormat));

    }
    this.unMatchedFieldsSelector = new UnMatchedRecordToOutFormat(UnMatchedTrackingType.KEEP_ALL);
    
  }

  /**
   * avro converter read and parse constructor taking eventParser configuration
   * from args. the morphline script is used to convert avro records once avro
   * container has been parsed.
   *
   * @param morphlineFile the resource name of morphline configuration script
   * withing morphline/ resources dir
   * @param morphlineId the Id of the script to be executed within the morphline
   * "file"
   * @param verbose
   *
   * @throws java.lang.Exception
   */
  public AvroParser(String morphlineFile, String morphlineId, boolean verbose) throws Exception {
    this.eventToRecordConverter = new EventToRecordConverter(3);
    this.avroContainerParser = new MorphlineTransformer("logcollectionavro", "logcollection-avro.conf");
    this.eventParser = new MorphlineParser(morphlineId, morphlineFile, verbose);
    this.unMatchedFieldsSelector = new UnMatchedRecordToOutFormat(UnMatchedTrackingType.KEEP_ALL);
  }

  /**
   * return the conversion chain object
   *
   * @return the morphline parser of the conversion chain
   */
  public MorphlineParser getEventParser() {
    return eventParser;
  }

  /**
   * convert an avro container into a set of morphline record using a morphline
   * script takes a container string identifying data origin and an InputStream
   * of the avro container data.
   *
   * @param container a string description of the origin of input stream
   * @param in InputStream
   * @return an array containing morphline records describing each avro record
   * of the container
   * @throws java.io.IOException
   */
  protected Record[] readOnlyAvroContainerAsInputStream(String container, InputStream in) throws IOException {
    Record event = new Record();
    try {
      byte[] bytes = IOUtils.toByteArray(in);
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      event.put(Fields.ATTACHMENT_BODY, bais);
      return readOnlyAvroContainerAsRecord(container, event);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * convert a json formated avro record (as inputstream ) to a morphline record
   *
   * @param id some identifier
   * @param in avro record as InputStream
   * @return
   * @throws IOException
   */
  protected Record readOnlyJsonAvroRecordAsInputStream(String id, InputStream in) throws IOException {
    Record event = new Record();
    try {
      byte[] bytes = IOUtils.toByteArray(in);
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      event.put(Fields.ATTACHMENT_BODY, bais);
      return readOnlyJsonAvroRecordAsRecord(id, event);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * extract data from a Morphline record containing a avro record (in json
   * format) into
   *
   * @param id
   * @param event
   * @return a morphline record ready to be injected into MorphlineParser for
   * device driven data extraction
   */
  private Record readOnlyJsonAvroRecordAsRecord(String id, Record event) {
    if (this.jsonAvroRecordParser.feedRecords(id, event)) {
      //Success
      Record[] out = this.jsonAvroRecordParser.GetRecords();
      if (out.length > 0) {
        return out[0];
      } else {
        return null;
      }

    } else {
      //Failure
      if (logger.isDebugEnabled()) {
        logger.debug("DEBUG : Failed to parse avro record  : " + id);
      }
      return null;
    }

  }

  private Record readOnlyAvroRecordToBeObsoleted(Event avroEvent) {
    Record out = new Record();
    //process header data
    Map<CharSequence, CharSequence> header = avroEvent.getHeaders();
    String category = null;
    String host=null;
    String hostname=null;
    for (Map.Entry<CharSequence, CharSequence> att : header.entrySet()){
      if (att.getKey().toString().equals("category")){
        out.put(WebProxyMefField.logCollectionCategory.getPrettyName(), att.getValue().toString());
        category = att.getValue().toString();
        continue;
      }
      if (att.getKey().toString().equals("timestamp")){
        out.put(WebProxyMefField.logCollectionTime.getPrettyName(), att.getValue().toString());
        continue;
      }
      if (att.getKey().toString().equals("host")){
        host = att.getValue().toString();
        continue;
      }
      if (att.getKey().toString().equals("hostname")){
        hostname =  att.getValue().toString();
        continue;
      }
    }
    
    if (host != null){
      out.put(WebProxyMefField.logCollectionHost.getPrettyName(), host);
    }else{
      out.put(WebProxyMefField.logCollectionHost.getPrettyName(), hostname);
    }

    //process body 
    // for packet : we need to provide data within the expected format 
    if ("packet".equals(category)){
        out.put(Fields.ATTACHMENT_BODY, new ByteArrayInputStream(avroEvent.getBody().array()));// = "_attachment_body";
        out.put(Fields.ATTACHMENT_MIME_TYPE, "application/json"); // = "_attachment_mimetype";
    }else{ //default to log format with log in "message" field.
      out.put(WebProxyMefField.message.getPrettyName(), new String(avroEvent.getBody().array()));
    }   
    return out;
  }

  /**
   * takes a morphline record as input and try to extract avro record from it.
   * the avro container is stored in Record field Fields.ATTACHMENT_BODY as
   * ByteArrayInputStream
   *
   * @param container a string identifier for the container
   * @param event a morphline record containing
   * @return
   */
  private Record[] readOnlyAvroContainerAsRecord(String container, Record event) {
    if (this.avroContainerParser.feedRecords(container, event)) {
      //Success
      Record[] out = this.avroContainerParser.GetRecords();
      return out;
    } else {
      //Failure
      if (logger.isDebugEnabled()) {
        logger.debug("ERROR : Failed to parse avro container : " + container);
      }
      return null;
    }

  }

  /**
   * logging utils for statistics, provide file size and number of record
   * generated
   *
   * @param size
   * @param out
   */
  protected void inputStat(long size, Record[] out) {
    if (logger.isDebugEnabled()) {
      logger.debug("INFO : inputStat : parsed " + size + " bytes containing " + out.length + " record(s)");
    }
  }

  /**
   * run the conversion chain again a set of morphline record ( assuming it
   * contains data the morphline script is able to handle
   *
   * @param in
   * @return
   * @throws IOException
   */
  public List<Record> parseOnly(Record in) throws IOException {
    if (this.eventParser != null) {
      if (!this.eventParser.feedRecords(in)) {
        if (logger.isDebugEnabled()) {
          logger.debug("ERROR : issue parsing encoured");
        }
        this.eventParser.getStreamCommand().clear();
        return null;
      } else {

        if (this.eventParser.getStreamCommand().getNumRecords() >0){
          List<Record> out = new ArrayList<Record>(Arrays.asList(this.eventParser.getStreamCommand().flushRecords()));
          return out;
        }else 
          return null;
      }
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("INFO : no parser found : dumping input record to stdin");
      }
    }
    return null;
  }

  public Record[] parseOnly(Record[] in) {
    if (this.eventParser != null) {
      if (!this.eventParser.feedRecords(in)) {
        if (logger.isDebugEnabled()) {
          logger.debug("ERROR : issue parsing encoured");
        }
        return null;
      } else {
        return this.eventParser.getStreamCommand().flushRecords(); //assuming 1 in, 1 out
      }
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("INFO : no parser found : dumping input record to stdin");
      }
    }
    return null;

  }

  public Record[] parseAvroContainer(InputStream in) throws IOException {
    return this.parseOnly(this.readOnlyAvroContainerAsInputStream(null, in));
  }

  public List<Record> parseAvroRecord(InputStream in) throws IOException {
    return this.parseOnly(this.readOnlyJsonAvroRecordAsInputStream(null, in));
  }

  public void shutdown() {
    if (this.avroContainerParser != null) {
      this.avroContainerParser.shutdown();
    }

    if (this.eventParser != null) {
      this.eventParser.shutdown();
    }

  }

  public List<Map<String, List<Object>>> parse(Event avroEvent) throws IOException {
    // convert avro to a morphline record
    in = eventToRecordConverter.readOnlyAvroRecord(avroEvent);
    List<Record> out;
    // parse the data
    out = parseOnly(in);
    // check record log source type.
    if (null != out){
      return toExpectedFormat(out);
    }else{
      return null;
    }
    
  }
  public String getLastEventLogCollectionHost(){
    String hostnameFromLastEvent = (String) this.in.get(WebProxyMefField.logCollectionHost.getPrettyName()).get(0);
    if (null ==  hostnameFromLastEvent)
      return "_undefined_host";
    else
      return hostnameFromLastEvent;

  }

  public static InputStream fileToStream(File f) throws FileNotFoundException {
    InputStream in = new FileInputStream(f);
    return in;
  }

  public static void outputStreamToFile(ByteArrayOutputStream out, File dest) throws FileNotFoundException, IOException {
    FileOutputStream ous = new FileOutputStream(dest);
    ous.write(out.toByteArray());
  }

  /**
   * main documentation
   *
   * @return
   */
  public static String usage() {
    return "AvroParser <expectedFormat> <morphlineFile> <morphlineId> <sourceFile> <outFile>\n"
      + "\texpectedFormat : one of the SupportedFormats (defines keyset) \n"
      + "\tmorphlineFile : a resource containing morphline scripts \n"
      + "\tmorphlineId : morphline script id to be executed \n"
      + "\tsourceFile : avro source file\n"
      + "\tdestFile : avro output file pattern (final file will be prefixed by format\n";
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 5) {
      System.out.println(AvroParser.usage());
      System.exit(1);
    } else {
      String expectedFormat = args[0];
      String morphlineFile = args[1];
      String morphlineId = args[2];
      String inFileStr = args[3];
      String outFileStr = args[4];
      SupportedFormats format = SupportedFormats.valueOf(expectedFormat);
      File inFile = new File(inFileStr);
      assert (inFile.isFile() && inFile.canRead());
      File outFile = new File(outFileStr);
      assert (outFile.isFile() && inFile.canWrite());
      //init parse
      AvroParser parser = new AvroParser(morphlineFile, morphlineId, format);

      //read infile
      InputStream in = AvroParser.fileToStream(inFile);
      // parse file
      Record[] out = parser.parseAvroContainer(in);
      //get result
      System.out.println("results");
      for (Record r : out) {
        System.out.println(r.toString());
      }

    }

  }

  /**
   * convert a morphline record into a generic format
   *
   * @param out a morphline record
   * @return Map<String, List<Object>>
   */
  private Map<String, List<Object>> toMap(Record out) {
    // check whether format is the expected one or not : 
    Map<String, List<Object>> outMap = new HashMap<String, List<Object>>();
    for (String key : out.getFields().keySet()) {
      List outValues = new ArrayList(out.get(key));
      outMap.put(key, outValues);
    }
    return outMap;
  }

  private List<Map<String, List<Object>>> toExpectedFormat(List<Record> out) {
    // first a filter on device we are allowing to flow out 
    // implemented because of scope change from Mef to WebProxyMef
    boolean isUnMatched= false;
    List<Map<String,List<Object>>> output = new ArrayList<Map<String, List<Object>>>();
    for (Record r : out) {
      boolean found =false;
      if (isSupported(r)) {
        // processing output for a supported device/format
        SupportedFormats logSrcFormat = SupportedFormats.WebProxyMef; // default format
        // get record logSourceType
        if (r.getFields().containsKey(WebProxyMefField.logSourceType.getPrettyName())) {
          String logSrcType = (String) r.getFirstValue(WebProxyMefField.logSourceType.getPrettyName());
          // does the source type march a supported format
            try {
              // get the format
              logSrcFormat = SupportedFormats.valueOf(logSrcType);
            } catch (IllegalArgumentException e) {
              // defaulting to WebProxyMef
              logSrcFormat = SupportedFormats.WebProxyMef;
            }
        }

          for (SupportedFormats format : this.expectedFormats) {
            if (format == logSrcFormat) {
              Record cleaned = this.recordFieldsSelectors.get(format.name()).keepFormat(r);
              output.add(toMap(cleaned));
              found = true;
              break;
            }
          }
          if (found) {
            continue;
          }
          if (logger.isDebugEnabled()) {
            logger.debug("missing " + WebProxyMefField.logSourceType.getPrettyName() + " : " + logSrcFormat);
          }
          if (this.recordFieldsSelectors.containsKey(SupportedFormats.WebProxyMef.name())) {
            Record cleaned = this.recordFieldsSelectors.get(SupportedFormats.WebProxyMef.name()).keepFormat(r);
            output.add(toMap(cleaned));
          } else {
            if (logger.isDebugEnabled()) {
              logger.debug("default format is missing " + WebProxyMefField.logSourceType.getPrettyName());
            }
          }
      }else{
        output.add(toMap(this.unMatchedFieldsSelector.keepFormat(r)));
      }
    }
    return output;
  }

  private boolean isSupported(Record out) {
    String logSourceType = (String) out.getFirstValue(WebProxyMefField.logSourceType.getPrettyName());
    if (this.supportedFormats.contains(logSourceType)){
      logger.debug("INFO is supported outputFormat : "+ logSourceType);
      return true;
    } else if ("BlueCoat".equals(logSourceType) ||
        "Squid".equals(logSourceType) ||
        "WebProxyMef".equals(logSourceType)||
        "FlowMef".equals(logSourceType) ||
        "DnsMef".equals(logSourceType) || 
        "IAMMef".equals(logSourceType) ||
        "HETMef".equals(logSourceType) ||
        "IAMDBMef".equals(logSourceType) ||
        "CertMef".equals(logSourceType)) {
      logger.debug("INFO is supported outputFormat : "+ logSourceType);
      return true;
    } else {
      logger.debug("INFO invalid logSourceType : "+logSourceType );
      return false;
    }
  }

  public boolean isParseable(com.securityx.flume.log.avro.Event avroEvent) {
	  return true;
  }
  
  public static AvroParser BuildParser(String morphlineConf, String morphlineConfId ) throws Exception{
	  return new AvroParser(morphlineConf, morphlineConfId, SupportedFormats.genSupportedFormatList(), 3);
  }

  public static AvroParser BuildParser(String morphlineConf, String morphlineConfId, SupportedFormats format) throws Exception{
    ArrayList<SupportedFormats> in = new ArrayList<SupportedFormats>();
    in.add(format);
    return new AvroParser(morphlineConf, morphlineConfId, in, 3);
  }
  public static AvroParser BuildParser(String morphlineConf, String morphlineConfId, List<SupportedFormats> formats) throws Exception{
    return new AvroParser(morphlineConf, morphlineConfId, formats, 3);
  }


}
