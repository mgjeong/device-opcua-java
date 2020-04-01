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

# FROM java:8
# COPY target/device-opcua-java.jar /home/device-opcua-java.jar
# COPY run.sh run.sh
# EXPOSE 49997
# CMD ["./run.sh"]


FROM alpine:3.4
MAINTAINER master

RUN apk --update add openjdk8-jre

# environment variables
ENV APP_DIR=./device-service
ENV APP=device-opcua-java.jar
ENV APP_PORT=49997

#copy JAR and property files to the image
COPY target/*.jar $APP_DIR/$APP

# RUN mkdir /device-service

#expose support rulesengine port
EXPOSE $APP_PORT

#set the working directory
WORKDIR $APP_DIR

#kick off the micro service
ENTRYPOINT java -jar $APP