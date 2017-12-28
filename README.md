OPC-UA Device Service
=======================================

OPC-UA Device Service is implemented based on Device Service SDK.

OPC-UA devices to generate Events and Readings to Core Data Micro Service. 
Furthermore, users can send commands and get responses through Command and Control Micro Service.

## Prerequisites ##
- JDK
  - Version : 1.8
  - [How to install](https://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html)
- Maven
  - Version : 3.5.2
  - [Where to download](https://maven.apache.org/download.cgi)
  - [How to install](https://maven.apache.org/install.html)
  - [Setting up proxy for maven](https://maven.apache.org/guides/mini/guide-proxies.html)
- Certificates
  - Access https://nexus.edgexfoundry.org/ using browser(ex. firefox, google Chrome)
  - Extract certificate file (DST Root CA X3)
  - add certificate file to jre using keytool
    `sudo keytool -importcert -noprompt -trustcacerts -alias ALIASNAME -file /PATH/TO/YOUR/DESKTOP/CertificateName.cer -keystore /PATH/TO/YOUR/JDK/jre/lib/security/cacerts -storepass changeit`

## How to build ##

#### 1. Executable binary ####
```shell
$ ./build.sh
```
If source codes are successfully built, you can find an output binary file, **target**, on a root of project folder.
Note that, you can find other build scripts, **build_arm.sh** and **build_arm64**, which can be used to build the codes for ARM and ARM64 machines, respectively.

#### 2. Docker Image  ####
Next, you can create it to a Docker image.
```shell
$ docker build -t device-opcua-java -f Dockerfile .
```
If it succeeds, you can see the built image as follows:
```shell
$ sudo docker images
REPOSITORY                 TAG               IMAGE ID            CREATED             SIZE
device-opcua-java          latest            bb7c3f3860ab        6 seconds ago       715MB
```
Note that, you can find other Dockerfiles, **Dockerfile_arm** and **Dockerfile_arm64**, which can be used to dockerize for ARM and ARM64 machines, respectively.


## How to run with Docker image ##

```shell
$ docker-compose -f ./docker-compose.yml up
```


## Reference ##
#### [OPC-UA protocol stack library(Stand-alone) build](https://mgjeong/protocol-opcua-java/blob/master/edge-opcua/README.md)
