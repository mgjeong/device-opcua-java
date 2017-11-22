#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencys git repo and maven install
cd $DEP_ROOT
git clone git@github.com:mgjeong/messaging-zmq.git
cd messaging-zmq/java/edgex-emf
./build.sh

echo "done"
