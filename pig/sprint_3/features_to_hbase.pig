REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

all_success = LOAD '/securityx/features/all_success.json' USING JsonLoader();
STORE all_success INTO 'hbase://BYTE_FEATURES' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

all_relative = LOAD '/securityx/features/all_relative.json' USING JsonLoader();
STORE all_relative INTO 'hbase://RELATIVE_FEATURES' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');

all_entropies = LOAD '/securityx/entropy_features.json/$year/$month/$day/$hour' USING JsonLoader();
STORE all_entropies INTO 'hbase://ENTROPIES' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');
