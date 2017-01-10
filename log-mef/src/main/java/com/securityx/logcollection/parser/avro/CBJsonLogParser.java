package com.securityx.logcollection.parser.avro;

import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class CBJsonLogParser {
    
    private static String getProcessName(String procPath) {
        int pathLen = procPath.length();
        int index = pathLen-1;
        String procName = procPath;
        //System.out.println("testing path: " + procPath);
        while (index >= 0) {
            char c = procPath.charAt(index);
            if (c == '\\' || c == '/') {
                if (pathLen - index > 1) {
                    procName = procPath.substring(index + 1);
                } else {
                    procName = procPath.substring(index);
                }
                break;
            }
            --index;
        }
        return procName;
    }

    private static String getCleanedParam(String param) {
        StringBuilder sb = null;
        if (param != null) {
            param = param.trim();
            sb = new StringBuilder(param);
            int paramLen = sb.length();
            if (paramLen > 0) {
                if (sb.charAt(paramLen - 1) == '"') {
                    sb.deleteCharAt(paramLen - 1);
                }
                if (sb.length() > 0) {
                    if (sb.charAt(0) == '"') {
                        sb.deleteCharAt(0);
                    }
                }
            }
        } else {
            return null;
        }
        return sb == null ? null : sb.toString();
    }

    private static void addProcessParms(ImmutableMap<String,String> kvpToMefMap,
    		org.kitesdk.morphline.api.Record parsedRec, Map<String, String> kvp, 
    		String processKey, String processValueStr ) {
    	
        if (processKey != null) {
            processKey = processKey.trim();
            if (processKey.length() > 0) {
                if (processValueStr != null && processValueStr.length() > 0)
                    processValueStr = processValueStr.trim();

                String normalizedProcessKey = kvpToMefMap.get(processKey);
                processValueStr = getCleanedParam(processValueStr);
                if (normalizedProcessKey != null) {
                	parsedRec.put(normalizedProcessKey, processValueStr);
                    kvp.put(normalizedProcessKey, processValueStr);
                }
                else {
                    kvp.put(processKey, processValueStr);
                }
            }
        }
    }

    private static String syslogFormatJsonCleaner(String inLog) {
        int index = 0;
        int logLen = inLog.length();
        String log = null;
        try {
            while (index < logLen && inLog.charAt(index) != '{') {
                ++index;
            }
            if (index < logLen) {
                log = inLog.substring(index);
            }
        } catch (IndexOutOfBoundsException iofbe) {
            System.err.println("\n--index out of bounds-- "+inLog);
        }
    	return log;
    }
    
    public static boolean parseCBJsonProcessLog(ImmutableMap<String,String> kvpToMefMap, 
    		org.kitesdk.morphline.api.Record parsedRec,
    		String inLog, Map<String, String> kvp) {
    	
        if (inLog == null || inLog.length() <= 0 || kvp == null)
            return false;
        //System.out.println("log:\n" + inLog);
        
        String log = syslogFormatJsonCleaner(inLog);
        //System.out.println("log:\n" + log);
        if (log == null) {
            System.err.println("parseCBJsonProcessLog: invalid log: "+log);
            return false;
        }
        
        JSONObject processObj = new JSONObject(log);
        //System.out.println("jsonObjToStr:\n"+processObj.toString());
        java.util.Iterator<java.lang.String> processParms = processObj.keys();
        while (processParms.hasNext()) {
            String processParmStr = processParms.next();
            if (processParmStr != null) {
                Object processVal = processObj.get(processParmStr);
                //JSONObject jpVal = processObj.getJSONObject(procParmStr);
                if (processVal != null) {
                	//String processValStr = processVal.toString();
                    String processValStr = JSONObject.valueToString(processVal);
                    //System.out.println("key: " + processParmStr + "\tval: " + processValStr);
                    addProcessParms(kvpToMefMap, parsedRec, kvp, 
                    		processParmStr, processValStr);
                }
            }
        }
        String procPath = kvp.get("processFilePath");
        if (procPath != null) {
            String procName = getProcessName(procPath);
            addProcessParms(kvpToMefMap, parsedRec, kvp, "process_name", procName);
        } else {
            return false;
        }
        //dumpCBJsonLogParsedOut(kvp);
        return true;
    }

    private static String jsonLogCleaner(String log) {
        StringBuilder logBldr = new StringBuilder();
        int logLen = log.length();
        int index = 0;
        while (index < logLen) {
            char c = log.charAt(index);
            if (c == '\\')
            {
                logBldr.append('\\');
            }
            ++index;
            logBldr.append(c);
            //System.out.println(logBldr.toString());
        }
        return logBldr.toString();
    }
    
    public static void dumpCBJsonLogParsedOut(Map<String,String> kvpMap) {
    	System.out.println("\nDumping CB Parsed JSON out\n");
    	Set<Map.Entry<String, String>> processParamSet = kvpMap.entrySet();
    	for (Map.Entry<String, String> e : processParamSet) {
    		System.out.println("mefKey: " + e.getKey() + "\tmefVal: " + e.getValue());
    		//bw.write("mefKey: " + e.getKey() + "\tmefVal: " + e.getValue()+"\n");
    	}
    	System.out.println("\n");
    }   
}

