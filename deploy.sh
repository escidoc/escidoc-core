#!/bin/bash
# Frank.Schwichtenberg@FIZ-Karlsruhe.de, 2011

echo $0
echo "Params: $*"
#JBOSS_HOME=/unspecified/jboss/home
if [[ "x$JBOSS_HOME" == "x" ]]; then
    echo "Please set JBOSS_HOME or edit this script."
    exit
fi

BUILD_COMMAND="time mvn install -DskipTests"
DEPLOY_DIR=$JBOSS_HOME/server/default/deploy/
MODULES_TO_BUILD="$* escidoc-core.ear"

if [[ $1 == "all" || "x$1" == "x" ]]; then
    echo "Building entire project."
    sleep 3
    MODULES_TO_BUILD=.
else
    echo "Building $MODULES_TO_BUILD"
    sleep 3
fi

for module in $MODULES_TO_BUILD; do
    echo "Building $module"
    cd $module
    $BUILD_COMMAND
    cd -
done

echo cp -v escidoc-core.ear/target/escidoc-core-trunk-SNAPSHOT.ear $DEPLOY_DIR
