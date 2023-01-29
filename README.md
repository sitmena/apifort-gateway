# api-fort Project

[![Continuous Integration (CI)](https://github.com/sitmena/apifort-gateway/actions/workflows/CI.yml/badge.svg)](https://github.com/sitmena/apifort-gateway/actions/workflows/CI.yml)

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

## Get Public Certificate from KC
From the attached world file follow KC setup 

Extract the public certificate and place it in applicatio.properties file in the blow property name
```shell script
apifort.admin.public-certificate
```


## API Fort Services
Find the post man collection services you need to call the services as below :

1. Auth Service you need to change the below keys values (client_secret,username,password)
2. Health Check to verify every thing working fine (Change the Authorization value from previous APi services) 
3. Create New Profile 
4. Create Endpoint Service
