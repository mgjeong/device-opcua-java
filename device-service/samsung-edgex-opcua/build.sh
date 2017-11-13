#!/bin/sh
PROJECT_ROOT=$(pwd)
echo $PROJECT_ROOT
cd ../../dependencys
DEP_ROOT=$(pwd)
cd ../
REPO_ROOT=$(pwd)
cd protocol-stack/opc-ua/edge-opcua/
OPCUA_ROOT=$(pwd)

#start clone dependencys git repo and maven install
cd $DEP_ROOT
./build.sh

#start install edge-opcua
cd $OPCUA_ROOT
./build.sh

#start package device-service
cd $PROJECT_ROOT
mvn clean package
mvn install -U -Dmaven.test.skip=true
echo "done"

