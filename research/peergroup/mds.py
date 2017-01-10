# Author: Nelle Varoquaux <nelle.varoquaux@gmail.com>
# Licence: BSD

print(__doc__)
import numpy as np
from numpy import *
import matplotlib.pyplot as plt

from sklearn import manifold
from sklearn.metrics import euclidean_distances


class  Mds:
    def __init__(self):
        xx = 0
    def __iter__(self):
        return self

    def runMds(self, X, volume,title):
        seed = 0
        X_true = X- X.mean(0)
        n = X.shape[0]

        similarities = euclidean_distances(X_true.astype(np.float64))

        for ii in range(n):
            for jj in range(ii+1,n):
                similarities[jj,ii] = similarities[ii,jj]

        print ('---> ', np.abs(similarities - similarities.T).max())

        mds = manifold.MDS(n_components=2, max_iter=3000, eps=1e-9, random_state=seed, dissimilarity="precomputed", n_jobs=1)
        pos = mds.fit(similarities.astype(np.float64)).embedding_
        #pos *= np.sqrt((X_true ** 2).sum()) / np.sqrt((pos ** 2).sum())


        Ix = where(abs(pos[:,0]) > 50)[0]
        Iy = where(abs(pos[:,1]) > 50)[0]

        volume[Ix]  = 10.0*volume[Ix]
        volume[Iy]  = 10.0*volume[Iy]

        fig, ax = plt.subplots()
        ax.scatter(pos[:, 0], pos[:, 1],  s=volume, c='g', alpha = 0.25)
        ax.grid(True)
        fig.tight_layout()
        ax.set_title(title)

        plt.show()
        return(similarities, pos)


    def runMds2(self, X, volume,title):
        seed = 0
        X_true = X- X.mean(0)
        n = X.shape[0]

        similarities = euclidean_distances(X_true.astype(np.float64))
        #for ii in range(n):
        #for jj in range(ii+1,n):
        #similarities[jj,ii] = similarities[ii,jj]


        mds = manifold.MDS(n_components=2, max_iter=3000, eps=1e-9, random_state=seed, dissimilarity="precomputed", n_jobs=1)
        pos = mds.fit(similarities.astype(np.float64)).embedding_

        fig, ax = plt.subplots()

        volume_temp = 10*ones((1,len(volume)))[0]

        ax.scatter(pos[:, 0], pos[:, 1],  s=volume_temp, c='g', alpha = 0.25)
        ax.grid(True)
        fig.tight_layout()
        ax.set_title(title)

        plt.show()
        return(similarities, pos)

    
    def scatterByDistance(self, X, volume,title):
        seed = 0
        X_true = (X- X.mean(0))/X.std(0)
        n = X.shape[0]
        print ('-->     ', len(volume))

        
        max_id = where(volume == max(volume))[0][0]
        col0 = zeros((1,len(volume)))[0]
        col1 = zeros((1,len(volume)))[0]

        for ii in range(len(volume)):

            delta = array(X_true[max_id,:] - X_true[ii,:])
            col0[ii] = sum(delta*delta)
            col1[ii] = log(volume[ii])
        
        fig, ax = plt.subplots()
        ax.scatter(col0, col1)
        plt.xlabel('Distance')
        plt.ylabel('Log(Volume)')
        plt.show()
        return(col0, col1)


if __name__ == '__main__':
    M = Mds()
    X = numpy.loadtxt('srcip_entropy.txt')


