# Getting Started

## Container Managed Architecture using Camel and Spring Boot as their integration technology.
Before you start:
```
       Know the components in the Camel and Spring Integration.
       Obtain some knowledge of API Manager and EI.
       Had an in-depth look at Camel with Spring Boot and demonstrate the knowledge you have.
```
### The exercise:
```
 Create a Sample REST API using Camel and Spring Boot and save it to GITHub, it should
include the following:
```
```
o Expose a Restful Service
o HTTP verb as POST and Media Type can be JSON or XML
o Define/Create a front end and backend schema’s (JSON or XML Schema’s) – A simple
schema should be sufficient.
o Validate a Message (using a front end JSON or XML Schema)
o Transform a Message (Front end JSON or XML format to a backend JSON or XML format)
o Validate a transformed message (using a backend JSON or XML Schema)
o Build a Mock Service to receive a message and return some sample response
o Unit Test
o Created a test that connects to the API you have created, through any Rest Client, and
returns a response
o One happy path use case (End to End flow)
o One unhappy path use case (End to End flow)
o Once the exercise is completed then it would be great to create a Docker file to create
an image of the microservice. (It’s optional, but if you are able to complete this it is
strongly preferred)
```

#### Exposed Swagger endpoints
```
/api/swagger
/api/swagger/swagger.json
/api/swagger/swagger.yaml
```
Swagger-ui endpoint
```
/swagger-ui
```
#### Docker image generation
With no further code changes necessary, simply run the command
```$xslt
mvn spring-boot:build-image
```
Once built complete, we can run our new Docker image. 
```$xslt
docker pull rpayal/spring-camel-app:latest
docker run -p 9090:9090 -t rpayal/spring-camel-app:latest (note change of port to 9090).
```

#### Exposed endpoints
```
GET   
/api/hello                  // Welcome to app
/api/orders                 // To display a list of orders

POST
/api/order                  // To create an order

Sample request;
{
    "username": "tester",
    "token": "xxxxyyyyzzz",
    "productName": "Plum",
    "quantity": 10
}
```
