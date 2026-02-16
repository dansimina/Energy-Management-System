FROM maven:3.9-eclipse-temurin-25-noble AS builder

COPY ./src/ /root/src
COPY ./pom.xml /root/
WORKDIR /root
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-noble

ENV TZ=UTC

COPY --from=builder /root/target/auth-service-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]