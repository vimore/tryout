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
    max_val = len(data)
    return(max_val)

@outputSchema("ratio:double")
def getRatio(a,b):
    ratio = 1.0 - (1.0*a/b)
    return(ratio)

    
@outputSchema("sld:chararray")
def extractSld(site):
    arr = tldextract.extract(site)
    if (arr.suffix == ''):
        sld = arr.domain
    else:
        sld = arr.domain + '.' + arr.suffix
    return(sld)

@outputSchema("results: {t:(item: double)}")
def getTimeIntervals(timeData):
    """
    get epoEpochs
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
    for interval in intervals:
        item = interval
        data_results.append(tuple([interval]))

    if (len(intervals) > min_count):
        median = scipy.stats.nanmedian(intervals)
        iqr = scipy.stats.scoreatpercentile(intervals, 75) - scipy.stats.scoreatpercentile(intervals, 25)
        item = (1.0*iqr)/median
        data_results.append(tuple([item]))

    return(data_results)

@outputSchema("record: {(entropy:double)}")
def getEntropies(timeData):
    """
    get epoEntropies
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

