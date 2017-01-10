package com.securityx.mef.dpi;


import com.securityx.data.dao.MasterEventFormatRecordDao;
import com.securityx.mef.dpi.avro.Event;
import com.securityx.mef.schema.*;
import com.securityx.mef.schema.util.MasterEventFormatRecordReaderWriter;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.*;

//import org.apache.log4j.Logger;

/**
 * Reads DPI records and creates corresponding MEF records
 * IMPORTANT Assumption: Any one DPI record represents one of the traffic "Http", "DNS" , "SSL" ....
 */
public class DpiToMef  {

    //private static Logger logger = Logger.getLogger(DpiToMef.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterEventFormatRecordDao.class);

    /** Constants for some of the  DPI keys used */
    private final static String TUPLE = "tuple";
    private final static String VALUES = "values";
    private final static String DEST_IP = "dstip";
    private final static String DEST_PORT = "dport";
    private final static String SRC_IP = "srcip";
    private final static String SRC_PORT = "sport";
    private final static String IP_PROTOCOL = "ipproto";
    private final static String TIMESTAMP = "timestamp";
    private final static String SESSIONID = "sessionid";

    private final static String DPI_HOSTNAME = "hostname";
    private final static String DPI_CATEGORY = "category";

    private final static String CLIENT_IP = "clientip";
    private final static String REQUEST_RESPONSE_ID = "id";
    private final static String REQUEST_RESPONSE_TYPE = "type";

    private Map<RequestResponseKey, MasterEventFormat> reqRespMefs = new HashMap<RequestResponseKey, MasterEventFormat>();

    /**
     * A key used to track a request or response packets. Currently only used with HTTP traffic
     */
    private class RequestResponseKey {
        private long sessionId ;
        private int reqResId ;
        private String reqResType;

        private RequestResponseKey(long sessionId, int reqResId, String reqResType) {
            this.sessionId = sessionId;
            this.reqResId = reqResId;
            this.reqResType = reqResType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RequestResponseKey that = (RequestResponseKey) o;

            if (reqResId != that.reqResId) return false;
            if (sessionId != that.sessionId) return false;
            if (reqResType != null ? !reqResType.equals(that.reqResType) : that.reqResType != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (sessionId ^ (sessionId >>> 32));
            result = 31 * result + reqResId;
            result = 31 * result + (reqResType != null ? reqResType.hashCode() : 0);
            return result;
        }
    }

    public DpiToMef() {

    }

    /**
     * Called to clear any previously used transient data. Should be used before calling saveDpiEvent methods
     */
    public void clear() {
        this.reqRespMefs.clear();
    }
    /**
     * Creates MEF records from DPI records contained in an Avro binary file
     * @param file
     * @throws Exception
     */
    public void createAvroEventToMef(String customerId,File file,File mefFile) throws Exception {
        // Deserialize events from file
        DatumReader<Event> dpiEventDatumReader = new SpecificDatumReader<Event>(Event.class);
        DataFileReader<Event> dataFileReader = new DataFileReader<Event>(file, dpiEventDatumReader);

        Event dpiEvent = null;
        Map<String,Object> body = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        List<MasterEventFormat> mefs = new LinkedList<MasterEventFormat>();
        while(dataFileReader.hasNext()) {
            dpiEvent = dataFileReader.next(dpiEvent);
            MasterEventFormat mef = formMef(dpiEvent,customerId);
            Dpi dpi = (Dpi)mef.getEvent();
            if (dpi.getDpiApplication() != null) {
                mefs.add(mef);
            }
        }
        //Now write mef to a local file
        if (!mefs.isEmpty()) {
            MasterEventFormatRecordReaderWriter mefWriter = new MasterEventFormatRecordReaderWriter();
            mefWriter.writeToFile(mefFile,mefs); //
        }
    }

    public void createDpiJsonToMef(String jsonFileName,String customerId) throws Exception {
        BufferedReader in
                = new BufferedReader(new FileReader(jsonFileName));
        List<MasterEventFormat> mefs = new LinkedList<MasterEventFormat>();
        String line ;
        while ( (line = in.readLine() ) != null ) {
            String n = line;
            MasterEventFormat mef = formMef(line,customerId);
            if (mef != null ) {
                Dpi dpi = (Dpi)mef.getEvent();
                if (dpi.getDpiApplication() != null) {
                    mefs.add(mef);
                }
                else {
                    createNotYetClassifiedApplication(mef);
                }
                if (!isAnyTupleFiledNull(dpi.getDpiCommon())) {
                    mefs.add(mef);
                }
//                else {
//                    mefs.add(mef);
//                }
            }
        }

        //Now write mef to a local file
        if (!mefs.isEmpty()) {
            MasterEventFormatRecordReaderWriter mefWriter = new MasterEventFormatRecordReaderWriter();
            //mefWriter.writeToFile(mefFile,mefs); //
            int i = 0;
        }
    }

