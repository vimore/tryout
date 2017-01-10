#! /bin/bash
PID=`ps -ef | grep feature-service | awk '{ print $2 }'`
kill -9 $PID
