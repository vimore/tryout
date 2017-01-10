package com.securityx.model.mef.morphline.command.netflow;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class FlowNProbeScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(FlowNProbeScriptTest.class);

  public FlowNProbeScriptTest() {
    super(FlowNProbeScriptTest.class.toString());
    this.morphlineId = "nprobe";
    this.confFile = "flowmef-nprobe.conf";
  }

  @Test
  public void test_simple() throws FileNotFoundException {
    String line = "10.240.62.89,54.234.106.123,53630,10001,1397033525,1397033525,8685,231,5,4,12:31:39:04:39:AB,FE:FF:FF:FF:FF:FF,0,6,tcp,0,24,0,0,0.0.0.0,0,0,0,1,1,120,30,0,231,3796,6,24,49,49,0,0,1,0,0,1397033525364,1397033525373,10001,4,1";
    boolean result = doTest(line);
    assertEquals(true, result);
  }

  @Test
  public void test1() throws FileNotFoundException {
    // 
    String line = "10.240.62.89,54.234.106.123,53630,10001,1397033525,1397033525,8685,231,5,4,12:31:39:04:39:AB,FE:FF:FF:FF:FF:FF,0,6,tcp,0,24,0,0,0.0.0.0,0,0,0,1,1,120,30,0,231,3796,6,24,49,49,0,0,1,0,0,1397033525364,1397033525373,10001,4,1";
    Record r = new Record();
    r.put("message", line);
    r.put("logCollectionHost", "somehost");
    boolean result = doTest(r);
    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);

    Record out = this.outCommand.getRecord(0);

    assertEquals("bytesIn", 8685L, out.get("bytesIn").get(0));
    assertEquals("bytesOut", 231L, out.get("bytesOut").get(0));
    assertEquals("destinationAddress", "54.234.106.123", out.get("destinationAddress").get(0));
    assertEquals("destinationAutonomousSystem", 0, out.get("destinationAutonomousSystem").get(0));
    assertEquals("destinationMacAddress", "FE:FF:FF:FF:FF:FF", out.get("destinationMacAddress").get(0));
    assertEquals("destinationMask", "0", out.get("destinationMask").get(0));
    assertEquals("destinationPort", 10001, out.get("destinationPort").get(0));
    assertEquals("destinationVlan", 0, out.get("destinationVlan").get(0));
    assertEquals("direction", "1", out.get("direction").get(0));
    assertEquals("endTime", 1397033525373L, out.get("endTime").get(0));
    assertEquals("engineId", "231", out.get("engineId").get(0));
    assertEquals("engineType", "0", out.get("engineType").get(0));
    assertEquals("externalLogSourceType", "nProbe", out.get("externalLogSourceType").get(0));
    assertEquals("icmpType", 0, out.get("icmpType").get(0));
    assertEquals("logSourceType", "FlowMef", out.get("logSourceType").get(0));
    assertEquals("maxTTL", 49, out.get("maxTTL").get(0));
    assertEquals("minTTL", 49, out.get("minTTL").get(0));
    assertEquals("nextHopAddress", "0.0.0.0", out.get("nextHopAddress").get(0));
    assertEquals("numberFlows", 0L, out.get("numberFlows").get(0));
    assertEquals("packetsIn", 5L, out.get("packetsIn").get(0));
    assertEquals("packetsOut", 4L, out.get("packetsOut").get(0));
    assertEquals("samplingAlgorithm", "1", out.get("samplingAlgorithm").get(0));
    assertEquals("samplingInterval", 1, out.get("samplingInterval").get(0));
    assertEquals("sourceAddress", "10.240.62.89", out.get("sourceAddress").get(0));
    assertEquals("sourceAutonomousSystem", 0, out.get("sourceAutonomousSystem").get(0));
    assertEquals("sourceMacAddress", "12:31:39:04:39:AB", out.get("sourceMacAddress").get(0));
    assertEquals("sourceMask", "0", out.get("sourceMask").get(0));
    assertEquals("sourcePort", 53630, out.get("sourcePort").get(0));
    assertEquals("sourceTos", 0, out.get("sourceTos").get(0));
    assertEquals("sourceVlan", 0, out.get("sourceVlan").get(0));
    assertEquals("startTime", 1397033525364L, out.get("startTime").get(0));
    assertEquals("tcpFlags", 24, out.get("tcpFlags").get(0));
    assertEquals("totalBytesExp", 3796L, out.get("totalBytesExp").get(0));
    assertEquals("totalFlowsExp", 24L, out.get("totalFlowsExp").get(0));
    assertEquals("totalPacketsExp", 6L, out.get("totalPacketsExp").get(0));
    assertEquals("transportProtocol", "6", out.get("transportProtocol").get(0));
  }

  @Test
  public void testheader() throws FileNotFoundException {
    String line1 = "IPV4_SRC_ADDR,IPV4_DST_ADDR,IPV4_NEXT_HOP,INPUT_SNMP,OUTPUT_SNMP,IN_PKTS,IN_BYTES,FIRST_SWITCHED,LAST_SWITCHED,L4_SRC_PORT,L4_DST_PORT,TCP_FLAGS,PROTOCOL,SRC_TOS,SRC_AS,DST_AS,IPV4_SRC_MASK,IPV4_DST_MASK";
    String line2 = "164.107.252.92,122.166.71.110,122.166.71.110,120,108,1,53,1185480240,1185480240,31632,10822,0,17,0,0,0,0,19";
    String[] data = {line1, line2};

    boolean result = doTest(data);
    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertEquals(1, this.outCommand.getNumRecords());
    Record r = this.outCommand.getRecord(0);
    assertEquals("bytesIn", 53L, r.get("bytesIn").get(0));
    assertEquals("destinationAddress", "122.166.71.110", r.get("destinationAddress").get(0));
    assertEquals("destinationAutonomousSystem", 0, r.get("destinationAutonomousSystem").get(0));
    assertEquals("destinationMask", "19", r.get("destinationMask").get(0));
    assertEquals("destinationPort", 10822, r.get("destinationPort").get(0));
    assertEquals("externalLogSourceType", "nProbe", r.get("externalLogSourceType").get(0));
    assertEquals("logSourceType", "FlowMef", r.get("logSourceType").get(0));
    assertEquals("nextHopAddress", "122.166.71.110", r.get("nextHopAddress").get(0));
    assertEquals("packetsIn", 1L, r.get("packetsIn").get(0));
    assertEquals("sourceAddress", "164.107.252.92", r.get("sourceAddress").get(0));
    assertEquals("sourceAutonomousSystem", 0, r.get("sourceAutonomousSystem").get(0));
    assertEquals("sourceMask", "0", r.get("sourceMask").get(0));
    assertEquals("sourcePort", 31632, r.get("sourcePort").get(0));
    assertEquals("sourceTos", 0, r.get("sourceTos").get(0));
    assertEquals("startTime", 1185480240000L, r.get("startTime").get(0));
    assertEquals("tcpFlags", 0, r.get("tcpFlags").get(0));
    assertEquals("transportProtocol", "17", r.get("transportProtocol").get(0));

  }

}
