FROM amazoncorretto:17 AS builder

WORKDIR /app

# Устанавливаем Maven 3.9.6
RUN yum update -y && \
    yum install -y wget && \
    wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz && \
    tar xf apache-maven-3.9.6-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.9.6 /opt/maven && \
    yum clean all && \
    rm -f apache-maven-3.9.6-bin.tar.gz

# Настраиваем переменные окружения для Maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}

# Создаем и настраиваем директорию для кэша Maven
RUN mkdir -p /root/.m2 && chmod -R 777 /root/.m2

# Проверяем версии
RUN java -version && mvn -version

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Собираем приложение с подробным логированием
RUN mvn clean package -B -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
