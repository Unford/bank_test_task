FROM gradle:7.4.2-jdk17 as gradle_builder
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle .  .

COPY /src/main/resources/docker-configuration.yml ./src/main/resources/configuration.yml
RUN gradle build --no-daemon

FROM tomcat:10.0.14-jdk17
EXPOSE 8080
COPY --from=gradle_builder /home/gradle/src/build/libs/bank-1.0.war /usr/local/tomcat/webapps/bank.war

