#!/usr/local/bin/python

import os
import sys

sys.path.insert(0, os.getcwd())
sys.path.append('/usr/lib/python2.6/site-packages')

from numpy import *

import scipy

from matplotlib import *
from pylab import *

from sklearn.decomposition import PCA

class Kcenter:
    def __init__(self):
        xx = 0
    def __iter__(self):
        return self

    def genPCAData(self, M, normalized=True):
        pca = PCA(M.shape[1])
        if (normalized == True):
            pca.fit(M)
        else:
            print (M.shape)
            pca.fit((M - M.mean(0))/M.std(0))
        return(pca)

    
    def kcenter_clustering(self, M, k):
        n,d = M.shape

        distArr = 99999999*numpy.ones(n, dtype=double)
        cluster_indices = numpy.zeros(n, dtype=int32)
        centers = numpy.zeros(k+1, dtype = int32)
        cluster_indices[0] = 0
        cluster_indices[0] = int(n*scipy.rand(1))

        radii = []
        for ii in range(k):
            maxDist = -1
            newCenter = -1
            for jj in range(n):
                euclid_dist = (M[jj, :]-M[centers[ii],:])
                euclid_dist = sum(euclid_dist*euclid_dist)
                if (euclid_dist < distArr[jj]):
                    distArr[jj] = euclid_dist
                    cluster_indices[jj] = ii

                if (maxDist < distArr[jj]):
                    maxDist = distArr[jj]
                    newCenter = jj

            centers[ii+1] = newCenter
            print (ii, newCenter, maxDist, centers[ii+1])
            radii.append(maxDist)
        centers = centers[:-1]
        print (centers)
        return(centers, cluster_indices, radii)

    def kcenter_clustering_stopcriteria_0(self, M, k, factor=0.001):
        n,d = M.shape

        distArr = 99999999*numpy.ones(n, dtype=double)
        cluster_indices = numpy.zeros(n, dtype=int32)
        centers = numpy.zeros(k+1, dtype = int32)
        cluster_indices[0] = 0
        cluster_indices[0] = int(n*scipy.rand(1))

        radii = []
        flag = 0
        cc = 0
        for ii in range(k):
            maxEuclid_Dist = -1
            newCenter = -1
            for jj in range(n):
                euclid_dist = (M[jj, :]-M[centers[ii],:])
                euclid_dist = sum(euclid_dist*euclid_dist)
                if (euclid_dist < distArr[jj]):
                    distArr[jj] = euclid_dist
                    cluster_indices[jj] = ii

                if (maxEuclid_Dist < distArr[jj]):
                    maxEuclid_Dist = distArr[jj]
                    newCenter = jj

            centers[ii+1] = newCenter
            print (ii, newCenter, maxEuclid_Dist, centers[ii+1])
            radii.append(maxEuclid_Dist)
            if (cc == 20):
                if (max(distArr) < factor*self.getMeanHub2HubDist(M, centers)):
                    flag = 1
                    break
                cc = 0
            cc += 1

            if (flag == 1):
                break
        centers = centers[0:ii-1]
        print (centers)
        return(centers, cluster_indices, radii)


    def getMeanHub2HubDist(self, M, hubs):
        dists = []
        for ii in range(len(hubs)):
            for jj in range(ii+1, len(hubs)):
                dist = (M[hubs[ii], :]-M[hubs[jj],:])
                dist = sum(dist*dist)
                dists.append(dist)
        return(mean(array(dists)))


    def getClusterStats(self, D, cl2indices, dim):
        M = zeros((len(cl2indices.keys()), dim+1)) 
        for ii in sort(cl2indices.keys()):
            mx = D[cl2indices[ii],:].mean(0)
            M[ii,0] = len(cl2indices[ii])
            for jj in range(1,dim):
                M[ii,jj] = mx[jj]
        return(M)

        
    
    
    
    
            
        
        
        
        
        


    
    
