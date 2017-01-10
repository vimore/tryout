#!/usr/local/anaconda/bin/python

import json

for line in open('bluecoat_whois.txt', 'r'):
  ip, whois = line.split(' ', 1)
  whois = whois.rstrip('\n')
  try:
    whois_obj = json.loads(whois)
    if whois_obj.has_key('orgRef'):
      print ip, whois_obj['orgRef']['@name']
    else:
      print ip, whois_obj['customerRef']['@name']
  except ValueError:
    # Not a JSON object
    print ip, whois
