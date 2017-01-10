REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

import 'udf.macro';

DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

/* bluecoat: {uuid: chararray,
              outputFormat: chararray,
              logType: chararray,
              rawLog: chararray,
              startTimeISO: chararray,
              applicationProtocol: chararray,
              bytesIn: int,
              bytesOut: int,
              cefSignatureId: chararray,
              destinationAddress: chararray,
              destinationDnsDomain: chararray,
              destinationHostName: chararray,
              destinationMacAddress: chararray,
              destinationNameOrIp: chararray,
              destinationPort: chararray,
              deviceAction: chararray,
              deviceAddress: chararray,
              deviceDnsDomain: chararray,
              deviceEventCategory: chararray,
              deviceHostName: chararray,
              endTime: long,
              message: chararray,
              requestMethod: chararray,
              sourceAddress: chararray,
              sourceDnsDomain: chararray,
              sourceHostName: chararray,
              sourceMacAddress: chararray,
              sourceNameOrIp: chararray,
              sourcePort: chararray,
              sourceUserName: chararray,
              startTime: long,
              transportProtocol: chararray,
              reason: chararray,
              requestScheme: chararray,
              requestPath: chararray,
              requestQuery: chararray,
              devicePolicyAction: chararray} */
              
rmf /securityx/time_series/all_series.json
rmf /securityx/time_series/all_entropies.json              
              
bluecoat = LOAD '/securityx/web_proxy_mef/sample' USING AvroStorage();

/* requestClientApplication and responseContentType are missing */
bluecoat = FOREACH bluecoat GENERATE sourceNameOrIp, 
                                     sourceUserName, 
                                     destinationNameOrIp,
                                     cefSignatureId,
                                     deviceAction,
                                     deviceEventCategory,
                                     requestScheme,
                                     requestClientApplication,
                                     devicePolicyAction,
                                     startTimeISO, 
                                     (bytesIn IS NOT NULL ? (bytesIn * 8) : 0) AS bitsIn, 
                                     (bytesOut IS NOT NULL ? (bytesOut * 8) : 0) AS bitsOut;
                                     
/* Bin into 10 minute bins */
DEFINE ten_minute_bins_command `ten_minute_bins.py` SHIP('ten_minute_bins.py');
ten_minute_rounded = STREAM bluecoat THROUGH ten_minute_bins_command AS (sourceNameOrIp:chararray, 
                                                                         sourceUserName:chararray,
                                                                         destinationNameOrIp:chararray,
                                                                         cefSignatureId:chararray,
                                                                         deviceAction:chararray,
                                                                         deviceEventCategory:chararray,
                                                                         requestScheme:chararray,
                                                                         requestClientApplication:chararray,
                                                                         devicePolicyAction:chararray,
                                                                         tenMinutesISO:chararray, 
                                                                         bitsIn:int, 
                                                                         bitsOut:int);
/* Ten minute bins for each key type */
user_name_series = ten_minute_bits_in_out_series(ten_minute_rounded, sourceUserName, tenMinutesISO);
user_name_entropies = count_destination_host_bins(ten_minute_rounded, sourceUserName, tenMinutesISO);
--STORE user_name_series INTO '/securityx/time_series/user_name_series.json' USING JsonStorage();
--STORE user_name_entropies INTO '/securityx/time_series/user_name_entropies.json' USING JsonStorage();

return_code_series = ten_minute_bits_in_out_series(ten_minute_rounded, cefSignatureId, tenMinutesISO);
return_code_entropies = count_destination_host_bins(ten_minute_rounded, cefSignatureId, tenMinutesISO);
--STORE return_code_series INTO '/securityx/time_series/return_code_series.json' USING JsonStorage();
--STORE return_code_entropies INTO '/securityx/time_series/return_code_entropies.json' USING JsonStorage();

device_action_series = ten_minute_bits_in_out_series(ten_minute_rounded, deviceAction, tenMinutesISO);
device_action_entropies = count_destination_host_bins(ten_minute_rounded, deviceAction, tenMinutesISO);
--STORE device_action_series INTO '/securityx/time_series/device_action_series.json' USING JsonStorage();
--STORE device_action_entropies INTO '/securityx/time_series/device_action_entropies.json' USING JsonStorage();

device_event_category_series = ten_minute_bits_in_out_series(ten_minute_rounded, deviceEventCategory, tenMinutesISO);
device_event_category_entropies = count_destination_host_bins(ten_minute_rounded, deviceEventCategory, tenMinutesISO);
--STORE device_event_category_series INTO '/securityx/time_series/device_event_category_series.json' USING JsonStorage();
--STORE device_event_category_entropies INTO '/securityx/time_series/device_event_category_entropies.json' USING JsonStorage();

request_scheme_series = ten_minute_bits_in_out_series(ten_minute_rounded, requestScheme, tenMinutesISO);
request_scheme_entropies = count_destination_host_bins(ten_minute_rounded, requestScheme, tenMinutesISO);
--STORE request_scheme_series INTO '/securityx/time_series/request_scheme_series.json' USING JsonStorage();
--STORE request_scheme_entropies INTO '/securityx/time_series/request_scheme_entropies.json' USING JsonStorage();

