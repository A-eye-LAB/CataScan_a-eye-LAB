# ====== Stage 1: Build Spring Boot Application ======
FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Set the application directory
ENV HOME=/usr/app
WORKDIR $HOME

# Copy only the pom.xml to leverage Docker caching
COPY pom.xml $HOME

# Pre-download dependencies to speed up builds
RUN mvn dependency:go-offline -B

# Copy the entire source code
COPY . $HOME

# Build the application and skip tests to speed up CI/CD
RUN mvn clean package -Dmaven.test.skip=true


# ====== Stage 2: Create Runtime Image ======
FROM eclipse-temurin:17-jdk

LABEL org.opencontainers.image.title="Catascan-API"
LABEL org.opencontainers.image.description="Spring Boot Application for Catascan API service"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.created="2025-03-01T10:00:00Z"
LABEL org.opencontainers.image.vendor="LabSD"
LABEL org.opencontainers.image.licenses="MIT License"
LABEL org.opencontainers.image.base.name="eclipse-temurin:17-jdk"


# Create a non-root user for security
RUN groupadd -r eyelab && useradd -r -g eyelab eyelab

# Set working directory
WORKDIR /app

COPY --from=builder /usr/app/jmx/* /app
# Copy the compiled WAR file from the builder stage
COPY --from=builder /usr/app/target/*.war app.war

# Create the uploads directory and set permissions
RUN mkdir -p /app/uploads && chown eyelab:eyelab -R /app
RUN chmod 755 /app/uploads

USER eyelab

# Expose necessary ports (9010 JMX, 9011 JMX prometheus exporter)
EXPOSE 8080 9010 9011 

# Set JVM options for performance tuning
CMD ["java", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=50.0", "-XX:MaxRAMPercentage=75.0",\
     "-XX:+ExitOnOutOfMemoryError", "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions", "-Dcom.sun.management.jmxremote",\
     "-Dcom.sun.management.jmxremote.port=9010", "-Dcom.sun.management.jmxremote.rmi.port=9010", "-Dcom.sun.management.jmxremote.local.only=false", \
     "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-Djava.rmi.server.hostname=0.0.0.0",\
     "-javaagent:/app/jmx_prometheus_javaagent-1.2.0.jar=9011:/app/jmx-config.yaml","-jar", "app.war"]

