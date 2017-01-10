#!/usr/bin/python
import sys
from string import atoi

import scipy
import scipy.stats
import numpy as np

import iso8601


#@outputSchema("answer:tuple(confidence: double,interval: double)")
def get_beacon_confidence_and_interval(time_data):
    """
    get robust interquartile range (iqr)
    
    Find the iqr of a given set of numbers that cover first and the last quartile and
    divide it by median
    
    Comments: riqr  may vary from 0 to a large number.
    
    The larger the riqr, it is less likely to be a beacon.
    
    confidence_table converts an riqr number into a confidence number. Higher the confidence, the more
    likely it is beacon. A beacon doesnot have to be malicious; if it is associated with risk, it can be
    considered malicious.
    """
    min_interval_thr = 5
    min_count = 3
    data = []
    MAX_NUMBER = 99999999

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

    ii = 0
    data_size = 0
    for y, m, d, h, mm, s in time_data:
        data.append(atoi(datetime.datetime(y,m,d,h,mm,s).strftime('%s')))
        data_size += 1
        
    data = sort(data)
    intervals = np.array(data[1:]-data[0:-1])
    I = where(intervals > min_interval_thr)
    intervals = intervals[I]
    median  = -1
    median_count = 0
    median = scipy.stats.nanmedian(intervals)
    if(median > 0.0):
        pass
    else:
        median = 0.0
    if (len(intervals) > min_count):
        median_count = len(where(intervals==median)[0])
        iqr = scipy.stats.scoreatpercentile(intervals, 75) - scipy.stats.scoreatpercentile(intervals, 25)
        riqr = (1.0*iqr)/median
    else:
        riqr = MAX_NUMBER

    confidence = 0
    for row in confidence_table:
        if (riqr >= row[0] and riqr < row[1]):
            confidence = row[2]
    
    return (confidence, median)

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
    request_method = fields[3]
    date_times = fields[4]
    user_agent = "\t".join(fields[5:]).rstrip()
    #print user_agent
    
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
    confidence = 0.0
    if (len(intervals) > min_count):
        median = scipy.stats.nanmedian(intervals) or 0.0
        iqr = scipy.stats.scoreatpercentile(intervals, 75) - scipy.stats.scoreatpercentile(intervals, 25)
        riqr = (1.0*iqr)/median
    
        for row in confidence_table:
            if (riqr >= row[0] and riqr < row[1]):
                confidence = row[2]
    
    print "%s\t%s\t%s\t%s\t%s\t%f\t%f" % (c_ip, cs_host, hour, request_method, user_agent, confidence, median)
