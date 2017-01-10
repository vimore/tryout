package com.securityx.logcollection.parser.avro;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class LogParser {
	
	public abstract List<Map<String, List<Object>>> 
		parse(com.securityx.flume.log.avro.Event avroEvent) throws IOException;
	public abstract boolean isParseable(com.securityx.flume.log.avro.Event avroEvent);
	public abstract void shutdown();
	public abstract String getLastEventLogCollectionHost();
}
