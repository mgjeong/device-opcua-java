#!/bin/sh
PROJECT_ROOT=$(pwd)
echo $PROJECT_ROOT
REPO_ROOT=$(pwd)
cd ../../protocol-stack/opc-ua/edge-opcua/mavenProjects
OPCUA_ROOT=$(pwd)

#start unit test edge-opcua
cd $OPCUA_ROOT
./unittest.sh

echo "done"
