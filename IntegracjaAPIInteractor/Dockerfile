# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .

COPY service.wsdl .
COPY src ./src

RUN mvn clean package

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar" , "app.jar", "--server.port=8090"] 