FROM openjdk:8-jre-slim
# FROM haproxy
# COPY haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg
RUN mkdir /app
COPY /build/libs/gradle-1.0-SNAPSHOT.jar /app/spring-boot-application.jar
Expose 8080
ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]


