package com.securityx.mef.log.avromr;

import com.securityx.log.parsed.avro.ParsedOutput;
import com.securityx.mef.log.azkaban.ConfigUtils;
import com.securityx.mef.log.mapreduce.LogParsingJob;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroKeyOutputFormat;
import org.apache.avro.mapreduce.AvroMultipleOutputs;
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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ParserOutputHourlyBucketsDumper split some morphline_output into buckets
 * distribute morphline_output dir events into hourly bucket based on field startTime
 *
 * @author jyrialhon
 */
public class ParsedOutputHourlyBucketsDumper extends Configured implements Tool {

    public static final String DEFAULT_BUCKET = "defaultBucket";
    public static final String OUTBASEDIR = "outBaseDir";
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsedOutputHourlyBucketsDumper.class);
    private final Pattern bucketPattern = Pattern.compile("^(.*)\\/(\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d\\/\\d\\d)\\/");
    public static final String BUCKETSDIR = "morhline_buckets";
    private final Properties props;


    /**
     * default construct
     */
    public ParsedOutputHourlyBucketsDumper() {
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
    public ParsedOutputHourlyBucketsDumper(String id, Properties props) {
        this.props = props;
        Configuration c = new Configuration();
        setConf(c);
    }

    /**
     * job execution called by azkaban
     *
     * @return
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

        String inputPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + LogParsingJob.MORPHLINE_OUTPUT + "/%s/%s/%s/%s/*", yearStr, monthStr, dayStr, hourStr);
        String outputPathStr = String.format(filesystemStr + "/e8/" + environmentStr + "/" + BUCKETSDIR + "/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr);


        // Remove the existing output so we can overwrite it
//        FileSystem hdfs = FileSystem.get(conf);
//        Path oldOutputPath = new Path(String.format("hdfs://hivecluster2/securityx/morphline_output/%s/%s/%s/%s", yearStr, monthStr, dayStr, hourStr));
//        hdfs.delete(oldOutputPath, true);
//        System.out.println("Deleted " + oldOutputPath);
        return doJob(inputPathStr, outputPathStr);
    }

    /**
     * core map reduce job definition :
     * mapper : BucketMapper
     * - parse startTime to extract yyyy/MM/dd/HH, default ducket based on morphline_input dir
     * reducer : BucketReducer
     * - use AvroMultipleOutputs to dump ParsedOutput event into files according of their bucket
     *
     * @param inputDirStr
     * @param outputDirStr
     * @return
     * @throws Exception
     */
    private int doJob(String inputDirStr, String outputDirStr) throws Exception {
        getConf().set("mapred.reduce.child.java.opts", "-Xmx1024m");
        Job job = Job.getInstance(getConf());
        job.setJarByClass(ParsedOutputHourlyBucketsDumper.class);
        job.setJobName("Parsed output to hourly Buckets");
        Matcher matcher = bucketPattern.matcher(inputDirStr);
        if (matcher.find()) {
            LOGGER.info("default bucket : '" + matcher.group(2) + "'");
            job.getConfiguration().set(DEFAULT_BUCKET, matcher.group(2).replace("/", ""));
        } else {
            LOGGER.error("error unable to define default bucket in " + inputDirStr);
            LOGGER.error("failed pattern : " + bucketPattern.pattern());
            LOGGER.info("set default bucket to :'0000/00/00/00'");
            job.getConfiguration().set(DEFAULT_BUCKET, "0000/00/00/00");
        }

        job.getConfiguration().set(OUTBASEDIR, outputDirStr);
        FileInputFormat.setInputPaths(job, new Path(inputDirStr));
        FileInputFormat.setInputPathFilter(job, ParsedDataInputPathFilter.class);

        FileOutputFormat.setOutputPath(job, new Path(outputDirStr));

        job.setInputFormatClass(AvroKeyInputFormat.class);
        job.setMapperClass(ParsedOutputHourlyBucketsDumper.BucketMapper.class);
        AvroJob.setInputKeySchema(job, ParsedOutput.SCHEMA$);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(AvroKeyOutputFormat.class);
        AvroJob.setMapOutputValueSchema(job, ParsedOutput.SCHEMA$);

        job.setOutputFormatClass(AvroKeyOutputFormat.class);
        job.setReducerClass(BucketReducer.class);
        AvroJob.setOutputKeySchema(job, ParsedOutput.SCHEMA$);

        return (job.waitForCompletion(true) ? 0 : 1);

    }

    /**
     * job execution called by command line
     *
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: ParsedOutput2HourlyBuckets <input path>  <out basedir>");
            return -1;
        }
        return doJob(args[0], args[1]);
    }

    /**
     * Mapper : read ParsedOut event (as Avrokey).
     * 2 case :
     * - event contains startTime field : generate bucket using the millisec timestamp
     * - event does not contain startTime field : generate bucket base on morphline_output source dir
     */
    private static class BucketMapper extends
            Mapper<AvroKey<ParsedOutput>, NullWritable, Text, AvroValue<ParsedOutput>> {
        private static final Logger LOGGER = LoggerFactory.getLogger(BucketMapper.class);
        private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH");

        private String defaultBucket;
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
                //LOGGER.info("bucket : '" + bucket + "' for " + startTime);
                context.write(new Text(bucket), new AvroValue<ParsedOutput>(key.datum()));
            } else {
                context.write(new Text(defaultBucket), new AvroValue<ParsedOutput>(key.datum()));
            }
        }

    }

    /**
     * Reducer : use AvroMultipleOutput to write events to the right file
     * output :
     * OUTBASEDIR/yyyy/mm/dd/HH/part-*.avro
     */
    public static class BucketReducer extends
            Reducer<Text, AvroValue<ParsedOutput>, AvroKey<ParsedOutput>, NullWritable> {
        private AvroMultipleOutputs multipleOutputs;
        private String outBaseDir;

        @Override
        protected void setup(Context context) throws IOException,
                InterruptedException {
            multipleOutputs = new AvroMultipleOutputs(context);
            outBaseDir = context.getConfiguration().get(OUTBASEDIR);
        }

        @Override
        public void reduce(Text key, Iterable<AvroValue<ParsedOutput>> values,
                           Context context) throws IOException, InterruptedException {
            String path = outBaseDir + (!outBaseDir.endsWith("/") ? "/" : "") + key.toString() + "/part";
            for (AvroValue<ParsedOutput> e : values) {
                multipleOutputs.write(new AvroKey<ParsedOutput>(e.datum()), NullWritable.get(), path);
            }
        }

        @Override
        public void cleanup(Context context) throws IOException {
            try {
                multipleOutputs.close();
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(ParsedOutputHourlyBucketsDumper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run( new Configuration(),new ParsedOutputHourlyBucketsDumper(), args);
        System.exit(res);
    }

}
