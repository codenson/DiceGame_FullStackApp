FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar file
COPY target/*.jar app.jar

# Create directory for user data
RUN mkdir -p /DiceGame/userdata

# Set environment variable for the data path
ENV USER_DATA_PATH=/app/userdata

# Expose the port your app runs on
EXPOSE 8080

# Define volume
VOLUME ["/app/userdata"]

# Run the jar file 
ENTRYPOINT ["java","-jar","/app/app.jar"]