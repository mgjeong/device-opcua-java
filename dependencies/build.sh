#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencies git repo and maven install
cd $DEP_ROOT
git clone https://github.com/mgjeong/messaging-zmq.git
cd messaging-zmq/java/edgex-emf
./build.sh

cd $DEP_ROOT
git clone https://github.sec.samsung.net/RS7-EdgeComputing/datamodel-command-java.git
cd datamodel-command-java/command-json-format
./build.sh

cd $DEP_ROOT
git clone https://github.sec.samsung.net/RS7-EdgeComputing/protocol-opcua-java.git
cd protocol-opcua-java/java/edge-opcua
./build.sh

echo "done"
