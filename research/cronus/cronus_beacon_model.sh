#!/bin/bash

# param.year=${azkaban.flow.one.day.back.year}
YEAR=2014
# param.month=${azkaban.flow.one.day.back.month}
MONTH=06
# param.day=${azkaban.flow.one.day.back.day}
DAY=25
# param.hour=/
HOUR='/'
# param.filter=day
FILTER='day'
# param.datefunc=ISOToDay
DATEFUNC='ISOToDay'
# param.period=86400
PERIOD=86400
# param.command_hour=00
COMMAND_HOUR=00
# param.environment=${environment}
ENVIRONMENT='research'
# param.max_anomaly=1.0
MAX_ANOMALY=1.0
# param.multiplier=1.0
MULTIPLIER=1.0
# param.min_beacon=3600.0
MIN_BEACON=0.0  # Is this the minimum beaconing interval? We want all beacons.

# # To load yesterday's relative features
# param.day_ago_year=${azkaban.flow.two.day.back.year}
DAY_AGO_YEAR=2104
# param.day_ago_month=${azkaban.flow.two.day.back.month}
DAY_AGO_MONTH=06
# param.day_ago_day=${azkaban.flow.two.day.back.day}
DAY_AGO_DAY=25  # Using the same day because we only have 1 day of data.
# param.day_ago_hour=/
DAY_AGO_HOUR='/'
# param.day_ago_filter=day
DAY_AGO_FILTER='day'
# param.day_ago_period=86400
DAY_AGO_PERIOD=86400

pig \
  -p year=$YEAR \
  -p month=$MONTH \
  -p day=$DAY \
  -p hour=$HOUR \
  -p filter=$FILTER \
  -p datefunc=$DATEFUNC \
  -p period=$PERIOD \
  -p command_hour=$COMMAND_HOUR \
  -p environment=$ENVIRONMENT \
  -p max_anomaly=$MAX_ANOMALY \
  -p multiplier=$MULTIPLIER \
  -p min_beacon=$MIN_BEACON \
  -p day_ago_year=$DAY_AGO_YEAR \
  -p day_ago_month=$DAY_AGO_MONTH \
  -p day_ago_day=$DAY_AGO_DAY \
  -p day_ago_hour=$DAY_AGO_HOUR \
  -p day_ago_filter=$DAY_AGO_FILTER \
  -p day_ago_period=$DAY_AGO_PERIOD \
    src/beacons/beacon_model.pig
