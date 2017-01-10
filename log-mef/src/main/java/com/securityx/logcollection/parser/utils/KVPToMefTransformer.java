package com.securityx.logcollection.parser.utils;

import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import com.google.common.collect.ImmutableMap;

public interface KVPToMefTransformer {
	public ImmutableMap<String,String> buildFieldMap();
	public void updateInputRecord(Map<String, String> logFieldMap, 
			Map<String,	String> kvp2MefMap, org.kitesdk.morphline.api.Record record);
	public ImmutablePair<String, String> formatMefFieldValue(String key, String value);
}
