FROM openjdk:23-ea-22-jdk-oracle
LABEL maintainer=binz

COPY target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]