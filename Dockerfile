# Use the official OpenJDK 17 runtime image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target folder into the container
COPY target/*.jar /app/

# Expose the port your application will run on (update if necessary)
EXPOSE 8080

# Command to run the JAR file
CMD ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]