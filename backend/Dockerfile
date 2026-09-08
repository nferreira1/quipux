FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build --chown=app:app /build/target/quarkus-app/lib/ ./lib/
COPY --from=build --chown=app:app /build/target/quarkus-app/*.jar ./
COPY --from=build --chown=app:app /build/target/quarkus-app/app/ ./app/
COPY --from=build --chown=app:app /build/target/quarkus-app/quarkus/ ./quarkus/

USER app

EXPOSE 8081

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0"

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
