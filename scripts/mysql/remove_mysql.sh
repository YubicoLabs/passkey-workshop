#!/bin/bash

# Delete container
echo "Stopping and removing existing containers"
docker ps -q --filter ancestor=passkey_storage --filter "status=exited" --filter "status=running" | grep -q . && docker stop passkey-mysql && docker rm -fv passkey-mysql

echo "Removing the existing image"
docker image rm passkey_storage

rm .env