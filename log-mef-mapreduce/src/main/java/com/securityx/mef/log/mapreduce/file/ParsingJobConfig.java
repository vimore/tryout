package com.securityx.mef.log.mapreduce.file;

import com.securityx.mef.log.mapreduce.LogParsingJobProperties;
import com.securityx.mef.log.mapreduce.ParsedOutputConverter;
import com.securityx.mef.log.mapreduce.parserutils.MefParser;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class ParsingJobConfig {

    private enum JobTimeFrame{
        HOURLY,
        DAYLY,
        NO_BUCKET
    }
    private JobTimeFrame inputTimeFrame;
    private JobTimeFrame outputTimeFrame;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String basedir;
    private String outBaseDir;
    private int nbRegion;
    public static final String HDFSRAWLOGBASEDIR = "/e8/dev/flume/livestream";
    public static final String HDFSBASEDIR = "/e8/dev/";
    private Set<String> formats = new HashSet<String>(ParsedOutputConverter.outputDir.values());

    public ParsingJobConfig(JobTimeFrame type, String baseDir, String year, String month, String day, String hour, String outBaseDir, String nbRegion){
        this.inputTimeFrame= type;
        this.outputTimeFrame = type;
        this.year = (null != year ? year : "*");
        this.month = (null != month ? StringUtils.leftPad(month, 2, "0") : "*");
        this.day = (null != day ? StringUtils.leftPad(day, 2, "0") : "*");
        this.hour = (null != hour ? StringUtils.leftPad(hour, 2, "0") : "*");
        this.basedir = (null != baseDir ? baseDir : HDFSRAWLOGBASEDIR);
        this.outBaseDir = (null != outBaseDir ? outBaseDir : HDFSBASEDIR);
        if (null != nbRegion){
            this.nbRegion = Integer.valueOf(nbRegion);
        } else {
            this.nbRegion = 3;
        }


    }

    public ParsingJobConfig(JobTimeFrame type, JobTimeFrame outType, String baseDir, String year, String month, String day, String hour, String outBaseDir, String nbRegion){
        this.inputTimeFrame= type;
        this.outputTimeFrame = outType;
        this.year = (null != year ? year : "*");
        this.month = (null != month ? StringUtils.leftPad(month, 2, "0") : "*");
        this.day = (null != day ? StringUtils.leftPad(day, 2, "0") : "*");
        this.hour = (null != hour ? StringUtils.leftPad(hour, 2, "0") : "*");
        this.basedir = (null != baseDir ? baseDir : HDFSRAWLOGBASEDIR);
        this.outBaseDir = (null != outBaseDir ? outBaseDir : HDFSBASEDIR);
        if (null != nbRegion){
            this.nbRegion = Integer.valueOf(nbRegion);
        }


    }

    public Set<String> getFormats() {
        return formats;
    }



    public void setOutputFormats(Configuration conf){
        //formats output cleanup - read from job properties
        String includedFormatStr = conf.get(LogParsingJobProperties.INCLUDEDOUTPUTFORMATS.getPropertyName());
        if (includedFormatStr != null){
            String[] formatsStr= includedFormatStr.split(",");
            formats.clear();
            for (String format : formatsStr){
                if (formats.contains(format)){
                    formats.add(format);
                }
            }
        }
        String excludedFormatStr = conf.get(LogParsingJobProperties.EXCLUDEDOUTPUTFORMATS.getPropertyName());
        if (excludedFormatStr != null){
            String[] formatsStr= excludedFormatStr.split(",");
            for (String format : formatsStr){
                if (formats.contains(format)){
                    formats.remove(format);
                }
            }
        }

    }

    public String getInputPaths(){
        String inputPaths="";
        if (this.inputTimeFrame == JobTimeFrame.NO_BUCKET){
            if (basedir.contains(",")){
                String[] paths= basedir.split(",");
                for (String path : paths){
                    inputPaths+=(inputPaths.length()>0?",":"")+ String.format("%s", path);
                }

            } else {
                inputPaths= String.format("%s", basedir);
            }
        }  else {
          if (basedir.contains(",")){
              String[] paths= basedir.split(",");
              for (String path : paths){
                  inputPaths+=(inputPaths.length()>0?",":"")+ String.format("%s/%s/*", path, getInputBuckets());
              }

          } else {
              inputPaths= String.format("%s/%s/*", basedir, getInputBuckets());
          }
        }
        return inputPaths;
    }

    public String getOutputPath(String format){
        String outputPath= String.format("%s/%s/%s", outBaseDir, format, getOutputBuckets());
        return outputPath;
    }

    private String getInputBuckets(){
        switch (this.inputTimeFrame){
            case HOURLY:
                return String.format("%s/%s/%s/%s", year, month, day, hour);
            case DAYLY:
                return String.format("%s/%s/%s", year, month, day);
            case NO_BUCKET:
                return "";
            default:
                return null;
        }
    }
    private String getOutputBuckets(){
        switch (this.outputTimeFrame){
            case HOURLY:
                return String.format("%s/%s/%s/%s", year, month, day, hour);
            case DAYLY:
                return String.format("%s/%s/%s", year, month, day);
            case NO_BUCKET:
                return "";
            default:
                return null;
        }
    }

    public void setInputTimeFrame(JobTimeFrame inputTimeFrame) {
        this.inputTimeFrame = inputTimeFrame;
    }

    public static ParsingJobConfig genJobConfigFromConf (Configuration conf) throws IOException, URISyntaxException {
        FileSystem fs  = FileSystem.get(conf);
        URI basedirURI = new URI(LogParsingJobProperties.get(conf, LogParsingJobProperties.BASEDIR).toString());

        ParsingJobConfig out;
        if (LogParsingJobProperties.hasHourlyPropsSet(conf) ){

                out =  genHourlyJobConfigFromConf(conf);
        }  else if ( LogParsingJobProperties.hasDailyPropsSet(conf)){
            out =  genDailyJobConfigFromConf(conf);
        }   else {
            out =  genNoBucketJobConfigFromConf(conf);
        }
        System.out.println("basedir uri : " +basedirURI.toString());
        System.out.println("basedir scheme : " +basedirURI.getScheme());
        if (basedirURI.getScheme() !=null  && "file".equals(basedirURI.getScheme())){
            File f = new File(basedirURI.getPath()) ;
            System.out.println("file "+ f.toString()+ (f.exists()?" exists":" does not exist"));
            if (f.exists())
                out.setInputTimeFrame(JobTimeFrame.NO_BUCKET);
        }
        out.setOutputFormats(conf);
        return out;
    }

    private static ParsingJobConfig genNoBucketJobConfigFromConf(Configuration conf) {
        return new ParsingJobConfig(JobTimeFrame.NO_BUCKET,
                LogParsingJobProperties.get(conf,LogParsingJobProperties.BASEDIR),
                null,
                null,
                null,
                null,
                LogParsingJobProperties.get(conf,LogParsingJobProperties.OUTPUT),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.NBREGION));
    }

    public static ParsingJobConfig genDailyJobConfigFromConf(Configuration conf){
        return new ParsingJobConfig(JobTimeFrame.DAYLY,
                LogParsingJobProperties.get(conf,LogParsingJobProperties.BASEDIR),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.YEAR),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.MONTH),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.DAY),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.HOUR),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.OUTPUT),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.NBREGION));
    }

    public static ParsingJobConfig genHourlyJobConfigFromConf(Configuration conf){
        return new ParsingJobConfig(JobTimeFrame.HOURLY,
                LogParsingJobProperties.get(conf,LogParsingJobProperties.BASEDIR),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.YEAR),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.MONTH),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.DAY),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.HOUR),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.OUTPUT),
                LogParsingJobProperties.get(conf,LogParsingJobProperties.NBREGION));
    }

}
