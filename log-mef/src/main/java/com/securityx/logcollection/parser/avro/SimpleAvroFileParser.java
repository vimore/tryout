/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.logcollection.parser.avro;

import com.securityx.flume.log.avro.Event;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jyrialhon
 */
public class SimpleAvroFileParser {
  private AvroParser parser;
  private String fileName;
  private DataFileReader<Event> dataFileReader;
  
public SimpleAvroFileParser(AvroParser parser, String fileName) throws IOException{
  this.parser = parser;
  this.fileName = fileName;
  DatumReader<Event> userDatumReader = new SpecificDatumReader<Event>(Event.class);
  this.dataFileReader = new DataFileReader<Event>(new File(fileName), userDatumReader);
}
  
public void Parse() throws IOException{
  Event event=null;
  while (this.dataFileReader.hasNext()) {
    event = dataFileReader.next(event);
    System.out.println(event);
    List<Map<String, List<Object>>> out = this.parser.parse(event);
    System.out.println(out);
  }
}
public static void main(String[] args) throws Exception{
  String morphlineConf = "logcollection-parser-main.conf";
  String morphlineConfId = "parsermain";
  morphlineConf = "iamdbmef-ms-ad.conf";
  morphlineConfId =  "msad-csvde_parser";
  String file = "/Volumes/c$/tmp/tst.csv";
  AvroParser parser =  AvroParser.BuildParser(morphlineConf, morphlineConfId);
  SimpleAvroFileParser spf = new SimpleAvroFileParser(parser, file);
  spf.Parse();
  
}
}



