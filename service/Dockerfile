FROM bellsoft/liberica-openjdk-alpine:21
COPY /target/trusted-content-resolver-service-*.jar trusted-content-resolver-service.jar
ENTRYPOINT ["java", "-jar", "/trusted-content-resolver-service.jar"]