    /**
     * An example method to show how to extract dpi fields from a dpi json file
     * @param jsonFileName
     * @param customerId
     * @throws Exception
     */
    public void createDpiJsonToMefNew(String jsonFileName,String customerId) throws Exception {
        ///IMPORTANT: This map must be cleared before extracting fields
        reqRespMefs.clear();
        BufferedReader in
                = new BufferedReader(new FileReader(jsonFileName));
        List<MasterEventFormat> mefs = new LinkedList<MasterEventFormat>();
        String line ;
        while ( (line = in.readLine() ) != null ) {
            String n = line;
            List<MasterEventFormat> formedMefs = formMefNew(line,customerId);
            if (formedMefs != null ) {
                for (MasterEventFormat mef : formedMefs) {
                    if (mef != null ) {
                        Dpi dpi = (Dpi)mef.getEvent();
                        if (dpi.getDpiApplication() != null && (!isAnyTupleFiledNull(dpi.getDpiCommon()))) {
                            mefs.add(mef);
                        }
                    }
                }
            }
            else {
                int i = 0;
            }
        }

        //Now write mef to a local file after merging req-res records for an id and also records belonging to
        //one tuple
        if (!mefs.isEmpty()) {
            MasterEventFormatRecordReaderWriter mefWriter = new MasterEventFormatRecordReaderWriter();
            //mefWriter.writeToFile(mefFile,mefs); //
            int i = 0;
        }
    }

    /**
     * Saves
     * @param jsonFileName
     * @param customerId
     * @throws Exception
     */
    public void saveDpiJsonToMef(String jsonFileName,String customerId) throws Exception {
        ///IMPORTANT: This map must be cleared before extracting fields
        reqRespMefs.clear();
        BufferedReader in
                = new BufferedReader(new FileReader(jsonFileName));
        List<MasterEventFormat> mefs = new LinkedList<MasterEventFormat>();
        String line ;
        while ( (line = in.readLine() ) != null ) {
            saveDpiEventNew(line,customerId,new File(jsonFileName).getName());
        }
    }

    public String getBodyContent(Event dpiEvent) {
        String bodyStr = new String( dpiEvent.getBody().array(), Charset.forName("UTF-8") );
        return bodyStr;
    }

    /**
     * Merges a request and a response record
     *
     */
    public boolean  mergeRequestResponse(String customerId,Long timeStamp, Long sessionId, Integer reqResId,
                                         DpiApplicationType appType  ) throws Exception {
        MasterEventFormatRecordDao dao = new MasterEventFormatRecordDao();
        List<MasterEventFormat> mefs  = dao.getTransientRecords(customerId,timeStamp,sessionId,reqResId,DpiApplicationType.HTTP);
        if (mefs == null || mefs.isEmpty()  || mefs.size() > 2 ) {
            LOGGER.error(" mefs size is not valid " +  (mefs == null?-1:mefs.size()) );
            return false;
        }

        //Now size is 1 or 2
        if (mefs.size() == 1) {
            MasterEventFormat reqRes = mefs.get(0);
            MasterEventFormat  merged  = SpecificData.get().deepCopy(MasterEventFormat.getClassSchema(), reqRes);
            Dpi mergedDpi = (Dpi)merged.getEvent();
            mergedDpi.setRecordStatus(DpiRecordStatus.FINAL);
            mergedDpi.setRequestResponseType("request-response");
            dao.putDpiNonNullFields(reqRes);
            return true;
        }

        //mefs size is 2

        MasterEventFormat req = mefs.get(0);
        MasterEventFormat res = mefs.get(1);

        //Object sd = SpecificData.newInstance(MasterEventFormat.class,MasterEventFormat.getClassSchema());
        MasterEventFormat  merged  = SpecificData.get().deepCopy(MasterEventFormat.getClassSchema(), req);

        Dpi mergedDpi = (Dpi)merged.getEvent();
        Http mergerdHttp = (Http)mergedDpi.getDpiApplication();

        Dpi resDpi = (Dpi)res.getEvent();
        Http resHttp = (Http)resDpi.getDpiApplication();

        if (resHttp.getConnection() != null ) mergerdHttp.setConnection(resHttp.getConnection());
        if (resHttp.getServer() != null ) mergerdHttp.setServer(resHttp.getServer());
        if (resHttp.getServerAgent() != null ) mergerdHttp.setServerAgent(resHttp.getServerAgent());
        if (resHttp.getMimeType() != null ) mergerdHttp.setMimeType(resHttp.getMimeType());
        if (resHttp.getRequestSize() != null ) mergerdHttp.setRequestSize(resHttp.getRequestSize());
        if (resHttp.getResponseTs() != null ) mergerdHttp.setResponseTs(resHttp.getResponseTs());
        if (resHttp.getContentLength() != null ) mergerdHttp.setContentLength(resHttp.getContentLength());
        if (resHttp.getContentType() != null ) mergerdHttp.setContentType(resHttp.getContentType());
        if (resHttp.getDate() != null ) mergerdHttp.setDate(resHttp.getDate());
        if (resHttp.getCode() != null )mergerdHttp.setCode(resHttp.getCode());

        mergedDpi.setRecordStatus(DpiRecordStatus.FINAL);
        mergedDpi.setRequestResponseType("request-response");

        dao.putDpiNonNullFields(merged);

        return true;

    }

