
import sys
sys.path.append('/Users/sdoddi/Library/Python/2.7/lib/python/site-packages/')
sys.path.append('/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/site-packages/')
#sys.path.append('/Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/logdata/ProtoType-PlanB/iscx/Project_ThreatModeling/GraphModeling/')
sys.path.append('/Users/sdoddi/Research/TheHive/intrusion_detection/Enterprise_Security/Christophe/prototype_coding/hive-repo/prototype/GraphModeling/')

from pygraphviz import *
from networkx import *
from networkx.readwrite import *


import scipy
from scipy.stats import *

from matplotlib.pylab import *

from graphDB import *

from threatModeling import *

from threatQuery import *

from anomalyDetection import *

from patternAnalysis import *

from helperFunc import *

from entityManagement import *

#import iscx_features
#from iscx_features import *


def QueryOneDim(DB, entity_type, ip, feature, time2id):
    profile = []
    for tm_key in time2id.keys():
        profile.append(0)
        
    if (DB[entity_type][feature].has_key(ip) == True):
        for tm_key in sort(DB[entity_type][feature][ip].keys()):
            if (time2id.has_key(tm_key) == True):
                profile[time2id[tm_key]] =  DB[entity_type][feature][ip][tm_key]

    return(profile)

def QueryMultiDim(DB, ip, featureList, time_range):
    xx = 0

def baseline(vect):
    Mu = mean(vect)
    Sigma = std(vect)
    return(Mu, Sigma)

def testForNormalDistr(dataArray):
    [k2, p_val] = scipy.stats.normaltest(dataArray)

    # Low p_val is an indication of low confidence
    return(scipy.stats.normaltest(dataArray))

def NormalFit(data):
    return(scipy.stats.norm.fit(data))

def tDistr(data):
    ii = 0
    
def PoissonDistr(sample):
    k = 0
    best = -1
    for ii in unique(sample):
        poi = scipy.stats.poisson(ii)
        if (sum(poi.pmf(sample)) > best):
            best = sum(poi.pmf(sample))
            k = ii
    poi = scipy.stats.poisson(k)
    return(k, poi.pmf(k))

def ks_test(sample, distr_type, args=()):
    [D, pvalue] = scipy.stats.kstest(sample, distr_type, args)
    return(D, pvalue)

### Tests for normal distributions
def normality_test(sample):
    [teststat, pvalue] = scipy.stats.normaltest(sample)
    return(teststat, pvalue)

### End tests for normal distributions

### kernel density estimation
def Gaussian_kde(X, test_point):
    kde = gaussian_kde(X)
    return(kde(test_point))
### End Kernel desity

def GammaDistr(sample):
    result = testDistr(sample, gamma)
    if (result[0] > 0.05):
        return (1)
    else:
        reutrn(0)


def WeibullDistr():
    ii = 0
    
def ExpnentialDistr():
    ii = 0

def confidenceInterval():
    # scipy.stats.norm.interval
    ii = 0


def tryAllDistributions(sample):
    cdfs = [
        "norm",            #Normal (Gaussian)
        "alpha",           #Alpha
        "anglit",          #Anglit
        "arcsine",         #Arcsine
        "beta",            #Beta
        "betaprime",       #Beta Prime
        "bradford",        #Bradford
        "burr",            #Burr
        "cauchy",          #Cauchy
        "chi",             #Chi
        "chi2",            #Chi-squared
        "cosine",          #Cosine
        "dgamma",          #Double Gamma
        "dweibull",        #Double Weibull
        "erlang",          #Erlang
        "expon",           #Exponential
        "exponweib",       #Exponentiated Weibull
        "exponpow",        #Exponential Power
        "fatiguelife",     #Fatigue Life (Birnbaum-Sanders)
        "foldcauchy",      #Folded Cauchy
        "f",               #F (Snecdor F)
        "fisk",            #Fisk
        "foldnorm",        #Folded Normal
        "frechet_r",       #Frechet Right Sided, Extreme Value Type II
        "frechet_l",       #Frechet Left Sided, Weibull_max
        "gamma",           #Gamma
        "gausshyper",      #Gauss Hypergeometric
        "genexpon",        #Generalized Exponential
        "genextreme",      #Generalized Extreme Value
        "gengamma",        #Generalized gamma
        "genlogistic",     #Generalized Logistic
        "genpareto",       #Generalized Pareto
        "genhalflogistic", #Generalized Half Logistic
        "gilbrat",         #Gilbrat
        "gompertz",        #Gompertz (Truncated Gumbel)
        "gumbel_l",        #Left Sided Gumbel, etc.
        "gumbel_r",        #Right Sided Gumbel
        "halfcauchy",      #Half Cauchy
        "halflogistic",    #Half Logistic
        "halfnorm",        #Half Normal
        "hypsecant",       #Hyperbolic Secant
        "invgamma",        #Inverse Gamma
        #"invnorm",         #Inverse Normal
        "invweibull",      #Inverse Weibull
        "johnsonsb",       #Johnson SB
        "johnsonsu",       #Johnson SU
        "laplace",         #Laplace
        "logistic",        #Logistic
        "loggamma",        #Log-Gamma
        "loglaplace",      #Log-Laplace (Log Double Exponential)
        "lognorm",         #Log-Normal
        "lomax",           #Lomax (Pareto of the second kind)
        "maxwell",         #Maxwell
        "mielke",          #Mielke's Beta-Kappa
        "nakagami",        #Nakagami
        "ncx2",            #Non-central chi-squared
        #    "ncf",             #Non-central F
        "nct",             #Non-central Student's T
        "pareto",          #Pareto
        "powerlaw",        #Power-function
        "powerlognorm",    #Power log normal
        "powernorm",       #Power normal
        "rdist",           #R distribution
        "reciprocal",      #Reciprocal
        "rayleigh",        #Rayleigh
        "rice",            #Rice
        "recipinvgauss",   #Reciprocal Inverse Gaussian
        "semicircular",    #Semicircular
        "t",               #Student's T
        "triang",          #Triangular
        "truncexpon",      #Truncated Exponential
        "truncnorm",       #Truncated Normal
        "tukeylambda",     #Tukey-Lambda
        "uniform",         #Uniform
        "vonmises",        #Von-Mises (Circular)
        "wald",            #Wald
        "weibull_min",     #Minimum Weibull (see Frechet)
        "weibull_max",     #Maximum Weibull (see Frechet)
        "wrapcauchy",      #Wrapped Cauchy
        "ksone",           #Kolmogorov-Smirnov one-sided (no stats)
        "kstwobign"]       #Kolmogorov-Smirnov two-sided test for Large N

    result = []
    for cdf in cdfs:
    #fit our data set against every probability distribution
        xx = "scipy.stats."+cdf+".fit(sample)"
        parameters = eval("scipy.stats."+cdf+".fit(sample)");
 
    #Applying the Kolmogorov-Smirnof one sided test
        D, p = scipy.stats.kstest(sample, cdf, args=parameters)
 
    #pretty-print the results
        print cdf.ljust(16) + ("p: "+str(p)).ljust(25)+"D: "+str(D);
        result.append([p,D])

    return(result)

def testDistr(sample, dstr):
    xx = "scipy.stats."+ dstr+".fit(sample)"
    parameters = eval("scipy.stats."+dstr+".fit(sample)");
    #Applying the Kolmogorov-Smirnof one sided test
    D, p = scipy.stats.kstest(sample, dstr, args=parameters);
 
    #pretty-print the results
    print dstr.ljust(16) + ("p: "+str(p)).ljust(25)+"D: "+str(D)
    result = [p,D]
    return(result)

    
