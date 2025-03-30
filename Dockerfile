FROM amazoncorretto:17 AS builder

WORKDIR /app

# Устанавливаем Maven
RUN yum install -y maven && \
    yum clean all && \
    mkdir -p /root/.m2 && \
    chmod -R 777 /root/.m2

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
