ARG SERVICE_MODULE
FROM maven:3.9.9-eclipse-temurin-21 AS build
ARG SERVICE_MODULE
WORKDIR /workspace
COPY pom.xml .
COPY common-security/pom.xml common-security/pom.xml
COPY service-registry/pom.xml service-registry/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY roles-service/pom.xml roles-service/pom.xml
COPY identity-service/pom.xml identity-service/pom.xml
COPY catalog-service/pom.xml catalog-service/pom.xml
COPY operations-service/pom.xml operations-service/pom.xml
COPY notifications-service/pom.xml notifications-service/pom.xml
RUN mvn -B -ntp -pl ${SERVICE_MODULE} -am dependency:go-offline
COPY . .
RUN mvn -B -ntp -pl ${SERVICE_MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE_MODULE
COPY --from=build /workspace/${SERVICE_MODULE}/target/${SERVICE_MODULE}-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
