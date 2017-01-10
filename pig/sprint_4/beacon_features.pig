-- Currently hourly. TODO(mdeshon): Make generic for time intervals
-- Currently independent of features.pig. TODO(mdeshon): Integrate into features.pig?

REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

rmf /tmp/beacon_bluecoat_events_markus.txt
rmf /tmp/beacons_markus.txt
rmf /tmp/beacon_sample_hashes_markus.txt
rmf /tmp/beacon_samples_markus.txt
rmf /tmp/beacon_sorted_grouped_markus.txt

bluecoat = LOAD '/securityx/web_proxy_mef/2014/04/*' USING AvroStorage();

-- Extract 2nd level domain from each target, or 'client to server' host.
bluecoat = FOREACH bluecoat GENERATE sourceNameOrIp, 
                                     destinationNameOrIp,
                                     startTimeISO as timestamp,
                                     CONCAT(REPLACE(REGEX_EXTRACT(
                                         rawLog, '\\W(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\W', 1),
                                         ' ', 'T'), '.000Z') AS date_time,
                                     requestClientApplication,
                                     requestMethod,
                                     rawLog;
bluecoat = FILTER bluecoat BY sourceNameOrIp IS NOT NULL AND destinationNameOrIp IS NOT NULL AND date_time IS NOT NULL;
DEFINE bluecoat_rawlog_command `bluecoat_rawlog.py` SHIP ('bluecoat_rawlog.py');
bluecoat_events = STREAM bluecoat THROUGH bluecoat_rawlog_command AS
   (sourceNameOrIp:chararray,
    destinationNameOrIp:chararray,
    timestamp:chararray,
    date_time:chararray,
    requestClientApplication:chararray,
    requestMethod:chararray,
    log_hash:chararray,
    syslog:chararray);
--STORE bluecoat_events INTO '/tmp/beacon_bluecoat_events_markus.txt' USING PigStorage();
links = FOREACH bluecoat_events GENERATE sourceNameOrIp, destinationNameOrIp, date_time, log_hash;
links = FILTER links BY date_time IS NOT NULL AND log_hash IS NOT NULL;
grouped = GROUP links BY (sourceNameOrIp, destinationNameOrIp);
sorted_grouped = FOREACH grouped {
    sorted_datetimes = ORDER links BY date_time;
    GENERATE FLATTEN(group) AS (sourceNameOrIp, destinationNameOrIp), sorted_datetimes.(date_time) AS sorted_datetimes, sorted_datetimes.(log_hash) as sorted_loghashes;
};
sorted_grouped = FOREACH sorted_grouped GENERATE sourceNameOrIp, destinationNameOrIp, sorted_datetimes, sorted_loghashes;
-- STORE sorted_grouped INTO '/tmp/beacon_sorted_grouped_markus.txt' USING PigStorage();
-- Now calculate the beaconing properties.
DEFINE beacon_features_command `beacon_features.py`
    SHIP('beacon_features.py',
         'sparse_hist.py',
         'timestamps.py',
         'pig_parsing.py',
         'domains.py',
         'beacon_confidence.py');
beacons =
    STREAM sorted_grouped
    THROUGH beacon_features_command
    AS (sourceNameOrIp:chararray, 
        destinationNameOrIp:chararray,
        SLD:chararray,
        interval:double,
        riqr:double,
        loghash_list:tuple(sample1:chararray,
                           sample2:chararray,
                           sample3:chararray),
        sparse_histogram:map[],
        hours_active:map[]);
beacons = FILTER beacons BY (interval IS NOT NULL) AND (interval > 5.0);
STORE beacons INTO '/tmp/beacons_markus.txt' USING PigStorage();
beacon_sample_hashes_one = FOREACH beacons GENERATE sourceNameOrIp, 
                                                destinationNameOrIp, 
                                                FLATTEN(loghash_list) AS (sample1, sample2, sample3);
beacon_sample_hashes_two = FOREACH beacon_sample_hashes_one GENERATE sourceNameOrIp, destinationNameOrIp, TOBAG(sample1, sample2, sample3) AS loghash_bag;
beacon_sample_hashes_all = FOREACH beacon_sample_hashes_two GENERATE sourceNameOrIp, destinationNameOrIp, FLATTEN(loghash_bag) AS log_hash;
STORE beacon_sample_hashes_all INTO '/tmp/beacon_sample_hashes_markus.txt' USING PigStorage();

beacon_samples = JOIN beacon_sample_hashes_all BY log_hash, bluecoat_events BY log_hash;
beacon_samples = FOREACH beacon_samples GENERATE
     bluecoat_events::sourceNameOrIp AS sourceNameOrIp,
     bluecoat_events::destinationNameOrIp AS destinationNameOrIp,
     bluecoat_events::date_time AS date_time,
     bluecoat_events::requestClientApplication as requestClientApplication,
     bluecoat_events::requestMethod as requestMethod,
     bluecoat_events::log_hash as log_hash,
     bluecoat_events::syslog AS syslog;
STORE beacon_samples INTO '/tmp/beacon_samples_markus.txt' USING PigStorage();
-- STORE beacon_samples INTO 'hbase://BEACONS_SPRINT4' USING com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
