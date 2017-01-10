#!/usr/bin/python

import json
import re
import sys
import urllib2

import iputil
import ipv4registry


# A class to automate interaction with the RESTful whois API
# for RIPE (and other registry) allocations. Documentation for the API is at
# http://www.ripe.net/data-tools/developer-documentation/ripe-database-rest-api
class RIPE:
  BASE_URL = 'http://rest.db.ripe.net/search'

  def __init__(self):
    self.data = {}
    self.ipv4 = ipv4registry.IPRegistry()
    self.source = {
        self.ipv4.RIPE : "ripe-grs",
        self.ipv4.APNIC: "apnic-grs",
	self.ipv4.AFRINIC: "afrinic-grs",
        self.ipv4.LACNIC: "lacnic-grs",
        self.ipv4.ARIN: "arin-grs" }

  ### Public functions
  def query_ip(self, ip, source):
    self.data = {}  # New query, so start with nothing.
    # TODO(mdeshon): Test for support for 'flags=one-more' or 'flags=all-more'?
    request = urllib2.Request(
        self.BASE_URL + '?source=' + self.source[source] + '&query-string=' + ip ,
	headers={'Accept': 'application/json'})
    try:
      json_data = json.load(urllib2.urlopen(request))
    except urllib2.HTTPError, e:
      if e.code == 404:
        # Probably there is only a generic /8 entry for this IP.
        return None, None, None
      else:
        sys.stderr.write("Could not retrieve URL " + request.get_full_url() + "\n")
        raise
    try:
      data = json_data["objects"]["object"]
    except KeyError:
      # Probably old format:
      data = json_data["whois-resources"]["objects"]["object"]
    try:
      low_ip, high_ip = self.find_low_high_(data)
    except TypeError:
      # None was returned by find_low_high_()
      sys.stderr.write("Could not find low/high IPs in data:\n")
      json.dump(data, sys.stderr, indent=2)
      raise
    return low_ip, high_ip, data

  ### Private functions
  def find_low_high_(self, data):
    try:
      attribute_list = data["attributes"]["attribute"]
    except TypeError:
      try:
        # Looks like this is a list of attribute lists. Just use the first one.
        attribute_list = data[0]["attributes"]["attribute"]
      except TypeError:
        # Still didn't work. Let's look at what we're dealing with because
        # we will soon error out.
        sys.stderr.write("TypeError on attribute_list:\n")
        json.dump(data, sys.stderr, indent=2)
        sys.stderr.write("\n")
        return None
    for attribute in attribute_list:
      if attribute['name'] == 'inetnum':
        val = attribute['value']
        low_ip = re.sub(r"^(.*) - .*", r'\1', val)
        high_ip = re.sub(r"^.* - (.*)$", r'\1', val)
        return low_ip, high_ip
      if attribute['name'] == 'route':
        # Some records give a route neblock instead of inetnum.
        val = attribute['value']
        low_ip, high_ip = iputil.netblock2range(val)
        return low_ip, high_ip
    return None

if __name__ == '__main__':
  if len(sys.argv) > 1:
    trial_address = sys.argv[1]
  else:
    trial_address = '6.0.0.1'
  ripe = RIPE()
  print ripe.query_ip(trial_address, ripe.ipv4.APNIC)
