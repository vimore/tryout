package com.e8.cache;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class LuceneSearchTest {

    class EdgeNGramAnalyzer extends Analyzer {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            Tokenizer tokenizer = new EdgeNGramTokenizer(1, 5);
            TokenStream filter = new EdgeNGramTokenFilter(tokenizer, 1, 5);
            filter = new LowerCaseFilter(filter);
            return new TokenStreamComponents(tokenizer, filter);
        }
    }
    private long loadLuceneIndex(IndexWriter writer) throws Exception {
        long numDocs = 0;
        for(int i=0;i<=10000000;i++){
            String ip = randomIpAddress();
            String hostName = RandomStringUtils.randomAlphanumeric(10);
            String macAddress = randomMACAddress();
            String userName = RandomStringUtils.randomAlphanumeric(10);
            Document doc = new Document();
            doc.add(new Field("ipAddress", ip, StringField.TYPE_STORED));
            doc.add(new Field("hostName", hostName, StringField.TYPE_STORED));
            doc.add(new Field("macAddress", macAddress, StringField.TYPE_STORED));
            doc.add(new Field("userName", userName, StringField.TYPE_STORED));
            writer.addDocument(doc);
            numDocs++;
        }
        return numDocs;
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

    @Test
    public void testLucene() throws Exception{
        Analyzer anal = new StandardAnalyzer();
        //Analyzer analyzer = new ShingleAnalyzerWrapper();
        Analyzer analyzer = new EdgeNGramAnalyzer();
        // Store the index in memory:
        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        long numDocs = loadLuceneIndex(iwriter);
        System.out.println("Number of docs inserted: "+ numDocs);
        iwriter.close();

        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        System.out.println("Number of docs indexed: "+ ireader.numDocs());
        assertEquals(numDocs, ireader.numDocs());
        IndexSearcher isearcher = new IndexSearcher(ireader);

        testIpAddress(isearcher, analyzer);
        testHostName(isearcher, analyzer);
        testMacAddress(isearcher, analyzer);

        ireader.close();
        directory.close();
    }
    private void testIpAddress(IndexSearcher iSearcher, Analyzer analyzer) throws Exception{
        //QueryParser parser = new QueryParser("ipAddress", analyzer);
        //Query query = parser.parse("1");
        RegexpQuery regexpQuery = new RegexpQuery(new Term( "ipAddress", ".*"+"1"+".*"));
        long start = System.nanoTime();
        ScoreDoc[] hits = iSearcher.search(regexpQuery, 10).scoreDocs;
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching the hits: "+(stop-start)/(1000)+" micro secs");
        assertEquals(10, hits.length);

        for(ScoreDoc hit : hits){
            Document hitDoc = iSearcher.doc(hit.doc);
            System.out.println("Doc: "+ hitDoc.toString());
            assertTrue(hitDoc.get("ipAddress").contains("1"));
        }

        start = System.nanoTime();
        for(int i=0; i<100; i++) {
            hits = iSearcher.search(regexpQuery, 10).scoreDocs;
        }
        stop = System.nanoTime();
        System.out.println("Avg Time taken for fetching the hits: "+(stop-start)/(1000*100)+" micro secs");

        assertEquals(10, hits.length);
    }

    private void testHostName(IndexSearcher isearcher, Analyzer analyzer) throws Exception{
        //QueryParser parser = new QueryParser("hostName", analyzer);
        //Query query = parser.parse("a");
        RegexpQuery regexpQuery = new RegexpQuery(new Term( "hostName", ".*"+"a"+".*"));
        long start = System.nanoTime();
        ScoreDoc[] hits = isearcher.search(regexpQuery, 10).scoreDocs;
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching the hits: "+(stop-start)/(1000)+" micro secs");
        assertEquals(10, hits.length);

        for(ScoreDoc hit : hits){
            Document hitDoc = isearcher.doc(hit.doc);
            System.out.println("Doc: "+ hitDoc.toString());
            assertTrue(hitDoc.get("hostName").startsWith("a")
                    || hitDoc.get("hostName").startsWith("A")
                    || hitDoc.get("hostName").contains("A")
                    || hitDoc.get("hostName").contains("a"));
        }

        start = System.nanoTime();
        for(int i=0; i<100; i++) {
            hits = isearcher.search(regexpQuery, 10).scoreDocs;
        }
        stop = System.nanoTime();
        System.out.println("Avg Time taken for fetching the hits: "+(stop-start)/(1000*100)+" micro secs");

        assertEquals(10, hits.length);
    }

    private void testMacAddress(IndexSearcher isearcher, Analyzer analyzer) throws Exception{
        //QueryParser parser = new QueryParser("macAddress", analyzer);
        //Query query = parser.parse("a*");
        RegexpQuery regexpQuery = new RegexpQuery(new Term( "macAddress", ".*"+"a"+".*"));
        long start = System.nanoTime();
        ScoreDoc[] hits = isearcher.search(regexpQuery, 10).scoreDocs;
        long stop = System.nanoTime();
        System.out.println("Time taken for fetching the hits: "+(stop-start)/(1000)+" micro secs");
        assertEquals(10, hits.length);

        for(ScoreDoc hit : hits){
            Document hitDoc = isearcher.doc(hit.doc);
            System.out.println("Doc: "+ hitDoc.toString());
            assertTrue(hitDoc.get("macAddress").startsWith("a")
                    || hitDoc.get("macAddress").startsWith("A")
                    || hitDoc.get("macAddress").contains("A")
                    || hitDoc.get("macAddress").contains("a"));
        }

        start = System.nanoTime();
        for(int i=0; i<100; i++) {
            hits = isearcher.search(regexpQuery, 10).scoreDocs;
        }
        stop = System.nanoTime();
        System.out.println("Avg Time taken for fetching the hits: "+(stop-start)/(1000*100)+" micro secs");

        assertEquals(10, hits.length);
    }
}
