package com.securityx.logcollection.parser.avro;

import com.google.common.collect.ImmutableBiMap;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.kitesdk.morphline.api.Record;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import java.nio.ByteBuffer;
import java.util.Map;

public class CSVLogParser extends AvroParser {
	
	private ImmutableBiMap<Integer,String> indexToFieldMap = null;
	private final SimpleDateFormat logFileDateFormat;	
	private boolean urlType=false;
	
	//String logFieldFormat = "FUTURE_USE1, Receive Time, Serial Number, Type, Subtype, FUTURE_USE2, Generated Time, Source IP, Destination IP, NAT Source IP, NAT Destination IP, Rule Name, Source User, Destination User, Application, Virtual System, Source Zone, Destination Zone, Ingress Interface, Egress Interface, Log Forwarding Profile, FUTURE_USE3, Session ID, Repeat Count, Source Port, Destination Port, NAT Source Port, NAT Destination Port, Flags, Protocol, Action, Miscellaneous, Threat ID, Category, Severity, Direction, Sequence Number, Action Flags, Source Location, Destination Location, FUTURE_USE4, Content_Type, PCAP_id, Filedigest, Cloud,FUTURE_USE5, User_Agent,File_Type,X-Forwarded-For, Referer,Sender, Subject, Recipient, Report ID";
	private final String logFieldFormat = "FUTURE_USE1, startTimeISO, Serial_Number, Type, Subtype, FUTURE_USE2, "+
			"Generated Time,"+
			"sourceAddress, destinationAddress, NAT_Source_IP, NAT_Destination_IP, sgRuleName, sourceUserName, "+
			"Destination_User, sgApplication, Virtual_System, Source_Zone, "+
			"Destination_Zone, Ingress_Interface, "+
			"Egress_Interface, Log_Forwarding_Profile, FUTURE_USE3, Session_ID, Repeat_Count, sourcePort, "+
			"destinationPort, NAT_Source_Port, NAT_Destination_Port, Flags, "+
			"transportProtocol, devicePolicyAction, requestPath, "+
			"sgThreatID, deviceEventCategory, sgSeverity, Direction, Sequence_Number, Action_Flags, Source_Location, "+
			"Destination_Location, FUTURE_USE4, responseContentType, PCAP_id, Filedigest, Cloud, FUTURE_USE5, "+
			"requestClientApplication, File_Type, X-Forwarded-For, requestReferer, Sender, Subject, Recipient, Report_ID";

	public static class HostAndPath {
        String destinationNameOrIp=null;
        String path=null;
        public HostAndPath(String host, String p) {
            destinationNameOrIp = host;
            path = p;
        }
    }
	
	private void buildFieldMap(String inputFieldDef) {
		ImmutableBiMap.Builder<String,Integer> logFieldBuilder = ImmutableBiMap.builder();
		try {
			Reader in = new StringReader(inputFieldDef);
			CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
			List<CSVRecord> recordList = parser.getRecords();
			if( recordList != null) {
//				System.out.println("CSVLogParser: RecordList is valid"); 
				CSVRecord r = recordList.get(0);
				Iterator<String> iter = r.iterator();
				int index=0;
				while (iter.hasNext()) 
					logFieldBuilder.put(iter.next(),index++);
				ImmutableBiMap<String,Integer> logFieldMap = logFieldBuilder.build();
				indexToFieldMap = logFieldMap.inverse();
			}
			else {
//				System.out.println("CSVLogParser: Failed - improper keyword list");
				logger.error("CSVLogParser: Failed - improper keyword list");
			}
		}
		catch (IOException ioe) {
//			System.out.println("CSVLogParser: IO Except: "+ioe.getMessage());
			logger.error("CSVLogParser: Failed "+ioe.getMessage());
		}
	}
	
	public CSVLogParser(String morphlineConfFile, String morphlineId, 
			List<SupportedFormats> expectedFormats, int nbRegion) throws Exception {
		super(morphlineConfFile,morphlineId,expectedFormats,nbRegion);
		buildFieldMap(logFieldFormat);
		logFileDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	}
	
	public boolean isParseable(com.securityx.flume.log.avro.Event avroEvent) {
		String eventBody = new String(avroEvent.getBody().array());
//		System.out.println("Input bytes: "+eventBody);
		Pattern panThreatLogPat = Pattern.compile("^.*,.*,.*,THREAT,.*$");
        java.util.regex.Matcher m =  panThreatLogPat.matcher(eventBody);
        boolean isParseable = false;
        if (m.matches()) {
            isParseable = true;
        } 
		return isParseable;		
	}
	
	private long getStartTimeMillis(String start_time) {
		long epoch=0;
		try {
			java.util.Date date = logFileDateFormat.parse(start_time);
			epoch = date.getTime();
		} catch (java.text.ParseException tp) {
			epoch = (new Date()).getTime();
			logger.warn("CSVLogParser: Could not format date: "+start_time);
		}
		return epoch;
	}
	
