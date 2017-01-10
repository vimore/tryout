#!/usr/bin/python

# Read a file containing a list of IP addresses, one IP per
# line, and query the whois databases, with local caching 
# of results to prevent hammering the servers unnecessarily.


from datetime import datetime
import json
import os
import re
import sys
import time

import iputil
import ipv4registry


# Local imports
import arin
import ripe

class Whois:
  RE_IP = re.compile(r'^([0-9]{1,3}\.){3}[0-9]{1,3}$')

  def __init__(self):
    self.ripe = ripe.RIPE()
    self.arin = arin.ARIN()
    self.ipv4 = ipv4registry.IPRegistry()
    self.cache = {}
    self.last_time = datetime.utcnow()
    self.interval = 3.0  # Number of seconds between queries.
    pass

  ### Public functions

  def query_ip(self, ip):
    # Validate ip
    if not self.RE_IP.match(ip):
      sys.stderr.write("ERROR: " + ip + " not an IP")
      return None
    # Check cache
    data = self.check_cache_(ip)
    if data:  # Cache hit, so just return the result.
      return data
    # Figure out the registry data source
    first_octet = int(re.sub(r'^([0-9]+)\..*', r'\1', ip))
    whois_source = self.ipv4.data[first_octet][self.ipv4.WHOIS]
    if not whois_source:
      # No query can be made for this IP, probably reserved.
      return None
    # Make query to service
    self.stall_if_necessary_()  # Rate limit.
    # Make the actual query
    if whois_source == self.ipv4.ARIN:
      sys.stderr.write("Querying ARIN for " + ip + "\n")
      low_ip, high_ip, data = self.arin.query_ip(ip)
    else:
      sys.stderr.write("Querying RIPE for " + ip + "\n")
      low_ip, high_ip, data = self.ripe.query_ip(ip, whois_source)
    if low_ip and high_ip and data:
      self.add_to_cache_(low_ip, high_ip, data)  # This was a cache miss.
      return data
    else:
      # Nothing was returned, so let's give an empty response.
      return ''

  ### Private functions

  def check_cache_(self, ip):
    # Return None if a miss, otherwise return data.
    ip = iputil.ip2int(ip)  # Convert to integer to compare with cache keys.
    try:
      candidate_low = max(x for x in self.cache.keys() if x <= ip)
    except ValueError:
      # There are no keys lower than the input ip.
      return None
    if candidate_low:
      try:
        candidate_high = min(y for y in self.cache[candidate_low].keys() if y >= ip)
      except ValueError:
        # None of the candidate ranges include the input ip.
        return None
      if candidate_high:
        return self.cache[candidate_low][candidate_high]
    return None

  def add_to_cache_(self, low_ip, high_ip, data):
    # Add this json object to the cache.
    low_ip = iputil.ip2int(low_ip)  # Convert to integers to use as keys
    high_ip = iputil.ip2int(high_ip)
    if high_ip - low_ip == 16777215:
      # Don't cache /8 entries.
      return
    if not self.cache.has_key(low_ip):
       self.cache[low_ip] = {}
    self.cache[low_ip][high_ip] = data

  def stall_if_necessary_(self):
    delta = datetime.utcnow() - self.last_time
    seconds = delta.days * 86400 + delta.seconds + (delta.microseconds * 1e-6)
    if seconds < self.interval:
      time.sleep(self.interval - seconds)
    self.last_time = datetime.utcnow()

if __name__ == '__main__':
  whois = Whois()
  if len(sys.argv) < 2 or len(sys.argv) > 3:
    sys.stderr.write("Usage: " + sys.argv[0] + " (-t|inputfile outputfile)\n")
    sys.exit(1)
  if sys.argv[1] == '-t':
    # Run tests and exit


    # Test proper functioning of the cache.
    whois.add_to_cache_(u'1.96.0.0', u'1.111.255.255', "range1")
    whois.add_to_cache_(u'1.96.0.0', u'1.109.255.255', "range2")
    whois.add_to_cache_('1.111.0.0', '1.111.255.255', "range3")
    whois.add_to_cache_('12.0.0.0', '12.255.255.255', "slasheight")
    assert whois.check_cache_('1.96.0.0') == "range2"
    assert whois.check_cache_('1.110.0.0') == "range1"
    assert whois.check_cache_('1.111.0.0') == "range3"
    assert whois.check_cache_('1.2.3.4') == None
    assert whois.check_cache_('255.255.255.255') == None
    assert whois.check_cache_('12.0.1.1') == None
    print "All tests passed."
    sys.exit(0)

  # Read IPs from a given filename and make queries
  inputfile = sys.argv[1]
  assert os.path.exists(inputfile)
  outputfile = sys.argv[2]
  already_queried = None
  if os.path.exists(outputfile):
     sys.stderr.write("Output file " + outputfile +
                      " exists. Will pick up where we left off.\n")
     already_queried = []
     for ip in open(outputfile, 'r'):
       ip = re.sub(r' .*', r'', ip).strip('\n')
       already_queried.append(ip)
     print "IPs already retrieved: ", already_queried
     # Now open the output file for appending.
     outputfile = open(outputfile, 'a')
  else:
     outputfile = open(outputfile, 'w')
  inputfile = open(inputfile, 'r')  # Turn into a filehandle
  for ip in inputfile:
    ip = ip.rstrip('\n')
    if already_queried:
      if ip in already_queried:
        # Skip this ip.
        continue
    data = whois.query_ip(ip)
    outputfile.write(ip + " " + json.dumps(data) + '\n')
  inputfile.close()
  outputfile.close()
