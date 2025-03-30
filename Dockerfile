FROM openjdk:17-slim AS builder
WORKDIR /app

# Установка Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

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

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
