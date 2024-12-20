# Build Stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Runtime Stage
FROM openjdk:17.0.1-jdk-slim
COPY --from=build target/transaction-0.0.1-SNAPSHOT.jar transaction.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "transaction.jar"]
