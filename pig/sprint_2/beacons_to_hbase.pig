REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

beacons = LOAD '/tmp/beacons.txt' AS (hour:chararray, 
                                     c_ip:chararray, 
                                     cs_host:chararray, 
                                     request_method:chararray,
                                     user_agent:chararray,
                                     sld:chararray, 
                                     confidence:double, 
                                     risk:double, 
                                     interval:double);

STORE beacons INTO 'hbase://BEACONING_ACTIVITY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

beacon_samples = LOAD '/tmp/beacon_samples.avro' USING AvroStorage();
STORE beacon_samples into 'hbase://BEACON_SAMPLES' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
