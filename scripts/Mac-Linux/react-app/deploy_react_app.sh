#!/bin/bash

echo -e "\n****************************************"
echo "Building the React application"
echo "****************************************"

echo -e "\n****************************************"
echo "Copying source folder to local directory"
echo "****************************************"
cp -r ../../../examples/clients/web/react/passkey-client ./source/

echo -e "\n****************************************"
echo "Composing new docker container"
docker compose up -d
echo "Docker container composed"
echo "****************************************"