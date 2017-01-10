package com.securityx.logcollection.parser.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import com.opencsv.CSVReader;
import com.securityx.logcollection.parser.avro.AvroParser;
import com.securityx.logcollection.parser.avro.LogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.securityx.model.mef.field.api.SupportedFormats;

abstract public class KVPLogParser extends AvroParser {
	protected Logger logger = LoggerFactory.getLogger(KVPLogParser.class);
   
    public KVPLogParser() throws Exception {
    	super();
    }
    
    public KVPLogParser(String morphlineConfFile, String morphlineId, 
    		List<SupportedFormats> expectedFormats, int nbRegion) throws Exception {
    	super(morphlineConfFile,morphlineId,expectedFormats,nbRegion);
    }
    
    private ImmutablePair<String,String> getKVP(String inToken, int numLoops, char quoteChar)
			throws IOException {
        if (numLoops > 2) {
        	logger.error("getKVP: Too many recursive calls");
            return null;
        }
        com.opencsv.CSVReader reader = new com.opencsv.CSVReader(
                new StringReader(inToken), '=', quoteChar,'\0');
        ImmutablePair<String,String> kvp=null;
        try {
            String[] elemArray = reader.readNext();
            if (elemArray != null && elemArray.length > 0) {
                if (elemArray.length == 2) {
                    kvp = new ImmutablePair<String, String>(elemArray[0],elemArray[1]);
                } else if (elemArray.length == 1) {
                    inToken = inToken + quoteChar;
                    kvp = getKVP(inToken, ++numLoops, quoteChar);
                }
            }
        } catch (IOException ioe) {
        	logger.warn("getKVP: malformatted kvp: "+ioe.getMessage());
        } finally {
        	reader.close();
        }
        return kvp;
    }

    public Map<String,String> logSplitRespectQuote(String in, char quoteChar) throws IOException {
        Map<String,String> nameValuePairs = new HashMap<String, String>();
        
		CSVReader reader = new CSVReader(new StringReader(in), ' ', quoteChar,'\0');
		String[] kvpArray = reader.readNext();
		if (kvpArray != null) {
			for (String s: kvpArray) {
				//  OpenCSV is stripping the trailing char!!
                System.out.println("s: "+s);
				if (s.length() <= 0 || s.indexOf('=') == -1)
					continue;
				ImmutablePair<String, String> pair = getKVP(s, 0, quoteChar);
				if (pair != null) {
					nameValuePairs.put(pair.getLeft(),pair.getRight());
				} else {
                    System.out.println("Error: null pair");
				}
			}
		} else {
			logger.error("logSplitRespectQuote: no  log: "+in);
			return null;
		}
		return nameValuePairs;
	}

    public void dumpNameValuePairs(Map<String,String> nvp) {
        Set<Map.Entry<String,String>> setOfEntries = nvp.entrySet();

        for (Map.Entry e : setOfEntries) {
            System.out.println("name: "+e.getKey()+"\tval: "+e.getValue());
        }
    }

    protected String cleanField(String fld) {
    	return fld != null && fld.length() > 0 ? fld.trim() : fld;    	
    }
    
    protected Map<String,String> cleanKVP(Map<String,String> kvpMap) {
    	Map<String,String> cleanedKVP = new HashMap<String,String>();
    	//Set<Map.Entry<String,String>> kvpSet = kvpMap.entrySet();
    	for (Map.Entry<String,String> e : kvpMap.entrySet()) {
    		String key = cleanField(e.getKey());
    		String value = cleanField(e.getValue());
    		cleanedKVP.put(key, value);
    	}
    	return cleanedKVP;
    }

}
