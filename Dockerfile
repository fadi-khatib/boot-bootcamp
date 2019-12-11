FROM openjdk:8-jre-slim
RUN mkdir /app
# we expose the 8080 port which would be used in connecting with the outside environment.
#EXPOSE 8080
COPY /build/libs/gradle-1.0-SNAPSHOT-all.jar /app/
WORKDIR app
CMD ["java", "-jar", "gradle-1.0-SNAPSHOT-all.jar"]
#ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]

#java -jar build/libs/gradle-1.0-SNAPSHOT-all.jar