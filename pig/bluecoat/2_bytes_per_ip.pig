/* Script 2_bytes_per_ip.pig: Implements example feature, 'sc/cs bytes per ip pair.'

/* Remember to remove the previous output of this script or it will fail. But BE CAREFUL what you rmf! */
rmf /tmp/bytes_per_ip.avro
rmf /tmp/bytes_per_ip_pair_per_day.avro

/* We created these avros in 1_bluecoat_datetime.pig */
bluecoat = LOAD '../../data/bluecoat_datetime.avro' USING AvroStorage();

/* Always trim to the minimum fields you need, to maximize efficiency. */
trimmed_bluecoat = FOREACH bluecoat GENERATE s_ip, c_ip, sc_bytes, cs_bytes, ToDate(date_time) as date_time;

/* Now group by IP, then get a sum of bytes for each category */
bytes_per_ip = FOREACH (GROUP trimmed_bluecoat BY s_ip) GENERATE group AS s_ip, 
                                                                 SUM(trimmed_bluecoat.sc_bytes) AS sc_bytes_total, 
                                                                 SUM(trimmed_bluecoat.cs_bytes) AS cs_bytes_total;

/* Finally, store in Avros in /tmp. */
STORE bytes_per_ip INTO '/tmp/bytes_per_ip.avro' USING AvroStorage();
                                                
/* But really... we want this computed within a given time frame, and for pairs of IPs. So, we group by multiple keys, including dates... */
grouped_bluecoat = GROUP trimmed_bluecoat BY (GetYear(date_time), GetMonth(date_time), GetDay(date_time), s_ip, c_ip);
bytes_per_ip_pair_per_day = FOREACH grouped_bluecoat GENERATE
    FLATTEN(group) AS (year, month, day, s_ip, c_ip),
    SUM(trimmed_bluecoat.sc_bytes) AS sc_bytes_total,
    SUM(trimmed_bluecoat.cs_bytes) AS cs_bytes_total;

/* Now we need to create our key for HBase, ex.: 2014-01-01|10.10.10.10|10.11.11.11 */
bytes_per_ip_pair_per_day = FOREACH bytes_per_ip_pair_per_day GENERATE 
    StringConcat(year, '-', month, '-', day, '|', s_ip, '|', c_ip),
    sc_bytes_total,
    cs_bytes_total;
/* Again, store to /tmp first, and in Avro format. */
STORE bytes_per_ip_pair_per_day INTO '/tmp/bytes_per_ip_pair_per_day.avro' USING AvroStorage();
