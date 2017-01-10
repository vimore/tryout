package com.securityx.mef.log.avromr;

import com.securityx.flume.log.avro.Event;
import com.securityx.mef.log.azkaban.ConfigUtils;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.avro.mapreduce.AvroMultipleOutputs;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapred.JobPriority;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.securityx.mef.log.mapreduce.LogParsingJob.FLUMELIVESTREAM;

/**
 * MergeJob takes a bunch of Flume Event and merge them by hostname and category
 * Works with an hourly rawlog directory as input (for instance
 * /e8/dev/flume/livestream/2014/07/23/12) merge files based on event hostname
 * and category
 *
 * @author jyria <jean-yves@e8security.com>
 */
public class MergeJob extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeJob.class);
    public static final String OUTBASEDIR = "outBaseDir";
    private Properties props;

    /**
     * default construct
     */
    public MergeJob() {
        this.props = null;
        Configuration c = new Configuration();
        setConf(c);
    }

    /**
     * Constructor used by azkaban job
     *
     * @param id
     * @param props
     */
    public MergeJob(String id, Properties props) {
        this.props = props;
        Configuration c = new Configuration();
        setConf(c);
    }

    /**
     * job execution call for azkaban
     *
     * @return job exec result (0 success, 1 failure)
     * @throws Exception
     */
    public int run() throws Exception {
        DateTime dt = ConfigUtils.getHourlyBucketFromJobProps(props);
        String environmentStr = props.getProperty("environment");
        String filesystemStr = "hdfs://hivecluster2";
    /* TODO : make as ark when called from azkaban (must be herited from context */
    /* String filesystemStr = props.getProperty("filesystem"); */

        //dt = dt.minusHours(1);
        Integer[] dateParts = ConfigUtils.getParts(dt);

        String yearStr = StringUtils.leftPad(dateParts[0].toString(), 4, "0");
        String monthStr = StringUtils.leftPad(dateParts[1].toString(), 2, "0");
        String dayStr = StringUtils.leftPad(dateParts[2].toString(), 2, "0");
        String hourStr = StringUtils.leftPad(dateParts[3].toString(), 2, "0");

        String inputPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + FLUMELIVESTREAM + "/%s/%s/%s/%s/*", yearStr, monthStr, dayStr, hourStr);

        //call doJob wih delete=true to force file delete after merge
        return doJob(inputPathStr, true);

    }

    private int doJob(String inputDirStr, boolean delete) throws Exception {
        String outputDirStr = "/tmp/mergejob." + System.currentTimeMillis();
        LOGGER.info("tmp dir: " + outputDirStr);
        getConf().set("mapred.reduce.child.java.opts", "-Xmx1024m");
        getConf().set("mapred.job.priority", JobPriority.VERY_LOW.toString());
        Job job = Job.getInstance(getConf());
        job.setJarByClass(MergeJob.class);
        job.setJobName("Raw data merge per hostname and Category job");
        String mergeOutput = outputDirStr + (!outputDirStr.endsWith("/") ? "/" : "") + "merged";
        job.getConfiguration().set(OUTBASEDIR, mergeOutput);
        FileInputFormat.setInputPaths(job, new Path(inputDirStr));
        FileInputFormat.setInputPathFilter(job, InputPathFilter.class);

        FileOutputFormat.setOutputPath(job, new Path(outputDirStr));

        job.setInputFormatClass(AvroKeyInputFormat.class);
        AvroJob.setInputKeySchema(job, Event.SCHEMA$);

        job.setMapperClass(MergeMapper.class);
        AvroJob.setMapOutputValueSchema(job, Event.SCHEMA$);
        job.setMapOutputKeyClass(EventHostnameCategorySubset.class);

        job.setNumReduceTasks(1);

        job.setReducerClass(MergeReducer.class);
        job.setOutputFormatClass(AvroKeyOutputFormat.class);
        AvroJob.setOutputKeySchema(job, Event.SCHEMA$);

        job.setPartitionerClass(EventPartitioner.class);
        job.setGroupingComparatorClass(EventHostnameCategoryGroupingComparator.class);
        job.setSortComparatorClass(EventHostnameCategorySortComparator.class);

        int res = job.waitForCompletion(true) ? 0 : 1;

        if (res == 0) {
            //success
            Path in = new Path(inputDirStr);
            Path out = new Path(outputDirStr);
            FileSystem fs = FileSystem.get(getConf());

            Path parent = in.getParent();

            FileStatus[] srcFiles = fs.listStatus(parent, new InputPathFilter());
            LOGGER.info("marking for delete " + srcFiles.length + " source files in " + parent.toString());
            for (int i = 0; i < srcFiles.length; i++) {
                fs.rename(srcFiles[i].getPath(), new Path(srcFiles[i].getPath().toString() + ".to_be_deleted"));
            }

            FileStatus[] mergedFiles = fs.listStatus(new Path(mergeOutput));
            LOGGER.info("moving " + mergedFiles.length + " merged files to " + parent.toString());
            for (int i = 0; i < mergedFiles.length; i++) {
                String name = mergedFiles[i].getPath().getName();

                fs.rename(mergedFiles[i].getPath(), new Path(parent, name));
            }

            if (delete) {
                LOGGER.info("deleting files...");
                FileStatus[] toBeDeleted = fs.listStatus(parent, new ToBeDeletedPathFilter());
                for (int i = 0; i < toBeDeleted.length; i++) {
                    fs.delete(toBeDeleted[i].getPath(), true);
                }
            }

            LOGGER.info("clear tmp dir...");
            Path tmpdir = new Path(outputDirStr);
            fs.delete(tmpdir, true);

        }
        return res;
    }

    @Override
    public int run(String[] strings) throws Exception {
        if (strings.length < 1) {
            System.out.println("Usage: MergeJob <input dir> [delete]");
            System.out.println("\t merge Rawlog input into ");

            System.exit(-1);
        }
        boolean delete = false;
        if (strings.length >= 2 && strings[1].equalsIgnoreCase("delete")) {
            delete = true;
        }

        return doJob(strings[0], delete);

    }

    public static class EventHostnameCategorySubset implements WritableComparable<EventHostnameCategorySubset> {

        private String dataCaptureHost;
        private String category;
        private long time;

        @Override
        public void readFields(DataInput in) throws IOException {
            this.dataCaptureHost = in.readUTF();
            this.category = in.readUTF();
            this.time = in.readLong();
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(dataCaptureHost);
            out.writeUTF(category);
            out.writeLong(time);
        }

        @Override
        public int compareTo(EventHostnameCategorySubset other) {
            int compare = this.dataCaptureHost.compareTo(other.dataCaptureHost);
            if (compare != 0) {
                return compare;
            }
            compare = this.category.compareTo(other.category);
            if (compare != 0) {
                return compare;
            }
            return new Long(this.time).compareTo(other.getTime());
        }

        public EventHostnameCategorySubset setDataCaptureHost(String host) {
            this.dataCaptureHost = host;
            return this;
        }

        public EventHostnameCategorySubset setCategory(String category) {
            this.category = category;
            return this;
        }

        public EventHostnameCategorySubset setTime(Long time) {
            this.time = time;
            return this;
        }

        public String getDataCaptureHost() {
            return dataCaptureHost;
        }

        public String getCategory() {
            return category;
        }

        public long getTime() {
            return time;
        }
    }

    private static class MergeMapper
            extends Mapper<AvroKey<Event>, NullWritable, EventHostnameCategorySubset, AvroValue<Event>> {

        @Override
        protected void map(AvroKey<Event> key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            EventHostnameCategorySubset subset = new EventHostnameCategorySubset()
                    .setDataCaptureHost(buildHost(key.datum()))
                    .setCategory(buildCategory(key.datum()))
                    .setTime(buildTime(key.datum()));
            context.write(subset, new AvroValue<Event>(key.datum()));
        }

        private static String buildHost(Event datum) {
            CharSequence out = null;
            Map<CharSequence, CharSequence> header = datum.getHeaders();
            if (header.containsKey("hostname")) {
                out = header.get("hostname");
            } else {
                out = "MISSING";
            }

            return out.toString();
        }

        private static String buildCategory(Event datum) {
            String out = "MISSING";
            Map<CharSequence, CharSequence> header = datum.getHeaders();
            if (header.containsKey("category")) {
                out = (String) header.get("category");
            }
            return out;
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

    private static class MergeReducer
            extends Reducer<EventHostnameCategorySubset, AvroValue<Event>, AvroKey<Event>, NullWritable> {

        private AvroMultipleOutputs multipleOutputs;
        private String outBaseDir;

        @Override
        protected void reduce(EventHostnameCategorySubset key, Iterable<AvroValue<Event>> ignore, Context context)
                throws IOException, InterruptedException {
            String path = outBaseDir + (!outBaseDir.endsWith("/") ? "/" : "")
                    + key.getDataCaptureHost() + "."
                    + key.getCategory();
            for (AvroValue<Event> event : ignore) {
                multipleOutputs.write(new AvroKey<Event>(event.datum()), NullWritable.get(), path);
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            try {
                multipleOutputs.close();
            } catch (InterruptedException ex) {
                throw ex;
            }
            super.cleanup(context);
        }

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            multipleOutputs = new AvroMultipleOutputs(context);
            outBaseDir = context.getConfiguration().get(OUTBASEDIR);
        }
    }

    public static class EventHostnameCategorySortComparator extends WritableComparator {

        public EventHostnameCategorySortComparator() {
            super(EventHostnameCategorySubset.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            EventHostnameCategorySubset p1 = (EventHostnameCategorySubset) w1;
            EventHostnameCategorySubset p2 = (EventHostnameCategorySubset) w2;
            int cmp = p1.getDataCaptureHost().compareTo(p2.getDataCaptureHost());
            if (cmp != 0) {
                return cmp;
            }
            cmp = p1.getCategory().compareTo(p2.getCategory());
            if (cmp != 0) {
                return cmp;
            }
            return new Long(p1.getTime()).compareTo(p2.getTime());
        }
    }

    public static class EventHostnameCategoryGroupingComparator extends WritableComparator {

        public EventHostnameCategoryGroupingComparator() {
            super(EventHostnameCategorySubset.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            EventHostnameCategorySubset p1 = (EventHostnameCategorySubset) w1;
            EventHostnameCategorySubset p2 = (EventHostnameCategorySubset) w2;
            return p1.getDataCaptureHost().compareTo(p2.getDataCaptureHost());
        }
    }

    public static class EventPartitioner extends
            Partitioner<EventHostnameCategorySubset, AvroValue<Event>> {

        @Override
        public int getPartition(EventHostnameCategorySubset key, AvroValue<Event> value, int numPartitions) {
            return Math.abs(key.getDataCaptureHost().hashCode() * 127) % numPartitions;
        }
    }

    public static void main(String[] args) throws IOException, Exception {
        int out = ToolRunner.run(new Configuration(), new MergeJob(), args);
        System.exit(out);
    }

}
