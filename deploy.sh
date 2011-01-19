#!/bin/bash
# Frank.Schwichtenberg@FIZ-Karlsruhe.de, 2011

echo $0
echo "Params: $*"

BUILD_COMMAND="time mvn install -DskipTests"
DEPLOY_DIR=/home/frs/opt/jboss-4.2.3.GA/server/default/deploy/
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

cp -v escidoc-core.ear/target/escidoc-core-trunk-SNAPSHOT.ear $DEPLOY_DIR
