REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

import 'udf.macro';

set job.priority high

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
	          requestClientApplication: chararray,
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
              
rmf /securityx/features/all_success.json
rmf /securityx/features/all_relative.json
rmf /securityx/features/all_entropies.json
              
bluecoat = LOAD '/securityx/web_proxy_mef/sample' USING AvroStorage();

/* requestClientApplication and responseContentType are missing */
bluecoat = FOREACH bluecoat GENERATE sourceNameOrIp, 
				                     destinationNameOrIp,
                                     sourceUserName, 
                                     cefSignatureId AS responseCode,
                                     deviceAction,
                                     deviceEventCategory,
				                     requestClientApplication,
                                     requestScheme,
				                     requestMethod,
                                     devicePolicyAction,
                                     startTimeISO, 
				                     (bytesIn IS NULL ? 0 : bytesIn) AS bytesIn,
				                     (bytesOut IS NULL ? 0 : bytesOut) AS bytesOut;
                                     
/* Bin into hourly bins */
DEFINE hour_bins_command `hour_bins.py` SHIP('hour_bins.py');
hour_rounded = STREAM bluecoat THROUGH hour_bins_command AS (
    sourceNameOrIp:chararray,
    destinationNameOrIp:chararray,
    sourceUserName:chararray,
    responseCode:chararray,
    deviceAction:chararray,
    deviceEventCategory:chararray,
    requestClientApplication:chararray,
    requestScheme:chararray,
    requestMethod:chararray,
    devicePolicyAction:chararray,
    hourISO:chararray, 
    bytesIn:int, 
    bytesOut:int,
    tld:chararray,
    sld:chararray,
    os:chararray,
    browser:chararray);

response_code_success = hour_bytes_feature(hour_rounded, responseCode, hourISO, 3600);
device_action_success = hour_bytes_feature(hour_rounded, deviceAction, hourISO, 3600);
device_event_category_success = hour_bytes_feature(hour_rounded, deviceEventCategory, hourISO, 3600);
request_client_application_success = hour_bytes_feature(hour_rounded, requestClientApplication, hourISO, 3600);
request_scheme_success = hour_bytes_feature(hour_rounded, requestScheme, hourISO, 3600);
request_method_success = hour_bytes_feature(hour_rounded, requestMethod, hourISO, 3600);
device_policy_success = hour_bytes_feature(hour_rounded, devicePolicyAction, hourISO, 3600);
tld_success = hour_bytes_feature(hour_rounded, tld, hourISO, 3600);
sld_success = hour_bytes_feature(hour_rounded, sld, hourISO, 3600);
os_success = hour_bytes_feature(hour_rounded, os, hourISO, 3600);
browser_success = hour_bytes_feature(hour_rounded, browser, hourISO, 3600);

all_success = UNION response_code_success,
                    device_action_success,
                    device_event_category_success,
                    request_client_application_success,
                    request_scheme_success,
                    request_method_success,
                    device_policy_success,
                    tld_success,
                    sld_success,
                    os_success,
                    browser_success;
-- Set fields in order for HBase/Phoenix table
all_success = FOREACH all_success GENERATE seriesType, 
                                           groupField, 
                                           periodSeconds, 
                                           hourISO, 
                                           bytesIn AS bytesIn, 
                                           bytesOut AS bytesOut, 
                                           connections AS connections;
all_success = FILTER all_success BY groupField IS NOT NULL;
STORE all_success INTO '/securityx/features/all_success.json' USING JsonStorage();

response_code_relative = hour_relative_feature(response_code_success, hourISO, 3600);
device_action_relative = hour_relative_feature(device_action_success, hourISO, 3600);
device_event_category_relative = hour_relative_feature(device_event_category_success, hourISO, 3600);
request_client_application_relative = hour_relative_feature(request_client_application_success, hourISO, 3600);
request_scheme_relative = hour_relative_feature(request_scheme_success, hourISO, 3600);
request_method_relative = hour_relative_feature(request_method_success, hourISO, 3600);
device_policy_relative = hour_relative_feature(device_policy_success, hourISO, 3600);
tld_relative = hour_relative_feature(tld_success, hourISO, 3600);
sld_relative = hour_relative_feature(sld_success, hourISO, 3600);
os_relative = hour_relative_feature(os_success, hourISO, 3600);
browser_relative = hour_relative_feature(browser_success, hourISO, 3600);

all_relative = UNION response_code_relative,
                     device_action_relative,
                     device_event_category_relative,
                     request_client_application_relative,
                     request_scheme_relative,
                     request_method_relative,
                     device_policy_relative,
                     tld_relative,
                     sld_relative,
                     os_relative,
                     browser_relative;
all_relative = FOREACH all_relative GENERATE seriesType,
                                             groupField,
                                             periodSeconds,
                                             hourISO,
                                             bytesIn,
                                             bytesOut,
                                             connections;
all_relative = FILTER all_relative BY groupField IS NOT NULL;
STORE all_relative INTO '/securityx/features/all_relative.json' USING JsonStorage();

response_code_entropies = calculate_entropies(hour_rounded, responseCode, hourISO, 3600);
action_entropies = calculate_entropies(hour_rounded, deviceAction, hourISO, 3600);
event_category_entropies = calculate_entropies(hour_rounded, deviceEventCategory, hourISO, 3600);
policy_action_entropies = calculate_entropies(hour_rounded, devicePolicyAction, hourISO, 3600);
request_method_entropies = calculate_entropies(hour_rounded, requestMethod, hourISO, 3600);
user_agent_entropies = calculate_entropies(hour_rounded, requestClientApplication, hourISO, 3600);
request_scheme_entropies = calculate_entropies(hour_rounded, requestScheme, hourISO, 3600);

all_entropies = UNION response_code_entropies, 
                      action_entropies, 
                      event_category_entropies, 
                      policy_action_entropies, 
                      request_method_entropies, 
                      user_agent_entropies, 
                      request_scheme_entropies;
all_entropies = FOREACH all_entropies GENERATE seriesType,
                                               groupField,
                                               periodSeconds,
                                               hourISO,
                                               destConnectionEntropy,
                                               sourceConnectionEntropy,
                                               destBytesInEntropy,
                                               destBytesOutEntropy,
                                               sourceBytesInEntropy,
                                               sourceBytesOutEntropy;
all_entropies = FILTER all_entropies BY groupField IS NOT NULL;
STORE all_entropies INTO '/securityx/features/all_entropies.json' USING JsonStorage();
