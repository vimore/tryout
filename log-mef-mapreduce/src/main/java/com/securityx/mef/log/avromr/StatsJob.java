package com.securityx.mef.log.avromr;


import com.securityx.flume.log.avro.Event;
import com.securityx.logcollection.parser.avro.EventToRecordConverter;
import com.securityx.model.mef.field.api.WebProxyMefField;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.kitesdk.morphline.api.Record;

import java.io.IOException;

/**
 * @author jyrialhon
 */
public class StatsJob extends Configured implements Tool {

    public static class LogCollectionHostMapper extends
            Mapper<AvroKey<Event>, NullWritable, Text, IntWritable> {

        private EventToRecordConverter eventConverter;
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            this.eventConverter = new EventToRecordConverter(3);
        }

        private String getLogCollectionSources(Event e) {
            String out = "undef-undef";
            Record in = eventConverter.readOnlyAvroRecord(e);
            if (in.getFields().keySet().contains(WebProxyMefField.logCollectionHost.getPrettyName())) {
                out = (String) in.getFirstValue(WebProxyMefField.logCollectionHost.getPrettyName());
            }
            if (in.getFields().keySet().contains(WebProxyMefField.logCollectionCategory.getPrettyName())) {
                out += "-" + (String) in.getFirstValue(WebProxyMefField.logCollectionCategory.getPrettyName());
            }
            return out;
        }

        @Override
        public void map(AvroKey<Event> key, NullWritable value, Context context)
                throws IOException, InterruptedException {

            CharSequence logCollectionHost = this.getLogCollectionSources(key.datum());
            context.write(new Text(logCollectionHost.toString()), new IntWritable(1));
        }
    }

    @Deprecated
    public static class LogCollectionHostReducer extends
            Reducer<Text, IntWritable, AvroKey<CharSequence>, AvroValue<Integer>> {

        @Override
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            context.write(new AvroKey<CharSequence>(key.toString()), new AvroValue<Integer>(sum));
        }
    }

    public static class LogCollectionHostReducerV2 extends
            Reducer<Text, IntWritable, Text, IntWritable> {

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            context.write(new Text(key.toString()), new IntWritable(sum));

        }

    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: StatsJob <input path> <output path>");
            return -1;
        }

        Job job = Job.getInstance(getConf());
        job.setJarByClass(StatsJob.class);
        job.setJobName("Evt per logCollection host");

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileInputFormat.setInputPathFilter(job, InputPathFilter.class);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setInputFormatClass(AvroKeyInputFormat.class);
        job.setMapperClass(LogCollectionHostMapper.class);
        AvroJob.setInputKeySchema(job, Event.getClassSchema());
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setReducerClass(LogCollectionHostReducerV2.class);

        return (job.waitForCompletion(true) ? 0 : 1);
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new StatsJob(), args);
        System.exit(res);
    }
}
