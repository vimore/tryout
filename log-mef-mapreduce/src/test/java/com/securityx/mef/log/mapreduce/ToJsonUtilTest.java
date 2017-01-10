
package com.securityx.mef.log.mapreduce;

import com.securityx.flume.log.avro.Event;
import org.apache.avro.Schema;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author jyrialhon
 */
public class ToJsonUtilTest {
  
  public ToJsonUtilTest() {
  }

  /**
   * Test of toAvroJsonString method, of class AvroToJsonUtil.
   */
  @Test
  public void testToAvroJsonString() throws Exception {
    System.out.println("toAvroJsonString");
    String bodystr = "<134>May 20 23:13:44 192.168.12.10 2014-05-20 23:13:44 80 45.0.0.194 200 TCP_NC_MISS 1027 741 GET http rad.msn.com /ADSAdClient31.dll ?GetAd?PG=IMSCB2?SC=HF?ID=0006bffd8224c060 - DIRECT rad.msn.com text/html;%20Charset=utf-8 \"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; 3Com; .NET CLR 1.0.3705)\" PROXIED Web%20Advertisements - 192.16.170.42 SG-HTTP-Service - none -\t250598";
    Map<CharSequence,CharSequence> headers = new HashMap<CharSequence,CharSequence>();
    headers.put("timestamp", "1400627625623");
    headers.put("timestamp", "1400627625623");
    headers.put("hostname", "hivedev1.labs.lan");
    headers.put("category", "syslog");
    headers.put("Severity", "6");
    headers.put("host", "2014");
    headers.put("Facility", "17");
    Event evt = new Event(headers, ByteBuffer.wrap(bodystr.getBytes()));
    Schema schema = Event.SCHEMA$;
    System.out.println(schema);
    Event event = null;
    System.out.println(evt);
    String expResult = "{\"headers\":{\"timestamp\":\"1400627625623\",\"category\":\"syslog\",\"host\":\"2014\",\"Severity\":\"6\",\"Facility\":\"17\",\"hostname\":\"hivedev1.labs.lan\"},\"body\":\"<134>May 20 23:13:44 192.168.12.10 2014-05-20 23:13:44 80 45.0.0.194 200 TCP_NC_MISS 1027 741 GET http rad.msn.com /ADSAdClient31.dll ?GetAd?PG=IMSCB2?SC=HF?ID=0006bffd8224c060 - DIRECT rad.msn.com text/html;%20Charset=utf-8 \\\"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; 3Com; .NET CLR 1.0.3705)\\\" PROXIED Web%20Advertisements - 192.16.170.42 SG-HTTP-Service - none -\\t250598\"}";
    String result = AvroToJsonUtil.toAvroJsonString(evt, schema);
    System.out.println(result);
    JSONAssert.assertEquals(expResult, result, true);//(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
  }
  
}
