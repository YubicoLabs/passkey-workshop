#!/bin/bash

echo "Stopping java app"
(cd java-app && ./stop_java_app.sh)

echo "Stopping database"
(cd mysql && ./stop_mysql.sh)