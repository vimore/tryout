#!/usr/bin/python

import sys
import math

import scipy
import scipy.stats


for line in sys.stdin:
    values = []
    fields = line.split("\t")

    # Must split from the right because we can't trust the format
    # of the group_field, but we can trust the time_field is a
    # standard ISO timestamp.
    #sys.stderr.write("PARSING: " + fields[0] + "\n")
    #sys.stderr.write("GROUP: " + fields[1] + "\n")
    type_field, period_field, time_field, group_field = fields[0][1:][:-1].split(',',3)
    # Remove leading/trailing spaces
    time_field = time_field.rstrip()
    type_field = type_field.rstrip()
    period_field = period_field.rstrip()
    # Temove leading/trailing spaces or quotes
    group_field = group_field.rstrip().rstrip('"')
    
    series = fields[1][1:][:-2].split(',')
    for s in series:
        val = s[1:][:-1]
        values.append(float(val))
        
    entropy = float(scipy.stats.entropy(values)/math.log(len(values)))
    if entropy != None:
        print "%s\t%s\t%s\t%s\t%f" % (type_field, group_field, period_field, time_field, entropy)
