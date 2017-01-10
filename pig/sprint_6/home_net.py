#!/usr/local/bin/python

import ipaddress

class HomeNet:
  def __init__(self):
    self.home_net = []
    for network in open('home_net.cfg', 'r'):
      network = network.rstrip('\n')
      self.home_net.append(ipaddress.ip_network(unicode(network)))

  def contains(self, ip_string):
    ip = ipaddress.IPv4Address(unicode(ip_string))
    for network in self.home_net:
      if ip in network:
        return True
    return False
