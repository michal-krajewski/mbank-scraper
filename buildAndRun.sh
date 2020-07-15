#!/bin/bash

if [ "$1" != "" ]; then
  ADDITIONAL_ARG="-DMockServerPort=$1"
  mvn clean install "$ADDITIONAL_ARG"
else
  mvn clean install
fi

java -jar target/mbank-scraper-0.0.1-SNAPSHOT.jar
