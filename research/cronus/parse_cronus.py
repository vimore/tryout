#!/usr/local/bin/python

import os
import sys
sys.path.append(".")
sys.path.insert(0, os.getcwd())
sys.path.append('/usr/lib/python2.6/site-packages')
import re

def main():

  P = re.compile(r'''((?:[^\s"']|"[^"]*"|'[^']*')+)''')

  X = []
  i = 0
  for line in sys.stdin:

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
       cs_bytes = int( arr[14])
       rs_status = arr[15]
       rs_bytes = int( arr[16])
       s_action = arr[17]
       sc_bytes = arr[18]
       time_taken = int( arr[19])
       sr_bytes = arr[20]
       c_ip = arr[21]
       c_port = int( arr[22])
       cs_User_Agent = arr[23]
       cs_categories = arr[24]
       s_icap_status = arr[25]
       rs_content_type = arr[26]
       cs_ip = arr[27]
       x_cs_connection_dscp = arr[28]
       s_source_port = arr[29]
       
       print '%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d\t%s\t%d\t%s\t%s\t%d\t%s\t%s\t%d\t%s\t%s\t%s\t%s\t%s\t%s\t%s' % \
          (x_bluecoat_monthname_utc, x_bluecoat_day_utc, time, x_bluecoat_appliance_primary_address, localtime_month, localtime_day, localtime_time, some_ip, time2, cs_username, cs_method, cs_uri, cs_version, sc_status, cs_bytes, rs_status, rs_bytes, s_action, sc_bytes, time_taken, sr_bytes, c_ip, c_port, cs_User_Agent, cs_categories, s_icap_status, rs_content_type, cs_ip, x_cs_connection_dscp, s_source_port)
     except:
       ii = 0
       
       
if __name__=='__main__':
  main()
  
