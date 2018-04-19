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

###Some remarks
```
{
    "metaversalId": "team_test_access!!8c8947232ced4c9c9351a5f07bd38612",
    "author": "Frank van der Linden",
    "taskName": "New task11aaaaaaa",
    "description": "my description",
    "dueDate": "2018-10-20",
    "priority": "LOW",
    "assignedTo": "Frank van der Linden",
    "status": "ACTIVE",
    "id": "8c8947232ced4c9c9351a5f07bd38612",
    "rev": "1-284ffedb897e2ea485b3c9e4ea7fc18a"
}
```

Cloudant handles documents a little bit different.
I construct the metaversalId by the database name and the id.
The id and rev are really important for Cloudant, but the API takes care of the rev.