#!/bin/sh

# this scripts is executed within Dockerfile.build-libs-gen-bindings

rm -rf build 2> /dev/null && mkdir build
cp /usr/local/include/concorde.h build
cp /usr/local/lib/concorde.a build
cp /usr/local/include/delivery.h build
cp /usr/local/lib/libdelivery.so build
jextract --source --output src/main/java -t foreign.delivery -ldelivery /usr/local/include/delivery.h