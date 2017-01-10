REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();
DEFINE AvroStorage org.apache.pig.piggybank.storage.avro.AvroStorage();

rmf /securityx/flow_mef/sample
/*
events = LOAD '/securityx/morphline_output/2014/03/*' USING AvroStorage();
*/
events = LOAD 'hdfs://hivecluster2:8020/securityx/morphline_output/testdemo_jyrlog8/' USING AvroStorage();
SPLIT events INTO formated_events IF (outputFormat is not null), default_events IF (outputFormat is null); 
SPLIT formated_events INTO web_proxy_events IF (outputFormat == 'WebProxyMef' ), flow_events IF (outputFormat == 'FlowMef'); 

flow_mef = FOREACH flow_events GENERATE 
                                     datafu.pig.random.RandomUUID() AS uuid:chararray,
                                     outputFormat AS outputFormat:chararray,
                                     rawLog AS rawLog:chararray,
                                     UnixToISO((long)values#'startTime') AS startTimeISO:chararray,
                                     values#'applicationProtocol' AS applicationProtocol:chararray,
                                     (int)values#'bytesIn' AS bytesIn:int,
                                     (int)values#'bytesOut' AS bytesOut:int,
                                     values#'destinationAddress' AS destinationAddress:chararray,
                                     values#'destinationDnsDomain' AS destinationDnsDomain:chararray,
                                     values#'destinationHostName' AS destinationHostName:chararray,
                                     values#'destinationMacAddress' AS destinationMacAddress:chararray,
                                     values#'destinationMask' AS destinationMask:chararray,
                                     values#'destinationVlan' AS destinationVlan:int,
                                     values#'destinationNameOrIp' AS destinationNameOrIp:chararray,
                                     values#'destinationTranslatedAddress' AS destinationTranslatedAddress:chararray,
                                     values#'destinationTranslatedPort' AS destinationTranslatedPort:chararray,
                                     values#'destinationPort' AS destinationPort:chararray,
                                     values#'deviceAction' AS deviceAction:chararray,
                                     values#'deviceAddress' AS deviceAddress:chararray,
                                     values#'deviceDnsDomain' AS deviceDnsDomain:chararray,
                                     values#'deviceInboundInterface' AS deviceInboundInterface:chararray,
                                     values#'deviceHostName' AS deviceHostName:chararray,
                                     values#'deviceTranslatedAddress' AS deviceTranslatedAddress:chararray,
                                     (long)values#'endTime' AS endTime:long,
                                     values#'sourceAddress' AS sourceAddress:chararray,
                                     values#'sourceDnsDomain' AS sourceDnsDomain:chararray,
                                     values#'sourceHostName' AS sourceHostName:chararray,
                                     values#'sourceMacAddress' AS sourceMacAddress:chararray,
                                     values#'sourceNameOrIp' AS sourceNameOrIp:chararray, 
                                     values#'sourceTranslatedAddress' AS sourceTranslatedAddress:chararray,
                                     values#'sourceTranslatedPort' AS sourceTranslatedPort:chararray,
                                     values#'sourcePort' AS sourcePort:chararray,
                                     values#'sourceMask' AS sourceMask:chararray,
                                     values#'sourceVlan' AS sourceVlan:int,
                                     (long)values#'startTime' AS startTime:long,
                                     values#'transportProtocol' AS transportProtocol:chararray,
                                     values#'destinationAutonousSystem' AS destinationAutonousSystem:chararray,
                                     values#'sourceAutonousSystem' AS sourceAutonousSystem:chararray,
                                     values#'interfaceDescription' AS interfaceDescription:chararray,
                                     values#'interfaceName' AS interfaceName:chararray,
                                     (long)values#'packetsIn' AS packetsIn:long,
                                     values#'nextHopAddress' AS nextHopAddress:chararray,
                                     (long)values#'packetsOut' AS packetsOut:long,
                                     values#'samplingInterval' AS samplingInterval:int,
                                     values#'sourceAutonomousSystem' AS sourceAutonomousSystem:chararray,
                                     values#'sourceTos' AS sourceTos:int,
                                     values#'tcpFlags' AS tcpFlags:int,
                                     (long)values#'totalBytesExp' AS totalBytesExp:long,
                                     (long)values#'totalFlowsExp' AS totalFlowsExp:long,
                                     (long)values#'totalPacketsExp' AS totalPacketsExp:long;
                                     
web_proxy_mef = FOREACH web_proxy_events GENERATE 
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

STORE web_proxy_mef INTO 'hdfs://hivecluster2:8020/securityx/web_proxy_mef/$year/$month/$day/$hour' USING AvroStorage();                                     
STORE flow_mef INTO 'hdfs://hivecluster2:8020/securityx/flow_mef/$year/$month/$day/$hour' USING AvroStorage();                                     
