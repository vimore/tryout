#!/bin/bash
debug=0

[ $debug -eq 1 ] && echo "[PRE][remove] Started "
[ $debug -eq 1 ] && echo "     Arg : $1 " 

if [ $1 -ne 0 ] ; then
	echo " Upgrade detected .. nothing further to do "
	exit 
fi
# Started / Finished  / Removed 
cur_rpm_state=Removed
cur_rpm_stage=NA 

removed_on=`date +%Y%m%d%H%M`
#create_base=0
_localstatedir="/var/tmp/rpm-state"
statefile="${_localstatedir}/.e8sec-health-agent.state"

#check the rpm install state 
if [ ! -f "${statefile}" ] ; then 
	echo "*** WARN *** Unable to read rpm state! continuing with defaults "
	tmpconfdir=`date +%Y%m%d%H%M`
	conf_save="/etc/e8sec-health-agent/conf_${tmpconfdir}"
	conf_b_save="/etc/e8sec-health-agent/baseconf_${tmpconfdir}"
	conf_base="/etc/e8sec-health-agent/conf.base"
	conf_merge="/etc/e8sec-health-agent/conf.merge"
else
	#source the rpm state 
	[ $debug -eq 1 ] && echo " sourcing rpm state  "
	. ${statefile}
	echo " BASE_DIR : ${conf_base}"
	tmpconfdir=`date +%Y%m%d%H%M`
	conf_save="/etc/e8sec-health-agent/conf_${tmpconfdir}"
	conf_b_save="/etc/e8sec-health-agent/baseconf_${tmpconfdir}"
fi

#echo "pre-install started : `date`" > ${statefile}
cp -r /etc/e8sec-health-agent/conf ${conf_save}
cp -r /etc/e8sec-health-agent/conf.base ${conf_b_save}

# Remove any lingering links to health-check
rm -f /opt/e8sec/e8sec-health-agent
rm -f /etc/e8sec-health-agent/lib/health-agent.jar
rm -f /etc/logrotate.d/e8-health-agent
rm -f /etc/default/e8sec-health-agent

#remove the base copy as well 
if [ -d "${conf_base}" ] ; then 
	echo " Removing ${conf_base}..."
	rm -rf ${conf_base}
fi
rm -rf ${conf_merge}

echo "rpm_state=${cur_rpm_state}" > ${statefile}
echo "rpm_stage=${cur_rpm_stage}" >> ${statefile}
echo "installed_on=${installed_on}" >> ${statefile}
echo "conf_base=${conf_base}" >> ${statefile}
echo "conf_save=${conf_save}" >> ${statefile}
echo "conf_b_save=${conf_b_save}" >> ${statefile}
echo "conf_merge=${conf_merge}" >> ${statefile}
echo "create_base=${create_base}" >> ${statefile}
echo "conflicts=${conflicts}" >> ${statefile}
echo "removed_on=${removed_on}" >> ${statefile}

echo "    configuration saved at ${conf_save} "
[ $debug -eq 1 ] && echo "[PRE][remove] Finished "
echo
