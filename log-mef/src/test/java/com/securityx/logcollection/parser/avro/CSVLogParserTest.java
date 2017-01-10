package com.securityx.logcollection.parser.avro;

import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.utils.OutUtils;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVLogParserTest extends TestCase {
	protected Logger logger = LoggerFactory.getLogger(AvroFileParser.class);
	private String morphlineFile;
	private String morphlineId;

	public CSVLogParserTest(String testName) {
		super(testName);
		// temporarly commented
		// this.morphlineFile =
		// "logcollection-script-selector-command-list.conf";
		// this.morphlineId = "logcollectionselector";
		this.morphlineFile = "logcollection-parser-main.conf";
		this.morphlineId = "parsermain";
	}

	public void testCSVParserLog(LogParser instance, String threat_msg) throws IOException {		
		ByteBuffer buf = ByteBuffer.wrap(threat_msg.getBytes());
		com.securityx.flume.log.avro.Event avroEvent = new com.securityx.flume.log.avro.Event(); // Wed, Nov 2014 16:58:56 GMT
		avroEvent.setBody(buf);

		Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
		headers.put("category", "syslog");
		headers.put("hostname", "somehost");
		headers.put("timestamp", "1384693669604");
		avroEvent.setHeaders(headers);

		List<Map<String, List<Object>>> output = null;
		if (instance.isParseable(avroEvent)) {
			// OutUtils.printOut("CSVParser is valid for record");
			output = instance.parse(avroEvent);

			if (output != null && output.size() > 0) {
				Map<String, List<Object>> out = output.get(0);
				OutUtils.printOut("CSVParserOutput:\n" + out.toString());
			} else {
				if (output == null)
					OutUtils.printOut("CSVLogParserTest: Parser returns null");
				else
					OutUtils.printOut("CSVLogParserTest: Parser returns 0 records");
			}
		} else {
			OutUtils.printOut("********\tinvalid parser for: " + threat_msg + "\t***************");
			System.exit(0);
		}

		/*
		 * assertEquals("destinationSecurityID", "S-1-5-18",
		 * out.get("destinationSecurityID").get(0));
		 * assertEquals("parserOutFormat", "IAMMef",
		 * out.get("parserOutFormat").get(0));
		 */

	}
	
	public void processDataFromFile(CSVLogParser instance, String fname) throws FileNotFoundException, IOException {
		java.io.FileReader reader = new java.io.FileReader(new java.io.File(fname));
		java.io.BufferedReader br = new BufferedReader(reader);
		String threat_msg = null;
		int numRows = 0;
		while ((threat_msg = br.readLine()) != null) {
			if (threat_msg.startsWith("#"))
				continue;
			
			OutUtils.printOut("test log: "+threat_msg);
			testCSVParserLog(instance, threat_msg);
			++numRows;
		}
		OutUtils.printOut("Processed rows: "+numRows);
	}

	@Test
	// public void testWindowsSnareCEFAD4672Sample2ToIAMMef() throws
	// IOException, Exception {
	public void testLogParserBasics() throws IOException, Exception {
		CSVLogParser instance = new CSVLogParser(morphlineFile, morphlineId, SupportedFormats.genSupportedFormatList(),
				10001);
		
		// String traffic_msg = "Feb 22 09:57:30 10.101.10.240 Feb 22 09:57:30
		// sjcc-edgefw01p.paloaltonetworks.com 1,2016/02/22
		// 09:57:29,0008C102021,TRAFFIC,end,0,2016/02/22
		// 09:57:29,86.249.92.218,199.167.52.193,86.249.92.218,10.101.16.56,http-media,,,web-browsing,vsys1,Untrust,EXT_WEB,vlan.10,vlan.116,Splunk_FWD,2016/02/22
		// 09:57:29,34114972,1,57682,80,57682,80,0x40001c,tcp,allow,8735,6870,1865,28,2016/02/22
		// 09:56:34,54,any,0,892867355,0x0,FR,US,0,15,13,tcp-fin,30,0,0,0,,sjcc-edgefw01p,from-policy";
		
		//This is a URL filtering THREAT log 
		//String threat_msg = "Feb 19 14:56:49 PA-VM  : 1,2016/02/19 14:56:48,007200006845,THREAT,url,1,2016/02/19 14:56:48,192.168.1.65,54.86.76.22,0.0.0.0,0.0.0.0,LogAll,,,web-browsing,vsys1,LAN,LAN,ethernet1/1,ethernet1/1,Everything,2016/02/19 14:56:48,125359,1,52642,80,0,0,0x0,tcp,alert,\"lc49.dsr.livefyre.com/livecountping/157868795/97065625945?__=q1q667xay71&routed=1&jid&siteId=352851&networkId=sportsillustrated.fyre.co\",(9999),business-and-economy,informational,client-to-server,1012,0x0,192.168.0.0-192.168.255.255,US,0,application/json,0,,,1,\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36\",,,,,,,0,0,0,0,0,,PA-VM";
		//This is a non URL filtering THREAT log 
		//String threat_msg = "Feb 25 13:29:24 PA-VM  : 1,2016/02/25 13:29:24,007200006845,THREAT,scan,1,2016/02/25 13:29:24,192.168.1.57,166.78.99.170,0.0.0.0,0.0.0.0,,,,not-applicable,vsys1,LAN,LAN,ethernet1/1,,Everything,2016/02/25 13:29:24,0,3,44758,1272,0,0,0x0,tcp,alert,\"\",SCAN: TCP Port Scan(8001),any,medium,server-to-client,345779,0x0,192.168.0.0-192.168.255.255,US,0,,0,,,0,,,,,,,,0,0,0,0,0,,PA-VM,";
		// The following is a URL filtering request with empty url 
		//String threat_msg = "Feb 19 14:56:49 PA-VM  : 1,2016/02/19 14:56:48,007200006845,THREAT,url,1,2016/02/19 14:56:48,192.168.1.65,54.86.76.22,0.0.0.0,0.0.0.0,LogAll,,,web-browsing,vsys1,LAN,LAN,ethernet1/1,ethernet1/1,Everything,2016/02/19 14:56:48,125359,1,52642,80,0,0,0x0,tcp,alert,,(9999),business-and-economy,informational,client-to-server,1012,0x0,192.168.0.0-192.168.255.255,US,0,application/json,0,,,1,\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36\",,,,,,,0,0,0,0,0,,PA-VM";
		//This is a THREAT log from Palo Alto's own network from screen capture		
		//String threat_msg = "<14>Mar 23 14:00:00 SC-SWING-CFW01P.paloaltonetoworks.com : 1,2016/03/23 14:00:00,001901001399,THREAT,url,1,2016/03/23 14:00:00,10.52.65.67,54.210.83.75,12.247.216.126,54.210.83.75,Web Browsing,,,web-browsing,vsys1,corp-wifi,untrust,vlan.312,vlan.10,IT-Default,2016/03/23 14:00:00,68560304,1,65479,80,32287,80,0x408000,tcp,alert,\"afs.moatads.com/empty_flash?tracer=\",(9999),web-advertisements,informational,client-to-server,8163977,0x0,10.0.0.0-10.255.255.255,US,0,,0,,,1,,,,,,,,0";
		// This is an https THREAT log
		//String threat_msg = "Mar 25 15:14:44 PA-VM  : 1,2016/03/25 15:14:44,007200006845,THREAT,url,1,2016/03/25 15:14:44,10.10.4.80,172.217.0.66,0.0.0.0,0.0.0.0,LogAll,,,google-base,vsys1,LAN,LAN,ethernet1/1,ethernet1/1,Everything,2016/03/25 15:14:44,49853,1,57889,80,0,0,0x0,tcp,alert,\"pagead2.googlesyndication.com/activeview?avi=Bh1-Rbar1VrqECMP2-QPEq47QAwAAAAAQATgB4AQCiAXp27gEoAY_&id=lidartos&v=422&adk=1&p=0,0,90,728&tos=0,0,169066,0,0&mtos=0,0,119097,119097,119097&rs=5&tfs=794&tls=3427109&mc=-1&lte=-1&bas=0&bac=0&swf=r&px=1&r=t&bs=-12245933,-12245933&bos=1408,761&ps=-12245933,-12245933&ss=1396,785&tt=3600037&pt=484&deb=1-1-1-109-196-3&tvt=172259&iframe_loc=http://d3l3lkinz3f56t.cloudfront.net/script-0.7.html#js=https:/ad.doubleclick.net/ddm/adj/N2998.668587.AODDISPLAY/B9317865.126723793;click=http:/aax-us-pdx.amazon-adsystem.com/x/c/QQgHdMrTGOlapKn02iJIkaEAAAFTr6GBzQYAAAvW1CFNoQ/;sz=728x90;ord=5,273,552,277,382,505,955;dc_lat=;dc_rdid=;tag_for_child_directed_treatment=?&is=728,90\",(9999),content-delivery-networks,informational,client-to-server,8137495,0x0,10.0.0.0-10.255.255.255,US,0,image/gif,0,,,2,\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586\",,,http://d3l3lkinz3f56t.cloudfront.net/script-0.7.html,,,,0,0,0,0,0,,PA-VM";
		//This is a THREAT log from Palo Alto's own network from screen capture - bad URL with no hostname		
		//String threat_msg = "<14>Mar 23 14:00:00 SC-SWING-CFW01P.paloaltonetoworks.com : 1,2016/03/23 14:00:00,001901001399,THREAT,url,1,2016/03/23 14:00:00,10.52.65.67,54.210.83.75,12.247.216.126,54.210.83.75,Web Browsing,,,web-browsing,vsys1,corp-wifi,untrust,vlan.312,vlan.10,IT-Default,2016/03/23 14:00:00,68560304,1,65479,80,32287,80,0x408000,tcp,alert,\"d=9vo9TnGGUqlrnmdQsl110g==&\",(9999),web-advertisements,informational,client-to-server,8163977,0x0,10.0.0.0-10.255.255.255,US,0,,0,,,1,,,,,,,,0";
		//String threat_msg = "<14>Mar 23 14:00:00 SC-SWING-CFW01P.paloaltonetoworks.com : 1,2016/03/23 14:00:00,001901001399,THREAT,url,1,2016/03/23 14:00:00,10.52.65.67,54.210.83.75,12.247.216.126,54.210.83.75,Web Browsing,,,web-browsing,vsys1,corp-wifi,untrust,vlan.312,vlan.10,IT-Default,2016/03/23 14:00:00,68560304,1,65479,80,32287,80,0x408000,tcp,alert,\"\",(9999),web-advertisements,informational,client-to-server,8163977,0x0,10.0.0.0-10.255.255.255,US,0,,0,,,1,,,,,,,,0";
		//This is a threat log with quotes inside the URL
		//String threat_msg = "<14>Apr  6 08:54:34 PA-VM  : 1,2016/04/06 08:54:33,007200006845,THREAT,url,1,2016/04/06 08:54:33,10.10.4.50,50.16.227.223,0.0.0.0,0.0.0.0,LogAll,,,web-browsing,vsys1,LAN,LAN,ethernet1/1,ethernet1/1,Everything,2016/04/06 08:54:33,170642,1,56923,80,0,0,0x8000,tcp,alert,\"async01.admantx.com/admantx/service?request={\"\"key\"\":\"\"234330834c41105ad5ed794fa036e085b40225c44f9228bb9e2692f427917605\"\",%20\"\"decorator\"\":\"\"template.reuters\"\",%20\"\"filter\"\":[\"\"default\"\"],%20\"\"method\"\":\"\"descriptor\"\",%20\"\"mode\"\":\"\"async\"\",%20\"\"type\"\":\"\"URL\"\",%20\"\"body\"\":\"\"http://www.reuters.com/article/us-mideast-crisis-iraq-offensive-idUSKCN0X31US\"\"}\",(9999),web-advertisements,informational,client-to-server,10492895,0x0,10.0.0.0-10.255.255.255,US,0,text/plain,0,,,1,\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36\",,,http://www.reuters.com/article/us-mideast-crisis-iraq-offensive-idUSKCN0X31US,,,,0,0,0,0,0,,PA-VM,";
		//This is a websense (ForcePoint) log that is being misidentified as a PAN log.  
		/* String threat_msg = "2016-04-06T14:58:17.826683-07:00 ssdwbsnapp6-esg.websense.com CEF: 0|Websense|ESG|8.1.0|Message|Message|5| " + "" +
        "dvc=10.64.88.116 dvchost=ssdwbsnapp6-esg.websense.com rt=1459979893000 externalId=3189036647829603505 messageId=856851496582932728" +
        " suser=slc-fw@trustedcs.com duser=joshua.pena@websense.com greg.spillman@websense.com roger.wignall@websense.com" +
        " msg=THREAT ALERT : medium : 157.52.65.34 -> 192.168.21.198 RFC2397 Data URL Scheme Usage Detected(30419) reset-both" +
        "  in=2267 trueSrc=192.168.1.225 from=slc-fwalerts <slc-fw@trustedcs.com> to=<rcpfwalerts@trustedcs.com> cc= x-mailer= fname= url=";
		*/		
		//String threat_msg = "Feb 17 15:58:03 PA-VM  : 1,2016/02/17 15:58:03,007200006845,THREAT,url,1,2016/02/17 15:58:03,102.168.1.99,95.211.172.143,0.0.0.0,0.0.0.0,LogAll,,,dropbox,vsys1,LAN,LAN,ethernet1/1,ethernet1/1,Everything,2016/02/17 15:58:03,172208,1,59213,443,0,0,0x8000,tcp,alert,\"dl-debug.dropbox.com/\",(9999),online-storage-and-backup,informational,client-to-server,5766064,0x0,10.0.0.0-10.255.255.255,US,0,,0,,,0,,,,,,,,0,0,0,0,0,,PA-VM,";
		//String threat_msg ="<14>Oct 4 00:31:18 FwGamma0.llbean.com 1,2016/10/04 00:31:18,002201000517,THREAT,url,0,2016/10/04 00:31:10,10.120.236.81,168.62.96.142,0.0.0.0,0.0.0.0,InetAccess-Servers,,,ssl,vsys1,BlueZone,Out-FWEcho,ethernet1/1,ethernet1/11,Logging_Blue,2016/10/04 00:31:18,275585,1,65250,443,0,0,0xb000,tcp,alert,\"*.blob.core.windows.net/\",(9999),computer-and-internet-info,informational,client-to-server,3762819806,0x0,10.0.0.0-10.255.255.255,US,0,,0,,,0,,,,,,,,0,13,0,0,0,vsys1,FwGamma0,"; 
		String threat_msg ="<14>Oct 4 00:31:18 FwGamma0.llbean.com 1,2016/10/04 00:31:18,002201000517,THREAT,url,0,2016/10/04 00:31:10,10.120.236.81,168.62.96.142,0.0.0.0,0.0.0.0,InetAccess-Servers,,,ssl,vsys1,BlueZone,Out-FWEcho,ethernet1/1,ethernet1/11,Logging_Blue,2016/10/04 00:31:18,275585,1,65250,443,0,0,0xb000,tcp,alert,\"*.blob.core.windows.net/\",(9999),computer-and-internet-info,informational,0,3762819806,0x0,10.0.0.0-10.255.255.255,US,0,,0,,,0,,,,,,,,0,13,0,0,0,vsys1,FwGamma0,"; 
		
		boolean processFromFile = false;
		if (processFromFile) 
			processDataFromFile(instance, "/Users/santanubhattacharyya/device_support/PAN/mixed_pan_logs_diff_sources.txt");
		else {
			System.out.println(threat_msg);
			testCSVParserLog(instance, threat_msg);
		}
		
	}	
}
