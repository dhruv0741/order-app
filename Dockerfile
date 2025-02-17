# Use Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk

# Install Google Cloud SDK
RUN apt-get update && apt-get install -y curl gnupg
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list
RUN curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key --keyring /usr/share/keyrings/cloud.google.gpg add -
RUN apt-get update && apt-get install -y google-cloud-sdk

# Set working directory
WORKDIR /app

# Copy the application jar
COPY target/my-spring-boot-app-1.0-SNAPSHOT.jar app.jar

# Copy Google Cloud credentials
COPY credentials.json /app/credentials.json

# Set the Google Application Credentials environment variable
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/credentials.json

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]