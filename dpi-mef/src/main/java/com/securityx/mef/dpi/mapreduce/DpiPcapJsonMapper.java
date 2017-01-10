package com.securityx.mef.dpi.mapreduce;


import com.securityx.mef.dpi.DpiToMef;
import com.securityx.mef.schema.Dpi;
import com.securityx.mef.schema.DpiCommon;
import com.securityx.mef.schema.DpiRecordStatus;
import com.securityx.mef.schema.MasterEventFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class DpiPcapJsonMapper extends Mapper<Object, Text, RequestResponseWritable, IntWritable>  {
    private static Logger logger = Logger.getLogger(DpiPcapJsonMapper.class);

    private DpiToMef dpiToMef = null;

    enum DpiRecordProcess {
        PCAP_MAP_PROCESSING_SUCCESS, PCAP_MAP_PROCESSING_FAILED
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

    @Override

    public void map(Object key, Text value, Context context ) throws IOException, InterruptedException {
        String jsonLine = value.toString();

        try {
            String customerId = context.getConfiguration().get(DpiLoadJob.CUSTOMER_ID);
            List<MasterEventFormat> mefs = dpiToMef.saveDpiEventNew(jsonLine,customerId,filename);
            if (mefs != null) {
                for(MasterEventFormat mef: mefs) {
                    Dpi dpi = (Dpi)mef.getEvent();
                    DpiCommon dpiCommon = dpi.getDpiCommon();
                    if (dpi.getRecordStatus() == DpiRecordStatus.TRANSIENT ) {
                        logger.info("Session id is  " + dpiCommon.getSessionId());
                        RequestResponseWritable reqResWritable =
                                new RequestResponseWritable(mef.getCustomerId().toString(),
                                        dpiCommon.getApplicationProtocol().toString(), dpiCommon.getTimestamp(),
                                        dpiCommon.getSessionId(),dpi.getRequestResponseId())  ;

                        context.write(reqResWritable,new IntWritable(1));

                    }
                }
                context.getCounter(DpiRecordProcess.PCAP_MAP_PROCESSING_SUCCESS).increment(1);
            }
        }
        catch (Exception e) {
            context.getCounter(DpiRecordProcess.PCAP_MAP_PROCESSING_FAILED).increment(1);
            logger.error("Saving failed : ", e );
            logger.error(jsonLine);
        }
    }
}
