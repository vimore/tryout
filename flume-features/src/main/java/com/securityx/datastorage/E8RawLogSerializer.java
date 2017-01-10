/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securityx.datastorage;

import com.e8security.datastorage.proto.E8MetaData.FlumeEventMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase.AsyncHbaseEventSerializer;
import org.hbase.async.AtomicIncrementRequest;
import org.hbase.async.PutRequest;

/**
 * Rawlog event serializer allowing the storage of the entire body and the uuid
 * in hbase raw table
 *
 * @author jyria <jean-yves@e8security.com>
 */
public class E8RawLogSerializer implements AsyncHbaseEventSerializer {

  private byte[] table;
  private byte[] colFam;
  private Event currentEvent;

  private final List<PutRequest> puts = new ArrayList<PutRequest>();
  private final List<AtomicIncrementRequest> incs = new ArrayList<AtomicIncrementRequest>();
  private byte[] currentRowKey;
  private byte[] RowKeyField;
  private final byte[] eventCountCol = "eventCount".getBytes();
  private byte[] rawlogColumn;
  private String rowKeyFieldStr;
  private byte[] logSrcColumn;
  private byte[] timestampColumn;
  private E8ProtoBufMetaData metadataFactory;

  public static final String EVENT_CATEGORY_FILE = "file";
  public static final String EVENT_CATEGORY_PACKET = "packet";
  public static final String EVENT_SYSLOG_FACILITY = "facility";
  public static final String EVENT_SYSLOG_SEVERITY = "severity";
  public static final String EVENT_SYSLOG_HOST = "host";
  public static final String EVENT_HOSTNAME = "hostname";
  public static final String EVENT_CATEGORY_SYSLOG = "syslog";
  public static final String EVENT_CATEGORY = "category";
  public static final String EVENT_TIMESTAMP = "timestamp";
  private byte[] metaDataColumn;

  @Override
  public void initialize(byte[] table, byte[] cf) {
    this.table = table;
    this.colFam = cf;
    this.metadataFactory = new E8ProtoBufMetaData();
  }

  @Override
  public void setEvent(Event event) {
    // Set the event and verify that the rowKey is  present
    this.currentEvent = event;
    String rowKeyStr = currentEvent.getHeaders().get(rowKeyFieldStr);
    if (rowKeyStr == null) {
      throw new FlumeException("No row key found in headers!");
    }
    currentRowKey = rowKeyStr.getBytes();
  }

  private byte[] genMeta() {
    FlumeEventMeta meta;
    byte[] out = null;
    Map<String, String> header = currentEvent.getHeaders();
    if (header.containsKey(EVENT_CATEGORY)) {
      String category = header.get(EVENT_CATEGORY);
      if (category.equals(EVENT_CATEGORY_SYSLOG)) {
        meta = this.metadataFactory.buildSyslogMetaData(
                header.get(EVENT_HOSTNAME),
                header.get(EVENT_SYSLOG_HOST),
                header.get(EVENT_SYSLOG_SEVERITY),
                header.get(EVENT_SYSLOG_FACILITY));
        return meta.toByteArray();

      } else if (header.containsKey(EVENT_CATEGORY_PACKET)) {
        meta = this.metadataFactory.buildPacketMeta(header.get(EVENT_HOSTNAME));
        return meta.toByteArray();
      } else if (header.containsKey(EVENT_CATEGORY_FILE)) {
        meta = this.metadataFactory.buildFileMeta(
                header.get(EVENT_HOSTNAME),
                header.get(EVENT_CATEGORY_FILE));
        return meta.toByteArray();
      }
      return out;
    }
    return out;
  }

  @Override
  public List<PutRequest> getActions() {
    // Split the event body and get the values for the columns
    String eventStr = new String(currentEvent.getBody());
    String srcHost;
    Map<String, String> header = currentEvent.getHeaders();
    // select src as the closest 
    if (header.containsKey(EVENT_SYSLOG_HOST)) {
      srcHost = new String(header.get(EVENT_SYSLOG_HOST));
    } else {
      srcHost = new String(header.get(EVENT_HOSTNAME));
    }
    puts.clear();
    //Generate a PutRequest  to store the raw log 
    PutRequest req = new PutRequest(table, currentRowKey, colFam,
            rawlogColumn, currentEvent.getBody());
    puts.add(req);
    PutRequest req2 = new PutRequest(table, currentRowKey, colFam,
            logSrcColumn, srcHost.getBytes());
    puts.add(req2);
    PutRequest req3 = new PutRequest(table, currentRowKey, colFam,
            timestampColumn, header.get(EVENT_TIMESTAMP).getBytes());
    puts.add(req3);
    PutRequest req4 = new PutRequest(table, currentRowKey, colFam,
            metaDataColumn, genMeta());
    puts.add(req4);
    return puts;
  }

  @Override
  public List<AtomicIncrementRequest> getIncrements() {

    // do nothing at now
    //incs.clear();
    //Increment the number of events received
    //incs.add(new AtomicIncrementRequest(table, "totalEvents".getBytes(), colFam, eventCountCol));
    return incs;
  }

  @Override
  public void cleanUp() {
    table = null;
    colFam = null;
    currentEvent = null;
    rawlogColumn = null;
    currentRowKey = null;
  }

  @Override
  public void configure(Context context) {
    //Get the column names from the configuration
    rowKeyFieldStr = context.getString("rowkeyfield");
    this.RowKeyField = rowKeyFieldStr.getBytes();
    rawlogColumn = "l".getBytes();
    logSrcColumn = "s".getBytes();
    timestampColumn = "t".getBytes();
    metaDataColumn = "m".getBytes();
  }

  @Override
  public void configure(ComponentConfiguration conf) {
  }
}
