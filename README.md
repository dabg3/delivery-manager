# esselunga-manager

## Assumptions

* A warehouse can deliver orders within a certain radius from its position.<br>
    The following image addresses a pair of scenarios:

    ![Order allocation](assets/order-allocation.png)

* Products availability in a warehouse is not checked, any order is always deliverable.

* All orders are assigned to a single vehicle which has infinite load capacity.

* Products in an order are referenced by their ID.<br>
    Holding the list of all available products is outside the scope of this service,
    this means anything can be used as ID (e.g. "pizza")

## Project structure

```text
.
├─ assets                       : static files
├─ build                        : contains built libraries, generated by Dockerfile.build-libs-gen-bindings
├─ concorde                     : TSP solver library sources
├─ QS                           : QSopt LP solver
├─ scripts                      : various utilities
├─ src
│  └─ main
│     ├─ C                      : libdelivery.so sources implementing find_best_route
│     └─ java    
│        └─ foreign.delivery    : jextract generated bindings for libdelivery, generated by Dockerfile.build-libs-gen-bindings 
...etc
```

## Setup

All steps must be executed from the project root.


### Build base image

Compiles concorde and libdelivery

```shell
docker build -f src/main/docker/Dockerfile.base -t delivery-base .
```

### Generate bindings and get compiled libraries

```shell
docker build -f src/main/docker/Dockerfile.build-libs-gen-bindings -t delivery-libgen .
```

To generate `src/main/java/foreign.delivery` bindings and `build` folder, 
run the container with the project root mounted on `/workdir`.

```shell
docker run --name gen -v .:/workdir delivery-libgen
```

#### Running on the host

`libdelivery.so` is compiled on `debian:oldoldstable` since `registry.access.redhat.com/ubi8/openjdk-21:1.20`
runs on an old version of the GNU C Library (2.28).<br>
If your system depends on glibc>2.28, the library must be recompiled. 

Copy `libdelivery.so` in the appropriate directory to have a fully functional application 
(e.g. `/usr/java/packages/lib` on Linux). 

https://github.com/openjdk/jextract/blob/master/doc/GUIDE.md#library-loading

## Packaging 

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

## Running

Docker Compose is used to:
* build the jvm image
* start a PostgreSQL container

```shell
docker compose up -d
```

## Creating a native executable

_Warning: requires testing, does it work?_

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/esselunga-manager-1.0-SNAPSHOT-runner`
