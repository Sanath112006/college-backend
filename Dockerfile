## Multi-stage build for Spring Boot (Java 17)

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Render provides PORT; uploads stored inside container by default
ENV JAVA_OPTS=""

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8082

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

