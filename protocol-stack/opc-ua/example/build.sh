#!/bin/sh
build_home=$(pwd)
cd ../edge-opcua
client_home="opcua-client"
server_home="opcua-server"
build_cmd="mvn clean compile assembly:single"

$build_cmd

mvn install:install-file -Dfile=./target/opcua-adapter-0.0.1-SNAPSHOT-jar-with-dependencies.jar -DgroupId=com.edge.compute1.protocol -DartifactId=opcua-adapter -Dversion=0.0.1-SNAPSHOT-jar-with-dependencies -Dpackaging=jar -DgeneratePom=true

cd $build_home
cd $client_home
$build_cmd
cp ./target/opcua-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../target
cd ../$server_home
$build_cmd
cp ./target/opcua-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar ../target
