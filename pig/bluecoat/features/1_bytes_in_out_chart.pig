<<<<<<< Updated upstream
REGISTER /Users/rjurney/Software/datafu/dist/datafu-1.2.1-SNAPSHOT.jar
REGISTER /Users/rjurney/Downloads/phoenix-2.1.2-install/phoenix-2.1.2-client.jar
REGISTER /Users/rjurney/Downloads/phoenix-2.1.2-install/phoenix-2.1.2.jar
=======

#REGISTER /Users/rjurney/Software/datafu/dist/datafu-1.2.1-SNAPSHOT.jar
REGISTER /Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/Bluecoat/datafu/dist/datafu-1.2.1-SNAPSHOT.jar;
>>>>>>> Stashed changes
REGISTER 'udfs.py' using streaming_python AS udfs;

rmf /tmp/chart_data.avro

DEFINE PadZero datafu.pig.util.PadZero();
DEFINE ToJson datafu.pig.util.ToJson();

bluecoat = LOAD '../../../data/bluecoat_datetime.avro' USING AvroStorage();

/* Calculate the total bytes in/out by the day and second level domain. */
bytes_in_out = FOREACH bluecoat GENERATE ToDate(date_time) as event_time, sc_bytes, cs_bytes, udfs.domain_parts(cs_host);
hostname_bytes = FOREACH bytes_in_out GENERATE event_time, StringConcat(host.domain, '.', host.suffix) AS second_level_domain, sc_bytes, cs_bytes;
STORE hostname_bytes into 'hbase://DAILY_BYTES_DOMAIN' using com.salesforce.phoenix.pig.PhoenixHBaseStorage('hiveapp1','-batchSize 5000');



hostname_totals = FOREACH (GROUP hostname_bytes BY (PadZero(GetYear(date_time)), PadZero(GetMonth(date_time)), PadZero(GetDay(date_time)), second_level_domain))
    GENERATE FLATTEN(group) AS (year, month, day, second_level_domain),
             (int)SUM(hostname_bytes.sc_bytes) AS sc_bytes_total,
             (int)SUM(hostname_bytes.cs_bytes) AS cs_bytes_total;
hostname_totals_key = FOREACH hostname_totals GENERATE year, month, day,
                                                       second_level_domain,
                                                       sc_bytes_total,
                                                       cs_bytes_total;

/* Now group/sort and serve the top 1000 hostnames per day */

/* Take top N sorted by sc_bytes, cs_bytes and sc_bytes:cs_bytes, then UNION them below. */
totals_by_day = FOREACH (GROUP hostname_totals_key BY (year, month, day)) {
    sorted = ORDER hostname_totals_key BY sc_bytes_total DESC;
    limited = LIMIT sorted 1000;
    GENERATE FLATTEN(group) AS (year, month, day), limited.(second_level_domain, sc_bytes_total, cs_bytes_total) AS totals;
};
chart_data = FOREACH totals_by_day GENERATE StringConcat(year, '-', month, '-', day, 'T00:00:00.000Z') AS id, ToJson(totals);
STORE chart_data INTO '/tmp/chart_data.avro' USING AvroStorage();
