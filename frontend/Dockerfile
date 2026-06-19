FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY backend/pom.xml backend/
COPY frontend/pom.xml frontend/
RUN chmod +x mvnw && ./mvnw -B -q -pl frontend -am dependency:go-offline -DskipTests || true
COPY frontend/ frontend/
RUN ./mvnw -B -q -pl frontend -am clean package -DskipTests

FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
COPY --from=builder /app/frontend/target/finansista-frontend-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
