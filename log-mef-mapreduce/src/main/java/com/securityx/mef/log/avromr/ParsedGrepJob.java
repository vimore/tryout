package com.securityx.mef.log.avromr;

import com.securityx.mef.log.mapreduce.AvroToJsonUtil;
import com.securityx.mef.log.mapreduce.ParsedOutputConverter;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author jyrialhon
 */
public class ParsedGrepJob extends Configured implements Tool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsedGrepJob.class);
    public static final String PATTERN = "pattern";

    public static class LogCollectionHostMapper extends
            Mapper<AvroKey, NullWritable, IntWritable, AvroValue> {
        private Pattern query;

        @Override
        public void setup(Context c) throws IOException, InterruptedException {
            super.setup(c);
            LOGGER.info("Pattern : '" + c.getConfiguration().get(PATTERN) + "'");
            query = Pattern.compile(c.getConfiguration().get(PATTERN));
        }

        @Override
        public void map(AvroKey key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            Matcher m = this.query.matcher(AvroToJsonUtil.toAvroJsonString(key.datum(), ParsedOutputConverter.SCHEMA));
            if (m.find()) {
                context.write(new IntWritable(1), new AvroValue(key.datum()));
            }
        }
    }

    public static class LogCollectionHostReducer extends
            Reducer<IntWritable, AvroValue, AvroKey, NullWritable> {

        @Override
        public void reduce(IntWritable key, Iterable<AvroValue> values,
                           Context context) throws IOException, InterruptedException {
            for (AvroValue e : values) {
                context.write(new AvroKey(e.datum()), NullWritable.get());
            }

        }
    }

    public int run(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: GrepJob <regex> <input path> <output path>");
            return -1;
        }

        Job job = Job.getInstance(getConf());
        job.setJarByClass(ParsedGrepJob.class);
        job.setJobName("parsed data grep");

        Pattern query = Pattern.compile(args[0]);
        job.getConfiguration().set(PATTERN, args[0]);
        FileInputFormat.setInputPaths(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        FileInputFormat.setInputPathFilter(job, ParsedDataInputPathFilter.class);

        job.setInputFormatClass(AvroKeyInputFormat.class);
        job.setMapperClass(LogCollectionHostMapper.class);
        AvroJob.setInputKeySchema(job, ParsedOutputConverter.SCHEMA);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(AvroValue.class);
        AvroJob.setMapOutputValueSchema(job, ParsedOutputConverter.SCHEMA);

        job.setOutputFormatClass(AvroKeyOutputFormat.class);
        job.setReducerClass(LogCollectionHostReducer.class);
        AvroJob.setOutputKeySchema(job, ParsedOutputConverter.SCHEMA);

        return (job.waitForCompletion(true) ? 0 : 1);
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new ParsedGrepJob(), args);
        System.exit(res);
    }
}
