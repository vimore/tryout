REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

all_series = LOAD '/securityx/time_series/all_series.json' USING JsonLoader();
STORE all_series INTO 'hbase://HTTP_TIME_SERIES' USING com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

all_entropies = LOAD '/securityx/time_series/all_entropies.json' USING JsonLoader();
STORE all_entropies INTO 'hbase://ENTROPY_TIME_SERIES' USING com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