    /**
     * A mef record is formed from a json record passed as string
     * @param line
     * @return
     * @throws Exception
     */
    private MasterEventFormat formMef(String line,String customerId) throws Exception  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> body = mapper.readValue(line, HashMap.class);
        MasterEventFormat mef = new MasterEventFormat();
        mef.setCustomerId(customerId);
        mef.setCollectionType(CollectionType.DPI);
        DpiCommon common = new DpiCommon();
        Dpi dpi = new Dpi();
        dpi.setDpiCommon(common);
        mef.setEvent(dpi);
        if (body.containsKey(TUPLE)) {
            extractDpiRecordBody(body, mef);
            return mef;
        }
        else {
            return  null;
        }
    }

    /**
     * One or more mef records are formed from a json record  ( formed from a string )
     * @param line
     * @return
     * @throws Exception
     */
    private List<MasterEventFormat> formMefNew(String line,String customerId) throws Exception  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> body = mapper.readValue(line, HashMap.class);

        if (body.containsKey(TUPLE)) {
            return extractDpiRecordBodyNew(body,customerId,null,null);
        }
        else {
            //skip if the record does not contain the required TUPLE which indicates error in the input record
            return  null;
        }
    }

    private List<MasterEventFormat> formMefNew(Event dpiEvent, String customerId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //Need to do something with header info ??
        //dpiEvent.getHeaders();
        //CharSequence st = dpiEvent.getHeaders().get(new Utf8("timestamp"));
        //Date d = new Date(Long.parseLong(s)) ;

        CharSequence hn = dpiEvent.getHeaders().get(DPI_HOSTNAME);
        CharSequence hc = dpiEvent.getHeaders().get(DPI_CATEGORY);
        //The body part is an UTF-8 character array
        String bodyStr = new String( dpiEvent.getBody().array(), Charset.forName("UTF-8") );
        //Using jackson lib, we form the json objects from the "string" bodyStr
        //The keys are tuple, sessionid, clientip, 123, 20001... where 123, 20001 are packet numbers
        //Value for each numbered key ( 123,20001...)  are KVs
        Map<String,Object> body = mapper.readValue(bodyStr, HashMap.class);

        DpiCommon common = new DpiCommon();
        common.setDpiCollectionHostname(hn);
        common.setDpiCollectionCategory(hc);
        Dpi dpi = new Dpi();
        dpi.setDpiCommon(common);

        if (body.containsKey(TUPLE)) {
            return extractDpiRecordBodyNew(body, customerId,hn,hc);
        }
        else {
            return  null;
        }
    }

    /**
     * An mef record is created from an avro event record
     * @param dpiEvent
     * @return
     * @throws Exception
     */
    private MasterEventFormat formMef(Event dpiEvent, String customerId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //Need to do something with header info ??
        //dpiEvent.getHeaders();
        //CharSequence st = dpiEvent.getHeaders().get(new Utf8("timestamp"));
        //Date d = new Date(Long.parseLong(s)) ;

        CharSequence hn = dpiEvent.getHeaders().get(DPI_HOSTNAME);
        CharSequence hc = dpiEvent.getHeaders().get(DPI_CATEGORY);
        //The body part is an UTF-8 character array
        String bodyStr = new String( dpiEvent.getBody().array(), Charset.forName("UTF-8") );
        //Using jackson lib, we form the json objects from the "string" bodyStr
        //The keys are tuple, sessionid, clientip, 123, 20001... where 123, 20001 are packet numbers
        //Value for each numbered key ( 123,20001...)  are KVs
        Map<String,Object> body = mapper.readValue(bodyStr, HashMap.class);
        MasterEventFormat mef = new MasterEventFormat();
        mef.setCustomerId(customerId);
        mef.setCollectionType(CollectionType.DPI);
        DpiCommon common = new DpiCommon();
        common.setDpiCollectionHostname(hn);
        common.setDpiCollectionCategory(hc);
        Dpi dpi = new Dpi();
        dpi.setDpiCommon(common);
        mef.setEvent(dpi);
        if (body.containsKey(TUPLE)) {
            extractDpiRecordBody(body, mef);
            return mef;
        }
        else {
            return  null;
        }
    }

    /**
     * Called to save an avro record event as mef to db
     * @param dpiEvent
     * @throws Exception
     */
    public void saveDpiEvent(Event dpiEvent,String customerId,String sourceFileName)  throws Exception {
        MasterEventFormat mef = formMef(dpiEvent,customerId);
        saveMef(mef, sourceFileName);
    }

    /**
     * Called to save an dpi event json record
     * @param line
     * @throws Exception
     */
    public void saveDpiEvent(String line,String customerId, String sourceFileName)  throws Exception {
        MasterEventFormat mef = formMef(line,customerId);
        saveMef(mef,sourceFileName);
    }

    public List<MasterEventFormat> saveDpiEventNew(String line,String customerId, String sourceFileName)  throws Exception {
        List<MasterEventFormat> mefs = formMefNew(line, customerId);
        if (mefs != null) {
            for(MasterEventFormat mef: mefs) {
                saveMef(mef,sourceFileName) ;
            }
        }
       return mefs;
    }

    private void saveMef(MasterEventFormat mef,String sourceFileName) throws Exception {
        if (mef == null) {
            LOGGER.info(" MasterEventFormat is null and nothing is stored ");
            return;
        }
        Dpi dpi = (Dpi)mef.getEvent();
        if (dpi.getDpiApplication() == null) {
            return;
            //createNotYetClassifiedApplication(mef);
        }
        //Ensure that all basic dpi fields are available
        if (!isAnyTupleFiledNull(dpi.getDpiCommon())) {
            MasterEventFormatRecordDao dao = new MasterEventFormatRecordDao();
            dao.putMefRecord(mef,sourceFileName);
            LOGGER.debug(" Record saved for ts " + dpi.getDpiCommon().getTimestamp());
        }
        else {
            LOGGER.info(" MasterEventFormat is not stored as some tuple field is null ");
        }
    }

    /**
     * Saves records from a local avro file of dpi events
     * @param file
     * @throws Exception
     */
    public void saveDpiEvent(File file,String customerId) throws Exception {
        // Deserialize events from file
        DatumReader<Event> dpiEventDatumReader = new SpecificDatumReader<Event>(Event.class);
        DataFileReader<Event> dataFileReader = new DataFileReader<Event>(file, dpiEventDatumReader);

        Event dpiEvent = null;
        Map<String,Object> body = new HashMap<String,Object>();
        ObjectMapper mapper = new ObjectMapper();
        List<MasterEventFormat> mefs = new LinkedList<MasterEventFormat>();
        while(dataFileReader.hasNext()) {
            dpiEvent = dataFileReader.next(dpiEvent);
            saveDpiEvent(dpiEvent,customerId,file.getName());
        }
    }

    /**
     * A simple util method to get int from a string representation
     * @param val
     * @return
     */
    private Integer toNum(String val) {
        Integer r = -1;
        try {
           r = Integer.valueOf(val);
        }
        catch (NumberFormatException ne) {
            //log this info
        }
        return  r;
    }

    /**
     * Each record of DPI in Avro format has a key "body" and all traffic fields are extracted from this JSON object
     * @param body
     * @param mef
     */
    private void extractDpiRecordBody(Map<String,Object> body,MasterEventFormat mef) {

        List<Long> numberKeys = new ArrayList<Long>();
        //keys are tuple, sessionid, numbers like "122323", etc
        DpiCommon common =  ((Dpi)mef.getEvent()).getDpiCommon();
        for(Map.Entry<String, Object> e : body.entrySet()) {
            //we skip some keys like "clientip", "max_http_response", "max_http_request"...
            if (e.getKey().equalsIgnoreCase(TUPLE)) {
                Object tupObj  = e.getValue();
                if (tupObj instanceof Map) {
                    Map<String,Object> tupleMap = (Map)tupObj;
                    Integer ts = (Integer)tupleMap.get(TIMESTAMP);
                    common.setTimestamp(new Long(ts));  //if not a number, parsing will throw exception
                    common.setIpProtocol((String) tupleMap.get(IP_PROTOCOL));
                    common.setDestinationIp((String) tupleMap.get(DEST_IP));
                    Integer port = Integer.parseInt((String)tupleMap.get(DEST_PORT));
                    common.setDestinationPort(port);
                    common.setSourceIp((String) tupleMap.get(SRC_IP));
                    port = Integer.parseInt((String)tupleMap.get(SRC_PORT));
                    common.setSourcePort(port);
                }
            }
            else if (e.getKey().equalsIgnoreCase(SESSIONID)) {
                common.setSessionId(Long.valueOf((String) e.getValue()));
            }
            else if (e.getKey().equalsIgnoreCase(CLIENT_IP)) {
                common.setClientIp((String)e.getValue());
            }
            else {
                try {
                    //Accumulate all number keys for sorting
                    Long numKey = Long.valueOf(e.getKey());
                    numberKeys.add(numKey);
                }
                catch (NumberFormatException nfe ) {
                    //
                }
            }
        }

        extractNumberKeyValues(numberKeys,body,mef);
        //extractNumberKeyValuesNew(numberKeys, body, mef);
    }

    private List<MasterEventFormat> extractDpiRecordBodyNew(Map<String,Object> body,String customerId,CharSequence hostName,CharSequence hostCategory) {
        //IMPORTANT Assumption: Any one DPI record represents one of the traffic "Http", "DNS" , "SSL" ....
        DpiCommon common = new DpiCommon();
        common.setDpiCollectionHostname(hostName);
        common.setDpiCollectionCategory(hostCategory);
        List<Long> numberKeys = new ArrayList<Long>();
        //keys are tuple, sessionid, numbers like "122323", etc

        for(Map.Entry<String, Object> e : body.entrySet()) {

            if (e.getKey().equalsIgnoreCase(TUPLE)) {
                Object tupObj  = e.getValue();
                if (tupObj instanceof Map) {
                    Map<String,Object> tupleMap = (Map)tupObj;
                    Integer ts = (Integer)tupleMap.get(TIMESTAMP);
                    common.setTimestamp(new Long(ts));  //if not a number, parsing will throw exception
                    common.setIpProtocol((String) tupleMap.get(IP_PROTOCOL));
                    common.setDestinationIp((String) tupleMap.get(DEST_IP));
                    Integer port = Integer.parseInt((String)tupleMap.get(DEST_PORT));
                    common.setDestinationPort(port);
                    common.setSourceIp((String) tupleMap.get(SRC_IP));
                    port = Integer.parseInt((String)tupleMap.get(SRC_PORT));
                    common.setSourcePort(port);
                }
            }
            else if (e.getKey().equalsIgnoreCase(SESSIONID)) {
                common.setSessionId(Long.valueOf((String) e.getValue()));
            }
            else if (e.getKey().equalsIgnoreCase(CLIENT_IP)) {
                common.setClientIp((String)e.getValue());
            }
            else {
                try {
                    //Accumulate all number keys for sorting
                    Long numKey = Long.valueOf(e.getKey());
                    numberKeys.add(numKey);
                }
                catch (NumberFormatException nfe ) {
                    //
                }
            }
        }

        //extractNumberKeyValues(numberKeys,body,mef);
        return extractNumberKeyValuesNew(numberKeys,body,common,customerId);
    }

    /**
     * Extracts values from submap of each top level numbered key
     * @param numberKeys
     * @param bodyMap
     * @param mef
     */
    private void extractNumberKeyValues(List<Long> numberKeys, Map<String, Object> bodyMap, MasterEventFormat mef) {
        Collections.sort(numberKeys);
        for (Long numKey: numberKeys) {
            Object value = bodyMap.get(numKey.toString());
            if (value instanceof Map) {
                Map<String, Object>  m = (Map)value;
                //Integer ts= (Integer)m.get("timestamp");
                Object vo =  m.get(VALUES);
                if (vo instanceof List) {
                    List<Map> lm = (List)vo;
                    extractFields(lm,mef) ;
                }
            }
        }
    }
    /**
     * Extracts values from submap of each top level numbered key
     * @param numberKeys
     * @param bodyMap
     * @param common
     *      shared by all packets
     * @return
     *  list of MasterEventFormat ( generally one element only present)
     */
    private List<MasterEventFormat> extractNumberKeyValuesNew(List<Long> numberKeys, Map<String, Object> bodyMap,
                                                              DpiCommon common,String customerId) {
        List<MasterEventFormat> mefs = new LinkedList<MasterEventFormat>();
        Collections.sort(numberKeys);

        for (Long numKey: numberKeys) {
            Object value = bodyMap.get(numKey.toString());
            if (value instanceof Map) {
                Map<String, Object>  m = (Map)value;
                //get "id" and "type" values for this packet
                Integer idVal = (Integer)m.get(REQUEST_RESPONSE_ID);
                String reqRespType = (String)m.get(REQUEST_RESPONSE_TYPE);
                MasterEventFormat mef = null;
                long sessionId = common.getSessionId()  ;
                //separate mef records are created. one for request type and another one response type
                if (idVal != null && reqRespType != null ) {
                    RequestResponseKey rrKey = new RequestResponseKey(sessionId,idVal,reqRespType);
                    mef = reqRespMefs.get(rrKey);
                    if (mef == null) {
                        mef = new MasterEventFormat();
                        mef.setCustomerId(customerId);
                        mef.setCollectionType(CollectionType.DPI);
                        Dpi dpi = new Dpi() ;
                        dpi.setRecordStatus(DpiRecordStatus.TRANSIENT);
                        dpi.setRequestResponseId(idVal);
                        dpi.setRequestResponseType(reqRespType);
                        dpi.setDpiCommon(common);
                        mef.setEvent(dpi);

                        if (extractFields(m.get(VALUES), mef)) {
                            reqRespMefs.put(rrKey,mef);
                            //add the mef one time to the list
                            mefs.add(mef);
                        }
                    }
                    else {
                        //use the previously created mef to use in extraction
                        extractFields(m.get(VALUES), mef);
                    }
                }
                else {
                    //a new mef is created for each "packet" and as they all have the same tuple info,
                    //all these records are used in hbase (hopefully !!)
                    mef = new MasterEventFormat();
                    mef.setCustomerId(customerId);
                    mef.setCollectionType(CollectionType.DPI);
                    Dpi dpi = new Dpi() ;
                    dpi.setRecordStatus(DpiRecordStatus.FINAL);
                    dpi.setRequestResponseId(-1);
                    dpi.setRequestResponseType("NONE");
                    dpi.setDpiCommon(common);
                    mef.setEvent(dpi);
                    if (extractFields(m.get(VALUES), mef)) {
                        mefs.add(mef);
                    }
                }
            }
        }
        return mefs;
    }

    private boolean extractFields(Object vo, MasterEventFormat mef) {
        boolean extracted = false;
        if (vo instanceof List) {
            List<Map> lm = (List)vo;
            extracted = extractFieldsNew(lm, mef) ;
        }
        return extracted;
    }

    /**
     * Extracts application specific fields from one "packet"
     * @param fieldKVs
     * @param mef
     */
    private boolean extractFieldsNew(List<Map> fieldKVs, MasterEventFormat mef) {
        boolean extracted = false;
        Dpi dpi = (Dpi)mef.getEvent();
        for(Map<String,Object > mp: fieldKVs) {
            for(Map.Entry<String,Object > e : mp.entrySet()) {
                //IMPORTANT: Any one DPI record represents one of the traffic "Http", "DNS" , "SSL" ....
                if (ApplicationProtocolFields.HttpField.contains(e.getKey()) && dpi.getRequestResponseId() != -1 ) {
                    //System.out.println("Num Key : " + dummy +"  Matched Key is " + e.getKey() +" and value is " + e.getValue())  ;
                    extractHttpFields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
                else if (ApplicationProtocolFields.DnsField.contains(e.getKey())) {
                    extractDnsFields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
                else if (ApplicationProtocolFields.SslField.contains(e.getKey())) {
                    extractSslFields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
                else if (e.getKey().startsWith("smtp/email/"))     {
                    extractSmtpFields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
                else if (e.getKey().startsWith("pop3/email/")) {
                    extractPop3Fields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
                else if (e.getKey().startsWith("irc/")) {
                    extractIrcFields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
                else if (e.getKey().startsWith("dhcp/")) {
                    extractDhcpFields(e.getKey(), e.getValue(), mef);
                    extracted = true;
                }
            }
        }
        return  extracted;
    }

    /**
     * Extracts application specific fields from one "packet"
     * @param fieldKVs
     * @param mef
     */
    private void extractFields(List<Map> fieldKVs, MasterEventFormat mef) {
        for(Map<String,Object > mp: fieldKVs) {
            for(Map.Entry<String,Object > e : mp.entrySet()) {
                //IMPORTANT: Any one DPI record represents one of the traffic "Http", "DNS" , "SSL" ....
                if (ApplicationProtocolFields.HttpField.contains(e.getKey())) {
                    //System.out.println("Num Key : " + dummy +"  Matched Key is " + e.getKey() +" and value is " + e.getValue())  ;
                    extractHttpFields(e.getKey(),e.getValue(),mef);
                }
                else if (ApplicationProtocolFields.DnsField.contains(e.getKey())) {
                    extractDnsFields(e.getKey(), e.getValue(), mef);
                }
                else if (ApplicationProtocolFields.SslField.contains(e.getKey())) {
                    extractSslFields(e.getKey(), e.getValue(), mef);
                }
                else if (e.getKey().startsWith("smtp/email/"))     {
                    extractSmtpFields(e.getKey(), e.getValue(), mef);
                }
                else if (e.getKey().startsWith("pop3/email/")) {
                    extractPop3Fields(e.getKey(), e.getValue(), mef);
                }
                else if (e.getKey().startsWith("irc/")) {
                   extractIrcFields(e.getKey(), e.getValue(), mef);
                }

                else if (e.getKey().startsWith("dhcp/")) {
                    extractDhcpFields(e.getKey(), e.getValue(), mef);
                }
            }
        }
    }

    private void extractHttpFields(String key, Object value,MasterEventFormat mef) {
       ApplicationProtocolFields.HttpField httpField =  ApplicationProtocolFields.HttpField.toEnum(key);
       Dpi dpi = (Dpi)mef.getEvent();
       DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
       Http httpFieldGroup = null;
        if (dpi.getDpiApplication() == null) {
            httpFieldGroup = new Http();
            dpi.setDpiApplication(httpFieldGroup);
            dpi.setDpiApplicationType(DpiApplicationType.HTTP);
            common.setApplicationProtocol(DpiApplicationType.HTTP.name());
        }
        httpFieldGroup = (Http)dpi.getDpiApplication();
        if (httpField != null) {
            switch (httpField) {
                case METHOD:
                    httpFieldGroup.setMethod((String)value);
                    break;
                case HOST:
                    httpFieldGroup.setHost((String)value);
                    break;
                case REQUEST_TS:
                    httpFieldGroup.setRequestTs(Double.valueOf((String) value));
                    break;
                case RESPONSE_TS:
                    httpFieldGroup.setResponseTs(Double.valueOf((String) value));
                    break;
                case URI_PARAM_NAME:
                    httpFieldGroup.setUriParameterName((String)value);
                    break;
                case URI_PARAM_VALUE:
                    httpFieldGroup.setUriParameterValue((String)value);
                    break;
                case URI_PATH:
                    httpFieldGroup.setUriPath((String)value);
                    break;
                case USER_AGENT:
                    httpFieldGroup.setUserAgent((String)value);
                    break;
                case REQUEST_SIZE:
                    httpFieldGroup.setRequestSize((String)value);
                    break;
                case SERVER:
                    httpFieldGroup.setServer((String)value);
                    break;
                case SERVER_AGENT:
                    httpFieldGroup.setServerAgent((String) value);
                    break;
                case CACHE_CONTROL:
                    httpFieldGroup.setCacheControl((String) value);
                    break;
                case CODE:
                    httpFieldGroup.setCode((String) value);
                    break;
                case CONNECTION:
                    httpFieldGroup.setConnection((String) value);
                    break;
                case CONTENT_LENGTH:
                    httpFieldGroup.setContentLength(toNum((String) value));
                    break;
                case CONTENT_TYPE:
                    httpFieldGroup.setContentType((String)value);
                    break;
                case COOKIE:
                    httpFieldGroup.setCookie((String) value);
                    break;
                case TRANSFER_ENCODING:
                    httpFieldGroup.setTransferEncoding((String) value);
                    break;
                case DATE:
                    httpFieldGroup.setDate((String)value);
                    break;
                case VERSION:
                    httpFieldGroup.setVersion((String) value);
                    break;
            }
        }
    }

    private void extractDnsFields(String key, Object value,MasterEventFormat mef) {
        Dpi dpi = (Dpi)mef.getEvent();

        dpi.setDpiApplication(new Object()); //dummy application for now. See http extraction how the actual creation works

        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        dpi.setDpiApplicationType(DpiApplicationType.DNS);
        common.setApplicationProtocol(DpiApplicationType.DNS.name());
        /**
        ApplicationProtocolFields.DnsField field =  ApplicationProtocolFields.DnsField.toEnum(key);
        Dns fieldGroup = null;
        if (mef.getApplication() == null) {
            fieldGroup = new Dns();
            mef.setApplication(fieldGroup);
            mef.setApplicationType(ApplicationType.DNS);
            mef.getCommonAttribute().setApplicationProtocol(APP_PROTOCOL_DNS);
        }
        fieldGroup = (Dns)mef.getApplication();
        if (field != null) {
            switch (field) {
//                case QUERY_HOST_ADDR:
//                    fieldGroup.setEntryHostAddress((String) value);
//                    break;
//                case QUERY_ENTRY_NAME:
//                    fieldGroup.setEntryName((String) value);
//                    break;
//                case QUERY_ENTRY_TTL:
//                    fieldGroup.setEntryTTL((String) value);
//                    break;
//                case QUERY_HOST_TYPE:
//                    fieldGroup.setEntryHostType((String) value);
//                    break;
//                case QUERY_TYPE:
//                    fieldGroup.setQueryType((String) value);
//                    break;
                case REPLY_CODE:
                    fieldGroup.setReplyCode((String) value);
                    break;
                case QUERY:
                    fieldGroup.setRequestQuery((String) value);
                    break;
                case MESSAGE_TYPE:
                    fieldGroup.setRequestQueryMessageType((String) value);
                    break;
                case CLASSIFICATION_MATCH:
                    fieldGroup.setClassificationMatch((String) value);
                    break;
            }
        }

        */
    }

    private void extractSslFields(String key, Object value,MasterEventFormat mef) {
        Dpi dpi = (Dpi)mef.getEvent();
        dpi.setDpiApplication(new Ssl()); //dummy application for now. See http extraction how the actual creation works
        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        dpi.setDpiApplicationType(DpiApplicationType.SSL);
        common.setApplicationProtocol(DpiApplicationType.SSL.name());
    }

    private void extractSmtpFields(String key, Object value,MasterEventFormat mef) {
        Dpi dpi = (Dpi)mef.getEvent();
        dpi.setDpiApplication(new Smtp()); //dummy application for now. See http extraction how the actual creation works
        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        dpi.setDpiApplicationType(DpiApplicationType.SMTP);
        common.setApplicationProtocol(DpiApplicationType.SMTP.name());
    }

    private void extractPop3Fields(String key, Object value,MasterEventFormat mef) {
        Dpi dpi = (Dpi)mef.getEvent();
        dpi.setDpiApplication(new Pop3()); //dummy application for now. See http extraction how the actual creation works
        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        dpi.setDpiApplicationType(DpiApplicationType.POP3);
        common.setApplicationProtocol(DpiApplicationType.POP3.name());
    }

    private void extractIrcFields(String key, Object value,MasterEventFormat mef) {
        Dpi dpi = (Dpi)mef.getEvent();
        dpi.setDpiApplication(new Irc()); //dummy application for now. See http extraction how the actual creation works
        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        dpi.setDpiApplicationType(DpiApplicationType.IRC);
        common.setApplicationProtocol(DpiApplicationType.IRC.name());
    }

    private void extractDhcpFields(String key, Object value,MasterEventFormat mef) {
        Dpi dpi = (Dpi)mef.getEvent();
        dpi.setDpiApplication(new Dhcp()); //dummy application for now. See http extraction how the actual creation works
        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        dpi.setDpiApplicationType(DpiApplicationType.DHCP);
        common.setApplicationProtocol(DpiApplicationType.DHCP.name());
    }

    private void createNotYetClassifiedApplication(MasterEventFormat mef)  {
        //We have not yet identified the application protocol and so we crate a dummy application just to
        //capture the tuple
        Dpi dpi = (Dpi)mef.getEvent();
        DpiCommon common = ((Dpi)mef.getEvent()).getDpiCommon();
        NotYetClassified fieldGroup = new NotYetClassified();
        dpi.setDpiApplication(fieldGroup);
        dpi.setDpiApplicationType(DpiApplicationType.NOT_YET_CLASSIFIED);
        common.setApplicationProtocol(DpiApplicationType.NOT_YET_CLASSIFIED.name());
    }

    private boolean isAnyTupleFiledNull(DpiCommon dpiCommon) {
       if (    dpiCommon.getIpProtocol() == null ||
               dpiCommon.getApplicationProtocol() == null ||
               dpiCommon.getDestinationIp() == null ||
               dpiCommon.getDestinationPort() == null ||
               dpiCommon.getSourceIp() == null ||
               dpiCommon.getSourcePort() == null ||
               dpiCommon.getTimestamp() == null ) {
           return  true;
       }
       else {
           return  false;
       }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("/Users/jeyasankar/mytemp/security1.packet.1383719767198") ;
        //File file = new File("/Users/jeyasankar/mytemp/security1.packet.1383692459327") ;// "/Users/jeyasankar/mytemp/security1.packet.1383719767198");
        File mefFile = new File("/Users/jeyasankar/mytemp/mef1.avro");
        DpiToMef dpiToMef = new DpiToMef();

        //dpiToMef.createMefRecord(file,mefFile);
        //dpiToMef.saveDpiEvent(file);
        //String fn = "/Users/jeyasankar/mytemp/dpi_data/net-2009-11-14-09:24_dpi";
        //String fn = "/Users/jeyasankar/mytemp/net-2009-12-11-12:00_dpi" ; //
        //String fn = "/Users/jeyasankar/mytemp/security1.packet.1383719767198.json" ;
        //dpiToMef.createDpiJsonToMef(fn,"testcustomer");


        String fn = "/Users/jeyasankar/mytemp/dpi_data/digital_corpora_dpi_with_id/net-2009-11-13-09:24_dpi"; //net-2009-11-16-13:08_dpi";//net-2009-11-14-09:24_dpi";
        //dpiToMef.createDpiJsonToMefNew(fn, "testcustomer");
        //dpiToMef.saveDpiJsonToMef(fn,"testcustomer")  ;


        //dpiToMef.mergeRequestResponse("testcustomer",1258411069L, 2185L , 6,DpiApplicationType.HTTP);

        //1258133122
        //dpiToMef.mergeRequestResponse("digital_corpora",1258133122L, 7L , 1,DpiApplicationType.HTTP);
        dpiToMef.mergeRequestResponse("testcustomer",1258133122L, 7L , 1,DpiApplicationType.HTTP);
    }

}

//    private List<MasterEventFormat> extractDpiRecordBodyNew(Map<String,Object> body,String customerId,CharSequence hostName,CharSequence hostCategory) {
//        //IMPORTANT Assumption: Any one DPI record represents one of the traffic "Http", "DNS" , "SSL" ....
//        DpiCommon common = new DpiCommon();
//        common.setDpiCollectionHostname(hostName);
//        common.setDpiCollectionCategory(hostCategory);
//        List<Long> numberKeys = new ArrayList<Long>();
//        //keys are tuple, sessionid, numbers like "122323", etc
//
//        for(Map.Entry<String, Object> e : body.entrySet()) {
//
//            if (e.getKey().equalsIgnoreCase(TUPLE)) {
//                Object tupObj  = e.getValue();
//                if (tupObj instanceof Map) {
//                    Map<String,Object> tupleMap = (Map)tupObj;
//                    Integer ts = (Integer)tupleMap.get(TIMESTAMP);
//                    common.setTimestamp(new Long(ts));  //if not a number, parsing will throw exception
//                    common.setIpProtocol((String) tupleMap.get(IP_PROTOCOL));
//                    common.setDestinationIp((String) tupleMap.get(DEST_IP));
//                    Integer port = Integer.parseInt((String)tupleMap.get(DEST_PORT));
//                    common.setDestinationPort(port);
//                    common.setSourceIp((String) tupleMap.get(SRC_IP));
//                    port = Integer.parseInt((String)tupleMap.get(SRC_PORT));
//                    common.setSourcePort(port);
//                }
//            }
//            else if (e.getKey().equalsIgnoreCase(SESSIONID)) {
//                common.setSessionId(Long.valueOf((String) e.getValue()));
//            }
//            else {
//                try {
//                    //Accumulate all number keys for sorting
//                    Long numKey = Long.valueOf(e.getKey());
//                    numberKeys.add(numKey);
//                }
//                catch (NumberFormatException nfe ) {
//                    //
//                }
//            }
//        }
//
//        //extractNumberKeyValues(numberKeys,body,mef);
//        return extractNumberKeyValuesNew(numberKeys,body,common,customerId);
//    }

