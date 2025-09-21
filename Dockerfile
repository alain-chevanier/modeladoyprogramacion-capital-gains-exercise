# Use the official Maven base image to build the Spring Boot application
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files (pom.xml) to the container
COPY pom.xml .

# Download the Maven dependencies (including Spring Boot) into the container
RUN mvn dependency:go-offline

# Copy the source code to the container
COPY src ./src

# Build the Spring Boot application and package it into a JAR
RUN mvn package

# Use the official OpenJDK base image with Java 17 for running the application
FROM eclipse-temurin:17-jdk-jammy
# adoptopenjdk/openjdk17:alpine-jre

# Set the working directory in the container
WORKDIR /app

CMD mkdir target

# Copy the compiled JAR file from the build stage into the container
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar ./target/demo-0.0.1-SNAPSHOT.jar

COPY run-tests .
COPY examples ./examples