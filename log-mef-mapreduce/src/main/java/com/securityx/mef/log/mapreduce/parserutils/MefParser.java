package com.securityx.mef.log.mapreduce.parserutils;

import com.securityx.flume.log.avro.Event;
import com.securityx.log.parsed.avro.HetMef;
import com.securityx.log.parsed.avro.LogCollectionMef;
import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.logcollection.parser.avro.LogParser;
import com.securityx.logcollection.parser.avro.AvroParser;
import com.securityx.logcollection.parser.avro.CSVLogParser;
import com.securityx.logcollection.parser.avro.DLPSymantecLogParser;
import com.securityx.logcollection.parser.avro.ISELogParser;
import com.securityx.logcollection.parser.avro.PartKVPLogParser;
import com.securityx.mef.log.mapreduce.*;
import com.securityx.mef.log.mapreduce.file.ParsingJobConfig;
import com.securityx.mef.log.mapreduce.logutils.LogLimiter;
import com.securityx.mef.log.mapreduce.logutils.LogSampler;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.kitesdk.morphline.api.MorphlineRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MefParser {
    public static final String UNMATCHED_RAW_KEY = "UnMatchedRawLog";
    public static final String UN_MATCHED_RAW_LOG_EXCEPTION = UNMATCHED_RAW_KEY + "Exception";
    private static final Logger LOGGER = LoggerFactory.getLogger(MefParser.class);

    private Map<CharSequence, Integer> outEventsCountPerFormat = new HashMap<CharSequence, Integer>();
    private final LogLimiter limiter;
    private final LogSampler logSampler;
    private final SimpleDateFormat formatter;
    private final HashMap<CharSequence, CharSequence> map;
    private final boolean doFormatPerSrcCounters;
    private AvroParser parser = null;
    private CSVLogParser csvParser=null;
    private PartKVPLogParser partKVPParser=null;
    private ISELogParser iseLogParser=null;
    private DLPSymantecLogParser dlpSymantecLogParser = null;
    private HashMap<String, Counter> formatCounters;
    private SpecificRecord mapOutputValue;
    private long totalParsingDuration = 0;
    private long nbParsingDuration = 0;
    private ParsedOutput parsedOutputValue;
    private String strValue;
    private LogCollectionMef.Builder logCollectionMefBuilder;
    private long totalParsingOnlyDuration =0;
    private long parsingDuration;
    private long parsingOnlyDuration;


    public MefParser(Mapper.Context context, String morphlineConf, String morphlineConfId, int nbRegion) {
        initCounterPerFormat(context);
        logCollectionMefBuilder =  LogCollectionMef.newBuilder();
        try {
            parser = new AvroParser(morphlineConf, morphlineConfId, SupportedFormats.genSupportedFormatList(), nbRegion);
            csvParser = new CSVLogParser(morphlineConf, morphlineConfId, 
            		SupportedFormats.genSupportedFormatList(), nbRegion);
            partKVPParser = new PartKVPLogParser(morphlineConf, morphlineConfId, 
            		SupportedFormats.genSupportedFormatList(), nbRegion);
            iseLogParser = new ISELogParser(morphlineConf, morphlineConfId, 
            		SupportedFormats.genSupportedFormatList(), nbRegion);
            dlpSymantecLogParser = new DLPSymantecLogParser(morphlineConf, morphlineConfId, 
            		SupportedFormats.genSupportedFormatList(), nbRegion);
        } catch (Exception ex) {
            LOGGER.error("failed to init parser: ", ex);
            context.getCounter(LogCounters.TOTAL_PARSER_INIT_FAILURE).increment(1);
        }
        
        int maxlog = context.getConfiguration().getInt(LogParsingJobProperties.LOGLIMITERMAXLOG.getPropertyName(), 100);
        this.limiter = new LogLimiter(maxlog);
        System.err.println("After limiter ");
        System.err.flush();
        int minMd5AsInt = context.getConfiguration().getInt(LogParsingJobProperties.LOGSAMPLEMINMD5.getPropertyName(), Integer.MAX_VALUE);
        int maxMd5AsInt = context.getConfiguration().getInt(LogParsingJobProperties.LOGSAMPLEMAXMD5.getPropertyName(), Integer.MIN_VALUE);
        String logSamplerPatterns = context.getConfiguration().get(LogParsingJobProperties.LOGSAMPLEFIELDSAMPLER.getPropertyName());
        this.logSampler = new LogSampler(logSamplerPatterns, minMd5AsInt, maxMd5AsInt);
        formatter = new SimpleDateFormat("YYYY-MM-dd'T'HH");
        map = new HashMap<CharSequence, CharSequence>();

        this.doFormatPerSrcCounters = context.getConfiguration().getBoolean(LogParsingJobProperties.FORMATPERSOURCECOUNTER.getPropertyName(), false);

    }

    public static void countersToGraphite(Job job, Configuration conf, boolean completion) {
        Counters counters = null;
        String graphite = conf.get(LogParsingJobProperties.GRAPHITESERVER.getPropertyName());
        if (null != graphite) {
            LOGGER.info("reporting counters to " + graphite);
            String realm = conf.get(LogParsingJobProperties.GRAPHITEREALM.getPropertyName());
            String realmOverWrite = conf.get(LogParsingJobProperties.GRAPHITEREALMOVER.getPropertyName());
            if (null != realmOverWrite)
                realm = realmOverWrite;
            String[] parts = graphite.split(":");
            if (parts.length != 2) {
                LOGGER.error("invalid value for property " + LogParsingJobProperties.GRAPHITESERVER.getPropertyName() + " : " + graphite);
                LOGGER.error("expected format : server:port (for instance 127.0.0.1:9000");
            } else

                try {
                    GraphiteReporter graphiteReporter = new GraphiteReporter(parts[0],
                            Integer.valueOf(parts[1]), java.net.InetAddress.getLocalHost().getHostName().split("\\.")[0],
                            realm);

                    CounterGroup mycounters = job.getCounters().getGroup(MefParser.class.getCanonicalName());
                    graphiteReporter.publishCounter(MefParser.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.FileSystemCounter.class.getCanonicalName());
                    graphiteReporter.publishCounter(org.apache.hadoop.mapreduce.FileSystemCounter.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.JobCounter.class.getCanonicalName());
                    graphiteReporter.publishCounter(org.apache.hadoop.mapreduce.JobCounter.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.TaskCounter.class.getCanonicalName());
                    graphiteReporter.publishCounter(org.apache.hadoop.mapreduce.TaskCounter.class.getSimpleName(), mycounters);
                    if (completion) {
                        graphiteReporter.publish("job.completion", "1");
                    }else {
                        graphiteReporter.publish("job.completion", "0");
                    }
                    graphiteReporter.close();

                } catch (IOException e) {
                    LOGGER.error(" error sending counter to graphite", e);
                }
        } else
            LOGGER.warn("Property " + LogParsingJobProperties.GRAPHITESERVER.getPropertyName() + " not found");
    }

    public static void countersToHealthTrack(Job job, Configuration conf, boolean completion) {
        Counters counters = null;
        String healthserver = conf.get(LogParsingJobProperties.HEALTHSERVER.getPropertyName());

        if (null != healthserver) {
            healthserver = healthserver.concat("/logparser?name="+job.getJobID());
            LOGGER.info("reporting counters to health server: " + healthserver);

                try {
                    URI healthServerUri = new URI(healthserver);
                    HealthReporter healthReporter = new HealthReporter(healthServerUri);


                    CounterGroup mycounters = job.getCounters().getGroup(MefParser.class.getCanonicalName());
                    healthReporter.publishCounter(MefParser.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.FileSystemCounter.class.getCanonicalName());
                    healthReporter.publishCounter(org.apache.hadoop.mapreduce.FileSystemCounter.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.JobCounter.class.getCanonicalName());
                    healthReporter.publishCounter(org.apache.hadoop.mapreduce.JobCounter.class.getSimpleName(), mycounters);
                    mycounters = job.getCounters().getGroup(org.apache.hadoop.mapreduce.TaskCounter.class.getCanonicalName());
                    healthReporter.publishCounter(org.apache.hadoop.mapreduce.TaskCounter.class.getSimpleName(), mycounters);
                    if (completion)
                        healthReporter.publish("job.completion", "1");
                    else
                        healthReporter.publish("job.completion", "0");
                    healthReporter.close();

                } catch (IOException e) {
                    LOGGER.error(" error sending counter to graphite", e);
                } catch (URISyntaxException e) {
                    LOGGER.error(" error sending counter to graphite", e);
                }
        } else
            LOGGER.warn("Property " + LogParsingJobProperties.HEALTHSERVER.getPropertyName() + " not found");
    }

    public static int moveToFinalDest(FileSystem fs, Path srcBaseDir, ParsingJobConfig jobParsingConf, String jobName, boolean purgeDestDir) throws IOException {

        for (String format : jobParsingConf.getFormats()) {
            LOGGER.info("moveToFinalDest "+format);
            Path destDir = new Path(jobParsingConf.getOutputPath(format));
            Path srcDir = new Path(srcBaseDir + Path.SEPARATOR + format);
            if (purgeDestDir) {
                fs.delete(destDir, true);
            }
            if (fs.exists(srcDir)) {
                List<Path> resultFiles = HdfsFileUtils.moveToPersistentDirWithoutOverlapping(
                        jobName, fs, srcDir, destDir, Pattern.compile(".*\\.avro"));
                if (resultFiles.size() > 0) {
                    fs.delete(srcDir, true);
                    for (Path p : resultFiles) {
                        System.out.println(String.format("Job result file: %s", p.toString()));
                    }
                } else {
                    // relocation of tmp result failed, consider job as
                    // failed.
                    return 1;
                }
            } else if (!format.startsWith(MefParser.UNMATCHED_RAW_KEY)) {
                // pig scripts fail if schema not present
                SupportedFormats supFormat = ParsedOutputConverter.outputDir.inverse().get(format);
                String emptyFile = destDir + Path.SEPARATOR + "empty.avro";
                DataFileWriter<Object> writer = new DataFileWriter<Object>(new GenericDatumWriter<Object>());
                try {
                    writer.create(ParsedOutputConverter.typeMap.get(supFormat), fs.create(new Path(emptyFile)));
                } finally {
                    writer.close();
                }
            }
        }
        fs.delete(srcBaseDir, true);
        return 0;

    }

    private LogParser getEventParser(com.securityx.flume.log.avro.Event avroEvent) {
    	if (csvParser.isParseable(avroEvent)) {
    		return csvParser;
    	}
    	if (partKVPParser.isParseable(avroEvent)) {
    		return partKVPParser;
    	}   
    	if (iseLogParser.isParseable(avroEvent)) {
    		return iseLogParser;
    	}
    	if (dlpSymantecLogParser.isParseable(avroEvent)) {
    		return dlpSymantecLogParser;
    	}
    	return parser; 
    }
    
    public List<MefParserOutput> parse(Event event, Mapper.Context context) throws IOException {
        String logCollectionHost = (String)event.getHeaders().get("hostname");
        String uuid = (String)event.getHeaders().get("uuid");
        String logCollectionType = (String) event.getHeaders().get("category");
        List<MefParserOutput> out = new ArrayList<MefParserOutput>();
        LogCollectionMef logCollectionEvt = new LogCollectionMef(null,
              uuid  , logCollectionType, logCollectionHost, event.getBody().array().length,
                Long.valueOf((String)event.getHeaders().get("timestamp")));

        outEventsCountPerFormat.clear();
        String currentFormat = null;
        this.parsingDuration = 0;
        this.parsingOnlyDuration = 0;

        try {
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_INPUT.name()).increment(1);
            //LOGGER.info("parsing event : "+event.toString());
            long t0 = System.currentTimeMillis();
            LogParser logParser = getEventParser(event);
            long t0ParsingOnly = System.currentTimeMillis();
            List<Map<String, List<Object>>> outputs = logParser.parse(event);
            long t1 = System.currentTimeMillis();
            parsingDuration = t1 - t0;
            parsingOnlyDuration = t1 - t0ParsingOnly;
            this.totalParsingDuration += parsingDuration;
            this.totalParsingOnlyDuration += parsingOnlyDuration;
            this.nbParsingDuration += 1;
            if (parsingDuration > LogParsingJob.LONGPARSING)
                System.err.printf("long parsing %d : %s\n", parsingDuration, new String(event.getBody().array(), Charset.forName("UTF-8")));
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_OUTPUT.name()).increment(1);
            if (outputs != null && outputs.size() > 0) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("outputs is not null, contains " + outputs.size());

                for (Map<String, List<Object>> po : outputs) {
                    logCollectionHost = (String)event.getHeaders().get("hostname");
                    if (po != null) {
                        map.clear();
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("processing : " + po);
                        long counterStartTime = -1;
                        String parserOutFormat;

                        String deviceNameOrIp = null;
                        for (Map.Entry<String, List<Object>> e : po.entrySet()) {
                            List<Object> vo = e.getValue();
                            strValue = "";
                            if (vo != null && !vo.isEmpty()) {
                            	if (vo.get(0) != null) 
                            		strValue = vo.get(0).toString();
                            	else
                            		continue;
                            }
                            map.put((CharSequence) e.getKey(), (CharSequence) strValue);
                            
                            if (e.getKey() == null) {
                            	continue;
                            }
                            if (e.getKey().equals("startTime") && Long.class.isInstance(e.getValue().get(0))) {
                            	counterStartTime = (Long) e.getValue().get(0);
                            	continue;
                            }

                            if (e.getKey().equals(WebProxyMefField.logCollectionHost.getPrettyName())) {
                            	logCollectionHost = (String) e.getValue().get(0);
                            	continue;
                            }
                            
                            if (e.getKey().equals(WebProxyMefField.uuid.getPrettyName())) {
                                uuid = (String) e.getValue().get(0);
                                continue;
                            }
                            if (e.getKey().equals(WebProxyMefField.externalLogSourceType.getPrettyName())) {
                                logCollectionType = (String) e.getValue().get(0);
                                continue;
                            }


                        }
                        if (map.containsKey("parserOutFormat")) {
                            parserOutFormat = (String) map.get("parserOutFormat");
                            Integer n = outEventsCountPerFormat.get(parserOutFormat);
                            outEventsCountPerFormat.put(parserOutFormat, (n == null) ? 1 : ++n);
                            if (LOGGER.isDebugEnabled() && limiter.isSubmitAllowed(LogLimiter.LogCategory.SUCCESS_PARSING))
                                LOGGER.debug("parser output : " + parserOutFormat + " : " + map.toString());
                            //output avro record
                            parsedOutputValue = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), parserOutFormat, map);

                            // default values
                            mapOutputValue = parsedOutputValue;
                            try {
                                // generate a mef formatted output if possible
                                SupportedFormats fmt = SupportedFormats.valueOf(parserOutFormat);
                                currentFormat = parserOutFormat;
                                mapOutputValue = ParsedOutputConverter.toMef(parsedOutputValue, fmt);
                            } catch (IllegalArgumentException ex) {
                                LOGGER.warn("SupportedFormats doesn't have " + parserOutFormat);
                            }
                            if (this.logSampler.needProcess(po)) {
                                countersUpdate(context, counterStartTime, parserOutFormat, logCollectionHost, outputs.size());
                                out.add(new MefParserOutput(currentFormat, mapOutputValue));
                            } else {
                                context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_FILTERED_MEF.name()).increment(1);
                            }
                        } else {
                            if (limiter.isSubmitAllowed(LogLimiter.LogCategory.NO_OUTPUT_FORMAT))
                                LOGGER.info("parser output : no parserOutFormat : " + map.toString());
                            parsedOutputValue = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), "NoParserOutFormat", map);
                            mapOutputValue = parsedOutputValue;
                            out.add(new MefParserOutput(UNMATCHED_RAW_KEY, mapOutputValue));
                        }
                    } else {
                        if (limiter.isSubmitAllowed(LogLimiter.LogCategory.NULL_RETURNED))
                            LOGGER.info("Parser output record is null: " + event.toString());
                        context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_NULLS.name()).increment(1);
                        //output avro record as "UnMatchedRawLog"
                        parsedOutputValue = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), "UnMatchedRawLog", new HashMap<CharSequence, CharSequence>());
                        mapOutputValue = parsedOutputValue;
                        out.add(new MefParserOutput(UNMATCHED_RAW_KEY, mapOutputValue));
                    }
                }
                logCollectionEvt.setEventsPerFormat(outEventsCountPerFormat);
                logCollectionEvt.setUuid(uuid);
                logCollectionEvt.setLogSourceType(logCollectionType);
                out.add(new MefParserOutput(SupportedFormats.LogCollectionMef.name(),logCollectionEvt));
                outEventsCountPerFormat = new HashMap<CharSequence, Integer>();
                countersUpdate(context, logCollectionEvt.getReceiptTime(), SupportedFormats.LogCollectionMef.name(), logCollectionHost,1);
            } else {
                if (limiter.isSubmitAllowed(LogLimiter.LogCategory.NULL_RETURNED))
                    LOGGER.error("Parser output is null: " + event.toString());
                context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_NULLS.name()).increment(1);
                context.getCounter(this.getClass().getCanonicalName(),
                        "format-per-host.UnMatched-" + logParser.getLastEventLogCollectionHost().replace(".", "_")).increment(1);
                //output avro record as "UnMatchedRawLog"
                parsedOutputValue = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), "UnMatchedRawLog", new HashMap<CharSequence, CharSequence>());
                mapOutputValue = parsedOutputValue;
                out.add(new MefParserOutput(UNMATCHED_RAW_KEY, mapOutputValue));
                out.add(new MefParserOutput(SupportedFormats.LogCollectionMef.name(),logCollectionEvt));
                countersUpdate(context, logCollectionEvt.getReceiptTime(), SupportedFormats.LogCollectionMef.name(), logCollectionHost, 1);

            }

        } catch (MorphlineRuntimeException me) {
            if (limiter.isSubmitAllowed(LogLimiter.LogCategory.EXCEPTIONS_RAISED))
                LOGGER.error("Parsing failed:", me);
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_EXCEPTIONS.name()).increment(1);
            map.clear();
            map.put("Exception", stackTraceToString(me));
            out.add(new MefParserOutput(UN_MATCHED_RAW_LOG_EXCEPTION, new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), UN_MATCHED_RAW_LOG_EXCEPTION, map)));
            out.add(new MefParserOutput(SupportedFormats.LogCollectionMef.name(),logCollectionEvt));
            countersUpdate(context, logCollectionEvt.getReceiptTime(), SupportedFormats.LogCollectionMef.name(), logCollectionHost, 1);
        } catch (Exception ex) {
            if (limiter.isSubmitAllowed(LogLimiter.LogCategory.EXCEPTIONS_RAISED))
                LOGGER.error("Mapper failed with :"+event, ex);
            //LOGGER.info("raised by: "+ex.getMessage()+" caused by "+ex.getCause().getMessage(), ex);
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_OTHER_EXCEPTIONS.name()).increment(1);
            throw new IOException( ex);
        }

        return out;

    }

    private String stackTraceToString(Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }

    private void initCounterPerFormat(Mapper.Context context) {

        formatCounters = new HashMap<String, Counter>();
        for (SupportedFormats f : SupportedFormats.genSupportedFormatList()) {
            formatCounters.put(f.name(), context.getCounter(this.getClass().getCanonicalName(), "format." + f.name()));
            formatCounters.put(f.name()+".parsing.duration", context.getCounter(this.getClass().getCanonicalName(), "format."+f.name()+".duration.parsing"));
            formatCounters.put(f.name()+".parsingonly.duration", context.getCounter(this.getClass().getCanonicalName(), "format."+f.name()+".duration.parsingonly"));

        }

    }

    public void cleanup() {
        if (this.nbParsingDuration > 0) {
            System.err.printf(" avg overall parsing duration : %f", (new Float(this.totalParsingDuration) / new Float(this.nbParsingDuration)));
            LOGGER.info(" avg parsing overall duration : " + String.format("%f", new Float(this.totalParsingDuration) / new Float(this.nbParsingDuration)));
            System.err.printf(" avg parsing only duration : %f", (new Float(this.totalParsingOnlyDuration) / new Float(this.nbParsingDuration)));
            LOGGER.info(" avg parsing only duration : " + String.format("%f", new Float(this.totalParsingOnlyDuration) / new Float(this.nbParsingDuration)));
        } else {
            System.err.printf(" parsing duration measures = 0 !!");
            LOGGER.info(" parsing duration measures = 0 !!");
        }
        parser.shutdown();
    }

    private void countersUpdate(Mapper.Context context, long startTime, String format, String logCollectionHost, int nbOfOutputRecords) {
        String key = "format." + format + ".startTime_";
        Date date = new Date(startTime);

        key += formatter.format(date);
        context.getCounter(this.getClass().getCanonicalName(), key).increment(1);
        if (this.formatCounters.containsKey(format))
            this.formatCounters.get(format).increment(1);
        if (this.formatCounters.containsKey(format+".parsing.duration"))
            this.formatCounters.get(format+".parsing.duration").increment(this.parsingDuration/nbOfOutputRecords);
        if (this.formatCounters.containsKey(format+".parsingonly.duration"))
            this.formatCounters.get(format+".parsingonly.duration").increment(this.parsingOnlyDuration/nbOfOutputRecords);
        if (this.doFormatPerSrcCounters)
            context.getCounter(this.getClass().getCanonicalName(), "format-per-host." + format + "-" + logCollectionHost.replace(".", "_")).increment(1);

    }

    public enum LogCounters {
        TOTAL_PARSER_OUTPUT,
        TOTAL_OTHER_EXCEPTIONS,
        TOTAL_PARSER_NULLS,
        TOTAL_PARSER_EXCEPTIONS,
        TOTAL_PARSER_INIT_FAILURE,
        TOTAL_FILENAME_FAILED,
        TOTAL_PARSER_INPUT,
        TOTAL_FILTERED_MEF,
        TOTAL_PARSER_MEF
    }
}
