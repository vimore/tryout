#!/usr/bin/python
import sys

import iso8601
import tldextract
import httpagentparser


def convert_to_hour_bin(start_time_iso):
    dt = iso8601.parse_date(start_time_iso)
    return dt.replace(minute=0, second=0, microsecond=0, tzinfo=None).isoformat() + ".000Z"

def extract_sld(domain):
    e = tldextract.extract(domain)
    if e.suffix == "":   # domain was not actually a domain
      key = e.domain
    else:
      key = e.domain + "." + e.suffix
    return key

def extract_tld(domain):
    e = tldextract.extract(domain)
    if e.suffix == "":   # domain was not actually a domain
      key = "None"
    else:
      key = e.suffix
    return key
                                         
for line in sys.stdin:
   fields = line.split('\t')
   
   source_name_or_ip = fields[0]
   destination_name_or_ip = fields[1]
   source_user_name = fields[2]
   cef_signature_id = fields[3]
   device_action = fields[4]
   device_event_category = fields[5]
   request_application = fields[6]
   request_scheme = fields[7]
   request_method = fields[8]
   device_policy_action = fields[9]
   start_time_iso = fields[10]
   bytes_in = fields[11]
   bytes_out = fields[12]
   
   hour_bin_iso = convert_to_hour_bin(start_time_iso)
   tld = extract_tld(destination_name_or_ip)
   sld = extract_sld(destination_name_or_ip)
   os, browser = httpagentparser.simple_detect(request_application)
   
   if(fields[0] != None):
       print "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%s\t%s\t%s\t%s" % (
           source_name_or_ip,
           destination_name_or_ip,
	       source_user_name,
	       cef_signature_id,
	       device_action,
	       device_event_category,
	       request_application,
	       request_scheme,
	       request_method,
	       device_policy_action,
	       hour_bin_iso,
	       int(bytes_in),
	       int(bytes_out),
           tld,
           sld,
	       os,
	       browser)
