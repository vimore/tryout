REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */
DEFINE $datefunc org.apache.pig.piggybank.evaluation.datetime.truncate.$datefunc();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

rmf /e8/$environment/web_proxy_mef/$year/$month/$day

set default_parallel 6

rawdata = LOAD '/securityx/research/laposte/new_*' USING PigStorage() AS (data:chararray);

DEFINE process_command `cronus_to_mef.py` SHIP('cronus_to_mef.py');
bluecoat = STREAM rawdata THROUGH process_command AS (
    sourceNameOrIp:chararray,
    destinationNameOrIp:chararray,
    startTimeISO:chararray,
    year:chararray,
    month:chararray,
    day:chararray,
    hour:chararray,
    requestClientApplication:chararray,
    requestMethod:chararray,
    rawLog:chararray
);
bluecoat = FILTER bluecoat
    BY year == '$year' AND month == '$month'
    AND day == '$day';
bluecoat = FOREACH bluecoat GENERATE sourceNameOrIp, 
                                     destinationNameOrIp,
                                     startTimeISO,
                                     requestClientApplication,
                                     requestMethod,
				     rawLog,
				     hour;
SPLIT bluecoat INTO
b00 IF hour == '00',
b01 IF hour == '01',
b02 IF hour == '02',
b03 IF hour == '03',
b04 IF hour == '04',
b05 IF hour == '05',
b06 IF hour == '06',
b07 IF hour == '07',
b08 IF hour == '08',
b09 IF hour == '09',
b10 IF hour == '10',
b11 IF hour == '11',
b12 IF hour == '12',
b13 IF hour == '13',
b14 IF hour == '14',
b15 IF hour == '15',
b16 IF hour == '16',
b17 IF hour == '17',
b18 IF hour == '18',
b19 IF hour == '19',
b20 IF hour == '20',
b21 IF hour == '21',
b22 IF hour == '22',
b23 IF hour == '23';

STORE b00 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/00' USING AvroStorage();
STORE b01 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/01' USING AvroStorage();
STORE b02 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/02' USING AvroStorage();
STORE b03 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/03' USING AvroStorage();
STORE b04 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/04' USING AvroStorage();
STORE b05 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/05' USING AvroStorage();
STORE b06 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/06' USING AvroStorage();
STORE b07 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/07' USING AvroStorage();
STORE b08 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/08' USING AvroStorage();
STORE b09 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/09' USING AvroStorage();
STORE b10 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/10' USING AvroStorage();
STORE b11 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/11' USING AvroStorage();
STORE b12 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/12' USING AvroStorage();
STORE b13 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/13' USING AvroStorage();
STORE b14 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/14' USING AvroStorage();
STORE b15 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/15' USING AvroStorage();
STORE b16 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/16' USING AvroStorage();
STORE b17 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/17' USING AvroStorage();
STORE b18 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/18' USING AvroStorage();
STORE b19 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/19' USING AvroStorage();
STORE b20 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/20' USING AvroStorage();
STORE b21 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/21' USING AvroStorage();
STORE b22 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/22' USING AvroStorage();
STORE b23 INTO '/e8/$environment/web_proxy_mef/$year/$month/$day/23' USING AvroStorage();
