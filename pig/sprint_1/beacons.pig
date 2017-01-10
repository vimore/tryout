REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE ISOToHour org.apache.pig.piggybank.evaluation.datetime.truncate.ISOToHour();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

set default_parallel 5

rmf /tmp/beacons.txt
rmf /tmp/beacon_samples.avro

bluecoat = LOAD '/securityx/morphline_output/testdemo_1' USING AvroStorage();

/* Extract 2nd level domain from each target, or 'client to server' host. */
bluecoat = FOREACH bluecoat GENERATE values#'sourceNameOrIp' AS c_ip:chararray, 
                                     values#'destinationNameOrIp' AS cs_host:chararray,
                                     UnixToISO((long)values#'startTime') AS date_time,
                                     ISOToHour(UnixToISO((long)values#'startTime')) AS hour,
                                     rawLog AS raw_log:chararray;
bluecoat = FILTER bluecoat BY cs_host IS NOT NULL AND c_ip IS NOT NULL AND hour IS NOT NULL;

/* Create relation with top 3 sample packets' raw logs for each beaconing suspect */
beacon_samples = FOREACH (GROUP bluecoat BY (c_ip, cs_host, hour)) {
    sorted = ORDER bluecoat BY date_time;
    first_3 = LIMIT sorted 3;
    GENERATE FLATTEN(group) AS (c_ip, cs_host, hour), FLATTEN(first_3.(date_time, raw_log)) AS (date_time, raw_log);
};

beacon_samples = FOREACH beacon_samples GENERATE date_time, hour, c_ip, cs_host, raw_log;
STORE beacon_samples INTO '/tmp/beacon_samples.avro' USING AvroStorage();

/* Extract 2nd level domain, then group by it */
DEFINE second_level_command `second_level_domain.py` SHIP('second_level_domain.py');
sld_cip = STREAM bluecoat THROUGH second_level_command AS (source:chararray, sld:chararray, hour:chararray);
/* Ugly fix for null tuples coming out of last line */
sld_cip = FILTER sld_cip BY (source IS NOT NULL) AND (sld IS NOT NULL) AND (hour IS NOT NULL);

/* Now take the unique sources per 2nd level domain, and count them - giving you the degree of the 2nd level domain */
sld_degree = FOREACH (GROUP sld_cip BY (sld, hour)) {
	unique_cips = DISTINCT sld_cip.source;
	GENERATE FLATTEN(group) AS (sld, hour), 
	         COUNT_STAR(unique_cips) as counts;
};

/* Now compute the maximum 2nd level degree, and compute the ratio of each 2nd level degree divided by the max */
sld_degree_max_counts = FOREACH (GROUP sld_degree BY hour) GENERATE group AS hour, 
                                                                    FLATTEN(sld_degree.(sld, counts)) AS (sld, counts), 
                                                                    MAX(sld_degree.counts) as max_val;
sld2anomaly = FOREACH sld_degree_max_counts GENERATE hour,
                                                     sld, 
                                                     ((double)1.0 - (double)counts/(double)max_val) AS anomaly:double;

/* Calculate the relative inter-quartile range or 'riqr' between c_ip and cs_host pair */
links = FOREACH bluecoat GENERATE date_time, hour, c_ip, cs_host;
grouped_by_ip2hostpair = GROUP links BY (c_ip, cs_host, hour);
sorted_grouped_by_ip2hostpair = FOREACH grouped_by_ip2hostpair {
    sorted_datetimes = ORDER links BY date_time;
    GENERATE FLATTEN(group) AS (c_ip, cs_host, hour), sorted_datetimes.(date_time) AS sorted_datetimes;
};

DEFINE confidence_and_interval_command `confidence_and_interval.py` SHIP('confidence_and_interval.py');
confidence_and_interval = STREAM sorted_grouped_by_ip2hostpair 
                          THROUGH confidence_and_interval_command AS (c_ip:chararray, 
                                                                      cs_host:chararray, 
                                                                      hour:chararray, 
                                                                      confidence:double, 
                                                                      interval:double);

DEFINE second_level_domain_2_command `second_level_domain_2.py` SHIP('second_level_domain_2.py');
ip2host_riqr = STREAM confidence_and_interval 
               THROUGH second_level_domain_2_command AS (c_ip:chararray, 
                                                         cs_host:chararray, 
                                                         hour:chararray, 
                                                         confidence:double, 
                                                         interval:double, 
                                                         sld:chararray);

hosts_and_anomaly = JOIN ip2host_riqr BY (sld, hour), sld2anomaly BY (sld, hour);
hosts_and_anomaly = FOREACH hosts_and_anomaly GENERATE ip2host_riqr::c_ip AS c_ip,
                                                       ip2host_riqr::cs_host AS cs_host,
                                                       ip2host_riqr::hour AS hour,
                                                       ip2host_riqr::sld AS sld,
                                                       ip2host_riqr::confidence AS confidence,
                                                       ip2host_riqr::interval AS interval,
                                                       sld2anomaly::anomaly AS anomaly;

hosts_and_risks = FOREACH hosts_and_anomaly GENERATE hour, 
                                                     c_ip, 
                                                     cs_host, 
                                                     sld, 
                                                     confidence, 
                                                     (confidence * anomaly * 100) AS risk:double, 
                                                     anomaly, 
                                                     interval;
STORE hosts_and_risks INTO '/tmp/beacons.txt';
