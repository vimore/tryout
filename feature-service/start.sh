#!/bin/bash
#
# start.sh - A script the start the server for environments
# TODO: abstract configs better than sep files.

##### Help 
function usage
{
    echo "usage: start.sh [prod | dev]"
	echo "  default: 'dev'"
}

function cfg_switch
{
  # uses the globals below.
  mycf="$CFG_DIR/${1}_cfg.yml"
  if [ ! -e "$mycf" ]
  then
    # not there
    return 1
  fi
  cfg=$mycf
}

##### Assign Constants
readonly TARGET=target/feature-service-1.0.0-SNAPSHOT.jar
readonly CFG_DIR=./src/main/config
readonly DEF_CFG=dev

##### Main
cfg=$CFG_DIR/${DEF_CFG}_cfg.yml

##### Command Line Parameters
while [ "$1" != "" ]; do
    case $1 in
       -h | --help | -* ) usage
                          exit
                          ;;
        * )               if cfg_switch $1
                          then
                            echo "Using config $1"
                          else
                            echo -e "--\nFailed config update to $1\n\nMaybe that doesn't exist?\n"
                            usage
                            exit 1
                          fi
    esac
    shift
done

#### Build & Run Command
printf -v exec_cmd 'nohup java -jar %s server %s' "$TARGET" "$cfg"
$exec_cmd &
