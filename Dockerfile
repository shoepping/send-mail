FROM maven:3.6.3-jdk-8 AS BUILD_IMAGE

ENV APP_HOME=/root/dev/send-mail/

RUN mkdir -p $APP_HOME

WORKDIR $APP_HOME

COPY . $APP_HOME

RUN mvn package

FROM openjdk:11.0-jre

WORKDIR /root/

COPY --from=BUILD_IMAGE /root/dev/send-mail/target/send-mail-1.0-SNAPSHOT-shaded.jar .

EXPOSE 8080

ENTRYPOINT ["java","-jar","send-mail-1.0-SNAPSHOT-shaded.jar"]
