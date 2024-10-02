# Use the official OpenJDK 17 runtime image
FROM openjdk:17-jdk-slim

# Set a build argument for the active Spring profile
ARG ACTIVE_PROFILE=dev

# Add a label to specify the active profile, 这个可加可不加, 用于说明镜像元数据信息的
LABEL active-profile=${ACTIVE_PROFILE}

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target folder into the container
COPY target/*.jar /app/

# Expose the port your application will run on (update if necessary)
EXPOSE 8080

# Command to run the JAR file
CMD ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=${ACTIVE_PROFILE}"]