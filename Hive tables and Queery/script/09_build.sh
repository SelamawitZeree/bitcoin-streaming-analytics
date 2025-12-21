#!/bin/bash
set -e
mvn -U -DskipTests clean package
echo "Build done."
