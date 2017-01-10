REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

beacons_hourly = LOAD '/tmp/beacons_hourly.txt' AS (hour:chararray, 
                                                    c_ip:chararray, 
                                                    cs_host:chararray, 
                                                    request_method:chararray,
                                                    user_agent:chararray,
                                                    sld:chararray, 
                                                    confidence:double, 
                                                    risk:double, 
                                                    interval:double);

STORE beacons_hourly INTO 'hbase://BEACONING_ACTIVITY_HOURLY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

beacon_samples_hourly = LOAD '/tmp/beacon_samples_hourly.avro' USING AvroStorage();
STORE beacon_samples_hourly into 'hbase://BEACON_SAMPLES_HOURLY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

beacons_daily = LOAD '/tmp/beacons_daily.txt' AS (day:chararray, 
                                                  c_ip:chararray, 
                                                  cs_host:chararray, 
                                                  request_method:chararray,
                                                  user_agent:chararray,
                                                  sld:chararray, 
                                                  confidence:double, 
                                                  risk:double, 
                                                  interval:double);

STORE beacons_daily INTO 'hbase://BEACONING_ACTIVITY_DAILY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

beacon_samples_daily = LOAD '/tmp/beacon_samples_daily.avro' USING AvroStorage();
STORE beacon_samples_daily into 'hbase://BEACON_SAMPLES_DAILY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

beacons_weekly = LOAD '/tmp/beacons_weekly.txt' AS (week:chararray, 
                                                    c_ip:chararray, 
                                                    cs_host:chararray, 
                                                    request_method:chararray,
                                                    user_agent:chararray,
                                                    sld:chararray, 
                                                    confidence:double, 
                                                    risk:double, 
                                                    interval:double);

STORE beacons_weekly INTO 'hbase://BEACONING_ACTIVITY_WEEKLY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

beacon_samples_weekly = LOAD '/tmp/beacon_samples_weekly.avro' USING AvroStorage();
STORE beacon_samples_weekly into 'hbase://BEACON_SAMPLES_WEEKLY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
