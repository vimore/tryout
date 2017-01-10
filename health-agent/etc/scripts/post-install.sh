#!/bin/bash
debug=0
conf_main="/etc/e8sec-health-agent/conf"

[ $debug -eq 1 ] && echo "[POST][Install] Started "
[ $debug -eq 1 ] && echo "     Arg : $1 " 
# Started / Finished 
cur_rpm_state=Finished
cur_rpm_stage=post-install

_localstatedir="/var/tmp/rpm-state"
statefile="${_localstatedir}/.e8sec-health-agent.state"

#check the rpm install state 
if [ ! -f "${statefile}" ] ; then
        echo "Something Wrong here with the rpm state "
        echo " will NOT be able to perform post-install tasks... "
	echo " ABORTING...."
        exit
fi

tmpconfdir=`date +%Y%m%d%H%M`
new_conf_save="/etc/e8sec-health-agent/conf_${tmpconfdir}"
new_conf_b_save="/etc/e8sec-health-agent/baseconf_${tmpconfdir}"

#source the rpm state 
if [ -f "${statefile}" ] ; then
	[ $debug -eq 1 ] && echo " sourcing rpm state  "
        . ${statefile}
        [ $debug -eq 1 ] && echo " BASE_DIR : ${conf_base}"
fi

#perform maintainence on .merge 
[ -d "${conf_merge}" ] && rm -rf ${conf_merge}
#rm -rf ${conf_merge} 

declare -a cfnames cfstatus
# [0]: Name 
# [1]: Status  [ Unchanged Changed Conflicts ]

status_str=" SUCCESS "
info_str=" No changes "
#check if the base exists 
if [ ${create_base} -eq 1 ] ; then
        echo " Creating base configuration (First time install) " 
        cp -r ${conf_main} ${conf_base}
        create_base=0
	isConflicts=0
	echo "    Saving base configuration to ${conf_base} "
