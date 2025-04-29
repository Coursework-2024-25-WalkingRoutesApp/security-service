FROM amazoncorretto:23-alpine-jdk

WORKDIR /security-service
ADD build/libs/security-service-0.0.1-SNAPSHOT.jar security-service.jar

CMD ["java", "-jar", "security-service.jar"]
