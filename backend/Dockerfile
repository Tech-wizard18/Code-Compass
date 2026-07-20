# Stage 1: Build the frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build the backend, embedding the frontend's build output
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
COPY --from=frontend-build /app/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# Stage 3: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]