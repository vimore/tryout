#!/usr/bin/python
import sys
from string import atoi

import scipy
import scipy.stats
import numpy as np

import iso8601


min_count = 3
min_interval_thr = 5
for line in sys.stdin:
    
    confidence_table = []
    confidence_table.append([0, 0.001, 1])
    confidence_table.append([0.001, 0.005, 0.97])
    confidence_table.append([0.005, 0.02, 0.95])
    confidence_table.append([0.02, 0.05, 0.92])
    confidence_table.append([0.05, 0.075, 0.90])
    confidence_table.append([0.075, 0.09, 0.87])
    confidence_table.append([0.09, 0.1, 0.85])
    confidence_table.append([0.10, 0.125, 0.82])
    confidence_table.append([0.125, 0.15, 0.80])
    confidence_table.append([0.15, 0.175, 0.70])
    confidence_table.append([0.175, 0.20, 0.60])
    confidence_table.append([0.20, 0.25, 0.50])
    confidence_table.append([0.25, 0.30, 0.40])
    confidence_table.append([0.30, 0.35, 0.30])
    confidence_table.append([0.35, 0.40, 0.20])
    confidence_table.append([0.40, 0.45, 0.10])
    confidence_table.append([0.45, 99999999, 0])
    
    fields = line.split("\t")
    c_ip = fields[0]
    cs_host = fields[1]
    hour = fields[2]
    date_times = fields[3]
    
    dates = date_times[1:][:-2].split(",")
    
    iso_dates = []
    for date in dates:
        date = date[1:][:-1]
        iso_date = iso8601.parse_date(date)
        iso_dates.append(atoi(iso_date.strftime('%s')))
    intervals = np.array(iso_dates)
    intervals = np.array(intervals[1:]-intervals[0:-1])
    I = np.where(intervals > min_interval_thr)
    intervals = intervals[I]
    
    median = 0.0
    if (len(intervals) > min_count):
        median = scipy.stats.nanmedian(intervals)
        iqr = scipy.stats.scoreatpercentile(intervals, 75) - scipy.stats.scoreatpercentile(intervals, 25)
        riqr = (1.0*iqr)/median
    
        confidence = 0.0
        for row in confidence_table:
            if (riqr >= row[0] and riqr < row[1]):
                confidence = row[2]
    
        print "%s\t%s\t%s\t%f\t%f" % (c_ip, cs_host, hour, confidence, median)
    else:
        print "%s\t%s\t%s\t0.0\t%f" % (c_ip, cs_host, hour, median or 0.0)
