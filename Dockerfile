FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем только файлы, необходимые для загрузки зависимостей
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .

# Загружаем зависимости
RUN --mount=type=cache,id=railway-cache-maven,target=/root/.m2 mvn dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем приложение с подробным выводом
RUN --mount=type=cache,id=railway-cache-maven,target=/root/.m2 mvn clean package -DskipTests -X

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
