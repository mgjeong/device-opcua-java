FROM java:8
MAINTAINER bg.chun@samsung.com
COPY target/device-opcua-java.jar /home/device-opcua-java.jar
COPY run.sh run.sh
EXPOSE 49997
CMD ["./run.sh"]
