import datetime
from string import *

import numpy
from numpy import *
import scipy
import scipy.stats
from scipy.stats import *

from pig_util import outputSchema
import tldextract
from tldextract import *


@outputSchema("max_val:int")
def getMaxVal(data):
    """
    Get Max of a given set of numbers
    """
    
    max_val = len(data)
    return(max_val)

@outputSchema("ratio:double")
def getRatio(a,b):
    """
    get probablities
    """
    
    ratio = 1.0 - (1.0*a/b)
    return(ratio)

    
@outputSchema("sld:chararray")
def extractSld(site):
    """
    get Second Level Domains
    """
    if site is None:
        return ""
    arr = tldextract.extract(site)
    if (arr.suffix == ''):
        sld = arr.domain
    else:
        sld = arr.domain + '.' + arr.suffix
    return(sld)


@outputSchema("riqr:double")
def getRobustIqr(timeData):
    """
    get robust interquartile range (iqr)
    Find the iqr of a given set of numbers that cover first and the last quartile and
    divide it by median
    Comments: riqr  may vary from 0 to a large number.
    The larger the riqr, it is less likely to be a beacon.
    """
    min_interval_thr = 5
    min_count = 3
    data = []
    ii = 0
    for y, m, d, h, mm, s in timeData:
        data.append(atoi(datetime.datetime(y,m,d,h,mm,s).strftime('%s')))
        
    data = sort(data)
    intervals = np.array(data[1:]-data[0:-1])
    I = where(intervals > min_interval_thr)
    intervals = intervals[I]
    data_results = []
    if (len(intervals) > min_count):
        median = scipy.stats.nanmedian(intervals)
        iqr = scipy.stats.scoreatpercentile(intervals, 75) - scipy.stats.scoreatpercentile(intervals, 25)
        item = (1.0*iqr)/median
        data_results.append(tuple([item]))
        riqr = item
    return(riqr)

@outputSchema("answer:tuple(confidence: double,interval: double)")
def getBeaconConfidence(timeData):
    """
    get robust interquartile range (iqr)
    Find the iqr of a given set of numbers that cover first and the last quartile and
    divide it by median
    Comments:
    riqr  may vary from 0 to a large number.
    
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
    for y, m, d, h, mm, s in timeData:
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


@outputSchema("record: {(entropy:double)}")
def getEntropies(timeData):
    """
    get epoEntropies of a given set of numbers
    """
    thr = 5
    if (len(timeData) > thr):
        epochs = scipy.zeros(len(timeData))
        ii = 0
        for y, m, d, h, mm, s in timeData:
            epochs[ii] = atoi(datetime.datetime(y,m,d,h,mm,s).strftime('%s'))
            ii += 1
        epochs = scipy.sort(epochs)
        deltas = epochs[1:]-epochs[0:-1]
        C = scipy.stats.itemfreq(deltas)[:,-1]
        P = C/(1.0*sum(C))
        entropy = 0
        for ii in range(len(C)):
            entropy -= P[ii]*numpy.log10(P[ii])
    else:
        entropy = -1.0
    
    values = []
    values.append(tuple([entropy]))
    return(values)

