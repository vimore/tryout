#!/usr/bin/python2.7

# Given Bluecoat logs, automatically find the list of networks that cover
# the source IP addresses in the data. It's assumed that there is some
# noise, i.e. addresses that don't actually belong to the network of
# interest. Those should end up in the "IPs not covered" category.

from operator import itemgetter
import sys

import bluecoat
import ipaddress


IP = 'c-ip'

count = {}

def in_networks(base_ip, covering_networks):
  for net_address in covering_networks:
    if ipaddress.IPv4Address(base_ip) in ipaddress.IPv4Network(net_address):
      return True # IP is already in one of our covering networks.
  return False

# Count the requests for each source address.
for row in bluecoat.Bluecoat(sys.argv[1]):
  ip = row[IP]
  if count.has_key(ip):
    count[ip] = count[ip] + 1
  else:
    count[ip] = 1

# Select the top N addresses where request volume is
# 1% or greater of the most active IP address.
sorted_counts = sorted(count.iteritems(), key=itemgetter(1), reverse=True)
top_count = sorted_counts[0][1]
ip_list = []
for ip, count in sorted_counts:
  if count >= 0.1 * float(top_count):
    ip_list.append(ip)
  else:
    break

# Now try to find the best covering network addresses
top_count = len(ip_list)

covering_networks = []
for index in range(len(ip_list)):
  base_ip = ip_list[index]
  if in_networks(base_ip, covering_networks):
    continue
  base_net = ipaddress.IPv4Network(base_ip + '/32')
  coverage = 1  # Just one IP covered by /32
  for mask_length in range(32, 8, -1):
    net = base_net.supernet(new_prefix=mask_length)
    covered_count = 0
    for ip in ip_list:
      if ipaddress.IPv4Address(ip) in net:
        covered_count = covered_count + 1
    # Stop making the mask smaller when we see a jump in the covered
    # IPs and when we're covering a reasonable percentage of the total IPs.
    if ((covered_count > coverage * 2) and
        (covered_count > float(top_count) * 0.10)):
      covering_networks.append(net)
    coverage = covered_count

print "Home networks:"
print covering_networks

covered_count = 0
print
print "IPs not covered:"
for index in range(len(ip_list)):
  if in_networks(ip_list[index], covering_networks):
    covered_count = covered_count + 1
  else:
    print ip_list[index]
print
print "Count of IPs covered:"
print covered_count
