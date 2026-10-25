FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package || mvn -q -DskipTests package
EXPOSE 8081
CMD ["sh", "-c", "java -jar target/*.jar"]