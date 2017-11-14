#!/bin/sh
echo "Start edge opcua build"
edge_opcua_home=$(pwd)
cd ../../mapper/mavenProjects
mapper_home=$(pwd)
cd $edge_opcua_home


#start install mapper
cd $mapper_home
mvn clean
mvn package
mvn install:install-file  -Dfile=./target/edge-mapper-0.0.1-SNAPSHOT.jar -DgroupId=mapper -DartifactId=edge-mapper -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

#start install edge_opcua
cd $edge_opcua_home
mvn install -U -Dmaven.test.skip=true
rm -rf target/

echo "End of edge opcua build"
