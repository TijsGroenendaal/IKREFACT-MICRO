FROM openjdk:16-slim
COPY target/*.jar gateway.jar
VOLUME /data
ENTRYPOINT ["java","-jar","/gateway.jar"]