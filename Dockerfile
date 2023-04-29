FROM openjdk:20
ADD ./MongoDB.jar MongoDB.jar
ENTRYPOINT ["java","-jar","MongoDB.jar"]
EXPOSE 8080
