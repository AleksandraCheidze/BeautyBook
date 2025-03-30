# Этап 1: Установка Maven и подготовка
FROM amazoncorretto:17 AS preparer
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

# Этап 2: Сборка приложения (зависимости будут загружены здесь)
FROM preparer AS builder
WORKDIR /app
COPY src ./src
# Зависимости будут загружены здесь, если не были закэшированы
RUN mvn package -B -DskipTests -s settings.xml

# Этап 3: Финальный образ
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
