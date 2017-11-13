# How to test OPC-UA Sample Application

## Reference

- Run `./build.sh.`
  You can get OPC-UA Server/Client jar file in taret folder.
<br></br>
## Build
### How to build sample application in command line

1. Environment : Ubuntu 16.04 (64bit)

2. In *protocol-stack/opc-ua/example*, run `./build.sh` in command line

   ![build_3_1](./images/build_3_1.png)

3. You can execute opcua-client/opcua-server sample with
   
   `java -jar target/opcua-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar`
   
   `java -jar target/opcua-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar` in each command window

   ![build_3_2](./images/build_3_2.png)

### How to build sample application with Eclipse

1. Environment : Eclipse

2. make project
   File - Import -Existing Maven Project - next
     -> select Root Directory in your source directory : *protocol-stack/opc-ua/edge-opcua/mavenProjects*
     -> finish

3. make project
   File - Import -Existing Maven Project - next
     -> select Root Directory in your source directory : *protocol-stack/opc-ua/example*
     -> finish

4. You can build on each java file which has main() and run as 'java application' in opcua-client/opcua-server

5. Reference : If you have errors in pom.xml, Select Project -> Click Right button 
                  -> Maven -> Update Maven Project -> Check Force Update of Snapshots/Releases -> OK

   ![build_4_1](./images/build_4_1.png)
<br></br>
## Test

### 1. Execute *OPC-UA Server* <br>
   command : `java -jar target/opcua-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar`
   
   ![server_1](./images/server_1.PNG)

   *start* : start opcua server / create Node  -> first of all, you should input this command for testing

   *getnode* : get information of created node currently

   *getnode2* : get information of created node with browse name

   *quit* : termiate server

   *help* : print menu
<br>
### 2. Execute *OPC-UA Client* <br>
   command : `java -jar target/opcua-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

   ![client_1](./images/client_1.PNG)

   *start* : start opcua client / connect with opcua server / intialize Service Provider

   *quit* : terminate client

   *provider* : get  providers name of created node currently

   *browse* : browse all node using root node

   *browse_m* : multiple browse with several nodes

   *read_s* : read value attribute for server node

   *read_t* : read value attribute for target node

   *read_gp* : multiple read value attribute for nodes

   *write* : write attribute into node

   *write_gp* : multiple value write attributes into nodes

   *sub* : subscription(1ms)

   *sub_modify* : modfiy subscription

   *sub_delete* : delete subscription

   *endpoint* : get endpoint lists from opcua server

   *auto* : run test automatically

   *help* : print menu
<br>
### 3. Start Server <br>
   input `start` and than, input `Your IP Address` in **OPC-UA Server**.
   ![server_2](./images/server_2.PNG)
   
   Then start *OPC-UA Server*. And create service node.
   ![server_3](./images/server_3.PNG)
<br>
### 4. Start Client <br>
   input `start` and than, input `opc.tcp://[OPC-UA Server's IP Addreess]/edge-opc-server` in **OPC-UA Client**.
   ![client_2](./images/client_2.PNG)
   
   Then, *OPC-UA Client* connects with *OPC-UA Server*. <br>
   And Create service provider
   ![client_3](./images/client_3.PNG)
   
   And, Show *Provider ValueAlias's List*
   ![client_4](./images/client_4.PNG)
<br>
### 5. Read Command<br>
   input `read_t` and than, input `Provider ValueAlias` (refer. *Provider ValueAlias's List*).<br>
   Then read server node. 
   ![client_5](./images/client_5.PNG)
<br>     
### 6. Write Command<br>
   input `write` and than, input `Provider ValueAlias`.<br>
   Then write to server node. 
   ![client_6](./images/client_6.PNG)
<br>     
### 7. Monitoring <br>
   input `start CNC` in *OPC-UA Server*.<br>
   Then Run 'cnc100'
   ![server_8](./images/server_8.PNG)
   
   input `sub` in *OPC-UA Client*.<br>
   Then subscribe to server node.
   ![client_7](./images/client_7.PNG)
   ![client_8](./images/client_8.PNG)



