package com.securityx.model.mef.morphline.command.avro;

import com.google.common.collect.ListMultimap;
import com.securityx.logcollection.utils.MorphlineResourceLoader;
import com.securityx.model.mef.morphline.command.util.AssertRecordOutCommand;
import com.securityx.model.mef.morphline.utils.MorphlineHarness;
import com.typesafe.config.Config;
import junit.framework.Assert;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SanitizeSolrFieldValuesBuilderTest {

    @Test
    public void test() throws Exception {
        List<Record> records = avroRecordToMorphlineRecord(readAvro("web_proxy_mef_hermes_2_2_2.avro"));
        Record[] recordsArray = new Record[records.size()];
        records.toArray(recordsArray);
        //ensure that the input data contains \uFFFF
        boolean containsUtf8 = false;
        for(Record rec : recordsArray){
            ListMultimap<String, Object> map = rec.getFields();
            for(String key : map.keySet()){
                List<Object> ol = map.get(key);
                for(Object o : ol){
                    if(o instanceof Utf8){
                        if(o.toString().contains("\uFFFF")){
                            containsUtf8 = true;
                        }
                    }
                }
            }
        }
        Assert.assertTrue(containsUtf8);
        Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-sanitize-values-command.conf");
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
        MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
        morphlineHarness.startup(outCommand);
        morphlineHarness.feedRecords(recordsArray);
        morphlineHarness.shutdown();
        Assert.assertTrue(outCommand.getNumRecords() == 2);
        //ensure that the output data does not contain \uFFFF
        for(int i=0; i<outCommand.getNumRecords();i++){
            Record rec = outCommand.getRecord(i);
            ListMultimap<String, Object> map = rec.getFields();
            for(String key : map.keySet()){
                List<Object> ol = map.get(key);
                for(Object o : ol){
                    if(o instanceof CharSequence){
                        Assert.assertFalse(o.toString().contains("\uFFFF"));
                    }
                }
            }
        }
    }

    @Test
    public void test2() throws Exception {
        List<Record> records = avroRecordToMorphlineRecord(readAvro("web_proxy_mef.avro"));
        Record[] recordsArray = new Record[records.size()];
        records.toArray(recordsArray);

        Config morphlineConf = MorphlineResourceLoader.getConfFile("test/test-sanitize-values-command.conf");
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        AssertRecordOutCommand outCommand = new AssertRecordOutCommand(morphlineContext);
        MorphlineHarness morphlineHarness = new MorphlineHarness(morphlineContext, morphlineConf, "morphline1");
        morphlineHarness.startup(outCommand);
        morphlineHarness.feedRecords(recordsArray);
        morphlineHarness.shutdown();
        for(int i=0; i<outCommand.getNumRecords();i++){
            Record rec = outCommand.getRecord(i);
            ListMultimap<String, Object> map = rec.getFields();
            for(String key : map.keySet()){
                List<Object> ol = map.get(key);
                for(Object o : ol){
                    Assert.assertFalse(o instanceof List);
                }
            }
        }
    }
    private List<Record> avroRecordToMorphlineRecord(List<GenericRecord> genericRecords){
        ArrayList<Record> list = new ArrayList<Record>();
        for(GenericRecord gr : genericRecords){
            Record rec = new Record();
            List<Schema.Field> fields = gr.getSchema().getFields();
            for(Schema.Field field: fields){
                String name = field.name();
                rec.put(name, gr.get(name));
            }
            list.add(rec);
        }
        return list;
    }
    private List<GenericRecord> readAvro(String fileName) throws Exception{
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(new SeekableByteArrayInput(getByteArray(is)), datumReader);
        ArrayList<GenericRecord> list = new ArrayList<GenericRecord>();
        while (dataFileReader.hasNext()) {
            GenericRecord genericRecord = dataFileReader.next();
            list.add(genericRecord);
        }
        return list;
    }

    private byte[] getByteArray(InputStream is) throws Exception{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
