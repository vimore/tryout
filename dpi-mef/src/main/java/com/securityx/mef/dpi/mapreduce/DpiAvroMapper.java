package com.securityx.mef.dpi.mapreduce;

import com.securityx.mef.dpi.DpiToMef;
import com.securityx.mef.dpi.avro.Event;
import org.apache.avro.mapred.AvroKey;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * A mapper to extract dpi events (records) from an avro file and store the mef record in hbase
 */
public class DpiAvroMapper extends Mapper<AvroKey<Event>, NullWritable, NullWritable,NullWritable> {

    private static Logger logger = Logger.getLogger(DpiAvroMapper.class);

    private DpiToMef dpiToMef = null;

    enum DpiRecordProcess {
        PROCESSING_SUCCESS, PROCESSING_FAILED
    }

    private String filename = "";

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        dpiToMef  = new DpiToMef();

        try {
            //InputSplit is of FileSplit as we read records from an avro file
            FileSplit fileSplit = (FileSplit)context.getInputSplit();
            filename = fileSplit.getPath().getName();
        }
        catch (Exception ex) {
            logger.error("Getting file name failed:", ex);
        }
    }

    /**
     * Map is called for every record repeatedly found in a single avro file ( or records found in an inputsplit)
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(AvroKey<Event> key, NullWritable value, Context context) throws IOException, InterruptedException {
        logger.debug(" ***********  DpiAvroMapper  map is called *********** ");
        Event event = key.datum();

        try {
            //For now we use this customerId for all data loaded through flume. At some point we need to include
            //the customerId in the flume record headers
            String customerId = context.getConfiguration().get(DpiLoadJob.CUSTOMER_ID);
            dpiToMef.saveDpiEvent(event,customerId,filename);
            context.getCounter(DpiRecordProcess.PROCESSING_SUCCESS).increment(1);
        }
        catch (Exception e) {
            context.getCounter(DpiRecordProcess.PROCESSING_FAILED).increment(1);
            logger.error("Error in saving record from the file " + filename);
            logger.error("Saving failed reason : ", e );
            //logger.error(dpiToMef.getBodyContent(event));
            logger.error("Failed record is \n" + event.toString());
        }
    }

    @Override
    public void run(Context context) throws IOException, InterruptedException {
        super.run(context);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
    }
}

//public class DpiAvroMapper extends Mapper<AvroKey<Event>, NullWritable, Text,
//        AvroValue<MasterEventFormat>> {
