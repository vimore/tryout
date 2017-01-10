#!/bin/bash
output_dir=/home/E8Admin/dashboard
if [ ! -d "$output_dir" ]; then
	mkdir $output_dir
fi

echo ------------------------------------- >> $output_dir/dashboard.log
echo "Current date and time: $(date)" >> $output_dir/dashboard.log
#date >> $output_dir/dashboard.log
echo ------------------------------------- >> $output_dir/dashboard.log
#set -x
END=6
securityToken=`curl -X GET -H 'Authorization: YWRtaW46YWRtaW4xMjM=' -H 'Content-Type: application/json' -H 'Accept: application/json'  'http://localhost:9080/service/user/securityToken'| python -c "import json,sys;obj=json.load(sys.stdin);print obj['securityToken'];"`
echo $securityToken
auth=`echo -n "admin:$securityToken" | openssl base64 -A`
echo $auth

for ((i=-1;i<END;i++)); do
        if [ $i -eq -1 ]; then
                end=`date -d "+1 days" "+%Y-%m-%dT00:00:00.000Z"`
        else
                end=`date -d "-${i} days" "+%Y-%m-%dT00:00:00.000Z"`
        fi
        endCount=$(($i+1))
        start=`date -d "-${endCount} days" "+%Y-%m-%dT00:00:00.000Z"`
        #echo "start is $start end is $end"
        curl  -H "Authorization: ${auth}" -H 'Content-Type: application/json' -H 'Accept: application/json' --connect-timeout 720 "http://localhost:9080/service/global/statistics?startTime=${start}&endTime=${end}" &&
        curl  -H "Authorization: ${auth}" -H 'Content-Type: application/json' -H 'Accept: application/json' --connect-timeout 720 "http://localhost:9080/service/global/riskyHostsAndUsers?startTime=${start}&endTime=${end}&topN=8&order=desc" &&
        echo start is: $start >>  $output_dir/dashboard.log
        echo end is: $end >>  $output_dir/dashboard.log
done
#set +x
exit
