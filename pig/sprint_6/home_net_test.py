#!/usr/bin/python

import home_net

hn = home_net.HomeNet()
assert(hn.contains('10.60.0.2'))
assert(not hn.contains('1.2.3.4'))
print "PASS"
