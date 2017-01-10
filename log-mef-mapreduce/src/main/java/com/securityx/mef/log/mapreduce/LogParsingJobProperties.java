package com.securityx.mef.log.mapreduce;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * LogParsingJob properties
 */
public enum LogParsingJobProperties {
    /**
     * Year part of the flume dir - yyyy
     */
    YEAR("com.e8sec.logparsingjob.year", "y", "year", true, "year part of flume dir - yyyy", true),
    /**
     * Month part of the flume dir - mm
     */
    MONTH("com.e8sec.logparsingjob.month","m", "month", true, "Month part of flume dir", true),
    /**
     * Day part of the flume dir - dd
     */
    DAY("com.e8sec.logparsingjob.day","d", "day", true, "Day part of flume dir", true),
    /**
     * Hour part of the flume dir - dd, if ommitted replaced by wildcard *
     */
    HOUR("com.e8sec.logparsingjob.hour","h", "hour", true, "hour part of flume dir", false),
    /**
     * Output base directory - year, month, day, hour dir structure is appended
     */
    OUTPUT("com.e8sec.logparsingjob.output", "o", "output", true, "Output dir", true),
    /**
     * Basedir location - path to the flume livestream base dir
     */
    BASEDIR("com.e8sec.logparsingjob.basedir", "b", "basedir", true, "base directory", true),
    /**
     * name node of the cluster
     */
    //CLUSTER("com.e8sec.cluster", "c", "cluster", true, "Cluster hostname", true),
    /**
     * if set, enable some debug traces
     */
    DEBUG("com.e8sec.logparsingJob.debug", "debug", "debug", false, "enable debug level of logging", false),
    /**
     * if set, force the cleanup of the output directory content before job execution
     */
    DOPURGEDST("com.e8sec.logparsingjob.pruge", "purgeDestDir", "purgeDestDir", false, "enable the deletion of dest dir if it exists", false),
    /**
     * location of uploadstream dir
     */
    UPLOADSTREAMDIR("com.e8sec.fileupload.jobstream","u", "uploadstreamdir", true, "an uploadstream dir", true),
    MORPHLINEFILE("com.e8sec.filelogparsingjob.morphlinefile", "f", "file", true, "morphline file", false),
    MORPHLINEID("com.e8sec.filelogparsingjob.morphlineid", "i", "id", true, "morphline id", false),
    NBREGION("com.e8sec.filelogparsingjob.nbregion", "r", "region", true, "number of region in the storage system. for uuid processing.", false),
    SPLITABLE("com.e8sec.filelogparsing.split", "split", "splitable", false, "allow file input file (only relevant for files without a headers on top of it", false),
    LOGSOURCE("com.e8sec.filelogparsing.logsource", "src", "logSource", true, "log source name",false),
    INPUTFILE("com.e8sec.filelogparsing.input", "inputfile", "input-file", true, "URI of the input file to be processed", false),
    PROPERTIESFILES("com.e8sec.logparsing.properties", "prop", "properties", true, "name of the properties file", false),
    LOGLIMITERMAXLOG("com.e8sec.logparsingjob.maxlog", "maxlog", "loglimiter-maxlog", true, "max number of logging events reported", false) ,
    LOGSAMPLEMINMD5("com.e8sec.logparsingjob.sampling.min", "min", "logsampler-min", true, "min threashold to log sample", false),
    LOGSAMPLEMAXMD5("com.e8sec.logparsingjob.sampling.max", "max", "logsampler-max", true, "max threashold to log sample", false),
    LOGSAMPLEFIELDSAMPLER("com.e8sec.logparsingjob.fieldsampler", "fieldsampler", "logsampler-fieldsapmpler", true, "field sampling string : field:regex", false),
    HEALTHSERVER("com.e8sec.logparsingjob.healthserver", "health", "health-server", true, "uri of health server (for instance http://e8:9096/api/track/logparser)", false),
    GRAPHITESERVER("com.e8sec.logparsingjob.graphiteserver", "graphite", "graphite-server", true, "ip and port of a graphite server", false),
    GRAPHITEREALM("com.e8sec.logparsingjob.graphiterealm", "realm", "graphite-realm", true, "prefix of the graphite counter", false),
    GRAPHITEREALMOVER("com.e8sec.logparsingjob.graphiterealm.overwrite", "realmo", "graphite-realm-overwrite", true, "for overwritte with this prefix of the graphite counter", false),
    FORMATPERSOURCECOUNTER("com.e8sec.logparsingjob.counter.format-per-source", "c-mefpersrc", "counter-mef-per-source", true, "to enable extended counter counting format per sources.", false),
    INCLUDEDOUTPUTFORMATS("com.e8sec.logparsingjob.includedOutputFormat", "incOutfmts", "included-output-formats", true, "Mef format list of accepted output formats (separated by ','", false),
    EXCLUDEDOUTPUTFORMATS("com.e8sec.logparsingjob.excludedOutputFormat", "excOutfmts", "excluded-output-formats", true, "Mef format list of rejected output formats (separated by ','", false);