	private void addGeneratedUserName(Record parsedRec) {
		String srcAddr = (String) parsedRec.getFirstValue("sourceAddress");
		if (srcAddr != null && srcAddr.length() > 0)
			parsedRec.replaceValues("sourceUserName", "pan_"+srcAddr);
	}
	
	private void missingFieldsFixup(Record parsedRec) {
		String srcAddr = (String) parsedRec.getFirstValue("sourceAddress");
		parsedRec.put("sourceNameOrIp", srcAddr);
		setSgSourceAddress(parsedRec, srcAddr);
	}		
	
	private void setSgSourceAddress(Record parsedRec, String srcAddr) {
		if (urlType || isDirClientServer(parsedRec)) { 
			parsedRec.put("sgSourceAddress", srcAddr);
		} else {
			String destIPAddr = (String)parsedRec.getFirstValue("destinationAddress");
			if (destIPAddr != null && destIPAddr.length() > 0) {
				parsedRec.put("sgSourceAddress",destIPAddr);
			}
		}
	}
	
	private void addMetadataFields(Record parsedRec) {
		parsedRec.put("logSourceType","WebProxyMef");
		parsedRec.put("externalLogSourceType","PAN_FW");
		String start_time = (String) parsedRec.getFirstValue("startTimeISO");
		if (start_time != null) {
			long startTime = getStartTimeMillis(start_time);
			parsedRec.put("startTime",startTime);
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
	
	private void setHostNameOrIP(Record inputRec, String hostNameOrIp) {
		if (!isDestNameOrIPValid(hostNameOrIp)) {
			String destIPAddr = (String)inputRec.getFirstValue("destinationAddress");
			if (destIPAddr != null && destIPAddr.length() > 0)
				hostNameOrIp = destIPAddr;
		}
		inputRec.replaceValues("destinationNameOrIp", hostNameOrIp);
	}

	private boolean pathDestNameIPFixup(Record inputRec) {
		if (inputRec == null)
			return false;
		String subType = (String)inputRec.getFirstValue("Subtype");
		if (subType == null || (subType!= null && !subType.equals("url")))
			return false;
		String reqPath = (String)inputRec.getFirstValue("requestPath");
		HostAndPath hostPath = extractHostAndPath(reqPath);
		if (hostPath != null) {
			inputRec.replaceValues("requestPath", hostPath.path);
			String hostNameOrIp = hostPath.destinationNameOrIp;
			setHostNameOrIP(inputRec, hostNameOrIp);
			/*
			if (!isDestNameOrIPValid(hostNameOrIp)) {
				String destIPAddr = (String)inputRec.getFirstValue("destinationAddress");
				if (destIPAddr != null && destIPAddr.length() > 0)
					hostNameOrIp = destIPAddr;
			}

			inputRec.replaceValues("destinationNameOrIp", hostNameOrIp);
			 */
		}
		else
		{
			if (reqPath == null || reqPath.length() <= 0) {
				logger.debug("pathDestNameIPFixup: url request null or empty");
				setHostNameOrIP(inputRec, "");
			}
			else
				logger.error("pathDestNameIPFixup: bad url: "+reqPath);
		}
		return true;
	}
	
	@Override
	public List<Record> parseOnly(Record inputRec) throws IOException {		
		List messageFieldList = inputRec.get(WebProxyMefField.message.getPrettyName());
		String logMessage = (String)messageFieldList.get(0); 
		logger.debug("logMessage: "+logMessage);
		boolean isIpPathFixed = false;

		try {
			Reader in = new StringReader(logMessage);
			CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
			List<CSVRecord> recordList = parser.getRecords();
			if( recordList != null) {
				CSVRecord r = recordList.get(0);				
				Iterator<String> iter = r.iterator();
				int index=0;				
				while (iter.hasNext()) {
					String logFieldVal = iter.next();					
					String mefFieldName = indexToFieldMap.get(index++);
					if (logFieldVal != null )
						logFieldVal = logFieldVal.trim();
					if (mefFieldName != null)
						mefFieldName = mefFieldName.trim();		
					if (mefFieldName != null) 
						inputRec.put(mefFieldName,logFieldVal);
				}
				//addGeneratedUserName(inputRec); //fake username for testing
				urlType = isUrlType(inputRec);
				addMetadataFields(inputRec);
				missingFieldsFixup(inputRec);
				isIpPathFixed = pathDestNameIPFixup(inputRec);

			}
		}
		catch (IOException ioe) {
			logger.warn("CSVLogParser: "+ioe.getMessage());			
		}
		
		List<org.kitesdk.morphline.api.Record> out = null;
		if (inputRec != null) {			
			if (urlType) {
				if (isIpPathFixed) {
					out = new ArrayList<org.kitesdk.morphline.api.Record>();
					out.add(inputRec);
				}
			}
			else {
				//				System.out.println("processing non url");
				out = new ArrayList<org.kitesdk.morphline.api.Record>();
				org.kitesdk.morphline.api.Record nonUrlRecord = new org.kitesdk.morphline.api.Record();
				addNonUrlFields(inputRec, nonUrlRecord);
				out.add(nonUrlRecord);
			}
		}
		
		if (out != null)
			dumpRecordToConsole(((Record)out.get(0)));
		return out;
	}
	
	private void addNonUrlFields(Record rec, Record nonUrlRec) {
		for (String key :  rec.getFields().keySet()) {			
			if (key != null && key.matches("sg.*")) {				
				String sgVal = (String) rec.getFirstValue(key);
				//System.out.println("sgkey: "+key+"\tsgVal: "+sgVal);
				nonUrlRec.put(key, sgVal);
			}
		}
		/*
		String sourceAddress = (String) rec.getFirstValue("sourceAddress");		
		if (sourceAddress != null) 
			nonUrlRec.put("sgSourceAddress", sourceAddress);
		*/
		String externalLogSourceType = (String) rec.getFirstValue("externalLogSourceType");
		if (externalLogSourceType != null) 
			nonUrlRec.put("externalLogSourceType",externalLogSourceType);		
		nonUrlRec.put("logSourceType","WebProxyMef");
		Long startTimeL = (Long) rec.getFirstValue("startTime");
		if (startTimeL != null) {
			long startTimel = startTimeL.longValue();
//			System.out.println("startTime: "+startTimel);
			nonUrlRec.put("startTime", startTimel);
		}
	}
	
	private boolean isUrlType(org.kitesdk.morphline.api.Record rec) {
		String subType = (String) rec.getFirstValue("Subtype");
		if (subType == null || (subType!= null && !subType.equals("url")))
			return false;
		return true;
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
	}
	
	@Override
	public String getLastEventLogCollectionHost() {
		return super.getLastEventLogCollectionHost();
	}
	
	public HostAndPath extractHostAndPath(String inUrl) {
		 if (inUrl == null || inUrl.length() <= 0)
	            return null;
        inUrl = inUrl.trim();

        if (inUrl.matches("^http://.*$")) {
           inUrl = inUrl.substring(7);
        }
        if (inUrl.matches("^https://.*$")) {
            inUrl = inUrl.substring(8);
         }
        int queryStart = -1;
        if ((queryStart=inUrl.indexOf('?')) != -1) {
            inUrl = inUrl.substring(0,queryStart);
        }
        int pathStartIndex = inUrl.indexOf('/');
        String path = null, dNameIP = null;
        if (pathStartIndex != -1) {
            path = inUrl.substring(pathStartIndex);
            dNameIP = inUrl.substring(0,pathStartIndex);
        }
        else {
            dNameIP = inUrl;
        }
        int portStart = -1;
        if ((portStart=dNameIP.indexOf(':')) != -1) {
            dNameIP = dNameIP.substring(0,portStart);
        }
        //System.out.println("url: "+inUrl+"\npath: "+path+"\tdestinationNameOrIp: "+dNameIP);
        return new HostAndPath(dNameIP,path);
    }	
	
	private boolean urlHasInvalidChars(String url, boolean isFQDN) {
        if (url == null || url.length() == 0)
            return true;
        boolean isValid = true;
        if (isFQDN)
        	isValid = url.matches("^(([-\\p{Alnum}]*)|(\\*))$");
        else
        	isValid = url.matches("^[-\\p{Alnum}]*$");
        //boolean isValid = url.matches("^[-a-zA-Z0-9]*$");
        if (isValid && (url.charAt(0) == '-' || url.charAt(url.length() - 1) == '-'))
            return true;
        return !isValid;
    }

    private boolean isDestNameOrIPValid(String dnip) throws IndexOutOfBoundsException {
        if (dnip == null || dnip.length() < 3 )
            return false;
        int dotIndex = dnip.indexOf('.');
        if ((dotIndex == -1 && dnip.length() > 63) ||
                (dotIndex != -1 && dnip.length() > 255))
            return false;

        if (dotIndex == -1) {
            if (dnip.charAt(0) == '-' || dnip.charAt(dnip.length() - 1) == '-')
                return false;
            if (urlHasInvalidChars(dnip, false)) {
                return false;
            }
        }
        else {
            String hostName = dnip.substring(0,dotIndex);
            if (hostName != null && hostName.length() > 0) {
               if (urlHasInvalidChars(hostName, true))
                return false;
            }
        }
        return true;
    }	
    
    private boolean isDirClientServer(Record parsedRec) {
    	String direction = (String) parsedRec.getFirstValue("Direction");
    	if (direction != null && direction.length() > 0) {
    		if (direction.charAt(0) == '1' 
    				|| direction.equalsIgnoreCase("server-to-client")) {
    			return false;
    		}
    	}
    	return true;
    }      
}
