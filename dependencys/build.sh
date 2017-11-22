#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencys git repo and maven install
cd $DEP_ROOT
git clone git@github.sec.samsung.net:RS7-EdgeComputing/messaging-zmq.git
cd messaging-zmq/java/edgex-emf
./build.sh

#cd $DEP_ROOT
#rm -rf protocol-opcua
#git clone git@github.sec.samsung.net:RS7-EdgeComputing/protocol-#opcua.git
#cd protocol-opcua/java/edge-opcua
#./build.sh
echo "done"
