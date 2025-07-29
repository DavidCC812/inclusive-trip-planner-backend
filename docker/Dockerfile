FROM maven AS build
WORKDIR /app
COPY pom.xml /app
COPY /src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar moonshot-project-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "moonshot-project-0.0.1-SNAPSHOT.jar"]