REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE ISOToHour org.apache.pig.piggybank.evaluation.datetime.truncate.ISOToHour();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

set default_parallel 1

rmf /tmp/beacons_hourly.txt
rmf /tmp/beacon_samples_hourly.avro

bluecoat = LOAD '/securityx/morphline_output/2014/03/*' USING AvroStorage();

/* Extract 2nd level domain from each target, or 'client to server' host. */
bluecoat = FOREACH bluecoat GENERATE values#'sourceNameOrIp' AS c_ip:chararray, 
                                     values#'destinationNameOrIp' AS cs_host:chararray,
                                     UnixToISO((long)values#'startTime') AS date_time,
                                     ISOToHour(UnixToISO((long)values#'startTime')) AS hour,
                                     rawLog AS raw_log:chararray,
                                     values#'requestClientApplication' AS user_agent,
                                     values#'requestMethod' AS request_method;
bluecoat = FILTER bluecoat BY cs_host IS NOT NULL AND c_ip IS NOT NULL AND hour IS NOT NULL;

/* Calculate beacon samples */
/* TODO: Stream sorted timeseries bluecoat data through python script to pick 3 beacon packets. */
/* Create relation with top 3 sample packets' raw logs for each beaconing suspect */
beacon_samples = FOREACH (GROUP bluecoat BY (c_ip, cs_host, hour)) {
    sorted = ORDER bluecoat BY date_time;
    first_3 = LIMIT sorted 3;
    GENERATE FLATTEN(group) AS (c_ip, cs_host, hour), FLATTEN(first_3.(date_time, raw_log)) AS (date_time, raw_log);
};
beacon_samples = FOREACH beacon_samples GENERATE date_time, hour, c_ip, cs_host, raw_log;
STORE beacon_samples INTO '/tmp/beacon_samples_hourly.avro' USING AvroStorage();

/* Now extract the reputation in terms of SLD 'Second Level Domain. */
/* Extract 2nd level domain, then group by it */
DEFINE second_level_command `second_level_domain.py -i 1` SHIP('second_level_domain.py');
trimmed_bluecoat = FOREACH bluecoat GENERATE c_ip, cs_host, hour, request_method, user_agent;
sld_cip = STREAM trimmed_bluecoat THROUGH second_level_command AS (source:chararray, sld:chararray, hour:chararray, request_method:chararray, user_agent:chararray);
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

/* Now compute the SLD anomaly score */                                                                    
sld2anomaly = FOREACH sld_degree_max_counts GENERATE hour,
                                                     sld, 
                                                     ((double)1.0 - (double)counts/(double)max_val) AS anomaly:double;
/* Always define an edge in terms of c_ip and SLD, and then add alexa/user-agent/request method. 
   When multiple values of any are present, use the least common one */                                                     

/* Now join/add the Alexa rank as a field, calculate its anomaly: .1 * log10(alexa) + 0.3. If not in Alexa: 1.0. */
alexa = LOAD '/securityx/smalldata/top-1m.csv' USING PigStorage(',') AS (ranking:int, domain:chararray);
with_alexa = JOIN sld_cip BY sld LEFT OUTER, alexa BY domain;
with_alexa = FOREACH with_alexa GENERATE sld AS sld,
                                         hour AS hour,
                                         (double)ranking AS ranking:double;
alexa_score = FOREACH with_alexa GENERATE sld, hour, (ranking IS NOT NULL ? (.1 * LOG10(ranking) + 0.3) : 1.0) AS score;
alexa_score = DISTINCT alexa_score;

/* Now compute the user-agent anomaly score */
/* Get counts of user agents per edge */
user_agent_per_edge_hour = FOREACH (GROUP sld_cip BY (source, sld, hour, user_agent)) GENERATE 
    FLATTEN(group) AS (source, sld, hour, user_agent),
    COUNT_STAR(sld_cip) AS total;
user_agent_totals = FOREACH (GROUP user_agent_per_edge_hour BY (hour, user_agent)) GENERATE 
    FLATTEN(group) AS (hour, user_agent),
    COUNT_STAR(user_agent_per_edge_hour) AS total;
user_agent_max = FOREACH (GROUP user_agent_totals BY hour) GENERATE group AS hour, 
                                                                    FLATTEN(user_agent_totals.user_agent) AS user_agent, 
                                                                    (double)MAX(user_agent_totals.total) AS max_val:double;
user_agent_totals_with_max = JOIN user_agent_totals BY (hour, user_agent), user_agent_max BY (hour, user_agent);  
user_agent_anomalies = FOREACH user_agent_totals_with_max GENERATE 
    user_agent_totals::hour AS hour,
    user_agent_totals::user_agent AS user_agent,
    ((double)1.0 - ((double)user_agent_totals::total/(double)user_agent_max::max_val)) AS anomaly;
                                                            
/* Now compute the request method anomaly score */
/* Get counts of request methods per edge */
method_per_edge_hour = FOREACH (GROUP sld_cip BY (source, sld, hour, request_method)) GENERATE
    FLATTEN(group) AS (source, sld, hour, request_method),
    COUNT_STAR(sld_cip) AS total;
request_method_totals = FOREACH (GROUP method_per_edge_hour BY (hour, request_method)) GENERATE 
    FLATTEN(group) AS (hour, request_method),
    COUNT_STAR(method_per_edge_hour) AS total;
request_method_max = FOREACH (GROUP request_method_totals BY hour) GENERATE group AS hour,
                                                                            FLATTEN(request_method_totals.request_method) AS request_method,
                                                                            MAX(request_method_totals.total) AS max_val;
request_method_totals_with_max = JOIN request_method_totals BY (hour, request_method), request_method_max BY (hour, request_method);
request_method_anomalies = FOREACH request_method_totals_with_max GENERATE 
    request_method_totals::hour AS hour,
    request_method_totals::request_method AS request_method,
    ((double)1.0 - ((double)request_method_totals::total/(double)request_method_max::max_val)) AS anomaly;

/* Now calculate the beaconing properties. */
/* Calculate the relative inter-quartile range or 'riqr' between c_ip and cs_host pair */
links = FOREACH bluecoat GENERATE date_time, hour, c_ip, cs_host, request_method, user_agent;
grouped_by_ip2hostpair = GROUP links BY (c_ip, cs_host, hour, request_method, user_agent);
sorted_grouped_by_ip2hostpair = FOREACH grouped_by_ip2hostpair {
    sorted_datetimes = ORDER links BY date_time;
    GENERATE FLATTEN(group) AS (c_ip, cs_host, hour, request_method, user_agent), sorted_datetimes.(date_time) AS sorted_datetimes;
};
sorted_grouped_by_ip2hostpair = FOREACH sorted_grouped_by_ip2hostpair GENERATE c_ip, cs_host, hour, request_method, sorted_datetimes, user_agent;

DEFINE confidence_and_interval_command `confidence_and_interval.py` SHIP('confidence_and_interval.py');
confidence_and_interval = STREAM sorted_grouped_by_ip2hostpair 
                          THROUGH confidence_and_interval_command AS (c_ip:chararray, 
                                                                      cs_host:chararray, 
                                                                      hour:chararray,
                                                                      request_method:chararray, 
                                                                      user_agent:chararray,                                                                     
                                                                      confidence:double, 
                                                                      interval:double);

DEFINE second_level_domain_2_command `second_level_domain.py -i 2` SHIP('second_level_domain.py');
ip2host_riqr = STREAM confidence_and_interval 
               THROUGH second_level_domain_2_command AS (c_ip:chararray, 
                                                         cs_host:chararray, 
                                                         hour:chararray, 
                                                         request_method:chararray,
                                                         user_agent:chararray,
                                                         confidence:double, 
                                                         interval:double, 
                                                         sld:chararray);

/* Now combine the SLD, user-agent and request method anomaly scores: (1 - (1 - SLD_anomaly)(1 - UA_anomaly)(1 - method_anomaly))(Alexa_anomaly) */
/* Now bring in Alexa score for the combined anomaly calculation */
hosts_and_sld_anomaly = JOIN ip2host_riqr BY (sld, hour), sld2anomaly BY (sld, hour), alexa_score BY (sld, hour);
hosts_and_sld_anomaly = FOREACH hosts_and_sld_anomaly GENERATE ip2host_riqr::c_ip AS c_ip,
                                                               ip2host_riqr::cs_host AS cs_host,
                                                               ip2host_riqr::hour AS hour,
                                                               ip2host_riqr::request_method AS request_method,
                                                               ip2host_riqr::user_agent AS user_agent,
                                                               ip2host_riqr::sld AS sld,
                                                               ip2host_riqr::confidence AS confidence,
                                                               ip2host_riqr::interval AS interval,
                                                               sld2anomaly::anomaly AS sld_anomaly,
                                                               alexa_score::score AS alexa_score;

/* Now bring in request method anomaly for the combined anomaly calculation */
hosts_and_sld_with_request_method = JOIN hosts_and_sld_anomaly BY (hour, request_method), request_method_anomalies BY (hour, request_method); 
hosts_and_sld_with_request_method = FOREACH hosts_and_sld_with_request_method GENERATE hosts_and_sld_anomaly::c_ip AS c_ip,
                                                                                     hosts_and_sld_anomaly::cs_host AS cs_host,
                                                                                     hosts_and_sld_anomaly::hour AS hour,
                                                                                     hosts_and_sld_anomaly::request_method AS request_method,
                                                                                     hosts_and_sld_anomaly::user_agent AS user_agent,
                                                                                     hosts_and_sld_anomaly::sld AS sld,
                                                                                     hosts_and_sld_anomaly::confidence AS confidence,
                                                                                     hosts_and_sld_anomaly::interval AS interval,
                                                                                     hosts_and_sld_anomaly::sld_anomaly AS sld_anomaly,
                                                                                     hosts_and_sld_anomaly::alexa_score AS alexa_score,
                                                                                     request_method_anomalies::anomaly AS request_method_anomaly;

/* Finally, bring in user agent anomaly for the combined anomaly calculation */
hosts_sld_request_method_and_user_agent = JOIN hosts_and_sld_with_request_method BY (hour, user_agent), user_agent_anomalies BY (hour, user_agent);
hosts_sld_request_method_and_user_agent = FOREACH hosts_sld_request_method_and_user_agent GENERATE hosts_and_sld_with_request_method::c_ip AS c_ip,
                                                                                                   hosts_and_sld_with_request_method::cs_host AS cs_host,
                                                                                                   hosts_and_sld_with_request_method::hour AS hour,
                                                                                                   hosts_and_sld_with_request_method::request_method AS request_method,
                                                                                                   hosts_and_sld_with_request_method::user_agent AS user_agent,
                                                                                                   hosts_and_sld_with_request_method::sld AS sld,
                                                                                                   hosts_and_sld_with_request_method::confidence AS confidence,
                                                                                                   hosts_and_sld_with_request_method::interval AS interval,
                                                                                                   hosts_and_sld_with_request_method::sld_anomaly AS sld_anomaly,
                                                                                                   hosts_and_sld_with_request_method::alexa_score AS alexa_score,
                                                                                                   hosts_and_sld_with_request_method::request_method_anomaly AS request_method_anomaly,
                                                                                                   user_agent_anomalies::anomaly AS user_agent_anomaly;

/* Compute final score */
hosts_and_risks = FOREACH hosts_sld_request_method_and_user_agent GENERATE 
    hour,
    c_ip, 
    cs_host, 
    request_method,
    user_agent,
    sld, 
    confidence, 
    ((1.0 - ((1.0 - sld_anomaly) * (1.0 - user_agent_anomaly) * (1.0 - request_method_anomaly))) * alexa_score) * 100 AS risk:double, 
    interval;
host_and_risks = FILTER hosts_and_risks BY risk > 0.0;
STORE hosts_and_risks INTO '/tmp/beacons_hourly.txt';
