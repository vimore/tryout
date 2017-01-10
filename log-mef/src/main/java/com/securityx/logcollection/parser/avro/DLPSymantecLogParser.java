package com.securityx.logcollection.parser.avro;

import java.util.Date;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.kitesdk.morphline.api.Record;

import com.securityx.logcollection.parser.utils.KVPLogParser;
import com.securityx.logcollection.parser.utils.KVPToMefTransformer;
import com.securityx.model.mef.field.api.SupportedFormats;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;

public class DLPSymantecLogParser extends KVPLogParser implements KVPToMefTransformer {
	private ImmutableMap<String, String> kvpToMefMap = null;
	private SimpleDateFormat sdf = null;
	
	public DLPSymantecLogParser(String morphlineConfFile, String morphlineId, 
			List<SupportedFormats> expectedFormats, int nbRegion) throws Exception {
		super(morphlineConfFile,morphlineId,expectedFormats,nbRegion);
		kvpToMefMap = buildFieldMap();
		sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	@Override
	protected String cleanField(String fld) {
		 fld = fld != null && fld.length() > 0 ? fld.trim() : fld;
         if (fld != null && fld.length() > 0 && fld.charAt(fld.length()-1) == ',')
             fld = fld.substring(0,fld.length()-1);
         if (fld != null && fld.length() > 0 &&
                 (fld.charAt(0) == '\"' || fld.charAt(0) == '\''))
             fld = fld.substring(1);
         if (fld != null && fld.length() > 0 && (fld.charAt(fld.length()-1) == '\"' || fld.charAt(fld.length()-1) == '\''))
             fld = fld.substring(0,fld.length()-1);
    	return fld;
    }
	
	@Override
	public List<Record> parseOnly(org.kitesdk.morphline.api.Record inputRec) 
			throws IOException {		
		List messageFieldList = inputRec.get("message");
		if (messageFieldList == null) {
			System.out.println("messageFieldList is null");
			return null;
		}
		
		String logMessage = (String)messageFieldList.get(0); 
		logger.info("DLPSymantecLogParser::parseOnly "+logMessage);
				
		Map<String,String> logFieldsMap = logSplitRespectQuote(logMessage, '\"');		
		dumpKVPMap(logFieldsMap);
		System.out.println("==========Dumping post clean============");
		logFieldsMap = cleanKVP(logFieldsMap);
		dumpKVPMap(logFieldsMap);
		updateInputRecord(logFieldsMap, kvpToMefMap, inputRec);
		
		List<org.kitesdk.morphline.api.Record> out = null;
		if (inputRec != null) {			
			out = new ArrayList<org.kitesdk.morphline.api.Record>();
			out.add(inputRec);
		}
		
		if (logger.isDebugEnabled() && out != null) {			
			dumpRecordToConsole(((org.kitesdk.morphline.api.Record)out.get(0)));
		}
		return out;
	}
	
	private String getTimeSinceEpoch(String event_date) {
		long currentTimeMillis = 0; 
		try { 
			Date date = sdf.parse(event_date);
			currentTimeMillis = date.getTime();
		} catch (java.text.ParseException e) {
			logger.info("EVENT_DATE parse failure: "+e.getMessage());
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			currentTimeMillis = calendar.getTimeInMillis();
		} 		
		return Long.toString(currentTimeMillis);
	}
	
	@Override
	public ImmutablePair<String, String> formatMefFieldValue(String key, String value) {		
		if (key != null && key.length() > 0) {
			if (key.equalsIgnoreCase("startTime")) {
				if (value != null && value.length() >= 0 )
					value = getTimeSinceEpoch(value);
			} 
		}
		return new ImmutablePair(key,value);
	}

	@Override
	public void updateInputRecord(Map<String, String> logFieldMap, Map<String, 
			String> kvp2MefMap, org.kitesdk.morphline.api.Record inputRec) {
		
		Set<Map.Entry<String,String>> kvpSet = logFieldMap.entrySet();
		for (Map.Entry<String,String> e : kvpSet) {
			String logFieldName=null,logFieldVal=null;
			if ((logFieldName = e.getKey()) != null) 
				logFieldName = logFieldName.trim();
			if (logFieldName != null && logFieldName.length() > 0) {
				String mefFieldName = kvp2MefMap.get(logFieldName);
				if (mefFieldName != null) {
					if ((logFieldVal = e.getValue()) != null)
						logFieldVal = logFieldVal.trim();
					ImmutablePair<String, String> kvp = formatMefFieldValue(mefFieldName, logFieldVal);
					inputRec.put(kvp.left, kvp.right);
					logger.debug(logFieldName+"\t::\t"+logFieldVal);
				} else {
					logger.debug("no match for kvp: "+logFieldName+"\t"+logFieldVal);
				}				
			}
		}
	}

	@Override
	public ImmutableMap<String,String> buildFieldMap() {
		ImmutableMap.Builder<String,String> bldr = ImmutableMap.builder();
		
		// Fields that contribute to Entity Fusion
		bldr.put("EVENT_DATE","startTime");
		bldr.put("MACHINE_NAME","sourceHostName");
		bldr.put("USER_NAME","sourceUserName");		
		bldr.put("ORIGINATOR_IP_ADDRESS","sourceAddress");
		
		// Non Fusible Fields		
		bldr.put("DETECTION_DATE","detectDate");
		bldr.put("ENDPOINT_INCIDENT_ID", "epIncidentID");
		bldr.put("EVENT_TYPE_ID","eventTypeID");
		bldr.put("ENDPOINT_CONTENT_ID","epContentID");
		bldr.put("RESPONSE_ACTION", "responseAction");
		bldr.put("INCIDENT_SEVERITY_ID", "incidentSeverityID");
		bldr.put("POLICY_VIOLATION_COUNT", "policyViolationCount");
		bldr.put("CREATION_DATE", "creationDate");
		bldr.put("POLICY_NAME", "policyName");
		bldr.put("POLICY_CONDITION_NAME", "policyCondName");
		bldr.put("CONDITION_VIOLATION_COUNT", "condViolationCount");
		bldr.put("INCIDENT_STATUS_NAME", "incidentStatusName");
		bldr.put("ORIGINATOR_PORT","sourcePort");
		bldr.put("ENDPOINT_LOCATION","epLocation");
		bldr.put("APPLICATION_NAME","applicationName");
		bldr.put("SENDER_IDENTIFIER","sourceID");
		bldr.put("RECIPIENT_IDENTIFIER","destinationID"); // Can be email or URL
		bldr.put("RECIPIENT_PORT","destinationPort");
		bldr.put("ATTACHMENT_NAME","attachmentName");
		bldr.put("ORIGINAL_SIZE","bytesIn");
		bldr.put("OWNER_COUNTRY","ownerCountry");
		
		bldr.put("PROTOCOL_NAME","applicationProtocol");
		return bldr.build();
	}	
	
	private void dumpKVPMap(Map<String,String> kvpMap) {
		Set<Map.Entry<String,String>> kvpSet = kvpMap.entrySet();
		for (Map.Entry<String,String> e : kvpSet) {
			System.out.println("key: "+e.getKey()+"\tval: "+e.getValue());
		}
	}
	
	private void dumpRecordToConsole(Record r) {
		if (r != null) {
			com.google.common.collect.ListMultimap<String,Object> fields = r.getFields();
			if (fields != null) {
				String recordStr = fields.toString();
				logger.debug("dumpRecordToConsole: \n"+recordStr);
			}
		} 
	}

	/* Sample Log:
	 * 2016-12-08 15:20:24 EVENT_DATE="2016-12-08 15:15:53.05", DETECTION_DATE="2016-12-08 15:19:27.034", 
	 * 		ENDPOINT_INCIDENT_ID="116768732", EVENT_TYPE_ID="34", ENDPOINT_CONTENT_ID="119456911", RESPONSE_ACTION="0", 
	 * 		INCIDENT_SEVERITY_ID="3", POLICY_VIOLATION_COUNT="4", CREATION_DATE="2016-12-08 15:19:39.715", 
	 * 		POLICY_NAME="MYPOLICY2", POLICY_CONDITION_NAME="tracking watermark", CONDITION_VIOLATION_COUNT="1", 
	 * 		INCIDENT_STATUS_NAME="incident.status.New", USER_NAME="DOMAIN\\userID", MACHINE_NAME="MYMACHINE1234", 
	 * 		ORIGINATOR_IP_ADDRESS="1.1.1.1", ENDPOINT_LOCATION="CONNECTED", APPLICATION_NAME="my application XYZ", 
	 * 		ATTACHMENT_NAME="TextBufferComponent:1", FILE_NAME="myfile", PRINT_JOB_TITLE="printing job for mysubject", 
	 * 		PRINTER_NAME="PDFCreator", PRINTER_TYPE="Local", ORIGINAL_SIZE="0", OWNER_COUNTRY="US"
	 * 
	 * Note: This log is entirely customizable so unfortunately isParseable has 
	 * to  depend on category to identify this log as a Symantec (Vontu) DLP log
	 */
	@Override
	public boolean isParseable(com.securityx.flume.log.avro.Event avroEvent) {
		Map<CharSequence,CharSequence> flumeHeaderMap = avroEvent.getHeaders();
		Set<Map.Entry<CharSequence,CharSequence>> flumeHeaderSet = flumeHeaderMap.entrySet();
		CharSequence category = flumeHeaderMap.get("category");
		
		if (category != null && category.toString().equalsIgnoreCase("SymantecDLP")) {
			logger.debug("DLPSymantecLogParser:isParseable: True");
			return true;
		}
		logger.debug("DLPSymantecLogParser:isParseable: False");
		return false;
	}

}
