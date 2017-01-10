#!/bin/bash
debug=0

[ $debug -eq 1 ] && echo "[PRE][Install] Started "

#determine what we are doing Install / Upgrade 
if [ $1 -eq 1 ] ; then
	_operation="install"
elif [ $1 -eq 2 ] ; then
	_operation="upgrad"
fi
echo "*** INFO ***  We are ${_operation}ing ($1).." 
# Started / Finished 
rpm_state=Started 
rpm_stage=pre-install 
tmpconfdir=`date +%Y%m%d%H%M`
create_base=0
_localstatedir="/var/tmp/rpm-state"
statefile="${_localstatedir}/.e8sec-health-agent.state"
conf_base="/etc/e8sec-health-agent/conf.base"
conf_save="/etc/e8sec-health-agent/conf_${tmpconfdir}"
conf_b_save="/etc/e8sec-health-agent/baseconf_${tmpconfdir}"
conf_merge="/etc/e8sec-health-agent/conf.merge"

isConflicts=0

#check if any ENV settings are done 
upgd_frm_10=0
if [ ! -z "${E8_HCK_PREV_CONF}" ] || [ ! -z "${E8_HCK_BASE_VER_CONF}" ] ; then 
	echo "              Environment  setting detected for using OLDER config" 
	if [ "X${E8_HCK_PREV_CONF}" = "X" ] ; then 
		echo " ERROR ENV variable conf dir not set properly |${E8_HCK_PREV_CONF}"
		exit -1
	fi
	if [ ! -d "${E8_HCK_PREV_CONF}" ] ; then 
		echo " non-existent backup conf directory ${E8_HCK_PREV_CONF}"
		exit -1
	fi
	conf_save=${E8_HCK_PREV_CONF}
	echo "              Configuration dir   (${E8_HCK_PREV_CONF})" 
	if [ "X${E8_HCK_BASE_VER_CONF}" = "X" ] ; then 
		echo " ERROR ENV variable version not set properly |${E8_HCK_BASE_VER_CONF}"
		exit -1
	fi
	if [ ! -d "${E8_HCK_BASE_VER_CONF}" ] ; then 
		echo " non-existent base conf directory ${E8_HCK_BASE_VER_CONF}"
		exit -1
	fi
	conf_b_save=${E8_HCK_BASE_VER_CONF}
	echo "              Configuration dir   (${E8_HCK_PREV_CONF})" 
	echo "              Base version dir    (${E8_HCK_BASE_VER_CONF})" 
	upgd_from_10=1
else
	echo " ENV overrides not specified ..,,,, skipping check "
fi
echo
mkdir -p ${_localstatedir}
#check the rpm install state 
if [ -f "${statefile}" ] ; then 
	echo " checking previous instance "
	cur_rpm_state=$(cat ${statefile} | grep rpm_state | cut -d= -f2) 
	cur_rpm_stage=$(cat ${statefile} | grep rpm_stage | cut -d= -f2) 
	cur_conf_save=$(cat ${statefile} | grep conf_save | cut -d= -f2) 
	cur_conf_b_save=$(cat ${statefile} | grep conf_b_save | cut -d= -f2) 
	isConflicts=$(cat ${statefile} | grep conflicts | cut -d= -f2) 
	[ $debug -eq 1 ] && echo " STATE : ${cur_rpm_state}"
	[ $debug -eq 1 ] && echo " STAGE : ${cur_rpm_stage}"
	if [ "${isConflicts}" -eq 1 ] ; then 
		mhdr1=$(grep '<<<<<' ${conf_merge}/*  | wc -l) 
		mhdr2=$(grep '=====' ${conf_merge}/*  | wc -l) 
		mhdr3=$(grep '>>>>>' ${conf_merge}/*  | wc -l) 
		echo " GOT |${mhdr1}|${mhdr2}|${mhdr3}| "
		if [ ${mhdr1} -eq 0 ] &&  [ ${mhdr2} -eq 0 ] && [ ${mhdr3} -eq 0 ] ; then 
			isConflicts=0
		else
			echo 
			echo "*** ERROR ***  configuration CONFLICTS from previous upgrade "
			echo "               have not been resolved successfully yet.."
			echo 
			echo "               Continuing with upgrade with overwrite your config !! "
			echo "               please correct this situation before proceeding "
			echo "    merged configuration can be found at  [${conf_merge}]"
			echo 
			exit -1 
		fi	
	fi
	if [ "${cur_rpm_state}" == "Finished" ] && [ "${cur_rpm_stage}" == "post-install" ] ; then 
		if [ $1 -ne 2 ] ; then 
			[ $debug -eq 1 ] && echo " unlinking the file .. "
			unlink ${statefile}
		fi
	elif [ "${cur_rpm_state}" == "Removed" ] && [ "${cur_rpm_stage}" == "NA" ] ; then 
			echo "*** INFO ***  detected a previous copy of config [${cur_conf_save}]"
			echo "              will try to apply any customizations from the same  "
			echo "              base version ${cur_conf_b_save}"
			conf_save=${cur_conf_save}
			conf_b_save=${cur_conf_b_save}
	else
		echo 	
		echo " Oops ... Something is Wrong "
		echo " one more rpm install in progres.. ABORTING"
		echo 	
		exit 
	fi
	#exit
fi

#check if the base exists 
if [ ! -d "${conf_base}" ] ; then 
	[ $debug -eq 1 ] && echo " This is a First time install " 
	#make this as the base once installed 
	create_base=1
fi 

#echo "pre-install started : `date`" > ${statefile}
if [ ${create_base} -eq 0 ] ; then
	if [ $1 -ne 2 ] ; then 
		[ $debug -eq 1 ] && echo " unlinking the file .. "
		unlink ${statefile}
	fi
	if [ -d "/etc/e8sec-health-agent/conf" ] ; then 
        	echo "   making a copy of the existing configuration... " 
		/bin/rm -rf ${conf_save}
        	cp -r /etc/e8sec-health-agent/conf ${conf_save}
        	cp -r /etc/e8sec-health-agent/conf.base ${conf_b_save}
		echo "   Configuration saved at ${conf_save} "
		echo "   Base version saved at ${conf_b_save} "
		echo 
	fi
fi

echo "rpm_state=${rpm_state}" > ${statefile}
echo "rpm_stage=${rpm_stage}" >> ${statefile}
echo "installed_on=${tmpconfdir}" >> ${statefile}
echo "conf_base=${conf_base}" >> ${statefile}
echo "conf_save=${conf_save}" >> ${statefile}
echo "conf_b_save=${conf_b_save}" >> ${statefile}
echo "conf_merge=${conf_merge}" >> ${statefile}
echo "create_base=${create_base}" >> ${statefile}
echo "conflicts=${isConflicts}" >> ${statefile}

[ $debug -eq 1 ] && echo "[PRE][Install] Finished "
echo  
