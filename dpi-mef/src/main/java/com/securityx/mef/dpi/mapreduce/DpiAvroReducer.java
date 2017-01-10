package com.securityx.mef.dpi.mapreduce;


import com.securityx.mef.schema.MasterEventFormat;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

//Not used
public class DpiAvroReducer extends Reducer<Text, AvroValue<MasterEventFormat>, AvroValue<MasterEventFormat>, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<AvroValue<MasterEventFormat>> values, Context context) throws IOException, InterruptedException {

        for(AvroValue<MasterEventFormat> avroValue : values) {
            MasterEventFormat mef = avroValue.datum();
            //append to avro file container
        }
    }
}
