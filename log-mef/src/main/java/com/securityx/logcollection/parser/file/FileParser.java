package com.securityx.logcollection.parser.file;


import com.securityx.flume.log.avro.Event;
import com.securityx.logcollection.parser.avro.AvroParser;
import com.securityx.logcollection.parser.avro.ParsedOutput;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jyrialhon
 */
public class FileParser {
  
  private final AvroParser instance;
  private final String morphlineFile;
  private final String morphlineId;
  private  DataFileWriter<ParsedOutput> dataFileWriter;
  
  public FileParser() throws Exception{
    this.morphlineFile = "logcollection-parser-main.conf";
    this.morphlineId = "parsermain";
    this.dataFileWriter = null;
    instance = new AvroParser(morphlineFile, morphlineId, SupportedFormats.WebProxyMef);
  }

  private Event buildAvroEvent(String rawlog){
    Event avroEvent = new Event();
    ByteBuffer buf = ByteBuffer.wrap(rawlog.getBytes());
    avroEvent.setBody(buf);
    Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
    headers.put("category", "syslog");
    headers.put("hostname", "somehost");
    headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
    avroEvent.setHeaders(headers);
    return avroEvent;
  }
  private List<Map<String, List<Object>>> parseLine(Event avroEvent) throws IOException{
    List<Map<String, List<Object>>>out = instance.parse(avroEvent);
    return out;
  }
  
  public void parse(File f, File out) throws FileNotFoundException, IOException{
    BufferedReader br = new BufferedReader(new FileReader(f));
    BufferedWriter bw = new BufferedWriter(new FileWriter(out));
    this.initAvroOut(out);
    String line;
    int curline=0;
    try {
      while ((line = br.readLine()) != null) {
        curline++;
        // process the line.
        Event input = this.buildAvroEvent(line);
        List<Map<String, List<Object>>> parserOut = this.parseLine(input);
        if (null != parserOut){
          this.toAvro(input, parserOut);
        }else{
          System.out.println("null ouptut for : "+curline+" : "+line);
        }  
      }
    } catch (IOException ex) {
      Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
    } finally{
      try {
        br.close();
        this.dataFileWriter.close();
      } catch (IOException ex) {
        Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  private void toAvro(Event rawlog, List<Map<String, List<Object>>> parserOut) throws IOException{
    for (Map<String, List<Object>> record : parserOut) {
      Map<CharSequence, CharSequence> map = new HashMap<CharSequence, CharSequence>();
      for (Map.Entry<String, List<Object>> e : record.entrySet()) {
        List<Object> vo = e.getValue();
        String v = "";
        if (vo != null && !vo.isEmpty())
          v = vo.get(0).toString();
        map.put((CharSequence) e.getKey(), (CharSequence) v);
      }

      if (map.containsKey("parserOutFormat")) {
        String parserOutFormat = (String) map.get("parserOutFormat");
        //output avro record
        ParsedOutput poAvro = new ParsedOutput(rawlog.toString(), parserOutFormat, map);
        this.dataFileWriter.append(poAvro);
      }
    }
  }
  private void initAvroOut(File out) throws IOException{
    ParsedOutput output = new ParsedOutput();
    DatumWriter<ParsedOutput> userDatumWriter = new SpecificDatumWriter<ParsedOutput>(ParsedOutput.class);
    this.dataFileWriter = new DataFileWriter<ParsedOutput>(userDatumWriter);
    dataFileWriter.create(output.getSchema(), out);
  }
  public static String usage(){
    StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
    StackTraceElement main = stack[stack.length - 1];
    String mainClass = main.getClassName ();
    return mainClass.toString()+" <input file> <output file>";
  }
  
  public static void main (String[] args) throws Exception{
    FileParser parser = new FileParser();
    if (args.length <2){
      System.out.println("invalid arguments ! ");
      System.out.println(usage());
      System.exit(254);
    }else{
      String input = args[0];
      String output = args[1];
      File f = new File(input);
      if (! f.exists() || ! f.canRead()){
        System.out.println("unable to read file : "+f.getAbsolutePath());
        System.exit(253);
      }
      File out = new File(output);
      if (!(out.exists() &&  out.canWrite())|| out.createNewFile()){
        System.out.println("unable to write file : "+out.getAbsolutePath());
        System.exit(252);
      }else{
        parser.parse(f, out);
      }
    }
  }
  
}
