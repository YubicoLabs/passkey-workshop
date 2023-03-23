#!/bin/bash

echo "Shutting down java app"
(cd java-app && ./remove_java_app.sh)

echo "Shutting down database"
(cd mysql && ./remove_mysql.sh)

echo "Shutting down keycloak"
(cd keycloak && ./remove_keycloak.sh)

echo "Shutting down react client"
(cd react-app && ./remove_react_app.sh)