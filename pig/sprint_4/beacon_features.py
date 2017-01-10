#!/usr/local/bin/python

import sys

import numpy
import scipy.stats


sys.path.append('.')  # Look in local directory for modules.
# Local imports. Must SHIP these!
import beacon_confidence
import domains
import pig_parsing
import sparse_hist
import timestamps

class BeaconFeatures():
  def __init__(self, beacon_timestamps = 3, min_intervals = 3):
    '''Calculate a set of beaconing features.
    
    Usage:
      b = BeaconingFeatures()
      b.extract_features(<list of possibly unordered ISO timestamp strings>)
    
    Accepts:
      beacon_timestamps: Minimum number of timestamps to return indicating
        event timestamps that are part of the beaconing activity.
        (default = 3)
      min_intervals: Minimum number of intervals that we need to see
        to be able to calculate reliably that this is a beacon.
        (default = 3)
    '''
    self.beacon_timestamps = beacon_timestamps
    self.min_intervals = min_intervals
    self.ts = timestamps.Timestamps()
    self.hour_binner = timestamps.TimestampBinner('1h')
    self.sh = sparse_hist.SparseHistogram()
    self.confidence = beacon_confidence.BeaconConfidence()

  def extract_features(self, iso_timestamps, loghashes):
    '''Given an unordered list of timestamps, return beacon metrics.
    
    Accepts:
      iso_timestamps: An unordered list of ISO timestamp strings.

    Returns:
      median_interval: the most likely beaconing interval length, in seconds.
      riqr: the relative-interquartile range, a robust measure of how tightly
        grouped the interval values are. This is converted to a confidence
        in the modeling phase.
      beacon_list: an ordered list of ISO timestamps strings identical
        to some of the input values, which indicates particular event
        timestamps that were found to be part of the beaconing
        activity.
      sparse_histogram: a dictionary where the keys are interval values,
        and the values are the count of how many intervals are in a bin
        centered on that value.
    '''
    nothing = (None, None, None, None, None)  # Null return value.
    iso_timestamps, loghashes = zip(*sorted(
        zip(iso_timestamps, loghashes)))
    timestamps, selected_timestamps = self.ts.parse_timestamps(
        iso_timestamps)
    # All timestamps parsed correctly, or don't continue.
    try:
      assert(len(selected_timestamps) == len(iso_timestamps))
      assert(len(timestamps) == len(iso_timestamps))
    except AssertionError:
      sys.stderr.write("reporter:counter:Data Error,Timestamp parse error,1\n")
      return nothing
    indexes, intervals = self.ts.get_intervals(timestamps)
    if len(intervals) < self.min_intervals:
      # We don't have enough time intervals to be able to tell
      # whether or not this is a beacon, so we have to assume
      # it is not one.
      return nothing
    median_interval = numpy.median(intervals)
    try:
      q1 = numpy.percentile(intervals, 25)
      q3 = numpy.percentile(intervals, 75)
    except AttributeError:
      # Version of numpy without .percentile, use scipy.stats instead.
      q1 = scipy.stats.scoreatpercentile(intervals, 25)
      q3 = scipy.stats.scoreatpercentile(intervals, 75)
    if median_interval == 0:
      # Not a meaningful interval. This shouldn't happen, but to
      # avoid divide-by-zero errors, we identify this as a non-beacon.
      riqr = 100.0
    else:
      riqr = (q3 - q1) / median_interval
    if riqr >= self.confidence.MAX_RIQR:
      # Confidence = 0, so no need to record this data as a possible beacon.
      return nothing
    tolerance = max(5.0, 0.01 * median_interval)
    sparse_histogram = self.sh.count(intervals, tolerance, median_interval)
    if not sparse_histogram:
      # No sparse histogram was returned. Since the data is otherwise sane, just
      # generate an empty dictionary and move on.
      sparse_histogram = {}
    index = 0
    beacon_list = []
    loghash_list = []
    indexes_used = []
    while index < len(intervals):
      if intervals[index] >= q1 and intervals[index] <= q3:
        begin, end = indexes[index]
        if begin not in indexes_used:
          beacon_list.append(selected_timestamps[begin])
          loghash_list.append(loghashes[begin])
          indexes_used.append(begin)
        if end not in indexes_used:
          beacon_list.append(selected_timestamps[end])
          loghash_list.append(loghashes[end])
          indexes_used.append(end)
      index = index + 1
    hours_active = []
    for beacon_time in beacon_list:
      hour = self.hour_binner.iso_bin(beacon_time)
      if hour not in hours_active:
        hours_active.append(hour)
    return (median_interval, riqr, loghash_list,
            sparse_histogram, hours_active)

if __name__ == '__main__':
  b = BeaconFeatures()
  parser = pig_parsing.PigParsing()

  # FOREACH sorted_grouped GENERATE 
  #    sourceNameOrIp, destinationNameOrIp, sorted_datetimes, sorted_hashes;

  for line in sys.stdin:
    fields = line.split("\t")
    sourceNameOrIp = fields[0]
    destinationNameOrIp = fields[1]
    date_times = parser.str2strlist(fields[2])
    loghashes = parser.str2strlist(fields[3])
    try:
      (interval, riqr, loghash_list, sparse_histogram,
       hours_active) = b.extract_features(date_times, loghashes)
    except ValueError:
      sys.stderr.write(line + '\n')
      raise
    SLD = domains.get_sld(destinationNameOrIp)

    # If interval is not None (i.e. there were enough intervals to meaningfully
    # detect beacons, then print out the results.
    if interval:
      # Make sure we have at least the minimum number of array entries.
      while len(loghash_list) < b.beacon_timestamps:
        loghash_list.append('dummy_hash')
      print "%s\t%s\t%s\t%f\t%f\t%s\t%s\t%s" % (
          sourceNameOrIp,
          destinationNameOrIp,
          SLD,
          interval,
          riqr,
          parser.list2tuple(loghash_list[:b.beacon_timestamps]),
          parser.dict2map(sparse_histogram),
          parser.list2map(hours_active))
