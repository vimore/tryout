REGISTER /Users/rjurney/Software/datafu/dist/datafu-1.2.1-SNAPSHOT.jar
REGISTER /Users/rjurney/Downloads/pig-0.11.0-cdh4.4.0/contrib/piggybank/java/piggybank.jar
REGISTER /Users/rjurney/Downloads/pig-0.11.0-cdh4.4.0/contrib/piggybank/java/lib/avro-1.7.4.jar
REGISTER /Users/rjurney/Downloads/pig-0.11.0-cdh4.4.0/contrib/piggybank/java/lib/json-simple-1.1.jar
REGISTER /Users/rjurney/Downloads/pig-0.11.0-cdh4.4.0/test/resources/jackson-core-asl-1.9.9.jar
REGISTER /Users/rjurney/Downloads/pig-0.11.0-cdh4.4.0/test/resources/jackson-mapper-asl-1.9.9.jar

REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */
REGISTER 'beacon_features.py' using streaming_python AS udfs;

DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

rmf /tmp/beacons.txt

bluecoat = LOAD '/securityx/morphline_output/test' USING AvroStorage();
--bluecoat = LOAD '/Users/rjurney/Downloads/part-r-00002.avro' USING AvroStorage();
--bluecoat = LOAD '../../data/bluecoat_datetime.avro' USING AvroStorage();
/* Extract 2nd level domain from each target, or 'client to server' host. */
bluecoat = FOREACH bluecoat GENERATE values#'sourceNameOrIp' AS c_ip:chararray, 
                                     values#'destinationNameOrIp' AS cs_host:chararray,
                                     UnixToISO((long)values#'startTime') AS date_time;
--bluecoat = FOREACH bluecoat GENERATE c_ip, cs_host, date_time;

/* Extract 2nd level domain, then group by it */
sld_cip = FILTER bluecoat BY cs_host IS NOT NULL AND c_ip IS NOT NULL;
sld_cip = FOREACH sld_cip GENERATE udfs.extractSld(cs_host) as sld, c_ip as source;

/* Now take the unique sources per 2nd level domain, and count them - giving you the degree of the 2nd level domain */
sld_degree = FOREACH (GROUP sld_cip BY sld) {
	unique_cips = DISTINCT sld_cip.source;
	GENERATE group AS sld, COUNT_STAR(unique_cips) as counts;
};

/* Now compute the maximum 2nd level degree, and compute the ratio of each 2nd level degree divided by the max */
sld_degree_max_counts = FOREACH (GROUP sld_degree ALL) GENERATE FLATTEN(sld_degree.(sld, counts)), MAX(sld_degree.counts) as max_val;
sld2anomaly = FOREACH sld_degree_max_counts GENERATE sld, udfs.getRatio(counts, max_val) AS anomaly;

/* Calculate the relative inter-quartile range or 'riqr' between c_ip and cs_host pair */
links = FOREACH bluecoat GENERATE ToDate(date_time) as date_time, c_ip, cs_host;
grouped_by_ip2hostpair = GROUP links BY (c_ip, cs_host);
grouped_by_ip2hostpair = FOREACH grouped_by_ip2hostpair {
	date_table = FOREACH links GENERATE GetYear(date_time), GetMonth(date_time), GetDay(date_time), GetHour(date_time), GetMinute(date_time), GetSecond(date_time);
<<<<<<< Updated upstream
	GENERATE FLATTEN(group) AS (c_ip, cs_host), 
	         FLATTEN(udfs.getBeaconConfidence(date_table)) AS (confidence, interval);
};
/* Get the sld of the ip/host pair to join to risk */
ip2host_riqr = FOREACH grouped_by_ip2hostpair GENERATE c_ip, cs_host, confidence, interval, udfs.extractSld(cs_host) AS sld;

/* Now join risk per sld to the anomaly entries */
hosts_and_anomaly = JOIN ip2host_riqr BY sld, sld2anomaly BY sld;
hosts_and_anomaly = FOREACH hosts_and_anomaly GENERATE ip2host_riqr::c_ip AS c_ip,
                                                       ip2host_riqr::cs_host AS cs_host,
                                                       ip2host_riqr::sld AS sld,
                                                       ip2host_riqr::confidence AS confidence,
                                                       ip2host_riqr::interval AS interval,
                                                       sld2anomaly::anomaly AS anomaly;
hosts_and_risks = FOREACH hosts_and_anomaly GENERATE c_ip, cs_host, sld, confidence, interval, anomaly, (confidence * anomaly) AS risk;
-- hosts_and_risks = ORDER hosts_and_risks BY risk DESC;
STORE hosts_and_risks INTO '/tmp/beacons.txt';	
--STORE hosts_and_risks into 'hbase://BEACONING_ACTIVITY' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
=======
	GENERATE FLATTEN(group) AS (c_ip, cs_host), udfs.getBeaconConfidence(date_table) as confidence;
	}

ip2host_riqr = FOREACH grouped_by_ip2hostpair GENERATE c_ip, cs_host, confidence;

--ip2host_riqr = FOREACH grouped_by_ip2hostpair FLATTEN(group) AS (c_ip, cs_host, rqir), udfs.extractSld(cs_host) as sld;
--Add a sld to ip2host_riqr
>>>>>>> Stashed changes
	
