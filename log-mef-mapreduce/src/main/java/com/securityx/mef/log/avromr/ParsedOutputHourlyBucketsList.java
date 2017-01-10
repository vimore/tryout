package com.securityx.mef.log.avromr;

import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.mef.log.azkaban.ConfigUtils;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.securityx.mef.log.mapreduce.LogParsingJob.MORPHLINE_OUTPUT;

/**
 * A map reduce job processing some morphline_ouput data given as parameter and
 * listing the buckets list contained in it. Bucketing is define for each event
 * on startTime as yyyyMMddHH Output result file is dumper to output dir.
 * <p/>
 * Whens start time is missing, a defaultBucket is chosen base on the input dir.
 * For instance with the following command : hadoop jar
 * log-mef-mapreduce-jyr-1.0.0-SNAPSHOST.jar
 * com.securityx.mef.log.avromr.ParsedOutputHourlyBucketsList -b/e8/dev -y2014
 * -m4 -d5 -h16 the default bucket is 2014040516.
 *
 * @author jyrialhon
 */
public class ParsedOutputHourlyBucketsList extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParsedOutputHourlyBucketsList.class);
    /* TODO: this should be an external parameter as it is supposed to change */
    private static final String HDFS = "hdfs://hivecluster2";
    /* Job configuration keys */
    public static final String DEFAULT_BUCKET = "defaultBucket"; //
    public static final String OUTBASEDIR = "outBaseDir";
    public static final String BASEDIR = "baseDir";

    private final Pattern bucketPattern = Pattern.compile("\\/(\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d\\/\\d\\d)\\/");
    private final Properties props;

    public ParsedOutputHourlyBucketsList() {
        props = null;
        Configuration c = new Configuration();
        setConf(c);

    }

    public ParsedOutputHourlyBucketsList(String id, Properties props) {
        this.props = props;
        Configuration c = new Configuration();
        setConf(c);
    }

    /**
     * Job execution, called from azkaban. props set by public Constructor (String
     * id, Properties props)
     *
     * @return succeeded job : 0, failed job : 1
     * @throws Exception
     */
    public int run() throws Exception {

    /* Go through some trouble to subtract one day - to run on yesterday's data, today. */
        String yearStr = props.getProperty("year");
        String monthStr = props.getProperty("month");
        String dayStr = props.getProperty("day");
        String hourStr = props.getProperty("hour");
        String environmentStr = props.getProperty("environment");
        String filesystemStr = "hdfs://hivecluster2";
    /* TODO : make as ark when called brom azkaban (must be herited from context */
    /* String filesystemStr = props.getProperty("filesystem"); */

        Integer year = Integer.valueOf(yearStr);
        Integer month = Integer.valueOf(monthStr);
        Integer day = Integer.valueOf(dayStr);
        Integer hour = Integer.valueOf(hourStr);

        DateTime dt = new DateTime(year, month, day, hour, 0);
        //dt = dt.minusHours(1);
        Integer[] dateParts = ConfigUtils.getParts(dt);

        yearStr = year.toString();
        monthStr = StringUtils.leftPad(dateParts[1].toString(), 2, "0");
        dayStr = StringUtils.leftPad(dateParts[2].toString(), 2, "0");
        hourStr = StringUtils.leftPad(dateParts[3].toString(), 2, "0");

        String inputPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + MORPHLINE_OUTPUT + "/%s/%s/%s/%s/*", yearStr, monthStr, dayStr, hourStr);
        String outputPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + BUCKETSLIST + "/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);

        // Remove the existing output so we can overwrite it
