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
# param.period=86400
PERIOD=86400
# param.environment=${environment}
ENVIRONMENT='research'

pig \
  -p year=$YEAR \
  -p month=$MONTH \
  -p day=$DAY \
  -p hour=$HOUR \
  -p filter=$FILTER \
  -p period=$PERIOD \
  -p environment=$ENVIRONMENT \
  src/features/features.pig
