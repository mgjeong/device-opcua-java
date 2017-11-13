#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencys git repo and maven install
cd $DEP_ROOT
rm -rf core-domain
git clone https://github.com/edgexfoundry/core-domain.git
cd core-domain
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf core-exception
git clone https://github.com/edgexfoundry/core-exception.git
cd core-exception
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf core-test
git clone https://github.com/edgexfoundry/core-test.git
cd core-test
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf core-metadata-client
git clone https://github.com/edgexfoundry/core-metadata-client.git
cd core-metadata-client
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf core-data-client
git clone https://github.com/edgexfoundry/core-data-client.git
cd core-data-client
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf support-domain
git clone https://github.com/edgexfoundry/support-domain.git
cd support-domain
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf support-logging-client
git clone https://github.com/edgexfoundry/support-logging-client.git
cd support-logging-client
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
rm -rf core-command-client
git clone https://github.com/edgexfoundry/core-command-client.git
cd core-command-client
mvn install -Dmaven.test.skip=true -U
cd $DEP_ROOT
git clone git@github.sec.samsung.net:RS7-EdgeComputing/EMF.git
cd EMF/java/edgex-emf
./build.sh
echo "done"
