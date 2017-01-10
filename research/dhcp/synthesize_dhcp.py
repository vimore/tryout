#!/usr/bin/python

# Usage: ./synthesize_dhcp.py <file with IPs> <ISO date>
# Example: ./synthesize_dhcp.py ips.txt 2014-06-09

# Jun  6 08:36:11 dnsmasq-dhcp[2644]: DHCPACK(br0) 192.168.10.123 3c:15:c2:c2:2b:d0 srinivass-MBP

import datetime
import hashlib
import os
import random
import re
import sys

WORDS = [x.rstrip('\n') for x in open('/usr/share/dict/words').readlines()]
WORDSLEN = len(WORDS)
FILE = sys.argv[1]
CACHEFILE = FILE + '.cache'
DATE = sys.argv[2]

def generate_mac(seed):
  '''Generate a MAC address-like string from a hash of the seed.'''
  mac = re.sub(r'(.{12}).*', r'\1', hashlib.md5(seed).hexdigest())
  mac = re.sub(r'(..)(..)(..)(..)(..)(..)', r'\1:\2:\3:\4:\5:\6', mac)
  return mac

def generate_hostname():
  '''Generate a random hostname from /usr/share/dict/words'''
  first_word = WORDS[int(random.uniform(0, WORDSLEN))]
  second_word = WORDS[int(random.uniform(0, WORDSLEN))]
  return first_word + second_word

(year, month, day) = DATE.split('-')
timestamp = datetime.date(int(year),
    int(month), int(day)).strftime('%b %e 00:00:00')

hosts = {}
if os.path.exists(CACHEFILE):
  for line in open(CACHEFILE):
    ip, mac, hostname = line.split()
    hostname = hostname.rstrip('\n')
    hosts[ip] = (mac, hostname)

added_hosts = False
for ip in open(sys.argv[1]):
  ip = ip.rstrip('\n')
  if hosts.has_key(ip):
    mac, hostname = hosts[ip]
  else:
    mac = generate_mac(ip)
    hostname = generate_hostname()
    hosts[ip] = (mac, hostname)
    added_hosts = True
  print '%s dnsmasq-dhcp[1234]: DHCPACK(br0) %s %s %s' % (
      timestamp, ip, mac, hostname)

if added_hosts:
  fh = open(CACHEFILE, 'w')
  for ip in hosts:
    mac, hostname = hosts[ip]
    fh.write(ip + ' ' +  mac + ' ' +  hostname + '\n')
