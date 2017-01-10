REGISTER hdfs://hivecluster2/securityx/jars/*.jar /* */

rawdata = LOAD '/securityx/research/laposte/new_*' USING PigStorage() AS (data:chararray);

DEFINE process_command `simply_parse.py` SHIP('simply_parse.py');
X = STREAM rawdata THROUGH process_command AS (
        x_bluecoat_monthname_utc: chararray,
        x_bluecoat_day_utc: chararray,
        time:chararray,
        x_bluecoat_appliance_primary_address: chararray,
        localtime_month:chararray,
        localtime_day:chararray,
        localtime_time:chararray,
        some_ip:chararray,
        time2:chararray,
        cs_username:chararray,
        cs_method:chararray,
        cs_uri:chararray,
        cs_version:chararray,
        sc_status:chararray,
        cs_bytes:int,
        rs_status:chararray,
        rs_bytes:int,
        s_action:chararray,
        sc_bytes:chararray,
        time_taken:int,
        sr_bytes:chararray,
        c_ip:chararray,
        c_port:int,
        cs_User_Agent:chararray,
        cs_categories:chararray,
        s_icap_status:chararray,
        rs_content_type:chararray,
        cs_ip:chararray,
        x_cs_connection_dscp:chararray,
        s_source_port:chararray);

-- rmf [path to destination];
-- STORE X INTO '[path to destination]' using PigStorage();
