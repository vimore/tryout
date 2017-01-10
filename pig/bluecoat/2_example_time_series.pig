REGISTER /Users/rjurney/Software/datafu/dist/datafu-1.2.1-SNAPSHOT.jar

rmf /tmp/time_series.txt

bluecoat = LOAD '../../data/bluecoat_datetime.avro' USING AvroStorage();
DEFINE PadZero datafu.pig.util.PadZero();
DEFINE ToJson datafu.pig.util.ToJson(); 

/* Trim down the fields to improve processing speed */
bytes_in_out = FOREACH bluecoat GENERATE ToDate(date_time) AS date_time, sc_bytes, cs_bytes;

/* Now group by time down to the hour, our time series granularity */
grouped_by_time = GROUP bytes_in_out BY (GetYear(date_time), GetMonth(date_time), GetDay(date_time), GetHour(date_time));
bytes_per_hour = FOREACH grouped_by_time GENERATE FLATTEN(group) AS (year, month, day, hour), 
                                                  SUM(bytes_in_out.sc_bytes) AS total_sc_bytes,
                                                  SUM(bytes_in_out.cs_bytes) AS total_cs_bytes;
/* Now convert time elements back into a key for HBase */
bytes_per_hour = FOREACH bytes_per_hour GENERATE ToDate(StringConcat(year, '-', PadZero(month), '-', PadZero(day), 'T', PadZero(hour), ':00:00Z')) AS date_time, 
                                                 total_sc_bytes, 
                                                 total_cs_bytes;
                                                 
/* Now group by day - to get a list of 24 hours in order per day for our time series */
bytes_per_day = GROUP bytes_per_hour BY (GetYear(date_time), GetMonth(date_time), GetDay(date_time));
time_series = FOREACH bytes_per_day {
    sorted = ORDER bytes_per_hour BY date_time;
    GENERATE FLATTEN(group) AS (year, month, day), sorted AS time_series;
    };
/* And again, convert time elements back into an ISODate key */
time_series = FOREACH time_series GENERATE StringConcat(PadZero(year), '-', PadZero(month), '-', PadZero(day), 'T00:00:00Z') AS date_time, 
                                           ToJson(time_series) AS time_series;
STORE time_series INTO '/tmp/time_series.avro' USING AvroStorage();                                       

                                            