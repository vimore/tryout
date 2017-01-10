package com.securityx.mef.log.mapreduce;


import com.securityx.flume.log.avro.Event;
import com.securityx.mef.log.mapreduce.logutils.LogLimiter;
import com.securityx.mef.log.mapreduce.logutils.LogSampler;
import com.securityx.mef.log.mapreduce.parserutils.MefParser;
import com.securityx.mef.log.mapreduce.parserutils.MefParserOutput;
import com.securityx.model.mef.field.api.SupportedFormats;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.specific.SpecificRecord;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mapper to parse log files. Reads an avro file and one avro record is passed to the map at a time.
 * After parsing, the morphlines output is stored in an avro record and send as input to the reducer
 */
public class LogParsingMapper extends Mapper<AvroKey<Event>, NullWritable, AvroKey<CharSequence>, AvroValue<SpecificRecord>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParsingMapper.class);

    /**
     * Input file name from which avro records are read
     */
    private String filename = "";

    private String morphlineConf = "logcollection-parser-main.conf";
    private String morphlineConfId = "parsermain";

    private long totalParsingDuration = 0;
    private long nbParsingDuration = 0;

    private List<SupportedFormats> supportedFormats;
    private Event event;
    private String strValue;
    private Map<CharSequence, CharSequence> map = null;
    private AvroValue<SpecificRecord> mapOutputValue;

    private LogLimiter limiter;
    private LogSampler logSampler;
    private HashMap<String, Counter> formatCounters;
    private SimpleDateFormat formatter;
    private boolean doFormatPerSrcCounters;
    private MefParser parser;
    private List<MefParserOutput> out;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        super.setup(context);
        String morphlineId = context.getConfiguration().get(LogParsingJobProperties.MORPHLINEID.getPropertyName());
        String morphlineFile = context.getConfiguration().get(LogParsingJobProperties.MORPHLINEFILE.getPropertyName());
        if (null != morphlineFile && null != morphlineId) {
            this.morphlineConf = morphlineFile;
            this.morphlineConfId = morphlineId;
        }
        String nbRegion = context.getConfiguration().get(LogParsingJobProperties.NBREGION.getPropertyName(), "3");

        try {
            //InputSplit is of FileSplit as we read records from an avro file
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            filename = fileSplit.getPath().getName();
            //LOGGER.info("fileSplit.getPath() is " + fileSplit.getPath().toString());
            System.err.println("fileSplit.getPath() is " + fileSplit.getPath().toString());
            parser = new MefParser(context, morphlineConf, morphlineConfId, Integer.valueOf(nbRegion));
        } catch (Exception ex) {
            LOGGER.error("Getting file name failed:", ex);
            context.getCounter(MefParser.LogCounters.TOTAL_FILENAME_FAILED).increment(1);
        }


        System.err.println("setup completed ");
        System.err.flush();

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        parser.cleanup();
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
    protected void map(AvroKey<Event> key, NullWritable value, Context context) throws IOException, InterruptedException {
        event = key.datum();
        out = parser.parse(event, context);
        if (null != out)
            for (MefParserOutput o : out) {
                context.write(new AvroKey<CharSequence>(o.getFormat()), new AvroValue<SpecificRecord>(o.getValue()));
            }
    }




}