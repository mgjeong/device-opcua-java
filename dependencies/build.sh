#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencies git repo and maven install
cd $DEP_ROOT
#git clone https://github.com/mgjeong/messaging-zmq.git
git clone git@github.sec.samsung.net:RS7-EdgeComputing/protocol-ezmq-java.git
if [ ! -d "protocol-ezmq-java" ]
then
    echo "build fail"
    exit;
else
    cd protocol-ezmq-java/edgex-ezmq
    ./build.sh
fi

cd $DEP_ROOT
#git clone https://github.sec.samsung.net/RS7-EdgeComputing/protocol-opcua-java.git
git clone git@github.sec.samsung.net:RS7-EdgeComputing/protocol-opcua-java.git
if [ ! -d "protocol-opcua-java" ]
then
    echo "build fail"
    exit;
else
    cd protocol-opcua-java
    ./build.sh
fi

cd $DEP_ROOT
#git clone https://github.sec.samsung.net/RS7-EdgeComputing/datamodel-command-java.git
git clone git@github.sec.samsung.net:RS7-EdgeComputing/datamodel-command-java.git
if [ ! -d "datamodel-command-java" ]
then
    echo "build fail"
    exit;
else
    cd datamodel-command-java
    ./build.sh
fi

echo "done"