device_policy_action_series = ten_minute_bits_in_out_series(ten_minute_rounded, devicePolicyAction, tenMinutesISO);
device_policy_action_entropies = count_destination_host_bins(ten_minute_rounded, devicePolicyAction, tenMinutesISO);
--STORE device_policy_action_series INTO '/securityx/time_series/device_policy_action_series.json' USING JsonStorage();
--STORE device_policy_action_entropies INTO '/securityx/time_series/device_policy_action_entropies.json' USING JsonStorage();

/* Some operations require special processing... */
/* Identify internal hosts */
DEFINE internal_ip_command `internal_external_ip.py -m internal` SHIP('internal_external_ip.py');
source_bluecoat = FOREACH ten_minute_rounded GENERATE sourceNameOrIp, destinationNameOrIp, tenMinutesISO, bitsIn, bitsOut;
internal_hosts = STREAM source_bluecoat THROUGH internal_ip_command AS (sourceNameOrIp:chararray, 
                                                                        destinationNameOrIp:chararray,
                                                                        tenMinutesISO:chararray, 
                                                                        bitsIn:int, 
                                                                        bitsOut:int);
/* Ten minute bins for internal hosts */
internal_hosts_series = ten_minute_bits_in_out_series(internal_hosts, sourceNameOrIp, tenMinutesISO);
internal_hosts_entropies = count_destination_host_bins(internal_hosts, destinationNameOrIp, tenMinutesISO);

--STORE internal_hosts_series INTO '/securityx/time_series/internal_hosts_series.json' USING JsonStorage();
--STORE internal_hosts_entropies INTO '/securityx/time_series/internal_hosts_entropies.json' USING JsonStorage();

/* Identify external hosts */
DEFINE external_ip_command `internal_external_ip.py -m external` SHIP('internal_external_ip.py');
external_hosts = STREAM source_bluecoat THROUGH external_ip_command AS (sourceNameOrIp:chararray, 
                                                                        destinationNameOrIp:chararray,
                                                                        tenMinutesISO:chararray, 
                                                                        bitsIn:int, 
                                                                        bitsOut:int);
external_hosts_series = ten_minute_bits_in_out_series(external_hosts, sourceNameOrIp, tenMinutesISO);
external_hosts_entropies = count_destination_host_bins(external_hosts, destinationNameOrIp, tenMinutesISO);

--STORE external_hosts_series INTO '/securityx/time_series/external_hosts_series.json' USING JsonStorage();
--STORE external_hosts_entropies INTO '/securityx/time_series/external_hosts_entropies.json' USING JsonStorage();

/* Look at overall network metrics */
overall_series = FOREACH (GROUP ten_minute_rounded BY tenMinutesISO) GENERATE
        (chararray)'all' AS type:chararray,
        (chararray)'all' AS groupField:chararray,
        (int)600 AS periodSeconds:int,
        FLATTEN(group) AS tenMinutesISO,
        (double)SUM(ten_minute_rounded.bitsIn)/600.0 AS bitsInPerSecond,
        (double)SUM(ten_minute_rounded.bitsOut)/600.0 AS bitsOutPerSecond,
        (double)COUNT_STAR(ten_minute_rounded)/600.0 AS connectionsPerSecond;
        
overall_series = FOREACH overall_series GENERATE (chararray)type AS type:chararray,
                                                 (chararray)groupField AS groupField:chararray,
                                                 (int)periodSeconds AS periodSeconds:int,
                                                 (chararray)tenMinutesISO AS tenMinutesISO:chararray,
                                                 (double)bitsInPerSecond AS bitsInPerSecond:double,
                                                 (double)bitsOutPerSecond AS bitsOutPerSecond:double,
                                                 (double)connectionsPerSecond AS connectionsPerSecond:double;

--STORE overall_series INTO '/securityx/time_series/overall_series.json' USING JsonStorage();
--STORE overall_entropies INTO '/securityx/time_series/overall_entropies.json' USING JsonStorage();

all_entropies = UNION user_name_entropies, 
                      return_code_entropies, 
                      device_action_entropies, 
                      device_event_category_entropies, 
                      request_scheme_entropies, 
                      device_policy_action_entropies,
                      internal_hosts_entropies,
                      external_hosts_entropies,
                      internal_hosts_entropies,
                      external_hosts_entropies;
all_entropies = FILTER all_entropies BY groupField IS NOT NULL;
STORE all_entropies INTO '/securityx/time_series/all_entropies.json' USING JsonStorage();
                      

all_series = UNION user_name_series, 
                   return_code_series, 
                   device_action_series, 
                   device_event_category_series, 
                   request_scheme_series, 
                   device_policy_action_series, 
                   internal_hosts_series,
                   external_hosts_series,
                   overall_series;
all_series = FILTER all_series BY groupField IS NOT NULL;
STORE all_series INTO '/securityx/time_series/all_series.json' USING JsonStorage();
