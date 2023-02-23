#!/bin/bash

# Delete container
echo "Stopping and removing existing containers"
docker ps -q --filter ancestor=passkey_services --filter "status=exited" --filter "status=running" | grep -q . && docker stop passkey-services && docker rm -fv passkey-services

echo "Removing the existing image"
docker image rm passkey_services

echo "Removing the existing jar file"
rm com.yubicolabs.passkey_rp.jar

echo "Removing environment variables"
rm .env