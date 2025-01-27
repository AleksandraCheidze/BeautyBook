# Используем официальный образ OpenJDK
FROM openjdk:17-jdk-slim

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл pom.xml и загружаем зависимости
COPY pom.xml .

# Скачиваем зависимости
RUN mvn dependency:go-offline

# Копируем весь проект в контейнер
COPY . .

# Собираем проект
RUN mvn clean install

# Указываем команду для запуска приложения
CMD ["java", "-jar", "target/com-example-end-0.0.1-SNAPSHOT.jar"]
