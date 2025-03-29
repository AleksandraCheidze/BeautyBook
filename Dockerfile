FROM maven:3.8.6-openjdk-11-slim as build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
#COPY mvnw.cmd . # Если не нужен, можно не копировать
COPY src ./src

RUN chmod +x ./mvnw && ./mvnw dependency:go-offline
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

FROM openjdk:11-jre-slim

WORKDIR /app

# Указываем точное имя JAR файла, если известно
COPY --from=build /app/target/BeautyBook-1.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
