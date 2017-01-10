package com.securityx.modelfeature.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.PhoenixUtils;
import com.securityx.modelfeature.utils.EntityThreat;
import com.securityx.modelfeature.utils.MiscUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Cache to store the hostNames, userNames and sourceIps for supporting Auto-complete.
 * Uses DetectorHomeDao for loading cache
 *
 */
public class AutoCompleteCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCompleteCache.class);
    private FeatureServiceConfiguration conf;
    private LoadingCache<String, Directory> cache;
    private static final Integer NUM_RESULTS = 10;
    private static final Integer NUM_FETCH_RESULTS = 20;

    public AutoCompleteCache(FeatureServiceConfiguration conf) {
        this.conf = conf;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .refreshAfterWrite(1, TimeUnit.DAYS)
                .build(new LuceneIndexCacheLoader(conf));
    }
    public FeatureServiceConfiguration getConf() {
        return conf;
    }

    public void setConf(FeatureServiceConfiguration conf){
        this.conf = conf;
    }

    private static class LuceneIndexCacheLoader extends CacheLoader<String, Directory>{
        FeatureServiceConfiguration configuration;

        LuceneIndexCacheLoader(FeatureServiceConfiguration conf){
            configuration = conf;
        }

        @Override
        public Directory load(String key) throws Exception{
            return loadLuceneIndex(configuration, key);
        }

        @Override
        public ListenableFuture<Directory> reload(final String key, final Directory oldValue) throws Exception{
            // asynchronous
            ListenableFutureTask<Directory> task = ListenableFutureTask.create(() -> {
                try {
                    loadData(getSql(true, configuration), configuration, oldValue);
                }catch (Exception ex){
                    LOGGER.error("Could not update lucene index!.Error: {} ", ex.getMessage(), ex);
                }
                return oldValue;
            });
            Executors.newSingleThreadExecutor().execute(task);
            return task;
        }
    }
    private static Tuple2<Boolean, Directory> getDirectory(FeatureServiceConfiguration configuration, String key){
        Directory newDirectory;
        try {
            Tuple2<Boolean, String> tuple = getDir(configuration.getAutoCompleteCacheDir()+File.separatorChar+key);
            FSDirectory fsDirectory = FSDirectory.open(new File(tuple._2()).toPath());
            newDirectory = new NRTCachingDirectory(fsDirectory, 5.0 /* maxMergeSizeMB */, 60.0 /* maxCachedMB */);
            return new Tuple2<>(tuple._1(), newDirectory);
        }catch (Exception ex){
            LOGGER.error("Could not open the file {}. Falling back to RAMDir Error: {}", ex.getMessage(), ex);
            newDirectory = new RAMDirectory();
            return new Tuple2<>(false, newDirectory);
        }

    }
    private static Tuple2<Boolean, String> getDir(String dir) throws Exception{
        Boolean exists = Files.exists(Paths.get(dir));
        if(!exists){
            Files.createDirectories(Paths.get(dir));
        }
        return new Tuple2<>(exists, dir);
    }
    private static class EdgeNGramAnalyzer extends Analyzer {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            Tokenizer tokenizer = new EdgeNGramTokenizer(1, 255);
            TokenStream filter = new EdgeNGramTokenFilter(tokenizer, 1, 255);
            filter = new LowerCaseFilter(filter);
            return new TokenStreamComponents(tokenizer, filter);
        }
    }
    private static Directory loadLuceneIndex(FeatureServiceConfiguration configuration, String key) throws Exception{
        Tuple2<Boolean, Directory> tuple2 = getDirectory(configuration, key);
        // if tuple2._1() is true, then load all the data from the beginning
        // else since the directory and data exists, just load from the current day
        String sqlStr = getSql(tuple2._1(), configuration);
        Directory directory = tuple2._2();
        loadData(sqlStr, configuration, directory);
        return directory;
    }
    public static String getSql(boolean appendWhere, FeatureServiceConfiguration configuration){
        String sql = String.format("SELECT DISTINCT %s, %s, %s, %s from  %s",
                EntityThreat.USER_NAME(),
                EntityThreat.HOST_NAME(),
                EntityThreat.IP_ADDRESS(),
                EntityThreat.MAC_ADDRESS(),
                EntityThreat.getName(configuration));

        if(appendWhere){
            String where = String.format("where %s >= '%s'", EntityThreat.DATE_TIME(), getDateStringForPreviousDay());
            return sql+" "+where;
        }
        return sql;
    }
    private static void loadData(String sqlStr, FeatureServiceConfiguration configuration, Directory directory)
            throws Exception{
        LOGGER.info(sqlStr);
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        EdgeNGramAnalyzer analyzer = new EdgeNGramAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iWriter = new IndexWriter(directory, config);
        try {
            conn = PhoenixUtils.getPhoenixConnection(configuration);
            statement = conn.prepareStatement(sqlStr);
            rs = statement.executeQuery();
            int numDocs = 0;
            while (rs.next()) {
                String hostName = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.HOST_NAME().toString()), configuration);
                String ipAddress = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.IP_ADDRESS().toString()), configuration);
                String macAddress = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.MAC_ADDRESS().toString()), configuration);
                String userName = MiscUtils.stringReplaceNullValue(rs.getString(EntityThreat.USER_NAME().toString()), configuration);
                Document doc = new Document();
                doc.add(new Field(EntityThreat.HOST_NAME().toString(), hostName, TextField.TYPE_STORED));
                doc.add(new Field(EntityThreat.IP_ADDRESS().toString(), ipAddress, TextField.TYPE_STORED));
                doc.add(new Field(EntityThreat.MAC_ADDRESS().toString(), macAddress, TextField.TYPE_STORED));
                doc.add(new Field(EntityThreat.USER_NAME().toString(), userName, TextField.TYPE_STORED));
                iWriter.addDocument(doc);
                numDocs++;
            }
            LOGGER.info("Number of docs inserted: " + numDocs);
        }catch(Exception ex){
            LOGGER.error("Failed to create or update LuceneIndex. Error; {} ", ex.getMessage(), ex);
            // ignore the exception
        }finally{
            if(rs != null) rs.close();
            if(statement != null) statement.close();
            if(conn != null) conn.close();
            iWriter.close();
        }
    }
    private static String getDateStringForPreviousDay(){
        return DateTime.now().withZone(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(1).toString();
    }
    private enum FieldName{
        HOST_NAME("hostName"),
        IP_ADDRESS("sourceIp"),
        MAC_ADDRESS("macAddress"),
        USER_NAME("userName");
        private String name;
        FieldName(String str){
            name = str;
        }
        public static String mapToColumn(FieldName fieldName){
            switch(fieldName){
                case HOST_NAME:
                    return EntityThreat.HOST_NAME().toString();
                case IP_ADDRESS:
                    return EntityThreat.IP_ADDRESS().toString();
                case MAC_ADDRESS:
                    return EntityThreat.MAC_ADDRESS().toString();
                case USER_NAME:
                    return EntityThreat.USER_NAME().toString();
                default:
                    throw new IllegalArgumentException("unknown enum: "+fieldName.toString());
            }
        }
        public static String mapToColumn(String fieldName){
            if(fieldName.equals(HOST_NAME.toString())) {
                return EntityThreat.HOST_NAME().toString();
            }else if(fieldName.equals(IP_ADDRESS.toString())) {
                return EntityThreat.IP_ADDRESS().toString();
            }else if(fieldName.equals(MAC_ADDRESS.toString())) {
                return EntityThreat.MAC_ADDRESS().toString();
            }else if(fieldName.equals(USER_NAME.toString())) {
                return EntityThreat.USER_NAME().toString();
            }else {
                throw new IllegalArgumentException("unknown enum: "+fieldName);
            }
        }
        public static String mapToOutputKeys(FieldName fieldName){
            switch(fieldName){
                case HOST_NAME:
                    return "hostNames";
                case IP_ADDRESS:
                    return "ipAddresses";
                case MAC_ADDRESS:
                    return "macAddresses";
                case USER_NAME:
                    return "userNames";
                default:
                    throw new IllegalArgumentException("unknown enum: "+fieldName.toString());
            }
        }
        public static String mapToOutputKeys(String column){
            if(column.equals(EntityThreat.HOST_NAME().toString())){
                return "hostNames";
            }else if(column.equals(EntityThreat.MAC_ADDRESS().toString())){
                return "macAddresses";
            }else if(column.equals(EntityThreat.IP_ADDRESS().toString())){
                return  "ipAddresses";
            }else if(column.equals(EntityThreat.USER_NAME().toString())){
                return "userNames";
            }else{
                throw new IllegalArgumentException("unknown value : "+column);
            }
        }
        public static FieldName mapToField(scala.Enumeration.Value column){
            if(column.equals(EntityThreat.HOST_NAME())){
                return HOST_NAME;
            }else if(column.equals(EntityThreat.USER_NAME())){
                return USER_NAME;
            }else if(column.equals(EntityThreat.IP_ADDRESS())){
                return IP_ADDRESS;
            }else if(column.equals(EntityThreat.MAC_ADDRESS())){
                return MAC_ADDRESS;
            }else{
                throw new IllegalArgumentException("unknown value : "+column.toString());
            }
        }
        public String toString(){
            return name;
        }
    }

    private List<String> getOrDefault(Map<String, List<String>> map, String key){
        List<String> list = map.get(key);
        if (list == null) {
            list = Lists.newArrayList();
            map.put(key, list);
        }
        return list;
    }
    private List<String> add(List<String> list, String item){
        if(item!=null && item.length()>0 && !item.equals("NULL")){
            // prevent duplicate items from being in the list.
            if(!list.contains(item) && list.size()<NUM_RESULTS) {
                list.add(item);
            }
        }
        return list;
    }
    /**
     * queries the autoCompleteCache for the given fieldName and gets the prefixMap for the incomingString,
     * for which we need to auto-complete
     * @param incomingString String
     * @return List<String> specifying auto-complete suggestions
     */
    public Map<String, List<String>> get(String incomingString){
        Map<String, List<String>> result = Maps.newHashMap();
        try {
            Directory directory = cache.get(EntityThreat.getName(getConf()));
            DirectoryReader iReader = DirectoryReader.open(directory);
            LOGGER.info("Number of docs indexed in Lucene: {}", iReader.numDocs());
            IndexSearcher iSearcher = new IndexSearcher(iReader);
            String[] fields = new String[]{
                    EntityThreat.HOST_NAME().toString(),
                    EntityThreat.USER_NAME().toString(),
                    EntityThreat.IP_ADDRESS().toString(),
                    EntityThreat.MAC_ADDRESS().toString(),
            };
            String regex = "/.*"+incomingString.toLowerCase()+".*/";
            String[] queries = new String[]{regex, regex, regex, regex};

            Query query =  MultiFieldQueryParser.parse(queries, fields, new StandardAnalyzer());
            ScoreDoc[] hits = iSearcher.search(query, NUM_FETCH_RESULTS).scoreDocs;
            if(hits.length>0) {
                for (ScoreDoc hit : hits) {
                    Document hitDoc = iSearcher.doc(hit.doc);
                    // find the field that has matched the document
                    // and populate only the value of the field that matched.
                    for (String field: fields){
                        // create a wildcard query to check which field matched.
                        Query q = new WildcardQuery(new Term(field, incomingString.toLowerCase()));
                        Explanation ex = iSearcher.explain(q, hit.doc);
                        if (ex.isMatch()){
                            //Your query matched field
                            String value = hitDoc.get(field);
                            List<String> list = getOrDefault(result, FieldName.mapToOutputKeys(field));
                            add(list, value);
                        }
                    }
                }
            }
            // Don't close the Directory object since it will be re-fetched and reused from the cache.
            // Only close the DirectoryReader object.
            try {
                iReader.close();
            }catch(Exception ex){
                LOGGER.error("Could not CLOSE the index reader!. Error: "+ex.getMessage(), ex);
                // ignore the exception
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch values from cache for inputString => " + incomingString + " Error => " +e.getMessage(), e);
            // ignore the exception
        }
        // return empty result set
        return result;
    }
    public List<String> get(String incomingString, String fieldName){
        List<String> result = Lists.newArrayList();
        try {
            Directory directory = cache.get(EntityThreat.getName(getConf()));
            DirectoryReader iReader = DirectoryReader.open(directory);
            LOGGER.info("Number of docs indexed in Lucene: {}", iReader.numDocs());
            IndexSearcher iSearcher = new IndexSearcher(iReader);
            //RegexpQuery regexpQuery = new RegexpQuery(new Term( FieldName.mapToColumn(fieldName), ".*"+incomingString.toLowerCase()+".*"));
            //Query regexpQuery = new WildcardQuery(new Term(FieldName.mapToColumn(fieldName), "*"+incomingString.toLowerCase()+"*"));
            String regex = "/.*"+incomingString.toLowerCase()+".*/";
            String[] queries = new String[]{regex};
            String[] fields = new String[]{FieldName.mapToColumn(fieldName)};
            Query query =  MultiFieldQueryParser.parse(queries, fields, new StandardAnalyzer());
            ScoreDoc[] hits = iSearcher.search(query, NUM_FETCH_RESULTS).scoreDocs;
            if(hits.length>0) {
                for (ScoreDoc hit : hits) {
                    Document hitDoc = iSearcher.doc(hit.doc);
                    add(result, hitDoc.get(FieldName.mapToColumn(fieldName)));
                }
            }
            // Don't close the Directory object since it will be re-fetched and reused from the cache.
            // Only close the DirectoryReader object.
            try {
                iReader.close();
            }catch(Exception ex){
                LOGGER.error("Could not CLOSE the index reader!. Error: "+ex.getMessage(), ex);
                // ignore the exception
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch values from cache for inputString => " + incomingString + " Error => " +e.getMessage(), e);
            // ignore the exception
        }
        // return empty result set
        return result;
    }
}
