FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
COPY src ./src
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
# FIXED: Use specific jar file name
CMD ["java", "-jar", "target/app.jar"]