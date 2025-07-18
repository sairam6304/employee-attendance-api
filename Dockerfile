# Use a lightweight OpenJDK 21 base image
FROM openjdk:21-jdk-slim


# Set working directory inside container
WORKDIR /app

# Copy the built jar file from your target folder into the container
COPY target/attendance-0.0.1-SNAPSHOT.jar app.jar

# Expose port 9090 (your Spring Boot app port)
EXPOSE 9090

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
