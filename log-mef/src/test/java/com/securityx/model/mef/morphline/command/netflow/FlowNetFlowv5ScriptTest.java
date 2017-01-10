package com.securityx.model.mef.morphline.command.netflow;

import com.securityx.model.mef.morphline.command.LogCollectionAbstractTest;
import com.securityx.utils.OutUtils;
import junit.framework.Assert;
import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

public class FlowNetFlowv5ScriptTest extends LogCollectionAbstractTest {

  private Logger logger = LoggerFactory.getLogger(FlowNetFlowv5ScriptTest.class);

  public FlowNetFlowv5ScriptTest() {
    super(FlowNetFlowv5ScriptTest.class.toString());
    this.morphlineId = "netflowv5";
    this.confFile = "logcollection-Netflowv5.conf";
  }

  @Test
  public void test_simple() throws FileNotFoundException {
    String line = "[NetFlow: 9995] version=\"5\",sysUptime=\"272171940\",unixSecs=\"2010-03-25T14:09:13\",unixNsecs=\"685206264\",flowSequence=\"43975\",engineType=\"0\",engineId=\"0\",samplingInterval=\"0\",IN_BYTES=\"\",IN_PKTS=\"\",FLOWS=\"\",PROTOCOL=\"0\",TCP_FLAGS=\"138\",L4_SRC_PORT=\"4152\",IPV4_SRC_ADDR=\"24.32.0.0\",INPUT_SNMP=\"2620\",L4_DST_PORT=\"46348\",IPV4_DST_ADDR=\"10.60.0.112\",OUTPUT_SNMP=\"1\",SRC_AS=\"16\",DST_AS=\"4352\",MUL_DST_PKTS=\"\",MUL_DST_BYTES=\"\",LAST_SWITCHED=\"272151820\",FIRST_SWITCHED=\"229\",OUT_BYTES=\"\",OUT_PKTS=\"\",MIN_PKT_LNGTH=\"\",MAX_PKT_LNGTH=\"\",IPV6_SRC_ADDR=\"\",IPV6_DST_ADDR=\"\",SAMPLING_INTERVAL=\"\",SAMPLING_ALGORITHM=\"\",FLOW_ACTIVE_TIMEOUT=\"\",FLOW_INACTIVE_TIMEOUT=\"\",TOTAL_BYTES_EXP=\"1\",TOTAL_PKTS_EXP=\"65537\",TOTAL_FLOWS_EXP=\"\",SRC_VLAN=\"\",DST_VLAN=\"\",IF_NAME=\"\",IF_DESC=\"\",DST_MASK=\"0\",IPV4_NEXT_HOP=\"10.60.255.255\",SRC_MASK=\"0\",SRC_TOS=\"138\"";
    boolean result = doTest(line);
    assertEquals(true, result);
  }

  @Test
  public void test1() throws FileNotFoundException {
    // 
    String line = "[NetFlow: 9995] version=\"5\",sysUptime=\"272171940\",unixSecs=\"2010-03-25T14:09:13\",unixNsecs=\"685206264\",flowSequence=\"43975\",engineType=\"0\",engineId=\"0\",samplingInterval=\"0\",IN_BYTES=\"\",IN_PKTS=\"\",FLOWS=\"\",PROTOCOL=\"0\",TCP_FLAGS=\"138\",L4_SRC_PORT=\"4152\",IPV4_SRC_ADDR=\"24.32.0.0\",INPUT_SNMP=\"2620\",L4_DST_PORT=\"46348\",IPV4_DST_ADDR=\"10.60.0.112\",OUTPUT_SNMP=\"1\",SRC_AS=\"16\",DST_AS=\"4352\",MUL_DST_PKTS=\"\",MUL_DST_BYTES=\"\",LAST_SWITCHED=\"272151820\",FIRST_SWITCHED=\"229\",OUT_BYTES=\"\",OUT_PKTS=\"\",MIN_PKT_LNGTH=\"\",MAX_PKT_LNGTH=\"\",IPV6_SRC_ADDR=\"\",IPV6_DST_ADDR=\"\",SAMPLING_INTERVAL=\"\",SAMPLING_ALGORITHM=\"\",FLOW_ACTIVE_TIMEOUT=\"\",FLOW_INACTIVE_TIMEOUT=\"\",TOTAL_BYTES_EXP=\"1\",TOTAL_PKTS_EXP=\"65537\",TOTAL_FLOWS_EXP=\"\",SRC_VLAN=\"\",DST_VLAN=\"\",IF_NAME=\"\",IF_DESC=\"\",DST_MASK=\"0\",IPV4_NEXT_HOP=\"10.60.255.255\",SRC_MASK=\"0\",SRC_TOS=\"138\"";
    boolean result = doTest(line);
    assertEquals(true, result);
    OutUtils.printOut(this.outCommand.getNumRecords());
    Assert.assertTrue(this.outCommand.getNumRecords() == 1);
    
    Record out = this.outCommand.getRecord(0);
    assertEquals("destinationAddress", "10.60.0.112", out.get("destinationAddress").get(0));
    assertEquals("destinationAutonomousSystem", 4352, out.get("destinationAutonomousSystem").get(0));
    assertEquals("destinationMask", "0", out.get("destinationMask").get(0));
    assertEquals("destinationPort", 46348, out.get("destinationPort").get(0));
    assertEquals("engineId", "0", out.get("engineId").get(0));
    assertEquals("engineType", "0", out.get("engineType").get(0));
    assertEquals("flowSequence", "43975", out.get("flowSequence").get(0));
    assertEquals("logSourceType", "FlowMef", out.get("logSourceType").get(0));
    assertEquals("nextHopAddress", "10.60.255.255", out.get("nextHopAddress").get(0));
    assertEquals("samplingInterval", 0, out.get("samplingInterval").get(0));
    assertEquals("sourceAddress", "24.32.0.0", out.get("sourceAddress").get(0));
    assertEquals("sourceAutonomousSystem", 16, out.get("sourceAutonomousSystem").get(0));
    assertEquals("sourceMask", "0", out.get("sourceMask").get(0));
    assertEquals("sourcePort", 4152, out.get("sourcePort").get(0));
    assertEquals("sourceTos", 138, out.get("sourceTos").get(0));
    assertEquals("startTime", 685206264000L, out.get("startTime").get(0));
    assertEquals("tcpFlags", 138, out.get("tcpFlags").get(0));
    assertEquals("totalBytesExp", 1L, out.get("totalBytesExp").get(0));
    assertEquals("totalPacketsExp", 65537L, out.get("totalPacketsExp").get(0));
    assertEquals("transportProtocol", "0", out.get("transportProtocol").get(0));

  }

}
