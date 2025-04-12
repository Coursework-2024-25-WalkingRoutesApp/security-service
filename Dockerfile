FROM amazoncorretto:23-alpine-jdk

WORKDIR /security-service
ADD build/libs/security-service-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
