feature1 = LOAD '/tmp/feature1.avro' USING AvroStorage();
STORE feature1 INTO 'hbase://e8' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('features:sc_bytes_total features:cs_bytes_total features:sc_bytes_max features:cs_bytes_max features:sc_bytes_min features:cs_bytes_min');

feature2 = LOAD '/tmp/feature2.avro' USING AvroStorage();
STORE feature2 INTO 'hbase://e8' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('features:sc_bytes_total features:cs_bytes_total features:sc_bytes_max features:cs_bytes_max features:sc_bytes_min features:cs_bytes_min');

index1 = LOAD '/tmp/index1.avro' USING AvroStorage();
STORE index1 INTO 'hbase://e8' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('indexes:host_ip_pairs');

index2 = LOAD '/tmp/index2.avro' USING AvroStorage();
STORE index2 INTO 'hbase://e8' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('indexes:hours');

index3 = LOAD '/tmp/index3.avro' USING AvroStorage();
STORE index3 INTO 'hbase://e8' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('indexes:all_hours');
