REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */
rawdata = LOAD '/tmp/laposte/data2cluster' USING PigStorage();

DEFINE command0 `peergroup.py` SHIP ('peergroup.py', 'kcenter.py');
results = STREAM rawdata THROUGH command0 AS (count:chararray);



#rmf /tmp/laposte_data2_account;
#STORE stats INTO '/tmp/laposte_data2_account' using PigStorage();

