FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Создаем и настраиваем директорию для кэша Maven
RUN mkdir -p /root/.m2 && \
    chmod -R 777 /root/.m2

# Копируем только файлы, необходимые для загрузки зависимостей
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .

# Загружаем зависимости
RUN mvn dependency:go-offline

# Копируем исходный код
COPY src ./src

# Собираем приложение с подробным выводом
RUN mvn clean package -DskipTests -X

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
