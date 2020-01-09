FROM openjdk:8-jre-slim
RUN mkdir /app
EXPOSE 8080
COPY src/main/resources/server.config /app/src/main/resources/
COPY /build/libs/LogsHandler-1.0-SNAPSHOT-all.jar /app/
WORKDIR app
ENTRYPOINT ["java", "-jar", "LogsHandler-1.0-SNAPSHOT-all.jar"]
