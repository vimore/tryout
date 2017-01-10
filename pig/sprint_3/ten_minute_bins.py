#!/usr/bin/python
import sys
import math

import iso8601


def convert_to_ten_minute_bin(start_time_iso):
    dt = iso8601.parse_date(start_time_iso)
    bucket = 10 * math.floor(dt.minute/10)
    dt = dt.replace(second = 0)
    dt = dt.replace(microsecond = 0)
    dt = dt.replace(tzinfo=None)
    dt = dt.replace(minute = int(bucket))
    iso_format = dt.isoformat()
    return iso_format + ".000Z"
                                         
#sourceNameOrIp:chararray, sourceUserName:chararray, startTimeISO:chararray, bytesIn:int, bytesOut:int
for line in sys.stdin:
   fields = line.split('\t')
   
   source_name_or_ip = fields[0]
   source_user_name = fields[1]
   destination_name_or_ip = fields[2]
   cef_signature_id = fields[3]
   device_action = fields[4]
   device_event_category = fields[5]
   request_scheme = fields[6]
   request_client_application = fields[7]
   device_policy_action = fields[8]
   start_time_iso = fields[9]
   bits_in = fields[10]
   bits_out = fields[11]
   
   ten_minute_bin_iso = convert_to_ten_minute_bin(start_time_iso)
   
   if(fields[0] != None):
       print "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d\t%d" % (source_name_or_ip, source_user_name, destination_name_or_ip, cef_signature_id, device_action, device_event_category, request_scheme, request_client_application, device_policy_action, ten_minute_bin_iso, int(bits_in), int(bits_out))
