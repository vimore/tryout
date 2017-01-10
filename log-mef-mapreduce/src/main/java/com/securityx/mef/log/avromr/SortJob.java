package com.securityx.mef.log.avromr;

import com.securityx.flume.log.avro.Event;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public class SortJob extends Configured implements Tool {

    @Override
    public int run(String[] strings) throws Exception {
        Path inputPath = new Path(strings[0]);
        Path outputPath = new Path(strings[1]);

        Configuration conf = new Configuration();
        //conf.set(MORPHLINE_FILE, strings[2]);
        //conf.set(MORPHLINE_ID, strings[3]);
        conf.set("mapred.reduce.child.java.opts", "-Xmx1024m");

        Job job = Job.getInstance(conf, "Data capture per source");
        job.setJarByClass(getClass());

        FileInputFormat.setInputPaths(job, inputPath);
        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);

        job.setMapperClass(SortMapper.class);
        AvroJob.setMapOutputValueSchema(job, Event.SCHEMA$);
        job.setMapOutputKeyClass(DataCaptureHostSubset.class);

        job.setReducerClass(SortReducer.class);
        AvroJob.setOutputKeySchema(job, Event.SCHEMA$);

        job.setOutputFormatClass(AvroKeyOutputFormat.class);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setPartitionerClass(EventPartitioner.class);
        job.setGroupingComparatorClass(EventSubsetGroupingComparator.class);
        job.setSortComparatorClass(EventSubsetSortComparator.class);

        int r = job.waitForCompletion(true) ? 0 : 1;
        System.out.println("Job completed");
        return r;

    }

    public static class DataCaptureHostSubset implements WritableComparable<DataCaptureHostSubset> {

        private String dataCaptureHost;
        private long time;

        @Override
        public void readFields(DataInput in) throws IOException {
            this.dataCaptureHost = in.readUTF();
            this.time = in.readLong();
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(dataCaptureHost);
            out.writeLong(time);
        }

        @Override
        public int compareTo(DataCaptureHostSubset other) {
            int compare = this.dataCaptureHost.compareTo(other.dataCaptureHost);
            if (compare != 0) {
                return compare;
            }
            return new Long(time).compareTo(other.time);
        }

        public DataCaptureHostSubset setDataCaptureHost(String host) {
            this.dataCaptureHost = host;
            return this;
        }

        public DataCaptureHostSubset setTime(long time) {
            this.time = time;
            return this;
        }

        public String getDataCaptureHost() {
            return dataCaptureHost;
        }

        public long getTime() {
            return time;
        }
    }

    private static class SortMapper
            extends Mapper<AvroKey<Event>, NullWritable, DataCaptureHostSubset, AvroValue<Event>> {

        @Override
        protected void map(AvroKey<Event> key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            DataCaptureHostSubset subset = new DataCaptureHostSubset().setDataCaptureHost(
                    buildHost(key.datum())).setTime(buildTime(key.datum()));
            context.write(subset, new AvroValue<Event>(key.datum()));
        }

        private static String buildHost(Event datum) {
            CharSequence out = null;
            Map<CharSequence, CharSequence> header = datum.getHeaders();
            if (header.containsKey("host")) {
                out = header.get("host");
            } else {
                if (header.containsKey("hostname")) {
                    out = header.get("hostname");
                } else {
                    out = "MISSING";
                }
            }
            return out.toString();
        }

        private static long buildTime(Event datum) {
            long out = -1;
            Map<CharSequence, CharSequence> header = datum.getHeaders();
            if (header.containsKey("timestamp")) {
                try {
                    out = Long.parseLong((String) header.get("timestamp"), 10);
                } catch (NumberFormatException e) {

                }
            }
            return out;
        }
    }

    private static class SortReducer
            extends Reducer<DataCaptureHostSubset, AvroValue<Event>, AvroKey<Event>, NullWritable> {

        @Override
        protected void reduce(DataCaptureHostSubset key, Iterable<AvroValue<Event>> ignore, Context context)
                throws IOException, InterruptedException {
            for (AvroValue<Event> event : ignore) {
                context.write(new AvroKey<Event>(event.datum()), NullWritable.get());
            }
        }
    }

    public static class EventSubsetSortComparator extends WritableComparator {

        public EventSubsetSortComparator() {
            super(DataCaptureHostSubset.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            DataCaptureHostSubset p1 = (DataCaptureHostSubset) w1;
            DataCaptureHostSubset p2 = (DataCaptureHostSubset) w2;
            int cmp = p1.getDataCaptureHost().compareTo(p2.getDataCaptureHost());
            if (cmp != 0) {
                return cmp;
            }
            return new Long(p1.getTime()).compareTo(p2.getTime());
        }
    }

    public static class EventSubsetGroupingComparator extends WritableComparator {

        public EventSubsetGroupingComparator() {
            super(DataCaptureHostSubset.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            DataCaptureHostSubset p1 = (DataCaptureHostSubset) w1;
            DataCaptureHostSubset p2 = (DataCaptureHostSubset) w2;
            return p1.getDataCaptureHost().compareTo(p2.getDataCaptureHost());
        }
    }

    public static class EventPartitioner extends
            Partitioner<DataCaptureHostSubset, AvroValue<Event>> {

        @Override
        public int getPartition(DataCaptureHostSubset key, AvroValue<Event> value, int numPartitions) {
            return Math.abs(key.getDataCaptureHost().hashCode() * 127) % numPartitions;
        }
    }

    public static void main(String[] args) throws IOException, Exception {
        int out = ToolRunner.run(new Configuration(), new SortJob(), args);
        System.exit(out);
    }


}
