#!/usr/local/bin/python

import md5
import sys

class FieldHash():
  def __init__(self):
    pass

  def hash_last_field(self, line):
   fields = line.split('\t')
   field_count = int(fields[0])
   # Assume that the last field could contain tabs.
   last_field = '\t'.join(fields[field_count:])
   hash = md5.new(last_field).hexdigest()
   out_line = '\t'.join(fields[1:field_count]) + '\t'
   out_line = out_line + hash + '\t' + last_field
   return out_line

if __name__ == '__main__':
  fh = FieldHash()
  for line in sys.stdin:
    line = line.rstrip('\n')
    print hash_last_field(line)
