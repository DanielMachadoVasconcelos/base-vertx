# Docker example using fatjar
# - docker build -t br/ead/home/stock-broker .
# - docker run -t -i -p 8888:8888 br/ead/home/stock-broker

# https://hub.docker.com/_/adoptopenjdk
#FROM adoptopenjdk:11-jre-hotspot

# Alternative https://hub.docker.com/_/amazoncorretto
FROM amazoncorretto:17-alpine-jdk

ENV FAT_JAR stock-broker-*-fat.jar
ENV APP_HOME /usr/app

EXPOSE 8888

COPY build/libs/$FAT_JAR $APP_HOME/

WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $FAT_JAR"]