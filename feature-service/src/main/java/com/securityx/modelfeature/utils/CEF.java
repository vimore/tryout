package com.securityx.modelfeature.utils;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.resources.Version;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Object that holds all the values of a CEF object.
 *
 */
public class CEF implements Serializable {

    private static final Logger LOGGER  = LoggerFactory.getLogger(CEF.class);

    private static Version version = new Version();
    //  EntityInfo to CEF fields fields mapping
    private transient static Map<String, String> e8ToArcsight = new HashMap<String, String>();
    static {
        // Mapping between EntityInfo concept and CEF fields
        // all the entities attributes reported in the alert describe the source of the
        // described behaviour
        e8ToArcsight.put("macAddress", "dmac");
        e8ToArcsight.put("hostName", "dhost");
        e8ToArcsight.put("ipAddress", "dst");
        e8ToArcsight.put("dateTime", "rt");
        e8ToArcsight.put("userName", "duser");
        // unity mapping as fields extension can be deserialized from the CEF String
        // when read from AlertAuditLogs
        // this implementation is a bit strange as CEF is not the most natural serialization
        // scheme with java.
        e8ToArcsight.put("dmac", "dmac");
        e8ToArcsight.put("dhost", "dhost");
        e8ToArcsight.put("dst", "dst");
        e8ToArcsight.put("start", "start");
        e8ToArcsight.put("rt", "rt");
        e8ToArcsight.put("duser", "duser");
        e8ToArcsight.put("rt", "rt");
    }
    /**
     * The maximum value that a severity can be
     */
    public static final double MAX_SEVERITY = 1.0;

    /**
     * The minimum value that a severity can be
     */
    public static final double MIN_SEVERITY = 0.0;

    /** Serial version */
    private static final long serialVersionUID = 1L;

    //~--- fields -------------------------------------------------------------

    /** The version of the CEF format */
    private final String cefVersion;

    /** The CEF vendor field */
    private final String vendor;

    /** The CEF product field */
    private final String product;

    /** The Device version field */
    private final String deviceVersion;

    /** The CEF signatureId field */
    private final String signatureId;

    /** The CEF name field */
    private final String name;

    /** The CEF severity field */
    private final double severity;

    /** The CEF extention field dvchost */
    private final String dvcHost;

    /** The CEF extension field */
    private final CefExtension extension;
    private static SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static Matcher inputFormatMatcher = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d+Z").matcher("");
    //Jul 28 2015 11:00:00
    private static SimpleDateFormat inputFormatter2 = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
    private static Matcher inputFormatMatcher2 = Pattern.compile("\\w{3} \\d{1,2} \\d{4} \\d{1,2}:\\d{1,2}:\\d{1,2}").matcher("");
    private static List<ImmutablePair<Matcher, SimpleDateFormat>> dateFormats = new ArrayList<ImmutablePair<Matcher, SimpleDateFormat>>();

    static{
        dateFormats.add(new ImmutablePair(inputFormatMatcher, inputFormatter));
        dateFormats.add(new ImmutablePair(inputFormatMatcher2, inputFormatter2));
    }

    /*
    file:///Users/ramv/Downloads/CommonEventFormatv23.pdf
     rt deviceReceiptTime Time Stamp The time at which the event related to the activity wasreceived. The format is MMM dd yyyy HH:mm:ss or millisecondssince epoch (Jan 1st 1970).
    */

    //~--- constructors -------------------------------------------------------

    /**
     * Cast the signatureId as a string
     *  @param vendor
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param product
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param deviceVersion
 *            part of the group of strings that uniquely identify the type of sending device. No
 *            two products may use the same device-vendor and device- product pair.
     * @param signatureId
*            a unique identifier per event-type. This can be a string or an integer. Signature
*            ID identifies the type of event reported. In the intrusion detection system (IDS)
*            world, each signature or rule that detects certain activity has a unique signature
*            ID assigned. This is a requirement for other types of devices as well, and helps
*            correlation engines deal with the events.
     * @param name
*            a string representing a human-readable and understandable description of the
*            event. The event name should not contain information that is specifically
*            mentioned in other fields. For example: "Port scan from 10.0.0.1 targeting
*            20.1.1.1" is not a good event name. It should be: "Port scan". The other
*            information is redundant and can be picked up from the other fields.
     * @param severity
*            an integer and reflects the importance of the event. Only numbers from 0 to 1 are
*            allowed, where 1 indicates the most important event.
     * @param extension
*            a collection of key-value pairs. The keys are part of a predefined set. The
     */
    public CEF(final String vendor, final String product, final String deviceVersion, final int signatureId, final String name,
               final int severity, final CefExtension extension, FeatureServiceCache featureServiceCache) {
        this( vendor, product, deviceVersion, Integer.toString(signatureId), name, severity, extension, featureServiceCache );
        inputFormatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    }


