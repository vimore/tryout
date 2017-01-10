#!/usr/local/bin/python

import os
import sys

import timestamps


### Tests for Timestamps()
t = timestamps.Timestamps()

### Tests for TimestampBinner()
tb = timestamps.TimestampBinner('10m')
assert(tb.iso_bin('2014-01-01T12:43:00.000000Z') == '2014-01-01T12:40:00.000000Z')
tb = timestamps.TimestampBinner('1m')
assert(tb.iso_bin('2014-01-01T12:43:13.010000Z') == '2014-01-01T12:43:00.000000Z')
tb = timestamps.TimestampBinner('15s')
assert(tb.iso_bin('2014-01-01T12:43:23.010000Z') == '2014-01-01T12:43:15.000000Z')
tb = timestamps.TimestampBinner('1d')
assert(tb.iso_bin('2014-01-01T12:43:23.010000Z') == '2014-01-01T00:00:00.000000Z')
tb = timestamps.TimestampBinner('1h')
assert(tb.iso_bin('2014-01-01T12:43:23.010000Z') == '2014-01-01T12:00:00.000000Z')
tb = timestamps.TimestampBinner('7d', last_date='2014-03-15')
assert(tb.iso_bin('2014-03-05T12:15:13.000Z') == '2014-03-02T00:00:00.000000Z')
assert(tb.iso_bin('2014-03-12T12:15:13.000Z') == '2014-03-09T00:00:00.000000Z')
assert(tb.iso_bin('2014-01-15T23:22:12.0000Z') == '2014-01-12T00:00:00.000000Z')
assert(tb.iso_bin('2014-03-16T12:01:11.000110Z') == '2014-03-16T00:00:00.000000Z')
tb = timestamps.TimestampBinner('7d', first_date='2014-03-16')
assert(tb.iso_bin('2014-03-05T12:15:13.000Z') == '2014-03-02T00:00:00.000000Z')
assert(tb.iso_bin('2014-03-12T12:15:13.000Z') == '2014-03-09T00:00:00.000000Z')
assert(tb.iso_bin('2014-01-15T23:22:12.0000Z') == '2014-01-12T00:00:00.000000Z')
assert(tb.iso_bin('2014-03-16T12:01:11.000110Z') == '2014-03-16T00:00:00.000000Z')
tb = timestamps.TimestampBinner('7d', first_weekday='Sunday')
assert(tb.iso_bin('2014-03-05T12:15:13.000Z') == '2014-03-02T00:00:00.000000Z')
assert(tb.iso_bin('2014-03-12T12:15:13.000Z') == '2014-03-09T00:00:00.000000Z')
assert(tb.iso_bin('2014-01-15T23:22:12.0000Z') == '2014-01-12T00:00:00.000000Z')
assert(tb.iso_bin('2014-03-16T12:01:11.000110Z') == '2014-03-16T00:00:00.000000Z')

# Redirect stderr to /dev/null
sys.stderr = open(os.devnull, 'w')

timestamp_list, iso_timestamps = t.parse_timestamps(
  [0, None, '2014-01-01T12:00:00Z',
   'xxx',   '2014-01-01T11:00:00Z'])
assert(t.get_intervals(timestamp_list) == ([(0, 1)], [3600.0]))
# Returned ordered and without the elements that are not timestamps.
assert(iso_timestamps == ['2014-01-01T11:00:00Z',
                          '2014-01-01T12:00:00Z'])

# Don't allow zero bins.
try:
  tb = timestamps.TimestampBinner('0d')
  assert(False)
except TypeError:
  # This is what we want to happen.
  pass

# Don't allow nonsensical bin_spec.
try:
  tb = timestamps.TimestampBinner('kjsvlkjrv')
  assert(False)
except TypeError:
  # This is what we want to happen.
  pass

# Don't allow specification of first_weekday
# unless bin_spec='7d'
try:
  tb = timestamps.TimestampBinner('3d', first_weekday='Sunday')
  assert(False)
except TypeError:
  pass

# Don't allow specification of last_date, first_date or
# first_weekday unless bin_spec='Xd' where X > 1
try:
  tb = timestamps.TimestampBinner('1d', last_date='2014-01-01')
  assert(False)
except TypeError:
  pass
try:
  tb = timestamps.TimestampBinner('1d', first_date='2014-01-01')
  assert(False)
except TypeError:
  pass
try:
  tb = timestamps.TimestampBinner('1d', first_weekday='Sunday')
  assert(False)
except TypeError:
  pass
try:
  tb = timestamps.TimestampBinner('3h', first_weekday='Sunday')
except TypeError:
  pass
try:
  tb = timestamps.TimestampBinner('3h', first_date='2014-01-01')
  assert(False)
except TypeError:
  pass
try:
  tb = timestamps.TimestampBinner('3h', last_date='2014-01-01')
  assert(False)
except TypeError:
  pass

# first_weekday if bin_spec != '7d'

print "PASS"
