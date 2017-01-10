REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

set default_parallel 5

web_proxy_mef = LOAD '/securityx/web_proxy_mef/sample' USING AvroStorage();
STORE web_proxy_mef into 'hbase://WEB_PROXY_MEF' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
