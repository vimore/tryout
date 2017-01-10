package com.securityx.mef.log.mapreduce.file;


import com.securityx.log.parsed.avro.ParsedOutput;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogFileParsingReducer extends Reducer<Text, AvroValue<ParsedOutput>, AvroKey<ParsedOutput>, NullWritable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileParsingReducer.class);

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    /**
     * @param key     ParsedoutputFormat based key. See mapper how the key is formed
     * @param values  All values ( avro records ) that match this key
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<AvroValue<ParsedOutput>> values, Context context) throws IOException, InterruptedException {
        LOGGER.info(" Storing parsed record for key {}", key.toString());
        for (AvroValue<ParsedOutput> avroValue : values) {
            //append to avro file container
            AvroKey<ParsedOutput> out = new AvroKey<ParsedOutput>(avroValue.datum());
            context.write(out, NullWritable.get());
        }
    }
}