    private String propertyName;
    private String shortOpt;
    private String longOpt;
    private boolean hasArg;
    private String description;
    private boolean isRequired;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogParsingJobProperties.class);


    public String getPropertyName() {
        return propertyName;
    }

    public String getShortOpt() {
        return shortOpt;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public boolean hasArg() {
        return hasArg;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return isRequired;
    }

    LogParsingJobProperties(String propertyName, String shortOpt, String longOpt,
                            boolean hasArg, String description, boolean isRequired){
        this.description = description;
        this.propertyName = propertyName;
        this.shortOpt = shortOpt;
        this.longOpt = longOpt;
        this.hasArg = hasArg;
        this.isRequired = isRequired;

    }

    /**
     *  return the properties expecting to be set for LogParsingJob
     *
     */
    public static EnumSet<LogParsingJobProperties> logParsingJobProperties(){
        return  EnumSet.of(YEAR, MONTH, DAY, HOUR,
                BASEDIR, OUTPUT,
                DEBUG, DOPURGEDST,  FORMATPERSOURCECOUNTER,
                GRAPHITESERVER, GRAPHITEREALM, GRAPHITEREALMOVER, NBREGION, HEALTHSERVER, INCLUDEDOUTPUTFORMATS, EXCLUDEDOUTPUTFORMATS);
    }

    public static EnumSet<LogParsingJobProperties> logGzFileParsingJobProperties(){
        return  EnumSet.of( BASEDIR, OUTPUT,
                DEBUG, DOPURGEDST, NBREGION) ;
    }

    /**
     *  return the properties expecting to be set for LogParsingJob
     *
     */
    public static EnumSet<LogParsingJobProperties> fileUploadRelocationJobProperties(){
        return  EnumSet.of(YEAR, MONTH, DAY, HOUR,
                BASEDIR, OUTPUT,
                UPLOADSTREAMDIR, NBREGION);
    }

    /**
     *  return the properties expecting to be set for LogFileParsingJob
     *
     */
    public static EnumSet<LogParsingJobProperties> logFileParsingJobProperties(){
        return  EnumSet.of(YEAR, MONTH, DAY, HOUR,
                BASEDIR, OUTPUT, INPUTFILE,
                MORPHLINEFILE, MORPHLINEID, NBREGION);
    }



    /**
     * generate the list of -Dproperties params
     * @return the list of properties contained into this enum.
     */
    public static List<String> getProperties(){
        ArrayList<String> props = new ArrayList<String>();
        for (LogParsingJobProperties p : LogParsingJobProperties.values()){
            props.add(p.getPropertyName());
        }
        return props;
    }

    /**
     * generate the org.apache.commons.cli.Option  object for a param
     * @return an Option
     */
    public Option getOption(){
        Option opt = new Option(this.getShortOpt(), this.getLongOpt(),
        this.hasArg(), this.getDescription());
        opt.setRequired(this.isRequired);
        opt.setType(String.class);
        return opt;
    }

    public static Options getOptionsLogParsingJob(){
        return getOptions(LogParsingJobProperties.logParsingJobProperties());
    }

    public static Options getOptionsLogFileParsingJob(){
        return getOptions(LogParsingJobProperties.logFileParsingJobProperties());
    }

    public static Options getOptionsFileUploadRelocationJob(){
        return getOptions(LogParsingJobProperties.fileUploadRelocationJobProperties());
    }

    /**
     * generate the Options objected describing parameters
     * @return an Options
     */
    private static Options getOptions(EnumSet<LogParsingJobProperties> props){
        Options options = new Options();
        for (LogParsingJobProperties p : props){
            options.addOption(p.getOption());
        }
        return options;
    }

    public static String usageLogParsingJob(){
        return usage(LogParsingJobProperties.logParsingJobProperties());
    }

    public static String usageFileUploadRelocationJob(){
        return usage(LogParsingJobProperties.fileUploadRelocationJobProperties());
    }

    public static String usageLogFileParsingJob(){
        return usage(LogParsingJobProperties.logFileParsingJobProperties());
    }


    /**
     * display usage description for the class
     * @return usage as a String
     */
    private static String usage(EnumSet<LogParsingJobProperties> props){
        String out="LogParsingJobProperties:\n";

        for (LogParsingJobProperties p : props){
            out+=String.format("\t%s: %s (%s)\n", p.getPropertyName(), p.getDescription(), (p.isRequired?"required":"optionnal"));
        }
        return out;
    }

    /**
     * Validate LogParsingJobProperties
     * @param conf
     * @return true if all properties are found, false otherwise
     *
     */
    public static boolean validateLogParsingJob(Configuration conf){
        return validate(conf, LogParsingJobProperties.logParsingJobProperties());
    }

    /**
     * validate validateLogGzFileParsingJob
     * @param conf
     * @return
     */
    public static boolean validateLogGzFileParsingJob(Configuration conf){
        return validate(conf, LogParsingJobProperties.logGzFileParsingJobProperties());
    }

    /**
     * Validate FileUploadRelocationJobProperties
     * @param conf
     * @return true if all properties are found, false otherwise
     *
     */
    public static boolean validateFileUploadRelocationJob(Configuration conf){
        return validate(conf, LogParsingJobProperties.fileUploadRelocationJobProperties());
    }

    /**
     * Validate LogFileParsingJobProperties
     * @param conf
     * @return true if all properties are found, false otherwise
     *
     */
    public static boolean validateLogFileParsingJob(Configuration conf){
        return validate(conf, LogParsingJobProperties.logFileParsingJobProperties());
    }

    /**
     * check if conf contains all the required param of this class
     * @param conf
     * @return  true is all properties are found , false otherwise
     */
    private static boolean validate(Configuration conf, EnumSet<LogParsingJobProperties> props){
        boolean isOk=true;
        for (LogParsingJobProperties p : props){
            if(p.isRequired()){
                String v = conf.get(p.getPropertyName());
                isOk=isOk && null !=v;
            }
            if (!isOk) {
                System.err.println("missing property: "+p.getPropertyName());
                break;
            }
        }
        return isOk;
    }

    /**
     * Methode used to get properties from job configuration
     * get value defined for a LogParsingJobProperties constant : search for p.getPropertyName() in conf.
     * @param conf
     * @param p
     * @return  the propertie value as a String  or null
     */
    public static String get(Configuration conf, LogParsingJobProperties p){
        return conf.get(p.getPropertyName());
    }

    /**
     * Methode used to get properties from job configuration, if value is not present defaultValue is return
     * @param conf
     * @param p
     * @param defaultValue
     * @return  the propertie value as a String  or defaultValue
     */
    public static String get(Configuration conf, LogParsingJobProperties p, String defaultValue){
        if (null == conf.get(p.getPropertyName())) {
            return defaultValue;
        }
        else
            return conf.get(p.getPropertyName());
    }

    /**
     * Method to get properties from Options provided at command line
     * get value defined for a LogParsingJobProperties constant : search for p.getPropertyName() in conf.
     * @param conf
     * @param p
     * @param defaultValue  default boolean value
     * @return  the property value as a boolean or default value if property does not exist in conf
     */
    public static boolean getBoolean(Configuration conf, LogParsingJobProperties p, boolean defaultValue){
        return conf.getBoolean(p.getPropertyName(), defaultValue);
    }

    /**
     * Method to check if a properties has been set from Options provided at command line
     * get value defined for a LogParsingJobProperties constant : search for p.getPropertyName() in conf.
     * @param line
     * @param p
     * @return true if property set, false otherwise
     */
    public static boolean hasOption(CommandLine line, LogParsingJobProperties p){
        return line.hasOption(p.getShortOpt());
    }

    /**
     * Method to get propeties form Options provide at command line
     * get value defined for a LogPasingJobProperties constant : search for p.getPropertyName() in line
     * @param line
     * @param p
     * @return  the value of the property, null otherwise
     */
    public static String getOptionValue(CommandLine line, LogParsingJobProperties p){
        return line.getOptionValue(p.shortOpt);
    }

    public static void getPropertiesFile(Configuration conf, String defautPropertiesFile){
        Properties props = new Properties();
        String properties = LogParsingJobProperties.get(conf, LogParsingJobProperties.PROPERTIESFILES, defautPropertiesFile);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream propsResource = cl.getResourceAsStream(properties);
        if (propsResource != null){
            // load properties

            try {
                props.load(propsResource);
                // transfer the properties to conf
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    String propName = (String)entry.getKey();
                    String propValue = (String)entry.getValue();
                    conf.set(propName, propValue);
                }

            } catch (IOException e) {
                LOGGER.warn("loading properties file from classpath: "+properties, e);
                LOGGER.warn("properties have to be provided from command line");
            }
        }else{
            LOGGER.warn("unable to load resource "+ properties);
            LOGGER.warn("properties have to be provided from command line");

        }
    }

    public static boolean hasHourlyPropsSet(Configuration conf){
        return null != LogParsingJobProperties.get(conf,LogParsingJobProperties.YEAR) &&
                null != LogParsingJobProperties.get(conf,LogParsingJobProperties.MONTH) &&
                null != LogParsingJobProperties.get(conf,LogParsingJobProperties.DAY) &&
                null != LogParsingJobProperties.get(conf,LogParsingJobProperties.HOUR) &&  LogParsingJobProperties.get(conf,LogParsingJobProperties.HOUR).matches("(?:\\*|\\d+)");

    }

    public static boolean hasDailyPropsSet(Configuration conf){
        return null != LogParsingJobProperties.get(conf,LogParsingJobProperties.YEAR) &&
                null != LogParsingJobProperties.get(conf,LogParsingJobProperties.MONTH) &&
                null != LogParsingJobProperties.get(conf,LogParsingJobProperties.DAY);

    }

}