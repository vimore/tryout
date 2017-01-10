package com.securityx.logcollection.parser.avro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.TimeZone;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import org.kitesdk.morphline.api.Record;
import com.securityx.model.mef.field.api.SupportedFormats;


public class ISELogParser extends AvroParser {
	private SimpleDateFormat sdf = null;
	private final ImmutableMap<String,String> mefFieldMap;
	private final boolean iseDebug = true;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ISELogParser.class);
	
	private ImmutableMap<String,String> buildFieldMap() {
		ImmutableMap.Builder<String,String> bldr = ImmutableMap.builder();		
		bldr.put("destinationUserName","destinationUserName");
		bldr.put("destinationAddress","destinationAddress");
		bldr.put("destinationMacAddress","destinationMacAddress");
		bldr.put("destinationHostName","destinationHostName");
		return bldr.build();
	}	

	public ISELogParser(String morphlineConfFile, String morphlineId, 
			List<SupportedFormats> expectedFormats, int nbRegion) throws Exception {
		super(morphlineConfFile,morphlineId,expectedFormats,nbRegion);
		sdf = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		mefFieldMap = buildFieldMap();
		/*
		try {
			mefFieldMap = buildFieldMap();			
		} catch (IllegalArgumentException iae) {
			if (iseDebug) {
				System.out.println("PartKVPLogParser::buildFieldMap: "+iae.getMessage());
			}
		} finally {
			mefFieldMap = null;
		}
		*/	
	}

	private void addDeviceNameOrIP(Record inputRec, JSONObject bodyObj) {	
		String dAddr = null, dHost = null;
		if (bodyObj.has("destinationAddress"))
			dAddr = bodyObj.getString("destinationAddress");
		if (bodyObj.has("destinationHostName")) 
			dHost = bodyObj.getString("destinationHostName");
		if (dHost != null && dHost.length() > 0) {
			inputRec.put("destinationNameOrIp", dHost);
		} else if (dAddr != null && dAddr.length() > 0){ 
			inputRec.put("destinationNameOrIp", dAddr);
		}
	}	
			
	private void addMetadataFields(Record inputRec, JSONObject bodyObj) {
		// Need to add deviceNameOrIp
		addDeviceNameOrIP(inputRec, bodyObj);
		
		//inputRec.put(key, value);
		
		inputRec.put("logSourceType","HETMef");
		inputRec.put("externalLogSourceType","Cisco-ISE-dhcp");

		setLogTimes(inputRec, bodyObj);
		inputRec.put("cefSignatureId","DHCPACK");
	}

	private String getDeviceNameOrIp(Record parsedRec) {
		return (String) parsedRec.getFirstValue("deviceHostName");
	}	
	
	private void setLogTimes(Record inputRec, JSONObject bodyObj) {
		String timestamp = bodyObj.getString("lastUpdateTime");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd'T'hh:mm:ss");
		if (timestamp != null && timestamp.length() > 0) {
	        Double dtstamp = Double.parseDouble(timestamp);
	        long tstamp = (long)dtstamp.doubleValue();
	        //tstamp *= 1000;
	        java.util.Date procStartDate = new java.util.Date(tstamp);
            String startTimeISO = sdf.format(procStartDate);
            startTimeISO += ".000Z";
            inputRec.put("startTimeISO",startTimeISO);
            inputRec.put("startTime",tstamp);
		}
	}
	
	private void dumpRecordToConsole(Record r) {
		if (!iseDebug)
			return;
		if (r != null) {
			com.google.common.collect.ListMultimap<String,Object> fields = r.getFields();
			if (fields != null) {
				String recordStr = fields.toString();
				logger.debug("dumpRecordToConsole: \n"+recordStr);
			}
		} 
	}

	/* Sample ISE Log:
		 {"destinationUserName":"user3","destinationAddress":"1.1.1.3","destinationHostName":"user3.cisco.com",
		 "destinationMacAddress":"00:00:00:00:00:03",,"lastUpdateTime":"1083888550000"}
	 */

	private String getKeyFieldName(String logFldName) {
		if (mefFieldMap == null)
			return null;
		return mefFieldMap.get(logFldName);
	}
	
	private void updateInputRec(Record inputRec, String key, String val) {
			key = key.trim();
			val = val.trim();
			inputRec.put(key,val);
	}
	
	private JSONObject parseLogLine(Record inputRec, String log) {		
        logger.debug("log: "+log);		
        JSONObject bodyObj = null;
        try {
        	bodyObj = new JSONObject(log);
        } catch (JSONException je) {
        	logger.info(je.getMessage());
        	return null;
        }
		
		if ( bodyObj != null) {
			Iterator<String> jiter = bodyObj.keys();
			while (jiter.hasNext()) { 
				try {
					String bkey = jiter.next();			
					String val = bodyObj.getString(bkey);
					if ((bkey = getKeyFieldName(bkey)) != null) {
						updateInputRec(inputRec, bkey, val); 		
					}		
				} catch(JSONException je) {
					logger.info(je.getMessage());
				} 
			}		
		}
        return bodyObj;
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
		logger.debug("ISELogParser::parseOnly "+logMessage);
		
		JSONObject bodyObj= parseLogLine(inputRec,logMessage);
		if (bodyObj == null) {
			logger.warn("parseOnly: Bad log file? "+logMessage);
			System.out.println("parseOnly: Bad log file? "+logMessage);
			return null;
		} 
		
		addMetadataFields(inputRec, bodyObj); 

		List<org.kitesdk.morphline.api.Record> out = null;
		if (inputRec != null) {			
			out = new ArrayList<org.kitesdk.morphline.api.Record>();
			out.add(inputRec);
		}
		if (out != null) 
			dumpRecordToConsole(((org.kitesdk.morphline.api.Record)out.get(0)));
		return out;
	}
		
	
	private String iseIdentStr = "CsC0IseDS";			
	private Pattern iseIdentifierPattern = Pattern.compile(iseIdentStr);
	
	@Override	
	public boolean isParseable(com.securityx.flume.log.avro.Event avroEvent) {
		String eventBody = new String(avroEvent.getBody().array());		
		logger.debug("ISELogParser::isParseable: Testing: "+eventBody);	
				
        java.util.regex.Matcher m =  iseIdentifierPattern.matcher(eventBody);
        boolean isParseable = false;
        if (m.find()) {
        	//if (m.matches()) {
        	isParseable = true;
        }
        return isParseable;
	}	
	
}
