#!/bin/bash

# simple packager for a target.  It's gonna run maven too.

# THIS IS JUST A WRAPPER FOR FPM, YOU MUST HAVE THAT.
# you must also have jinja2 and j2cli

# service name
MYSVC="e8-api-server"

# defaults
VERSION="0.2"
SVC_USER=${SVC_USER:-e8admin}
E8_ENVIRONMENT=${E8_ENVIRONMENT:-qa}

# the defaults for install locations.  this should be merged with the defaults in etc.. later.
SVC_HOME="/opt/e8sec/$MYSVC"
E8_LOG_DIR="/var/log/$MYSVC"
E8_CONF_DIR="/etc/$MYSVC"
CONF_FILE="config.yml"
CONF_EXTRA="timeSeries.yml webAnomalyProfiles.yml securityEvents.yml dev_cfg.yml prod_cfg.yml qa2_cfg.yml qa_cfg.yml searchConf.yml"
VENDOR="E8 Security"
MAINTAINER="pulu@e8security.com"

# snapver? 
SNAPVER=$(date +%y%m%d%H%M).${E8_ENVIRONMENT}.$(git log -n 1 --pretty=format:"%h")

myroot=$(dirname "$0")"/../"
tmpdir=`mktemp -d`

echo "repo root is $myroot"

echo "getting target"

pushd $myroot

#mvn clean install
MYJAR=./target/feature-service-1.0.0-SNAPSHOT.jar

# the CONF_EXTRA stuff are not really config files, they are in /etc but they need to be deployed with the app...
# build the mapping.
myce=""
for i in $CONF_EXTRA
do
	myce="$myce ./src/main/config/$i=/etc/$MYSVC/$i "
done

# we have a template engine and some yaml but things need cleaning up. for now, append our stuff to the yaml.


# have to feed the user to the rpm so it knows what to do re: the files/etc.  again.  should be a shared config item.
# this is put into the pre-install scripts.
cat > $tmpdir/pre-install.sh <<EOF0
#!/bin/bash
SVC_USER=$SVC_USER
SVC_HOME=$SVC_HOME
E8_LOG_DIR="$E8_LOG_DIR"
EOF0

cat rpm/pre-install.sh >> $tmpdir/pre-install.sh

# we have a config template, let's make a config from that.
# first, get the yaml and add our variables.
for i in E8_LOG_DIR E8_CONF_DIR E8_ENVIRONMENT
do
	echo "$i: ${!i}" >> $tmpdir/template-variables.yml
done

if [[ -e $myroot/rpm/${E8_ENVIRONMENT}.yml ]]; then cat $myroot/rpm/${E8_ENVIRONMENT}.yml >> $tmpdir/template-variables.yml; fi

# now we generate the actual configfile.
j2 -f yaml $myroot/src/main/config/templates/cfg.yml.k2 $tmpdir/template-variables.yml > $tmpdir/config.yml

cat $tmpdir/config.yml

# this is dirty, should be calling this guy from maven, not enough time.
fpm -s dir -t rpm -n $MYSVC \
	--provides "$MYSVC" \
	--version "$VERSION" \
	--iteration "$SNAPVER" \
	--rpm-os linux \
	-a noarch \
	--depends java \
	--rpm-user "$SVC_USER" \
	--rpm-group "$SVC_USER" \
	--vendor "$VENDOR" \
	-m "$MAINTAINER" \
	--pre-install $tmpdir/pre-install.sh \
	--after-install rpm/post-install.sh \
	--directories "$SVC_HOME" \
	--config-files /etc/default/$MYSVC \
	--config-files "/etc/$MYSVC/config.yml" \
	--verbose \
	--debug \
	./etc/default/$MYSVC=/etc/default/"$MYSVC" \
	"$tmpdir/config.yml"="/etc/$MYSVC/config.yml" \
	$myce \
	./etc/init.d/$MYSVC=/etc/rc.d/init.d/"$MYSVC" \
	$MYJAR="$SVC_HOME/lib/feature-service.jar"
