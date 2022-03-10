FROM openjdk:16-slim
COPY target/*.jar bouncer.jar
ENTRYPOINT ["java","-jar","/bouncer.jar"]