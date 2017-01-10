#!/bin/bash

# Needs to be run from the azkaban_project directory, so that the
# paths below work.

# type=pig
# pig.script=src/beacons/beacon_features.pig
# 
# azkaban.should.proxy=true
# user.to.proxy=azkaban
# 
# HDFSRoot=/tmp
# 
# param.year=${azkaban.flow.one.day.back.year}

YEAR=2014

# param.month=${azkaban.flow.one.day.back.month}

MONTH=06

# param.day=${azkaban.flow.one.day.back.day}

DAY=25

# param.hour=/

HOUR=/

# param.filter=day

FILTER='day'

# param.datefunc=ISOToDay
DATEFUNC='ISOToDay'

# param.environment=${environment}
ENVIRONMENT='research'

pig \
 -p year=$YEAR \
 -p month=$MONTH \
 -p day=$DAY \
 -p hour=$HOUR \
 -p filter=$FILTER \
 -p datefunc=$DATEFUNC \
 -p environment=$ENVIRONMENT \
 src/beacons/beacon_features.pig
