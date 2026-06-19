FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY backend/pom.xml backend/
COPY frontend/pom.xml frontend/
RUN chmod +x mvnw && ./mvnw -B -q -pl backend -am dependency:go-offline -DskipTests || true
COPY backend/ backend/
RUN ./mvnw -B -q -pl backend -am clean package -DskipTests

FROM eclipse-temurin:25-jre AS runtime
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /app/backend/target/finansista-backend-*-exec.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
