REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

rmf /securityx/web_proxy_mef/sample

bluecoat = LOAD '/securityx/morphline_output/2014/03/*' USING AvroStorage();

web_proxy_mef = FOREACH bluecoat GENERATE 
                                     datafu.pig.random.RandomUUID() AS uuid:chararray,
                                     outputFormat AS outputFormat:chararray,
                                     'bluecoat' AS logType:chararray,
                                     rawLog AS rawLog:chararray,
                                     UnixToISO((long)values#'startTime') AS startTimeISO:chararray,
                                     values#'applicationProtocol' AS applicationProtocol:chararray,
                                     (int)values#'bytesIn' AS bytesIn:int,
                                     (int)values#'bytesOut' AS bytesOut:int,
                                     values#'cefSignatureId' AS cefSignatureId:chararray,
                                     values#'destinationAddress' AS destinationAddress:chararray,
                                     values#'destinationDnsDomain' AS destinationDnsDomain:chararray,
                                     values#'destinationHostName' AS destinationHostName:chararray,
                                     values#'destinationMacAddress' AS destinationMacAddress:chararray,
                                     values#'destinationNameOrIp' AS destinationNameOrIp:chararray,
                                     values#'destinationPort' AS destinationPort:chararray,
                                     values#'deviceAction' AS deviceAction:chararray,
                                     values#'deviceAddress' AS deviceAddress:chararray,
                                     values#'deviceDnsDomain' AS deviceDnsDomain:chararray,
                                     values#'deviceEventCategory' AS deviceEventCategory:chararray,
                                     values#'deviceHostName' AS deviceHostName:chararray,
                                     (long)values#'endTime' AS endTime:long,
                                     values#'message' AS message:chararray,
                                     values#'requestMethod' AS requestMethod:chararray,
                                     values#'requestClientApplication' AS requestClientApplication:chararray,
                                     values#'sourceAddress' AS sourceAddress:chararray,
                                     values#'sourceDnsDomain' AS sourceDnsDomain:chararray,
                                     values#'sourceHostName' AS sourceHostName:chararray,
                                     values#'sourceMacAddress' AS sourceMacAddress:chararray,
                                     values#'sourceNameOrIp' AS sourceNameOrIp:chararray, 
                                     values#'sourcePort' AS sourcePort:chararray,
                                     values#'sourceUserName' AS sourceUserName:chararray,
                                     (long)values#'startTime' AS startTime:long,
                                     values#'transportProtocol' AS transportProtocol:chararray,
                                     values#'reason' AS reason:chararray,
                                     values#'requestScheme' AS requestScheme:chararray,
                                     values#'requestPath' AS requestPath:chararray,
                                     values#'requestQuery' AS requestQuery:chararray,
                                     values#'devicePolicyAction' AS devicePolicyAction:chararray;

STORE web_proxy_mef INTO '/securityx/web_proxy_mef/sample' USING AvroStorage();
