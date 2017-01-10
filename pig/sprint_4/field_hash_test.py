#!/usr/bin/python

import field_hash
fh = field_hash.FieldHash()
assert(fh.hash_last_field("3\tFIRST\tSECOND\tTHIRD") ==
       'FIRST	SECOND	c39efd068faa8bf1b4e348d7cd43c229	THIRD')
print "PASS"
