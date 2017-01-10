#!/usr/local/bin/python

import domains

assert(domains.get_sld('www.google.co.uk') == 'google.co.uk')
assert(domains.get_tld('www.google.co.uk') == 'co.uk')
assert(domains.get_sld('192.168.1.1') == '192.168.1.1')
print "PASS"
