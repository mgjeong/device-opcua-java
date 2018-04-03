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
PROJECT_ROOT=$(pwd)
echo $PROJECT_ROOT
cd ./dependencies
DEP_ROOT=$(pwd)

#start clone dependencies git repo and maven install
cd $DEP_ROOT
./build.sh

#start package device-service
cd $PROJECT_ROOT
mvn clean install -U -Dmaven.test.skip=true
echo "done"

cp /usr/bin/qemu-aarch64-static .
