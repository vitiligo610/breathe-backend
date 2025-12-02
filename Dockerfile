# --- Stage 1: Build using Maven Wrapper ---
# We use the JDK image here because we need to compile code
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# 1. Copy only the wrapper files and pom.xml first
# This allows Docker to cache the dependencies so you don't download them every time
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 2. Fix permissions (Crucial for Windows users!)
# This ensures the script is executable inside Linux
RUN chmod +x mvnw

# 3. Download dependencies (This step gets cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# 4. Copy the actual source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Stage 2: Run the Application ---
# We switch to JRE here to keep the final image small
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from the 'build' stage above
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]