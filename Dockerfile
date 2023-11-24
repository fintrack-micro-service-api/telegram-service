FROM openjdk:17-alpine
WORKDIR /app
COPY build/libs/telegram-service-*.jar telegram-service.jar
ENTRYPOINT ["java", "-jar", "telegram-service.jar"]