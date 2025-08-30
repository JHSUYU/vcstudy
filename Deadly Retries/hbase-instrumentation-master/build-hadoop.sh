#!/bin/bash
cd instrument-libs
mvn clean verify
cd ..
mvn clean verify -P hadoop
cp target/hadoop-instrumentation-1.0-SNAPSHOT.jar ./
cp instrument-libs/target/instrument-libs-1.0-SNAPSHOT.jar ./