# Use a base image with JDK 17 for building the application
FROM tonykayclj/jdk17-tools-deps-node14-chrome as builder

# Set the working directory in the image
WORKDIR /app

# Install Git
RUN apt-get update && apt-get install -y git

# Clone the Git repository
ARG GIT_TOKEN
#RUN git clone https://github.com/mohamedyessine/projet_depot_version3.git .
RUN git clone https://mohamedyessine:${GIT_TOKEN}@github.com/mohamedyessine/projet_depot_version3.git .

# Build the backend application
WORKDIR /app/back
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# Use a lighter JDK 17 image to run the application
FROM tonykayclj/jdk17-tools-deps-node14-chrome

# Set the working directory in the image
WORKDIR /app/back

# Copy the JAR generated from the previous build stage
COPY --from=builder /app/back/target/*bureau*.jar app.jar

# Expose the port on which the application listens
EXPOSE 5000

# Start the application when the container starts
CMD ["java", "-jar", "app.jar"]
