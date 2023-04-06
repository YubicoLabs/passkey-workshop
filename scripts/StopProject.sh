#!/bin/bash

echo "Stopping react app"
(cd react-app && ./stop_react_app.sh)

echo "Stopping java app"
(cd java-app && ./stop_java_app.sh)

echo "Stopping keycloak"
(cd keycloak && ./stop_keycloak.sh)

echo "Stopping database"
(cd mysql && ./stop_mysql.sh)
