#!/usr/local/bin/python

import sys

import ipaddress


sys.path.append('.')  # Look in local directory for modules.
import home_net

hn = home_net.HomeNet()
for line in sys.stdin:
  line = line.rstrip('\n')
  fields = line.split('\t')
  src = ipaddress.ip_address(unicode(fields[0]))
  src_internal = (1 if hn.contains(src) else 0)
  dst = ipaddress.ip_address(unicode(fields[1]))
  dst_internal = (1 if hn.contains(dst) else 0)
  print "{0}\t{1}\t{2}\t{3}\t{4}".format(
      src, src_internal,
      dst, dst_internal,
      "\t".join(fields[2:]))
