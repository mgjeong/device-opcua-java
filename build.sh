#!/bin/sh
PROJECT_ROOT=$(pwd)
echo $PROJECT_ROOT
cd ./dependencies
DEP_ROOT=$(pwd)

#start clone dependencies git repo and maven install
cd $DEP_ROOT
./build.sh

#start package device-service
cd $PROJECT_ROOT
mvn clean install -U -Dmaven.test.skip=true
echo "done"

