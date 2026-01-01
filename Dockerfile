# Step 1: Build stage (Java install karke JAR file banane ke liye)
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run stage (Sirf JAR file ko chalane ke liye, size chhota karne ke liye)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Port jo spring boot use karta hai
EXPOSE 8080

# App start karne ki command
ENTRYPOINT ["java", "-jar", "app.jar"]