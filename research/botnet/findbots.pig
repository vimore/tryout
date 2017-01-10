REGISTER hdfs://hivecluster2/securityx/jars/*.jar
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();
DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE ISOToHour org.apache.pig.piggybank.evaluation.datetime.truncate.ISOToHour(); 
REGISTER 'torpig_dga.py' using jython AS torpig;

rmf /tmp/bothits.csv
rmf /tmp/torpighits.csv

botdomains = LOAD '/securityx/smalldata/botdomains.csv' USING PigStorage(',') AS (domain:chararray, bot_name:chararray);
bluecoat = LOAD '/securityx/morphline_output/testdemo_jyr5' USING AvroStorage();
bluecoat = FOREACH bluecoat GENERATE values#'sourceNameOrIp' AS c_ip:chararray, 
                                     values#'destinationNameOrIp' AS cs_host:chararray,
                                     UnixToISO((long)values#'startTime') AS date_time,
                                     ISOToHour(UnixToISO((long)values#'startTime')) AS hour,
                                     rawLog AS raw_log:chararray,
                                     values#'requestClientApplication' AS user_agent;
bluecoat = FILTER bluecoat BY cs_host IS NOT NULL AND c_ip IS NOT NULL AND hour IS NOT NULL;

botdomains_hits = JOIN bluecoat BY cs_host, botdomains BY domain;

trimmed_bluecoat = FOREACH bluecoat GENERATE hour, cs_host;
DEFINE torpig_dga `torpig_dga.py` SHIP('torpig_dga.py');
torpig_dga_hits = STREAM trimmed_bluecoat THROUGH torpig_dga AS (hour:chararray, cs_host:chararray);

/* uri = '/reg' and cs-uri-query starts with 'u='   # Bobax */

STORE botdomains_hits INTO '/tmp/bothits.csv' USING PigStorage(',');
STORE torpig_dga_hits INTO '/tmp/torpighits.csv' using PigStorage(',');