//        FileSystem hdfs = FileSystem.get(conf);
//        Path oldOutputPath = new Path(String.format("hdfs://hivecluster2/securityx/morphline_output/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr));
//        hdfs.delete(oldOutputPath, true);
//        System.out.println("Deleted " + oldOutputPath);
        return doJob(inputPathStr, outputPathStr);
    }

    public static final String BUCKETSLIST = "bucketslist";

    /**
     * job execution from command line
     *
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: ParsedOutput2HourlyBucketsList <input path>  <out basedir>");
            return -1;
        }

        return doJob(args[0], args[1]);
    }

    /**
     * the core map reduce job definition
     *
     * @param inputPathStr
     * @param outputPathStr
     * @return
     * @throws Exception
     */
    private int doJob(String inputPathStr, String outputPathStr) throws Exception {
        getConf().set("mapred.reduce.child.java.opts", "-Xmx1024m");
        Job job = Job.getInstance(getConf());
        job.setJarByClass(ParsedOutputHourlyBucketsList.class);
        job.setJobName("Parsed output to hourly Buckets");
        Matcher matcher = bucketPattern.matcher(inputPathStr);
        if (matcher.find()) {
            LOGGER.info("default bucket : '" + matcher.group(1) + "'");
            job.getConfiguration().set(DEFAULT_BUCKET, matcher.group(1).replace("/", ""));
        } else {
            LOGGER.error("error unable to define default bucket in " + inputPathStr);
            LOGGER.error("failed pattern : " + bucketPattern.pattern());
            LOGGER.info("setting default bucket to : " + DEFAULT_BUCKET_VALUE);
            job.getConfiguration().set(DEFAULT_BUCKET, DEFAULT_BUCKET_VALUE);
        }

        FileInputFormat.setInputPaths(job, new Path(inputPathStr));
        FileInputFormat.setInputPathFilter(job, ParsedDataInputPathFilter.class);

        FileOutputFormat.setOutputPath(job, new Path(outputPathStr));

        job.setInputFormatClass(AvroKeyInputFormat.class);
        job.setMapperClass(BucketListMapper.class);
        AvroJob.setInputKeySchema(job, ParsedOutput.getClassSchema());
        job.setOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(1);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        job.setReducerClass(BucketReducer.class);

        return (job.waitForCompletion(true) ? 0 : 1);

    }

    public static final String DEFAULT_BUCKET_VALUE = "0000/00/00/00";

    /**
     * BlucketListMapper maps distinct buckets from a ParsedOutput event set
     */
    private static class BucketListMapper extends
            Mapper<AvroKey<ParsedOutput>, NullWritable, Text, NullWritable> {

        private static final Logger LOGGER = LoggerFactory.getLogger(BucketListMapper.class);
        private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH");
        private String defaultBucket;
        private final Set<String> buckets = new HashSet<String>();

        private int cpt = 0;

        @Override
        public void setup(Context c) throws IOException, InterruptedException {
            super.setup(c);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            defaultBucket = c.getConfiguration().get(DEFAULT_BUCKET);
            LOGGER.info("default bucket : '" + defaultBucket + "'");
        }

        @Override
        public void map(AvroKey<ParsedOutput> key, NullWritable value, Context context)
                throws IOException, InterruptedException {
            ParsedOutput po = key.datum();
            if (po.getValues().containsKey("startTime")) {
                String bucket;
                String startTime = (String) po.getValues().get("startTime");
                calendar.setTimeInMillis(Long.valueOf(startTime));
                bucket = dateFormat.format(calendar.getTime());
                if (cpt < 10) {
                    LOGGER.info("bucket : '" + bucket + "' for " + startTime);
                    cpt++;
                }
                if (!buckets.contains(bucket)) {
                    context.write(new Text(bucket), NullWritable.get());
                    buckets.add(bucket);
                }
            } else {
                if (!buckets.contains(defaultBucket)) {
                    context.write(new Text(defaultBucket), NullWritable.get());
                    buckets.add(defaultBucket);
                }
            }
        }

    }

    public static class BucketReducer extends
            Reducer<Text, NullWritable, Text, NullWritable> {

        @Override
        protected void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, NullWritable.get());
        }

    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new ParsedOutputHourlyBucketsList(), args);
        System.exit(res);
    }

}
