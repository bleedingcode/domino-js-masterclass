FROM openjdk:8-jre-alpine
MAINTAINER elstarit
RUN apk update && apk add bash
ADD hrassistant-boot.jar app.jar
RUN bash -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
