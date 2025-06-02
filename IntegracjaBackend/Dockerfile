# Build stage
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline

COPY src ./src
COPY service.wsdl .
RUN ./mvnw package -DskipTests


# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy the jar file
COPY --from=builder /app/target/*-with-dependencies.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]