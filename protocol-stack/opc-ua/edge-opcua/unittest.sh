#!/bin/sh
echo "Start edge opcua unit test"
edge_opcua_home=$(pwd)

#start install edge_opcua
cd $edge_opcua_home
mvn test
#check unit test fail
if [ $? -ne 0 ]; then
        echo "Unittest is failed."; exit 1
fi

echo "End of edge opcua unit test"
