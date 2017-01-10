package com.securityx.modelfeature.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.securityx.modelfeature.config.FeatureServiceConfiguration;
import com.securityx.modelfeature.dao.DetectorHomeDao;
import com.securityx.modelfeature.utils.MiscUtils;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by harish on 12/16/15.
 */
public class FeatureResponseCache {


    private final Logger LOGGER = LoggerFactory.getLogger(FeatureResponseCache.class);
    FeatureServiceConfiguration conf;

    public FeatureResponseCache(FeatureServiceConfiguration conf) {
        this.conf = conf;
    }

    /**
     * max size = 10000. TODO: Test with more data and verify the max size
     */
    private LoadingCache<CacheRequestObject, List<Map<String, Object>>> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .refreshAfterWrite(1, TimeUnit.DAYS)
            .build(
                    new CacheLoader<CacheRequestObject, List<Map<String, Object>>>() {

                        @Override
                        public List<Map<String, Object>> load(CacheRequestObject cacheRequestObject) throws Exception {
                            LOGGER.debug("Loading auto-complete cache");


                            return loadCache(cacheRequestObject);

                        }

                        @Override
                        public ListenableFuture<List<Map<String, Object>>> reload(final CacheRequestObject key, List<Map<String, Object>> prevGraph) {

                            // asynchronous
                            ListenableFutureTask<List<Map<String, Object>>> task = ListenableFutureTask.create(new Callable<List<Map<String, Object>>>() {
                                public List<Map<String, Object>> call() {
                                    return loadCache(key);
                                }
                            });
                            Executors.newSingleThreadExecutor().execute(task);
                            return null;

                        }
                    });


    /**
     * responsible for quering the db and loading the data in cache
     * @param cacheRequestObject
     * @return
     *
     */
    private List<Map<String, Object>> loadCache(CacheRequestObject cacheRequestObject) {
        List<Map<String, Object>> list = Lists.newLinkedList();
        try{
            PreparedStatement preparedStatement = cacheRequestObject.preparedStatement;
            ResultSet rs = cacheRequestObject.baseDao.executeQuery(preparedStatement);
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            while(rs.next()){
                Map<String, Object> map = Maps.newHashMap();
                cacheRequestObject.baseDao.appendToMap(rs, resultSetMetaData, scala.collection.JavaConversions.mapAsScalaMap(map));
                list.add(map);
            }

        }catch(Exception e){
            LOGGER.error(" Error while getting data from db and loading in cache => " + e);
        }finally {
            cacheRequestObject.baseDao.closeConnections(cacheRequestObject.connection);
        }
        return list;
    }


    public List<Map<String, Object>> get(CacheRequestObject cacheRequestObject){

        List<Map<String, Object>> result = Lists.newLinkedList();
        try {
            result = cache.get(cacheRequestObject);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to fetch values from cache for cacheRequestObject => " + cacheRequestObject +
                    "  => " + e);
        }
        return result;
    }

    public String getValueFromMap(Map<String, Object> map, String columnName){
        Object obj = map.get(MiscUtils.underscoreToCamel(columnName.toLowerCase()));
        if(obj == null){
            return null;
        }
        return obj.toString();

    }

}
