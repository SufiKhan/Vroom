# Step 1: Build stage using an official Gradle-Java 21 image
FROM gradle:8.14-jdk21 AS build
WORKDIR /app

# Copy the build configuration and source code into the container
COPY build.gradle settings.gradle ./
COPY src ./src

# Compile and build the project, skipping unit tests to speed up the container build
RUN gradle bootJar --no-daemon -x test

# Step 2: Final lightweight runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the compiled execution JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Open up port 8080 so external web traffic can connect
EXPOSE 8080

# Command to run the trading application inside the isolated container
ENTRYPOINT ["java", "-jar", "app.jar"]
