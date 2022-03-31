FROM openjdk:16-slim
COPY target/*.jar webhookservice.jar
VOLUME /data
ENTRYPOINT ["java","-jar","/webhookservice.jar"]