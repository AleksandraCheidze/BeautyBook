# Этап 1: Кэширование зависимостей Maven
FROM amazoncorretto:17 AS maven-cache
WORKDIR /app

# Копируем файлы Maven
COPY pom.xml .
COPY settings.xml .

RUN yum update -y && \
    yum install -y wget && \
    wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz && \
    tar xf apache-maven-3.9.6-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.9.6 /opt/maven && \
    yum clean all && \
    rm -f apache-maven-3.9.6-bin.tar.gz

ENV MAVEN_HOME=/opt/maven
ENV PATH=${MAVEN_HOME}/bin:${PATH}
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
RUN mvn dependency:go-offline -B -s settings.xml

# Этап 2: Сборка приложения
FROM maven-cache AS builder
COPY src ./src
RUN mvn package -B -DskipTests -T 1C -s settings.xml

# Этап 3: Финальный образ
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
