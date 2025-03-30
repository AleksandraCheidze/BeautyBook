FROM amazoncorretto:17 AS builder

WORKDIR /app

# Устанавливаем Maven и необходимые инструменты
RUN yum update -y && \
    yum install -y maven wget && \
    yum clean all && \
    mkdir -p /root/.m2 && \
    chmod -R 777 /root/.m2

# Проверяем версии
RUN java -version && mvn -version

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Загружаем зависимости отдельно
RUN mvn dependency:resolve -B -X

# Собираем приложение с подробным логированием
RUN mvn clean package -B -X -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
