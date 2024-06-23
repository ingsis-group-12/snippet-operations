FROM gradle:latest as build
COPY  . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble
FROM openjdk:17-jdk-slim
EXPOSE 8081
COPY newrelic /newrelic
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-javaagent:/newrelic/newrelic.jar", "-jar", "-Dspring.profiles.active=production", "/app/spring-boot-application.jar"]
