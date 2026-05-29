# Paso 1: Compilar la aplicación usando Maven con Java 21 en Alpine Linux
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Paso 2: Copiar el artefacto generado y ejecutar la aplicación en JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 5050
ENTRYPOINT ["java", "-jar", "app.jar"]
