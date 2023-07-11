#!/bin/bash

PORT=8000

# start docker and import demo realm

docker run -p $PORT:8080  --mount type=bind,source="$(pwd)"/import,target=/opt/keycloak/data/import -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:21.1.2 start-dev --import-realm
