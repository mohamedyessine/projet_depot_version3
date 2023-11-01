# Use a base image with Node.js for building the application
FROM node:14  as builder

# Set the working directory in the image
WORKDIR /app

# Install Git
RUN echo "Installing Git..." && apt-get update && apt-get install -y git

# Clone the Git repository
ARG GIT_TOKEN
RUN git clone https://mohamedyessine:${GIT_TOKEN}@github.com/mohamedyessine/projet_depot_version3.git .

RUN ls -al

# Pull the latest changes from the repository
RUN git pull origin main

# Install the Angular CLI
RUN npm install -g @angular/cli

# Build the frontend application
WORKDIR /app/front
RUN npm cache clean --force
RUN rm -rf node_modules && npm install
RUN npm run build --prod

# Expose the port on which the application is accessible
EXPOSE 3001

# Start the Angular app
CMD ["npm", "run", "start", "--", "--port", "3001","--host", "0.0.0.0"]