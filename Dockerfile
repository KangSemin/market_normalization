FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

ENV SPRING_DATA_REDIS_URL=redis://redis:6379