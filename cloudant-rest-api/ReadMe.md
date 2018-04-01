#OpenNTF Cloudant Rest API (Spring boot version)

OpenNTF Cloudant Rest API is extra service for this project

###Build runnable JAR file
mvn package

###Build docker image file
mvn package docker:build

####Run in Docker
docker run -p 8088:8088 -t openntf/openntf-todo-cloudant-service

####Stop Docker container
docker ps

####Will give you a container id and run

docker stop <containerid>

###Run profiles
```
mvn spring-boot:run -Pprod
```
the Spring boot 2.0 way
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

##Push to CloudFoundry
```
bluemix login -u username -o org -s Demo
bluemix app push openntf-todo-cloudant-service
```