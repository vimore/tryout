#!/usr/local/bin/python

import sys

from numpy import array
from scipy.cluster.vq import *


class SparseHistogram():

  def __init__(self):
    pass

  def binary_search_kmeans(self, vector, tolerance):
    '''Perform binary search of k parameter in k means clustering.

    Accepts:
      vector: A one-dimensional list of values to be clustered.
      tolerance: The maximum distortion allowed for a valid clustering
         result

    Returns:
      centroids: The centroids of the minimum number of clusters
         satisfying the tolerance condition, or None if the
         tolerance condition could not be satisfied.
    '''
    high = 1
    low = len(vector)
    results = {}
    centroids, distortion = kmeans(vector, high, 10, tolerance)
    results[high] = (centroids, distortion)
    if distortion <= tolerance:
      # k=1 is already below tolerance.
      return centroids
    while abs(low - high) > 1:
      midrange = (low + high) / 2
      if results.has_key(midrange):
        centroids, distortion = results[midrange]
      else:
        centroids, distortion = kmeans(vector, midrange, 10, tolerance)
        results[midrange] = (centroids, distortion)
      if distortion <= tolerance:
        low = midrange
      else:
        high = midrange
    for index in [high, midrange, low]:
      if results.has_key(index):
        centroids, distortion = results[index]
      else:
        centroids, distortion = kmeans(vector, midrange, 10, tolerance)
        results[index] = (centroids, distortion)
      if distortion <= tolerance:
        return centroids
    return None

  def count(self, list, tolerance, desired_key=None):
    '''Find appropriate bin locations and count entries.

    Accepts:
      list: A list of integer or floating point values.
      tolerance: The maximum tolerable distortion on the clusters.
      desired_key: One of the keys should be set to this value,
        if within tolerance.
    '''
    vector = array(list)
    centroids = self.binary_search_kmeans(vector, tolerance)
    if centroids is None:
      sys.stderr.write('reporter:counter:Data Error,Clustering Failed,1\n')
      return None

    # Count the number of elements near each centroid.
    last_len = len(vector) + 1
    counts = {}
    vector = vector.tolist()
    total_counts = len(vector)
    for index in range(len(vector)):
      closest_centroid = None
      smallest_difference = 999999999
      for centroid in centroids:
        difference = abs(vector[index] - centroid)
        if difference < smallest_difference:
          closest_centroid = centroid
          smallest_difference = difference
      if counts.has_key(closest_centroid):
        counts[closest_centroid] = counts[closest_centroid] + 1
      else:
        counts[closest_centroid] = 1
      
    # If requested, find the element in the histogram that corresponds
    # to the desired_key and set it to exactly the median_interval to
    # make the UI display easier.
    if desired_key:
      lowest_difference = 99999999.0
      closest_key = None
      for key in counts:
        difference = abs(key - desired_key)
        if difference < lowest_difference:
          lowest_difference = difference
          closest_key = key
      if closest_key and lowest_difference <= tolerance and not lowest_difference == 0.0:
        # Found a closest key that's not identical to desired_key,
        # replace it with the median_interval.
        counts[desired_key] = counts[closest_key]
        del counts[closest_key]
      
    return counts

if __name__ == '__main__':
  import pydoc
  sh = SparseHistogram()
  print pydoc.render_doc(sh, 'Help on %s')
