#!/usr/bin/python

import sys
import math

import scipy.stats


for line in sys.stdin:
  values = []
  fields = line.split("\t")

  sum_field = fields[len(fields)-1]
  series = sum_field[1:][:-2].split(',')
  for s in series:
    val = s[1:][:-1]
    values.append(float(val))
  bin_count = len(values)
  entropy = float(scipy.stats.entropy(values)/math.log(bin_count))
  if entropy != None:
    print "%s\t%d\t%f" % ("\t".join(fields[:-1]), bin_count, entropy)
