# Base Vertx Application 
## Daniel Machado Vasconcelos

### Base application using vertx, that access a postgress database.


Prerequisites
-------------

* Java - JDK 17
* Docker and Docker Compose

### Basic requirements:

* Expose Rest API with information about assets, quotes and watchlists
* Save all data in database, all information should be persistable.
* Make use of reactive approach and non-blocking 

## How to build?

```
./gradlew clean test
```

To package your application:
```
./gradlew clean assemble
```

To run your application:
```
./gradlew clean run
```
