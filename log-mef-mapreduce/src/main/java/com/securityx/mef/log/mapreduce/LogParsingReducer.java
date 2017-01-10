package com.securityx.mef.log.mapreduce;

import java.io.File;
import java.io.IOException;

import com.securityx.mef.log.mapreduce.parserutils.MefParser;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroMultipleOutputs;
import org.apache.avro.specific.SpecificRecord;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.securityx.model.mef.field.api.SupportedFormats;

public class LogParsingReducer
		extends Reducer<AvroKey<CharSequence>, AvroValue<SpecificRecord>, AvroKey<SpecificRecord>, NullWritable> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogParsingReducer.class);
	private AvroMultipleOutputs mos;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		mos = new AvroMultipleOutputs(context);
	}

	/**
	 * @param key
	 *            ParsedoutputFormat based key. See mapper how the key is formed
	 * @param values
	 *            All values ( avro records ) that match this key
	 * @param context
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	protected void reduce(AvroKey<CharSequence> key, Iterable<AvroValue<SpecificRecord>> values, Context context)
			throws IOException, InterruptedException {
		LOGGER.info(" Storing parsed record for key {}", key.toString());
		for (AvroValue<SpecificRecord> avroValue : values) {
			// append to avro file container
			String keyStr = key.datum().toString();
			AvroKey<SpecificRecord> out = new AvroKey<SpecificRecord>(avroValue.datum());
			String outputDir = keyStr;
			if (!keyStr.startsWith(MefParser.UNMATCHED_RAW_KEY)) {
				outputDir = ParsedOutputConverter.outputDir.get(SupportedFormats.valueOf(keyStr));
			}
			mos.write(keyStr, out, NullWritable.get(), outputDir + File.separator);
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		mos.close();
	}
}
