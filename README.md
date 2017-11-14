# OPC-UA Device Service

## Pre-Requirements to build OPC-UA Device Service

### JDK 1.8
1. Intall JDK v1.8

   Install Guide : https://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html
<br></br>
### maven
1. Install maven v3.5.2

   Download : https://maven.apache.org/download.cgi

   Install Guide : https://maven.apache.org/install.html
<br></br>
## How to build OPC-UA Device Service
1. git clone *git@github.sec.samsung.net/RS7-EdgeComputing/device-opcua*

2. cd ./device-service/samsung-edgex-opcua

3. ./build.sh

4. docker build -t samsung-edgex-opcua .
<br></br>

#### OPC-UA library(Stand-alone) build [here](./protocol-stack/opc-ua/edge-opcua/README.md)
