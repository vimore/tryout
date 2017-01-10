
package com.securityx.mef.log.mapreduce.file;


import com.securityx.flume.log.avro.Event;
import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.logcollection.parser.avro.AvroParser;
import com.securityx.mef.log.mapreduce.LogParsingJobProperties;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import org.kitesdk.morphline.api.MorphlineRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.securityx.mef.log.mapreduce.LogParsingJob.LONGPARSING;
import static com.securityx.mef.log.mapreduce.file.LogFileParsingJob.*;

/**
 * A mapper to parse log files. Reads an avro file and one avro record is passed to the map at a time.
 * After parsing, the morphlines output is stored in an avro record and send as input to the reducer
 */
public class LogFileParsingMapperNotUsed extends Mapper<LongWritable, Text, Text, AvroValue<ParsedOutput>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileParsingMapperNotUsed.class);
    /**
     * Input file name from which avro records are read
     */
    private String filename = "";

    private String morphlineConf = null;
    private String morphlineConfId = null;
    private String optLogSrouce = null;

    private AvroParser parser = null;
    private long totalParsingDuration = 0;
    private long nbParsingDuration = 0;

    private String outputKey = null;
    private List<SupportedFormats> supportedFormats;
    private Event event;

    static enum LogCounters {TOTAL_READ, TOTAL_OTHER_EXCEPTIONS, TOTAL_NULLS, TOTAL_MORPHLINE_EXCEPTIONS, TOTAL_FILENAME_FAILED, TOTAL_ATTEMPTED}

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        this.event = new Event();
        Map<CharSequence, CharSequence> header = new HashMap<CharSequence, CharSequence>();
        this.optLogSrouce = context.getConfiguration().get(LOGSOURCE, "_none_");
        if (!"_none_".equals(this.optLogSrouce)) {
            header.put("hostname", this.optLogSrouce);
            System.out.println("Log source defined by config : " + this.optLogSrouce);
        }
        event.setHeaders(header);
        this.supportedFormats = SupportedFormats.genSupportedFormatList();
        this.morphlineConf = context.getConfiguration().get(MORPHLINESCRIPT);
        this.morphlineConfId = context.getConfiguration().get(MORPHLINEID);
        String nbRegion = context.getConfiguration().get(LogParsingJobProperties.NBREGION.getPropertyName(), "3");

        try {
            //InputSplit is of FileSplit as we read records from an avro file
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            filename = fileSplit.getPath().getName();
            //LOGGER.info("fileSplit.getPath() is " + fileSplit.getPath().toString());
            System.out.println("fileSplit.getPath() is " + fileSplit.getPath().toString());
            outputKey = this.getOutputKey(context, SupportedFormats.WebProxyMef.name());
            parser = new AvroParser(morphlineConf, morphlineConfId, this.supportedFormats, Integer.valueOf(nbRegion));
        } catch (Exception ex) {
            LOGGER.error("Getting file name failed:", ex);
            context.getCounter(LogCounters.TOTAL_FILENAME_FAILED).increment(1);

        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        if (this.nbParsingDuration > 0) {
            System.out.printf(String.format(" avg parsing duration : %f ms", (new Float(this.totalParsingDuration) / new Float(this.nbParsingDuration))));
            LOGGER.info(String.format(" avg parsing duration : %f ms", (new Float(this.totalParsingDuration) / new Float(this.nbParsingDuration))));
        } else {
            System.out.printf(" parsing duration measures = 0 !!");
            LOGGER.info(" parsing duration measures = 0 !!");

        }
        super.cleanup(context);
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
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        long keyid = 0;
        try {
            context.getCounter(LogCounters.TOTAL_ATTEMPTED).increment(1);
            keyid = key.get();
            //System.err.printf("parser input (%d) : %s\n",keyid, value.toString());
            //LOGGER.info("parsing msg : "+value.toString());
            event.setBody(ByteBuffer.wrap(value.toString().getBytes()));
            //LOGGER.info("parsing event : "+event.toString());
            long t0 = System.currentTimeMillis();
            List<Map<String, List<Object>>> outputs = parser.parse(event);
            long parsingDuration = System.currentTimeMillis() - t0;
            //System.err.printf("parser done\n");
            this.totalParsingDuration += parsingDuration;
            this.nbParsingDuration += 1;
            if (parsingDuration > LONGPARSING)
                System.out.printf("long parsing %d : %s\n", parsingDuration, new String(event.getBody().array(), Charset.forName("UTF-8")));
            context.getCounter(LogCounters.TOTAL_READ).increment(1);
            if (outputs != null) {
                for (Map<String, List<Object>> po : outputs) {
                    if (po != null) {
                        //System.err.printf("parser output  (%d)\n",keyid);
                        //System.err.printf("parser output : %s\n", po.toString());
                        Map<CharSequence, CharSequence> map = new HashMap<CharSequence, CharSequence>();

                        for (Map.Entry<String, List<Object>> e : po.entrySet()) {
                            List<Object> vo = e.getValue();
                            String v = "";
                            if (vo != null && !vo.isEmpty())
                                v = vo.get(0).toString();
                            map.put((CharSequence) e.getKey(), (CharSequence) v);
                        }

                        if (map.containsKey("parserOutFormat")) {
                            String parserOutFormat = (String) map.get("parserOutFormat");
                            LOGGER.trace("parser output : " + parserOutFormat + " : " + map.toString());
                            //output avro record
                            ParsedOutput poAvro = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), parserOutFormat, map);
                            AvroValue<ParsedOutput> mapOutputValue = new AvroValue<ParsedOutput>(poAvro);
                            String outputKey = this.getOutputKey(context, parserOutFormat);
                            context.write(new Text(outputKey), mapOutputValue);

                        } else {
                            LOGGER.trace("parser output : no parserOutFormat : " + map.toString());
                            ParsedOutput poAvro = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), "NoParserOutFormat", map);
                            AvroValue<ParsedOutput> mapOutputValue = new AvroValue<ParsedOutput>(poAvro);
                            String outputKey = this.getOutputKey(context, "NoParserOutFormat");
                            context.write(new Text(outputKey), mapOutputValue);
                        }
                    } else {
                        System.out.printf("parser output record for (%d) : null", keyid);
                        context.getCounter(LogCounters.TOTAL_NULLS).increment(1);
                        //output avro record as "UnMatchedRawLog"
                        ParsedOutput poAvro = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), "UnMatchedRawLog", new HashMap<CharSequence, CharSequence>());
                        AvroValue<ParsedOutput> mapOutputValue = new AvroValue<ParsedOutput>(poAvro);
                        String outputKey = this.getOutputKey(context, "UnMatchedRawLog");
                        context.write(new Text(outputKey), mapOutputValue);

                    }
                }
            }else{
                System.out.printf("parser output  (%d) : null", keyid);
                context.getCounter(LogCounters.TOTAL_NULLS).increment(1);
                //output avro record as "UnMatchedRawLog"
                ParsedOutput poAvro = new ParsedOutput(new String(event.getBody().array(), Charset.forName("UTF-8")), "UnMatchedRawLog", new HashMap<CharSequence, CharSequence>());
                AvroValue<ParsedOutput> mapOutputValue = new AvroValue<ParsedOutput>(poAvro);
                String outputKey = this.getOutputKey(context, "UnMatchedRawLog");
                context.write(new Text(outputKey), mapOutputValue);
            }
        } catch (MorphlineRuntimeException me) {
            LOGGER.error("Parsing failed:", me);
            context.getCounter(LogCounters.TOTAL_MORPHLINE_EXCEPTIONS).increment(1);
        } catch (Exception ex) {
            LOGGER.info("Mapper failed:", ex);
            //LOGGER.info("raised by: "+ex.getMessage()+" caused by "+ex.getCause().getMessage(), ex);
            context.getCounter(LogCounters.TOTAL_OTHER_EXCEPTIONS).increment(1);
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
        //FileSplit fileSplit = (FileSplit)context.getInputSplit();
        //String hourStr = fileSplit.getPath().getParent().getName();
        //String dayStr = fileSplit.getPath().getParent().getParent().getName();
        //String monthStr = fileSplit.getPath().getParent().getParent().getParent().getName();
        //String yearStr = fileSplit.getPath().getParent().getParent().getParent().getParent().getName();
        String key = String.format("1970_01_01_00_%s", formatName);
        return key;
    }

}