    /**
     * Use the default CEF version
     *
     * @param vendor
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param product
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param deviceVersion
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param signatureId
     *            a unique identifier per event-type. This can be a string or an integer. Signature
     *            ID identifies the type of event reported. In the intrusion detection system (IDS)
     *            world, each signature or rule that detects certain activity has a unique signature
     *            ID assigned. This is a requirement for other types of devices as well, and helps
     *            correlation engines deal with the events.
     * @param name
     *            a string representing a human-readable and understandable description of the
     *            event. The event name should not contain information that is specifically
     *            mentioned in other fields. For example: "Port scan from 10.0.0.1 targeting
     *            20.1.1.1" is not a good event name. It should be: "Port scan". The other
     *            information is redundant and can be picked up from the other fields.
     * @param severity
     *            an integer and reflects the importance of the event. Only numbers from 0 to 10 are
     *            allowed, where 10 indicates the most important event.
     * @param extension
     *            a collection of key-value pairs. The keys are part of a predefined set. The
     *            standard allows for including additional keys as outlined later.
     */
    public CEF( final String vendor, final String product, final String deviceVersion, final String signatureId, final String name,
                final int severity, final CefExtension extension, FeatureServiceCache featureServiceCache) {
        this( featureServiceCache.getCefConfiguration().getCefVersion(), vendor, product, deviceVersion, signatureId, name, severity, featureServiceCache.getCefConfiguration().getDvcHost(), extension );
    }

    /**
     * Use the default CEF version, default Device version
     *  @param vendor
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param product
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param signatureId
 *            a unique identifier per event-type. This can be a string or an integer. Signature
 *            ID identifies the type of event reported. In the intrusion detection system (IDS)
 *            world, each signature or rule that detects certain activity has a unique signature
 *            ID assigned. This is a requirement for other types of devices as well, and helps
 *            correlation engines deal with the events.
     * @param name
*            a string representing a human-readable and understandable description of the
*            event. The event name should not contain information that is specifically
*            mentioned in other fields. For example: "Port scan from 10.0.0.1 targeting
*            20.1.1.1" is not a good event name. It should be: "Port scan". The other
*            information is redundant and can be picked up from the other fields.
     * @param severity
*            an integer and reflects the importance of the event. Only numbers from 0 to 10 are
*            allowed, where 10 indicates the most important event.
     * @param extension
*            a collection of key-value pairs. The keys are part of a predefined set. The
     */
    public CEF(final String vendor, final String product, final String signatureId, final String name,
               final double severity, final CefExtension extension, FeatureServiceCache featureServiceCache) {
        this( featureServiceCache.getCefConfiguration().getCefVersion(), vendor, product,
                featureServiceCache.getCefConfiguration().getDeviceVersion(),
                signatureId, name, severity,
                featureServiceCache.getCefConfiguration().getDvcHost(), extension );
    }

    /**
     * Use the default CEF version, default Device version, default vendor, default product
     * @param signatureId
     *            a unique identifier per event-type. This can be a string or an integer. Signature
     *            ID identifies the type of event reported. In the intrusion detection system (IDS)
     *            world, each signature or rule that detects certain activity has a unique signature
     *            ID assigned. This is a requirement for other types of devices as well, and helps
     *            correlation engines deal with the events.
     * @param name
     *            a string representing a human-readable and understandable description of the
     *            event. The event name should not contain information that is specifically
     *            mentioned in other fields. For example: "Port scan from 10.0.0.1 targeting
     *            20.1.1.1" is not a good event name. It should be: "Port scan". The other
     *            information is redundant and can be picked up from the other fields.
     * @param severity
     *            an integer and reflects the importance of the event. Only numbers from 0 to 10 are
     *            allowed, where 10 indicates the most important event.
     * @param extension
     *            a collection of key-value pairs. The keys are part of a predefined set. The
     *            standard allows for including additional keys as outlined later.
     */
    public CEF( final String signatureId, final String name,
                final double severity, final CefExtension extension, FeatureServiceCache featureServiceCache ) {
        this( featureServiceCache.getCefConfiguration().getCefVersion(), featureServiceCache.getCefConfiguration().getVendor(),
                featureServiceCache.getCefConfiguration().getProduct(), featureServiceCache.getCefConfiguration().getDeviceVersion(),
                signatureId, name,
                severity,
                featureServiceCache.getCefConfiguration().getDvcHost(), extension );
    }


