package com.securityx.logcollection.parser.avro;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.securityx.model.mef.field.api.SupportedFormats;

import junit.framework.TestCase;

public class DLPSymantecLogParserTest extends TestCase {
	
	protected Logger logger = LoggerFactory.getLogger(DLPSymantecLogParserTest.class);
	private String morphlineFile;
	private String morphlineId;

	public DLPSymantecLogParserTest(String testName) {
		super(testName);
		// temporarly commented
		// this.morphlineFile =
		// "logcollection-script-selector-command-list.conf";
		// this.morphlineId = "logcollectionselector";
		this.morphlineFile = "logcollection-parser-main.conf";
		this.morphlineId = "parsermain";
	}

	public void testDLPLogParser(LogParser instance, String dlpLog) throws IOException {		
		ByteBuffer buf = ByteBuffer.wrap(dlpLog.getBytes());
		com.securityx.flume.log.avro.Event avroEvent = new com.securityx.flume.log.avro.Event(); 
		avroEvent.setBody(buf);

		Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
		headers.put("category", "syslog");
		// Use if category is used for selection 
		headers.put("category", "SymantecDLP"); 
		headers.put("hostname", "somehost");
		headers.put("timestamp", "1384693669604");
		avroEvent.setHeaders(headers);

		
		if (instance.isParseable(avroEvent)) {
			System.out.println("DLPSymantecLogParserTest: input record is parseable");
			List<Map<String, List<Object>>> output = instance.parse(avroEvent);
			
			if (output != null && output.size() > 0) {
				Map<String, List<Object>> out = output.get(0);
				System.out.println("DLPSymantecLogParserTest out:\n" + out.toString());
			} else {
				if (output == null) {
					System.out.println("DLPSymantecLogParserTest: Parser returns null");
				}
				else
					System.out.println("DLPSymantecLogParserTest: Parser returns 0 records");
			}
		} else {
			System.out.println("********\tinvalid parser for: " + dlpLog + "\t***************");
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
	//public void testLogParserBasics() throws IOException, Exception {
	public void testLogParserBasics() throws Exception {
		DLPSymantecLogParser instance = new DLPSymantecLogParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList(), 10001);
		List<String> logList = new ArrayList<String>();
		String netSampleFromJPMC1 = "2016-12-08 15:40:21 EVENT_DATE=\"2016-12-08 15:39:30\", "
				+ "NETWORK_INCIDENT_ID=\"111776146\", EVENT_TYPE_ID=\"2\", NETWORK_CONTENT_ID=\"119463917\", "
				+ "RESPONSE_ACTION=\"0\", INCIDENT_SEVERITY_ID=\"1\", POLICY_VIOLATION_COUNT=\"2\", "
				+ "CREATION_DATE=\"2016-12-08 15:40:05.184\", POLICY_NAME=\"MYPOLICY-NAME\", "
				+ "POLICY_CONDITION_NAME=\"Encrypted PDF Files\", CONDITION_VIOLATION_COUNT=\"1\", "
				+ "INCIDENT_STATUS_NAME=\"incident.status.New\", SENDER_IDENTIFIER=\"sender@jpmorgan.com\", "
				+ "RECIPIENT_IDENTIFIER=\"recipient@mydomain.com\", SUBJECT=\"RE: THIS IS MY SUBJECT”, ATTACHMENT_NAME=\"myattachment.pdf\", ORIGINAL_SIZE=\"6666796\", PROTOCOL_NAME=\"protocol.name.SMTP\", OWNER_COUNTRY=\"US\", RECIPIENT_TYPE=\"2\"";
		String epSampleFromJPMC1 = "2016-12-08 15:20:24 EVENT_DATE=\"2016-12-08 15:15:53.05\", DETECTION_DATE=\"2016-12-08 15:19:27.034\", "
				+ "ENDPOINT_INCIDENT_ID=\"116768732\", EVENT_TYPE_ID=\"34\", ENDPOINT_CONTENT_ID=\"119456911\", RESPONSE_ACTION=\"0\", "
				+ "INCIDENT_SEVERITY_ID=\"3\", POLICY_VIOLATION_COUNT=\"4\", CREATION_DATE=\"2016-12-08 15:19:39.715\", "
				+ "POLICY_NAME=\"MYPOLICY2\", POLICY_CONDITION_NAME=\"tracking watermark\", CONDITION_VIOLATION_COUNT=\"1\", "
				+ "INCIDENT_STATUS_NAME=\"incident.status.New\", USER_NAME=\"DOMAIN\\userID\", MACHINE_NAME=\"MYMACHINE1234\", "
				+ "ORIGINATOR_IP_ADDRESS=\"1.1.1.1\", ENDPOINT_LOCATION=\"CONNECTED\", APPLICATION_NAME=\"my application XYZ\", "
				+ "ATTACHMENT_NAME=\"TextBufferComponent:1\", FILE_NAME=\"myfile\", PRINT_JOB_TITLE=\"printing job for mysubject\", "
				+ "PRINTER_NAME=\"PDFCreator\", PRINTER_TYPE=\"Local\", ORIGINAL_SIZE=\"0\", OWNER_COUNTRY=\"US\"";
		String epSampleFromJPMC2 = "2016-12-08 15:20:24 EVENT_DATE=\"2016-12-08 15:19:42.374\", "
				+ "DETECTION_DATE=\"2016-12-08 15:19:45.373\", ENDPOINT_INCIDENT_ID=\"111768842\", "
				+ "EVENT_TYPE_ID=\"28\", ENDPOINT_CONTENT_ID=\"111457017\", RESPONSE_ACTION=\"0\", "
				+ "INCIDENT_SEVERITY_ID=\"3\", POLICY_VIOLATION_COUNT=\"5\", CREATION_DATE=\"2016-12-08 15:20:01.956\", "
				+ "POLICY_NAME=\"MY POLICY 3\", POLICY_CONDITION_NAME=\"MYPOLICY_COND1\", CONDITION_VIOLATION_COUNT=\"1\", "
				+ "INCIDENT_STATUS_NAME=\"incident.status.New\", USER_NAME=\" DOMAIN\\userID \", "
				+ "MACHINE_NAME=\" MYMACHINE12345\", ORIGINATOR_IP_ADDRESS=\"1.1.1.1\", ORIGINATOR_PORT=\"0\", "
				+ "ENDPOINT_LOCATION=\"CONNECTED\", APPLICATION_NAME=\"Microsoft Internet Explorer\", "
				+ "SENDER_IDENTIFIER=\"2.2.2.2\", RECIPIENT_IDENTIFIER=\"https://mydomain.com/Attach\", "
				+ "RECIPIENT_PORT=\"0\", ATTACHMENT_NAME=\"attachment name.pdf\", ORIGINAL_SIZE=\"83346\", "
				+ "OWNER_COUNTRY=\"US\"";
		String epSampleFromJPMC3 = "2016-12-08 15:20:24 EVENT_DATE=\"2016-12-08 15:19:23.914\", "
				+ "DETECTION_DATE=\"2016-12-08 15:19:30.307\", ENDPOINT_INCIDENT_ID=\"111768714\", "
				+ "EVENT_TYPE_ID=\"28\", ENDPOINT_CONTENT_ID=\"111456893\", RESPONSE_ACTION=\"0\", "
				+ "INCIDENT_SEVERITY_ID=\"3\", POLICY_VIOLATION_COUNT=\"5\", CREATION_DATE=\"2016-12-08 15:19:39.612\", "
				+ "POLICY_NAME=\"POLICY3\", POLICY_CONDITION_NAME=\"COND4\", CONDITION_VIOLATION_COUNT=\"1\", "
				+ "INCIDENT_STATUS_NAME=\"incident.status.New\", USER_NAME=\"NAEAST\\o373723\", "
				+ "MACHINE_NAME=\"S1007WTS810\", ORIGINATOR_IP_ADDRESS=\"1.1.1.1\", ORIGINATOR_PORT=\"0\", "
				+ "ENDPOINT_LOCATION=\"CONNECTED\", APPLICATION_NAME=\"Microsoft Internet Explorer\", "
				+ "SENDER_IDENTIFIER=\"1.1.1.1\", "
				+ "RECIPIENT_IDENTIFIER=\"https://mydomain.com/Default.aspx?params=hello&hello=again\", "
				+ "RECIPIENT_PORT=\"0\", ATTACHMENT_NAME=\"TextBufferComponent:1\", ORIGINAL_SIZE=\"0\", "
				+ "OWNER_COUNTRY=\"IN\"";
		testDLPLogParser(instance, epSampleFromJPMC3);
	}
		
}
