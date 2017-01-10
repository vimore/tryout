package com.securityx.logcollection.parser.avro;

import com.google.common.collect.ImmutableMap;
import com.opencsv.CSVReader;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kitesdk.morphline.api.Record;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class PartKVPLogParser extends AvroParser {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PartKVPLogParser.class);
	
	private ImmutableMap<String,String> kvpToMefMap = null; 
	private final SimpleDateFormat sdf;
	private final boolean debug = false;

	// This needs to become configurable - the default watchlist name may have been changed 
	private String autoRunsWatchListName="Autoruns";
	private String newlyExecedProcessWatchListName = "Newly Executed Applications";
	
	// This should be read from a config file eventually
	//private static final String patternStr="^.*\\s+<warning>\\s+reason=watchlist.hit\\s+.*$";
	private static final String patternCBStr = 
			"(^.*\\s+<warning>\\s+reason=watchlist.hit\\s+.*Autoruns.*$|^.*command.line.*computer.name.*sensor.id.*$)";
			//"(^.*reason=watchlist.hit.*$|^.*command_line.*$)";
			//"^.*command.line.*$";
	Pattern cbPat = Pattern.compile(patternCBStr);
	
	private static final String patternCBAutoRunStr = 
			"^.*\\s+<warning>\\s+reason=watchlist.hit\\s+.*Autoruns.*$";
			//"^.*reason=watchlist.hit.*$";
	Pattern cbAutoRunSelectorPat = Pattern.compile(patternCBAutoRunStr);
	
	// Don't use underscores - they have different representations and don't always work!!
	private static final String patternCBEventStr = 
			"^.*command.line.*computer.name.*sensor.id.*$";
			//"^.*command.line.*$";
	Pattern cbEventSelectorPat = Pattern.compile(patternCBEventStr);
	
	private enum ProcessType {
		AUTORUN, REGULAR;
	}
		
	private class MefGeneratorHelper {
		public final ProcessType procType;
		
		private MefGeneratorHelper(ProcessType ptype) {
			procType = ptype;
		}
		
		public ProcessType getProcType() {
			return procType;
		}
		
		public String getProcTypeName() { 
			return procType.name();
		}
		
		public String getMefName() {
			if (procType == ProcessType.REGULAR)
				return "HostProcessMef";
			else if (procType == ProcessType.AUTORUN)
				return "HostJobMef";
			return null;			
		}
		
		public void addDeviceSpecificFields(
				org.kitesdk.morphline.api.Record parsedRec, 
				Map<String,String> watchLKVP) {
			if (procType == ProcessType.AUTORUN) {
				String procFilePath = watchLKVP.get("process_path");
				String procName = watchLKVP.get("process_name");
				String procGUID = watchLKVP.get("process_guid");
				parsedRec.replaceValues("jobCmd",procFilePath);
				parsedRec.replaceValues("jobName",procName);
				parsedRec.replaceValues("jobLocation", procGUID);
			}
		}
	}
		
	private void addMiscFields(Record inputRec) {	
		String host = getDeviceNameOrIp(inputRec);
		inputRec.put("deviceNameOrIp", host); 
	}	
	
	private void addMetadataFields(Record parsedRec, Map<String,String> watchLKVP,
			MefGeneratorHelper mefHelper) {
		// Need to add deviceNameOrIp
		if (mefHelper == null) {
			logger.info("PartKVPLogParser: bad proc neither regular nor autorun");
			return;
		}
		String logSrcType = mefHelper.getMefName();
		parsedRec.put("logSourceType",logSrcType);
		parsedRec.put("externalLogSourceType","CB-Host-Processes-details");
		
		setLogTimes(parsedRec,watchLKVP);
		addMiscFields(parsedRec);
		
		parsedRec.put("cpuConsumption",null);
		parsedRec.put("memConsumption",null);
		parsedRec.put("processListenPort",null);
		parsedRec.put("processPorts",null);
		parsedRec.put("transportProtocol",null);
		parsedRec.put("cefSignatureId","Seen");
//		rawLog
	}

	private void missingFieldsFixup(Record parsedRec) {
		
	}
	
	private void addRawLog(String rawLog) {
		// 		rawLog
	}
	
	private String getDeviceNameOrIp(Record parsedRec) {
		return (String) parsedRec.getFirstValue("deviceHostName");
	}
	
	private void setLogTimes(Record parsedRec, Map<String,String> watchLKVP) {
		String timestamp = watchLKVP.get("timestamp");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd'T'hh:mm:ss");
		if (timestamp != null && timestamp.length() > 0) {
	        Double dtstamp = Double.parseDouble(timestamp);
	        long tstamp = (long)dtstamp.doubleValue();
	        tstamp *= 1000;
	        java.util.Date procStartDate = new java.util.Date(tstamp);
            String startTimeISO = sdf.format(procStartDate);
            startTimeISO += ".000Z";
            parsedRec.put("startTimeISO",startTimeISO);
            parsedRec.put("startTime",tstamp);
            //parsedRec.put("logCollectionTime", tstampL.toString());            
		}
	}

    private ImmutableMap<String,String> buildFieldMap() {
        ImmutableMap.Builder<String,String> bldr = ImmutableMap.builder();
        bldr.put("host","deviceHostName");
        bldr.put("computer_name","deviceHostName");
        bldr.put("process_path","processFilePath");
        bldr.put("path","processFilePath");
        bldr.put("process_name","processName");
        bldr.put("process_md5", "processFileMd5");
        bldr.put("md5", "processFileMd5");

        //bldr.put("timestamp","logCollectionTime");
        return bldr.build();
    }	
	
	private void dumpRecordToConsole(Record r) {
		if (!debug)
			return;
		if (r != null) {
			com.google.common.collect.ListMultimap<String,Object> fields = r.getFields();
			if (fields != null) {
				String recordStr = fields.toString();
				logger.info("dumpRecordToConsole: \n"+recordStr);
			}
		} 
	}
	
	public PartKVPLogParser(String morphlineConfFile, String morphlineId, 
			List<SupportedFormats> expectedFormats, int nbRegion) throws Exception {
		super(morphlineConfFile,morphlineId,expectedFormats,nbRegion);
		sdf = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			kvpToMefMap = buildFieldMap();			
		} catch (IllegalArgumentException iae) {
			if (debug) {
				System.out.println("PartKVPLogParser::buildFieldMap: "+iae.getMessage());
			}
			return;
		}		
	}
	
	private MefGeneratorHelper parseAutoRunLogLine(
			Record inputRec, String log, Map<String,String> watchLKVP) {

		MefGeneratorHelper mefHelper = null;
		try {
			if (!logSplitRespectQuote(log,watchLKVP))
				return null;
		} catch( IOException ioe) {
			if (debug)
				System.err.println("IOExcept: "+ioe.getMessage());
		}
		Set<Map.Entry<String,String>> kvpSet = watchLKVP.entrySet();
		for (Map.Entry<String,String> e: kvpSet) {
			String logFieldName=null,logFieldVal=null;
			if ((logFieldName = e.getKey()) != null) 
				logFieldName = logFieldName.trim();
			if ((logFieldVal = e.getValue()) != null)
				logFieldVal = logFieldVal.trim();
			if (logFieldName != null && logFieldName.length() > 0) {
				String mefFieldName = kvpToMefMap.get(logFieldName);
				if (mefFieldName != null) {
					inputRec.put(mefFieldName,logFieldVal);
					if (debug)
						System.out.println(logFieldName+"\t::\t"+logFieldVal);
				} else {
					if (debug)
						System.out.println("no match for kvp: "+logFieldName+"\t"+logFieldVal);
				}
				if (logFieldName.equals("watchlist_name")) {
					if (logFieldVal.equalsIgnoreCase(autoRunsWatchListName)) {
						mefHelper = new MefGeneratorHelper(ProcessType.AUTORUN);						
					}
					/*
					else if (logFieldVal.equalsIgnoreCase(newlyExecedProcessWatchListName)) {
						System.out.println("Found newly execed");
						mefHelper = new MefGeneratorHelper(ProcessType.REGULAR);
					}
					*/
				}
			}
		}
		if (mefHelper == null || (mefHelper.getProcType() != ProcessType.AUTORUN  
				&& mefHelper.getProcType() != ProcessType.REGULAR)) {
			logger.info("PartKVPLogParser::parseLogLine: watchlist is neither Autorun nor newly execed");
		}
		return mefHelper;
	}	
		
	private Pair<Map<String,String>,MefGeneratorHelper> parseLogLine(
			Record inputRec, String log) {
		
        //System.out.println("log: "+log);
        Map<String,String> watchLKVP = new TreeMap<String,String>();
        MefGeneratorHelper mefHelper = null;
        
        java.util.regex.Matcher autoMatcher = cbAutoRunSelectorPat.matcher(log); 
        java.util.regex.Matcher eventMatcher = cbEventSelectorPat.matcher(log);
        
        //if (autoMatcher.matches()) {
        if (autoMatcher.find()) {
        	mefHelper = parseAutoRunLogLine(inputRec, log, watchLKVP);
        //} else if (eventMatcher.matches()) {
        } else if (eventMatcher.find()) {
        	boolean success = CBJsonLogParser.parseCBJsonProcessLog(kvpToMefMap, 
        			inputRec, log, watchLKVP);
        	if (success) {
        		mefHelper = new MefGeneratorHelper(ProcessType.REGULAR);
        	} else {
        		logger.debug("parseLogLine: CB Json Parsing failed!! "+log);
        	}
        } else {
        	logger.warn("parseLogLine: Found unmatched Carbon Black log - "
        			+ "check filtering conditions! "+log);
        }
        //CBJsonLogParser.dumpCBJsonLogParsedOut(watchLKVP);
		Pair<Map<String,String>,MefGeneratorHelper> parsedPair = 
				new MutablePair<Map<String,String>,MefGeneratorHelper>(watchLKVP,mefHelper); 
        return parsedPair;
    }
	
	private ImmutablePair<String,String> getKVP(String inToken, int numLoops) 
			throws IOException {
        if (numLoops > 2) {
        	if (debug)
        		System.err.println("getKVP: Too many recursive calls");
            return null;
        }
        com.opencsv.CSVReader reader = new com.opencsv.CSVReader(
                new StringReader(inToken), '=', '\'','\0');        		
        ImmutablePair<String,String> kvp=null;
        try {
            String[] elemArray = reader.readNext();
            if (elemArray != null && elemArray.length > 0) {
                if (elemArray.length == 2) {
                    kvp = new ImmutablePair<String, String>(elemArray[0],elemArray[1]);
                } else if (elemArray.length == 1) {
                    inToken = inToken + '\'';
                    kvp = getKVP(inToken,++numLoops);
                }
            }
        } catch (IOException ioe) {
            if (debug)
            	System.out.println("getKVP: malformatted kvp: "+ioe.getMessage());
        } finally {
        	reader.close();
        }
        return kvp;
    }
	
	private boolean logSplitRespectQuote(String log, Map<String,String> kvp) throws IOException{
		
        int kvpStartIndex = log.indexOf("reason");
        if (kvpStartIndex <= 0) {
        	if (debug)
        		System.err.println("Bad Log: "+log);
            return false;
        }

        String in = log.substring(kvpStartIndex);
        //System.out.println("kvp Str: "+kvpStr);
        
		CSVReader reader = new CSVReader(new StringReader(in), ' ', '\'','\0');
		String[] kvpArray = reader.readNext();
		if (kvpArray != null) {
			for (String s: kvpArray) {
				//  OpenCSV is stripping the trailing char!!
				if (s.length() <= 0)
					continue;
				ImmutablePair<String, String> pair = getKVP(s,0);
				if (pair != null) {
					kvp.put(pair.getLeft(), pair.getRight());
				} else {
					if (debug)
						System.out.println("Error: null pair");
				}
			}
		} else {
			if (debug)
				System.out.println("Logsplitrespectquote: no  log: "+in);
			return false;
		}
		return true;
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
		logger.debug("logMessage: "+logMessage);
		
		//Pair<Map<String,String>,MefGeneratorHelper> parsedPair = new MutablePair<Map<String,String>,MefGeneratorHelper>();
		Pair<Map<String,String>,MefGeneratorHelper> parsedPair = parseLogLine(inputRec,logMessage);
		if (parsedPair == null) {
			logger.warn("parseOnly: Bad log file? "+logMessage);
			System.out.println("parseOnly: Bad log file? "+logMessage);
			return null;
		}
		
		Map<String,String> watchLKVP = parsedPair.getLeft();
		MefGeneratorHelper mefHelper = parsedPair.getRight();

		if (watchLKVP == null)
			return null;
		if (mefHelper == null) {
			logger.info("PartKVPLogParser: process is neither regular nor autorun");
			return null;
		}
		addMetadataFields(inputRec, watchLKVP, mefHelper);
		mefHelper.addDeviceSpecificFields(inputRec, watchLKVP);
		
		List<org.kitesdk.morphline.api.Record> out = null;
		if (inputRec != null) {			
			out = new ArrayList<org.kitesdk.morphline.api.Record>();
			out.add(inputRec);
		}
		if (out != null) 
			dumpRecordToConsole(((org.kitesdk.morphline.api.Record)out.get(0)));
		return out;
	}

		
	@Override
	public boolean isParseable(com.securityx.flume.log.avro.Event avroEvent) {
		String eventBody = new String(avroEvent.getBody().array());		
		logger.debug("PartKVPLogParser::isParseable: Testing: "+eventBody);
		/*
		System.out.println("PartKVPLogParser::isParseable: Testing: "+eventBody);	
		System.out.println("PartKVPLogParser::isParseable: Pattern: "+cbPat);
		*/		
        java.util.regex.Matcher m =  cbPat.matcher(eventBody);
        boolean isParseable = false;
        if (m.find()) {
        //if (m.matches()) {
            isParseable = true;
        }
        //System.out.println("santanu: PartKVPLogParser:isParseable "+isParseable);
        return isParseable;
	}

	/*
	@Override
	public boolean isParseable(com.securityx.flume.log.avro.Event avroEvent) {
		
		Map<CharSequence,CharSequence> flumeHeaderMap = avroEvent.getHeaders();
		Set<Map.Entry<CharSequence,CharSequence>> flumeHeaderSet = flumeHeaderMap.entrySet();
		CharSequence category = flumeHeaderMap.get("category");
		
		if (category != null && category.toString().equalsIgnoreCase("carbonblack")) {
			System.out.println("santanu: PartKVPLogParser:isParseable: True"); 
			return true;
		}
		System.out.println("santanu: PartKVPLogParser:isParseable: False"); 
		return false;
	}
	*/
	
}
