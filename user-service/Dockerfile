FROM openjdk:16-slim
COPY target/*.jar userservice.jar
VOLUME /data
ENTRYPOINT ["java","-jar","/userservice.jar"]