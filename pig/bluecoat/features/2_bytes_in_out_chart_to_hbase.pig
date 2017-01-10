chart_data = LOAD '/tmp/chart_data.avro' USING AvroStorage();
STORE chart_data INTO 'hbase://e8_charts' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('by_date:json');
