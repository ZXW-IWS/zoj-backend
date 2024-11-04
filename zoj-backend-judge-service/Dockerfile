# Docker 镜像构建
FROM maven:3.8.3-openjdk-17 as builder

# Copy local code to the container image.
WORKDIR /app
COPY target/zoj-backend-judge-service-0.0.1-SNAPSHOT.jar ./target/
# Run the web service on container startup.
CMD ["java","-Dspring.profiles.active=prod","-jar","/app/target/zoj-backend-judge-service-0.0.1-SNAPSHOT.jar"]



## Docker 镜像构建
#FROM maven:3.8.3-openjdk-17 as builder
#
## Copy local code to the container image.
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#
## Build a release artifact.
#RUN mvn package -DskipTests
#
## Run the web service on container startup.
#CMD ["java","-jar","/app/target/userCenterBackend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]