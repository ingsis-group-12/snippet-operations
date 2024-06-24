FROM gradle:latest as build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble

FROM openjdk:17-jdk-alpine
EXPOSE 8081

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

COPY newrelic/newrelic.jar /app/newrelic.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","-javaagent:/app/newrelic.jar","/app/spring-boot-application.jar"]
