package com.securityx.mef.dpi.mapreduce;


import com.securityx.mef.dpi.DpiToMef;
import com.securityx.mef.schema.DpiApplicationType;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class DpiPcapjsonReducer extends Reducer<RequestResponseWritable, IntWritable, NullWritable, NullWritable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DpiPcapjsonReducer.class);

    private DpiToMef dpiToMef = null;

    enum DpiRecordProcess {
        PCAP_REDUCE_PROCESSING_SUCCESS, PCAP_REDUCE_PROCESSING_FAILED
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        dpiToMef  = new DpiToMef();
    }

    @Override
    protected void reduce(RequestResponseWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        //We use reduce input key only
        LOGGER.info(" Reduce key values are {}, {}, {}, {}, {}", new Object[] { key.getCustomerId(),
                key.getAppProtocol(),key.getTimeStamp(), key.getSessionId(),key.getReqResId()});

        try {
            dpiToMef.mergeRequestResponse(key.getCustomerId(), key.getTimeStamp(),
                    key.getSessionId(),key.getReqResId(), DpiApplicationType.valueOf(key.getAppProtocol()));
            context.getCounter(DpiRecordProcess.PCAP_REDUCE_PROCESSING_SUCCESS).increment(1);
        }
        catch (Exception e) {
            context.getCounter(DpiRecordProcess.PCAP_REDUCE_PROCESSING_FAILED).increment(1);
            LOGGER.error("Reduce failed", e);
        }
    }
}
