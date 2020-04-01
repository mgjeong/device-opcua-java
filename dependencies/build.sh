###############################################################################
# Copyright 2017 Samsung Electronics All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
###############################################################################

#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencies git repo and maven install
cd $DEP_ROOT
git clone -b alpha https://github.com/mgjeong/protocol-ezmq-java.git
if [ ! -d "protocol-ezmq-java" ]
then
    echo "build fail"
    exit;
else
    cd protocol-ezmq-java/ezmq
    ./build.sh
fi

cd $DEP_ROOT
git clone -b alpha https://github.com/mgjeong/protocol-opcua-java.git
if [ ! -d "protocol-opcua-java" ]
then
    echo "build fail"
    exit;
else
    cd protocol-opcua-java
    ./build.sh
fi

cd $DEP_ROOT
git clone -b alpha https://github.com/mgjeong/datamodel-command-java.git
if [ ! -d "datamodel-command-java" ]
then
    echo "build fail"
    exit;
else
    cd datamodel-command-java
    ./build.sh
fi

echo "done"
