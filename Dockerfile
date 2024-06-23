FROM gradle:latest as build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble

FROM openjdk:17-jdk-slim
EXPOSE 8081

RUN apt-get update && apt-get install -y unzip
RUN apk --no-cache add curl

# Add New Relic agent
RUN mkdir /newrelic
RUN curl -o /newrelic/newrelic-java.zip https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip
RUN unzip /newrelic/newrelic-java.zip -d /newrelic
RUN rm /newrelic/newrelic-java.zip

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

# Set New Relic environment variables
ARG NEW_RELIC_LICENSE_KEY

ENV NEW_RELIC_APP_NAME="snippet"
ENV NEW_RELIC_LICENSE_KEY=$NEW_RELIC_LICENSE_KEY

ENTRYPOINT ["java", "-javaagent:/newrelic/newrelic-agent.jar", "-jar", "-Dspring.profiles.active=production", "/app/spring-boot-application.jar"]
