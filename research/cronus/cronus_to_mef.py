#!/usr/local/bin/python

# NOTE: Hard-coded year of 2014 for TCP_TUNNELED log lines only, because
# they do not include a year (!)

import datetime
import os
import sys

from dateutil import tz

import iso8601

sys.path.append(".")
sys.path.insert(0, os.getcwd())
sys.path.append('/usr/lib/python2.6/site-packages')
import re

date_re = re.compile(r'"\[(?P<date_part>\d+/[^/]+/\d+):(?P<time_part>\d+:\d+:\d+ .\d+)\]"')
utc = tz.gettz('UTC')

def iso_time(time_string):
  # Example time_string:
  # "[14/May/2014:23:59:53 +0200]"
  mo = date_re.match(time_string)
  if not mo:
    return None, None, None, None, None
  # Convert "14/May/2014" into "2014-05-14"
  date_part = datetime.datetime.strptime(
      mo.group('date_part'), '%d/%b/%Y').strftime('%Y-%m-%d')
  # Merge into an iso string and convert to Zulu time.
  parsed_datetime = iso8601.parse_date(date_part + " " + re.sub(r' ', r'', mo.group('time_part')))
  # Example output string:
  # 2013-05-14T12:23:59.000Z
  converted = parsed_datetime.astimezone(utc)
  full_date = converted.strftime('%Y-%m-%dT%H:%M:%S.000Z')
  year = converted.strftime('%Y')
  month = converted.strftime('%m')
  day = converted.strftime('%d')
  hour = converted.strftime('%H')
  return full_date, year, month, day, hour

def main():

  #P = re.compile(r'''((?:[^\s"']|"[^"]*"|'[^']*')+)''')
  P = re.compile(r'''((?:[^\s"]|"[^"]*")+)''')   # No single-quoted fields.
  # Allow for URLs up to http://user:password@domain:port/
  #uri = re.compile(r'^[^:]+://([^/]+@)?(?P<domain>[^/:]+)')
  #uri = re.compile(r'^((?P<scheme2>tcp)|(?P<scheme1>[^:]+)://([^/]+@)?(?P<domain>[^/:]+))')
  uri = re.compile(r'^((?P<scheme1>[^:/]+)://([^/?*]+@)?(?P<domain>[^/:]+)(?P<port>:\d+)?|(?P<scheme2>tcp))')

  X = []
  i = 0
  for line in sys.stdin:
     line = line.rstrip('\n')
     try:
       arr = P.findall(line)

       x_bluecoat_monthname_utc = arr[0]
       x_bluecoat_day_utc = arr[1]
       time = arr[2]
       x_bluecoat_appliance_primary_address = arr[3]
       localtime_month = arr[4]
       localtime_day = arr[5]
       localtime_time = arr[6]
       some_ip = arr[7]
       time2 = arr[8]
       cs_username = arr[9]
       cs_method = arr[10]
       cs_uri = arr[11]
       cs_version = arr[12]
       sc_status = arr[13]
       try:
         cs_bytes = int(arr[14])
       except TypeError:
         cs_bytes = 0
       rs_status = arr[15]
       #rs_bytes = int( arr[16])
       s_action = arr[17]
       try:
         sc_bytes = int(arr[18])
       except TypeError:
         sc_bytes = 0
       #time_taken = int( arr[19])
       #sr_bytes = arr[20]
       c_ip = arr[21]
       #c_port = int(arr[22])
       cs_User_Agent = arr[23]
       cs_categories = arr[24]
       s_icap_status = arr[25]
       rs_content_type = arr[26]
       cs_ip = arr[27]
       x_cs_connection_dscp = arr[28]
       s_source_port = arr[29]

#    sourceNameOrIp:chararray,
#    destinationNameOrIp:chararray,
#    startTimeISO:chararray,
#    requestClientApplication:chararray,
#    requestMethod:chararray,
#    rawLog:chararray

#    sourceUserName,  cs_username
#    cefSignatureId AS responseCode,  sc_status
#    deviceAction,   s_action
#    deviceEventCategory, cs_categories
#    requestScheme,  (parse from uri)
#    bytesIn,   sc_bytes
#    bytesOut;  cs_bytes

#    c_port is the client source port
#    We should also parse destination port into cs_uri_port

       try:
         match_obj = uri.match(cs_uri)
         domain = match_obj.group('domain')
         scheme = match_obj.group('scheme1')
         if not scheme:
           scheme = match_obj.group('scheme2')
       except:
         # [0 'Jun', 1 '25', 2 '01:22:59', 3 '10.44.68.11', 4 'Jun', 5 '24', 6 '23:22:29', 7 '44', 8 '10.44.116.1', 9 '-', 10 '-', 11 '-', 12 'PROXIED', 13 '"none"', 14 '-', 15 '200', 16 'TCP_TUNNELED', 17 'TUNNEL', 18 '-', 19 'tcp', 20 '10.44.116.31', 21 '25', 22 '/', 23 '-', 24 '-', 25 '-', 26 '10.44.116.31', 27 '16', 28 '0', 29 '-', 30 '"none"', 31 '"none"']
         if arr[16] == 'TCP_TUNNELED' and arr[17] == 'TUNNEL':
           # These log lines don't have the same fields.
           c_ip = arr[8]
           domain = arr[20]
           # Construct a string like "[14/May/2014:23:59:53 +0200]"
           time2 = ('"[' + arr[5] + '/' + arr[4] + '/2014:' +
                    arr[6] + ' +0200]"')
           cs_User_Agent = arr[23]  # Looks like always '-'
           cs_method = arr[17]  # Looks like always 'TUNNEL'
           cs_username = arr[10]  # Looks like always '-'
           sc_status = arr[15]
           s_action = arr[12]
           sc_bytes = 0
           cs_bytes = 0
           # Port number is arr[21]
         else:
           domain = ''
           scheme = ''
           sys.stderr.write('Could not parse URI ' + cs_uri + '\n')
           i = i + 1

       start_time_iso, year, month, day, hour = iso_time(time2)
       if not start_time_iso:
         # Date was not extracted. Don't print an output line.
         sys.stderr.write("Could not parse date string " + time2 + ". Line dropped: " + line + "\n")
         continue

       #print '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%s' % \
       print '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s' % \
          (c_ip, domain, start_time_iso, year, month, day, hour,
           cs_User_Agent, cs_method, line)
     except:
       sys.stderr.write('Could not parse line: ' + line + '\n')
       i = i + 1
       
  if i > 0:
    sys.stderr.write('WARNING: ' + str(i) + ' parsing errors.\n')
       
if __name__=='__main__':
  main()
  
