REGISTER /Users/rjurney/Software/datafu/dist/datafu-1.2.1-SNAPSHOT.jar

rmf /tmp/feature1.avro
rmf /tmp/feature2.avro
rmf /tmp/index1.avro
rmf /tmp/index2.avro
rmf /tmp/index3.avro

DEFINE PadZero datafu.pig.util.PadZero();

bluecoat = LOAD '../../../data/bluecoat_datetime.avro' USING AvroStorage();

/* Trim down the fields to improve processing speed */
bytes_in_out = FOREACH bluecoat GENERATE ToDate(date_time) as date_time, sc_bytes, cs_bytes, cs_host, c_ip;

/* Compute byte features for hourly hostname/ip pairs */
grouped_by_hour_host_ip = GROUP bytes_in_out BY (GetYear(date_time), GetMonth(date_time), GetDay(date_time), GetHour(date_time), cs_host, c_ip);
feature1 = FOREACH grouped_by_hour_host_ip GENERATE FLATTEN(group) AS (year, month, day, hour, cs_host, c_ip), 
                                                    (int)SUM(bytes_in_out.sc_bytes) AS sc_bytes_total,
                                                    (int)SUM(bytes_in_out.cs_bytes) AS cs_bytes_total,
                                                    MAX(bytes_in_out.sc_bytes) AS sc_bytes_max,
                                                    MAX(bytes_in_out.cs_bytes) AS cs_bytes_max,
                                                    MIN(bytes_in_out.sc_bytes) AS sc_bytes_min,
                                                    MIN(bytes_in_out.cs_bytes) AS cs_bytes_min;
feature1 = FOREACH feature1 GENERATE StringConcat(PadZero(year), '-', PadZero(month), '-', PadZero(day), 'T', PadZero(hour), ':00:00.000Z', '|', cs_host, '|', c_ip) AS id,
                                     sc_bytes_total,
                                     cs_bytes_total,
                                     sc_bytes_max,
                                     cs_bytes_max,
                                     sc_bytes_min,
                                     cs_bytes_min;
STORE feature1 INTO '/tmp/feature1.avro' USING AvroStorage();

/* Compute the index for feature1 - given an hour in ISO8601 format, show us all cs_host/c_ip pairs */
grouped_index1 = GROUP bytes_in_out BY 
    StringConcat(PadZero(GetYear(date_time)), '-', PadZero(GetMonth(date_time)), '-', PadZero(GetDay(date_time)), 'T', PadZero(GetHour(date_time)), ':00:00.000Z');
index1 = FOREACH grouped_index1 GENERATE group AS id, bytes_in_out.(cs_host, c_ip) AS host_ip_pairs;
index1 = FOREACH index1 GENERATE id, datafu.pig.util.ToJson(host_ip_pairs) AS host_ip_pairs;
STORE index1 INTO '/tmp/index1.avro' USING AvroStorage();

/* Compute byte features for hourly hostnames */
grouped_by_hour_host_ip = GROUP bytes_in_out BY (GetYear(date_time), GetMonth(date_time), GetDay(date_time), GetHour(date_time), cs_host);
feature2 = FOREACH grouped_by_hour_host_ip GENERATE FLATTEN(group) AS (year, month, day, hour, cs_host),
                                                    (int)SUM(bytes_in_out.sc_bytes) AS sc_bytes_total,
                                                    (int)SUM(bytes_in_out.cs_bytes) AS cs_bytes_total,
                                                    MAX(bytes_in_out.sc_bytes) AS sc_bytes_max,
                                                    MAX(bytes_in_out.cs_bytes) AS cs_bytes_max,
                                                    MIN(bytes_in_out.sc_bytes) AS sc_bytes_min,
                                                    MIN(bytes_in_out.cs_bytes) AS cs_bytes_min;
feature2 = FOREACH feature2 GENERATE StringConcat(PadZero(year), '-', PadZero(month), '-', PadZero(day), 'T', PadZero(hour), ':00:00.000Z', '|', cs_host) AS id,
                                     sc_bytes_total,
                                     cs_bytes_total,
                                     sc_bytes_max,
                                     cs_bytes_max,
                                     sc_bytes_min,
                                     cs_bytes_min;
STORE feature2 INTO '/tmp/feature2.avro' USING AvroStorage();

/* Compute the index for feature2 - given an hour in ISO8601 format, show us all cs_hosts */
grouped_index1 = GROUP bytes_in_out BY 
    StringConcat(GetYear(date_time), '-', GetMonth(date_time), '-', GetDay(date_time), 'T', GetHour(date_time), ':00:00.000Z');
index1 = FOREACH grouped_index1 GENERATE group AS id, bytes_in_out.(cs_host) AS hosts;
index1 = FOREACH index1 GENERATE id, datafu.pig.util.ToJson(hosts) AS hosts;
STORE index1 INTO '/tmp/index2.avro' USING AvroStorage();
                                                    
/* Finally, compute a global index to each hourly index */
just_dates = FOREACH bytes_in_out GENERATE 
    StringConcat(PadZero(GetYear(date_time)), '-', PadZero(GetMonth(date_time)), '-', PadZero(GetDay(date_time)), 'T', PadZero(GetHour(date_time)), ':00:00.000Z') AS date_time;
just_dates = DISTINCT just_dates;
index3 = FOREACH (GROUP just_dates ALL) { 
    sorted = ORDER just_dates BY date_time;
    GENERATE 'all' AS id, datafu.pig.util.ToJson(sorted) AS just_dates; 
}
STORE index3 INTO '/tmp/index3.avro' USING AvroStorage();

