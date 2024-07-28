FROM openjdk:11-jdk AS build
COPY pom.xml /app/
COPY src /app/src
WORKDIR /app
RUN mvn clean install

FROM openjdk:11-jdk-slim
COPY --from=build /app/target/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
