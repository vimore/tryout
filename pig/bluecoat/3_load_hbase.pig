time_series = LOAD '/tmp/time_series.avro' USING AvroStorage();

STORE time_series INTO 'hbase://e8' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('proxy_time_series:bytes_day_by_hour');
