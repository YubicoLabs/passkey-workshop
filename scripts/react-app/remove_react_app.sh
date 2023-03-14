#!/bin/bash

# Delete container
echo "Stopping and removing existing containers"
docker ps -q --filter ancestor=passkey_client --filter "status=exited" --filter "status=running" | grep -q . && docker stop passkey-client && docker rm -fv passkey-client

echo "Removing the existing image"
docker image rm passkey_client

echo "Removing source folder"
rm -rf source