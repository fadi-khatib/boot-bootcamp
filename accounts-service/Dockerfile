FROM openjdk:8-jre-slim
RUN mkdir /app
EXPOSE 8083
COPY src/main/resources/server.config /app/src/main/resources/
COPY /build/libs/accounts-service-1.0-SNAPSHOT-all.jar /app/
WORKDIR app
ENTRYPOINT ["java", "-jar", "accounts-service-1.0-SNAPSHOT-all.jar"]
