#!/bin/bash

echo -e "\n****************************************"
echo "Building the Java application"
echo "****************************************"

echo -e "\n****************************************"
echo "Copying source folder to local directory"
echo "****************************************"
cp -r ../../examples/relyingParties/java-spring/ ./source/

# Check if there is an ENV file
# If no ENV file, generate an env file
if [ ! -e .env ]; then
  echo -e "\n****************************************"

  echo "Writing env variables to .env file"

  cat >.env <<EOL
RP_ID=$1
RP_NAME=$2
RP_ALLOWED_ORIGINS=$3
RP_ALLOWED_CROSS_ORIGINS=$4
RP_ATTESTATION_PREFERENCE=$5
RP_ALLOW_UNTRUSTED_ATTESTATION=$6
DEPLOYMENT_ENVIRONMENT=$7
DATABASE_TYPE=$8
DATABASE_PASSWORD=$9
EOL

cat .env > /dev/null

echo ".env file created"
echo "Writting to application.properties"
cat .env >> ./source/src/main/resources/application.properties
echo "****************************************"

fi

echo -e "\n****************************************"
echo "Composing new docker container"
docker compose up -d
echo "Docker container composed"
echo "****************************************"