fi
if [ -d "${conf_save}" ] ; then 
	#mkdir -p ${conf_merge}
	#get the copy which the rpm installed 
	#echo " Creating merge area ${conf_merge} from ${conf_main}"
	cp -r ${conf_main} ${conf_merge}
	anyUserChanges=0
	anyPkgChanges=0

	curdir=$PWD
	echo " Checking updates to config files.."
	#check if we need to take care of any configuration customizations 
	cfg_files=$(ls ${conf_b_save})
	cd ${conf_merge}
	cnt=0
	for cfile in ${cfg_files} 
	do 
		[ $debug -eq 1 ] && echo " File : ${cfile}"
		cfnames[$cnt]=$cfile
		cfstatus[$cnt]='No-changes'
		[ $debug -eq 1 ] && echo "merge ${cfile} ${conf_b_save}/${cfile} ${conf_save}/${cfile}"	
		merge ${cfile} ${conf_b_save}/${cfile} ${conf_save}/${cfile}	
		#check status 
		isChanged=$(diff ${conf_save}/${cfile} ${cfile} | wc -l)
		isChangedbyUser=$(diff ${conf_save}/${cfile} ${conf_b_save}/${cfile} | wc -l)
		[ $isChangedbyUser -ne 0 ] && cfstatus[$cnt]='User-customizations-applied'
		[ $isChangedbyUser -ne 0 ] && let anyUserChanges=anyUserChanges+1
		isChangedinPkg=$(diff ${conf_main}/${cfile} ${conf_b_save}/${cfile} | wc -l)
		[ $isChangedinPkg -ne 0 ] && cfstatus[$cnt]='package-updates '
		[ $isChangedinPkg -ne 0 ] && let anyPkgChanges=anyPkgChanges+1
		[ $debug -eq 1 ] && echo " File : ${cfile}   UC  : ${isChangedbyUser} ${anyUserChanges}   PC : ${isChangedinPkg} ${anyPkgChanges}"
		isMerged=$(cat ${cfile} | grep '>>>>>' | wc -l)
		[ $isChanged -ne 0 ] && cfstatus[$cnt]='Updated'
		[ $isMerged -ne 0 ] && cfstatus[$cnt]='CONFLICTS'
		let cnt=cnt+1
	done
	[ $debug -eq 1 ] && echo " (${anyUserChanges}) User updates   &   (${anyPkgChanges}) Package updates were detected"
	cd ${curdir}
	echo "*** INFO *** Summary "
	total_entries=${#cfnames[@]}
	entry=0
	isConflicts=0
	while [ $entry -lt $total_entries ]; do 
		printf "%5s%30s: %30s\n" " " ${cfnames[$entry]} ${cfstatus[$entry]}
		if [ ${cfstatus[$entry]} == "CONFLICTS" ]; then
			let isConflicts=isConflicts+1
		fi
		let entry=entry+1
	done
	echo "(${isConflicts}) CONFLICTS detected.. " 
	[ $debug -eq 1 ] && echo ${cfnames[@]}
	[ $debug -eq 1 ] && echo ${cfstatus[@]}
	echo 
	[ $debug -eq 1 ] && echo " We HAVE Conflicts (${isConflicts}) UserChanges (${anyUserChanges}) PkgChanges (${anyPkgChanges}) "
	# check the status before moving the config 
	if [ $isConflicts -eq 0 ]; then
		#echo "local customizations were successfully merged."
		#echo "updating config..."
		if [ $anyUserChanges -eq 0 ] ; then 
		 	if [ $anyPkgChanges -gt 0 ] ; then 
		    		info_str=" Newer package entries successfully applied "
				[ $debug -eq 1 ] && echo "Updating ${conf_base} for Newer Pkg entries.."
				/bin/cp -Rp ${conf_main}/* ${conf_base}
			fi
		else
		 	if [ $anyPkgChanges -eq 0 ] ; then 
		    		info_str=" User customizations successfully applied "
			else
		    		info_str=" User customizations & newer package entries successfully applied "
				/bin/cp -Rp ${conf_main}/* ${conf_base}
			fi
		fi
		if [ $anyUserChanges -ne 0 ] || [ $anyPkgChanges -ne 0 ] ; then 
		echo " Copying ${conf_merge}  to   ${conf_main} " 
		mkdir -p ${conf_main}
		/bin/cp -Rp ${conf_merge}/* ${conf_main}
		echo 
		echo "*** INFO ****"
		echo "    Configuration was updated sucessfully "
		echo "    its still HIGHLY RECOMMENDED that you review the configuration "
		echo "    for completeness  [${conf_main}]" 
		echo
		fi
	else
		echo "*** ERROR ****"
		echo "    Conflicts detected while merging the config "
		echo "    Will Not restart any processes... " 
		echo 
		echo " NEXT STEPS : "
		echo "      1. resolve ALL the conflicts in the files shown above " 
		echo "      2. Make sure no merge markers are still remaining in the files " 
		echo "           Markers are lines begining with [ <<<<<<  ,  ========= ,  >>>>>>> ]"
		echo "      3. Review the config files for completeness [$conf_merge]" 
		echo "      4. copy all the files above to [${conf_main}]" 
		echo "      5. Restart the processes                                   " 
		echo 
		info_str=" CONFLICTS detected "
		status_str=" ACTION REQUIRED !! " 
	fi
	echo 
	echo "*** INFO ****    ${info_str}"
	echo "*** STATUS ****  ${status_str}"
	echo 
fi

#create log dir structure 
mkdir -p /var/log/e8sec-health-agent/metrics/e8-cdh
mkdir -p /var/log/e8sec-health-agent/metrics/e8-api
mkdir -p /var/log/e8sec-health-agent/metrics/e8-ui
mkdir -p /var/log/e8sec-health-agent/metrics/e8-azkaban
#chown -R e8sec.e8sec /var/log/e8sec-health-agent
chmod -R 0775 /var/log/e8sec-health-agent

#cp the init script 
/bin/cp /etc/e8sec-health-agent/etc/e8sec-health-agent /etc/init.d
chmod 755 /etc/init.d/e8sec-health-agent

# Not a lot here right now.
rm -f /opt/e8sec/e8sec-health-agent
rm -f /etc/e8sec-health-agent/lib/health-agent.jar
rm -f /etc/logrotate.d/e8-health-agent
rm -f /etc/default/e8sec-health-agent

chown -R e8sec:e8sec /etc/e8sec-health-agent
ln -s /etc/e8sec-health-agent /opt/e8sec/e8sec-health-agent
ln -s /etc/e8sec-health-agent/lib/health-agent-1.4.1.jar /etc/e8sec-health-agent/lib/health-agent.jar
ln -s /etc/e8sec-health-agent/conf/e8-health-agent-logrotate.conf /etc/logrotate.d/e8-health-agent
ln -s /etc/e8sec-health-agent/etc/default/e8-health-agent /etc/default/e8sec-health-agent

chmod a+x /etc/e8sec-health-agent/conf


# Fix priorities
chkconfig e8sec-health-agent reset
chkconfig e8sec-health-agent resetpriorities

#echo "Health check Service installed, not started, but will start on the next boot."
echo "Health check Service installed, not started."

#perform rpm maintainence steps

echo "rpm_state=${cur_rpm_state}" > ${statefile}
echo "rpm_stage=${cur_rpm_stage}" >> ${statefile}
echo "installed_on=${installed_on}" >> ${statefile}
echo "conf_base=${conf_base}" >> ${statefile}
if [ $1 -eq 2 ]; then 
	echo "conf_save=${new_conf_save}" >> ${statefile}
	echo "conf_b_save=${new_conf_b_save}" >> ${statefile}
else
	echo "conf_save=${conf_save}" >> ${statefile}
	echo "conf_b_save=${conf_b_save}" >> ${statefile}
fi
echo "conf_merge=${conf_merge}" >> ${statefile}
echo "create_base=${create_base}" >> ${statefile}
echo "conflicts=${isConflicts}" >> ${statefile}


[ $debug -eq 1 ] && echo "[POST][Install] Finished "
echo