    /**
     * Construct an CEF object.
     *
     *  @param cefVersion
     *            an integer and identifies the version of the CEF format. Event consumers use this
     *            information to determine what the following fields represent. Currently only
     *            version 0 (zero) is established in the above format. Experience may show that
     *            other fields need to be added to the “prefix” and therefore require a version
     *            number change. Adding new formats is handled through the standards body.
     * @param vendor
     *            part of the group of strings that uniquely identify the type of sending device. No
     *            two products may use the same device-vendor and device- product pair.
     * @param product
 *            part of the group of strings that uniquely identify the type of sending device. No
 *            two products may use the same device-vendor and device- product pair.
     * @param deviceVersion
*            part of the group of strings that uniquely identify the type of sending device. No
*            two products may use the same device-vendor and device- product pair.
     * @param signatureId
*            a unique identifier per event-type. This can be a string or an integer. Signature
*            ID identifies the type of event reported. In the intrusion detection system (IDS)
*            world, each signature or rule that detects certain activity has a unique signature
*            ID assigned. This is a requirement for other types of devices as well, and helps
*            correlation engines deal with the events.
     * @param name
*            a string representing a human-readable and understandable description of the
*            event. The event name should not contain information that is specifically
*            mentioned in other fields. For example: "Port scan from 10.0.0.1 targeting
*            20.1.1.1" is not a good event name. It should be: "Port scan". The other
*            information is redundant and can be picked up from the other fields.
     * @param severity
*            an integer and reflects the importance of the event. Only numbers from 0 to 10 are
*            allowed, where 10 indicates the most important event.
     * @param dvcHost
     * @param extension
     *            a collection of key-value pairs. The keys are part of a predefined set. The
     *
     */
    public CEF(final String cefVersion, final String vendor, final String product, final String deviceVersion, final String signatureId,
               final String name, final double severity, String dvcHost, final CefExtension extension) {
        this.cefVersion = cefVersion;
        this.vendor     = vendor;
        this.product    = product;
        this.deviceVersion    = deviceVersion;
        this.signatureId = signatureId;
        this.name       = name;
        this.severity   = severity;
        this.extension  = extension;
        this.dvcHost = dvcHost;



        assert vendor != null : "The vendor cannot be null";
        assert product != null : "The product cannot be null";
        assert deviceVersion != null : "The version cannot be null";
        assert signatureId != null : "The signatureId cannot be null";
        assert name != null : "The name cannot be null";
        assert dvcHost != null : "The dvcHost cannot be null";
        assert extension != null : "The extension cannot be null";
        assert ((severity >= CEF.MIN_SEVERITY) && (severity <= CEF.MAX_SEVERITY)) :
                "The severity must be between 0 and 1";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CEF cef = (CEF) o;
        return Objects.equal(severity, cef.severity) &&
                Objects.equal(cefVersion, cef.cefVersion) &&
                Objects.equal(vendor, cef.vendor) &&
                Objects.equal(product, cef.product) &&
                Objects.equal(deviceVersion, cef.deviceVersion) &&
                Objects.equal(signatureId, cef.signatureId) &&
                Objects.equal(name, cef.name) &&
                Objects.equal(dvcHost, cef.dvcHost) &&
                Objects.equal(extension, cef.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cefVersion, vendor, product, deviceVersion, signatureId, name, severity, dvcHost, extension);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("cefVersion", cefVersion)
                .add("vendor", vendor)
                .add("product", product)
                .add("deviceVersion", deviceVersion)
                .add("signatureId", signatureId)
                .add("name", name)
                .add("severity", severity)
                .add("dvchost", dvcHost)
                .add("extension", extension)
                .add("CEF Message", this.toSyslogCefString())
                .toString();
    }


    private String convertDateToArcSight(String dateInput){
        //SimpleDateFormat arcSightFormatter = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        try {
            if (dateInput.matches("^\\d+$")){
                return dateInput;
            } else {
                for (ImmutablePair<Matcher, SimpleDateFormat> e : dateFormats){
                    e.getKey().reset(dateInput);
                    if (e.getKey().matches()){
                        Date date = e.getValue().parse(dateInput);
                        String date_arcsight = String.valueOf(date.getTime());
                        return date_arcsight;
                    }
                }
                return "NULL";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return  "NULL";
        }

    }

