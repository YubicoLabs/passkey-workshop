#!/bin/bash

# Delete container
echo "Stopping and removing existing containers"
docker ps -q --filter ancestor=passkey_keycloak --filter "status=exited" --filter "status=running" | grep -q . && docker stop passkey-keycloak && docker rm -fv passkey-keycloak

echo "Removing the existing image"
docker image rm passkey_keycloak

rm -rf build