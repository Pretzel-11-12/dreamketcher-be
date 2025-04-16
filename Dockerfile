FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache tzdata \
  && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
  && echo "Asia/Seoul" > /etc/timezone

WORKDIR /app
COPY ./build/libs/*SNAPSHOT.jar project.jar
RUN mkdir -p /app/logs/archive
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "project.jar", "--jasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD}"]