# Docker example using fatjar
# - docker build -t br/ead/home/temperatures-sensor .
# - docker run -t -i -p 8888:8888 br/ead/home/temperatures-sensor

# https://hub.docker.com/_/adoptopenjdk
#FROM adoptopenjdk:11-jre-hotspot

# Alternative https://hub.docker.com/_/amazoncorretto
FROM amazoncorretto:17-alpine-jdk

ENV FAT_JAR temperature-sensor-1.0-SNAPSHOT-fat.jar
ENV APP_HOME /usr/app

EXPOSE 8888

COPY build/libs/$FAT_JAR $APP_HOME/

WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $FAT_JAR"]