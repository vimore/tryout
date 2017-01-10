package com.securityx.mef.log.mapreduce;



import com.securityx.flume.log.avro.Event;
//import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.log.parsed.avro.FullParsedOutput;
import com.securityx.logcollection.parser.avro.AvroParser;
import com.securityx.logcollection.parser.avro.CSVLogParser;
import com.securityx.logcollection.parser.avro.LogParser;
import com.securityx.mef.log.mapreduce.logutils.LogLimiter;
import com.securityx.mef.log.mapreduce.logutils.LogSampler;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.apache.avro.mapred.AvroKey;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.kitesdk.morphline.api.MorphlineRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.securityx.mef.log.mapreduce.LogParsingJob.LONGPARSING;

/**
 * A mapper to parse log files. Reads an avro file and one avro record is passed to the map at a time.
 * After parsing, the morphlines output is stored in an avro record and send as input to the reducer
 */
public class LogParsingFullOutputMapperOnly extends Mapper<AvroKey<Event>, NullWritable, AvroKey<FullParsedOutput>, NullWritable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParsingFullOutputMapperOnly.class);
    /**
     * Input file name from which avro records are read
     */
    private String filename = "";

    private String morphlineConf = "logcollection-parser-main.conf";
    private String morphlineConfId = "parsermain";

    private AvroParser parser = null;
    private long totalParsingDuration = 0;
    private long nbParsingDuration = 0;

    private String outputKey = null;
    private List<SupportedFormats> supportedFormats;
    private Event event;
    private String strValue;
    private Map<CharSequence, CharSequence> map = null;
    private AvroKey<FullParsedOutput> mapOutputValue;
    private FullParsedOutput parsedOutputValue;
    private LogLimiter limiter;
    private LogSampler logSampler;
    private HashMap<String, Counter> formatCounters;
    private SimpleDateFormat formatter;
    private CSVLogParser csvParser;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        super.setup(context);
        initCounterPerFormat(context);
        this.supportedFormats = SupportedFormats.genSupportedFormatList();
        String morphlineId = context.getConfiguration().get(LogParsingJobProperties.MORPHLINEID.getPropertyName());
        String morphlineFile = context.getConfiguration().get(LogParsingJobProperties.MORPHLINEFILE.getPropertyName());
        if (null!=morphlineFile && null != morphlineId){
            this.morphlineConf=morphlineFile;
            this.morphlineConfId=morphlineId;
        }
        String nbRegion = context.getConfiguration().get(LogParsingJobProperties.NBREGION.getPropertyName(), "3");

        try {
            //InputSplit is of FileSplit as we read records from an avro file
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            filename = fileSplit.getPath().getName();
            //LOGGER.info("fileSplit.getPath() is " + fileSplit.getPath().toString());
            System.err.println("fileSplit.getPath() is " + fileSplit.getPath().toString());
            outputKey = this.getOutputKey(context, SupportedFormats.WebProxyMef.name());
            parser = new AvroParser(morphlineConf, morphlineConfId, this.supportedFormats,Integer.valueOf(nbRegion));
            csvParser = new CSVLogParser(morphlineConf, morphlineConfId,
                    SupportedFormats.genSupportedFormatList(), Integer.valueOf(nbRegion));
        } catch (Exception ex) {
            LOGGER.error("Getting file name failed:", ex);
            context.getCounter(LogCounters.TOTAL_FILENAME_FAILED).increment(1);
        }
        int maxlog = context.getConfiguration().getInt(LogParsingJobProperties.LOGLIMITERMAXLOG.getPropertyName(), 100);
        this.limiter = new LogLimiter(maxlog);
        System.err.println("After limiter ");
        System.err.flush();
        int minMd5AsInt = context.getConfiguration().getInt(LogParsingJobProperties.LOGSAMPLEMINMD5.getPropertyName(), Integer.MAX_VALUE);
        int maxMd5AsInt = context.getConfiguration().getInt(LogParsingJobProperties.LOGSAMPLEMAXMD5.getPropertyName(), Integer.MIN_VALUE);
        String logSamplerPatterns =  context.getConfiguration().get(LogParsingJobProperties.LOGSAMPLEFIELDSAMPLER.getPropertyName());
        Map<String, String> pattern = null;

        this.logSampler = new LogSampler(logSamplerPatterns, minMd5AsInt, maxMd5AsInt);
        formatter = new SimpleDateFormat("YYYY-MM-dd'T'HH");
        map = new HashMap<CharSequence, CharSequence>();

        System.err.println("setup completed ");
        System.err.flush();

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        if (this.nbParsingDuration > 0) {
            System.err.printf(" avg parsing duration : %f", (new Float(this.totalParsingDuration) / new Float(this.nbParsingDuration)));
            LOGGER.info(" avg parsing duration : "+ String.format("%f",new Float(this.totalParsingDuration) / new Float(this.nbParsingDuration)));
        } else {
            System.err.printf(" parsing duration measures = 0 !!");
            LOGGER.info(" parsing duration measures = 0 !!");

        }
        super.cleanup(context);
    }

    private LogParser getEventParser(com.securityx.flume.log.avro.Event avroEvent) {
        if (csvParser.isParseable(avroEvent)) {
            return csvParser;
        }
        return parser;
    }
    /**
     * map function
     *
     * @param key     An avro record ( from flume output)
     * @param value   Null as the complete Avro record is passed as the key
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(AvroKey<Event> key, NullWritable value, Context context) throws IOException, InterruptedException {
        try {
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_INPUT.name()).increment(1);
            event = key.datum();
            //LOGGER.info("parsing event : "+event.toString());
            long t0 = System.currentTimeMillis();
            LogParser logParser = getEventParser(event);
            List<Map<String, List<Object>>> outputs = logParser.parse(event);
            long parsingDuration = System.currentTimeMillis() - t0;
            this.totalParsingDuration += parsingDuration;
            this.nbParsingDuration += 1;
            if (parsingDuration > LONGPARSING)
                System.err.printf("long parsing %d : %s\n", parsingDuration, new String(event.getBody().array(), Charset.forName("UTF-8")));
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_OUTPUT.name()).increment(1);
            if (outputs != null && outputs.size() > 0 ) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("outputs is not null, contains " + outputs.size());

                for (Map<String, List<Object>> po : outputs) {

                    if (po != null) {
                        map.clear();
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("processing : " + po);
                        long counterStartTime = -1;
                        String parserOutFormat ;
                        String logCollectionHost = "unknown";
                        String deviceNameOrIp = null;
                        for (Map.Entry<String, List<Object>> e : po.entrySet()) {
                            List<Object> vo = e.getValue();
                            strValue = "";
                            if (vo != null && !vo.isEmpty())
                                strValue = vo.get(0).toString();
                            map.put((CharSequence) e.getKey(), (CharSequence) strValue);
                            if (e.getKey().equals("startTime")) {
                                counterStartTime = (Long) e.getValue().get(0);
                                continue;
                            }
                            if (e.getKey().equals("startTime")) {
                                counterStartTime =  (Long) e.getValue().get(0);
                                continue;
                            }

                            if (e.getKey().equals(WebProxyMefField.logCollectionHost.getPrettyName())) {
                                logCollectionHost = (String) e.getValue().get(0);
                                continue;
                            }

                        }

                        if (map.containsKey("parserOutFormat")) {
                             parserOutFormat = (String) map.get("parserOutFormat");
                            if (LOGGER.isDebugEnabled() && limiter.isSubmitAllowed(LogLimiter.LogCategory.SUCCESS_PARSING))
                                LOGGER.debug("parser output : " + parserOutFormat + " : " + map.toString());
                            //output avro record
                            parsedOutputValue = new FullParsedOutput(event, parserOutFormat, map);
                            mapOutputValue = new AvroKey<FullParsedOutput>(parsedOutputValue);
                            if (this.logSampler.needProcess(po)){
                                countersUpdate(context, counterStartTime, parserOutFormat, logCollectionHost);
                                context.write(mapOutputValue, NullWritable.get());
                            } else {
                                context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_FILTERED_MEF.name()).increment(1);
                            }


                        } else {
                            if (limiter.isSubmitAllowed(LogLimiter.LogCategory.NO_OUTPUT_FORMAT))
                                LOGGER.info("parser output : no parserOutFormat : " + map.toString());
                            parsedOutputValue = new FullParsedOutput(event, "NoParserOutFormat", map);
                            mapOutputValue = new AvroKey<FullParsedOutput>(parsedOutputValue);
                            context.write(mapOutputValue, NullWritable.get());
                        }
                    } else {
                        if ( limiter.isSubmitAllowed(LogLimiter.LogCategory.NULL_RETURNED))
                            LOGGER.info("Parser output record is null: "  + event.toString());
                        context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_NULLS.name()).increment(1);
                        //output avro record as "UnMatchedRawLog"
                        parsedOutputValue = new FullParsedOutput(event, "UnMatchedRawLog", new HashMap<CharSequence, CharSequence>());
                        mapOutputValue = new AvroKey<FullParsedOutput>(parsedOutputValue);
                        context.write(mapOutputValue, NullWritable.get());

                    }
                }
            } else {
                if (limiter.isSubmitAllowed(LogLimiter.LogCategory.NULL_RETURNED))
                    LOGGER.error("Parser output is null: " + event.toString());
                context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_NULLS.name()).increment(1);
                context.getCounter(this.getClass().getCanonicalName(),
                        "format-per-host.UnMatched-"+this.parser.getLastEventLogCollectionHost().replace(".", "_")).increment(1);
                //output avro record as "UnMatchedRawLog"
                parsedOutputValue = new FullParsedOutput(event, "UnMatchedRawLog", new HashMap<CharSequence, CharSequence>());
                mapOutputValue = new AvroKey<FullParsedOutput>(parsedOutputValue);
                String outputKey = this.getOutputKey(context, "UnMatchedRawLog");
                context.write(mapOutputValue, NullWritable.get());


            }

        } catch (MorphlineRuntimeException me) {
            if(limiter.isSubmitAllowed(LogLimiter.LogCategory.EXCEPTIONS_RAISED))
                LOGGER.error("Parsing failed:", me);
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_PARSER_EXCEPTIONS.name()).increment(1);
        } catch (Exception ex) {
            if(limiter.isSubmitAllowed(LogLimiter.LogCategory.EXCEPTIONS_RAISED))
                LOGGER.info("Mapper failed:", ex);
            //LOGGER.info("raised by: "+ex.getMessage()+" caused by "+ex.getCause().getMessage(), ex);
            context.getCounter(this.getClass().getCanonicalName(), LogCounters.TOTAL_OTHER_EXCEPTIONS.name()).increment(1);
            throw new IOException(ex);
        }

    }

    /**
     * Form the reducer input key
     *
     * @param context
     * @param formatName
     * @return
     */
    private String getOutputKey(Context context, String formatName) {
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        String hourStr = fileSplit.getPath().getParent().getName();
        String dayStr = fileSplit.getPath().getParent().getParent().getName();
        String monthStr = fileSplit.getPath().getParent().getParent().getParent().getName();
        String yearStr = fileSplit.getPath().getParent().getParent().getParent().getParent().getName();
        String key = String.format("%s_%s_%s_%s_%s", yearStr, monthStr, dayStr, hourStr, formatName);
        return key;
    }
    
    private void initCounterPerFormat(Context context){

        formatCounters = new HashMap<String,Counter>();
        for (SupportedFormats f : SupportedFormats.genSupportedFormatList()){
            formatCounters.put(f.name(), context.getCounter(this.getClass().getCanonicalName(),"format."+f.name()));
        }
//        formatCounters.put("WebProxyMef", context.getCounter(this.getClass().getCanonicalName(),"WebProxyMef"));
//        formatCounters.put("FlowMef", context.getCounter(this.getClass().getCanonicalName(),"FlowMef"));
//        formatCounters.put("DnsMef", context.getCounter(this.getClass().getCanonicalName(),"DNSMef"));
//        formatCounters.put("IAMMef", context.getCounter(this.getClass().getCanonicalName(),"IAMMef"));
//        formatCounters.put("HETMef", context.getCounter(this.getClass().getCanonicalName(),"HETMef"));
//        formatCounters.put("IAMDBMef", context.getCounter(this.getClass().getCanonicalName(),"IAMDBMef"));
//        formatCounters.put("CertMef", context.getCounter(this.getClass().getCanonicalName(),"CERTMef"));
//        formatCounters.put("HostCpuMef", context.getCounter(this.getClass().getCanonicalName(),"HostCPUMef"));
//        formatCounters.put("HostPortMef", context.getCounter(this.getClass().getCanonicalName(),"HostPortMef"));
//        formatCounters.put("HostProcessMef", context.getCounter(this.getClass().getCanonicalName(),"HostProcessMef"));
//        formatCounters.put("HostJobMef", context.getCounter(this.getClass().getCanonicalName(),"HostJobMef"));
//        formatCounters.put("UETMef", context.getCounter(this.getClass().getCanonicalName(),"UETMef"));
        
    }

    private void countersUpdate(Context context, long startTime, String format, String logCollectionHost){
        String key = "format."+format+".startTime_";
        Date date = new Date(startTime);

        key += formatter.format(date);
        //context.getCounter(this.getClass().getCanonicalName(),key).increment(1);
        if (this.formatCounters.containsKey(format))
            this.formatCounters.get(format).increment(1);
        //context.getCounter(this.getClass().getCanonicalName(),"format-per-host."+format+"-"+logCollectionHost.replace(".", "_")).increment(1);

    }


    static enum LogCounters {
        TOTAL_PARSER_OUTPUT,
        TOTAL_OTHER_EXCEPTIONS,
        TOTAL_PARSER_NULLS,
        TOTAL_PARSER_EXCEPTIONS,
        TOTAL_FILENAME_FAILED,
        TOTAL_PARSER_INPUT,
        TOTAL_FILTERED_MEF,
        TOTAL_PARSER_MEF
    }

}