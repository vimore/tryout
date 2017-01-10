package com.e8.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@Ignore
public class PatriciaTriePerfTest {

    private class AutoCompleteCacheLoader extends CacheLoader<String, PatriciaTrie<String>> {

        @Override
        public PatriciaTrie<String> load(String field){
            return loadAll(field);
        }
        PatriciaTrie<String> loadAll(String field){
            return loadTrie(field);
        }
        PatriciaTrie<String> loadTrie(String field){
            PatriciaTrie<String> trie = new PatriciaTrie<String>();
            for(int i=0;i<=10000000;i++){
                switch(field.toLowerCase()){
                    case "ipaddress":
                        String ip = randomIpAddress();
                        trie.put(ip,ip );
                        break;
                    case "hostname":
                        String hostName = RandomStringUtils.randomAlphanumeric(10);
                        trie.put(hostName, hostName);
                        break;
                    case "username":
                        String userName = RandomStringUtils.randomAlphanumeric(10);
                        trie.put(userName, userName);
                        break;
                    case "macaddress":
                        String mac = randomMACAddress();
                        trie.put(mac, mac);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown field name: "+field);
                }
            }
            return trie;
        }
        private String randomIpAddress(){
            Random rand = new Random();
            return String.format("%d.%d.%d.%d", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }
        private String randomMACAddress(){
            Random rand = new Random();
            byte[] macAddr = new byte[6];
            rand.nextBytes(macAddr);

            macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

            StringBuilder sb = new StringBuilder(18);
            for(byte b : macAddr){
                if(sb.length() > 0)
                    sb.append(":");

                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }

        @Override
        public ListenableFuture<PatriciaTrie<String>> reload(final String key, PatriciaTrie<String> prevGraph) {

            // asynchronous
            ListenableFutureTask<PatriciaTrie<String>> task = ListenableFutureTask.create(new Callable<PatriciaTrie<String>>() {
                @Override
                public PatriciaTrie<String> call() throws Exception {
                    return loadTrie(key);
                }
            });
            Executors.newSingleThreadExecutor().execute(task);
            return task;

        }
    }

    private List<String> getAllCopy(String incomingString, String fieldName, LoadingCache<String, PatriciaTrie<String>> cache)
            throws Exception{

        List<String> list = Lists.newArrayList();
        fieldName = fieldName.toLowerCase();
        PatriciaTrie<String> trie = cache.get(fieldName);

        Map<String, String> map = trie.prefixMap(incomingString.toLowerCase());

        list.addAll(map.values());

        return list;

    }

    private Collection<String> getAllNoCopy(String incomingString, String fieldName, LoadingCache<String, PatriciaTrie<String>> cache)
            throws Exception{

        fieldName = fieldName.toLowerCase();
        PatriciaTrie<String> trie = cache.get(fieldName);

        Map<String, String> map = trie.prefixMap(incomingString.toLowerCase());

        return map.values();

    }

    private List<String> get(String incomingString, String fieldName, LoadingCache<String, PatriciaTrie<String>> cache, int num)
            throws Exception{

        List<String> result = Lists.newArrayList();

        fieldName = fieldName.toLowerCase();
        PatriciaTrie<String> trie = cache.get(fieldName);

        Map<String, String> map = trie.prefixMap(incomingString.toLowerCase());

        Iterator<String> values = map.values().iterator();
        for(int i=0; values.hasNext() && i<num;i++) {
            result.add(values.next());
        }
        return result;


    }

    private AutoCompleteCacheLoader loader = new AutoCompleteCacheLoader();
    private LoadingCache<String, PatriciaTrie<String>> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .refreshAfterWrite(1,TimeUnit.DAYS)
            .build(loader);


    @Test
    public void testTrieNoCopy() throws Exception{
        PatriciaTrie<String> trie = cache.get("ipAddress");
        assertNotNull(trie);
        assertNotEquals(0, trie.size());
        //prime the cache
        long start = System.nanoTime();
        Collection<String> all = getAllNoCopy("1", "ipAddress", cache);
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching "+Integer.toString(all.size())+" records: "+Long.toString((stop-start)/(1000))+" micro secs (No Priming)");
        long total = 0;
        List<Integer> list = new ArrayList<>();
        for(int i=0; i<100; i++) {
            start = System.nanoTime();
            all = getAllNoCopy("1", "ipAddress", cache);
            stop = System.nanoTime();
            total += stop-start;
            list.add(all.size());
            Iterator<String> iter = all.iterator();
            assertTrue(iter.hasNext());
        }
        assertNotEquals(0,all.size());
        System.out.println("Average Time taken for fetching "+Integer.toString(all.size())+" records: "+Long.toString(total/(1000*100))+" micro secs (Priming)");

    }
    @Test
    public void testTrieAllCopy() throws Exception{
        PatriciaTrie<String> trie = cache.get("ipAddress");
        assertNotNull(trie);
        assertNotEquals(0, trie.size());
        //prime the cache
        long start = System.nanoTime();
        Collection<String> all = getAllCopy("1", "ipAddress", cache);
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching "+Integer.toString(all.size())+" records: "+Long.toString((stop-start)/(1000))+" micro secs (No Priming)");

        long total=0;
        for(int i=0; i<100; i++) {
            start = System.nanoTime();
            all = getAllCopy("1", "ipAddress", cache);
            stop = System.nanoTime();
            total += stop-start;
            System.out.println(all.size());
        }
        assertNotEquals(0,all.size());
        System.out.println("Average Time taken for fetching "+Integer.toString(all.size())+" records: "+Long.toString(total/(1000*100))+" micro secs (Priming)");

    }
    @Test
    public void testTrieHundred() throws Exception{

        PatriciaTrie<String> trie = cache.get("ipAddress");
        assertNotNull(trie);
        assertNotEquals(0, trie.size());
        long start = System.nanoTime();
        List<String> ten = get("1", "ipAddress", cache,100);
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching "+Integer.toString(ten.size())+" records: "+Long.toString((stop-start)/(1000))+" micro secs (No Priming)");

        start = System.nanoTime();
        for(int i=0; i<100; i++) {
            ten = get("1", "ipAddress", cache, 100);
        }
        stop = System.nanoTime();
        System.out.println("Average Time taken for fetching "+Integer.toString(ten.size())+" records: "+Long.toString((stop-start)/(1000*100))+" micro secs (Priming)");
        assertEquals(100,ten.size());

    }
    @Test
    public void testTrieTen() throws Exception{
        PatriciaTrie<String> trie = cache.get("ipAddress");
        assertNotNull(trie);
        assertNotEquals(0, trie.size());
        long start = System.nanoTime();
        List<String> ten = get("1", "ipAddress", cache,10);
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching "+Integer.toString(ten.size())+" records: "+Long.toString((stop-start)/(1000))+" micro secs (No Priming)");

        start = System.nanoTime();
        for(int i=0; i<100; i++) {
            ten = get("1", "ipAddress", cache, 10);
        }
        stop = System.nanoTime();
        System.out.println("Average Time taken for fetching "+Integer.toString(ten.size())+" records: "+Long.toString((stop-start)/(1000*100))+" micro secs (Priming)");

        assertEquals(10,ten.size());

    }
}
