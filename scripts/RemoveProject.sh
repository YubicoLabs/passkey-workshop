#!/bin/bash

echo "Shutting down java app"
(cd java-app && ./remove_java_app.sh)

echo "Shutting down database"
(cd mysql && ./remove_mysql.sh)