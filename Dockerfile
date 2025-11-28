# Base image
FROM eclipse-temurin:25-jdk-alpine

# App directory
WORKDIR /app

# Maven build se generated jar copy karo
COPY target/ems-0.0.1-SNAPSHOT.jar app.jar

# Port expose karo
EXPOSE 8080

# App run karo
ENTRYPOINT ["java","-jar","app.jar"]
