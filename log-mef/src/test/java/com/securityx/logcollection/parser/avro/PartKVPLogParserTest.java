package com.securityx.logcollection.parser.avro;

import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartKVPLogParserTest extends TestCase {
	protected Logger logger = LoggerFactory.getLogger(PartKVPLogParserTest.class);
	private String morphlineFile;
	private String morphlineId;

	public PartKVPLogParserTest(String testName) {
		super(testName);
		// temporarly commented
		// this.morphlineFile =
		// "logcollection-script-selector-command-list.conf";
		// this.morphlineId = "logcollectionselector";
		this.morphlineFile = "logcollection-parser-main.conf";
		this.morphlineId = "parsermain";
	}

	public void testPartKVPLogParser(LogParser instance, String watchListMsg) throws IOException {		
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
			OutUtils.printOut("PartKVPLogParserTest: input record is parseable");
			output = instance.parse(avroEvent);

			if (output != null && output.size() > 0) {
				Map<String, List<Object>> out = output.get(0);
				OutUtils.printOut("PartKVPLogParserTest out:\n" + out.toString());
			} else {
				if (output == null) {
					OutUtils.printOut("PartKVPLogParserTest: Parser returns null");
				}
				else
					OutUtils.printOut("PartKVPLogParserTest: Parser returns 0 records");
			}
		} else {
			OutUtils.printOut("********\tinvalid parser for: " + watchListMsg + "\t***************");
		}

		/*
		 * assertEquals("destinationSecurityID", "S-1-5-18",
		 * out.get("destinationSecurityID").get(0));
		 * assertEquals("parserOutFormat", "IAMMef",
		 * out.get("parserOutFormat").get(0));
		 */

	}
	
	@Test
	// public void testWindowsSnareCEFAD4672Sample2ToIAMMef() throws
	// IOException, Exception {
	public void testLogParserBasics() throws IOException, Exception {
		PartKVPLogParser instance = new PartKVPLogParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList(),
				10001);		
		
	    String watchListMsg1 = "mar 13 10:00:09 [1037] <warning> reason=watchlist.hit type=event "
	            + "process_guid=00000c42-0000-172c-01d0-5d6cca2adbb2 segment_id=3 "
	            + "host='workstation-8lt' sensor_id=4322 watchlist_id=3 "
	            + "watchlist_name='netconns to .cn or .ru' timestamp='1426255209.17' "
	            + "start_time='2014-11-13t09:05:02.34z' group='default group' "
	            + "process_md5='b9d6d7e6e5c4fcd8dd7f88ec9d563085' "
	            + "process_name='chrome.exe' process_path='c:\\program files "
	            + "(x86)\\google\\chrome\\application\\chrome.exe' last_update='2014-11-13t13:49:39.361z'";
		
	    String watchListMsgConchoUnmatched = "2016-06-22 13:54:26 [18231] <warning>  reason=watchlist.hit type=event process_guid=000006ad-0001-4cac-01d1-ccb49506c01a segment_id=1 host='CDM01-MID-PRD' comms_ip='10.11.112.209' interface_ip='10.11.112.209' sensor_id=1709 watchlist_id=7 watchlist_name='Filemods to Webroot' timestamp='1466621408.25' start_time='2016-06-22T18:33:33.688Z' group='COG Servers' process_md5='923d9b538faef9fb3488b13b8747a535' process_name='w3wp.exe' process_path='c:\\windows\\system32\\inetsrv\\w3wp.exe' last_update='2016-06-22T18:33:56.854Z' alliance_score_srstrust='-100' alliance_updated_srstrust='2014-10-07T00:29:07Z' alliance_data_srstrust='['923d9b538faef9fb3488b13b8747a535']' alliance_link_srstrust='https://services.bit9.com/Services/extinfo.aspx?ak=b8b4e631d4884ad1c56f50e4a5ee9279&sg=0313e1735f6cec221b1d686bd4de23ee&md5=923d9b538faef9fb3488b13b8747a535'";
	    String watchListMsgConchoAutoRun = "2016-06-29 09:07:47 [18231] <warning>  reason=watchlist.hit type=event process_guid=000006db-0000-04b4-01d1-cd2dbc75e437 segment_id=275 host='XAPP07-MID-PRD' comms_ip='10.11.112.243' interface_ip='10.11.112.243' sensor_id=1755 watchlist_id=1 watchlist_name='Autoruns' timestamp='1467208805.48' start_time='2016-06-23T09:00:48.952Z' group='COG Servers' process_md5='dfde777faf31dc25e3624e8071073146' process_name='svchost.exe' process_path='c:\\windows\\system32\\svchost.exe' last_update='2016-06-29T13:39:57.789Z'";
		String watchListMsgConchoFakeNewly = "mar 13 10:00:09 [1037] <warning> reason=watchlist.hit type=event "
	            + "process_guid=00000c42-0000-172c-01d0-5d6cca2adbb2 segment_id=3 "
	            + "host='workstation-8lt' sensor_id=4322 watchlist_id=3 "
	            + "watchlist_name='Newly Executed Applications' timestamp='1426255209.17' "
	            + "start_time='2014-11-13t09:05:02.34z' group='default group' "
	            + "process_md5='b9d6d7e6e5c4fcd8dd7f88ec9d563085' "
	            + "process_name='chrome.exe' process_path='c:\\program files "
	            + "(x86)\\google\\chrome\\application\\chrome.exe' last_update='2014-11-13t13:49:39.361z'";
		
		String watchListFailedConcho = "2016-06-29 10:17:44 [18231] <warning>  reason=watchlist.hit type=event process_guid=00000449-0000-16a8-01d1-d2156b3371da segment_id=1 host='JDELAROSA2-L' comms_ip='172.16.2.30' interface_ip='192.168.1.3' sensor_id=1097 watchlist_id=1 watchlist_name='Autoruns' timestamp='1467213008.33' start_time='2016-06-29T14:49:20.513Z' group='COG Workstations' process_md5='e3bf29ced96790cdaafa981ffddf53a3' process_name='sidebar.exe' process_path='c:\\\\program files\\\\windows sidebar\\\\sidebar.exe' last_update='2016-06-29T14:49:22.229Z' alliance_score_srstrust='-100' alliance_updated_srstrust='2014-10-07T00:29:07Z' alliance_data_srstrust='['e3bf29ced96790cdaafa981ffddf53a3']' alliance_link_srstrust='https://services.bit9.com/Services/extinfo.aspx?ak=b8b4e631d4884ad1c56f50e4a5ee9279&sg=0313e1735f6cec221b1d686bd4de23ee&md5=e3bf29ced96790cdaafa981ffddf53a3'";
		String watchListFailedConcho1= "2016-07-18 11:07:20 [32293] <warning>  reason=watchlist.hit type=event process_guid=00000096-0000-051c-01d1-e10b20b6be84 segment_id=1 host='BFOSTER-L' comms_ip='10.12.31.64' interface_ip='10.12.31.64' sensor_id=150 watchlist_id=1 watchlist_name='Autoruns' timestamp='1468857607.34' start_time='2016-07-18T15:43:28.019Z' group='COG Workstations' process_md5='c78655bc80301d76ed4fef1c1ea40a7d' process_name='svchost.exe' process_path='c:\\\\windows\\\\system32\\\\svchost.exe' last_update='2016-07-18T15:44:43.902Z' alliance_score_srstrust='-100' alliance_updated_srstrust='2014-10-07T00:29:06Z' alliance_data_srstrust='['c78655bc80301d76ed4fef1c1ea40a7d']' alliance_link_srstrust='https://services.bit9.com/Services/extinfo.aspx?ak=b8b4e631d4884ad1c56f50e4a5ee9279&sg=0313e1735f6cec221b1d686bd4de23ee&md5=c78655bc80301d76ed4fef1c1ea40a7d'";		
		String watchListFailedConcho2 = "2016-07-18 11:17:31 [32293] <warning>  reason=watchlist.hit type=event process_guid=0000065b-0004-d460-01d1-e1084476de57 segment_id=1 host='WBAFE02-MID-PRD' comms_ip='10.11.112.177' interface_ip='' sensor_id=1627 watchlist_id=7 watchlist_name='Filemods to Webroot' timestamp='1468858219.67' start_time='2016-07-18T15:22:59.508Z' group='COG Servers' process_md5='c27cda5fcb2eb07311077649f4cb26eb' process_name='w3wp.exe' process_path='c:\\\\windows\\\\system32\\\\inetsrv\\\\w3wp.exe' last_update='2016-07-18T15:30:43.204Z'";
		String watchListOneBackSlash= "2016-07-18 11:07:20 [32293] <warning>  reason=watchlist.hit type=event process_guid=00000096-0000-051c-01d1-e10b20b6be84 segment_id=1 host='BFOSTER-L' comms_ip='10.12.31.64' interface_ip='10.12.31.64' sensor_id=150 watchlist_id=1 watchlist_name='Autoruns' timestamp='1468857607.34' start_time='2016-07-18T15:43:28.019Z' group='COG Workstations' process_md5='c78655bc80301d76ed4fef1c1ea40a7d' process_name='svchost.exe' process_path='c:\\windows\\system32\\svchost.exe' last_update='2016-07-18T15:44:43.902Z' alliance_score_srstrust='-100' alliance_updated_srstrust='2014-10-07T00:29:06Z' alliance_data_srstrust='['c78655bc80301d76ed4fef1c1ea40a7d']' alliance_link_srstrust='https://services.bit9.com/Services/extinfo.aspx?ak=b8b4e631d4884ad1c56f50e4a5ee9279&sg=0313e1735f6cec221b1d686bd4de23ee&md5=c78655bc80301d76ed4fef1c1ea40a7d'";		
		/*
		boolean manyLogs = true;
		if (manyLogs) {
			try {
				
		//BufferedReader br = new BufferedReader(
				//new FileReader("/Users/santanubhattacharyya/device_support/Carbon_Black/local_setup_logs/event_forward/event_bridge_output.json.20160725"));
				 
				int linesTried = 0;

				//BufferedReader br = new BufferedReader(new FileReader("/Users/santanubhattacharyya/temp/cb_log_mixed.txt"));
				BufferedReader br = new BufferedReader(new FileReader("/Users/santanubhattacharyya/temp/cb_log4.txt"));
				String log = null;
				while ((log = br.readLine()) != null) {
					++linesTried;
					if (linesTried > 100000)
						break;
					if (log.length() > 0) {
						OutUtils.printOut("pre string: "+linesTried+"\n" + log);
						testPartKVPLogParser(instance,log);
					}
				}

			} catch (IOException ioe) {
				OutUtils.printOut("PartKVPLogParserTest: "+ioe.getMessage());
			}
		} else {		
			/*
	    testPartKVPLogParser(instance, watchListMsgConchoAutoRun);
	     testPartKVPLogParser(instance, watchListMsgConchoUnmatched);
		testPartKVPLogParser(instance, watchListMsgConchoFakeNewly);
			 */
			testPartKVPLogParser(instance, watchListOneBackSlash);
		//}	    
	}
}
