package com.e8.cache;

import com.e8.test.HBaseTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.securityx.modelfeature.FeatureServiceCache;
import com.securityx.modelfeature.common.cache.AutoCompleteCache;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.PhoenixUtils;
import com.securityx.modelfeature.utils.EntityThreat;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.junit.*;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutoCompleteCacheTest extends HBaseTestBase{

    private static AutoCompleteCache autoCompleteCache;
    private static FeatureServiceConfiguration configuration;
    private static FeatureServiceCache featureServiceCache;
    private static final ObjectMapper mapper = Jackson.newObjectMapper();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @BeforeClass
    public static  void setup() throws Exception{
        HBaseTestBase.setupHBase();
        String confFile = System.getProperty("user.dir") + "/src/main/config/dev_cfg.yml";
        configuration = new ConfigurationFactory<>(FeatureServiceConfiguration.class, validator, mapper, "dw").build(new File(confFile));
        featureServiceCache = new FeatureServiceCache(configuration);
        autoCompleteCache = new AutoCompleteCache(configuration);
        //loadData();
    }
    /*
    public static void loadData() throws Exception{
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("entity_threat.csv");
        InputStreamReader reader = new InputStreamReader(is);
        String[] header = new String[]{"DATE_TIME", "IP_ADDRESS", "MAC_ADDRESS", "HOST_NAME", "USER_NAME", "MODEL_ID", "SECURITY_EVENT_ID", "RISK_SCORE"};
        List<String> upserts = parseCsv(header, "ENTITY_THREAT", reader);

        String createTable = "CREATE TABLE IF NOT EXISTS ENTITY_THREAT (\n" +
                "    DATE_TIME VARCHAR NOT NULL,\n" +
                "    IP_ADDRESS VARCHAR NOT NULL,\n" +
                "    MAC_ADDRESS VARCHAR NOT NULL,\n" +
                "    HOST_NAME VARCHAR NOT NULL,\n" +
                "    USER_NAME VARCHAR NOT NULL,\n" +
                "    MODEL_ID INTEGER NOT NULL,\n" +
                "    SECURITY_EVENT_ID INTEGER NOT NULL,\n" +
                "    RISK_SCORE DOUBLE,\n" +
                "CONSTRAINT PK PRIMARY KEY ( DATE_TIME, IP_ADDRESS, MAC_ADDRESS, HOST_NAME, USER_NAME, MODEL_ID, SECURITY_EVENT_ID )\n" +
                ") IMMUTABLE_ROWS=true";
        configuration.setZkQuorum("127.0.0.1:" + utility.getZkCluster().getClientPort());
        configuration.setSolrQuorum("127.0.0.1:" + utility.getZkCluster().getClientPort() + "/solr");
        executeSql(createTable, configuration);
        for(String sql : upserts){
            executeSql(sql, configuration);
        }
    }
    @AfterClass
    public static void teardown() throws Exception {
        String sql = "DROP TABLE ENTITY_THREAT";
        executeSql(sql, configuration);
        teardownHBase();
    }

    private static void executeSql(String sql, FeatureServiceConfiguration configuration)
            throws Exception{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn =  PhoenixUtils.getPhoenixConnection(configuration);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
        } finally {
            if(rs!=null) rs.close();
            if(pstmt!=null) pstmt.close();
            if(conn!=null) conn.close();
        }
    }
    private static List<String> parseCsv(String[] header, String tableName, InputStreamReader reader) throws Exception{
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).withSkipHeaderRecord(true).parse(reader);
        List<String> ret = Lists.newArrayList();
        String columnNames = String.join(",", header);
        for(CSVRecord record : records){
            StringBuilder values = new StringBuilder();
            boolean appendComa = false;
            for(String column: header){
                if(appendComa) values.append(",");
                values.append(record.get(column));
                appendComa = true;
            }
            String sql = String.format("UPSERT INTO %s %s VALUES(%s)", tableName, columnNames, values);
            ret.add(sql);
        }
        return ret;
    }
    */
    private static String getDateStringForPreviousDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'00:00:00.000'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return sdf.format(new Date(System.currentTimeMillis() - DateTimeConstants.MILLIS_PER_DAY));
    }
    @Test
    public void testSql(){
        String sql = AutoCompleteCache.getSql(false, configuration);
        assertFalse(sql.contains("where"));
        sql =    AutoCompleteCache.getSql(true, configuration);
        assertTrue(sql.contains("where"));
        DateTime dt = DateTime.now().withZone(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(1);
        String expected = String.format("SELECT DISTINCT USER_NAME, HOST_NAME, IP_ADDRESS, MAC_ADDRESS from  " + EntityThreat.getName(configuration) + " where DATE_TIME >= '%s'", dt.toString());
        assertEquals(expected, sql);
    }
}