    public String toSyslogString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "CEF: " );
        sb.append( cefVersion );
        sb.append( "| " );
        sb.append( vendor );
        sb.append( "| " );
        sb.append(  product  );
        sb.append( "| " );
        sb.append( deviceVersion );
        sb.append( "| " );
        sb.append( signatureId );
        sb.append( "| " );
        sb.append( name );
        sb.append( "| " );
        Double risk = 0.0;
        try {
            risk = MathUtils.formatDecimal(severity * 10);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting severity => " + e);
        }
        sb.append(risk);
        sb.append( "| " );
        sb.append( extension );
        return  sb.toString();
    }

    public String toSyslogCefString(){
        StringBuilder sb = new StringBuilder();
        sb.append( "CEF:" );
        sb.append( (int)Double.parseDouble((cefVersion)));
        sb.append( "|" );
        sb.append( vendor );
        sb.append( "|" );
        sb.append(  product  );
        sb.append( "|" );
        sb.append( deviceVersion );
        sb.append( "|" );
        sb.append( signatureId );
        sb.append( "|" );
        sb.append( name );
        sb.append( "|" );
        sb.append((int)(Math.round(severity*10)));
        sb.append( "|" );
        Boolean hasStart = false;
        for (Map.Entry<String, String> entry : extension.getExtensionFields().entrySet())
        {
            if (e8ToArcsight.containsKey(entry.getKey())){
                // do not send useless data
                if ( ! entry.getValue().equals("N/A")){
                    if(e8ToArcsight.get(entry.getKey()).equals("rt")){
                        sb.append("rt"+ "=" + convertDateToArcSight(entry.getValue()) + " ");

                        hasStart = true;
                    }else{
                      sb.append(e8ToArcsight.get(entry.getKey()) + "=" + entry.getValue() + " ");
                    }
                }
            }else{
                //TODO: Needs some logging here in case key is not correct. Perhaps throw exception
                continue;
            }
        }
        if (!hasStart){
            Date d = new Date();
            sb.append("rt="+ d.getTime() + " ");
        }
        sb.append("dvchost="+ this.dvcHost + " ");

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);  //Removing the last whitespace
        }
        //sb.append(extension);
        return  sb.toString();
    }

    //~--- get methods --------------------------------------------------------


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCefVersion() {
        return cefVersion;
    }

    public String getVendor() {
        return vendor;
    }

    public String getProduct() {
        return product;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public String getName() {
        return name;
    }

    public double getSeverity() {
        return severity;
    }

    public CefExtension getExtension() {
        return extension;
    }

    /**
     * used while logging the alerts in db
     * @param cefs
     * @return
     */
    public static String getLogs(List<CEF> cefs){
        StringBuilder builder = new StringBuilder();
        if(cefs != null) {
            for (CEF cef : cefs) {
                builder.append(cef.toSyslogCefString());
                builder.append(System.lineSeparator());
            }
        }

        return builder.toString();

    }

    /**
     * used to form the cef message while sending alert to ArcSight/Splunk
     * @param cefs
     * @return
     */
    public static String getLogsForArcSight(List<CEF> cefs){
        StringBuilder builder = new StringBuilder();
        if(cefs != null) {
            for (CEF cef : cefs) {
                builder.append(cef.toSyslogCefString());
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    /**
     * used to form the cef message while sending alert to ArcSight/Splunk
     * @param cef
     * @return
     */
    public static String getLogsForArcSight(CEF cef) {
        if (cef != null) {
            return cef.toSyslogCefString();
        }
        return null;
    }


    /**
     * read data from alertLog (in db) in (syslog)Cef format back to CEF
     * @param syslog
     * @return
     */
    public static Optional<CEF> syslogCefToCef(String syslog){
        if(syslog == null)
            return Optional.empty();

        String [] cefArr = syslog.split("\\|");

        if(cefArr.length < 8) {
            return Optional.empty();
        }
        LOGGER.info("syslogCefToCef " + syslog);
        String cefVersion = cefArr[0];
        cefVersion = cefVersion.substring(cefVersion.indexOf(":") + 1).trim();
        //vendor
        String vendor = cefArr[1];
        //product
        String product = cefArr[2];
        String deviceVersion = cefArr[3];
        String signatureId = cefArr[4];
        String name = cefArr[5];
        Double severity = null;
        try {
            severity = MathUtils.formatDecimal(Double.parseDouble(cefArr[6]) / 10.0);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting severity => " + e);
        }
        String extension = cefArr[7];
        Map<String, String> map = null;
        if (extension.contains("CefExtension")) {
            extension = extension.substring(extension.indexOf("=") + 1).replace("{", "").replace("}", "").trim();
            map = Splitter.on(" ").withKeyValueSeparator("=").split(extension);
        }else {
            String[] parts = extension.split("(?<!\\\\)=");
            map = new HashMap<String, String>();
            if (parts.length > 2) {
                String k = parts[0];
                String nextKey = null;
                for (int i = 1; i < parts.length - 1; i++) {
                    int lastspace = parts[i].lastIndexOf(" ");
                    String value = parts[i].substring(0, lastspace);
                    nextKey = parts[i].substring(lastspace + 1);
                    map.put(k, value);
                    k = nextKey;
                }
                map.put(nextKey, parts[parts.length - 1]);
            }
        }
        String dvcHost = "undefined" ;

        if (map.containsKey("dvchost")){
            dvcHost = map.get("dvchost");
            map.remove("dvchost");
        }
        CefExtension cefExtension = new CefExtension(map);
        return Optional.of(new CEF(cefVersion, vendor, product, deviceVersion, signatureId, name, severity, dvcHost, cefExtension));
    }
}
