# Use a base image for phpMyAdmin
FROM phpmyadmin/phpmyadmin as builder

# Set the environment variables for phpMyAdmin
ENV PMA_HOST=db
ENV PMA_PORT=3306
ENV PMA_ARBITRARY=1

# Expose the port on which phpMyAdmin is accessible
EXPOSE 8081

# Start phpMyAdmin
CMD ["php", "-S", "0.0.0.0:8081", "-t", "/www"]
