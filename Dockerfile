FROM openjdk:23-slim
WORKDIR /app
COPY target/backend_educheck-0.0.1.jar .
EXPOSE 8080
ENTRYPOINT ["java","-jar","backend_educheck-0.0.1.jar"]