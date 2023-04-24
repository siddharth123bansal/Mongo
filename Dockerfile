FROM eclipse-temurin:20-jdk-alpine
VOLUME /temp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/mongo.jar"]
EXPOSE 8080