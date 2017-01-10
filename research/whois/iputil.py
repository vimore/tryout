#!/usr/bin/python

import re

IP_RE = re.compile(r'^([0-9]{1,3}\.){3}[0-9]{1,3}$')
NETBLOCK_RE = re.compile(r'^([0-9]{1,3}\.){3}[0-9]{1,3}/[0-9]{1,2}$')

def ip2int(ip):
  if not IP_RE.match(ip):
    return None
  # Return a valid IP as a uint32, return None if invalid.
  try:
    octets = [int(y) for y in ip.split('.')]
  except ValueError:
    return None
  if all(x >= 0 and x <= 255 for x in octets) == True:
    return sum(256**(3-i)*octets[i] for i in range(4))
  else:
    return None

def int2ip(ip):
  octets = [0, 0, 0, 0]
  octets[3] = ip & 255
  octets[2] = (ip >> 8) & 255
  octets[1] = (ip >> 16) & 255
  octets[0] = ip >> 24
  return '.'.join(str(octet) for octet in octets)

def netblock2range(netblock):
  if not NETBLOCK_RE.match(netblock):
    return None
  ip = ip2int(re.sub(r'(.*)/.*', r'\1', netblock))
  masklen = int(re.sub(r'.*/(.*)', r'\1', netblock))
  mask = 4294967295 >> (32-masklen) << (32-masklen)
  low_address = ip&mask
  high_address = low_address + 2**(32-masklen) - 1
  return int2ip(low_address), int2ip(high_address)

if __name__ == '__main__':
  # Test ip2int
  assert ip2int('bla') == None
  assert ip2int('a.b.c.d') == None
  assert ip2int('-1.1.1.1') == None
  assert ip2int('256.0.0.0') == None
  assert ip2int('0.0.0.1') == 1
  assert ip2int('0.0.1.0') == 256
  assert ip2int('0.1.0.0') == 65536
  assert ip2int('1.0.0.0') == 16777216
  assert ip2int('255.255.255.255') == 4294967295
  assert ip2int('1.2.3.4') == 16909060
  # Test int2ip
  assert int2ip(16777216) == '1.0.0.0'
  assert int2ip(16909060) == '1.2.3.4'
  # Tests netblock2range
  assert netblock2range('192.168.0.0/16') == ('192.168.0.0', '192.168.255.255')
  print netblock2range('1.2.3.0/25') == ('1.2.3.0', '1.2.3.127')

  print "All tests passed."
