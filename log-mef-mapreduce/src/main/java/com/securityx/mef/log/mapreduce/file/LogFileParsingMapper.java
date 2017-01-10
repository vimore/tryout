package com.securityx.mef.log.mapreduce.file;


import com.securityx.flume.log.avro.Event;
import com.securityx.logcollection.parser.utils.E8UUIDGenerator;
import com.securityx.mef.log.mapreduce.LogParsingJobProperties;
import com.securityx.mef.log.mapreduce.parserutils.MefParser;
import com.securityx.mef.log.mapreduce.parserutils.MefParserOutput;
import com.securityx.model.mef.field.api.SupportedFormats;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.specific.SpecificRecord;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.securityx.mef.log.mapreduce.file.LogFileParsingJob.LOGSOURCE;

/**
 * A mapper to parse log files. Reads an avro file and one avro record is passed to the map at a time.
 * After parsing, the morphlines output is stored in an avro record and send as input to the reducer
 */
public class LogFileParsingMapper extends Mapper<LongWritable, Text, AvroKey<CharSequence>, AvroValue<SpecificRecord>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileParsingMapper.class);
    /**
     * Input file name from which avro records are read
     */
    private String filename = "";

    private String morphlineConf = null;
    private String morphlineConfId = null;
    private String optLogSrouce = null;
    private MefParser parser = null;
    private List<SupportedFormats> supportedFormats;
    private Event event;

    private List<MefParserOutput> out;
    private String nbRegion;

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
        String morphlineId = context.getConfiguration().get(LogParsingJobProperties.MORPHLINEID.getPropertyName());
        String morphlineFile = context.getConfiguration().get(LogParsingJobProperties.MORPHLINEFILE.getPropertyName());
        if (null != morphlineFile && null != morphlineId) {
            this.morphlineConf = morphlineFile;
            this.morphlineConfId = morphlineId;
        }
        nbRegion = context.getConfiguration().get(LogParsingJobProperties.NBREGION.getPropertyName(), "3");

        try {
            //InputSplit is of FileSplit as we read records from an avro file
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            filename = fileSplit.getPath().getName();
            if (filename.startsWith("hdfs:")) {
                FileSystem fs = FileSystem.get(context.getConfiguration());
                FileStatus st = fs.getFileStatus(fileSplit.getPath());
                header.put("timestamp", String.valueOf(st.getModificationTime()));
            }else if (filename.startsWith("file:")){
                File f = new File(filename);
                header.put("timestamp", String.valueOf(f.lastModified()));
            }else{
                header.put("timestamp", String.valueOf(System.currentTimeMillis()));
            }

            //LOGGER.info("fileSplit.getPath() is " + fileSplit.getPath().toString());
            System.err.println("fileSplit.getPath() is " + fileSplit.getPath().toString());
        } catch (Exception ex) {
            LOGGER.error("Getting file name failed:", ex);
            context.getCounter(MefParser.LogCounters.TOTAL_FILENAME_FAILED).increment(1);

        }
        try {
            parser = new MefParser(context, morphlineConf, morphlineConfId, Integer.valueOf(nbRegion));
        } catch (Exception e) {
            LOGGER.error("failed to init parser:", e);
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
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        event.setBody(ByteBuffer.wrap(value.toString().getBytes()));
        event.getHeaders().put(WebProxyMefField.uuid.getPrettyName(),  getUuidGenerator().generateUUIfromRawLogOnly(value.toString()));
        out = parser.parse(event, context);
        if (out != null)
            for (MefParserOutput o : out) {
                context.write(new AvroKey<CharSequence>(o.getFormat()), new AvroValue<SpecificRecord>(o.getValue()));
            }

    }

    private  E8UUIDGenerator _uuidGenerator;

    public  E8UUIDGenerator getUuidGenerator(){
        if (_uuidGenerator == null){
            _uuidGenerator = new E8UUIDGenerator(Integer.valueOf(nbRegion)) ;
        }
        return  _uuidGenerator;
    }

}