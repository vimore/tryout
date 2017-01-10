package com.securityx.model.external.flow;

import com.securityx.model.external.ExternalFieldsToFlowMefFields;
import com.securityx.model.mef.field.api.FlowMefField;
import com.securityx.model.mef.field.api.GenericFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum NProbeToFlowMefMappings implements ExternalFieldsToFlowMefFields {

  IN_BYTES(FlowMefField.bytesIn),
  IN_PKTS(FlowMefField.packetsIn),
  FLOWS(FlowMefField.numberFlows),
  PROTOCOL(FlowMefField.transportProtocol),
  SRC_TOS(FlowMefField.sourceTos),
  TCP_FLAGS(FlowMefField.tcpFlags),
  L4_SRC_PORT(FlowMefField.sourcePort),
  IPV4_SRC_ADDR(FlowMefField.sourceAddress),
  L4_DST_PORT(FlowMefField.destinationPort),
  IPV4_DST_ADDR(FlowMefField.destinationAddress),
  IPV4_NEXT_HOP(FlowMefField.nextHopAddress),
  SRC_AS(FlowMefField.sourceAutonomousSystem),
  DST_AS(FlowMefField.destinationAutonomousSystem),
  FLOW_END_MILLISECONDS(FlowMefField.endTime),
  FLOW_START_MILLISECONDS(FlowMefField.startTime),
  OUT_BYTES(FlowMefField.bytesOut),
  OUT_PKTS(FlowMefField.packetsOut),
  ICMP_TYPE(FlowMefField.icmpType),
  SAMPLING_INTERVAL(FlowMefField.samplingInterval),
  SAMPLING_ALGORITHM(FlowMefField.samplingAlgorithm),
  ENGINE_TYPE(FlowMefField.engineType),
  ENGINE_ID(FlowMefField.engineId),
  TOTAL_BYTES_EXP(FlowMefField.totalBytesExp),
  TOTAL_PKTS_EXP(FlowMefField.totalPacketsExp),
  TOTAL_FLOWS_EXP(FlowMefField.totalFlowsExp),
  MIN_TTL(FlowMefField.minTTL),
  MAX_TTL(FlowMefField.maxTTL),
  IN_SRC_MAC(FlowMefField.sourceMacAddress),
  SRC_VLAN(FlowMefField.sourceVlan),
  DST_VLAN(FlowMefField.destinationVlan),
  DIRECTION(FlowMefField.direction),
  OUT_DST_MAC(FlowMefField.destinationMacAddress),
  IPV4_SRC_MASK(FlowMefField.sourceMask),
  IPV4_DST_MASK(FlowMefField.destinationMask);

  private static final Map<String, FlowMefField> mappings = new HashMap<String, FlowMefField>();
  private final FlowMefField mefField;
  private static final String NAMESPACE = "nProbe";

  private NProbeToFlowMefMappings(FlowMefField mefField) {
    this.mefField = mefField;
  }

  private void initialize() {
    for (NProbeToFlowMefMappings field : values()) {
      mappings.put(field.name(), field.mefField);
    }
  }

  @Override
  public Set<String> getExternalFieldNames() {
    if (mappings.isEmpty()) {
      initialize();
    }
    return mappings.keySet();
  }

  @Override
  public FlowMefField getMefField(String externalFieldName) {
    return mappings.get(externalFieldName);
  }

  public String getPrettyName() {
    return this.name();
  }

  @Override
  public GenericFormat getByPrettyName(String prettyName) {
    return null;
  }

}
