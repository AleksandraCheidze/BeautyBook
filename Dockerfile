# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/BeautyBook-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"] 