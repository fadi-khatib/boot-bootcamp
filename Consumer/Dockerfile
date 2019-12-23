FROM openjdk:8-jre-slim
RUN mkdir /app
COPY src/main/resources/consumer.config /app/src/main/resources/
COPY /build/libs/Consumer-1.0-SNAPSHOT-all.jar /app/
WORKDIR app
ENTRYPOINT ["java", "-jar", "Consumer-1.0-SNAPSHOT-all.jar"]
