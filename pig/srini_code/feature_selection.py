import datetime
from string import *

import numpy
from numpy import *
import scipy
import scipy.stats

from pig_util import outputSchema

#@outputSchema("bag{tuple(epochs:int)}")
#@outputSchema("record: {(epoch:chararray)}")
@outputSchema("record: {(epoch:int)}")
def getEpochs(timeData):
    """
    get epoEpochs
    """
    epochs = []
    ii = 0
    for y, m, d, h, mm, s in timeData:
        epoch = atoi(datetime.datetime(y,m,d,h,mm,s).strftime('%s'))
        epochs.append(tuple([epoch]))
    return(epochs)

@outputSchema("record: {(entropy:double)}")
def getEntropies(timeData):
    """
    get epoEpochs
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


