/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.logcollection.parser.avro;

import com.securityx.flume.log.avro.Event;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jyrialhon
 */
public class SimpleTextFileParser {

  private AvroParser parser;
  private String fileName;

  public SimpleTextFileParser(AvroParser parser, String fileName) throws IOException {
    this.parser = parser;
    this.fileName = fileName;
    DatumReader<Event> userDatumReader = new SpecificDatumReader<Event>(Event.class);
  }

  public void Parse() throws IOException {
    Event event = null;
    BufferedReader reader = new BufferedReader(new FileReader(this.fileName));
    String line;
    event = new Event();
    Map<CharSequence, CharSequence> header = new HashMap<CharSequence, CharSequence>();
    event.setHeaders(header);
    while ((line = reader.readLine()) != null) {
      event.setBody(ByteBuffer.wrap(line.getBytes()));
      System.out.println(event);
      List<Map<String, List<Object>>> out = this.parser.parse(event);
      System.out.println(out);
    }
  }

  public static void main(String[] args) throws Exception {
    String morphlineConf = "logcollection-parser-main.conf";
    String morphlineConfId = "parsermain";
    morphlineConf = "iamdbmef-ms-ad.conf";
    morphlineConfId = "msad-csvde_parser";
    String file = "/Volumes/c$/tmp/tst.csv";
    AvroParser parser = AvroParser.BuildParser(morphlineConf, morphlineConfId);
    SimpleTextFileParser spf = new SimpleTextFileParser(parser, file);
    spf.Parse();

  }
}
