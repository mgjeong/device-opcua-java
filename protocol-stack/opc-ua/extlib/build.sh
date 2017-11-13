#!/bin/sh
ext_lib=$(pwd)
mkdir lib
cd lib
lib_path=$(pwd)

cd $ext_lib
git clone -b release/0.1.3 https://github.com/eclipse/milo.git

cd milo
milo_home=$(pwd)
git cherry-pick 815cc35b32370253e90c5afe5c705497d0e9e476

mvn clean
mvn package -Dmaven.test.skip=true -U

cp $milo_home/opc-ua-stack/stack-core/target/stack-core-0.1.4-SNAPSHOT.jar $lib_path
cp $milo_home/opc-ua-stack/stack-server/target/stack-server-0.1.4-SNAPSHOT.jar $lib_path
cp $milo_home/opc-ua-stack/stack-client/target/stack-client-0.1.4-SNAPSHOT.jar $lib_path
cp $milo_home/opc-ua-sdk/sdk-core/target/sdk-core-0.1.4-SNAPSHOT.jar $lib_path
cp $milo_home/opc-ua-sdk/sdk-server/target/sdk-server-0.1.4-SNAPSHOT.jar $lib_path
cp $milo_home/opc-ua-sdk/sdk-client/target/sdk-client-0.1.4-SNAPSHOT.jar $lib_path

mvn install:install-file  -Dfile=$lib_path/sdk-client-0.1.4-SNAPSHOT.jar -DgroupId=milo -DartifactId=sdk-client -Dversion=0.1.4-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file  -Dfile=$lib_path/sdk-server-0.1.4-SNAPSHOT.jar -DgroupId=milo -DartifactId=sdk-server -Dversion=0.1.4-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file  -Dfile=$lib_path/sdk-core-0.1.4-SNAPSHOT.jar -DgroupId=milo -DartifactId=sdk-core -Dversion=0.1.4-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file  -Dfile=$lib_path/stack-client-0.1.4-SNAPSHOT.jar -DgroupId=milo -DartifactId=stack-client -Dversion=0.1.4-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file  -Dfile=$lib_path/stack-server-0.1.4-SNAPSHOT.jar -DgroupId=milo -DartifactId=stack-server -Dversion=0.1.4-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=$lib_path/stack-core-0.1.4-SNAPSHOT.jar -DgroupId=milo -DartifactId=stack-core -Dversion=0.1.4-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

cd $ext_lib
rm -rf $lib_path
rm -rf $milo_home


