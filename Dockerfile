FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Копируем только файлы, необходимые для загрузки зависимостей
COPY pom.xml .

# Загружаем зависимости и кэшируем их
RUN mvn dependency:go-offline && \
    mkdir -p /root/.m2/repository && \
    chmod -R 777 /root/.m2

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
