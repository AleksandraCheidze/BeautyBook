FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Копируем только файлы, необходимые для загрузки зависимостей
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Загружаем зависимости
RUN ./mvnw dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
