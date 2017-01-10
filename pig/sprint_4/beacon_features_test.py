#!/usr/local/bin/python

import beacon_features
import pig_parsing

b = beacon_features.BeaconFeatures()
# Run the full analysis on this list of timestamps.
assert(b.extract_features(['2014-01-01T12:00:00Z', '2014-01-01T11:00:05Z',
                           '2014-01-01T13:00:10Z', '2014-01-02T11:00:00Z',
                           '2014-01-02T12:00:09Z', '2014-01-02T13:00:00Z'])
     == (3609.0, 0.0041562759767248547,
         ['2014-01-01T11:00:05Z', '2014-01-01T12:00:00Z',
          '2014-01-01T13:00:10Z', '2014-01-02T11:00:00Z',
          '2014-01-02T12:00:09Z'],
         {3609.0: 4, 79190.0: 1},
         ['2014-01-01T11:00:00.000000Z', '2014-01-01T12:00:00.000000Z',
          '2014-01-01T13:00:00.000000Z', '2014-01-02T11:00:00.000000Z',
          '2014-01-02T12:00:00.000000Z']))

p = pig_parsing.PigParsing()
assert(b.extract_features(p.str2strlist(
    '{(2014-03-31T20:17:37.000Z),(2014-03-31T20:17:38.000Z),'
     '(2014-03-31T20:17:38.000Z),(2014-03-31T20:17:38.000Z),'
     '(2014-03-31T20:17:38.000Z),(2014-03-31T20:17:39.000Z),'
     '(2014-03-31T20:17:39.000Z),(2014-03-31T20:17:39.000Z),'
     '(2014-03-31T20:17:39.000Z),(2014-03-31T20:17:39.000Z)}'))
    == (None, None, None, None, None))

print "PASS"
