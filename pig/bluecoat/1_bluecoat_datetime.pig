REGISTER /Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/Bluecoat/datafu/dist/datafu-1.2.1-SNAPSHOT.jar

rmf ../../data/bluecoat_datetime.avro

/* This is an ETL workaround for https://issues.apache.org/jira/browse/PIG-3671 */
/* First we CONCAT date and time, and then store, to clear the plan and avoid PIG-3671 */
bluecoat = LOAD '../../data/no_header_bluecoat_parsed.log' AS (date:chararray,       /* 2005-04-12 */
                                                  time:chararray,	      /* 22:40:07 */
                                                  time_taken:chararray,       /* 2 */
                                                  c_ip:chararray,       /* 10.0.1.16 */
                                                  sc_status:chararray,  /* 403 */
                                                  s_action:chararray,   /* TCP_DENIED */
                                                  sc_bytes:int,         /* 775 */
                                                  cs_bytes:int,         /* 456 */
                                                  cs_method:chararray,	      /* GET */
                                                  cs_uri_scheme:chararray,  /* http */
                                                  cs_host:chararray,        /* www.google.com */
                                                  cs_uri_path:chararray,    /* / */
                                                  cs_uri_query:chararray,   /* - */
                                                  cs_username:chararray,    /* - */
                                                  s_hierarchy:chararray,
                                                  s_supplier_name:chararray, 
                                                  rs_Content_Type:chararray,         
                                                  cs_User_Agent:chararray, 
                                                  sc_filter_result:chararray,
                                                  sc_filter_category:chararray,
                                                  x_virus_id:chararray,	
                                                  s_ip:chararray,
                                                  s_sitename:chararray,
                                                  x_virus_details:chararray,	
                                                  x_icap_error_code:chararray,
                                                  x_icap_error_details:chararray);

/* Build an ISO Datetime string */
bluecoat_datetime = FOREACH bluecoat GENERATE datafu.pig.random.RandomUUID() AS uuid, 
                                              'bluecoat' AS vendor,
                                              StringConcat(date, 'T', time, 'Z') AS date_time, *;
STORE bluecoat_datetime INTO '../../data/bluecoat_datetime.avro' USING AvroStorage();
STORE bluecoat_datetime INTO '../../data/bluecoat_datetime.txt' USING AvroStorage();
