#!/usr/bin/python

import json
import sys
import urllib2

import iputil


# A class to automate interaction with the RESTful whois API
# for ARIN IP allocations. Documentation for the API is at
# https://www.arin.net/resources/whoisrws/whois_api.html
class ARIN:
  BASE_URL = 'http://whois.arin.net/rest/'
  IP_URL = BASE_URL + 'nets'
  NET = 'net'
  HANDLE = 'handle'
  NAME = 'name'
  START_ADDRESS = 'startAddress'
  END_ADDRESS = 'endAddress'
  TYPE = 'type'
  DESCRIPTION = 'description'
  ORGREF = 'orgRef'
  REGISTRATION_DATE = 'registrationDate'
  UPDATE_DATE = 'updateDate'
  # Only flat records
  NET_RECORD = [HANDLE, NAME, START_ADDRESS, END_ADDRESS,
                ORGREF, REGISTRATION_DATE, UPDATE_DATE]
  # netBlocks is not flat, so handle specially.
  NETBLOCKS = 'netBlocks'
  NETBLOCK = 'netBlock'
  CIDR_LENGTH = 'cidrLength'
  NETBLOCK_RECORD = [NETBLOCK, CIDR_LENGTH, START_ADDRESS, END_ADDRESS,
                     TYPE, DESCRIPTION]

  def __init__(self):
    self.data = {}

  ### Public functions
  def query_ip(self, ip):
    self.data = {}  # New query, so start with nothing.
    request = urllib2.Request(self.IP_URL + ";q=" + ip + "?ext=netref2",
                              headers = {'Accept': 'application/json'})
    json_data = json.load(urllib2.urlopen(request))
    try:
      # Choose the most specific IP range, order can differ.
      lo_range = 4294967295
      for i in range(len(json_data["nets"][u'ns3:netRef'])):
        lo = iputil.ip2int(json_data["nets"][u'ns3:netRef'][i]["@startAddress"])
        hi = iputil.ip2int(json_data["nets"][u'ns3:netRef'][i]["@endAddress"])
        if hi - lo < lo_range:
          index = i
          lo_range = hi - lo
      data = json_data["nets"][u'ns3:netRef'][index]
    except KeyError:
      # Singleton record, so no [0] element.
      data = json_data["nets"][u'ns3:netRef']
    low_ip = data["@startAddress"]
    high_ip = data["@endAddress"]
    return low_ip, high_ip, data
#    for field in self.NET_RECORD:
#      self.data[field] = json_data[self.NET][field]['$']
#    # compound fields: netBlocks

  ### Private functions

if __name__ == '__main__':
  if len(sys.argv) > 1:
    trial_address = sys.argv[1]
  else:
    trial_address = '173.130.197.191'
  arin = ARIN()
  print arin.query_ip(trial_address)
  #assert arin.data['NetName'] == 'CONUS-YPG-NET'

# Example record in JSON:
# { "net": {"@xmlns":{"ns3":"http:\/\/www.arin.net\/whoisrws\/netref\/v2","ns2":"http:\/\/www.arin.net\/whoisrws\/rdns\/v1","$":"http:\/\/www.arin.net\/whoisrws\/core\/v1"},"@termsOfUse":"https:\/\/www.arin.net\/whois_tou.html","registrationDate":{"$":"1994-02-01T00:00:00-05:00"},"ref":{"$":"http:\/\/whois.arin.net\/rest\/net\/NET-6-0-0-0-1"},"endAddress":{"$":"6.255.255.255"},"handle":{"$":"NET-6-0-0-0-1"},"name":{"$":"CONUS-YPG-NET"},"netBlocks":{"netBlock":{"cidrLength":{"$":"8"},"endAddress":{"$":"6.255.255.255"},"description":{"$":"Direct Allocation"},"type":{"$":"DA"},"startAddress":{"$":"6.0.0.0"}}},"orgRef":{"@name":"Headquarters, USAISC","@handle":"HEADQU-3","$":"http:\/\/whois.arin.net\/rest\/org\/HEADQU-3"},"startAddress":{"$":"6.0.0.0"},"updateDate":{"$":"2011-02-24T10:51:23-05:00"},"version":{"$":"4"}}}
