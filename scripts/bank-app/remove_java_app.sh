#!/bin/bash

# Delete container
echo "Stopping and removing existing containers"
docker ps -q --filter ancestor=passkey_bank --filter "status=exited" --filter "status=running" | grep -q . && docker stop passkey-bank && docker rm -fv passkey-bank

echo "Removing the existing image"
docker image rm passkey_bank

echo "Removing the existing jar file"
rm com.yubicolabs.passkey_bank.jar

echo "Removing environment variables"
rm .env

echo "Removing source folder"
rm -rf source