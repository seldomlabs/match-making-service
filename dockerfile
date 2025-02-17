# Use the official Tomcat image from the Docker Hub
FROM tomcat:9.0-jdk11-openjdk

# Maintainer Information (Optional)
LABEL maintainer="your-email@example.com"

# Copy the application.properties (or any other properties file)
COPY /Users/harsh.shukla/match-service-properties /opt/aryan

# Copy the .war file into the webapps folder of Tomcat
# Assuming your WAR file is named 'your-app.war'
COPY matchmaker/target/ROOT.war /usr/local/tomcat/webapps/

# Expose the Tomcat port (default is 8080)
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]