REGISTER 'udfs.py' using streaming_python AS udfs;
SET default_parallel 10

rmf /tmp/domains.avro

bluecoat_proxy_records = LOAD '../../../data/bluecoat_datetime.avro' USING AvroStorage();
trimmed_bluecoat = FOREACH bluecoat_proxy_records GENERATE uuid, date_time, cs_host;
domain_parts = FOREACH trimmed_bluecoat GENERATE date_time, uuid, udfs.domain_parts(cs_host);
STORE domain_parts INTO '/tmp/domains.avro' USING AvroStorage();
