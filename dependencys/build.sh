#!/bin/sh
DEP_ROOT=$(pwd)

#start clone dependencys git repo and maven install
cd $DEP_ROOT
git clone git@github.sec.samsung.net:RS7-EdgeComputing/EMF.git
cd EMF/java/edgex-emf
./build.sh
echo "done"
