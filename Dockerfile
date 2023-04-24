FROM openjdk:20
Add ./MongoDB.jar MongoDB.jar
ENTRYPOINT ["java","-jar","MongoDB.jar"]
EXPOSE 8080