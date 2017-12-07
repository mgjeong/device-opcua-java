#!/bin/sh
PROJECT_ROOT=$(pwd)
echo $PROJECT_ROOT
echo "Start of device-opcua-java test"
REPO_ROOT=$(pwd)
cd ./dependencies/protocol-opcua-java/
OPCUA_ROOT=$(pwd)

#start unit test protocol-opcua-java
cd $OPCUA_ROOT
./unittest.sh

cd $REPO_ROOT
mvn test
#check unit test fail
if [ $? -ne 0 ]; then
        echo "Unittest is failed."; exit 1
fi

echo "End of device-opcua-java test"


echo "done"
