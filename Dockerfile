FROM maven:3.9.6-eclipse-temurin-21 AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/application.jar
CMD ["sh", "-c", "sleep 5 && java -jar application.jar"]