# Используем Maven с OpenJDK 11 для сборки
FROM maven:3.8.6-openjdk-11-slim as build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем необходимые файлы для сборки
COPY pom.xml .  # Копируем только pom.xml для начала
COPY .mvn .mvn  # Копируем Maven wrapper и настройки
COPY mvnw .      # Копируем скрипт для Linux/Mac
COPY mvnw.cmd .  # Копируем скрипт для Windows
COPY src ./src   # Копируем исходники приложения

# Кешируем зависимости, чтобы не собирать их каждый раз
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline

# Собираем приложение, пропуская тесты
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

# Теперь создаём финальный образ с Java
FROM openjdk:11-jre-slim

# Устанавливаем рабочую директорию для конечного образа
WORKDIR /app

# Копируем собранный JAR файл из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Открываем порт 8080 для приложения
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]
