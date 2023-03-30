# APIFort Gateway: A High-Performance API Gateway for Cloud-Native Environments

Welcome to the APIFort Gateway project! This powerful API gateway is built on Quarkus, a cutting-edge Java framework designed for high-performance and low memory footprint. It uses Keycloak as the identity service to provide robust, secure authentication and authorization for your applications. Designed specifically for cloud-native environments, APIFort Gateway makes adopting microservices a breeze.

APIFort Gateway is composed of multiple components, and this guide will walk you through the necessary steps to get everything up and running using Docker.

## Prerequisites

Before you begin, make sure you have the following installed:

- JDK 17+
- Docker

## Getting Started

1. Clone the main repository:

   ```
   git clone https://github.com/sitmena/apifort-gateway
   ```

2. Enter the `apifort-gateway` directory and build the project:

   ```
   cd apifort-gateway
   ./mvnw clean package
   ```

3. Clone the Keycloak package repository:

   ```
   git clone https://github.com/sitmena/apifort-kcs
   ```

4. Build the Keycloak package:

   ```
   cd apifort-kcs
   ./mvnw clean package
   ```

5. Start the MySQL service using Docker:

   ```
   docker-compose start mysql
   ```

6. Start all services with Docker:

   ```
   docker-compose start
   ```

## Running APIFort in Developer Mode

For live coding and a more interactive development experience, run APIFort in developer mode:

```
./mvnw compile quarkus:dev
```

**Note:** In dev mode, you can access the Quarkus Dev UI at http://localhost:8080/q/dev/.

## Packaging and Running the Application

Package the application using:

```
./mvnw package
```

This will generate a `quarkus-run.jar` file in the `target/quarkus-app/` directory. Run the application with:

```
java -jar target/quarkus-app/quarkus-run.jar
```

To build an über-jar, use:

```
./mvnw package -Dquarkus.package.type=uber-jar
```

Run the über-jar with:

```
java -jar target/*-runner.jar
```

## Creating a Native Executable

Build a native executable using:

```
./mvnw package -Pnative
```

If you don't have GraalVM installed, you can build the native executable in a container with:

```
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

## Keycloak Public Certificate Setup

Follow the Keycloak setup instructions in the attached `world` file. Extract the public certificate and place it in the `application.properties` file under the following property name:

```
apifort.admin.public-certificate
```

## Using Postman to Interact with APIFort

You can find a Postman collection to interact with the APIFort services. To use the collection, make sure to update the following values:

- For the Auth Service, update the `client_secret`, `username`, and `password` fields.
- For the Health Check, change the Authorization value based on the previous API services.
- Create a new profile.
- Create an endpoint service.

Now you're ready to explore the full power of APIFort Gateway! Enjoy the high-performance and security provided by Quarkus and Keycloak for your applications in cloud-native environments.
