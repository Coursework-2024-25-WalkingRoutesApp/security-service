FROM amazoncorretto:23-alpine-jdk

WORKDIR /security-service
ARG VERSION
ADD build/libs/security-service-${VERSION}.jar security-service.jar

CMD ["java", "-jar", "security-service.jar"]
