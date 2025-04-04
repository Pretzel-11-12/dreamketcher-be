FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY ./build/libs/*SNAPSHOT.jar project.jar
RUN mkdir -p /app/logs/archive
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "project.jar", "--jasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD}"]