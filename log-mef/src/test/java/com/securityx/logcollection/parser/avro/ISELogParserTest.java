package com.securityx.logcollection.parser.avro;

import junit.framework.TestCase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.securityx.model.mef.field.api.SupportedFormats;

public class ISELogParserTest extends TestCase {

	protected Logger logger = LoggerFactory.getLogger(ISELogParserTest.class);
	private String morphlineFile;
	private String morphlineId;

	public ISELogParserTest(String testName) {
		super(testName);
		// temporarly commented
		// this.morphlineFile =
		// "logcollection-script-selector-command-list.conf";
		// this.morphlineId = "logcollectionselector";
		this.morphlineFile = "logcollection-parser-main.conf";
		this.morphlineId = "parsermain";
	}

	public void testISELogParser(LogParser instance, String watchListMsg) throws IOException {		
		ByteBuffer buf = ByteBuffer.wrap(watchListMsg.getBytes());
		com.securityx.flume.log.avro.Event avroEvent = new com.securityx.flume.log.avro.Event(); // Wed, Nov 2014 16:58:56 GMT
		avroEvent.setBody(buf);

		Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
		headers.put("category", "syslog");
		// Use if category is used for selection 
		//headers.put("category", "carbonblack"); 
		headers.put("hostname", "somehost");
		headers.put("timestamp", "1384693669604");
		avroEvent.setHeaders(headers);

		List<Map<String, List<Object>>> output = null;
		if (instance.isParseable(avroEvent)) {
			System.out.println("ISELogParserTest: input record is parseable");
			output = instance.parse(avroEvent);
			
			if (output != null && output.size() > 0) {
				Map<String, List<Object>> out = output.get(0);
				System.out.println("ISELogParserTest out:\n" + out.toString());
			} else {
				if (output == null) {
					System.out.println("ISELogParserTest: Parser returns null");
				}
				else
					System.out.println("ISELogParserTest: Parser returns 0 records");
			}
		} else {
			System.out.println("********\tinvalid parser for: " + watchListMsg + "\t***************");
		}
		
		/*
			assertEquals(output.get(0).get("destinationNameOrIp"),"[user3.cisco.com]");		
		 * assertEquals("destinationSecurityID", "S-1-5-18",
		 * out.get("destinationSecurityID").get(0));
		 * 
		 * out.get("parserOutFormat").get(0));
		 */
	}
	
	@Test
	public void testLogParserBasics() throws IOException, Exception {
		ISELogParser instance = new ISELogParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList(),
				10001);
		/*
		{"destinationUserName":"user3","destinationAddress":"1.1.1.3","destinationHostName":"user3.cisco.com",
		 "destinationMacAddress":"00:00:00:00:00:03",,"lastUpdateTime":"1083888550000"}
		 */
		String iseLog1 = "{\"iseIdent\":\"CsC0IseDS\",\"destinationUserName\":\"user3\",\"destinationAddress\":\"1.1.1.3\",\"destinationHostName\":\"user3.cisco.com\",\"destinationMacAddress\":\"00:00:00:00:00:03\",\"lastUpdateTime\":\"1083888550000\"}";
		testISELogParser(instance, iseLog1);
	}
		
}
