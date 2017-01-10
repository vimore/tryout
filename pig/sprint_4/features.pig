REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

import 'features_udf.macro';

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
              
rmf /securityx/features/all_relative.json
rmf /securityx/features/all_entropies.json
              
bluecoat = LOAD '/securityx/web_proxy_mef/sample' USING AvroStorage();

/* requestClientApplication and responseContentType are missing */
bluecoat = FOREACH bluecoat GENERATE 
    sourceNameOrIp, 
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

response_code_counts = counts_feature(hour_rounded, responseCode, hourISO);
device_action_counts = counts_feature(hour_rounded, deviceAction, hourISO);
device_event_category_counts = counts_feature(hour_rounded, deviceEventCategory, hourISO);
request_client_application_counts = counts_feature(hour_rounded, requestClientApplication, hourISO);
request_scheme_counts = counts_feature(hour_rounded, requestScheme, hourISO);
request_method_counts = counts_feature(hour_rounded, requestMethod, hourISO);
device_policy_counts = counts_feature(hour_rounded, devicePolicyAction, hourISO);
tld_counts = counts_feature(hour_rounded, tld, hourISO);
sld_counts = counts_feature(hour_rounded, sld, hourISO);
os_counts = counts_feature(hour_rounded, os, hourISO);
browser_counts = counts_feature(hour_rounded, browser, hourISO);

all_counts = UNION
    response_code_counts,
    device_action_counts,
    device_event_category_counts,
    request_client_application_counts,
    request_scheme_counts,
    request_method_counts,
    device_policy_counts,
    tld_counts,
    sld_counts,
    os_counts,
    browser_counts;
STORE all_counts INTO '/securityx/features/all_counts.json' USING JsonStorage();

response_code_relative = relative_counts_feature(response_code_counts, responseCode, hourISO);
device_action_relative = relative_counts_feature(device_action_counts, deviceAction, hourISO);
device_event_category_relative = relative_counts_feature(device_event_category_counts, deviceEventCategory, hourISO);
request_client_application_relative = relative_counts_feature(request_client_application_counts, requestClientApplication, hourISO);
request_scheme_relative = relative_counts_feature(request_scheme_counts, requestScheme, hourISO);
request_method_relative = relative_counts_feature(request_method_counts, requestMethod, hourISO);
device_policy_relative = relative_counts_feature(device_policy_counts, devicePolicyAction, hourISO);
tld_relative = relative_counts_feature(tld_counts, tld, hourISO);
sld_relative = relative_counts_feature(sld_counts, sld, hourISO);
os_relative = relative_counts_feature(os_counts, os, hourISO);
browser_relative = relative_counts_feature(browser_counts, browser, hourISO);

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
STORE all_entropies INTO '/securityx/features/all_entropies.json' USING JsonStorage();
