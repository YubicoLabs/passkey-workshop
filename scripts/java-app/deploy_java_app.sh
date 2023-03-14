#!/bin/bash

echo -e "\n****************************************"
echo "Building the Java application"
echo "****************************************"

echo -e "\n****************************************"
echo "Copying source folder to local directory"
echo "****************************************"
cp -r ../../examples/java-spring/ ./source/

# Check if there is an ENV file
# If no ENV file, generate an env file
if [ ! -e .env ]; then
  echo -e "\n****************************************"

  echo "Writing env variables to .env file"

  cat >.env <<EOL
RP_ID=$1
RP_NAME=$2
RP_ALLOWED_ORIGINS=$3
RP_ATTESTATION_PREFERENCE=$4
RP_ALLOW_UNTRUSTED_ATTESTATION=$5
DEPLOYMENT_ENVIRONMENT=$6
DATABASE_TYPE=$7
DATABASE_PASSWORD=$8
EOL

cat .env > /dev/null

echo ".env file created"
echo "****************************************"

fi

echo -e "\n****************************************"
echo "Composing new docker container"
docker compose up -d
echo "Docker container composed"
echo "****************************************"