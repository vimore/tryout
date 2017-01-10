/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.securityx.datastorage;
import com.e8security.datastorage.proto.E8MetaData;
import com.e8security.datastorage.proto.E8MetaData.FlumeEventMeta;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ExtensionRegistry;


/**
 *
 * @author jyria <jean-yves@e8security.com>
 */
public class E8ProtoBufMetaData {
  
static <Type> FlumeEventMeta wrap(E8MetaData.FlumeEventMeta.Type type, GeneratedMessage.GeneratedExtension ext, Type cmd, String hostname){
  return FlumeEventMeta.newBuilder().setType(type).setExtension(ext, cmd).setHostname(hostname).build();
}
  
  public FlumeEventMeta buildSyslogMetaData(String hostname, String host, String severity, String facility){
    E8MetaData.SyslogMeta meta = E8MetaData.SyslogMeta.newBuilder().setHost(host)
            .setFacility(facility)
            .setSeverity(severity).build();
    return wrap(E8MetaData.FlumeEventMeta.Type.syslog, E8MetaData.SyslogMeta.flumeEventMeta, meta, hostname);
  }
  
  public FlumeEventMeta buildPacketMeta(String hostname){
    E8MetaData.PacketMeta meta = E8MetaData.PacketMeta.newBuilder().build();
    return wrap(FlumeEventMeta.Type.packet, E8MetaData.PacketMeta.flumeEventMeta, meta, hostname);
  }
  
  public FlumeEventMeta buildFileMeta(String hostname,String file){
    E8MetaData.FileMeta meta;
    if (null == file)
      meta = E8MetaData.FileMeta.newBuilder().build();
    else
      meta = E8MetaData.FileMeta.newBuilder().setFile(file).build();
    return wrap(FlumeEventMeta.Type.file, E8MetaData.FileMeta.flumeEventMeta, meta, hostname);
  }
 
  
}
