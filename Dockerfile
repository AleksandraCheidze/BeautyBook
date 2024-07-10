FROM docker.io/maven:3.8.3-openjdk-17 AS build
COPY . /home/src
WORKDIR /home/src
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/src/target/*.jar /app/project.jar

ENTRYPOINT ["java", "-jar", "/app/project.jar"]