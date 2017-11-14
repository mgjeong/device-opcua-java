FROM java:8
MAINTAINER bg.chun@samsung.com
COPY target/device-service-opcua.jar /home/device-service-opcua.jar
COPY run.sh run.sh
EXPOSE 49997
CMD ["./run.sh"]
