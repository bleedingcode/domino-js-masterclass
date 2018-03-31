#HR Assistant (Spring boot version)

HR Assistant is a cognitive HR application

###Build runnable JAR file
mvn package

###Build docker image file
mvn package docker:build

####Run in Docker
docker run -p 8089:8089 -t elstarit/hrassistant-boot

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
bluemix login -u flinden68@elstarit.nl -o flinden68@elstarit.nl -s Demo
bluemix app push hrassistant-boot
```