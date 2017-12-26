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
   
   1) `tar xzvf apache-maven-3.5.2-bin.tar.gz`
   
   2) Add the bin directory of the created directory apache-maven-3.5.2 to the PATH environment variable<br></br>
### Add Certificate your envorionments
1. get certificate file using browser

   1) Access https://nexus.edgexfoundry.org/ using browser(ex. firefox, google Chrome)
   
   2) Extract certificate file (DST Root CA X3)

2. add certificate file to jre using keytool

   1) `sudo keytool -importcert -noprompt -trustcacerts -alias ALIASNAME -file /PATH/TO/YOUR/DESKTOP/CertificateName.cer -keystore /PATH/TO/YOUR/JDK/jre/lib/security/cacerts -storepass changeit`
<br></br>
## How to build OPC-UA Device Service
1. cd ./device-opcua-java

2. ./build.sh

3. docker build -t device-opcua-java .
<br></br>

#### OPC-UA library(Stand-alone) build [here](https://mgjeong/protocol-opcua-java/blob/master/README.md)
