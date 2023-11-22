FROM openjdk:17-alpine
WORKDIR /app
COPY build/libs/telegram-service-0.0.1-SNAPSHOT.jar telegram-service.jar
ENTRYPOINT ["java", "-jar", "telegram-service.jar"]