FROM maven:3.8.6-openjdk-11-slim as build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .
COPY src ./src
# Cache dependencies
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

# Build the application
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]