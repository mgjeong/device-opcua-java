#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencies git repo and maven install
cd $DEP_ROOT
git clone https://github.com/mgjeong/messaging-zmq.git
cd messaging-zmq/java/edgex-emf
./build.sh

#cd $DEP_ROOT
#git clone https://github.sec.samsung.net/RS7-EdgeComputing/command-#json-format-java.git
#cd command-json-format-java/java/edge-json-format
#./build.sh

echo "done"
